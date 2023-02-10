package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Finding;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.repos.FindingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindingServiceImpl implements FindingService {
    private final FindingRepository findingRepository;

    public FindingServiceImpl(FindingRepository findingRepository) {
        this.findingRepository = findingRepository;
    }

    @Override
    public Finding findById(long id) {
        return findingRepository.findById(id).orElse(null);
    }

    @Override
    public List<Finding> findByUser(User user) {
        return findingRepository.findByUser(user);
    }

    @Override
    public List<Finding> findByProject(Project project) {
        return findingRepository.findByProject(project);
    }

    @Override
    public List<Finding> findByProjectAndUser(Project project, User user) {
        return findingRepository.findByProjectAndUser(project, user);
    }

    @Override
    public Finding save(Finding comment) {
        return findingRepository.save(comment);
    }

    @Override
    public void delete(Finding comment) {
        this.findingRepository.delete(comment);
    }
}
