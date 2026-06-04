package hr.tvz.foodiehub.config;

import hr.tvz.foodiehub.scheduler.jobs.PurgeSoftDeletedRecipesJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class QuartzJobsConfig {
    private static final String RECIPE_MAINTENANCE_GROUP = "recipe-maintenance";
    private static final String PURGE_SOFT_DELETED_RECIPES_JOB = "purge-soft-deleted-recipes-job";
    private static final String PURGE_SOFT_DELETED_RECIPES_TRIGGER = "purge-soft-deleted-recipes-trigger";

    @Bean
    public JobDetail purgeSoftDeletedRecipesJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("retentionDays", 0);
        jobDataMap.put("batchSize", 50);
        jobDataMap.put("dryRun", true);
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
}
