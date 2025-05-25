// src/app/models/settings.model.ts

export type AlertType = 'above' | 'below';

export interface ThresholdSetting {
  id: string;
  metric: string;
  thresholdValue: number;
  alertType: AlertType;
}

export interface TrafficSettings {
  density: ThresholdSetting;
  avgSpeed: ThresholdSetting;
  congestionLevel: ThresholdSetting;
}

export interface AirSettings {
  co: ThresholdSetting;
  ozone: ThresholdSetting;
  pollutionLevel: ThresholdSetting;
}

export interface LightSettings {
  brightnessLevel: ThresholdSetting;
  powerConsumption: ThresholdSetting;
  status: ThresholdSetting;
}

export interface EnvironmentalSettings {
  traffic: TrafficSettings;
  air: AirSettings;
  light: LightSettings;
}

// Strongly typed metric keys for safety (optional)
export type TrafficMetricKey = keyof TrafficSettings;         // 'density' | 'avgSpeed' | 'congestionLevel'
export type AirMetricKey = keyof AirSettings;                 // 'co' | 'ozone' | 'pollutionLevel'
export type LightMetricKey = keyof LightSettings;             // 'brightnessLevel' | 'powerConsumption' | 'status'

// Dropdown value options (for UI binding)
export const CongestionLevelOptions = ['Low', 'Moderate', 'High', 'Severe'];
export const PollutionLevelOptions = ['Good', 'Moderate', 'Unhealthy', 'Very Unhealthy', 'Hazardous'];
export const StatusOptions = ['ON', 'OFF'];

// ✅ Runtime type guard to ensure object is a valid ThresholdSetting
export function isThresholdSetting(obj: any): obj is ThresholdSetting {
  return obj !== null &&
    typeof obj === 'object' &&
    'id' in obj &&
    'metric' in obj &&
    typeof obj.thresholdValue === 'number' &&
    (obj.alertType === 'above' || obj.alertType === 'below');
}
