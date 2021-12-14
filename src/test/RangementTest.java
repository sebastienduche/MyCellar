package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.MyCellarException;
import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangementTest {

  private Rangement caisseNoLimit;
  private Rangement caisseLimit;
  private Rangement armoire1x3x3;
  private Rangement armoire1x3x3Builder;
  private Rangement armoire2x2_3x22545;
  private Rangement armoire2x2_3x22545Builder;
  private Rangement rangement;

  @BeforeEach
  void setUp() {
    caisseNoLimit = new Rangement.SimplePlaceBuilder("caisseNoLimit").build();
    // Caisse avec 2 emplacements commencant a 1 et limite a 6 bouteilles
    caisseLimit = new Rangement.SimplePlaceBuilder("caisseLimit").nbParts(2).startSimplePlace(1).limited(true).limit(6).build();
    Part partie = new Part(0);
    LinkedList<Part> list = new LinkedList<>();
    list.add(partie);
    partie.setRows(3);
    for (int i = 0; i < 3; i++) {
      partie.getRow(i).setCol(3);
    }
    armoire1x3x3 = new Rangement("armoire1x3x3", list);
    armoire1x3x3Builder = new Rangement.RangementBuilder("armoire1x3x3")
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
    try {
      armoire2x2_3x22545Builder = new Rangement.RangementBuilder("armoire2x2_3x22545")
          .nbParts(new int[]{2, 3})
          .differentColumnsNumber()
          .columnsNumberForPart(0, new int[]{2, 2})
          .columnsNumberForPart(1, new int[]{5, 4, 5})
          .build();
    } catch (Exception ignored) {
    }
    rangement = new Rangement.SimplePlaceBuilder("test").build();

    Program.addCave(caisseLimit);
    Program.addCave(caisseNoLimit);
    Program.addCave(armoire1x3x3);
    Program.addCave(armoire1x3x3Builder);
    Program.addCave(armoire2x2_3x22545);
    Program.addCave(armoire2x2_3x22545Builder);
    Program.addCave(rangement);
  }

  @Test
  void getNom() {
    assertEquals("caisseNoLimit", caisseNoLimit.getName());
  }

  @Test
  void setNom() {
    rangement.setName("toto");
    assertEquals("toto", rangement.getName());
  }

  @Test
  void getStartCaisse() {
    assertEquals(1, caisseLimit.getStartSimplePlace());
    assertEquals(0, caisseNoLimit.getStartSimplePlace());
  }

  @Test
  void setStartCaisse() {
    rangement.setStartSimplePlace(2);
    assertEquals(2, rangement.getStartSimplePlace());
  }

  @Test
  void getNbEmplacements() {
    assertEquals(1, armoire1x3x3.getNbParts());
    assertEquals(1, armoire1x3x3Builder.getNbParts());
    assertEquals(2, armoire2x2_3x22545.getNbParts());
    assertEquals(2, armoire2x2_3x22545Builder.getNbParts());
  }

  @Test
  void getNbColonnesStock() {
    assertEquals(3, armoire1x3x3.getNbColumnsStock());
    assertEquals(3, armoire1x3x3Builder.getNbColumnsStock());
    assertEquals(5, armoire2x2_3x22545.getNbColumnsStock());
    assertEquals(5, armoire2x2_3x22545Builder.getNbColumnsStock());
  }

  @Test
  void isLimited() {
    assertTrue(caisseLimit.isSimplePlaceLimited());
    assertFalse(caisseNoLimit.isSimplePlaceLimited());
  }

  @Test
  void setLimited() {
    assertFalse(rangement.isSimplePlaceLimited());
    rangement.setSimplePlaceLimited(true);
    assertTrue(rangement.isSimplePlaceLimited());
  }

  @Test
  void setNbBottleInCaisse() {
    caisseLimit.setNbObjectInSimplePlace(50);
    assertEquals(50, caisseLimit.getNbColumnsStock());
    caisseNoLimit.setNbObjectInSimplePlace(50);
    assertEquals(-1, caisseNoLimit.getNbColumnsStock());
  }

  @Test
  void getNbLignesInt() {
    assertEquals(3, armoire1x3x3.getLineCountAt(0));
    assertEquals(3, armoire1x3x3Builder.getLineCountAt(0));
    assertEquals(2, armoire2x2_3x22545.getLineCountAt(0));
    assertEquals(2, armoire2x2_3x22545Builder.getLineCountAt(0));
    assertEquals(3, armoire2x2_3x22545.getLineCountAt(1));
    assertEquals(3, armoire2x2_3x22545Builder.getLineCountAt(1));
  }

  @Test
  void isExistingCell() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertTrue(armoire1x3x3.isExistingCell(0, i, j));
        assertTrue(armoire1x3x3Builder.isExistingCell(0, i, j));
      }
      assertFalse(armoire1x3x3.isExistingCell(0, i, 3));
      assertFalse(armoire1x3x3Builder.isExistingCell(0, i, 3));
    }
    assertFalse(armoire1x3x3.isExistingCell(0, 3, 3));
    assertFalse(armoire1x3x3Builder.isExistingCell(0, 3, 3));
    assertFalse(armoire1x3x3.isExistingCell(0, 3, 0));
    assertFalse(armoire1x3x3Builder.isExistingCell(0, 3, 0));

    LinkedList<Rangement> list = new LinkedList<>();
    list.add(armoire1x3x3);
    list.add(armoire1x3x3Builder);
    list.add(armoire2x2_3x22545);
    list.add(armoire2x2_3x22545Builder);
    for (Rangement r : list) {
      int emplacementMax = r.getNbParts();
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
  void getNbColonnesIntInt() {
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
  }

  @Test
  void getNbColonnesMaxInt() {
    assertEquals(3, armoire1x3x3.getMaxColumCountAt(0));
    assertEquals(3, armoire1x3x3Builder.getMaxColumCountAt(0));
    assertEquals(2, armoire2x2_3x22545.getMaxColumCountAt(0));
    assertEquals(2, armoire2x2_3x22545Builder.getMaxColumCountAt(0));
    assertEquals(5, armoire2x2_3x22545.getMaxColumCountAt(1));
    assertEquals(5, armoire2x2_3x22545Builder.getMaxColumCountAt(1));
  }

  @Test
  void getNbColonnesMax() {
    assertEquals(3, armoire1x3x3.getMaxColumCountAt());
    assertEquals(3, armoire1x3x3Builder.getMaxColumCountAt());
    assertEquals(5, armoire2x2_3x22545.getMaxColumCountAt());
    assertEquals(5, armoire2x2_3x22545Builder.getMaxColumCountAt());
  }

  @Test
  void getNbCaseUseLigne() {
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire1x3x3Builder.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire2x2_3x22545Builder.getNbCaseUseInLine(0, 0));
    Bouteille b = new Bouteille();
    b.setNom("B1");
    updateToArmoire1x3x3(b, 1, 1);
    assertEquals(1, armoire1x3x3.getNbCaseUseInLine(0, 0));
    Bouteille b1 = new Bouteille();
    b1.setNom("B2");
    updateToArmoire1x3x3(b1, 2, 1);
    assertEquals(1, armoire1x3x3.getNbCaseUseInLine(0, 1));
    Bouteille b2 = new Bouteille();
    b2.setNom("B3");
    b2.setNumLieu(1);
    b2.setLigne(3);
    b2.setColonne(3);
    b2.setEmplacement("armoire1x3x3");
    armoire1x3x3.updateToStock(b2);
    Bouteille b3 = new Bouteille();
    b3.setNom("B4");
    b3.setNumLieu(1);
    b3.setLigne(3);
    b3.setColonne(2);
    b3.setEmplacement("armoire1x3x3");
    armoire1x3x3.updateToStock(b3);
    assertEquals(2, armoire1x3x3.getNbCaseUseInLine(0, 2));
    armoire1x3x3.clearStock(b, b.getPlace());
    armoire1x3x3.clearStock(b1, b1.getPlace());
    armoire1x3x3.clearStock(b2, b2.getPlace());
    armoire1x3x3.clearStock(b3, b3.getPlace());
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 0));
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 1));
    assertEquals(0, armoire1x3x3.getNbCaseUseInLine(0, 2));
    b = new Bouteille();
    b.setNom("B5");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseInLine(1, 0));
    armoire2x2_3x22545.clearStock(b, b.getPlace());
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseInLine(1, 0));
  }

  private void updateToArmoire(Bouteille b, int numLieu, int ligne, int colonne, String armoire, Rangement place) {
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

  @Test
  void getNbCaseFreeCoteLigne() {
    LinkedList<Rangement> list = new LinkedList<>();
    list.add(armoire1x3x3);
    list.add(armoire1x3x3Builder);
    list.add(armoire2x2_3x22545);
    list.add(armoire2x2_3x22545Builder);
    for (Rangement r : list) {
      int emplacementMax = r.getNbParts();
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
    assertEquals(0, armoire1x3x3.getCountFreeCellFrom(0, 0, 0));
    assertEquals(2, armoire1x3x3.getCountFreeCellFrom(0, 0, 1));
    Bouteille b1 = new Bouteille();
    b1.setNom("B28");
    updateToArmoire1x3x3(b1, 1, 3);
    assertEquals(1, armoire1x3x3.getCountFreeCellFrom(0, 0, 1));
    armoire1x3x3.clearStock(b, b.getPlace());
    armoire1x3x3.clearStock(b1, b1.getPlace());
    b = new Bouteille();
    b.setEmplacement(armoire2x2_3x22545.getName());
    b.setNom("B6");
    b.setNumLieu(2);
    b.setLigne(1);
    b.setColonne(2);
    armoire2x2_3x22545.addObject(b);
    assertEquals(1, armoire2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
    assertEquals(3, armoire2x2_3x22545.getCountFreeCellFrom(1, 0, 2));
    armoire2x2_3x22545.clearStock(b, b.getPlace());
    assertEquals(5, armoire2x2_3x22545.getCountFreeCellFrom(1, 0, 0));
  }

  @Test
  void getNbCaseUse() {
    assertEquals(0, armoire1x3x3.getTotalCellUsed(0));
    assertEquals(0, armoire1x3x3Builder.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545Builder.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(1));
    assertEquals(0, armoire2x2_3x22545Builder.getTotalCellUsed(1));
    Bouteille b = new Bouteille();
    b.setNom("B7");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
    assertEquals(1, armoire2x2_3x22545.getTotalCellUsed(1));
    armoire2x2_3x22545.clearStock(b, b.getPlace());
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
    assertEquals(0, armoire2x2_3x22545.getTotalCellUsed(0));
  }

  @Test
  void getNbCaseUseAll() throws MyCellarException {
    assertEquals(0, armoire1x3x3.getTotalCountCellUsed());
    assertEquals(0, armoire1x3x3Builder.getTotalCountCellUsed());
    assertEquals(0, armoire2x2_3x22545.getTotalCountCellUsed());
    assertEquals(0, armoire2x2_3x22545Builder.getTotalCountCellUsed());
    Bouteille b = new Bouteille();
    b.setNom("B8");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getTotalCountCellUsed());
    Bouteille b1 = new Bouteille();
    b.setNom("B9");
    updateToArmoire(b1, 1, 2, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(2, armoire2x2_3x22545.getTotalCountCellUsed());
    armoire2x2_3x22545.removeObject(b);
    assertEquals(1, armoire2x2_3x22545.getTotalCountCellUsed());
    armoire2x2_3x22545.removeObject(b1);
    assertEquals(0, armoire2x2_3x22545.getTotalCountCellUsed());
    assertEquals(0, caisseLimit.getTotalCountCellUsed());
    assertEquals(0, caisseNoLimit.getTotalCountCellUsed());
    Bouteille b2 = new Bouteille();
    b2.setNom("B29");
    b2.setNumLieu(2);
    b2.setEmplacement("caisseLimit");
    caisseLimit.addObject(b2);
    assertEquals(1, caisseLimit.getTotalCountCellUsed());
    Bouteille b3 = new Bouteille();
    b3.setNom("B30");
    b3.setNumLieu(0);
    b3.setEmplacement("caisseNoLimit");
    caisseNoLimit.addObject(b3);
    assertEquals(1, caisseNoLimit.getTotalCountCellUsed());
    caisseLimit.removeObject(b2);
    assertEquals(0, caisseLimit.getTotalCountCellUsed());
    caisseNoLimit.removeObject(b3);
    assertEquals(0, caisseNoLimit.getTotalCountCellUsed());
  }

  @Test
  void getNbCaseUseForCaisse() throws MyCellarException {
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
  }

  @Test
  void addWine() throws MyCellarException {
    Bouteille b = addAndRemoveBottle();
    b.setEmplacement("caisseLimit");
    caisseLimit.addObject(b);
    assertEquals(1, caisseLimit.getTotalCellUsed(1));
    caisseLimit.removeObject(b);
    assertEquals(0, caisseLimit.getTotalCellUsed(1));
  }

  private Bouteille addAndRemoveBottle() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B12");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseInLine(1, 0));
    assertEquals(b, armoire2x2_3x22545.getObject(1, 0, 1).get());
    assertEquals(b, armoire2x2_3x22545.getObject(b).get());
    armoire2x2_3x22545.removeObject(b);
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
    assertEquals(b, armoire1x3x3.getObject(b).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B14");
    updateToArmoire1x3x3(b1, 1, 2);
    assertEquals(b1, armoire1x3x3.getObject(0, 0, 1).get());
    assertEquals(b1, armoire1x3x3.getObject(b1).get());
    armoire1x3x3.clearStock(b1, b.getPlace());
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    armoire1x3x3.removeObject(b);
  }

  @Test
  void moveLineWine() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B15");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getObject(0, 0, 1).get());
    armoire1x3x3.moveLine(b, 2);
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    assertEquals(b, armoire1x3x3.getObject(0, 1, 1).get());
  }

  @Test
  void getBouteille() {
    Bouteille b = new Bouteille();
    b.setNom("B16");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getObject(0, 0, 1).get());
    assertEquals(b, armoire1x3x3.getObject(b.getPlace()).get());
    Bouteille b1 = new Bouteille();
    b.setNom("B17");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(b1, armoire2x2_3x22545.getObject(1, 1, 2).get());
    assertEquals(b1, armoire2x2_3x22545.getObject(b1.getPlace()).get());
    armoire1x3x3.clearStock(b, b.getPlace());
    armoire2x2_3x22545.clearStock(b1, b1.getPlace());
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    assertTrue(armoire1x3x3.getObject(b.getPlace()).isEmpty());
    assertTrue(armoire2x2_3x22545.getObject(b1.getPlace()).isEmpty());
  }

  @Test
  void clearStock() throws MyCellarException {
    Bouteille b = new Bouteille();
    b.setNom("B18");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getObject(0, 0, 1).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B19");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(b1, armoire2x2_3x22545.getObject(1, 1, 2).get());
    armoire1x3x3.clearStock(b, b.getPlace());
    armoire2x2_3x22545.clearStock(b1, b1.getPlace());
    assertTrue(armoire1x3x3.getObject(0, 0, 1).isEmpty());
    assertTrue(armoire2x2_3x22545.getObject(1, 1, 2).isEmpty());
    armoire2x2_3x22545.removeObject(b1);
    armoire1x3x3.removeObject(b);
  }

  @Test
  void isSameColumnNumber() {
    assertTrue(armoire1x3x3.isSameColumnNumber());
    assertTrue(armoire1x3x3Builder.isSameColumnNumber());
    assertFalse(armoire2x2_3x22545.isSameColumnNumber());
    assertFalse(armoire2x2_3x22545Builder.isSameColumnNumber());
  }

  @Test
  void canAddBottle() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
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
    for (Rangement r : list) {
      int emplacementMax = r.getNbParts();
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
    b.setNumLieu(1);
    assertFalse(armoire1x3x3.canAddObjectAt(b));
    assertFalse(armoire1x3x3Builder.canAddObjectAt(b));
    b.setLigne(1);
    assertFalse(armoire1x3x3.canAddObjectAt(b));
    assertFalse(armoire1x3x3Builder.canAddObjectAt(b));
    b.setColonne(1);
    assertTrue(armoire1x3x3.canAddObjectAt(b));
    assertTrue(armoire1x3x3Builder.canAddObjectAt(b));
  }

  @Test
  void putTabStock() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(2).build();
    Program.addCave(caisse);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    Bouteille b = new Bouteille();
    b.setNom("B20");
    b.setEmplacement("caisse");
    b.setNumLieu(1);
    caisse.addObject(b);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    assertEquals(1, caisse.getTotalCellUsed(0));
    assertTrue(caisse.canAddObjectAt(0, 0, 0));
    assertTrue(caisse.canAddObjectAt(1, 0, 0));
    assertTrue(caisse.canAddObjectAt(b));
    assertTrue(caisse.hasFreeSpaceInSimplePlace(b.getPlace()));
    b.setNumLieu(0);
    assertFalse(caisse.canAddObjectAt(b));
  }

  @Test
  void hasFreeSpaceInCaisse() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    Program.addCave(caisse);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    Bouteille b = new Bouteille();
    b.setNom("B21");
    b.setEmplacement("caisse");
    b.setNumLieu(1);
    assertTrue(caisse.canAddObjectAt(b));
    caisse.addObject(b);
    assertFalse(caisse.canAddObjectAt(b));
    assertFalse(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(1).build()));
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(2).build()));
    assertFalse(caisse.hasFreeSpaceInSimplePlace(b.getPlace()));
  }

  @Test
  void getNumberOfBottlesPerPlace() {
    Map<Integer, Integer> numberOfBottlesPerPlace = caisseLimit.getNumberOfBottlesPerPlace();
    assertEquals(0, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    Bouteille b = new Bouteille();
    b.setNom("B21");
    b.setNumLieu(1);
    caisseLimit.addObject(b);
    numberOfBottlesPerPlace = caisseLimit.getNumberOfBottlesPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
    assertEquals(0, (int) numberOfBottlesPerPlace.get(1));
    b = new Bouteille();
    b.setNom("B1");
    updateToArmoire1x3x3(b, 1, 1);
    numberOfBottlesPerPlace = armoire1x3x3.getNumberOfBottlesPerPlace();
    assertEquals(1, (int) numberOfBottlesPerPlace.get(0));
  }

  @Test
  void getFreeNumPlaceInCaisse() {
    assertEquals(1, caisseLimit.getFreeNumPlaceInSimplePlace());
    assertEquals(0, caisseNoLimit.getFreeNumPlaceInSimplePlace());
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse").nbParts(2).startSimplePlace(1).limited(true).limit(1).build();
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(2).build()));
    Bouteille b = new Bouteille();
    b.setNom("B22");
    b.setNumLieu(1);
    caisse.addObject(b);
    assertEquals(2, caisse.getFreeNumPlaceInSimplePlace());
    b = new Bouteille();
    b.setNom("B23");
    b.setNumLieu(2);
    caisse.addObject(b);
    assertEquals(-1, caisse.getFreeNumPlaceInSimplePlace());
  }

  @Test
  void getLastNumEmplacement() {
    assertEquals(3, caisseLimit.getLastPartNumber());
    assertEquals(1, caisseNoLimit.getLastPartNumber());
    assertEquals(1, armoire1x3x3.getLastPartNumber());
    assertEquals(1, armoire1x3x3Builder.getLastPartNumber());
    assertEquals(2, armoire2x2_3x22545.getLastPartNumber());
    assertEquals(2, armoire2x2_3x22545Builder.getLastPartNumber());
  }

  @Test
  void complexCaisse() {
    Rangement caisse = new Rangement.SimplePlaceBuilder("caisse").nbParts(1).startSimplePlace(0).limited(true).limit(3).build();
    Program.addCave(caisse);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    Bouteille b = new Bouteille();
    b.setNom("B24");
    b.setNumLieu(0);
    b.setEmplacement("caisse");
    caisse.addObject(b);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    Bouteille b1 = new Bouteille();
    b1.setNom("B25");
    b1.setNumLieu(0);
    b1.setEmplacement("caisse");
    caisse.addObject(b1);
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    Bouteille b2 = new Bouteille();
    b2.setNom("B26");
    b2.setNumLieu(0);
    b2.setEmplacement("caisse");
    caisse.addObject(b2);
    assertFalse(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    caisse.clearStock(b1, b1.getPlace());
    assertTrue(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
    caisse.addObject(b1);
    assertFalse(caisse.hasFreeSpaceInSimplePlace(new Place.PlaceBuilder(caisse).withNumPlace(0).build()));
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
    Rangement r = new Rangement.SimplePlaceBuilder("r").build();
    Rangement r1 = new Rangement.SimplePlaceBuilder("r").build();
    assertTrue(r.isSame(r1));
    LinkedList<Part> list = new LinkedList<>();
    Part partie = new Part(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setCol(1);
    Rangement r2 = new Rangement("r", list);
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
    assertFalse(r2.isSame(r3));
    list = new LinkedList<>();
    partie = new Part(0);
    list.add(partie);
    partie.setRows(2);
    partie.getRow(0).setCol(1);
    partie.getRow(1).setCol(2);
    Rangement r4 = new Rangement("r", list);
    assertFalse(r2.isSame(r4));
    list = new LinkedList<>();
    partie = new Part(0);
    list.add(partie);
    partie.setRows(1);
    partie.getRow(0).setCol(3);
    Rangement r5 = new Rangement("r", list);
    assertFalse(r2.isSame(r5));
  }

  @Test
  void toXml() {
    final String s = armoire1x3x3.toXml();
    final String s1 = caisseLimit.toXml();
    assertEquals("<place name=\"\" IsCaisse=\"false\" NbPlace=\"1\">\n" +
        "<internal-place NbLine=\"3\">\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "<line NbColumn=\"3\"/>\n" +
        "</internal-place>\n" +
        "<name><![CDATA[armoire1x3x3]]></name></place>", s);
    assertEquals("<place name=\"\" IsCaisse=\"true\" NbPlace=\"2\" NumStart=\"1\" NbLimit=\"6\" default=\"false\"><name><![CDATA[caisseLimit]]></name></place>", s1);
  }
}
