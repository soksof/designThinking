package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.Reaction;
import gr.uoi.dthink.model.User;

import java.util.List;

public interface ReactionService {
    List<Reaction> findByUser(User user);
    Reaction save(Reaction reaction);
//    List<Reaction> findByProject(Project project);
}
