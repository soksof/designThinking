package gr.uoi.dthink.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Stage {
    @Id
    @GeneratedValue
    private int id;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
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

    public String getStartDateString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(this.getStartDate());
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        if(this.getEndDate()==null)
            return "τώρα";
        return dateFormat.format(this.getEndDate());
    }

    public boolean isLate() {
        Date today = new Date();
        if(this.getDueDate()==null)
            return false;
        if(this.getEndDate()!=null)
            return this.getDueDate().compareTo(this.getEndDate()) < 0;
        return this.getDueDate().compareTo(today) < 0;
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
