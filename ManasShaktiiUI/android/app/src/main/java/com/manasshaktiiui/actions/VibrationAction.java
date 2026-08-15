package com.manasshaktiiui.actions;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

/**
 * Android Haptic and Vibration Action handler.
 */
public class VibrationAction {

    private static final String TAG = "VibrationAction";

    public static void triggerBreathingPulse(Context context) {
        try {
            Vibrator vibrator = getVibrator(context);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Gentle breathing pulse pattern (e.g. 200ms pulse, 300ms pause, 200ms pulse)
                    long[] timings = new long[]{0, 200, 300, 200};
                    int[] amplitudes = new int[]{0, 128, 0, 180};
                    VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, -1);
                    vibrator.vibrate(effect);
                } else {
                    vibrator.vibrate(400);
                }
                Log.d(TAG, "Breathing pulse vibration triggered.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error executing vibration effect", e);
        }
    }

    private static Vibrator getVibrator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return manager != null ? manager.getDefaultVibrator() : null;
        } else {
            return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }
}
