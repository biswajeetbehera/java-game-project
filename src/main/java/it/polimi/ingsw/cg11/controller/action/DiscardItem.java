package it.polimi.ingsw.cg11.controller.action;

import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.players.PlayerState;
/**
 * Simple action for discarding an item when one is holding too many
 * @author GerlandoSavio
 *
 */
public class DiscardItem extends Action {
    /**
     * the type of card we want to get rid of
     */
    private ItemType itemType;

    /**
     * constructor for action
     * @param game the game on which we execute the action
     * @param itemType the type of the item we want to discard
     */
    public DiscardItem(Model game, ItemType itemType) {
        super(game);
        this.itemType = itemType;
    }

    /**
     * removes an item from the player's collection
     */
    @Override
    public boolean execute() {

        ItemCard cardToRemove = null ;

        for(ItemCard card: this.player.getItems()){
            if(card.getType() == itemType){
                cardToRemove = card;
                break;
            }
        }

        if(cardToRemove!=null){
            this.game.getItemCards().discard(cardToRemove);
            this.player.getItems().remove(cardToRemove);
            return true;
        }
        return false;


    }
    /**
     * since we only get here after we have picked one too many items, the next state can only be is safe
     */
    @Override
    public void nextState() {
        this.player.setPlayerState(PlayerState.IS_SAFE);
    }
    /**
     * generates string
     */
    @Override
    public String toString() {
        return "DiscardItem";
    }

}
