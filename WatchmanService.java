package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class WatchmanService extends Service implements SensorEventListener {

    private static final String TAG = "FFT_DEBUG";
    private static final String CHANNEL_ID = "WatchmanChannel";
    private static final int NOTIFICATION_ID = 1;
    // CRITICAL: Ensure this matches the MediaPipe-converted model in your assets
    private static final String MODEL_FILE = "gemma-2b-it-gpu-int4.bin";
    private static final long EXPECTED_MODEL_SIZE = 1354301440L; // Gemma 2B int4 exact size

    public static final String ACTION_STATUS_UPDATE = "com.example.myapplication.STATUS_UPDATE";
    public static final String EXTRA_STATUS = "status";

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final float[] rollingBuffer = new float[300];
    private int bufferIndex = 0;

    private WindowManager windowManager;
    private View overlayView;

    // Concurrency and Threading Guards
    private final AtomicBoolean isAiProcessing = new AtomicBoolean(false);
    private long lastTriggerTime = 0;
    private long lastBufferTime = 0;
    private double currentFs = 50.0; // Default sampling rate estimate

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification());

        // Worker thread for FFT and LLM to prevent Main Thread lag or LMK kills
        backgroundThread = new HandlerThread("WatchmanWorker");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // Using Linear Acceleration to ignore gravity and detect movement more precisely
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            if (accelerometer == null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }
        }

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        sendStatus("Watchman Active (Stability Optimized)");

        ensureModelFileExists();
    }

    private void sendStatus(String status) {
        Intent intent = new Intent(ACTION_STATUS_UPDATE);
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
        Log.d(TAG, "Status Update: " + status);
    }

    private File getModelFile() {
        return new File(getFilesDir(), MODEL_FILE);
    }

    private void ensureModelFileExists() {
        new Thread(() -> {
            try {
                File modelFile = getModelFile();
                if (modelFile.exists() && modelFile.length() == EXPECTED_MODEL_SIZE) {
                    Log.d(TAG, "Model file already exists and size matches.");
                    return;
                }

                sendStatus("Preparing AI Model Bundle...");
                File tempFile = new File(modelFile.getParent(), MODEL_FILE + ".tmp");
                try (InputStream is = getAssets().open(MODEL_FILE);
                     FileOutputStream os = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[131072]; // Larger buffer for faster copy
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                }

                if (tempFile.length() == EXPECTED_MODEL_SIZE) {
                    if (tempFile.renameTo(modelFile)) {
                        Log.d(TAG, "Model file extraction complete. Size: " + modelFile.length());
                    } else {
                        Log.e(TAG, "Failed to rename temp model file. Current file may be locked.");
                    }
                } else {
                    Log.e(TAG, "Extracted model size mismatch: " + tempFile.length() + " vs " + EXPECTED_MODEL_SIZE);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to prepare model file", e);
            }
        }).start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        rollingBuffer[bufferIndex] = magnitude;
        bufferIndex = (bufferIndex + 1) % rollingBuffer.length;

        // Process every 300 samples
        if (bufferIndex == 0) {
            long currentTime = System.currentTimeMillis();
            if (lastBufferTime != 0) {
                double durationSec = (currentTime - lastBufferTime) / 1000.0;
                if (durationSec > 0) {
                    currentFs = 300.0 / durationSec;
                    Log.d(TAG, String.format(Locale.US, "Measured Sampling Rate: %.2f Hz", currentFs));
                }
            }
            lastBufferTime = currentTime;

            final float[] snapshot = rollingBuffer.clone();
            final double fs = currentFs;
            backgroundHandler.post(() -> analyzeBuffer(snapshot, fs));
        }
    }

    private void analyzeBuffer(float[] dataBuffer, double fs) {
        double[] data = new double[256];
        double mean = 0;
        
        // Use the last 256 samples for FFT (must be power of 2)
        for (int i = 0; i < 256; i++) {
            data[i] = dataBuffer[i];
            mean += data[i];
        }
        mean /= 256.0;

        for (int i = 0; i < 256; i++) {
            data[i] -= mean; // DC Offset Removal
            // Hanning window to reduce spectral leakage
            double window = 0.5 * (1 - Math.cos(2 * Math.PI * i / 255.0));
            data[i] *= window;
        }

        try {
            FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] result = transformer.transform(data, TransformType.FORWARD);

            // Dynamic bin mapping: index = freq * N / fs
            int startBin = (int) Math.round(256.0 / fs);
            int endBin = (int) Math.round(3.5 * 256.0 / fs);
            startBin = Math.max(1, startBin);
            endBin = Math.min(127, endBin);

            double powerInRange = 0;
            for (int i = startBin; i <= endBin; i++) {
                powerInRange += result[i].abs();
            }

            Log.d(TAG, String.format(Locale.US, "Calculated Power (1-3.5Hz): %.2f", powerInRange));

            long currentTime = System.currentTimeMillis();
            // Trigger logic: Intensity > 8.0, 60s cooldown, and AI not currently busy
            if (powerInRange > 8.0 && (currentTime - lastTriggerTime > 60000)) {
                if (isAiProcessing.compareAndSet(false, true)) {
                    lastTriggerTime = currentTime;
                    processWithLLM(powerInRange);
                } else {
                    Log.d(TAG, "AI Engine busy, skipping this cycle.");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "FFT Calculation Error", e);
        }
    }

    private void processWithLLM(double intensity) {
        backgroundHandler.post(() -> {
            LlmInference lazyLlm = null;
            try {
                File modelFile = getModelFile();
                if (!modelFile.exists() || modelFile.length() != EXPECTED_MODEL_SIZE) {
                    Log.w(TAG, "AI Model not ready or corrupted. Size: " + modelFile.length() + " bytes.");
                    isAiProcessing.set(false);
                    return;
                }

                Log.d(TAG, "Loading LLM Model into RAM... (Size: " + modelFile.length() + " bytes)");
                LlmInferenceOptions options = LlmInferenceOptions.builder()
                        .setModelPath(modelFile.getAbsolutePath())
                        .setMaxTokens(128)
                        .setPreferredBackend(LlmInference.Backend.GPU)
                        .build();

                try {
                    lazyLlm = LlmInference.createFromOptions(this, options);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to create LlmInference. Check if model format is correct (.task or converted .bin).", e);
                    isAiProcessing.set(false);
                    return;
                }

                String prompt = String.format(Locale.US, 
                        "The user is doom-scrolling with intensity %.2f. " +
                        "Generate a single, compassionate, grounding sentence (max 12 words) " +
                        "to nudge them to take a deep breath and look up.", intensity);

                Log.d(TAG, "Generating compassionate nudge...");
                String response = lazyLlm.generateResponse(prompt);
                Log.d(TAG, "AI Intervention: " + response);

                new Handler(Looper.getMainLooper()).post(() -> showOverlay(response));

            } catch (Throwable t) {
                Log.e(TAG, "FATAL: AI Engine Error. Ensure model is MediaPipe-compatible.", t);
            } finally {
                if (lazyLlm != null) {
                    try {
                        lazyLlm.close();
                        Log.d(TAG, "LLM Closed. RAM reclaimed.");
                    } catch (Exception ignored) {}
                }
                isAiProcessing.set(false);
            }
        });
    }

    private void showOverlay(String message) {
        if (overlayView != null) return;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;

        LayoutInflater inflater = LayoutInflater.from(this);
        overlayView = inflater.inflate(R.layout.overlay_watchman, null);

        TextView textView = overlayView.findViewById(R.id.mentor_text);
        if (textView != null) {
            textView.setText(message);
        }

        // Pressure-sensitive exit validation
        overlayView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getPressure() > 0.7f) {
                    if (windowManager != null && overlayView != null) {
                        windowManager.removeView(overlayView);
                        overlayView = null;
                        Log.d(TAG, "Overlay dismissed by conscious pressure.");
                    }
                }
            }
            v.performClick();
            return false;
        });

        Button closeButton = overlayView.findViewById(R.id.close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                if (windowManager != null && overlayView != null) {
                    windowManager.removeView(overlayView);
                    overlayView = null;
                }
            });
        }

        try {
            windowManager.addView(overlayView, params);
            Log.d(TAG, "Intervention overlay displayed.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to display overlay", e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Watchman Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Watchman Monitoring")
                .setContentText("Keeping you present and mindful...")
                .setSmallIcon(R.drawable.avatar_1)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backgroundThread != null) backgroundThread.quitSafely();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
}
