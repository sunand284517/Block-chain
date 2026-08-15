import React, { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList, SafeAreaView } from 'react-native';

export const AIChatScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [messages, setMessages] = useState([
    { id: '1', sender: 'ai', text: 'Hello! I am your on-device ManasShaktii companion. How are you feeling right now?' }
  ]);
  const [input, setInput] = useState('');

  const send = () => {
    if (!input.trim()) return;
    const userMsg = { id: Date.now().toString(), sender: 'user', text: input };
    setMessages(prev => [...prev, userMsg]);
    setInput('');

    setTimeout(() => {
      const aiReply = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: 'Take a deep breath in... and let it go. Remember to pause and stay grounded.'
      };
      setMessages(prev => [...prev, aiReply]);
    }, 600);
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack} style={styles.backBtn}><Text style={styles.backText}>← Back</Text></TouchableOpacity>
        <Text style={styles.title}>AI Companion (Local)</Text>
      </View>
      <FlatList
        data={messages}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <View style={[styles.bubble, item.sender === 'user' ? styles.userBubble : styles.aiBubble]}>
            <Text style={styles.msgText}>{item.text}</Text>
          </View>
        )}
        contentContainerStyle={{ padding: 16 }}
      />
      <View style={styles.inputRow}>
        <TextInput
          style={styles.input}
          placeholder="Type a message..."
          placeholderTextColor="#64748B"
          value={input}
          onChangeText={setInput}
        />
        <TouchableOpacity style={styles.sendBtn} onPress={send}><Text style={styles.sendText}>Send</Text></TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0F172A' },
  header: { flexDirection: 'row', alignItems: 'center', padding: 16, borderBottomWidth: 1, borderBottomColor: '#1E293B' },
  backBtn: { marginRight: 12 },
  backText: { color: '#38BDF8', fontSize: 16, fontWeight: '600' },
  title: { fontSize: 18, fontWeight: '700', color: '#F8FAFC' },
  bubble: { padding: 12, borderRadius: 12, marginBottom: 10, maxWidth: '80%' },
  userBubble: { backgroundColor: '#0284C7', alignSelf: 'flex-end' },
  aiBubble: { backgroundColor: '#1E293B', alignSelf: 'flex-start' },
  msgText: { color: '#F8FAFC', fontSize: 15 },
  inputRow: { flexDirection: 'row', padding: 12, backgroundColor: '#1E293B' },
  input: { flex: 1, backgroundColor: '#0F172A', color: '#F8FAFC', borderRadius: 8, paddingHorizontal: 12 },
  sendBtn: { backgroundColor: '#0284C7', marginLeft: 8, paddingHorizontal: 16, justifyContent: 'center', borderRadius: 8 },
  sendText: { color: '#FFF', fontWeight: '700' }
});
