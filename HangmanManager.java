/**
 * Creates a new Evil Hangman game.
 * @fields: guesses: letters already guessed, choices: dictionary words which match required length,
 * patterns: all possible words in dictionary given guesses, patterns: words in dictionary
 * which match current pattern of guesses, display: text currently shown to user
 */
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.TreeMap;

public class HangmanManager {
    private Set<String> choices;
    private SortedSet<Character> guesses;
    private Map<String, String> patterns;
    private int guessesleft;
    private String display;

    // constructor given desired word length and max number of guesses
    public HangmanManager(List<String> dictionary, int length, int max) {
        if (length < 1 || max < 0) {
            throw new IllegalArgumentException();
        }
        patterns = new TreeMap<String, String>();
        choices = new TreeSet<String>();
        for (String word: dictionary) {
            if (word.length() == length) {
                choices.add(word);
            }
        }
        guessesleft = max;
        guesses = new TreeSet<Character>();
        display = "";
        for (int i = 0; i < length; i++) { // user shown one dash per character in word
            display += "-";
        }
    }

    public Set<String> words() {
        return choices;
    }

    public int guessesLeft() {
        return guessesleft;
    }

    public SortedSet<Character> guesses() {
        return guesses;
    }

    public String pattern() {
        return display;
    }

    // handles an user's guess, returns number of letters in pattern
    public int record(char guess) {
        if (guessesleft < 1 || choices.size() == 0) {
            throw new IllegalStateException("Zero guesses or empty list");
        } else if (choices.size() > 0 && guesses.contains(guess)) {
            throw new IllegalArgumentException("You've already guessed that");
        }
        guesses.add(guess);
        display = getPattern(guess);
        int occurences = 0; // number of matching letters in new chosen pattern
        for (int i = 0; i < display.length(); i++) {
            if (display.charAt(i) == guess) {
                occurences++;
            }
        }
        if (occurences == 0) { // only penalize wrong answers
            guessesleft--;
        }
        // update choices to contain words which match pattern
        String[] matches = patterns.get(display).split(",");
        choices.clear();
        patterns.clear();
        for (int i = 0; i < matches.length; i++) {
            choices.add(matches[i]);
        }
        return occurences;
    }

    // find set of words with least number of occurences of guessed character
    private String getPattern(char guess) {
        String newpattern = "";
        int max = display.length();
        int count;
        for (String word: choices) {
            count = 0;
            char[] charpattern = display.toCharArray();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    charpattern[i] = guess;
                    count++;
                }
            }
            String currpattern = new String(charpattern);
            if (count < max) {
                max = count;
                newpattern = currpattern;
            }
            if (!patterns.containsKey(currpattern)) { // populate all possible patterns
                patterns.put(currpattern, word);
            } else {
                patterns.put(currpattern, patterns.get(currpattern) + "," + word);
            }
        }
        return newpattern;
    }

}
