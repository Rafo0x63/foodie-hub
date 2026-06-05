package hr.tvz.foodiehub.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewRecipesReportDTO {
    private LocalDateTime from;
    private LocalDateTime to;
    private long totalNewRecipes;
    private Map<String, Long> countByCategory;
    private List<String> titles;
}
