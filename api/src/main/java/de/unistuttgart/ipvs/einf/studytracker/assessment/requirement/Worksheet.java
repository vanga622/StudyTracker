package de.unistuttgart.ipvs.einf.studytracker.assessment.requirement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class Worksheet {

    private final Long id;
    private String description;
    private LocalDateTime deadline;

    private boolean done;
    private boolean passed;

    @JsonCreator
    public Worksheet(@JsonProperty("id") Long id,
                     @JsonProperty("description") String description,
                     @JsonProperty("deadline") LocalDateTime deadline) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Worksheet ws = (Worksheet) o;
        return Objects.equals(id, ws.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return done;
    }
    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isPassed() {
        return passed;
    }
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
