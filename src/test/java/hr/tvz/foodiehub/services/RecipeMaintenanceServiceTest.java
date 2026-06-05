package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.services.implementations.RecipeMaintenanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeMaintenanceService - unit tests")
class RecipeMaintenanceServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-06-05T12:00:00Z"),
            ZoneId.of("UTC")
    );

    @Mock
    private RecipeRepository recipeRepository;

    private RecipeMaintenanceServiceImpl recipeMaintenanceService;

    @BeforeEach
    void setUp() {
        recipeMaintenanceService = new RecipeMaintenanceServiceImpl(recipeRepository, FIXED_CLOCK);
    }

    @Test
    @DisplayName("purgeSoftDeletedRecipesOlderThan uses retention cutoff")
    void purgeSoftDeletedRecipesOlderThan_usesRetentionCutoff() {
        when(recipeRepository.findByDeletedAtBefore(org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of());

        recipeMaintenanceService.purgeSoftDeletedRecipesOlderThan(7, 50, true);

        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(recipeRepository).findByDeletedAtBefore(cutoffCaptor.capture());

        assertThat(cutoffCaptor.getValue())
                .isEqualTo(LocalDateTime.of(2026, 5, 29, 12, 0));
    }

    @Test
    @DisplayName("purgeSoftDeletedRecipesOlderThan limits deleted recipes by batch size")
    void purgeSoftDeletedRecipesOlderThan_limitsBatchSize() {
        Recipe first = recipe(1L);
        Recipe second = recipe(2L);
        Recipe third = recipe(3L);

        when(recipeRepository.findByDeletedAtBefore(org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(first, second, third));

        int result = recipeMaintenanceService.purgeSoftDeletedRecipesOlderThan(0, 2, false);

        assertThat(result).isEqualTo(2);
        verify(recipeRepository).deleteAll(List.of(first, second));
    }

    @Test
    @DisplayName("purgeSoftDeletedRecipesOlderThan does not delete recipes in dry run")
    void purgeSoftDeletedRecipesOlderThan_dryRunDoesNotDelete() {
        Recipe first = recipe(1L);
        Recipe second = recipe(2L);

        when(recipeRepository.findByDeletedAtBefore(org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(first, second));

        int result = recipeMaintenanceService.purgeSoftDeletedRecipesOlderThan(0, 10, true);

        assertThat(result).isEqualTo(2);
        verify(recipeRepository, never()).deleteAll(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("purgeSoftDeletedRecipesOlderThan deletes selected batch when dry run is false")
    void purgeSoftDeletedRecipesOlderThan_deletesWhenDryRunIsFalse() {
        Recipe first = recipe(1L);
        Recipe second = recipe(2L);

        when(recipeRepository.findByDeletedAtBefore(org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(first, second));

        int result = recipeMaintenanceService.purgeSoftDeletedRecipesOlderThan(0, 10, false);

        assertThat(result).isEqualTo(2);
        verify(recipeRepository).deleteAll(List.of(first, second));
    }

    private Recipe recipe(Long id) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        return recipe;
    }
}
