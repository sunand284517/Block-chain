import React, { useState } from 'react';
import { StatusBar, StyleSheet, View } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { HomeScreen } from './src/screens/HomeScreen';
import { AIChatScreen } from './src/screens/AIChatScreen';
import { BreathingExerciseScreen } from './src/screens/BreathingExerciseScreen';
import { JournalScreen } from './src/screens/JournalScreen';
import { InsightsScreen } from './src/screens/InsightsScreen';
import { InterventionHistoryScreen } from './src/screens/InterventionHistoryScreen';
import { SettingsScreen } from './src/screens/SettingsScreen';
import { PrivacyStatusScreen } from './src/screens/PrivacyStatusScreen';

function App() {
  const [currentScreen, setCurrentScreen] = useState<string>('Home');

  const renderScreen = () => {
    switch (currentScreen) {
      case 'AIChat':
        return <AIChatScreen onBack={() => setCurrentScreen('Home')} />;
      case 'Breathing':
        return <BreathingExerciseScreen onBack={() => setCurrentScreen('Home')} />;
      case 'Journal':
        return <JournalScreen onBack={() => setCurrentScreen('Home')} />;
      case 'Insights':
        return <InsightsScreen onBack={() => setCurrentScreen('Home')} />;
      case 'History':
        return <InterventionHistoryScreen onBack={() => setCurrentScreen('Home')} />;
      case 'Settings':
        return <SettingsScreen onBack={() => setCurrentScreen('Home')} />;
      case 'Privacy':
        return <PrivacyStatusScreen onBack={() => setCurrentScreen('Home')} />;
      case 'Home':
      default:
        return <HomeScreen onNavigate={screen => setCurrentScreen(screen)} />;
    }
  };

  return (
    <SafeAreaProvider>
      <StatusBar barStyle="light-content" backgroundColor="#0F172A" />
      <View style={styles.container}>{renderScreen()}</View>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0F172A',
  },
});

export default App;
