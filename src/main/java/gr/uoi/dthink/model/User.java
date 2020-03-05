package gr.uoi.dthink.model;

import javax.persistence.*;
import java.util.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;
    @Transient
    private String passwordConfirm;
    private String profilePic;
    @ManyToOne
    private UserRole role;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;
    @ManyToMany
//            (fetch = FetchType.LAZY,
//            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "members")
    private Set<Project> projects = new HashSet<>();

    public User() {
    }

    public User(String name, String lastName, String email, UserRole role) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public User(String name, String lastName, String email, String pass, UserRole role) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.password=pass;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastLogin() {
        this.lastLogin = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
    }
    public Date getCreatedOn() {
        return createdOn;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedOn = new Date();
    }
    public Date getUpdatedOn() {
        return updatedOn;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project){
        this.projects.add(project);
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
