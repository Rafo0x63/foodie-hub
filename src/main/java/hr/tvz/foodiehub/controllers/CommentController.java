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
@RequestMapping("/api/recipes/{recipeId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllRecipeComments(@PathVariable Long recipeId) {
        return ResponseEntity.ok(commentService.getAllRecipeComments(recipeId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long recipeId,
                                                     @PathVariable Long commentId)
    {
        return ResponseEntity.ok(commentService.getCommentById(recipeId, commentId));

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long recipeId,
                                                  @PathVariable Long commentId) {
        commentService.deleteCommentById(recipeId, commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createNewComment(
            @PathVariable Long recipeId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        CommentDTO createdComment = commentService.createNewComment(recipeId, createCommentRequest);
        URI location = URI.create("/api/recipes/" + recipeId + "/comments/" + createdComment.getId());

        return ResponseEntity.created(location).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long recipeId,
            @PathVariable Long commentId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        return ResponseEntity.ok(commentService.updateComment(recipeId, commentId, createCommentRequest));
    }
}