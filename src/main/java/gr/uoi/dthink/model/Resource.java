package gr.uoi.dthink.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Resource {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String description;
    private Date createdOn;
    private Date updatedOn;
    @ManyToOne
    private Project project;

}
