package gr.uoi.dthink.model;

import java.util.List;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Resource {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private Date createdOn;
    private Date updatedOn;
    @ManyToOne
    private Project project;
    private
    @OneToMany
    List<Comment> comments;
    @OneToMany
    List<Reaction> reactions;
    @ManyToOne
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    @PrePersist
    protected void onCreate() {
        this.setCreatedOn(new Date());
        this.setUpdatedOn(this.getCreatedOn());
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedOn(new Date());
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
