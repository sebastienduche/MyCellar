package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.ComplexPlaceBuilder;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.SimplePlace;
import mycellar.placesmanagement.places.SimplePlaceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacePositionTest {

  private SimplePlace caisseNoLimit;
  private ComplexPlace armoire1x3x3;
  private ComplexPlace armoire2x3x3;
  private SimplePlace caisseLimit;

  @BeforeEach
  void setUp() {
    Program.removePlace(caisseLimit);
    Program.removePlace(caisseNoLimit);
    Program.removePlace(armoire1x3x3);
    Program.removePlace(armoire2x3x3);
    caisseNoLimit = new SimplePlaceBuilder("caisseNoLimit").build(); // 1 emplacement : 0
    // Caisse avec 2 emplacements commencant a 1 et limite a 6 bouteilles
    caisseLimit = new SimplePlaceBuilder("caisseLimit")
        .nbParts(2) // 1 , 2
        .startSimplePlace(1)
        .limited(true)
        .limit(6).build();
    armoire1x3x3 = new ComplexPlaceBuilder("armoire1x3x3")
        .nbParts(new int[]{3})
        .sameColumnsNumber(new int[]{3})
        .build();
    armoire2x3x3 = new ComplexPlaceBuilder("armoire2x3x3")
        .nbParts(new int[]{3, 1})
        .sameColumnsNumber(new int[]{3, 3})
        .build();
    Program.addPlace(caisseLimit);
    Program.addPlace(caisseNoLimit);
    Program.addPlace(armoire1x3x3);
    Program.addPlace(armoire2x3x3);
  }

  @Test
  void testSimplePlace() {
    PlacePosition placeNoLimit = new PlacePosition.PlacePositionBuilder(caisseNoLimit).withNumPlace(0).build();
    PlacePosition placeNoLimit2 = new PlacePosition.PlacePositionBuilderZeroBased(caisseNoLimit).withNumPlace(0).build();
    assertEquals(0, placeNoLimit.getPart());
    assertEquals(0, placeNoLimit.getPlaceNumIndex());
    assertEquals(0, placeNoLimit2.getPart());
    assertEquals(0, placeNoLimit2.getPlaceNumIndex());
    assertEquals(caisseNoLimit, placeNoLimit.getAbstractPlace());
    assertTrue(placeNoLimit.isSimplePlace());
  }

  @Test
  void testSimplePlaceLimit() {
    PlacePosition placeLimit = new PlacePosition.PlacePositionBuilder(caisseLimit).withNumPlace(2).build();
    PlacePosition placeLimit2 = new PlacePosition.PlacePositionBuilderZeroBased(caisseLimit).withNumPlace(1).build();
    assertEquals(2, placeLimit.getPart());
    assertEquals(1, placeLimit.getPlaceNumIndex());
    assertEquals(1, placeLimit2.getPart());
    assertEquals(0, placeLimit2.getPlaceNumIndex());
    assertEquals(caisseLimit, placeLimit.getAbstractPlace());
    assertTrue(placeLimit.isSimplePlace());
  }

  @Test
  void testArmoire() {
    PlacePosition placeArmoire = new PlacePosition.PlacePositionBuilder(armoire1x3x3).withNumPlace(1).withLine(1).withColumn(1).build();
    PlacePosition placeArmoire2 = new PlacePosition.PlacePositionBuilderZeroBased(armoire1x3x3).withNumPlace(0).withLine(0).withColumn(0).build();
    assertEquals(1, placeArmoire.getPart());
    assertEquals(0, placeArmoire.getPlaceNumIndex());
    assertEquals(1, placeArmoire.getLine());
    assertEquals(0, placeArmoire.getLineIndex());
    assertEquals(1, placeArmoire.getColumn());
    assertEquals(0, placeArmoire.getColumnIndex());
    assertEquals(0, placeArmoire2.getPart());
    assertEquals(0, placeArmoire2.getPlaceNumIndex());
    assertEquals(0, placeArmoire2.getLine());
    assertEquals(0, placeArmoire2.getLineIndex());
    assertEquals(0, placeArmoire2.getColumn());
    assertEquals(0, placeArmoire2.getColumnIndex());
    assertEquals(armoire1x3x3, placeArmoire.getAbstractPlace());
    assertFalse(placeArmoire.isSimplePlace());
  }

  @Test
  void replaceObjectSamePlace() throws MyCellarException {
    final Bouteille b1 = new Bouteille.BouteilleBuilder("b1")
        .place(armoire1x3x3.getName())
        .numPlace(1)
        .line(1)
        .column(1).build();
    final Bouteille b2 = new Bouteille.BouteilleBuilder("b2")
        .place(armoire1x3x3.getName())
        .numPlace(1)
        .line(1)
        .column(2).build();
    armoire1x3x3.addObject(b1);
    armoire1x3x3.addObject(b2);
    assertEquals(2, armoire1x3x3.getTotalCountCellUsed());
    final PlacePosition oldb2Place = b2.getPlacePosition();
    b2.setColonne(1);
    PlaceUtils.replaceMyCellarObject(b1, b2, oldb2Place);
    assertEquals(1, armoire1x3x3.getTotalCountCellUsed());
    assertEquals("b2", armoire1x3x3.getObject(new PlacePosition.PlacePositionBuilder(armoire1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(1)
        .build()).get().getNom());
    assertTrue(armoire1x3x3.getObject(new PlacePosition.PlacePositionBuilder(armoire1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(2)
        .build()).isEmpty());

    armoire1x3x3.removeObject(b2);
    assertEquals(0, armoire1x3x3.getTotalCountCellUsed());
    assertTrue(armoire1x3x3.getObject(new PlacePosition.PlacePositionBuilder(armoire1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(1)
        .build()).isEmpty());
  }

  @Test
  void replaceObjectDifferentPlace() throws MyCellarException {
    final Bouteille b1 = new Bouteille.BouteilleBuilder("b1")
        .place(armoire1x3x3.getName())
        .numPlace(1)
        .line(1)
        .column(1).build();
    final Bouteille b2 = new Bouteille.BouteilleBuilder("b2")
        .place(armoire2x3x3.getName())
        .numPlace(1)
        .line(1)
        .column(1).build();
    armoire1x3x3.addObject(b1);
    armoire2x3x3.addObject(b2);
    assertEquals(1, armoire1x3x3.getTotalCountCellUsed());
    assertEquals(1, armoire2x3x3.getTotalCountCellUsed());
    final PlacePosition oldb2Place = b2.getPlacePosition();
    b2.setEmplacement(armoire1x3x3.getName());
    PlaceUtils.replaceMyCellarObject(b1, b2, oldb2Place);
    assertEquals(1, armoire1x3x3.getTotalCountCellUsed());
    assertEquals(0, armoire2x3x3.getTotalCountCellUsed());
    assertEquals("b2", armoire1x3x3.getObject(new PlacePosition.PlacePositionBuilder(armoire1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(1)
        .build()).get().getNom());
    assertTrue(armoire2x3x3.getObject(new PlacePosition.PlacePositionBuilder(armoire2x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(1)
        .build()).isEmpty());

    armoire1x3x3.removeObject(b2);
    assertEquals(0, armoire1x3x3.getTotalCountCellUsed());
    assertTrue(armoire1x3x3.getObject(new PlacePosition.PlacePositionBuilder(armoire1x3x3)
        .withNumPlace(1)
        .withLine(1)
        .withColumn(1)
        .build()).isEmpty());
  }
}
