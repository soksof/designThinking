package gr.uoi.dthink.repos;

import gr.uoi.dthink.model.FileResource;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileResourceRepository extends JpaRepository<FileResource, Long> {
    List<FileResource> findAllByFileName(String fileName);
    List<FileResource> findAllByProject(Project project);
    List<FileResource> findAllByProject_Id(int id);
    List<FileResource> findAllByUser(User user);
    FileResource findByFileNameAndProject(String fileName, Project Project);
    FileResource findByFileNameAndProject_Id(String fileName, int id);
    Boolean existsByFileNameAndProject(String name, Project project);
}
