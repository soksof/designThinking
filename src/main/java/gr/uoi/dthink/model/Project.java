package gr.uoi.dthink.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

        System.out.println("---->" +this.fileResources.size());
        return this.fileResources;
    }

    public void addFileResource(FileResource fileResource){
        System.out.println("BEFORE: "+this.fileResources.size());
        this.fileResources.add(fileResource);
        System.out.println("AFTER:"+this.fileResources.size());
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
}
