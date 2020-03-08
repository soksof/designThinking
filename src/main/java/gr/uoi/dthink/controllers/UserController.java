package gr.uoi.dthink.controllers;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.services.SecurityService;
import gr.uoi.dthink.services.UserService;
import gr.uoi.dthink.validators.UserValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {
    private final UserService userService;
    private final SecurityService securityService;
    private final UserValidator userValidator;

    public UserController(UserService userService, SecurityService securityService, UserValidator userValidator) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        userService.save(userForm);

        securityService.autoLogin(userForm.getEmail(), userForm.getPasswordConfirm());
        return "redirect:/index";
    }

    @RequestMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Αποτυχία εισαγωγής! Παρακαλούμε έλεγξε τα στοιχεία σου.");

        if (logout != null)
            model.addAttribute("message", "Έχετε αποσυνδεθεί με επιτυχία.");
        return "login";
    }

    @GetMapping({"/", "/index"})
    public String welcome(Model model) {
        return this.getDashboard(model);
    }

    @GetMapping("/passForgot")
    public String forgotPassword() {
        return "passForgot";
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        Set<Project> userProjects = userService.findAllProjects();
        List<Project> projects = new ArrayList<>();
        List<Project> managedProjects = new ArrayList<>();
        String email = userService.getLoggedInUserName();
        for(Project project : userProjects){
            System.out.println(">>>"+project);
            if(project.getManager().getEmail().equals(email))
                managedProjects.add(project);
            else
                projects.add(project);
        }
        model.addAttribute("managedProjects", managedProjects);
        model.addAttribute("projects", projects);
        return "user/dashboard";
    }
}
