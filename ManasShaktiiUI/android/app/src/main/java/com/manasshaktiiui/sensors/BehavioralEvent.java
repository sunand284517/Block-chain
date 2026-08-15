package com.manasshaktiiui.sensors;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * Structured behavioral event produced by local sensor feature extraction engine.
 * Never send raw continuous sensor streams directly to the LLM.
 */
public class BehavioralEvent {
    private final String eventType;
    private final double confidence;
    private final long durationSeconds;
    private final Map<String, Double> features;

    public BehavioralEvent(String eventType, double confidence, long durationSeconds, Map<String, Double> features) {
        this.eventType = eventType;
        this.confidence = confidence;
        this.durationSeconds = durationSeconds;
        this.features = features != null ? features : new HashMap<>();
    }

    public String getEventType() {
        return eventType;
    }

    public double getConfidence() {
        return confidence;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public Map<String, Double> getFeatures() {
        return features;
    }

    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("event", eventType);
            json.put("confidence", confidence);
            json.put("duration", durationSeconds);
            JSONObject featObj = new JSONObject();
            for (Map.Entry<String, Double> entry : features.entrySet()) {
                featObj.put(entry.getKey(), entry.getValue());
            }
            json.put("features", featObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }
}
