# Safety & Intervention Guidelines

## Critical Boundaries
- **No Diagnosis**: The model MUST NOT diagnose clinical mental health conditions.
- **Supportive Interventions Only**: Provide grounding techniques, deep breathing guidance, and conscious-friction overlays.
- **Human Escalation**: Escalate severe crisis signals to human/professional crisis helpline numbers directly through system notifications.
- **Strict Allowlist**: Reject any action string outside the approved enum set (`SHOW_OVERLAY`, `SHOW_BREATH_GATE`, `VIBRATE`, `SHOW_NOTIFICATION`, `OPEN_JOURNAL`, `START_BREATHING_EXERCISE`).
