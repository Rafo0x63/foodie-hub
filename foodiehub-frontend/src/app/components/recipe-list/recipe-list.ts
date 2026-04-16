import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { Router, RouterLink } from '@angular/router';
import { RecipeModel } from '../../models/recipe.model';
import { RecipeService } from '../../core/services/recipe.service';
import { ButtonModule } from 'primeng/button';

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
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  recipes: RecipeModel[] = [];
  filteredRecipes: RecipeModel[] = [];
  loading = false;
  showFilters = false;

  deletingRecipeId: number | null = null;

  readonly categories = [
    { label: 'Sve', value: null },
    { label: 'Tjestenina', value: 'Pasta' },
    { label: 'Salata', value: 'Salad' },
    { label: 'Deserti', value: 'Deserts' },
    { label: 'Pizza', value: 'Pizza' },
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
      .get()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          this.recipes = result;
          this.applyFilters();
          this.loading = false;
          this.cdr.detectChanges();
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

  editRecipe(id: number): void {
    this.router.navigate(['/edit-recipe', id]);
  }

  deleteRecipe(recipe: RecipeModel): void {
    const confirmed = window.confirm(`Jesi siguran da želiš obrisati recept "${recipe.title}"?`);

    if (!confirmed) {
      return;
    }

    this.deletingRecipeId = recipe.id;

    this.recipeService
      .delete(recipe.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => {
          this.deletingRecipeId = null;
          this.cdr.detectChanges();
        }),
      )
      .subscribe({
        next: () => {
          this.recipes = this.recipes.filter((r) => r.id !== recipe.id);
          this.applyFilters();
        },
        error: (err) => {
          console.error('Greška pri brisanju recepta', err);
        },
      });
  }

  addRecipe(): void {
    this.router.navigate(['/add-recipe']);
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
