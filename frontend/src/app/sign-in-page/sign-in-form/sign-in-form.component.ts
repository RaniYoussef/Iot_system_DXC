import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { AuthService } from '../../services/auth.service'; // Import AuthService
import { HttpClientModule } from '@angular/common/http'; // Needed for HTTP requests
import { RouterModule, Router } from '@angular/router'; // ✅ Import RouterModule & Router

@Component({
  selector: 'app-sign-in-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule, RouterModule], // ✅ Added RouterModule
  templateUrl: './sign-in-form.component.html',
  styleUrls: ['./sign-in-form.component.scss']
})
export class SignInFormComponent {
  signInForm: FormGroup;
  forgotPasswordForm: FormGroup;
  isSubmitting = false;
  showForgotPasswordForm = false;
  resetMessage = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) { // ✅ Inject Router
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

  // Submit sign-in form with backend call
  onSubmit(): void {
    if (this.signInForm.valid) {
      this.isSubmitting = true;
      const formData = this.signInForm.value;
      this.authService.signIn(formData).subscribe(
        response => {
          console.log('Sign in successful:', response);
          this.isSubmitting = false;
          // Redirect or token storage can be added here
        },
        error => {
          console.error('Sign in failed:', error);
          this.isSubmitting = false;
        }
      );
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
    // You can integrate Firebase or OAuth here
  }
}
