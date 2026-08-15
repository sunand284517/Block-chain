package com.manasshaktiiui.ai;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.manasshaktiiui.actions.ActionHandler;
import com.manasshaktiiui.sensors.BehavioralEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Core Local AI Agent coordinator.
 * Coordinates feature event processing, prompt generation, local runtime model inference,
 * output parsing, safety validation, and OS action dispatch.
 */
public class ManasShaktiiAgent {

    private static final String TAG = "ManasShaktiiAgent";
    private static ManasShaktiiAgent instance;

    private final Context context;
    private LocalModelRuntime modelRuntime;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private long lastInterventionTime = 0;
    private static final long COOLDOWN_MS = 60000; // 60s cooldown between AI interventions

    private final HandlerThread agentWorkerThread;
    private final Handler agentHandler;

    private ManasShaktiiAgent(Context context) {
        this.context = context.getApplicationContext();
        // Default runtime set to MediaPipeModelRuntime; replaceable with any LocalModelRuntime implementation
        this.modelRuntime = new MediaPipeModelRuntime();

        agentWorkerThread = new HandlerThread("ManasAgentWorker");
        agentWorkerThread.start();
        agentHandler = new Handler(agentWorkerThread.getLooper());

        initializeRuntime();
    }

    public static synchronized ManasShaktiiAgent getInstance(Context context) {
        if (instance == null) {
            instance = new ManasShaktiiAgent(context);
        }
        return instance;
    }

    public void setModelRuntime(LocalModelRuntime runtime) {
        if (runtime != null) {
            if (this.modelRuntime != null) {
                this.modelRuntime.release();
            }
            this.modelRuntime = runtime;
            initializeRuntime();
        }
    }

    private void initializeRuntime() {
        if (modelRuntime != null) {
            modelRuntime.initialize(context, new LocalModelRuntime.InitializationCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "LocalModelRuntime initialized successfully.");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.w(TAG, "LocalModelRuntime initialization deferred/failed: " + throwable.getMessage());
                }
            });
        }
    }

    /**
     * Entry point for incoming behavioral events from WatchmanService.
     */
    public void onBehavioralEvent(BehavioralEvent event) {
        if (event == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastInterventionTime < COOLDOWN_MS) {
            Log.d(TAG, "Skipping event processing: Cooldown active.");
            return;
        }

        if (isProcessing.compareAndSet(false, true)) {
            lastInterventionTime = currentTime;
            agentHandler.post(() -> processEvent(event));
        } else {
            Log.d(TAG, "Agent busy with active inference task.");
        }
    }

    private void processEvent(BehavioralEvent event) {
        try {
            Log.d(TAG, "Processing behavioral event: " + event.getEventType());
            String prompt = PromptBuilder.buildInterventionPrompt(event);

            String rawOutput = null;
            if (modelRuntime != null && modelRuntime.isReady()) {
                try {
                    rawOutput = modelRuntime.generate(prompt);
                } catch (Exception e) {
                    Log.e(TAG, "Local runtime model inference error, using fallback prompt", e);
                }
            }

            if (rawOutput == null || rawOutput.isEmpty()) {
                rawOutput = PromptBuilder.buildFallbackJson();
            }

            StructuredAction action = ActionParser.parse(rawOutput);
            if (ActionValidator.isValid(action)) {
                ActionHandler.execute(context, action);
            } else {
                Log.w(TAG, "Action validation failed. Executing default fallback action.");
                ActionHandler.execute(context, ActionParser.parse(PromptBuilder.buildFallbackJson()));
            }

        } catch (Throwable t) {
            Log.e(TAG, "Unhandled exception during agent processing cycle", t);
        } finally {
            isProcessing.set(false);
        }
    }
}
