import { Component, OnInit } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { User } from '../../interfaces/user.interface';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class SignupComponent implements OnInit {
  signupForm!: FormGroup;
  passwordVisible = false;
  passwordMeetsLength = false;
  passwordHasLowerAndUpper = false;
  passwordHasNumberAndSymbol = false;
  
  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initForm();
    this.watchPassword();
  }

  initForm(): void {
    this.signupForm = this.fb.group({
      firstName: ['', [
        Validators.required,
        Validators.pattern(/^[A-Za-z]+$/)
      ]],
      lastName: ['', [
        Validators.required,
        Validators.pattern(/^[A-Za-z]+$/)
      ]],
      phoneNumber: ['', [
        Validators.required, 
        Validators.pattern('^[0-9]{10,15}$')
      ]],
      email: ['', [
        Validators.required,
        Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)
      ]],
      password: ['', [
        Validators.required, 
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
      ]],
      agreeToTerms: [false, [Validators.requiredTrue]]
    });
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
      const user: User = this.signupForm.value;
      console.log('User submitted:', user);
      // Call backend service here
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

  signupWithGithub(): void {
    console.log('Sign up with GitHub clicked');
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
