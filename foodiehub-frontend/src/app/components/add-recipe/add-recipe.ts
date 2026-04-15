import { Component, inject } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RecipeService } from '../../core/services/recipe.service';
import { CreateRecipeRequest } from '../../models/recipe.model';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-add-recipe',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, Button],
  templateUrl: './add-recipe.html',
  styleUrl: './add-recipe.css',
})
export class AddRecipe {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly recipeService = inject(RecipeService);

  createRecipeRequest!: CreateRecipeRequest;

  addRecipeForm = this.fb.group({
    title: [''],
    description: [''],
    category: [''],
    imageUrl: [''],
    prepTime: [null as number | null],
    cookTime: [null as number | null],
    servings: [null as number | null],
    ingredients: this.fb.array([]),
    steps: this.fb.array([]),
  });

  get ingredients(): FormArray {
    return this.addRecipeForm.get('ingredients') as FormArray;
  }

  get steps(): FormArray {
    return this.addRecipeForm.get('steps') as FormArray;
  }

  saveRecipe(): void {
    console.log(this.addRecipeForm.value);

    this.createRecipeRequest = this.addRecipeForm.value as CreateRecipeRequest;

    this.recipeService.create(this.createRecipeRequest).subscribe({
      next: (createdRecipe) => {
        console.log('Recept uspješno kreiran', createdRecipe);
        this.router.navigate(['/recipe-list']);
      },
      error: (error) => {
        console.error('Greška pri kreiranju recepta', error);
      },
    });
  }

  addIngredient(): void {
    this.ingredients.push(
      this.fb.group({
        name: [''],
        amount: [''],
      }),
    );
  }

  removeIngredient(ingredientIndex: number): void {
    this.ingredients.removeAt(ingredientIndex);
  }

  addStep(): void {
    this.steps.push(
      this.fb.group({
        description: [''],
        imageUrl: [''],
      }),
    );
  }

  removeStep(stepIndex: number): void {
    this.steps.removeAt(stepIndex);
  }

  removeTag(tagIndex: number): void {
    throw new Error('Method not implemented.');
  }

  addTag(): void {
    throw new Error('Method not implemented.');
  }
}
