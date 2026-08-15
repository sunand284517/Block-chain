import React from 'react';
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, SafeAreaView } from 'react-native';

export const InsightsScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}><Text style={styles.backText}>← Back</Text></TouchableOpacity>
        <Text style={styles.headerTitle}>Behavioral Insights (Local FFT)</Text>
      </View>
      <ScrollView contentContainerStyle={{ padding: 16 }}>
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Spectral Motion Power (1.0 - 3.5 Hz)</Text>
          <Text style={styles.statValue}>12.4 FFT Power</Text>
          <Text style={styles.statLabel}>Repetitive motion threshold set at 8.0 FFT Power</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitle}>Accelerometer Variance</Text>
          <Text style={styles.statValue}>0.71 m/s²</Text>
          <Text style={styles.statLabel}>Calculated over 300 rolling samples</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitle}>Daily Interventions Summary</Text>
          <Text style={styles.statValue}>3 Mindful Pauses</Text>
          <Text style={styles.statLabel}>All feature processing & action validation local</Text>
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
  card: { backgroundColor: '#1E293B', borderRadius: 14, padding: 18, marginBottom: 14 },
  cardTitle: { color: '#94A3B8', fontSize: 14, fontWeight: '600' },
  statValue: { color: '#38BDF8', fontSize: 26, fontWeight: '800', marginVertical: 6 },
  statLabel: { color: '#64748B', fontSize: 13 }
});
