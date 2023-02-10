package gr.uoi.dthink.model;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

public class FileResourceMapper extends Resource{
    private String description;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToOne
    private ResourceType type;
    @ManyToOne
    private ExtremeUserCategory extremeUserCategory;
    private MultipartFile file;

    public FileResourceMapper(){
    }

    public FileResourceMapper(FileResource file){
        this.setId(file.getId());
        this.setTitle(file.getTitle());
        this.setProject(file.getProject());
        this.setDescription(file.getDescription());
        this.setContent(file.getContent());
        this.setType(file.getType());
        this.setExtremeUserCategory(file.getExtremeUserCategory());
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public FileResource getFileResource(){
        return new FileResource(this);
    }
}
