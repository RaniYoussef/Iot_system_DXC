import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from './sidebar/sidebar.component';
import { ProfileComponent } from './profile/profile.component';
import { User } from 'src/app/model/user.model';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, ProfileComponent],
  template: `
    <div class="container" *ngIf="currentUser">
      <app-sidebar [user]="currentUser"></app-sidebar>
      <app-profile [user]="currentUser"></app-profile>
    </div>
  `,
  styles: [`
    .container {
      display: flex;
      height: 100vh;
      overflow: hidden;
      background-color: var(--background-light);
    }
    @media (max-width: 768px) {
      .container {
        flex-direction: column;
      }
    }
  `]
})
export class ProfilePageComponent implements OnInit {
  currentUser: User | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getProfile().subscribe({
      next: (data: Partial<User>) => {
        this.currentUser = {
          id: data.id ?? '0',
          firstName: data.firstName ?? 'Guest',
          lastName: data.lastName ?? 'User',
          email: data.email ?? 'guest@iotelligence.com',
          phoneNumber: data.phoneNumber ?? '000-000-0000',
          role: data.role ?? 'Visitor',
          location: data.location ?? 'Unknown',
          profileCompletion: data.profileCompletion ?? 0,
          lastUpdated: data.lastUpdated ?? new Date().toLocaleDateString('en-US', {
            month: 'long',
            day: 'numeric',
            year: 'numeric'
          })
        };
      },
      error: (err) => {
        console.error('❌ Failed to load user profile:', err);
        // Optionally keep full fallback here if profile can't load at all
      }
    });
  }
  
}
