import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss']
})
export class AlertComponent implements OnInit {
  @Input() statusCode: number | null = null;
  @Input() message: string = 'New-learner offer | Courses from E£249.99.';

  showAlert = true;
  alertType: 'warning' | 'info' = 'info';

  ngOnInit(): void {
    if (this.statusCode === 500) {
      this.alertType = 'warning';
      this.message = '⚠️ Warning: 500 Internal Server Error';
    }

    setTimeout(() => {
      this.showAlert = false;
    }, 5000);
  }
  

}
