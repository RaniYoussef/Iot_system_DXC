import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr'; // ✅ Toast import
import { AuthService } from '../../services/auth.service';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() user!: User;

  constructor(
    private router: Router,
    private authService: AuthService, // still here if you need it later
    private toastr: ToastrService      // ✅ Injected toast service
  ) {}

  getUserInitials(): string {
    return this.user?.firstName.charAt(0) + this.user?.lastName.charAt(0);
  }

  logout(): void {
    const confirmed = window.confirm('Are you sure you want to log out?');
    if (confirmed) {
      this.authService.logout().subscribe({
        next: () => {
          localStorage.clear();
          sessionStorage.clear();
          this.toastr.success('Logged out successfully', 'Success');
          this.router.navigate(['/sign-in']);
        },
        error: () => {
          this.toastr.error('Logout failed. Please try again.', 'Error');
        }
      });
    }
  }
  
}
