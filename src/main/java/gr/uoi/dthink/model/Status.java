package gr.uoi.dthink.model;

public enum Status {
    DEFINITION {
        public String toString() {
            return "Ορισμος προβλήματος";
        }
    },
    RESOURCE_COLLECTION {
        public String toString() {
            return "Συγκεντρωση Υλικου";
        }
    },
    IDEA_COLLECTION {
        public String toString() {
            return "Καταιγισμος ιδεων";
        }
    },
    COMPLETED {
        public String toString() {
            return "Ολοκληρωση";
        }
    }
}
