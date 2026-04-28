import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RecipeService } from '../services/recipe.service';
import { AuthService } from '../services/auth.service';
import { map } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return authService.isAuthenticated().pipe(
    map(isAuth => 
      isAuth ? true : router.createUrlTree(['/login'])
  ));
};
