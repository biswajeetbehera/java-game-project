package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.controller.action.Move;
import it.polimi.ingsw.cg11.controller.action.PickCard;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.cards.SectorCard;
import it.polimi.ingsw.cg11.model.cards.SectorCardType;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import org.junit.Before;
import org.junit.Test;

public class PickCardTest {

    Model m;
    Player p1, p2;
    Move move;
    PickCard pickCard;
    Controller c;

    @Before
    public void setUp() throws Exception {
        m= new Model(1,"Galilei");
        p1 = m.getCurrPlayer();
        m.getSectorCards().getCardsToPick().clear();
    }
    
    //in the following three tests we cover the execution of the Pick Card action
    //for each kind of sector card the player picks

    /**
     * Tests that the Silence Sector Card is working
     */
    @Test
    public void testSilence() {
    	m.getSectorCards().getCardsToPick().add(new SectorCard(false, SectorCardType.SILENCE)); 
        c = new Controller(m);
        c.parseAction("move", "L05");
        assertEquals(PlayerState.IS_IN_DANGER, p1.getPlayerState());
        c.parseAction("pickcard", null);
    }
    
    
    /**
     * Tests that the Noise In Your Sector Card in the Sector Deck is working as expected 
     */
    @Test
    public void testNoiseInYourSector() {
    	m.getSectorCards().getCardsToPick().add(new SectorCard(false, SectorCardType.NOISE_IN_YOUR_SECTOR)); 
        c = new Controller(m);
        c.parseAction("move", "L05");
        assertEquals(PlayerState.IS_IN_DANGER, p1.getPlayerState());
        c.parseAction("pickcard", null);
    }

    /**
     * Tests that the noise in any sector card is working as expected
     */
    @Test
    public void testNoiseInAnySector() {
    	m.getSectorCards().getCardsToPick().add(new SectorCard(false, SectorCardType.NOISE_IN_ANY_SECTOR)); 
        c = new Controller(m);
        c.parseAction("move", "L05");
        assertEquals(PlayerState.IS_IN_DANGER, p1.getPlayerState());
        c.parseAction("pickcard", null);
        assertEquals(PlayerState.NOISE_LOCK, p1.getPlayerState());
    }
    
    /**
     * Tests that when a sector card has a "Item icon" the action puts an item card into the players collection 
     * (also remember that we change the player state iff the execute method returns true)
     */
    @Test
    public void testSectorCardWithItem() {
    	m.getSectorCards().getCardsToPick().add(new SectorCard(true, SectorCardType.NOISE_IN_ANY_SECTOR)); 
        c = new Controller(m);
        c.parseAction("move", "L05");
        assertEquals(PlayerState.IS_IN_DANGER, p1.getPlayerState());
        c.parseAction("pickcard", null);
        assertEquals(1, p1.getItems().size());
        assertEquals(PlayerState.NOISE_LOCK, p1.getPlayerState());
    }
    
    
    /**
     * Tests that the re-shuffling of the sector deck works when all the sector cards
     * have been discarded
     */
    @Test
    public void testSectorCardWithItem2() {
    	m.getSectorCards().getCardsToPick().add(new SectorCard(true, SectorCardType.NOISE_IN_ANY_SECTOR));
    	m.getItemCards().getDiscardedCards().addAll(m.getItemCards().getCardsToPick());
        m.getItemCards().getCardsToPick().clear();
        c = new Controller(m);
        c.parseAction("move", "L05");
        assertEquals(PlayerState.IS_IN_DANGER, p1.getPlayerState());
        c.parseAction("pickcard", null);
        assertEquals(PlayerState.NOISE_LOCK, p1.getPlayerState());
    }
    
    
    /**
     * Tests that if there are no more item cards left in the Item Deck, the Pick Card action is performed even when
     * the card shows an Item icon. (we just don't pick an item!)
     */
    @Test
    public void testSectorCardWithItem3() {
    	m.getSectorCards().getCardsToPick().add(new SectorCard(true, SectorCardType.NOISE_IN_YOUR_SECTOR));
        m.getItemCards().getCardsToPick().clear();
        c = new Controller(m);
        c.parseAction("move", "L05");
        c.parseAction("pickcard", null);
        assertEquals(0, p1.getItems().size());
        assertEquals(p1.getPlayerState(), PlayerState.HAS_ATTACKED);
    }
    
    /**
     * Tests that when a player is holding four items after picking a sector card then he must 
     * use or discard one immediatelly
     */
    @Test
    public void testFourItemCards() {
    	m.getSectorCards().getCardsToPick().add(new SectorCard(true, SectorCardType.NOISE_IN_YOUR_SECTOR));
    	p1.getItems().add(new ItemCard(ItemType.ADRENALINE));
    	p1.getItems().add(new ItemCard(ItemType.DEFENSE));
    	p1.getItems().add(new ItemCard(ItemType.SPOTLIGHT));
        c = new Controller(m);
        c.parseAction("move", "L05");
        c.parseAction("pickcard", null);
        assertEquals(PlayerState.ITEM_LOCK, p1.getPlayerState()); //this state forces the player to use or discard an item
    }
    
    /**
     * We repeat some of the above assertions for the human player, just to be super-safe.
     */
    @Test
    public void testHumanCase(){
    	m.getSectorCards().getCardsToPick().add(new SectorCard(false, SectorCardType.NOISE_IN_YOUR_SECTOR));
    	p2 = new HumanPlayer(new Coordinate('M',9), 5, "CAPTAIN");
        m.getPlayers().add(p2);
        m.getMap().getSectors().get(p2.getCurrPosition()).addPlayer(p2);
        m.setCurrPlayer(p2);
        p2.setPlayerState(PlayerState.BEGIN_TURN);
        assertEquals(p2, m.getCurrPlayer());
        c = new Controller(m);
        c.parseAction("move", "M08");
        assertEquals( new Coordinate('M',8), p2.getCurrPosition());
        c.parseAction("pickcard", null);
        assertEquals(PlayerState.IS_SAFE, p2.getPlayerState());
    }
}
