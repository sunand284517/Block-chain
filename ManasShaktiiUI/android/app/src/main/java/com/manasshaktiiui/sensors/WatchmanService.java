package com.manasshaktiiui.sensors;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.manasshaktiiui.ai.ManasShaktiiAgent;
import com.manasshaktiiui.R;

import java.util.Locale;

/**
 * Native Android Sensor Foreground Service.
 * Collects accelerometer and gyroscope data, manages buffering, delegates FFT feature extraction,
 * and emits structured BehavioralEvents to the ManasShaktiiAgent without direct model coupling.
 */
public class WatchmanService extends Service implements SensorEventListener {

    private static final String TAG = "WatchmanService";
    private static final String CHANNEL_ID = "WatchmanChannel";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_STATUS_UPDATE = "com.manasshaktiiui.STATUS_UPDATE";
    public static final String EXTRA_STATUS = "status";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private final float[] rollingBuffer = new float[300];
    private int bufferIndex = 0;
    private long lastBufferTime = 0;
    private double currentFs = 50.0; // Default sampling frequency estimate (Hz)

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    private static WatchmanService instance;
    private ManasShaktiiAgent agent;

    public static boolean isRunning() {
        return instance != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification());

        backgroundThread = new HandlerThread("WatchmanSensorWorker");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        agent = ManasShaktiiAgent.getInstance(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            if (accelerometer == null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }

            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if (gyroscope != null) {
                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
            }
        }

        sendStatus("Watchman Active (Decoupled Feature Architecture)");
    }

    private void sendStatus(String status) {
        Intent intent = new Intent(ACTION_STATUS_UPDATE);
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
        Log.d(TAG, "Status Update: " + status);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

            rollingBuffer[bufferIndex] = magnitude;
            bufferIndex = (bufferIndex + 1) % rollingBuffer.length;

            if (bufferIndex == 0) {
                long currentTime = System.currentTimeMillis();
                if (lastBufferTime != 0) {
                    double durationSec = (currentTime - lastBufferTime) / 1000.0;
                    if (durationSec > 0) {
                        currentFs = 300.0 / durationSec;
                    }
                }
                lastBufferTime = currentTime;

                final float[] snapshot = rollingBuffer.clone();
                final double fs = currentFs;

                backgroundHandler.post(() -> processSensorSnapshot(snapshot, fs));
            }
        }
    }

    private void processSensorSnapshot(float[] snapshot, double fs) {
        BehavioralEvent event = FeatureExtractor.analyzeBuffer(snapshot, fs);
        if (event != null && "HIGH_REPETITIVE_MOTION".equals(event.getEventType())) {
            Log.d(TAG, String.format(Locale.US, "Behavioral Event Detected: %s (Confidence: %.2f)",
                    event.getEventType(), event.getConfidence()));
            if (agent != null) {
                agent.onBehavioralEvent(event);
            }
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        if (backgroundThread != null) backgroundThread.quitSafely();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        Log.d(TAG, "WatchmanService destroyed.");
    }
}
