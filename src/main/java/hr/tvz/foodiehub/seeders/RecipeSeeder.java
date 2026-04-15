package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecipeSeeder {

    @Bean
    CommandLineRunner seedRecipes(RecipeRepository recipeRepository) {
        return args -> {
            if (recipeRepository.findByDeletedAtIsNull().isEmpty()) {
                Recipe recipe1 = new Recipe();
                recipe1.setTitle("Carbonara");
                recipe1.setDescription("Classic pasta with eggs, cheese and pancetta.");
                recipe1.setImage("carbonara.jpg");
                recipe1.setCategory("Pasta");

                Recipe recipe2 = new Recipe();
                recipe2.setTitle("Margherita Pizza");
                recipe2.setDescription("Simple pizza with tomato sauce, mozzarella and basil.");
                recipe2.setImage("margherita.jpg");
                recipe2.setCategory("Pizza");

                Recipe recipe3 = new Recipe();
                recipe3.setTitle("Caesar Salad");
                recipe3.setDescription("Fresh salad with chicken, croutons and Caesar dressing.");
                recipe3.setImage("caesar.jpg");
                recipe3.setCategory("Salad");

                recipeRepository.save(recipe1);
                recipeRepository.save(recipe2);
                recipeRepository.save(recipe3);
            }
        };
    }
}