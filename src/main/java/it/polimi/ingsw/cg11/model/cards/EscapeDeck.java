package it.polimi.ingsw.cg11.model.cards;

import java.util.ArrayList;
import java.util.Collections;
/**
 * Deck of Escape Cards
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class EscapeDeck extends Deck<EscapeCard> {
    /**
     * Constructor for an escape deck. 3 green cards, 3 red ones.
     */
    public EscapeDeck() {
        this.cardsToPick = new ArrayList<EscapeCard>();
        this.discardedCards = new ArrayList<EscapeCard>();

        for(int i=0;i<3;i++){
            this.cardsToPick.add(new EscapeCard(EscapeColor.GREEN));
            this.cardsToPick.add(new EscapeCard(EscapeColor.RED));
        }

        Collections.shuffle(this.cardsToPick);
    }
}
