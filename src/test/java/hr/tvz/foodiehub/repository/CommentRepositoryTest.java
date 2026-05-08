package hr.tvz.foodiehub.repository;

import hr.tvz.foodiehub.entities.Comment;
import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.repositories.CommentRepository;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByDeletedAtIsNull_returnsOnlyNonDeletedComments() {

        User user = createUser("Active User", "test@user.com", null);
        user = userRepository.save(user);

        Recipe recipe = new Recipe();
        recipe.setTitle("Pizza");
        recipe = recipeRepository.save(recipe);

        Comment activeComment = createComment(user, recipe);

        Comment deletedComment = createComment(user, recipe);

        commentRepository.save(activeComment);
        commentRepository.save(deletedComment);

        List<Comment> comments = commentRepository.findByDeletedAtIsNull();

        assertTrue(comments.size() > 1);
    }

    @Test
    void findByIdAndDeletedAtIsNull_returnsComment() {

        User user = createUser("Active User", "test@user.com", null);
        user = userRepository.save(user);

        Recipe recipe = new Recipe();
        recipe.setTitle("Burger");
        recipe = recipeRepository.save(recipe);

        Comment comment = createComment(user, recipe);

        comment = commentRepository.save(comment);

        Optional<Comment> result =
                commentRepository.findByIdAndDeletedAtIsNull(comment.getId());

        assertTrue(result.isPresent());
    }

    @Test
    void findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc_returnsComments() {

        User user = createUser("Active User", "test@user.com", null);
        user = userRepository.save(user);

        Recipe recipe = new Recipe();
        recipe.setTitle("Pasta");
        recipe = recipeRepository.save(recipe);

        Comment comment = createComment(user, recipe);

        commentRepository.save(comment);

        List<Comment> comments =
                commentRepository.findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(user.getId());

        assertFalse(comments.isEmpty());
    }

    @Test
    void findByRecipeIdAndDeletedAtIsNullOrderByCreatedAtDesc_returnsComments() {

        User user = createUser("Active User", "test@user.com", null);
        user = userRepository.save(user);

        Recipe recipe = new Recipe();
        recipe.setTitle("Soup");
        recipe = recipeRepository.save(recipe);

        Comment comment = createComment(user, recipe);

        commentRepository.save(comment);

        List<Comment> comments =
                commentRepository.findByRecipe_IdAndDeletedAtIsNullOrderByCreatedAtDesc(recipe.getId());

        assertFalse(comments.isEmpty());
    }

    @Test
    void findByIdAndRecipeIdAndDeletedAtIsNull_returnsComment() {

        User user = createUser("Active User", "test@user.com", null);
        user = userRepository.save(user);

        Recipe recipe = new Recipe();
        recipe.setTitle("Cake");
        recipe = recipeRepository.save(recipe);

        Comment comment = createComment(user, recipe);

        comment = commentRepository.save(comment);

        Optional<Comment> result =
                commentRepository.findByIdAndRecipe_IdAndDeletedAtIsNull(
                        comment.getId(),
                        recipe.getId()
                );

        assertTrue(result.isPresent());
    }

    private User createUser(String name, String email, LocalDateTime deletedAt) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password");
        user.setDeletedAt(deletedAt);

        return user;
    }

    private static Comment createComment(User user, Recipe recipe) {
        Comment activeComment = new Comment();
        activeComment.setUser(user);
        activeComment.setRecipe(recipe);
        activeComment.setText("Great recipe!");
        activeComment.setCreatedAt(LocalDateTime.now());
        return activeComment;
    }
}
