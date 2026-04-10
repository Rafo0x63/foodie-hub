import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';

import { RecipeModel } from '../../models/recipe.model';
import { RecipeService } from '../../core/services/recipe.service';

import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-recipe-detail',
  standalone: true,
  imports: [CommonModule, ButtonModule, ProgressSpinnerModule, CardModule, TagModule],
  templateUrl: './recipe-detail.html',
  styleUrl: './recipe-detail.css',
})
export class RecipeDetailComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly route = inject(ActivatedRoute);
  private readonly recipeService = inject(RecipeService);
  private readonly router = inject(Router);

  recipeData = signal<RecipeModel | null>(null);
  loading = signal(false);
  notFound = signal(false);

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : null;

    if (!id || Number.isNaN(id)) {
      this.notFound.set(true);
      return;
    }

    this.loadRecipe(id);
  }

  loadRecipe(id: number): void {
    this.loading.set(true);
    this.notFound.set(false);

    this.recipeService
      .getById(id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (recipe) => {
          this.recipeData.set(recipe);
        },
        error: (err) => {
          console.error('Greška pri dohvaćanju recepta', err);
          this.recipeData.set(null);
          this.notFound.set(true);
        },
      });
  }

  goBack(): void {
    this.router.navigate(['/recipe-list']);
  }
}
