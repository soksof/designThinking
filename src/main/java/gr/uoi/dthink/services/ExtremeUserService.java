package gr.uoi.dthink.services;

import java.util.List;
import gr.uoi.dthink.model.ExtremeUser;
import gr.uoi.dthink.model.ExtremeUserCategory;
import gr.uoi.dthink.model.Project;

public interface ExtremeUserService {
    ExtremeUser findByEmail(String email);
    List<ExtremeUser> findByCategory(ExtremeUserCategory extremeUserCategory);
    List<ExtremeUser> findByProject(Project project);
    ExtremeUser save(ExtremeUser extremeUser);
    void delete(ExtremeUser extremeUser);
}
