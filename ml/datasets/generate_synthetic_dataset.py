import json
import random

ACTIONS = [
    "SHOW_OVERLAY",
    "SHOW_BREATH_GATE",
    "VIBRATE",
    "SHOW_NOTIFICATION",
    "OPEN_JOURNAL",
    "START_BREATHING_EXERCISE"
]

GROUNDING_MESSAGES = [
    "Take a slow breath in and feel your feet on the floor.",
    "Notice the present moment. Release the tension in your hands.",
    "Pause for 10 seconds and look around your environment.",
    "Reflect on how you feel right now in your reflection journal.",
    "Let's pause together and take a deep breath."
]

def generate_synthetic_samples(num_samples=100):
    samples = []
    for i in range(num_samples):
        intensity = round(random.uniform(8.5, 25.0), 2)
        duration = random.randint(15, 120)
        action = random.choice(ACTIONS)
        msg = random.choice(GROUNDING_MESSAGES)
        
        sample = {
            "instruction": "You are ManasShaktii, a supportive intervention agent. Produce a JSON structured action.",
            "input_features": {
                "event": "HIGH_REPETITIVE_MOTION",
                "motion_power": intensity,
                "duration": duration
            },
            "output_action": {
                "message": msg,
                "action": action,
                "parameters": {
                    "duration_seconds": random.randint(15, 45)
                }
            }
        }
        samples.append(sample)
    return samples

if __name__ == "__main__":
    data = generate_synthetic_samples(200)
    with open("ml/datasets/manashaktii_train.json", "w") as f:
        json.dump(data, f, indent=2)
    print(f"Generated {len(data)} synthetic training examples in ml/datasets/manashaktii_train.json")
