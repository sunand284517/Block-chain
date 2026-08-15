package com.manasshaktiiui.ai;

import android.content.Context;
import android.util.Log;

/**
 * Lightweight Fallback Local Model Runtime.
 * Operates instantly without needing gigabyte-sized binary weight files.
 * Guarantees zero downtime, zero network dependency, and instant responses when large weights are absent or loading.
 */
public class FallbackModelRuntime implements LocalModelRuntime {

    private static final String TAG = "FallbackModelRuntime";
    private boolean isReady = true;

    @Override
    public String getRuntimeName() {
        return "Fallback Rule Engine (Offline)";
    }

    @Override
    public String getRuntimeStatus() {
        return "Active Fallback";
    }

    @Override
    public void initialize(Context context, InitializationCallback callback) {
        isReady = true;
        Log.d(TAG, "Fallback model runtime active and ready.");
        if (callback != null) callback.onSuccess();
    }

    @Override
    public String generate(String prompt) throws Exception {
        Log.d(TAG, "Generating structured response via Fallback Model Engine...");
        return PromptBuilder.buildFallbackJson();
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void release() {
        isReady = false;
    }
}
