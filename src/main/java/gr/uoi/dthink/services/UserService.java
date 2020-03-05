package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.model.UserRole;

import java.util.Set;

public interface UserService {
    User findByEmail(String email);
    boolean userExists(String email);
    boolean isAdmin();
    String getLoggedInUserName();
    Set<User> findByRole(UserRole role);
    Set<Project> findAllProjects();
    User save(User user);
}
