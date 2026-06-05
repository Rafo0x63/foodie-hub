package hr.tvz.foodiehub.config;

import hr.tvz.foodiehub.scheduler.jobs.NewRecipesReportJob;
import hr.tvz.foodiehub.scheduler.jobs.PurgeSoftDeletedRecipesJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class QuartzJobsConfig {
    private static final String RECIPE_MAINTENANCE_GROUP = "recipe-maintenance";
    private static final String PURGE_SOFT_DELETED_RECIPES_JOB = "purge-soft-deleted-recipes-job";
    private static final String PURGE_SOFT_DELETED_RECIPES_TRIGGER = "purge-soft-deleted-recipes-trigger";

    private static final String RECIPE_REPORTING_GROUP = "recipe-reporting";
    private static final String NEW_RECIPES_REPORT_JOB = "new-recipes-report-job";
    private static final String NEW_RECIPES_REPORT_TRIGGER = "new-recipes-report-trigger";

    @Bean
    public JobDetail purgeSoftDeletedRecipesJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("retentionDays", 0);
        jobDataMap.put("batchSize", 50);
        jobDataMap.put("dryRun", false);
        jobDataMap.put("runCount", 0);

        return newJob(PurgeSoftDeletedRecipesJob.class)
                .withIdentity(PURGE_SOFT_DELETED_RECIPES_JOB, RECIPE_MAINTENANCE_GROUP)
                .withDescription("Purges recipes that were soft-deleted before the configured retention period.")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger purgeSoftDeletedRecipesTrigger(JobDetail purgeSoftDeletedRecipesJobDetail) {
        return newTrigger()
                .forJob(purgeSoftDeletedRecipesJobDetail)
                .withIdentity(PURGE_SOFT_DELETED_RECIPES_TRIGGER, RECIPE_MAINTENANCE_GROUP)
                .withDescription("Runs the soft-deleted recipe purge job on a fixed demo interval.")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever()
                        .withMisfireHandlingInstructionNextWithExistingCount())
                .build();
    }

    @Bean
    public JobDetail newRecipesReportJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("lookbackHours", 24);
        jobDataMap.put("runCount", 0);

        return newJob(NewRecipesReportJob.class)
                .withIdentity(NEW_RECIPES_REPORT_JOB, RECIPE_REPORTING_GROUP)
                .withDescription("Generates a daily report of recipes created within the configured look-back window.")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger newRecipesReportTrigger(JobDetail newRecipesReportJobDetail) {
        return newTrigger()
                .forJob(newRecipesReportJobDetail)
                .withIdentity(NEW_RECIPES_REPORT_TRIGGER, RECIPE_REPORTING_GROUP)
                .withDescription("Fires the new-recipes report every day at 23:00.")
                .withSchedule(cronSchedule("0 55 8 * * ?") // TEMP: prezentacija 08:55, vratiti na "0 0 23 * * ?"
                        .inTimeZone(TimeZone.getDefault())
                        .withMisfireHandlingInstructionDoNothing())
                .build();
    }
}
