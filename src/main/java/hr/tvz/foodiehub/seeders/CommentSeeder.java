package hr.tvz.foodiehub.seeders;

import hr.tvz.foodiehub.entities.Comment;
import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.repositories.CommentRepository;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class CommentSeeder {
    @Bean
    @Order(6)
    CommandLineRunner seedComments(CommentRepository commentRepository, RecipeRepository recipeRepository, UserRepository userRepository){
        return args -> {
            if(commentRepository.findByDeletedAtIsNull().isEmpty()){
                List<Recipe> recipeList = recipeRepository.findByDeletedAtIsNull();
                List<User> userList = userRepository.findByDeletedAtIsNull();

                if(recipeList.isEmpty() || userList.isEmpty()){
                    return;
                }

                Recipe recipe1 = recipeList.getFirst();
                Recipe recipe2 = recipeList.size() > 1 ? recipeList.get(1) : recipeList.getFirst();
                Recipe recipe3 = recipeList.size() > 2 ? recipeList.get(2) : recipeList.getFirst();

                User user1 = userList.getFirst();
                User user2 = userList.size() > 1 ? userList.get(1) : userList.getFirst();
                User user3 = userList.size() > 2 ? userList.get(2) : userList.getFirst();

                Comment comment1 = new Comment();
                comment1.setText("Odličan recept, stvarno jednostavan za pripremu!");
                comment1.setRating(5);
                comment1.setCreatedAt(LocalDateTime.now().minusDays(3));
                comment1.setDeletedAt(null);
                comment1.setRecipe(recipe1);
                comment1.setUser(user1);

                Comment comment2 = new Comment();
                comment2.setText("Jako fino, ali ja sam dodao još malo začina.");
                comment2.setRating(4);
                comment2.setCreatedAt(LocalDateTime.now().minusDays(2));
                comment2.setDeletedAt(null);
                comment2.setRecipe(recipe1);
                comment2.setUser(user2);

                Comment comment3 = new Comment();
                comment3.setText("Super recept, moja obitelj je bila oduševljena.");
                comment3.setRating(5);
                comment3.setCreatedAt(LocalDateTime.now().minusDays(1));
                comment3.setDeletedAt(null);
                comment3.setRecipe(recipe2);
                comment3.setUser(user3);

                Comment comment4 = new Comment();
                comment4.setText("Brzo i ukusno, sigurno radim ponovno.");
                comment4.setRating(4);
                comment4.setCreatedAt(LocalDateTime.now().minusHours(10));
                comment4.setDeletedAt(null);
                comment4.setRecipe(recipe3);
                comment4.setUser(user1);

                Comment comment5 = new Comment();
                comment5.setText("Dobar recept, ali meni je ispalo malo preslano.");
                comment5.setRating(3);
                comment5.setCreatedAt(LocalDateTime.now().minusHours(5));
                comment5.setDeletedAt(null);
                comment5.setRecipe(recipe2);
                comment5.setUser(user2);

                commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));
            }
        };
    }
}
