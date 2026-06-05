package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.CommentDTO;
import hr.tvz.foodiehub.model.requests.CreateCommentRequest;
import hr.tvz.foodiehub.services.interfaces.RecipeCommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/comments")
public class CommentController {

    private final RecipeCommentService recipeCommentService;

    public CommentController(RecipeCommentService recipeCommentService) {
        this.recipeCommentService = recipeCommentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllRecipeComments(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeCommentService.getAllRecipeComments(recipeId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long recipeId,
                                                     @PathVariable Long commentId)
    {
        return ResponseEntity.ok(recipeCommentService.getCommentById(recipeId, commentId));

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long recipeId,
                                                  @PathVariable Long commentId) {
        recipeCommentService.deleteCommentById(recipeId, commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createNewComment(
            @PathVariable Long recipeId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        CommentDTO createdComment = recipeCommentService.createNewComment(recipeId, createCommentRequest);
        URI location = URI.create("/api/recipes/" + recipeId + "/comments/" + createdComment.getId());

        return ResponseEntity.created(location).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long recipeId,
            @PathVariable Long commentId,
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        return ResponseEntity.ok(recipeCommentService.updateComment(recipeId, commentId, createCommentRequest));
    }
}