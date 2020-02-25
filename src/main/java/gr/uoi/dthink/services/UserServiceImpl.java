package gr.uoi.dthink.services;

import gr.uoi.dthink.model.User;
import gr.uoi.dthink.model.UserRole;
import gr.uoi.dthink.repos.UserRepository;
import gr.uoi.dthink.repos.UserRoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Set<User> findByRoles(UserRole role) {
        return userRepository.findByRoles(role);
    }

    @Override
    public User save(User user) {
        return null;
    }
}
