package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.ExtremeUser;
import gr.uoi.dthink.model.ExtremeUserCategory;
import gr.uoi.dthink.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExtremeUserRepository extends JpaRepository<ExtremeUser, Long> {
    Optional<ExtremeUser> findByEmail(String email);
    List<ExtremeUser> findAllByExtremeUserCategory(ExtremeUserCategory extremeUserCategory);
    List<ExtremeUser> findAllByExtremeUserCategory_Project(Project project);
}
