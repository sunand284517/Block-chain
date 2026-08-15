package com.manasshaktiiui.ai;

import android.content.Context;
import android.util.Log;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions;

import java.io.File;

/**
 * Concrete LocalModelRuntime implementation powered by MediaPipe GenAI LlmInference.
 * All MediaPipe specific imports, configurations, options, and file paths are strictly isolated inside this class.
 */
public class MediaPipeModelRuntime implements LocalModelRuntime {

    private static final String TAG = "MediaPipeRuntime";
    private final String modelFilename;
    private File modelFile;
    private boolean isInitialized = false;

    public MediaPipeModelRuntime() {
        this(ModelManager.DEFAULT_MODEL_FILE);
    }

    public MediaPipeModelRuntime(String modelFilename) {
        this.modelFilename = modelFilename;
    }

    @Override
    public String getRuntimeName() {
        return "MediaPipe GenAI / Gemma INT4";
    }

    @Override
    public String getRuntimeStatus() {
        if (isReady()) return "Ready";
        if (modelFile != null && !modelFile.exists()) return "Model file missing: " + modelFilename;
        return "Uninitialized";
    }

    @Override
    public void initialize(Context context, InitializationCallback callback) {
        modelFile = ModelManager.getModelFile(context, modelFilename);
        if (!modelFile.exists()) {
            ModelManager.ensureModelExtracted(context, modelFilename, () -> {
                isInitialized = modelFile.exists();
                if (callback != null) {
                    if (isInitialized) callback.onSuccess();
                    else callback.onError(new Exception("Model file missing: " + modelFilename));
                }
            });
        } else {
            isInitialized = true;
            if (callback != null) callback.onSuccess();
        }
    }

    @Override
    public String generate(String prompt) throws Exception {
        if (modelFile == null || !modelFile.exists()) {
            throw new IllegalStateException("Model file not ready: " + modelFilename);
        }

        Log.d(TAG, "Spinning up MediaPipe LlmInference instance...");
        LlmInferenceOptions options = LlmInferenceOptions.builder()
                .setModelPath(modelFile.getAbsolutePath())
                .setMaxTokens(128)
                .setPreferredBackend(LlmInference.Backend.GPU)
                .build();

        LlmInference llmInference = null;
        try {
            llmInference = LlmInference.createFromOptions(null, options);
            Log.d(TAG, "Generating local response from prompt...");
            String result = llmInference.generateResponse(prompt);
            Log.d(TAG, "Local generation complete.");
            return result;
        } finally {
            if (llmInference != null) {
                try {
                    llmInference.close();
                    Log.d(TAG, "LlmInference instance closed. Memory reclaimed.");
                } catch (Exception e) {
                    Log.w(TAG, "Error closing LlmInference", e);
                }
            }
        }
    }

    @Override
    public boolean isReady() {
        return isInitialized && modelFile != null && modelFile.exists();
    }

    @Override
    public void release() {
        isInitialized = false;
    }
}
