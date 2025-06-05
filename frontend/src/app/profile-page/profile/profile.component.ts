import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../model/user.model';
import { ProfileFormComponent } from '../profile-form/profile-form.component';
import { PasswordFormComponent } from '../password-form/password-form.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfileFormComponent, PasswordFormComponent, FooterComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  @Input() user!: User;
  isPasswordVerified = false;

  ngOnInit() {
    const today = new Date();
    this.user.lastUpdated = today.toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });
  }

  onProfileUpdate(): void {
    // Handle profile updates
  }

  onPasswordVerified(verified: boolean): void {
    this.isPasswordVerified = verified;
  }

  canProceed(): boolean {
    return this.user.profileCompletion === 100 && this.isPasswordVerified;
  }

  onNext(): void {
    if (this.canProceed()) {
      console.log('Proceeding to next step');
    }
  }
}
