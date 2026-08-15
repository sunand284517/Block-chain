package com.manasshaktiiui.ai;

import com.manasshaktiiui.sensors.BehavioralEvent;
import java.util.Locale;

/**
 * Builds structured prompts enforcing JSON schema output for the local fine-tuned model.
 */
public class PromptBuilder {

    /**
     * Formats behavioral event features into structured JSON generation prompt.
     */
    public static String buildInterventionPrompt(BehavioralEvent event) {
        double motionPower = event.getFeatures().containsKey("motion_power") ?
                event.getFeatures().get("motion_power") : 10.0;

        return String.format(Locale.US,
                "You are ManasShaktii, a compassionate, supportive on-device intervention agent. " +
                "The user is exhibiting repetitive motion (intensity: %.2f, duration: %ds). " +
                "Respond ONLY with a valid JSON object matching this structure:\n" +
                "{\n" +
                "  \"message\": \"<supportive grounding message max 12 words>\",\n" +
                "  \"action\": \"SHOW_BREATH_GATE\",\n" +
                "  \"parameters\": {\n" +
                "    \"duration_seconds\": 20\n" +
                "  }\n" +
                "}\n" +
                "Supported actions: SHOW_OVERLAY, HIDE_OVERLAY, SHOW_BREATH_GATE, VIBRATE, SHOW_NOTIFICATION, OPEN_JOURNAL, START_BREATHING_EXERCISE.\n" +
                "Do not include any conversational filler outside the JSON object.",
                motionPower, event.getDurationSeconds());
    }

    /**
     * Builds fallback JSON string if prompt building or model fails.
     */
    public static String buildFallbackJson() {
        return "{\n" +
                "  \"message\": \"Take a deep breath and ground yourself in this moment.\",\n" +
                "  \"action\": \"SHOW_BREATH_GATE\",\n" +
                "  \"parameters\": {\n" +
                "    \"duration_seconds\": 15\n" +
                "  }\n" +
                "}";
    }
}
