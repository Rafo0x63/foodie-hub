import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { combineLatest } from 'rxjs';
import { debounceTime, distinctUntilChanged, finalize, startWith, switchMap } from 'rxjs/operators';
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
    { label: 'Tjestenina', value: 'Tjestenina' },
    { label: 'Salata', value: 'Salata' },
    { label: 'Deserti', value: 'Deserti' },
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
    this.setupSearch();
    this.setupSortFilter();
  }

  private setupSearch(): void {
    const title$ = this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      startWith(null as string | null),
    );

    const category$ = this.filterForm.get('category')!.valueChanges.pipe(
      startWith(null as string | null),
    );

    combineLatest([title$, category$])
      .pipe(
        switchMap(([titleValue, categoryValue]) => {
          const title = titleValue?.trim() || undefined;
          const category = categoryValue || undefined;
          this.loading = true;

          return (title || category
            ? this.recipeService.search(title, undefined, undefined, category)
            : this.recipeService.getAll()
          ).pipe(
            finalize(() => {
              this.loading = false;
              this.cdr.detectChanges();
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (result) => {
          this.recipes = result;
          this.applySort();
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

  private setupSortFilter(): void {
    this.filterForm.get('sortBy')!.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((sortBy) => this.applySort(sortBy ?? 'titleAsc'));
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  clearFilters(): void {
    this.searchControl.setValue(null);
    this.filterForm.reset({
      category: null,
      sortBy: 'titleAsc',
    });
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
          this.applySort();
        },
        error: (err) => {
          console.error('Greška pri brisanju recepta', err);
        },
      });
  }

  addRecipe(): void {
    this.router.navigate(['/add-recipe']);
  }

  private applySort(sortBy?: string): void {
    const sort = sortBy ?? this.filterForm.value.sortBy ?? 'titleAsc';

    this.filteredRecipes = [...this.recipes].sort((a, b) => {
      switch (sort) {
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
  }
}
