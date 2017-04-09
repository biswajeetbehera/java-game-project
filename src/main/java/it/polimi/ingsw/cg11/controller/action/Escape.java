package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.EscapeCard;
import it.polimi.ingsw.cg11.model.cards.EscapeColor;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.map.EscapeSector;
import it.polimi.ingsw.cg11.model.map.Sector;
import it.polimi.ingsw.cg11.model.map.SectorType;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;

/**
 * The escape action. It is send when a human 
 * @author GerlandoSavio
 *
 */
public class Escape extends Action {
    private EscapeSector escapeSector;

    /**
     * Constructor for the Escape action
     * @param game
     */
    public Escape(Model game) {
        super(game);
        Coordinate escapeCoordinate = this.game.getCurrPlayer().getCurrPosition();
        this.escapeSector = (EscapeSector) this.game.getMap().getSectors().get(escapeCoordinate);
    }

    /**
     * Pick a card from the escape deck, if it's green the player can escape 
     */
    @Override
    public boolean execute() {

        EscapeCard card = this.game.getEscapeCards().pickCard();

        //if it's green the human can escape
        if(card.getColor() == EscapeColor.GREEN){
            this.game.addEvent("The human " + this.player.toString() + " escaped from the Aliens!");
            this.game.addEvent("The escape sector" + escapeSector.getNumber() + " is now blocked!");

            this.player.setPlayerState(PlayerState.WIN);
            this.escapeSector.removePlayer(player);
            this.escapeSector.setBlocked(true);
            return true;
        }

        else{
            this.game.addEvent("The human " + this.player.toString() + " found that the sector is BLOCKED!! No escaping from here!");
            this.player.setPlayerState(PlayerState.IS_SAFE);
            escapeSector.setBlocked(true);
            return true;
        }


    }
    /**
     * We need to perform a few checks here.
     * 1 - If after a player has escaped there's no other humans on the spaceship the game ends and all the aliens lose.
     * 2 - If after this action all the escape hatcher are blocked all the remaining humans on the spaceship are doomed and the aliens win
     * These two together make for a colorful method
     */
    @Override
    public void nextState() {

        EndTurn endTurn = new EndTurn(this.game);
        boolean allEscapeHatchesAreBlocked = true;
        boolean allHumansAreGone = endTurn.allHumansAreGone();

        for(Sector sector : this.game.getMap().getSectors().values()){
            if(sector.getType() == SectorType.ESCAPE && !((EscapeSector) sector).isBlocked()){
                allEscapeHatchesAreBlocked = false;
            }
        }


        if(allHumansAreGone){
            this.game.addEvent("All the humans have escaped from the aliens! The game ends!");
            allAliensLose();
            endTurn.endGame(); 
            return;
        }

        if(allEscapeHatchesAreBlocked){
            this.game.addEvent("All the escape sectors are blocked! The game ends!");
            allHumansAreDoomed();
            endTurn.endGame();
            return;
        }

        endTurn.nextState(); 

    }

    
    /**
     * All aliens lose if the last human on the spaceship escapes
     */
    private void allAliensLose() {
        for(Player p: this.game.getPlayers()){
            if(p.getType() == PlayerType.ALIEN && p.getPlayerState() != PlayerState.WIN){
                p.setPlayerState(PlayerState.DEAD);
            }
        }
    }

    /**
     * All the humans that have not yet escaped are considered dead after all the esccape hatches are blocked
     */
    private void allHumansAreDoomed(){

        for(Player p: this.game.getPlayers()){
            if(p.getType() == PlayerType.HUMAN && !(p.getPlayerState() == PlayerState.WIN)){
                this.game.addEvent(p.toString() + " is doomed to stay on the spaceship!");
                p.setPlayerState(PlayerState.DEAD);
            }
        }
    }
    /**
     * generates string
     */
    @Override
    public String toString() {
        return "Escape";
    }

}
