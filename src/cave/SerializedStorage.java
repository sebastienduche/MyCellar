package Cave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import Cave.vignobles.CountryVignobles;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 3.3
 * @since 15/01/17
 */

public class SerializedStorage implements Storage {

	public LinkedHashMap<Integer,Integer> annees; // Liste des années
	protected static HistoryList m_HistoryList = new HistoryList();
	protected ListeBouteille listBouteilles = new ListeBouteille();

	private LinkedList<String> listeUniqueBouteille = new LinkedList<String>(); // Liste des noms de bouteille (un seule nom)

	// Private constructor prevents instantiation from other classes
	private SerializedStorage() {
		initialize();
	}

	public void setListBouteilles(ListeBouteille listBouteilles) {
		this.listBouteilles = listBouteilles;
		listeUniqueBouteille.clear();
		for(Bouteille b: listBouteilles.bouteille) {
			if(!listeUniqueBouteille.contains(b.getNom()))
				listeUniqueBouteille.add(b.getNom());
		}
	}

	public void setListBouteilles(LinkedList<Bouteille> listBouteilles) {
		this.listBouteilles.bouteille = listBouteilles;
		listeUniqueBouteille.clear();
		for(Bouteille b: listBouteilles) {
			if(!listeUniqueBouteille.contains(b.getNom()))
				listeUniqueBouteille.add(b.getNom());
		}
	}
	
	public void addBouteilles(ListeBouteille listBouteilles) {
		this.listBouteilles.getBouteille().addAll(listBouteilles.getBouteille());
		for(Bouteille b: listBouteilles.bouteille) {
			if(!listeUniqueBouteille.contains(b.getNom()))
				listeUniqueBouteille.add(b.getNom());
		}
	}

	public ListeBouteille getListBouteilles() {
		return listBouteilles;
	}
	
	public LinkedList<String> getBottleNames() {
		return listeUniqueBouteille;
	}

