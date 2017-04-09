package it.polimi.ingsw.cg11.model.players;

import it.polimi.ingsw.cg11.model.map.Coordinate;

import java.util.ArrayList;
import java.util.List;
/**
 * The class for a player of alien type
 * Purpose: setting a different movable distance from the human, overriding the available moves method
 * @author GerlandoSavio
 *
 */
public class AlienPlayer extends Player {
    /**
     * Constructor for alien player
     * @param startPosition the alien sector
     * @param i (player number)
     * @param role (to get details from enumeration)
     */
    public AlienPlayer(Coordinate startPosition, int i, String role) {
        super(startPosition, i, role);
        this.movableDistance=2;
        this.type = PlayerType.ALIEN;
    }

    /**
     * returns a list of actions the alien can do based on its state
     */
    @Override
    public List<String> getAvailableActions(){
        List<String> availableMoves = new ArrayList<String>();
        availableMoves.addAll(this.getPlayerState().availableActions());
        if(availableMoves.contains("UseItem")){
            availableMoves.remove("UseItem");
        }
        return availableMoves;
    }

}
