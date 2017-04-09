package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.map.Sector;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import java.util.Map;
/**
 * The spotlight action
 * @author GerlandoSavio
 *
 */
public class Spotlight extends Action {
    private Coordinate lightCoordinate; 
    private Sector lightSector;

    
    /**
     * Constructor for the Spotlight action, takes a game where the action needs to happen and the coordinate
     * the player wants to check
     * @param game
     * @param lightCoordinate
     */
    public Spotlight(Model game, Coordinate lightCoordinate) {
        super(game);
        this.lightCoordinate=lightCoordinate;
        this.lightSector=this.game.getMap().getSectors().get(lightCoordinate);
    }



    /**
     * Looks for any players present in the sector indicated through the lightCoorinate and all its neighbors, when it
     * finds one (or more than one) it adds an event revealing that player's position to everybody.
     */
    @Override
    public boolean execute() {
        if(this.game.getMap().getSectors().keySet().contains(lightCoordinate)){
            Map<Coordinate,Sector> sectorsToCheck = this.game.getMap().findNearSectors(lightCoordinate);
            sectorsToCheck.put(lightCoordinate, lightSector);

            for(Sector s: sectorsToCheck.values()){
                for(Player p: s.getPlayersInSector()){
                    this.game.addEvent(p.toString() + " is in " + p.getCurrPosition());
                }
            }

            return true;
        }
        return false;

    }

    /**
     * we need to check that if the player has moved before performing this action we set its next state to safe,
     * otherwise we let it move
     */
    @Override
    public void nextState() {
        if(player.hasMoved())
            this.player.setPlayerState(PlayerState.IS_SAFE);
        else
            this.player.setPlayerState(PlayerState.BEGIN_TURN);
    }

    @Override
    public String toString() {
        return "Spotlight";
    }

}
