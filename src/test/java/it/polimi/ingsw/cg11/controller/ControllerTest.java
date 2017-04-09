package it.polimi.ingsw.cg11.controller;

import static org.junit.Assert.*;

import java.util.UUID;

import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;
import it.polimi.ingsw.cg11.controller.action.EndTurn;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;

import org.junit.Before;
import org.junit.Test;

public class ControllerTest {

	Model m;
	Controller c;
	Player p1;
	
	@Before
	public void setUp() throws Exception {
		m = new Model(2, "Galvani");
		p1 = m.getCurrPlayer();
		if(p1.getType() == PlayerType.HUMAN){
			p1.setPlayerState(PlayerState.WAITING);
			m.nextPlayer();
		}
		UUID p1Id = UUID.randomUUID();
		p1.setClientId(p1Id);
		m.getPlayerById().put(p1Id, p1);
		c= new Controller(m);
	}

	//we test each action and each query
	/**
	 * test for the spaceship map name
	 */
	@Test
	public void testMapName() {
		ResponseMessage request = c.computeQuery(p1.getClientId(), "spaceship");
		assertEquals("Galvani", request.getOptionalMessage());
		assertFalse("galvani".equals(request.getOptionalMessage()));
		assertFalse("galvn".equals(request.getOptionalMessage()));
	}

	/**
	 * test for the round number query
	 */
	@Test
	public void testRoundNumber() {
		m.setRoundNumber(25);
		ResponseMessage request = c.computeQuery(p1.getClientId(), "roundNumber");
		assertEquals("25", request.getOptionalMessage());
	}

	/**
	 * test that we return the current position formatted in a way
	 * that is usable for the GUI
	 */
	@Test
	public void testCurrPosition(){
		ResponseMessage request = c.computeQuery(p1.getClientId(), "currposition");
		assertEquals("L06", request.getOptionalMessage());
	}

	/**
	 * Tests that we get the available actions we expect
	 */
	@Test
	public void testAvailableActions(){
		ResponseMessage request = c.computeQuery(p1.getClientId(), "availableactions");
		assertEquals("[Move]", request.getOptionalMessage());
	}

	/**
	 * Tests that we get the available items we expect
	 */
	@Test
	public void testAvailableItems(){
		p1.getItems().add(new ItemCard(ItemType.DEFENSE));
		ResponseMessage request = c.computeQuery(p1.getClientId(), "availableitems");
		assertEquals("[DEFENSE]", request.getOptionalMessage());
	}

	/**
	 * test that we get info about the character
	 */
	@Test
	public void testIdentity(){
		ResponseMessage request = c.computeQuery(p1.getClientId(), "playerinfo");
		
		//the player identity is randomly chosen
		assertTrue(request.isAck());
	}

	/**
	 * Check that we don't process the invalid queries
	 */
	@Test
	public void testInvalidQuery(){
		ResponseMessage request = c.computeQuery(p1.getClientId(), "ciao");
		assertFalse(request.isAck());
	}
	
	/**
	 * Test that we refuse an action that can't be done (by the rules of the game)
	 */
	@Test
	public void testInvalidAction(){
		EndTurn endTurn = new EndTurn(m);
		boolean outcome = c.execute(endTurn);
		assertFalse(outcome);
	}
	
	/**
	 * Test that the controller refuses to execute an action with a wrong name
	 */
	@Test
	public void testIllegalAction(){
		boolean outcome = c.parseAction("ciao", null);
		assertFalse(outcome);
	}
	

}
