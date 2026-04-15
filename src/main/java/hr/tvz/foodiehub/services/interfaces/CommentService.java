package hr.tvz.foodiehub.services.interfaces;
import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.model.requests.CreateCommentRequest;

import java.util.List;

public interface CommentService {

    List<CommentDTO> getAllComments();
    CommentDTO getCommentById(Long id);
    void deleteCommentById(Long id);
    CommentDTO createNewComment(CreateCommentRequest createCommentRequest);
    CommentDTO updateComment(Long id, CreateCommentRequest createCommentRequest);
    List<CommentDTO> getAllUserComments(Long userId);
}
