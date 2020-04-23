package gr.uoi.dthink.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Stage {
    @Id
    @GeneratedValue
    private int id;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    private Date startDate;
    private Date endDate;
    private Date dueDate;

    public Stage(){
        this.setStartDate(new Date());
    }

    public Stage(Status status){
        this.setStartDate(new Date());
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getProgress(){
        if(status.equals(Status.CHALLENGE_DEFINITION))
            return 10;
        else if(status.equals(Status.RESOURCE_COLLECTION))
            return 25;
        else if(status.equals(Status.FINDINGS_COLLECTION))
            return 50;
        else if(status.equals(Status.IDEA_CREATION))
            return 75;
        return 99;
    }
}