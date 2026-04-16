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
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients() {
        List<IngredientDTO> ingredients = ingredientService.getAllIngredients();

        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id) {
        IngredientDTO ingredient = ingredientService.getIngredientById(id);

        return ResponseEntity.ok(ingredient);
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<IngredientDTO>> getAllRecipeIngredients(@PathVariable Long recipeId) {
        List<IngredientDTO> ingredients = ingredientService.getAllRecipeIngredients(recipeId);

        return ResponseEntity.ok(ingredients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredientById(@PathVariable Long id) {
        ingredientService.deleteIngredientById(id);

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
            @Valid @RequestBody CreateIngredientRequest createIngredientRequest
    ) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, createIngredientRequest));
    }
}