package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.model.RecipeDTO;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.services.interfaces.RecipeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public RecipeDTO getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        return mapToDTO(recipe);
    }

    @Override
    public void deleteRecipeById(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new RuntimeException("Recipe not found with id: " + id);
        }

        recipeRepository.deleteById(id);
    }

    private RecipeDTO mapToDTO(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getCategory()
        );
    }
}
