package de.unistuttgart.ipvs.einf.studytracker.util;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonStorageService {

    private static final JsonMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();


    private static final String FILE_NAME = "data.json";

    private final Path storagePath;

    public JsonStorageService() {
        this.storagePath = Paths.get(".").toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public JsonStorageService(Path storagePath) {
        this.storagePath = storagePath;
    }

    public void writeModules(List<Module> modules) {
        File file = storagePath.toFile();
        try {
            // create new file if it doesn't exist yet
            mapper.writeValue(file, modules);
        } catch (JacksonException e) {
            System.err.println("Error while writing JSON: " + e.getMessage());
        }
    }

    public List<Module> readModules() {
        File file = storagePath.toFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (JacksonException e) {
            System.err.println("Error while reading JSON: " + e.getMessage());
            return null;
        }
    }
}
