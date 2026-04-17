package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Comment;
import hr.tvz.foodiehub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDeletedAtIsNull();

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    List<Comment> findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

    List<Comment> findByRecipe_IdAndDeletedAtIsNullOrderByCreatedAtDesc(Long recipeId);

    Optional<Comment> findByIdAndRecipe_IdAndDeletedAtIsNull(Long commentId, Long recipeId);
}
