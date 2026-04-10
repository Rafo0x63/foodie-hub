import { Routes } from '@angular/router';
import { RecipeListComponent } from './components/recipe-list/recipe-list';
import { RecipeDetailComponent } from './components/recipe-detail/recipe-detail';
import { EditRecipe } from './components/edit-recipe/edit-recipe';
import { AddRecipe } from './components/add-recipe/add-recipe';

export const routes: Routes = [
  { path: '', redirectTo: 'recipe-list', pathMatch: 'full' },
  { path: 'recipe-list', component: RecipeListComponent },
  { path: 'recipe/:id', component: RecipeDetailComponent },
  { path: 'add-recipe', component: AddRecipe },
  { path: 'edit-recipe/:id', component: EditRecipe },
];
