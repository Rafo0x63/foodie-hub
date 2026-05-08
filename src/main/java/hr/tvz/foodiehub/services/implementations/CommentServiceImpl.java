package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Comment;
import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.CreateCommentRequest;
import hr.tvz.foodiehub.repositories.CommentRepository;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.services.interfaces.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, RecipeRepository recipeRepository, UserRepository userRepository){
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;

    }


    @Override
    public List<CommentDTO> getAllComments() {
        return commentRepository.findByDeletedAtIsNull()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<CommentDTO> getAllRecipeComments(Long recipeId) {
        return commentRepository.findByRecipe_IdAndDeletedAtIsNullOrderByCreatedAtDesc(recipeId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public CommentDTO getCommentById(Long recipeId, Long commentId) {
        Comment comment = commentRepository.findByIdAndRecipe_IdAndDeletedAtIsNull(commentId, recipeId)
                .orElseThrow(() -> new RuntimeException(
                        "Comment not found with id: " + commentId + " for recipe id: " + recipeId
                ));

        return mapToDTO(comment);
    }

    @Override
    public void deleteCommentById(Long recipeId, Long commentId) {
        Comment comment = commentRepository.findByIdAndRecipe_IdAndDeletedAtIsNull(commentId, recipeId)
                .orElseThrow(() -> new RuntimeException(
                        "Comment not found with id: " + commentId + " for recipe id: " + recipeId
                ));

        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    public CommentDTO createNewComment(Long recipeId, CreateCommentRequest createCommentRequest) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        Comment newComment = new Comment();
        newComment.setText(createCommentRequest.text());
        newComment.setRating(createCommentRequest.rating());
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setDeletedAt(null);
        newComment.setRecipe(recipe);

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found with id: 1"));
        newComment.setUser(user);

        Comment comment = commentRepository.save(newComment);

        return mapToDTO(comment);
    }

    @Override
    public CommentDTO updateComment(Long recipeId, Long commentId, CreateCommentRequest createCommentRequest) {
        Comment comment = commentRepository.findByIdAndRecipe_IdAndDeletedAtIsNull(commentId, recipeId)
                .orElseThrow(() -> new RuntimeException( "Comment not found with id: " + commentId + " for recipe id: " + recipeId));

        comment.setText(createCommentRequest.text());
        comment.setRating(createCommentRequest.rating());

        commentRepository.save(comment);

        return mapToDTO(comment);
    }

    @Override
    public List<CommentDTO> getAllUserComments(Long userId) {
        return commentRepository.findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private CommentDTO mapToDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getRating(),
                comment.getCreatedAt(),
                comment.getUser().getId(),
                comment.getRecipe().getId(),
                comment.getUser().getName()
        );
    }
}
