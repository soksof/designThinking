package gr.uoi.dthink.controllers;

import com.google.common.io.ByteStreams;
import gr.uoi.dthink.model.*;
import gr.uoi.dthink.services.SecurityService;
import gr.uoi.dthink.services.UserRoleService;
import gr.uoi.dthink.services.UserService;
import gr.uoi.dthink.validators.UserValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {
    private final UserService userService;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserRoleService userRoleService;

    public UserController(UserService userService, SecurityService securityService, UserValidator userValidator,
                          UserRoleService userRoleService) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userRoleService = userRoleService;
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

    public Model addUserProjectsToModel(Model model){
        Set<Project> userProjects = userService.findAllProjects();

        List<Project> projects = new ArrayList<>();
        List<Project> managedProjects = new ArrayList<>();
        String email = userService.getLoggedInUserName();
        for(Project project : userProjects){
            if(project.getManager().getEmail().equals(email))
                managedProjects.add(project);
            else
                projects.add(project);
        }
        System.out.println("1) "+managedProjects.size());
        System.out.println("2) "+projects.size());
        model.addAttribute("managedProjects", managedProjects);
        model.addAttribute("projects", projects);
        return model;
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        model = addUserProjectsToModel(model);
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        User user = userService.getLoggedInUser();
        model.addAttribute("userNew", user);
        List<UserRole> userRoles = userRoleService.findAll();
        model.addAttribute("userRoles", userRoles);
        return "user/profile";
    }

    @GetMapping("/admin/user/view/{id}")
    public String updateUser(@PathVariable("id") int userId, Model model) {
        User user = this.userService.findById(userId);
        if (user == null)
            return "error/404";
        model.addAttribute("user", user);
        List<UserRole> userRoles = userRoleService.findAll();
        model.addAttribute("userRoles", userRoles);
        return "user/profile";
    }

    @Transactional
    @PostMapping("/user/update")
    public String updateProject(@ModelAttribute("user") @Valid User user, BindingResult bindingRes,
                                Model model) {
        if(!bindingRes.hasErrors()) {
            User currentUser = userService.getLoggedInUser();
            user.setId(currentUser.getId());
            userService.update(user);
            return "redirect:/dashboard";
        }
        else{
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            return "user/profile";
        }
    }

    @GetMapping("/admin/user/all")
    public String allUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user/userList";
    }

    @GetMapping("/admin/user/new")
    public String newUser(Model model) {
        model.addAttribute("userNew", new User());
        List<UserRole> userRoles = userRoleService.findAll();
        model.addAttribute("userRoles", userRoles);
        return "user/new";
    }

    @Transactional
    @PostMapping("/admin/user/new")
    public String saveUser(@ModelAttribute("userNew") @Valid User userNew, BindingResult bindingRes,
                              Model model) {
        if (!bindingRes.hasErrors()) {
            userService.save(userNew);
            return "redirect:/dashboard";
        }
        else{
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            return "user/new";
        }
    }

    @GetMapping(value = "/user/avatar")
    public ResponseEntity<byte[]> getAvatar() throws IOException {
        ClassPathResource resource = new ClassPathResource("static"+userService.getLoggedInUserAvatar());
        byte[] image = ByteStreams.toByteArray(resource.getInputStream());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
