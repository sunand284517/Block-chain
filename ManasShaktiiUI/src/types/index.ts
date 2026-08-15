export interface BehavioralEvent {
  event: string;
  confidence: number;
  duration: number;
  features?: Record<string, number>;
  timestamp?: number;
}

export interface InterventionAction {
  action:
    | 'SHOW_OVERLAY'
    | 'HIDE_OVERLAY'
    | 'SHOW_BREATH_GATE'
    | 'VIBRATE'
    | 'SHOW_NOTIFICATION'
    | 'OPEN_JOURNAL'
    | 'START_BREATHING_EXERCISE';
  message: string;
  parameters?: Record<string, any>;
  timestamp?: number;
}

export interface MonitoringStatus {
  isMonitoringActive: boolean;
  isOfflineMode: boolean;
  localRuntime: string;
}

export interface JournalEntry {
  id: number;
  timestamp: number;
  title: string;
  content: string;
}

export type RootTabParamList = {
  Home: undefined;
  AIChat: undefined;
  Journal: undefined;
  Insights: undefined;
  History: undefined;
  Breathing: undefined;
  Settings: undefined;
  Privacy: undefined;
};
