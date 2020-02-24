package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    UserRole findByName(String role);
}
