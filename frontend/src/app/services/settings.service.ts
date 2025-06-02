import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {
  EnvironmentalSettings,
  TrafficSettings,
  AirSettings,
  LightSettings,
  AlertType
} from '../model/settings.model';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr'; // ✅ Added
import { ConfigService } from './config.service'; 


@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private settingsSource = new BehaviorSubject<EnvironmentalSettings>(this.getDefaultSettings());
  public settings$ = this.settingsSource.asObservable();

  // ✅ Replace this URL later with your real ngrok endpoint
private get backendUrl() {
  return `${this.configService.apiBaseUrl}/api/alert-settings`;
}

constructor(
  private http: HttpClient,
  private toastr: ToastrService,
  private configService: ConfigService
) {}

  private getDefaultSettings(): EnvironmentalSettings {
    return {
      traffic: {
        density: {
          id: 'traffic-density',
          metric: 'density',
          thresholdValue: 200,
          alertType: 'above'
        },
        avgSpeed: {
          id: 'traffic-speed',
          metric: 'avgSpeed',
          thresholdValue: 50,
          alertType: 'below'
        },
        congestionLevel: {
          id: 'traffic-congestion',
          metric: 'congestionLevel',
          thresholdValue: 2,
          alertType: 'above'
        }
      },
      air: {
        co: {
          id: 'air-co',
          metric: 'co',
          thresholdValue: 15,
          alertType: 'above'
        },
        ozone: {
          id: 'air-ozone',
          metric: 'ozone',
          thresholdValue: 100,
          alertType: 'above'
        },
        pollutionLevel: {
          id: 'air-pollution',
          metric: 'pollutionLevel',
          thresholdValue: 2,
          alertType: 'above'
        }
      },
      light: {
        brightnessLevel: {
          id: 'light-brightness',
          metric: 'brightnessLevel',
          thresholdValue: 50,
          alertType: 'below'
        },
        powerConsumption: {
          id: 'light-power',
          metric: 'powerConsumption',
          thresholdValue: 1000,
          alertType: 'above'
        },
        status: {
          id: 'light-status',
          metric: 'status',
          thresholdValue: 0,
          alertType: 'below'
        }
      }
    };
  }

  updateTrafficSettings(settings: TrafficSettings): void {
    const currentSettings = this.settingsSource.value;
    this.settingsSource.next({
      ...currentSettings,
      traffic: settings
    });
    console.log('Updated traffic settings:', settings);
  }

  updateAirSettings(settings: AirSettings): void {
    const currentSettings = this.settingsSource.value;
    this.settingsSource.next({
      ...currentSettings,
      air: settings
    });
    console.log('Updated air settings:', settings);
  }

  updateLightSettings(settings: LightSettings): void {
    const currentSettings = this.settingsSource.value;
    this.settingsSource.next({
      ...currentSettings,
      light: settings
    });
    console.log('Updated light settings:', settings);
  }

  saveSetting(
    category: keyof EnvironmentalSettings,
    metricKey: string,
    value: number | string,
    alertType?: AlertType
  ): void {
    const currentSettings = this.settingsSource.value;
    const categorySettings = { ...currentSettings[category] } as Record<string, any>;
    const metric = categorySettings[metricKey];

    const updatedMetric =
      typeof metric === 'object' && metric !== null
        ? {
            ...metric,
            thresholdValue: value,
            ...(alertType !== undefined ? { alertType } : {})
          }
        : {
            id: `${category}-${metricKey}`,
            metric: metricKey,
            thresholdValue: value,
            ...(alertType !== undefined ? { alertType } : {})
          };

    const updatedCategory = {
      ...categorySettings,
      [metricKey]: updatedMetric
    };

    this.settingsSource.next({
      ...currentSettings,
      [category]: updatedCategory
    });

    console.log(`Saved ${category} - ${metricKey}:`, value, alertType ?? 'no alertType');
  }

  // ✅ Now with Toastr feedback
  sendSettingToBackend(
    category: 'traffic' | 'air' | 'light',
    metricKey: string,
    thresholdValue: number | string,
    alertType?: AlertType
  ) {
    const typeMap: Record<string, string> = {
      traffic: 'Traffic',
      air: 'Air_Pollution',
      light: 'Street_Light'
    };

    const metricMap: Record<string, string> = {
      density: 'trafficDensity',
      avgSpeed: 'avgSpeed',
      congestionLevel: 'congestionLevel',
      co: 'co',
      ozone: 'ozone',
      pollutionLevel: 'pollutionLevel',
      brightnessLevel: 'brightnessLevel',
      powerConsumption: 'powerConsumption',
      status: 'status'
    };

    const payload = {
      type: typeMap[category],
      metric: metricMap[metricKey],
      thresholdValue,
      ...(alertType !== undefined ? { alertType } : {})
    };

    this.http.post(this.backendUrl, payload).subscribe({
      next: () => {
        console.log('✅ Sent setting to backend:', payload);
        this.toastr.success(`${metricMap[metricKey]} saved successfully`, 'Settings Saved');
      },
      error: err => {
        console.error('❌ Backend send failed:', err);
        this.toastr.error(`Failed to save ${metricMap[metricKey]}`, 'Save Error');
      }
    });
  }
}
