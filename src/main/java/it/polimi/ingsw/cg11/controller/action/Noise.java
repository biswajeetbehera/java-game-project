package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;
/**
 * The Noise Action that is called when a player picks a noise in any sector card
 * @author GerlandoSavio, Matteo Pagliari
 * @version 1.0
 */
public class Noise extends Action {
    /**
     * The coordinate where the player wants to make the noise
     */
    private Coordinate noiseCoordinate;


    /**
     * The constructor for the Noise Action
     * @param game 
     * @param noiseCoordinate
     */
    public Noise(Model game, Coordinate noiseCoordinate) {
        super(game);
        this.noiseCoordinate = noiseCoordinate;
    }

    /**
     * If the coordinate is within the map we can execute the action, which adds the event to the game log.
     */
    @Override
    public boolean execute() {

        if(this.game.getMap().getSectors().keySet().contains(noiseCoordinate)){
            String event = this.player.toString() + " made a Noise in " + noiseCoordinate.toString();
            this.game.addEvent(event);
            return true;
        }

        return false;
    }


    /**
     * Since a Noise In Any Sector Card can still result in picking an Item, we need to check that the player is not holding too many items
     * else the state changes to one where the player doesn't have to pick any more cards
     */
    @Override
    public void nextState() {
        if(this.player.getItems().size()>=4){
            this.player.setPlayerState(PlayerState.ITEM_LOCK);
        }

        else if(this.player.getType() == PlayerType.ALIEN)
            this.player.setPlayerState(PlayerState.HAS_ATTACKED);
        else
            this.player.setPlayerState(PlayerState.IS_SAFE);
    }

    /**
     * generates string with action name
     */
    @Override
    public String toString() {
        return "Noise";
    }

}
