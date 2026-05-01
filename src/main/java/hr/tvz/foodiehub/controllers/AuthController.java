package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.LoginRequest;
import hr.tvz.foodiehub.services.interfaces.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        return ResponseEntity.ok(authService.login(loginRequest, response));
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return authService.getCurrentUser()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

}
