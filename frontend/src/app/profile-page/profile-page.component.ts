import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // NEW
import { SidebarComponent } from './sidebar/sidebar.component';
import { ProfileComponent } from './profile/profile.component';
import { User } from 'src/app/model/user.model';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, ProfileComponent],
  template: `
    <div class="container">
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
export class ProfilePageComponent {
  currentUser: User = {
    id: '123',
    firstName: 'John',
    lastName: 'Doe',
    email: 'johndoe@iotelligence.com',
    phoneNumber: '123-456-783',
    role: 'System Administrator',
    location: 'New York, USA',
    profileCompletion: 0,
    lastUpdated: new Date().toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    })
  };
}
