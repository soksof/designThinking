package gr.uoi.dthink.model;

import javax.persistence.*;

@Entity
public class ExtremeUser {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String notes;
    @ManyToOne
    private ExtremeUserCategory extremeUserCategory;

    public ExtremeUser() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ExtremeUserCategory getCategory() {
        return extremeUserCategory;
    }

    public void setCategory(ExtremeUserCategory extremeUserCategory) {
        this.extremeUserCategory = extremeUserCategory;
    }
}
