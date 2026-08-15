import React, { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList, SafeAreaView } from 'react-native';

export const JournalScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [entries, setEntries] = useState([
    { id: '1', date: 'Today, 2:15 PM', title: 'Mindful Pause', content: 'Noticed scrolling loop. Took 3 deep breaths.' }
  ]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  const save = () => {
    if (!title.trim() || !content.trim()) return;
    setEntries(prev => [{ id: Date.now().toString(), date: 'Just now', title, content }, ...prev]);
    setTitle('');
    setContent('');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack}><Text style={styles.backText}>← Back</Text></TouchableOpacity>
        <Text style={styles.headerTitle}>Local Reflection Journal</Text>
      </View>
      <View style={styles.form}>
        <TextInput style={styles.input} placeholder="Title..." placeholderTextColor="#64748B" value={title} onChangeText={setTitle} />
        <TextInput style={[styles.input, { height: 70 }]} multiline placeholder="Reflections..." placeholderTextColor="#64748B" value={content} onChangeText={setContent} />
        <TouchableOpacity style={styles.saveBtn} onPress={save}><Text style={styles.saveText}>Save Journal Entry (Offline)</Text></TouchableOpacity>
      </View>
      <FlatList
        data={entries}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text style={styles.date}>{item.date}</Text>
            <Text style={styles.cardTitle}>{item.title}</Text>
            <Text style={styles.cardContent}>{item.content}</Text>
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
  form: { padding: 16, backgroundColor: '#1E293B' },
  input: { backgroundColor: '#0F172A', color: '#F8FAFC', borderRadius: 8, padding: 12, marginBottom: 10 },
  saveBtn: { backgroundColor: '#0284C7', padding: 12, borderRadius: 8, alignItems: 'center' },
  saveText: { color: '#FFF', fontWeight: '700' },
  card: { backgroundColor: '#1E293B', borderRadius: 12, padding: 14, marginBottom: 10 },
  date: { color: '#64748B', fontSize: 12, marginBottom: 4 },
  cardTitle: { color: '#F8FAFC', fontSize: 16, fontWeight: '700' },
  cardContent: { color: '#94A3B8', fontSize: 14, marginTop: 4 }
});
