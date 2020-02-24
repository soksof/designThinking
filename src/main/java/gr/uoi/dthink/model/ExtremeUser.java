package gr.uoi.dthink.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ExtremeUser {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String comments;
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "extremeUsers")
    private Set<Project> projects = new HashSet<>();

    public ExtremeUser() {
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
}
