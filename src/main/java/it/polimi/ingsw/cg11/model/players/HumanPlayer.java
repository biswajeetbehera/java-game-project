package it.polimi.ingsw.cg11.model.players;

import it.polimi.ingsw.cg11.model.map.Coordinate;

import java.util.ArrayList;
import java.util.List;
/**
 * The class for the human. It extends player and it's main purpose is to override the available
 * actions method and set a different movable distance from the alien
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class HumanPlayer extends Player {
    private boolean usedSedatives;

    /**
     * The constructor for the human player.
     * @param startPosition , the Human Sector.
     * @param i (player number)
     * @param role
     */
    public HumanPlayer(Coordinate startPosition, int i, String role){
        super(startPosition, i, role);
        this.movableDistance=1;
        this.type = PlayerType.HUMAN;
        this.usedSedatives = false;
    }
    /**
     * returns a list of actions a human can do based on its current status
     */
    @Override
    public List<String> getAvailableActions(){
        List<String> availableMoves = new ArrayList<String>();
        availableMoves.addAll(this.getPlayerState().availableActions());
        if(availableMoves.contains("Attack")){
            availableMoves.remove("Attack");
        }
        return availableMoves;
    }

    /**
     * @return the usedSedatives 
     */
    public boolean hasUsedSedatives() {
        return usedSedatives;
    }

    /**
     * @param usedSedatives the usedSedatives to set
     */
    public void setUsedSedatives(boolean usedSedatives) {
        this.usedSedatives = usedSedatives;
    }
}
