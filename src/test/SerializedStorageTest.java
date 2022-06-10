package test;

import mycellar.Bouteille;
import mycellar.core.IMyCellarObject;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.storage.SerializedStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SerializedStorageTest {

  private Bouteille bouteille3;
  private Bouteille bouteille4;
  private Bouteille bouteille5;
  private SerializedStorage serializedStorage;

  @BeforeEach
  void setUp() {
    serializedStorage = SerializedStorage.getInstance();
    Bouteille bouteille = new Bouteille.BouteilleBuilder("bouteille")
        .place("place")
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

    Bouteille bouteille1 = new Bouteille.BouteilleBuilder("bouteille1")
        .place("place")
        .numPlace(1)
        .line(1)
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

    Bouteille bouteille2 = new Bouteille.BouteilleBuilder("bouteille2")
        .place("place")
        .numPlace(1)
        .line(2)
        .column(2)
        .type("type")
        .annee("2018")
        .color("Red")
        .comment("comment")
        .maturity("maturity")
        .parker("100")
        .price("123")
        .vignoble("fr", "vignoble", "aoc", "igp")
        .build();

    bouteille3 = new Bouteille.BouteilleBuilder("bouteille3")
        .place("place3")
        .numPlace(1)
        .type("type")
        .annee("2018")
        .color("Red")
        .comment("comment")
        .maturity("maturity")
        .parker("100")
        .price("123")
        .vignoble("fr", "vignoble", "aoc", "igp")
        .build();
    bouteille4 = new Bouteille.BouteilleBuilder("bouteille4")
        .place("place3")
        .numPlace(1)
        .type("type")
        .annee("2018")
        .color("Red")
        .comment("comment")
        .maturity("maturity")
        .parker("100")
        .price("123")
        .vignoble("fr", "vignoble", "aoc", "igp")
        .build();
    bouteille5 = new Bouteille.BouteilleBuilder("bouteille4")
        .place("place3")
        .numPlace(1)
        .type("type")
        .annee("2018")
        .color("Red")
        .comment("comment")
        .maturity("maturity")
        .parker("100")
        .price("123")
        .vignoble("fr", "vignoble", "aoc", "igp")
        .build();
    serializedStorage.getListMyCellarObject().getBouteille().add(bouteille);
    serializedStorage.getListMyCellarObject().getBouteille().add(bouteille1);
    serializedStorage.getListMyCellarObject().getBouteille().add(bouteille2);
    serializedStorage.getListMyCellarObject().getBouteille().add(bouteille3);
    serializedStorage.getListMyCellarObject().getBouteille().add(bouteille4);
    serializedStorage.getListMyCellarObject().getBouteille().add(bouteille5);
    serializedStorage.getListMyCellarObject().getBouteille().add(new Bouteille(bouteille5));
  }

  @Test
  void deleteWine() throws MyCellarException {
    assertEquals(7, serializedStorage.getListMyCellarObject().getBouteille().size());
    serializedStorage.deleteWine(bouteille3);
    assertEquals(6, serializedStorage.getListMyCellarObject().getBouteille().size());
    for (IMyCellarObject b : serializedStorage.getListMyCellarObject().getBouteille()) {
      assertNotEquals("bouteille3", b.getNom());
    }
    final Bouteille bouteille6 = new Bouteille.BouteilleBuilder("bouteille6")
        .place("place3")
        .numPlace(1)
        .type("type")
        .annee("2018")
        .color("Red")
        .comment("comment")
        .maturity("maturity")
        .parker("100")
        .price("123")
        .vignoble("fr", "vignoble", "aoc", "igp")
        .build();
    serializedStorage.addWine(bouteille6);
    final Bouteille bouteille7 = new Bouteille.BouteilleBuilder("bouteille7")
        .place("place")
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
    serializedStorage.addWine(bouteille7);
    assertEquals(8, serializedStorage.getListMyCellarObject().getBouteille().size());
    serializedStorage.deleteWine(bouteille6);
    serializedStorage.deleteWine(bouteille7);
    assertEquals(6, serializedStorage.getListMyCellarObject().getBouteille().size());
    serializedStorage.deleteWine(bouteille4);
    serializedStorage.deleteWine(bouteille5);
    assertEquals(4, serializedStorage.getListMyCellarObject().getBouteille().size());
  }

}
