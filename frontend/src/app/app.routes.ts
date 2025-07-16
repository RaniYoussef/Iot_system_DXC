import { Routes } from '@angular/router';
import { SignInComponent } from './pages/sign-in/sign-in.component';
import { ProfilePageComponent } from './profile-page/profile-page.component';
import { SignupComponent } from './signup-page/signup/signup.component';
import { ForgotPasswordComponent } from './sign-in-page/forgot-password/forgot-password.component';
import { AuthGuard } from './auth.guard';
import { SettingsPageComponent } from './settings/settings-page/settings-page.component';
import { ChooseDashboardComponent } from './Dashboard/Choose-dashboard/Choose-dashboard.component';
import { TrafficDashboardComponent } from './Dashboard/traffic-dashboard/traffic-dashboard.component';
import { LightDashboardComponent } from './Dashboard/light-dashboard/light-dashboard.component';
import { AirDashboardComponent } from './Dashboard/air-dashboard/air-dashboard.component';
// import { AlertComponent } from './Dashboard/Alert/alert.component';
import { AlertComponent } from './Dashboard/alert/alert.component';
//import { TrafficVisualisationComponent} from './Dashboard/traffic-visualisation/traffic-visualisation.component';

export const routes: Routes = [
  { path: '', redirectTo: 'sign-in', pathMatch: 'full' },
  { path: 'sign-in', component: SignInComponent },
  { path: 'sign-up', component: SignupComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: SignInComponent },
  { path: 'profile', component: ProfilePageComponent , canActivate: [AuthGuard]  },
  { path: 'dashboard', component: ChooseDashboardComponent },
  { path: 'traffic-dashboard', component: TrafficDashboardComponent },
  { path: 'Settings', component: SettingsPageComponent},
  { path: 'light-dashboard', component: LightDashboardComponent },
  { path: 'air-dashboard', component: AirDashboardComponent },
  { path: 'alert', component: AlertComponent }


];
