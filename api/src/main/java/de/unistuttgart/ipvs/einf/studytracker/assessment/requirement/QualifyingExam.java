package de.unistuttgart.ipvs.einf.studytracker.assessment.requirement;

import de.unistuttgart.ipvs.einf.studytracker.module.Module;

import java.time.Duration;
import java.time.LocalDateTime;

/** Scheinklausur */
public record QualifyingExam(
        Module module,
        boolean fulfilled,
        LocalDateTime startingTime,
        Duration duration
        ) implements Requirement {
}
