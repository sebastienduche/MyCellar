package test;

import mycellar.Bouteille;
import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.Rangement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

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
    caisseNoLimit = new Rangement.CaisseBuilder("caisseNoLimit").build();
    // Caisse avec 2 emplacements commençant à 1 et limité à 6 bouteilles
    caisseLimit = new Rangement.CaisseBuilder("caisseLimit").nb_emplacement(2).start_caisse(1).limit(true).limite_caisse(6).build();
    Part partie = new Part(0);
    LinkedList<Part> list = new LinkedList<>();
    list.add(partie);
    partie.setRows(3);
    for(int i=0; i<3; i++) {
      partie.getRow(i).setCol(3);
    }
    armoire1x3x3 = new Rangement("armoire1x3x3", list);
    armoire1x3x3Builder = new Rangement.RangementBuilder("armoire1x3x3")
        .nb_emplacement(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    partie = new Part(0);
    list = new LinkedList<>();
    list.add(partie);
    partie.setRows(2);
    for(int i=0; i<2; i++) {
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
          .nb_emplacement(new int[]{2,3})
          .differentColumnsNumber()
          .columnsNumberForPart(0, new int[]{2,2})
          .columnsNumberForPart(1, new int[]{5,4,5})
          .build();
    } catch (Exception ignored) {
    }
    rangement = new Rangement.CaisseBuilder("test").build();
  }

  @Test
  void getNom() {
    assertEquals("caisseNoLimit", caisseNoLimit.getNom());
  }

  @Test
  void setNom() {
    rangement.setNom("toto");
    assertEquals("toto", rangement.getNom());
  }

  @Test
  void getStartCaisse() {
    assertEquals(1, caisseLimit.getStartCaisse());
    assertEquals(0, caisseNoLimit.getStartCaisse());
  }

  @Test
  void setStartCaisse() {
    rangement.setStartCaisse(2);
    assertEquals(2, rangement.getStartCaisse());
  }

  @Test
  void getNbEmplacements() {
    assertEquals(1, armoire1x3x3.getNbEmplacements());
    assertEquals(1, armoire1x3x3Builder.getNbEmplacements());
    assertEquals(2, armoire2x2_3x22545.getNbEmplacements());
    assertEquals(2, armoire2x2_3x22545Builder.getNbEmplacements());
  }

  @Test
  void getNbColonnesStock() {
    assertEquals(3, armoire1x3x3.getNbColonnesStock());
    assertEquals(3, armoire1x3x3Builder.getNbColonnesStock());
    assertEquals(5, armoire2x2_3x22545.getNbColonnesStock());
    assertEquals(5, armoire2x2_3x22545Builder.getNbColonnesStock());
  }

  @Test
  void isLimited() {
    assertTrue(caisseLimit.isLimited());
    assertFalse(caisseNoLimit.isLimited());
  }

  @Test
  void setLimited() {
    assertFalse(rangement.isLimited());
    rangement.setLimited(true);
    assertTrue(rangement.isLimited());
  }

  @Test
  void setNbBottleInCaisse() {
    caisseLimit.setNbBottleInCaisse(50);
    assertEquals(50, caisseLimit.getNbColonnesStock());
    caisseNoLimit.setNbBottleInCaisse(50);
    assertEquals(-1, caisseNoLimit.getNbColonnesStock());
  }

  @Test
  void getNbLignesInt() {
    assertEquals(3, armoire1x3x3.getNbLignes(0));
    assertEquals(3, armoire1x3x3Builder.getNbLignes(0));
    assertEquals(2, armoire2x2_3x22545.getNbLignes(0));
    assertEquals(2, armoire2x2_3x22545Builder.getNbLignes(0));
    assertEquals(3, armoire2x2_3x22545.getNbLignes(1));
    assertEquals(3, armoire2x2_3x22545Builder.getNbLignes(1));
  }

  @Test
  void isExistingCell() {
    for(int i=0; i<3; i++) {
      for(int j=0; j<3; j++) {
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
      int emplacementMax = r.getNbEmplacements();
      for (int i=0; i<emplacementMax; i++) {
        int ligneMax = r.getNbLignes(i);
        for (int j=0; j<ligneMax; j++) {
          int colMax = r.getNbColonnes(i, j);
          for (int k=0; k<colMax; k++) {
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
    assertEquals(3, armoire1x3x3.getNbColonnes(0, 0));
    assertEquals(3, armoire1x3x3Builder.getNbColonnes(0, 0));
    assertEquals(3, armoire1x3x3.getNbColonnes(0, 1));
    assertEquals(3, armoire1x3x3Builder.getNbColonnes(0, 1));
    assertEquals(3, armoire1x3x3.getNbColonnes(0, 2));
    assertEquals(3, armoire1x3x3Builder.getNbColonnes(0, 2));
    assertEquals(2, armoire2x2_3x22545.getNbColonnes(0, 0));
    assertEquals(2, armoire2x2_3x22545Builder.getNbColonnes(0, 0));
    assertEquals(2, armoire2x2_3x22545.getNbColonnes(0, 1));
    assertEquals(2, armoire2x2_3x22545Builder.getNbColonnes(0, 1));
    assertEquals(5, armoire2x2_3x22545.getNbColonnes(1, 0));
    assertEquals(5, armoire2x2_3x22545Builder.getNbColonnes(1, 0));
    assertEquals(4, armoire2x2_3x22545.getNbColonnes(1, 1));
    assertEquals(4, armoire2x2_3x22545Builder.getNbColonnes(1, 1));
    assertEquals(5, armoire2x2_3x22545.getNbColonnes(1, 2));
    assertEquals(5, armoire2x2_3x22545Builder.getNbColonnes(1, 2));
  }

  @Test
  void getNbColonnesMaxInt() {
    assertEquals(3, armoire1x3x3.getNbColonnesMax(0));
    assertEquals(3, armoire1x3x3Builder.getNbColonnesMax(0));
    assertEquals(2, armoire2x2_3x22545.getNbColonnesMax(0));
    assertEquals(2, armoire2x2_3x22545Builder.getNbColonnesMax(0));
    assertEquals(5, armoire2x2_3x22545.getNbColonnesMax(1));
    assertEquals(5, armoire2x2_3x22545Builder.getNbColonnesMax(1));
  }

  @Test
  void getNbColonnesMax() {
    assertEquals(3, armoire1x3x3.getNbColonnesMax());
    assertEquals(3, armoire1x3x3Builder.getNbColonnesMax());
    assertEquals(5, armoire2x2_3x22545.getNbColonnesMax());
    assertEquals(5, armoire2x2_3x22545Builder.getNbColonnesMax());
  }

  @Test
  void getNbCaseUseLigne() {
    assertEquals(0, armoire1x3x3.getNbCaseUseLigne(0, 0));
    assertEquals(0, armoire1x3x3Builder.getNbCaseUseLigne(0, 0));
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseLigne(0, 0));
    assertEquals(0, armoire2x2_3x22545Builder.getNbCaseUseLigne(0, 0));
    Bouteille b = new Bouteille();
    b.setNom("B1");
    updateToArmoire1x3x3(b, 1, 1);
    assertEquals(1, armoire1x3x3.getNbCaseUseLigne(0, 0));
    Bouteille b1 = new Bouteille();
    b1.setNom("B2");
    updateToArmoire1x3x3(b1, 2, 1);
    assertEquals(1, armoire1x3x3.getNbCaseUseLigne(0, 1));
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
    assertEquals(2, armoire1x3x3.getNbCaseUseLigne(0, 2));
    armoire1x3x3.clearStock(b);
    armoire1x3x3.clearStock(b1);
    armoire1x3x3.clearStock(b2);
    armoire1x3x3.clearStock(b3);
    assertEquals(0, armoire1x3x3.getNbCaseUseLigne(0, 0));
    assertEquals(0, armoire1x3x3.getNbCaseUseLigne(0, 1));
    assertEquals(0, armoire1x3x3.getNbCaseUseLigne(0, 2));
    b = new Bouteille();
    b.setNom("B5");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseLigne(1, 0));
    armoire2x2_3x22545.clearStock(b);
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseLigne(1, 0));
  }

  private void updateToArmoire(Bouteille b, int numLieu, int ligne, int colonne, String armoire, Rangement place) {
    b.setNumLieu(numLieu);
    b.setLigne(ligne);
    b.setColonne(colonne);
    b.setEmplacement(armoire);
    place.addWine(b);
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
      int emplacementMax = r.getNbEmplacements();
      for (int i=0; i<emplacementMax; i++) {
        int ligneMax = r.getNbLignes(i);
        for (int j=0; j<ligneMax; j++) {
          int colMax = r.getNbColonnes(i, j);
          for (int k=0; k<colMax; k++) {
            assertEquals(colMax - k, r.getNbCaseFreeCoteLigne(i, j, k));
          }
        }
      }
    }
    Bouteille b = new Bouteille();
    b.setNom("B27");
    updateToArmoire1x3x3(b, 1, 1);
    assertEquals(0, armoire1x3x3.getNbCaseFreeCoteLigne(0, 0, 0));
    assertEquals(2, armoire1x3x3.getNbCaseFreeCoteLigne(0, 0, 1));
    Bouteille b1 = new Bouteille();
    b1.setNom("B28");
    updateToArmoire1x3x3(b1, 1, 3);
    assertEquals(1, armoire1x3x3.getNbCaseFreeCoteLigne(0, 0, 1));
    armoire1x3x3.clearStock(b);
    armoire1x3x3.clearStock(b1);
    b = new Bouteille();
    b.setNom("B6");
    b.setNumLieu(2);
    b.setLigne(1);
    b.setColonne(2);
    armoire2x2_3x22545.addWine(b);
    assertEquals(1, armoire2x2_3x22545.getNbCaseFreeCoteLigne(1, 0, 0));
    assertEquals(3, armoire2x2_3x22545.getNbCaseFreeCoteLigne(1, 0, 2));
    armoire2x2_3x22545.clearStock(b);
    assertEquals(5, armoire2x2_3x22545.getNbCaseFreeCoteLigne(1, 0, 0));
  }

  @Test
  void getNbCaseUse() {
    assertEquals(0, armoire1x3x3.getNbCaseUse(0));
    assertEquals(0, armoire1x3x3Builder.getNbCaseUse(0));
    assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
    assertEquals(0, armoire2x2_3x22545Builder.getNbCaseUse(0));
    assertEquals(0, armoire2x2_3x22545.getNbCaseUse(1));
    assertEquals(0, armoire2x2_3x22545Builder.getNbCaseUse(1));
    Bouteille b = new Bouteille();
    b.setNom("B7");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
    assertEquals(1, armoire2x2_3x22545.getNbCaseUse(1));
    armoire2x2_3x22545.clearStock(b);
    assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
    assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
  }

  @Test
  void getNbCaseUseAll() {
    assertEquals(0, armoire1x3x3.getNbCaseUseAll());
    assertEquals(0, armoire1x3x3Builder.getNbCaseUseAll());
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseAll());
    assertEquals(0, armoire2x2_3x22545Builder.getNbCaseUseAll());
    Bouteille b = new Bouteille();
    b.setNom("B8");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseAll());
    Bouteille b1 = new Bouteille();
    b.setNom("B9");
    updateToArmoire(b1, 1, 2, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(2, armoire2x2_3x22545.getNbCaseUseAll());
    armoire2x2_3x22545.removeWine(b);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseAll());
    armoire2x2_3x22545.removeWine(b1);
    assertEquals(0, armoire2x2_3x22545.getNbCaseUseAll());
    assertEquals(0, caisseLimit.getNbCaseUseAll());
    assertEquals(0, caisseNoLimit.getNbCaseUseAll());
    Bouteille b2 = new Bouteille();
    b2.setNom("B29");
    b2.setNumLieu(2);
    b2.setEmplacement("caisseLimit");
    caisseLimit.addWine(b2);
    assertEquals(1, caisseLimit.getNbCaseUseAll());
    Bouteille b3 = new Bouteille();
    b3.setNom("B30");
    b3.setNumLieu(0);
    b3.setEmplacement("caisseNoLimit");
    caisseNoLimit.addWine(b3);
    assertEquals(1, caisseNoLimit.getNbCaseUseAll());
    caisseLimit.removeWine(b2);
    assertEquals(0, caisseLimit.getNbCaseUseAll());
    caisseNoLimit.removeWine(b3);
    assertEquals(0, caisseNoLimit.getNbCaseUseAll());
  }

  @Test
  void getNbCaseUseForCaisse() {
    assertEquals(0, caisseLimit.getNbCaseUse(1));
    assertEquals(0, caisseNoLimit.getNbCaseUse(0));
    Bouteille b = new Bouteille();
    b.setNom("B10");
    b.setNumLieu(2);
    b.setEmplacement("caisseLimit");
    caisseLimit.addWine(b);
    assertEquals(1, caisseLimit.getNbCaseUse(1));
    Bouteille b1 = new Bouteille();
    b1.setNom("B11");
    b1.setNumLieu(0);
    b1.setEmplacement("caisseNoLimit");
    caisseNoLimit.addWine(b1);
    assertEquals(1, caisseNoLimit.getNbCaseUse(0));
    caisseLimit.removeWine(b);
    assertEquals(0, caisseLimit.getNbCaseUse(1));
    caisseNoLimit.removeWine(b1);
    assertEquals(0, caisseNoLimit.getNbCaseUse(0));
  }

  @Test
  void addWine() {
    Bouteille b = addAndRemoveBottle();
    b.setEmplacement("caisseLimit");
    caisseLimit.addWine(b);
    assertEquals(1, caisseLimit.getNbCaseUse(1));
    caisseLimit.removeWine(b);
    assertEquals(0, caisseLimit.getNbCaseUse(1));
  }

  private Bouteille addAndRemoveBottle() {
    Bouteille b = new Bouteille();
    b.setNom("B12");
    updateToArmoire(b, 2, 1, 2, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(1, armoire2x2_3x22545.getNbCaseUseLigne(1, 0));
    assertEquals(b, armoire2x2_3x22545.getBouteille(1, 0, 1).get());
    assertEquals(b, armoire2x2_3x22545.getBouteille(b).get());
    armoire2x2_3x22545.removeWine(b);
    return b;
  }

  @Test
  void removeWine() {
    Bouteille b = addAndRemoveBottle();
    caisseLimit.addWine(b);
    assertEquals(1, caisseLimit.getNbCaseUse(1));
    caisseLimit.removeWine(b);
    assertEquals(0, caisseLimit.getNbCaseUse(1));
  }

  @Test
  void updateToStock() {
    Bouteille b = new Bouteille();
    b.setNom("B13");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1).get());
    assertEquals(b, armoire1x3x3.getBouteille(b).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B14");
    updateToArmoire1x3x3(b1, 1, 2);
    assertEquals(b1, armoire1x3x3.getBouteille(0, 0, 1).get());
    assertEquals(b1, armoire1x3x3.getBouteille(b1).get());
    armoire1x3x3.clearStock(b1);
    assertTrue(armoire1x3x3.getBouteille(0, 0, 1).isEmpty());
    armoire1x3x3.removeWine(b);
  }

  @Test
  void moveLineWine() {
    Bouteille b = new Bouteille();
    b.setNom("B15");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1).get());
    armoire1x3x3.moveLineWine(b, 2);
    assertTrue(armoire1x3x3.getBouteille(0, 0, 1).isEmpty());
    assertEquals(b, armoire1x3x3.getBouteille(0, 1, 1).get());
  }

  @Test
  void getBouteille() {
    Bouteille b = new Bouteille();
    b.setNom("B16");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1).get());
    Bouteille b1 = new Bouteille();
    b.setNom("B17");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(b1, armoire2x2_3x22545.getBouteille(1, 1, 2).get());
    armoire1x3x3.clearStock(b);
    armoire2x2_3x22545.clearStock(b1);
    assertTrue(armoire1x3x3.getBouteille(0, 0, 1).isEmpty());
    assertTrue(armoire2x2_3x22545.getBouteille(1, 1, 2).isEmpty());
  }

  @Test
  void clearStock() {
    Bouteille b = new Bouteille();
    b.setNom("B18");
    updateToArmoire(b, 1, 1, 2, "armoire1x3x3", armoire1x3x3);
    assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1).get());
    Bouteille b1 = new Bouteille();
    b1.setNom("B19");
    updateToArmoire(b1, 2, 2, 3, "armoire2x2_3x22545", armoire2x2_3x22545);
    assertEquals(b1, armoire2x2_3x22545.getBouteille(1, 1, 2).get());
    armoire1x3x3.clearStock(b);
    armoire2x2_3x22545.clearStock(b1);
    assertTrue(armoire1x3x3.getBouteille(0, 0, 1).isEmpty());
    assertTrue(armoire2x2_3x22545.getBouteille(1, 1, 2).isEmpty());
    armoire2x2_3x22545.removeWine(b1);
    armoire1x3x3.removeWine(b);
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
    Rangement caisse = new Rangement.CaisseBuilder("caisse").nb_emplacement(2).start_caisse(1).limit(true).limite_caisse(1).build();
    assertTrue(caisse.hasFreeSpaceInCaisse(1));
    Bouteille b = new Bouteille();
    b.setNom("B20");
    b.setNumLieu(1);
    caisse.addWine(b);
    assertFalse(caisse.canAddBottle(0, 0, 0));
    assertTrue(caisse.canAddBottle(1, 0, 0));
    assertFalse(caisse.canAddBottle(b));
    b.setNumLieu(0);
    assertFalse(caisse.canAddBottle(b));
    b.setNumLieu(2);
    assertTrue(caisse.canAddBottle(b));
    LinkedList<Rangement> list = new LinkedList<>();
    list.add(armoire1x3x3);
    list.add(armoire1x3x3Builder);
    list.add(armoire2x2_3x22545);
    list.add(armoire2x2_3x22545Builder);
    for (Rangement r : list) {
      int emplacementMax = r.getNbEmplacements();
      for (int i=0; i<emplacementMax; i++) {
        int ligneMax = r.getNbLignes(i);
        for (int j=0; j<ligneMax; j++) {
          int colMax = r.getNbColonnes(i, j);
          for (int k=0; k<colMax; k++) {
            assertTrue(r.canAddBottle(i, j, k));
          }
          assertFalse(r.canAddBottle(i, j, colMax));
        }
        assertFalse(r.canAddBottle(i, ligneMax, 0));
      }
      assertFalse(r.canAddBottle(emplacementMax, 0, 0));
    }
    b = new Bouteille();
    b.setNom("B20bis");
    assertFalse(armoire1x3x3.canAddBottle(b));
    assertFalse(armoire1x3x3Builder.canAddBottle(b));
    b.setNumLieu(1);
    assertFalse(armoire1x3x3.canAddBottle(b));
    assertFalse(armoire1x3x3Builder.canAddBottle(b));
    b.setLigne(1);
    assertFalse(armoire1x3x3.canAddBottle(b));
    assertFalse(armoire1x3x3Builder.canAddBottle(b));
    b.setColonne(1);
    assertTrue(armoire1x3x3.canAddBottle(b));
    assertTrue(armoire1x3x3Builder.canAddBottle(b));
  }

  @Test
  void putTabStock() {
    Rangement caisse = new Rangement.CaisseBuilder("caisse").nb_emplacement(2).start_caisse(1).limit(true).limite_caisse(2).build();
    assertTrue(caisse.hasFreeSpaceInCaisse(0));
    Bouteille b = new Bouteille();
    b.setNom("B20");
    b.setNumLieu(1);
    caisse.addWine(b);
    assertTrue(caisse.hasFreeSpaceInCaisse(0));
    assertTrue(caisse.getNbCaseUse(0) == 1);
    assertTrue(caisse.canAddBottle(0, 0, 0));
    assertTrue(caisse.canAddBottle(1, 0, 0));
    assertTrue(caisse.canAddBottle(b));
    assertTrue(caisse.hasFreeSpaceInCaisse(b.getNumLieu() - caisse.getStartCaisse()));
    b.setNumLieu(0);
    assertFalse(caisse.canAddBottle(b));
  }

  @Test
  void hasFreeSpaceInCaisse() {
    Rangement caisse = new Rangement.CaisseBuilder("caisse").nb_emplacement(2).start_caisse(1).limit(true).limite_caisse(1).build();
    assertTrue(caisse.hasFreeSpaceInCaisse(1));
    Bouteille b = new Bouteille();
    b.setNom("B21");
    b.setNumLieu(1);
    assertTrue(caisse.canAddBottle(b));
    caisse.addWine(b);
    assertFalse(caisse.canAddBottle(b));
    assertFalse(caisse.hasFreeSpaceInCaisse(0));
    assertTrue(caisse.hasFreeSpaceInCaisse(1));
  }

  @Test
  void getFreeNumPlaceInCaisse() {
    assertEquals(1, caisseLimit.getFreeNumPlaceInCaisse());
    assertEquals(0, caisseNoLimit.getFreeNumPlaceInCaisse());
    Rangement caisse = new Rangement.CaisseBuilder("caisse").nb_emplacement(2).start_caisse(1).limit(true).limite_caisse(1).build();
    assertTrue(caisse.hasFreeSpaceInCaisse(1));
    Bouteille b = new Bouteille();
    b.setNom("B22");
    b.setNumLieu(1);
    caisse.addWine(b);
    assertEquals(2, caisse.getFreeNumPlaceInCaisse());
    b = new Bouteille();
    b.setNom("B23");
    b.setNumLieu(2);
    caisse.addWine(b);
    assertEquals(-1, caisse.getFreeNumPlaceInCaisse());
  }

  @Test
  void getLastNumEmplacement() {
    assertEquals(3, caisseLimit.getLastNumEmplacement());
    assertEquals(1, caisseNoLimit.getLastNumEmplacement());
    assertEquals(1, armoire1x3x3.getLastNumEmplacement());
    assertEquals(1, armoire1x3x3Builder.getLastNumEmplacement());
    assertEquals(2, armoire2x2_3x22545.getLastNumEmplacement());
    assertEquals(2, armoire2x2_3x22545Builder.getLastNumEmplacement());
  }

  @Test
  void complexCaisse() {
    Rangement caisse = new Rangement.CaisseBuilder("caisse").nb_emplacement(1).start_caisse(0).limit(true).limite_caisse(3).build();
    assertTrue(caisse.hasFreeSpaceInCaisse(0));
    Bouteille b = new Bouteille();
    b.setNom("B24");
    b.setNumLieu(0);
    b.setEmplacement("caisse");
    caisse.addWine(b);
    assertTrue(caisse.hasFreeSpaceInCaisse(0));
    Bouteille b1 = new Bouteille();
    b1.setNom("B25");
    b1.setNumLieu(0);
    b1.setEmplacement("caisse");
    caisse.addWine(b1);
    assertTrue(caisse.hasFreeSpaceInCaisse(0));
    Bouteille b2 = new Bouteille();
    b2.setNom("B26");
    b2.setNumLieu(0);
    b2.setEmplacement("caisse");
    caisse.addWine(b2);
    assertFalse(caisse.hasFreeSpaceInCaisse(0));
    caisse.clearStock(b1);
    assertTrue(caisse.hasFreeSpaceInCaisse(0));
    caisse.addWine(b1);
    assertFalse(caisse.hasFreeSpaceInCaisse(0));
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
    Rangement r = new Rangement.CaisseBuilder("r").build();
    Rangement r1 = new Rangement.CaisseBuilder("r").build();
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
}
