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
  difficulty: string;
  totalTime: string;
  prepTime?: string;
  cookTime?: string;
  image: string;
  author: RecipeAuthor;
  nutrition: RecipeNutrition;
  servings: number;
  rating: number;
  reviews: number;
  likes: number;
  views: number;
  saves?: number;
  ingredients?: RecipeIngredient[];
  steps?: RecipeStep[];
  tips?: string[];
  comments?: RecipeComment[];
  tags?: string[];
}
