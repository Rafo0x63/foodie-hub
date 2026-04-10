import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { recipesDatabase } from '../../mock-data/recipes';
import { RecipeModel } from '../../models/recipe.model';

@Injectable({
  providedIn: 'root',
})
export class Recipe {
  private api = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) {}

  getAll(): RecipeModel[] {
    return Object.values(recipesDatabase);
  }

  getById(id: number): RecipeModel | null {
    return Object.values(recipesDatabase).find((r) => r.id === id) ?? null;
  }
}
