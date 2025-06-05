import { Component } from '@angular/core';
import { Router } from '@angular/router'; // ✅ Make sure this is a value import
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-choose-dashboard',
  standalone: true,
  imports: [CommonModule, HeaderComponent],
  templateUrl: './Choose-dashboard.component.html',
  styleUrls: ['./Choose-dashboard.component.scss']
})
export class ChooseDashboardComponent {
  constructor(private router: Router) {}

  goToTrafficDashboard(): void {
    this.router.navigate(['/traffic-dashboard']);
  }

  goToLightDashboard(): void {
    this.router.navigate(['/light-dashboard']);
  }
}
