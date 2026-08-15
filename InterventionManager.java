package com.example.myapplication;

import android.content.Context;
import com.google.mediapipe.tasks.genai.llminference.LlmInference;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class InterventionManager {
    private LlmInference llmInference = null;

    private String getModelPath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        if (!file.exists()) {
            try (InputStream in = context.getAssets().open(assetName);
                 OutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    public synchronized void triggerIntervention(Context context) {
        if (llmInference == null) {
            try {
                String modelPath = getModelPath(context, "gemma-2b-it-cpu-int4.task");
                LlmInference.LlmInferenceOptions options = LlmInference.LlmInferenceOptions
                        .builder()
                        .setModelPath(modelPath)
                        .setMaxTokens(256)
                        .build();

                llmInference = LlmInference.createFromOptions(context, options);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        // Generate response when triggered
        if (llmInference != null) {
            String response = llmInference.generateResponse("Provide a gentle breathing prompt.");
            // Pass this response to your overlay UI or log it
        }

        // Immediately close model to free RAM
        closeModel();
    }

    public synchronized void closeModel() {
        if (llmInference != null) {
            llmInference.close();
            llmInference = null;
        }
    }
}