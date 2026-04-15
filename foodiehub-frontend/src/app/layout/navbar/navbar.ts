import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from "primeng/button";


@Component({
  selector: 'app-navbar',
  imports: [ButtonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private readonly router = inject(Router);

  toRegister() {
    this.router.navigate(['/register']);
  }
  toLogin() {
    this.router.navigate(['/login']);
  }

  toHomepage(){
    this.router.navigate(['/']);
  }
}
