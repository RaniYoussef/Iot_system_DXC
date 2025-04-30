import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr'; // ✅ NEW

@Component({
  selector: 'app-sign-in-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule, RouterModule],
  templateUrl: './sign-in-form.component.html',
  styleUrls: ['./sign-in-form.component.scss']
})
export class SignInFormComponent {
  signInForm: FormGroup;
  forgotPasswordForm: FormGroup;
  isSubmitting = false;
  showForgotPasswordForm = false;
  resetMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService // ✅ NEW
  ) {
    this.signInForm = this.fb.group({
      email: ['', [
        Validators.required,
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
      ]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });

    this.forgotPasswordForm = this.fb.group({
      email: ['', [
        Validators.required,
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
      ]]
    });
  }

  get email() {
    return this.signInForm.get('email');
  }

  get password() {
    return this.signInForm.get('password');
  }

  get forgotEmail() {
    return this.forgotPasswordForm.get('email');
  }

  toggleForgotPassword(): void {
    this.showForgotPasswordForm = !this.showForgotPasswordForm;
    this.resetMessage = '';
    this.forgotPasswordForm.reset();
  }

  onSubmit(): void {
    if (this.signInForm.valid) {
      this.isSubmitting = true;
      const formData = this.signInForm.value;

      this.authService.signIn(formData).subscribe(
        response => {
          console.log('Sign in successful:', response);
          this.toastr.success('Signed in successfully!', 'Success'); // ✅ TOAST
          this.isSubmitting = false;
          this.router.navigate(['/profile']);
        },
        error => {
          console.error('Sign in failed:', error);
          this.toastr.error('Sign-in failed. Please try again.', 'Error'); // ✅ TOAST
          this.isSubmitting = false;
        }
      );
    } else {
      this.signInForm.markAllAsTouched();
    }
  }

  sendResetLink(): void {
    if (this.forgotPasswordForm.valid) {
      const email = this.forgotPasswordForm.get('email')?.value;
      console.log('Reset link sent to:', email);
      this.resetMessage = `A reset link has been sent to ${email}`;
    } else {
      this.forgotPasswordForm.markAllAsTouched();
    }
  }

  signInWithGoogle(): void {
    console.log('Google sign-in clicked');
  }
}
