package de.unistuttgart.ipvs.einf.studytracker.module;

import java.util.Map;

import de.unistuttgart.ipvs.einf.studytracker.util.JsonStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleRepository {

    private final static Logger log = LoggerFactory.getLogger(ModuleRepository.class);

    private final JsonStorageService jsonStorageService;

    public ModuleRepository(JsonStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
    }

    public Module find(Long id) {
        Map<Long, Module> modules = jsonStorageService.readModules();
        Module module = modules.get(id);
        if (module == null) {
            throw new IllegalStateException("Module with id " + id + " does not exist");
        }
        return module;
    }

    public void save(Module module) {
        Map<Long, Module> modules = jsonStorageService.readModules();
        modules.put(module.getId(), module);
        jsonStorageService.writeModules(modules);
        log.info("Module saved");
    }

    public void save(Map<Long, Module> modules) {
        jsonStorageService.writeModules(modules);
        log.info("Modules saved");
    }

    public Map<Long, Module> getModules() {
        return jsonStorageService.readModules();
    }
}
