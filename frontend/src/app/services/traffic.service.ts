import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';

export interface Alert {
  id: string;
  message: string;
  timestamp: string;
}

export interface TrafficReadingWithAlertDTO {
  id: string;
  location: string;
  timestamp: string;
  trafficDensity: number;
  avgSpeed: number;
  congestionLevel: string;
  alertTimestamp?: string;
  alerts?: Alert[];
}

@Injectable({ providedIn: 'root' })
export class TrafficService {
  private baseUrl: string;

  constructor(private http: HttpClient, private configService: ConfigService) {
    this.baseUrl = `${this.configService.apiBaseUrl}/api/traffic-sensor`;
  }

  getAllLocations(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/location`);
  }

  getTrafficDataWithAlerts(): Observable<TrafficReadingWithAlertDTO[]> {
    return this.http.get<TrafficReadingWithAlertDTO[]>(`${this.baseUrl}/with-alerts`);
  }

getTrafficData(filters: {
  location?: string;
  congestionLevel?: string;
  start?: string;
  end?: string;
  sortBy?: string;
  sortDir?: string;
  page?: number;
  size?: number;
}): Observable<{ content: TrafficReadingWithAlertDTO[], totalElements: number }> {
  let params = new HttpParams();
  Object.entries(filters).forEach(([key, val]) => {
    if (val !== undefined && val !== null && val !== '') {
      params = params.set(key, val.toString());
    }
  });

  return this.http.get<{ content: TrafficReadingWithAlertDTO[], totalElements: number }>(
    `${this.baseUrl}/with-alerts`,
    { params }
  );
}

}
