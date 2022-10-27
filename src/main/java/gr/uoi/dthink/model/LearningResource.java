package gr.uoi.dthink.model;

import javax.persistence.*;

@Entity
public class LearningResource {


    @Id
    @GeneratedValue
    private int id;
    private String name;
    private LearningResourceType type;
    private String description;
    @Column(unique = true)
    private String content;
    @ManyToOne
    private User user;

    public LearningResource(){
    }

    public LearningResource(String name, String description, User user, LearningResourceType type) {
        this.name = name;
        this.description = description;
        this.user = user;
        this.type = type;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LearningResourceType getType() {
        return type;
    }

    public void setType(LearningResourceType type) {
        this.type = type;
    }
}
