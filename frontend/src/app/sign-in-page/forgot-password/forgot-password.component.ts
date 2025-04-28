import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  resetLinkSent = false;

  constructor(private fb: FormBuilder, private router: Router) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.forgotForm.valid) {
      const email = this.forgotForm.get('email')?.value;
      console.log('Reset link sent to:', email); // Simulate sending email
      this.resetLinkSent = true;
    } else {
      this.forgotForm.markAllAsTouched();
    }
  }

  backToSignIn(): void {
    this.router.navigate(['/sign-in']);
  }

  get email() {
    return this.forgotForm.get('email');
  }
}
