package hr.tvz.foodiehub.repository;

import hr.tvz.foodiehub.entities.Ingredient;
import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.repositories.IngredientRepository;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("findByRecipe_IdAndDeletedAtIsNull vraca samo neobrisane sastojke za recept")
    void shouldFindByRecipeIdAndDeletedAtIsNull() {
        Recipe recipe = recipeRepository.saveAndFlush(createRecipe("Recipe With Ingredients"));
        Recipe otherRecipe = recipeRepository.saveAndFlush(createRecipe("Other Recipe"));

        Ingredient activeIngredient = createIngredient("Flour", "200 g", recipe, null);
        Ingredient deletedIngredient = createIngredient("Sugar", "50 g", recipe, LocalDateTime.now().minusDays(1));
        Ingredient otherRecipeIngredient = createIngredient("Salt", "1 tsp", otherRecipe, null);

        ingredientRepository.saveAllAndFlush(List.of(activeIngredient, deletedIngredient, otherRecipeIngredient));

        List<Ingredient> ingredients = ingredientRepository.findByRecipe_IdAndDeletedAtIsNull(recipe.getId());

        assertThat(ingredients)
                .extracting(Ingredient::getId)
                .contains(activeIngredient.getId())
                .doesNotContain(deletedIngredient.getId(), otherRecipeIngredient.getId());

        assertThat(ingredients)
                .allMatch(ingredient -> ingredient.getRecipe().getId().equals(recipe.getId()))
                .allMatch(ingredient -> ingredient.getDeletedAt() == null);
    }

    @Test
    @DisplayName("findByRecipe_IdAndIdAndDeletedAtIsNull vraca sastojak kada pripada receptu i nije obrisan")
    void shouldFindByRecipeIdAndIdAndDeletedAtIsNull() {
        Recipe recipe = recipeRepository.saveAndFlush(createRecipe("Recipe For Single Ingredient"));
        Recipe otherRecipe = recipeRepository.saveAndFlush(createRecipe("Other Single Recipe"));

        Ingredient activeIngredient = ingredientRepository.saveAndFlush(createIngredient("Milk", "250 ml", recipe, null));
        Ingredient deletedIngredient = ingredientRepository.saveAndFlush(createIngredient("Butter", "30 g", recipe, LocalDateTime.now().minusDays(1)));
        Ingredient otherRecipeIngredient = ingredientRepository.saveAndFlush(createIngredient("Pepper", "1 pinch", otherRecipe, null));

        Optional<Ingredient> activeResult = ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(
                recipe.getId(),
                activeIngredient.getId()
        );
        Optional<Ingredient> deletedResult = ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(
                recipe.getId(),
                deletedIngredient.getId()
        );
        Optional<Ingredient> wrongRecipeResult = ingredientRepository.findByRecipe_IdAndIdAndDeletedAtIsNull(
                recipe.getId(),
                otherRecipeIngredient.getId()
        );

        assertThat(activeResult).isPresent();
        assertThat(activeResult.get().getName()).isEqualTo(activeIngredient.getName());
        assertThat(deletedResult).isEmpty();
        assertThat(wrongRecipeResult).isEmpty();
    }

    private Ingredient createIngredient(String name, String amount, Recipe recipe, LocalDateTime deletedAt) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setAmount(amount);
        ingredient.setRecipe(recipe);
        ingredient.setDeletedAt(deletedAt);

        return ingredient;
    }

    private Recipe createRecipe(String title) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title + " " + System.nanoTime());
        recipe.setDescription("Test recipe description");
        recipe.setImage("test-image.jpg");
        recipe.setCategory("Test category");
        recipe.setTime(30);
        recipe.setDeletedAt(null);

        return recipe;
    }
}
