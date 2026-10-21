package de.unistuttgart.ipvs.einf.studytracker;

import de.unistuttgart.ipvs.einf.studytracker.console.MainMenu;
import de.unistuttgart.ipvs.einf.studytracker.console.Page;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleRepository;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.util.JsonStorageService;


public class Main {
    static void main() {
        JsonStorageService jsonStorageService = new JsonStorageService();
        ModuleRepository moduleRepository = new ModuleRepository(jsonStorageService);
        ModuleService moduleService = new ModuleService(moduleRepository);

        MainMenu mainMenu = new MainMenu(moduleService, jsonStorageService);
        Page.setHome(mainMenu);

        Page current = mainMenu;
        while (current != null) {
            current.showDialogue();
            current = current.next();
        }
    }
}
