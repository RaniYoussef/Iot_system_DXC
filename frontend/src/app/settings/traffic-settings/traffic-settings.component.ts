import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingCardComponent } from '../setting-card/setting-card.component';
import { SettingsService } from '../../services/settings.service';
import { TrafficSettings, CongestionLevelOptions } from '../../model/settings.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-traffic-settings',
  standalone: true,
  imports: [CommonModule, SettingCardComponent],
  templateUrl: './traffic-settings.component.html',
  styleUrls: ['./traffic-settings.component.scss']
})
export class TrafficSettingsComponent implements OnInit, OnDestroy {
  settings!: TrafficSettings;
  congestionOptions = CongestionLevelOptions;
  private destroy$ = new Subject<void>();

  constructor(private settingsService: SettingsService) {}

  ngOnInit() {
    this.settingsService.settings$
      .pipe(takeUntil(this.destroy$))
      .subscribe(settings => {
        this.settings = settings.traffic;
      });
  }

  handleSave(metric: keyof TrafficSettings, event: { value: any; alertType?: 'above' | 'below' }) {
    // Optional: update state in local observable (not backend)
    this.settingsService.saveSetting('traffic', metric, event.value, event.alertType);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
