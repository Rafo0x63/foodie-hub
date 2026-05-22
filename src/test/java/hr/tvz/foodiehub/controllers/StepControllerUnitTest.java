package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.foodiehub.model.dtos.StepDTO;
import hr.tvz.foodiehub.model.requests.CreateStepRequest;
import hr.tvz.foodiehub.services.interfaces.StepService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("StepController - standalone unit tests")
class StepControllerUnitTest {

    @Mock
    private StepService stepService;

    @InjectMocks
    private StepController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/recipes/{id}/steps returns the list")
    void getAllSteps_returnsOk() throws Exception {
        StepDTO dto = new StepDTO(1L, 1, "Mix", "Mix ingredients", 5, 1L);
        when(stepService.getAllStepsByRecipeId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/recipes/1/steps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Mix"));
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/steps returns 201 with Location")
    void createStep_returnsCreated() throws Exception {
        StepDTO dto = new StepDTO(42L, 1, "Mix", "Mix ingredients", 5, 1L);
        when(stepService.createStepForRecipe(eq(1L), any())).thenReturn(dto);

        CreateStepRequest request = new CreateStepRequest(1, "Mix", "Mix ingredients well", 5);

        mockMvc.perform(post("/api/recipes/1/steps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/recipes/1/steps/42"));
    }
}
