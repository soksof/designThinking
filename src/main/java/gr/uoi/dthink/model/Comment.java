package gr.uoi.dthink.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Users can share comments regarding ideas and resources collected for a Project.
 * If they like something then the liked Field is true, if they don't like it, it is false.
 */
@Entity
public class Comment {
    @Id
    @GeneratedValue
    private long id;
    private String description;
    @ManyToOne
    private User user;
    @ManyToOne
    private Project project;
    private Date createdOn;

    @PrePersist
    protected void onCreate() {
        this.setCreatedOn(new Date());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getDateOnly(){
        return new SimpleDateFormat("dd/MM/yyyy").format(this.createdOn);
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
