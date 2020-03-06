package gr.uoi.dthink.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FileResource extends Resource{
    private String content;
    @ManyToOne
    private ResourceType type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }
}
