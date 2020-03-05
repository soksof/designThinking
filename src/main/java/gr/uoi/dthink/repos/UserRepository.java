package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.User;
import gr.uoi.dthink.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Set<User> findByRole(UserRole role);
}
