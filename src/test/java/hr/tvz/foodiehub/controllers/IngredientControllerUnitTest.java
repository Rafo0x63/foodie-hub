package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.foodiehub.model.dtos.IngredientDTO;
import hr.tvz.foodiehub.model.requests.CreateIngredientRequest;
import hr.tvz.foodiehub.services.interfaces.IngredientService;
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
@DisplayName("IngredientController - standalone unit tests")
class IngredientControllerUnitTest {

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private IngredientController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/recipes/{id}/ingredients returns list")
    void getAllIngredients_returnsOk() throws Exception {
        IngredientDTO dto = new IngredientDTO(1L, "Cheese", "200g", 1L);
        when(ingredientService.getAllRecipeIngredients(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/recipes/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cheese"));
    }

    @Test
    @DisplayName("DELETE /api/recipes/{rid}/ingredients/{iid} returns 204")
    void deleteIngredient_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/recipes/1/ingredients/2"))
                .andExpect(status().isNoContent());

        verify(ingredientService).deleteIngredientById(1L, 2L);
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/ingredients returns 201 and Location")
    void createIngredient_returnsCreated() throws Exception {
        IngredientDTO dto = new IngredientDTO(7L, "Cheese", "200g", 1L);
        when(ingredientService.createNewIngredient(any())).thenReturn(dto);

        CreateIngredientRequest request = new CreateIngredientRequest("Cheese", "200g", 1L);

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/ingredients/7"));
    }

    @Test
    @DisplayName("PUT /api/recipes/{rid}/ingredients/{iid} returns 200 with updated body")
    void updateIngredient_returnsOk() throws Exception {
        IngredientDTO dto = new IngredientDTO(2L, "Mozzarella", "300g", 1L);
        when(ingredientService.updateIngredient(eq(2L), eq(1L), any())).thenReturn(dto);

        CreateIngredientRequest request = new CreateIngredientRequest("Mozzarella", "300g", 1L);

        mockMvc.perform(put("/api/recipes/1/ingredients/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mozzarella"));
    }
}
