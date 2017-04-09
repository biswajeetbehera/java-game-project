package it.polimi.ingsw.cg11.model.cards;

import java.util.Collections;
import java.util.List;
/**
 * The generic deck type. 
 * Provides a few useful methods for all the decks.
 * @author GerlandoSavio
 *
 * @param <T> the type of card for the deck
 */
public abstract class Deck<T extends Card>{
    protected List<T> cardsToPick;
    protected List<T> discardedCards;
    /**
     * method to pick a card
     * @return a card from the top of the deck
     */
    public T pickCard(){
        T card = cardsToPick.iterator().next();
        cardsToPick.remove(card);
        return card;
    }
    /**
     * adds a card into the discarded pile
     * @param card the card to discard
     */
    public void discard(T card){
        discardedCards.add(card);
    }
    /**
     * @return boolean true iff there are no more cards in the pick pile
     */
    public boolean isEmpty(){
        return this.cardsToPick.isEmpty();
    }
    /**
     * gets all the distarded cards, puts them again in the pick pile and shuffles it
     */
    public void shuffle(){
        this.cardsToPick.addAll(discardedCards);
        discardedCards.clear();
        Collections.shuffle(cardsToPick);
    }
    /**
     * @return list of cards to pick
     */
    public List<T> getCardsToPick() {
        return cardsToPick;
    }
    /**
     * @return list of discared cards
     */
    public List<T> getDiscardedCards() {
        return discardedCards;
    }

}
