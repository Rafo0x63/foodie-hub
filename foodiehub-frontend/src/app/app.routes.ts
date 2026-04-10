import { Routes } from '@angular/router';
import { RecipeList } from './components/recipe-list/recipe-list';
import { RecipeDetail } from './components/recipe-detail/recipe-detail';

export const routes: Routes = [
  { path: '', redirectTo: 'recipe-list', pathMatch: 'full' },
  { path: 'recipe-list', component: RecipeList },
  { path: 'recept/:id', component: RecipeDetail },
];
