package mycellar;

import mycellar.core.LabelProperty;
import mycellar.core.MyCellarObject;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetData;
import mycellar.core.datas.worksheet.WorkSheetList;
import mycellar.placesmanagement.Rangement;
import mycellar.vignobles.CountryVignobleController;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 6.9
 * @since 07/05/21
 */

public class SerializedStorage implements Storage {

	private static final String HISTORY_XML = "history.xml";
	private static final String WORKSHEET_XML = "worksheet.xml";
	private static final HistoryList HISTORY_LIST = new HistoryList();
	private static final WorkSheetList WORKSHEET_LIST = new WorkSheetList();
	private ListeBouteille listMyCellarObject = new ListeBouteille();

	private int itemsCount;
	private boolean worksheetModified = false;
	private boolean historyModified = false;

	private final LinkedList<String> distinctNames = new LinkedList<>(); // Liste des noms de bouteille (un seule nom)

	private SerializedStorage() {
	}

	private static class SerializedStorageHolder {
		private static final SerializedStorage INSTANCE = new SerializedStorage();
	}

	public static SerializedStorage getInstance() {
		return SerializedStorageHolder.INSTANCE;
	}

	@Override
	public void setListMyCellarObject(ListeBouteille listMyCellarObject) {
		this.listMyCellarObject = listMyCellarObject;
		distinctNames.clear();
		if (Program.isWineType()) {
			if (this.listMyCellarObject.bouteille == null) {
				this.listMyCellarObject.bouteille = new LinkedList<>();
			}
			for (MyCellarObject b : this.listMyCellarObject.bouteille) {
				if (!distinctNames.contains(b.getNom())) {
					distinctNames.add(b.getNom());
				}
			}
		} else if (Program.isMusicType()) {
			if (this.listMyCellarObject.music == null) {
				this.listMyCellarObject.music = new LinkedList<>();
			}
			for (MyCellarObject b : this.listMyCellarObject.music) {
				if (!distinctNames.contains(b.getNom())) {
					distinctNames.add(b.getNom());
				}
			}
		} else {
			Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
		}
		itemsCount = listMyCellarObject.getItemsCount();
	}

	public int getItemsCount() {
		return itemsCount;
	}

	@Override
	public void addBouteilles(ListeBouteille listBouteille) {
		if (Program.isWineType()) {
			listMyCellarObject.getBouteille().addAll(listBouteille.getBouteille());
			for (MyCellarObject myCellarObject : listMyCellarObject.bouteille) {
				final List<History> theBottle = HISTORY_LIST.getHistory().stream().filter(history -> history.getBouteille().getId() == myCellarObject.getId()).collect(Collectors.toList());
				if (myCellarObject.updateID() && !theBottle.isEmpty()) {
					theBottle.get(0).getBouteille().setId(myCellarObject.getId());
				}
				if (!distinctNames.contains(myCellarObject.getNom())) {
					distinctNames.add(myCellarObject.getNom());
				}
			}
		} else if (Program.isMusicType()) {
			listMyCellarObject.getMusic().addAll(listBouteille.getMusic());
			for (MyCellarObject myCellarObject : listMyCellarObject.music) {
				final List<History> theMusic = HISTORY_LIST.getHistory().stream().filter(history -> history.getMusic().getId() == myCellarObject.getId()).collect(Collectors.toList());
				if (myCellarObject.updateID() && !theMusic.isEmpty()) {
					theMusic.get(0).getMusic().setId(myCellarObject.getId());
				}
				if (!distinctNames.contains(myCellarObject.getNom())) {
					distinctNames.add(myCellarObject.getNom());
				}
			}
		}
		itemsCount = listMyCellarObject.getItemsCount();
	}

	@Override
	public ListeBouteille getListMyCellarObject() {
		return listMyCellarObject;
	}

	@Override
	public LinkedList<String> getDistinctNames() {
		return distinctNames;
	}


	@Override
	public void addHistory(HistoryState type, MyCellarObject myCellarObject) {
		historyModified = true;
		Program.setModified();
		HISTORY_LIST.add(new History(myCellarObject, type.ordinal(), getItemsCount()));
	}

	@Override
	public void addToWorksheet(MyCellarObject myCellarObject) {
		worksheetModified = true;
		Program.setModified();
		WORKSHEET_LIST.add(new WorkSheetData(myCellarObject));
	}

	@Override
	public void removeFromWorksheet(MyCellarObject myCellarObject) {
		worksheetModified = true;
		Program.setModified();
		final List<WorkSheetData> collect = WORKSHEET_LIST.getWorsheet()
				.stream()
				.filter(workSheetData -> workSheetData.getBouteilleId() == myCellarObject.getId())
				.collect(Collectors.toList());
		WORKSHEET_LIST.getWorsheet().removeAll(collect);
	}

