import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Protects pages (Lab L4 section 6.3 "Conditionally render routes").
 *  - not logged in         -> go to /login
 *  - logged in, wrong role -> go to Home with an "Access denied" message
 * The page's allowed roles are written in app.routes.ts as data: { roles: [...] }.
 * (The backend checks the role again - the frontend check is only for convenience.)
 */
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }
  const roles = route.data?.['roles'] as string[] | undefined;
  if (roles && roles.length > 0 && !auth.hasAnyRole(roles)) {
    return router.createUrlTree(['/'], { queryParams: { denied: 1 } });
  }
  return true;
};
