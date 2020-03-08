package gr.uoi.dthink.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    @ManyToOne
    private User manager;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "projects")
    private Set<User> members = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_extreme_users",
            joinColumns = { @JoinColumn(name = "project_id") },
            inverseJoinColumns = { @JoinColumn(name = "extreme_user_id") })
    private Set<ExtremeUser> extremeUsers = new HashSet<>();
    private Date startDate;
    private Date dueDate;
    private Date endDate;
    @OneToOne
    private Stage definitionStage;
    @OneToOne
    private Stage resourceCollectionStage;
    @OneToOne
    private Stage ideaCollectionStage;
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public Project() {
    }

    public Project(String name, String description){
        this.name = name;
        this.description = description;
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

    public Set<ExtremeUser> getExtremeUsers() {
        return extremeUsers;
    }

    public void setExtremeUsers(Set<ExtremeUser> extremeUsers) {
        this.extremeUsers = extremeUsers;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Date getStartDate() {
        return startDate;
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

    public Stage getDefinitionStage() {
        return definitionStage;
    }

    public void setDefinitionStage(Stage definitionStage) {
        this.definitionStage = definitionStage;
    }

    public Stage getResourceCollectionStage() {
        return resourceCollectionStage;
    }

    public void setResourceCollectionStage(Stage resourceCollectionStage) {
        this.resourceCollectionStage = resourceCollectionStage;
    }

    public Stage getIdeaCollectionStage() {
        return ideaCollectionStage;
    }

    public void setIdeaCollectionStage(Stage ideaCollectionStage) {
        this.ideaCollectionStage = ideaCollectionStage;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
