package de.unistuttgart.ipvs.einf.studytracker.console.modules;

import de.unistuttgart.ipvs.einf.studytracker.console.Page;
import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.QualifyingExam;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Requirement;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Tutorial;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Worksheet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RequirementDetails extends Page {

    private final ModuleService moduleService;
    private final Module module;
    private final Requirement requirement;

    public RequirementDetails(ModuleService moduleService, Module module, Requirement requirement) {
        this.moduleService = moduleService;
        this.module = module;
        this.requirement = requirement;
    }

    @Override
    public void showDialogue() {
        if (requirement instanceof QualifyingExam qe) {
            showQualifyingExamDetails(qe);
            System.out.println();
            System.out.println("0. Back");
        } else if (requirement instanceof Tutorial t) {
            showTutorialDetails(t);
            System.out.println();
            System.out.println("1. Add worksheet");
            System.out.println("2. Mark worksheet done");
            System.out.println("3. Mark worksheet passed");
            System.out.println("4. Reset worksheet progression");
            System.out.println("5. Update worksheet");
            System.out.println("6. Remove worksheet");
            System.out.println("0. Back");
        }
        checkInput();
    }

    private void showQualifyingExamDetails(QualifyingExam qe) {
        System.out.println("Qualifying Exam details:");
        System.out.printf("%-15s %s%n", "Fulfilled:", qe.fulfilled());
        System.out.printf("%-15s %s%n", "Starting time:", qe.startingTime());
        System.out.printf("%-15s %s%n", "Duration:", qe.duration());
    }

    private void showTutorialDetails(Tutorial t) {
        System.out.println("Tutorial details:");
        System.out.printf("%-15s %s%n", "Name:", t.name());
        System.out.printf("%-15s %s%n", "Fulfilled:", t.fulfilled());
        System.out.println("Worksheets:");
        List<Worksheet> worksheets = t.worksheets();
        if (worksheets.isEmpty()) {
            System.out.println("  No worksheets added yet.");
            return;
        }
        for (Worksheet ws : worksheets) {
            System.out.printf("  [%d] %s - deadline: %s, done: %s, passed: %s%n",
                    ws.getId(), ws.getDescription(), ws.getDeadline(), ws.isDone(), ws.isPassed());
        }
    }

    @Override
    public void navigateTo(int input) {
        if (input == 0) {
            setNextPage(new RequirementOverview(moduleService, module));
            return;
        }
        if (!(requirement instanceof Tutorial tutorial)) {
            System.out.println("Invalid input. Please try again.");
            checkInput();
            return;
        }
        switch (input) {
            case 1:
                addWorksheet(tutorial);
                break;
            case 2:
                markWorksheet(tutorial, true, false);
                break;
            case 3:
                markWorksheet(tutorial, false, true);
                break;
            case 4:
                markWorksheet(tutorial, false, false);
                break;
            case 5:
                updateWorksheet(tutorial);
                break;
            case 6:
                removeWorksheet(tutorial);
                break;
            default:
                System.out.println("Invalid input. Please try again.");
                checkInput();
                return;
        }
        Module freshModule = moduleService.getModule(module.getId());
        setNextPage(new RequirementDetails(moduleService, freshModule, refreshRequirement(freshModule, requirement)));
    }

    private Requirement refreshRequirement(Module freshModule, Requirement stale) {
        if (stale instanceof QualifyingExam) {
            return freshModule.getRequirements().stream()
                    .filter(QualifyingExam.class::isInstance)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Qualifying exam no longer exists"));
        }
        if (stale instanceof Tutorial t) {
            return freshModule.getRequirements().stream()
                    .filter(Tutorial.class::isInstance)
                    .map(Tutorial.class::cast)
                    .filter(other -> other.name().equals(t.name()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Tutorial \"" + t.name() + "\" no longer exists"));
        }
        throw new IllegalStateException("Unknown requirement type");
    }

    private void addWorksheet(Tutorial tutorial) {
        String description = readString("Description: ");
        LocalDateTime deadline = readDateTime("Deadline (yyyy-MM-dd HH:mm): ");
        if (askYesNo("Worksheet id is assigned incrementally, do you want to add worksheet with specific Id? ")) {
            long specificId = readLong("Specific Id: ");
            boolean exists = tutorial.worksheets().stream().anyMatch(ws -> ws.getId().equals(specificId));
            if (exists) {
                moduleService.addWorksheet(module.getId(), tutorial.name(), description, deadline);
                System.out.println("Worksheet with id " + specificId + " already exists. Id assigned incrementally instead. ");
            } else {
                moduleService.addWorksheetWithId(module.getId(), tutorial.name(), specificId, description, deadline);
            }
        } else {
            moduleService.addWorksheet(module.getId(), tutorial.name(), description, deadline);
        }
        System.out.println("Worksheet added.");
    }

    private void markWorksheet(Tutorial tutorial, boolean isDoneAction, boolean isPassedAction) {
        Long id = readWorksheetId(tutorial);
        if (isDoneAction) {
            boolean done = askYesNo("Mark as done? ");
            moduleService.setWorksheetCompletion(module.getId(), tutorial.name(), id, done);
        } else if (isPassedAction) {
            boolean passed = askYesNo("Mark as passed? ");
            moduleService.setWorksheetCompletion(module.getId(), tutorial.name(), id, true);
            moduleService.setWorksheetStatus(module.getId(), tutorial.name(), id, passed);
        } else {
            moduleService.setWorksheetCompletion(module.getId(), tutorial.name(), id, false);
            moduleService.setWorksheetStatus(module.getId(), tutorial.name(), id, false);
        }
        System.out.println("Worksheet updated.");
    }

    private void updateWorksheet(Tutorial tutorial) {
        Long id = readWorksheetId(tutorial);
        Worksheet ws = moduleService.getWorksheetOrElseThrow(tutorial.name(), id, module);
        String description = readOptionalString("New description", ws.getDescription());
        LocalDateTime deadline = readOptionalDateTime("New deadline", ws.getDeadline());
        moduleService.updateWorksheet(module.getId(), tutorial.name(), id, description, deadline);
        System.out.println("Worksheet updated.");
    }

    private void removeWorksheet(Tutorial tutorial) {
        Long id = readWorksheetId(tutorial);
        if (askYesNo("Are you sure you want to remove worksheet " + id + "? ")) {
            moduleService.removeWorksheet(module.getId(), tutorial.name(), id);
            System.out.println("Worksheet removed.");
        }
    }

    private Long readWorksheetId(Tutorial tutorial) {
        while (true) {
            System.out.print("Enter worksheet ID: ");
            String line = scanner.nextLine().trim();
            try {
                Long id = Long.parseLong(line);
                boolean exists = tutorial.worksheets().stream().anyMatch(ws -> ws.getId().equals(id));
                if (!exists) {
                    System.out.println("No worksheet with that ID. Please try again.");
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

}
