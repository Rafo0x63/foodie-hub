package hr.tvz.foodiehub.repositories;

import hr.tvz.foodiehub.entities.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByDeletedAtIsNullAndRecipe_IdOrderByStepNumberAsc(Long id);
}
