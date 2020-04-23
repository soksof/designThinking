package gr.uoi.dthink.model;

public enum Status {
    CHALLENGE_DEFINITION("Καθορισμός Πρόκλησης", "alternate"),
    RESOURCE_COLLECTION("Παρατήρηση Χρηστών", "focus"),
    FINDINGS_COLLECTION("Διαπιστώσεις", "secondary"),
    IDEA_CREATION("Δημιουργία ιδεών", "info"),
    PROTOTYPE_CREATION("Δημιουργία Πρωτότυπου", "success"),
    COMPLETED("Ολοκλήρωση", "primary");

    private final String full;
    private final String color;

    private Status(String full, String color) {
        this.full = full;
        this.color = color;
    }

    public String toString(){
        return this.full;
    }

    public String getColor(){
        return this.color;
    }
}
