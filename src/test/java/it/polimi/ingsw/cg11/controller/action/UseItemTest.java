package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import org.junit.Before;
import org.junit.Test;

public class UseItemTest {

    Model m;
    Player p0;
    HumanPlayer p1;
    ItemCard itemCard;
    UseItem useItem;
    Controller c;

    //we test that we handle the USAGE of each item correctly
    
    @Before
    public void setUp() throws Exception {
        m = new Model(1, "Fermi");
        p0 = m.getCurrPlayer();
        p1 = new HumanPlayer(new Coordinate('M',2), 5, "CAPTAIN");
        m.getPlayers().add(p1);
        m.getMap().getSectors().get(p1.getCurrPosition()).addPlayer(p1);
        p1.setPlayerState(PlayerState.WAITING);
        m.setCurrPlayer(p1);
        p1.setPlayerState(PlayerState.BEGIN_TURN);
        c = new Controller(m);
    }

    // FOR EACH TEST of the following three test we check that the item is discarded
    // after using it
    
    /**
     * We test that the Teleport item changes the player position to its start position (human or alien sector)
     */
    @Test
    public void testTeleport() {
        itemCard= new ItemCard(ItemType.TELEPORT);
        p1.getItems().add(itemCard);
        assertEquals(p1, m.getCurrPlayer());
        assertEquals(1, p1.getItems().size());
        c.parseAction("useitem", "TELEPORT");
        assertEquals(PlayerState.BEGIN_TURN, p1.getPlayerState());
        assertEquals(new Coordinate('L',10), p1.getCurrPosition());
        assertEquals(0, p1.getItems().size());
    }

    /**
     * tests the sedatives item, which secures the human that it won't pick a sector card 
     * even when he lands in a dangerous sector (we perform another check in the Move action)
     */
    @Test
    public void testSedatives() {
        itemCard= new ItemCard(ItemType.SEDATIVES);
        p1.getItems().add(itemCard);
        assertEquals(p1, m.getCurrPlayer());
        assertEquals(1, p1.getItems().size());
        c.parseAction("useitem", "SEDATIVES");
        assertEquals(PlayerState.BEGIN_TURN, p1.getPlayerState());
        assertTrue(p1.hasUsedSedatives());
        assertEquals(0, p1.getItems().size());
    }

    /**
     * Tests the Adrenaline item which increases the movable distance of the human
     */
    @Test
    public void testAdrenaline() {
        itemCard= new ItemCard(ItemType.ADRENALINE);
        p1.getItems().add(itemCard);
        assertEquals(p1, m.getCurrPlayer());
        assertEquals(1, p1.getItems().size());
        c.parseAction("useitem", "ADRENALINE");
        assertTrue(p1.hasGainedSpeed());
        c.parseAction("move", "M04");
        assertEquals(new Coordinate('M',4), p1.getCurrPosition());
        assertEquals(0, p1.getItems().size());
    }
    
    /**
     * tests that the player changes state when using an item in an item_lock state
     * (the state we put the player in, when it is holding 4 items)
     */
    @Test
    public void testItemLock(){
    	p1.setPlayerState(PlayerState.ITEM_LOCK);
    	itemCard= new ItemCard(ItemType.TELEPORT);
        p1.getItems().add(itemCard);
        c.parseAction("useitem", "TELEPORT");
    	assertEquals(PlayerState.IS_SAFE, p1.getPlayerState());
    }

}
