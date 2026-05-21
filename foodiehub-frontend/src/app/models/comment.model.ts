export interface CommentModel {
  id: number;
  text: string;
  rating: number;
  createdAt: string;
  recipeId: number;
  userId: number;
  username: string;
}

export interface CreateCommentRequest {
  text: string;
  rating: number;
  recipeId: number;
}
