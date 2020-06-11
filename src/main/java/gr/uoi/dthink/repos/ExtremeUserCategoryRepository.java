package gr.uoi.dthink.repos;

import java.util.List;
import gr.uoi.dthink.model.ExtremeUserCategory;
import gr.uoi.dthink.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExtremeUserCategoryRepository extends JpaRepository<ExtremeUserCategory, Long> {
    Optional<ExtremeUserCategory> findByName(String name);
    List<ExtremeUserCategory> findAllByProject(Project project);
}
