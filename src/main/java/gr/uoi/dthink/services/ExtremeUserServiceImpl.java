package gr.uoi.dthink.services;

import gr.uoi.dthink.model.ExtremeUser;
import gr.uoi.dthink.model.ExtremeUserCategory;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.repos.ExtremeUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtremeUserServiceImpl implements ExtremeUserService{
    ExtremeUserRepository extremeUserRepository;

    public ExtremeUserServiceImpl(ExtremeUserRepository extremeUserRepository){
        this.extremeUserRepository = extremeUserRepository;
    }

    @Override
    public ExtremeUser findByEmail(String email) {
        return this.extremeUserRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<ExtremeUser> findByCategory(ExtremeUserCategory extremeUserCategory) {
        return this.extremeUserRepository.findAllByExtremeUserCategory(extremeUserCategory);
    }

    @Override
    public List<ExtremeUser> findByProject(Project project) {
        return this.extremeUserRepository.findAllByExtremeUserCategory_Project(project);
    }

    @Override
    public ExtremeUser save(ExtremeUser extremeUser) {
        return this.extremeUserRepository.save(extremeUser);
    }

    @Override
    public void delete(ExtremeUser extremeUser) {
        this.extremeUserRepository.delete(extremeUser);
    }
}
