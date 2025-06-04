import { Injectable } from '@angular/core'; 
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Auth, GoogleAuthProvider, signInWithPopup } from '@angular/fire/auth';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private auth: Auth,
    private configService: ConfigService
  ) {}

  private get baseUrl() {
    return this.configService.apiBaseUrl;
  }

  signUp(data: {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
  }): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/register`, data);
  }

  signIn(data: { email: string; password: string; rememberMe?: boolean }): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/login`, data, { withCredentials: true });
  }

  getProfile(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/user`, { withCredentials: true });
  }

  sendForgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/reset-password?token=${token}`, { newPassword });
  }

  signInWithGoogleFirebase(): Promise<any> {
    const provider = new GoogleAuthProvider();
    return signInWithPopup(this.auth, provider)
      .then(result => result.user.getIdToken())
      .then(idToken => {
        return this.http.post(`${this.baseUrl}/api/auth/google`, { token: idToken }, { withCredentials: true }).toPromise();
      });
  }

  getGoogleRedirectUrl(): string {
    return `${this.baseUrl}/oauth2/authorization/google`;
  }

  logout(): Observable<any> {
    return this.http.get(`${this.baseUrl}/api/logout`, { withCredentials: true });
  }

  verifyPassword(password: string) {
  return this.http.post<{ valid: boolean }>(
    `${this.configService.apiBaseUrl}/api/user/verify-password`,
    { password },
    { withCredentials: true }
  );
  }

  updatePassword(oldPassword: string, newPassword: string) {
    return this.http.put<{ success: boolean }>(
      `${this.configService.apiBaseUrl}/api/user/update-password`,
      { oldPassword, newPassword },
      { withCredentials: true }
    );
  }
  
  updateProfilePhoto(base64Photo: string) {
    return this.http.put(
      `${this.configService.apiBaseUrl}/api/user/update-photo`,
      { profilePhoto: base64Photo },
      { withCredentials: true }
    );
  }

  updateUserProfile(data: {
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
  }) {
    return this.http.post(
      `${this.configService.apiBaseUrl}/api/user/update-profile`,
      data,
      { withCredentials: true }
    );
  }



}
