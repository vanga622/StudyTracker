package de.unistuttgart.ipvs.einf.studytracker.assessment.requirement;

import de.unistuttgart.ipvs.einf.studytracker.module.Module;
import java.util.List;

public record Tutorial(
        Module module,
        boolean fulfilled,
        List<Worksheet> worksheets
        ) implements Requirement {

}
