package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.map.EscapeSector;
import it.polimi.ingsw.cg11.model.map.Sector;
import it.polimi.ingsw.cg11.model.map.SectorType;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;
/**
 * One of our most prized actions, the move let's the player change its current position following
 * precisely the game rules.
 * ...but what happens if a human lands in an escape sector? Stay tuned to find out.
 * @author GerlandoSavio
 *
 */
public class Move extends Action {
    /**
     * starting coordinate for the move
     */
    private Coordinate start;
    /**
     * starting sector for the move
     */
    private Sector startSector;
    /**
     * destination coordinate
     */
    private Coordinate destination;
    /**
     * destination sector
     */
    private Sector destinationSector;


    /**
     * The constructor for the move action
     * @param game
     * @param destination
     */
    public Move(Model game, Coordinate destination) {
        super(game);
        this.start=this.player.getCurrPosition();
        this.startSector=this.game.getMap().getSectors().get(start);
        this.destination=destination;
        this.destinationSector=this.game.getMap().getSectors().get(destination);

    }


    /**
     *  * Checks if the move is possible using the isReachableInNMoves(Coordinate start, Coordinate destination, int distance) method of the SpaceshipMap
     * if an alien has turned into a super-alien, or a human has played an adrenaline card, then they can move one sector forward
     * Uses the Player's class move(Coordinate c) method to change the current player's current position and save it in its list of movements.
     * If the player lands in a dangerous sector it calls over the DangerousSector action
     */
    @Override
    public boolean execute() {
        int extraDistance = this.player.hasGainedSpeed() ? 1 : 0;

        if(!start.equals(destination) && 
            this.game.getMap().isReachableInNMoves(start, destination, this.player.getMovableDistance()+extraDistance)){


            //an alien can't land in an escape sector
            if(this.destinationSector.getType() == SectorType.ESCAPE && 
                    (this.player.getType() == PlayerType.ALIEN || ((EscapeSector) destinationSector).isBlocked())){
                return false; 
            }

            this.player.setCurrPosition(this.destination); //Model method: just sets the current position of the player
            this.startSector.removePlayer(player);
            this.destinationSector.addPlayer(player);
            return true;
        }
        return false;
    }

    /**
     * If a player lands in a dangerous sector than he must pick a sector card, unless the player is 
     * a human and has used a sedative card.
     */
    @Override
    public void nextState() {
        this.player.setHasMoved(true);
        
        if(this.destinationSector.getType() == SectorType.DANGEROUS){
            if(this.player.getType() == PlayerType.HUMAN && ((HumanPlayer) player).hasUsedSedatives()){
                this.player.setPlayerState(PlayerState.IS_SAFE);
            }
            else
                this.player.setPlayerState(PlayerState.IS_IN_DANGER);
        }
        else if(this.destinationSector.getType() == SectorType.ESCAPE){
            this.game.addEvent(player.toString() + " has landed into the Escape Hatch number" + ((EscapeSector) destinationSector).getNumber());
            this.player.setPlayerState(PlayerState.ESCAPE_LOCK);
        }
        else
            this.player.setPlayerState(PlayerState.IS_SAFE);
    }

    /**
     * toString method
     */
    @Override
    public String toString() {
        return "Move";
    }

}
