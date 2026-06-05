package hr.tvz.foodiehub.scheduler.jobs;

import hr.tvz.foodiehub.model.dtos.NewRecipesReportDTO;
import hr.tvz.foodiehub.services.interfaces.RecipeReportService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NewRecipesReportJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(NewRecipesReportJob.class);

    private final RecipeReportService recipeReportService;

    public NewRecipesReportJob(RecipeReportService recipeReportService) {
        this.recipeReportService = recipeReportService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = context.getMergedJobDataMap();

            int lookbackHours = jobDataMap.getInt("lookbackHours");
            int runCount = jobDataMap.getInt("runCount");

            if (lookbackHours <= 0) {
                throw new JobExecutionException("lookbackHours must be greater than zero");
            }

            runCount++;
            jobDataMap.put("runCount", runCount);

            log.info("Starting new-recipes report. runCount={}, lookbackHours={}", runCount, lookbackHours);

            NewRecipesReportDTO report = recipeReportService.generateNewRecipesReport(lookbackHours);

            log.info("New-recipes report [{} - {}]: {} new recipe(s). Breakdown by category: {}. Titles: {}",
                    report.getFrom(),
                    report.getTo(),
                    report.getTotalNewRecipes(),
                    report.getCountByCategory(),
                    report.getTitles());
        } catch (JobExecutionException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("New-recipes report failed", ex);
            throw new JobExecutionException(ex);
        }
    }
}
