package wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FileReader {
    static void readWordsFromFile(String filename, List<String> wordList) throws FileNotFoundException, ParseException {
        List<String> tempList = new ArrayList<>();
        File inputFile = new File(filename);
        Scanner fileScanner = new Scanner(inputFile);

        int lineCounter = 1;
        while (fileScanner.hasNextLine()) {
            String word = fileScanner.nextLine();
            if (word.length() != 5) {
                fileScanner.close();
                throw new ParseException("words must only contain 5 characters", lineCounter);
            }
            else if (!word.matches("[a-zA-Z]+")) {
                fileScanner.close();
                throw new ParseException("words must only contain alphabetic characters", lineCounter);
            }
            
            tempList.add(word.toLowerCase());
            lineCounter++;
        }

        fileScanner.close();
        wordList.addAll(tempList);
    }
}
