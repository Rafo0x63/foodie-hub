import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CommentModel, CreateCommentRequest } from '../../models/comment.model';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private readonly http = inject(HttpClient);
  private readonly recipeUrl = 'http://localhost:8080/api/recipes';

  getByRecipeId(recipeId: number): Observable<CommentModel[]> {
    return this.http.get<CommentModel[]>(`${this.recipeUrl}/${recipeId}/comments`);
  }

  create(recipeId: number, request: CreateCommentRequest): Observable<CommentModel> {
    return this.http.post<CommentModel>(`${this.recipeUrl}/${recipeId}/comments`, request);
  }

  update(recipeId: number, commentId: number, request: CreateCommentRequest): Observable<CommentModel> {
    return this.http.put<CommentModel>(`${this.recipeUrl}/${recipeId}/comments/${commentId}`, request);
  }

  delete(recipeId: number, commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.recipeUrl}/${recipeId}/comments/${commentId}`);
  }
}
