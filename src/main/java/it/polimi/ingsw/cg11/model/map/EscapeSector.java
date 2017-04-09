package it.polimi.ingsw.cg11.model.map;
/**
 * The class for a specific escape sector. The reason it extends a sector is to add
 * methods that block it after one player has used it. (See game rules for details)
 * @author GerlandoSavio, Matteo Pagliari
 * @version 1.0
 *
 */
public class EscapeSector extends Sector {
    private int number;
    private boolean blocked;
    /**
     * Constructor for EscapeSector
     * @param number
     */
    public EscapeSector(int number){
        super(SectorType.ESCAPE);
        this.number=number;
        this.blocked=false;
    }

    /**
     * String method
     */
    @Override
    public String toString() {
        return "EscapeSector [" + number + "]";
    }
    /**
     * @return boolean (true iff it was blocked)
     */
    public boolean isBlocked(){
        return blocked;
    }

    /**
     * @param blocked the blocked to set
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }



}
