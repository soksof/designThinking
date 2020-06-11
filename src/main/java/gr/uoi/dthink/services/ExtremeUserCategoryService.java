package gr.uoi.dthink.services;

import gr.uoi.dthink.model.ExtremeUserCategory;
import gr.uoi.dthink.model.Project;
import java.util.List;

public interface ExtremeUserCategoryService {
    List<ExtremeUserCategory> findByProject(Project project);
    ExtremeUserCategory findById(long id);
    ExtremeUserCategory findByName(String name);
    ExtremeUserCategory save(ExtremeUserCategory category);
    void delete(ExtremeUserCategory category);
}
