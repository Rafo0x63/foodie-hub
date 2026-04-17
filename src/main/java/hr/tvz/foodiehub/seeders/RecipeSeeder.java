package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Random;

@Configuration
public class RecipeSeeder {
    private final Random random = new Random();
    @Bean
    @Order(4)
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
                recipe1.setCategory("Tjestenina");
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
                recipe3.setCategory("Salata");
                recipe3.setDeletedAt(null);
                recipe3.setUser(user3);
                recipe3.setTime(10 + random.nextInt(90));

                Recipe recipe4 = new Recipe();
                recipe4.setTitle("Tiramisu");
                recipe4.setDescription("Italian dessert with mascarpone, espresso and ladyfingers.");
                recipe4.setImage("tiramisu.jpg");
                recipe4.setCategory("Deserti");
                recipe4.setDeletedAt(null);
                recipe4.setUser(user1);
                recipe4.setTime(10 + random.nextInt(90));

                Recipe recipe5 = new Recipe();
                recipe5.setTitle("Penne Arrabbiata");
                recipe5.setDescription("Spicy pasta with tomato sauce, garlic and chilli.");
                recipe5.setImage("arrabbiata.jpg");
                recipe5.setCategory("Tjestenina");
                recipe5.setDeletedAt(null);
                recipe5.setUser(user2);
                recipe5.setTime(10 + random.nextInt(90));

                Recipe recipe6 = new Recipe();
                recipe6.setTitle("Pepperoni Pizza");
                recipe6.setDescription("Classic pizza loaded with pepperoni and mozzarella.");
                recipe6.setImage("pepperoni.jpg");
                recipe6.setCategory("Pizza");
                recipe6.setDeletedAt(null);
                recipe6.setUser(user3);
                recipe6.setTime(10 + random.nextInt(90));

                Recipe recipe7 = new Recipe();
                recipe7.setTitle("Greek Salad");
                recipe7.setDescription("Tomatoes, cucumbers, olives and feta with olive oil.");
                recipe7.setImage("greek_salad.jpg");
                recipe7.setCategory("Salata");
                recipe7.setDeletedAt(null);
                recipe7.setUser(user1);
                recipe7.setTime(10 + random.nextInt(90));

                Recipe recipe8 = new Recipe();
                recipe8.setTitle("Chocolate Lava Cake");
                recipe8.setDescription("Warm chocolate cake with a gooey molten centre.");
                recipe8.setImage("lava_cake.jpg");
                recipe8.setCategory("Deserti");
                recipe8.setDeletedAt(null);
                recipe8.setUser(user2);
                recipe8.setTime(10 + random.nextInt(90));

                Recipe recipe9 = new Recipe();
                recipe9.setTitle("Pad Thai");
                recipe9.setDescription("Stir-fried rice noodles with shrimp, peanuts and lime.");
                recipe9.setImage("pad_thai.jpg");
                recipe9.setCategory("Azijska kuhinja");
                recipe9.setDeletedAt(null);
                recipe9.setUser(user3);
                recipe9.setTime(10 + random.nextInt(90));

                Recipe recipe10 = new Recipe();
                recipe10.setTitle("Spaghetti Bolognese");
                recipe10.setDescription("Slow-cooked meat sauce served over spaghetti.");
                recipe10.setImage("bolognese.jpg");
                recipe10.setCategory("Tjestenina");
                recipe10.setDeletedAt(null);
                recipe10.setUser(user1);
                recipe10.setTime(10 + random.nextInt(90));

                Recipe recipe11 = new Recipe();
                recipe11.setTitle("Ramen");
                recipe11.setDescription("Japanese noodle soup with rich broth, soft egg and chashu pork.");
                recipe11.setImage("ramen.jpg");
                recipe11.setCategory("Azijska kuhinja");
                recipe11.setDeletedAt(null);
                recipe11.setUser(user2);
                recipe11.setTime(10 + random.nextInt(90));

                Recipe recipe12 = new Recipe();
                recipe12.setTitle("Caprese Salad");
                recipe12.setDescription("Fresh mozzarella, tomatoes and basil drizzled with balsamic glaze.");
                recipe12.setImage("caprese.jpg");
                recipe12.setCategory("Salata");
                recipe12.setDeletedAt(null);
                recipe12.setUser(user3);
                recipe12.setTime(10 + random.nextInt(90));

                Recipe recipe13 = new Recipe();
                recipe13.setTitle("Four Cheese Pizza");
                recipe13.setDescription("Pizza topped with mozzarella, gorgonzola, parmesan and ricotta.");
                recipe13.setImage("four_cheese.jpg");
                recipe13.setCategory("Pizza");
                recipe13.setDeletedAt(null);
                recipe13.setUser(user1);
                recipe13.setTime(10 + random.nextInt(90));

                recipeRepository.save(recipe1);
                recipeRepository.save(recipe2);
                recipeRepository.save(recipe3);
                recipeRepository.save(recipe4);
                recipeRepository.save(recipe5);
                recipeRepository.save(recipe6);
                recipeRepository.save(recipe7);
                recipeRepository.save(recipe8);
                recipeRepository.save(recipe9);
                recipeRepository.save(recipe10);
                recipeRepository.save(recipe11);
                recipeRepository.save(recipe12);
                recipeRepository.save(recipe13);
            }
        };
    }
}