import { Component, HostListener, ViewChild, ElementRef, AfterViewInit, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClickOutsideDirective } from 'src/app/shared/directives/click-outside.directive';
import { ToastrService } from 'ngx-toastr';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service'; // Import AuthService


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, ClickOutsideDirective],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements AfterViewInit {
  firstName = 'User';
  dropdownOpen = false;

  @ViewChild('headerVideo') headerVideoRef!: ElementRef<HTMLVideoElement>;

  constructor(
    private toastr: ToastrService,
    private http: HttpClient,          // ✅ Add this
    private router: Router ,            // ✅ And this
    private authService: AuthService // Inject AuthService

  ) {}

  ngOnInit(): void {
    this.authService.getProfile().subscribe({
      next: (profile) => {
        this.firstName = profile.firstName || 'User'; // Update firstName from profile
      },
      error: () => {
        console.error('Failed to fetch user profile');
      }
    });
  }
  
  ngAfterViewInit() {
    const videoEl = this.headerVideoRef?.nativeElement;
    if (videoEl) {
      videoEl.muted = true; // Redundant but safe for autoplay policy
      videoEl.play().catch(err => {
        console.warn('Autoplay failed:', err);
      });
    }
  }

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  closeDropdown() {
    this.dropdownOpen = false;
  }

  // signOut() {
  //   this.toastr.success('You have been signed out.', 'Signed Out');
  //   setTimeout(() => {
  //     window.location.href = '/sign-in';
  //   }, 1500);
  // }


  
  // signOut(): void {
  //   this.http.get('http://localhost:8080/api/logout', { withCredentials: true }).subscribe({
  //     next: () => {
  //       localStorage.clear();
  //       sessionStorage.clear();
  
  //       // Check if the user is an OAuth user (you can store a flag after login)
  //       const isOAuthUser = localStorage.getItem('oauthUser') === 'true';
  
  //       if (isOAuthUser) {
  //         // ✅ Logout from Google and redirect
  //         window.location.href = 'https://accounts.google.com/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:4200/sign-in';
  //       } else {
  //         this.router.navigate(['/sign-in']);
  //       }
  
  //       this.toastr.success('You have been signed out.');
  //     },
  //     error: () => {
  //       this.toastr.error('Logout failed. Please try again.');
  //     }
  //   });
  // }
  signOut(): void {
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
  
  

  

  @HostListener('document:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent) {
    this.closeDropdown();
  }
}
