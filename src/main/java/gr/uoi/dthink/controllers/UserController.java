package gr.uoi.dthink.controllers;

import com.google.common.io.ByteStreams;
import gr.uoi.dthink.model.*;
import gr.uoi.dthink.repos.LearningResourceRepository;
import gr.uoi.dthink.services.SecurityService;
import gr.uoi.dthink.services.UserRoleService;
import gr.uoi.dthink.services.UserService;
import gr.uoi.dthink.validators.UserValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {
    private final String LR_DIR = "uploads/LearningResources/";
    private final UserService userService;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserRoleService userRoleService;
    private final LearningResourceRepository learningResourceRepository;

    public UserController(UserService userService, SecurityService securityService, UserValidator userValidator,
                          UserRoleService userRoleService, LearningResourceRepository learningResourceRepository) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userRoleService = userRoleService;
        this.learningResourceRepository = learningResourceRepository;
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
        String currentRole = userService.getLoggedInUser().getRole().getName();
        model.addAttribute("currentRole", currentRole);
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
    public String updateProject(@ModelAttribute("user") @Valid User user, BindingResult bindingRes, Model model) {
        User currentUser = userService.getLoggedInUser();
        if(!bindingRes.hasErrors()) {
            //This is an admin editing the user
            if(currentUser.getId() != user.getId()){
                //Another admin wants to change the user role, name, or email
                if((user.getPassword().compareTo(user.getPasswordConfirm())==0) && (user.getPassword().length() >= 8)){
                    User initUser = userService.findById(user.getId());
                    initUser.setRole(user.getRole());
                    initUser.setName(user.getName());
                    initUser.setEmail(user.getEmail());
                    initUser.setLastName(user.getLastName());
                    initUser.setPassword(user.getPassword());
                    userService.update(initUser);
                }
            }
            else {
                userService.update(user);
            }
        }
        else {
            for (ObjectError error : bindingRes.getAllErrors()) {
                System.out.println("USER UPDATE ERROR: " + error.getDefaultMessage());
            }
            //model.addAttribute("userNew", user);
            List<UserRole> userRoles = userRoleService.findAll();
            model.addAttribute("userRoles", userRoles);
            String currentRole = userService.getLoggedInUser().getRole().getName();
            model.addAttribute("currentRole", currentRole);
            return "user/profile";
        }
        return "redirect:/dashboard";
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
        byte[] image;
        try {
            image = ByteStreams.toByteArray(resource.getInputStream());
        }catch(java.io.FileNotFoundException e){
            resource = new ClassPathResource("static/img/avatars/default.png");
            image = ByteStreams.toByteArray(resource.getInputStream());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @GetMapping("/learningHub")
    public String learningHub(Model model) {
        List<LearningResource> learningResources = this.learningResourceRepository.findAll();
        model.addAttribute("learningResources", learningResources);
        return "learningHub";
    }

    public org.springframework.core.io.Resource loadFile(LearningResource learningResource) {
        try {
            Path file = Paths.get(LR_DIR + learningResource.getContent());
            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
                throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error! -> message = " + e.getMessage());
        }
    }


    @GetMapping(value = "/learningResource/{id}")
    public ResponseEntity getLearningResource(@PathVariable("id") int id) throws IOException {
        LearningResource resource = this.learningResourceRepository.findById(id).orElse(null);
        if (resource == null)
            return new ResponseEntity<byte[]>(null, null, HttpStatus.NOT_FOUND);
        ResponseEntity<byte[]> response = null;

        switch (resource.getType()){
            case PDF:
                org.springframework.core.io.Resource file = loadFile(resource);
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                        "filename=\"" + file.getFilename() + "\"").body(file);
            case URL:
                break;
            case VIDEO:
                break;
        }
        return response;
    }

//    public ResponseEntity<byte[]> getLearningResource(@PathVariable("id") int id) throws IOException {
//        LearningResource resource = this.learningResourceRepository.findById(id).orElse(null);
//        if (resource == null)
//            return new ResponseEntity<byte[]>(null, null, HttpStatus.NOT_FOUND);
//        ResponseEntity<byte[]> response = null;
//
//        switch (resource.getType()){
//            case PDF:
//                Path pdfPath = Paths.get(LR_DIR+"\\"+resource.getContent());
//                System.out.println(pdfPath.toAbsolutePath());
//                byte[] contents;
//                try {
//                    contents = Files.readAllBytes(pdfPath);
//                }catch(java.io.FileNotFoundException e){
//                    return new ResponseEntity<byte[]>(null, null, HttpStatus.NOT_FOUND);
//                }
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_PDF);
////                headers.setContentDispositionFormData("inline", resource.getContent());
//                headers.add("Content-Disposition", "inline;filename=" + resource.getContent());
//                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//                response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
//                break;
//            case URL:
//                break;
//            case VIDEO:
//                break;
//        }
//        return response;
//    }
}
