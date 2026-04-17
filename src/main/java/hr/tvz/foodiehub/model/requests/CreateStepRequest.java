package hr.tvz.foodiehub.model.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStepRequest(
        @NotNull @Min(1)
        int stepNumber,
        @NotBlank @Size(min = 2, max = 30)
        String title,
        @NotBlank @Size(min = 3, max = 200)
        String description,
        @NotNull @Min(1)
        int time
) {
}
