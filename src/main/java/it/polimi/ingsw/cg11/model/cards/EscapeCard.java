package it.polimi.ingsw.cg11.model.cards;
/**
 * The class that defines an escape card object
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class EscapeCard implements Card {
    private EscapeColor color;
    /**
     * constructor for escape card
     * @param color
     */
    public EscapeCard(EscapeColor color) {
        this.color=color;
    }

    /**
     * @return the color
     */
    public EscapeColor getColor() {
        return color;
    }


}
