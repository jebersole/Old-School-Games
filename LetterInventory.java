/**
 * A Letter Inventory object keeps track of the number of letters in a string.
 * If two such objects are added or subtracted, a new object is returned, reflecting
 * the resulting letter count. Methods also allow the LI's string itself to be modified.
 * @fields: letters: a count of each letter in the string, size: number of letters
 * in the string.
 */
public class LetterInventory {
    private int[] letters;
    private int size;
    private char start = 'a';
    private static final int ALPHABET_MAX = 26;

    // basic constructor without initial string provided
    public LetterInventory() {
        this("");
    }

    // constructor which counts the number of alphabetic characters, ignoring others
    public LetterInventory(String data) {
        letters = new int[ALPHABET_MAX];
        data = data.toLowerCase();
        size = 0;
        for (int i = 0; i < data.length(); i++) { // ASCII values for letters are ints
            if ((data.charAt(i) < (ALPHABET_MAX + start)) && (data.charAt(i) >= start)) {
                letters[data.charAt(i) - start]++;
                size++;
            }
        }
    }

    // adds two LI objects together, returning a new, larger LI object
    public LetterInventory add(LetterInventory other) {
        LetterInventory compound = new LetterInventory();
        for (int i = 0; i < letters.length; i++) {
            compound.letters[i] = this.letters[i] + other.letters[i];
        }
        compound.size = this.size + other.size;
        return compound;
    }

    // subtracts this LI's letters from a provided LI object, returns resulting object
    public LetterInventory subtract(LetterInventory other) {
        LetterInventory difference = new LetterInventory();
        for (int i = 0; i < letters.length; i++) {
            difference.letters[i] = this.letters[i] - other.letters[i];
            if (difference.letters[i] < 0) { // not allowed if other has more of a given letter than this
                return null;
            }
        }
        difference.size = this.size - other.size;
        return difference;
    }

    // outputs the character equivalents of the character count, e.g. if letters[]
    // contains 4 a's, 2 b's and 2 c's, toString() will return [aaaabbcc]
    public String toString() {
        String result = "[";
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] != 0) {
                for (int j = 1; j <= letters[i]; j++) {
                    result += (char)(start + i);
                }
            }
        }
        result += "]";
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // returns the count of a given letter
    public int get(char letter) {
        letter = Character.toLowerCase(letter);
        if (letter < start || letter > (ALPHABET_MAX + start)) {
            throw new IllegalArgumentException();
        }
        return letters[letter - start];
    }

    // changes the count of a given letter to a provided value
    public void set(char letter, int value) {
        letter = Character.toLowerCase(letter);
        if (value < 0 || letter < start || letter > (ALPHABET_MAX + start)) {
            throw new IllegalArgumentException();
        }
        if (value == 0) {
            size -= letters[letter - start];
            letters[letter - start] = 0;
        } else { // update size to reflect difference
            int change = letters[letter - start] - value;
            letters[letter - start] = value;
            if (change > 0) {
                size -= change;
            } else {
                size += Math.abs(change);
            }
        }

    }

    // returns fractional percent of given letters in this object
    public double getLetterPercentage(char letter) {
        letter = Character.toLowerCase(letter);
        if (letter < start || letter > (ALPHABET_MAX + start)) {
            throw new IllegalArgumentException();
        }
        if (size == 0) {
            return 0;
        }
        return (double) letters[letter - start] / size;
    }
}
