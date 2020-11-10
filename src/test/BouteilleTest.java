package test;

import mycellar.Bouteille;
import mycellar.core.datas.jaxb.VignobleJaxb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BouteilleTest {

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
        .build();
  }

  @Test
  void getId() {
    bouteille.setId(123);
    assertEquals(123, bouteille.getId());
  }

  @Test
  void setId() {
    bouteille.setId(123);
    assertEquals(123, bouteille.getId());
  }

  @Test
  void getNom() {
    assertEquals("bouteille", bouteille.getNom());
  }

  @Test
  void setNom() {
    bouteille.setNom("val");
    assertEquals("val", bouteille.getNom());
  }

  @Test
  void getAnnee() {
    assertEquals("2018", bouteille.getAnnee());
  }

  @Test
  void setAnnee() {
    bouteille.setAnnee("2020");
    assertEquals("2020", bouteille.getAnnee());
  }

  @Test
  void getType() {
    assertEquals("type", bouteille.getType());
  }

  @Test
  void setType() {
    bouteille.setType("test");
    assertEquals("test", bouteille.getType());
  }

  @Test
  void getEmplacement() {
    assertEquals("place", bouteille.getEmplacement());
  }

  @Test
  void setEmplacement() {
    bouteille.setEmplacement("test");
    assertEquals("test", bouteille.getEmplacement());
  }

  @Test
  void getNumLieu() {
    assertEquals(1, bouteille.getNumLieu());
  }

  @Test
  void setNumLieu() {
    bouteille.setNumLieu(9);
    assertEquals(9, bouteille.getNumLieu());
  }

  @Test
  void getLigne() {
    assertEquals(2, bouteille.getLigne());
  }

  @Test
  void setLigne() {
    bouteille.setLigne(9);
    assertEquals(9, bouteille.getLigne());
  }

  @Test
  void getColonne() {
    assertEquals(3, bouteille.getColonne());
  }

  @Test
  void setColonne() {
    bouteille.setColonne(9);
    assertEquals(9, bouteille.getColonne());
  }

  @Test
  void getPrix() {
    assertEquals("123", bouteille.getPrix());
  }

  @Test
  void setPrix() {
    bouteille.setPrix("999");
    assertEquals("999", bouteille.getPrix());
  }

  @Test
  void getComment() {
    assertEquals("comment", bouteille.getComment());
  }

  @Test
  void setComment() {
    bouteille.setComment("test");
    assertEquals("test", bouteille.getComment());
  }

  @Test
  void getMaturity() {
    assertEquals("maturity", bouteille.getMaturity());
  }

  @Test
  void setMaturity() {
    bouteille.setMaturity("m");
    assertEquals("m", bouteille.getMaturity());
  }

  @Test
  void getParker() {
    assertEquals("100", bouteille.getParker());
  }

  @Test
  void setParker() {
    bouteille.setParker("pa");
    assertEquals("pa", bouteille.getParker());
  }

  @Test
  void getColor() {
    assertEquals("Red", bouteille.getColor());
  }

  @Test
  void setColor() {
    bouteille.setColor("color");
    assertEquals("color", bouteille.getColor());
  }

  @Test
  void getVignoble() {
    VignobleJaxb v = new VignobleJaxb();
    v.setCountry("fr");
    v.setName("vignoble");
    v.setIGP("igp");
    v.setAOC("aoc");
    v.setAOP("aop");
    assertEquals(v, bouteille.getVignoble());
  }

  @Test
  void setVignoble() {
    VignobleJaxb v = new VignobleJaxb();
    v.setCountry("f");
    v.setName("n");
    v.setIGP("i");
    v.setAOC("ao");
    v.setAOP("aop");
    bouteille.setVignoble(v);
    assertEquals(v, bouteille.getVignoble());
  }

  @Test
  void getAnneeInt() {
    assertEquals(2018, bouteille.getAnneeInt());
    bouteille.setAnnee("");
    assertEquals(0, bouteille.getAnneeInt());
    bouteille.setAnnee(Bouteille.NON_VINTAGE);
    assertEquals(9999, bouteille.getAnneeInt());
  }

  @Test
  void isNonVintage() {
    assertFalse(bouteille.isNonVintage());
    bouteille.setAnnee(Bouteille.NON_VINTAGE);
    assertTrue(bouteille.isNonVintage());
  }

  @Test
  void getPriceDouble() {
    assertEquals(123.00, bouteille.getPriceDouble());
    bouteille.setPrix("123.45");
    assertEquals(123.45, bouteille.getPriceDouble());
    bouteille.setPrix("");
    assertEquals(0.00, bouteille.getPriceDouble());
  }

  @Test
  void getPrice() {
    assertEquals(new BigDecimal("123.00"), bouteille.getPrice());
    bouteille.setPrix("123.45");
    assertEquals(new BigDecimal("123.45"), bouteille.getPrice());
  }

  @Test
  void hasPrice() {
    assertTrue(bouteille.hasPrice());
    bouteille.setPrix("");
    assertFalse(bouteille.hasPrice());
    bouteille.setPrix(Bouteille.NON_VINTAGE);
    assertFalse(bouteille.hasPrice());
  }

  @Test
  void isRedWine() {
    assertTrue(bouteille.isRedWine());
    bouteille.setColor("");
    assertFalse(bouteille.isRedWine());
    bouteille.setColor("pink");
    assertFalse(bouteille.isRedWine());
    bouteille.setColor("white");
    assertFalse(bouteille.isRedWine());
  }

  @Test
  void isWhiteWine() {
    assertFalse(bouteille.isWhiteWine());
    bouteille.setColor("");
    assertFalse(bouteille.isWhiteWine());
    bouteille.setColor("pink");
    assertFalse(bouteille.isWhiteWine());
    bouteille.setColor("White");
    assertTrue(bouteille.isWhiteWine());
  }

  @Test
  void isPinkWine() {
    assertFalse(bouteille.isPinkWine());
    bouteille.setColor("");
    assertFalse(bouteille.isPinkWine());
    bouteille.setColor("Pink");
    assertTrue(bouteille.isPinkWine());
    bouteille.setColor("white");
    assertFalse(bouteille.isPinkWine());
  }

  @Test
  void update() {
    Bouteille test = new Bouteille.BouteilleBuilder("b")
        .place("p")
        .numPlace(9)
        .line(99)
        .column(999)
        .type("t")
        .annee("2")
        .color("R")
        .comment("c")
        .maturity("m")
        .parker("1")
        .price("23")
        .vignoble("a", "b", "c", "d", "e")
        .build();
    bouteille.update(test);
    assertEquals("b", bouteille.getNom());
    assertEquals("p", bouteille.getEmplacement());
    assertEquals(9, bouteille.getNumLieu());
    assertEquals(99, bouteille.getLigne());
    assertEquals(999, bouteille.getColonne());
    assertEquals("t", bouteille.getType());
    assertEquals("2", bouteille.getAnnee());
    assertEquals("R", bouteille.getColor());
    assertEquals("c", bouteille.getComment());
    assertEquals("m", bouteille.getMaturity());
    assertEquals("1", bouteille.getParker());
    assertEquals(new BigDecimal("23.00"), bouteille.getPrice());
    VignobleJaxb v = new VignobleJaxb();
    v.setCountry("a");
    v.setName("b");
    v.setAOC("c");
    v.setIGP("d");
    v.setAOP("e");
    assertEquals(v, bouteille.getVignoble());
  }

  @Test
  void setValue() {
  }

  @Test
  void isInTemporaryStock() {
  }

  @Test
  void getBouteilleFromXML() {
  }
}
