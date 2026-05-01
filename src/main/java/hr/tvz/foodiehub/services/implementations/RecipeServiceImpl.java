package hr.tvz.foodiehub.services.implementations;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.UserDTO;
import hr.tvz.foodiehub.model.requests.CreateRecipeRequest;
import hr.tvz.foodiehub.model.dtos.RecipeDTO;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.repositories.specification.RecipeSpecification;
import hr.tvz.foodiehub.services.interfaces.RecipeService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findByDeletedAtIsNull()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public RecipeDTO getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        return mapToDTO(recipe);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN') or @recipeServiceImpl.isOwner(#id, authentication.name)")
    public void deleteRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        recipe.setDeletedAt(LocalDateTime.now());
        recipeRepository.save(recipe);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_CHEF') or hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
    public RecipeDTO createNewRecipe(CreateRecipeRequest createRecipeRequest) {
        Recipe newRecipe = new Recipe();
        newRecipe.setTitle(createRecipeRequest.title());
        newRecipe.setDescription(createRecipeRequest.description());
        newRecipe.setCategory(createRecipeRequest.category());
        newRecipe.setDeletedAt(null);

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmailAndDeletedAtIsNull(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        newRecipe.setUser(user);

        Recipe recipe = recipeRepository.save(newRecipe);
        return mapToDTO(recipe);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN') or @recipeServiceImpl.isOwner(#id, authentication.name)")
    public RecipeDTO updateRecipe(Long id, CreateRecipeRequest createRecipeRequest) {
        Recipe recipe = recipeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));

        recipe.setTitle(createRecipeRequest.title());
        recipe.setDescription(createRecipeRequest.description());
        recipe.setCategory(createRecipeRequest.category());

        return mapToDTO(recipeRepository.save(recipe));
    }

    @Override
    public List<RecipeDTO> search(String title, Integer maxTime, List<String> tags, String category) {
        Specification<Recipe> spec = Specification
                .where(RecipeSpecification.notDeleted())
                .and(RecipeSpecification.hasTitle(title))
                .and(RecipeSpecification.maxTime(maxTime))
                .and(RecipeSpecification.hasTags(tags))
                .and(RecipeSpecification.hasCategory(category));

        return recipeRepository.findAll(spec)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private RecipeDTO mapToDTO(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getCategory(),
                new UserDTO(
                        recipe.getUser().getId(),
                        recipe.getUser().getName(),
                        recipe.getUser().getEmail(),
                        recipe.getUser().getRoles().get(0).getRoleName()
                )
        );
    }

    public boolean isOwner(Long recipeId, String email) {
        return recipeRepository.findById(recipeId)
                .map(r -> r.getUser().getEmail().equals(email))
                .orElse(false);
    }
}
