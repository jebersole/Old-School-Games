/**
 * Random sentence generator. @field: grammar: given rules for this language
 */
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Random;

public class GrammarSolver {
    Map<String, String> grammar;

    // create rules from List given in Backus–Naur notation, see sentence.txt for example
    public GrammarSolver(List<String> rules) {
        if (rules == null || rules.size() == 0) {
            throw new IllegalArgumentException("Null object or empty list!");
        }
        grammar = new TreeMap<String, String>();
        for (String line: rules) {
            line = line.trim();
            String[] parts = line.split("::=");
            String aftersplit = "";
            for (int i = 1; i < parts.length; i++) {
                aftersplit += parts[i];
            }
            if (grammar.containsKey(parts[0])) {
                throw new IllegalArgumentException("More than one rule definition not allowed.");
            }
            grammar.put(parts[0].toLowerCase(), aftersplit);
        }
    }

    // returns set of all symbols in grammar
    public Set<String> getSymbols() {
        Set<String> symbols = new TreeSet<String>();
        for (String nonterminal: grammar.keySet()) {
            symbols.add(nonterminal);
        }
        return symbols;
    }

    // main method to generate a sentence with symbol <s>, calls private method
    public String generate() {
        String sentence = generate("<s>").trim();
        sentence = correctArticles(sentence);
        return sentence.substring(0, 1).toUpperCase() +
            sentence.substring(1, sentence.length()) + ".\n";
    }

    // recursively create a sentence
    private String generate(String symbol) {
        if (symbol == null || symbol.length() == 0) {
            throw new IllegalArgumentException("Null object or empty string");
        }
        String result = "";
        if (!symbol.contains(" ") && !symbol.contains("|") && !grammar.containsKey(symbol)) {
            return symbol; // symbol is a word, a terminal
        } else {
            String[] parts = {};
            if (symbol.contains("|")) {
                parts = symbol.split("[|]");
                if (symbol.contains(" ")) { // the notation can indicate "or" with both pipes and spaces
                    parts = parts[random(parts)].split("[ \t]+");
                }
            } else if (symbol.contains(" ")) {
                parts = symbol.split("[ \t]+");
            } else {
                parts = new String[] {
                    "" + symbol
                }; // only one, nonterminal symbol
            }
            if (grammar.containsKey(parts[0])) { // symbol is a part of speech, a nonterminal
                for (int i = 0; i < parts.length; i++) {
                    symbol = grammar.get(parts[i].toLowerCase());
                    result += generate(symbol);
                }
            } else { // symbol is a set of terminal values which were piped (|), so choose one
                result += " " + parts[random(parts)];
            }
        }
        return result;
    }

    // chooses a random word/part of speech
    private int random(String[] parts) {
        Random r = new Random();
        return r.nextInt(parts.length);
    }

    // changes indefinite articles from "a" to "an" as needed
    private String correctArticles(String sentence) {
        if (sentence.contains("a ")) {
            List<Integer> list = new LinkedList<Integer>();
            for (int j = 0; j < sentence.length() - 2; j++) { // noun starts with a vowel
                if (sentence.charAt(j) == 'a' && sentence.charAt(j + 1) == ' ' &&
                    ((sentence.charAt(j + 2) == 'a') || (sentence.charAt(j + 2) == 'e') ||
                        (sentence.charAt(j + 2) == 'i') || (sentence.charAt(j + 2) == 'o') || (sentence.charAt(j + 2) == 'u'))) {
                    if (j == 0) { // sentence begins with an article
                        list.add(j);
                    } else { // article in middle of sentence, check for exception of Cinderella/Barbarella
                        if (sentence.charAt(j - 1) == ' ') {
                            list.add(j);
                        }
                    }
                }
            }
            for (Integer article: list) {
                if (article == 0) { // article at beginning of sentence
                    sentence = sentence.substring(0, article).trim() + "an " + sentence.substring(article + 2).trim();
                } else {
                    sentence = sentence.substring(0, article).trim() + " an " + sentence.substring(article + 2).trim();
                }
            }
        }
        return sentence;
    }

}
