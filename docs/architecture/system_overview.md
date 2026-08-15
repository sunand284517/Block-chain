# MANASHAKTII Architectural Specification

## Core Principles
1. **100% On-Device Execution**: Zero external cloud or API server dependencies.
2. **Decoupled Sensor Engine**: `WatchmanService` extracts FFT spectral power features independently of model runtimes.
3. **Replaceable Local Model Runtime**: `LocalModelRuntime` interface isolates model libraries (MediaPipe GenAI, LiteRT, ONNX Runtime Mobile).
4. **Structured Action Allowlist**: Model outputs are parsed, schema-validated, and safety-checked before reaching the Android OS layer.
5. **Offline Data Storage**: Local reflection journals, intervention logs, and behavioral stats are persisted strictly in encrypted device storage.

## Data Flow Pipeline
```
SensorManager (Accel/Gyro) -> Rolling Buffer -> FFT Spectral Analysis -> BehavioralEvent
 -> ManasShaktiiAgent -> PromptBuilder -> LocalModelRuntime (MediaPipe)
 -> ActionParser -> ActionValidator -> ActionHandler -> Android OS (Overlay / Vibration / Notification)
```
