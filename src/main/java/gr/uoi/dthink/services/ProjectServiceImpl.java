package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.Stage;
import gr.uoi.dthink.model.Status;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.repos.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService{
    private final ProjectRepository projectRepository;
    private final StageService stageService;

    public ProjectServiceImpl(ProjectRepository projectRepository, StageService stageService) {
        this.projectRepository = projectRepository;
        this.stageService = stageService;
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
    public Project findById(int id) {
        return this.projectRepository.findById(id).orElse(null);
    }

    @Override
    public Project save(Project project) {
        return this.projectRepository.save(project);
    }

    @Override
    public Project nextStage(Project project) {
        if(project.getCurrentStage()==null){
            // This is the beginning of the project
            Stage challengeDefinition = new Stage(Status.CHALLENGE_DEFINITION);
            stageService.save(challengeDefinition);
            project.setCurrentStage(challengeDefinition);
            projectRepository.save(project);
        }
        else {
            Status status = project.getCurrentStage().getStatus();
            if (status.equals(Status.CHALLENGE_DEFINITION)) {
                Stage challengeDefinition = project.getChallengeDefinition();
                challengeDefinition.setEndDate(new Date());
                stageService.save(challengeDefinition);
                if(project.getResourceCollection()!= null) {
                    Stage resourceCollection = new Stage(Status.RESOURCE_COLLECTION);
                    stageService.save(resourceCollection);
                    project.setResourceCollection(resourceCollection);
                }
                project.setCurrentStage(project.getResourceCollection());
                projectRepository.save(project);
            } else if (status.equals(Status.RESOURCE_COLLECTION)) {
                Stage resourceCollection = project.getResourceCollection();
                resourceCollection.setEndDate(new Date());
                stageService.save(resourceCollection);
                if(project.getFindingsCollection()!= null) {
                    Stage findingsCollection = new Stage(Status.FINDINGS_COLLECTION);
                    stageService.save(findingsCollection);
                    project.setFindingsCollection(findingsCollection);
                }
                project.setCurrentStage(project.getFindingsCollection());
                projectRepository.save(project);
            } else if (status.equals(Status.FINDINGS_COLLECTION)) {
                Stage findingsCollection = project.getFindingsCollection();
                findingsCollection.setEndDate(new Date());
                stageService.save(findingsCollection);
                if(project.getIdeaCreation()!= null) {
                    Stage ideaCreation = new Stage(Status.IDEA_CREATION);
                    stageService.save(ideaCreation);
                    project.setIdeaCreation(ideaCreation);
                }
                project.setCurrentStage(project.getIdeaCreation());
                projectRepository.save(project);
            } else if (status.equals(Status.IDEA_CREATION)) {
                Stage ideaCreation = project.getIdeaCreation();
                ideaCreation.setEndDate(new Date());
                stageService.save(ideaCreation);
                if(project.getPrototypeCreation()!= null) {
                    Stage prototypeCreation = new Stage(Status.PROTOTYPE_CREATION);
                    stageService.save(prototypeCreation);
                    project.setPrototypeCreation(prototypeCreation);
                }
                project.setCurrentStage(project.getPrototypeCreation());
                projectRepository.save(project);
            } else if (status.equals(Status.PROTOTYPE_CREATION)) {
                Stage prototypeCreation = project.getPrototypeCreation();
                prototypeCreation.setEndDate(new Date());
                stageService.save(prototypeCreation);
                if(project.getCompletedProject()!= null) {
                    Stage completedProject = new Stage(Status.COMPLETED);
                    completedProject.setEndDate(new Date());
                    stageService.save(completedProject);
                    project.setCompletedProject(completedProject);
                }
                project.setCurrentStage(project.getCompletedProject());
                projectRepository.save(project);
            }
        }
        return project;
    }

    @Override
    public Project previousStage(Project project) {
        if(project.getCurrentStage()!=null) {
            Status status = project.getCurrentStage().getStatus();
            if (status.equals(Status.PROTOTYPE_CREATION)) {
                project.setCurrentStage(project.getIdeaCreation());
                projectRepository.save(project);
            } else if (status.equals(Status.IDEA_CREATION)) {
                project.setCurrentStage(project.getFindingsCollection());
                projectRepository.save(project);
            } else if (status.equals(Status.FINDINGS_COLLECTION)) {
                project.setCurrentStage(project.getResourceCollection());
                projectRepository.save(project);
            } else if (status.equals(Status.RESOURCE_COLLECTION)) {
                project.setCurrentStage(project.getChallengeDefinition());
                projectRepository.save(project);
            }
        }
        return project;
    }

    @Override
    public void delete(Project project) {
        this.projectRepository.delete(project);
    }
}
