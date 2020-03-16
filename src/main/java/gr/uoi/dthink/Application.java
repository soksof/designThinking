package gr.uoi.dthink;

import gr.uoi.dthink.model.*;
import gr.uoi.dthink.repos.ProjectRepository;
import gr.uoi.dthink.repos.ResourceTypeRepository;
import gr.uoi.dthink.services.UserService;
import gr.uoi.dthink.repos.UserRoleRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ApplicationRunner init(UserRoleRepository urr, UserService us, ProjectRepository pr, ResourceTypeRepository rtr) {
        return args -> {
            //user Roles
            Stream.of("USER", "ADMIN", "GUEST").forEach(roleName -> {
                UserRole uRole = new UserRole();
                uRole.setName(roleName);
                urr.save(uRole);
            });

            //Resource types
            Stream.of("IMAGE", "ZIP", "AUDIO", "PDF", "EXCEL FILE", "WORD DOCUMENT", "VIDEO", "URL").forEach(type -> {
                ResourceType rType = new ResourceType(type);
                rtr.save(rType);
            });

            //Projects
            Project project1 = new Project("Τεχνολογίες  επαυξημένης πραγματικότητας για επαγγελματίες",
                    "Η προσφορά μιας ολοκληρωμένης υπηρεσίας,όπου ο πελάτης θα μπορεί να παραγγείλει και  να  λάβει  εκτυπωμένα  τα  έντυπα  που  δημιούργησε  καθώς  και  την  εφαρμογή επαυξημένης πραγματικότητας που τα συνοδεύει. Η εφαρμογή θα προσφέρεται δωρεάν κάτω από το λογότυπο του έργου, είτε θα μπορεί να την αποκτήσει ο πελάτης με το αντίστοιχο κόστος.");
            Project project2 = new Project("Πρόσβαση στα υποκαταστήματα τράπεζας για όλους", "Μελέτη των αλλαγών που απαιτούνται στα υποκαταστήματα της τράπεζας για ευκολότερη πρόσβαση σε αυτά απο ΑΜΕΑ.");
            project1.setStatus(Status.IDEA_COLLECTION);
            project1.setStartDate(new Date("12/1/2020"));
            project2.setStatus(Status.DEFINITION);
            project2.setStartDate(new Date("15/2/2020"));

            project1 = pr.save(project1);
            project2 = pr.save(project2);

            //users
            User sok = new User("Σωκράτης", "Σοφιανόπουλος", "s_sofian@ilsp.gr", "12345678", urr.findByName("ADMIN"));
            User vPapa = new User("Βασίλης", "Παπαβασιλείου", "vpapa@ilsp.gr", "12345678", urr.findByName("ADMIN"));
            User chris = new User("Χριστόφορος", "Νίκου", "cnikou@cse.uoi.gr", "12345678", urr.findByName("USER"));
            sok.addProject(project1);
            sok.addProject(project2);
            vPapa.addProject(project1);
            vPapa.addProject(project2);
            chris.addProject(project2);
            sok = us.save(sok);
            vPapa = us.save(vPapa);
            us.save(chris);

            project1.setManager(sok);
            project2.setManager(vPapa);
            pr.save(project1);
            pr.save(project2);
        };
    }
}
