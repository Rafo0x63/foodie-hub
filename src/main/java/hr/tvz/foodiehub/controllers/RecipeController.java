package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.RecipeDTO;
import hr.tvz.foodiehub.services.interfaces.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipeById(@PathVariable Long id) {
        recipeService.deleteRecipeById(id);
        return ResponseEntity.noContent().build();
    }
}
