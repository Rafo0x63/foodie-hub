package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.services.interfaces.RecipeMaintenanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeMaintenanceServiceImpl implements RecipeMaintenanceService {

    private final RecipeRepository recipeRepository;
    private final Clock clock;

    public RecipeMaintenanceServiceImpl(RecipeRepository recipeRepository, Clock clock) {
        this.recipeRepository = recipeRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public int purgeSoftDeletedRecipesOlderThan(int retentionDays, int batchSize, boolean dryRun) {
        LocalDateTime cutoff = LocalDateTime.now(clock).minusDays(retentionDays);
        List<Recipe> softDeletedRecipes = recipeRepository.findByDeletedAtBefore(cutoff);
        List<Recipe> recipeBatch = softDeletedRecipes.stream()
                .limit(batchSize)
                .toList();

        if (!dryRun) {
            recipeRepository.deleteAll(recipeBatch);
        }

        return recipeBatch.size();
    }
}
