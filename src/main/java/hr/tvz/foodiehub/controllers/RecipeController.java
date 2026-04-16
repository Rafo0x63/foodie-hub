package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.requests.CreateRecipeRequest;
import hr.tvz.foodiehub.model.dtos.RecipeDTO;
import hr.tvz.foodiehub.services.interfaces.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(required = false) List<String> tags
    ) {
        return ResponseEntity.ok(recipeService.search(name, maxTime, tags));
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

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id, @Valid @RequestBody CreateRecipeRequest createRecipeRequest) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, createRecipeRequest));
    }

    @PostMapping
    public ResponseEntity<RecipeDTO> createNewRecipe(@Valid @RequestBody CreateRecipeRequest createRecipeRequest){
        RecipeDTO recipeDTO = recipeService.createNewRecipe(createRecipeRequest);
        return ResponseEntity
                .created(URI.create("/api/recipes/" + recipeDTO.getId()))
                .body(recipeDTO);
    }
}
