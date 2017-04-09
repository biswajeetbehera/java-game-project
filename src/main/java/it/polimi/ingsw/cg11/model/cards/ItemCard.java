package it.polimi.ingsw.cg11.model.cards;

/**
 * The class that defines an ItemCard
 * @author GerlandoSavio, Matteo Pagliari
 * @version 1.0
 *
 */
public class ItemCard implements Card {

    private ItemType type;
    /**
     * Constructor for an ItemCard
     * @param type (from enumeration)
     */
    public ItemCard(ItemType type) {
        super();
        this.type=type;
    }

    /**
     * @return the type
     */
    public ItemType getType() {
        return type;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return type.toString();
    }





}
