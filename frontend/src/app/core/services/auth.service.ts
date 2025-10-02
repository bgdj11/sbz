import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap, catchError, of } from 'rxjs';
import { User } from '../models/user.model';

interface LoginReq { email: string; password: string; }
interface RegisterReq { firstName: string; lastName: string; email: string; password: string; city?: string; }
interface AuthResponse { user: User; message?: string; success?: boolean; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private base = `${environment.apiBaseUrl}/auth`;

  private _currentUser = signal<User | null>(null);
  private _initialized = false;

  readonly currentUser = this._currentUser.asReadonly();
  readonly isLoggedIn = () => !!this._currentUser();
  readonly isAdmin = () => !!this._currentUser()?.admin;

  constructor() {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage(): void {
    try {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        const user = JSON.parse(storedUser);
        this._currentUser.set(user);
        this._initialized = true;
      }
    } catch (error) {
      console.error('Error loading user from storage:', error);
      localStorage.removeItem('currentUser');
    }
  }

  private saveUserToStorage(user: User | null): void {
    try {
      if (user) {
        localStorage.setItem('currentUser', JSON.stringify(user));
      } else {
        localStorage.removeItem('currentUser');
      }
    } catch (error) {
      console.error('Error saving user to storage:', error);
    }
  }

  initializeAuth(): Observable<User | null> {
    if (this._initialized) {
      return of(this._currentUser());
    }

    this._initialized = true;
    return of(this._currentUser());
  }

  me(): Observable<User> {
    const user = this._currentUser();
    if (user) {
      return of(user);
    }
    throw new Error('User not authenticated');
  }

  login(body: LoginReq): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/login`, body).pipe(
      tap(res => {
        if (res.user) {
          this._currentUser.set(res.user);
          this.saveUserToStorage(res.user);
        }
      })
    );
  }

  register(body: RegisterReq): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.base}/register`, body);
  }

  logout(): Observable<any> {
    return this.http.post(`${this.base}/logout`, {}).pipe(
      tap(() => {
        this._currentUser.set(null);
        this.saveUserToStorage(null);
        this._initialized = false;
      }),
      catchError(error => {
        // Even if logout fails on server, clear local state
        this._currentUser.set(null);
        this.saveUserToStorage(null);
        this._initialized = false;
        return of(null);
      })
    );
  }

  get currentUserValue(): User | null {
    return this._currentUser();
  }
}
