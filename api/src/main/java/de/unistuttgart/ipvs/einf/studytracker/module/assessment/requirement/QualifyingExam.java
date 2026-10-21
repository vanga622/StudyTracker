package de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalDateTime;

public final class QualifyingExam implements Requirement {

        @JsonProperty("fulfilled")
        private boolean fulfilled;
        @JsonProperty("startingTime")
        private final LocalDateTime startingTime;
        @JsonProperty("duration")
        private final Duration duration;

        @JsonCreator
        public QualifyingExam(@JsonProperty("fulfilled") boolean fulfilled,
                              @JsonProperty("startingTime") LocalDateTime startingTime,
                              @JsonProperty("duration") Duration duration) {
                this.fulfilled = fulfilled;
                this.startingTime = startingTime;
                this.duration = duration;
        }

        @Override
        public boolean fulfilled() {
                return fulfilled;
        }

        public void setFulfilled(boolean fulfilled) {
                this.fulfilled = fulfilled;
        }

        public LocalDateTime startingTime() {
                return startingTime;
        }

        public Duration duration() {
                return duration;
        }
}