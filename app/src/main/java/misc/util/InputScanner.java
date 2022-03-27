package misc.util;

import java.util.Scanner;

public class InputScanner implements AutoCloseable {
    private Scanner scanner = new Scanner(System.in);

    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }
}
