# MANASHAKTII Model & Dataset Development Suite (`ml/`)

This directory contains the Python pipeline for training, converting, and quantizing custom supportive intervention LLMs for the MANASHAKTII Android application.

## Directory Layout
- `datasets/`: Dataset schema definitions and synthetic prompt generator.
- `preprocessing/`: Formatting scripts for formatting conversations into chat templates.
- `training/`: QLoRA / LoRA fine-tuning scripts.
- `evaluation/`: Benchmark scripts for evaluating JSON schema compliance and safety boundaries.
- `conversion/`: Scripts to convert PyTorch / HuggingFace checkpoints to MediaPipe / LiteRT model formats.
- `quantization/`: INT4 / INT8 quantization tooling.
- `configs/`: Hyperparameters and model training configurations.

## Core Directives for Dataset & Fine-Tuning
1. **Supportive Interventions Only**: The model acts as a grounding & conscious friction agent.
2. **NO Medical/Psychiatric Diagnosis**: The model MUST NOT attempt to diagnose, prescribe, or offer psychiatric treatment.
3. **Structured JSON Output**: Every output MUST strictly follow the `StructuredAction` schema (`message`, `action`, `parameters`).
4. **Safety Escalation**: When severe distress or self-harm signals are present, the model MUST select `SHOW_NOTIFICATION` / `SHOW_OVERLAY` with human support escalation resources.
