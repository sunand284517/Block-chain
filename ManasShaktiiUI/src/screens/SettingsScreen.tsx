import React from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView, ScrollView } from 'react-native';

export const SettingsScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}><Text style={styles.backText}>← Back</Text></TouchableOpacity>
        <Text style={styles.headerTitle}>Settings & Local AI Model</Text>
      </View>
      <ScrollView contentContainerStyle={{ padding: 16 }}>
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Model Runtime Settings</Text>
          <Text style={styles.itemTitle}>Active Runtime Implementation</Text>
          <Text style={styles.itemValue}>MediaPipe / Gemma 2B GPU INT4</Text>
          <Text style={styles.desc}>Model files loaded strictly from local storage path without runtime network dependencies.</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Sensor & FFT Engine</Text>
          <Text style={styles.itemTitle}>Sampling Frequency Target</Text>
          <Text style={styles.itemValue}>50 Hz (SensorManager.SENSOR_DELAY_UI)</Text>
          <Text style={styles.itemTitle}>FFT Trigger Threshold</Text>
          <Text style={styles.itemValue}>8.0 Spectral Power (1.0 - 3.5 Hz)</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0F172A' },
  header: { flexDirection: 'row', alignItems: 'center', padding: 16, borderBottomWidth: 1, borderBottomColor: '#1E293B' },
  backText: { color: '#38BDF8', fontSize: 16, marginRight: 12 },
  headerTitle: { fontSize: 18, fontWeight: '700', color: '#F8FAFC' },
  card: { backgroundColor: '#1E293B', borderRadius: 14, padding: 16, marginBottom: 14 },
  sectionTitle: { color: '#38BDF8', fontSize: 16, fontWeight: '700', marginBottom: 10 },
  itemTitle: { color: '#F8FAFC', fontSize: 14, fontWeight: '600', marginTop: 6 },
  itemValue: { color: '#10B981', fontSize: 14, marginBottom: 4 },
  desc: { color: '#64748B', fontSize: 12, marginTop: 4 }
});
