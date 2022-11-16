package gr.uoi.dthink.controllers;

import gr.ilsp.extractor.Extractor;
import gr.ilsp.utils.ContentNormalizer;
import gr.ilsp.utils.XMLExporter;
import gr.uoi.dthink.model.*;
import gr.uoi.dthink.services.*;
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

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.apache.commons.io.FileUtils;

// TODO: Add logger with all the actions
@Controller
public class ProjectController {
    private final String UPLOAD_DIR = "uploads/";
    UserController userController;
    UserService userService;
    ProjectService projectService;
    StageService stageService;
    ExtremeUserCategoryService extremeUserCategoryService;
    FileResourceService fileResourceService;
    ResourceTypeService resourceTypeService;
    CommentService commentService;
    EmpathyMapService empathyMapService;

    public ProjectController(UserController userController, UserService userService, ProjectService projectService,
                             StageService stageService, ExtremeUserCategoryService extremeUserCategoryService,
                             FileResourceService fileResourceService, ResourceTypeService resourceTypeService,
                             CommentService commentService, EmpathyMapService empathyMapService) {
        this.userController = userController;
        this.userService = userService;
        this.projectService = projectService;
        this.stageService = stageService;
        this.extremeUserCategoryService = extremeUserCategoryService;
        this.fileResourceService = fileResourceService;
        this.resourceTypeService = resourceTypeService;
        this.commentService = commentService;
        this.empathyMapService = empathyMapService;
    }

    @GetMapping("/project/view/{id}")
    public String viewProject(@PathVariable("id") int projectId, Model model) {
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";
        model.addAttribute("project", project);
        boolean isManager = userService.getLoggedInUser().getId() == project.getManager().getId();
        model.addAttribute("isManager", isManager);
        long uid = userService.getLoggedInUser().getId();
        model.addAttribute("userId", uid);
        List<User> users = userService.findAllButMembers(project);
        model.addAttribute("usersNotInProject", users);
        String status = project.getCurrentStage().getStatus().getView();
        if(status.compareTo("challengeDefinition")==0) {
            model.addAttribute("newFileResource", new FileResource());
        }
        return "project/views/"+status;
    }

    @GetMapping("/project/{pid}/empathy/note/remove/{id}")
    public String removeEmpathyNote(@PathVariable("pid") int projectId, @PathVariable("id") long noteId) {
        Project project = this.projectService.findById(projectId);
        Comment note = this.commentService.findById(noteId);
        if (project == null || note == null)
            return "error/404";

        EmpathyMap map = project.getEmpathyMap();
        map.removeNote(note);
        this.empathyMapService.save(map);
        this.commentService.delete(note);
        return "redirect:/project/view/"+project.getId();
    }

