package hr.tvz.foodiehub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
}
