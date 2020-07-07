package gr.uoi.dthink.controllers;

import gr.uoi.dthink.model.*;
import gr.uoi.dthink.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


// TODO: Add logger with all the actions
@Controller
public class ProjectController {
    UserController userController;
    UserService userService;
    ProjectService projectService;
    StageService stageService;
    ExtremeUserService extremeUserService;
    ExtremeUserCategoryService extremeUserCategoryService;

    public ProjectController(UserController userController, UserService userService, ProjectService projectService,
                             StageService stageService, ExtremeUserService extremeUserService,
                             ExtremeUserCategoryService extremeUserCategoryService) {
        this.userController = userController;
        this.userService = userService;
        this.projectService = projectService;
        this.stageService = stageService;
        this.extremeUserService = extremeUserService;
        this.extremeUserCategoryService = extremeUserCategoryService;
    }

    @GetMapping("/project/view/{id}")
    public String viewProject(@PathVariable("id") int projectId, Model model) {
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";
        model.addAttribute("project", project);
        boolean isManager = userService.getLoggedInUser().getId() == project.getManager().getId();
        model.addAttribute("isManager", isManager);
        List<User> users = userService.findAllButMembers(project);
        model.addAttribute("usersNotInProject", users);
        String status = project.getCurrentStage().getStatus().getView();
        return "project/views/"+status;
    }

    @GetMapping("/admin/project/{pid}/member/remove/{id}")
    public String removeMember(@PathVariable("pid") int projectId, @PathVariable("id") long userId) {
        Project project = this.projectService.findById(projectId);
        User user = this.userService.findById(userId);
        if (project == null || user == null)
            return "error/404";
        project.removeMember(user);
        projectService.save(project);
        user.removeProject(project);
        userService.save(user);
        return "redirect:/project/view/"+project.getId();
    }


    @GetMapping("/admin/project/{pid}/nextStage")
    public String nextStage(@PathVariable("pid") int projectId) {
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";
        projectService.nextStage(project);
        return "redirect:/project/view/"+project.getId();
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

    @PostMapping("/admin/project/{id}/addCategory")
    public String addCategory(@PathVariable("id") int projectId,
                              @RequestParam(value = "category" , required = false)ExtremeUserCategory category) {
        Project project = projectService.findById(projectId);
        if (project == null)
            return "error/404";

        if(category != null) {
            category.setProject(project);
            System.out.println("Adding extreme user category " + category);
            this.extremeUserCategoryService.save(category);
            project.addCategory(category);
            projectService.save(project);
        }
        return "redirect:/project/view/" + project.getId();
    }

    @PostMapping("/admin/project/{id}/addMembers")
    public String addMembers(@PathVariable("id") int projectId,
                             @RequestParam(value = "members" , required = false) int[] members) {
        Project project = projectService.findById(projectId);
        if (project == null)
            return "error/404";

        if(members != null) {
            for(int member : members) {
                User user = userService.findById(member);
                System.out.println("Adding user " + user);
                project.addMember(user);
                projectService.save(project);
                user.addProject(project);
                userService.save(user);
            }
        }
        return "redirect:/project/view/" + project.getId();
    }

    @GetMapping("/admin/update/project/{id}")
    public String updateProject(@PathVariable("id") int projectId, Model model) {
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";

        model.addAttribute("proj", project);
        List<User> users = userService.findAllButMe();
        model.addAttribute("users", users);
        return "project/update";
    }

    @GetMapping("/admin/delete/project/{id}")
    public String deleteProject(@PathVariable("id") int projectId) {
        Project project = this.projectService.findById(projectId);
        this.projectService.delete(project);
        return "redirect:/dashboard";
    }

    @Transactional
    @PostMapping("/admin/project/update")
    public String updateProject(@ModelAttribute("proj") @Valid Project proj, BindingResult bindingRes,
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
    public String saveProject(@ModelAttribute("projectNew") @Valid Project projectNew, BindingResult bindingRes,
                              Model model) {
        if (!bindingRes.hasErrors()) {
            User thisUser = userService.getLoggedInUser();
            projectNew.setManager(thisUser);
            projectNew.addMember(thisUser);
            //Adding all the users selected in the form
            for(User member: projectNew.getMembers()){
                projectNew.addMember(member);
            }
            Stage stage = new Stage(Status.CHALLENGE_DEFINITION);
            stageService.save(stage);
            projectNew.setChallengeDefinition(stage);
            projectNew.setCurrentStage(stage);
            projectService.save(projectNew);
            for(User member: projectNew.getMembers()){
                member.addProject(projectNew);
                userService.save(member);
            }
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
