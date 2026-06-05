package hr.tvz.foodiehub.services.interfaces;

public interface RecipeMaintenanceService {
    int purgeSoftDeletedRecipesOlderThan(int retentionDays, int batchSize, boolean dryRun);
}
