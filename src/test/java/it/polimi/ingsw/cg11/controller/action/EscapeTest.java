package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;

import java.util.UUID;

import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.EscapeCard;
import it.polimi.ingsw.cg11.model.cards.EscapeColor;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.map.EscapeSector;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;

import org.junit.Before;
import org.junit.Test;


public class EscapeTest {

	Model m;
	Player p0, p1;
	Escape escape;
	Controller c;

	@Before
	public void setUp() throws Exception {
		m = new Model(2, "Fermi");
		p0 = m.getCurrPlayer();
		for(Player p: m.getPlayers()){
			p.setClientId(UUID.randomUUID());
			m.getPlayerById().put(p.getClientId(), p);
		}
		m.setGameState(GameState.BEGUN);
		if(p0.getType() == PlayerType.ALIEN){
			p0.setPlayerState(PlayerState.WAITING);
			m.nextPlayer();
		}
		m.getEscapeCards().getCardsToPick().removeAll(m.getEscapeCards().getCardsToPick());
		m.getEscapeCards().getCardsToPick().add(new EscapeCard(EscapeColor.GREEN));
		c = new Controller(m);

	}
	
	/**
	 * Tests that the human that escapes wins
	 * we put only an green escape card in the deck to be sure that
	 * the test would go through
	 */
    @Test
	public void testWin() {
    	m.getEscapeCards().getCardsToPick().removeAll(m.getEscapeCards().getCardsToPick());
		m.getEscapeCards().getCardsToPick().add(new EscapeCard(EscapeColor.GREEN));
    	p1 = m.getCurrPlayer();
    	p1.setCurrPosition(new Coordinate('J',5));
    	m.getCurrPlayer().setPlayerState(PlayerState.ESCAPE_LOCK);
		escape = new Escape(m);
		c.execute(escape);
		assertEquals(PlayerState.WIN, p1.getPlayerState());
	}
    
    /**
     * Tests that when a human player picks a red sector card he doesn't escape and its turn ends
     * pwuah pwuah
     */
    @Test
	public void testEscapeBlocked() {
    	m.getEscapeCards().getCardsToPick().removeAll(m.getEscapeCards().getCardsToPick());
		m.getEscapeCards().getCardsToPick().add(new EscapeCard(EscapeColor.RED));
    	p1 = m.getCurrPlayer();
    	p1.setCurrPosition(new Coordinate('J',5));
    	m.getCurrPlayer().setPlayerState(PlayerState.ESCAPE_LOCK);
		escape = new Escape(m);
		c.execute(escape);
		assertEquals(PlayerState.WAITING, p1.getPlayerState());
	}
    
    /**
     * Tests the the game ends when no more escape hatches are available
     * for escape (all the humans remaining die)
     */
    @Test
    public void testAllEscapeHatchesBlocked(){
        //we set three out of four escape hatches to blocked
        
    	Coordinate escape1 = new Coordinate('J',1);
		EscapeSector escapeSector1 = (EscapeSector) m.getMap().getSectors().get(escape1);
		escapeSector1.setBlocked(true);
		Coordinate escape2 = new Coordinate('N',1);
		EscapeSector escapeSector2 = (EscapeSector) m.getMap().getSectors().get(escape2);
		escapeSector2.setBlocked(true);
		Coordinate escape3 = new Coordinate('N',5);
		EscapeSector escapeSector3 = (EscapeSector) m.getMap().getSectors().get(escape3);
		escapeSector3.setBlocked(true);
		
		//we put only a red sector card in the deck...
		m.getEscapeCards().getCardsToPick().removeAll(m.getEscapeCards().getCardsToPick());
		m.getEscapeCards().getCardsToPick().add(new EscapeCard(EscapeColor.RED));
		//...the player lands in an escape sector
    	p1 = m.getCurrPlayer();
    	p1.setCurrPosition(new Coordinate('J',5));
    	m.getCurrPlayer().setPlayerState(PlayerState.ESCAPE_LOCK);
    	//...the player performs an escape action
		c.parseAction("escape", null);
		//it fails and no more escape hatches are available so he dies.
		assertEquals(PlayerState.DEAD, p1.getPlayerState());
    }
	
}
