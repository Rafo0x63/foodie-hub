package hr.tvz.foodiehub.services.interfaces;
import hr.tvz.foodiehub.model.requests.CreateRecipeRequest;
import hr.tvz.foodiehub.model.dtos.RecipeDTO;

import java.util.List;

public interface RecipeService {
    List<RecipeDTO> getAllRecipes();
    RecipeDTO getRecipeById(Long id);
    void deleteRecipeById(Long id);
    RecipeDTO createNewRecipe(CreateRecipeRequest createRecipeRequest);
    RecipeDTO updateRecipe(Long id, CreateRecipeRequest createRecipeRequest);
}
