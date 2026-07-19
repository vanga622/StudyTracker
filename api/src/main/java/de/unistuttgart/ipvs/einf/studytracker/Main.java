package de.unistuttgart.ipvs.einf.studytracker;

import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import de.unistuttgart.ipvs.einf.studytracker.assessment.requirement.Tutorial;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.module.SemesterRotation;
import de.unistuttgart.ipvs.einf.studytracker.util.JsonStorageService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static void main() {
        JsonStorageService storage = new JsonStorageService();
        ModuleService moduleService = new ModuleService(storage);

        long randomId = ThreadLocalRandom.current().nextLong(10_000, 100_000);

        Module module = moduleService.createModule(
                randomId,"Quantum Physics I", "Finding out if the cat is alive", 9, 6, Set.of(SemesterRotation.WINTER_SEMESTER));
        System.out.println("Created module: " + module.getId() + " " + module.getName());

        moduleService.addRequirement(module.getId(), new Tutorial("Group A", false, new ArrayList<>()));
        System.out.println("Added tutorial requirement");

        moduleService.addWorksheet(module.getId(), "Group A", "Introduction", LocalDateTime.now().plusWeeks(1));
        moduleService.addWorksheet(module.getId(), "Group A", "Wave-Particle Duality", LocalDateTime.now().plusWeeks(2));
        moduleService.addWorksheet(module.getId(), "Group A", "Superposition", LocalDateTime.now().plusWeeks(3));
        System.out.println("Added worksheet");

        // reload from disk
        Map<Long, Module> reloaded = storage.readModules();
        for  (Map.Entry<Long, Module> entry : reloaded.entrySet()) {
            Module reloadedModule = entry.getValue();
            System.out.println("Reloaded: " + reloadedModule.getId() + " " + reloadedModule.getName());
            reloadedModule.getRequirements().forEach(r -> {
                if (r instanceof Tutorial t) {
                    System.out.println("  Tutorial " + t.name() + ", worksheets: " + t.worksheets().size());
                }
            });
        }
    }
}
