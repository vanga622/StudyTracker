package de.unistuttgart.ipvs.einf.studytracker.accessment;

import de.unistuttgart.ipvs.einf.studytracker.module.Module;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class FinalExam {

    private final Long id;

    private Module module;

    private LocalDateTime startingTime;
    private Duration duration;

    private String allowedTools;

    public FinalExam(Long id, Module module, LocalDateTime startingTime,
                     Duration duration, String allowedTools) {
        this.id = id;
        this.module = module;
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

    public Module getModule() {
        return module;
    }
    public void setModule(Module module) {
        this.module = module;
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
