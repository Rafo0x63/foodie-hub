package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.services.interfaces.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/comments")
public class UserCommentController {

    private final CommentService commentService;

    public UserCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllUserComments(@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getAllUserComments(userId));
    }
}