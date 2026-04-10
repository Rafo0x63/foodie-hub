import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';
import { RecipeModel } from '../../models/recipe.model';
import { Recipe } from '../../core/services/recipe';

@Component({
  selector: 'app-recipe-detail',
  imports: [RouterLink, FormsModule, NgClass],
  templateUrl: './recipe-detail.html',
  styleUrl: './recipe-detail.css',
})
export class RecipeDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private recipeService = inject(Recipe);

  recipeData: RecipeModel | null = null;
  liked = false;
  saved = false;
  following = false;
  newComment = '';
  commentRating = 0;

  readonly starValues = [1, 2, 3, 4, 5];

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam !== null ? +idParam : null;
    this.recipeData = id !== null ? this.recipeService.getById(id) : null;
  }

  getStarsArray(count: number): number[] {
    return Array.from({ length: count }, (_, i) => i);
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.onerror = null;
    img.src = '/Placeholder.png';
  }
}
