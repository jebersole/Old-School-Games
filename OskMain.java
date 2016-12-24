// This GUI is a modified version of one written by Marty Stepp of Stanford.
// The program also contains some of his content, such as dictionaries.
import java.applet.AudioClip;
import java.applet.Applet;
import java.awt.Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JCheckBox;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OskMain implements ActionListener, KeyListener, Runnable, UserInterface {

    // sound options
    private static final boolean MUSIC = true;
    private static final boolean SOUNDFX = true;

    private static final String ADICTIONARY_FILE = "adictionary.txt"; // Anagrams
    private static final String HDICTIONARY_FILE = "hdictionary.txt"; // Hangman
    private static final String SENTENCE_FILE = "sentence.txt"; // Random Sentence

    private static final String MUSIC_MESSAGE = "Music";
    private static final String SFX_MESSAGE = "Sound";
    private static final String YES_MESSAGE = "Yes";
    private static final String NO_MESSAGE = "No";
    private static final String END_MESSAGE = "End Game";
    private static String TITLE = "Old School Games";
    private static String CURRENT_LABEL = "What would you like to play next?";
    // Possibilities: What would you like to play next?, Anagrams, Evil Hangman, Random Sentence, Twenty Questions

    // file names, paths, URLs
    private static final String SAVE_DEFAULT_FILE_NAME = "animals.txt";
    private static final String GAME_MUSIC_FILENAME = "forest.wav";
    private static final String TITLE_MUSIC_FILENAME = "gameland.wav";
    private static String BACKGROUND_IMAGE_FILE_NAME = "astroblast.jpg";
    private static String SOUND_FILE_NAME = "blip.wav";

    // visual elements
    private static final Font FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Color COLOR = new Color(6, 226, 240); // light teal

    private volatile boolean running = false; // if user is playing a game or not

    // runs the program
    public static void main(String[] args) throws FileNotFoundException {
        new OskMain();
    }

    // GUI components
    private JFrame frame;
    private JLabel osk, bannerLabel;
    private JTextArea statsArea, messageLabel, promptLabel;
    private JTextField inputField;
    private JButton yesButton, noButton, endButton, anaButton, twqButton, evilhButton, rsentButton;
    private JCheckBox musicBox, soundBox;
    private AudioClip titleMusic, gameMusic;

    // these queues hold boolean or String user input waiting to be read
    private BlockingQueue<Boolean> booleanQueue = new LinkedBlockingQueue<Boolean>();
    private BlockingQueue<String> stringQueue = new LinkedBlockingQueue<String>();
    private boolean waitingForBoolean = false;
    private boolean waitingForString = false;

    // constructs and sets up GUI and components
    public OskMain() throws FileNotFoundException {

        // construct everybody
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.addKeyListener(this);

        osk = new JLabel();
        osk.setLayout(null);
        ImageIcon img = new ImageIcon(ClassLoader.getSystemResource("icon.png"));
        frame.setIconImage(img.getImage());
        osk.setIcon(new ImageIcon(ClassLoader.getSystemResource(BACKGROUND_IMAGE_FILE_NAME)));

        // layout
        frame.add(osk);
        frame.pack();
        center(frame);

        // construct other components
        inputField = new JTextField(30);
        setupComponent(inputField, new Point(30, 205), new Dimension(300, 25));
        inputField.setCaretColor(Color.GREEN);
        inputField.addActionListener(this);

        messageLabel = new JTextArea();
        messageLabel.setLocation(new Point(485, 120));
        messageLabel.setSize(new Dimension(500, 500));
        messageLabel.setForeground(COLOR);
        messageLabel.setFont(FONT);
        messageLabel.setOpaque(false);
        messageLabel.setLineWrap(true);
        messageLabel.setWrapStyleWord(true);
        messageLabel.setEditable(false);
        messageLabel.setFocusable(false);
        JScrollPane scroller = new JScrollPane(messageLabel);

        scroller.setBounds(530, 100, 300, 300);
        osk.add(scroller);
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
        scroller.getVerticalScrollBar().setBackground(Color.black);

        promptLabel = new JTextArea();
        setupComponent(promptLabel, new Point(30, 120), new Dimension(300, 75));
        promptLabel.setLineWrap(true);
        promptLabel.setWrapStyleWord(true);
        promptLabel.setEditable(false);
        promptLabel.setFocusable(false);

        bannerLabel = new JLabel();
        setupComponent(bannerLabel, new Point(0, 0), new Dimension(osk.getWidth(), 30));
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statsArea = new JTextArea();
        setupComponent(statsArea,
            new Point(osk.getWidth() - 200, osk.getHeight() - 50),
            new Dimension(200, 50));
        statsArea.setEditable(false);
        statsArea.setFocusable(false);

        evilhButton = makeButton("Evil Hangman", new Point(10, osk.getHeight() - 85), new Dimension(180, 60));
        twqButton = makeButton("Twenty Questions", new Point(200, osk.getHeight() - 85), new Dimension(220, 60));
        anaButton = makeButton("Anagrams", new Point(430, osk.getHeight() - 85), new Dimension(140, 60));
        rsentButton = makeButton("Random Sentence", new Point(580, osk.getHeight() - 85), new Dimension(220, 60));
        yesButton = makeButton(YES_MESSAGE, new Point(30, 180), new Dimension(80, 30));
        noButton = makeButton(NO_MESSAGE, new Point(120, 180), new Dimension(80, 30));
        endButton = makeButton(END_MESSAGE, new Point(30, osk.getHeight() - 218), new Dimension(180, 60));
        yesButton.addKeyListener(this);
        noButton.addKeyListener(this);

        musicBox = makeCheckBox(MUSIC_MESSAGE, MUSIC,
            new Point(810, osk.getHeight() - 85),
            new Dimension(120, 20));
        soundBox = makeCheckBox(SFX_MESSAGE, SOUNDFX,
            new Point(810, osk.getHeight() - 65),
            new Dimension(120, 20));
        musicBox.addKeyListener(this);
        soundBox.addKeyListener(this);

        doEnabling();
        bannerLabel.setText("Welcome to Old School Games! Please choose a game.");
        frame.setVisible(true);

        // start background thread to loop and play the actual games
        // (it has to be in a thread so that the game loop can wait without
        // the GUI locking up)
        new Thread(this).start();
    }

    /** Handles user interactions with the graphical components. */
    public void actionPerformed(ActionEvent event) {
        Object src = event.getSource();
        playSound();
        if (src == yesButton) {
            yes();
        } else if (src == noButton) {
            no();
        } else if (src == inputField) {
            input();
        } else if (src == anaButton) {
            runAnagrams(this);
        } else if (src == evilhButton) {
            runHangman();
        } else if (src == rsentButton) {
            runSentence();
        } else if (src == twqButton) {
            runTwentyQuestions(this);
        } else if (src == endButton) {
            endGame();
            playMusic();
        } else if (src == musicBox) {
            playMusic();
        }
    }

    /** Part of the KeyListener interface.  Responds to key presses. */
    public void keyPressed(KeyEvent event) {
        if (!yesButton.isVisible() || event.isAltDown() || event.isControlDown()) {
            return;
        }
        char key = Character.toLowerCase(event.getKeyChar());
        if (key == 'y') {
            yes();
        } else if (key == 'n') {
            no();
        }
    }

    /** Part of the KeyListener interface.  Responds to key releases. */
    public void keyReleased(KeyEvent event) {}

    /** Part of the KeyListener interface.  Responds to key typing. */
    public void keyTyped(KeyEvent event) {}

    /** Waits for the user to type a line of text and returns that line. */
    public String nextLine() {
        return nextLine(null);
    }

    /** Outputs the given text onto the GUI. */
    public void print(String text) {
        if (!running) {
            return;
        }
        messageLabel.setText(text);
    }

    public void promptPrint(String text) {
        if (!running) {
            return;
        }
        promptLabel.setText(text);
    }

    /* The basic game loop, which will be run in a separate thread. */
    public void run() {
        playMusic();
    }

    public void runTwentyQuestions(UserInterface ui) {
        new Thread(new Runnable() {
            public void run() {
                QuestionTree game = new QuestionTree(ui);
                running = true;
                CURRENT_LABEL = "Twenty Questions";
                doEnabling();
                playMusic();
                saveLoad(false, game);
                if (!running) {
                    return;
                }
                do {
                    game.play();
                    print(PLAY_AGAIN_MESSAGE);
                } while (running && nextBoolean());
                if (!running) {
                    return;
                }
                saveLoad(true, game);
                promptPrint("");
                CURRENT_LABEL = "What would you like to play next?";
                setWaitingForBoolean(false);
                running = false;
                playMusic();
                doEnabling();
            }
        }).start();
    }

    public void runSentence() {
        new Thread(new Runnable() {
            public void run() {
                running = true;
                CURRENT_LABEL = "Random Sentence";
                doEnabling();
                playMusic();
                ArrayList<String> lines = null;
                try {
                    lines = readLines(SENTENCE_FILE);
                } catch (FileNotFoundException e) {
                    print("File not found.");
                    return;
                }
                // construct grammar solver and begin user input loop
                GrammarSolver solver = new GrammarSolver(Collections.unmodifiableList(lines));
                do {
                    // repeatedly prompt for symbols to generate, and generate them
                    promptPrint("How many sentences would you like to generate?");
                    int num = 0;
                    while (running && num <= 0) {
                        try {
                            num = Integer.parseInt(nextLine());
                        } catch (NumberFormatException e) {
                            promptPrint("Please enter a number.");
                        }
                    }
                    if (!running) {
                        return;
                    }
                    String result = "";
                    for (int i = 0; i < num; i++) {
                        String sentence = solver.generate();
                        result += sentence;
                    }
                    print(result);
                } while (running);
                promptPrint("");
                CURRENT_LABEL = "What would you like to play next?";
                setWaitingForBoolean(false);
                running = false;
                doEnabling();
                playMusic();
            }
        }).start();
    }

    // main Evil Hangman loop
    public void runHangman() {
        new Thread(new Runnable() {
            public void run() {
                running = true;
                CURRENT_LABEL = "Evil Hangman";
                doEnabling();
                playMusic();
                print("Welcome to Evil Hangman.");
                // open the dictionary file and read dictionary into an ArrayList
                InputStream in = getClass().getClassLoader().getResourceAsStream(HDICTIONARY_FILE);
                Scanner input;
                try {
                    input = new Scanner(in);
                } catch (NullPointerException e) {
                    print("No dictionary file found.");
                    return;
                }
                ArrayList<String> hdictionary = new ArrayList<String>();
                while (running && input.hasNext()) {
                    hdictionary.add(input.next().toLowerCase());
                }

                // set basic parameters
                do {
                    int length = 0;
                    int max = 0;
                    String message = "";
                    promptPrint("What length word do you want to use?");
                    while (running && length <= 0) {
                        try {
                            length = Integer.parseInt(nextLine());
                        } catch (NumberFormatException e) {
                            promptPrint("Please enter a number.");
                        }
                    }
                    promptPrint("How many wrong answers allowed?");
                    while (running && max <= 0) {
                        try {
                            max = Integer.parseInt(nextLine());
                        } catch (NumberFormatException e) {
                            promptPrint("Please enter a number.");
                        }
                    }
                    if (!running) {
                        return;
                    }
                    // set up the HangmanManager and start the game
                    HangmanManager hangman;
                    try {
                        hangman = new HangmanManager(hdictionary, length, max);
                    } catch (IllegalArgumentException e) {
                        print(e.getMessage());
                        return;
                    }
                    if (hangman.words().isEmpty()) {
                        promptPrint("No words of that length in the dictionary. Try again?");
                    } else { // play game
                        while (running && hangman.guessesLeft() > 0 && hangman.pattern().contains("-")) {
                            print("guesses : " + hangman.guessesLeft() +
                                "\nguessed : " + hangman.guesses() +
                                "\ncurrent : " + hangman.pattern());
                            if (hangman.guessesLeft() == max) {
                                promptPrint("Your guess?");
                            } else {
                                promptPrint(message + ".\nYour guess?");
                            }
                            char ch = nextLine().toLowerCase().charAt(0);
                            try {
                                int count = hangman.record(ch);
                                if (count == 0) {
                                    message = "Sorry, there are no " + ch + "'s";
                                } else if (count == 1) {
                                    message = "Yes, there is one " + ch;
                                } else {
                                    message = "Yes, there are " + count + " " + ch +
                                        "'s";
                                }
                            } catch (Exception exc) {
                                // IllegalArgumentException: "You've already guessed that!"
                                // if already guessed, message will be printed in next iteration
                                // IllegalStateException: "Zero guesses or empty list"
                                message = exc.getMessage();
                                if (message.charAt(0) == 'Z') {
                                    print(message);
                                    return;
                                }
                            }
                        }
                        if (!running) {
                            return;
                        }
                        //show results
                        String answer = hangman.words().iterator().next();
                        print("The answer was: " + answer);
                        if (hangman.guessesLeft() > 0) {
                            promptPrint("You beat me.\nWould you like to play again?");
                        } else {
                            promptPrint("Sorry, you lose.\nWould you like to play again?");
                        }
                    }
                } while (nextBoolean());
                promptPrint("");
                CURRENT_LABEL = "What would you like to play next?";
                setWaitingForBoolean(false);
                running = false;
                playMusic();
                doEnabling();
            }
        }).start();
    }

    // main Anagrams loop
    public void runAnagrams(UserInterface ui) {
        new Thread(new Runnable() {
            public void run() {
                running = true;
                InputStream in = getClass().getClassLoader().getResourceAsStream(ADICTIONARY_FILE);
                Scanner input;
                try {
                    input = new Scanner(in);
                } catch (NullPointerException e) {
                    print("No dictionary file found.");
                    return;
                }
                Set<String> dictionary = new TreeSet<String>();
                while (input.hasNextLine()) {
                    dictionary.add(input.nextLine());
                }
                dictionary = Collections.unmodifiableSet(dictionary); // read-only
                // create Anagrams object for, well, solving anagrams
                Anagrams ana;
                try {
                    ana = new Anagrams(dictionary, ui);
                } catch (IllegalArgumentException e) {
                    print(e.getMessage());
                    return;
                }
                CURRENT_LABEL = "Anagrams";
                doEnabling();
                playMusic();
                print("Welcome to Anagrams.");
                promptPrint("Phrase to scramble?\n(Enter to quit)");
                String phrase = nextLine();
                if (!running) {
                    return;
                }
                // loop to get/solve each phrase
                while (phrase.length() > 0) {
                    Set<String> allWords;
                    try {
                        allWords = ana.getWords(phrase);
                    } catch (IllegalArgumentException e) {
                        print(e.getMessage());
                        return;
                    }
                    String display = "All words found in \"" + phrase + "\": ";
                    for (String word: allWords) {
                        display += word + " ";
                    }
                    print(display);
                    promptPrint("Press enter to continue.\n" + "(Up to 200 results will be shown)");
                    String line = nextLine();
                    promptPrint("");
                    print("Computing...");
                    ana.print(phrase); //  all anagrams of phrase

                    // get next phrase to solve
                    promptPrint("Phrase to scramble?\n(Enter to quit)");
                    phrase = nextLine();
                }
                promptPrint("");
                CURRENT_LABEL = "What would you like to play next?";
                running = false;
                doEnabling();
                playMusic();
            }
        }).start();
    }

    /** Waits for the user to press Yes or No and returns the boolean. */
    public boolean nextBoolean() {
        setWaitingForBoolean(true);
        try {
            boolean result = booleanQueue.take();
            messageLabel.setText(null);
            return result;
        } catch (InterruptedException e) {
            return false;
        } finally {
            setWaitingForBoolean(false);
        }
    }

    // sets JFrame's position to be in the center of the screen
    private void center(JFrame frame) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screen.width - frame.getWidth()) / 2,
            (screen.height - frame.getHeight()) / 2);
    }

    // turns on/off various graphical components when game events occur
    private void doEnabling() {
        if (!running) {
            waitingForString = false;
            waitingForBoolean = false;
        }
        evilhButton.setEnabled(!running);
        rsentButton.setEnabled(!running);
        anaButton.setEnabled(!running);
        twqButton.setEnabled(!running);
        endButton.setVisible(running);
        inputField.setVisible(waitingForString);
        if (waitingForString) {
            inputField.requestFocus();
            inputField.setCaretPosition(inputField.getText().length());
        }
        yesButton.setVisible(waitingForBoolean);
        noButton.setVisible(waitingForBoolean);
        bannerLabel.setText(CURRENT_LABEL);
    }

    // response to pressing Enter on the input text field; completes user input
    private void input() {
        try {
            // user pressed Enter on input text field; capture input
            String text = inputField.getText();
            inputField.setText(null);
            stringQueue.put(text);
            doEnabling();
        } catch (InterruptedException e) {
            return;
        }
    }

    // helper method to create one button at specified position/size
    private JButton makeButton(String text, Point location, Dimension size) {
        JButton button = new JButton(text);
        button.setMnemonic(text.charAt(0));
        setupComponent(button, location, size);
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.addActionListener(this);
        button.setFocusPainted(false);
        return button;
    }

    // helper method to create one button at specified position/size
    private JCheckBox makeCheckBox(String text, boolean selected, Point location, Dimension size) {
        JCheckBox box = new JCheckBox(text, selected);
        box.setMnemonic(text.charAt(0));
        setupComponent(box, location, size);
        box.setOpaque(true);
        box.setContentAreaFilled(false);
        box.addActionListener(this);
        box.setFocusPainted(false);
        return box;
    }

    // private helper for asking a question with the given initial text
    private String nextLine(String defaultValue) {
        if (!running) {
            return "";
        }
        inputField.setText(defaultValue);
        setWaitingForString(true);
        try {
            // grab/store text from box; clear the box text
            String result = stringQueue.take();
            messageLabel.setText(null);
            return result;
        } catch (InterruptedException e) {
            return "";
        } finally {
            setWaitingForString(false);
        }
    }

    // response to a 'no' button click or typing 'n'
    private void no() {
        try {
            booleanQueue.put(false);
            doEnabling();
        } catch (InterruptedException e) {
            return;
        }
    }

    // loads and plays/loops the sound/music file with the given file name
    private AudioClip playAudioClip(String filename, boolean loop) {
        AudioClip clip = null;
        try {
            clip = Applet.newAudioClip(ClassLoader.getSystemResource(filename));
            if (loop) {
                clip.loop();
            } else {
                clip.play();
            }
        } catch (NullPointerException e) {
            print("Unable to play audio clip.");
        }
        return clip;
    }

    // plays the background theme music
    private void playMusic() {
        if (musicBox.isSelected()) {
            try {
                if (running) {
                    if (titleMusic != null) {
                        titleMusic.stop();
                    }
                    if (gameMusic == null) {
                        gameMusic = playAudioClip(GAME_MUSIC_FILENAME, true);
                    } else {
                        gameMusic.loop();
                    }
                } else {
                    if (gameMusic != null) {
                        gameMusic.stop();
                    }
                    if (titleMusic == null) {
                        titleMusic = playAudioClip(TITLE_MUSIC_FILENAME, true);
                    } else {
                        titleMusic.loop();
                    }
                }
            } catch (NullPointerException e) {
                print("Unable to play music.");
            }
        } else { // music box has been unselected
            if (titleMusic != null) {
                titleMusic.stop();
            }
            if (gameMusic != null) {
                gameMusic.stop();
            }
        }
    }

    private void playSound() {
        if (soundBox.isSelected()) {
            new Thread(new SoundPlayer()).start();
        }
    }

    private void playSoundReally() {
        playAudioClip(SOUND_FILE_NAME, false);
    }

    // common code for asking the user whether they want to save or load
    // if the boolean save is true, it will save; otherwise load
    private void saveLoad(boolean save, QuestionTree game) {
        promptPrint(save ? SAVE_MESSAGE : LOAD_MESSAGE);
        if (nextBoolean()) {
            promptPrint(SAVE_LOAD_FILENAME_MESSAGE);
            String filename = nextLine(SAVE_DEFAULT_FILE_NAME);
            try {
                if (save) {
                    PrintStream out;
                    try {
                        out = new PrintStream(new File(filename));
                    } catch (FileNotFoundException e) {
                        print("Unable to write file.");
                        return;
                    }
                    try {
                        game.save(out);
                    } catch (IllegalArgumentException e) {
                        print("Unable to write file.");
                        return;
                    }
                } else {
                    Scanner input;
                    try {
                        input = new Scanner(new File(filename)); // NullPointer or FileNotFound
                    } catch (Exception e) {
                        print("File not found.");
                        return;
                    }
                    try {
                        game.load(input);
                    } catch (IllegalArgumentException e) {
                        print("File not found.");
                        return;
                    }
                }
            } catch (Exception e) {
                print("File error.");
                return;
            }
        }
    }

    // sets standard fonts, colors, location and such for the given component
    private void setupComponent(JComponent comp, Point location, Dimension size) {
        comp.setLocation(location);
        comp.setSize(size);
        comp.setForeground(COLOR);
        comp.setFont(FONT);
        comp.setOpaque(false);
        osk.add(comp);
    }

    // sets the GUI to wait for a yes/no user input
    private void setWaitingForBoolean(boolean value) {
        waitingForBoolean = value;
        doEnabling();
    }

    // sets the GUI to wait for a text user input
    private void setWaitingForString(boolean value) {
        waitingForString = value;
        doEnabling();
    }

    // response to a 'yes' button click or typing 'y'
    private void yes() {
        try {
            booleanQueue.put(true);
            doEnabling();
        } catch (InterruptedException e) {
            return;
        }
    }

    // ends current game thread after 'End Game' button press
    private void endGame() {
        running = false;
        if (waitingForBoolean) {
            try {
                booleanQueue.put(false);
            } catch (InterruptedException e) {
                return;
            }
        } else if (waitingForString) {
            try {
                stringQueue.put("end");
            } catch (InterruptedException e) {
                return;
            }
        }
        setWaitingForString(false);
        setWaitingForBoolean(false);
        messageLabel.setText("");
        promptLabel.setText("");
        CURRENT_LABEL = "What would you like to play next?";
        endButton.setVisible(false);
        doEnabling();
    }

    // runnable thread object so sounds play in background and don't lock up UI
    private class SoundPlayer implements Runnable {
        public void run() {
            playSoundReally();
        }
    }

    public boolean getRunning() {
        return running;
    }

    // Reads text from the file with the given name and returns as a List.
    // Strips empty lines and trims leading/trailing whitespace from each line.
    // pre: a file with the given name exists, throws FileNotFoundException otherwise
    private ArrayList<String> readLines(String fileName) throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<String>();
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        Scanner input = new Scanner(in);
        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (line.length() > 0) {
                lines.add(line);
            }
        }
        return lines;
    }

}
