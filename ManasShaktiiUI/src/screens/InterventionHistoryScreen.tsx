import React from 'react';
import { StyleSheet, Text, View, FlatList, TouchableOpacity, SafeAreaView } from 'react-native';

export const InterventionHistoryScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const history = [
    { id: '1', time: '10 mins ago', action: 'SHOW_BREATH_GATE', message: 'Take a deep breath and look up.' },
    { id: '2', time: '2 hours ago', action: 'VIBRATE', message: 'Conscious friction pulse delivered.' },
    { id: '3', time: 'Yesterday', action: 'SHOW_NOTIFICATION', message: 'Mindful pause recommendation.' }
  ];

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}><Text style={styles.backText}>← Back</Text></TouchableOpacity>
        <Text style={styles.headerTitle}>Intervention History</Text>
      </View>
      <FlatList
        data={history}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text style={styles.time}>{item.time}</Text>
            <Text style={styles.action}>{item.action}</Text>
            <Text style={styles.msg}>{item.message}</Text>
          </View>
        )}
        contentContainerStyle={{ padding: 16 }}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0F172A' },
  header: { flexDirection: 'row', alignItems: 'center', padding: 16, borderBottomWidth: 1, borderBottomColor: '#1E293B' },
  backText: { color: '#38BDF8', fontSize: 16, marginRight: 12 },
  headerTitle: { fontSize: 18, fontWeight: '700', color: '#F8FAFC' },
  card: { backgroundColor: '#1E293B', borderRadius: 12, padding: 14, marginBottom: 10 },
  time: { color: '#64748B', fontSize: 12 },
  action: { color: '#10B981', fontWeight: '700', fontSize: 14, marginVertical: 4 },
  msg: { color: '#E2E8F0', fontSize: 14 }
});
