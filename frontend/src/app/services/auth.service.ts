import { Injectable } from '@angular/core'; 
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Updated URLs
  private signUpUrl = 'http://localhost:8080/api/register'; // Sign Up Endpoint
  private signInUrl = 'http://localhost:8080/api/login'; // Sign In Endpoint

  constructor(private http: HttpClient) {}

  // Accepts firstName and lastName separately
  signUp(data: {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
  }): Observable<any> {
    return this.http.post<any>(this.signUpUrl, data);
  }
  

  signIn(data: { email: string; password: string }): Observable<any> {
    return this.http.post<any>(this.signInUrl, data); // Added <any> for type clarity
  }
}
