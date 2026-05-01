package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.LoginRequest;
import hr.tvz.foodiehub.model.requests.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface AuthService {
    UserDTO login(LoginRequest loginRequest, HttpServletResponse response);
    Optional<UserDTO> getCurrentUser();
    UserDTO register(RegisterRequest registerRequest);
    void logout(HttpServletResponse response);
}
