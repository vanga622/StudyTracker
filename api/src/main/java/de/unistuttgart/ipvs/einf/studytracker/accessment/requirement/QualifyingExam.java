package de.unistuttgart.ipvs.einf.studytracker.accessment.requirement;

import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import java.time.LocalDate;
import java.time.LocalTime;

/** Scheinklausur */
public record QualifyingExam(
        Module module,
        boolean fulfilled,
        LocalDate date,
        LocalTime time,
        Integer duration
        ) implements Requirement {
}
