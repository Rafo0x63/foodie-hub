import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';

import { RecipeModel } from '../../models/recipe.model';
import { RecipeService } from '../../core/services/recipe.service';
import { CommentModel } from '../../models/comment.model';
import { CommentService } from '../../core/services/comment.service';

import { ButtonModule } from 'primeng/button';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';

@Component({
  selector: 'app-recipe-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonModule, ProgressSpinnerModule, CardModule, TagModule],
  templateUrl: './recipe-detail.html',
  styleUrl: './recipe-detail.css',
})
export class RecipeDetailComponent {
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly recipeService = inject(RecipeService);
  private readonly commentService = inject(CommentService);
  private readonly router = inject(Router);

  recipeData = signal<RecipeModel | null>(null);
  comments = signal<CommentModel[]>([]);
  loading = signal(false);
  commentsLoading = signal(false);
  commentSubmitting = signal(false);
  notFound = signal(false);
  commentError = signal('');
  editingCommentId = signal<number | null>(null);

  readonly ratingOptions = [1, 2, 3, 4, 5];

  commentForm = this.fb.nonNullable.group({
    text: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
  });

  editCommentForm = this.fb.nonNullable.group({
    text: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
  });

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
          this.loadComments(recipe.id);
        },
        error: (err) => {
          console.error('Greska pri dohvacanju recepta', err);
          this.recipeData.set(null);
          this.notFound.set(true);
        },
      });
  }

  loadComments(recipeId: number): void {
    this.commentsLoading.set(true);
    this.commentError.set('');

    this.commentService
      .getByRecipeId(recipeId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.commentsLoading.set(false)),
      )
      .subscribe({
        next: (comments) => this.comments.set(comments),
        error: (err) => {
          console.error('Greska pri dohvacanju komentara', err);
          this.comments.set([]);
          this.commentError.set('Komentari se trenutno ne mogu ucitati.');
        },
      });
  }

  submitComment(): void {
    const recipe = this.recipeData();

    if (!recipe) {
      return;
    }

    if (this.commentForm.invalid) {
      this.commentForm.markAllAsTouched();
      return;
    }

    this.commentSubmitting.set(true);
    this.commentError.set('');

    const request = {
      ...this.commentForm.getRawValue(),
      recipeId: recipe.id,
    };

    this.commentService
      .create(recipe.id, request)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.commentSubmitting.set(false)),
      )
      .subscribe({
        next: (comment) => {
          this.comments.set([comment, ...this.comments()]);
          this.commentForm.reset({ text: '', rating: 5 });
        },
        error: (err) => {
          console.error('Greska pri dodavanju komentara', err);
          this.commentError.set('Komentar nije spremljen.');
        },
      });
  }

  startEdit(comment: CommentModel): void {
    this.editingCommentId.set(comment.id);
    this.editCommentForm.reset({
      text: comment.text,
      rating: comment.rating,
    });
  }

  cancelEdit(): void {
    this.editingCommentId.set(null);
    this.editCommentForm.reset({ text: '', rating: 5 });
  }

  saveComment(comment: CommentModel): void {
    const recipe = this.recipeData();

    if (!recipe) {
      return;
    }

    if (this.editCommentForm.invalid) {
      this.editCommentForm.markAllAsTouched();
      return;
    }

    const request = {
      ...this.editCommentForm.getRawValue(),
      recipeId: recipe.id,
    };

    this.commentService
      .update(recipe.id, comment.id, request)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updatedComment) => {
          this.comments.set(this.comments().map((item) => item.id === updatedComment.id ? updatedComment : item));
          this.cancelEdit();
        },
        error: (err) => {
          console.error('Greska pri uredjivanju komentara', err);
          this.commentError.set('Komentar nije azuriran.');
        },
      });
  }

  deleteComment(comment: CommentModel): void {
    const recipe = this.recipeData();

    if (!recipe) {
      return;
    }

    this.commentService
      .delete(recipe.id, comment.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.comments.set(this.comments().filter((item) => item.id !== comment.id));
        },
        error: (err) => {
          console.error('Greska pri brisanju komentara', err);
          this.commentError.set('Komentar nije obrisan.');
        },
      });
  }

  goBack(): void {
    this.router.navigate(['/recipe-list']);
  }
}
