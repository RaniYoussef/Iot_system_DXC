import { Component, HostListener, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClickOutsideDirective } from 'src/app/shared/directives/click-outside.directive';
import { ToastrService } from 'ngx-toastr';

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

  constructor(private toastr: ToastrService) {}

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

  signOut() {
    this.toastr.success('You have been signed out.', 'Signed Out');
    setTimeout(() => {
      window.location.href = '/sign-in';
    }, 1500);
  }

  @HostListener('document:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent) {
    this.closeDropdown();
  }
}
