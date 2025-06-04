import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../model/user.model';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-profile-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-form.component.html',
  styleUrls: ['./profile-form.component.scss']
})
export class ProfileFormComponent implements OnInit {
  @Input() user!: User;
  @Output() profileUpdate = new EventEmitter<void>();


  private toastr = inject(ToastrService);

  private http = inject(HttpClient);
  // 👇 NEW: Holds the base64 preview of the selected image
  profilePhotoUrl: string | null = null;

  // 👇 NEW: Controls if form fields are editable
  isEditable = false;

  // 👇 NEW: Toggles editable mode
  toggleEdit(): void {
    this.isEditable = !this.isEditable;
  }

  ngOnInit() {
    if (this.user.profilePhoto) {
      this.profilePhotoUrl = this.user.profilePhoto; // 👈 Show from backend
    }
  }
  

  // 👇 NEW: Used to trigger the hidden file input
  triggerPhotoInput(event: Event): void {
    event.preventDefault();
    const input = document.getElementById('photoInput') as HTMLInputElement;
    if (input) {
      input.click();
    }
  }

  // 👇 NEW: Handles photo removal and resets input
  removePhoto(): void {
    this.profilePhotoUrl = null;

    const fileInput = document.getElementById('photoInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = ''; // Clear input so same file can be selected again
    }

    this.onInputChange(); // Optionally update profile status
  }

  // 👇 NEW: Handles image selection and sets preview
  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.profilePhotoUrl = reader.result as string;
  
        // ✅ Automatically send to server as soon as it's ready
        this.http.put('http://localhost:8080/api/user/update-photo', 
          { profilePhoto: this.profilePhotoUrl },
          { withCredentials: true }
        ).subscribe({
          next: () => {
            this.toastr.success('Profile photo updated!');
            this.profileUpdate.emit(); // Optionally notify parent
          },
          error: () => {
            this.toastr.error('Failed to update photo.');
          }
        });
      };
      reader.readAsDataURL(file);
    }
    input.value = '';
  }
  

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


  saveProfile(): void {
    if (!this.isEditable) return;
  
    const updatedProfile = {
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      email: this.user.email,
      phoneNumber: this.user.phoneNumber,
      //profilePhoto: this.profilePhotoUrl // base64 string
    };
  
    this.http.post('http://localhost:8080/api/user/update-profile', updatedProfile, { withCredentials: true }).subscribe({
      next: () => {
        //alert('✅ Profile updated successfully!');
        this.toastr.success('Profile updated successfully!');
        this.isEditable = false;
        this.profileUpdate.emit(); // Notify parent to refresh user data
      },
      error: () => {
        alert('❌ Failed to update profile. Please try again.');
      }
    });
  }
  

}

