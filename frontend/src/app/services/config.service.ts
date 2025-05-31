// src/app/services/config.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private config: any;

  constructor(private http: HttpClient) {}

  loadConfig() {
    return this.http.get('/assets/config.json')
      .toPromise()
      .then(data => {
        this.config = data;
      });
  }

  get apiBaseUrl(): string {
    return this.config?.apiBaseUrl || 'http://localhost:8080';
  }
}
// Usage in app.module.ts