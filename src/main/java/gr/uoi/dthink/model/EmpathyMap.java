package gr.uoi.dthink.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class EmpathyMap {
    @Id
    @GeneratedValue
    private int id;
    @OneToMany
    private List<Comment> empSay = new ArrayList<>();
    @OneToMany
    private List<Comment> empThink = new ArrayList<>();
    @OneToMany
    private List<Comment> empDo = new ArrayList<>();
    @OneToMany
    private List<Comment> empFeel = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Comment> getEmpSay() {
        return empSay;
    }

    public void setEmpSay(List<Comment> empSay) {
        this.empSay = empSay;
    }

    public void addEmpSay(Comment comment){
        this.empSay.add(comment);
    }
    public void removeEmpSay(Comment comment){
        this.empSay.remove(comment);
    }

    public List<Comment> getEmpThink() {
        return empThink;
    }

    public void setEmpThink(List<Comment> empThink) {
        this.empThink = new ArrayList<Comment>();
        this.empThink.addAll(empThink);    }

    public void addEmpThink(Comment comment){
        this.empThink.add(comment);
    }
    public void removeEmpThink(Comment comment){
        this.empThink.remove(comment);
    }

    public List<Comment> getEmpDo() {
        return empDo;
    }

    public void setEmpDo(List<Comment> empDo) {
        this.empDo = new ArrayList<Comment>();
        this.empDo.addAll(empDo);
    }

    public void addEmpDo(Comment comment){
        this.empDo.add(comment);
    }
    public void removeEmpDo(Comment comment){
        this.empDo.remove(comment);
    }

    public List<Comment> getEmpFeel() {
        return empFeel;
    }

    public void setEmpFeel(List<Comment> empFeel) {
        this.empFeel = new ArrayList<Comment>();
        this.empFeel.addAll(empFeel);
    }

    public void addEmpFeel(Comment comment){
        this.empFeel.add(comment);
    }
    public void removeEmpFeel(Comment comment){
        this.empFeel.remove(comment);
    }

    public List<Comment> getAllNotes(){
        return Stream.of(this.empDo, this.empSay, this.empFeel, this.empThink)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public boolean removeNote(Comment note){
        if(this.empDo.remove(note))
            return true;
        else if(this.empFeel.remove(note))
            return true;
        else if(this.empThink.remove(note))
            return true;
        else if (this.empSay.remove(note))
            return true;
        return false;
    }
}
