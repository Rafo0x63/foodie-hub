package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController - standalone unit tests")
class AdminControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // standalone setup bypasses @PreAuthorize, so we test the controller method itself
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/admin/users returns list when authorized")
    void getAllUsers_returnsList() throws Exception {
        UserDTO dto = new UserDTO(1L, "Ana", "ana@gmail.com", "ROLE_USER");
        when(userService.getAllUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@gmail.com"));
    }
}
