package hr.tvz.foodiehub.services.interfaces;
import hr.tvz.foodiehub.model.RecipeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RecipeService {
    List<RecipeDTO> getAllRecipes();
    RecipeDTO getRecipeById(Long id);
    void deleteRecipeById(Long id);
}
