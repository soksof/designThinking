package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.Idea;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    List<Idea> findByUser(User user);
    List<Idea> findByProject(Project project);
    List<Idea> findByProjectAndUser(Project project, User user);
}
