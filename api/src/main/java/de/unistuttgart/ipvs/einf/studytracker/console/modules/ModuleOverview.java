package de.unistuttgart.ipvs.einf.studytracker.console.modules;

import de.unistuttgart.ipvs.einf.studytracker.console.Page;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleStatus;
import de.unistuttgart.ipvs.einf.studytracker.module.SemesterRotation;
import de.unistuttgart.ipvs.einf.studytracker.module.Module;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleOverview extends Page {

    private final ModuleService moduleService;

    public ModuleOverview(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public void showDialogue() {
        listModules();
        System.out.println();
        System.out.println("You are at the Module Management page. Please enter the corresponding number to navigate: ");
        System.out.println("1. Module Details");
        System.out.println("2. Add Module");
        System.out.println("3. Delete Module");
        System.out.println("0. Back to Main");
        checkInput();
    }

    private void listModules() {
        List<Module> modules = moduleService.getAllModules();
        if (modules.isEmpty()) {
            System.out.println("No modules added yet.");
            return;
        }

        modules.sort(Comparator.comparing(m -> m.getStatus() != ModuleStatus.IN_PROGRESS));

        System.out.println("Modules:");
        for (Module m : modules) {
            if (m.getFinalGrade() != null) {
                System.out.printf("%-10s%s - %s%n          credits: %d, status: %s, final grade: %.1f%n",
                        "[" + m.getId() + "]", m.getName(), m.getDescription(), m.getCredits(), m.getStatus(), m.getFinalGrade());
            } else {
                String date = m.getFinalExam() == null ? "TBD" : m.getFinalExam().getStartingTime().toString();
                System.out.printf("%-10s%s - %s%n          credits: %d, status: %s, eligible to exam: %b, final exam date: %s%n",
                        "[" + m.getId() + "]", m.getName(), m.getDescription(), m.getCredits(), m.getStatus(), m.isEligibleToExam(), date);
            }

        }
    }

    @Override
    public void navigateTo(int input) {
        Module module;
        switch (input) {
            case 1:
                List<Module> modules = moduleService.getAllModules();
                if (modules.isEmpty()) {
                    return;
                }
                module = findModule();
                setNextPage(new ModuleDetails(moduleService, moduleService.getModule(module.getId())));
                break;
            case 2:
                do {
                    createNewModule();
                } while (askYesNo("Do you want to add another module? "));
                break;
            case 3:
                module = findModule();
                deleteModule(module);
                break;
            case 0:
                backToMain();
                break;
            default:
                System.out.println("Invalid input. Please try again.");
                checkInput();
        }
    }

    private Module findModule() {
        while (true) {
            Long id = readModuleId();
            try {
                return moduleService.getModule(id);
            } catch (IllegalStateException e) {
                System.out.println("No module found with id " + id + ". Please try again.");
            }
        }
    }

    private void createNewModule() {
        Long id = readUniqueModuleId();

        String name = readString("Enter module name: ");
        String description = readString("Enter module description: ");
        String professor = readString("Enter module professor: ");

        int credits = readPositiveInt("Enter credits: ");
        int weeklyHours = readPositiveInt("Enter weekly hours: ");

        Set<SemesterRotation> availableSemester = readSemesters();

        Module module = moduleService.createModule(id, name, description, professor, credits, weeklyHours, availableSemester);
        System.out.println("Module \"" + module.getName() + "\" created with id " + module.getId() + ".");
    }

    private void deleteModule(Module module) {
        if (askYesNo("Are you sure you want to delete this module? ")) {
            moduleService.removeModule(module.getId());
            System.out.println("Module deleted successfully.");
            backToMain();
        } else {
            System.out.println("Delete module canceled.");
        }
    }

    private Long readModuleId() {
        while (true) {
            System.out.print("Enter module ID: ");
            String line = scanner.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID. Please enter a numeric value.");
            }
        }
    }

    private Long readUniqueModuleId() {
        while (true) {
            Long id = readModuleId();
            if (moduleService.exists(id)) {
                System.out.println("A module with that ID already exists. Please enter a different ID.");
                continue;
            }
            return id;
        }
    }

    private Set<SemesterRotation> readSemesters() {
        while (true) {
            System.out.print("Enter available semester(s), comma-separated (e.g. WS,SS): ");
            String line = scanner.nextLine().trim().toUpperCase();
            String[] tokens = line.split(",");

            try {
                Set<SemesterRotation> result = new HashSet<>();
                for (String token : tokens) {
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
}
