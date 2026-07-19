package de.unistuttgart.ipvs.einf.studytracker.assessment.requirement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = QualifyingExam.class, name = "qualifyingExam"),
        @JsonSubTypes.Type(value = Tutorial.class, name = "tutorial")
})
public sealed interface Requirement permits QualifyingExam, Tutorial {
    boolean fulfilled();
}
