package hr.tvz.foodiehub.repository;

import hr.tvz.foodiehub.entities.RecipeTag;
import hr.tvz.foodiehub.repositories.RecipeTagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RecipeTagRepositoryTest {

    @Autowired
    private RecipeTagRepository recipeTagRepository;

    @Test
    void shouldFindRecipesByTagId() {
        Long tagId = 1L;

        List<RecipeTag> list =
                recipeTagRepository.findByTagId(tagId);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldFindEnrollmentsByCourseId() {
        Long recipeId = 1L;

        List<RecipeTag> list =
                recipeTagRepository.findByRecipeId(recipeId);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void shouldContainRecipeAndTag() {
        RecipeTag recipeTag = recipeTagRepository.findAll().get(0);

        assertNotNull(recipeTag.getRecipe());
        assertNotNull(recipeTag.getTag());
    }
}
