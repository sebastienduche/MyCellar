package mycellar;

import mycellar.vignobles.CountryVignobles;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 5.5
 * @since 05/07/19
 */

public class SerializedStorage implements Storage {

	private static HistoryList m_HistoryList = new HistoryList();
	private ListeBouteille listBouteilles = new ListeBouteille();

	private final LinkedList<String> listeUniqueBouteille = new LinkedList<>(); // Liste des noms de bouteille (un seule nom)

	// Private constructor prevents instantiation from other classes
	private SerializedStorage() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SerializedStorageHolder {
		private static final SerializedStorage INSTANCE = new SerializedStorage();
	}

	public static SerializedStorage getInstance() {
		return SerializedStorageHolder.INSTANCE;
	}

	@Override
	public void setListBouteilles(ListeBouteille listBouteilles) {
		this.listBouteilles = listBouteilles;
		listeUniqueBouteille.clear();
		if(this.listBouteilles.bouteille == null) {
			this.listBouteilles.bouteille = new LinkedList<>();
		}
		for(Bouteille b: this.listBouteilles.bouteille) {
			if(!listeUniqueBouteille.contains(b.getNom())) {
				listeUniqueBouteille.add(b.getNom());
			}
		}
	}


	@Override
	public void addBouteilles(ListeBouteille listBouteilles) {
		this.listBouteilles.getBouteille().addAll(listBouteilles.getBouteille());
		for(Bouteille b: listBouteilles.bouteille) {
			final List<History> theBottle = m_HistoryList.getHistory().stream().filter(history -> history.getBouteille().getId() == b.getId()).collect(Collectors.toList());
			if (b.updateID() && !theBottle.isEmpty()) {
				theBottle.get(0).getBouteille().setId(b.getId());
			}
			if(!listeUniqueBouteille.contains(b.getNom())) {
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
	public boolean addHistory(int type, Bouteille bottle) {
		Program.setModified();
		m_HistoryList.addLast(new History(bottle, type));
		return true;
	}

	@Override
	public boolean clearHistory(int value) {
		/* -1 Clear All
		 * 0 Clear Add
		 * 1 Clear Modify
		 * 2 Clear Del
		 * 3 Clear Validated
		 */
		Debug("Program: Clearing history: "+value);
		String sValue;
		switch (value) {
			case -1:
				sValue = Program.getError("Error182");
				break;
			case 0:
				sValue = Program.getError("Error189");
				break;
			case 1:
				sValue = Program.getError("Error191");
				break;
			case 2:
				sValue = Program.getError("Error190");
				break;
			case 3:
				sValue = Program.getError("Error.HistoryValidatedDelete");
				break;
			default:
				sValue = "";
		}

		if( JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, sValue, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
			return false;
		}

		Program.setModified();
		if(value == -1) {
			m_HistoryList.clear();
			return true;
		}
		final List<History> list = m_HistoryList.getHistory().stream().filter(history -> history.getType() == value).collect(Collectors.toList());

		// Suppression de l'historique
		for (History h : list) {
			removeHistory(h);
		}
		return true;
	}

	@Override
	public void removeHistory(History oB) {
		m_HistoryList.remove(oB);
	}


	/**
	 * deleteWine: Supression d'une bouteille dans un rangement
	 *
	 * @param bottle Bouteille: Bouteille à supprimer
	 */
	@Override
	public boolean deleteWine(Bouteille bottle) {

		final String nom = bottle.getNom();
		final String annee = bottle.getAnnee();
		final String emplacement = bottle.getEmplacement();
		final int numLieu = bottle.getNumLieu();
		final int ligne = bottle.getLigne();
		final int colonne = bottle.getColonne();

		Debug("DeleteWine: Trying deleting bottle " + nom.trim() + " " + annee + " " + emplacement.trim() + " " + numLieu + " " + ligne + " " + colonne);
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
		return true;
	}

	/**
	 * addWine: Ajoute une bouteille 
	 *
	 * @param wine Bouteille
	 */
	@Override
	public boolean addWine(Bouteille wine) { 
		if(null == wine)
			return false;

		Debug("AddWine: Adding bottle " + wine.getNom() + " " + wine.getAnnee() + " " + wine.getEmplacement() + " " + wine.getNumLieu() + " " + wine.getLigne() + " " + wine.getColonne());

		Program.setModified();

		if(!listeUniqueBouteille.contains(wine.getNom())) {
			listeUniqueBouteille.add(wine.getNom());
		}
		CountryVignobles.addVignobleFromBottle(wine);
		return listBouteilles.getBouteille().add(wine);
	}

	/**
	 * getAllList: retourne le tableau
	 *
	 * @return LinkedList
	 */
	@Override
	public LinkedList<Bouteille> getAllList() {
		return listBouteilles.getBouteille();
	}

	/**
	 * getBottlesCount: retourne le nombre total de bouteilles du fichier
	 *
	 * @return int
	 */
	@Override
	public int getBottlesCount() {
		return listBouteilles.getBouteille().size();
	}


	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("SerializedStorage: " + sText);
	}

	@Override
	public boolean saveHistory() {
		Debug("Saving History...");
		boolean resul = HistoryList.writeXML(new File(Program.getWorkDir(true) + "history.xml"));
		Debug("Saving History OK");
		return resul;
	}

	@Override
	public boolean loadHistory() {
		Debug("Loadinging History...");
		boolean resul = HistoryList.loadXML(new File(Program.getWorkDir(true) + "history.xml"));
		if(!resul) {
			setHistoryList(new HistoryList());
			Debug("Loading History KO");
		} else {
			Debug("Loading History OK");
		}
		return resul;
	}

	@Override
	public HistoryList getHistoryList() {
		return m_HistoryList;
	}
	
	@Override
	public void setHistoryList(HistoryList list) {
		m_HistoryList = list;
	}

	@Override
	public void close() {
		if(listBouteilles != null) {
			listBouteilles.resetBouteille();
		}
		listeUniqueBouteille.clear();
	}
}
