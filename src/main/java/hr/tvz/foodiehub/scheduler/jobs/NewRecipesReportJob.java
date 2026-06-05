package hr.tvz.foodiehub.scheduler.jobs;

import hr.tvz.foodiehub.model.dtos.NewRecipesReportDTO;
import hr.tvz.foodiehub.report.RecipeReportPublisher;
import hr.tvz.foodiehub.services.interfaces.RecipeReportService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NewRecipesReportJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(NewRecipesReportJob.class);

    private final RecipeReportService recipeReportService;
    private final List<RecipeReportPublisher> publishers;

    public NewRecipesReportJob(RecipeReportService recipeReportService, List<RecipeReportPublisher> publishers) {
        this.recipeReportService = recipeReportService;
        this.publishers = publishers;
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

            publishers.forEach(publisher -> publisher.publish(report));
        } catch (JobExecutionException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("New-recipes report failed", ex);
            throw new JobExecutionException(ex);
        }
    }
}
