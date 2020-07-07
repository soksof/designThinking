package gr.uoi.dthink;

import gr.uoi.dthink.model.*;
import gr.uoi.dthink.repos.ProjectRepository;
import gr.uoi.dthink.repos.ResourceTypeRepository;
import gr.uoi.dthink.repos.StageRepository;
import gr.uoi.dthink.services.UserService;
import gr.uoi.dthink.repos.UserRoleRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

@SpringBootApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @Bean
    ApplicationRunner init(UserRoleRepository urr, UserService us, ProjectRepository pr, ResourceTypeRepository rtr,
                           StageRepository stageRepository) {
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
            project1.setStartDate(parseDate("12/01/2020"));

            Stage stage = new Stage(Status.CHALLENGE_DEFINITION);
            stage.setStartDate(project1.getStartDate());
            stage.setEndDate(parseDate("17/01/2020"));
            stage.setDueDate(parseDate("17/01/2020"));
            stageRepository.save(stage);
            project1.setChallengeDefinition(stage);

            stage = new Stage(Status.RESOURCE_COLLECTION);
            stage.setStartDate(parseDate("18/01/2020"));
            stage.setEndDate(parseDate("15/02/2020"));
            stage.setDueDate(parseDate("18/02/2020"));
            stageRepository.save(stage);
            project1.setResourceCollection(stage);

            stage = new Stage(Status.FINDINGS_COLLECTION);
            stage.setStartDate(parseDate("16/02/2020"));
            stage.setEndDate(parseDate("20/03/2020"));
            stage.setDueDate(parseDate("16/03/2020"));
            stageRepository.save(stage);
            project1.setFindingsCollection(stage);

            stage = new Stage(Status.IDEA_CREATION);
            stage.setStartDate(parseDate("15/05/2020"));
            stage.setDueDate(parseDate("17/05/2020"));
            stageRepository.save(stage);
            project1.setIdeaCreation(stage);

            project1.setCurrentStage(stage);

            project2.setStartDate(parseDate("15/04/2020"));
            stage = new Stage(Status.CHALLENGE_DEFINITION);
            stage.setStartDate(project2.getStartDate());
            stage.setDueDate(parseDate("17/04/2020"));
            stageRepository.save(stage);
            project2.setChallengeDefinition(stage);
            project2.setCurrentStage(stage);

            project1 = pr.save(project1);
            project2 = pr.save(project2);

            //users
            User sok = new User("Σωκράτης", "Σοφιανόπουλος", "sokratis@dthink.gr", "12345678", urr.findByName("ADMIN"));
            User vPapa = new User("Βασίλης", "Παπαβασιλείου", "vassilis@dthink.gr", "12345678", urr.findByName("ADMIN"));
            User chris = new User("Χριστόφορος", "Νίκου", "cnikou@cse.uoi.gr", "12345678", urr.findByName("USER"));
            sok.addProject(project1);
            sok.addProject(project2);
            vPapa.addProject(project1);
            vPapa.addProject(project2);
            chris.addProject(project2);
            sok = us.initSave(sok);
            vPapa = us.initSave(vPapa);
            us.initSave(chris);

            project1.setManager(sok);
            project2.setManager(vPapa);
            pr.save(project1);
            pr.save(project2);
        };
    }
}
