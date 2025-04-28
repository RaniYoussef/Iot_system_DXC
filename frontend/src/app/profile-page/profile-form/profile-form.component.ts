import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-profile-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-form.component.html',
  styleUrls: ['./profile-form.component.scss']
})
export class ProfileFormComponent {
  @Input() user!: User;
  @Output() profileUpdate = new EventEmitter<void>();

  getUserInitials(): string {
    return this.user?.firstName.charAt(0) + this.user?.lastName.charAt(0);
  }

  onNameChange(): void {
    this.onInputChange();
  }

  onInputChange(): void {
    this.calculateProfileCompletion();
    this.updateLastModified();
    this.profileUpdate.emit();
  }

  private calculateProfileCompletion(): void {
    const fields = [
      this.user.firstName,
      this.user.lastName,
      this.user.phoneNumber,
      this.user.email,
    ];

    const filledFields = fields.filter(
      (field) => field?.trim().length > 0
    ).length;
    this.user.profileCompletion = Math.round(
      (filledFields / fields.length) * 100
    );
  }

  private updateLastModified(): void {
    const today = new Date();
    this.user.lastUpdated = today.toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric',
    });
  }
}
