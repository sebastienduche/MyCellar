package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.ComplexPlaceBuilder;
import mycellar.placesmanagement.places.IAbstractPlace;
import mycellar.placesmanagement.places.SimplePlace;
import mycellar.placesmanagement.places.SimplePlaceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangementTest {

  private Rangement caisseNoLimit;
  private Rangement caisseLimit;
  private SimplePlace simplePlaceNoLimit;
  private SimplePlace simplePlaceLimit;
  private Rangement armoire1x3x3;
  private Rangement armoire1x3x3Builder;
  private Rangement armoire2x2_3x22545;
  private Rangement armoire2x2_3x22545Builder;
  private Rangement rangement;
  private ComplexPlace complexPlace1x3x3;
  private ComplexPlace complexPlace1x3x3Builder;
  private ComplexPlace complexPlace2x2_3x22545;
  private ComplexPlace complexPlace2x2_3x22545Builder;
  private ComplexPlace complexPlace2x2_3x22545Builder2;
  private ComplexPlace complexPlace;
  private SimplePlace simplePlace;

  @BeforeEach
  void setUp() {
    caisseNoLimit = new Rangement.SimplePlaceBuilder("caisseNoLimit").build();
    simplePlaceNoLimit = new SimplePlaceBuilder("simplePlaceNoLimit").build();
    // Caisse avec 2 emplacements commencant a 1 et limite a 6 bouteilles
    caisseLimit = new Rangement.SimplePlaceBuilder("caisseLimit").nbParts(2).startSimplePlace(1).limited(true).limit(6).build();
    simplePlaceLimit = new SimplePlaceBuilder("simplePlaceLimit").nbParts(2).startSimplePlace(1).limited(true).limit(6).build();
    Part partie = new Part(0);
    LinkedList<Part> list = new LinkedList<>();
    list.add(partie);
    partie.setRows(3);
    for (int i = 0; i < 3; i++) {
      partie.getRow(i).setCol(3);
    }
    armoire1x3x3 = new Rangement("armoire1x3x3", list);
    complexPlace1x3x3 = new ComplexPlace("armoire1x3x3", list);
    armoire1x3x3Builder = new Rangement.RangementBuilder("armoire1x3x3")
        .nbParts(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    complexPlace1x3x3Builder = new ComplexPlaceBuilder("armoire1x3x3")
        .nbParts(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    partie = new Part(0);
    list = new LinkedList<>();
    list.add(partie);
    partie.setRows(2);
    for (int i = 0; i < 2; i++) {
      partie.getRow(i).setCol(2);
    }
    partie = new Part(1);
    list.add(partie);
    partie.setRows(3);
    partie.getRow(0).setCol(5);
    partie.getRow(1).setCol(4);
    partie.getRow(2).setCol(5);
    armoire2x2_3x22545 = new Rangement("armoire2x2_3x22545", list);
    complexPlace2x2_3x22545 = new ComplexPlace("armoire2x2_3x22545", list);
    try {
      armoire2x2_3x22545Builder = new Rangement.RangementBuilder("armoire2x2_3x22545")
          .nbParts(new int[]{2, 3})
          .differentColumnsNumber()
          .columnsNumberForPart(0, new int[]{2, 2})
          .columnsNumberForPart(1, new int[]{5, 4, 5})
          .build();
    } catch (Exception ignored) {
    }
    try {
      complexPlace2x2_3x22545Builder = new ComplexPlaceBuilder("armoire2x2_3x22545")
          .nbParts(new int[]{2, 3})
          .differentColumnsNumber()
          .columnsNumberForPart(0, new int[]{2, 2})
          .columnsNumberForPart(1, new int[]{5, 4, 5})
          .build();
      complexPlace2x2_3x22545Builder2 = new ComplexPlaceBuilder("armoire2x2_3x22545")
    		  .withPartList(list).build();
    } catch (Exception ignored) {
    }
    rangement = new Rangement.SimplePlaceBuilder("test").build();
    complexPlace = new ComplexPlaceBuilder("test").build();
    simplePlace = new SimplePlaceBuilder("test").build();

    Program.addPlace(caisseLimit);
    Program.addPlace(caisseNoLimit);
    Program.addPlace(armoire1x3x3);
    Program.addPlace(armoire1x3x3Builder);
    Program.addPlace(armoire2x2_3x22545);
    Program.addPlace(armoire2x2_3x22545Builder);
    Program.addPlace(rangement);
    Program.addBasicPlace(simplePlaceNoLimit);
    Program.addBasicPlace(simplePlaceLimit);
    Program.addBasicPlace(simplePlace);
    Program.addBasicPlace(complexPlace1x3x3);
    Program.addBasicPlace(complexPlace1x3x3Builder);
    Program.addBasicPlace(complexPlace2x2_3x22545);
    Program.addBasicPlace(complexPlace2x2_3x22545Builder);
    Program.addBasicPlace(complexPlace2x2_3x22545Builder2);
    Program.addBasicPlace(complexPlace);
  }

  @Test
  void getName() {
    assertEquals("caisseNoLimit", caisseNoLimit.getName());
    assertEquals("simplePlaceNoLimit", simplePlaceNoLimit.getName());
  }

  @Test
  void setName() {
    rangement.setName(" toto ");
    assertEquals("toto", rangement.getName());
    simplePlaceNoLimit.setName(" toto ");
    assertEquals("toto", simplePlaceNoLimit.getName());
  }

  @Test
  void getStartCaisse() {
    assertEquals(1, caisseLimit.getStartSimplePlace());
    assertEquals(0, caisseNoLimit.getStartSimplePlace());
    assertEquals(1, simplePlaceLimit.getPartNumberIncrement());
    assertEquals(0, simplePlaceNoLimit.getPartNumberIncrement());
  }

  @Test
  void setStartCaisse() {
    rangement.setStartSimplePlace(2);
    assertEquals(2, rangement.getStartSimplePlace());
    simplePlace.setPartNumberIncrement(2);
    assertEquals(2, simplePlace.getPartNumberIncrement());
  }

  @Test
  void getPartCount() {
    assertEquals(1, armoire1x3x3.getPartCount());
    assertEquals(1, armoire1x3x3Builder.getPartCount());
    assertEquals(2, armoire2x2_3x22545.getPartCount());
    assertEquals(2, armoire2x2_3x22545Builder.getPartCount());
    assertEquals(2, caisseLimit.getPartCount());
    assertEquals(2, simplePlaceLimit.getPartCount());
    assertEquals(1, complexPlace1x3x3.getPartCount());
    assertEquals(1, complexPlace1x3x3Builder.getPartCount());
    assertEquals(2, complexPlace2x2_3x22545.getPartCount());
    assertEquals(2, complexPlace2x2_3x22545Builder.getPartCount());
    assertEquals(2, complexPlace2x2_3x22545Builder2.getPartCount());
  }

  @Test
  void getMexColumnNumber() {
    assertEquals(3, armoire1x3x3.getMaxColumnNumber());
    assertEquals(3, armoire1x3x3Builder.getMaxColumnNumber());
    assertEquals(5, armoire2x2_3x22545.getMaxColumnNumber());
    assertEquals(5, armoire2x2_3x22545Builder.getMaxColumnNumber());
    assertEquals(3, complexPlace1x3x3.getMaxColumnNumber());
    assertEquals(3, complexPlace1x3x3Builder.getMaxColumnNumber());
    assertEquals(5, complexPlace2x2_3x22545.getMaxColumnNumber());
    assertEquals(5, complexPlace2x2_3x22545Builder.getMaxColumnNumber());
    assertEquals(5, complexPlace2x2_3x22545Builder2.getMaxColumnNumber());
  }

  @Test
  void isLimited() {
    assertTrue(caisseLimit.isLimited());
    assertFalse(caisseNoLimit.isLimited());
    assertTrue(simplePlaceLimit.isLimited());
    assertFalse(simplePlaceNoLimit.isLimited());
  }

  @Test
  void setLimited() {
    assertFalse(rangement.isLimited());
    rangement.setLimited(true, 1);
    assertTrue(rangement.isLimited());
    assertFalse(simplePlace.isLimited());
    simplePlace.setLimited(true, 1);
    assertTrue(simplePlace.isLimited());
  }

  @Test
  void setMaxItemCount() {
    caisseLimit.setMaxItemCount(50);
    assertEquals(50, caisseLimit.getMaxColumnNumber());
    caisseNoLimit.setMaxItemCount(50);
    assertEquals(-1, caisseNoLimit.getMaxColumnNumber());
    simplePlaceLimit.setMaxItemCount(50);
    assertEquals(50, simplePlaceLimit.getMaxItemCount());
    simplePlaceNoLimit.setMaxItemCount(50);
    assertEquals(-1, simplePlaceNoLimit.getMaxItemCount());
  }

  @Test
  void getNbLignesInt() {
    assertEquals(3, armoire1x3x3.getLineCountAt(0));
    assertEquals(3, armoire1x3x3Builder.getLineCountAt(0));
    assertEquals(2, armoire2x2_3x22545.getLineCountAt(0));
    assertEquals(2, armoire2x2_3x22545Builder.getLineCountAt(0));
    assertEquals(3, armoire2x2_3x22545.getLineCountAt(1));
    assertEquals(3, armoire2x2_3x22545Builder.getLineCountAt(1));
    assertEquals(3, complexPlace1x3x3.getLineCountAt(0));
    assertEquals(3, complexPlace1x3x3Builder.getLineCountAt(0));
    assertEquals(2, complexPlace2x2_3x22545.getLineCountAt(0));
    assertEquals(2, complexPlace2x2_3x22545Builder.getLineCountAt(0));
    assertEquals(2, complexPlace2x2_3x22545Builder2.getLineCountAt(0));
    assertEquals(3, complexPlace2x2_3x22545.getLineCountAt(1));
    assertEquals(3, complexPlace2x2_3x22545Builder.getLineCountAt(1));
    assertEquals(3, complexPlace2x2_3x22545Builder2.getLineCountAt(1));
  }

  @Test
  void isExistingCell() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertTrue(armoire1x3x3.isExistingCell(0, i, j));
        assertTrue(armoire1x3x3Builder.isExistingCell(0, i, j));
        assertTrue(complexPlace1x3x3.isExistingCell(0, i, j));
        assertTrue(complexPlace1x3x3Builder.isExistingCell(0, i, j));
      }
      assertFalse(armoire1x3x3.isExistingCell(0, i, 3));
      assertFalse(armoire1x3x3Builder.isExistingCell(0, i, 3));
      assertFalse(complexPlace1x3x3.isExistingCell(0, i, 3));
      assertFalse(complexPlace1x3x3Builder.isExistingCell(0, i, 3));
    }
    assertFalse(armoire1x3x3.isExistingCell(0, 3, 3));
    assertFalse(armoire1x3x3Builder.isExistingCell(0, 3, 3));
    assertFalse(armoire1x3x3.isExistingCell(0, 3, 0));
    assertFalse(armoire1x3x3Builder.isExistingCell(0, 3, 0));
    assertFalse(complexPlace1x3x3.isExistingCell(0, 3, 3));
    assertFalse(complexPlace1x3x3Builder.isExistingCell(0, 3, 3));
    assertFalse(complexPlace1x3x3.isExistingCell(0, 3, 0));
    assertFalse(complexPlace1x3x3Builder.isExistingCell(0, 3, 0));

    LinkedList<Rangement> list = new LinkedList<>();
    LinkedList<ComplexPlace> listPlace = new LinkedList<>();
    list.add(armoire1x3x3);
    list.add(armoire1x3x3Builder);
    list.add(armoire2x2_3x22545);
    list.add(armoire2x2_3x22545Builder);
    listPlace.add(complexPlace1x3x3);
    listPlace.add(complexPlace1x3x3Builder);
    listPlace.add(complexPlace2x2_3x22545);
    listPlace.add(complexPlace2x2_3x22545Builder);
    listPlace.add(complexPlace2x2_3x22545Builder2);
    for (Rangement r : list) {
      int emplacementMax = r.getPartCount();
      for (int i = 0; i < emplacementMax; i++) {
        int ligneMax = r.getLineCountAt(i);
        for (int j = 0; j < ligneMax; j++) {
          int colMax = r.getColumnCountAt(i, j);
          for (int k = 0; k < colMax; k++) {
            assertTrue(r.isExistingCell(i, j, k));
          }
          assertFalse(r.isExistingCell(i, j, colMax));
        }
        assertFalse(r.isExistingCell(i, ligneMax, 0));
      }
      assertFalse(r.isExistingCell(emplacementMax, 0, 0));
    }
    for (ComplexPlace r : listPlace) {
      int emplacementMax = r.getPartCount();
      for (int i = 0; i < emplacementMax; i++) {
        int ligneMax = r.getLineCountAt(i);
        for (int j = 0; j < ligneMax; j++) {
          int colMax = r.getColumnCountAt(i, j);
          for (int k = 0; k < colMax; k++) {
            assertTrue(r.isExistingCell(i, j, k));
          }
          assertFalse(r.isExistingCell(i, j, colMax));
        }
        assertFalse(r.isExistingCell(i, ligneMax, 0));
      }
      assertFalse(r.isExistingCell(emplacementMax, 0, 0));
    }
  }

  @Test
  void getColumnCountAt() {
    assertEquals(3, armoire1x3x3.getColumnCountAt(0, 0));
    assertEquals(3, armoire1x3x3Builder.getColumnCountAt(0, 0));
    assertEquals(3, armoire1x3x3.getColumnCountAt(0, 1));
    assertEquals(3, armoire1x3x3Builder.getColumnCountAt(0, 1));
    assertEquals(3, armoire1x3x3.getColumnCountAt(0, 2));
    assertEquals(3, armoire1x3x3Builder.getColumnCountAt(0, 2));
    assertEquals(2, armoire2x2_3x22545.getColumnCountAt(0, 0));
    assertEquals(2, armoire2x2_3x22545Builder.getColumnCountAt(0, 0));
    assertEquals(2, armoire2x2_3x22545.getColumnCountAt(0, 1));
    assertEquals(2, armoire2x2_3x22545Builder.getColumnCountAt(0, 1));
    assertEquals(5, armoire2x2_3x22545.getColumnCountAt(1, 0));
    assertEquals(5, armoire2x2_3x22545Builder.getColumnCountAt(1, 0));
    assertEquals(4, armoire2x2_3x22545.getColumnCountAt(1, 1));
    assertEquals(4, armoire2x2_3x22545Builder.getColumnCountAt(1, 1));
    assertEquals(5, armoire2x2_3x22545.getColumnCountAt(1, 2));
    assertEquals(5, armoire2x2_3x22545Builder.getColumnCountAt(1, 2));
    assertEquals(3, complexPlace1x3x3.getColumnCountAt(0, 0));
    assertEquals(3, complexPlace1x3x3Builder.getColumnCountAt(0, 0));
    assertEquals(3, complexPlace1x3x3.getColumnCountAt(0, 1));
    assertEquals(3, complexPlace1x3x3Builder.getColumnCountAt(0, 1));
    assertEquals(3, complexPlace1x3x3.getColumnCountAt(0, 2));
    assertEquals(3, complexPlace1x3x3Builder.getColumnCountAt(0, 2));
    assertEquals(2, complexPlace2x2_3x22545.getColumnCountAt(0, 0));
    assertEquals(2, complexPlace2x2_3x22545Builder.getColumnCountAt(0, 0));
    assertEquals(2, complexPlace2x2_3x22545Builder2.getColumnCountAt(0, 0));
    assertEquals(2, complexPlace2x2_3x22545.getColumnCountAt(0, 1));
    assertEquals(2, complexPlace2x2_3x22545Builder.getColumnCountAt(0, 1));
    assertEquals(2, complexPlace2x2_3x22545Builder2.getColumnCountAt(0, 1));
    assertEquals(5, complexPlace2x2_3x22545.getColumnCountAt(1, 0));
    assertEquals(5, complexPlace2x2_3x22545Builder.getColumnCountAt(1, 0));
    assertEquals(5, complexPlace2x2_3x22545Builder2.getColumnCountAt(1, 0));
    assertEquals(4, complexPlace2x2_3x22545.getColumnCountAt(1, 1));
    assertEquals(4, complexPlace2x2_3x22545Builder.getColumnCountAt(1, 1));
    assertEquals(4, complexPlace2x2_3x22545Builder2.getColumnCountAt(1, 1));
    assertEquals(5, complexPlace2x2_3x22545.getColumnCountAt(1, 2));
    assertEquals(5, complexPlace2x2_3x22545Builder.getColumnCountAt(1, 2));
    assertEquals(5, complexPlace2x2_3x22545Builder2.getColumnCountAt(1, 2));
  }

  @Test
  void getMaxColumnCountAt() {
    assertEquals(3, armoire1x3x3.getMaxColumCountAt(0));
    assertEquals(3, armoire1x3x3Builder.getMaxColumCountAt(0));
    assertEquals(2, armoire2x2_3x22545.getMaxColumCountAt(0));
    assertEquals(2, armoire2x2_3x22545Builder.getMaxColumCountAt(0));
    assertEquals(5, armoire2x2_3x22545.getMaxColumCountAt(1));
    assertEquals(5, armoire2x2_3x22545Builder.getMaxColumCountAt(1));
    assertEquals(3, complexPlace1x3x3.getMaxColumCountAt(0));
    assertEquals(3, complexPlace1x3x3Builder.getMaxColumCountAt(0));
    assertEquals(2, complexPlace2x2_3x22545.getMaxColumCountAt(0));
    assertEquals(2, complexPlace2x2_3x22545Builder.getMaxColumCountAt(0));
    assertEquals(2, complexPlace2x2_3x22545Builder2.getMaxColumCountAt(0));
    assertEquals(5, complexPlace2x2_3x22545.getMaxColumCountAt(1));
    assertEquals(5, complexPlace2x2_3x22545Builder.getMaxColumCountAt(1));
    assertEquals(5, complexPlace2x2_3x22545Builder2.getMaxColumCountAt(1));
  }

  @Test
  void getMaxColumnCount() {
    assertEquals(3, armoire1x3x3.getMaxColumCount());
    assertEquals(3, armoire1x3x3Builder.getMaxColumCount());
    assertEquals(5, armoire2x2_3x22545.getMaxColumCount());
    assertEquals(5, armoire2x2_3x22545Builder.getMaxColumCount());
    assertEquals(3, complexPlace1x3x3.getMaxColumCount());
    assertEquals(3, complexPlace1x3x3Builder.getMaxColumCount());
    assertEquals(5, complexPlace2x2_3x22545.getMaxColumCount());
    assertEquals(5, complexPlace2x2_3x22545Builder.getMaxColumCount());
    assertEquals(5, complexPlace2x2_3x22545Builder2.getMaxColumCount());
  }

  @Test
  void getNbCaseUseLigne() {
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire1x3x3Builder.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire2x2_3x22545Builder.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace1x3x3Builder.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace2x2_3x22545.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace2x2_3x22545Builder.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace2x2_3x22545Builder2.getNbCaseUseInLine(0, 0));
    Bouteille b = new Bouteille();
    b.setNom("B1");
    updateToArmoire1x3x3(b, 1, 1);
    updateToComplexPlace1x3x3(b, 1, 1);
    assertEquals(1, armoire1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(1, complexPlace1x3x3.getNbCaseUseInLine(0, 0));
    Bouteille b1 = new Bouteille();
    b1.setNom("B2");
    updateToArmoire1x3x3(b1, 2, 1);
    updateToComplexPlace1x3x3(b1, 2, 1);
    assertEquals(1, armoire1x3x3.getNbCaseUseInLine(0, 1));
    assertEquals(1, complexPlace1x3x3.getNbCaseUseInLine(0, 1));
    Bouteille b2 = new Bouteille();
    b2.setNom("B3");
    //b2.setNumLieu(1);
    //b2.setLigne(3);
    //b2.setColonne(3);
    //b2.setEmplacement("armoire1x3x3");
    //armoire1x3x3.updateToStock(b2);
    updateToArmoire1x3x3(b2, 3, 3);
    updateToComplexPlace1x3x3(b2, 3, 3);
    Bouteille b3 = new Bouteille();
    b3.setNom("B4");
    //b3.setNumLieu(1);
    //b3.setLigne(3);
    //b3.setColonne(2);
    //b3.setEmplacement("armoire1x3x3");
    //armoire1x3x3.updateToStock(b3);
    updateToArmoire1x3x3(b3, 3, 2);
    updateToComplexPlace1x3x3(b3, 3, 2);
    assertEquals(2, armoire1x3x3.getNbCaseUseInLine(0, 2));
    assertEquals(2, complexPlace1x3x3.getNbCaseUseInLine(0, 2));
    armoire1x3x3.clearStock(b);
    armoire1x3x3.clearStock(b1);
    armoire1x3x3.clearStock(b2);
    armoire1x3x3.clearStock(b3);
    complexPlace1x3x3.clearStorage(b);
    complexPlace1x3x3.clearStorage(b1);
    complexPlace1x3x3.clearStorage(b2);
    complexPlace1x3x3.clearStorage(b3);
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 1));
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 2));
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 1));
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 2));
    b = new Bouteille();
    b.setNom("B5");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseInLine(1, 0));
    assertEquals(1, complexPlace2x2_3x22545.getNbCaseUseInLine(1, 0));
    armoire2x2_3x22545.clearStock(b);
    complexPlace2x2_3x22545.clearStorage(b);
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseInLine(1, 0));
    assertEquals(0, complexPlace2x2_3x22545.getNbCaseUseInLine(1, 0));
  }

  private void updateToArmoire(Bouteille b, int numLieu, int ligne, int colonne, String armoire, IAbstractPlace place) {
    b.setNumLieu(numLieu);
    b.setLigne(ligne);
    b.setColonne(colonne);
    b.setEmplacement(armoire);
    place.addObject(b);
  }

  private void updateToArmoire1x3x3(Bouteille b, int ligne, int colonne) {
    b.setNumLieu(1);
    b.setLigne(ligne);
    b.setColonne(colonne);
    b.setEmplacement("armoire1x3x3");
    armoire1x3x3.updateToStock(b);
  }

  private void updateToComplexPlace1x3x3(Bouteille b, int ligne, int colonne) {
    b.setNumLieu(1);
    b.setLigne(ligne);
    b.setColonne(colonne);
    b.setEmplacement("armoire1x3x3");
    complexPlace1x3x3.updateToStock(b);
  }

  @Test
  void getNbCaseFreeCoteLigne() {
    LinkedList<Rangement> list = new LinkedList<>();
    list.add(armoire1x3x3);
    list.add(armoire1x3x3Builder);
    list.add(armoire2x2_3x22545);
    list.add(armoire2x2_3x22545Builder);
    for (Rangement r : list) {
      int emplacementMax = r.getPartCount();
      for (int i = 0; i < emplacementMax; i++) {
        int ligneMax = r.getLineCountAt(i);
        for (int j = 0; j < ligneMax; j++) {
          int colMax = r.getColumnCountAt(i, j);
          for (int k = 0; k < colMax; k++) {
            assertEquals(colMax - k, r.getCountFreeCellFrom(i, j, k));
          }
        }
      }
    }
    LinkedList<ComplexPlace> listPlace = new LinkedList<>();
    listPlace.add(complexPlace1x3x3);
    listPlace.add(complexPlace1x3x3Builder);
    listPlace.add(complexPlace2x2_3x22545);
    listPlace.add(complexPlace2x2_3x22545Builder);
    listPlace.add(complexPlace2x2_3x22545Builder2);
    for (ComplexPlace r : listPlace) {
      int emplacementMax = r.getPartCount();
      for (int i = 0; i < emplacementMax; i++) {
        int ligneMax = r.getLineCountAt(i);
        for (int j = 0; j < ligneMax; j++) {
          int colMax = r.getColumnCountAt(i, j);
          for (int k = 0; k < colMax; k++) {
            assertEquals(colMax - k, r.getCountFreeCellFrom(i, j, k));
          }
        }
      }
    }
    Bouteille b = new Bouteille();
    b.setNom("B27");
    updateToArmoire1x3x3(b, 1, 1);
    updateToComplexPlace1x3x3(b, 1, 1);
    assertEquals(0, armoire1x3x3.getCountFreeCellFrom(0, 0, 0));
    assertEquals(2, armoire1x3x3.getCountFreeCellFrom(0, 0, 1));
    assertEquals(0, complexPlace1x3x3.getCountFreeCellFrom(0, 0, 0));
    assertEquals(2, complexPlace1x3x3.getCountFreeCellFrom(0, 0, 1));
    Bouteille b1 = new Bouteille();
    b1.setNom("B28");
    updateToArmoire1x3x3(b1, 1, 3);
    updateToComplexPlace1x3x3(b1, 1, 3);
    assertEquals(1, armoire1x3x3.getCountFreeCellFrom(0, 0, 1));
    assertEquals(1, complexPlace1x3x3.getCountFreeCellFrom(0, 0, 1));
    armoire1x3x3.clearStock(b);
    armoire1x3x3.clearStock(b1);
    complexPlace1x3x3.clearStorage(b);
    complexPlace1x3x3.clearStorage(b1);
    b = new Bouteille();
    b.setEmplacement(armoire2x2_3x22545.getName());
    b.setNom("B6");
    b.setNumLieu(2);
    b.setLigne(1);
    b.setColonne(2);
    armoire2x2_3x22545.addObject(b);
    b = new Bouteille();
    b.setEmplacement(complexPlace2x2_3x22545.getName());
    b.setNom("B6");
    b.setNumLieu(2);
    b.setLigne(1);
    b.setColonne(2);
    complexPlace2x2_3x22545.addObject(b);
    assertEquals(1, armoire2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
    assertEquals(3, armoire2x2_3x22545.getCountFreeCellFrom(1, 0, 2));
    assertEquals(1, complexPlace2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
    assertEquals(3, complexPlace2x2_3x22545.getCountFreeCellFrom(1, 0, 2));
    armoire2x2_3x22545.clearStock(b);
    complexPlace2x2_3x22545.clearStorage(b);
    assertEquals(5, armoire2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
    assertEquals(5, complexPlace2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
  }

  @Test
  void getTotalCellUsed() {
    assertEquals(0, armoire1x3x3.getTotalCellUsed(0));
    assertEquals(0, armoire1x3x3Builder.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545Builder.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(1));
    assertEquals(0, armoire2x2_3x22545Builder.getTotalCellUsed(1));
    assertEquals(0, complexPlace1x3x3.getTotalCellUsed(0));
    assertEquals(0, complexPlace1x3x3Builder.getTotalCellUsed(0));
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, complexPlace2x2_3x22545Builder.getTotalCellUsed(0));
    assertEquals(0, complexPlace2x2_3x22545Builder2.getTotalCellUsed(0));
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(1));
    assertEquals(0, complexPlace2x2_3x22545Builder.getTotalCellUsed(1));
    assertEquals(0, complexPlace2x2_3x22545Builder2.getTotalCellUsed(1));
    Bouteille b = new Bouteille();
    b.setNom("B7");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
    assertEquals(1, armoire2x2_3x22545.getTotalCellUsed(1));
    armoire2x2_3x22545.clearStock(b);
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(0));
    assertEquals(1, complexPlace2x2_3x22545.getTotalCellUsed(1));
    complexPlace2x2_3x22545.clearStorage(b);
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(0));
  }

  @Test
  void getTotalCountCellUsed() throws MyCellarException {
    assertEquals(0, armoire1x3x3.getTotalCountCellUsed());
    assertEquals(0, armoire1x3x3Builder.getTotalCountCellUsed());
    assertEquals(0, armoire2x2_3x22545.getTotalCountCellUsed());
    assertEquals(0, armoire2x2_3x22545Builder.getTotalCountCellUsed());
    assertEquals(0, complexPlace1x3x3.getTotalCountCellUsed());
    assertEquals(0, complexPlace1x3x3Builder.getTotalCountCellUsed());
    assertEquals(0, complexPlace2x2_3x22545.getTotalCountCellUsed());
    assertEquals(0, complexPlace2x2_3x22545Builder.getTotalCountCellUsed());
    assertEquals(0, complexPlace2x2_3x22545Builder2.getTotalCountCellUsed());
    Bouteille b = new Bouteille();
    b.setNom("B8");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getTotalCountCellUsed());
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(1, complexPlace2x2_3x22545.getTotalCountCellUsed());
    Bouteille b1 = new Bouteille();
    b.setNom("B9");
    updateToArmoire(b1, 1, 2, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(2, armoire2x2_3x22545.getTotalCountCellUsed());
    armoire2x2_3x22545.removeObject(b);
    updateToArmoire(b1, 1, 2, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(2, complexPlace2x2_3x22545.getTotalCountCellUsed());
    complexPlace2x2_3x22545.removeObject(b);
    assertEquals(1, armoire2x2_3x22545.getTotalCountCellUsed());
    armoire2x2_3x22545.removeObject(b1);
    assertEquals(1, complexPlace2x2_3x22545.getTotalCountCellUsed());
    complexPlace2x2_3x22545.removeObject(b1);
    assertEquals(0, armoire2x2_3x22545.getTotalCountCellUsed());
    assertEquals(0, complexPlace2x2_3x22545.getTotalCountCellUsed());
    assertEquals(0, caisseLimit.getTotalCountCellUsed());
    assertEquals(0, caisseNoLimit.getTotalCountCellUsed());
    Bouteille b2 = new Bouteille();
    b2.setNom("B29");
    b2.setNumLieu(2);
    b2.setEmplacement("caisseLimit");
    caisseLimit.addObject(b2);
    assertEquals(1, caisseLimit.getTotalCountCellUsed());
    Bouteille b21 = new Bouteille();
    b21.setNom("B291");
    b21.setNumLieu(2);
    b21.setEmplacement("simplePlaceLimit");
    simplePlaceLimit.addObject(b21);
    assertEquals(1, simplePlaceLimit.getTotalCellUsed());
    Bouteille b3 = new Bouteille();
    b3.setNom("B30");
    b3.setNumLieu(0);
    b3.setEmplacement("caisseNoLimit");
    caisseNoLimit.addObject(b3);
    assertEquals(1, caisseNoLimit.getTotalCountCellUsed());
    Bouteille b31 = new Bouteille();
    b31.setNom("B301");
    b31.setNumLieu(0);
    b31.setEmplacement("simplePlaceNoLimit");
    simplePlaceNoLimit.addObject(b31);
    assertEquals(1, simplePlaceNoLimit.getTotalCellUsed());
    caisseLimit.removeObject(b2);
    assertEquals(0, caisseLimit.getTotalCountCellUsed());
    caisseNoLimit.removeObject(b3);
    assertEquals(0, caisseNoLimit.getTotalCountCellUsed());
    simplePlaceLimit.removeObject(b21);
    assertEquals(0, simplePlaceLimit.getTotalCellUsed());
    simplePlaceNoLimit.removeObject(b31);
    assertEquals(0, simplePlaceNoLimit.getTotalCellUsed());
  }

  @Test
  void getTotalCellUsedSimplePlace() throws MyCellarException {
    assertEquals(0, caisseLimit.getTotalCellUsed(1));
    assertEquals(0, caisseNoLimit.getTotalCellUsed(0));
    Bouteille b = new Bouteille();
    b.setNom("B10");
    b.setNumLieu(2);
    b.setEmplacement("caisseLimit");
    caisseLimit.addObject(b);
    assertEquals(1, caisseLimit.getTotalCellUsed(1));
    assertEquals(1, caisseLimit.getTotalCellUsed(b.getPlace().getPlaceNumIndex()));
    assertEquals(1, caisseLimit.getTotalCellUsed(b.getPlace()));
    Bouteille b1 = new Bouteille();
    b1.setNom("B11");
    b1.setNumLieu(0);
    b1.setEmplacement("caisseNoLimit");
    caisseNoLimit.addObject(b1);
    assertEquals(1, caisseNoLimit.getTotalCellUsed(0));
    assertEquals(1, caisseNoLimit.getTotalCellUsed(b1.getPlace().getPlaceNumIndex()));
    assertEquals(1, caisseNoLimit.getTotalCellUsed(b1.getPlace()));
    caisseLimit.removeObject(b);
    assertEquals(0, caisseLimit.getTotalCellUsed(1));
    assertEquals(0, caisseLimit.getTotalCellUsed(b.getPlace().getPlaceNumIndex()));
    assertEquals(0, caisseLimit.getTotalCellUsed(b.getPlace()));
    caisseNoLimit.removeObject(b1);
    assertEquals(0, caisseNoLimit.getTotalCellUsed(0));
    assertEquals(0, caisseNoLimit.getTotalCellUsed(b1.getPlace().getPlaceNumIndex()));
    assertEquals(0, caisseNoLimit.getTotalCellUsed(b1.getPlace()));

    assertEquals(0, simplePlaceLimit.getCountCellUsed(1));
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(0));
    Bouteille b01 = new Bouteille();
    b01.setNom("B010");
    b01.setNumLieu(2);
    b01.setEmplacement("simplePlaceLimit");
    simplePlaceLimit.addObject(b01);
    assertEquals(1, simplePlaceLimit.getCountCellUsed(1));
    assertEquals(1, simplePlaceLimit.getCountCellUsed(b01.getPlace().getPlaceNumIndex()));
    assertEquals(1, simplePlaceLimit.getCountCellUsed(b.getPlace()));
    Bouteille b11 = new Bouteille();
    b11.setNom("B11");
    b11.setNumLieu(0);
    b11.setEmplacement("simplePlaceNoLimit");
    simplePlaceNoLimit.addObject(b11);
    assertEquals(1, simplePlaceNoLimit.getCountCellUsed(0));
    assertEquals(1, simplePlaceNoLimit.getCountCellUsed(b11.getPlace().getPlaceNumIndex()));
    assertEquals(1, simplePlaceNoLimit.getCountCellUsed(b11.getPlace()));
    simplePlaceLimit.removeObject(b01);
    assertEquals(0, simplePlaceLimit.getCountCellUsed(1));
    assertEquals(0, simplePlaceLimit.getCountCellUsed(b01.getPlace().getPlaceNumIndex()));
    assertEquals(0, simplePlaceLimit.getCountCellUsed(b01.getPlace()));
    simplePlaceNoLimit.removeObject(b11);
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(0));
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(b11.getPlace().getPlaceNumIndex()));
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(b11.getPlace()));
  }

  @Test
  void addWine() throws MyCellarException {
    Bouteille b = addAndRemoveBottle();
    b.setEmplacement("caisseLimit");
    caisseLimit.addObject(b);
    assertEquals(1, caisseLimit.getTotalCellUsed(1));
    caisseLimit.removeObject(b);
    assertEquals(0, caisseLimit.getTotalCellUsed(1));
    b.setEmplacement("simplePlaceLimit");
    simplePlaceLimit.addObject(b);
    assertEquals(1, simplePlaceLimit.getCountCellUsed(1));
    simplePlaceLimit.removeObject(b);
    assertEquals(0, simplePlaceLimit.getCountCellUsed(1));
  }

  private Bouteille addAndRemoveBottle() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B12");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseInLine(1, 0));
    assertEquals(b, armoire2x2_3x22545.getObject(1, 0, 1).get());
    assertEquals(b, armoire2x2_3x22545.getObject(b.getPlace()).get());
    armoire2x2_3x22545.removeObject(b);
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(1, complexPlace2x2_3x22545.getNbCaseUseInLine(1, 0));
    assertEquals(b, complexPlace2x2_3x22545.getObject(1, 0, 1).get());
    assertEquals(b, complexPlace2x2_3x22545.getObject(b.getPlace()).get());
    complexPlace2x2_3x22545.removeObject(b);
    return b;
  }

  @Test
  void removeWine() throws MyCellarException {
    Bouteille b = addAndRemoveBottle();
    caisseLimit.addObject(b);
    assertEquals(1, caisseLimit.getTotalCellUsed(1));
    caisseLimit.removeObject(b);
    assertEquals(0, caisseLimit.getTotalCellUsed(1));
  }

  @Test
  void updateToStock() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B13");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getObject(0, 0, 1).get());
    assertEquals(b, armoire1x3x3.getObject(b.getPlace()).get());
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(0, 0, 1).get());
    assertEquals(b, complexPlace1x3x3.getObject(b.getPlace()).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B14");
    updateToArmoire1x3x3(b1, 1, 2);
    updateToComplexPlace1x3x3(b1, 1, 2);
    assertEquals(b1, armoire1x3x3.getObject(0, 0, 1).get());
    assertEquals(b1, armoire1x3x3.getObject(b1.getPlace()).get());
    armoire1x3x3.clearStock(b1);
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    armoire1x3x3.removeObject(b);
    assertEquals(b1, complexPlace1x3x3.getObject(0, 0, 1).get());
    assertEquals(b1, complexPlace1x3x3.getObject(b1.getPlace()).get());
    complexPlace1x3x3.clearStorage(b1);
    assertTrue(complexPlace1x3x3.getObject(0, 0, 1).isEmpty());
    complexPlace1x3x3.removeObject(b);
  }

  @Test
  void moveLineWine() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B15");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getObject(0, 0, 1).get());
    armoire1x3x3.moveToLine(b, 2);
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    assertEquals(b, armoire1x3x3.getObject(0, 1, 1).get());
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(0, 0, 1).get());
    complexPlace1x3x3.moveToLine(b, 2);
    assertTrue(complexPlace1x3x3.getObject(0, 0, 1).isEmpty());
    assertEquals(b, complexPlace1x3x3.getObject(0, 1, 1).get());
  }

  @Test
  void getObject() {
    Bouteille b = new Bouteille();
    b.setNom("B16");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getObject(0, 0, 1).get());
    assertEquals(b, armoire1x3x3.getObject(b.getPlace()).get());
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(0, 0, 1).get());
    assertEquals(b, complexPlace1x3x3.getObject(b.getPlace()).get());
    Bouteille b1 = new Bouteille();
    b.setNom("B17");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(b1, armoire2x2_3x22545.getObject(1, 1, 2).get());
    assertEquals(b1, armoire2x2_3x22545.getObject(b1.getPlace()).get());
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(b1, complexPlace2x2_3x22545.getObject(1, 1, 2).get());
    assertEquals(b1, complexPlace2x2_3x22545.getObject(b1.getPlace()).get());
    armoire1x3x3.clearStock(b);
    armoire2x2_3x22545.clearStock(b1);
    complexPlace1x3x3.clearStorage(b);
    complexPlace2x2_3x22545.clearStorage(b1);
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    assertTrue(armoire1x3x3.getObject(b.getPlace()).isEmpty());
    assertTrue(armoire2x2_3x22545.getObject(b1.getPlace()).isEmpty());
    assertTrue(complexPlace1x3x3.getObject(0, 0, 1).isEmpty());
    assertTrue(complexPlace1x3x3.getObject(b.getPlace()).isEmpty());
    assertTrue(complexPlace2x2_3x22545.getObject(b1.getPlace()).isEmpty());
  }

  @Test
  void clearStock() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B18");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getObject(0, 0, 1).get());
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(0, 0, 1).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B19");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(b1, armoire2x2_3x22545.getObject(1, 1, 2).get());
    armoire1x3x3.clearStock(b);
    armoire2x2_3x22545.clearStock(b1);
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(b1, complexPlace2x2_3x22545.getObject(1, 1, 2).get());
    complexPlace1x3x3.clearStorage(b);
    complexPlace2x2_3x22545.clearStorage(b1);
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    assertTrue(armoire2x2_3x22545.getObject(1, 1, 2).isEmpty());
    assertTrue(complexPlace1x3x3.getObject(0, 0, 1).isEmpty());
    assertTrue(complexPlace2x2_3x22545.getObject(1, 1, 2).isEmpty());
    armoire2x2_3x22545.removeObject(b1);
    armoire1x3x3.removeObject(b);
    complexPlace2x2_3x22545.removeObject(b1);
    complexPlace1x3x3.removeObject(b);
  }

  @Test
  void isSameColumnNumber() {
    assertTrue(armoire1x3x3.isSameColumnNumber());
    assertTrue(armoire1x3x3Builder.isSameColumnNumber());
    assertFalse(armoire2x2_3x22545.isSameColumnNumber());
    assertFalse(armoire2x2_3x22545Builder.isSameColumnNumber());
    assertTrue(complexPlace1x3x3.isSameColumnNumber());
    assertTrue(complexPlace1x3x3Builder.isSameColumnNumber());
    assertFalse(complexPlace2x2_3x22545.isSameColumnNumber());
    assertFalse(complexPlace2x2_3x22545Builder.isSameColumnNumber());
    assertFalse(complexPlace2x2_3x22545Builder2.isSameColumnNumber());
  }

  @Test
  void canAddBottle() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse20").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    Bouteille b = new Bouteille();
    b.setNom("B20");
    b.setNumLieu(1);
    caisse.addObject(b);
    assertFalse(caisse.canAddObjectAt(0, 0, 0));
    assertTrue(caisse.canAddObjectAt(1, 0, 0));
    assertFalse(caisse.canAddObjectAt(b));
    b.setNumLieu(0);
    assertFalse(caisse.canAddObjectAt(b));
    b.setNumLieu(2);
    assertTrue(caisse.canAddObjectAt(b));
    LinkedList<Rangement> list = new LinkedList<>();
    list.add(armoire1x3x3);
    list.add(armoire1x3x3Builder);
    list.add(armoire2x2_3x22545);
    list.add(armoire2x2_3x22545Builder);
    LinkedList<ComplexPlace> listPlace = new LinkedList<>();
    listPlace.add(complexPlace1x3x3);
    listPlace.add(complexPlace1x3x3Builder);
    listPlace.add(complexPlace2x2_3x22545);
    listPlace.add(complexPlace2x2_3x22545Builder);
    listPlace.add(complexPlace2x2_3x22545Builder2);
    for (Rangement r : list) {
      int emplacementMax = r.getPartCount();
      for (int i = 0; i < emplacementMax; i++) {
        int ligneMax = r.getLineCountAt(i);
        for (int j = 0; j < ligneMax; j++) {
          int colMax = r.getColumnCountAt(i, j);
          for (int k = 0; k < colMax; k++) {
            assertTrue(r.canAddObjectAt(i, j, k));
          }
          assertFalse(r.canAddObjectAt(i, j, colMax));
        }
        assertFalse(r.canAddObjectAt(i, ligneMax, 0));
      }
      assertFalse(r.canAddObjectAt(emplacementMax, 0, 0));
    }
    for (ComplexPlace r : listPlace) {
      int emplacementMax = r.getPartCount();
      for (int i = 0; i < emplacementMax; i++) {
        int ligneMax = r.getLineCountAt(i);
        for (int j = 0; j < ligneMax; j++) {
          int colMax = r.getColumnCountAt(i, j);
          for (int k = 0; k < colMax; k++) {
            assertTrue(r.canAddObjectAt(i, j, k));
          }
          assertFalse(r.canAddObjectAt(i, j, colMax));
        }
        assertFalse(r.canAddObjectAt(i, ligneMax, 0));
      }
      assertFalse(r.canAddObjectAt(emplacementMax, 0, 0));
    }
    b = new Bouteille();
    b.setNom("B20bis");
    assertFalse(armoire1x3x3.canAddObjectAt(b));
    assertFalse(armoire1x3x3Builder.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b));
    b.setNumLieu(1);
    assertFalse(armoire1x3x3.canAddObjectAt(b));
    assertFalse(armoire1x3x3Builder.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b));
    b.setLigne(1);
    assertFalse(armoire1x3x3.canAddObjectAt(b));
    assertFalse(armoire1x3x3Builder.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b));
    b.setColonne(1);
    assertTrue(armoire1x3x3.canAddObjectAt(b));
    assertTrue(armoire1x3x3Builder.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3.canAddObjectAt(b));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b));
  }

  @Test
  void putTabStock() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(2).build();
    Program.addPlace(caisse);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(2).build();
    Program.addBasicPlace(simplePlace);
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(1).build()));

    Bouteille b = new Bouteille();
    b.setNom("B20");
    b.setEmplacement("caisse");
    b.setNumLieu(1);
    caisse.addObject(b);
    simplePlace.addObject(b);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(1).build()));
    assertEquals(1, caisse.getTotalCellUsed(0));
    assertTrue(caisse.canAddObjectAt(0, 0, 0));
    assertTrue(caisse.canAddObjectAt(1, 0, 0));
    assertTrue(caisse.canAddObjectAt(b));
    assertTrue(caisse.hasFreeSpaceInSimplePlace(b.getPlace()));
    assertEquals(1, simplePlace.getCountCellUsed(0));
    assertTrue(simplePlace.canAddObjectAt(0, 0, 0));
    assertTrue(simplePlace.canAddObjectAt(1, 0, 0));
    assertTrue(simplePlace.canAddObjectAt(b));
    assertTrue(simplePlace.hasFreeSpace(b.getPlace()));
    b.setNumLieu(0);
    assertFalse(caisse.canAddObjectAt(b));
    assertFalse(simplePlace.canAddObjectAt(b));
  }

  @Test
  void hasFreeSpaceInCaisse() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    Program.addPlace(caisse);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    Program.addBasicPlace(simplePlace);
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(1).build()));
    Bouteille b = new Bouteille();
    b.setNom("B21");
    b.setEmplacement("caisse");
    b.setNumLieu(1);
    assertTrue(caisse.canAddObjectAt(b));
    assertTrue(simplePlace.canAddObjectAt(b));
    caisse.addObject(b);
    simplePlace.addObject(b);
    assertFalse(caisse.canAddObjectAt(b));
    assertFalse(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(2).build()));
    assertFalse(caisse.hasFreeSpaceInSimplePlace(b.getPlace()));
    assertFalse(simplePlace.canAddObjectAt(b));
    assertFalse(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(1).build()));
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(2).build()));
    assertFalse(simplePlace.hasFreeSpace(b.getPlace()));
  }

  @Test
  void getNumberOfObjectsPerPlace() {
    Map<Integer, Integer> numberOfBottlesPerPlace = caisseLimit.getNumberOfObjectsPerPlace();
    assertEquals(0, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    numberOfBottlesPerPlace = simplePlaceLimit.getNumberOfObjectsPerPlace();
    assertEquals(0, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    Bouteille b = new Bouteille();
    b.setNom("B21");
    b.setNumLieu(1);
    caisseLimit.addObject(b);
    simplePlaceLimit.addObject(b);
    numberOfBottlesPerPlace = caisseLimit.getNumberOfObjectsPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    numberOfBottlesPerPlace = simplePlaceLimit.getNumberOfObjectsPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    b = new Bouteille();
    b.setNom("B1");
    updateToArmoire1x3x3(b, 1, 1);
    numberOfBottlesPerPlace = armoire1x3x3.getNumberOfObjectsPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
    updateToComplexPlace1x3x3(b, 1, 1);
    numberOfBottlesPerPlace = complexPlace1x3x3.getNumberOfObjectsPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
  }

  @Test
  void getFreeNumPlaceInCaisse() {
    assertEquals(1, caisseLimit.getFreeNumPlaceInSimplePlace());
    assertEquals(0, caisseNoLimit.getFreeNumPlaceInSimplePlace());
    assertEquals(1, simplePlaceLimit.getFreeNumPlace());
    assertEquals(0, simplePlaceNoLimit.getFreeNumPlace());
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse22").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(2).build()));
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse22").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(2).build()));
    Bouteille b = new Bouteille();
    b.setNom("B22");
    b.setNumLieu(1);
    caisse.addObject(b);
    assertEquals(2, caisse.getFreeNumPlaceInSimplePlace());
    simplePlace.addObject(b);
    assertEquals(2, simplePlace.getFreeNumPlace());
    b = new Bouteille();
    b.setNom("B23");
    b.setNumLieu(2);
    caisse.addObject(b);
    assertEquals(-1, caisse.getFreeNumPlaceInSimplePlace());
    simplePlace.addObject(b);
    assertEquals(-1, simplePlace.getFreeNumPlace());
  }

  @Test
  void getLastNumEmplacement() {
    assertEquals(3, caisseLimit.getLastPartNumber());
    assertEquals(1, caisseNoLimit.getLastPartNumber());
    assertEquals(1, armoire1x3x3.getLastPartNumber());
    assertEquals(1, armoire1x3x3Builder.getLastPartNumber());
    assertEquals(2, armoire2x2_3x22545.getLastPartNumber());
    assertEquals(2, armoire2x2_3x22545Builder.getLastPartNumber());
    assertEquals(3, simplePlaceLimit.getLastPartNumber());
    assertEquals(1, simplePlaceNoLimit.getLastPartNumber());
    assertEquals(1, complexPlace1x3x3.getLastPartNumber());
    assertEquals(1, complexPlace1x3x3Builder.getLastPartNumber());
    assertEquals(2, complexPlace2x2_3x22545.getLastPartNumber());
    assertEquals(2, complexPlace2x2_3x22545Builder.getLastPartNumber());
    assertEquals(2, complexPlace2x2_3x22545Builder2.getLastPartNumber());
  }

  @Test
  void complexCaisse() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse24").nbParts(1).startSimplePlace(0).limited(true).limit(3).build();
    Program.addPlace(caisse);
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse24").nbParts(1).startSimplePlace(0).limited(true).limit(3).build();
    Program.addBasicPlace(simplePlace);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(0).build()));
    Bouteille b = new Bouteille();
    b.setNom("B24");
    b.setNumLieu(0);
    b.setEmplacement("caisse24");
    caisse.addObject(b);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    simplePlace.addObject(b);
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(0).build()));
    Bouteille b1 = new Bouteille();
    b1.setNom("B25");
    b1.setNumLieu(0);
    b1.setEmplacement("caisse24");
    caisse.addObject(b1);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    simplePlace.addObject(b1);
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(0).build()));
    Bouteille b2 = new Bouteille();
    b2.setNom("B26");
    b2.setNumLieu(0);
    b2.setEmplacement("caisse24");
    caisse.addObject(b2);
    assertFalse(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    caisse.clearStock(b1);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    caisse.addObject(b1);
    assertFalse(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    simplePlace.addObject(b2);
    assertFalse(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(0).build()));
    simplePlace.clearStorage(b1);
    assertTrue(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(0).build()));
    simplePlace.addObject(b1);
    assertFalse(simplePlace.hasFreeSpace(new Place.PlaceBuilder(simplePlace).withNumPlace(0).build()));
  }

  @Test
  void isSame() {
    assertTrue(armoire1x3x3.isSame(armoire1x3x3));
    assertTrue(armoire1x3x3Builder.isSame(armoire1x3x3Builder));
    assertTrue(armoire1x3x3Builder.isSame(armoire1x3x3));
    assertTrue(armoire2x2_3x22545.isSame(armoire2x2_3x22545));
    assertTrue(armoire2x2_3x22545Builder.isSame(armoire2x2_3x22545Builder));
    assertTrue(armoire2x2_3x22545Builder.isSame(armoire2x2_3x22545));
    assertFalse(armoire1x3x3.isSame(armoire2x2_3x22545));
    assertTrue(complexPlace1x3x3.isSame(complexPlace1x3x3));
    assertTrue(complexPlace1x3x3Builder.isSame(complexPlace1x3x3Builder));
    assertTrue(complexPlace1x3x3Builder.isSame(complexPlace1x3x3));
    assertTrue(complexPlace2x2_3x22545.isSame(complexPlace2x2_3x22545));
    assertTrue(complexPlace2x2_3x22545Builder.isSame(complexPlace2x2_3x22545Builder));
    assertTrue(complexPlace2x2_3x22545Builder2.isSame(complexPlace2x2_3x22545Builder));
    assertTrue(complexPlace2x2_3x22545Builder.isSame(complexPlace2x2_3x22545));
    assertEquals(complexPlace1x3x3, complexPlace1x3x3);
    assertEquals(complexPlace1x3x3Builder, complexPlace1x3x3Builder);
    assertEquals(complexPlace1x3x3Builder, complexPlace1x3x3);
    assertEquals(complexPlace2x2_3x22545, complexPlace2x2_3x22545);
    assertEquals(complexPlace2x2_3x22545Builder, complexPlace2x2_3x22545Builder);
    assertEquals(complexPlace2x2_3x22545Builder2, complexPlace2x2_3x22545Builder);
    assertEquals(complexPlace2x2_3x22545Builder2, complexPlace2x2_3x22545Builder2);
    assertEquals(complexPlace2x2_3x22545Builder, complexPlace2x2_3x22545);
    assertFalse(armoire1x3x3.isSame(armoire2x2_3x22545));
    Rangement r = new Rangement.SimplePlaceBuilder("r").build();
    Rangement r1 = new Rangement.SimplePlaceBuilder("r").build();
    assertTrue(r.isSame(r1));
    assertEquals(r, r1);
    SimplePlace simplePlace = new SimplePlaceBuilder("r").build();
    SimplePlace simplePlace1 = new SimplePlaceBuilder("r").build();
    assertTrue(simplePlace.isSame(simplePlace1));
    assertEquals(simplePlace, simplePlace1);

    LinkedList<Part> list = new LinkedList<>();
    Part partie = new Part(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setCol(1);
    Rangement r2 = new Rangement("r", list);
    ComplexPlace complexPlace2 = new ComplexPlace("r", list);
    list = new LinkedList<>();
    partie = new Part(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setCol(1);
    partie = new Part(1);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setCol(2);
    Rangement r3 = new Rangement("r", list);
    ComplexPlace complexPlace3 = new ComplexPlace("r", list);
    assertFalse(r2.isSame(r3));
    assertFalse(complexPlace2.isSame(complexPlace3));
    assertNotEquals(complexPlace2, complexPlace3);
    list = new LinkedList<>();
    partie = new Part(0);
    list.add(partie);
    partie.setRows(2);
    partie.getRow(0).setCol(1);
    partie.getRow(1).setCol(2);
    Rangement r4 = new Rangement("r", list);
    ComplexPlace complexPlace4 = new ComplexPlace("r", list);
    assertFalse(r2.isSame(r4));
    assertFalse(complexPlace2.isSame(complexPlace4));
    assertNotEquals(complexPlace2, complexPlace4);
    list = new LinkedList<>();
    partie = new Part(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setCol(3);
    Rangement r5 = new Rangement("r", list);
    ComplexPlace complexPlace5 = new ComplexPlace("r", list);
    assertFalse(r2.isSame(r5));
    assertFalse(complexPlace2.isSame(complexPlace5));
    assertNotEquals(complexPlace2, complexPlace5);
  }

  @Test
  void toXml() {
    final String s = armoire1x3x3.toXml();
    final String complexPlace = complexPlace1x3x3.toXml();
    final String s1 = caisseLimit.toXml();
    assertEquals("<place name=\"\" IsCaisse=\"false\" NbPlace=\"1\">\n" +
        "<internal-place NbLine=\"3\">\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "</internal-place>\n" +
        "<name><![CDATA[armoire1x3x3]]></name></place>", s);
    assertEquals("<place name=\"\" IsCaisse=\"false\" NbPlace=\"1\">\n" +
        "<internal-place NbLine=\"3\">\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "</internal-place>\n" +
        "<name><![CDATA[armoire1x3x3]]></name></place>", complexPlace);
    assertEquals("<place name=\"\" IsCaisse=\"true\" NbPlace=\"2\" NumStart=\"1\" NbLimit=\"6\" default=\"false\"><name><![CDATA[caisseLimit]]></name></place>", s1);
    assertEquals("<place name=\"\" IsCaisse=\"true\" NbPlace=\"2\" NumStart=\"1\" NbLimit=\"6\" default=\"false\"><name><![CDATA[simplePlaceLimit]]></name></place>", simplePlaceLimit.toXml());
  }
}
