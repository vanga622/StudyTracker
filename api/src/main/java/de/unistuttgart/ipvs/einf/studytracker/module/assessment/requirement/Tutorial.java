package de.unistuttgart.ipvs.einf.studytracker.module.assessment.requirement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class Tutorial implements Requirement {

        @JsonProperty("name")
        private final String name;
        @JsonProperty("fulfilled")
        private boolean fulfilled;
        @JsonProperty("worksheets")
        private final List<Worksheet> worksheets;

        @JsonCreator
        public Tutorial(@JsonProperty("name") String name,
                        @JsonProperty("fulfilled") boolean fulfilled,
                        @JsonProperty("worksheets") List<Worksheet> worksheets) {
                this.name = name;
                this.fulfilled = fulfilled;
                this.worksheets = worksheets;
        }

        public String name() { return name; }
        @Override public boolean fulfilled() { return fulfilled; }
        public void setFulfilled(boolean fulfilled) { this.fulfilled = fulfilled; }
        public List<Worksheet> worksheets() { return worksheets; }
}