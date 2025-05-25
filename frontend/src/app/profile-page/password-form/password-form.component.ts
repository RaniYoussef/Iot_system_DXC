import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PasswordState } from '../../model/user.model';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
//import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-password-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './password-form.component.html',
  styleUrls: ['./password-form.component.scss']
})
export class PasswordFormComponent {
  @Output() passwordVerified = new EventEmitter<boolean>();

  passwordState: PasswordState = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
    isVerified: false,
    showPasswordFields: false
  };

  showResetOptions = false;
  showCurrentPassword = false;
  showNewPassword = false;
  resetMethod: 'email' | 'phone' | null = null;
  otpValue = '';

  constructor(private http: HttpClient, private toastr: ToastrService) {}

  verifyPassword(): void {
    const payload = { password: this.passwordState.currentPassword };

    this.http.post<{ valid: boolean }>(
      //`${environment.apiBaseUrl}/verify-password`
      "http://localhost:8080/api/user/verify-password" ,
      payload,
      { withCredentials: true }
    ).pipe(
      catchError((error) => {
        this.toastr.error('Verification failed.');
        return of({ valid: false });
      })
    ).subscribe(res => {
      if (res.valid) {
        this.passwordState.isVerified = true;
        this.passwordVerified.emit(true);
        this.toastr.success('Password verified successfully!');
      } else {
        this.toastr.error('Incorrect password.');
      }
    });
  }

  toggleCurrentPasswordVisibility(): void {
    this.showCurrentPassword = !this.showCurrentPassword;
  }

  toggleNewPasswordVisibility(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  passwordsMatch(): boolean {
    return this.passwordState.newPassword === this.passwordState.confirmPassword 
      && this.passwordState.newPassword !== '';
  }

  canUpdatePassword(): boolean {
    return this.passwordsMatch() && this.passwordState.newPassword !== this.passwordState.currentPassword;
  }

  updatePassword(): void {
    if (this.canUpdatePassword()) {
      const payload = {
        oldPassword: this.passwordState.currentPassword,
        newPassword: this.passwordState.newPassword
      };

      this.http.put<{ success: boolean }>(
        "http://localhost:8080/api/user/update-password",
       // `${environment.apiBaseUrl}/update-password`,
        payload,
        { withCredentials: true }
      ).pipe(
        catchError((error) => {
          this.toastr.error('Password update failed.');
          return of({ success: false });
        })
      ).subscribe(res => {
        if (res.success) {
          this.toastr.success('Password updated successfully!');
          this.resetForm();
        } else {
          this.toastr.error('Failed to update password.');
        }
      });
    }
  }

  cancelUpdate(): void {
    this.resetForm();
  }

  toggleResetOptions(event?: Event): void {
    if (event) {
      event.preventDefault();
    }
    this.showResetOptions = !this.showResetOptions;
    this.resetMethod = null;
    this.otpValue = '';
  }

  selectResetMethod(method: 'email' | 'phone'): void {
    this.resetMethod = method;
    this.otpValue = '';
  }

  sendResetEmail(): void {
    console.log('Reset email sent');
  }

  verifyOTP(): void {
    console.log('OTP verified:', this.otpValue);
  }

  private resetForm(): void {
    this.passwordState = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
      isVerified: false,
      showPasswordFields: false
    };
    this.showCurrentPassword = false;
    this.showNewPassword = false;
    this.passwordVerified.emit(false);
  }
}