import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Button, ButtonModule } from "primeng/button";
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [Button, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private readonly router = inject(Router);
  public readonly authService = inject(AuthService);

  toRegister() {
    this.router.navigate(['/register']);
  }
  toLogin() {
    this.router.navigate(['/login']);
  }

  toHomepage(){
    this.router.navigate(['/']);
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
