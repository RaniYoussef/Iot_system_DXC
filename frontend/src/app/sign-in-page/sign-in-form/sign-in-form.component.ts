import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

@Component({
  selector: 'app-sign-in-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './sign-in-form.component.html',
  styleUrls: ['./sign-in-form.component.scss']
})
export class SignInFormComponent {
  signInForm: FormGroup;
  forgotPasswordForm: FormGroup;
  isSubmitting = false;
  showForgotPasswordForm = false;
  resetMessage = '';

  constructor(private fb: FormBuilder) {
    this.signInForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });

    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  // Getters for sign-in form controls
  get email() {
    return this.signInForm.get('email');
  }

  get password() {
    return this.signInForm.get('password');
  }

  // Toggle forgot password UI
  toggleForgotPassword(): void {
    this.showForgotPasswordForm = !this.showForgotPasswordForm;
    this.resetMessage = '';
    this.forgotPasswordForm.reset();
  }

  // Submit sign-in form
  onSubmit(): void {
    if (this.signInForm.valid) {
      this.isSubmitting = true;
      setTimeout(() => {
        console.log('Sign in with:', this.signInForm.value);
        this.isSubmitting = false;
      }, 1000);
    } else {
      this.signInForm.markAllAsTouched();
    }
  }

  // Submit forgot password form
  sendResetLink(): void {
    if (this.forgotPasswordForm.valid) {
      const email = this.forgotPasswordForm.get('email')?.value;
      console.log('Reset link sent to:', email);
      this.resetMessage = `A reset link has been sent to ${email}`;
    } else {
      this.forgotPasswordForm.markAllAsTouched();
    }
  }

  // Getters for forgot password form controls
  get forgotEmail() {
    return this.forgotPasswordForm.get('email');
  }

  // Optional: Google auth stub
  signInWithGoogle(): void {
    console.log('Google sign-in clicked');
    // Integrate real provider if needed
  }
}
