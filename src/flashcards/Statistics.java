package flashcards;

public class Statistics {

    //holds all the mistakes for a card
    private int mistakeCount;

    public int getMistakeCount() {
        return mistakeCount;
    }

    public void setMistakeCount(int mistakeCount) {
        this.mistakeCount = mistakeCount;
    }

    public void incrementMistakeCount() {
        mistakeCount++;
    }

}
