package gr.uoi.dthink.services;

import gr.uoi.dthink.model.UserRole;
import gr.uoi.dthink.repos.UserRoleRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService{
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserRole findByName(String role) {
        return userRoleRepository.findByName(role);
    }
}
