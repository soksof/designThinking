package gr.uoi.dthink.services;

import gr.uoi.dthink.model.FileResource;
import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.repos.FileResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileResourceServiceImpl implements FileResourceService{
    FileResourceRepository fileResourceRepository;

    public FileResourceServiceImpl(FileResourceRepository fileResourceRepository){
        this.fileResourceRepository = fileResourceRepository;
    }

    @Override
    public FileResource findByNameAndProject(String name, Project project) {
        return fileResourceRepository.findByFileNameAndProject(name, project);
    }

    @Override
    public FileResource findByNameAndProjectId(String name, int projectId) {
        return fileResourceRepository.findByFileNameAndProject_Id(name, projectId);
    }

    @Override
    public List<FileResource> findByProject(Project project) {
        return fileResourceRepository.findAllByProject(project);
    }

    @Override
    public List<FileResource> findByProjectId(int projectId) {
        return fileResourceRepository.findAllByProject_Id(projectId);
    }

    @Override
    public FileResource findById(long id) {
        return fileResourceRepository.findById(id).orElse(null);
    }

    @Override
    public List<FileResource> findByUser(User user) {
        return fileResourceRepository.findAllByUser(user);
    }

    @Override
    public FileResource save(FileResource fileResource) {
        return fileResourceRepository.save(fileResource);
    }

    @Override
    public void delete(FileResource fileResource) {
        fileResourceRepository.delete(fileResource);
    }

    @Override
    public Boolean fileNameExistsInProject(String name, Project project) {
        return fileResourceRepository.existsByFileNameAndProject(name, project);
    }
}
