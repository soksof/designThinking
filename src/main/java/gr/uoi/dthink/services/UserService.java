package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.model.UserRole;

import java.util.List;
import java.util.Set;

public interface UserService {
    User findByEmail(String email);
    boolean userExists(String email);
    boolean isAdmin();
    User getLoggedInUser();
    String getLoggedInUserName();
    Set<User> findByRole(UserRole role);
    Set<Project> findAllProjects();
    User initSave(User user);
    User save(User user);
    List<User> findAll();
    List<User> findAllButMe();
    List<User> findAllButMembers(Project project);
    User findById(long id);
}
