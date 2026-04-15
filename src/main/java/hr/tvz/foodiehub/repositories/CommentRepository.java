package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Comment;
import hr.tvz.foodiehub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);
}
