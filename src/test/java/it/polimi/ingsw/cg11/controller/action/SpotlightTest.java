package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.controller.action.EndTurn;
import it.polimi.ingsw.cg11.controller.action.Move;
import it.polimi.ingsw.cg11.controller.action.Spotlight;
import it.polimi.ingsw.cg11.controller.action.UseItem;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import org.junit.Before;
import org.junit.Test;

public class SpotlightTest {

    Model m;
    Player p0, p1, p2, p3, p4;
    ItemCard itemCard = new ItemCard(ItemType.SPOTLIGHT);
    Controller c;

    /**
     * We create a game with a few players and put them all into a sector
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        m = new Model(1, "Fermi");
        p0 = m.getCurrPlayer();
        p1 = new HumanPlayer(new Coordinate('M',2), 5, "CAPTAIN");
        p2 = new HumanPlayer(new Coordinate('M', 8), 2, "PILOT");
        p3 = new HumanPlayer(new Coordinate('K',8),3, "PSYCHOLOGIST");
        p4 = new HumanPlayer(new Coordinate('L',7), 4, "SOLDIER");
        m.getPlayers().add(p1);
        m.getPlayers().add(p2);
        m.getPlayers().add(p3);
        m.getPlayers().add(p4);
        m.getMap().getSectors().get(p1.getCurrPosition()).addPlayer(p1);
        m.getMap().getSectors().get(p1.getCurrPosition()).addPlayer(p2);
        m.getMap().getSectors().get(p1.getCurrPosition()).addPlayer(p3);
        m.getMap().getSectors().get(p1.getCurrPosition()).addPlayer(p4);
        p1.getItems().add(itemCard);

        c = new Controller(m);
    }

    /**
     * Tests that the spotlight works as expected
     */
    @Test
    public void testSpotLight() {
        assertEquals(PlayerState.BEGIN_TURN, p0.getPlayerState());
        assertEquals(p0, m.getCurrPlayer());
        Move move = new Move(m, new Coordinate('L', 8));
        move.execute();
        move.nextState();
        
        EndTurn endTurn = new EndTurn(m);
        endTurn.execute();
        m.setCurrPlayer(p1);
        assertEquals(p1, m.getCurrPlayer());
        
        UseItem useItem = new UseItem(m, ItemType.SPOTLIGHT);
        useItem.execute();
        useItem.nextState();
        
        assertEquals(PlayerState.SPOTLIGHT_LOCK, p1.getPlayerState());
        
        //we send the spotlight action to the controller with a valid coordinate string
        c.parseAction("spotlight", "L08");
        assertEquals(PlayerState.BEGIN_TURN, p1.getPlayerState());
    }
    
    /**
     * Tests that the Spotlight Action doesn't work in an invalid coordinate 
     */
    @Test
    public void testSpotLightInvalidCoordinate() {
        //first we move
        assertEquals(PlayerState.BEGIN_TURN, p0.getPlayerState());
        assertEquals(p0, m.getCurrPlayer());
        Move move = new Move(m, new Coordinate('L', 8));
        move.execute();
        move.nextState();
        EndTurn endTurn = new EndTurn(m);
        endTurn.execute();
        m.setCurrPlayer(p1);
        assertEquals(p1, m.getCurrPlayer());
        
        //then we us the spotlight item
        UseItem useItem = new UseItem(m, ItemType.SPOTLIGHT);
        useItem.execute();
        useItem.nextState();
        assertEquals(PlayerState.SPOTLIGHT_LOCK, p1.getPlayerState());
        
        //then we try to use the spotlight in an invalid coordinate
        Spotlight spotlight = new Spotlight(m, new Coordinate('N',2));
        assertFalse(spotlight.execute());
    }

}
