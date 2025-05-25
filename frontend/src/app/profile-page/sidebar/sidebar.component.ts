import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router'; // ✅ Import RouterModule
import { ToastrService } from 'ngx-toastr'; // ✅ Toast import
import { AuthService } from '../../services/auth.service';
import { User } from '../../model/user.model';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule], // ✅ Add RouterModule here
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Input() user!: User;


  constructor(
    private router: Router,
    private authService: AuthService,
    private toastr: ToastrService,
    private http: HttpClient  // ✅ Add this
  ) {}
  

  getUserInitials(): string {
    return this.user?.firstName.charAt(0) + this.user?.lastName.charAt(0);
  }

  logout(): void {
    this.http.get('http://localhost:8080/api/logout', { withCredentials: true }).subscribe({
      next: () => {
        const isOAuthUser = localStorage.getItem('oauthUser') === 'true';
  
        // ✅ Clear app data
        localStorage.clear();
        sessionStorage.clear();
  
        // ✅ Just redirect regardless of login type — no Google logout
        this.router.navigate(['/sign-in'], { queryParams: { loggedOut: 'true' } });

        this.toastr.success('You have been signed out.');
      },
      error: () => {
        this.toastr.error('Logout failed. Please try again.');
      }
    });
  }
  

  // logout(): void {
  //   this.http.get('http://localhost:8080/api/logout', { withCredentials: true }).subscribe({
  //     next: () => {
  //       localStorage.clear();
  //       sessionStorage.clear();
  
  //       // ✅ Only redirect to sign-in if NOT Google user
  //       if (!this.user.oauthUser) {
  //         this.router.navigate(['/sign-in']);
  //       } else {
  //         // ✅ Optional: Show custom screen for Google logout or force refresh
  //         window.location.href = '/sign-in?loggedOutGoogle=true';
  //       }
  //     },
  //     error: () => {
  //       this.toastr.error('Logout failed. Please try again.');
  //     }
  //   });
  // }
  
  
  
  
}
