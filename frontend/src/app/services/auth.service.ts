import { Injectable } from '@angular/core'; 
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Auth, GoogleAuthProvider, signInWithPopup } from '@angular/fire/auth'; // <-- Added
import { environment } from '../../environments/environment'; // <-- Needed for consistency

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private signUpUrl = 'http://localhost:8080/api/register';
  private signInUrl = 'http://localhost:8080/api/login';
  private profileUrl = 'http://localhost:8080/api/user';
  private googleAuthUrl = 'http://localhost:8080/api/auth/google'; // <-- Google backend endpoint

  constructor(private http: HttpClient, private auth: Auth) {}

  // Email/password sign up
  signUp(data: {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
  }): Observable<any> {
    return this.http.post<any>(this.signUpUrl, data);
  }

  // Email/password sign in
  signIn(data: { email: string; password: string; rememberMe?: boolean }): Observable<any> {
    return this.http.post<any>(this.signInUrl, data, { withCredentials: true });
  }

  // Get user profile
  getProfile(): Observable<any> {
    return this.http.get<any>(this.profileUrl, { withCredentials: true });
  }

  // ✅ Google Sign-In logic
  async signInWithGoogle(): Promise<any> {
    const provider = new GoogleAuthProvider();
    const result = await signInWithPopup(this.auth, provider);
    const idToken = await result.user.getIdToken();

    // Send the token to your backend for verification/login/registration
    return this.http.post(this.googleAuthUrl, { token: idToken }, { withCredentials: true }).toPromise();
  }

  logout() {
    return this.http.get('http://localhost:8080/api/logout', { withCredentials: true });
  }
  
}
