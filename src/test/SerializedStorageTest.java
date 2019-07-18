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
    serializedStorage.getListBouteilles().getBouteille().add(new Bouteille(bouteille5));
    final LinkedList<Rangement> cave = Program.getCave();
    final Rangement caisse = new Rangement.CaisseBuilder("place3").nb_emplacement(10).build();
    final Rangement place = new Rangement.RangementBuilder("place").nb_emplacement(new int[] {3}).sameColumnsNumber(new int[] {3}).build();
    cave.add(caisse);
    cave.add(place);

  }

  @Test
  void deleteWine() {
    assertEquals(7, serializedStorage.getListBouteilles().getBouteille().size());
    serializedStorage.deleteWine(bouteille3);
    assertEquals(6, serializedStorage.getListBouteilles().getBouteille().size());
    for (Bouteille b : serializedStorage.getListBouteilles().getBouteille()) {
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
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
        .vignoble("fr", "vignoble", "aoc", "igp", "aop")
        .build();
    serializedStorage.addWine(bouteille7);
    assertEquals(8, serializedStorage.getListBouteilles().getBouteille().size());
    serializedStorage.deleteWine(bouteille6);
    serializedStorage.deleteWine(bouteille7);
    assertEquals(6, serializedStorage.getListBouteilles().getBouteille().size());
    serializedStorage.deleteWine(bouteille4);
    serializedStorage.deleteWine(bouteille5);
    assertEquals(4, serializedStorage.getListBouteilles().getBouteille().size());
  }

}
