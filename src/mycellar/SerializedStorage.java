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
 * @version 5.0
 * @since 04/07/18
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
		if(this.listBouteilles.bouteille == null)
			this.listBouteilles.bouteille = new LinkedList<>();
		for(Bouteille b: this.listBouteilles.bouteille) {
			if(!listeUniqueBouteille.contains(b.getNom()))
				listeUniqueBouteille.add(b.getNom());
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
			if(!listeUniqueBouteille.contains(b.getNom()))
				listeUniqueBouteille.add(b.getNom());
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
	public boolean clearHistory(int _nValue) {
		/* -1 Clear All
		 * 0 Clear Add
		 * 1 Clear Modify
		 * 2 Clear Del
		 */
		Debug("Program: Clearing history: "+_nValue);
		String sValue = "";
		if( _nValue == -1 )
			sValue = Program.getError("Error182");
		if( _nValue == 0 )
			sValue = Program.getError("Error189");
		if( _nValue == 1 )
			sValue = Program.getError("Error191");
		if( _nValue == 2 )
			sValue = Program.getError("Error190");
		if( JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, sValue, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
			return false;

		Program.setModified();
		if( _nValue == -1 ) {
			m_HistoryList.clear();
			return true;
		}
		LinkedList<History> tmpList = new LinkedList<>();
		for (History h : m_HistoryList.getHistory()) {
			if (h.getType() == _nValue) {
				tmpList.addLast(h);
			}
		}
		// Suppression de l'historique
		for (History h : tmpList) {
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
		return deleteWine(bottle.getNom(), bottle.getAnneeInt(), bottle.getEmplacement(), bottle.getNumLieu(), bottle.getLigne(), bottle.getColonne());
	}

	/**
	 * deleteWine: Supression d'une bouteille dans un rangement
	 *
	 * @param nom2 Bouteille: Bouteille à supprimer
	 * @param annee int: Année du vin
	 * @param empl String: emplacement du vin
	 * @param num_empl int: numéro de l'emplacement (1...n+1)
	 * @param line int: numéro de ligne (1...n+1)
	 * @param column int: numéro de colonne (1...n+1)
	 */
	private boolean deleteWine(String nom2, int annee, String empl, int num_empl, int line, int column) {

		int i = 0;
		boolean resul = false;
		int num = -1;

		Debug("DeleteWine: Trying deleting bottle " + nom2.trim() + " " + annee + " " + empl.trim() + " " + num_empl + " " + line + " " + column);
		try {
			do {
				Bouteille b = listBouteilles.getBouteille().get(i);
				String empl1 = b.getEmplacement();
				int num_empl1 = b.getNumLieu();
				int line1 = b.getLigne();
				int column1 = b.getColonne();
				String nom1 = b.getNom();
				int annee1 = b.getAnneeInt();

				Rangement rangement = Program.getCave(empl);

				if (rangement != null && rangement.isCaisse()) {
					if (nom1.equals(nom2) && empl.equals(empl1) && num_empl == num_empl1 && annee == annee1) {
						resul = true;
						num = i;
					}
				}
				else {
					if (nom1.equals(nom2) && empl.equals(empl1) && num_empl == num_empl1 && line == line1 && column == column1) {
						resul = true;
						num = i;
					}
				}
				i++;
			}
			while (!resul && i < listBouteilles.getBouteille().size());
		}
		catch (Exception ex) {Program.showException(ex, false);}

		if (resul) {
			Program.setModified();
			Debug("DeleteWine: Deleted bottle number " + num);
			listBouteilles.getBouteille().remove(num);
		}

		return resul;
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

		if(!listeUniqueBouteille.contains(wine.getNom()))
			listeUniqueBouteille.add(wine.getNom());
		CountryVignobles.addVignobleFromBottle(wine);
		return listBouteilles.getBouteille().add(wine);
	}

	/**
	 * replaceWineAll: Modifie une bouteille dans un rangement non caisse
	 *
	 * @param wine Bouteille: Bouteille à ajouter
	 * @param num_empl int: numéro de l'emplacement (1...n+1)
	 * @param line int: numéro de ligne (1...n+1)
	 * @param column int: numéro de colonne (1...n+1)
	 */

	@Override
	public void replaceWineAll(Bouteille wine, int num_empl, int line, int column) { //Remplace une bouteille existante par une autre

		int i = 0;
		boolean resul = false;
		do {
			Bouteille b = listBouteilles.getBouteille().get(i);
			String empl1 = wine.getEmplacement();
			String empl = b.getEmplacement();
			int num_empl1 = b.getNumLieu();
			int line1 = b.getLigne();
			int column1 = b.getColonne();

			if (empl.equals(empl1) && num_empl == num_empl1 && line == line1 && column == column1) {
				b.update(wine);
				resul = true;
			}
			i++;
		}
		while (!resul && i < getAllNblign());
		
		if(resul)
			Program.setModified();
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
	 * getAllNblign: retourne le nombre de lignes du rangement
	 *
	 * @return int
	 */
	@Override
	public int getAllNblign() {
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
			m_HistoryList = new HistoryList();
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
		if(listBouteilles != null)
			listBouteilles.resetBouteille();
	}
}
