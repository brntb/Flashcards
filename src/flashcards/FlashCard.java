package flashcards;

public class FlashCard {

    private final String term;
    private final String definition;
    private final Statistics cardStatistics;

    public FlashCard(String term, String definition) {
        this.term = term;
        this.definition = definition;
        this.cardStatistics = new Statistics();
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public Statistics getCardStatistics() {
        return this.cardStatistics;
    }

}
