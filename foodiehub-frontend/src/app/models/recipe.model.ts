export interface RecipeAuthor {
  name: string;
  avatar?: string;
  rank?: string;
}

export interface RecipeNutrition {
  calories: number;
  protein?: number;
  carbs?: number;
  fat?: number;
}

export interface RecipeIngredient {
  name: string;
  amount: string;
}

export interface RecipeStep {
  number: number;
  title: string;
  description: string;
  time: string;
  image: string;
}

export interface RecipeComment {
  id: number | string;
  author: string;
  rating: number;
  date: string;
  text: string;
  likes: number;
}

export interface RecipeModel {
  id: number;
  title: string;
  description: string;
  category: string;
}

export interface CreateRecipeRequest {
  title: string;
  description: string;
  category: string;
  image?: string;
  /*difficulty: string;
  totalTime: string;
  prepTime?: string;
  cookTime?: string;
  servings: number;
  calories?: number;*/
}
