package hr.tvz.foodiehub.controllers;

import hr.tvz.foodiehub.model.dtos.StepDTO;
import hr.tvz.foodiehub.model.requests.CreateStepRequest;
import hr.tvz.foodiehub.services.interfaces.StepService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/steps")
public class StepController {

    private final StepService stepService;

    public StepController(StepService stepService) {
        this.stepService = stepService;
    }

    @GetMapping
    public ResponseEntity<List<StepDTO>> getAllRecipeSteps(@PathVariable Long recipeId) {
        return ResponseEntity.ok(stepService.getAllStepsByRecipeId(recipeId));
    }

    @PostMapping
    public ResponseEntity<StepDTO> createStep(
            @PathVariable Long recipeId,
            @Valid @RequestBody CreateStepRequest createStepRequest
    ) {
        StepDTO createdStep = stepService.createStepForRecipe(recipeId, createStepRequest);
        URI location = URI.create("/api/recipes/" + recipeId + "/steps/" + createdStep.getId());

        return ResponseEntity.created(location).body(createdStep);
    }
}