package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.map.Sector;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Specifies the rules that make the attack possible.
 * @author GerlandoSavio
 *
 */
public class Attack extends Action {
    /**
     * The sector where the attack takes place
     */
    private Sector attackSector;
    /**
     * the set of attacked players
     */
    private Set<Player> attackedPlayers;
    /**
     * The constructor for the attack action
     * @param game
     */
    public Attack(Model game) {
        super(game);
        this.attackSector = this.game.getMap().getSectors().get(this.player.getCurrPosition());
        this.attackedPlayers = new HashSet<Player>();

    }


    /**
     * The execution of an attack. The attack is announced to all the players.
     * All the players in the sectors are killed, except for any human player holding a 
     */
    @Override
    public boolean execute() {

        this.game.addEvent(this.player.toString() + " attacked in " + this.player.getCurrPosition());

        calculateAttackedPlayers();

        //all the remaining players in the sector are killed and removed from the sector.
        for(Player p: attackedPlayers){

            //we reveal the identity of the player killed
            String event = p.toString() + " was brutally killed! \n An examination of the corpe reveals that it was " 
                    + p.getRole().getName() + ", the " + p.getRole().toString().toLowerCase();
            if(p.getType() == PlayerType.ALIEN)
                event += " alien";

            this.game.addEvent(event);

            p.setPlayerState(PlayerState.DEAD);

            //if a player is killed they must discard any item they may be holding
            for(ItemCard heldcard: p.getItems()){
                this.game.getItemCards().discard(heldcard);
            }

            attackSector.removePlayer(p);
        }

        //if the attacker is an alien and it has killed at least one human, then he becomes a SuperAlien
        if(this.player.getType() == PlayerType.ALIEN)
            makeSuperAlien();
        else
            humanAttackCheck();

        //whether the player has killed anyone or not, he must change State, so we return true.
        return true;
    }


    /**
     * If an alien has attacked he automatically ends its turn (there are no other available actions)
     * Furthermore, this let's us check that if after the attack the alien has killed the last human
     * in the spaceship then the game can end (see EndTurn class for details).
     */
    @Override
    public void nextState() {

        EndTurn endTurn = new EndTurn(this.game);

        //returns true if there are still humans alive
        if(!endTurn.allHumansAreGone()){
            //if there are still players alive then we call the method that changes the current player
            endTurn.nextState();
        }

        //if all the humans died after an attack then the game ends
        else
        {
            this.game.addEvent("The last human in the spaceship has been killed, the Aliens WIN!!");

            for(Player p: this.game.getPlayers()){
                if(p.getType() == PlayerType.ALIEN){
                    p.setPlayerState(PlayerState.WIN);
                }
            }

            endTurn.endGame();
        }
    }
    /**
     * Internal method to set an alien to super alien (gain speed)
     * This happens iff an alien has killed at least one human during the attack
     */
    private void makeSuperAlien(){
        if(!this.attackedPlayers.isEmpty()){
            for(Player p: attackedPlayers){
                if(p.getType() == PlayerType.HUMAN)
                    this.player.setHasGainedSpeed(true);
            }
        }
    }

    /**
     * Calculates the players that are going to die during this attack.
     * Checks that if a human is holding a defense card he must be spared.
     */
    private void calculateAttackedPlayers(){

        for(Player p: attackSector.getPlayersInSector()){

            if(!p.equals(this.player)){
                this.attackedPlayers.add(p);
            }

            if(p.getType() == PlayerType.HUMAN){
                ItemCard cardToRemove = null;

                for(ItemCard card: p.getItems()){
                    if(card.getType() == ItemType.DEFENSE){
                        cardToRemove = card;
                        this.game.getItemCards().discard(cardToRemove);
                        this.game.addEvent(p.toString() + " used a Defense Card to avoid the attack!");
                        this.attackedPlayers.remove(p);
                        break; //we don't want to remove more than one defense card if a human player has holding multiple ones.
                    }
                }

                if(cardToRemove!=null){
                    p.getItems().remove(cardToRemove);
                }
            }

        }
    }

    /**
     * while playing we noticed that we didn't handle the case when a human is the only one left on the spaceship after 
     * using an attack card. This is so ridiculously rare that it would probably never happen. But just in case that happens,
     * we can check it with this method.
     */
    private void humanAttackCheck(){
        boolean noOneLeft = true;
        List<Player> others = new ArrayList<Player>();
        others.addAll(this.game.getPlayers());
        others.remove(player);

        for(Player p: others){
            if(p.getPlayerState() == PlayerState.WAITING){
                noOneLeft=false;
            }
        }

        if(noOneLeft){
            this.game.addEvent("What a plot twist! " + player.toString() + " killed everyone that was left on the spaceship!");
            this.game.setGameState(GameState.ENDED);
        }
    }

    /**
     * generates string
     */
    @Override
    public String toString() {
        return "Attack";
    }

}
