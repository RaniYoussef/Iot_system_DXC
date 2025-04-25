import { Routes } from '@angular/router';
import { SignInComponent } from './pages/sign-in/sign-in.component';
import { ProfilePageComponent } from './profile-page/profile-page.component';
import { SignupPageComponent } from './signup-page/signup-page.component';
import { ForgotPasswordComponent } from './sign-in-page/forgot-password/forgot-password.component'; // ✅ Added

export const routes: Routes = [
  { path: '', redirectTo: 'sign-in', pathMatch: 'full' },
  { path: 'sign-in', component: SignInComponent },
  {
    path: 'sign-up',
    component: SignupPageComponent,
    loadChildren: () =>
      import('./signup-page/signup/signup.routes').then((m) => m.signupRoutes)
  },
  { path: 'forgot-password', component: ForgotPasswordComponent }, // ✅ New route
  { path: 'profile', component: ProfilePageComponent }
];
