import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config'; // <-- Use this instead of direct providers

bootstrapApplication(AppComponent, appConfig); // <-- Use appConfig here
