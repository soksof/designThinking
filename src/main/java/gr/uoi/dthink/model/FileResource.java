package gr.uoi.dthink.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FileResource extends Resource{
    private String description;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @ManyToOne
    private ResourceType type;
    @ManyToOne
    private ExtremeUserCategory extremeUserCategory;
    private String fileName;

    public FileResource(){

    }

    public FileResource(FileResourceMapper file){
        this.setId(file.getId());
        this.setTitle(file.getTitle());
        this.setProject(file.getProject());
        this.setDescription(file.getDescription());
        this.setContent(file.getContent());
        this.setType(file.getType());
        this.setExtremeUserCategory(file.getExtremeUserCategory());
        this.setFileName(file.getFile().getOriginalFilename());
        this.setUser(file.getUser());
        this.setCreatedOn(file.getCreatedOn());
        this.setUpdatedOn(file.getUpdatedOn());
        this.setComments(file.getComments());
        this.setReactions(file.getReactions());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ResourceType getType() {
        return type;
    }

    public String getTypeString() {
        return type.getType()+".jpg";
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public ExtremeUserCategory getExtremeUserCategory() {
        return extremeUserCategory;
    }

    public void setExtremeUserCategory(ExtremeUserCategory extremeUserCategory) {
        this.extremeUserCategory = extremeUserCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileResourceMapper getFileResourceMapper(){
        return new FileResourceMapper(this);
    }
}
