package de.unistuttgart.ipvs.einf.studytracker.module;

import de.unistuttgart.ipvs.einf.studytracker.module.assessment.FinalExam;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.QualifyingExam;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Requirement;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Tutorial;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Worksheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ModuleService {

    private final static Logger log = LoggerFactory.getLogger(ModuleService.class);

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    // -- Module Management --

    public Module createModule(Long id,
                               String name,
                               String description,
                               String professor,
                               int credits,
                               int weeklyHours,
                               Set<SemesterRotation> availableSemester) {
        Map<Long, Module> modules = moduleRepository.getModules();
        if (modules.containsKey(id)) {
            throw new IllegalStateException("Module with id " + id + " already exists");
        }
        Module module = new Module(id, name, description, professor, credits, weeklyHours, availableSemester);
        modules.put(id, module);
        moduleRepository.save(modules);
        return module;
    }

    public Module getModule(Long id) {
        return moduleRepository.find(id);
    }

    public List<Module> getAllModules() {
        return new ArrayList<>(moduleRepository.getModules().values());
    }

    public void updateModule(Long moduleId, Module module) {
        moduleRepository.find(moduleId); // ensures a module with this id exists
        if (!moduleId.equals(module.getId())) {
            throw new IllegalStateException("Module id does not match");
        }
        moduleRepository.save(module);
        log.info("Module with id {} updated", moduleId);
    }

    public void removeModule(Long moduleId) {
        Map<Long, Module> modules = moduleRepository.getModules();
        modules.remove(moduleId);
        moduleRepository.save(modules);
        log.info("Module with id {} removed", moduleId);
    }

    public void setModuleStatus(Long moduleId, ModuleStatus moduleStatus) {
        Module module = moduleRepository.find(moduleId);
        module.setStatus(moduleStatus);
        moduleRepository.save(module);
    }

    // -- Final Exam Management --

    public FinalExam createFinalExam(Long id, LocalDateTime startingTime, int durationMinutes, String allowedTools) {
        if  (id == null || startingTime == null) {
            throw new NullPointerException("id or startingTime must not be null");
        }
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("durationMinutes must not be negative");
        }

        return new FinalExam(id, startingTime, Duration.ofMinutes(durationMinutes), allowedTools);
    }

    public FinalExam findFinalExam(Long id) {
        if  (id == null) {
            throw new NullPointerException("id must not be null");
        }
        Map<Long, Module> modules = moduleRepository.getModules();
        for  (Map.Entry<Long, Module> entry : modules.entrySet()) {
            FinalExam exam = entry.getValue().getFinalExam();
            if (exam != null && exam.getId().equals(id)) {
                return exam;
            }
        }
        return null;
    }

    public FinalExam getFinalExamByModule(Long moduleId) {
        Module module = moduleRepository.find(moduleId);
        return module.getFinalExam();
    }

    public void setFinalExam(Long moduleId, FinalExam exam) {
        Module module = moduleRepository.find(moduleId);
        module.setFinalExam(exam);
        moduleRepository.save(module);
        log.info("Module with id {} updated final exam {}", moduleId, exam.getId());
    }

    public void removeFinalExam(Long moduleId) {
        Module module = moduleRepository.find(moduleId);
        module.setFinalExam(null);
        moduleRepository.save(module);
        log.info("Final exam of module with id {} removed", moduleId);
    }

    public void setFinalGrade(Long moduleId, Double grade) {
        Module module = moduleRepository.find(moduleId);
        module.setFinalGrade(grade);
        moduleRepository.save(module);
        log.info("Module with id {} updated grade {}", moduleId, grade);
    }

    // -- Object Management in Modules --

    public void addRequirement(Long moduleId, Requirement req) {
        Module module = moduleRepository.find(moduleId);
        module.getRequirements().add(req);
        moduleRepository.save(module);
        log.info("Module with id {} updated requirement", moduleId);
    }

    public void setRequirementStatus(Long moduleId, Requirement req, boolean status) {
        Module module = moduleRepository.find(moduleId);
        if (req instanceof QualifyingExam) {
            module.getRequirements().stream()
                    .filter(QualifyingExam.class::isInstance)
                    .map(QualifyingExam.class::cast)
                    .findFirst()
                    .ifPresent(exam -> exam.setFulfilled(status));
        } else if (req instanceof Tutorial t) {
            module.getRequirements().stream()
                    .filter(Tutorial.class::isInstance)
                    .map(Tutorial.class::cast)
                    .filter(other -> other.name().equals(t.name()))
                    .findFirst()
                    .ifPresent(tutorial -> tutorial.setFulfilled(status));
        }
        moduleRepository.save(module);
        log.info("Module with id {} requirement fulfilled = {}", moduleId, status);
    }

    public void removeRequirement(Long moduleId, Requirement req) {
        if (req instanceof QualifyingExam) {
            removeQualifyingExam(moduleId);
        } else if (req instanceof Tutorial t) {
            removeTutorial(moduleId, t.name());
        }
    }

    public void removeQualifyingExam(Long moduleId) {
        Module module = moduleRepository.find(moduleId);
        boolean removed = module.getRequirements()
                .removeIf(QualifyingExam.class::isInstance);

        if (removed) {
            moduleRepository.save(module);
            log.info("Module with id {} updated qualifying exam", moduleId);
        }
    }

    public void removeTutorial(Long moduleId, String tutorialName) {
        Module module = moduleRepository.find(moduleId);
        boolean removed = module.getRequirements().removeIf(r ->
                r instanceof Tutorial t && t.name().equals(tutorialName));

        if (removed) {
            moduleRepository.save(module);
            log.info("Module with id {} updated tutorial", moduleId);
        }
    }

    private static Tutorial getTutorial(String tutorialName, Module module) {
        return module.getRequirements().stream()
                .filter(Tutorial.class::isInstance)
                .map(Tutorial.class::cast)
                .filter(t -> t.name().equals(tutorialName))
                .findFirst()
                .orElseGet(() -> {
                    Tutorial newTutorial = new Tutorial(tutorialName, false, new ArrayList<>());
                    module.getRequirements().add(newTutorial);
                    return newTutorial;
                });
    }

    // -- Worksheets --

    public void addWorksheet(Long moduleId, String tutorialName, String description, LocalDateTime deadline) {
        Module module = moduleRepository.find(moduleId);

        Tutorial tutorial = getTutorial(tutorialName, module);

        Long id = nextWorksheetId(tutorial.worksheets());
        Worksheet ws = new Worksheet(id, description, deadline);
        tutorial.worksheets().add(ws);
        moduleRepository.save(module);
        log.info("Module with id {} added worksheet {}", moduleId, id);
    }

    public void addWorksheetWithId(Long moduleId, String tutorialName,
                                   Long id, String description, LocalDateTime deadline) {
        Module module = moduleRepository.find(moduleId);

        Tutorial tutorial = getTutorial(tutorialName, module);

        if (tutorial.worksheets().stream().anyMatch(ws -> ws.getId().equals(id))) {
            throw new IllegalArgumentException("Worksheet with id " + id + " already exists");
        }

        Worksheet ws = new Worksheet(id, description, deadline);
        tutorial.worksheets().add(ws);
        moduleRepository.save(module);
        log.info("Module with id {} added worksheet {}", moduleId, id);
    }

    public void setWorksheetCompletion(Long moduleId, String tutorialName, Long worksheetId, boolean completed) {
        Module module = moduleRepository.find(moduleId);

        Worksheet ws = getWorksheetOrElseThrow(tutorialName, worksheetId, module);

        ws.setDone(completed);
        moduleRepository.save(module);
        log.info("Module {} with worksheet {} is {}", moduleId, worksheetId, completed ? "completed" : "not done");
    }

    public void setWorksheetStatus(Long moduleId, String tutorialName, Long worksheetId, boolean passed) {
        Module module = moduleRepository.find(moduleId);

        Worksheet ws = getWorksheetOrElseThrow(tutorialName, worksheetId, module);

        ws.setPassed(passed);
        moduleRepository.save(module);
        log.info("Module {} with worksheet {} is {}", moduleId, worksheetId, passed ? "passed" : "not passed");
    }

    public void removeWorksheet(Long moduleId, String tutorialName, Long worksheetId) {
        Module module = moduleRepository.find(moduleId);
        Tutorial tutorial = getTutorial(tutorialName, module);
        boolean removed = tutorial.worksheets().removeIf(ws -> ws.getId().equals(worksheetId));
        if (!removed) {
            throw new IllegalStateException("Worksheet with id " + worksheetId + " not found");
        }
        moduleRepository.save(module);
        log.info("Module with id {} removed worksheet {}", moduleId, worksheetId);
    }

    public void updateWorksheet(Long moduleId, String tutorialName, Long worksheetId,
                                String description, LocalDateTime deadline) {
        Module module = moduleRepository.find(moduleId);
        Worksheet ws = getWorksheetOrElseThrow(tutorialName, worksheetId, module);
        ws.setDescription(description);
        ws.setDeadline(deadline);
        moduleRepository.save(module);
        log.info("Module with id {} updated worksheet {}", moduleId, worksheetId);
    }

    public Worksheet getWorksheetOrElseThrow(String tutorialName, Long worksheetId, Module module) {
        Tutorial tutorial = module.getRequirements().stream()
                .filter(Tutorial.class::isInstance)
                .map(Tutorial.class::cast)
                .filter(t -> t.name().equals(tutorialName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tutorial with name " + tutorialName + " not found"));

        return tutorial.worksheets().stream()
                .filter(m -> m.getId().equals(worksheetId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Worksheet with id " + worksheetId + " not found"));
    }

    private static Long nextWorksheetId(List<Worksheet> worksheets) {
        return worksheets.stream()
                .map(Worksheet::getId)
                .max(Comparator.naturalOrder())
                .map(id -> id + 1)
                .orElse(1L);
    }

    public boolean exists(Long id) {
        return moduleRepository.getModules().containsKey(id);
    }
}
