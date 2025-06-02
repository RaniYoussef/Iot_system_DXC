import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingCardComponent } from '../setting-card/setting-card.component';
import { SettingsService } from '../../services/settings.service';
import { LightSettings, StatusOptions } from '../../model/settings.model';

@Component({
  selector: 'app-light-settings',
  standalone: true,
  imports: [CommonModule, SettingCardComponent],
  templateUrl: './light-settings.component.html',
  styleUrls: ['./light-settings.component.scss']
})
export class LightSettingsComponent implements OnInit {
  settings!: LightSettings;
  statusOptions = StatusOptions;

  constructor(private settingsService: SettingsService) {}

  ngOnInit() {
    this.settingsService.settings$.subscribe(settings => {
      this.settings = settings.light;
    });
  }

  handleSave(metric: keyof LightSettings, event: { value: number | string; alertType?: 'above' | 'below' }) {
    this.settingsService.saveSetting('light', metric, event.value, event.alertType);
  }
}
