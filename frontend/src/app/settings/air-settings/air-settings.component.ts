import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingCardComponent } from '../setting-card/setting-card.component';
import { SettingsService } from '../../services/settings.service';
import { AirSettings, PollutionLevelOptions } from '../../model/settings.model';

@Component({
  selector: 'app-air-settings',
  standalone: true,
  imports: [CommonModule, SettingCardComponent],
  templateUrl: './air-settings.component.html',
  styleUrls: ['./air-settings.component.scss']
})
export class AirSettingsComponent implements OnInit {
  settings!: AirSettings;
  pollutionOptions = PollutionLevelOptions;

  constructor(private settingsService: SettingsService) {}

  ngOnInit() {
    this.settingsService.settings$.subscribe(settings => {
      this.settings = settings.air;
    });
  }

  handleSave(metric: keyof AirSettings, event: { value: number | string; alertType?: 'above' | 'below' }) {
    this.settingsService.saveSetting('air', metric, event.value, event.alertType);
  }
}
