import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CreateRecipeRequest, RecipeModel } from '../../models/recipe.model';
import { Observable } from 'rxjs/internal/Observable';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/recipes';

  getAll(): Observable<RecipeModel[]> {
    return this.http.get<RecipeModel[]>(this.baseUrl);
  }

  getById(id: number): Observable<RecipeModel> {
    return this.http.get<RecipeModel>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateRecipeRequest): Observable<RecipeModel> {
    return this.http.post<RecipeModel>(this.baseUrl, request);
  }

  update(id: number, request: CreateRecipeRequest): Observable<RecipeModel> {
    return this.http.put<RecipeModel>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
