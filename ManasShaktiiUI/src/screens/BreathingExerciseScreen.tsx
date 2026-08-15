import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView } from 'react-native';

export const BreathingExerciseScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [phase, setPhase] = useState<'Inhale' | 'Hold' | 'Exhale'>('Inhale');
  const [seconds, setSeconds] = useState(4);

  useEffect(() => {
    const timer = setInterval(() => {
      setSeconds(prev => {
        if (prev > 1) return prev - 1;
        setPhase(p => (p === 'Inhale' ? 'Hold' : p === 'Hold' ? 'Exhale' : 'Inhale'));
        return 4;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <TouchableOpacity onPress={onBack} style={styles.backBtn}><Text style={styles.backText}>← Close Breath Gate</Text></TouchableOpacity>
      <View style={styles.content}>
        <Text style={styles.title}>Mindful Breath Gate</Text>
        <View style={styles.circle}>
          <Text style={styles.phase}>{phase}</Text>
          <Text style={styles.counter}>{seconds}s</Text>
        </View>
        <Text style={styles.subtitle}>Slow down your movement and release tension in your shoulders.</Text>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0F172A', padding: 20 },
  backBtn: { alignSelf: 'flex-start', margin: 16 },
  backText: { color: '#38BDF8', fontSize: 16, fontWeight: '600' },
  content: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  title: { fontSize: 24, fontWeight: '800', color: '#F8FAFC', marginBottom: 40 },
  circle: { width: 200, height: 200, borderRadius: 100, backgroundColor: '#0284C7', justifyContent: 'center', alignItems: 'center', marginBottom: 40 },
  phase: { color: '#FFFFFF', fontSize: 22, fontWeight: '700' },
  counter: { color: '#E0F2FE', fontSize: 32, fontWeight: '900', marginTop: 8 },
  subtitle: { color: '#94A3B8', fontSize: 16, textAlign: 'center', paddingHorizontal: 20 }
});
