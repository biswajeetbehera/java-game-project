package it.polimi.ingsw.cg11.model.cards;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ItemDeckTest {

	ItemDeck itemDeck;
	
	@Before
	public void setUp() throws Exception {
		itemDeck = new ItemDeck();
	}

	@Test
	public void testConstructor() {
		assertEquals(12 , itemDeck.cardsToPick.size());
	}

}
