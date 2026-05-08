package hr.tvz.foodiehub.repository;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.RecipeTag;
import hr.tvz.foodiehub.entities.Tag;
import hr.tvz.foodiehub.model.RecipeTagId;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.RecipeTagRepository;
import hr.tvz.foodiehub.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RecipeRepositoryTest {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RecipeTagRepository recipeTagRepository;

    @Test
    void shouldFindByIdAndDeletedAtIsNull() {

        Recipe active = recipeRepository.saveAndFlush(
                createRecipe("Active Recipe", null)
        );

        Recipe deleted = recipeRepository.saveAndFlush(
                createRecipe("Deleted Recipe", LocalDateTime.now())
        );

        Optional<Recipe> activeResult =
                recipeRepository.findByIdAndDeletedAtIsNull(active.getId());

        Optional<Recipe> deletedResult =
                recipeRepository.findByIdAndDeletedAtIsNull(deleted.getId());

        assertThat(activeResult).isPresent();
        assertThat(activeResult.get().getTitle()).isEqualTo("Active Recipe");

        assertThat(deletedResult).isEmpty();
    }

    @Test
    void shouldFindByDeletedAtIsNull() {

        List<Recipe> result1 = recipeRepository.findByDeletedAtIsNull();

        recipeRepository.saveAndFlush(createRecipe("Test recipe 1", null));
        recipeRepository.saveAndFlush(createRecipe("Test recipe 2", LocalDateTime.now()));

        List<Recipe> result2 = recipeRepository.findByDeletedAtIsNull();

        assertThat(result1.size()).isEqualTo(result2.size() - 1);
    }

    @Test
    void shouldFindByTagName() {

        Recipe recipe = recipeRepository.saveAndFlush(
                createRecipe("Salad", null)
        );

        Tag tag = tagRepository.saveAndFlush(
                createTag("Healthy")
        );

        recipeTagRepository.saveAndFlush(
                createRecipeTag(recipe, tag)
        );

        List<Recipe> result = recipeRepository.findByTagName("Healthy");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(recipe.getId());
    }

    private Recipe createRecipe(String title, LocalDateTime deletedAt) {
        Recipe recipe = new Recipe();
        recipe.setTitle(title);
        recipe.setDescription("desc");
        recipe.setCategory("general");
        recipe.setTime(30);
        recipe.setImage("img.png");
        recipe.setDeletedAt(deletedAt);
        return recipe;
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }

    private RecipeTag createRecipeTag(Recipe recipe, Tag tag) {
        RecipeTag rt = new RecipeTag();
        rt.setRecipe(recipe);
        rt.setTag(tag);
        rt.setId(new RecipeTagId(1L, 1L));
        return rt;
    }
}
