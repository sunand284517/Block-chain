"""
Evaluates fine-tuned LLM output against the ManasShaktii Action Allowlist and JSON Schema.
"""

import json

APPROVED_ACTIONS = {
    "SHOW_OVERLAY", "HIDE_OVERLAY", "SHOW_BREATH_GATE",
    "VIBRATE", "SHOW_NOTIFICATION", "OPEN_JOURNAL", "START_BREATHING_EXERCISE"
}

def evaluate_model_outputs(predictions_file):
    print("=== ManasShaktii Action Allowlist Compliance Evaluation ===")
    total = 0
    valid_json = 0
    valid_action = 0

    with open(predictions_file, "r") as f:
        data = json.load(f)

    for item in data:
        total += 1
        raw_pred = item.get("prediction", "")
        try:
            parsed = json.loads(raw_pred)
            valid_json += 1
            if parsed.get("action") in APPROVED_ACTIONS:
                valid_action += 1
        except Exception:
            pass

    print(f"Total Evaluated: {total}")
    print(f"Valid JSON Rate: {(valid_json / total) * 100:.2f}%")
    print(f"Allowlisted Action Compliance: {(valid_action / total) * 100:.2f}%")

if __name__ == "__main__":
    print("Run evaluation script with predictions JSON file.")
