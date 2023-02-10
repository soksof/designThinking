package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.LearningResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningResourceRepository extends JpaRepository<LearningResource, Integer> {
    Boolean existsByName(String name);
}
