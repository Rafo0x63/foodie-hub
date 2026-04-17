package hr.tvz.foodiehub.services.interfaces;
import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.model.requests.CreateCommentRequest;

import java.util.List;

public interface CommentService {

    List<CommentDTO> getAllComments();

    List<CommentDTO> getAllRecipeComments(Long recipeId);

    CommentDTO getCommentById(Long recipeId, Long commentId);

    void deleteCommentById(Long recipeId, Long commentId);

    CommentDTO createNewComment(Long recipeId, CreateCommentRequest createCommentRequest);

    CommentDTO updateComment(Long recipeId, Long commentId, CreateCommentRequest createCommentRequest);

    List<CommentDTO> getAllUserComments(Long userId);
}
