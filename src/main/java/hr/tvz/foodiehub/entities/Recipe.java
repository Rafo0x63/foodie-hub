package hr.tvz.foodiehub.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String image;
    private String category;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "chef_id")
    private Chef chef;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @OrderBy("stepNumber ASC")
    private List<Step> steps;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeTag> recipeTags;
}
