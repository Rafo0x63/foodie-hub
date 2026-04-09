import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class Recipe {
  private api = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) {}
}
