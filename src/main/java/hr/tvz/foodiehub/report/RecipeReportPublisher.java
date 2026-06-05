package hr.tvz.foodiehub.report;

import hr.tvz.foodiehub.model.dtos.NewRecipesReportDTO;

/**
 * Strategy for delivering a generated recipe report to some destination
 * (log, database, e-mail, ...). New destinations are added by providing a new
 * {@code @Component} implementation — the report job does not change (OCP).
 */
public interface RecipeReportPublisher {
    void publish(NewRecipesReportDTO report);
}
