import {HttpClient, HttpParams} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CreateRecipeRequest, RecipeModel } from '../../models/recipe.model';
import { Observable } from 'rxjs/internal/Observable';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/recipes';

  get(name?: string, maxTime?: number, tags?: string[]): Observable<RecipeModel[]> {
    let params = new HttpParams();

    if (name) {
      params = params.set('name', name);
    }

    if (maxTime !== null && maxTime !== undefined) {
      params = params.set('maxTime', maxTime.toString());
    }

    if (tags && tags.length > 0) {
      tags.forEach(tag => {
        params = params.append('tags', tag);
      });
    }

    return this.http.get<RecipeModel[]>(this.baseUrl, {params});
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
