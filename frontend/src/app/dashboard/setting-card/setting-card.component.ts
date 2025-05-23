import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ThresholdSetting, AlertType } from '../../model/settings.model';
import { SettingsService } from '../../services/settings.service';

@Component({
  selector: 'app-setting-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './setting-card.component.html',
  styleUrls: ['./setting-card.component.scss']
})
export class SettingCardComponent {
  @Input() setting!: ThresholdSetting;
  @Input() displayName: string = '';
  @Input() min: number = 0;
  @Input() max: number = 1000;
  @Input() step: number = 1;
  @Input() options: string[] = [];
  @Input() hideAlertType: boolean = false;

  @Input() category!: 'traffic' | 'air' | 'light'; // ✅ used to build JSON payload

  thresholdValue!: number;
  alertType!: AlertType;

  constructor(private settingsService: SettingsService) {}

  ngOnInit() {
    this.thresholdValue = this.setting.thresholdValue;
    this.alertType = this.setting.alertType;
  }

  saveSettings() {
    this.settingsService.sendSettingToBackend(
      this.category,
      this.setting.metric,
      this.thresholdValue,
      this.hideAlertType ? undefined : this.alertType
    );
  }
}
