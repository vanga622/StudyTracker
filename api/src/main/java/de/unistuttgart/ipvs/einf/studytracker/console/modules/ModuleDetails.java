package de.unistuttgart.ipvs.einf.studytracker.console.modules;

import de.unistuttgart.ipvs.einf.studytracker.console.Page;
import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleStatus;
import de.unistuttgart.ipvs.einf.studytracker.module.SemesterRotation;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Requirement;

import java.util.HashSet;
import java.util.Set;

public class ModuleDetails extends Page {

    private final ModuleService moduleService;
    private final Module module;

    public ModuleDetails(ModuleService moduleService, Module module) {
        this.moduleService = moduleService;
        this.module = module;
    }

    @Override
    public void showDialogue() {
        System.out.println("Module details page of module ID: " + module.getId() + ": ");
        System.out.printf("%-20s %s%n", "Name: ", module.getName());
        System.out.printf("%-20s %s%n", "Description: ", module.getDescription());
        System.out.printf("%-20s %s%n", "Professor: ", module.getProfessor());
        System.out.printf("%-20s %s%n", "Credits: ", module.getCredits());
        System.out.printf("%-20s %s%n", "Weekly hours: ", module.getWeeklyHours());
        System.out.printf("%-20s %s%n", "Semester(s): ", module.getAvailableSemester());
        System.out.printf("%-20s %s%n", "Status: ", module.getStatus());
        long fulfilledCount = module.getRequirements().stream().filter(Requirement::fulfilled).count();
        System.out.printf("%-20s %s/%s%n", "Requirements: ", fulfilledCount, module.getRequirements().size());
        System.out.printf("%-20s %s%n", "Eligible to exam: ", module.isEligibleToExam());
        System.out.printf("%-20s %s%n", "Final exam schedule:", module.getFinalExam() != null ? module.getFinalExam().getStartingTime() : "TBD");
        System.out.printf("%-20s %s%n", "Final grade: ", module.getFinalGrade());
        System.out.println();
        System.out.println("You are at the Module Details page. Please enter the corresponding number to navigate: ");
        System.out.println("1. Requirements: tutorials and qualifying exam");
        System.out.println("2. Final exam");
        System.out.println("3. Update module");
        System.out.println("4. Delete module");
        System.out.println("9. Back to previous page");
        System.out.println("0. Return to main page");

        checkInput();
    }

    @Override
    public void navigateTo(int input) {
        switch (input) {
            case 1:
                setNextPage(new RequirementOverview(moduleService, moduleService.getModule(module.getId())));
                break;
            case 2:
                setNextPage(new FinalExamDetails(moduleService, moduleService.getModule(module.getId())));
                break;
            case 3:
                updateModule();
                setNextPage(new ModuleDetails(moduleService, moduleService.getModule(module.getId())));
                break;
            case 4:
                deleteModule();
                break;
            case 9:
                setNextPage(new ModuleOverview(moduleService));
                break;
            case 0:
                backToMain();
                break;
            default:
                System.out.println("Invalid input. Please try again.");
                checkInput();
        }
    }

    private void deleteModule() {
        if (askYesNo("Are you sure you want to delete this module? ")) {
            moduleService.removeModule(module.getId());
            System.out.println("Module deleted successfully.");
            backToMain();
        } else {
            System.out.println("Delete module canceled.");
        }
    }

    private void updateModule() {
        String description = readOptionalString("Description", module.getDescription());
        String professor = readOptionalString("Professor", module.getProfessor());
        int credits = readOptionalPositiveInt("Credits", module.getCredits());
        int weeklyHours = readOptionalPositiveInt("Weekly hours", module.getWeeklyHours());
        Set<SemesterRotation> semesters = readOptionalSemesters(module.getAvailableSemester());

        ModuleStatus moduleStatus = readOptionalStatus("Status", module.getStatus());
        module.setStatus(moduleStatus);

        module.setEligibleToExam(askYesNo("Are you eligible to take final exam? "));

        if (module.isEligibleToExam()) {
            Double finalGrade = readOptionalDouble("Final Grade", module.getFinalGrade());
            if  (finalGrade != null) {
                if (finalGrade < 5.0) {
                    module.setStatus(ModuleStatus.COMPLETED);
                } else {
                    module.setAttempts(module.getAttempts() + 1);
                    module.setStatus(ModuleStatus.FAILED);
                }
            }
            module.setFinalGrade(finalGrade);
        }

        module.setDescription(description);
        module.setProfessor(professor);
        module.setCredits(credits);
        module.setWeeklyHours(weeklyHours);
        module.setAvailableSemester(semesters);

        moduleService.updateModule(module.getId(), module);
        System.out.println("Module updated.");
    }

    private ModuleStatus readOptionalStatus(String label, ModuleStatus currentValue) {
        while (true) {
            System.out.printf("%s (AVAILABLE, IN_PROGRESS, COMPLETED, FAILED - leave blank to keep %s): ",
                    label, currentValue);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return currentValue;
            }
            try {
                return ModuleStatus.valueOf(line.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Please try again.");
            }
        }
    }

    private Set<SemesterRotation> readOptionalSemesters(Set<SemesterRotation> currentValue) {
        while (true) {
            System.out.printf("Semester(s), comma-separated WS/SS (leave blank to keep %s): ", currentValue);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return currentValue;
            }
            try {
                Set<SemesterRotation> result = new HashSet<>();
                for (String token : line.split(",")) {
                    result.add(parseShorthand(token));
                }
                if (result.isEmpty()) {
                    System.out.println("Please enter at least one semester.");
                    continue;
                }
                return result;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid semester value. Please try again.");
            }
        }
    }

    private SemesterRotation parseShorthand(String token) {
        return switch (token.trim().toUpperCase()) {
            case "WS" -> SemesterRotation.WINTER_SEMESTER;
            case "SS" -> SemesterRotation.SUMMER_SEMESTER;
            default -> throw new IllegalArgumentException("Unknown semester: " + token);
        };
    }

    private Double readOptionalDouble(String label, Double currentValue) {
        String currentDisplay = (currentValue == null) ? "none" : currentValue.toString();
        while (true) {
            System.out.printf("%s (leave blank to keep %s): ", label, currentDisplay);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return currentValue;
            }
            try {
                double value = Double.parseDouble(line);
                if (value < 1.0 || value > 5.0 ) {
                    System.out.println("Value must be between 1.0 and 5.0. Please try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }


}
