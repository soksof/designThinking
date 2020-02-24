package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Project findByManager(User manager);
    List<Project> findAllByUsers(User user);
}
