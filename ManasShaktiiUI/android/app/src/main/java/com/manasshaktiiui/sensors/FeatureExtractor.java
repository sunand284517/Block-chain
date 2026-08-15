package com.manasshaktiiui.sensors;

import android.util.Log;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Native signal processing engine responsible for FFT, DC removal, windowing,
 * spectral power extraction, and converting sensor streams to structured BehavioralEvents.
 */
public class FeatureExtractor {

    private static final String TAG = "FeatureExtractor";

    /**
     * Analyzes raw accelerometer buffer snapshot and computes spectral power in the 1.0 - 3.5 Hz band.
     *
     * @param dataBuffer Snapshot array of sensor magnitudes (e.g. 300 samples)
     * @param fs Measured sampling frequency in Hz
     * @return BehavioralEvent if threshold met/analyzed, or null if insufficient activity.
     */
    public static BehavioralEvent analyzeBuffer(float[] dataBuffer, double fs) {
        if (dataBuffer == null || dataBuffer.length < 256 || fs <= 0) {
            return null;
        }

        // Standard FFT requires power-of-two size (N = 256)
        int N = 256;
        double[] data = new double[N];
        double mean = 0;

        for (int i = 0; i < N; i++) {
            data[i] = dataBuffer[i];
            mean += data[i];
        }
        mean /= (double) N;

        double varianceSum = 0;
        for (int i = 0; i < N; i++) {
            data[i] -= mean; // DC Offset Removal
            varianceSum += data[i] * data[i];
            // Apply Hanning Window to reduce spectral leakage
            double window = 0.5 * (1.0 - Math.cos(2.0 * Math.PI * i / (N - 1)));
            data[i] *= window;
        }
        double accelVariance = varianceSum / N;

        try {
            FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] result = transformer.transform(data, TransformType.FORWARD);

            // Dynamic bin mapping: binIndex = freq * N / fs
            int startBin = (int) Math.round(1.0 * N / fs);
            int endBin = (int) Math.round(3.5 * N / fs);
            startBin = Math.max(1, startBin);
            endBin = Math.min(N / 2 - 1, endBin);

            double powerInRange = 0;
            for (int i = startBin; i <= endBin; i++) {
                powerInRange += result[i].abs();
            }

            Log.d(TAG, String.format(Locale.US, "FFT Power (1-3.5Hz): %.2f, Variance: %.4f, Fs: %.1fHz",
                    powerInRange, accelVariance, fs));

            // Structured features map
            Map<String, Double> features = new HashMap<>();
            features.put("motion_power", powerInRange);
            features.put("accel_variance", accelVariance);
            features.put("sampling_rate", fs);

            if (powerInRange > 8.0) {
                double confidence = Math.min(1.0, 0.5 + (powerInRange - 8.0) / 20.0);
                return new BehavioralEvent("HIGH_REPETITIVE_MOTION", confidence, 30, features);
            } else {
                return new BehavioralEvent("NORMAL_MOTION", 0.95, 0, features);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error executing FFT calculation", e);
            return null;
        }
    }
}
