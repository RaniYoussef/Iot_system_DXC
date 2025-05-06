import { Component, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SignInFormComponent } from '../../sign-in-page/sign-in-form/sign-in-form.component';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [
    CommonModule,
    SignInFormComponent
  ],
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent implements AfterViewInit, OnDestroy {
  private videoElement!: HTMLVideoElement;

  ngAfterViewInit(): void {
    this.videoElement = document.createElement('video');
    this.videoElement.src = 'assets/videos/IOTSignInBackground.mp4';
    this.videoElement.autoplay = true;
    this.videoElement.muted = true;
    this.videoElement.loop = true;
    this.videoElement.style.width = '100%';
    this.videoElement.style.height = '100%';
    this.videoElement.style.objectFit = 'cover';
    this.videoElement.style.position = 'absolute';
    this.videoElement.style.top = '0';
    this.videoElement.style.left = '0';
    this.videoElement.style.zIndex = '0';

    const topSection = document.querySelector('.top-section');
    if (topSection) {
      topSection.prepend(this.videoElement);
    }
  }

  ngOnDestroy(): void {
    if (this.videoElement && this.videoElement.parentNode) {
      this.videoElement.parentNode.removeChild(this.videoElement);
    }
  }
}
