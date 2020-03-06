package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.Reaction;
import gr.uoi.dthink.model.User;

import java.util.List;

public interface ReactionService {
    List<Reaction> findByUser(User user);
    List<Reaction> findByProject(Project project);
    List<Reaction> findByProjectAndUser(Project project, User user);
}
