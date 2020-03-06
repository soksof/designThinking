package gr.uoi.dthink.model;

import javax.persistence.Entity;
import java.util.List;

/**
 *
 */
@Entity
public class Idea extends Resource {
    List<Comment> comments;
    List<Reaction> reactions;
}
