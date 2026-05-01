package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Recipe;
import hr.tvz.foodiehub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDeletedAtIsNull();
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
