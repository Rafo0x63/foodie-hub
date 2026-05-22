package hr.tvz.foodiehub.repositories.specification;

import hr.tvz.foodiehub.entities.Recipe;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RecipeSpecification - unit tests")
class RecipeSpecificationTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Root<Recipe> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CriteriaBuilder cb;

    @Test
    @DisplayName("notDeleted invokes cb.isNull on the deletedAt path")
    void notDeleted_callsIsNull() {
        Specification<Recipe> spec = RecipeSpecification.notDeleted();

        spec.toPredicate(root, query, cb);

        verify(cb).isNull(any());
    }

    @Test
    @DisplayName("hasCategory returns null predicate for null input")
    void hasCategory_nullReturnsNull() {
        Specification<Recipe> spec = RecipeSpecification.hasCategory(null);

        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    @DisplayName("hasCategory calls cb.equal with the provided category")
    void hasCategory_withValueCallsEqual() {
        Specification<Recipe> spec = RecipeSpecification.hasCategory("dessert");

        spec.toPredicate(root, query, cb);

        verify(cb).equal(any(), eq("dessert"));
    }

    @Test
    @DisplayName("hasTitle returns null predicate for null input")
    void hasTitle_nullReturnsNull() {
        Specification<Recipe> spec = RecipeSpecification.hasTitle(null);

        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    @DisplayName("hasTitle lowercases and wraps title in % for a LIKE query")
    void hasTitle_withValueCallsLike() {
        Specification<Recipe> spec = RecipeSpecification.hasTitle("Pizza");

        spec.toPredicate(root, query, cb);

        verify(cb).lower(any());
        verify(cb).like(any(), eq("%pizza%"));
    }

    @Test
    @DisplayName("maxTime returns null predicate for null input")
    void maxTime_nullReturnsNull() {
        Specification<Recipe> spec = RecipeSpecification.maxTime(null);

        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    @DisplayName("maxTime calls cb.lessThanOrEqualTo with the provided value")
    void maxTime_withValueCallsLte() {
        Specification<Recipe> spec = RecipeSpecification.maxTime(30);

        spec.toPredicate(root, query, cb);

        verify(cb).lessThanOrEqualTo(any(), eq(30));
    }

    @Test
    @DisplayName("hasTags returns null predicate for null list")
    void hasTags_nullReturnsNull() {
        Specification<Recipe> spec = RecipeSpecification.hasTags(null);

        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    @DisplayName("hasTags returns null predicate for empty list")
    void hasTags_emptyReturnsNull() {
        Specification<Recipe> spec = RecipeSpecification.hasTags(Collections.emptyList());

        assertThat(spec.toPredicate(root, query, cb)).isNull();
    }

    @Test
    @DisplayName("hasTags makes the query distinct and joins recipeTags + tag")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void hasTags_withValuesJoinsAndDistinct() {
        Join recipeTagJoin = Mockito.mock(Join.class, Answers.RETURNS_DEEP_STUBS);
        Join tagJoin = Mockito.mock(Join.class, Answers.RETURNS_DEEP_STUBS);
        Mockito.when(root.join("recipeTags")).thenReturn(recipeTagJoin);
        Mockito.when(recipeTagJoin.join("tag")).thenReturn(tagJoin);

        Specification<Recipe> spec = RecipeSpecification.hasTags(List.of("vegan"));
        spec.toPredicate(root, query, cb);

        verify(query).distinct(true);
        verify(root).join("recipeTags");
        verify(recipeTagJoin).join("tag");
    }
}
