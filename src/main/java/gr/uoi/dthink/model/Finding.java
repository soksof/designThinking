package gr.uoi.dthink.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Users can share comments regarding ideas and resources collected for a Project.
 * If they like something then the liked Field is true, if they don't like it, it is false.
 */
@Entity
public class Finding {
    @Id
    @GeneratedValue
    private long id;
    @NotEmpty(message="Ο τίτλος είναι κενός")
    private String title;
    @NotEmpty(message="Η περιγραφή είναι κενή")
    private String description;
    @ManyToOne
    private User user;
    @ManyToOne
    private Project project;
    private Date createdOn;
    @OneToMany
    private List<Reaction> reactions = new ArrayList<>();


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

    public String getUserFullName(){
        return this.getUser().getFullName();
    }

    public long getUserId(){
        return user.getId();
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
        return new SimpleDateFormat("dd/MM/yyyy").format(this.getCreatedOn());
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public void addReaction(Reaction reaction){
        this.reactions.add(reaction);
    }

    public int getLikes(){
        return this.getReactions().size();
    }
}
