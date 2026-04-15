package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.model.requests.CreateCommentRequest;
import hr.tvz.foodiehub.services.interfaces.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        List<CommentDTO> comments = commentService.getAllComments();

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id);

        return ResponseEntity.ok(comment);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentDTO>> getAllUserComments(@PathVariable Long userId) {
        List<CommentDTO> comments = commentService.getAllUserComments(userId);

        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long id) {
        commentService.deleteCommentById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<CommentDTO> createNewComment(
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        CommentDTO createdComment = commentService.createNewComment(createCommentRequest);
        URI location = URI.create("/comments/" + createdComment.getId());

        return ResponseEntity.created(location).body(createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        return ResponseEntity.ok(commentService.updateComment(id, createCommentRequest));
    }
}