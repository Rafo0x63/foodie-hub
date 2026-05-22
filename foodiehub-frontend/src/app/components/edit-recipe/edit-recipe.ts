import { Component, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { RecipeService } from '../../core/services/recipe.service';
import { CreateRecipeRequest, RecipeModel } from '../../models/recipe.model';
import { Button } from "primeng/button";


@Component({
  selector: 'app-edit-recipe',
  imports: [ReactiveFormsModule, RouterLink, Button],
  templateUrl: './edit-recipe.html',
  styleUrl: './edit-recipe.css',
})
export class EditRecipe {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private readonly recipeService = inject(RecipeService);

  editRecipeRequest!: CreateRecipeRequest;  
  id!: number;
  recipeData = signal<RecipeModel | null>(null);
  submitted = false;

  editRecipeForm = this.fb.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
    category: ['', Validators.required],
    imageUrl: [''],
    prepTime: [null as number | null],
    cookTime: [null as number | null],
    servings: [null as number | null],
    ingredients: this.fb.array([]),
    steps: this.fb.array([])
  });

  constructor(){
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.recipeService.getById(this.id).subscribe({
      next: (recipe) => {
        this.recipeData.set(recipe);
        this.editRecipeForm.patchValue({
          title: recipe.title ?? '',
          description: recipe.description ?? '',
          category: recipe.category ?? '',
          imageUrl: '',
          prepTime: null,
          cookTime: null,
          servings: null
        });
      },
      error: (error) => {
        console.error('Greška pri dohvaćanju recepta', error);
        this.recipeData.set(null);
      }
    });
  }

  get ingredients(): FormArray{
    return this.editRecipeForm.get('ingredients') as FormArray;
  }

  get steps(): FormArray{
    return this.editRecipeForm.get('steps') as FormArray
  }

  saveRecipe(){
    this.submitted = true;

    if (this.editRecipeForm.invalid) {
      this.editRecipeForm.markAllAsTouched();
      return;
    }

    console.log(this.editRecipeForm.value);
    this.editRecipeRequest = this.editRecipeForm.value as CreateRecipeRequest;
    this.recipeService.update(this.id, this.editRecipeRequest).subscribe({
      next: (updatedRecipe) => {
        this.router.navigate(['/recipe-list']);
        console.log('Recept uspješno ažuriran', updatedRecipe);
      },
      error: (error) => {
        console.error('Greška pri ažuriranju recepta', error);
      }
    })
  }



  
  
  


  /*Za kasnije*/
  removeTag(ingredientId: number) {
    throw new Error('Method not implemented.');
  }
  addTag() {
    throw new Error('Method not implemented.');
  }
  addIngredient() {
  throw new Error('Method not implemented.');
  }
  removeIngredient(ingredientId: number) {
  throw new Error('Method not implemented.');
  }
  addStep() {
  throw new Error('Method not implemented.');
  }
  removeStep(stepId: number) {
  throw new Error('Method not implemented.');
  }

}
