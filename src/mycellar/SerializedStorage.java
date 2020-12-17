package mycellar;

import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetData;
import mycellar.core.datas.worksheet.WorkSheetList;
import mycellar.placesmanagement.Rangement;
import mycellar.vignobles.CountryVignobleController;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 6.3
 * @since 17/12/20
 */

public class SerializedStorage implements Storage {

	private static final String HISTORY_XML = "history.xml";
	private static final String WORKSHEET_XML = "worksheet.xml";
	private static final HistoryList HISTORY_LIST = new HistoryList();
	private static final WorkSheetList WORKSHEET_LIST = new WorkSheetList();
	private ListeBouteille listBouteilles = new ListeBouteille();

	private int bottleCount;

	private final LinkedList<String> listeUniqueBouteille = new LinkedList<>(); // Liste des noms de bouteille (un seule nom)

	private SerializedStorage() {
	}

	private static class SerializedStorageHolder {
		private static final SerializedStorage INSTANCE = new SerializedStorage();
	}

	public static SerializedStorage getInstance() {
		return SerializedStorageHolder.INSTANCE;
	}

	@Override
	public void setListBouteilles(ListeBouteille listBouteilles) {
		this.listBouteilles = listBouteilles;
		bottleCount = listBouteilles.getBouteille().size();
		listeUniqueBouteille.clear();
		if (this.listBouteilles.bouteille == null) {
			this.listBouteilles.bouteille = new LinkedList<>();
		}
		for (Bouteille b : this.listBouteilles.bouteille) {
			if (!listeUniqueBouteille.contains(b.getNom())) {
				listeUniqueBouteille.add(b.getNom());
			}
		}
	}

	public int getBottleCount() {
		return bottleCount;
	}

	@Override
	public void addBouteilles(ListeBouteille listBouteilles) {
		this.listBouteilles.getBouteille().addAll(listBouteilles.getBouteille());
		bottleCount = listBouteilles.getBouteille().size();
		for (Bouteille b : listBouteilles.bouteille) {
			final List<History> theBottle = HISTORY_LIST.getHistory().stream().filter(history -> history.getBouteille().getId() == b.getId()).collect(Collectors.toList());
			if (b.updateID() && !theBottle.isEmpty()) {
				theBottle.get(0).getBouteille().setId(b.getId());
			}
			if (!listeUniqueBouteille.contains(b.getNom())) {
				listeUniqueBouteille.add(b.getNom());
			}
		}
	}

	@Override
	public ListeBouteille getListBouteilles() {
		return listBouteilles;
	}

	@Override
	public LinkedList<String> getBottleNames() {
		return listeUniqueBouteille;
	}


	@Override
	public void addHistory(HistoryState type, Bouteille bottle) {
		Program.setModified();
		HISTORY_LIST.addLast(new History(bottle, type.ordinal(), getBottleCount()));
	}

	@Override
	public void addToWorksheet(Bouteille bottle) {
		Program.setModified();
		WORKSHEET_LIST.add(new WorkSheetData(bottle));
	}

	@Override
	public void clearHistory(HistoryState historyState) {
		Debug("Program: Clearing history: " + historyState);
		String sValue;
		switch (historyState) {
			case ALL:
				sValue = Program.getError("Error182");
				break;
			case ADD:
				sValue = Program.getError("Error189");
				break;
			case MODIFY:
				sValue = Program.getError("Error191");
				break;
			case DEL:
				sValue = Program.getError("Error190");
				break;
			case VALIDATED:
				sValue = Program.getError("Error.HistoryValidatedDelete");
				break;
			case TOCHECK:
				sValue = Program.getError("Error.HistoryToCheckDelete");
				break;
			default:
				sValue = "";
		}

		if( JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, sValue, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
			return;
		}

		Program.setModified();
		if(historyState == HistoryState.ALL) {
			HISTORY_LIST.clear();
			return;
		}
		final List<History> list = HISTORY_LIST.getHistory().stream().filter(history -> history.getType() == historyState.ordinal()).collect(Collectors.toList());

		// Suppression de l'historique
		for (History h : list) {
			removeHistory(h);
		}
	}

