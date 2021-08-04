package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Controller {

    private final CardDeck cards;
    private final Scanner scanner;
    private final StringBuilder logHolder;
    private boolean exportOnClose = false;
    private String exportFile;

    public Controller(Scanner scanner, String importFile, String exportFile) {
        this.scanner = scanner;
        this.cards = new CardDeck();
        this.logHolder = new StringBuilder();

        if (importFile != null) {
            importCards(importFile);
        }

        if (exportFile != null) {
            exportOnClose = true;
            this.exportFile = exportFile;
        }

    }

    public void run() {
        String menu = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):";
        boolean isRunning = true;

        while (isRunning) {
            log(menu, true);
            String choice = scanner.nextLine();
            log(choice, false);

            switch (choice) {
                case "add":
                    addNewCard();
                    break;

                case "remove":
                    removeCard();
                    break;

                case "import":
                    importCards();
                    break;

                case "export":
                    exportCards();
                    break;

                case "ask":
                    study();
                    break;

                case "log":
                    log();
                    break;

                case "hardest card":
                    printHardestCards();
                    break;

                case "reset stats":
                    resetStats();
                    break;

                case "exit":
                    isRunning = false;
                    log("Bye bye!", true);
                    if (exportOnClose) {
                        exportCards(exportFile);
                    }
                    break;

                default:
                    log("Unknown choice. Try again!", true);
            }
            log("", true);
        }
    }

    /**
     * Create a new card
     * duplicate card terms are not allowed and definitions cannot be reused
     */
    private void addNewCard() {
        log("The card:", true);
        String term = scanner.nextLine().trim();
        log(term, false);

        //check if term has already been added
        if (cards.containsTerm(term)) {
            log("The card \"" + term + "\" already exists.", true);
            return;
        }

        log("The definition of the card:", true);
        String definition = scanner.nextLine().trim();
        log(definition, false);

        //check if the definition already belongs to another card
        if (cards.containsDefinition(definition)) {
            log("The definition \"" + definition + "\" already exists.", true);
            return;

        }

        cards.add(new FlashCard(term, definition));
        log("The pair (\"" + term + "\":\"" + definition + "\") has been added.", true);
    }

    /**
     * Removes a card if present in deck
     */
    private void removeCard() {
        log("Which card?", true);
        String cardToRemove = scanner.nextLine();
        log(cardToRemove, false);

        boolean isRemoved = cards.remove(cardToRemove);

        if (isRemoved) {
            log("The card has been removed.", true);
        } else {
            log("Can't remove \"" + cardToRemove + "\": there is no such card.", true);
        }
    }


    /**
     * Imports a deck of cards from a file then merges with any cards in memory
     * File must be formatted as:
     *          term:definition:missedCount
     */
    private void importCards() {
        log("File name:", true);
        String fileName = scanner.nextLine();
        log(fileName, false);
        importCards(fileName);
    }

    private void importCards(String fileName) {
        File file = new File(fileName);

        try (Scanner scanner = new Scanner(file)) {
            Map<String, FlashCard> importedDeck = new LinkedHashMap<>();

            while (scanner.hasNext()) {
                String[] line = scanner.nextLine().trim().split(":");
                String term;
                String definition;
                int missedCount = 0;

                if (line.length == 3 && line[2].matches("[0-9]+")) {
                    term = line[0];
                    definition = line[1];
                    missedCount = Integer.parseInt(line[2]);
                } else if (line.length == 2) {
                    term = line[0];
                    definition = line[1];
                } else {
                    log("ERROR PARSING LINE WITH TEXT " + Arrays.toString(line) + " SKIPPING LINE.", true);
                    continue;
                }

                FlashCard toAdd = new FlashCard(term, definition);
                toAdd.getCardStatistics().setMistakeCount(missedCount);
                importedDeck.put(term, toAdd);
            }

            log(importedDeck.size() + " cards have been loaded.", true);

            //merge imported deck into any cards in memory
            cards.merge(importedDeck);

        } catch (FileNotFoundException e) {
            log("File not found.", true);
        }
    }


    /**
     * Exports all cards created to a file
     */
    private void exportCards() {
        if (cards.size() == 0) {
            log("No cards to export!", true);
            return;
        }

        log("File name:", true);
        String fileName = scanner.nextLine().trim();
        log(fileName, false);
        exportCards(fileName);
    }

    private void exportCards(String fileName) {
        File file = new File(fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (FlashCard card : cards.getCards()) {
                String term = card.getTerm();
                String definition = card.getDefinition();
                int mistakeCount = card.getCardStatistics().getMistakeCount();
                printWriter.println(term + ":" + definition + ":" + mistakeCount);
            }

            log(cards.size() + " cards have been saved.", true);

        } catch (IOException e) {
            log("ERROR! File could not be saved", true);
        }
    }

    /**
     * Test user on a given amount of cards
     */
    private void study() {

        if (cards.size() == 0) {
            log("No cards to study!", true);
            return;
        }

        log("How many times to ask?", true);
        String input = scanner.nextLine();
        log(input, false);

        //make sure user actually gave a number
        if (!input.matches("[0-9]+")) {
            log("Invalid input given! \"" + input + "\" is not a positive integer!", true);
            return;
        }

        int cardLimit = Integer.parseInt(input);
        int current = 0;

        while (current < cardLimit) {

            FlashCard randomCard = cards.getRandomCard();
            current++;

            log("Print the definition of \"" + randomCard.getTerm() + "\":", true);
            String definitionGiven = scanner.nextLine();
            log(definitionGiven, false);

            //if user got card right, continue studying the terms
            if (cards.isCorrect(randomCard.getTerm(), definitionGiven)) {
                log("Correct!", true);
                continue;
            }

            //user didn't get card right
            String correctAnswer = cards.getDefinition(randomCard.getTerm());

            // check if answer is valid for another card
            if (cards.isCorrectForAnotherCard(randomCard.getTerm(), definitionGiven)) {
                String otherCardTerm = cards.getTermFromDefinition(definitionGiven);
                log("Wrong. The right answer is \"" + correctAnswer + "\", " +
                        "but your definition is correct for \"" + otherCardTerm + "\".", true);
            } else {
                log("Wrong. The right answer is \"" + correctAnswer + "\".", true);
            }

            //increment mistake count for missed card
            cards.incrementMistakeCount(randomCard.getTerm());
        }
    }

    /**
     * logs all lines that have been input/output to the console to a file
     *
     */
    private void log() {
        log("File name:", true);
        String fileName = scanner.nextLine().trim();
        log(fileName, false);
        File file = new File(fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.println(logHolder);
            log("The log has been saved.", true);
        } catch (IOException e) {
            log("ERROR! File could not be saved", false);
        }
    }

    /**
     *
     * @param text        text that is to be logged
     * @param isOutput    if true, output to console and log else just log
     */
    private void log(String text, boolean isOutput) {
        if (isOutput) {
            System.out.println(text);
        }

        logHolder.append(text).append("\n");
    }

    /**
     * prints all the cards with most misses
     */
    private void printHardestCards() {
        List<FlashCard> hardestCards = cards.getHardestCards();

        if (hardestCards.isEmpty()) {
            log("There are no cards with errors.", true);
            return;
        }

        int missedCount = hardestCards.get(0).getCardStatistics().getMistakeCount();

        if (hardestCards.size() == 1) {
            String term = hardestCards.get(0).getTerm();
            log("The hardest card is \"" + term + "\". You have " + missedCount + " errors answering it.", true);
            return;
        }

        //holds all the missed cards term
        StringBuilder holder = new StringBuilder();
        for (FlashCard card : hardestCards) {
            holder.append("\"").append(card.getTerm()).append("\"").append(", ");
        }

        //remove last comma
        holder.setLength(holder.length() - 2);

        log("The hardest cards are " + holder + ". You have " + missedCount + " errors answering them.", true);
    }

    /**
     * reset all the cards mistakes back to zero
     */
    private void resetStats() {
        cards.clearAllMistakeCounts();
        log("Card statistics have been reset.", true);
    }
}
