import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface IoTDevice {
  id: number;
  top: string;
  left: string;
  type: 'smartwatch' | 'sensor' | 'home' | 'cloud' | 'router';
  size: 'small' | 'medium' | 'large';
  animationDelay: string;
}

@Component({
  selector: 'app-iot-background',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './iot-background.component.html',
  styleUrls: ['./iot-background.component.scss']
})
export class IoTBackgroundComponent implements OnInit {
  devices: IoTDevice[] = [];

  ngOnInit(): void {
    this.generateDevices();
  }

  generateDevices(): void {
    const deviceTypes: ('smartwatch' | 'sensor' | 'home' | 'cloud' | 'router')[] = 
      ['smartwatch', 'sensor', 'home', 'cloud', 'router'];
    const sizes: ('small' | 'medium' | 'large')[] = ['small', 'medium', 'large'];

    // Generate fewer devices for a cleaner background
    for (let i = 0; i < 15; i++) {
      this.devices.push({
        id: i,
        top: `${Math.floor(Math.random() * 100)}%`,
        left: `${Math.floor(Math.random() * 100)}%`,
        type: deviceTypes[Math.floor(Math.random() * deviceTypes.length)],
        size: sizes[Math.floor(Math.random() * sizes.length)],
        animationDelay: `-${Math.floor(Math.random() * 12)}s`
      });
    }
  }

  getDeviceIcon(type: string): string {
    switch (type) {
      case 'smartwatch':
        return '⌚';
      case 'sensor':
        return '📡';
      case 'home':
        return '🏠';
      case 'cloud':
        return '☁️';
      case 'router':
        return '📶';
      default:
        return '📱';
    }
  }
}