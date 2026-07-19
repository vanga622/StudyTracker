package de.unistuttgart.ipvs.einf.studytracker.assessment.requirement;

import java.time.Duration;
import java.time.LocalDateTime;

/** Scheinklausur */
public record QualifyingExam(
        boolean fulfilled,
        LocalDateTime startingTime,
        Duration duration
        ) implements Requirement {
}
