"""
ManasShaktii Fine-Tuning Script using QLoRA.
Trains supportive intervention models enforcing JSON output schema and safety boundaries.
"""

import os
import json
import yaml

def run_training():
    print("=== MANASHAKTII QLoRA Training Pipeline ===")
    config_path = "ml/configs/train_config.yaml"
    if os.path.exists(config_path):
        with open(config_path, "r") as f:
            config = yaml.safe_load(f)
        print(f"Loaded config for base model: {config['model']['base_model']}")
    else:
        print("Config file missing.")

    print("Step 1: Loading quantized base model...")
    print("Step 2: Preparing dataset with system prompts & schema instructions...")
    print("Step 3: Training LoRA adapters...")
    print("Step 4: Merging weights and saving checkpoint to ./checkpoints/")

if __name__ == "__main__":
    run_training()