	public void initialize() {
		if (annees == null) {
			Debug("initialize: Creating year LinkedHashMap");
			annees = new LinkedHashMap<Integer, Integer>();
		}
		else
			annees.clear();
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SerializedStorageHolder { 
		public static final SerializedStorage INSTANCE = new SerializedStorage();
	}

	public static SerializedStorage getInstance() {
		return SerializedStorageHolder.INSTANCE;
	}


	@Override
	public boolean addHistory(int type, Bouteille bottle) {
		Program.setModified();
		m_HistoryList.addLast(new History(bottle, type));
		return true;
	}

	@Override
	public boolean clearHistory(int _nValue) {
		/* -1 Do Nothing
		 * 0 Clear Add
		 * 1 Clear Modify
		 * 2 Clear Del
		 * 3 Clear All
		 */
		Debug("Program: Clearing history: "+_nValue);
		if( _nValue == -1 )
			return false;
		String sValue = "";
		if( _nValue == 0 )
			sValue = Program.getError("Error189");
		if( _nValue == 1 )
			sValue = Program.getError("Error191");
		if( _nValue == 2 )
			sValue = Program.getError("Error190");
		if( _nValue == 3 )
			sValue = Program.getError("Error182");
		if( JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, sValue + " " + Program.getError("Error183"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))
			return false;

		Program.setModified();
		if( _nValue == 3 ) {
			m_HistoryList.clear();
			return true;
		}
		LinkedList<History> tmpList = new LinkedList<History>();
		for (History h : m_HistoryList) {
			if (h.GetType() == _nValue) {
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
	public void clearHistory() {
		Program.setModified();
		m_HistoryList.clear();
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
	public boolean deleteWine(String nom2, int annee, String empl, int num_empl, int line, int column) {

		int num_empl1 = 0;
		int line1 = 0;
		int column1 = 0;
		int i = 0;
		boolean resul = false;
		int num = -1;
		int annee1 = 0;
		String nom1 = null;
		String empl1 = null;
		Bouteille b = null;

		Debug("DeleteWine: Trying deleting bottle " + nom2.trim() + " " + annee + " " + empl.trim() + " " + num_empl + " " + line + " " + column);
		try {
			do {
				b = listBouteilles.getBouteille().get(i);
				empl1 = b.getEmplacement();
				num_empl1 = b.getNumLieu();
				line1 = b.getLigne();
				column1 = b.getColonne();
				nom1 = b.getNom();
				annee1 = b.getAnneeInt();

				int nCave = Rangement.convertNom_Int(empl);

				if (nCave != -1 && Program.getCave(nCave).isCaisse()) {
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
		removeAnnee(annee);

		if (listBouteilles.getBouteille().size() == 0) {
			annees = new LinkedHashMap<Integer,Integer>();
		}
		return resul;
	}
	
	/**
	 * getNbAutreAnnee
	 * @return int
	 */
	public int getNbAutreAnnee() {
		int nb = 0;
		Bouteille b;
		for (int i = 0; i < listBouteilles.getBouteille().size(); i++) {
			b = listBouteilles.getBouteille().get(i);
			if (b != null && b.getAnneeInt() < 1000 ) {
				nb++;
			}
		}
		return nb;
	}

	/**
	 * getNbNonVintage
	 * @return int
	 */
	public int getNbNonVintage() {
		int nb = 0;
		Bouteille b;
		for (int i = 0; i < listBouteilles.getBouteille().size(); i++) {
			b = listBouteilles.getBouteille().get(i);
			if (b != null && Bouteille.isNonVintageYear(b.getAnnee())) {
				nb++;
			}
		}
		return nb;
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
		int prix = 0;
		try {
			prix = Integer.parseInt(Program.convertStringFromHTMLString(wine.getPrix()));
		}
		catch (NumberFormatException nfe) {
			prix = 0;
		}
		if (Bouteille.prix_max < prix) {
			Bouteille.prix_max = prix;
		}
		addAnnee(wine.getAnneeInt());
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

	public void replaceWineAll(Bouteille wine, int num_empl, int line, int column) { //Remplace une bouteille existante par une autre

		int num_empl1 = 0;
		int line1 = 0;
		int column1 = 0;
		int i = 0;
		boolean resul = false;
		String empl = "";
		Bouteille b = null;
		do {
			b = listBouteilles.getBouteille().get(i);
			String empl1 = wine.getEmplacement();
			empl = b.getEmplacement();
			num_empl1 = b.getNumLieu();
			line1 = b.getLigne();
			column1 = b.getColonne();

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
	public LinkedList<Bouteille> getAllList() {
		return listBouteilles.getBouteille();
	}

	/**
	 * setAllList: retourne le tableau
	 *
	 * @param _all LinkedList
	 */
	public void setAllList(LinkedList<Bouteille> _all) {
		listBouteilles.getBouteille().clear();
		listBouteilles.getBouteille().addAll(_all);
	}

	/**
	 * getAllAt: retourne une Bouteille du tableau
	 *
	 * @param i int: numéro de la bouteille (0...n)
	 * @return Bouteille
	 */
	public Bouteille getAllAt(int i) {
		Bouteille b = null;
		try {
			b = (Bouteille) listBouteilles.getBouteille().get(i);
		}
		catch (IndexOutOfBoundsException ioobe) {
			b = null;
		}
		return b;
	}

	/**
	 * getAllNblign: retourne le nombre de lignes du rangement
	 *
	 * @return int
	 */
	public int getAllNblign() {
		return listBouteilles.getBouteille().size();
	}

	/**
	 * setAll: Remplace le tableau all par un autre
	 *
	 * @param all1 Bouteille[]: tableau
	 */
	public void setAll(Bouteille all1[]) {
		if (all1 != null) {
			listBouteilles.getBouteille().clear();

			if (all1.length > 0) {
				for (int i = 0; i < all1.length; i++) {
					if (all1[i] != null ) {
						listBouteilles.getBouteille().add(all1[i]);
					}
				}
			}
		}
	}

	/**
	 * removeAnnee: Décremente le compteur pour une année
	 *
	 * @param annee int
	 */

	public void removeAnnee(int annee) {
		Integer an = new Integer(annee);
		if (annees.containsKey(an)) {
			int nb = Integer.parseInt(annees.get(an).toString());
			nb--;
			if (nb > 0) {
				annees.put(an, new Integer(nb));
			}
			else {
				annees.remove(an);
			}
		}
	}

	/**
	 * addAnnee: Incremente le compteur pour une année
	 *
	 * @param annee int
	 */

	public void addAnnee(int annee) {
		if (annees == null) {
			annees = new LinkedHashMap<Integer,Integer>();
		}
		Integer an = new Integer(annee);
		if (annees.containsKey(an)) {
			int nb = Integer.parseInt(annees.get(an).toString());
			nb++;
			annees.put(an, new Integer(nb));
		}
		else {
			annees.put(an, new Integer(1));
		}
	}

	/**
	 * getNbBouteilleAnnee: retourne le nombre de bouteilles d'une année
	 *
	 * @param an int: année souhaitée
	 * @return int
	 */
	public int getNbBouteilleAnnee(int an) {
		Integer annee = new Integer(an);
		if (annees.containsKey(annee)) {
			return Integer.parseInt(annees.get(annee).toString());
		}
		else {
			return 0;
		}
	}

	/**
	 * setAnnee: Met à jour le tableau des années
	 *
	 * @param year LinkedHashMap
	 */
	public void setAnnee(LinkedHashMap<Integer,Integer> year) {
		annees = year;
		if (annees == null) {
			annees = new LinkedHashMap<Integer, Integer>();
		}
	}

	/**
	 * getAnneeList: Retourne la liste des années
	 *
	 */
	public LinkedHashMap<Integer,Integer> getAnneeList() {
		return annees;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("SerializedStorage: " + sText);
	}

	public boolean readRangement(LinkedList<Rangement> cave) {
		boolean bresul = false;
		ObjectInputStream ois = null;
		File f1 = new File( Program.getWorkDir(true) );
		final File[] list = f1.listFiles(new MyFilenameFilter());
		if( list.length > 0 )
			bresul = true;
		for (int i = 0; i < list.length; i++) {
			try {
				ois = new ObjectInputStream(new FileInputStream(list[i]));
				Rangement r = (Rangement) ois.readObject();
				if(r != null)
					cave.add(r);
				ois.close();
				MyXmlDom.appendRangement(r);
			}
			catch (IOException ex) {
				bresul = false;
				try {
					ois.close();
				}
				catch (IOException ioe) {}
				catch (NullPointerException ioe) {}
				list[i].delete();
			}
			catch (ClassNotFoundException ex1) {bresul = false;
			}
		}
		return bresul;
	}

	@Override
	public boolean saveHistory() {
		boolean resul = true;
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Program.getWorkDir(true) + "history.sinfo"));
			oos.writeObject(m_HistoryList);
			oos.close();
		}
		catch (FileNotFoundException fnfe) {resul = false;
		}
		catch (IOException ioe) {resul = false;
		}
		Debug("Program: Saving History OK");
		return resul;
	}

	@Override
	public boolean loadHistory() {
		// Historique
		boolean resul = true;
		File f1 = new File( Program.getWorkDir(true) + "history.sinfo");
		if(!f1.exists()) {
			m_HistoryList = new HistoryList();
			return false;
		}
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(f1));
			m_HistoryList = (HistoryList) ois.readObject();
			ois.close();
			Debug("Loading History OK");
		}
		catch (IOException ex) {
			Program.showException(ex, false);
			Debug("ERROR: Loading History");
			resul = false;
			m_HistoryList = new HistoryList();
			try {
				ois.close();
			}
			catch (IOException ioe) {}
			catch (NullPointerException ioe) {}
			f1.delete();
		}
		catch (ClassNotFoundException ex1) {
			resul = false;
			Debug("SerializedStorage: ERROR: Loading History");
			m_HistoryList = new HistoryList();
		}

		return resul;
	}

	@Override
	public HistoryList getHistory() {
		return m_HistoryList;
	}

	@Override
	public void close() {
		if(listBouteilles != null)
			listBouteilles.resetBouteille();
	}
	
	

}
