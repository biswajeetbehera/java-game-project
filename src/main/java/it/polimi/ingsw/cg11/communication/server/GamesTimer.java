package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.EventMessage.EventType;
import it.polimi.ingsw.cg11.controller.TurnTimer;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The big timer of the timer. It serves two main purposes: deleting a game where not enough players connected to start playing and
 * creating the turn timer class for a single game. For more information about that please read the documentation of TurnTimer.
 * @author GerlandoSavio
 *
 */
public class GamesTimer  {
    private Timer timer;
    private Map<Model, TurnTimer> turnTimers;
    /**
     * Constructor for the GamesTimer, initializes a new timer and the map of turn timers for each game. (Remember, one turn time per game,
     * that's why it is in the controller package).
     */
    public GamesTimer(){
        timer = new Timer();
        turnTimers = new HashMap<Model,TurnTimer>();
    }

    /**
     * The function that schedules the removal of a game after two minutes
     * @param game
     */
    public void setForRemoval(Model game){
        timer.schedule(new RemoveGame(game),  240000);
    }


    /**
     * Creates a new Turn timer type for a single game
     * @param game
     */
    public void createTurnTimer(Model game){
        turnTimers.put(game, new TurnTimer(game));
    }


    /**
     * The actual task we need to schedule in order to remove a game where not enough players connected.
     * @author GerlandoSavio
     *
     */
    private class RemoveGame extends TimerTask {
        /**
         * the game to remove
         */
        private Model game;

        /**
         * Constructor for the task
         * @param game the game to remove
         */
        public RemoveGame(Model game){
            this.game=game;
        }

        /**
         * Removes ended games or games that are taking too long to start
         */
        @Override
        public void run() {
            if(game.getGameState() == GameState.WAITING_FOR_PLAYERS){
                game.addEvent(new EventMessage(EventType.GAME_TIMEOUT, ""));
                //the task involves modifying several attributes in the games manager, so we put all the commands into a remove game function
                GamesManager.getInstance().removeGame(game);
                turnTimers.remove(game);
            }

            if(game.getGameState() == GameState.ENDED){
                GamesManager.getInstance().removeGame(game);
                turnTimers.remove(game);
            }

        }

    }




}

