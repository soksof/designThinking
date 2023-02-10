package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Finding;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;

import java.util.List;

public interface FindingService {
    Finding findById(long id);
    List<Finding> findByUser(User user);
    List<Finding> findByProject(Project project);
    List<Finding> findByProjectAndUser(Project project, User user);
    Finding save(Finding comment);
    void delete(Finding comment);
}
