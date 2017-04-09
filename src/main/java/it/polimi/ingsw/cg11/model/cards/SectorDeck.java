package it.polimi.ingsw.cg11.model.cards;

import java.util.ArrayList;
import java.util.Collections;
/**
 * Defines the deck of Sector Cards
 * @author GerlandoSavio
 *
 */
public class SectorDeck extends Deck<SectorCard> {
    /**
     * Constructor for a sector deck.
     * 5 Silence cards
     * 10 Noise In Your Sector Cards
     * (4 let you get an item, 6 don't)
     * 10 Noise In Any Sector Cards
     * (4 let you get an item, 6 don't)
     */
    public SectorDeck(){
        this.cardsToPick = new ArrayList<SectorCard>();
        this.discardedCards = new ArrayList<SectorCard>();

        for(int i=0;i<5;i++) 
            this.cardsToPick.add(new SectorCard(false,SectorCardType.SILENCE));

        for(int i=0;i<4;i++) 
            this.cardsToPick.add(new SectorCard(true,SectorCardType.NOISE_IN_YOUR_SECTOR)); //true specifies that the card has an Item Icon

        for(int i=0;i<6;i++) 
            this.cardsToPick.add(new SectorCard(false,SectorCardType.NOISE_IN_YOUR_SECTOR));

        for(int i=0;i<4;i++) 
            this.cardsToPick.add(new SectorCard(true,SectorCardType.NOISE_IN_ANY_SECTOR)); //true specifies that the card has an Item Icon

        for(int i=0;i<6;i++) 
            this.cardsToPick.add(new SectorCard(false,SectorCardType.NOISE_IN_ANY_SECTOR));

        Collections.shuffle(cardsToPick);

    }
    /**
     * Overridden method of pick card, that automatically adds picked card to the discarded pile.
     * (A player can't hold them)
     */
    @Override
    public SectorCard pickCard(){
        if(this.cardsToPick.isEmpty())
            this.shuffle();

        SectorCard card = cardsToPick.iterator().next();
        cardsToPick.remove(card);
        this.discard(card); //automatically adds picked card to discarded pile
        return card;
    }

}
