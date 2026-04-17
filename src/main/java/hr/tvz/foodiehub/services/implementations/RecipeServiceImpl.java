package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.model.requests.CreateRecipeRequest;
import hr.tvz.foodiehub.model.dtos.RecipeDTO;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.specification.RecipeSpecification;
import hr.tvz.foodiehub.services.interfaces.RecipeService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findByDeletedAtIsNull()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public RecipeDTO getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        return mapToDTO(recipe);
    }

    @Override
    public void deleteRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        recipe.setDeletedAt(LocalDateTime.now());
        recipeRepository.save(recipe);
    }

    @Override
    public RecipeDTO createNewRecipe(CreateRecipeRequest createRecipeRequest) {
        Recipe newRecipe = new Recipe();
        newRecipe.setTitle(createRecipeRequest.title());
        newRecipe.setDescription(createRecipeRequest.description());
        newRecipe.setCategory(createRecipeRequest.category());
        newRecipe.setDeletedAt(null);

        Recipe recipe = recipeRepository.save(newRecipe);
        return mapToDTO(recipe);
    }

    @Override
    public RecipeDTO updateRecipe(Long id, CreateRecipeRequest createRecipeRequest) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        recipe.setTitle(createRecipeRequest.title());
        recipe.setDescription(createRecipeRequest.description());
        recipe.setCategory(createRecipeRequest.category());

        return mapToDTO(recipeRepository.save(recipe));
    }

    @Override
    public List<RecipeDTO> search(String title, Integer maxTime, List<String> tags, String category) {
        Specification<Recipe> spec = Specification
                .where(RecipeSpecification.notDeleted())
                .and(RecipeSpecification.hasTitle(title))
                .and(RecipeSpecification.maxTime(maxTime))
                .and(RecipeSpecification.hasTags(tags))
                .and(RecipeSpecification.hasCategory(category));

        return recipeRepository.findAll(spec)
                .stream()
                .map(this::mapToDTO)
                .toList();
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
