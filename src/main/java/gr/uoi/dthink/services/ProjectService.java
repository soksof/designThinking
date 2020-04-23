package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;

import java.util.List;

public interface ProjectService {
    List<Project> findByManager(User manager);
    List<Project> findByMembers(User user);
    Project findById(int id);
    Project save(Project project);
    Project nextStage(Project project);
    void delete(Project project);
}
