import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

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
  alerts?: Alert[]; // ✅ Add this
}


@Injectable({
  providedIn: 'root'
})
export class TrafficService {
  private baseUrl = 'http://localhost:8080/api/traffic-sensor/with-alerts'; // Adjust if deployed

  constructor(private http: HttpClient) {}


  getAllLocations(): Observable<string[]> {
  return this.http.get<string[]>('http://localhost:8080/api/traffic-sensor/locations');
}

getTrafficDataWithAlerts() {
  return this.http.get<TrafficReadingWithAlertDTO[]>('http://localhost:8080/api/traffic-sensor/with-alerts');
}



  getTrafficData(filters: {
    location?: string;
    congestionLevel?: string;
    start?: string;
    end?: string;
    sortBy?: string;
    sortDir?: string;
  }): Observable<TrafficReadingWithAlertDTO[]> {
    let params = new HttpParams();
    Object.entries(filters).forEach(([key, val]) => {
      if (val) params = params.set(key, val);
    });

    return this.http.get<TrafficReadingWithAlertDTO[]>(this.baseUrl, { params });
  }
}
