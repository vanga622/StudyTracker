package de.unistuttgart.ipvs.einf.studytracker.accessment;

import de.unistuttgart.ipvs.einf.studytracker.module.Module;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class FinalExam {

    private final Long id;

    private Module module;

    private LocalDate date;
    private LocalTime startingTime;
    private Integer duration;

    private String allowedTools;

    public FinalExam(Long id, Module module, LocalDate date, LocalTime startingTime,
                     Integer duration, String allowedTools) {
        this.id = id;
        this.module = module;
        this.date = date;
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

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartingTime() {
        return startingTime;
    }
    public void setStartingTime(LocalTime startingTime) {
        this.startingTime = startingTime;
    }

    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getAllowedTools() {
        return allowedTools;
    }
    public void setAllowedTools(String allowedTools) {
        this.allowedTools = allowedTools;
    }
}
