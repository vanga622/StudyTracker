package de.unistuttgart.ipvs.einf.studytracker.console.modules;

import de.unistuttgart.ipvs.einf.studytracker.console.Page;
import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.QualifyingExam;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Requirement;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Tutorial;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Worksheet;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequirementOverview extends Page {

    private final ModuleService moduleService;
    private final Module module;

    private final Map<Integer,Requirement> requirementDetailsMap = new HashMap<>();

    public RequirementOverview(ModuleService moduleService, Module module) {
        this.moduleService = moduleService;
        this.module = module;
    }

    @Override
    public void showDialogue() {
        System.out.println("Requirements for module " + module.getId() + ", " + module.getName() + ":");
        listRequirements();
        System.out.println("You are at the Requirement Overview page. Please enter the corresponding number to navigate: ");
        System.out.println("1. Requirement details");
        System.out.println("2. Update fulfillment of a requirement");
        System.out.println("3. Add qualifying exam");
        System.out.println("4. Add tutorial");
        System.out.println("5. Remove requirement");
        System.out.println("9. Back to previous page");
        System.out.println("0. Back to main menu");
        checkInput();
    }

    @Override
    public void navigateTo(int input) {
        switch (input) {
            case 1:
                Requirement toView = selectRequirement();
                setNextPage(new RequirementDetails(moduleService, moduleService.getModule(module.getId()), toView));
                break;
            case 2:
                Requirement selected = selectRequirement();
                String type = selected instanceof QualifyingExam ? "qualifying exam" : "tutorial";
                boolean status = askYesNo("Is the requirement of " + type + " fulfilled? ");
                moduleService.setRequirementStatus(module.getId(), selected, status);
                setNextPage(new RequirementOverview(moduleService, moduleService.getModule(module.getId())));
                break;
            case 3:
                addQualifyingExam();
                setNextPage(new RequirementOverview(moduleService, moduleService.getModule(module.getId())));
                break;
            case 4:
                addTutorial();
                setNextPage(new RequirementOverview(moduleService, moduleService.getModule(module.getId())));
                break;
            case 5:
                Requirement toRemove = selectRequirement();
                removeRequirement(toRemove);
                setNextPage(new RequirementOverview(moduleService, moduleService.getModule(module.getId())));
                break;
            case 9:
                setNextPage(new ModuleDetails(moduleService, moduleService.getModule(module.getId())));
                break;
            case 0:
                backToMain();
                break;
            default:
                System.out.println("Invalid input. Please try again.");
                checkInput();
        }
    }

    private void removeRequirement(Requirement toBeRemoved) {
        String type = toBeRemoved instanceof QualifyingExam ? "qualifying exam" : "tutorial";
        String typeCapped = type.substring(0, 1).toUpperCase() + type.substring(1);
        if (askYesNo("Are you sure you want to delete this " + type + "? ")) {
            moduleService.removeRequirement(module.getId(), toBeRemoved);
            System.out.println(typeCapped + " deleted successfully.");
        } else {
            System.out.println("Delete " + type + " canceled.");
        }
    }

    private void listRequirements() {
        List<Requirement> requirements = module.getRequirements();
        if (requirements.isEmpty()) {
            System.out.println("No requirements added yet.");
            return;
        }
        int index = 1;
        for (Requirement r : requirements) {
            if (r instanceof QualifyingExam qe) {
                System.out.printf("  [%d] Qualifying Exam - fulfilled: %s, starts: %s, duration: %s%n",
                        index, qe.fulfilled(), qe.startingTime(), qe.duration());

            } else if (r instanceof Tutorial t) {
                long overdueCount = t.worksheets().stream()
                        .filter(ws -> !ws.isDone()
                                && ws.getDeadline() != null
                                && ws.getDeadline().isBefore(LocalDateTime.now()))
                        .count();
                long doneCount = t.worksheets().stream().filter(Worksheet::isDone).count();
                long passedCount = t.worksheets().stream().filter(Worksheet::isPassed).count();
                System.out.printf("  [%d] Tutorial \"%s\" - fulfilled: %s, worksheets: %d, overdue: %d, done : %d, passed: %d%n",
                        index, t.name(), t.fulfilled(), t.worksheets().size(), overdueCount, doneCount, passedCount);
            }
            requirementDetailsMap.put(index, r);
            index++;
        }
    }

    private Requirement selectRequirement() {
        while (true) {
            int index = readInt("Enter the index from the list above: ");
            Requirement requirement = requirementDetailsMap.get(index);
            if (requirement == null) {
                System.out.println("Requirement index out of bounds.");
                continue;
            }
            return requirement;
        }
    }

    private void addQualifyingExam() {
        boolean alreadyExists = module.getRequirements().stream()
                .anyMatch(QualifyingExam.class::isInstance);
        if (alreadyExists) {
            System.out.println("This module already has a qualifying exam.");
            return;
        }

        LocalDateTime startingTime = readDateTime("Starting time (yyyy-MM-dd HH:mm): ");
        int durationMinutes = readPositiveInt("Duration (minutes): ");

        QualifyingExam exam = new QualifyingExam(false, startingTime, Duration.ofMinutes(durationMinutes));
        moduleService.addRequirement(module.getId(), exam);
        System.out.println("Qualifying exam added.");
    }

    private void addTutorial() {
        String name = readUniqueTutorialName();
        Tutorial tutorial = new Tutorial(name, false, new ArrayList<>());
        moduleService.addRequirement(module.getId(), tutorial);
        System.out.println("Tutorial added.");
    }

    private String readUniqueTutorialName() {
        while (true) {
            String name = readString("Tutorial name: ");
            boolean nameTaken = module.getRequirements().stream()
                    .filter(Tutorial.class::isInstance)
                    .map(Tutorial.class::cast)
                    .anyMatch(t -> t.name().equals(name));
            if (nameTaken) {
                System.out.println("A tutorial with that name already exists. Please enter a different name.");
                continue;
            }
            return name;
        }
    }
}