	@Override
	public void clearWorksheet() {
		worksheetModified = true;
		Program.setModified();
		WORKSHEET_LIST.getWorsheet().clear();
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
				sValue = Program.getError("Error.HistoryValidatedDelete", LabelProperty.OF_THE_PLURAL);
				break;
			case TOCHECK:
				sValue = Program.getError("Error.HistoryToCheckDelete", LabelProperty.OF_THE_PLURAL);
				break;
			default:
				sValue = "";
		}

		if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, sValue, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
			return;
		}

		Program.setModified();
		historyModified = true;
		if (historyState == HistoryState.ALL) {
			HISTORY_LIST.clear();
			return;
		}
		HISTORY_LIST.getHistory()
				.stream()
				.filter(history -> history.getType() == historyState.ordinal())
				.forEach(this::removeHistory);
	}

	@Override
	public void removeHistory(History oB) {
		historyModified = true;
		HISTORY_LIST.remove(oB);
	}

	@Override
	public boolean deleteWine(MyCellarObject myCellarObject) {

		final String nom = myCellarObject.getNom();
		final String annee = myCellarObject.getAnnee();
		final String emplacement = myCellarObject.getEmplacement();
		final int numLieu = myCellarObject.getNumLieu();
		final int ligne = myCellarObject.getLigne();
		final int colonne = myCellarObject.getColonne();

		Debug("DeleteWine: Trying deleting myCellarObject " + nom.strip() + " " + annee + " " + emplacement.strip() + " " + numLieu + " " + ligne + " " + colonne);
		Rangement rangement = myCellarObject.getRangement();
		boolean isCaisse = rangement == null || rangement.isCaisse();
		final List<MyCellarObject> resultBouteilles = listMyCellarObject.getBouteille().stream()
				.filter(
						bouteille -> emplacement.equals(bouteille.getEmplacement())
								&& nom.equals(bouteille.getNom())
								&& numLieu == bouteille.getNumLieu()
								&& (isCaisse ? annee.equals(bouteille.getAnnee()) : (ligne == bouteille.getLigne() && colonne == bouteille.getColonne()))).collect(Collectors.toList());
		if (resultBouteilles.isEmpty()) {
			Debug("DeleteWine: Unable to find the wine!");
			return false;
		}
		Program.setModified();
		final MyCellarObject myCellarObject1 = resultBouteilles.get(0);
		Debug("DeleteWine: Deleted object " + myCellarObject1);
		listMyCellarObject.remove(myCellarObject1);
		itemsCount--;
		return true;
	}

	@Override
	public boolean addWine(MyCellarObject myCellarObject) {
		if (null == myCellarObject) {
			return false;
		}

		myCellarObject.setModified();

		Debug("AddWine: Adding bottle " + myCellarObject.getNom() + " " + myCellarObject.getAnnee() + " " + myCellarObject.getEmplacement() + " " + myCellarObject.getNumLieu() + " " + myCellarObject.getLigne() + " " + myCellarObject.getColonne());

		Program.setModified();

		if (!distinctNames.contains(myCellarObject.getNom())) {
			distinctNames.add(myCellarObject.getNom());
		}
		if (myCellarObject instanceof Bouteille) {
			CountryVignobleController.addVignobleFromBottle((Bouteille) myCellarObject);
		}
		itemsCount++;
		return listMyCellarObject.add(myCellarObject);
	}

	@Override
	public List<? extends MyCellarObject> getAllList() {
		if (Program.isMusicType()) {
			return listMyCellarObject.getMusic();
		}
		if (Program.isWineType()) {
			return listMyCellarObject.getBouteille();
		}
		Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
		return Collections.emptyList();
	}

	@Override
	public boolean add(MyCellarObject myCellarObject) {
		if (myCellarObject instanceof Bouteille) {
			return listMyCellarObject.getBouteille().add((Bouteille) myCellarObject);
		} else if (myCellarObject instanceof Music) {
			return listMyCellarObject.getMusic().add((Music) myCellarObject);
		} else {
			Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
		}
		return false;
	}

	@Override
	public int getBottlesCount() {
		return listMyCellarObject.getBouteille().size();
	}

	private static void Debug(String sText) {
		Program.Debug("SerializedStorage: " + sText);
	}

	@Override
	public void saveHistory() {
		if (historyModified) {
			historyModified = false;
			Debug("Saving History...");
			HistoryList.writeXML(new File(Program.getWorkDir(true) + HISTORY_XML));
			Debug("Saving History OK");
		}
	}

	@Override
	public void loadHistory() {
		historyModified = false;
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
		if (worksheetModified) {
			worksheetModified = false;
			Debug("Saving Worksheet...");
			WorkSheetList.writeXML(new File(Program.getWorkDir(true) + WORKSHEET_XML));
			Debug("Saving Worksheet OK");
		}
	}

	@Override
	public void loadWorksheet() {
		worksheetModified = false;
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
		if (listMyCellarObject != null) {
			listMyCellarObject.resetBouteille();
		}
		distinctNames.clear();
	}
}
