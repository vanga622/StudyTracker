package de.unistuttgart.ipvs.einf.studytracker.console;

import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.FinalExam;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.QualifyingExam;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Requirement;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Tutorial;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Worksheet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DeadlineOverview extends Page {

    private final ModuleService moduleService;

    private boolean groupByModule = false;
    private Long filterModuleId = null;
    private DeadlineType filterType = null;

    public DeadlineOverview(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    private enum DeadlineType { WORKSHEET, QUALIFYING_EXAM, FINAL_EXAM }

    private record DeadlineEntry(LocalDateTime dateTime, Module module, DeadlineType type, String description) {}


    @Override
    public void showDialogue() {
        System.out.println("Upcoming Deadlines" + (groupByModule ? " (grouped by module):" : " (chronological):"));
        if (filterModuleId != null) {
            System.out.println("  [filter: module id " + filterModuleId + "]");
        }
        if (filterType != null) {
            System.out.println("  [filter: type " + filterType + "]");
        }
        listDeadlines();
        System.out.println();
        System.out.println("1. Toggle sort order");
        System.out.println("2. Filter by module");
        System.out.println("3. Filter by type");
        System.out.println("4. Clear filters");
        System.out.println("0. Back to main menu");
        checkInput();
    }

    @Override
    public void navigateTo(int input) {
        switch (input) {
            case 1:
                groupByModule = !groupByModule;
                break;
            case 2:
                filterModuleId = readLong("Enter module ID to filter by: ");
                break;
            case 3:
                filterType = readDeadlineType();
                break;
            case 4:
                filterModuleId = null;
                filterType = null;
                break;
            case 0:
                backToMain();
                return;
            default:
                System.out.println("Invalid input. Please try again.");
                checkInput();
        }
    }

    private void listDeadlines() {
        List<DeadlineEntry> entries = new ArrayList<>();
        for (Module module : moduleService.getAllModules()) {
            for (Requirement req : module.getRequirements()) {
                if (req instanceof Tutorial t) {
                    for (Worksheet ws : t.worksheets()) {
                        if (!ws.isDone() && ws.getDeadline() != null) {
                            entries.add(new DeadlineEntry(
                                    ws.getDeadline(),
                                    module,
                                    DeadlineType.WORKSHEET,
                                    "Worksheet - " + t.name() + " - " + ws.getDescription()
                            ));
                        }
                    }
                } else if (req instanceof QualifyingExam qe) {
                    if (qe.startingTime().isAfter(LocalDateTime.now())) {
                        entries.add(new DeadlineEntry(
                                qe.startingTime(), module, DeadlineType.QUALIFYING_EXAM, "Qualifying Exam")
                        );
                    }
                }
            }

            FinalExam exam = module.getFinalExam();
            if (exam != null && exam.getStartingTime() != null && exam.getStartingTime().isAfter(LocalDateTime.now())) {
                entries.add(new DeadlineEntry(
                        exam.getStartingTime(), module, DeadlineType.FINAL_EXAM, "Final Exam"
                ));
            }
        }

        entries.removeIf(e -> filterModuleId != null && !e.module().getId().equals(filterModuleId));
        entries.removeIf(e -> filterType != null && e.type() != filterType);

        if (entries.isEmpty()) {
            System.out.println("No upcoming deadlines.");
            return;
        }

        Comparator<DeadlineEntry> comparator = groupByModule ?
                Comparator.comparing((DeadlineEntry e) -> e.module().getName()).thenComparing(DeadlineEntry::dateTime)
                : Comparator.comparing(DeadlineEntry::dateTime);

        entries.sort(comparator);

        for (DeadlineEntry e : entries) {
            boolean overdue = e.dateTime().isBefore(LocalDateTime.now()) && e.type() == DeadlineType.WORKSHEET;
            System.out.printf("%-15s %-8s[%d] %s - %s%n", e.dateTime(), overdue ? "overdue" : "", e.module().getId(), e.module().getName(), e.description());
        }
    }

    private DeadlineType readDeadlineType() {
        while (true) {
            System.out.println("1. Worksheet  2. Qualifying Exam  3. Final Exam");
            int choice = readInt("Select type: ");
            switch (choice) {
                case 1: return DeadlineType.WORKSHEET;
                case 2: return DeadlineType.QUALIFYING_EXAM;
                case 3: return DeadlineType.FINAL_EXAM;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

}
