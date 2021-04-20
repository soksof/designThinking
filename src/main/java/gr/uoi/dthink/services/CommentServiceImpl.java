package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Comment;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.repos.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment findById(long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Comment> findByUser(User user) {
        return commentRepository.findByUser(user);
    }

    @Override
    public List<Comment> findByProject(Project project) {
        return commentRepository.findByProject(project);
    }

    @Override
    public List<Comment> findByProjectAndUser(Project project, User user) {
        return commentRepository.findByProjectAndUser(project, user);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }
}
