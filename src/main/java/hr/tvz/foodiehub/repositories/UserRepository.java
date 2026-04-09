package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
