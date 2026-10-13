import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { Login } from './auth/login/login';
import { FdCalculator } from './calculator/fd-calculator';
import { Home } from './home/home';

/** Which page is shown for which address. */
export const routes: Routes = [
  { path: '', component: Home, title: 'Home | BankLab' },
  { path: 'login', component: Login, title: 'Login | BankLab' },
  {
    path: 'fd-calculator',
    component: FdCalculator,
    canActivate: [authGuard],
    data: { roles: ['CUSTOMER'] }, // Lab L10: "Access restricted to CUSTOMER role"
    title: 'FD Calculator | BankLab',
  },
  { path: '**', redirectTo: '' },
];
