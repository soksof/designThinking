package gr.uoi.dthink.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String description;
    @ManyToOne
    private User manager;
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "project_users",
            joinColumns = { @JoinColumn(name = "project_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> members = new HashSet<>();
        /*@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<User> members = new ArrayList<>();*/
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "project_extreme_users",
            joinColumns = { @JoinColumn(name = "project_id") },
            inverseJoinColumns = { @JoinColumn(name = "extreme_user_id") })
    private Set<ExtremeUser> extremeUsers = new HashSet<>();

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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Set<User> getUsers() {
        return members;
    }

    public void setUsers(Set<User> users) {
        this.members = users;
    }

    public Set<ExtremeUser> getExtremeUsers() {
        return extremeUsers;
    }

    public void setExtremeUsers(Set<ExtremeUser> extremeUsers) {
        this.extremeUsers = extremeUsers;
    }
}
