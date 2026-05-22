package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.CreateUserRequest;
import hr.tvz.foodiehub.services.interfaces.UserService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController - standalone unit tests")
class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private UserDTO sample() {
        return new UserDTO(1L, "Ana", "ana@gmail.com", "ROLE_USER");
    }

    @Test
    @DisplayName("GET /api/users returns the list")
    void getAllUsers_returnsOk() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@gmail.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} returns the user")
    void getUserById_returnsOk() throws Exception {
        when(userService.getUserByID(1L)).thenReturn(sample());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} returns 204")
    void deleteUser_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUserById(1L);
    }

    @Test
    @DisplayName("POST /api/users returns 201 and Location header")
    void createNewUser_returnsCreated() throws Exception {
        when(userService.createNewUser(any())).thenReturn(sample());

        CreateUserRequest request = new CreateUserRequest("Ana", "ana@gmail.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} returns 200 with updated body")
    void updateUser_returnsOk() throws Exception {
        when(userService.updateUser(eq(1L), any())).thenReturn(sample());

        CreateUserRequest request = new CreateUserRequest("Ana", "ana@gmail.com");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
