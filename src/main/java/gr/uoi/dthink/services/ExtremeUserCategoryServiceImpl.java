package gr.uoi.dthink.services;

import gr.uoi.dthink.model.ExtremeUserCategory;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.repos.ExtremeUserCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtremeUserCategoryServiceImpl implements ExtremeUserCategoryService{
    ExtremeUserCategoryRepository extremeUserCategoryRepository;

    public ExtremeUserCategoryServiceImpl(ExtremeUserCategoryRepository extremeUserCategoryRepository){
        this.extremeUserCategoryRepository = extremeUserCategoryRepository;
    }

    @Override
    public List<ExtremeUserCategory> findByProject(Project project) {
        return extremeUserCategoryRepository.findAllByProject(project);
    }

    @Override
    public ExtremeUserCategory findById(int id) {
        return extremeUserCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public ExtremeUserCategory findByName(String name) {
        return extremeUserCategoryRepository.findByName(name).orElse(null);
    }

    @Override
    public ExtremeUserCategory save(ExtremeUserCategory category) {
        return extremeUserCategoryRepository.save(category);
    }

    @Override
    public void delete(ExtremeUserCategory category) {
        //TODO: Delete all extreme users before deleting category?
        extremeUserCategoryRepository.delete(category);
    }
}
