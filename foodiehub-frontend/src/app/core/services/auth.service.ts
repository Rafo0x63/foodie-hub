import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { LoginData } from '../../models/loginData.model';
import { RegisterData } from '../../models/registerData.model';
import { UserDTO } from '../../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly authUrl = 'http://localhost:8080/api/auth';

  currentUser = signal<UserDTO | null>(null);

  constructor() {
    this.http.get<UserDTO>(`${this.authUrl}/currentUser`).pipe(
      catchError(() => of(null))
    ).subscribe(user => this.currentUser.set(user));
  }

  isAuthenticated() {
    return this.http.get(`${this.authUrl}/currentUser`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  login(loginData: LoginData): Observable<UserDTO> {
    return this.http.post<UserDTO>(`${this.authUrl}/login`, loginData).pipe(
      tap(user => {
        this.currentUser.set(user);
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }

  register(registerData: RegisterData): Observable<UserDTO> {
    return this.http.post<UserDTO>(`${this.authUrl}/register`, registerData);
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.authUrl}/logout`, {}).pipe(
      tap(() => this.currentUser.set(null)),
      catchError(() => {
        this.currentUser.set(null);
        return of(void 0);
      })
    );
  }

  loadUserFromStorage() {
    const user = localStorage.getItem('user');
    if (user) {
      this.currentUser.set(JSON.parse(user));
    }
  }

  getCurrentUser(): any {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  getUserRole(): string | null {
    return this.getCurrentUser()?.role || null;
  }

  getUserId(): number | null {
    return this.getCurrentUser()?.id || null;
  }

  hasRole(role: string): boolean {
    return this.getUserRole() === role;
  }

  isAdmin(): boolean {
    const role = this.getUserRole();
    return role === 'ROLE_ADMIN' || role ==='ROLE_SUPER_ADMIN';
  }
}
