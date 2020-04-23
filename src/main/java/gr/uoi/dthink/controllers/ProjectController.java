package gr.uoi.dthink.controllers;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.Stage;
import gr.uoi.dthink.model.Status;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.services.ProjectService;
import gr.uoi.dthink.services.StageService;
import gr.uoi.dthink.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;


@Controller
public class ProjectController {
    UserController userController;
    UserService userService;
    ProjectService projectService;
    StageService stageService;

    public ProjectController(UserController userController, UserService userService, ProjectService projectService,
                             StageService stageService) {
        this.userController = userController;
        this.userService = userService;
        this.projectService = projectService;
        this.stageService = stageService;
    }

    @GetMapping("/project/view/{id}")
    public String viewProject(@PathVariable("id") int projectId, Model model) {
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";
        model.addAttribute("project", project);
        return "project/dashboard";
    }

    @GetMapping("/project/all")
    public String allUserProjects(Model model) {
        model = userController.addUserProjectsToModel(model);
        return "project/projectList";
    }

    @GetMapping("/admin/project/new")
    public String newProject(Model model) {
        model.addAttribute("projectNew", new Project());
        List<User> users = userService.findAllButMe();
        model.addAttribute("users", users);
        return "project/new";
    }

    @GetMapping("/admin/update/project/{id}")
    public String updateProject(@PathVariable("id") int projectId, Model model) {
        Project project = this.projectService.findById(projectId);
        model.addAttribute("proj", project);
        List<User> users = userService.findAllButMe();
        model.addAttribute("users", users);
        return "project/update";
    }

    @GetMapping("/admin/delete/project/{id}")
    public String deleteProject(@PathVariable("id") int projectId, Model model) {
        Project project = this.projectService.findById(projectId);
        this.projectService.delete(project);
        return "redirect:/dashboard";
    }

    @Transactional
    @PostMapping("/admin/project/update")
    public String updateproject(@ModelAttribute("proj") @Valid Project proj, BindingResult bindingRes,
                              Model model) {
        if(!bindingRes.hasErrors()) {
            projectService.save(proj);
            return "redirect:/project/view/" + proj.getId();
        }
        else{
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            System.out.println(">>>>"+proj.getEndDate());
            List<User> users = userService.findAllButMe();
            model.addAttribute("users", users);
            return "project/update";
        }
    }

    @Transactional
    @PostMapping("/admin/project/new")
    public String saveproject(@ModelAttribute("projectNew") @Valid Project projectNew, BindingResult bindingRes,
                              Model model) {
        if (!bindingRes.hasErrors()) {
            User thisUser = userService.getLoggedInUser();
            projectNew.setManager(thisUser);
            projectNew.addMember(thisUser);
            Stage stage = new Stage(Status.CHALLENGE_DEFINITION);
            stageService.save(stage);
            projectNew.setCurrentStage(stage);
            projectService.save(projectNew);
            thisUser.addProject(projectNew);
            userService.save(thisUser);
            return "redirect:/project/view/" + projectNew.getId();
        }
        else{
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            System.out.println(">>>>"+projectNew.getEndDate());
            List<User> users = userService.findAllButMe();
            model.addAttribute("users", users);
            return "project/new";
        }
    }
}