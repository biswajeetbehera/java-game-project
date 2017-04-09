package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.players.Player;
/**
 * The abstract class for an Action
 * @author GerlandoSavio
 *
 */
public abstract class Action {
    protected String name;
    protected Model game;
    protected Player player;

    /**
     * Common constructor for an action
     * @param game the model the action modifies
     */
    public Action(Model game){
        this.game=game;
        this.player=game.getCurrPlayer();
    }
    /**
     * @return name of the action
     */
    public String getName() {
        return name;
    }

    /**
     * execution of the action. 
     * @return true iff the action was performed correctly and we can go to the next state
     */
    public abstract boolean execute();
    
    /**
     * sets the next state of the player and/or the game
     */
    public abstract void nextState();
    
    /**
     * toString method
     */
    @Override
    public abstract String toString();
}
