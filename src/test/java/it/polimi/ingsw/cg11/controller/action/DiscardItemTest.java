package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import org.junit.Before;
import org.junit.Test;

public class DiscardItemTest {

    Player p1;
    Model m;
    DiscardItem discard;
    ItemCard item;
    Controller c;

    @Before
    public void setUp() throws Exception {
        m = new Model(1, "Fermi");
        p1 = m.getCurrPlayer();
        item = new ItemCard(ItemType.SPOTLIGHT);
        p1.getItems().add(item);
        p1.setPlayerState(PlayerState.ITEM_LOCK);
        c = new Controller(m);

    }

    /**
     * Check that we correctly discard an item card
     */
    @Test
    public void testDiscard() {
        assertEquals(1, p1.getItems().size());
        c.parseAction("discarditem", "SPOTLIGHT");
        assertEquals(PlayerState.IS_SAFE, p1.getPlayerState());
        assertEquals(0, p1.getItems().size());
        assertEquals(1 ,m.getItemCards().getDiscardedCards().size());
    }
    
    /**
     * Checks that we don't perform the action when we pass an invalid parameter
     * (a card that we are not holding)
     */
    @Test
    public void testInvalidDiscard(){
    	boolean ack = c.parseAction("discardItem", "ATTACK");
    	assertFalse(ack);
    }

}
