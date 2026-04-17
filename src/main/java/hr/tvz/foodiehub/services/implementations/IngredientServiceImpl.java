package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Ingredient;
import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.model.dtos.IngredientDTO;
import hr.tvz.foodiehub.model.requests.CreateIngredientRequest;
import hr.tvz.foodiehub.repositories.IngredientRepository;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.services.interfaces.IngredientService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, RecipeRepository recipeRepository){
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void deleteIngredientById(Long recipeId, Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(recipeId, ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient in recipe not found with id: " + ingredientId));

        ingredient.setDeletedAt(LocalDateTime.now());
        ingredientRepository.save(ingredient);
    }

    @Override
    public IngredientDTO createNewIngredient(CreateIngredientRequest createIngredientRequest) {
        Ingredient newIngredient = new Ingredient();

        newIngredient.setName(createIngredientRequest.name());
        newIngredient.setAmount(createIngredientRequest.amount());
        newIngredient.setDeletedAt(null);

        Recipe recipe = recipeRepository.findById(createIngredientRequest.recipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + createIngredientRequest.recipeId()));
        newIngredient.setRecipe(recipe);

        Ingredient ingredient = ingredientRepository.save(newIngredient);

        return mapToDTO(ingredient);
    }


    @Override
    public IngredientDTO updateIngredient(Long recipeId, Long ingredientId, CreateIngredientRequest createIngredientRequest) {
        Ingredient ingredient = ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(recipeId, ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + ingredientId));

        ingredient.setName(createIngredientRequest.name());
        ingredient.setAmount(createIngredientRequest.amount());

        Recipe recipe = recipeRepository.findById(createIngredientRequest.recipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + createIngredientRequest.recipeId()));
        ingredient.setRecipe(recipe);

        ingredientRepository.save(ingredient);

        return mapToDTO(ingredient);
    }

    @Override
    public List<IngredientDTO> getAllRecipeIngredients(Long recipeId) {
        return ingredientRepository.findByRecipe_IdAndDeletedAtIsNull(recipeId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private IngredientDTO mapToDTO(Ingredient ingredient) {
        return new IngredientDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getAmount(),
                ingredient.getRecipe().getId()
        );
    }
}
