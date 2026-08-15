package com.manasshaktiiui;

import com.manasshaktiiui.ai.ActionParser;
import com.manasshaktiiui.ai.ActionValidator;
import com.manasshaktiiui.ai.StructuredAction;

import org.junit.Test;
import static org.junit.Assert.*;

public class ActionValidatorTest {

    @Test
    public void testValidActionParsingAndValidation() {
        String json = "{\n" +
                "  \"message\": \"Take a deep breath.\",\n" +
                "  \"action\": \"SHOW_BREATH_GATE\",\n" +
                "  \"parameters\": {\n" +
                "    \"duration_seconds\": 20\n" +
                "  }\n" +
                "}";

        StructuredAction action = ActionParser.parse(json);
        assertTrue(action.isValid());
        assertEquals("SHOW_BREATH_GATE", action.getAction());
        assertTrue(ActionValidator.isValid(action));
    }

    @Test
    public void testUnallowlistedActionRejection() {
        String json = "{\n" +
                "  \"message\": \"Malicious command.\",\n" +
                "  \"action\": \"EXECUTE_SHELL_COMMAND\",\n" +
                "  \"parameters\": {}\n" +
                "}";

        StructuredAction action = ActionParser.parse(json);
        assertFalse(ActionValidator.isValid(action));
    }
}
