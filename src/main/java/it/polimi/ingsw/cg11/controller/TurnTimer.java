package it.polimi.ingsw.cg11.controller;

import it.polimi.ingsw.cg11.controller.action.Action;
import it.polimi.ingsw.cg11.controller.action.EndTurn;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The timer for the turn of a player, it is relative to a single game.
 * @author GerlandoSavio
 *
 */
public class TurnTimer implements Observer {
    private final Model game;
    private Timer timer;
    private ForceEndTurn currentTask;
    private ForceEndTurn oldTask;
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");

    /**
     * Constructor for a turn timer. Adds the turn timer as an observer to all the players of a game.
     * @param game the game we want to time
     */
    public TurnTimer(Model game){
        this.game = game;
        this.timer = new Timer();

        for(Player p: game.getPlayers()){
            p.addObserver(this);
        }
    }

    /**
     * The timer task to force the end of the turn of a player. Nothing fancy, really. It just creates a new EndTurn task and forces it to run
     * instead of going through the main controller.
     * @author GerlandoSavio
     *
     */
    public class ForceEndTurn extends TimerTask {
        private final Player player;
        private boolean wasInterrupted = false;
        /**
         * Constructor for the ForceEndTurn task
         * @param player the player we are timing
         */
        public ForceEndTurn(Player player){
            this.player=player;
        }

        /**
         * When it is run it first warns the player to complete its turn soon other, then if after another minute he has not played 
         * (meaning that the task has not been interrupted) it procedes to set its state to disconnected,
         * it warns all the other players and from this moment on the player will not play another turn
         * (Its state changes to DISCONNECTED)
         */
        @Override
        public void run(){
            if(game.getCurrPlayer().equals(player)){
                game.addEvent("SERVER: " + player.toString() + " is taking a long time to complete its turn and is going to be removed in a minute... Hurry up!");
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "error in turn timer", e);
                }

            }

            if(game.getCurrPlayer().equals(player) && !this.wasInterrupted){
                game.addEvent("SERVER: Looks like " + player.toString() + " has disconnected.");
                Action endTurn = new EndTurn(game);
                endTurn.execute();
                endTurn.nextState();
                player.setPlayerState(PlayerState.DISCONNECTED);
            }
        }

        
        private void interrupt() {
            this.wasInterrupted = true;
        }
    }

    /**
     * When the turn timer receives an update from a player it checks if it is interesting to him, meaning 
     * that the update involves a change of the turn, in that case handles the scheduling of a new task and 
     * cancels the old one
     */
    @Override
    public void update(Observable observedPlayer, Object playerStatus) {
        Player currentPlayer = (Player) observedPlayer;
        PlayerState currState = (PlayerState) playerStatus;

        if(currState == PlayerState.BEGIN_TURN){
            currentPlayer = (Player) observedPlayer;  
            oldTask = currentTask;
            currentTask = new ForceEndTurn(currentPlayer);
            timer.schedule(currentTask, 60000);
        }

        if(currState == PlayerState.WAITING && oldTask != null){
            oldTask.interrupt(); //we need some way of telling a task that is running that its services are no longer required
            oldTask.cancel();
        }


    }



}

