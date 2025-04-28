import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnimatedLogoComponent } from '../../sign-in-page/animated-logo/animated-logo.component';
import { IoTBackgroundComponent } from '../../sign-in-page/iot-background/iot-background.component';
import { SignInFormComponent } from '../../sign-in-page/sign-in-form/sign-in-form.component';

@Component({
  selector: 'app-sign-in',
  imports: [
    CommonModule,
    AnimatedLogoComponent,
    IoTBackgroundComponent,
    SignInFormComponent
  ],
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent {}
