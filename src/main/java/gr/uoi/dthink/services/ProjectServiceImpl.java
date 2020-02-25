package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.repos.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService{
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Project> findByManager(User manager) {
        return projectRepository.findByManager(manager);
    }

    @Override
    public List<Project> findByMembers(User user) {
        return projectRepository.findByMembers(user);
    }

    @Override
    public Project save(Project project) {
        return null;
    }
}
