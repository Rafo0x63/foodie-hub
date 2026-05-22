package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Role;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.LoginRequest;
import hr.tvz.foodiehub.model.requests.RegisterRequest;
import hr.tvz.foodiehub.repositories.RoleRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.security.CookieService;
import hr.tvz.foodiehub.security.JwtService;
import hr.tvz.foodiehub.services.implementations.AuthServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - unit tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private CookieService cookieService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role userRole;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setRoleName("ROLE_USER");

        user = new User();
        user.setId(10L);
        user.setName("Ana");
        user.setEmail("ana@gmail.com");
        user.setPassword("hashed-password");
        user.setRoles(new ArrayList<>(List.of(userRole)));

        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("login returns user DTO and sets access cookie on valid credentials")
    void login_success() {
        LoginRequest request = new LoginRequest("ana@gmail.com", "plain-password");

        when(userRepository.findByEmailAndDeletedAtIsNull("ana@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "hashed-password"))
                .thenReturn(true);
        when(jwtService.generateToken(eq("ana@gmail.com"), any()))
                .thenReturn("jwt-token");

        UserDTO result = authService.login(request, response);

        assertThat(result.getEmail()).isEqualTo("ana@gmail.com");
        verify(cookieService).setAccessTokenCookie(response, "jwt-token");
    }

    @Test
    @DisplayName("login throws when user is not found")
    void login_throwsWhenUserMissing() {
        when(userRepository.findByEmailAndDeletedAtIsNull("missing@example.com"))
                .thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("missing@example.com", "x");

        assertThatThrownBy(() -> authService.login(request, response))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Korisnik");
    }

    @Test
    @DisplayName("login throws on wrong password")
    void login_throwsOnWrongPassword() {
        when(userRepository.findByEmailAndDeletedAtIsNull("ana@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed-password"))
                .thenReturn(false);

        LoginRequest request = new LoginRequest("ana@gmail.com", "wrong");

        assertThatThrownBy(() -> authService.login(request, response))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("lozinka");
    }

    @Test
    @DisplayName("register saves new user with default role and returns DTO")
    void register_success() {
        RegisterRequest request = new RegisterRequest(
                "New User", "new@example.com", "verysecret"
        );

        when(userRepository.findByEmailAndDeletedAtIsNull("new@example.com"))
                .thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("ROLE_USER"))
                .thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("verysecret")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        UserDTO result = authService.register(request);

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("register throws when email already exists")
    void register_throwsOnDuplicateEmail() {
        when(userRepository.findByEmailAndDeletedAtIsNull("ana@gmail.com"))
                .thenReturn(Optional.of(user));

        RegisterRequest request = new RegisterRequest("Ana", "ana@gmail.com", "verysecret");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("već postoji");
    }

    @Test
    @DisplayName("register throws when default role is missing")
    void register_throwsWhenRoleMissing() {
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString()))
                .thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("ROLE_USER"))
                .thenReturn(Optional.empty());

        RegisterRequest request = new RegisterRequest("X", "x@example.com", "verysecret");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("uloga");
    }

    @Test
    @DisplayName("getCurrentUser returns DTO when authenticated")
    void getCurrentUser_returnsUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ana@gmail.com", null, List.of())
        );
        when(userRepository.findByEmailAndDeletedAtIsNull("ana@gmail.com"))
                .thenReturn(Optional.of(user));

        Optional<UserDTO> result = authService.getCurrentUser();

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("ana@gmail.com");
    }

    @Test
    @DisplayName("getCurrentUser returns empty for anonymous principal")
    void getCurrentUser_emptyForAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of())
        );

        Optional<UserDTO> result = authService.getCurrentUser();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("logout clears the access cookie")
    void logout_clearsAccessCookie() {
        authService.logout(response);

        verify(cookieService).clearAccessToken(response);
    }
}
