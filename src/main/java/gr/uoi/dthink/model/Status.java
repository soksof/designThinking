package gr.uoi.dthink.model;

public enum Status {
    CHALLENGE_DEFINITION("Καθορισμός Πρόκλησης", "alternate", "challengeDefinition"),
    RESOURCE_COLLECTION("Παρατήρηση Χρηστών", "focus", "resourcesCollection"),
    FINDINGS_COLLECTION("Διαπιστώσεις", "secondary", "findingsCollection"),
    IDEA_CREATION("Δημιουργία ιδεών", "info", "ideaCreation"),
    PROTOTYPE_CREATION("Δημιουργία Πρωτότυπου", "success", "prototypeCreation"),
    COMPLETED("Ολοκλήρωση", "primary", "completed");

    private final String full;
    private final String color;
    private final String view;

    private Status(String full, String color, String view) {
        this.full = full;
        this.color = color;
        this.view = view;
    }

    public String toString(){
        return this.full;
    }

    public String getColor(){
        return this.color;
    }

    public String getView() { return this.view; }
}
