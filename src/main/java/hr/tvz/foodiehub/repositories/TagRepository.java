package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
