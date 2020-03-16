package gr.uoi.dthink.controllers;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ProjectController {
    UserController userController;
    UserService userService;

    public ProjectController(UserController userController, UserService userService) {
        this.userController = userController;
        this.userService = userService;
    }

    @GetMapping("/projectList")
    public String allUserProjects(Model model){
        model = userController.addUserProjectsToModel(model);
        return "project/projectList";
    }

    @GetMapping("/newProject")
    public String newProject(Model model){
        return "project/newProject";
    }
}
