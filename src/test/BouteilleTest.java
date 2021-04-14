package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BouteilleTest {

  private Bouteille bouteille;
  private Bouteille bouteilleCaisse;
  private Rangement armoire1x3x3;
  private Rangement caisse;

  @BeforeEach
  void setUp() {
    bouteille = new Bouteille.BouteilleBuilder("bouteille")
        .place("armoire1x3x3")
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
        .build();

    // Caisse avec 2 emplacements commençant à 1 et limité à 6 bouteilles
    armoire1x3x3 = new Rangement.RangementBuilder("armoire1x3x3")
        .nb_emplacement(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    caisse = new Rangement.CaisseBuilder("caisse")
        .nb_emplacement(1)
        .start_caisse(1)
        .build();
    bouteilleCaisse = new Bouteille.BouteilleBuilder("bouteille")
        .place("caisse")
        .numPlace(1)
        .build();

    Program.addCave(armoire1x3x3);
    Program.addCave(caisse);
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
    assertEquals("armoire1x3x3", bouteille.getEmplacement());
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
    assertEquals(v, bouteille.getVignoble());
  }

  @Test
  void setVignoble() {
    VignobleJaxb v = new VignobleJaxb();
    v.setCountry("f");
    v.setName("n");
    v.setIGP("i");
    v.setAOC("ao");
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
        .vignoble("fr", "b", "c", "d")
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
    v.setCountry("fr");
    v.setName("b");
    v.setAOC("c");
    v.setIGP("d");
    assertEquals(v.toString(), bouteille.getVignoble().toString());
  }

  @Test
  void getRangement() {
    assertEquals(armoire1x3x3 ,bouteille.getRangement());
  }

  @Test
  void getPlace() {
    Place place = bouteille.getPlace();
    assertEquals(armoire1x3x3, place.getRangement());
    assertEquals(1, place.getPlaceNum());
    assertEquals(2, place.getLine());
    assertEquals(3, place.getColumn());

    place = bouteilleCaisse.getPlace();
    assertEquals(caisse, place.getRangement());
    assertEquals(1, place.getPlaceNum());
  }

  @Test
  void setValue() {
  }

  @Test
  void isInTemporaryStock() {
  }

  @Test
  void getBouteilleFromXML() throws ParserConfigurationException, IOException, SAXException {
    final int id = 1987;
    final String name = "Aalto PS 04";
    final String year = "2004";
    final String type = "75cl";
    final String place = "Courlon 3a";
    final int numPlace = 0;
    final int line = 1;
    final int column = 2;
    final String price = "75";
    final String comment = "comment";
    final String maturity = "2018-2030";
    final String parker = "89";
    final String country = "ESP";
    final String vignoble = "Castilla y Leon";
    final String aoc = "Ribera del Duero";
    final String igp = "Ribera del Duero";
    final String color = "RED";
    final String status = "MODIFIED";
    final String lastModified = "17-11-2020 12:08";
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<ListeBouteille><Bouteille>\n" +
        "        <id>" + id + "</id>\n" +
        "        <nom>" + name + "</nom>\n" +
        "        <annee>" + year + "</annee>\n" +
        "        <type>" + type + "</type>\n" +
        "        <emplacement>" + place + "</emplacement>\n" +
        "        <num_lieu>" + numPlace + "</num_lieu>\n" +
        "        <ligne>" + line + "</ligne>\n" +
        "        <colonne>" + column + "</colonne>\n" +
        "        <prix>" + price + "</prix>\n" +
        "        <comment>" + comment + "</comment>\n" +
        "        <maturity>" + maturity + "</maturity>\n" +
        "        <parker>" + parker + "</parker>\n" +
        "        <vignoble>\n" +
        "            <country>" + country + "</country>\n" +
        "            <name>" + vignoble + "</name>\n" +
        "            <AOC>" + aoc + "</AOC>\n" +
        "            <IGP>" + igp + "</IGP>\n" +
        "            <AOP></AOP>\n" +
        "            <id>27618</id>\n" +
        "        </vignoble>\n" +
        "        <color>" + color + "</color>\n" +
        "        <status>" + status + "</status>\n" +
        "        <lastModified>" + lastModified + "</lastModified>\n" +
        "    </Bouteille></ListeBouteille>";
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    doc.getDocumentElement().normalize();
    NodeList nodeList = doc.getElementsByTagName("Bouteille");
    final Bouteille bouteilleFromXML = Bouteille.getBouteilleFromXML((Element) nodeList.item(0));
    assertEquals(id, bouteilleFromXML.getId());
    assertEquals(name, bouteilleFromXML.getNom());
    assertEquals(year, bouteilleFromXML.getAnnee());
    assertEquals(type, bouteilleFromXML.getType());
    assertEquals(place, bouteilleFromXML.getEmplacement());
    assertEquals(numPlace, bouteilleFromXML.getNumLieu());
    assertEquals(line, bouteilleFromXML.getLigne());
    assertEquals(column, bouteilleFromXML.getColonne());
    assertEquals(price, bouteilleFromXML.getPrix());
    assertEquals(comment, bouteilleFromXML.getComment());
    assertEquals(maturity, bouteilleFromXML.getMaturity());
    assertEquals(parker, bouteilleFromXML.getParker());
    assertEquals(color, bouteilleFromXML.getColor());
    assertEquals(status, bouteilleFromXML.getStatus());
    assertEquals(lastModified, bouteilleFromXML.getLastModified());
    assertEquals(country, bouteilleFromXML.getVignoble().getCountry());
    assertEquals(vignoble, bouteilleFromXML.getVignoble().getName());
    assertEquals(aoc, bouteilleFromXML.getVignoble().getAOC());
    assertEquals(igp, bouteilleFromXML.getVignoble().getIGP());
  }
}
