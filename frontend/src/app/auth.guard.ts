import { inject, Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from './services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  private authService = inject(AuthService); // ✅ modern DI
  private router = inject(Router);           // ✅ modern DI

  canActivate(): Observable<boolean> {
    return this.authService.getProfile().pipe(
      map(() => true), // ✅ Authenticated
      catchError(() => {
        this.router.navigate(['/sign-in']); // ❌ Redirect if not authenticated
        return of(false);
      })
    );
  }
}
