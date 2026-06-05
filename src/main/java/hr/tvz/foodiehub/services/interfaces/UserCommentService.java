package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.CommentDTO;

import java.util.List;

public interface UserCommentService {

    List<CommentDTO> getAllUserComments(Long userId);
}
