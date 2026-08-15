package com.manasshaktiiui.ai;

import android.util.Log;
import org.json.JSONObject;

/**
 * Parses raw text responses from the local model into StructuredAction objects.
 */
public class ActionParser {

    private static final String TAG = "ActionParser";

    public static StructuredAction parse(String rawOutput) {
        if (rawOutput == null || rawOutput.trim().isEmpty()) {
            Log.w(TAG, "Raw LLM output is empty.");
            return new StructuredAction("Pause for a moment.", "SHOW_OVERLAY", new JSONObject(), false);
        }

        try {
            String cleaned = rawOutput.trim();
            // Extract JSON substring if model wrapped response in markdown fence
            if (cleaned.contains("```json")) {
                int start = cleaned.indexOf("```json") + 7;
                int end = cleaned.indexOf("```", start);
                if (end > start) {
                    cleaned = cleaned.substring(start, end).trim();
                }
            } else if (cleaned.contains("```")) {
                int start = cleaned.indexOf("```") + 3;
                int end = cleaned.indexOf("```", start);
                if (end > start) {
                    cleaned = cleaned.substring(start, end).trim();
                }
            }

            int firstBrace = cleaned.indexOf('{');
            int lastBrace = cleaned.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace > firstBrace) {
                cleaned = cleaned.substring(firstBrace, lastBrace + 1);
            }

            JSONObject json = new JSONObject(cleaned);
            String message = json.optString("message", "Take a mindful pause.");
            String action = json.optString("action", "SHOW_OVERLAY");
            JSONObject params = json.optJSONObject("parameters");
            if (params == null) params = new JSONObject();

            return new StructuredAction(message, action, params, true);

        } catch (Exception e) {
            Log.e(TAG, "Failed to parse JSON action from LLM raw output: " + rawOutput, e);
            // Return fallback structured action
            return new StructuredAction(rawOutput.length() > 60 ? rawOutput.substring(0, 60) : rawOutput,
                    "SHOW_OVERLAY", new JSONObject(), false);
        }
    }
}
