package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.model.dtos.NewRecipesReportDTO;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.services.interfaces.RecipeReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RecipeReportServiceImpl implements RecipeReportService {

    private static final String UNCATEGORIZED = "(uncategorized)";

    private final RecipeRepository recipeRepository;
    private final Clock clock;

    public RecipeReportServiceImpl(RecipeRepository recipeRepository, Clock clock) {
        this.recipeRepository = recipeRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public NewRecipesReportDTO generateNewRecipesReport(int lookbackHours) {
        LocalDateTime to = LocalDateTime.now(clock);
        LocalDateTime from = to.minusHours(lookbackHours);

        List<Recipe> newRecipes = recipeRepository.findByCreatedAtAfterAndDeletedAtIsNull(from);

        Map<String, Long> countByCategory = newRecipes.stream()
                .collect(Collectors.groupingBy(
                        recipe -> recipe.getCategory() == null ? UNCATEGORIZED : recipe.getCategory(),
                        TreeMap::new,
                        Collectors.counting()));

        List<String> titles = newRecipes.stream()
                .map(Recipe::getTitle)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        return new NewRecipesReportDTO(from, to, newRecipes.size(), countByCategory, titles);
    }
}
