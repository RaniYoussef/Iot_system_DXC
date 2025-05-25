import { Component } from '@angular/core';
import { Router } from '@angular/router'; // ✅ Import Router
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
// import { AlertComponent } from '../alert/alert.component';

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
}
