package flashcards;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String importFile = null;
        String exportFile = null;

        //check if program needs to run with args passed
        if (args.length == 2) {
            if (args[0].equals("-import")) {
                importFile = args[1];
            } else if (args[0].equals("-export")) {
                exportFile = args[1];
            }
        }

        if (args.length == 4) {
            for (int i = 0; i < 4; i+=2) {
                if (args[i].equals("-import")) {
                    importFile = args[i + 1];
                } else if (args[i].equals("-export")) {
                    exportFile = args[i + 1];
                }
            }
        }

        Controller controller = new Controller(scanner, importFile, exportFile);
        controller.run();
    }
}
