package hr.tvz.foodiehub.report;

import hr.tvz.foodiehub.model.dtos.NewRecipesReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingRecipeReportPublisher implements RecipeReportPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingRecipeReportPublisher.class);

    @Override
    public void publish(NewRecipesReportDTO report) {
        log.info("New-recipes report [{} - {}]: {} new recipe(s). Breakdown by category: {}. Titles: {}",
                report.getFrom(),
                report.getTo(),
                report.getTotalNewRecipes(),
                report.getCountByCategory(),
                report.getTitles());
    }
}
