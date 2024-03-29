package gr.uoi.dthink.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ResourceType {
    @Id
    @GeneratedValue
    private int id;
    private String type;

    public ResourceType(){

    }

    public ResourceType(String type){
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ResourceType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
