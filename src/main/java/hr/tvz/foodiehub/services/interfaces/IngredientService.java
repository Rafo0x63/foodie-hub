package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.IngredientDTO;
import hr.tvz.foodiehub.model.requests.CreateIngredientRequest;
import java.util.List;

public interface IngredientService {

    List<IngredientDTO> getAllIngredients();
    IngredientDTO getIngredientById(Long id);
    void deleteIngredientById(Long id);
    IngredientDTO createNewIngredient(CreateIngredientRequest createIngredientRequest);
    IngredientDTO updateIngredient(Long id, CreateIngredientRequest createIngredientRequest);
    List<IngredientDTO> getAllRecipeIngredients(Long recipeId);
}
