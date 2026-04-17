package hr.tvz.foodiehub.repositories.specification;

import hr.tvz.foodiehub.entities.Recipe;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RecipeSpecification {
    public static Specification<Recipe> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Recipe> hasCategory(String category) {
        return (root, query, cb) ->
                category == null ? null :
                        cb.equal(root.get("category"), category);
    }

    public static Specification<Recipe> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null :
                        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Recipe> maxTime(Integer maxTime) {
        return (root, query, cb) ->
                maxTime == null ? null :
                        cb.lessThanOrEqualTo(root.get("time"), maxTime);
    }

    public static Specification<Recipe> hasTags(List<String> tagNames) {
        return (root, query, cb) -> {

            if (tagNames == null || tagNames.isEmpty()) return null;

            query.distinct(true);

            Join<Object, Object> recipeTagJoin = root.join("recipeTags");
            Join<Object, Object> tagJoin = recipeTagJoin.join("tag");

            return tagJoin.get("name").in(tagNames);
        };
    }

}
