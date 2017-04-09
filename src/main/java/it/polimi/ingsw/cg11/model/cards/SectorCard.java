package it.polimi.ingsw.cg11.model.cards;
/**
 * Defines the Sector Card. The boolean withItem signals the player that picks it that he must
 * pick a card from the item deck right after this one
 * @author GerlandoSavio
 *
 */
public class SectorCard implements Card {
    /**
     * boolean to indicate if a card has an "Item Icon"
     */
    private boolean withItem;
    /**
     * type of the card
     */
    private SectorCardType type;
    /**
     * Constructor for a sector card
     * @param withItem
     * @param type
     */
    public SectorCard(boolean withItem, SectorCardType type) {
        this.withItem=withItem;
        this.type=type;
    }

    /**
     * @return the withItem
     */
    public boolean isWithItem() {
        return withItem;
    }

    /**
     * @return the type
     */
    public SectorCardType getType() {
        return type;
    }


}
