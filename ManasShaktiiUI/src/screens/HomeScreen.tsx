import React, { useEffect, useState } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  ScrollView,
  SafeAreaView,
} from 'react-native';
import { NativeModuleService } from '../services/NativeModuleService';
import { MonitoringStatus, BehavioralEvent } from '../types';

export const HomeScreen: React.FC<{ onNavigate: (screen: string) => void }> = ({
  onNavigate,
}) => {
  const [status, setStatus] = useState<MonitoringStatus>({
    isMonitoringActive: false,
    isOfflineMode: true,
    localRuntime: 'MediaPipe/Gemma 2B INT4',
  });
  const [lastEvent, setLastEvent] = useState<BehavioralEvent | null>(null);

  useEffect(() => {
    NativeModuleService.getMonitoringStatus().then(setStatus);

    const sub = NativeModuleService.subscribeBehavioralEvents(event => {
      setLastEvent(event);
    });

    return () => sub.remove();
  }, []);

  const toggleMonitoring = async () => {
    if (status.isMonitoringActive) {
      await NativeModuleService.stopMonitoring();
    } else {
      await NativeModuleService.startMonitoring();
    }
    const updated = await NativeModuleService.getMonitoringStatus();
    setStatus(updated);
  };

  const triggerTestIntervention = async () => {
    await NativeModuleService.triggerManualIntervention();
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scroll}>
        <View style={styles.header}>
          <Text style={styles.appTitle}>MANASHAKTII</Text>
          <View style={styles.badge}>
            <Text style={styles.badgeText}>100% OFF-DEVICE LOCAL AI</Text>
          </View>
        </View>

        {/* Status Card */}
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Watchman Sensor Service</Text>
          <View style={styles.statusRow}>
            <View
              style={[
                styles.dot,
                { backgroundColor: status.isMonitoringActive ? '#10B981' : '#EF4444' },
              ]}
            />
            <Text style={styles.statusText}>
              {status.isMonitoringActive ? 'Active Monitoring' : 'Service Stopped'}
            </Text>
          </View>
          <Text style={styles.metaText}>Model Engine: {status.localRuntime}</Text>

          <TouchableOpacity style={styles.button} onPress={toggleMonitoring}>
            <Text style={styles.buttonText}>
              {status.isMonitoringActive ? 'Stop Watchman Service' : 'Start Watchman Service'}
            </Text>
          </TouchableOpacity>
        </View>

        {/* Behavioral Activity Card */}
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Behavioral Event Monitor</Text>
          {lastEvent ? (
            <View>
              <Text style={styles.eventText}>Event: {lastEvent.event}</Text>
              <Text style={styles.metaText}>
                Confidence: {(lastEvent.confidence * 100).toFixed(0)}% | Duration: {lastEvent.duration}s
              </Text>
            </View>
          ) : (
            <Text style={styles.metaText}>
              No repetitive motion detected. Gyroscope & Accelerometer sampling active.
            </Text>
          )}
          <TouchableOpacity style={styles.secondaryButton} onPress={triggerTestIntervention}>
            <Text style={styles.secondaryButtonText}>Trigger Test Mindful Intervention</Text>
          </TouchableOpacity>
        </View>

        {/* Navigation Shortcut Grid */}
        <Text style={styles.sectionHeader}>Quick Actions</Text>
        <View style={styles.grid}>
          <TouchableOpacity style={styles.gridCard} onPress={() => onNavigate('AIChat')}>
            <Text style={styles.gridIcon}>💬</Text>
            <Text style={styles.gridTitle}>AI Companion</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.gridCard} onPress={() => onNavigate('Breathing')}>
            <Text style={styles.gridIcon}>🫁</Text>
            <Text style={styles.gridTitle}>Breath Gate</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.gridCard} onPress={() => onNavigate('Journal')}>
            <Text style={styles.gridIcon}>📓</Text>
            <Text style={styles.gridTitle}>Journal</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.gridCard} onPress={() => onNavigate('Insights')}>
            <Text style={styles.gridIcon}>📊</Text>
            <Text style={styles.gridTitle}>Behavior Insights</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.gridCard} onPress={() => onNavigate('History')}>
            <Text style={styles.gridIcon}>📜</Text>
            <Text style={styles.gridTitle}>Interventions</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.gridCard} onPress={() => onNavigate('Privacy')}>
            <Text style={styles.gridIcon}>🔒</Text>
            <Text style={styles.gridTitle}>Offline Status</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0F172A' },
  scroll: { padding: 20 },
  header: { marginBottom: 20, alignItems: 'center' },
  appTitle: { fontSize: 28, fontWeight: '800', color: '#F8FAFC', letterSpacing: 1 },
  badge: { backgroundColor: '#1E293B', paddingHorizontal: 12, paddingVertical: 4, borderRadius: 12, marginTop: 6 },
  badgeText: { color: '#38BDF8', fontSize: 11, fontWeight: '700' },
  card: { backgroundColor: '#1E293B', borderRadius: 16, padding: 18, marginBottom: 16, borderBackdrop: 'solid' },
  cardTitle: { fontSize: 18, fontWeight: '700', color: '#F8FAFC', marginBottom: 10 },
  statusRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 8 },
  dot: { width: 10, height: 10, borderRadius: 5, marginRight: 8 },
  statusText: { color: '#E2E8F0', fontSize: 15, fontWeight: '600' },
  metaText: { color: '#94A3B8', fontSize: 13, marginBottom: 12 },
  eventText: { color: '#38BDF8', fontSize: 15, fontWeight: '700', marginBottom: 4 },
  button: { backgroundColor: '#0284C7', paddingVertical: 12, borderRadius: 10, alignItems: 'center' },
  buttonText: { color: '#FFFFFF', fontWeight: '700', fontSize: 15 },
  secondaryButton: { backgroundColor: '#334155', paddingVertical: 10, borderRadius: 10, alignItems: 'center', marginTop: 8 },
  secondaryButtonText: { color: '#38BDF8', fontWeight: '600', fontSize: 14 },
  sectionHeader: { fontSize: 18, fontWeight: '700', color: '#F8FAFC', marginVertical: 12 },
  grid: { flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-between' },
  gridCard: { backgroundColor: '#1E293B', width: '48%', borderRadius: 14, padding: 16, alignItems: 'center', marginBottom: 12 },
  gridIcon: { fontSize: 26, marginBottom: 6 },
  gridTitle: { color: '#E2E8F0', fontWeight: '600', fontSize: 14 },
});
