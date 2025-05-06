import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
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

  profilePhotoUrl: string | null = null;
  isEditable = false;

  private http = inject(HttpClient);

  toggleEdit(): void {
    this.isEditable = !this.isEditable;
  }

  saveProfile(): void {
    if (!this.isEditable) return;

    const updatedProfile = {
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      phoneNumber: this.user.phoneNumber,
      email: this.user.email,
      profilePhoto: this.profilePhotoUrl // optional: if your backend supports base64 image
    };

    this.http.post('https://your-backend-api.com/api/update-profile', updatedProfile).subscribe({
      next: (res) => {
        console.log('✅ Profile updated:', res);
        alert('✅ Profile updated successfully!');
        this.isEditable = false;
      },
      error: (err) => {
        console.error('❌ Failed to update profile:', err);
        alert('❌ Failed to update profile. Please try again.');
      }
    });
  }

  triggerPhotoInput(event: Event): void {
    event.preventDefault();
    const input = document.getElementById('photoInput') as HTMLInputElement;
    if (input) {
      input.click();
    }
  }

  removePhoto(): void {
    this.profilePhotoUrl = null;
    const fileInput = document.getElementById('photoInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
    this.onInputChange();
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.profilePhotoUrl = reader.result as string;
        this.onInputChange();
      };
      reader.readAsDataURL(file);
    }
    input.value = '';
  }

  getUserInitials(): string {
    return (this.user?.firstName?.charAt(0) || '') + (this.user?.lastName?.charAt(0) || '');
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
    const filledFields = fields.filter(field => field?.trim().length > 0).length;
    this.user.profileCompletion = Math.round((filledFields / fields.length) * 100);
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
