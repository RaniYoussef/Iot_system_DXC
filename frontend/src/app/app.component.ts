import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './dashboard/header/header.component'; // ✅ Make sure the path is correct

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet], // ✅ Add HeaderComponent
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'frontend-temp';
}
