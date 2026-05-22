package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Ingredient;
import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.model.dtos.IngredientDTO;
import hr.tvz.foodiehub.model.requests.CreateIngredientRequest;
import hr.tvz.foodiehub.repositories.IngredientRepository;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.services.implementations.IngredientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngredientService - unit tests")
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    private Recipe recipe;
    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pizza");

        ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Cheese");
        ingredient.setAmount("200g");
        ingredient.setRecipe(recipe);
        ingredient.setDeletedAt(null);
    }

    @Test
    @DisplayName("getAllRecipeIngredients returns mapped DTOs")
    void getAllRecipeIngredients_returnsList() {
        when(ingredientRepository.findByRecipe_IdAndDeletedAtIsNull(1L))
                .thenReturn(List.of(ingredient));

        List<IngredientDTO> result = ingredientService.getAllRecipeIngredients(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Cheese");
    }

    @Test
    @DisplayName("createNewIngredient persists ingredient with recipe and returns DTO")
    void createNewIngredient_success() {
        CreateIngredientRequest request = new CreateIngredientRequest("Tomato", "100g", 1L);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenAnswer(invocation -> {
                    Ingredient i = invocation.getArgument(0);
                    i.setId(5L);
                    return i;
                });

        IngredientDTO result = ingredientService.createNewIngredient(request);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Tomato");
        assertThat(result.getAmount()).isEqualTo("100g");
        assertThat(result.getRecipeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createNewIngredient throws when recipe is missing")
    void createNewIngredient_throwsWhenRecipeMissing() {
        CreateIngredientRequest request = new CreateIngredientRequest("Tomato", "100g", 99L);

        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.createNewIngredient(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Recipe not found");
    }

    @Test
    @DisplayName("updateIngredient updates fields and returns DTO")
    void updateIngredient_success() {
        CreateIngredientRequest request = new CreateIngredientRequest("Mozzarella", "300g", 1L);

        when(ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(1L, 1L))
                .thenReturn(Optional.of(ingredient));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        IngredientDTO result = ingredientService.updateIngredient(1L, 1L, request);

        assertThat(result.getName()).isEqualTo("Mozzarella");
        assertThat(result.getAmount()).isEqualTo("300g");
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    @DisplayName("updateIngredient throws when ingredient missing")
    void updateIngredient_throwsWhenMissing() {
        CreateIngredientRequest request = new CreateIngredientRequest("X", "1g", 1L);

        when(ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.updateIngredient(1L, 99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ingredient not found");
    }

    @Test
    @DisplayName("updateIngredient throws when recipe missing during update")
    void updateIngredient_throwsWhenRecipeMissing() {
        CreateIngredientRequest request = new CreateIngredientRequest("X", "1g", 99L);

        when(ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(1L, 1L))
                .thenReturn(Optional.of(ingredient));
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.updateIngredient(1L, 1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Recipe not found");
    }

    @Test
    @DisplayName("deleteIngredientById soft-deletes the ingredient")
    void deleteIngredientById_softDeletes() {
        when(ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(1L, 1L))
                .thenReturn(Optional.of(ingredient));

        ingredientService.deleteIngredientById(1L, 1L);

        assertThat(ingredient.getDeletedAt()).isNotNull();
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    @DisplayName("deleteIngredientById throws when ingredient missing")
    void deleteIngredientById_throwsWhenMissing() {
        when(ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.deleteIngredientById(1L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ingredient in recipe not found");
    }
}
