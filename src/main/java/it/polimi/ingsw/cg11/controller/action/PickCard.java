package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.SectorCard;
import it.polimi.ingsw.cg11.model.cards.SectorCardType;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;
/**
 * Action to pick a card
 * @author GerlandoSavio
 *
 */
public class PickCard extends Action {
    /**
     * Constructor for the PickCard action
     * @param game
     */
    public PickCard(Model game) {
        super(game);

    }
    /**
     * executing the action picks a card and executes the changes that the card implies. In particular
     * if the card has an item icon the player picks an item
     */
    @Override
    public boolean execute() {
        SectorCard sectorCard;
        this.game.addEvent(player.toString() + " picked a sector card!");
        sectorCard = this.game.getSectorCards().pickCard();
        SectorCardType type = sectorCard.getType();

        if(sectorCard.isWithItem()){
            this.pickItem();
        }

        switch(type){
        case SILENCE: 
            this.game.addEvent("You can't hear nothing but an eerie silence on the spaceship...");
            return true;

        case NOISE_IN_YOUR_SECTOR: 
            this.game.addEvent("The player " + this.player.toString() + " made a Noise in " + this.player.getCurrPosition().toString());
            return true;

        case NOISE_IN_ANY_SECTOR:
            //the next action of the player can only be to make a noise in some sector
            this.player.setPlayerState(PlayerState.NOISE_LOCK);
            return false; //when execute() returns false the nextState() method is not executed

        default: 
            return false;
        }
    }

    /**
     * If one is holding too many items after this action than he must use or discard one
     * If an alien picks a sector card he cannot attack during this turn
     */
    @Override
    public void nextState() {
        if(this.player.getItems().size()>=4){
            this.player.setPlayerState(PlayerState.ITEM_LOCK);
        }

        else if(this.player.getType() == PlayerType.ALIEN){
            this.player.setPlayerState(PlayerState.HAS_ATTACKED);
        }
        else
            this.player.setPlayerState(PlayerState.IS_SAFE);


    }
    /**
     * method to pick an item
     * @return boolean of the outcome returns true if we were able to correctly add an item to the player's collection
     */
    private boolean pickItem(){

        if(this.game.getItemCards().getCardsToPick().isEmpty()){
            this.game.getItemCards().shuffle();
        }

        if(!this.game.getItemCards().getCardsToPick().isEmpty()){
            ItemCard itemCard = this.game.getItemCards().pickCard();
            this.game.addEvent(player.toString() + " picked an Item Card!");
            this.player.getItems().add(itemCard);
            return true;
        }
        else
            return false;

    }

    /**
     * generates string
     */
    @Override
    public String toString() {
        return "PickCard";
    }

}
