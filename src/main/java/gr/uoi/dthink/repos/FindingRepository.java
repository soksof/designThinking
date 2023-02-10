package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.Finding;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FindingRepository extends JpaRepository<Finding, Long> {
    List<Finding> findByUser(User user);
    List<Finding> findByProject(Project project);
    List<Finding> findByProjectAndUser(Project project, User user);
}
