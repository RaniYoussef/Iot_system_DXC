import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';

export interface Alert {
  id: string;
  message: string;
  timestamp: string;
}

export interface AirPollutionReadingWithAlertDTO {
  id: string;
  location: string;
  timestamp: string;
  co: number;
  ozone: number;
  pollutionLevel: string;
  alertTimestamp?: string | null;
  alerts?: Alert[];
}

@Injectable({ providedIn: 'root' })
export class AirPollutionService {
  private baseUrl: string;

  constructor(private http: HttpClient, private configService: ConfigService) {
    this.baseUrl = `${this.configService.apiBaseUrl}/api/air-pollution-sensor`;
  }

  getAllLocations(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/location`);
  }

  getAirPollutionData(filters: {
    location?: string;
    pollutionLevel?: string;
    start?: string;
    end?: string;
    sortBy?: string;
    sortDir?: string;
    page?: number;
    size?: number;
  }): Observable<{ content: AirPollutionReadingWithAlertDTO[], totalElements: number }> {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        params = params.set(key, val.toString());
      }
    });

    return this.http.get<{ content: AirPollutionReadingWithAlertDTO[], totalElements: number }>(
      `${this.baseUrl}/with-alerts`,
      { params }
    );
  }
}
