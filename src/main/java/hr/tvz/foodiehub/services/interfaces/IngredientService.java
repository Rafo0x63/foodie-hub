package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.IngredientDTO;
import hr.tvz.foodiehub.model.requests.CreateIngredientRequest;
import java.util.List;

public interface IngredientService {

    void deleteIngredientById(Long recipeId, Long ingredientId);
    IngredientDTO createNewIngredient(CreateIngredientRequest createIngredientRequest);
    IngredientDTO updateIngredient(Long recipeId, Long ingredientId, CreateIngredientRequest createIngredientRequest);
    List<IngredientDTO> getAllRecipeIngredients(Long recipeId);
}
