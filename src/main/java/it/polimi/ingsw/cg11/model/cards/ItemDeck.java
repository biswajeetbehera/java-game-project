package it.polimi.ingsw.cg11.model.cards;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The deck of Item Cards
 * @author GerlandoSavio
 *
 */
public class ItemDeck extends Deck<ItemCard> {
    /**
     * Constructor for Item Deck.
     * 2 Attack cards
     * 2 Teleport cards
     * 2 Adrenaline cards
     * 2 Spotlight cards
     * 3 Sedatives cards
     * 1 Defense card
     */
    public ItemDeck(){
        this.cardsToPick = new ArrayList<ItemCard>();
        this.discardedCards = new ArrayList<ItemCard>();

        for(int i=0;i<2;i++) 
            this.cardsToPick.add(new ItemCard(ItemType.ATTACK));

        for(int i=0;i<2;i++) 
            this.cardsToPick.add(new ItemCard(ItemType.TELEPORT));

        for(int i=0;i<2;i++) 
            this.cardsToPick.add(new ItemCard(ItemType.ADRENALINE));

        for(int i=0;i<2;i++) 
            this.cardsToPick.add(new ItemCard(ItemType.SPOTLIGHT));

        for(int i=0;i<3;i++) 
            this.cardsToPick.add(new ItemCard(ItemType.SEDATIVES));

        this.cardsToPick.add(new ItemCard(ItemType.DEFENSE));

        Collections.shuffle(this.cardsToPick);

    }



}
