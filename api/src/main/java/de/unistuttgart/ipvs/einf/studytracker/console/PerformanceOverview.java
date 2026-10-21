package de.unistuttgart.ipvs.einf.studytracker.console;

import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.Module;

import java.util.List;

public class PerformanceOverview extends Page {

    private final ModuleService moduleService;

    public PerformanceOverview(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public void showDialogue() {
        System.out.println("Performance Overview:");
        showStats();
        System.out.println();
        System.out.println("0. Back to main menu");
        checkInput();
    }

    @Override
    public void navigateTo(int input) {
        if (input == 0) {
            backToMain();
            return;
        }
        System.out.println("Invalid input. Please try again.");
        checkInput();
    }

    private void showStats() {
        List<Module> modules = moduleService.getAllModules();

        if (modules.isEmpty()) {
            System.out.println("No modules added yet.");
            return;
        }

        int completedCount = 0;
        int inProgressCount = 0;
        int failedCount = 0;
        int availableCount = 0;

        int completedCredits = 0;
        int inProgressCredits = 0;

        double weightedGradeSum = 0.0;
        int gradedCredits = 0;

        int availableCredits = 0;

        for (Module m : modules) {
            switch (m.getStatus()) {
                case COMPLETED -> {
                    completedCount++;
                    completedCredits += m.getCredits();
                }
                case IN_PROGRESS -> {
                    inProgressCount++;
                    inProgressCredits += m.getCredits();
                }
                case FAILED -> failedCount++;
                case AVAILABLE -> {
                    availableCount++;
                    availableCredits += m.getCredits();
                }
            }

            if (m.getFinalGrade() != null) {
                weightedGradeSum += m.getFinalGrade() * m.getCredits();
                gradedCredits += m.getCredits();
            }
        }

        System.out.printf("%-25s %d%n", "Completed modules:", completedCount);
        System.out.printf("%-25s %d%n", "In progress modules:", inProgressCount);
        System.out.printf("%-25s %d%n", "Failed modules:", failedCount);
        System.out.printf("%-25s %d%n", "Available modules:", availableCount);
        System.out.printf("%-25s %d%n", "Completed credits:", completedCredits);
        System.out.printf("%-25s %d%n", "In-progress credits:", inProgressCredits);
        System.out.printf("%-25s %d%n", "Available credits:", availableCredits);

        if (gradedCredits > 0) {
            double average = weightedGradeSum / gradedCredits;
            System.out.printf("%-25s %.2f%n", "Weighted average grade:", average);
        } else {
            System.out.printf("%-25s %s%n", "Weighted average grade:", "N/A (no grades yet)");
        }
    }
}
