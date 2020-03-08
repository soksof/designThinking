package gr.uoi.dthink.model;

import javax.persistence.Entity;
import java.util.List;

/**
 *
 */
@Entity
public class Idea extends Resource {
    private String content;
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
