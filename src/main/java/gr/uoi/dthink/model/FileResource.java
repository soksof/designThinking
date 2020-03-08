package gr.uoi.dthink.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FileResource extends Resource{
    private String description;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToOne
    private ResourceType type;
    @ManyToOne
    private ExtremeUser extremeUser;

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

    public ExtremeUser getExtemeUser() {
        return extremeUser;
    }

    public void setExtemeUser(ExtremeUser extremeUser) {
        this.extremeUser = extremeUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
