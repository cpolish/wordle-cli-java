package wordle;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.AbstractMap.SimpleImmutableEntry;

import misc.util.*;

public class App {
    private static InputScanner inputScanner = new InputScanner();
    private static Random randomGenerator = new Random();

    private static List<String> wordList = new ArrayList<>();
    private static Character[] currentWord;

    private static List<List<Entry<Character, LetterColour>>> gameState;

    private static boolean playAgainPrompt() {
        if (wordList.size() == 0) {
            System.out.println("Looks like we've run out of words in the word bank!");
            return true;
        }

        while (true) {
            String userResponse = inputScanner.getInput("Play again? (Y/n) ");
            if (userResponse.length() == 0 || userResponse.matches("(?i)y|yes")) {
                return false;
            }
            else if (userResponse.matches("(?i)n|no")) {
                System.out.println("Thanks for playing!");
                return true;
            }
            else {
                System.out.println("Invalid response, pleas try again.");
            }
        }
    }

    private static String getUserGuess(int guessNum) {
        while (true) {
            String userGuess = inputScanner.getInput("Enter guess (" + guessNum + " of 6): ");
            if (userGuess.length() != 5) {
                System.out.println("Try again! Your guess must be a 5 letter word.");
            }
            else if (!userGuess.matches("[a-zA-Z]+")) {
                System.out.println("Try again! Your guess must only contain alphabetical characters.");
            }
            else {
                return userGuess.toLowerCase();
            }
        }
    }

    private static boolean guessWord(int guessIndex) {
        String userGuess = getUserGuess(guessIndex + 1);
        char[] userGuessArray = userGuess.toCharArray();
        gameState.add(new ArrayList<>());

        List<Entry<Character, LetterColour>> currentGuess = gameState.get(guessIndex);
        Map<Character, Integer> currentWordCharCount = CollectionUtils.countCharactersInString(currentWord);

        int correctPositionedCharacters = 0;
        for (int i = 0; i < userGuessArray.length; i++) {
            char userCharacter = userGuessArray[i];
            char currentWordCharacter = currentWord[i];
            Entry<Character, LetterColour> guessEntry = null;
            boolean foundCharacter = false;

            if (userCharacter == currentWordCharacter) {
                guessEntry = new SimpleImmutableEntry<>(userCharacter, LetterColour.GREEN);
                //currentGuess.add(guessEntry);
                foundCharacter = true;
                correctPositionedCharacters++;
            }
            else {
                for (Entry<Character, Integer> entry : currentWordCharCount.entrySet()) {
                    if (userCharacter == entry.getKey() && entry.getValue() > 0) {
                        guessEntry = new SimpleImmutableEntry<>(userCharacter, LetterColour.YELLOW);
                        //currentGuess.add(guessEntry);
                        foundCharacter = true;
                        break;
                    }
                }
            }

            if (foundCharacter) {
                // Minus available letter
                int availableLetterCount = currentWordCharCount.get(userCharacter) - 1;
                currentWordCharCount.put(userCharacter, availableLetterCount);
            }
            else {
                guessEntry = new SimpleImmutableEntry<>(userCharacter, LetterColour.GREY);
            }

            currentGuess.add(guessEntry);
        }

        return correctPositionedCharacters == 5;
    }

    private static void printGameState() {
        System.out.print("\033[2J");
        System.out.println("+---+---+---+---+---+");
        for (int i = 0; i < 6; i++) {
            if (i >= gameState.size()) {
                System.out.println("|   |   |   |   |   |");
            }
            else {
                StringBuilder line = new StringBuilder("|");
                for (Entry<Character, LetterColour> entry : gameState.get(i)) {
                    line.append(" " + entry.getValue() + entry.getKey() + "\033[0m |");
                }
                System.out.println(line);
            }

            System.out.println("+---+---+---+---+---+");
        }
    }

    private static void selectWord() {
        int randomIndex = randomGenerator.nextInt(wordList.size());
        String selectedWord = wordList.remove(randomIndex);
        currentWord = selectedWord.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
    }

    private static void playGame() {
        boolean finished = false;
        do {
            gameState = new ArrayList<>();
            selectWord();
            
            boolean fullyGuessedWord = false;
            for (int i = 0; i < 6 && !fullyGuessedWord; i++) {
                printGameState();
                fullyGuessedWord = guessWord(i);
            }

            printGameState();

            if (fullyGuessedWord) {
                System.out.println("Congratulations! You correctly guessed the word!");
            }
            else {
                System.out.println("Unlucky! The word was: " + CollectionUtils.characterArrayToString(currentWord));
            }

            finished = playAgainPrompt();
        } while (!finished);
    }

    private static boolean confirmFurtherFileRead(int numFilesParsed) {
        while (true) {
            String userResponse = inputScanner.getInput("Want to try another file? (Y/n) ");
            if (userResponse.matches("(?i)n|no")) {
                if (numFilesParsed == 0) {
                    System.out.println("No files parsed, exiting program");
                    System.exit(0);
                }
                else {
                    return true;
                }
            }
            else if (userResponse.length() == 0 || userResponse.matches("(?i)y|yes")) {
                return false;
            }
            else {
                System.out.println("Invalid input, try again.");
            }
        }
    }

    private static boolean parseWordsFromFile(String inputFilename) {
        try {
            FileReader.readWordsFromFile(inputFilename, wordList);
        }
        catch (FileNotFoundException fnfe) {
            String errorDetails;
            if (fnfe.getMessage().equals(inputFilename + " (Permission denied)")) {
                errorDetails = "Permission denied";
            }
            else {
                errorDetails = "File not found";
            }

            System.err.println("Error opening file '" + inputFilename + "': " + errorDetails);
            return false;
        }
        catch (ParseException pe) {
            System.err.println("Error parsing file '" + inputFilename + "' on line " + pe.getErrorOffset() + ": " + pe.getMessage());
            return false;
        }

        return true;
    }

    private static void readWordsFromFile(String[] args) {
        int numFilesParsed = 0;
        boolean fileSuccessfullyParsed;
        if (args.length == 0) {
            boolean readAllFiles = false;
            do {
                String inputFilename = inputScanner.getInput("Enter filename to read from file: ");
                fileSuccessfullyParsed = parseWordsFromFile(inputFilename);
                if (fileSuccessfullyParsed) {
                    numFilesParsed++;
                }

                readAllFiles = confirmFurtherFileRead(numFilesParsed);
            } while (!readAllFiles);
        }
        else {
            for (String filename : args) {
                fileSuccessfullyParsed = parseWordsFromFile(filename);
                if (fileSuccessfullyParsed) {
                    numFilesParsed++;
                }
            }
        }

        System.out.println("Successfully parsed words from " + numFilesParsed + " files.");
    }

    public static void main(String[] args) {
        readWordsFromFile(args);
        playGame();
        inputScanner.close();
    }
}
