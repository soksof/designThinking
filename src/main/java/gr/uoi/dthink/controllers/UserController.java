package gr.uoi.dthink.controllers;

import com.google.common.io.ByteStreams;
import gr.uoi.dthink.model.*;
import gr.uoi.dthink.repos.LearningResourceRepository;
import gr.uoi.dthink.repos.UserRepository;
import gr.uoi.dthink.services.ProjectService;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class UserController {
    private final String LR_DIR = "uploads/LearningResources/";
    private final UserService userService;
    private final ProjectService projectService;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserRoleService userRoleService;
    private final LearningResourceRepository learningResourceRepository;
    private final UserRepository userRepository;

    public UserController(UserService userService, SecurityService securityService, UserValidator userValidator,
                          UserRoleService userRoleService, LearningResourceRepository learningResourceRepository,
                          ProjectService projectService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userRoleService = userRoleService;
        this.projectService = projectService;
        this.learningResourceRepository = learningResourceRepository;
        this.userRepository = userRepository;
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
        List<Project> managedProjects = projectService.findByManager(userService.getLoggedInUser());
        List<Project> projects = projectService.findByMembers(userService.getLoggedInUser());
        projects.removeAll(managedProjects);
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
        model.addAttribute("userNew", user);
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
    public String saveUser(@ModelAttribute("userNew") @Valid User userNew, BindingResult bindingRes) {
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

    @GetMapping(value = "/user/{id}/avatar")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable("id") long id) throws IOException {
        User user = userService.findById(id);
        if(user == null)
            return new ResponseEntity<byte[]>(null, null, HttpStatus.NOT_FOUND);
        String img = "/img/avatars/user_"+user.getId()+".png";
        ClassPathResource resource = new ClassPathResource("static"+img);
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
        List<Integer> indexes = new ArrayList<>();
        int size = learningResources.size();
        int x = 0;
        int[] ind = {1, 2, 3, 4, 5};
        while(x < size){
            if(x >= 5)
                indexes.add(ind[x%5]);
            else
                indexes.add(ind[x]);
            x += 1;
        }
        model.addAttribute("indexes", indexes);
        return "learningHub";
    }

    @GetMapping("/admin/addLearningMaterial/pdf")
    public String addLearningMaterialPDF(Model model) {
        return "addLearningMaterial_pdf";
    }

    @GetMapping("/admin/addLearningMaterial/url")
    public String addLearningMaterialURL(Model model) {
        return "addLearningMaterial_url";
    }

    @PostMapping("/admin/learningMaterial/upload/url")
    public String uploadFile(@RequestParam("url") String url, @RequestParam("descr") String description,
                             @RequestParam("name") String name, @RequestParam("type") String type, RedirectAttributes attributes) {
        if(!isValidURL(url)){
            attributes.addFlashAttribute("message", "Η διεύθυνση του υλικού εκπαίδευσης δεν είναι σωστή!");
            System.out.println("LOG: URL UPLOAD: The url is not valid!");
            return "redirect:/admin/addLearningMaterial/url";
        }
        if(learningResourceRepository.existsByName(name)){
            attributes.addFlashAttribute("message", "Υπάρχει ήδη υλικό εκπαίδευσης στο σύστημα με το όνομα αυτό!");
            System.out.println("LOG: Learning Material UPLOAD: A learning material with the same name exists!");
            return "redirect:/admin/addLearningMaterial/url";
        }

        // save the new learning resource
        System.out.println("LOG: FILE UPLOAD: Uploading url: "+url);

        LearningResourceType lrType = LearningResourceType.VIDEO;
        if(type.toLowerCase().compareTo("url") == 0){
            lrType = LearningResourceType.URL;
        }
        LearningResource newLR = new LearningResource(name, description, userService.getLoggedInUser(), lrType);
        newLR.setContent(url);
        learningResourceRepository.save(newLR);
        // return success response
        attributes.addFlashAttribute("message", "Το υλικό με τίτλο " + name + " ανέβηκε με επιτυχία!");
        return "redirect:/learningHub";

    }

    boolean isValidURL(String url){
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException|URISyntaxException  e) {
            return false;
        }
    }

    @PostMapping("/admin/learningMaterial/upload/pdf")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("descr") String description,
                             @RequestParam("name") String name, RedirectAttributes attributes) {
        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        // Check if there is already a file with the same name
        if(learningResourceRepository.existsByName(fileName)){
            attributes.addFlashAttribute("message", "Υπάρχει ήδη υλικό εκπαίδευσης στο σύστημα με το όνομα αυτό!");
            System.out.println("LOG: FILE UPLOAD: A file with the same name exists!");
            return "redirect:/admin/addLearningMaterial/pdf";
        }
        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Παρακαλώ επιλέξτε αρχείο!");
            return "redirect:/admin/addLearningMaterial/pdf";
        }
        // save the file on the local file system and store the new learning resource
        try {
            Path path = Paths.get(this.LR_DIR + fileName);
            System.out.println("LOG: FILE UPLOAD: Uploading file at: "+path.toString());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            String type = fileName.substring(fileName.lastIndexOf(".")+1);
            LearningResourceType lrType = LearningResourceType.PDF;
            if(type.toLowerCase() == "pdf" || type.toLowerCase() == "doc" || type.toLowerCase() == "docx" || type.toLowerCase() == "txt"){
                lrType = LearningResourceType.PDF;
            }
            LearningResource newLR = new LearningResource(name, description, userService.getLoggedInUser(), lrType);
            newLR.setContent(fileName);
            learningResourceRepository.save(newLR);
        } catch (IOException e) {
            e.printStackTrace();
            attributes.addFlashAttribute("message", "Παρουσιάστηκε πρόβλημα κατά την αποθήκευση του αρχείου στο server!");

            return "redirect:/admin/addLearningMaterial/pdf";
        }
        // return success response
        attributes.addFlashAttribute("message", "Το αρχείο " + fileName + " ανέβηκε με επιτυχία!");
        return "redirect:/learningHub";
    }

    public org.springframework.core.io.Resource loadFile(LearningResource learningResource) {
        try {
            Path file = Paths.get(this.LR_DIR + learningResource.getContent());
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
