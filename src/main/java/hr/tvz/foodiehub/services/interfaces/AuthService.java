package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface AuthService {
    UserDTO login(LoginRequest loginRequest, HttpServletResponse response);
    Optional<UserDTO> getCurrentUser();
}
