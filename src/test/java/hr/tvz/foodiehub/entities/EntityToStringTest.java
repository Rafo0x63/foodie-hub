package hr.tvz.foodiehub.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Entity toString overrides - unit tests")
class EntityToStringTest {

    @Test
    @DisplayName("User#toString includes id, name and email")
    void user_toStringIncludesFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Ana");
        user.setEmail("ana@gmail.com");

        String s = user.toString();

        assertThat(s).contains("1", "Ana", "ana@gmail.com");
    }

    @Test
    @DisplayName("Role#toString includes id and name")
    void role_toStringIncludesFields() {
        Role role = new Role();
        role.setId(2L);
        role.setRoleName("ROLE_USER");

        String s = role.toString();

        assertThat(s).contains("2", "ROLE_USER");
    }

    @Test
    @DisplayName("Recipe#toString includes id, title and category")
    void recipe_toStringIncludesFields() {
        Recipe recipe = new Recipe();
        recipe.setId(3L);
        recipe.setTitle("Pizza");
        recipe.setCategory("main");

        String s = recipe.toString();

        assertThat(s).contains("3", "Pizza", "main");
    }

    @Test
    @DisplayName("Ingredient#toString includes id and name")
    void ingredient_toStringIncludesFields() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(4L);
        ingredient.setName("Cheese");

        String s = ingredient.toString();

        assertThat(s).contains("4", "Cheese");
    }
}
