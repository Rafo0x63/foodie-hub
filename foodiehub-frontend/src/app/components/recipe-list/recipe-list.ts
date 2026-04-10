import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';
import { RecipeModel } from '../../models/recipe.model';
import { Recipe } from '../../core/services/recipe';

const CATEGORIES = ['Sve', 'Pasta', 'Salate', 'Deserti', 'Doručak', 'Azijska kuhinja'];
const DIFFICULTIES = ['Sve', 'Lako', 'Srednje', 'Teško'];
const SORT_OPTIONS = [
  { value: 'rating', label: 'Najbolje ocijenjeno' },
  { value: 'likes', label: 'Najpopularnije' },
  { value: 'time', label: 'Najbrže' },
  { value: 'newest', label: 'Najnovije' },
];

@Component({
  selector: 'app-recipe-list',
  imports: [RouterLink, FormsModule, NgClass],
  templateUrl: './recipe-list.html',
  styleUrl: './recipe-list.css',
})
export class RecipeList implements OnInit {
  private recipeService = inject(Recipe);

  readonly categories = CATEGORIES;
  readonly difficulties = DIFFICULTIES;
  readonly sortOptions = SORT_OPTIONS;

  searchQuery = '';
  selectedCategory = 'Sve';
  selectedDifficulty = 'Sve';
  sortBy = 'rating';
  showFilters = false;
  maxTime = 120;
  maxCalories = 1000;

  private allRecipes: RecipeModel[] = [];

  get sortedRecipes(): RecipeModel[] {
    const filtered = this.allRecipes.filter((recipe) => {
      const matchesSearch =
        recipe.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        recipe.description.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchesCategory =
        this.selectedCategory === 'Sve' || recipe.category === this.selectedCategory;
      const matchesDifficulty =
        this.selectedDifficulty === 'Sve' || recipe.difficulty === this.selectedDifficulty;
      const matchesTime = parseInt(recipe.totalTime) <= this.maxTime;
      const matchesCalories = recipe.nutrition.calories <= this.maxCalories;

      return matchesSearch && matchesCategory && matchesDifficulty && matchesTime && matchesCalories;
    });

    return [...filtered].sort((a, b) => {
      switch (this.sortBy) {
        case 'rating':
          return b.rating - a.rating;
        case 'likes':
          return b.likes - a.likes;
        case 'time':
          return parseInt(a.totalTime) - parseInt(b.totalTime);
        case 'newest':
          return b.views - a.views;
        default:
          return 0;
      }
    });
  }

  ngOnInit() {
    this.allRecipes = this.recipeService.getAll();
  }

  resetFilters() {
    this.searchQuery = '';
    this.selectedCategory = 'Sve';
    this.selectedDifficulty = 'Sve';
    this.maxTime = 120;
    this.maxCalories = 1000;
  }

  setMaxTime(event: Event) {
    this.maxTime = +(event.target as HTMLInputElement).value;
  }

  setMaxCalories(event: Event) {
    this.maxCalories = +(event.target as HTMLInputElement).value;
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.onerror = null;
    img.src = '/Placeholder.png';
  }
}
