package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
