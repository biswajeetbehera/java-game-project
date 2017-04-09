package it.polimi.ingsw.cg11.model.cards;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EscapeDeckTest {

	EscapeDeck escapeDeck;
	
	@Before
	public void setUp() throws Exception {
		escapeDeck = new EscapeDeck();
	}

	@Test
	public void testConstructor() {
		assertEquals(6, escapeDeck.cardsToPick.size());
		assertEquals(0, escapeDeck.discardedCards.size());
	}
	
	//self-explanatory, remember that the methods for picking and discarding cards in escape deck and item deck are inherited
	//from the Deck abstract class
	
	@Test
	public void testPickCard(){
		escapeDeck.pickCard();
		assertEquals(5, escapeDeck.cardsToPick.size());
	}
	
	@Test
	public void testDiscardCard(){
		EscapeCard card = escapeDeck.pickCard();
		escapeDeck.discard(card);
		assertEquals(1, escapeDeck.discardedCards.size());
	}

}
