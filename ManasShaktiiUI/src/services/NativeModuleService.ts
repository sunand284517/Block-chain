import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
import { BehavioralEvent, InterventionAction, MonitoringStatus } from '../types';

const { ManasShaktiiNativeModule } = NativeModules;

const eventEmitter = ManasShaktiiNativeModule
  ? new NativeEventEmitter(ManasShaktiiNativeModule)
  : null;

export const NativeModuleService = {
  async startMonitoring(): Promise<boolean> {
    if (Platform.OS !== 'android' || !ManasShaktiiNativeModule) return false;
    return await ManasShaktiiNativeModule.startMonitoring();
  },

  async stopMonitoring(): Promise<boolean> {
    if (Platform.OS !== 'android' || !ManasShaktiiNativeModule) return false;
    return await ManasShaktiiNativeModule.stopMonitoring();
  },

  async getMonitoringStatus(): Promise<MonitoringStatus> {
    if (Platform.OS !== 'android' || !ManasShaktiiNativeModule) {
      return {
        isMonitoringActive: false,
        isOfflineMode: true,
        localRuntime: 'Simulator / Standalone',
      };
    }
    return await ManasShaktiiNativeModule.getMonitoringStatus();
  },

  async triggerManualIntervention(): Promise<boolean> {
    if (Platform.OS !== 'android' || !ManasShaktiiNativeModule) return false;
    return await ManasShaktiiNativeModule.triggerManualIntervention();
  },

  subscribeBehavioralEvents(callback: (event: BehavioralEvent) => void) {
    if (!eventEmitter) return { remove: () => {} };
    return eventEmitter.addListener('onBehavioralEvent', callback);
  },

  subscribeInterventions(callback: (action: InterventionAction) => void) {
    if (!eventEmitter) return { remove: () => {} };
    return eventEmitter.addListener('onInterventionTriggered', callback);
  },
};
