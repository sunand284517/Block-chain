package com.manasshaktiiui.ai;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Model file asset extractor and storage manager.
 * Ensures quantized model weights exist on device storage before runtime initialization.
 */
public class ModelManager {

    private static final String TAG = "ModelManager";
    public static final String DEFAULT_MODEL_FILE = "gemma-2b-it-gpu-int4.bin";
    private static final long EXPECTED_MODEL_SIZE = 1354301440L; // Example Gemma 2B int4 size

    public static File getModelFile(Context context, String filename) {
        return new File(context.getFilesDir(), filename);
    }

    public static boolean isModelReady(Context context, String filename) {
        File file = getModelFile(context, filename);
        return file.exists() && file.length() > 0;
    }

    public static void ensureModelExtracted(Context context, String filename, Runnable onComplete) {
        new Thread(() -> {
            try {
                File modelFile = getModelFile(context, filename);
                if (modelFile.exists() && modelFile.length() > 0) {
                    Log.d(TAG, "Model file already exists on device. Size: " + modelFile.length());
                    if (onComplete != null) onComplete.run();
                    return;
                }

                Log.d(TAG, "Extracting model asset to internal storage: " + filename);
                File tempFile = new File(modelFile.getParent(), filename + ".tmp");
                try (InputStream is = context.getAssets().open(filename);
                     FileOutputStream os = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[131072];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                }

                if (tempFile.exists() && tempFile.length() > 0) {
                    if (tempFile.renameTo(modelFile)) {
                        Log.d(TAG, "Model asset extraction completed successfully.");
                    } else {
                        Log.e(TAG, "Failed to rename temporary model file.");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Model asset extraction skipped or failed (model can be provided via external placement)", e);
            } finally {
                if (onComplete != null) onComplete.run();
            }
        }).start();
    }
}
