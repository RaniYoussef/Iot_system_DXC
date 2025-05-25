import { Component, OnInit, Inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormsModule,
  ValidationErrors,
  AbstractControl,
  ValidatorFn
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/auth.service';

const strongPasswordValidators = [
  Validators.required,
  Validators.minLength(8),
  Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])/)
];

// ✅ Custom validator to match passwords
function matchPasswordsValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get('newPassword')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  };
}

@Component({
  selector: 'app-sign-in-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule
  ],  
  templateUrl: './sign-in-form.component.html',
  styleUrls: ['./sign-in-form.component.scss']
})
export class SignInFormComponent implements OnInit {
  signInForm: FormGroup;
  forgotPasswordForm: FormGroup;
  resetPasswordForm: FormGroup;
  isSubmitting = false;
  showForgotPasswordForm = false;
  showResetPasswordForm = false;
  resetMessage = '';
  token: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient,
    private toastr: ToastrService
  ) {
    this.signInForm = this.fb.group({
      email: ['', [
        Validators.required,
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
      ]],
      password: ['', strongPasswordValidators],
      rememberMe: [false]
    });

    this.forgotPasswordForm = this.fb.group({
      email: ['', [
        Validators.required,
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
      ]]
    });

    this.resetPasswordForm = this.fb.group({
      newPassword: ['', strongPasswordValidators],
      confirmPassword: ['', Validators.required]
    }, { validators: matchPasswordsValidator() });
  }




  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
  
    if (this.token) {
      this.showResetPasswordForm = true;
      this.showForgotPasswordForm = false;
      return;
    }
  
    // ✅ Don't auto-login if redirected from logout
    const justLoggedOut = this.route.snapshot.queryParamMap.get('loggedOut') === 'true';
    if (justLoggedOut) return;
  
    // ✅ Auto-login check (only if not just logged out)
    this.http.get<{ username: string }>('http://localhost:8080/api/user', { withCredentials: true }).subscribe({
      next: (user) => {
        this.toastr.success(`Welcome back, ${user.username}`);
        this.router.navigateByUrl('/dashboard');
      },
      error: () => {
        // Stay on login page
      }
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

  get newPassword() {
    return this.resetPasswordForm.get('newPassword');
  }

  get confirmPassword() {
    return this.resetPasswordForm.get('confirmPassword');
  }

  toggleForgotPassword(): void {
    this.showForgotPasswordForm = !this.showForgotPasswordForm;
    this.resetMessage = '';
    this.forgotPasswordForm.reset();
  }

  onSubmit(): void {
    if (this.signInForm.invalid) {
      this.signInForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    // ✅ Trim email and password before sending to backend
    const { email, password } = this.signInForm.value;
    const trimmedEmail = email.trim();
    const trimmedPassword = password.trim();

    this.authService.signIn({ email: trimmedEmail, password: trimmedPassword }).subscribe({
      next: () => {
        this.toastr.success('Signed in successfully!', 'Success');
        this.isSubmitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.toastr.error('Sign-in failed. Please try again.', 'Error');
        this.isSubmitting = false;
      }
    });
  }

  sendResetLink(): void {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    const email = this.forgotPasswordForm.value.email;
    this.http.post('http://localhost:8080/api/forgot-password', { email }).subscribe({
      next: () => {
        this.resetMessage = `A reset link has been sent to ${email}`;
        this.toastr.success('Reset link sent!', 'Success');
      },
      error: () => {
        this.toastr.error('Failed to send reset link.', 'Error');
      }
    });
  }

  onResetSubmit(): void {
    if (this.resetPasswordForm.invalid || !this.token) return;
  
    if (this.resetPasswordForm.errors?.['passwordMismatch']) {
      this.toastr.error('Passwords do not match.', 'Error');
      return;
    }
  
    const password = this.resetPasswordForm.value.newPassword;
  
    this.http.post(`http://localhost:8080/api/reset-password?token=${this.token}`, {
      newPassword: password
    }).subscribe({
      next: () => {
        this.toastr.success('Password changed successfully!', 'Success');
        this.resetPasswordForm.reset();
        this.showResetPasswordForm = false;
        this.token = null;
        this.router.navigate(['/sign-in']);
      },
      error: () => {
        this.toastr.error('Reset failed. Token may be invalid or expired.', 'Error');
      }
    });
  }
  

  signInWithGoogle(): void {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }
}
