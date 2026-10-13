import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map } from 'rxjs';

/** The logged-in user, read from the JWT token. */
export interface AuthUser {
  username: string;
  role: string;
  token: string;
  expiresAt: number;
}

/** Shape of POST /api/auth/login answer (Group 10 spec: LoginResponse). */
interface LoginResponse {
  token: string;
  tokenType?: string;
  expiresIn?: number;
  role?: string;
  username?: string;
}

const STORAGE_KEY = 'banklab_token';

/**
 * Handles login and logout (Labs L2, L3, L10).
 * The token is kept in the browser's localStorage (Lab L2: "Store JWT token in local storage")
 * so the user stays logged in after a page refresh, until the token expires.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly user = signal<AuthUser | null>(null);

  constructor() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      const u = decodeToken(saved);
      if (u && u.expiresAt > Date.now()) {
        this.user.set(u);
      } else {
        localStorage.removeItem(STORAGE_KEY);
      }
    }
  }

  login(username: string, password: string): Observable<AuthUser> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(
      map((res) => {
        const u = decodeToken(res.token, res);
        if (!u) {
          throw new Error('The login service returned an invalid token.');
        }
        localStorage.setItem(STORAGE_KEY, res.token);
        this.user.set(u);
        return u;
      }),
    );
  }

  logout(goToLogin = true): void {
    localStorage.removeItem(STORAGE_KEY);
    this.user.set(null);
    if (goToLogin) {
      this.router.navigate(['/login']);
    }
  }

  /** The token to send to the backend, or null if not logged in / expired. */
  token(): string | null {
    const u = this.user();
    if (!u) {
      return null;
    }
    if (u.expiresAt <= Date.now()) {
      this.logout(false);
      return null;
    }
    return u.token;
  }

  isLoggedIn(): boolean {
    return this.token() !== null;
  }

  hasAnyRole(roles: string[]): boolean {
    const u = this.user();
    return !!u && roles.includes(u.role);
  }
}

/**
 * Reads the middle part of a JWT (the "payload") without any extra library.
 * (Lab L4 section 6.2 uses jwtDecode(); this does the same job.)
 */
function decodeToken(token: string, res?: LoginResponse): AuthUser | null {
  try {
    const part = token.split('.')[1];
    const base64 = part.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
    const payload = JSON.parse(atob(padded));
    const rawRole =
      payload.role ?? (Array.isArray(payload.roles) ? payload.roles[0] : payload.roles) ?? res?.role ?? '';
    const role = String(rawRole).toUpperCase().replace(/^ROLE_/, '');
    const expiresAt =
      typeof payload.exp === 'number' ? payload.exp * 1000 : Date.now() + (res?.expiresIn ?? 3600) * 1000;
    return { username: payload.sub ?? res?.username ?? '', role, token, expiresAt };
  } catch {
    return null;
  }
}
