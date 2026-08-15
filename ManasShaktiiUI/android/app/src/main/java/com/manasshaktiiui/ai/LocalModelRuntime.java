package com.manasshaktiiui.ai;

import android.content.Context;

/**
 * Replaceable local model runtime abstraction interface.
 * Allows swapping between MediaPipe GenAI, LiteRT / TensorFlow Lite, ONNX Runtime Mobile,
 * or custom quantized engines without touching sensor or service layers.
 */
public interface LocalModelRuntime {

    interface InitializationCallback {
        void onSuccess();
        void onError(Throwable throwable);
    }

    /**
     * Initializes the local runtime asynchronously.
     */
    void initialize(Context context, InitializationCallback callback);

    /**
     * Executes local model inference synchronously or via worker thread.
     * Must execute 100% on-device with zero internet connectivity.
     */
    String generate(String prompt) throws Exception;

    /**
     * Checks if the model file exists, is valid, and runtime is initialized.
     */
    boolean isReady();

    /**
     * Releases model memory, GPU handles, and RAM resources.
     */
    void release();
}
