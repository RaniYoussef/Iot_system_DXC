import { Routes } from '@angular/router';
import { SignInComponent } from './pages/sign-in/sign-in.component';
import { ProfilePageComponent } from './profile-page/profile-page.component';
import { SignupComponent } from './signup-page/signup/signup.component';
import { ForgotPasswordComponent } from './sign-in-page/forgot-password/forgot-password.component';
import { AuthGuard } from './auth.guard';



export const routes: Routes = [
  { path: '', redirectTo: 'sign-in', pathMatch: 'full' },
  {
    path: 'reset-password',component: SignInComponent },
  { path: 'sign-in', component: SignInComponent },
  { path: 'sign-up', component: SignupComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'profile', component: ProfilePageComponent , canActivate: [AuthGuard]  }
];
