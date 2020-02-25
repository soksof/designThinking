package gr.uoi.dthink.services;

import gr.uoi.dthink.model.User;
import gr.uoi.dthink.model.UserRole;

import java.util.Set;

public interface UserService {
    User findByEmail(String email);
    Set<User> findByRoles(UserRole role);
    User save(User user);
}
