package hr.tvz.foodiehub.model.requests;

import jakarta.validation.constraints.*;

public record CreateCommentRequest(
        @NotBlank @Size(min = 2, max = 100)
        String text,
        @Min(1) @Max(5)
        int rating,
        @NotNull
        Long recipeId
) {
}
