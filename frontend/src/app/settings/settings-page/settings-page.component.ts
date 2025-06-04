import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component'; // ✅ Import header
import { SectionCardComponent } from '../section-card/section-card.component';
import { TrafficSettingsComponent } from '../traffic-settings/traffic-settings.component';
import { AirSettingsComponent } from '../air-settings/air-settings.component';
import { LightSettingsComponent } from '../light-settings/light-settings.component';

@Component({
  selector: 'app-settings-page',
  standalone: true,
  imports: [
    HeaderComponent,              // ✅ Include header here
    SectionCardComponent,
    TrafficSettingsComponent,
    AirSettingsComponent,
    LightSettingsComponent
  ],
  templateUrl: './settings-page.component.html',
  styleUrls: ['./settings-page.component.scss']
})
export class SettingsPageComponent {}
