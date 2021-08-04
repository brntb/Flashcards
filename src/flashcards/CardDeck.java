package flashcards;

import java.util.*;

public class CardDeck {

    //holds all the 'cards' for the app
    private final Map<String, FlashCard> cardDeck;
    private final Random random;

    public CardDeck() {
        this.cardDeck = new LinkedHashMap<>(); //using linkedHashMap since order is important
        this.random = new Random();
    }

    /***
     *
     * @param card  the card that is to be added to the deck
     */
    public void add(FlashCard card) {
         cardDeck.putIfAbsent(card.getTerm(), card);
    }

    /**
     *
     * @param term  the term we are searching for in deck
     * @return      true if card decks contains the term
     */
    public boolean containsTerm(String term) {
        return cardDeck.containsKey(term);
    }

    /**
     *
     * @param definition  the definition we are searching for in deck
     * @return            true if card decks contains the definition
     */
    public boolean containsDefinition(String definition) {
        return cardDeck.values().stream().anyMatch(def -> def.getDefinition().equals(definition));
    }

    /**
     *
     * @return     turn if given definition matches term in deck
     */
    public boolean isCorrect(String term, String definitionGiven) {
        return cardDeck.get(term).getDefinition().equals(definitionGiven);
    }

    /**
     * @return    true if definition given is actually correct for a different term
     */
    public boolean isCorrectForAnotherCard(String term, String definitionGiven) {
        for (String current : cardDeck.keySet()) {
            if (!current.equals(term)) {
                if (cardDeck.get(current).getDefinition().equals(definitionGiven)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param term  term we are searching for
     * @return      definition of term
     */
    public String getDefinition(String term) {
        FlashCard card = cardDeck.get(term);
        return card.getDefinition();
    }

    /**
     *
     * @param definition   definition we are searching deck for
     * @return             term found with given definition
     */
    public String getTermFromDefinition(String definition) {
        for (FlashCard card : cardDeck.values()) {
            if (card.getDefinition().equals(definition)) {
                return card.getTerm();
            }
        }

        return "NO TERM FOUND!";
    }

    /**
     *
     * @param term  term of card to remove
     * @return      true if card is removed
     */
    public boolean remove(String term) {
        if (cardDeck.containsKey(term)) {
            cardDeck.remove(term);
            return true;
        }

        return false;
    }

    /**
     * merges a deck of cards coming from a file to current deck of cards in memory
     *
     * @param otherCardDeck  map to merge
     */
    public void merge(Map<String ,FlashCard> otherCardDeck) {
        cardDeck.putAll(otherCardDeck);
    }

    /**
     *
     * @return   size of card deck
     */
    public int size() {
        return cardDeck.size();
    }

    /**
     * if a card is answered incorrectly, increment it's mistake count
     *
     * @param term the term of the card that is missed
     */
    public void incrementMistakeCount(String term) {
        FlashCard card = cardDeck.get(term);
        card.getCardStatistics().incrementMistakeCount();
    }

    /**
     * resets all cards mistake count back to 0
     */
    public void clearAllMistakeCounts() {
        for (FlashCard card : cardDeck.values()) {
            card.getCardStatistics().setMistakeCount(0);
        }
    }

    /**
     *
     * @return   list of cards with highest miss count
     */
    public List<FlashCard> getHardestCards() {
        List<FlashCard> hardestCards = new ArrayList<>();
        int mostMissed = 1;

        for (FlashCard card : cardDeck.values()) {
            int missed = card.getCardStatistics().getMistakeCount();

            if (missed > mostMissed) {
                mostMissed = missed;
                hardestCards.clear();
                hardestCards.add(card);
            } else if (missed >= mostMissed) {
                hardestCards.add(card);
            }

        }

        return hardestCards;
    }

    public FlashCard getRandomCard() {
        int randomIdx = random.nextInt(size());
        List<String> keys = new ArrayList<>(cardDeck.keySet());

        return cardDeck.get(keys.get(randomIdx));
    }

    public Collection<FlashCard> getCards() {
        return cardDeck.values();
    }

}
