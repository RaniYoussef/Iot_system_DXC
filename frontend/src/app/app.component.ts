import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './settings/header/header.component'; // ✅ Make sure the path is correct
import { FormsModule } from '@angular/forms'; // ✅ Import FormsModule

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FormsModule,], // ✅ Add HeaderComponent
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'frontend-temp';
}
