import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PasswordState } from '../../model/user.model';

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

  verifyPassword(): void {
    if (this.passwordState.currentPassword === 'demo123') {
      this.passwordState.isVerified = true;
      this.passwordVerified.emit(true);
    }
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
      console.log('Password updated successfully');
      this.resetForm();
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
