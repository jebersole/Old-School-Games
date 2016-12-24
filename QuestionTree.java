/**
 * Creates a new game of Twenty Questions, using a binary tree.
 * A QuestionNode is one binary node within.
 * @fields: won: number of games won by computer, played: total games played.
 */
import java.util.Scanner;
import java.io.PrintStream;

public class QuestionTree {
    private UserInterface ui;
    private QuestionNode overallRoot;
    private int played;
    private int won;

    // basic constructor, default setup is one node called computer
    public QuestionTree(UserInterface ui) {
        if (ui == null) {
            throw new IllegalArgumentException();
        }
        this.ui = ui;
        overallRoot = new QuestionNode("computer");
        played = 0;
        won = 0;
    }

    // starts game loop, calling private method
    public void play() {
        play(overallRoot);
        played++;
    }

    // recursively builds game tree according to user responses
    private void play(QuestionNode current) {
        boolean answer = false;
        if (current.left == null && current.right == null) { // build new tree
            if (!ui.getRunning()) {
                return;
            } // true if user has pressed "End Game"
            ui.promptPrint("Would your object happen to be a " + current.data + "? ");
            if (ui.getRunning() && ui.nextBoolean()) { // user answered "Yes"
                ui.promptPrint("I win!");
                won++; // computer wins
            } else { // user says "No," so update tree, resulting in computer "learning"
                if (!ui.getRunning()) {
                    return;
                }
                ui.promptPrint("I lose. What is your object? ");
                String object = ui.nextLine();
                if (!ui.getRunning()) {
                    return;
                }
                ui.promptPrint("Type a yes/no question to distinguish your item from " +
                    current.data + ": ");
                if (!ui.getRunning()) {
                    return;
                }
                String question = ui.nextLine();
                ui.promptPrint("And what is the answer for your object? ");
                if (!ui.getRunning()) {
                    return;
                }
                answer = ui.nextBoolean();
                current = addQA(question, answer, object, current);
            }
        } else { // continue adding to existing tree
            if (!ui.getRunning()) {
                return;
            }
            ui.promptPrint(current.data + " ");
            if (ui.nextBoolean()) {
                play(current.left);
            } else {
                play(current.right);
            }
        }
    }

    // updates tree to reflect new user responses
    private QuestionNode addQA(String question, boolean answer, String object, QuestionNode current) {
        if (answer) {
            current.left = new QuestionNode(object);
            current.right = new QuestionNode(current.data);
        } else {
            current.right = new QuestionNode(object);
            current.left = new QuestionNode(current.data);
        }
        current.data = question;
        return current;
    }

    // saves to file, recursively writing tree through private method
    public void save(PrintStream output) {
        if (output == null) {
            throw new IllegalArgumentException();
        }
        save(output, overallRoot);
    }

    // writes text file
    private void save(PrintStream output, QuestionNode root) {
        if (root != null) {
            if (root.data.contains("?")) {
                output.print("Q:" + root.data + "\n");
            } else {
                output.print("A:" + root.data + "\n");
            }
            save(output, root.left);
            save(output, root.right);
        }
    }

    // loads in a file, recursively building tree through private method
    public void load(Scanner input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        overallRoot = load(input, overallRoot);
    }

    // tree generated from text file
    private QuestionNode load(Scanner input, QuestionNode root) {
        if (input.hasNextLine()) {
            String line = input.nextLine();
            root = new QuestionNode(line);
            if (root.data.contains("Q:")) {
                root.left = load(input, root.left);
                root.right = load(input, root.right);
            }
            root.data = root.data.substring(2);
        }
        return root;
    }

    public int totalGames() {
        return played;
    }

    public int gamesWon() {
        return won;
    }

}
