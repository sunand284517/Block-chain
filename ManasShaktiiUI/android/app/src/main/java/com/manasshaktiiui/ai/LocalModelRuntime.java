package com.manasshaktiiui.ai;

import android.content.Context;

/**
 * Model-Runtime Agnostic Interface.
 * Serves as the single abstraction barrier between ManasShaktiiAgent and any underlying local LLM runtime.
 * Supports MediaPipe GenAI, LiteRT / TFLite, ONNX Runtime Mobile, llama.cpp, or fallback template runtimes.
 */
public interface LocalModelRuntime {

    interface InitializationCallback {
        void onSuccess();
        void onError(Throwable throwable);
    }

    /**
     * Human-readable identifier of the active runtime (e.g. "MediaPipe/Gemma 2B", "LiteRT/SLM", "Fallback Engine").
     */
    String getRuntimeName();

    /**
     * Human-readable status description of the active runtime (e.g. "Ready", "Initializing", "Weights Missing").
     */
    String getRuntimeStatus();

    /**
     * Initializes the local model runtime asynchronously.
     */
    void initialize(Context context, InitializationCallback callback);

    /**
     * Executes local on-device model inference.
     * Must execute 100% on-device with zero network connectivity.
     */
    String generate(String prompt) throws Exception;

    /**
     * Checks if the model weights exist and runtime is ready to receive prompts.
     */
    boolean isReady();

    /**
     * Releases model memory, GPU handles, and RAM resources.
     */
    void release();
}
