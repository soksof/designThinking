package gr.uoi.dthink.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ExtremeUserCategory {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String description;
    @ManyToOne
    private Project project;

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "ExtremeUserCategory{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", project=" + project +
                '}';
    }
}
