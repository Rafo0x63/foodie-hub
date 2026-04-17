package hr.tvz.foodiehub.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "step_number")
    private int stepNumber;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer time;
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
