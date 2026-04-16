package hr.tvz.foodiehub.model.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateIngredientRequest(
        @NotBlank @Size(min = 3 , max = 100)
        String name,
        @NotBlank
        String amount,
        @NotNull
        Long recipeId
) {
}
