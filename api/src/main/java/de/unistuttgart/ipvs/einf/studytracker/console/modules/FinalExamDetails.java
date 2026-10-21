package de.unistuttgart.ipvs.einf.studytracker.console.modules;

import de.unistuttgart.ipvs.einf.studytracker.console.Page;
import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.FinalExam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FinalExamDetails extends Page {

    private final ModuleService moduleService;
    private final Module module;

    public FinalExamDetails(ModuleService moduleService, Module module) {
        this.moduleService = moduleService;
        this.module = module;
    }

    @Override
    public void showDialogue() {
        System.out.println("Details of final exam of module " + module.getId() + " " + module.getName() + ": ");
        FinalExam finalExam = module.getFinalExam();
        if (finalExam != null) {
            System.out.printf("%-20s %s%n", "Id: ", finalExam.getId());
            System.out.printf("%-20s %s%n", "Starting time: ", finalExam.getStartingTime());
            System.out.printf("%-20s %s%n", "Duration: ", finalExam.getDuration());
            System.out.printf("%-20s %s%n", "Allowed tools: ", finalExam.getAllowedTools());
            System.out.println();
            System.out.println("1. Update final exam");
            System.out.println("2. Remove final exam");
        } else {
            System.out.println("No final exam added.");
            System.out.println();
            System.out.println("1. Add final exam");
        }
        System.out.println("0. Back to previous page");
        checkInput();
    }

    @Override
    public void navigateTo(int input) {
        if (input == 0) {
            setNextPage(new ModuleDetails(moduleService, moduleService.getModule(module.getId())));
            return;
        }
        if (module.getFinalExam() != null) {
            switch (input) {
                case 1:
                    updateFinalExam();
                    setNextPage(new FinalExamDetails(moduleService, moduleService.getModule(module.getId())));
                    break;
                case 2:
                    removeFinalExam();
                    setNextPage(new FinalExamDetails(moduleService, moduleService.getModule(module.getId())));
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
                    checkInput();
            }
        } else {
            if (input == 1) {
                createFinalExam();
                setNextPage(new FinalExamDetails(moduleService, moduleService.getModule(module.getId())));
            } else {
                System.out.println("Invalid input. Please try again.");
                checkInput();
            }
        }
    }

    private void createFinalExam() {
        Long id = readUniqueExamId();
        LocalDateTime startingTime = readDateTime("Starting time (yyyy-MM-dd HH:mm): ");
        int durationMinutes = readPositiveInt("Duration (minutes): ");
        String allowedTools = readString("Allowed tools: ");

        FinalExam exam = moduleService.createFinalExam(id, startingTime, durationMinutes, allowedTools);
        moduleService.setFinalExam(module.getId(), exam);
        System.out.println("Final exam added.");
    }

    private void updateFinalExam() {
        FinalExam current = module.getFinalExam();

        LocalDateTime startingTime = readOptionalDateTime("Starting time", current.getStartingTime());
        int durationMinutes = readOptionalPositiveInt("Duration (minutes)", (int) current.getDuration().toMinutes());
        String allowedTools = readOptionalString("Allowed tools", current.getAllowedTools());

        current.setStartingTime(startingTime);
        current.setDuration(Duration.ofMinutes(durationMinutes));
        current.setAllowedTools(allowedTools);

        moduleService.setFinalExam(module.getId(), current);
        System.out.println("Final exam updated.");
    }

    private void removeFinalExam() {
        if (askYesNo("Are you sure you want to remove the final exam? ")) {
            moduleService.removeFinalExam(module.getId());
            System.out.println("Final exam removed.");
        }
    }

    private Long readUniqueExamId() {
        while (true) {
            System.out.print("Enter final exam ID: ");
            String line = scanner.nextLine().trim();
            Long id;
            try {
                id = Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID. Please enter a numeric value.");
                continue;
            }
            if (moduleService.findFinalExam(id) != null) {
                System.out.println("A final exam with that ID already exists. Please enter a different ID.");
                continue;
            }
            return id;
        }
    }

}
