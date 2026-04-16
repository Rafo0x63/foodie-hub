package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.Tag;
import hr.tvz.foodiehub.entities.RecipeTag;
import hr.tvz.foodiehub.model.RecipeTagId;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.RecipeTagRepository;
import hr.tvz.foodiehub.repositories.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Configuration
public class RecipeTagSeeder {
    private final Random random = new Random();
    @Bean
    CommandLineRunner seedRecipeTags(RecipeRepository recipeRepository, TagRepository tagRepository, RecipeTagRepository recipeTagRepository) {
        return args -> {
            if(recipeTagRepository.findAll().isEmpty()) {
                List<Recipe> recipes = recipeRepository.findAll();
                List<Tag> tags = tagRepository.findAll();

                for (Recipe recipe : recipes) {

                    int numberOfTags = 1 + random.nextInt(3); // 1–3 tags per recipe

                    Collections.shuffle(tags);

                    for (int i = 0; i < numberOfTags; i++) {

                        Tag tag = tags.get(i);

                        RecipeTag rt = new RecipeTag();
                        rt.setRecipe(recipe);
                        rt.setTag(tag);
                        rt.setId(new RecipeTagId(recipe.getId(), tag.getId()));

                        recipeTagRepository.save(rt);
                    }
                }
            }
        };
    }
}
