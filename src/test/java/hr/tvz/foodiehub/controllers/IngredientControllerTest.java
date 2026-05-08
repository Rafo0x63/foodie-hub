package hr.tvz.foodiehub.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.foodiehub.model.requests.CreateIngredientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class IngredientControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void getAllIngredients_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/api/recipes/1/ingredients"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAllIngredients_withAuthentication_returns200() throws Exception {
        mockMvc.perform(get("/api/recipes/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deleteIngredient_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(delete("/api/recipes/1/ingredients/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createIngredient_withoutAuthentication_returns401() throws Exception {

        CreateIngredientRequest request =
                new CreateIngredientRequest(
                        "Sugar",
                        "2 tbsp",
                        1L
                );

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createIngredient_withAuthentication_returns201() throws Exception {

        CreateIngredientRequest request =
                new CreateIngredientRequest(
                        "Sugar",
                        "2 tbsp",
                        1L
                );

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void createIngredient_withInvalidName_returns400() throws Exception {

        CreateIngredientRequest request =
                new CreateIngredientRequest(
                        "a",
                        "2 tbsp",
                        1L
                );

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createIngredient_withNullRecipeId_returns400() throws Exception {

        CreateIngredientRequest request =
                new CreateIngredientRequest(
                        "Sugar",
                        "2 tbsp",
                        null
                );

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateIngredient_withoutAuthentication_returns401() throws Exception {

        CreateIngredientRequest request =
                new CreateIngredientRequest(
                        "Salt",
                        "1 tsp",
                        1L
                );

        mockMvc.perform(put("/api/recipes/1/ingredients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void updateIngredient_withInvalidAmount_returns400() throws Exception {

        CreateIngredientRequest request =
                new CreateIngredientRequest(
                        "Salt",
                        "",
                        1L
                );

        mockMvc.perform(put("/api/recipes/1/ingredients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
