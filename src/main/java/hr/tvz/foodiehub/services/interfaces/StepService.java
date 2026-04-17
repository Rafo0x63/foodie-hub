package hr.tvz.foodiehub.services.interfaces;

import hr.tvz.foodiehub.model.dtos.StepDTO;
import hr.tvz.foodiehub.model.requests.CreateStepRequest;

import java.util.List;

public interface StepService {

    List<StepDTO> getAllStepsByRecipeId(Long id);

    StepDTO createStepForRecipe(Long recipeId, CreateStepRequest createStepRequest);
}
