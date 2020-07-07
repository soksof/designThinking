package gr.uoi.dthink.services;

import gr.uoi.dthink.model.UserRole;
import gr.uoi.dthink.repos.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<UserRole> findAll() {
        return userRoleRepository.findAll();
    }
}
