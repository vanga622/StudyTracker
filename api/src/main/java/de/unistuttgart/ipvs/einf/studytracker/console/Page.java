package de.unistuttgart.ipvs.einf.studytracker.console;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public abstract class Page {

    protected static final Scanner scanner = new Scanner(System.in);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static Page home;
    private Page nextPage = this;

    public Page next() {
        return nextPage;
    }

    public static void setHome(Page homePage) {
        home = homePage;
    }

    protected void setNextPage(Page page) {
        this.nextPage = page;
    }

    /**
     * Displays the page and all possible commands,
     * including an option to go back to the main menu in the subpages.
     * Invalid input is taken care of.
     */
    public abstract void showDialogue();

    /**
     * Call the next page/action as prompted. If no case matched, user is sent back to checkInput().
     *
     * @param input User prompt
     */
    public abstract void navigateTo(int input);

    /**
     * Check and verifying input. After all edge case checks passed, call the navigateTo(input) method.
     */
    protected void checkInput() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                int input = Integer.parseInt(line);
                navigateTo(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    /**
     * This is the simplified version of the method below. Defaulting Y/y as a yes, and N/n a no.
     *
     * @param question Yes/no question
     * @return User said yes.
     */
    protected boolean askYesNo(String question) {
        return askYesNo(question, "[Y]", "[N]");
    }

    /**
     * Method to ask the Yes/No question while returning the boolean for easier if-else construction.
     * Not answering the question would prompt the question again until otherwise.
     *
     * @param question The question that prompt the yes/no response
     * @param positive Defining yes in regex.
     * @param negative Defining no in regex.
     * @return User said yes.
     */
    protected boolean askYesNo(String question, String positive, String negative) {
        positive = positive.toUpperCase();
        negative = negative.toUpperCase();
        String answer;
        do {
            String questionGuided = question.trim() + " [y/N] ";
            System.out.print(questionGuided);
            answer = scanner.nextLine().trim().toUpperCase();
            if (answer.isEmpty()) {
                answer = "N";
            }
        } while (!answer.matches(positive) && !answer.matches(negative));
        return answer.matches(positive);
    }

    /**
     * Method to prompt action done and redirect user back to the main menu.
     */
    protected void backToMain() {
        System.out.println("Press Enter key to continue.");
        try {
            scanner.nextLine();
            setNextPage(home);
        } catch (Exception ignored) {
        }
    }

    protected String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    protected String readOptionalString(String label, String currentValue) {
        System.out.printf("%s (leave blank to keep \"%s\"): ", label, currentValue);
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? currentValue : line;
    }

    protected int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    protected int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value <= 0) {
                System.out.println("Value must be greater than zero. Please try again.");
                continue;
            }
            return value;
        }
    }

    protected long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    protected int readOptionalPositiveInt(String label, int currentValue) {
        while (true) {
            System.out.printf("%s (leave blank to keep %d): ", label, currentValue);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return currentValue;
            }
            try {
                int value = Integer.parseInt(line);
                if (value <= 0) {
                    System.out.println("Value must be greater than zero. Please try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    protected LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return LocalDateTime.parse(line, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use yyyy-MM-dd HH:mm.");
            }
        }
    }

    protected LocalDateTime readOptionalDateTime(String label, LocalDateTime currentValue) {
        while (true) {
            System.out.printf("%s (yyyy-MM-dd HH:mm, leave blank to keep %s): ", label, currentValue);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return currentValue;
            }
            try {
                return LocalDateTime.parse(line, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use yyyy-MM-dd HH:mm.");
            }
        }
    }
}
