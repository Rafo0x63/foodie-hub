package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Role;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.LoginRequest;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.security.CookieService;
import hr.tvz.foodiehub.security.JwtService;
import hr.tvz.foodiehub.services.interfaces.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CookieService cookieService;

    @Override
    public UserDTO login(LoginRequest loginRequest, HttpServletResponse response) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new RuntimeException("Neispravna lozinka.");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        String token = jwtService.generateToken(user.getEmail(), roles);
        cookieService.setAccessTokenCookie(response, token);

        return mapToDTO(user);
    }

    @Override
    public Optional<UserDTO> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null || email.equals("anonymousUser")) {
            return Optional.empty();
        }
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(this::mapToDTO);
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }
}
