package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.EmpathyMap;
import gr.uoi.dthink.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpathyMapRepository extends JpaRepository<EmpathyMap, Integer> {
}
