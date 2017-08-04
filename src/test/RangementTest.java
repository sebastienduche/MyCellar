package test;

import java.util.LinkedList;

import mycellar.Bouteille;
import mycellar.Part;
import mycellar.Rangement;
import junit.framework.TestCase;

public class RangementTest extends TestCase {
	
	Rangement caisseNoLimit;
	Rangement caisseLimit;
	Rangement armoire1x3x3;
	Rangement armoire2x2_3x22545;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		caisseNoLimit = new Rangement("caisseNoLimit");
		// Caisse avec 2 emplacements commençant à 1 et limité à 6 bouteilles
		caisseLimit = new Rangement("caisseLimit",2,1,true,6);
		Part partie = new Part(0);
		LinkedList<Part> list = new LinkedList<Part>();
		list.add(partie);
		partie.setRows(3);
		for(int i=0; i<3; i++)
			partie.getRow(i).setCol(3);
		armoire1x3x3 = new Rangement("armoire1x3x3", list);
		partie = new Part(0);
		list = new LinkedList<Part>();
		list.add(partie);
		partie.setRows(2);
		for(int i=0; i<2; i++)
			partie.getRow(i).setCol(2);
		partie = new Part(1);
		list.add(partie);
		partie.setRows(3);
		partie.getRow(0).setCol(5);
		partie.getRow(1).setCol(4);
		partie.getRow(2).setCol(5);
		armoire2x2_3x22545 = new Rangement("armoire2x2_3x22545", list);
	}

	public void testGetNom() {
		assertEquals("caisseNoLimit", caisseNoLimit.getNom());
	}

	public void testSetNom() {
		Rangement r = new Rangement("test");
		r.setNom("toto");
		assertEquals("toto", r.getNom());
	}

	public void testGetStartCaisse() {
		assertEquals(1, caisseLimit.getStartCaisse());
		assertEquals(0, caisseNoLimit.getStartCaisse());
	}

	public void testSetStartCaisse() {
		Rangement r = new Rangement("test");
		r.setStartCaisse(2);
		assertEquals(2, r.getStartCaisse());
	}

	public void testGetNbEmplacements() {
		assertEquals(1, armoire1x3x3.getNbEmplacements());
		assertEquals(2, armoire2x2_3x22545.getNbEmplacements());
	}

	public void testGetNbColonnesStock() {
		assertEquals(3, armoire1x3x3.getNbColonnesStock());
		assertEquals(5, armoire2x2_3x22545.getNbColonnesStock());
	}

	public void testIsLimited() {
		assertEquals(true, caisseLimit.isLimited());
		assertEquals(false, caisseNoLimit.isLimited());
	}

	public void testSetLimited() {
		Rangement r = new Rangement("test");
		assertEquals(false, r.isLimited());
		r.setLimited(true);
		assertEquals(true, r.isLimited());
	}

	public void testSetNbBottleInCaisse() {
		caisseLimit.setNbBottleInCaisse(50);
		assertEquals(50, caisseLimit.getNbColonnesStock());
		caisseNoLimit.setNbBottleInCaisse(50);
		assertEquals(-1, caisseNoLimit.getNbColonnesStock());
	}

	public void testGetNbLignesInt() {
		assertEquals(3, armoire1x3x3.getNbLignes(0));
		assertEquals(2, armoire2x2_3x22545.getNbLignes(0));
		assertEquals(3, armoire2x2_3x22545.getNbLignes(1));
	}

	public void testIsExistingCell() {
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				assertTrue(armoire1x3x3.isExistingCell(0, i, j));
			}
			assertFalse(armoire1x3x3.isExistingCell(0, i, 3));
		}
		assertFalse(armoire1x3x3.isExistingCell(0, 3, 3));
		assertFalse(armoire1x3x3.isExistingCell(0, 3, 0));
		
		LinkedList<Rangement> list = new LinkedList<Rangement>();
		list.add(armoire1x3x3);
		list.add(armoire2x2_3x22545);
		for(Rangement r : list) {
			int emplacementMax = r.getNbEmplacements();
			for(int i=0; i<emplacementMax; i++) {
				int ligneMax = r.getNbLignes(i);
				for(int j=0; j<ligneMax; j++) {
					int colMax = r.getNbColonnes(i, j);
					for(int k=0; k<colMax; k++) {
						assertTrue(r.isExistingCell(i, j, k));
					}
					assertFalse(r.isExistingCell(i, j, colMax));
				}
				assertFalse(r.isExistingCell(i, ligneMax, 0));
			}
			assertFalse(r.isExistingCell(emplacementMax, 0, 0));
		}
	}

	public void testGetNbColonnesIntInt() {
		assertEquals(3, armoire1x3x3.getNbColonnes(0, 0));
		assertEquals(3, armoire1x3x3.getNbColonnes(0, 1));
		assertEquals(3, armoire1x3x3.getNbColonnes(0, 2));
		assertEquals(2, armoire2x2_3x22545.getNbColonnes(0, 0));
		assertEquals(2, armoire2x2_3x22545.getNbColonnes(0, 1));
		assertEquals(5, armoire2x2_3x22545.getNbColonnes(1, 0));
		assertEquals(4, armoire2x2_3x22545.getNbColonnes(1, 1));
		assertEquals(5, armoire2x2_3x22545.getNbColonnes(1, 2));
	}

	public void testGetNbColonnesMaxInt() {
		assertEquals(3, armoire1x3x3.getNbColonnesMax(0));
		assertEquals(2, armoire2x2_3x22545.getNbColonnesMax(0));
		assertEquals(5, armoire2x2_3x22545.getNbColonnesMax(1));
	}

	public void testGetNbColonnesMax() {
		assertEquals(3, armoire1x3x3.getNbColonnesMax());
		assertEquals(5, armoire2x2_3x22545.getNbColonnesMax());
	}

	public void testGetNbCaseUseLigne() {
		assertEquals(0, armoire1x3x3.getNbCaseUseLigne(0, 0));
		assertEquals(0, armoire2x2_3x22545.getNbCaseUseLigne(0, 0));
		Bouteille b = new Bouteille();
		b.setNom("B1");
		b.setNumLieu(1);
		b.setLigne(1);
		b.setColonne(1);
		b.setEmplacement("armoire1x3x3");
		armoire1x3x3.updateToStock(b);
		assertEquals(1, armoire1x3x3.getNbCaseUseLigne(0, 0));
		Bouteille b1 = new Bouteille();
		b1.setNom("B2");
		b1.setNumLieu(1);
		b1.setLigne(2);
		b1.setColonne(1);
		b1.setEmplacement("armoire1x3x3");
		armoire1x3x3.updateToStock(b1);
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
		b.setNumLieu(2);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b);
		assertEquals(1, armoire2x2_3x22545.getNbCaseUseLigne(1, 0));
		armoire2x2_3x22545.clearStock(b);
		assertEquals(0, armoire2x2_3x22545.getNbCaseUseLigne(1, 0));
	}

	public void testGetNbCaseFreeCoteLigne() {
		LinkedList<Rangement> list = new LinkedList<Rangement>();
		list.add(armoire1x3x3);
		list.add(armoire2x2_3x22545);
		for(Rangement r : list) {
			int emplacementMax = r.getNbEmplacements();
			for(int i=0; i<emplacementMax; i++) {
				int ligneMax = r.getNbLignes(i);
				for(int j=0; j<ligneMax; j++) {
					int colMax = r.getNbColonnes(i, j);
					for(int k=0; k<colMax; k++) {
						assertEquals(colMax - k, r.getNbCaseFreeCoteLigne(i, j, k));
					}
				}
			}
		}
		Bouteille b = new Bouteille();
		b.setNom("B27");
		b.setNumLieu(1);
		b.setLigne(1);
		b.setColonne(1);
		b.setEmplacement("armoire1x3x3");
		armoire1x3x3.updateToStock(b);
		assertEquals(0, armoire1x3x3.getNbCaseFreeCoteLigne(0, 0, 0));
		assertEquals(2, armoire1x3x3.getNbCaseFreeCoteLigne(0, 0, 1));
		Bouteille b1 = new Bouteille();
		b1.setNom("B28");
		b1.setNumLieu(1);
		b1.setLigne(1);
		b1.setColonne(3);
		b1.setEmplacement("armoire1x3x3");
		armoire1x3x3.updateToStock(b1);
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

	public void testGetNbCaseUse() {
		assertEquals(0, armoire1x3x3.getNbCaseUse(0));
		assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
		assertEquals(0, armoire2x2_3x22545.getNbCaseUse(1));
		Bouteille b = new Bouteille();
		b.setNom("B7");
		b.setNumLieu(2);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b);
		assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
		assertEquals(1, armoire2x2_3x22545.getNbCaseUse(1));
		armoire2x2_3x22545.clearStock(b);
		assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
		assertEquals(0, armoire2x2_3x22545.getNbCaseUse(0));
	}

	public void testGetNbCaseUseAll() {
		assertEquals(0, armoire1x3x3.getNbCaseUseAll());
		assertEquals(0, armoire2x2_3x22545.getNbCaseUseAll());
		Bouteille b = new Bouteille();
		b.setNom("B8");
		b.setNumLieu(2);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b);
		assertEquals(1, armoire2x2_3x22545.getNbCaseUseAll());
		Bouteille b1 = new Bouteille();
		b.setNom("B9");
		b1.setNumLieu(1);
		b1.setLigne(2);
		b1.setColonne(2);
		b1.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b1);
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

	public void testGetNbCaseUseForCaisse() {
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

	public void testAddWine() {
		Bouteille b = new Bouteille();
		b.setNom("B12");
		b.setNumLieu(2);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b);
		assertEquals(1, armoire2x2_3x22545.getNbCaseUseLigne(1, 0));
		assertEquals(b, armoire2x2_3x22545.getBouteille(1, 0, 1));
		armoire2x2_3x22545.removeWine(b);
		b.setEmplacement("caisseLimit");
		caisseLimit.addWine(b);
		assertEquals(1, caisseLimit.getNbCaseUse(1));
		caisseLimit.removeWine(b);
		assertEquals(0, caisseLimit.getNbCaseUse(1));
	}
	
	public void testRemoveWine() {
		Bouteille b = new Bouteille();
		b.setNom("B12");
		b.setNumLieu(2);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b);
		assertEquals(1, armoire2x2_3x22545.getNbCaseUseLigne(1, 0));
		assertEquals(b, armoire2x2_3x22545.getBouteille(1, 0, 1));
		armoire2x2_3x22545.removeWine(b);
		caisseLimit.addWine(b);
		assertEquals(1, caisseLimit.getNbCaseUse(1));
		caisseLimit.removeWine(b);
		assertEquals(0, caisseLimit.getNbCaseUse(1));
	}

	public void testUpdateToStock() {
		Bouteille b = new Bouteille();
		b.setNom("B13");
		b.setNumLieu(1);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire1x3x3");
		armoire1x3x3.addWine(b);
		assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1));
		Bouteille b1 = new Bouteille();
		b1.setNom("B14");
		b1.setNumLieu(1);
		b1.setLigne(1);
		b1.setColonne(2);
		b1.setEmplacement("armoire1x3x3");
		armoire1x3x3.updateToStock(b1);
		assertEquals(b1, armoire1x3x3.getBouteille(0, 0, 1));
		armoire1x3x3.clearStock(b1);
		assertEquals(null, armoire1x3x3.getBouteille(0, 0, 1));
		armoire1x3x3.removeWine(b);
	}

	public void testMoveLineWine() {
		Bouteille b = new Bouteille();
		b.setNom("B15");
		b.setNumLieu(1);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire1x3x3");
		armoire1x3x3.addWine(b);
		assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1));
		armoire1x3x3.moveLineWine(b, 2);
		assertEquals(null, armoire1x3x3.getBouteille(0, 0, 1));
		assertEquals(b, armoire1x3x3.getBouteille(0, 1, 1));
	}

	public void testGetBouteille() {
		Bouteille b = new Bouteille();
		b.setNom("B16");
		b.setNumLieu(1);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire1x3x3");
		armoire1x3x3.addWine(b);
		assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1));
		Bouteille b1 = new Bouteille();
		b.setNom("B17");
		b1.setNumLieu(2);
		b1.setLigne(2);
		b1.setColonne(3);
		b1.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b1);
		assertEquals(b1, armoire2x2_3x22545.getBouteille(1, 1, 2));
		armoire1x3x3.clearStock(b);
		armoire2x2_3x22545.clearStock(b1);
		assertEquals(null, armoire1x3x3.getBouteille(0, 0, 1));
		assertEquals(null, armoire2x2_3x22545.getBouteille(1, 1, 2));
	}

	public void testClearStock() {
		Bouteille b = new Bouteille();
		b.setNom("B18");
		b.setNumLieu(1);
		b.setLigne(1);
		b.setColonne(2);
		b.setEmplacement("armoire1x3x3");
		armoire1x3x3.addWine(b);
		assertEquals(b, armoire1x3x3.getBouteille(0, 0, 1));
		Bouteille b1 = new Bouteille();
		b1.setNom("B19");
		b1.setNumLieu(2);
		b1.setLigne(2);
		b1.setColonne(3);
		b1.setEmplacement("armoire2x2_3x22545");
		armoire2x2_3x22545.addWine(b1);
		assertEquals(b1, armoire2x2_3x22545.getBouteille(1, 1, 2));
		armoire1x3x3.clearStock(b);
		armoire2x2_3x22545.clearStock(b1);
		assertEquals(null, armoire1x3x3.getBouteille(0, 0, 1));
		assertEquals(null, armoire2x2_3x22545.getBouteille(1, 1, 2));
		armoire2x2_3x22545.removeWine(b1);
		armoire1x3x3.removeWine(b);
	}

	public void testIsSameColumnNumber() {
		assertTrue(armoire1x3x3.isSameColumnNumber());
		assertFalse(armoire2x2_3x22545.isSameColumnNumber());
	}

	public void testCanAddBottle() {
		Rangement caisse = new Rangement("caisse", 2, 1, true, 1);
		assertTrue(caisse.hasFreeSpaceInCaisse(1));
		Bouteille b = new Bouteille();
		b.setNom("B20");
		b.setNumLieu(1);
		caisse.addWine(b);
		assertFalse(caisse.canAddBottle(0, 0, 0));
		assertTrue(caisse.canAddBottle(1, 0, 0));
		assertTrue(caisse.canAddBottle(b));
		b.setNumLieu(0);
		assertFalse(caisse.canAddBottle(b));
		LinkedList<Rangement> list = new LinkedList<Rangement>();
		list.add(armoire1x3x3);
		list.add(armoire2x2_3x22545);
		for(Rangement r : list) {
			int emplacementMax = r.getNbEmplacements();
			for(int i=0; i<emplacementMax; i++) {
				int ligneMax = r.getNbLignes(i);
				for(int j=0; j<ligneMax; j++) {
					int colMax = r.getNbColonnes(i, j);
					for(int k=0; k<colMax; k++) {
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
		b.setNumLieu(1);
		assertFalse(armoire1x3x3.canAddBottle(b));
		b.setLigne(1);
		assertFalse(armoire1x3x3.canAddBottle(b));
		b.setColonne(1);
		assertTrue(armoire1x3x3.canAddBottle(b));
	}

	public void testHasFreeSpaceInCaisse() {
		Rangement caisse = new Rangement("caisse", 2, 1, true, 1);
		assertTrue(caisse.hasFreeSpaceInCaisse(1));
		Bouteille b = new Bouteille();
		b.setNom("B21");
		b.setNumLieu(1);
		caisse.addWine(b);
		assertFalse(caisse.hasFreeSpaceInCaisse(0));
		assertTrue(caisse.hasFreeSpaceInCaisse(1));
		
	}

	public void testGetFreeNumPlaceInCaisse() {
		assertEquals(1, caisseLimit.getFreeNumPlaceInCaisse());
		assertEquals(0, caisseNoLimit.getFreeNumPlaceInCaisse());
		Rangement caisse = new Rangement("caisse", 2, 1, true, 1);
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

	public void testGetLastNumEmplacement() {
		assertEquals(3, caisseLimit.getLastNumEmplacement());
		assertEquals(1, caisseNoLimit.getLastNumEmplacement());
		assertEquals(1, armoire1x3x3.getLastNumEmplacement());
		assertEquals(2, armoire2x2_3x22545.getLastNumEmplacement());
	}
	
	public void testComplexCaisse() {
		Rangement caisse = new Rangement("caisse", 1, 0, true, 3);
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
	
	public void testIsSame() {
		assertTrue(armoire1x3x3.isSame(armoire1x3x3));
		assertTrue(armoire2x2_3x22545.isSame(armoire2x2_3x22545));
		assertFalse(armoire1x3x3.isSame(armoire2x2_3x22545));
		Rangement r = new Rangement("r");
		Rangement r1 = new Rangement("r");
		assertTrue(r.isSame(r1));
		LinkedList<Part> list = new LinkedList<Part>();
		Part partie = new Part(0);
		list.add(partie);
		partie.setRows(1);
		partie.getRow(0).setCol(1);
		Rangement r2 = new Rangement("r", list);
		list = new LinkedList<Part>();
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
		list = new LinkedList<Part>();
		partie = new Part(0);
		list.add(partie);
		partie.setRows(2);
		partie.getRow(0).setCol(1);
		partie.getRow(1).setCol(2);
		Rangement r4 = new Rangement("r", list);
		assertFalse(r2.isSame(r4));
		list = new LinkedList<Part>();
		partie = new Part(0);
		list.add(partie);
		partie.setRows(1);
		partie.getRow(0).setCol(3);
		Rangement r5 = new Rangement("r", list);
		assertFalse(r2.isSame(r5));
	}


	/*public void testPutTabStock() {
		fail("Not yet implemented");
	}*/

}
