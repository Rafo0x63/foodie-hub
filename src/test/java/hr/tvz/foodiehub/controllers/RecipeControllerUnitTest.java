package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.foodiehub.model.dtos.RecipeDTO;
import hr.tvz.foodiehub.model.requests.CreateRecipeRequest;
import hr.tvz.foodiehub.services.interfaces.RecipeService;
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
@DisplayName("RecipeController - standalone unit tests")
class RecipeControllerUnitTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private RecipeDTO sampleDto() {
        return new RecipeDTO(1L, "Pizza", "tasty", "main", null);
    }

    private CreateRecipeRequest sampleRequest() {
        return new CreateRecipeRequest(
                "Pizza", "tasty", "main", null, 10, 20, 2, List.of(), List.of()
        );
    }

    @Test
    @DisplayName("GET /api/recipes returns the list")
    void getAllRecipes_returnsOk() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/recipes/search calls service.search with the params")
    void searchRecipes_returnsOk() throws Exception {
        when(recipeService.search(any(), any(), any(), any()))
                .thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/api/recipes/search")
                        .param("title", "pizza")
                        .param("maxTime", "30")
                        .param("category", "main")
                        .param("tags", "vegan,quick"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/recipes/{id} returns 200 with body")
    void getRecipeById_returnsOk() throws Exception {
        when(recipeService.getRecipeById(1L)).thenReturn(sampleDto());

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pizza"));
    }

    @Test
    @DisplayName("DELETE /api/recipes/{id} returns 204")
    void deleteRecipe_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());

        verify(recipeService).deleteRecipeById(1L);
    }

    @Test
    @DisplayName("POST /api/recipes returns 201 and Location header")
    void createNewRecipe_returnsCreated() throws Exception {
        when(recipeService.createNewRecipe(any())).thenReturn(sampleDto());

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/recipes/1"));
    }

    @Test
    @DisplayName("PUT /api/recipes/{id} returns 200 with updated body")
    void updateRecipe_returnsOk() throws Exception {
        when(recipeService.updateRecipe(eq(1L), any())).thenReturn(sampleDto());

        mockMvc.perform(put("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
