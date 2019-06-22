package test;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.SerializedStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SerializedStorageTest {

  private Bouteille bouteille;
  private Bouteille bouteille1;
  private Bouteille bouteille2;
  private Bouteille bouteille3;
  private Bouteille bouteille4;
  private Bouteille bouteille5;
  private SerializedStorage serializedStorage;

  @BeforeEach
  void setUp() {
    serializedStorage = SerializedStorage.getInstance();
    bouteille = new Bouteille.BouteilleBuilder("bouteille")
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
        .build();

    bouteille1 = new Bouteille.BouteilleBuilder("bouteille1")
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
        .build();

    bouteille2 = new Bouteille.BouteilleBuilder("bouteille2")
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
        .build();
    serializedStorage.getListBouteilles().getBouteille().add(bouteille);
    serializedStorage.getListBouteilles().getBouteille().add(bouteille1);
    serializedStorage.getListBouteilles().getBouteille().add(bouteille2);
    serializedStorage.getListBouteilles().getBouteille().add(bouteille3);
    serializedStorage.getListBouteilles().getBouteille().add(bouteille4);
    serializedStorage.getListBouteilles().getBouteille().add(bouteille5);
    final LinkedList<Rangement> cave = Program.getCave();
    final Rangement place3 = new Rangement("place3", 10, 0, false, 0);
    cave.add(place3);

  }

  @Test
  void deleteWine() {
    assertEquals(6, serializedStorage.getListBouteilles().getBouteille().size());
    serializedStorage.deleteWine(bouteille3);
    assertEquals(5, serializedStorage.getListBouteilles().getBouteille().size());
    for (Bouteille b : serializedStorage.getListBouteilles().getBouteille()) {
      assertNotEquals("bouteille3", b.getNom());
    }
  }

}
