package gr.uoi.dthink.controllers;

import com.google.common.io.ByteStreams;
import gr.ilsp.extractor.Extractor;
import gr.ilsp.utils.ContentNormalizer;
import gr.ilsp.utils.XMLExporter;
import gr.uoi.dthink.model.*;
import gr.uoi.dthink.repos.FindingRepository;
import gr.uoi.dthink.repos.IdeaRepository;
import gr.uoi.dthink.repos.UserRepository;
import gr.uoi.dthink.services.*;
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

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;
    private final FindingRepository findingRepository;
    private final ReactionService reactionService;
    private final IdeaRepository ideaRepository;


    public ProjectController(UserController userController, UserService userService, ProjectService projectService,
                             StageService stageService, ExtremeUserCategoryService extremeUserCategoryService,
                             FileResourceService fileResourceService, ResourceTypeService resourceTypeService,
                             CommentService commentService, EmpathyMapService empathyMapService,
                             UserRepository userRepository, ReactionService reactionService,
                             FindingRepository findingRepository, IdeaRepository ideaRepository) {
        this.userController = userController;
        this.userService = userService;
        this.projectService = projectService;
        this.stageService = stageService;
        this.extremeUserCategoryService = extremeUserCategoryService;
        this.fileResourceService = fileResourceService;
        this.resourceTypeService = resourceTypeService;
        this.commentService = commentService;
        this.empathyMapService = empathyMapService;
        this.userRepository = userRepository;
        this.reactionService = reactionService;
        this.findingRepository = findingRepository;
        this.ideaRepository = ideaRepository;
    }

    @GetMapping("/project/view/{id}")
    public String viewProject(@PathVariable("id") int projectId, Model model) {
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";
        model.addAttribute("project", project);
        boolean isManager = userService.getLoggedInUser().getId() == project.getManager().getId();
        boolean haveVisitedNextStage = project.haveVisitedNextStage();
        model.addAttribute("isManager", isManager);
        model.addAttribute("haveVisitedNextStage", haveVisitedNextStage);
        long uid = userService.getLoggedInUser().getId();
        model.addAttribute("userId", uid);
        List<User> users = userService.findAllButMembers(project);
        model.addAttribute("usersNotInProject", users);
        String status = project.getCurrentStage().getStatus().getView();
        if(status.compareTo("challengeDefinition")==0) {
            model.addAttribute("newFileResource", new FileResource());
        }
        else if(status.compareTo("findingsCollection")==0) {
            model.addAttribute("findings", findingRepository.findByProject(project));
        }
        else if(status.compareTo("ideaCreation")==0) {
            model.addAttribute("findings", findingRepository.findByProject(project));
            List<Idea> ideas = ideaRepository.findByProject(project);
            ideas.sort(Comparator.comparing(Idea::getReactionsCount).reversed());
            model.addAttribute("ideas", ideas);
            List<Comment> allComments = ideas.stream().flatMap(idea -> idea.getComments().stream()).collect(Collectors.toList());
            model.addAttribute("comments", allComments);
            long firstCommentUser = -1;
            if(allComments.size()>0)
                firstCommentUser = allComments.get(0).getUserId();
            model.addAttribute("firstCommentUser", firstCommentUser);
        }
        else if(status.compareTo("prototypeCreation")==0) {
            List<Idea> ideas = ideaRepository.findByProject(project);
            ideas.sort(Comparator.comparing(Idea::getReactionsCount).reversed());
            model.addAttribute("ideas", ideas);

            Set<FileResource> prototypeScreens = project.getPrototypeScreenShots();
            model.addAttribute("prototypeScreens", prototypeScreens);
        }
        else if(status.compareTo("completed")==0) {
            int ideasNum = ideaRepository.findByProject(project).size();
            model.addAttribute("ideasNum", ideasNum);
            int findingsNum = findingRepository.findByProject(project).size();
            model.addAttribute("findingsNum", findingsNum);
            model.addAttribute("resourcesNum", project.getFileResources().size());
            model.addAttribute("membersNum", project.getMembers().size());
            model.addAttribute("extremeUsersNum", project.getCategories().size());
            model.addAttribute("days", project.getDurationInDays());
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
    public String generateWordCloud(@PathVariable("pid") int projectId){
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";
        project.generateWordCloud();
        return "";
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

    @GetMapping("/admin/project/{pid}/finding/remove/{id}")
    public String removeFinding(@PathVariable("pid") int projectId, @PathVariable("id") long findingId) {
        Project project = this.projectService.findById(projectId);
        Finding finding = this.findingRepository.findById(findingId).orElse(null);
        if (project == null || finding == null)
            return "error/404";
        project.removeFinding(finding);
        projectService.save(project);
        findingRepository.delete(finding);
        return "redirect:/project/view/"+project.getId();
    }

    @GetMapping("/admin/project/{pid}/idea/remove/{id}")
    public String removeIdea(@PathVariable("pid") int projectId, @PathVariable("id") long ideaId) {
        Project project = this.projectService.findById(projectId);
        Idea idea = this.ideaRepository.findById(ideaId).orElse(null);
        if (project == null || idea == null)
            return "error/404";

        project.removeIdea(idea);
        projectService.save(project);
        ideaRepository.delete(idea);
        return "redirect:/project/view/"+project.getId();
    }

    @GetMapping("/admin/project/{pid}/screenshot/remove/{rid}")
    public String removeScreenshot(@PathVariable("pid") int projectId, @PathVariable("rid") long resourceId) {
        Project project = this.projectService.findById(projectId);
        FileResource resource = this.fileResourceService.findById(resourceId);
        if (project == null || resource == null)
            return "error/404";

        //Also delete the resource's file from the server
        File resFile = new File(UPLOAD_DIR.concat("p"+projectId)+System.getProperty("file.separator")+resource.getFileName());
        try {
            Files.delete(resFile.toPath());
        }
        catch (IOException e) {
            System.out.println(e);
        }
        if (resFile.delete()) {
            System.out.println("Deleted the file: " + resource.getFileName());
            project.removePrototypeScreenShot(resource);
            fileResourceService.delete(resource);
        }
        else
            System.out.println(resFile.exists()+" LOG: Failed to delete file "+resFile.getAbsolutePath());
        projectService.save(project);
        return "redirect:/project/view/"+project.getId();
    }


    @GetMapping("/admin/project/{pid}/resource/remove/{rid}")
    public String removeFileResource(@PathVariable("pid") int projectId, @PathVariable("rid") long resourceId) {
        Project project = this.projectService.findById(projectId);
        FileResource resource = this.fileResourceService.findById(resourceId);
        if (project == null || resource == null)
            return "error/404";

        //Also delete the resource's file from the server
        File resFile = new File(UPLOAD_DIR.concat("p"+projectId)+System.getProperty("file.separator")+resource.getFileName());
        if (resFile.delete()) {
            System.out.println("Deleted the file: " + resource.getFileName());
            project.removeFileResource(resource);
            fileResourceService.delete(resource);
        }
        else
            System.out.println("LOG: Failed to delete file "+resource.getFileName());
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

    @GetMapping("/admin/project/{pid}/previousStage")
    public String previousStage(@PathVariable("pid") int projectId) {
        Project project = this.projectService.findById(projectId);
        if (project == null)
            return "error/404";
        projectService.previousStage(project);
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

    @PostMapping("/resource/{rid}/reaction/add/positive")
    public String addReaction(@PathVariable("rid") int resourceId) {
        FileResource resource = fileResourceService.findById(resourceId);
        if (resource == null)
            return "error/404";
        return "redirect:/project/view/" + resource.getProject().getId();
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

    @PostMapping("/project/{pid}/idea/{iid}/addComment")
    public String addCommentForIdea(@PathVariable("pid") int pid, @PathVariable("iid") long ideaId,
                             @RequestParam(value = "ideaComment" , required = true)String strComment) {
        Project project = projectService.findById(pid);
        Idea idea = ideaRepository.findById(ideaId).orElse(null);
        if(project == null || idea == null)
            return "error/404";
        Status status = project.getCurrentStage().getStatus();
        if(!status.equals(Status.IDEA_CREATION))
            return "error/403";

        if(strComment.strip().compareTo("") != 0) {
            Comment comment  = new Comment();
            comment.setDescription(idea.getTitle()+": "+strComment);
            //comment.setDescription(strComment);
            comment.setUser(userService.getLoggedInUser());
            comment.setProject(project);
            commentService.save(comment);
            idea.addComment(comment);
            ideaRepository.save(idea);
        }
        return "redirect:/project/view/" + pid;
    }

    @GetMapping("/project/idea/{iid}/like")
    public String likeIdea(@PathVariable("iid") long ideaId, Model model) {
        Idea idea = ideaRepository.findById(ideaId).orElse(null);
        if(idea == null)
            return "error/404";
        Project project = idea.getProject();
        Status status = project.getCurrentStage().getStatus();
        if(!status.equals(Status.IDEA_CREATION))
            return "error/403";

        Reaction like = new Reaction();
        like.setLiked(true);
        like.setUser(userService.getLoggedInUser());
        reactionService.save(like);
        idea.addReaction(like);
        ideaRepository.save(idea);
        return "redirect:/project/view/"+project.getId();
    }

    @GetMapping("/project/{id}/finding/{fid}/like")
    public String likeFinding(@PathVariable("id") int pid, @PathVariable("fid") long fid, Model model) {
        Project project = projectService.findById(pid);
        if(project == null)
            return "error/404";
        Status status = project.getCurrentStage().getStatus();
        if(!status.equals(Status.FINDINGS_COLLECTION))
            return "error/403";

        Finding finding = findingRepository.findById(fid).orElse(null);
        if(finding == null)
            return "error/404";

        Reaction like = new Reaction();
        like.setLiked(true);
        like.setUser(userService.getLoggedInUser());
        reactionService.save(like);
        finding.addReaction(like);
        finding.setProject(project);
        findingRepository.save(finding);
        return "redirect:/project/view/" + pid;
    }

    @GetMapping("/project/{id}/idea/new")
    public String newIdea(@PathVariable("id") int projectId, Model model) {
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";
        Status status = project.getCurrentStage().getStatus();
        if(!status.equals(Status.IDEA_CREATION))
            return "error/403";
        Idea newIdea = new Idea();
        newIdea.setProject(project);
        model.addAttribute("ideaNew", newIdea);
        return "project/idea/new";
    }

    @Transactional
    @PostMapping("/project/{id}/idea/new")
    public String addFinding(@PathVariable("id") int projectId,
                             @ModelAttribute("ideaNew") @Valid Idea ideaNew,
                             BindingResult bindingRes, Model model){
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";
        ideaNew.setProject(project);
        model.addAttribute("ideaNew", ideaNew);

        if (bindingRes.hasErrors()) {
            System.out.println("ERRORS");
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            return "project/idea/new";
        }
        ideaNew.setUser(userService.getLoggedInUser());
        project.addIdea(ideaNew);
        projectService.save(project);
        return "redirect:/project/view/" + projectId;
    }

    @GetMapping("/project/{id}/finding/new")
    public String newFinding(@PathVariable("id") int projectId, Model model) {
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";
        Status status = project.getCurrentStage().getStatus();
        if(!status.equals(Status.FINDINGS_COLLECTION))
            return "error/403";
        Finding newFinding = new Finding();
        newFinding.setProject(project);
        model.addAttribute("findingNew", newFinding);
        return "project/finding/new";
    }

    @Transactional
    @PostMapping("/project/{id}/finding/new")
    public String addFinding(@PathVariable("id") int projectId,
                              @ModelAttribute("findingNew") @Valid Finding findingNew,
                              BindingResult bindingRes, Model model){
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";
        findingNew.setProject(project);
        model.addAttribute("findingNew", findingNew);

        if (bindingRes.hasErrors()) {
            System.out.println("ERRORS");
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            return "project/finding/new";
        }
        findingNew.setUser(userService.getLoggedInUser());
        project.addFinding(findingNew);
        projectService.save(project);
        return "redirect:/project/view/" + projectId;
    }

    @GetMapping("/project/{id}/screenshot/new")
    public String newScreenShot(@PathVariable("id") int projectId, Model model) {
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";

        Status status = project.getCurrentStage().getStatus();
        if(!status.equals(Status.PROTOTYPE_CREATION)) {
            return "error/403";
        }
        FileResourceMapper screenShotNew = new FileResourceMapper();
        ResourceType imageType = resourceTypeService.findByType("IMAGE");
        screenShotNew.setType(imageType);
        screenShotNew.setProject(project);
        model.addAttribute("screenShotNew", screenShotNew);
        return "project/resource/newScreen";
    }

    @Transactional
    @PostMapping("/project/{id}/screenshot/new")
    public String newScreenShot(@PathVariable("id") int projectId,
                              @ModelAttribute("screenShotNew") @Valid FileResourceMapper screenShotNew,
                              BindingResult bindingRes, Model model){
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";
        screenShotNew.setProject(project);
        model.addAttribute("screenShotNew", screenShotNew);

        if (bindingRes.hasErrors()) {
            System.out.println("ERRORS");
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            return "project/resource/newScreen";
        }
        // check if file is empty
        if (screenShotNew.getFile().isEmpty()) {
            bindingRes.rejectValue("file", "error.file", "Το αρχείο είναι άδειο.");
            return "project/resource/newScreen";
        }
        if(fileResourceService.fileNameExistsInProject(screenShotNew.getFile().getOriginalFilename(), project)) {
            bindingRes.rejectValue("file", "error.file", "Υπάρχει ήδη στο project αρχείο με το ίδιο όνομα.");
            return "project/resource/newScreen";
        }

        FileResource fileResource = new FileResource(screenShotNew);
        fileResource.setProject(project);
        fileResource.setUser(userService.getLoggedInUser());

        // normalize the file path
        String fileName = StringUtils.cleanPath(screenShotNew.getFile().getOriginalFilename());
        // save the file on the local file system
        File directory = new File(UPLOAD_DIR.concat("p"+projectId));
        try {
            if (!directory.exists())
                directory.mkdir();

            Path path = Paths.get(directory + "/" + fileName);
            Files.copy(screenShotNew.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("ERROR: Creating file from uploaded resource "+fileName);
        }
        //Map the resource to a FileResource instance and persist it
        project.addPrototypeScreenShot(fileResource);
        projectService.save(project);
        return "redirect:/project/view/"+projectId;
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
        //Extractor extr = new Extractor();
        File infile = new File(fileName);
        try {
            data = Extractor.extract(infile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String text = data.content;
        System.out.println("LOG: extracting content from file :"+fileName);
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
        Project project = projectService.findById(projectId);
        if(project == null)
            return "error/404";
        resourceNew.setProject(project);
        model.addAttribute("resourceNew", resourceNew);
        List<ResourceType> fileResourceTypes = resourceTypeService.findAllFileTypes();
        model.addAttribute("resourceTypes", fileResourceTypes);

        if (bindingRes.hasErrors()) {
            System.out.println("ERRORS");
            for(ObjectError error: bindingRes.getAllErrors()){
                System.out.println(error.getDefaultMessage());
            }
            return "project/resource/new";
        }
        // check if file is empty
        if (resourceNew.getFile().isEmpty()) {
            bindingRes.rejectValue("file", "error.file", "Το αρχείο είναι άδειο.");
            return "project/resource/new";
        }
        if(fileResourceService.fileNameExistsInProject(resourceNew.getFile().getOriginalFilename(), project)) {
            bindingRes.rejectValue("file", "error.file", "Υπάρχει ήδη στο project αρχείο με το ίδιο όνομα.");
            return "project/resource/new";
        }

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

    @GetMapping(value = "/screenshot/get/{resourceId}")
    public ResponseEntity<byte[]> downloadScreenShot(@PathVariable long resourceId) throws IOException {
        FileResource fileResource = fileResourceService.findById(resourceId);
        if(fileResource==null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        String img = UPLOAD_DIR + "p" + fileResource.getProject().getId() + "/" + fileResource.getFileName();
        Path imageFile = Paths.get(img);
        org.springframework.core.io.Resource resource = new UrlResource(imageFile.toUri());
        byte[] image;
        try {
            image = ByteStreams.toByteArray(resource.getInputStream());
        }catch(java.io.FileNotFoundException e){
            System.out.println(e);
            resource = new ClassPathResource("static/img/avatars/defaultScreen.jpg");
            image = ByteStreams.toByteArray(resource.getInputStream());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
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
        switch (pos) {
            case ("says") -> map.addEmpSay(comment);
            case ("feels") -> map.addEmpFeel(comment);
            case ("thinks") -> map.addEmpThink(comment);
            case ("does") -> map.addEmpDo(comment);
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
                userRepository.save(member);
            }
            thisUser.addProject(projectNew);
            userRepository.save(thisUser);
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
