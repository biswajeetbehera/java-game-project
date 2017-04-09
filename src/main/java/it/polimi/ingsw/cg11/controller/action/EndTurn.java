package it.polimi.ingsw.cg11.controller.action;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.EventMessage.EventType;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;
/**
 * The EndTurn action. Performs checks for the end of the game
 * @author GerlandoSavio
 *
 */
public class EndTurn extends Action {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");
    /**
     * Constructor for the EndTurn action
     * @param game
     */
    public EndTurn(Model game) {
        super(game);
    }

    /**
     * If the player is human the effects of sedatives and adrenaline end with the turn.
     */
    @Override
    public boolean execute() {
        if(this.player.getType() == PlayerType.HUMAN){
            ((HumanPlayer) player).setUsedSedatives(false);
            this.player.setHasGainedSpeed(false);
        }
        
        this.player.setHasMoved(false);

        return true;
    }

    /**
     * Sets the current player in waiting and calls the method to pass the turn to another one.
     */
    @Override
    public void nextState() {

        this.player.setPlayerState(PlayerState.WAITING);

        try{
            this.game.nextPlayer();
        } catch(NoSuchElementException e){

            LOGGER.log(Level.INFO, "no mo playas", e);
            game.addEvent("Looks like there's no one else available to play! The game is going to end...");
            endGame();
        }

        if(this.player.equals(game.getCurrPlayer())){
            game.addEvent("Looks like there's no one else available to play! The game is going to end...");
            endGame();
        }

        EventMessage turnEvent = new EventMessage(EventType.TURN_CHANGE, game.getCurrPlayer().getClientId().toString());
        this.game.addEvent(turnEvent);


        if(this.game.getRoundNumber()>39){
            this.game.addEvent("Your time is up! All the remaining humans on the spaceship are going to die...");
            endGame();
        }
    }

    /**
     * method that checks that all humans have either died or escaped
     * @return boolean
     */
    public boolean allHumansAreGone(){
        for(Player p: this.game.getPlayers()){
            if(p.getType() == PlayerType.HUMAN && !(p.getPlayerState() == PlayerState.DEAD || p.getPlayerState() == PlayerState.WIN)){
                return false;
            }
        }
        return true;
    }

    /**
     * We enter this method if and only if one of these has happened:
     * 
     * More than 39 rounds have been played.
     * In this case, if there are still humans in the spaceship, the alien team wins.
     * 
     * A human player has escaped after a move and he was the last human on the spaceship.
     * In this case all the remaining aliens lost the game. (see Escape class)
     * 
     * An Alien Player has attacked the last human in the spaceship 
     * In this case the alien team wins (see Attack class)
     * 
     * A human player has killed all the remaining players after using an attack card
     * 
     */
    public void endGame(){


        if(this.game.getRoundNumber()>39 && !allHumansAreGone()){
            for(Player p: this.game.getPlayers()){
                //all the remaining humans die
                if(p.getType() == PlayerType.HUMAN && p.getPlayerState() != PlayerState.WIN)
                    p.setPlayerState(PlayerState.DEAD);
                //all the aliens win
                if(p.getType() == PlayerType.ALIEN)
                    p.setPlayerState(PlayerState.WIN);
            }
        }


        this.game.setGameState(GameState.ENDED);
        
        //sends informations about who won and who lost (and who disconnected, the cowards)
        String gameStatistics = "";
        
        for(Player p: this.game.getPlayers()){
            gameStatistics += p.toString() + " Race: " + p.getType().toString() + " Final State: " + p.getPlayerState().toString() + "\n" ;
        }
        this.game.addEvent(new EventMessage(EventType.GAME_OVER, gameStatistics));
    }
    /**
     * generates string
     */
    @Override
    public String toString() {
        return "EndTurn";
    }

}
