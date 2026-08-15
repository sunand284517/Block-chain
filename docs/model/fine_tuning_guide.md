# Local Fine-Tuning Guide for ManasShaktii

## Fine-Tuning Workflow
1. Crate synthetic training prompts using `python ml/datasets/generate_synthetic_dataset.py`.
2. Configure parameters in `ml/configs/train_config.yaml`.
3. Launch QLoRA fine-tuning script `python ml/training/train_qlora.py`.
4. Evaluate action allowlist compliance with `python ml/evaluation/eval_action_compliance.py`.
5. Quantize model to INT4 with `python ml/quantization/quantize_model.py`.
6. Convert to MediaPipe `.bin` / `.task` format with `python ml/conversion/convert_to_mediapipe.py`.
7. Place converted model file into `ManasShaktiiUI/android/app/src/main/assets/`.
