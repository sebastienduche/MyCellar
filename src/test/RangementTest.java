package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.ComplexPlaceBuilder;
import mycellar.placesmanagement.places.Part;
import mycellar.placesmanagement.places.PlacePosition;
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

  private SimplePlace simplePlaceNoLimit;
  private SimplePlace simplePlaceLimit;
  private ComplexPlace complexPlace1x3x3;
  private ComplexPlace complexPlace1x3x3Builder;
  private ComplexPlace complexPlace2x2_3x22545;
  private ComplexPlace complexPlace2x2_3x22545Builder;
  private ComplexPlace complexPlace2x2_3x22545Builder2;
  private ComplexPlace complexPlace;
  private SimplePlace simplePlace;

  @BeforeEach
  void setUp() {
    simplePlaceNoLimit = new SimplePlaceBuilder("simplePlaceNoLimit").build();
    // Caisse avec 2 emplacements commencant a 1 et limite a 6 bouteilles
    simplePlaceLimit = new SimplePlaceBuilder("simplePlaceLimit").nbParts(2).startSimplePlace(1).limited(true).limit(6).build();
    Part partie = new Part();
    partie.setNumber(0);
    LinkedList<Part> list = new LinkedList<>();
    list.add(partie);
    partie.setRows(3);
    for (int i = 0; i < 3; i++) {
      partie.getRow(i).setColumnCount(3);
    }
    complexPlace1x3x3 = new ComplexPlace("armoire1x3x3", list);
    complexPlace1x3x3Builder = new ComplexPlaceBuilder("armoire1x3x3")
        .nbParts(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    partie = new Part();
    partie.setNumber(0);
    list = new LinkedList<>();
    list.add(partie);
    partie.setRows(2);
    for (int i = 0; i < 2; i++) {
      partie.getRow(i).setColumnCount(2);
    }
    partie = new Part();
    partie.setNumber(1);
    list.add(partie);
    partie.setRows(3);
    partie.getRow(0).setColumnCount(5);
    partie.getRow(1).setColumnCount(4);
    partie.getRow(2).setColumnCount(5);
    complexPlace2x2_3x22545 = new ComplexPlace("armoire2x2_3x22545", list);
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
    complexPlace = new ComplexPlaceBuilder("test").build();
    simplePlace = new SimplePlaceBuilder("test").build();

    Program.addPlace(simplePlaceNoLimit);
    Program.addPlace(simplePlaceLimit);
    Program.addPlace(simplePlace);
    Program.addPlace(complexPlace1x3x3);
    Program.addPlace(complexPlace1x3x3Builder);
    Program.addPlace(complexPlace2x2_3x22545);
    Program.addPlace(complexPlace2x2_3x22545Builder);
    Program.addPlace(complexPlace2x2_3x22545Builder2);
    Program.addPlace(complexPlace);
  }

  @Test
  void getName() {
    assertEquals("simplePlaceNoLimit", simplePlaceNoLimit.getName());
  }

  @Test
  void setName() {
    simplePlaceNoLimit.setName(" toto ");
    assertEquals("toto", simplePlaceNoLimit.getName());
  }

  @Test
  void getPartNumberIncrement() {
    assertEquals(1, simplePlaceLimit.getPartNumberIncrement());
    assertEquals(0, simplePlaceNoLimit.getPartNumberIncrement());
  }

  @Test
  void setPartNumberIncrement() {
    simplePlace.setPartNumberIncrement(2);
    assertEquals(2, simplePlace.getPartNumberIncrement());
  }

  @Test
  void getPartCount() {
    assertEquals(2, simplePlaceLimit.getPartCount());
    assertEquals(1, complexPlace1x3x3.getPartCount());
    assertEquals(1, complexPlace1x3x3Builder.getPartCount());
    assertEquals(2, complexPlace2x2_3x22545.getPartCount());
    assertEquals(2, complexPlace2x2_3x22545Builder.getPartCount());
    assertEquals(2, complexPlace2x2_3x22545Builder2.getPartCount());
  }

  @Test
  void getMexColumnNumber() {
    assertEquals(3, complexPlace1x3x3.getMaxColumnNumber());
    assertEquals(3, complexPlace1x3x3Builder.getMaxColumnNumber());
    assertEquals(5, complexPlace2x2_3x22545.getMaxColumnNumber());
    assertEquals(5, complexPlace2x2_3x22545Builder.getMaxColumnNumber());
    assertEquals(5, complexPlace2x2_3x22545Builder2.getMaxColumnNumber());
  }

  @Test
  void isLimited() {
    assertTrue(simplePlaceLimit.isLimited());
    assertFalse(simplePlaceNoLimit.isLimited());
  }

  @Test
  void setLimited() {
    assertFalse(simplePlace.isLimited());
    simplePlace.setLimited(true, 1);
    assertTrue(simplePlace.isLimited());
  }

  @Test
  void setMaxItemCount() {
    simplePlaceLimit.setMaxItemCount(50);
    assertEquals(50, simplePlaceLimit.getMaxItemCount());
    simplePlaceNoLimit.setMaxItemCount(50);
    assertEquals(-1, simplePlaceNoLimit.getMaxItemCount());
  }

  @Test
  void getLineCountAt() {
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
        assertTrue(complexPlace1x3x3.isExistingCell(0, i, j));
        assertTrue(complexPlace1x3x3Builder.isExistingCell(0, i, j));
      }
      assertFalse(complexPlace1x3x3.isExistingCell(0, i, 3));
      assertFalse(complexPlace1x3x3Builder.isExistingCell(0, i, 3));
    }
    assertFalse(complexPlace1x3x3.isExistingCell(0, 3, 3));
    assertFalse(complexPlace1x3x3Builder.isExistingCell(0, 3, 3));
    assertFalse(complexPlace1x3x3.isExistingCell(0, 3, 0));
    assertFalse(complexPlace1x3x3Builder.isExistingCell(0, 3, 0));

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
    assertEquals(3, complexPlace1x3x3.getMaxColumCount());
    assertEquals(3, complexPlace1x3x3Builder.getMaxColumCount());
    assertEquals(5, complexPlace2x2_3x22545.getMaxColumCount());
    assertEquals(5, complexPlace2x2_3x22545Builder.getMaxColumCount());
    assertEquals(5, complexPlace2x2_3x22545Builder2.getMaxColumCount());
  }

  @Test
  void getNbCaseUseLigne() {
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace1x3x3Builder.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace2x2_3x22545.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace2x2_3x22545Builder.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace2x2_3x22545Builder2.getNbCaseUseInLine(0, 0));
    Bouteille b = new Bouteille();
    b.setNom("B1");
    updateToComplexPlace1x3x3(b, 1, 1);
    assertEquals(1, complexPlace1x3x3.getNbCaseUseInLine(0, 0));
    Bouteille b1 = new Bouteille();
    b1.setNom("B2");
    updateToComplexPlace1x3x3(b1, 2, 1);
    assertEquals(1, complexPlace1x3x3.getNbCaseUseInLine(0, 1));
    Bouteille b2 = new Bouteille();
    b2.setNom("B3");
    updateToComplexPlace1x3x3(b2, 3, 3);
    Bouteille b3 = new Bouteille();
    b3.setNom("B4");
    updateToComplexPlace1x3x3(b3, 3, 2);
    assertEquals(2, complexPlace1x3x3.getNbCaseUseInLine(0, 2));
    complexPlace1x3x3.clearStorage(b);
    complexPlace1x3x3.clearStorage(b1);
    complexPlace1x3x3.clearStorage(b2);
    complexPlace1x3x3.clearStorage(b3);
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 1));
    assertEquals(0, complexPlace1x3x3.getNbCaseUseInLine(0, 2));
    b = new Bouteille();
    b.setNom("B5");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(1, complexPlace2x2_3x22545.getNbCaseUseInLine(1, 0));
    complexPlace2x2_3x22545.clearStorage(b);
    assertEquals(0, complexPlace2x2_3x22545.getNbCaseUseInLine(1, 0));
  }

  private void updateToArmoire(Bouteille b, int numLieu, int ligne, int colonne, String armoire, AbstractPlace place) {
    b.setNumLieu(numLieu);
    b.setLigne(ligne);
    b.setColonne(colonne);
    b.setEmplacement(armoire);
    place.addObject(b);
  }

  private void updateToComplexPlace1x3x3(Bouteille b, int ligne, int colonne) {
    b.setNumLieu(1);
    b.setLigne(ligne);
    b.setColonne(colonne);
    b.setEmplacement("armoire1x3x3");
    complexPlace1x3x3.updateToStock(b);
  }

  @Test
  void getCountFreeCellFrom() {
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
    updateToComplexPlace1x3x3(b, 1, 1);
    assertEquals(0, complexPlace1x3x3.getCountFreeCellFrom(0, 0, 0));
    assertEquals(2, complexPlace1x3x3.getCountFreeCellFrom(0, 0, 1));
    Bouteille b1 = new Bouteille();
    b1.setNom("B28");
    updateToComplexPlace1x3x3(b1, 1, 3);
    assertEquals(1, complexPlace1x3x3.getCountFreeCellFrom(0, 0, 1));
    complexPlace1x3x3.clearStorage(b);
    complexPlace1x3x3.clearStorage(b1);
    b = new Bouteille();
    b.setEmplacement(complexPlace2x2_3x22545.getName());
    b.setNom("B6");
    b.setNumLieu(2);
    b.setLigne(1);
    b.setColonne(2);
    complexPlace2x2_3x22545.addObject(b);
    assertEquals(1, complexPlace2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
    assertEquals(3, complexPlace2x2_3x22545.getCountFreeCellFrom(1, 0, 2));
    complexPlace2x2_3x22545.clearStorage(b);
    assertEquals(5, complexPlace2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
  }

  @Test
  void getTotalCellUsed() {
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
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(0));
    assertEquals(1, complexPlace2x2_3x22545.getTotalCellUsed(1));
    complexPlace2x2_3x22545.clearStorage(b);
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, complexPlace2x2_3x22545.getTotalCellUsed(0));
  }

  @Test
  void getTotalCountCellUsed() throws MyCellarException {
    assertEquals(0, complexPlace1x3x3.getTotalCountCellUsed());
    assertEquals(0, complexPlace1x3x3Builder.getTotalCountCellUsed());
    assertEquals(0, complexPlace2x2_3x22545.getTotalCountCellUsed());
    assertEquals(0, complexPlace2x2_3x22545Builder.getTotalCountCellUsed());
    assertEquals(0, complexPlace2x2_3x22545Builder2.getTotalCountCellUsed());
    Bouteille b = new Bouteille();
    b.setNom("B8");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(1, complexPlace2x2_3x22545.getTotalCountCellUsed());
    Bouteille b1 = new Bouteille();
    b.setNom("B9");
    updateToArmoire(b1, 1, 2, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(2, complexPlace2x2_3x22545.getTotalCountCellUsed());
    complexPlace2x2_3x22545.removeObject(b);
    assertEquals(1, complexPlace2x2_3x22545.getTotalCountCellUsed());
    complexPlace2x2_3x22545.removeObject(b1);
    assertEquals(0, complexPlace2x2_3x22545.getTotalCountCellUsed());
    Bouteille b21 = new Bouteille();
    b21.setNom("B291");
    b21.setNumLieu(2);
    b21.setEmplacement("simplePlaceLimit");
    simplePlaceLimit.addObject(b21);
    assertEquals(1, simplePlaceLimit.getTotalCellUsed());
    Bouteille b31 = new Bouteille();
    b31.setNom("B301");
    b31.setNumLieu(0);
    b31.setEmplacement("simplePlaceNoLimit");
    simplePlaceNoLimit.addObject(b31);
    assertEquals(1, simplePlaceNoLimit.getTotalCellUsed());
    simplePlaceLimit.removeObject(b21);
    assertEquals(0, simplePlaceLimit.getTotalCellUsed());
    simplePlaceNoLimit.removeObject(b31);
    assertEquals(0, simplePlaceNoLimit.getTotalCellUsed());
  }

  @Test
  void getCountCellUsed() throws MyCellarException {
    assertEquals(0, simplePlaceLimit.getCountCellUsed(1));
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(0));
    Bouteille b01 = new Bouteille();
    b01.setNom("B010");
    b01.setNumLieu(2);
    b01.setEmplacement("simplePlaceLimit");
    simplePlaceLimit.addObject(b01);
    assertEquals(1, simplePlaceLimit.getCountCellUsed(1));
    assertEquals(1, simplePlaceLimit.getCountCellUsed(b01.getPlacePosition().getPlaceNumIndex()));
    assertEquals(1, simplePlaceLimit.getCountCellUsed(b01.getPlacePosition()));
    Bouteille b11 = new Bouteille();
    b11.setNom("B11");
    b11.setNumLieu(0);
    b11.setEmplacement("simplePlaceNoLimit");
    simplePlaceNoLimit.addObject(b11);
    assertEquals(1, simplePlaceNoLimit.getCountCellUsed(0));
    assertEquals(1, simplePlaceNoLimit.getCountCellUsed(b11.getPlacePosition().getPlaceNumIndex()));
    assertEquals(1, simplePlaceNoLimit.getCountCellUsed(b11.getPlacePosition()));
    simplePlaceLimit.removeObject(b01);
    assertEquals(0, simplePlaceLimit.getCountCellUsed(1));
    assertEquals(0, simplePlaceLimit.getCountCellUsed(b01.getPlacePosition().getPlaceNumIndex()));
    assertEquals(0, simplePlaceLimit.getCountCellUsed(b01.getPlacePosition()));
    simplePlaceNoLimit.removeObject(b11);
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(0));
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(b11.getPlacePosition().getPlaceNumIndex()));
    assertEquals(0, simplePlaceNoLimit.getCountCellUsed(b11.getPlacePosition()));
  }

  @Test
  void addObject() throws MyCellarException {
    Bouteille b = addAndRemoveBottle();
    b.setEmplacement("simplePlaceLimit");
    simplePlaceLimit.addObject(b);
    assertEquals(1, simplePlaceLimit.getCountCellUsed(1));
    simplePlaceLimit.removeObject(b);
    assertEquals(0, simplePlaceLimit.getCountCellUsed(1));
  }

  private Bouteille addAndRemoveBottle() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B12");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(1, complexPlace2x2_3x22545.getNbCaseUseInLine(1, 0));
    assertEquals(b, complexPlace2x2_3x22545.getObject(b.getPlacePosition()).get());
    complexPlace2x2_3x22545.removeObject(b);
    return b;
  }

  @Test
  void removeObject() throws MyCellarException {
    Bouteille b = addAndRemoveBottle();
    simplePlaceLimit.addObject(b);
    assertEquals(1, simplePlaceLimit.getCountCellUsed(1));
    simplePlaceLimit.removeObject(b);
    assertEquals(0, simplePlaceLimit.getCountCellUsed(1));
  }

  @Test
  void updateToStock() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B13");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(b.getPlacePosition()).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B14");
    updateToComplexPlace1x3x3(b1, 1, 2);
    assertEquals(b1, complexPlace1x3x3.getObject(b1.getPlacePosition()).get());
    complexPlace1x3x3.clearStorage(b1);
    assertTrue(complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).isEmpty());
    complexPlace1x3x3.removeObject(b);
  }

  @Test
  void moveLineWine() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B15");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).get());
    complexPlace1x3x3.moveToLine(b, 2);
    assertTrue(complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).isEmpty());
    assertEquals(b, complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(2)
        .withColumn(2)
        .build()).get());
  }

  @Test
  void getObject() {
    Bouteille b = new Bouteille();
    b.setNom("B16");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).get());
    assertEquals(b, complexPlace1x3x3.getObject(b.getPlacePosition()).get());
    PlacePosition placePosition = new PlacePosition.PlacePositionBuilder(complexPlace1x3x3).withNumPlace(1).withLine(1).withColumn(2).build();
    assertEquals(b, complexPlace1x3x3.getObject(placePosition).get());
    Bouteille b1 = new Bouteille();
    b.setNom("B17");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(b1, complexPlace2x2_3x22545.getObject(new PlacePosition.PlacePositionBuilder(complexPlace2x2_3x22545)
        .withNumPlace(2)
        .withLine(2)
        .withColumn(3)
        .build()).get());
    assertEquals(b1, complexPlace2x2_3x22545.getObject(b1.getPlacePosition()).get());
    placePosition = new PlacePosition.PlacePositionBuilder(complexPlace2x2_3x22545).withNumPlace(2).withLine(2).withColumn(3).build();
    assertEquals(b1, complexPlace2x2_3x22545.getObject(placePosition).get());
    complexPlace1x3x3.clearStorage(b);
    complexPlace2x2_3x22545.clearStorage(b1);
    assertTrue(complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).isEmpty());
    assertTrue(complexPlace1x3x3.getObject(b.getPlacePosition()).isEmpty());
    assertTrue(complexPlace2x2_3x22545.getObject(b1.getPlacePosition()).isEmpty());
    assertTrue(complexPlace2x2_3x22545.getObject(placePosition).isEmpty());
  }

  @Test
  void clearStorage() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B18");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", complexPlace1x3x3);
    assertEquals(b, complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B19");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", complexPlace2x2_3x22545);
    assertEquals(b1, complexPlace2x2_3x22545.getObject(new PlacePosition.PlacePositionBuilder(complexPlace2x2_3x22545)
        .withNumPlace(2)
        .withLine(2)
        .withColumn(3)
        .build()).get());
    complexPlace1x3x3.clearStorage(b);
    complexPlace2x2_3x22545.clearStorage(b1);
    assertTrue(complexPlace1x3x3.getObject(new PlacePosition.PlacePositionBuilder(complexPlace1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).isEmpty());
    assertTrue(complexPlace2x2_3x22545.getObject(new PlacePosition.PlacePositionBuilder(complexPlace2x2_3x22545)
        .withNumPlace(2)
        .withLine(2)
        .withColumn(3)
        .build()).isEmpty());
    complexPlace2x2_3x22545.removeObject(b1);
    complexPlace1x3x3.removeObject(b);
  }

  @Test
  void isSameColumnNumber() {
    assertTrue(complexPlace1x3x3.isSameColumnNumber());
    assertTrue(complexPlace1x3x3Builder.isSameColumnNumber());
    assertFalse(complexPlace2x2_3x22545.isSameColumnNumber());
    assertFalse(complexPlace2x2_3x22545Builder.isSameColumnNumber());
    assertFalse(complexPlace2x2_3x22545Builder2.isSameColumnNumber());
  }

  @Test
  void canAddObjectAt() {
    SimplePlace caisse = new SimplePlaceBuilder("caisse20").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    Program.addPlace(caisse);
    final PlacePosition place = new PlacePosition.PlacePositionBuilder(caisse).withNumPlace(1).build();
    assertTrue(caisse.hasFreeSpace(place));
    Bouteille b = new Bouteille();
    b.setEmplacement("caisse20");
    b.setNom("B20");
    b.setNumLieu(1);
    caisse.addObject(b);
    assertFalse(caisse.canAddObjectAt(0, 0, 0));
    assertTrue(caisse.canAddObjectAt(1, 0, 0));
    assertFalse(caisse.canAddObjectAt(place));
    b.setNumLieu(0);
    assertFalse(caisse.canAddObjectAt(b.getPlacePosition()));
    b.setNumLieu(2);
    assertTrue(caisse.canAddObjectAt(b.getPlacePosition()));
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
    b.setEmplacement("caisse20");
    assertFalse(complexPlace1x3x3.canAddObjectAt(b.getPlacePosition()));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b.getPlacePosition()));
    b.setNumLieu(1);
    assertFalse(complexPlace1x3x3.canAddObjectAt(b.getPlacePosition()));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b.getPlacePosition()));
    b.setLigne(1);
    assertFalse(complexPlace1x3x3.canAddObjectAt(b.getPlacePosition()));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b.getPlacePosition()));
    b.setColonne(1);
    assertFalse(complexPlace1x3x3.canAddObjectAt(b.getPlacePosition()));
    assertFalse(complexPlace1x3x3Builder.canAddObjectAt(b.getPlacePosition()));
  }

  @Test
  void putTabStock() {
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(2).build();
    Program.addPlace(simplePlace);
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(1).build()));

    Bouteille b = new Bouteille();
    b.setNom("B20");
    b.setEmplacement("caisse");
    b.setNumLieu(1);
    simplePlace.addObject(b);
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(1).build()));
    assertEquals(1, simplePlace.getCountCellUsed(0));
    assertTrue(simplePlace.canAddObjectAt(0, 0, 0));
    assertTrue(simplePlace.canAddObjectAt(1, 0, 0));
    assertTrue(simplePlace.hasFreeSpace(b.getPlacePosition()));
    b.setNumLieu(0);
    assertFalse(simplePlace.canAddObjectAt(b.getPlacePosition()));
  }

  @Test
  void hasFreeSpaceInCaisse() {
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    Program.addPlace(simplePlace);
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(1).build()));
    Bouteille b = new Bouteille();
    b.setNom("B21");
    b.setEmplacement("caisse");
    b.setNumLieu(1);
    assertTrue(simplePlace.canAddObjectAt(b.getPlacePosition()));
    simplePlace.addObject(b);
    assertFalse(simplePlace.canAddObjectAt(b.getPlacePosition()));
    assertFalse(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(1).build()));
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(2).build()));
    assertFalse(simplePlace.hasFreeSpace(b.getPlacePosition()));
  }

  @Test
  void getNumberOfObjectsPerPlace() {
    Map<Integer, Integer> numberOfBottlesPerPlace;
    numberOfBottlesPerPlace = simplePlaceLimit.getNumberOfObjectsPerPlace();
    assertEquals(0, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    Bouteille b = new Bouteille();
    b.setNom("B21");
    b.setNumLieu(1);
    simplePlaceLimit.addObject(b);
    numberOfBottlesPerPlace = simplePlaceLimit.getNumberOfObjectsPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    b = new Bouteille();
    b.setNom("B1");
    updateToComplexPlace1x3x3(b, 1, 1);
    numberOfBottlesPerPlace = complexPlace1x3x3.getNumberOfObjectsPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
  }

  @Test
  void getFreeNumPlace() {
    assertEquals(1, simplePlaceLimit.getFreeNumPlace());
    assertEquals(0, simplePlaceNoLimit.getFreeNumPlace());
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse22").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(2).build()));
    Bouteille b = new Bouteille();
    b.setNom("B22");
    b.setNumLieu(1);
    simplePlace.addObject(b);
    assertEquals(2, simplePlace.getFreeNumPlace());
    b = new Bouteille();
    b.setNom("B23");
    b.setNumLieu(2);
    simplePlace.addObject(b);
    assertEquals(-1, simplePlace.getFreeNumPlace());
  }

  @Test
  void getLastPartNumber() {
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
    SimplePlace simplePlace = new SimplePlaceBuilder("caisse24").nbParts(1).startSimplePlace(0).limited(true).limit(3).build();
    Program.addPlace(simplePlace);
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(0).build()));
    Bouteille b = new Bouteille();
    b.setNom("B24");
    b.setNumLieu(0);
    b.setEmplacement("caisse24");
    simplePlace.addObject(b);
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(0).build()));
    Bouteille b1 = new Bouteille();
    b1.setNom("B25");
    b1.setNumLieu(0);
    b1.setEmplacement("caisse24");
    simplePlace.addObject(b1);
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(0).build()));
    Bouteille b2 = new Bouteille();
    b2.setNom("B26");
    b2.setNumLieu(0);
    b2.setEmplacement("caisse24");
    simplePlace.addObject(b2);
    assertFalse(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(0).build()));
    simplePlace.clearStorage(b1);
    assertTrue(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(0).build()));
    simplePlace.addObject(b1);
    assertFalse(simplePlace.hasFreeSpace(new PlacePosition.PlacePositionBuilder(simplePlace).withNumPlace(0).build()));
  }

  @Test
  void isSame() {
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
    SimplePlace simplePlace = new SimplePlaceBuilder("r").build();
    SimplePlace simplePlace1 = new SimplePlaceBuilder("r").build();
    assertTrue(simplePlace.isSame(simplePlace1));
    assertEquals(simplePlace, simplePlace1);

    LinkedList<Part> list = new LinkedList<>();
    Part partie = new Part();
    partie.setNumber(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setColumnCount(1);
    ComplexPlace complexPlace2 = new ComplexPlace("r", list);
    list = new LinkedList<>();
    partie = new Part();
    partie.setNumber(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setColumnCount(1);
    partie = new Part();
    partie.setNumber(1);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setColumnCount(2);
    ComplexPlace complexPlace3 = new ComplexPlace("r", list);
    assertFalse(complexPlace2.isSame(complexPlace3));
    assertNotEquals(complexPlace2, complexPlace3);
    list = new LinkedList<>();
    partie = new Part();
    partie.setNumber(0);
    list.add(partie);
    partie.setRows(2);
    partie.getRow(0).setColumnCount(1);
    partie.getRow(1).setColumnCount(2);
    ComplexPlace complexPlace4 = new ComplexPlace("r", list);
    assertFalse(complexPlace2.isSame(complexPlace4));
    assertNotEquals(complexPlace2, complexPlace4);
    list = new LinkedList<>();
    partie = new Part();
    partie.setNumber(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setColumnCount(3);
    ComplexPlace complexPlace5 = new ComplexPlace("r", list);
    assertFalse(complexPlace2.isSame(complexPlace5));
    assertNotEquals(complexPlace2, complexPlace5);
  }

  @Test
  void toXml() {
    final String complexPlace = complexPlace1x3x3.toXml();
    assertEquals("<place name=\"\" IsCaisse=\"false\" NbPlace=\"1\">\n" +
        "<internal-place NbLine=\"3\">\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "</internal-place>\n" +
        "<name><![CDATA[armoire1x3x3]]></name></place>", complexPlace);
    assertEquals("<place name=\"\" IsCaisse=\"true\" NbPlace=\"2\" NumStart=\"1\" NbLimit=\"6\" default=\"false\"><name><![CDATA[simplePlaceLimit]]></name></place>", simplePlaceLimit.toXml());
  }
}
