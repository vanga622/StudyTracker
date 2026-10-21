package de.unistuttgart.ipvs.einf.studytracker.console;

import de.unistuttgart.ipvs.einf.studytracker.console.modules.ModuleOverview;
import de.unistuttgart.ipvs.einf.studytracker.module.ModuleService;
import de.unistuttgart.ipvs.einf.studytracker.util.JsonStorageService;

import java.io.IOException;

public class MainMenu extends Page {

    private final JsonStorageService jsonStorageService;

    private final ModuleService moduleService;

    public MainMenu(ModuleService moduleService, JsonStorageService jsonStorageService) {
        this.moduleService = moduleService;
        this.jsonStorageService = jsonStorageService;
    }

    @Override
    public void showDialogue() {
        System.out.println("Welcome to the Study Tracker. Please enter the corresponding number to navigate: ");
        System.out.println("1. Module Management");
        System.out.println("2. Track Deadlines");
        System.out.println("3. Performance Overview");
        System.out.println("4. Show data in JSON format");
        checkInput();
    }

    @Override
    public void navigateTo(int input) {
        switch (input) {
            case 1:
                setNextPage(new ModuleOverview(moduleService));
                break;
            case 2:
                setNextPage(new DeadlineOverview(moduleService));
                break;
            case 3:
                setNextPage(new PerformanceOverview(moduleService));
                break;
            case 4:
                showJson();
                backToMain();
                break;
            default:
                System.out.println("Invalid input. Please try again.");
                checkInput();
        }
    }

    private void showJson() {
        try {
            System.out.println(jsonStorageService.printModules());
        } catch (IOException e) {
            System.out.println("Could not read modules file.");
        }
    }
}
