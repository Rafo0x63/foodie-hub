package hr.tvz.foodiehub.model.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateRecipeRequest(
        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotBlank
        String category,

        String imageUrl,
        Integer prepTime,
        Integer cookTime,
        Integer servings,
        List<String> ingredients,
        List<String> steps
){}
