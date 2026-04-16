package hr.tvz.foodiehub.repositories.specification;

import hr.tvz.foodiehub.entities.Recipe;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RecipeSpecification {
    public static Specification<Recipe> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
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
