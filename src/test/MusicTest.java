package test;

import mycellar.Music;
import mycellar.Program;
import mycellar.core.common.music.MusicSupport;
import mycellar.core.datas.jaxb.tracks.Track;
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

class MusicTest {

  private Music music;
  private Music musicCaisse;
  private Rangement armoire1x3x3;
  private Rangement caisse;

  @BeforeEach
  void setUp() {
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
        .diskNumber(1)
        .diskCount(2)
        .rating(5)
        .file("file")
        .album("album")
        .externalId(999)
        .track(1, "label", "duration", "comment")
        .build();

    // Caisse avec 2 emplacements commencant a 1 et limite a 6 musics
    armoire1x3x3 = new Rangement.RangementBuilder("armoire1x3x3")
        .nbParts(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    caisse = new Rangement.SimplePlaceBuilder("caisse")
        .nbParts(1)
        .startSimplePlace(1)
        .build();
    musicCaisse = new Music.MusicBuilder("music")
        .place("caisse")
        .numPlace(1)
        .build();

    Program.addCave(armoire1x3x3);
    Program.addCave(caisse);
  }

  @Test
  void getId() {
    music.setId(123);
    assertEquals(123, music.getId());
  }

  @Test
  void setId() {
    music.setId(123);
    assertEquals(123, music.getId());
  }

  @Test
  void getExternalId() {
    assertEquals(999, music.getExternalId());
  }

  @Test
  void setExternalId() {
    music.setExternalId(1234);
    assertEquals(1234, music.getExternalId());
  }

  @Test
  void getNom() {
    assertEquals("music", music.getNom());
  }

  @Test
  void setNom() {
    music.setNom("val");
    assertEquals("val", music.getNom());
  }

  @Test
  void getAnnee() {
    assertEquals("2018", music.getAnnee());
  }

  @Test
  void setAnnee() {
    music.setAnnee("2020");
    assertEquals("2020", music.getAnnee());
  }

  @Test
  void getAlbum() {
    assertEquals("album", music.getAlbum());
  }

  @Test
  void setAlbum() {
    music.setAlbum("Album");
    assertEquals("Album", music.getAlbum());
  }

  @Test
  void getKind() {
    assertEquals("CD", music.getKind());
  }

  @Test
  void setKind() {
    music.setKind("test");
    assertEquals("test", music.getKind());
  }

  @Test
  void getMusicSupport() {
    assertEquals(MusicSupport.CD, music.getMusicSupport());
  }

  @Test
  void setMusicSupport() {
    music.setMusicSupport(MusicSupport.K7);
    assertEquals("K7", music.getKind());
    assertEquals(MusicSupport.K7, music.getMusicSupport());
    music.setKind(null);
    assertEquals(MusicSupport.NONE, music.getMusicSupport());
  }

  @Test
  void getDuration() {
    assertEquals("duration", music.getDuration());
  }

  @Test
  void setDuration() {
    music.setDuration("test");
    assertEquals("test", music.getDuration());
  }

  @Test
  void getEmplacement() {
    assertEquals("armoire1x3x3", music.getEmplacement());
  }

  @Test
  void setEmplacement() {
    music.setEmplacement("test");
    assertEquals("test", music.getEmplacement());
  }

  @Test
  void getNumLieu() {
    assertEquals(1, music.getNumLieu());
  }

  @Test
  void setNumLieu() {
    music.setNumLieu(9);
    assertEquals(9, music.getNumLieu());
  }

  @Test
  void getLigne() {
    assertEquals(2, music.getLigne());
  }

  @Test
  void setLigne() {
    music.setLigne(9);
    assertEquals(9, music.getLigne());
  }

  @Test
  void getColonne() {
    assertEquals(3, music.getColonne());
  }

  @Test
  void setColonne() {
    music.setColonne(9);
    assertEquals(9, music.getColonne());
  }

  @Test
  void getDiskNumber() {
    assertEquals(1, music.getDiskNumber());
  }

  @Test
  void setDiskNumber() {
    music.setDiskNumber(9);
    assertEquals(9, music.getDiskNumber());
  }

  @Test
  void getDiskCount() {
    assertEquals(2, music.getDiskCount());
  }

  @Test
  void setDiskCount() {
    music.setDiskCount(9);
    assertEquals(9, music.getDiskCount());
  }

  @Test
  void getRating() {
    assertEquals(5, music.getRating());
  }

  @Test
  void setRating() {
    music.setRating(9);
    assertEquals(9, music.getRating());
  }

  @Test
  void getPrix() {
    assertEquals("123", music.getPrix());
  }

  @Test
  void setPrix() {
    music.setPrix("999");
    assertEquals("999", music.getPrix());
  }

  @Test
  void getFile() {
    assertEquals("file", music.getFile());
  }

  @Test
  void setFile() {
    music.setFile("999");
    assertEquals("999", music.getFile());
  }

  @Test
  void getComment() {
    assertEquals("comment", music.getComment());
  }

  @Test
  void setComment() {
    music.setComment("test");
    assertEquals("test", music.getComment());
  }

  @Test
  void getGenre() {
    assertEquals("genre", music.getGenre());
  }

  @Test
  void setGenre() {
    music.setGenre("m");
    assertEquals("m", music.getGenre());
  }

  @Test
  void getArtist() {
    assertEquals("artist", music.getArtist());
  }

  @Test
  void setArtist() {
    music.setArtist("pa");
    assertEquals("pa", music.getArtist());
  }

  @Test
  void getComposer() {
    assertEquals("composer", music.getComposer());
  }

  @Test
  void setComposer() {
    music.setComposer("color");
    assertEquals("color", music.getComposer());
  }

  @Test
  void getTrack() {
    Track v = new Track();
    v.setNumber(1);
    v.setLabel("label");
    v.setDuration("duration");
    v.setComment("comment");
    assertEquals(v, music.getTracks().getTracks().get(0));
  }

  @Test
  void setTrack() {
    Track v = new Track();
    v.setNumber(2);
    v.setLabel("label1");
    v.setDuration("duration1");
    v.setComment("comment1");
    music.getTracks().getTracks().add(v);
    assertEquals(v, music.getTracks().getTracks().get(1));
  }

  @Test
  void getPriceDouble() {
    assertEquals(123.00, music.getPriceDouble());
    music.setPrix("123.45");
    assertEquals(123.45, music.getPriceDouble());
    music.setPrix("");
    assertEquals(0.00, music.getPriceDouble());
  }

  @Test
  void getPrice() {
    assertEquals(new BigDecimal("123.00"), music.getPrice());
    music.setPrix("123.45");
    assertEquals(new BigDecimal("123.45"), music.getPrice());
  }

  @Test
  void hasPrice() {
    assertTrue(music.hasPrice());
    music.setPrix("");
    assertFalse(music.hasPrice());
  }

  @Test
  void update() {
    Music test = new Music.MusicBuilder("b")
        .place("p")
        .numPlace(9)
        .line(99)
        .column(999)
        .musicSupport(MusicSupport.DIGITAL)
        .annee("2")
        .composer("R")
        .comment("c")
        .artist("m")
        .genre("1")
        .price("23")
        .duration("dr")
        .diskCount(3)
        .diskNumber(2)
        .rating(0)
        .file("test")
        .externalId(10)
        .album("a")
        .track(1, "label", "duration", "comment")
        .build();
    music.update(test);
    assertEquals("b", music.getNom());
    assertEquals("p", music.getEmplacement());
    assertEquals(10, music.getExternalId());
    assertEquals(9, music.getNumLieu());
    assertEquals(99, music.getLigne());
    assertEquals(999, music.getColonne());
    assertEquals("DIGITAL", music.getKind());
    assertEquals("2", music.getAnnee());
    assertEquals(MusicSupport.DIGITAL, music.getMusicSupport());
    assertEquals("c", music.getComment());
    assertEquals("m", music.getArtist());
    assertEquals("R", music.getComposer());
    assertEquals("1", music.getGenre());
    assertEquals("dr", music.getDuration());
    assertEquals("test", music.getFile());
    assertEquals("a", music.getAlbum());
    assertEquals(3, music.getDiskCount());
    assertEquals(2, music.getDiskNumber());
    assertEquals(0, music.getRating());
    assertEquals(new BigDecimal("23.00"), music.getPrice());
    Track v = new Track();
    v.setNumber(1);
    v.setLabel("label");
    v.setDuration("duration");
    v.setComment("comment");
    assertEquals(v, music.getTracks().getTracks().get(0));
  }

  @Test
  void getRangement() {
    assertEquals(armoire1x3x3, music.getRangement());
  }

  @Test
  void getPlace() {
    Place place = music.getPlace();
    assertEquals(armoire1x3x3, place.getRangement());
    assertEquals(1, place.getPlaceNum());
    assertEquals(2, place.getLine());
    assertEquals(3, place.getColumn());

    place = musicCaisse.getPlace();
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
  void getMusicFromXML() throws ParserConfigurationException, IOException, SAXException {
    final int id = 1987;
    final int externalId = 2000;
    final String name = "Aalto PS 04";
    final String year = "2004";
    final String type = "CD";
    final String album = "album";
    final String place = "Courlon 3a";
    final int numPlace = 0;
    final int line = 1;
    final int column = 2;
    final String price = "75";
    final String comment = "comment";
    final String artist = "2018-2030";
    final String composer = "89";
    final int trackNumber = 1;
    final String label = "Castilla y Leon";
    final String trackDuration = "Ribera del Duero";
    final String trackComnment = "Ribera del Duero";
    final String genre = "RED";
    final String duration = "duration";
    final String status = "MODIFIED";
    final String lastModified = "17-11-2020 12:08";
    final int diskNumber = 1;
    final int diskCount = 2;
    final int rating = 3;
    final String file = "file";
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<ListeMusic><Music>\n" +
        "        <id>" + id + "</id>\n" +
        "        <external_id>" + externalId + "</external_id>\n" +
        "        <title>" + name + "</title>\n" +
        "        <annee>" + year + "</annee>\n" +
        "        <type>" + type + "</type>\n" +
        "        <emplacement>" + place + "</emplacement>\n" +
        "        <numLieu>" + numPlace + "</numLieu>\n" +
        "        <ligne>" + line + "</ligne>\n" +
        "        <colonne>" + column + "</colonne>\n" +
        "        <prix>" + price + "</prix>\n" +
        "        <comment>" + comment + "</comment>\n" +
        "        <artist>" + artist + "</artist>\n" +
        "        <composer>" + composer + "</composer>\n" +
        "        <diskNumber>" + diskNumber + "</diskNumber>\n" +
        "        <diskCount>" + diskCount + "</diskCount>\n" +
        "        <rating>" + rating + "</rating>\n" +
        "        <file>" + file + "</file>\n" +
        "        <album>" + album + "</album>\n" +
        "        <tracks>\n" +
        "        <track>\n" +
        "            <number>" + trackNumber + "</number>\n" +
        "            <label>" + label + "</label>\n" +
        "            <duration>" + trackDuration + "</duration>\n" +
        "            <comment>" + trackComnment + "</comment>\n" +
        "        </track>\n" +
        "        </tracks>\n" +
        "        <genre>" + genre + "</genre>\n" +
        "        <duration>" + duration + "</duration>\n" +
        "        <status>" + status + "</status>\n" +
        "        <lastModified>" + lastModified + "</lastModified>\n" +
        "    </Music></ListeMusic>";
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    doc.getDocumentElement().normalize();
    NodeList nodeList = doc.getElementsByTagName("Music");
    final Music musicFromXML = Music.fromXml((Element) nodeList.item(0));
    assertEquals(id, musicFromXML.getId());
    assertEquals(externalId, musicFromXML.getExternalId());
    assertEquals(name, musicFromXML.getNom());
    assertEquals(year, musicFromXML.getAnnee());
    assertEquals(type, musicFromXML.getKind());
    assertEquals(place, musicFromXML.getEmplacement());
    assertEquals(numPlace, musicFromXML.getNumLieu());
    assertEquals(line, musicFromXML.getLigne());
    assertEquals(column, musicFromXML.getColonne());
    assertEquals(price, musicFromXML.getPrix());
    assertEquals(comment, musicFromXML.getComment());
    assertEquals(artist, musicFromXML.getArtist());
    assertEquals(composer, musicFromXML.getComposer());
    assertEquals(genre, musicFromXML.getGenre());
    assertEquals(status, musicFromXML.getStatus());
    assertEquals(diskCount, musicFromXML.getDiskCount());
    assertEquals(diskNumber, musicFromXML.getDiskNumber());
    assertEquals(rating, musicFromXML.getRating());
    assertEquals(file, musicFromXML.getFile());
    assertEquals(album, musicFromXML.getAlbum());
    assertEquals(lastModified, musicFromXML.getLastModified());
    assertEquals(trackNumber, (int) musicFromXML.getTracks().getTracks().get(0).getNumber());
    assertEquals(label, musicFromXML.getTracks().getTracks().get(0).getLabel());
    assertEquals(trackDuration, musicFromXML.getTracks().getTracks().get(0).getDuration());
    assertEquals(trackComnment, musicFromXML.getTracks().getTracks().get(0).getComment());
  }
}
