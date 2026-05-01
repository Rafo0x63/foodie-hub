package hr.tvz.foodiehub.model.requests;

public record LoginRequest(
        String email,
        String password
){}
