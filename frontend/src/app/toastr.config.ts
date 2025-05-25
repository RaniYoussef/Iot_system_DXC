import { importProvidersFrom } from '@angular/core';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

export const toastrProviders = [
  importProvidersFrom(BrowserAnimationsModule),
  importProvidersFrom(ToastrModule.forRoot({
    positionClass: 'toast-bottom-right',
    timeOut: 3000
  }))
];
