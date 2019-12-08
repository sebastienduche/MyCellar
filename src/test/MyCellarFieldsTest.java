package test;

import mycellar.Bouteille;
import mycellar.core.MyCellarFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyCellarFieldsTest {

	private Bouteille bouteille;
	
	@BeforeEach
	void setUp() {
		bouteille = new Bouteille.BouteilleBuilder("bouteille")
		        .place("place")
		        .numPlace(1)
		        .line(2)
		        .column(3)
		        .type("type")
		        .annee("2018")
		        .color("Red")
		        .comment("comment")
		        .maturity("maturity")
		        .parker("100")
		        .price("123")
		        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
						.status("ToCheck")
		        .build();
	}
	
	@Test
	void testGetValue() {
		assertEquals("", MyCellarFields.getValue(MyCellarFields.NAME, null));
		assertEquals("bouteille", MyCellarFields.getValue(MyCellarFields.NAME, bouteille));
		assertEquals("place", MyCellarFields.getValue(MyCellarFields.PLACE, bouteille));
		assertEquals("1", MyCellarFields.getValue(MyCellarFields.NUM_PLACE, bouteille));
		assertEquals("2", MyCellarFields.getValue(MyCellarFields.LINE, bouteille));
		assertEquals("3", MyCellarFields.getValue(MyCellarFields.COLUMN, bouteille));
		assertEquals("type", MyCellarFields.getValue(MyCellarFields.TYPE, bouteille));
		assertEquals("2018", MyCellarFields.getValue(MyCellarFields.YEAR, bouteille));
		assertEquals("Red", MyCellarFields.getValue(MyCellarFields.COLOR, bouteille));
		assertEquals("comment", MyCellarFields.getValue(MyCellarFields.COMMENT, bouteille));
		assertEquals("maturity", MyCellarFields.getValue(MyCellarFields.MATURITY, bouteille));
		assertEquals("ToCheck", MyCellarFields.getValue(MyCellarFields.STATUS, bouteille));
		assertEquals("100", MyCellarFields.getValue(MyCellarFields.PARKER, bouteille));
		assertEquals("123", MyCellarFields.getValue(MyCellarFields.PRICE, bouteille));
		assertEquals("France", MyCellarFields.getValue(MyCellarFields.COUNTRY, bouteille));
		assertEquals("vignoble", MyCellarFields.getValue(MyCellarFields.VINEYARD, bouteille));
		assertEquals("aoc", MyCellarFields.getValue(MyCellarFields.AOC, bouteille));
		assertEquals("igp", MyCellarFields.getValue(MyCellarFields.IGP, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.EMPTY, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.USELESS, bouteille));
	}

	@Test
	void testHasSpecialHTMLCharacters() {
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(null));
		assertTrue(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.NAME));
		assertTrue(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.PLACE));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.NUM_PLACE));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.LINE));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.COLUMN));
		assertTrue(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.TYPE));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.YEAR));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.COLOR));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.STATUS));
		assertTrue(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.COMMENT));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.MATURITY));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.PARKER));
		assertTrue(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.PRICE));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.COUNTRY));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.VINEYARD));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.AOC));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.IGP));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.EMPTY));
		assertFalse(MyCellarFields.hasSpecialHTMLCharacters(MyCellarFields.USELESS));
	}

	@Test
	void testIsRealField() {
		assertFalse(MyCellarFields.isRealField(null));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.NAME));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.PLACE));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.NUM_PLACE));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.LINE));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.COLUMN));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.TYPE));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.YEAR));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.COLOR));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.STATUS));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.COMMENT));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.MATURITY));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.PARKER));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.PRICE));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.COUNTRY));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.VINEYARD));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.AOC));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.IGP));
		assertFalse(MyCellarFields.isRealField(MyCellarFields.EMPTY));
		assertFalse(MyCellarFields.isRealField(MyCellarFields.USELESS));
	}

	@Test
	void testGetFieldsList() {
		List<MyCellarFields> list = MyCellarFields.getFieldsList();
		assertTrue(list.contains(MyCellarFields.NAME));
		assertTrue(list.contains(MyCellarFields.PLACE));
		assertTrue(list.contains(MyCellarFields.NUM_PLACE));
		assertTrue(list.contains(MyCellarFields.LINE));
		assertTrue(list.contains(MyCellarFields.COLUMN));
		assertTrue(list.contains(MyCellarFields.TYPE));
		assertTrue(list.contains(MyCellarFields.YEAR));
		assertTrue(list.contains(MyCellarFields.COLOR));
		assertTrue(list.contains(MyCellarFields.STATUS));
		assertTrue(list.contains(MyCellarFields.COMMENT));
		assertTrue(list.contains(MyCellarFields.MATURITY));
		assertTrue(list.contains(MyCellarFields.PARKER));
		assertTrue(list.contains(MyCellarFields.PRICE));
		assertTrue(list.contains(MyCellarFields.COUNTRY));
		assertTrue(list.contains(MyCellarFields.VINEYARD));
		assertTrue(list.contains(MyCellarFields.AOC));
		assertTrue(list.contains(MyCellarFields.IGP));
		assertFalse(list.contains(MyCellarFields.EMPTY));
		assertFalse(list.contains(MyCellarFields.USELESS));
		assertEquals(17, list.size());
	}

}
