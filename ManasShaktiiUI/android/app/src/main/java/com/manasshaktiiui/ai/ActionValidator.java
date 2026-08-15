package com.manasshaktiiui.ai;

import android.util.Log;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Safety and Allowlist Action Validator.
 * Enforces zero unauthorized OS actions, caps parameter bounds, and rejects untrusted LLM commands.
 */
public class ActionValidator {

    private static final String TAG = "ActionValidator";

    private static final Set<String> APPROVED_ACTIONS = new HashSet<>(Arrays.asList(
            "SHOW_OVERLAY",
            "HIDE_OVERLAY",
            "SHOW_BREATH_GATE",
            "VIBRATE",
            "SHOW_NOTIFICATION",
            "OPEN_JOURNAL",
            "START_BREATHING_EXERCISE"
    ));

    private static final int MAX_DURATION_SECONDS = 120;
    private static final int MIN_DURATION_SECONDS = 1;

    public static boolean isValid(StructuredAction action) {
        if (action == null) {
            Log.w(TAG, "Action validation failed: action is null.");
            return false;
        }

        String actionName = action.getAction();
        if (!APPROVED_ACTIONS.contains(actionName)) {
            Log.e(TAG, "Action validation REJECTED unknown or unapproved action: " + actionName);
            return false;
        }

        // Validate parameter upper/lower bounds
        if (action.getParameters() != null) {
            if (action.getParameters().has("duration_seconds")) {
                int duration = action.getParameters().optInt("duration_seconds", 20);
                if (duration < MIN_DURATION_SECONDS || duration > MAX_DURATION_SECONDS) {
                    Log.w(TAG, "Validation warning: duration_seconds out of bounds (" + duration + "). Sanitizing.");
                }
            }
        }

        Log.d(TAG, "Action VALIDATED successfully: " + actionName);
        return true;
    }

    public static int sanitizeDuration(StructuredAction action, int defaultDuration) {
        int duration = action.getIntParam("duration_seconds", defaultDuration);
        if (duration < MIN_DURATION_SECONDS) return MIN_DURATION_SECONDS;
        if (duration > MAX_DURATION_SECONDS) return MAX_DURATION_SECONDS;
        return duration;
    }
}
