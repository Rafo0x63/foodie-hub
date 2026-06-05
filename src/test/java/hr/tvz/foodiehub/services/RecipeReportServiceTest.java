package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.model.dtos.NewRecipesReportDTO;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.services.implementations.RecipeReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeReportService - unit tests")
class RecipeReportServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-06-05T23:00:00Z"),
            ZoneId.of("UTC")
    );

    @Mock
    private RecipeRepository recipeRepository;

    private RecipeReportServiceImpl recipeReportService;

    @BeforeEach
    void setUp() {
        recipeReportService = new RecipeReportServiceImpl(recipeRepository, FIXED_CLOCK);
    }

    @Test
    @DisplayName("generateNewRecipesReport uses the look-back window as the createdAt cutoff")
    void generateNewRecipesReport_usesLookbackCutoff() {
        when(recipeRepository.findByCreatedAtAfterAndDeletedAtIsNull(ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of());

        NewRecipesReportDTO report = recipeReportService.generateNewRecipesReport(24);

        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(recipeRepository).findByCreatedAtAfterAndDeletedAtIsNull(cutoffCaptor.capture());

        assertThat(cutoffCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 6, 4, 23, 0));
        assertThat(report.getFrom()).isEqualTo(LocalDateTime.of(2026, 6, 4, 23, 0));
        assertThat(report.getTo()).isEqualTo(LocalDateTime.of(2026, 6, 5, 23, 0));
        assertThat(report.getTotalNewRecipes()).isZero();
    }

    @Test
    @DisplayName("generateNewRecipesReport aggregates count by category and collects titles")
    void generateNewRecipesReport_aggregatesByCategory() {
        when(recipeRepository.findByCreatedAtAfterAndDeletedAtIsNull(ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(
                        recipe("Carbonara", "Pasta"),
                        recipe("Lasagne", "Pasta"),
                        recipe("Tiramisu", "Dessert")));

        NewRecipesReportDTO report = recipeReportService.generateNewRecipesReport(24);

        assertThat(report.getTotalNewRecipes()).isEqualTo(3);
        assertThat(report.getCountByCategory())
                .containsEntry("Pasta", 2L)
                .containsEntry("Dessert", 1L);
        assertThat(report.getTitles()).containsExactly("Carbonara", "Lasagne", "Tiramisu");
    }

    @Test
    @DisplayName("generateNewRecipesReport groups recipes without a category under a placeholder")
    void generateNewRecipesReport_handlesNullCategory() {
        when(recipeRepository.findByCreatedAtAfterAndDeletedAtIsNull(ArgumentMatchers.any(LocalDateTime.class)))
                .thenReturn(List.of(recipe("Mystery dish", null)));

        NewRecipesReportDTO report = recipeReportService.generateNewRecipesReport(24);

        assertThat(report.getCountByCategory()).containsEntry("(uncategorized)", 1L);
    }

    private Recipe recipe(String title, String category) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setCategory(category);
        return recipe;
    }
}
