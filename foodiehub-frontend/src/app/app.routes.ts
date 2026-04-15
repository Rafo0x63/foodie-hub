import { Routes } from '@angular/router';
import { RecipeListComponent } from './components/recipe-list/recipe-list';
import { RecipeDetailComponent } from './components/recipe-detail/recipe-detail';
import { EditRecipe } from './components/edit-recipe/edit-recipe';
import { AddRecipe } from './components/add-recipe/add-recipe';
import { Homepage } from './components/homepage/homepage';

export const routes: Routes = [
  { path: '', redirectTo: 'homepage', pathMatch: 'full' },
  { path: 'homepage', component: Homepage },
  { path: 'recipe-list', component: RecipeListComponent },
  { path: 'recipe/:id', component: RecipeDetailComponent },
  { path: 'add-recipe', component: AddRecipe },
  { path: 'edit-recipe/:id', component: EditRecipe },
];
