/**
 * Interface describing abstract user interaction operations.
 * This interface is implemented by the graphical UI for the games.
 * Modified from original, @author Marty Stepp.
 */
public interface UserInterface {
    /**
     * Waits for the user to input a yes/no answer (by typing, clicking, etc.),
     * and returns that answer as a boolean value.
     * @return the answer typed by the user as a boolean (yes is true, no is false)
     */
    boolean nextBoolean();

    // checks if the user has pressed "End Game"
    boolean getRunning();

    /**
     * Waits for the user to input a text value, and returns that answer as a String.
     * @return the answer typed by the user as a String (empty string if no answer typed)
     */
    String nextLine();

    /**
     * Displays the given output message to the user.
     * @param message The message to display.  Assumes not null.
     */
    void print(String message);

    // displays a message above the UI input field
    void promptPrint(String text);

    // various messages that are output by the user interface
    final String PLAY_AGAIN_MESSAGE = "Challenge me again?";
    final String SAVE_MESSAGE = "Shall I remember these games?";
    final String LOAD_MESSAGE = "Shall I recall our previous games?";
    final String SAVE_LOAD_FILENAME_MESSAGE = "What is the file name?";
    final String STATUS_MESSAGE = "Games played: %d\nI won: %d";
}
