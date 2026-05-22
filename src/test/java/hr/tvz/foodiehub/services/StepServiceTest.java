package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.Step;
import hr.tvz.foodiehub.model.dtos.StepDTO;
import hr.tvz.foodiehub.model.requests.CreateStepRequest;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.StepRepository;
import hr.tvz.foodiehub.services.implementations.StepServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StepService - unit tests")
class StepServiceTest {

    @Mock
    private StepRepository stepRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private StepServiceImpl stepService;

    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pizza");
    }

    @Test
    @DisplayName("getAllStepsByRecipeId returns an empty list (stub)")
    void getAllStepsByRecipeId_returnsEmptyList() {
        List<StepDTO> result = stepService.getAllStepsByRecipeId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createStepForRecipe saves a new step and returns its DTO")
    void createStepForRecipe_savesAndReturnsDto() {
        CreateStepRequest request = new CreateStepRequest(
                1, "Mix", "Mix everything in a bowl", 5
        );

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(stepRepository.save(org.mockito.ArgumentMatchers.any(Step.class)))
                .thenAnswer(invocation -> {
                    Step s = invocation.getArgument(0);
                    s.setId(42L);
                    return s;
                });

        StepDTO result = stepService.createStepForRecipe(1L, request);

        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getStepNumber()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Mix");
        assertThat(result.getDescription()).isEqualTo("Mix everything in a bowl");
        assertThat(result.getTime()).isEqualTo(5);
        assertThat(result.getRecipeId()).isEqualTo(1L);

        verify(stepRepository).save(org.mockito.ArgumentMatchers.any(Step.class));
    }

    @Test
    @DisplayName("createStepForRecipe throws when recipe does not exist")
    void createStepForRecipe_throwsWhenRecipeMissing() {
        CreateStepRequest request = new CreateStepRequest(
                1, "Mix", "Mix everything", 5
        );

        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stepService.createStepForRecipe(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Recipe not found");
    }
}
