package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.IngredientDTO;
import hr.tvz.foodiehub.model.requests.CreateIngredientRequest;
import hr.tvz.foodiehub.services.interfaces.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients(@PathVariable Long recipeId) {
        return ResponseEntity.ok(ingredientService.getAllRecipeIngredients(recipeId));
    }

    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredientById(@PathVariable Long recipeId,
                                                     @PathVariable Long ingredientId) {
        ingredientService.deleteIngredientById(recipeId, ingredientId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<IngredientDTO> createNewIngredient(
            @Valid @RequestBody CreateIngredientRequest createIngredientRequest
    ) {
        IngredientDTO createdIngredient = ingredientService.createNewIngredient(createIngredientRequest);
        URI location = URI.create("/ingredients/" + createdIngredient.getId());

        return ResponseEntity.created(location).body(createdIngredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(
            @PathVariable Long id,
            @PathVariable Long recipeId,
            @Valid @RequestBody CreateIngredientRequest createIngredientRequest
    ) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, recipeId, createIngredientRequest));
    }
}