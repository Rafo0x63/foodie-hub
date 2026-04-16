package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
public class RecipeSeeder {
    private final Random random = new Random();
    @Bean
    CommandLineRunner seedRecipes(RecipeRepository recipeRepository, UserRepository userRepository) {
        return args -> {
            if (recipeRepository.findByDeletedAtIsNull().isEmpty()) {
                List<User> userList = userRepository.findByDeletedAtIsNull();
                if (userList.isEmpty()) {
                    return;
                }
                User user1 = userList.getFirst();
                User user2 = userList.size() > 1 ? userList.get(1) : userList.getFirst();
                User user3 = userList.size() > 2 ? userList.get(2) : userList.getFirst();

                Recipe recipe1 = new Recipe();
                recipe1.setTitle("Carbonara");
                recipe1.setDescription("Classic pasta with eggs, cheese and pancetta.");
                recipe1.setImage("carbonara.jpg");
                recipe1.setCategory("Pasta");
                recipe1.setDeletedAt(null);
                recipe1.setUser(user1);
                recipe1.setTime(10 + random.nextInt(90));

                Recipe recipe2 = new Recipe();
                recipe2.setTitle("Margherita Pizza");
                recipe2.setDescription("Simple pizza with tomato sauce, mozzarella and basil.");
                recipe2.setImage("margherita.jpg");
                recipe2.setCategory("Pizza");
                recipe2.setDeletedAt(null);
                recipe2.setUser(user2);
                recipe2.setTime(10 + random.nextInt(90));

                Recipe recipe3 = new Recipe();
                recipe3.setTitle("Caesar Salad");
                recipe3.setDescription("Fresh salad with chicken, croutons and Caesar dressing.");
                recipe3.setImage("caesar.jpg");
                recipe3.setCategory("Salad");
                recipe3.setDeletedAt(null);
                recipe3.setUser(user3);
                recipe3.setTime(10 + random.nextInt(90));

                recipeRepository.save(recipe1);
                recipeRepository.save(recipe2);
                recipeRepository.save(recipe3);
            }
        };
    }
}