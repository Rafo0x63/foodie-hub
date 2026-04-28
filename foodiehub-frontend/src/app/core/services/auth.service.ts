import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, map, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly authUrl = 'http://localhost:8080/api/auth';

  isAuthenticated(){
    return this.http.get(`${this.authUrl}/currentUser`).pipe(
      map(() => true),
      catchError(() => of(false))
    )
  }
}
