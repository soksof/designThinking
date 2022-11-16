package gr.uoi.dthink.model;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.springframework.format.annotation.DateTimeFormat;
import uio.text_proc.GenWordCloud;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class Project {
    @Id
    @GeneratedValue
    private int id;
    @NotEmpty(message="Το όνομα είναι κενό!")
    private String name;
    @NotEmpty(message="Η περιγραφή είναι κενή!")
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    @ManyToOne
    private User manager;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            mappedBy = "project")
    Set<ExtremeUserCategory> categories;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "projects")
    private Set<User> members = new LinkedHashSet<>();
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            mappedBy = "project")
    private Set<FileResource> fileResources = new LinkedHashSet<>();
    @NotNull(message="Η ημερομηνία έναρξης είναι κενή!")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dueDate;
//    @NotNull(message="Η ημερομηνία λήξης κενή!")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
    @OneToOne
    private Stage challengeDefinition; //Καθορισμός Πρόκλησης
    @OneToOne
    private Stage resourceCollection; //Παρατήρηση Χρηστών
    @OneToOne
    private Stage findingsCollection; //Διαπιστώσεις
    @OneToOne
    private Stage ideaCreation; //Δημιουργία ιδεών
    @OneToOne
    private Stage prototypeCreation; //Δημιουργία Πρωτότυπου
    @OneToOne
    private Stage completedProject; //Ολοκλήρωση
    @OneToOne
    private Stage currentStage;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;
    @OneToOne
    private EmpathyMap empathyMap;

    public Project() {
    }

    public Project(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Project(User manager){
        this.manager = manager;
    }

    public Project(String name, String description, User manager){
        this.name = name;
        this.description = description;
        this.manager = manager;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Set<User> getUsers() {
        return members;
    }

    public void setUsers(Set<User> users) {
        this.members = users;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public void addMember(User member){
        this.members.add(member);
    }

    public void removeMember(User member){
        this.members.remove(member);
    }

    public void removeFileResource(FileResource resource){
        this.fileResources.remove(resource);
    }

    public void removeExtremeUserCategory(ExtremeUserCategory category){
        this.categories.remove(category);
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(getStartDate());
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Stage getChallengeDefinition() {
        return challengeDefinition;
    }

    public int getProgress(){
        return this.currentStage.getProgress();
    }

    public void setChallengeDefinition(Stage challengeDefinition) {
        this.challengeDefinition = challengeDefinition;
    }

    public Stage getResourceCollection() {
        return resourceCollection;
    }

    public void setResourceCollection(Stage resourceCollection) {
        this.resourceCollection = resourceCollection;
    }

    public Stage getIdeaCreation() {
        return ideaCreation;
    }

    public void setIdeaCreation(Stage ideaCreation) {
        this.ideaCreation = ideaCreation;
    }

    public Stage getFindingsCollection() {
        return findingsCollection;
    }

    public void setFindingsCollection(Stage findingsCollection) {
        this.findingsCollection = findingsCollection;
    }

    public Stage getPrototypeCreation() {
        return prototypeCreation;
    }

    public void setPrototypeCreation(Stage prototypeCreation) {
        this.prototypeCreation = prototypeCreation;
    }

    public Stage getCompletedProject() {
        return completedProject;
    }

    public void setCompletedProject(Stage completedProject) {
        this.completedProject = completedProject;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
        updatedOn = createdOn;
    }

    public List<Stage> getStagesList(){
        List<Stage> stageList = new ArrayList<>();
        if(this.getChallengeDefinition()!=null)
            stageList.add(this.getChallengeDefinition());
        if(this.getResourceCollection()!=null)
            stageList.add(this.getResourceCollection());
        if(this.getFindingsCollection()!=null)
            stageList.add(this.getFindingsCollection());
        if(this.getIdeaCreation()!=null)
            stageList.add(this.getIdeaCreation());
        if(this.getPrototypeCreation()!=null)
            stageList.add(this.getPrototypeCreation());
        if(this.getCompletedProject()!=null)
            stageList.add(this.getCompletedProject());
        return stageList;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public Status getStatus(){
        return this.getCurrentStage().getStatus();
    }

    public Set<ExtremeUserCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<ExtremeUserCategory> categories) {
        this.categories = categories;
    }

    public void addCategory(ExtremeUserCategory extremeUserCategory){
        this.categories.add(extremeUserCategory);
    }

    public Set<FileResource> getFileResources() {
        return this.fileResources;
    }

    public void addFileResource(FileResource fileResource){
        this.fileResources.add(fileResource);
        if(!fileResource.getContent().trim().equals("")){
            this.generateWordCloud();
        }
    }

    public void setFileResources(Set<FileResource> fileResources) {
        this.fileResources = new HashSet<>();
        this.fileResources.addAll(fileResources);
    }

    public EmpathyMap getEmpathyMap() {
        return empathyMap;
    }

    public void setEmpathyMap(EmpathyMap empathyMap) {
        this.empathyMap = empathyMap;
    }

    public File generateWordCloud(){
        String content="";
        for(FileResource resource: this.fileResources){
            if(!resource.getContent().equals(""))
            content+=" "+resource.getContent();
        }
        File mapPng = new File("uploads/p"+this.getId()+"/word_cloud.png");
        File outFile = this.generateWordCloudFile(content, mapPng);
        return outFile;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private static List<String> getStopWords() throws IOException {
        List<String> stopwords = new ArrayList();

        File srcdir = new File("stopwords/");
        File[] files = srcdir.listFiles();
        File[] var4 = files;
        int var5 = files.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            File file = var4[var6];
            stopwords.addAll(FileUtils.readLines(file, "UTF8"));
        }

        return stopwords;
    }

    private File generateWordCloudFile(String text, File outFile){
        String mode = "grad";
        File resultFile = null;
        BasicConfigurator.configure();
        GenWordCloud wcgenerator = new GenWordCloud();
        try {
            resultFile = wcgenerator.generate(text, mode, outFile, getStopWords());
        } catch (Exception e) {
            System.out.println("ERROR: Problem in generating word cloud: "+e.getMessage());
//            String stacktrace = ExceptionUtils.getStackTrace(e);
//            try {
//                FileWriter myWriter = new FileWriter("uploads/p" + this.getId() + "/exception.txt");
//                myWriter.write(stacktrace);
//                myWriter.close();
//            }catch(java.io.IOException e222){
//
//            }
            return null;
        }
        return resultFile;
    }
}
