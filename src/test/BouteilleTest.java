package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryFactory;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.storage.ListeBouteille;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.ComplexPlaceBuilder;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.SimplePlace;
import mycellar.placesmanagement.places.SimplePlaceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static mycellar.ProgramConstants.DATE_FORMATER_DDMMYYYY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BouteilleTest {

  private Bouteille bouteille;
  private Bouteille bouteilleCaisse;
  private ComplexPlace armoire1x3x3;
  private SimplePlace caisse;

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
        .vignoble("fr", "vignoble", "aoc", "igp", UUID.randomUUID(), UUID.randomUUID())
        .build();

    // Caisse avec 2 emplacements commencant a 1 et limite a 6 bouteilles
    armoire1x3x3 = new ComplexPlaceBuilder("armoire1x3x3")
        .nbParts(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    caisse = new SimplePlaceBuilder("caisse")
        .nbParts(1)
        .startSimplePlace(1)
        .build();
    bouteilleCaisse = new Bouteille.BouteilleBuilder("bouteille")
        .place("caisse")
        .numPlace(1)
        .build();

    Program.addPlace(armoire1x3x3);
    Program.addPlace(caisse);
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
  void getUuid() {
    UUID uuid = UUID.randomUUID();
    bouteille.setUuid(uuid);
    assertEquals(uuid, bouteille.getUuid());
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
    assertEquals("type", bouteille.getKind());
  }

  @Test
  void setType() {
    bouteille.setKind("test");
    assertEquals("test", bouteille.getKind());
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
    bouteille.setColor("pink");
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
        .vignoble("fr", "b", "c", "d", UUID.randomUUID(), UUID.randomUUID())
        .build();
    bouteille.update(test);
    assertEquals("b", bouteille.getNom());
    assertEquals("p", bouteille.getEmplacement());
    assertEquals(9, bouteille.getNumLieu());
    assertEquals(99, bouteille.getLigne());
    assertEquals(999, bouteille.getColonne());
    assertEquals("t", bouteille.getKind());
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
  void getAbstractPlace() {
    assertEquals(armoire1x3x3, bouteille.getAbstractPlace());
  }

  @Test
  void getPlacePosition() {
    PlacePosition place = bouteille.getPlacePosition();
    assertEquals(armoire1x3x3, place.getAbstractPlace());
    assertEquals(1, place.getPart());
    assertEquals(2, place.getLine());
    assertEquals(3, place.getColumn());

    place = bouteilleCaisse.getPlacePosition();
    assertEquals(caisse, place.getAbstractPlace());
    assertEquals(1, place.getPart());
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
    UUID uuid = UUID.randomUUID();
    String xml = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <ListeBouteille><Bouteille>
                <id>%d</id>
                <nom>%s</nom>
                <annee>%s</annee>
                <type>%s</type>
                <emplacement>%s</emplacement>
                <num_lieu>%d</num_lieu>
                <ligne>%d</ligne>
                <colonne>%d</colonne>
                <prix>%s</prix>
                <comment>%s</comment>
                <maturity>%s</maturity>
                <parker>%s</parker>
                <vignoble>
                    <country>%s</country>
                    <name>%s</name>
                    <AOC>%s</AOC>
                    <IGP>%s</IGP>
                    <AOP></AOP>
                    <id>27618</id>
                </vignoble>
                <color>%s</color>
                <status>%s</status>
                <lastModified>%s</lastModified>
                <uuid>%s</uuid>
            </Bouteille></ListeBouteille>""".formatted(id, name, year, type, place, numPlace, line, column, price,
        comment, maturity, parker, country, vignoble, aoc, igp, color, status, lastModified, uuid);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    doc.getDocumentElement().normalize();
    NodeList nodeList = doc.getElementsByTagName("Bouteille");
    final Bouteille bouteilleFromXML = Bouteille.fromXml((Element) nodeList.item(0));
    assertEquals(id, bouteilleFromXML.getId());
    assertEquals(name, bouteilleFromXML.getNom());
    assertEquals(year, bouteilleFromXML.getAnnee());
    assertEquals(type, bouteilleFromXML.getKind());
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
    assertEquals(uuid.toString(), bouteilleFromXML.getUuid().toString());
    assertEquals(country, bouteilleFromXML.getVignoble().getCountry());
    assertEquals(vignoble, bouteilleFromXML.getVignoble().getName());
    assertEquals(aoc, bouteilleFromXML.getVignoble().getAOC());
    assertEquals(igp, bouteilleFromXML.getVignoble().getIGP());
  }

  @Test
  void shouldMarshalBottle() {
    ListeBouteille listeBouteille = new ListeBouteille();
    listeBouteille.getBouteille().add(new Bouteille.BouteilleBuilder("b")
        .id(27618)
        .uuid(UUID.fromString("a839b533-1a04-4a4b-94de-fa771bcbdeb7"))
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
        .vignoble("fr", "b", "c", "d", UUID.fromString("4baf6f7c-c9a4-40f4-a5bc-bc73fc43db05"), UUID.fromString("d9c2f699-c23f-4225-a42b-a53f85ee82a4"))
        .lastModified("17-11-2020 12:08")
        .build());
    try {
      JAXBContext jc = JAXBContext.newInstance(mycellar.core.storage.ObjectFactory.class);
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      StringWriter writer = new StringWriter();
      m.marshal(listeBouteille, new StreamResult(writer));
      writer.flush();
      String result = writer.toString();
      assertEquals("""
          <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
          <ListeBouteille>
              <Bouteille>
                  <id>27618</id>
                  <nom>b</nom>
                  <annee>2</annee>
                  <type>t</type>
                  <emplacement>p</emplacement>
                  <num_lieu>9</num_lieu>
                  <ligne>99</ligne>
                  <colonne>999</colonne>
                  <prix>23</prix>
                  <comment>c</comment>
                  <maturity>m</maturity>
                  <parker>1</parker>
                  <vignoble>
                      <country>fr</country>
                      <AOC>c</AOC>
                      <IGP>d</IGP>
                      <name>b</name>
                      <id>3</id>
                      <uuid>4baf6f7c-c9a4-40f4-a5bc-bc73fc43db05</uuid>
                      <countryUuid>d9c2f699-c23f-4225-a42b-a53f85ee82a4</countryUuid>
                  </vignoble>
                  <color>R</color>
                  <status></status>
                  <lastModified>17-11-2020 12:08</lastModified>
                  <uuid>a839b533-1a04-4a4b-94de-fa771bcbdeb7</uuid>
              </Bouteille>
          </ListeBouteille>
          """, result);
    } catch (JAXBException ignored) {
    }
  }

  @Test
  void shouldUnmarshal() throws ParserConfigurationException, IOException, SAXException {
    Bouteille bottle = new Bouteille.BouteilleBuilder("b")
        .id(27618)
        .uuid(UUID.fromString("a839b533-1a04-4a4b-94de-fa771bcbdeb7"))
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
        .lastModified("")
        .vignoble("fr", "b", "c", "d", UUID.randomUUID(), UUID.randomUUID())
        .lastModified("17-11-2020 12:08")
        .build();
    String xml = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <ListeBouteille>
            <Bouteille>
                <id>27618</id>
                <nom>b</nom>
                <annee>2</annee>
                <type>t</type>
                <emplacement>p</emplacement>
                <num_lieu>9</num_lieu>
                <ligne>99</ligne>
                <colonne>999</colonne>
                <prix>23</prix>
                <comment>c</comment>
                <maturity>m</maturity>
                <parker>1</parker>
                <vignoble>
                    <country>fr</country>
                    <AOC>c</AOC>
                    <IGP>d</IGP>
                    <name>b</name>
                    <id>3</id>
                </vignoble>
                <color>R</color>
                <status></status>
                <uuid>a839b533-1a04-4a4b-94de-fa771bcbdeb7</uuid>
                <lastModified>17-11-2020 12:08</lastModified>
            </Bouteille>
        </ListeBouteille>
        """;
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    doc.getDocumentElement().normalize();
    NodeList nodeList = doc.getElementsByTagName("Bouteille");
    final Bouteille bouteilleFromXML = Bouteille.fromXml((Element) nodeList.item(0));
    assertEquals(bottle.getId(), bouteilleFromXML.getId());
    assertEquals(bottle.getNom(), bouteilleFromXML.getNom());
    assertEquals(bottle.getAnnee(), bouteilleFromXML.getAnnee());
    assertEquals(bottle.getKind(), bouteilleFromXML.getKind());
    assertEquals(bottle.getEmplacement(), bouteilleFromXML.getEmplacement());
    assertEquals(bottle.getNumLieu(), bouteilleFromXML.getNumLieu());
    assertEquals(bottle.getLigne(), bouteilleFromXML.getLigne());
    assertEquals(bottle.getColonne(), bouteilleFromXML.getColonne());
    assertEquals(bottle.getPrix(), bouteilleFromXML.getPrix());
    assertEquals(bottle.getComment(), bouteilleFromXML.getComment());
    assertEquals(bottle.getMaturity(), bouteilleFromXML.getMaturity());
    assertEquals(bottle.getParker(), bouteilleFromXML.getParker());
    assertEquals(bottle.getColor(), bouteilleFromXML.getColor());
    assertEquals(bottle.getStatus(), bouteilleFromXML.getStatus());
    assertEquals(bottle.getLastModified(), bouteilleFromXML.getLastModified());
    assertEquals(bottle.getUuid().toString(), bouteilleFromXML.getUuid().toString());
    assertEquals(bottle.getVignoble().getCountry(), bouteilleFromXML.getVignoble().getCountry());
    assertEquals(bottle.getVignoble().getName(), bouteilleFromXML.getVignoble().getName());
    assertEquals(bottle.getVignoble().getAOC(), bouteilleFromXML.getVignoble().getAOC());
    assertEquals(bottle.getVignoble().getIGP(), bouteilleFromXML.getVignoble().getIGP());
  }

  @Test
  void shouldMarshalHistory() {
    HistoryList list = new HistoryList();
    History history = new History(new Bouteille.BouteilleBuilder("b")
        .id(27618)
        .uuid(UUID.fromString("a839b533-1a04-4a4b-94de-fa771bcbdeb7"))
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
        .vignoble("fr", "b", "c", "d", UUID.fromString("4baf6f7c-c9a4-40f4-a5bc-bc73fc43db05"), UUID.fromString("d9c2f699-c23f-4225-a42b-a53f85ee82a4"))
        .lastModified("17-11-2020 12:08")
        .build(),
        1, 2);
    history.setUuid(UUID.fromString("a839b533-1a04-4a4b-94de-fa771bcbdeb7"));
    list.add(history);
    try {
      JAXBContext jc = JAXBContext.newInstance(HistoryFactory.class);
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      StringWriter writer = new StringWriter();
      m.marshal(list, new StreamResult(writer));

      writer.flush();
      String result = writer.toString();
      assertEquals("""
          <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
          <HistoryList>
              <History>
                  <date>%s</date>
                  <type>1</type>
                  <Bouteille>
                      <id>27618</id>
                      <nom>b</nom>
                      <annee>2</annee>
                      <type>t</type>
                      <emplacement>p</emplacement>
                      <num_lieu>9</num_lieu>
                      <ligne>99</ligne>
                      <colonne>999</colonne>
                      <prix>23</prix>
                      <comment>c</comment>
                      <maturity>m</maturity>
                      <parker>1</parker>
                      <vignoble>
                          <country>fr</country>
                          <AOC>c</AOC>
                          <IGP>d</IGP>
                          <name>b</name>
                          <id>3</id>
                          <uuid>4baf6f7c-c9a4-40f4-a5bc-bc73fc43db05</uuid>
                          <countryUuid>d9c2f699-c23f-4225-a42b-a53f85ee82a4</countryUuid>
                      </vignoble>
                      <color>R</color>
                      <status></status>
                      <lastModified>17-11-2020 12:08</lastModified>
                      <uuid>a839b533-1a04-4a4b-94de-fa771bcbdeb7</uuid>
                  </Bouteille>
                  <totalBottle>2</totalBottle>
                  <uuid>a839b533-1a04-4a4b-94de-fa771bcbdeb7</uuid>
              </History>
          </HistoryList>
          """.formatted(LocalDate.now().format(DATE_FORMATER_DDMMYYYY)), result);
    } catch (JAXBException ignored) {
    }
  }

  @Test
  void shouldUnmarshalHistory() throws ParserConfigurationException, IOException, SAXException {
    Bouteille bottle = new Bouteille.BouteilleBuilder("b")
        .id(27618)
        .uuid(UUID.fromString("a839b533-1a04-4a4b-94de-fa771bcbdeb7"))
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
        .lastModified("")
        .vignoble("fr", "b", "c", "d", UUID.fromString("4baf6f7c-c9a4-40f4-a5bc-bc73fc43db05"), UUID.fromString("d9c2f699-c23f-4225-a42b-a53f85ee82a4"))
        .lastModified("17-11-2020 12:08")
        .build();
    String xml = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
          <HistoryList>
              <History>
                  <date>04/04/2026</date>
                  <type>1</type>
                  <Bouteille>
                      <id>27618</id>
                      <nom>b</nom>
                      <annee>2</annee>
                      <type>t</type>
                      <emplacement>p</emplacement>
                      <num_lieu>9</num_lieu>
                      <ligne>99</ligne>
                      <colonne>999</colonne>
                      <prix>23</prix>
                      <comment>c</comment>
                      <maturity>m</maturity>
                      <parker>1</parker>
                      <vignoble>
                          <country>fr</country>
                          <AOC>c</AOC>
                          <IGP>d</IGP>
                          <name>b</name>
                          <id>3</id>
                          <uuid>4baf6f7c-c9a4-40f4-a5bc-bc73fc43db05</uuid>
                          <countryUuid>d9c2f699-c23f-4225-a42b-a53f85ee82a4</countryUuid>
                      </vignoble>
                      <color>R</color>
                      <status></status>
                      <lastModified>17-11-2020 12:08</lastModified>
                      <uuid>a839b533-1a04-4a4b-94de-fa771bcbdeb7</uuid>
                  </Bouteille>
                  <totalBottle>2</totalBottle>
                  <uuid>a839b533-1a04-4a4b-94de-fa771bcbdeb8</uuid>
              </History>
          </HistoryList>
        """;
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    doc.getDocumentElement().normalize();
    NodeList nodeList = doc.getElementsByTagName("History");
    Element historyElement = (Element) nodeList.item(0);
    String date = historyElement.getElementsByTagName("date").item(0).getTextContent();
    String type = historyElement.getElementsByTagName("type").item(0).getTextContent();
    String totalBottle = historyElement.getElementsByTagName("totalBottle").item(0).getTextContent();
    String uuid = historyElement.getElementsByTagName("uuid").item(2).getTextContent();
    final Bouteille bouteilleFromXML = Bouteille.fromXml((Element) historyElement.getElementsByTagName("Bouteille").item(0));
    assertEquals("04/04/2026", date);
    assertEquals("1", type);
    assertEquals("2", totalBottle);
    assertEquals("a839b533-1a04-4a4b-94de-fa771bcbdeb8", uuid);
    assertEquals(bottle.getId(), bouteilleFromXML.getId());
    assertEquals(bottle.getNom(), bouteilleFromXML.getNom());
    assertEquals(bottle.getAnnee(), bouteilleFromXML.getAnnee());
    assertEquals(bottle.getKind(), bouteilleFromXML.getKind());
    assertEquals(bottle.getEmplacement(), bouteilleFromXML.getEmplacement());
    assertEquals(bottle.getNumLieu(), bouteilleFromXML.getNumLieu());
    assertEquals(bottle.getLigne(), bouteilleFromXML.getLigne());
    assertEquals(bottle.getColonne(), bouteilleFromXML.getColonne());
    assertEquals(bottle.getPrix(), bouteilleFromXML.getPrix());
    assertEquals(bottle.getComment(), bouteilleFromXML.getComment());
    assertEquals(bottle.getMaturity(), bouteilleFromXML.getMaturity());
    assertEquals(bottle.getParker(), bouteilleFromXML.getParker());
    assertEquals(bottle.getColor(), bouteilleFromXML.getColor());
    assertEquals(bottle.getStatus(), bouteilleFromXML.getStatus());
    assertEquals(bottle.getLastModified(), bouteilleFromXML.getLastModified());
    assertEquals(bottle.getUuid().toString(), bouteilleFromXML.getUuid().toString());
    assertEquals(bottle.getVignoble().getCountry(), bouteilleFromXML.getVignoble().getCountry());
    assertEquals(bottle.getVignoble().getName(), bouteilleFromXML.getVignoble().getName());
    assertEquals(bottle.getVignoble().getAOC(), bouteilleFromXML.getVignoble().getAOC());
    assertEquals(bottle.getVignoble().getIGP(), bouteilleFromXML.getVignoble().getIGP());
    assertEquals(bottle.getVignoble().getCountryUuid(), bouteilleFromXML.getVignoble().getCountryUuid());
    assertEquals(bottle.getVignoble().getUuid(), bouteilleFromXML.getVignoble().getUuid());
  }
}
