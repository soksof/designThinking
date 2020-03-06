package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.Reaction;
import gr.uoi.dthink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findByUser(User user);
    List<Reaction> findByProject(Project project);
    List<Reaction> findByProjectAndUser(Project project, User user);
}
