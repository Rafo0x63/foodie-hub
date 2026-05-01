import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from "./layout/navbar/navbar";
import { Footer } from './layout/footer/footer';
import {AuthService} from './core/services/auth.service';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar, Footer],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App {
  protected readonly title = signal('foodiehub-frontend');
  constructor(private authService: AuthService) {
    this.authService.loadUserFromStorage();
  }
}
