package gr.uoi.dthink.services;

import gr.uoi.dthink.model.FileResource;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;

import java.util.List;

public interface FileResourceService {
    FileResource findByNameAndProject(String name, Project project);
    FileResource findByNameAndProjectId(String name, int projectId);
    List<FileResource> findByProject(Project project);
    List<FileResource> findByProjectId(int projectId);
    FileResource findById(long id);
    List<FileResource> findByUser(User user);
    FileResource save(FileResource fileResource);
    void delete(FileResource fileResource);
    Boolean fileNameExistsInProject(String name, Project project);
}
