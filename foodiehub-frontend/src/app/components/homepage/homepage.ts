import { Component, inject } from '@angular/core';
import { ButtonModule } from "primeng/button";
import { RecipeService } from '../../core/services/recipe.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-homepage',
  imports: [ButtonModule],
  templateUrl: './homepage.html',
  styleUrl: './homepage.css',
})
export class Homepage {
  private readonly recipeService = inject(RecipeService);
  private readonly router = inject(Router)

  toRecipeList() {
    this.router.navigate(['/recipe-list']);
  }
  
  toRegister() {
    this.router.navigate(['/register']);
  }
}
