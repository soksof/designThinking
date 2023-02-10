package gr.uoi.dthink.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Users can express their feelings regarding ideas and resources of a Project using a Reaction.
 * If they like something then the liked Field is true, if they don't like it, it is false.
 */
@Entity
public class Reaction {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    private User user;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    private boolean liked;

    @PrePersist
    protected void onCreate() {
        createdOn = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
