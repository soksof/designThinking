package gr.uoi.dthink.services;

import gr.uoi.dthink.model.UserRole;

import java.util.List;

public interface UserRoleService {
    UserRole findByName(String role);
    List<UserRole> findAll();
}
