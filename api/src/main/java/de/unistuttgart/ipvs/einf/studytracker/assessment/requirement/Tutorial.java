package de.unistuttgart.ipvs.einf.studytracker.assessment.requirement;

import java.util.List;

public record Tutorial(
        boolean fulfilled,
        List<Worksheet> worksheets
        ) implements Requirement {

}
