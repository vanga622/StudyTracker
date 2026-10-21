package de.unistuttgart.ipvs.einf.studytracker.module.assessment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class FinalExam {

    private final Long id;

    private LocalDateTime startingTime;
    private Duration duration;

    private String allowedTools;

    @JsonCreator
    public FinalExam(@JsonProperty("id") Long id,
                     @JsonProperty("startingTime") LocalDateTime startingTime,
                     @JsonProperty("duration") Duration duration,
                     @JsonProperty("allowedTools") String allowedTools) {
        this.id = id;
        this.startingTime = startingTime;
        this.duration = duration;
        this.allowedTools = allowedTools;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FinalExam exam = (FinalExam) o;
        return Objects.equals(id, exam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }
    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getAllowedTools() {
        return allowedTools;
    }
    public void setAllowedTools(String allowedTools) {
        this.allowedTools = allowedTools;
    }
}
