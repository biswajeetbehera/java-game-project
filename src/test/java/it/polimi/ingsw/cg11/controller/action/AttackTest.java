package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;

import java.util.UUID;

import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;

import org.junit.Before;
import org.junit.Test;

public class AttackTest {

	Model m;
	Player p0, p1;
	Attack attack;
	Controller c;


	@Before
	public void setUp() throws Exception {

		m = new Model(2, "Galilei");
		p0 = m.getCurrPlayer();
		for(Player p: m.getPlayers()){
			p.setClientId(UUID.randomUUID());
			m.getPlayerById().put(p.getClientId(), p);
		}
		m.setGameState(GameState.BEGUN);
		if(p0.getType() == PlayerType.HUMAN){
			p0.setPlayerState(PlayerState.WAITING);
			m.nextPlayer();
		}
		m.setGameState(GameState.BEGUN);
		c = new Controller(m); 

	}


	/**
	 * Tests that we nobody dies when a player attacks in a sector with nobody else in it
	 */
	@Test
	public void testAttackNobody() {
		c.parseAction("move", "L05");
		attack = new Attack(m);
		c.execute(attack);
		assertEquals(PlayerState.WAITING, p0.getPlayerState());
	}


	/**
	 * Tests the attack performed by an human player when using the attack item card
	 * We see that it correctly passes its turn meaning that the action was correctly executed
	 */
	@Test
	public void testHumanAttack(){
		p0.setPlayerState(PlayerState.WAITING);
		m.nextPlayer();
		p1 = m.getCurrPlayer();
		p1.getItems().add( new ItemCard(ItemType.ATTACK));
		c.parseAction("useitem", "ATTACK");
		assertEquals(PlayerState.BEGIN_TURN, p1.getPlayerState());
	}
	
	/**
	 * Test that when we only two players are left on the spaceship 
	 * if the alien kills the last human on the spaceship it wins
	 */
	@Test
	public void testAttackWin() {
		p0.setPlayerState(PlayerState.WAITING);
		m.nextPlayer();
		p1 = m.getCurrPlayer();
		p1.setCurrPosition(new Coordinate('L',5));
		m.getMap().getSectors().get(p1.getCurrPosition()).addPlayer(p1);
		p1.getItems().add(new ItemCard(ItemType.SPOTLIGHT));
		p1.setPlayerState(PlayerState.WAITING);
		m.nextPlayer();
		p0 = m.getCurrPlayer();
		c.parseAction("move", "L05");
		attack = new Attack(m);
		c.execute(attack);
		assertEquals(PlayerState.WIN, p0.getPlayerState());
		assertEquals(PlayerState.DEAD, p1.getPlayerState());
	}
	
	/**
	 * Tests that when an alien attacks a human that is holding a defense card
	 * it survives
	 */
	@Test
	public void testAttackDefenseCard() {
		p0.setPlayerState(PlayerState.WAITING);
		m.nextPlayer();
		p1 = m.getCurrPlayer();
		p1.setCurrPosition(new Coordinate('L',5));
		m.getMap().getSectors().get(p1.getCurrPosition()).addPlayer(p1);
		p1.getItems().add(new ItemCard(ItemType.DEFENSE));
		p1.setPlayerState(PlayerState.WAITING);
		m.nextPlayer();
		p0 = m.getCurrPlayer();
		c.parseAction("move", "L05");
		c.parseAction("attack", null);
		assertEquals(PlayerState.WAITING, p0.getPlayerState());
		assertEquals(PlayerState.BEGIN_TURN, p1.getPlayerState());
	}
	

}


