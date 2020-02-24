package gr.uoi.dthink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class UserRole {
    @Id
    @GeneratedValue
    private int id;
    @Column(unique = true)
    private String name;

    public UserRole() {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name of the user role
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the role to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name.toUpperCase();
    }
}
