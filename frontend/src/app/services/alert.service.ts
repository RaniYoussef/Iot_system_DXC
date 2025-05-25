// alert.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Alert {
  type: string;
  metric: string;
  location: string;
  value: number;
  timestamp: string;
  alertType: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private apiUrl = 'http://localhost:8080/api/traffic-sensor/alerts'; // Adjust the URL as needed

  constructor(private http: HttpClient) {}

  getAlerts(): Observable<Alert[]> {
    return this.http.get<Alert[]>(this.apiUrl);
  }
}
