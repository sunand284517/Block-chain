import React from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView, ScrollView } from 'react-native';

export const PrivacyStatusScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const guarantees = [
    { title: 'Sensor Processing', status: '100% On-Device' },
    { title: 'FFT Signal Analysis', status: '100% On-Device' },
    { title: 'Feature Extraction', status: '100% On-Device' },
    { title: 'LLM Inference Engine', status: '100% On-Device' },
    { title: 'Action Validation & Allowlist', status: '100% On-Device' },
    { title: 'Window Overlays & Haptics', status: '100% On-Device' },
    { title: 'Local Reflection Journal', status: '100% On-Device' },
    { title: 'Cloud & Internet Runtime', status: 'ZERO Dependency' }
  ];

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}><Text style={styles.backText}>← Back</Text></TouchableOpacity>
        <Text style={styles.headerTitle}>Privacy & Offline Guarantees</Text>
      </View>
      <ScrollView contentContainerStyle={{ padding: 16 }}>
        <View style={styles.banner}>
          <Text style={styles.bannerTitle}>Zero Cloud Runtime Dependency</Text>
          <Text style={styles.bannerSub}>
            MANASHAKTII operates strictly offline on your Android hardware. No raw sensor data or user prompts ever leave your device.
          </Text>
        </View>

        {guarantees.map((g, index) => (
          <View key={index} style={styles.row}>
            <Text style={styles.title}>{g.title}</Text>
            <View style={styles.tag}><Text style={styles.tagText}>{g.status}</Text></View>
          </View>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0F172A' },
  header: { flexDirection: 'row', alignItems: 'center', padding: 16, borderBottomWidth: 1, borderBottomColor: '#1E293B' },
  backText: { color: '#38BDF8', fontSize: 16, marginRight: 12 },
  headerTitle: { fontSize: 18, fontWeight: '700', color: '#F8FAFC' },
  banner: { backgroundColor: '#1E293B', padding: 18, borderRadius: 14, marginBottom: 16, borderLeftWidth: 4, borderLeftColor: '#10B981' },
  bannerTitle: { color: '#F8FAFC', fontSize: 18, fontWeight: '700', marginBottom: 6 },
  bannerSub: { color: '#94A3B8', fontSize: 14, lineHeight: 20 },
  row: { backgroundColor: '#1E293B', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 14, borderRadius: 10, marginBottom: 8 },
  title: { color: '#E2E8F0', fontSize: 14, fontWeight: '600' },
  tag: { backgroundColor: '#064E3B', paddingHorizontal: 10, paddingVertical: 4, borderRadius: 8 },
  tagText: { color: '#34D399', fontSize: 12, fontWeight: '700' }
});
