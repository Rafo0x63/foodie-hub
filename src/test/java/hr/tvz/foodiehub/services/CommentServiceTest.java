package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Comment;
import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.model.requests.CreateCommentRequest;
import hr.tvz.foodiehub.repositories.CommentRepository;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.services.implementations.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService - unit tests")
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Recipe recipe;
    private Comment comment;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Test User");

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pizza");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Nice recipe");
        comment.setRating(5);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setRecipe(recipe);
        comment.setDeletedAt(null);
    }

    @Test
    @DisplayName("getAllComments returns all comments")
    void getAllComments_returnsList() {

        when(commentRepository.findByDeletedAtIsNull())
                .thenReturn(List.of(comment));

        List<CommentDTO> result = commentService.getAllComments();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);

        verify(commentRepository).findByDeletedAtIsNull();
    }

    @Test
    @DisplayName("getAllRecipeComments returns comments for recipe")
    void getAllRecipeComments_returnsList() {

        when(commentRepository.findByRecipe_IdAndDeletedAtIsNullOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(comment));

        List<CommentDTO> result = commentService.getAllRecipeComments(1L);

        assertThat(result).hasSize(1);

        verify(commentRepository)
                .findByRecipe_IdAndDeletedAtIsNullOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("getCommentById returns comment when exists")
    void getCommentById_returnsComment() {

        when(commentRepository.findByIdAndRecipe_IdAndDeletedAtIsNull(1L, 1L))
                .thenReturn(Optional.of(comment));

        CommentDTO result = commentService.getCommentById(1L, 1L);

        assertThat(result.getId()).isEqualTo(1L);

        verify(commentRepository)
                .findByIdAndRecipe_IdAndDeletedAtIsNull(1L, 1L);
    }

    @Test
    @DisplayName("deleteCommentById soft deletes comment")
    void deleteComment_softDeletes() {

        when(commentRepository.findByIdAndRecipe_IdAndDeletedAtIsNull(1L, 1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteCommentById(1L, 1L);

        assertThat(comment.getDeletedAt()).isNotNull();

        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("updateComment updates text and rating")
    void updateComment_updatesFields() {

        CreateCommentRequest request = new CreateCommentRequest(
                "Updated",
                3,
                1L
        );

        when(commentRepository.findByIdAndRecipe_IdAndDeletedAtIsNull(1L, 1L))
                .thenReturn(Optional.of(comment));

        when(commentRepository.save(comment))
                .thenReturn(comment);

        CommentDTO result =
                commentService.updateComment(1L, 1L, request);

        assertThat(result).isNotNull();

        verify(commentRepository).save(comment);

        assertThat(comment.getText()).isEqualTo("Updated");
        assertThat(comment.getRating()).isEqualTo(3);
    }

    @Test
    @DisplayName("getAllUserComments returns user comments")
    void getAllUserComments_returnsList() {

        when(commentRepository.findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(comment));

        List<CommentDTO> result = commentService.getAllUserComments(1L);

        assertThat(result).hasSize(1);

        verify(commentRepository)
                .findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(1L);
    }
}
