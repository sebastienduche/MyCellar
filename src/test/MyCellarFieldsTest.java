package test;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.core.BottlesStatus;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.music.MusicSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyCellarFieldsTest {

	private Bouteille bouteille;
	private Music music;
	
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
		        .vignoble("fr", "vignoble", "aoc", "igp")
						.status("TOCHECK")
		        .build();

		music = new Music.MusicBuilder("music")
				.place("armoire1x3x3")
				.numPlace(1)
				.line(2)
				.column(3)
				.genre("genre")
				.musicSupport(MusicSupport.CD)
				.annee("2018")
				.artist("artist")
				.composer("composer")
				.comment("comment")
				.duration("duration")
				.price("123")
				.track(1, "label", "duration", "comment")
				.status("TOCHECK")
				.diskNumber(1)
				.diskCount(2)
				.rating(5)
				.file("file")
				.album("album")
				.externalId(999)
				.build();
	}
	
	@Test
	void testGetValueBouteille() {
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
		assertEquals(BottlesStatus.TOCHECK.toString(), MyCellarFields.getValue(MyCellarFields.STATUS, bouteille));
		assertEquals("100", MyCellarFields.getValue(MyCellarFields.PARKER, bouteille));
		assertEquals("123", MyCellarFields.getValue(MyCellarFields.PRICE, bouteille));
		assertEquals("France", MyCellarFields.getValue(MyCellarFields.COUNTRY, bouteille));
		assertEquals("vignoble", MyCellarFields.getValue(MyCellarFields.VINEYARD, bouteille));
		assertEquals("aoc", MyCellarFields.getValue(MyCellarFields.AOC, bouteille));
		assertEquals("igp", MyCellarFields.getValue(MyCellarFields.IGP, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.EMPTY, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.USELESS, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.STYLE, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.COMPOSER, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.ARTIST, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.SUPPORT, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.DISK_COUNT, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.DISK_NUMBER, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.RATING, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.FILE, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.EXTERNAL_ID, bouteille));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.ALBUM, bouteille));
	}

	@Test
	void testGetValueMusic() {
		assertEquals("", MyCellarFields.getValue(MyCellarFields.NAME, null));
		assertEquals("music", MyCellarFields.getValue(MyCellarFields.NAME, music));
		assertEquals("armoire1x3x3", MyCellarFields.getValue(MyCellarFields.PLACE, music));
		assertEquals("1", MyCellarFields.getValue(MyCellarFields.NUM_PLACE, music));
		assertEquals("2", MyCellarFields.getValue(MyCellarFields.LINE, music));
		assertEquals("3", MyCellarFields.getValue(MyCellarFields.COLUMN, music));
		assertEquals("CD", MyCellarFields.getValue(MyCellarFields.TYPE, music));
		assertEquals("2018", MyCellarFields.getValue(MyCellarFields.YEAR, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.COLOR, music));
		assertEquals("comment", MyCellarFields.getValue(MyCellarFields.COMMENT, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.MATURITY, music));
		assertEquals(BottlesStatus.TOCHECK.toString(), MyCellarFields.getValue(MyCellarFields.STATUS, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.PARKER, music));
		assertEquals("123", MyCellarFields.getValue(MyCellarFields.PRICE, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.COUNTRY, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.VINEYARD, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.AOC, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.IGP, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.EMPTY, music));
		assertEquals("", MyCellarFields.getValue(MyCellarFields.USELESS, music));
		assertEquals("genre", MyCellarFields.getValue(MyCellarFields.STYLE, music));
		assertEquals("composer", MyCellarFields.getValue(MyCellarFields.COMPOSER, music));
		assertEquals("artist", MyCellarFields.getValue(MyCellarFields.ARTIST, music));
		assertEquals("CD", MyCellarFields.getValue(MyCellarFields.SUPPORT, music));
		assertEquals("2", MyCellarFields.getValue(MyCellarFields.DISK_COUNT, music));
		assertEquals("1", MyCellarFields.getValue(MyCellarFields.DISK_NUMBER, music));
		assertEquals("5", MyCellarFields.getValue(MyCellarFields.RATING, music));
		assertEquals("file", MyCellarFields.getValue(MyCellarFields.FILE, music));
		assertEquals("999", MyCellarFields.getValue(MyCellarFields.EXTERNAL_ID, music));
		assertEquals("album", MyCellarFields.getValue(MyCellarFields.ALBUM, music));
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
		assertTrue(MyCellarFields.isRealField(MyCellarFields.EXTERNAL_ID));
		assertTrue(MyCellarFields.isRealField(MyCellarFields.ALBUM));
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
