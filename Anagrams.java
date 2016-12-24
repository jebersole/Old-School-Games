/**
 * An Anagrams object uses a given phrase to produce anagrams recursively.
 * @fields: letters: a LetterIventory of all letters in the phrase, answers: anagrams produced,
 * matchwords: words in the dictionary which contain same number and type of letters, chosen: words being tried
 * at a given time by algorithm to produce anagrams.
 */
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;

public class Anagrams {
    private LetterInventory letters;
    private Set<String> dictionary, matchwords;
    private List<String> chosen, answers;
    private UserInterface ui;

    // construct Anagrams object given a dictionary and a pointer to an user interface
    public Anagrams(Set<String> dictionary, UserInterface ui) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Null dictionary");
        }
        this.ui = ui;
        this.dictionary = new TreeSet<String>();
        for (String word: dictionary) {
            this.dictionary.add(word);
        }
        chosen = new LinkedList<String>();
        matchwords = new HashSet<String>();
        answers = new LinkedList<String>();
    }

    // populate matchwords with all words found in phrase
    public Set<String> getWords(String phrase) {
        if (phrase == null) {
            throw new IllegalArgumentException("Null phrase");
        }
        chosen.clear();
        matchwords.clear();
        answers.clear();
        letters = new LetterInventory(phrase);
        // check dictionary word has the exact same type and number of letters as phrase
        for (String word: dictionary) {
            LetterInventory dictword = new LetterInventory(word);
            LetterInventory difference = letters.subtract(dictword);
            if (difference != null) { // contains same letters
                matchwords.add(word);
            }
        }
        return matchwords;
    }

    // print anagrams without a max word length
    public void print(String phrase) {
        generate();
        if (answers.size() > 0) { // sort alphabetically and smallest sets first
            Collections.sort(answers);
            Collections.sort(answers, new Comparator<String>() {
                @Override
                public int compare(final String first, String second) {
                    return first.length() - second.length();
                }
            });
            String result = "";
            for (String answer: answers) {
                result += answer + "\n";
            }
            ui.print(result);
        } else {
            ui.print("Sorry, no anagrams found.");
        }
    }

    // create anagrams through recursive backtracking
    private void generate() {
        if (answers.size() <= 200) { // first 200 is arbitrary, to limit user wait time
            if (letters.size() == 0) { // all letters used, must be anagram
                if (notDuplicate()) { // add only unique answers
                    answers.add(chosen.toString());
                }
            } else {
                for (String word: matchwords) {
                    LetterInventory letterword = new LetterInventory(word);
                    if (letters.subtract(letterword) != null) {
                        chosen.add(word);
                        letters = letters.subtract(letterword);
                        generate();
                        letters = letters.add(letterword);
                        chosen.remove(word);
                    }
                }
            }
        }
    }

    // check if answer already exists, e.g. [dog, man, bone] is the same as [man, dog, bone]
    private boolean notDuplicate() {
        for (String answer: answers) {
            int count = 0;
            for (String word: chosen) {
                if (answer.contains(word)) {
                    count++;
                }
            }
            if (count == chosen.size()) {
                return false;
            }
        }
        return true;
    }

}
