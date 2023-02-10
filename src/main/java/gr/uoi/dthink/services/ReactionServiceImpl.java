package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.Reaction;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.repos.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;

    public ReactionServiceImpl(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    @Override
    public List<Reaction> findByUser(User user) {
        return reactionRepository.findByUser(user);
    }

    @Override
    public Reaction save(Reaction reaction) {
        return this.reactionRepository.save(reaction);
    }

//    @Override
//    public List<Reaction> findByProject(Project project) {
//        return reactionRepository.findByProject(project);
//    }
}
