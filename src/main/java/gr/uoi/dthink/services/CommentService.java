package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Comment;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;

import java.util.List;

public interface CommentService {
    Comment findById(long id);
    List<Comment> findByUser(User user);
    List<Comment> findByProject(Project project);
    List<Comment> findByProjectAndUser(Project project, User user);
    Comment save(Comment comment);
    void delete(Comment comment);
}
