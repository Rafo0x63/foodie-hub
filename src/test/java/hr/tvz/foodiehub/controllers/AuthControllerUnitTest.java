package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.LoginRequest;
import hr.tvz.foodiehub.services.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - standalone unit tests")
class AuthControllerUnitTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/auth/login returns 200 with user DTO")
    void login_returnsOk() throws Exception {
        UserDTO dto = new UserDTO(1L, "Ana", "ana@gmail.com", "ROLE_USER");
        when(authService.login(any(LoginRequest.class), any())).thenReturn(dto);

        LoginRequest request = new LoginRequest("ana@gmail.com", "password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@gmail.com"));
    }

    @Test
    @DisplayName("POST /api/auth/logout returns 204")
    void logout_returnsNoContent() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());

        verify(authService).logout(any());
    }

    @Test
    @DisplayName("GET /api/auth/currentUser returns 200 when user is authenticated")
    void getCurrentUser_returnsOkWhenPresent() throws Exception {
        UserDTO dto = new UserDTO(1L, "Ana", "ana@gmail.com", "ROLE_USER");
        when(authService.getCurrentUser()).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/auth/currentUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@gmail.com"));
    }

    @Test
    @DisplayName("GET /api/auth/currentUser returns 401 when no user is authenticated")
    void getCurrentUser_returnsUnauthorizedWhenEmpty() throws Exception {
        when(authService.getCurrentUser()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/currentUser"))
                .andExpect(status().isUnauthorized());
    }
}
