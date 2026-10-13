import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { describeHttpError } from '../../calculator/fd-calculator.service';
import { AuthService } from '../auth.service';

/** Login page (Lab L2: masked password; Lab L14: "login page with FD Calculator integrated"). */
@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  username = '';
  password = '';
  showPassword = false;

  readonly busy = signal(false);
  readonly error = signal<string | null>(null);
  readonly expired = this.route.snapshot.queryParamMap.has('expired');

  submit(): void {
    this.error.set(null);
    if (!this.username.trim() || !this.password) {
      this.error.set('Please enter both username and password.');
      return;
    }
    this.busy.set(true);
    this.auth.login(this.username.trim(), this.password).subscribe({
      next: (user) => {
        this.busy.set(false);
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        this.router.navigateByUrl(returnUrl || (user.role === 'CUSTOMER' ? '/fd-calculator' : '/'));
      },
      error: (err) => {
        this.busy.set(false);
        if (err instanceof HttpErrorResponse && err.status === 401) {
          this.error.set('Invalid username or password.');
        } else if (err instanceof HttpErrorResponse && err.status === 404) {
          this.error.set('Login service not found. Check that the login service is running (see Step 16).');
        } else {
          this.error.set(describeHttpError(err)[0]);
        }
      },
    });
  }
}
