package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.Step;
import hr.tvz.foodiehub.model.dtos.StepDTO;
import hr.tvz.foodiehub.model.requests.CreateStepRequest;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.StepRepository;
import hr.tvz.foodiehub.services.interfaces.StepService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepServiceImpl implements StepService {

    private final StepRepository stepRepository;
    private final RecipeRepository recipeRepository;

    public StepServiceImpl(StepRepository stepRepository, RecipeRepository recipeRepository){
        this.stepRepository = stepRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<StepDTO> getAllStepsByRecipeId(Long id) {
        return List.of();
    }

    @Override
    public StepDTO createStepForRecipe(Long recipeId, CreateStepRequest createStepRequest) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));

        Step newStep = new Step();
        newStep.setStepNumber(createStepRequest.stepNumber());
        newStep.setTitle(createStepRequest.title());
        newStep.setDescription(createStepRequest.description());
        newStep.setTime(createStepRequest.time());
        newStep.setRecipe(recipe);

        Step savedStep = stepRepository.save(newStep);

        return mapToDto(savedStep);
    }

    private StepDTO mapToDto(Step step){
        return new StepDTO(
                step.getId(),
                step.getStepNumber(),
                step.getTitle(),
                step.getDescription(),
                step.getTime(),
                step.getRecipe().getId()
        );
    }
}
