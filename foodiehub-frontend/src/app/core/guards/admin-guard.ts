import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import {AuthService} from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);

  console.log('ROLE:', authService.getUserRole());

  return authService.isAdmin();
};
