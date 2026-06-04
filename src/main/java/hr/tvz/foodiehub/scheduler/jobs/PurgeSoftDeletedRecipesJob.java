package hr.tvz.foodiehub.scheduler.jobs;

import hr.tvz.foodiehub.services.interfaces.RecipeService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PurgeSoftDeletedRecipesJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(PurgeSoftDeletedRecipesJob.class);

    private final RecipeService recipeService;

    public PurgeSoftDeletedRecipesJob(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = context.getMergedJobDataMap();

            int retentionDays = jobDataMap.getInt("retentionDays");
            int batchSize = jobDataMap.getInt("batchSize");
            boolean dryRun = jobDataMap.getBoolean("dryRun");
            int runCount = jobDataMap.getInt("runCount");

            if (retentionDays < 0) {
                throw new JobExecutionException("retentionDays must not be negative");
            }

            if (batchSize <= 0) {
                throw new JobExecutionException("batchSize must be greater than zero");
            }

            runCount++;
            jobDataMap.put("runCount", runCount);

            log.info("Starting soft-deleted recipe purge. runCount={}, retentionDays={}, batchSize={}, dryRun={}",
                    runCount, retentionDays, batchSize, dryRun);

            int purgedRecipeBatchSize = recipeService.purgeSoftDeletedRecipesOlderThan(retentionDays, batchSize, dryRun);

            log.info("Finished soft-deleted recipe purge. processedRecipes={}, dryRun={}", purgedRecipeBatchSize, dryRun);
        } catch (JobExecutionException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Soft-deleted recipe purge failed", ex);
            throw new JobExecutionException(ex);
        }

    }
}