    @GetMapping("/project/{pid}/wordCloud")
    public void generateWordCloud(@PathVariable("pid") int projectId){

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

    @GetMapping("/admin/project/{pid}/resource/remove/{rid}")
    public String removeFileResource(@PathVariable("pid") int projectId, @PathVariable("rid") long resourceId) {
        System.out.println(">>>>> Went in here");
        Project project = this.projectService.findById(projectId);
        FileResource resource = this.fileResourceService.findById(resourceId);
        if (project == null || resource == null) {
            System.out.println(project+">>>>>> "+resource);
            return "error/404";
        }
        project.removeFileResource(resource);
        //Also delete the resource's file from the server
        File resFile = new File(UPLOAD_DIR.concat("p"+projectId)+System.getProperty("file.separator")+resource.getFileName());
        if (resFile.delete())
            System.out.println("Deleted the file: " + resource.getFileName());
        else
            System.out.println("Failed to delete the file: "+resource.getFileName());
        projectService.save(project);
        return "redirect:/project/view/"+project.getId();
    }

    @GetMapping("/admin/project/{pid}/cat/remove/{id}")
    public String removeCategory(@PathVariable("pid") int projectId, @PathVariable("id") int catId) {
        Project project = this.projectService.findById(projectId);
        ExtremeUserCategory category = this.extremeUserCategoryService.findById(catId);
        if (project == null || category == null)
            return "error/404";
        project.removeExtremeUserCategory(category);
        projectService.save(project);
        extremeUserCategoryService.delete(category);
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

    @PostMapping("/project/{id}/addCategory")
    public String addCategory(@PathVariable("id") int projectId,
                              @RequestParam(value = "name" , required = true)String name,
                              @RequestParam(value = "description" , required = true)String description) {
        Project project = projectService.findById(projectId);
        if (project == null)
            return "error/404";

        if(name.strip().compareTo("") != 0) {
            ExtremeUserCategory category = new ExtremeUserCategory();
            category.setName(name);
            category.setDescription(description.strip());
            category.setProject(project);
            System.out.println("Adding extreme user category " + category);
            this.extremeUserCategoryService.save(category);
            project.addCategory(category);
            projectService.save(project);
        }
        return "redirect:/project/view/" + project.getId();
    }

    @PostMapping("/resource/{rid}/addComment")
    public String addComment(@PathVariable("rid") int resourceId,
                              @RequestParam(value = "comment" , required = true)String strComment) {
        FileResource resource = fileResourceService.findById(resourceId);
        if (resource == null)
            return "error/404";

        if(strComment.strip().compareTo("") != 0) {
            Comment comment  = new Comment();
            comment.setDescription(strComment);
            comment.setUser(userService.getLoggedInUser());
            comment.setProject(resource.getProject());
            commentService.save(comment);
            resource.addComment(comment);
            System.out.println("Adding comment " + comment);
            fileResourceService.save(resource);
        }
        return "redirect:/project/view/" + resource.getProject().getId();
    }

    @GetMapping("/project/{id}/resource/new")
    public String newResource(@PathVariable("id") int projectId, Model model) {
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";

        Status status = project.getCurrentStage().getStatus();
        if(!status.equals(Status.RESOURCE_COLLECTION)) {
            return "error/403";
        }
        FileResourceMapper resourceNew = new FileResourceMapper();
        resourceNew.setProject(project);
        model.addAttribute("resourceNew", resourceNew);
        List<ResourceType> fileResourceTypes = resourceTypeService.findAllFileTypes();
        model.addAttribute("resourceTypes", fileResourceTypes);
        return "project/resource/new";
    }

    public String extract(String fileName) throws IOException {
        XMLExporter.DocData data = XMLExporter.initDocData();
        Extractor extr = new Extractor();
        File infile = new File(fileName);
        try {
            data = extr.extract(infile);
            System.out.println(data.format);
            System.out.println(data.sourceFilename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String text = data.content;
        System.out.println("extracting:::"+text);
        text = ContentNormalizer.removeBoilerPars(text);
        String text1 = ContentNormalizer.removeTextTags(text);
        System.out.println("extracting:::"+text1);
        //FileUtils.writeStringToFile(new File(infile.getAbsolutePath()+Constants.EXTENSION_TXT), text1, Constants.UTF8);
        return text1;
    }

    @Transactional
    @PostMapping("/project/{id}/resource/new")
    public String addResource(@PathVariable("id") int projectId,
                              @ModelAttribute("resourceNew") @Valid FileResourceMapper resourceNew,
                              BindingResult bindingRes, Model model){
        if (bindingRes.hasErrors()) {
            System.out.println("ERRORS");
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            return "project/resource/new";
        }
        // check if file is empty
        if (resourceNew.getFile().isEmpty()) {
            return "project/resource/new";
        }
        Project project = projectService.findById(projectId);
        FileResource fileResource = new FileResource(resourceNew);
        fileResource.setProject(project);
        fileResource.setUser(userService.getLoggedInUser());

        // normalize the file path
        String fileName = StringUtils.cleanPath(resourceNew.getFile().getOriginalFilename());
        // save the file on the local file system
        File directory = new File(UPLOAD_DIR.concat("p"+projectId));
        try {
            if (!directory.exists())
                directory.mkdir();

            Path path = Paths.get(directory + "/" + fileName);
            Files.copy(resourceNew.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            fileResource.setContent(this.extract(directory + "/" + fileName));
        } catch (IOException e) {
            System.out.println("ERROR: Creating file from uploaded resource "+fileName);
        }
        //Map the resource to a FileResource instance and persist it
        project.addFileResource(fileResource);
        projectService.save(project);

        return "redirect:/project/view/" + projectId;
    }

    @GetMapping("/resource/download/{resourceId}")
    public ResponseEntity downloadFile(@PathVariable long resourceId) {
        FileResource fileResource = fileResourceService.findById(resourceId);
        if(fileResource==null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        org.springframework.core.io.Resource file = loadFile(fileResource);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                        "filename=\"" + file.getFilename() + "\"").body(file);
    }

    public org.springframework.core.io.Resource loadFile(FileResource fileResource) {
        try {
            Path file = Paths.get(UPLOAD_DIR + "/p" + fileResource.getProject().getId() +
                    "/" + fileResource.getFileName());
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

    @PostMapping("/project/{id}/addSays")
    public String addSays(@PathVariable("id") int projectId,
                          @RequestParam(value = "descriptionSays") String description,
                          @RequestParam(value = "categorySays") int categoryId) {
        boolean result = this.addCommentToMap(projectId, description, categoryId, "says");
        if(!result)
            return "error/404";
        return "redirect:/project/view/" + projectId;
    }

    @PostMapping("/project/{id}/addDoes")
    public String addDo(@PathVariable("id") int projectId,
                          @RequestParam(value = "descriptionDo") String description,
                          @RequestParam(value = "categoryDo") int categoryId) {
        boolean result = this.addCommentToMap(projectId, description, categoryId, "does");
        if(!result)
            return "error/404";
        return "redirect:/project/view/" + projectId;
    }

    @PostMapping("/project/{id}/addThinks")
    public String addThinks(@PathVariable("id") int projectId,
                          @RequestParam(value = "descriptionThink") String description,
                          @RequestParam(value = "categoryThink") int categoryId) {
        boolean result = this.addCommentToMap(projectId, description, categoryId, "thinks");
        if(!result)
            return "error/404";
        return "redirect:/project/view/" + projectId;
    }

    @PostMapping("/project/{id}/addFeels")
    public String addFeels(@PathVariable("id") int projectId,
                          @RequestParam(value = "descriptionFeel") String description,
                          @RequestParam(value = "categoryFeel") int categoryId) {
        boolean result = this.addCommentToMap(projectId, description, categoryId, "feels");
        if(!result)
            return "error/404";
        return "redirect:/project/view/" + projectId;
    }

    public boolean addCommentToMap(int projectId, String description, int categoryId, String pos){
        Project project = projectService.findById(projectId);
        if (project == null)
            return false;

        ExtremeUserCategory category = extremeUserCategoryService.findById(categoryId);
        Comment comment = new Comment();
        comment.setProject(project);
        comment.setDescription(description);
        comment.setCategory(category);
        comment.setUser(userService.getLoggedInUser());
        commentService.save(comment);
        EmpathyMap map = project.getEmpathyMap();
        switch(pos){
            case("says"):
                map.addEmpSay(comment);break;
            case("feels"):
                map.addEmpFeel(comment);break;
            case("thinks"):
                map.addEmpThink(comment);break;
            case("does"):
                map.addEmpDo(comment);break;
        }
        empathyMapService.save(map);
        return true;
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
            EmpathyMap empMap = new EmpathyMap();
            empathyMapService.save(empMap);
            projectNew.setEmpathyMap(empMap);
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

    @GetMapping(value = "/project/{pid}/wordcloud")
    public ResponseEntity<byte[]> getWordCloud(@PathVariable("pid") int pid) throws IOException {

        // normalize the file path
        // save the file on the local file system
        File directory = new File(UPLOAD_DIR.concat("p"+pid));
        Path path = Paths.get(directory + "/" + "word_cloud.png");
        File resource = path.toFile();
        //ClassPathResource resource = new ClassPathResource(this.UPLOAD_DIR+"p"+pid+"/word_cloud.png");
        if(!resource.exists()) {
            return null;
        }
        //byte[] image = ByteStreams.toByteArray(resource.getInputStream());
        byte[] image = FileUtils.readFileToByteArray(resource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
