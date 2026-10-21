package de.unistuttgart.ipvs.einf.studytracker.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.FinalExam;
import de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement.Requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Module {

    private final Long id;

    private String name;
    private String description;
    private String professor;

    private int credits;
    private int weeklyHours;
    private Set<SemesterRotation> availableSemester;

    private ModuleStatus status = ModuleStatus.AVAILABLE;

    private List<Requirement> requirements = new ArrayList<>();

    private boolean eligibleToExam;
    private FinalExam finalExam;
    private int attempts;

    private Double finalGrade;

    @JsonCreator
    public Module(@JsonProperty("id") Long id,
                  @JsonProperty("name") String name,
                  @JsonProperty("description") String description,
                  @JsonProperty("professor") String professor,
                  @JsonProperty("credits") int credits,
                  @JsonProperty("weeklyHours") int weeklyHours,
                  @JsonProperty("availableSemester") Set<SemesterRotation> availableSemester) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.professor = professor;
        this.credits = credits;
        this.weeklyHours = weeklyHours;
        this.availableSemester = availableSemester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Module module = (Module) o;
        return Objects.equals(id, module.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfessor() {
        return professor;
    }
    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public int getCredits() {
        return credits;
    }
    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }
    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public Set<SemesterRotation> getAvailableSemester() {
        return availableSemester;
    }
    public void setAvailableSemester(Set<SemesterRotation> availableSemester) {
        this.availableSemester = availableSemester;
    }

    public ModuleStatus getStatus() {
        return status;
    }
    public void setStatus(ModuleStatus status) {
        this.status = status;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public boolean isEligibleToExam() {
        return eligibleToExam;
    }
    public void setEligibleToExam(boolean eligibleToExam) {
        this.eligibleToExam = eligibleToExam;
    }

    public FinalExam getFinalExam() {
        return finalExam;
    }
    public void setFinalExam(FinalExam finalExam) {
        this.finalExam = finalExam;
    }

    public int getAttempts() {
        return attempts;
    }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }
    public void setFinalGrade(Double finalGrade) {
        this.finalGrade = finalGrade;
    }
}
