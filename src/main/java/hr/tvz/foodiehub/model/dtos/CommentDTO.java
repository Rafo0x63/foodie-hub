package hr.tvz.foodiehub.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO{
    private Long id;
    private String text;
    private int rating;
    private LocalDateTime createdAt;
    private Long recipeId;
    private Long userId;
    private String username;
}
