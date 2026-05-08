package hr.tvz.foodiehub.services;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.Role;
import hr.tvz.foodiehub.entities.User;
import hr.tvz.foodiehub.model.dtos.RecipeDTO;
import hr.tvz.foodiehub.model.requests.CreateRecipeRequest;
import hr.tvz.foodiehub.repositories.RecipeRepository;
import hr.tvz.foodiehub.repositories.UserRepository;
import hr.tvz.foodiehub.services.implementations.RecipeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeService - unit tests")
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Captor
    private ArgumentCaptor<Recipe> recipeCaptor;

    private User user;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, "ROLE_CHEF");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRoles(List.of(role));

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pizza");
        recipe.setDescription("Good pizza");
        recipe.setCategory("Italian");
        recipe.setUser(user);
        recipe.setDeletedAt(null);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getAllRecipes returns all non-deleted recipes as DTOs")
    void getAllRecipes_returnsRecipeDTOList() {
        when(recipeRepository.findByDeletedAtIsNull())
                .thenReturn(List.of(recipe));

        List<RecipeDTO> result = recipeService.getAllRecipes();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getTitle()).isEqualTo("Pizza");
        assertThat(result.getFirst().getDescription()).isEqualTo("Good pizza");
        assertThat(result.getFirst().getCategory()).isEqualTo("Italian");
        assertThat(result.getFirst().getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirst().getUser().getRole()).isEqualTo("ROLE_CHEF");

        verify(recipeRepository).findByDeletedAtIsNull();
        verifyNoMoreInteractions(recipeRepository);
    }

    @Test
    @DisplayName("getRecipeById returns recipe when recipe exists")
    void getRecipeById_existingRecipe_returnsRecipeDTO() {
        when(recipeRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(recipe));

        RecipeDTO result = recipeService.getRecipeById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Pizza");
        assertThat(result.getUser().getName()).isEqualTo("Test User");

        verify(recipeRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("getRecipeById throws exception when recipe does not exist")
    void getRecipeById_nonExistingRecipe_throwsException() {
        when(recipeRepository.findByIdAndDeletedAtIsNull(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getRecipeById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Recipe not found with id: 99");

        verify(recipeRepository).findByIdAndDeletedAtIsNull(99L);
    }

    @Test
    @DisplayName("createNewRecipe saves recipe for authenticated user")
    void createNewRecipe_validRequest_savesAndReturnsRecipeDTO() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@example.com", null)
        );

        CreateRecipeRequest request = new CreateRecipeRequest(
                "Burger",
                "Nice burger",
                "Fast food",
                "image-url",
                10,
                20,
                2,
                List.of("meat", "bun"),
                List.of("cook meat", "assemble")
        );

        Recipe savedRecipe = new Recipe();
        savedRecipe.setId(2L);
        savedRecipe.setTitle("Burger");
        savedRecipe.setDescription("Nice burger");
        savedRecipe.setCategory("Fast food");
        savedRecipe.setUser(user);

        when(userRepository.findByEmailAndDeletedAtIsNull("test@example.com"))
                .thenReturn(Optional.of(user));

        when(recipeRepository.save(any(Recipe.class)))
                .thenReturn(savedRecipe);

        RecipeDTO result = recipeService.createNewRecipe(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("Burger");
        assertThat(result.getDescription()).isEqualTo("Nice burger");
        assertThat(result.getCategory()).isEqualTo("Fast food");
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findByEmailAndDeletedAtIsNull("test@example.com");
        verify(recipeRepository).save(recipeCaptor.capture());

        Recipe capturedRecipe = recipeCaptor.getValue();

        assertThat(capturedRecipe.getTitle()).isEqualTo("Burger");
        assertThat(capturedRecipe.getDescription()).isEqualTo("Nice burger");
        assertThat(capturedRecipe.getCategory()).isEqualTo("Fast food");
        assertThat(capturedRecipe.getDeletedAt()).isNull();
        assertThat(capturedRecipe.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("createNewRecipe throws exception when authenticated user does not exist")
    void createNewRecipe_userNotFound_throwsException() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("missing@example.com", null)
        );

        CreateRecipeRequest request = new CreateRecipeRequest(
                "Burger",
                "Nice burger",
                "Fast food",
                null,
                null,
                null,
                null,
                List.of(),
                List.of()
        );

        when(userRepository.findByEmailAndDeletedAtIsNull("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.createNewRecipe(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository).findByEmailAndDeletedAtIsNull("missing@example.com");
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    @DisplayName("deleteRecipeById soft deletes existing recipe")
    void deleteRecipeById_existingRecipe_setsDeletedAtAndSaves() {
        when(recipeRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipeById(1L);

        assertThat(recipe.getDeletedAt()).isNotNull();

        verify(recipeRepository).findByIdAndDeletedAtIsNull(1L);
        verify(recipeRepository).save(recipe);
    }

    @Test
    @DisplayName("deleteRecipeById throws exception when recipe does not exist")
    void deleteRecipeById_nonExistingRecipe_throwsException() {
        when(recipeRepository.findByIdAndDeletedAtIsNull(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipeById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Recipe not found with id: 99");

        verify(recipeRepository).findByIdAndDeletedAtIsNull(99L);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    @DisplayName("updateRecipe updates title, description and category")
    void updateRecipe_existingRecipe_updatesAndReturnsRecipeDTO() {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Updated Pizza",
                "Updated description",
                "Updated category",
                null,
                null,
                null,
                null,
                List.of(),
                List.of()
        );

        when(recipeRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(recipe));

        when(recipeRepository.save(recipe))
                .thenReturn(recipe);

        RecipeDTO result = recipeService.updateRecipe(1L, request);

        assertThat(result.getTitle()).isEqualTo("Updated Pizza");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getCategory()).isEqualTo("Updated category");

        verify(recipeRepository).findByIdAndDeletedAtIsNull(1L);
        verify(recipeRepository).save(recipe);
    }

    @Test
    @DisplayName("updateRecipe throws exception when recipe does not exist")
    void updateRecipe_nonExistingRecipe_throwsException() {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Updated Pizza",
                "Updated description",
                "Updated category",
                null,
                null,
                null,
                null,
                List.of(),
                List.of()
        );

        when(recipeRepository.findByIdAndDeletedAtIsNull(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Recipe not found with id: 99");

        verify(recipeRepository).findByIdAndDeletedAtIsNull(99L);
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    @DisplayName("search returns recipes matching specification")
    void search_returnsMatchingRecipes() {
        when(recipeRepository.findAll(ArgumentMatchers.<Specification<Recipe>>any()))
                .thenReturn(List.of(recipe));

        List<RecipeDTO> result = recipeService.search(
                "Pizza",
                30,
                List.of("cheese"),
                "Italian"
        );

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Pizza");

        verify(recipeRepository).findAll(ArgumentMatchers.<Specification<Recipe>>any());
    }

    @Test
    @DisplayName("isOwner returns true when recipe belongs to user")
    void isOwner_recipeBelongsToUser_returnsTrue() {
        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        boolean result = recipeService.isOwner(1L, "test@example.com");

        assertThat(result).isTrue();

        verify(recipeRepository).findById(1L);
    }

    @Test
    @DisplayName("isOwner returns false when recipe belongs to another user")
    void isOwner_recipeBelongsToAnotherUser_returnsFalse() {
        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        boolean result = recipeService.isOwner(1L, "other@example.com");

        assertThat(result).isFalse();

        verify(recipeRepository).findById(1L);
    }

    @Test
    @DisplayName("isOwner returns false when recipe does not exist")
    void isOwner_recipeDoesNotExist_returnsFalse() {
        when(recipeRepository.findById(99L))
                .thenReturn(Optional.empty());

        boolean result = recipeService.isOwner(99L, "test@example.com");

        assertThat(result).isFalse();

        verify(recipeRepository).findById(99L);
    }
}