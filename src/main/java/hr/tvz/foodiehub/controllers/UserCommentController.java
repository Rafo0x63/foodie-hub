package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.services.interfaces.UserCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/comments")
public class UserCommentController {

    private final UserCommentService userCommentService;

    public UserCommentController(UserCommentService userCommentService) {
        this.userCommentService = userCommentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllUserComments(@PathVariable Long userId) {
        return ResponseEntity.ok(userCommentService.getAllUserComments(userId));
    }
}