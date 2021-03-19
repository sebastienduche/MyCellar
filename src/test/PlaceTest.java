package test;

import mycellar.Program;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaceTest {

  private Rangement caisseNoLimit;
  private Rangement armoire1x3x3;
  private Rangement caisseLimit;

  @BeforeEach
  void setUp() {
    caisseNoLimit = new Rangement.CaisseBuilder("caisseNoLimit").build();
    // Caisse avec 2 emplacements commençant à 1 et limité à 6 bouteilles
    caisseLimit = new Rangement.CaisseBuilder("caisseLimit").nb_emplacement(2).start_caisse(1).limit(true).limite_caisse(6).build();
    armoire1x3x3 = new Rangement.RangementBuilder("armoire1x3x3")
        .nb_emplacement(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    Program.addCave(caisseLimit);
    Program.addCave(caisseNoLimit);
    Program.addCave(armoire1x3x3);
  }

  @Test
  void testSimplePlace() {
    Place placeNoLimit = new Place.PlaceBuilder(caisseNoLimit).withNumPlaces(1, 1).build();
    assertEquals(1, placeNoLimit.getPlaceNum());
    assertEquals(1, placeNoLimit.getPlaceNumValueSimplePlace());
    assertEquals(0, placeNoLimit.getPlaceNumIndex());
    assertEquals(2, placeNoLimit.getPlaceNumIndexForCombo());
    assertEquals(caisseNoLimit, placeNoLimit.getRangement());
    assertTrue(placeNoLimit.isSimplePlace());
  }

  @Test
  void testSimplePlaceLimit() {
    Place placeLimit = new Place.PlaceBuilder(caisseLimit).withNumPlaces(1, 1).build();
    assertEquals(1, placeLimit.getPlaceNum());
    assertEquals(1, placeLimit.getPlaceNumValueSimplePlace());
    assertEquals(0, placeLimit.getPlaceNumIndex());
    assertEquals(1, placeLimit.getPlaceNumIndexForCombo());
    assertEquals(caisseLimit, placeLimit.getRangement());
    assertTrue(placeLimit.isSimplePlace());
  }

  @Test
  void testSimplePlaceLimit2() {
    Place placeLimit2 = new Place.PlaceBuilder(caisseLimit).withNumPlaces(1, 2).build();
    assertEquals(2, placeLimit2.getPlaceNum());
    assertEquals(2, placeLimit2.getPlaceNumValueSimplePlace());
    assertEquals(1, placeLimit2.getPlaceNumIndex());
    assertEquals(1, placeLimit2.getPlaceNumIndexForCombo());
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
}
