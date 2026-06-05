package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.NewRecipesReportDTO;

public interface RecipeReportService {
    NewRecipesReportDTO generateNewRecipesReport(int lookbackHours);
}
