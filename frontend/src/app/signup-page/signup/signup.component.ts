import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { trigger, transition, style, animate, keyframes } from '@angular/animations';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
  standalone: true,
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        animate('1.2s ease-out', keyframes([
          style({ opacity: 0, transform: 'scale(0.95) translateY(20px)', offset: 0 }),
          style({ opacity: 0.5, transform: 'scale(0.98) translateY(10px)', offset: 0.5 }),
          style({ opacity: 1, transform: 'scale(1) translateY(0)', offset: 1.0 })
        ]))
      ])
    ])
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule
  ]
})
export class SignupComponent implements OnInit {
  signupForm!: FormGroup;
  passwordVisible = false;
  passwordMeetsLength = false;
  passwordHasLowerAndUpper = false;
  passwordHasNumberAndSymbol = false;
  isSubmitting = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  ngOnInit(): void {
    this.initForm();
    this.watchPassword();
  }

  initForm(): void {
    this.signupForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.pattern(/^[A-Za-z]+$/)]],
      lastName: ['', [Validators.required, Validators.pattern(/^[A-Za-z]+$/)]],
      username: ['', [Validators.required, Validators.pattern(/^\S+$/)]],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10,15}$')]],
      email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)]],
      password: ['', [
        Validators.required, 
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
      ]],
      agreeToTerms: [false, [Validators.requiredTrue]]
    });
  }

  preventSpace(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\s/g, '');

    // Limit the username to 10 characters
    if (input.value.length > 10) {
      input.value = input.value.slice(0, 10);
    }

    this.signupForm.controls['username'].setValue(input.value, { emitEvent: false });
  }

  watchPassword(): void {
    this.signupForm.get('password')?.valueChanges.subscribe((password: string) => {
      this.passwordMeetsLength = password.length >= 8;
      this.passwordHasLowerAndUpper = /[a-z]/.test(password) && /[A-Z]/.test(password);
      this.passwordHasNumberAndSymbol = /[0-9]/.test(password) && /[@$!%*?&]/.test(password);
    });
  }

  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.isSubmitting = true;

      const formValues = this.signupForm.value;

      const payload = {
        firstName: formValues.firstName,
        lastName: formValues.lastName,
        username: formValues.username,
        email: formValues.email,
        password: formValues.password
      };      

      this.authService.signUp(payload).subscribe(
        response => {
          console.log('Sign-up successful:', response);
          alert('Sign-up successful! Please sign in.');
          this.isSubmitting = false;
          window.location.href = '/sign-in'; 
        },
        error => {
          console.error('Sign-up failed:', error);
          alert('Sign-up failed! Please try again.');
          this.isSubmitting = false;
        }
      );

    } else {
      this.markFormGroupTouched(this.signupForm);
    }
  }

  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  signupWithGoogle(): void {
    console.log('Sign up with Google clicked');
  }

  getFieldError(fieldName: string): string {
    const control = this.signupForm.get(fieldName);

    if (!control || !control.errors || !control.touched) {
      return '';
    }

    if (control.errors['required']) {
      return 'This field is required';
    }

    if ((fieldName === 'firstName' || fieldName === 'lastName') && control.errors['pattern']) {
      return 'Only letters are allowed';
    }

    if (fieldName === 'username' && control.errors['pattern']) {
      return 'No spaces allowed in username';
    }

    if (fieldName === 'email' && control.errors['pattern']) {
      return 'Please enter a valid email (e.g., amr@example.com)';
    }

    if (fieldName === 'phoneNumber' && control.errors['pattern']) {
      return 'Please enter a valid phone number';
    }

    if (fieldName === 'password') {
      if (control.errors['minlength']) {
        return 'Password must be at least 8 characters';
      }
      if (control.errors['pattern']) {
        return 'Password must include uppercase, lowercase, number, and special character';
      }
    }

    return 'Invalid field';
  }
}
