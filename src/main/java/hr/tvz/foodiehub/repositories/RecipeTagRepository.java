package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.RecipeTag;
import hr.tvz.foodiehub.model.RecipeTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeTagRepository extends JpaRepository<RecipeTag, RecipeTagId> {
    List<RecipeTag> findByRecipeId(Long recipeId);
    List<RecipeTag> findByTagId(Long tagId);
}
