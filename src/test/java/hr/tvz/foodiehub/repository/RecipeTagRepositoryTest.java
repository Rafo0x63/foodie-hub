package hr.tvz.foodiehub.repository;

import hr.tvz.foodiehub.entities.RecipeTag;
import hr.tvz.foodiehub.repositories.RecipeTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecipeTagRepositoryTest {

    @Autowired
    private RecipeTagRepository recipeTagRepository;

    @Test
    void shouldFindRecipesByTagId() {
        RecipeTag existingRecipeTag = getExistingRecipeTag();

        Long tagId = existingRecipeTag.getTag().getId();

        List<RecipeTag> list = recipeTagRepository.findByTagId(tagId);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldFindRecipeTagsByRecipeId() {
        RecipeTag existingRecipeTag = getExistingRecipeTag();

        Long recipeId = existingRecipeTag.getRecipe().getId();

        List<RecipeTag> list = recipeTagRepository.findByRecipeId(recipeId);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldContainRecipeAndTag() {
        RecipeTag recipeTag = getExistingRecipeTag();

        assertNotNull(recipeTag.getRecipe());
        assertNotNull(recipeTag.getTag());
    }

    private RecipeTag getExistingRecipeTag() {
        List<RecipeTag> allRecipeTags = recipeTagRepository.findAll();

        assertNotNull(allRecipeTags);
        assertFalse(allRecipeTags.isEmpty(), "Database should contain at least one recipe_tag row");

        RecipeTag recipeTag = allRecipeTags.getFirst();

        assertNotNull(recipeTag.getRecipe());
        assertNotNull(recipeTag.getTag());

        return recipeTag;
    }
}