import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';

export interface StreetLightReading {
  id: string;
  location: string;
  timestamp: string;
  brightnessLevel: number;
  powerConsumption: number;
  status: 'ON' | 'OFF';
}

@Injectable({ providedIn: 'root' })
export class LightService {
  constructor(private http: HttpClient, private configService: ConfigService) {}

  getReadingsWithAlerts(filters: {
    location?: string;
    status?: string;
    start?: string;
    end?: string;
    sortBy?: string;
    sortDir?: string;
    page?: number;
    size?: number;
  }): Observable<{ content: StreetLightReading[], totalElements: number }> {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        params = params.set(key, val.toString());
      }
    });

    return this.http.get<{ content: StreetLightReading[], totalElements: number }>(
      `${this.configService.apiBaseUrl}/api/light-sensor/with-alerts`, // must match your controller's endpoint
      { params }
    );
  }

  getAllLocations(): Observable<string[]> {
    return this.http.get<string[]>(`${this.configService.apiBaseUrl}/api/light-sensor/location`);
  }
}
