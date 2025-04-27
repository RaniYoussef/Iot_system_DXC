import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Updated URLs
  private signUpUrl = 'https://real-foxes-follow.loca.lt/api/signup'; // Sign Up Endpoint
  private signInUrl = 'https://real-foxes-follow.loca.lt/api/signin'; // Sign In Endpoint

  constructor(private http: HttpClient) {}

  // Accepts firstName and lastName separately
  signUp(data: { firstName: string; lastName: string; email: string; password: string }): Observable<any> {
    return this.http.post(this.signUpUrl, data);
  }

  signIn(data: { email: string; password: string }): Observable<any> {
    return this.http.post(this.signInUrl, data);
  }
}