	@Override
	public void removeHistory(History oB) {
		HISTORY_LIST.remove(oB);
	}

	@Override
	public boolean deleteWine(Bouteille bottle) {

		final String nom = bottle.getNom();
		final String annee = bottle.getAnnee();
		final String emplacement = bottle.getEmplacement();
		final int numLieu = bottle.getNumLieu();
		final int ligne = bottle.getLigne();
		final int colonne = bottle.getColonne();

		Debug("DeleteWine: Trying deleting bottle " + nom.strip() + " " + annee + " " + emplacement.strip() + " " + numLieu + " " + ligne + " " + colonne);
		Rangement rangement = Program.getCave(emplacement);
		boolean isCaisse = rangement == null || rangement.isCaisse();
		final List<Bouteille> resultBouteilles = listBouteilles.getBouteille().stream().filter(
				bouteille -> emplacement.equals(bouteille.getEmplacement())
						&& nom.equals(bouteille.getNom())
						&& numLieu == bouteille.getNumLieu()
						&& (isCaisse ? annee.equals(bouteille.getAnnee()) : (ligne == bouteille.getLigne() && colonne == bouteille.getColonne()))).collect(Collectors.toList());
		if (resultBouteilles.isEmpty()) {
			Debug("DeleteWine: Unable to find the wine!");
			return false;
		}
		Program.setModified();
		final Bouteille bouteille = resultBouteilles.get(0);
		Debug("DeleteWine: Deleted bottle " + bouteille);
		listBouteilles.getBouteille().remove(bouteille);
		bottleCount--;
		return true;
	}

	@Override
	public boolean addWine(Bouteille wine) {
		if (null == wine) {
			return false;
		}

		wine.setModified();

		Debug("AddWine: Adding bottle " + wine.getNom() + " " + wine.getAnnee() + " " + wine.getEmplacement() + " " + wine.getNumLieu() + " " + wine.getLigne() + " " + wine.getColonne());

		Program.setModified();

		if (!listeUniqueBouteille.contains(wine.getNom())) {
			listeUniqueBouteille.add(wine.getNom());
		}
		CountryVignobleController.addVignobleFromBottle(wine);
		bottleCount++;
		return listBouteilles.getBouteille().add(wine);
	}

	@Override
	public LinkedList<Bouteille> getAllList() {
		return listBouteilles.getBouteille();
	}

	@Override
	public int getBottlesCount() {
		return listBouteilles.getBouteille().size();
	}

	private static void Debug(String sText) {
		Program.Debug("SerializedStorage: " + sText);
	}

	@Override
	public void saveHistory() {
		Debug("Saving History...");
		HistoryList.writeXML(new File(Program.getWorkDir(true) + HISTORY_XML));
		Debug("Saving History OK");
	}

	@Override
	public void loadHistory() {
		Debug ("Loading History...");
		boolean resul = HistoryList.loadXML(new File(Program.getWorkDir(true) + HISTORY_XML));
		if (!resul) {
			HISTORY_LIST.clear();
			Debug("Loading History KO");
		} else {
			Debug("Loading History OK");
		}
	}

	@Override
	public HistoryList getHistoryList() {
		return HISTORY_LIST;
	}


	@Override
	public void saveWorksheet() {
		Debug("Saving Worksheet...");
		WorkSheetList.writeXML(new File(Program.getWorkDir(true) + WORKSHEET_XML));
		Debug("Saving Worksheet OK");
	}

	@Override
	public void loadWorksheet() {
		Debug("Loading Worksheet...");
		boolean resul = WorkSheetList.loadXML(new File(Program.getWorkDir(true) + WORKSHEET_XML));
		if (!resul) {
			WORKSHEET_LIST.clear();
			Debug("Loading Worksheet KO");
		} else {
			Debug("Loading Worksheet OK");
		}
	}

	@Override
	public WorkSheetList getWorksheetList() {
		return WORKSHEET_LIST;
	}

	@Override
	public void close() {
		if (listBouteilles != null) {
			listBouteilles.resetBouteille();
		}
		listeUniqueBouteille.clear();
	}
}
