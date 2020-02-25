package gr.uoi.dthink.services;

import gr.uoi.dthink.model.UserRole;

public interface UserRoleService {
    UserRole findByName(String role);

}
