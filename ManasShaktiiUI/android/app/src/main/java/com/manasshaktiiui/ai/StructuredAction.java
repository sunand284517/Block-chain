package com.manasshaktiiui.ai;

import org.json.JSONObject;

/**
 * Parsed structured representation of an agent action produced by local model inference.
 */
public class StructuredAction {
    private final String message;
    private final String action;
    private final JSONObject parameters;
    private final boolean isValid;

    public StructuredAction(String message, String action, JSONObject parameters, boolean isValid) {
        this.message = message != null ? message : "";
        this.action = action != null ? action : "UNKNOWN";
        this.parameters = parameters != null ? parameters : new JSONObject();
        this.isValid = isValid;
    }

    public String getMessage() {
        return message;
    }

    public String getAction() {
        return action;
    }

    public JSONObject getParameters() {
        return parameters;
    }

    public boolean isValid() {
        return isValid;
    }

    public int getIntParam(String key, int defaultValue) {
        if (parameters != null && parameters.has(key)) {
            return parameters.optInt(key, defaultValue);
        }
        return defaultValue;
    }
}
