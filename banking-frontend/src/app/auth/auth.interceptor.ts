import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Lab L9: "Use HttpInterceptor to auto-attach JWT token in all requests".
 * Adds  Authorization: Bearer <token>  to every call, and sends the user back
 * to the login page if the backend answers 401 (token missing or expired).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const isLoginCall = req.url.includes('/api/auth/login');

  const token = isLoginCall ? null : auth.token();
  const request = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(request).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse && err.status === 401 && !isLoginCall) {
        auth.logout(false);
        router.navigate(['/login'], { queryParams: { returnUrl: router.url, expired: 1 } });
      }
      return throwError(() => err);
    }),
  );
};
