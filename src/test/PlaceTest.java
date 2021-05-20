package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.MyCellarException;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceTest {

  private Rangement caisseNoLimit;
  private Rangement armoire1x3x3;
  private Rangement armoire2x3x3;
  private Rangement caisseLimit;

  @BeforeEach
  void setUp() {
    Program.getCave().clear();
    caisseNoLimit = new Rangement.CaisseBuilder("caisseNoLimit").build(); // 1 emplacement : 0
    // Caisse avec 2 emplacements commençant à 1 et limité à 6 bouteilles
    caisseLimit = new Rangement.CaisseBuilder("caisseLimit")
        .nb_emplacement(2) // 1 , 2
        .start_caisse(1)
        .limit(true)
        .limite_caisse(6).build();
    armoire1x3x3 = new Rangement.RangementBuilder("armoire1x3x3")
        .nb_emplacement(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    armoire2x3x3 = new Rangement.RangementBuilder("armoire2x3x3")
        .nb_emplacement(new int[]{3, 1})
        .sameColumnsNumber(new int[]{3, 3})
        .build();
    Program.addCave(caisseLimit);
    Program.addCave(caisseNoLimit);
    Program.addCave(armoire1x3x3);
    Program.addCave(armoire2x3x3);
  }

  @Test
  void testSimplePlace() {
    Place placeNoLimit = new Place.PlaceBuilder(caisseNoLimit).withNumPlace(0).build();
    assertEquals(0, placeNoLimit.getPlaceNum());
    assertEquals(0, placeNoLimit.getPlaceNumIndex());
    assertEquals(caisseNoLimit, placeNoLimit.getRangement());
    assertTrue(placeNoLimit.isSimplePlace());
  }

  @Test
  void testSimplePlaceLimit() {
    Place placeLimit = new Place.PlaceBuilder(caisseLimit).withNumPlace(2).build();
    assertEquals(2, placeLimit.getPlaceNum());
    assertEquals(1, placeLimit.getPlaceNumIndex());
    assertEquals(caisseLimit, placeLimit.getRangement());
    assertTrue(placeLimit.isSimplePlace());
  }

  @Test
  void testArmoire() {
    Place placeArmoire = new Place.PlaceBuilder(armoire1x3x3).withNumPlace(1).withLine(1).withColumn(1).build();
    assertEquals(1, placeArmoire.getPlaceNum());
    assertEquals(0, placeArmoire.getPlaceNumIndex());
    assertEquals(1, placeArmoire.getLine());
    assertEquals(0, placeArmoire.getLineIndex());
    assertEquals(1, placeArmoire.getColumn());
    assertEquals(0, placeArmoire.getColumnIndex());
    assertEquals(armoire1x3x3, placeArmoire.getRangement());
    assertFalse(placeArmoire.isSimplePlace());
  }

  @Test
  void replaceObjectSamePlace() throws MyCellarException {
    final Bouteille b1 = new Bouteille.BouteilleBuilder("b1")
        .place(armoire1x3x3.getNom())
        .numPlace(1)
        .line(1)
        .column(1).build();
    final Bouteille b2 = new Bouteille.BouteilleBuilder("b2")
        .place(armoire1x3x3.getNom())
        .numPlace(1)
        .line(1)
        .column(2).build();
    armoire1x3x3.addWine(b1);
    armoire1x3x3.addWine(b2);
    assertEquals(2, armoire1x3x3.getNbCaseUseAll());
    final Place oldb2Place = b2.getPlace();
    b2.setColonne(1);
    RangementUtils.replaceObject(b1, b2, oldb2Place);
    assertEquals(1, armoire1x3x3.getNbCaseUseAll());
    assertEquals("b2", armoire1x3x3.getBouteille(0, 0, 0).get().getNom());
    assertTrue(armoire1x3x3.getBouteille(0, 0, 1).isEmpty());

    armoire1x3x3.removeWine(b2);
    assertEquals(0, armoire1x3x3.getNbCaseUseAll());
    assertTrue(armoire1x3x3.getBouteille(0, 0, 0).isEmpty());
  }

  @Test
  void replaceObjectDifferentPlace() throws MyCellarException {
    final Bouteille b1 = new Bouteille.BouteilleBuilder("b1")
        .place(armoire1x3x3.getNom())
        .numPlace(1)
        .line(1)
        .column(1).build();
    final Bouteille b2 = new Bouteille.BouteilleBuilder("b2")
        .place(armoire2x3x3.getNom())
        .numPlace(1)
        .line(1)
        .column(1).build();
    armoire1x3x3.addWine(b1);
    armoire2x3x3.addWine(b2);
    assertEquals(1, armoire1x3x3.getNbCaseUseAll());
    assertEquals(1, armoire2x3x3.getNbCaseUseAll());
    final Place oldb2Place = b2.getPlace();
    b2.setEmplacement(armoire1x3x3.getNom());
    RangementUtils.replaceObject(b1, b2, oldb2Place);
    assertEquals(1, armoire1x3x3.getNbCaseUseAll());
    assertEquals(0, armoire2x3x3.getNbCaseUseAll());
    assertEquals("b2", armoire1x3x3.getBouteille(0, 0, 0).get().getNom());
    assertTrue(armoire2x3x3.getBouteille(0, 0, 0).isEmpty());

    armoire1x3x3.removeWine(b2);
    assertEquals(0, armoire1x3x3.getNbCaseUseAll());
    assertTrue(armoire1x3x3.getBouteille(0, 0, 0).isEmpty());
  }
}
