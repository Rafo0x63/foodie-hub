import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserDTO} from '../../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/admin'

  getUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.baseUrl}/users`);
  }
}
