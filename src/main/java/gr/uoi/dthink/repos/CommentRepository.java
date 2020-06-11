package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.Comment;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);
    List<Comment> findByProject(Project project);
    List<Comment> findByProjectAndUser(Project project, User user);
}
