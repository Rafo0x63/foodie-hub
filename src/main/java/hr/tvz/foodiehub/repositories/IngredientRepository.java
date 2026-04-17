package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByRecipe_IdAndDeletedAtIsNull(Long id);
    Optional<Ingredient> findByRecipe_IdAndIdAndDeletedAtIsNull(Long recipeId, Long ingredientId);
}
