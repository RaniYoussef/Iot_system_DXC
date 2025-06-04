import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../services/auth.service';
import { User } from '../../model/user.model';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() user!: User;

  constructor(
    private router: Router,
    private authService: AuthService,
    private toastr: ToastrService
  ) {}

  getUserInitials(): string {
    return this.user?.firstName.charAt(0) + this.user?.lastName.charAt(0);
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        const isOAuthUser = localStorage.getItem('oauthUser') === 'true';

        localStorage.clear();
        sessionStorage.clear();

        this.router.navigate(['/sign-in'], { queryParams: { loggedOut: 'true' } });
        this.toastr.success('You have been signed out.');
      },
      error: () => {
        this.toastr.error('Logout failed. Please try again.');
      }
    });
  }
}
