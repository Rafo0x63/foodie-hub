package hr.tvz.foodiehub.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StepDTO {
    private Long id;
    private int stepNumber;
    private String title;
    private String description;
    private int time;
    private Long recipeId;
}
