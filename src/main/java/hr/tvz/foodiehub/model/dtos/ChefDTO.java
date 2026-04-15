package hr.tvz.foodiehub.model.dtos;

import hr.tvz.foodiehub.entities.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChefDTO {
    private Long id;
    private User user;
    private String username;
    private String bio;
}
