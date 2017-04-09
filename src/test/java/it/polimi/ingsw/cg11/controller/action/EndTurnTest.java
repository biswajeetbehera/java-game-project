package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class EndTurnTest {

	Player p1, p2;
	Model m;
	EndTurn endTurn;
	Move move;
	Controller c;

	@Before
	public void setUp() throws Exception {

		m = new Model(2, "Fermi");
		p1 = m.getCurrPlayer();
		for(Player p: m.getPlayers()){
			p.setClientId(UUID.randomUUID());
			m.getPlayerById().put(p.getClientId(), p);
		}
		m.setGameState(GameState.BEGUN);
		c = new Controller(m);
		p1.setPlayerState(PlayerState.IS_SAFE);
	}

	/**
	 * Tests the end turn for an alien player, we check that its
	 * state after the EndTurn action is what we expect
	 */
    @Test
	public void testEndTurnAlien() {
		endTurn = new EndTurn(m);
		c.execute(endTurn);
		p2 = m.getCurrPlayer();
		assertEquals(PlayerState.WAITING, p1.getPlayerState());
		assertEquals(PlayerState.BEGIN_TURN, p2.getPlayerState());	
	}
    
    /**
     * Slightly different test for the end turn action performed
     * by an human player, we check that the speed gained 
     * by using the adrenaline item, and the usedSedatives booleans
     * are reset to false
     */
    @Test
    public void testHuman(){
    	p1.setPlayerState(PlayerState.WAITING);
    	m.nextPlayer();
    	p2 = m.getCurrPlayer();
    	p2.setHasGainedSpeed(true);
    	p2.setPlayerState(PlayerState.IS_SAFE);
    	endTurn = new EndTurn(m);
		c.execute(endTurn);
		p1 = m.getCurrPlayer();
		assertEquals(PlayerState.WAITING, p2.getPlayerState());
		assertFalse(p2.hasGainedSpeed());
		assertFalse(((HumanPlayer)p2).hasUsedSedatives());
		assertEquals(PlayerState.BEGIN_TURN, p1.getPlayerState());	
    }

    /**
     * Simple test to see if the end game method is called when expected
     */
    @Test
	public void testEndGame() {
        m.setRoundNumber(40);
		c.parseAction("endturn", null);
		assertEquals(PlayerState.WIN, p1.getPlayerState());	
	}
    

}
