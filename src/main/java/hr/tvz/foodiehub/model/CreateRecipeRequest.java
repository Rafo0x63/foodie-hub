package hr.tvz.foodiehub.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRecipeRequest(
        @NotBlank @Size( min = 3, max = 50)
        String title,
        @NotBlank @Size( min = 10, max = 1000)
        String description,
        //String image,
        @NotBlank
        String category
){}
