package gr.uoi.dthink.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class Resource {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private Date createdOn;
    private Date updatedOn;
    @ManyToOne
    private Project project;
    @OneToMany
    private List<Comment> comments = new ArrayList<>();
    @OneToMany
    private List<Reaction> reactions = new ArrayList<>();
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

    public int getCommentsCount(){
        return getComments().size();
    }

    public long getFirstCommentedUserId(){
        if(this.comments.size()>0)
            return this.comments.get(0).getUserId();
        return -1;
    }

    public void setComments(List<Comment> comments) {
        this.comments = new ArrayList<>();
        this.comments.addAll(comments);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public int getReactionsCount(){
        return getReactions().size();
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = new ArrayList<>();
        this.reactions.addAll(reactions);
    }

    public void addReaction(Reaction reaction){
        this.reactions.add(reaction);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
