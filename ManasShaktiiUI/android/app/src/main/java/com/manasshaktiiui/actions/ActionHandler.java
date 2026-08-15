package com.manasshaktiiui.actions;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.manasshaktiiui.ai.ActionValidator;
import com.manasshaktiiui.ai.StructuredAction;
import com.manasshaktiiui.bridge.ManasShaktiiModule;

/**
 * Main Android OS Action Dispatcher.
 * Receives validated actions and routes them strictly to approved Android OS interaction components.
 * Arbitrary shell commands, arbitrary intents, and unapproved OS calls are strictly blocked.
 */
public class ActionHandler {

    private static final String TAG = "ActionHandler";

    public static void execute(Context context, StructuredAction action) {
        if (context == null || action == null) return;

        // Perform strict safety validation before dispatching to Android OS
        if (!ActionValidator.isValid(action)) {
            Log.e(TAG, "ActionHandler blocked invalid or unallowlisted action: " + action.getAction());
            return;
        }

        String actionName = action.getAction();
        String message = action.getMessage();
        int duration = ActionValidator.sanitizeDuration(action, 20);

        Log.d(TAG, "Dispatching validated action to Android OS layer: " + actionName);

        new Handler(Looper.getMainLooper()).post(() -> {
            switch (actionName) {
                case "SHOW_OVERLAY":
                case "SHOW_BREATH_GATE":
                    OverlayAction.showOverlay(context, message, duration);
                    VibrationAction.triggerBreathingPulse(context);
                    break;

                case "HIDE_OVERLAY":
                    OverlayAction.dismissOverlay();
                    break;

                case "VIBRATE":
                    VibrationAction.triggerBreathingPulse(context);
                    break;

                case "SHOW_NOTIFICATION":
                    NotificationAction.showNotification(context, "MANASHAKTII Guidance", message);
                    break;

                case "OPEN_JOURNAL":
                case "START_BREATHING_EXERCISE":
                    // Notify React Native UI thread via bridge
                    ManasShaktiiModule.sendInterventionEventToJs(actionName, message);
                    NotificationAction.showNotification(context, "MANASHAKTII Mindful Exercise", message);
                    break;

                default:
                    Log.w(TAG, "Unhandled action name: " + actionName);
                    break;
            }
        });
    }
}
