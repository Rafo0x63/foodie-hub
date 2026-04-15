package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCategory(String category, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.recipeTags rt JOIN rt.tag t WHERE t.name = :tag")
    List<Recipe> findByTagName(@Param("tag") String tag);

    List<Recipe> findByDeletedAtIsNull();

    List<Recipe> findByDeletedAtIsNotNull();

    Optional<Recipe> findByIdAndDeletedAtIsNull(Long id);



}
