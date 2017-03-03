# Old School Games

![Old School Games](osk.png?raw=true)

This program combines several small text games with a graphical interface.

One of these is called Evil Hangman. This is like normal hangman, except here, the computer tries not
to choose a word for you to guess until it absolutely has to. Instead, it chooses a set of words
which do not contain the letters you’ve guessed. Eventually, if you’re persistent (hint: choose
vowels), the computer’s cleverness can be overcome.

Another is called Twenty Questions. In this game, the computer tries to guess what object
you’re thinking of. At the moment, the only topic available is animals, so when you’re asked to load
a saved game, you should say “yes,” then just press enter when prompted for the filename. Otherwise
(for other topics), you’d have to teach the computer each question and answer, which could take a
while, but might still be fun eventually. This type of session should then be saved with a different
filename, something.txt.

The other two sort of play with words, and might not be considered actual games.
Anagrams tries to find all of the possible words in a given phrase, then provides
sets of words which use up all of the letters in that phrase. Sometimes, the possibilities are
virtually endless (and would have your computer working for many hours), so just the first 100
answers are provided.
Random Sentence does just that, computer-generated sentences.
Both of these use recursion, which may be of interest when perusing the source.

To play, simply clone, then compile and run `OskMain.java`. Alternatively, an executable .jar is available [here](https://drive.google.com/open?id=0B54awCBW9Yx-UEFvNXdDY3Q0ZWs).
Note: there have been issues running audio clips with the Linux openjdk, so the official
Oracle jdk is recommended.
Thanks to Marty Stepp of Stanford for the ideas and many GUI components.
