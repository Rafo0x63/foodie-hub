package hr.tvz.foodiehub.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private UserDTO user;
}
