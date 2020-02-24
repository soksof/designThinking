package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.ExtremeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface ExtremeUserRepository extends JpaRepository<ExtremeUser, Long> {
    Optional<ExtremeUser> findByEmail(String email);
}
