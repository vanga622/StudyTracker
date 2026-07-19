package de.unistuttgart.ipvs.einf.studytracker.module;

import de.unistuttgart.ipvs.einf.studytracker.assessment.FinalExam;
import de.unistuttgart.ipvs.einf.studytracker.assessment.requirement.Requirement;
import de.unistuttgart.ipvs.einf.studytracker.assessment.requirement.Tutorial;
import de.unistuttgart.ipvs.einf.studytracker.assessment.requirement.Worksheet;
import de.unistuttgart.ipvs.einf.studytracker.util.JsonStorageService;

import java.time.LocalDateTime;
import java.util.*;

public class ModuleService {

    private final JsonStorageService jsonStorageService;

    public ModuleService(JsonStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
    }

    // -- Module CRUD --

    public Module createModule(Long id, String name, String description, int credits,int weeklyHours,Set<SemesterRotation> availableSemester) {
        Map<Long, Module> modules = jsonStorageService.readModules();
        if (modules.containsKey(id)) {
            throw new IllegalStateException("Module with id " + id + " already exists");
        }
        Module module = new Module(id, name, description, credits, weeklyHours, availableSemester);
        modules.put(id, module);
        jsonStorageService.writeModules(modules);
        return module;
    }

    public void updateModule(Long moduleId, Module module) {
        Module oldModule = findModule(moduleId);
        if (!oldModule.getId().equals(module.getId())) {
            throw new IllegalStateException("Module id does not match");
        }
        saveModule(module);
    }

    public void removeModule(Long moduleId) {
        Map<Long, Module> modules = jsonStorageService.readModules();
        modules.remove(moduleId);
        jsonStorageService.writeModules(modules);
    }

    public Module findModule(Long id) {
        Map<Long, Module> modules = jsonStorageService.readModules();
        Module module = modules.get(id);
        if (module == null) {
            throw new IllegalStateException("Module with id " + id + " does not exist");
        }
        return module;
    }

    private void saveModule(Module module) {
        Map<Long, Module> modules = jsonStorageService.readModules();
        modules.put(module.getId(), module);
        jsonStorageService.writeModules(modules);
    }

    // -- adding objects into modules --

    public void addRequirement(Long moduleId, Requirement req) {
        Module module = findModule(moduleId);
        module.getRequirements().add(req);
        saveModule(module);
    }

    public void setFinalExam(Long moduleId, FinalExam exam) {
        Module module = findModule(moduleId);
        module.setFinalExam(exam);
        saveModule(module);
    }

    // -- Worksheet --

    public void addWorksheet(Long moduleId, String tutorialName, String description, LocalDateTime deadline) {
        Module module = findModule(moduleId);

        Tutorial tutorial = module.getRequirements().stream()
                .filter(Tutorial.class::isInstance)
                .map(Tutorial.class::cast)
                .filter(t -> t.name().equals(tutorialName))
                .findFirst()
                .orElseGet(() -> {
                    Tutorial newTutorial = new Tutorial(tutorialName, false, new ArrayList<>());
                    module.getRequirements().add(newTutorial);
                    return newTutorial;
                });

        Long id = nextWorksheetId(tutorial.worksheets());
        Worksheet ws = new Worksheet(id, description, deadline);
        tutorial.worksheets().add(ws);
        saveModule(module);
    }

    public void setWorksheetCompletion(Long moduleId, String tutorialName, Long worksheetId, boolean completed) {
        Module module = findModule(moduleId);
        Tutorial tutorial = module.getRequirements().stream()
                .filter(Tutorial.class::isInstance)
                .map(Tutorial.class::cast)
                .filter(t -> t.name().equals(tutorialName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tutorial with name " + tutorialName + " not found"));

        Worksheet ws = tutorial.worksheets().stream()
                .filter(m -> m.getId().equals(worksheetId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Worksheet with id " + worksheetId + " not found"));

        ws.setDone(completed);
        saveModule(module);
    }

    public static Long nextWorksheetId(List<Worksheet> worksheets) {
        return worksheets.stream()
                .map(Worksheet::getId)
                .max(Comparator.naturalOrder())
                .map(id -> id + 1)
                .orElse(1L);
    }
}
