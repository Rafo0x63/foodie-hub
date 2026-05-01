package hr.tvz.foodiehub.model.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50)
        String name,
        @Email @NotBlank
        String email,
        @NotBlank @Size(min = 8)
        String password
) {}
