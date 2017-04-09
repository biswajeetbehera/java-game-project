package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.map.Sector;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.PlayerState;
/**
 * The class that defines the UseItem action
 * @author GerlandoSavio
 *
 */
public class UseItem extends Action {
    /**
     * The type of item the player wants to use
     */
    private ItemType itemType;

    /**
     * Constructor for the useitem action
     * @param game
     * @param itemType
     */
    public UseItem(Model game, ItemType itemType) {
        super(game);
        this.itemType = itemType;
    }

    /**
     * execution follows these steps:
     * 1 - searches for a card of the same type in the player's collection
     * 2 - if it finds one then it continues, otherwise returns false
     * 3 - based on the item type we perform the appropriate action
     * 4 - A player must declare when he uses an item, so we add the event to the game
     * 5 - we remove the card from the player's collection and add it to the pile of discarded cards
     */
    @Override
    public boolean execute() {

        //find card held by player
        ItemCard cardHeld = null;

        for(ItemCard card: this.player.getItems()){
            if(card.getType() == itemType){
                cardHeld = card;
                break;
            }
        }

        //if the player is not holding any item of that type then we can't execute
        if(cardHeld==null)
            return false;



        switch(itemType){
        case TELEPORT:
            teleport();
            break;

        case SEDATIVES:
            ((HumanPlayer) player).setUsedSedatives(true);
            break;

        case SPOTLIGHT:
            this.player.setPlayerState(PlayerState.SPOTLIGHT_LOCK);
            break;

        case ADRENALINE:
            this.player.setHasGainedSpeed(true);
            break;

        case ATTACK:
            Action attack = new Attack(game);
            attack.execute();
            break;

        default: return false;
        }

        this.game.addEvent(player.toString() + " used " + itemType.toString());
        this.player.getItems().remove(cardHeld);
        this.game.getItemCards().discard(cardHeld);
        return true;
    }

    /**
     * a player can use an item at any time of the game, so its state doesn't change
     * unless the card was used because they were holding too many
     */
    @Override
    public void nextState() {
        if(this.player.getPlayerState() == PlayerState.ITEM_LOCK){
            this.player.setPlayerState(PlayerState.IS_SAFE);
        }
    }
    
    /**
     * method that performs all the commands needed to teleport
     */
    private void teleport(){
        Sector humanStart = this.game.getMap().getSectors().get(this.game.getMap().getHumanStart());
        Sector currSector = this.game.getMap().getSectors().get(this.player.getCurrPosition());
        this.player.setCurrPosition(this.game.getMap().getHumanStart());
        currSector.removePlayer(this.player);
        humanStart.addPlayer(this.player);
    }

    /**
     * generates string
     */
    @Override
    public String toString() {
        return "UseItem";
    }

}
