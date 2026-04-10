import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Router, RouterLink } from '@angular/router';
import { RecipeModel } from '../../models/recipe.model';
import { RecipeService } from '../../core/services/recipe.service';
import { Button, ButtonModule } from "primeng/button";


@Component({
  selector: 'app-recipe-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ButtonModule],
  templateUrl: './recipe-list.html',
  styleUrl: './recipe-list.css',
})
export class RecipeListComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly recipeService = inject(RecipeService);
  private readonly router = inject(Router)

  recipes: RecipeModel[] = [];
  filteredRecipes: RecipeModel[] = [];
  loading = false;
  showFilters = false;

  readonly categories = [
    { label: 'Sve', value: null },
    { label: 'Tjestenina', value: 'Tjestenina' },
    { label: 'Salate', value: 'Salate' },
    { label: 'Deserti', value: 'Deserti' },
    { label: 'Doručak', value: 'Doručak' },
    { label: 'Azijska kuhinja', value: 'Azijska kuhinja' },
  ];

  readonly sortOptions = [
    { label: 'Naziv A-Z', value: 'titleAsc' },
    { label: 'Naziv Z-A', value: 'titleDesc' },
    { label: 'Kategorija A-Z', value: 'categoryAsc' },
    { label: 'Kategorija Z-A', value: 'categoryDesc' },
  ];

  readonly searchControl = new FormControl<string | null>(null);

  filterForm: FormGroup = this.fb.group({
    category: new FormControl<string | null>(null),
    sortBy: new FormControl<string>('titleAsc'),
  });

  constructor() {
    this.handleFilterChanges();
    this.loadRecipes();
  }

  loadRecipes(): void {
    this.loading = true;

    this.recipeService
      .getAll()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          this.recipes = result;
          this.applyFilters();
          this.loading = false;
        },
        error: (err) => {
          console.error('Greška pri dohvaćanju recepata', err);
          this.recipes = [];
          this.filteredRecipes = [];
          this.loading = false;
        },
      });
  }

  private handleFilterChanges(): void {
    this.filterForm.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.applyFilters();
    });

    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.applyFilters();
      });
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  clearFilters(): void {
    this.searchControl.setValue(null, { emitEvent: false });

    this.filterForm.reset(
      {
        category: null,
        sortBy: 'titleAsc',
      },
      { emitEvent: false },
    );

    this.applyFilters();
  }

  editRecipe(id: number) {
    this.router.navigate(['/edit-recipe', id]);
  }

  private applyFilters(): void {
    const search = this.searchControl.value?.trim().toLowerCase() ?? '';
    const category = this.filterForm.value.category;
    const sortBy = this.filterForm.value.sortBy ?? 'titleAsc';

    let filtered = this.recipes.filter((recipe) => {
      const matchesSearch =
        search.length === 0 ||
        recipe.title.toLowerCase().includes(search) ||
        recipe.description.toLowerCase().includes(search);

      const matchesCategory = !category || recipe.category === category;

      return matchesSearch && matchesCategory;
    });

    filtered = [...filtered].sort((a, b) => {
      switch (sortBy) {
        case 'titleAsc':
          return a.title.localeCompare(b.title);
        case 'titleDesc':
          return b.title.localeCompare(a.title);
        case 'categoryAsc':
          return a.category.localeCompare(b.category);
        case 'categoryDesc':
          return b.category.localeCompare(a.category);
        default:
          return 0;
      }
    });

    this.filteredRecipes = filtered;
  }
}
