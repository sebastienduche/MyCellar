package mycellar.placesmanagement;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.MyCellarException;
import mycellar.core.MyCellarObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 28.3
 * @since 09/06/21
 */
public class Rangement implements Comparable<Rangement> {

	private String nom;
	private int nb_emplacements; //Nombre d'emplacements
	private int nbColonnesStock; //Nombre max de colonnes pour tous les emplacements
	private int stock_nblign; //Nombre max de lignes pour tous les emplacements
	private boolean caisse; //Indique si le rangement est une caisse
	private int start_caisse; //Indique l'indice de démarrage des caisses
	private MyCellarObject[][][] stockage; //Stocke les vins du rangement: stockage[nb_emplacements][stock_nblign][stock_nbcol]
	private boolean limited; //Indique si une limite de caisse est activée
	private List<Part> listePartie = null;
	private Map<Integer, ArrayList<MyCellarObject>> storageCaisse;
	private boolean defaultPlace = false;

	/**
	 * Rangement: Constructeur de création d'un rangement de type Armoire
	 *
	 * @param nom String: nom du rangement
	 * @param listPart LinkedList<Part>: liste des parties
	 */
	public Rangement(String nom, List<Part> listPart) {
		this.nom = nom.strip();
		setPlace(listPart);
	}

	/**
	 * Rangement: Constructeur: rangement de type caisse
	 *
	 * @param nom String: nom du rangement
	 * @param nb_emplacement int: nombre d'emplacement
	 * @param start_caisse int: Numéro de démarrage de l'indice des caisses
	 * @param isLimit boolean: Limite de caisse activée?
	 * @param limite_caisse int: Capacité pour la limite
	 */
	private Rangement(String nom, int nb_emplacement, int start_caisse, boolean isLimit, int limite_caisse) {
		this.nom = nom.strip();
		nb_emplacements = nb_emplacement;
		this.start_caisse = start_caisse;

		limited = isLimit;
		if (limited) {
			nbColonnesStock = limite_caisse;
		} else {
			nbColonnesStock = -1;
		}

		stock_nblign = 1;
		caisse = true;

		storageCaisse = new HashMap<>(nb_emplacements);
		for (int i = start_caisse; i < start_caisse + nb_emplacements; i++) {
			storageCaisse.put(i, new ArrayList<>());
		}
	}

	/**
	 * getNom: retourne le nom du rangement
	 *
	 * @return String
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * setNom: Met à jour le nom du rangement
	 *
	 * @param name String
	 */
	public void setNom(String name) {
		nom = name.strip();
	}

	/**
	 * getStartCaisse: retourne l'indice de démarrage d'une caisse
	 *
	 * @return int
	 */
	public int getStartCaisse() {
		return start_caisse;
	}

	/**
	 * setStartCaisse: Positionne l'indice de démarrage d'une caisse
	 *
	 * @return int
	 */
	public void setStartCaisse(int start_caisse) {
		this.start_caisse = start_caisse;
	}

	/**
	 * getNbEmplacements: retourne le nombre d'emplacement
	 *
	 * @return int
	 */
	public int getNbEmplacements() {
		return nb_emplacements;
	}

	public void setNbEmplacements(int nb_emplacements) {
		this.nb_emplacements = nb_emplacements;
	}

	/**
	 * getNbColonnesStock: retourne le nombre de colonnes autorisé par le stock
	 *
	 * @return int
	 */
	public int getNbColonnesStock() {
		return nbColonnesStock;
	}

	/**
	 * isLimited: retourne true si la caisse possède une limite
	 *
	 * @return boolean
	 */
	public boolean isLimited() {
		return limited;
	}

	/**
	 * setLimited: Positionne true si la caisse possède une limite
	 *
	 * @return boolean
	 */
	public void setLimited(boolean limited) {
		this.limited = limited;
	}

	/**
	 * setNbBottleInCaisse: Positionne le nombre de bouteilles disponible dans une Caisse si elle est limité
	 *
	 * @return boolean
	 */
	public void setNbBottleInCaisse(int nbBottle) {
		if(limited && nbBottle > 0) {
			nbColonnesStock = nbBottle;
		}
	}

	/**
	 * getNbLignes: retourne le nombre de lignes d'un emplacement
	 *
	 * @param emplacement int: numéro de l'emplacement (0...n)
	 * @return int
	 */
	public int getNbLignes(int emplacement) {
		if (isCaisse()) {
			return -1;
		}
		return listePartie.get(emplacement).getRowSize();
	}

	/**
	 * Indique si la cellule demandée existe
	 *
	 * @param emplacement
	 * @param ligne
	 * @param col
	 * @return
	 */
	public boolean isExistingCell(int emplacement, int ligne, int col) {
		if (isCaisse()) {
			Debug("ERROR: Function isExistingCell can't be called on a simple place!");
			return false;
		}
		if (isInexistingNumPlace(emplacement)) {
			return false;
		}
		if (getNbLignes(emplacement) <= ligne) {
			return false;
		}
		int nbCol = getNbColonnes(emplacement, ligne);
		return (col < nbCol);
	}

	/**
	 * getNbColonnes: retourne le nombre de colonnes sur la ligne d'un emplacement
	 *
	 * @param emplacement int: numéro d'emplacement (0...n)
	 * @param ligne int: numéro de ligne (0...n)
	 * @return int
	 */
	public int getNbColonnes(int emplacement, int ligne) {
		if (isCaisse()) {
			Debug("ERROR: Function getNbColonnes can't be called on a simple place!");
			return -1;
		}
		if (emplacement < 0 || ligne < 0) {
			return -1;
		}
		return listePartie.get(emplacement).getRow(ligne).getCol();
	}

	/**
	 * getNbColonnesMax: retourne le nombre maximal de colonnes d'un emplacement
	 *
	 * @param emplacement int: numéro d'emplacement (0...n)
	 * @return int
	 */
	public int getNbColonnesMax(int emplacement) {
		if (isCaisse()) {
			Debug("ERROR: Function getNbColonnesMax can't be called on a simple place!");
			return -1;
		}
		return listePartie.get(emplacement).getRows().stream().mapToInt(Row::getCol).max().getAsInt();
	}

	/**
	 * getNbColonnesMax: retourne le nombre maximal de colonnes du rangement
	 *
	 * @return int
	 */
	public int getNbColonnesMax() {
		if (isCaisse()) {
			Debug("ERROR: Function getNbColonnesMax can't be called on a simple place!");
			return -1;
		}
		int max = 0;
		for (int i=0; i<getNbEmplacements(); i++) {
			int val = getNbColonnesMax(i);
			if (val > max) {
				max = val;
			}
		}
		return max;
	}

	/**
	 * getNbCaseUseLigne: retourne le nombre de cases utilisées dans une ligne d'un
	 * emplacement
	 *
	 * @param emplacement int: numéro de l'emplacement (0...n)
	 * @param ligne int: numéro de ligne (0...n)
	 * @return int
	 */
	public int getNbCaseUseLigne(int emplacement, int ligne) {
		if (isCaisse()) {
			Debug("ERROR: Function getNbCaseUseLigne can't be called on a simple place!");
			return -1;
		}
		int resul = 0;
		try {
			int nb_colonne = getNbColonnes(emplacement, ligne);
			for (int i = 0; i < nb_colonne; i++) {
				if (stockage[emplacement][ligne][i] != null) {
					resul += 1;
				}
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return resul;
	}

	/**
	 * getNbCaseFreeCoteLigne: retourne le nombre de cases libre côte à côte dans une ligne d'un
	 * emplacement à partir de la colonne indiquée
	 *
	 * @param emplacement int: numéro de l'emplacement (0...n)
	 * @param ligne int: numéro de ligne (0...n)
	 * @param colonne int: numéro de colonne (0...n)
	 * @return int
	 */
	public int getNbCaseFreeCoteLigne(int emplacement, int ligne, int colonne) {
		if (isCaisse()) {
			Debug("ERROR: Function getNbCaseFreeCoteLigne can't be called on a simple place!");
			return -1;
		}
		int resul = 0;
		try {
			int nb_colonne = getNbColonnes(emplacement, ligne);
			for (int i = colonne; i < nb_colonne; i++) {
				if (stockage[emplacement][ligne][i] == null) {
					resul++;
				} else {
					return resul;
				}
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return resul;
	}

	/**
	 * getNbCaseUse: retourne le nombre de case utilisée dans toutes les lignes
	 * d'un emplacement
	 *
	 * @param emplacement int: numéro d'emplacement (0...n)
	 * @return int
	 */
	public int getNbCaseUse(int emplacement) {
		if (isCaisse()) {
			return getNbCaseUseCaisse(emplacement + start_caisse);
		}

		int resul = 0;
		int nb_ligne = getNbLignes(emplacement);
		for (int j = 0; j < nb_ligne; j++) {
			int nb_colonne = getNbColonnes(emplacement, j);
			for (int i = 0; i < nb_colonne; i++) {
				if (stockage[emplacement][j][i] != null) {
					resul++;
				}
			}
		}
		return resul;
	}

	public int getNbCaseUse(Place place) {
		return getNbCaseUse(place.getPlaceNumIndex());
	}

	/**
	 * getNbCaseUseCaisse: retourne le nombre de case utilisée dans toutes les lignes
	 * d'une caisse
	 *
	 * @param emplacement int: numéro d'emplacement (start_caisse...n)
	 * @return int
	 */
	private int getNbCaseUseCaisse(int emplacement) {

		if (!isCaisse()) {
			Debug("ERROR: Function getNbCaseUseCaisse can't be called on a complex place!");
			return -1;
		}
		return storageCaisse.get(emplacement).size();
	}

	/**
	 * getNbCaseUseAll: Nombre de case utilisée dans toutes les lignes
	 *
	 * @return int
	 */
	public int getNbCaseUseAll() {
		int resul = 0;
		for (int i = 0; i < nb_emplacements; i++) {
			resul += getNbCaseUse(i);
		}
		return resul;
	}

	/**
	 * addWine: Ajout d'une bouteille
	 *
	 * @param wine Bouteille: bouteille à ajouter
	 *
	 * @return int
	 */
	public boolean addWine(MyCellarObject wine) {
		if (wine.hasNoStatus()) {
			wine.setCreated();
		}
		if (isCaisse()) {
			return putWineCaisse(wine);
		}
		return putWineStandard(wine);
	}

	/**
	 * removeWine: Suppression d'une bouteille
	 *
	 * @param wine Bouteille: bouteille à supprimer
	 *
	 * @return int
	 */
	public void removeWine(MyCellarObject wine) throws MyCellarException {
		clearStock(wine, wine.getPlace());
		Program.getStorage().deleteWine(wine);
	}

	/**
	 * putWine: Ajout d'une bouteille dans une caisse
	 *
	 * @param wine Bouteille: bouteille à ajouter
	 *
	 * @return int
	 */
	private boolean putWineCaisse(MyCellarObject wine) {
		int num_empl = wine.getNumLieu();
		wine.setLigne(0);
		wine.setColonne(0);

		Debug("putWineCaisse: " + wine.getNom() + " " + wine.getEmplacement() + " " + num_empl);

		try {
			int nb_vin = getNbCaseUse(num_empl - start_caisse);
			if (limited && nb_vin == nbColonnesStock) {
				return false;
			}
			storageCaisse.get(num_empl).add(wine);
			Program.getStorage().addWine(wine);
		}
		catch (Exception e) {
			Program.showException(e);
			return false;
		}
		return true;
	}

	/**
	 * putWineStandard: Ajout d'une bouteille dans un rangement non caisse
	 *
	 * @param wine Bouteille: Bouteille à ajouter
	 */
	private boolean putWineStandard(MyCellarObject wine) {
		Debug("putWineStandard: " + wine.getNom() + " " + wine.getEmplacement() + " " + wine.getNumLieu() + " " + wine.getLigne() + " " + wine.getColonne());

		int num_empl = wine.getNumLieu();
		int line = wine.getLigne();
		int column = wine.getColonne();
		try {
			stockage[num_empl - 1][line - 1][column - 1] = wine;
			Program.getStorage().addWine(wine);
		}
		catch (Exception e) {
			Program.showException(e);
			return false;
		}
		return true;
	}

	/**
	 * updateToStock: Change une bouteille dans le stock
	 *
	 * @param wine Bouteille: Bouteille à changer
	 */
	public void updateToStock(MyCellarObject wine) {
		if (isCaisse()) {
			storageCaisse.get(wine.getNumLieu()).add(wine);
			return;
		}
		int line = wine.getLigne();
		int num_empl = wine.getNumLieu();
		int column = wine.getColonne();
		try {
			stockage[num_empl - 1][line - 1][column - 1] = wine;
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}


	/**
	 * moveLine: Déplacement d'un objet dans un rangement
	 *
	 * @param myCellarObject MyCellarObject: Objet à déplacer
	 * @param nNewLine int: nouveau numéro de ligne
	 */
	public void moveLine(MyCellarObject myCellarObject, int nNewLine) throws MyCellarException {
		Program.getStorage().deleteWine(myCellarObject);
		clearStock(myCellarObject, myCellarObject.getPlace());
		myCellarObject.setLigne(nNewLine);
		addWine(myCellarObject);
	}

	public Optional<MyCellarObject> getBouteille(final MyCellarObject tempBouteille) {
		return getBouteille(tempBouteille.getNumLieu() - 1, tempBouteille.getLigne() - 1, tempBouteille.getColonne() - 1);
	}

	/**
	 * getBouteille: retourne la bouteille se trouvant à un emplacement précis dans une armoire
	 *
	 * @param num_empl int: numéro d'emplacement (0...n)
	 * @param line int: numéro de ligne (0...n)
	 * @param column int: numéro de colonne (0...n)
	 * @return Bouteille
	 */
	public Optional<MyCellarObject> getBouteille(int num_empl, int line, int column) {
		if (isCaisse()) {
			Debug("ERROR: Function getBouteille can't be called on a simple place!");
			return Optional.empty();
		}
		try {
			final MyCellarObject bouteille = stockage[num_empl][line][column];
			return Optional.ofNullable(bouteille);
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return Optional.empty();
	}

	public Optional<MyCellarObject> getBouteille(Place place) {
		return getBouteille(place.getPlaceNumIndex(), place.getLineIndex(), place.getColumnIndex());
	}

	/**
	 * Vide la case
	 *
	 * @param bottle Bouteille
	 */
	public void clearStock(MyCellarObject bottle, Place place) {
		if (isCaisse()) {
			storageCaisse.get(place.getPlaceNum()).remove(bottle);
		} else {
			clearComplexStock(place);
		}
	}

	public void clearComplexStock(Place place) {
		if (isCaisse()) {
			return;
		}
		try {
			stockage[place.getPlaceNumIndex()][place.getLineIndex()][place.getColumnIndex()] = null;
		} catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * getBouteilleCaisseAt: retourne la bouteille se trouvant à un emplacement précis
	 *
	 * @param num_empl int: numéro d'emplacement (0...n)
	 * @param index int: index de la bouteille (0...n)
	 * @return Bouteille
	 */
	public MyCellarObject getBouteilleCaisseAt(int num_empl, int index) {
		try {
			return storageCaisse.get(num_empl + start_caisse).get(index);
		} catch (Exception e) {
			Program.showException(e);
		}
		return null;
	}

	/**
	 * isCaisse: retourne true si c'est une caisse
	 *
	 * @return boolean
	 */
	public boolean isCaisse() {
		return caisse;
	}

	public boolean isSameColumnNumber() {
		for (int i = 0; i < nb_emplacements; i++){
			int nbCol = 0;
			for (int j = 0; j < getNbLignes(i); j++){
				if (nbCol == 0) {
					nbCol = getNbColonnes(i, j);
					continue;
				}
				if (nbCol != getNbColonnes(i, j)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Rangement: " + sText);
	}

	/**
	 * toXml Converti un rangement en Xml
	 *
	 * @return String
	 */
	public String toXml() {
		StringBuilder sText = new StringBuilder();
		if (isCaisse()) {
			sText.append("<place name=\"\" IsCaisse=\"true\" NbPlace=\"")
					.append(getNbEmplacements())
					.append("\" NumStart=\"")
					.append(getStartCaisse())
					.append("\"");
			if (isLimited()) {
				sText.append(" NbLimit=\"").append(getNbColonnesStock()).append("\"");
			} else {
				sText.append(" NbLimit=\"0\"");
			}
			if (isDefaultPlace()) {
				sText.append(" default=\"true\">");
			} else {
				sText.append(" default=\"false\">");
			}
		} else {
			sText.append("<place name=\"\" IsCaisse=\"false\" NbPlace=\"")
					.append(getNbEmplacements())
					.append("\">\n");
			for (int i=0; i<getNbEmplacements(); i++) {
				sText.append("<internal-place NbLine=\"").append(getNbLignes(i)).append("\">\n");
				for (int j=0; j<getNbLignes(i); j++) {
					sText.append("<line NbColumn=\"").append(getNbColonnes(i, j)).append("\"/>\n");
				}
				sText.append("</internal-place>\n");
			}
		}
		sText.append("<name><![CDATA[").append(getNom()).append("]]></name></place>");
		return sText.toString();
	}

	public boolean canAddBottle(MyCellarObject b) {
		if (isCaisse()) {
			return canAddBottle(b.getNumLieu() - start_caisse, 0, 0);
		}
		return canAddBottle(b.getNumLieu() - 1, b.getLigne() - 1, b.getColonne() - 1);
	}

	/**
	 * Indique si l'on peut ajouter une bouteille dans un rangement
	 *
	 * @param _nEmpl Numero d'emplacement (0, n)
	 * @param _nLine Numero de ligne (0, n)
	 * @param _nCol Numero de colonne (0, n)
	 * @return
	 */
	public boolean canAddBottle(int _nEmpl, int _nLine, int _nCol) {
		if (_nEmpl < 0 || _nEmpl >= getNbEmplacements()) {
			return false;
		}
		if (isCaisse()) {
			return !(isLimited() && !hasFreeSpaceInCaisse(_nEmpl));
		}

		if (_nLine < 0 || _nLine >= getNbLignes(_nEmpl)) {
			return false;
		}
		return !(_nCol < 0 || _nCol >= getNbColonnes(_nEmpl, _nLine));
	}

	public boolean canAddBottle(Place place) {
		return canAddBottle(place.getPlaceNumIndex(), place.getLineIndex(), place.getColumnIndex());
	}

	/**
	 * Indique si le numero du lieu existe
	 *
	 * @param numPlace Numero d'emplacement (startCaisse, n)
	 * @return
	 */
	boolean isInexistingNumPlace(int numPlace) {
		return numPlace < start_caisse || numPlace >= getNbEmplacements() + start_caisse;
	}

	@Deprecated
	private boolean hasFreeSpaceInCaisse(int _nEmpl) {
		if (!isCaisse()) {
			return false;
		}

		return !isLimited() || getNbCaseUse(_nEmpl) != getNbColonnesStock();
	}

	/**
	 * HasFreeSpaceInCaisse Indique si l'on peut encore ajouter des
	 * bouteilles dans une caisse
	 */
	public boolean hasFreeSpaceInCaisse(Place place) {
		return isCaisse() && (!isLimited() || getNbCaseUseCaisse(place.getPlaceNum()) != getNbColonnesStock());

	}


	/**
	 * Retourne le premier emplacement ou se trouve de la place ou -1
	 * @return
	 */
	public int getFreeNumPlaceInCaisse() {
		if (!isCaisse()) {
			return -1;
		}

		for (int i = 0; i < getNbEmplacements(); i++) {
			if (hasFreeSpaceInCaisse(i)) {
				return i + start_caisse;
			}
		}
		return -1;
	}

	/**
	 * Retourne le dernier emplacement utilisable
	 *
	 * @return
	 */
	public int getLastNumEmplacement() {
		if (isCaisse()) {
			return getStartCaisse() + getNbEmplacements();
		}
		return getNbEmplacements();
	}

	/**
	 * Retourne le premier emplacement utilisable
	 *
	 * @return
	 */
	public int getFirstNumEmplacement() {
		if (isCaisse()) {
			return getStartCaisse();
		}
		return 1;
	}

	/**
	 * Réinitialisation du stockage pour les caisses
	 */
	public void updateCaisse(int nbEmplacements) {
		if (!isCaisse()) {
			return;
		}
		nb_emplacements = nbEmplacements;
		storageCaisse = new HashMap<>(nbEmplacements);
		for (int i = start_caisse; i < start_caisse + nbEmplacements; i++) {
			storageCaisse.put(i, new ArrayList<>());
		}
	}

	/**
	 * Réinitialisation du stockage
	 */
	public void resetStock() {
		if (isCaisse()) {
			updateCaisse(nb_emplacements);
		} else {
			stockage = new Bouteille[nb_emplacements][stock_nblign][nbColonnesStock];
		}
	}
	/**
	 * Initialise les parties
	 *
	 * @param listPart LinkedList<Part>: liste des parties
	 */
	private void setPlace(List<Part> listPart) {
		nbColonnesStock = 0;
		stock_nblign = 0;
		nb_emplacements = listPart.size();
		listePartie = new LinkedList<>();
		for (int i = 0; i < nb_emplacements; i++) {
			Part part = new Part(listPart.get(i).getNum());
			listePartie.add(part);
			int rowSize = listPart.get(i).getRowSize();
			part.setRows(rowSize);
			if(rowSize > stock_nblign) {
				stock_nblign = rowSize;
			}
			for (int j = 0; j < rowSize; j++) {
				int colSize = listPart.get(i).getRow(j).getCol();
				part.getRow(j).setCol(colSize);
				if (colSize > nbColonnesStock) {
					nbColonnesStock = colSize;
				}
			}
		}

		start_caisse = 0;
		limited = false;
		caisse = false;

		stockage = new Bouteille[nb_emplacements][stock_nblign][nbColonnesStock];
	}

	public LinkedList<Part> getPlace() {
		LinkedList<Part> listPart = new LinkedList<>();
		for (Part p : listePartie) {
			Part part = new Part(p.getNum());
			listPart.add(part);
			for (int j = 0; j < p.getRowSize(); j++) {
				part.setRows(p.getRowSize());
				part.getRow(j).setCol(p.getRow(j).getCol());
			}
		}
		return listPart;
	}

	public void updatePlace(List<Part> listPart) {
		Debug("Updating the list of places: ");
		listPart.forEach(part -> Debug(part.toString()));
		setPlace(listPart);
		Program.setListCaveModified();
		Program.setModified();
	}

	/**
	 * getNumberOfBottlesPerPlace: retourne le nombre de case utilisée par partie
	 *
	 * @return Map: le numero d'emplacement commence toujours à 0
	 */
	public Map<Integer, Integer> getNumberOfBottlesPerPlace() {
		Map<Integer, Integer> numberOfBottlesPerPlace = new HashMap<>(nb_emplacements);
		if (isCaisse()) {
			for (int i = 0; i < nb_emplacements; i++) {
				numberOfBottlesPerPlace.put(i, getNbCaseUseCaisse(i + start_caisse));
			}
		} else {
			for (int i = 0; i < nb_emplacements; i++) {
				numberOfBottlesPerPlace.put(i, getNbCaseUse(i));
			}
		}
		return numberOfBottlesPerPlace;
	}

	@Override
	public String toString() {
		return nom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rangement other = (Rangement) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}

	@Override
	public int compareTo(Rangement o) {
		return getNom().compareTo(o.getNom());
	}

	public boolean isSame(Rangement r) {
		if (!getNom().equals(r.getNom())) {
			return false;
		}
		if (getNbEmplacements() != r.getNbEmplacements()) {
			return false;
		}
		if (isCaisse() != r.isCaisse()) {
			return false;
		}
		if (!isCaisse()) {
			for (int i = 0; i < getNbEmplacements(); i++) {
				int lignes = getNbLignes(i);
				if (lignes != r.getNbLignes(i)) {
					return false;
				}
				for (int j = 0; j < lignes; j++) {
					if (getNbColonnes(i, j) != r.getNbColonnes(i, j)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isDefaultPlace() {
		return defaultPlace;
	}

	public void setDefaultPlace(boolean defaultPlace) {
		this.defaultPlace = defaultPlace;
	}

	public static class RangementBuilder {
		private final String nom;
		private final List<Part> partList;
		private int nb_emplacement;

		private boolean sameColumns;
		private int[] nb_columnsByEmplacement;

		private int[] linesByEmplacement;
		private int[][] columnsByLines;

		public RangementBuilder(String nom) {
			this.nom = nom;
			partList = new LinkedList<>();
		}

		public RangementBuilder nb_emplacement(int[] linesByEmplacement) {
			nb_emplacement = linesByEmplacement.length;
			this.linesByEmplacement = linesByEmplacement;
			return this;
		}

		public RangementBuilder sameColumnsNumber(int[] nb_columnsByEmplacement) {
			sameColumns = true;
			this.nb_columnsByEmplacement = nb_columnsByEmplacement;
			return this;
		}

		public RangementBuilder differentColumnsNumber() {
			sameColumns = false;
			columnsByLines = new int[nb_emplacement][1];
			return this;
		}

		public RangementBuilder columnsNumberForPart(int part, int[] columns) throws Exception {
			if (sameColumns) {
				throw new Exception("This place has the same column number option set!");
			}
			if (part >= nb_emplacement) {
				throw new Exception("Incorrect part number! :" + part);
			}

			if (columns.length < linesByEmplacement[part]) {
				throw new Exception("Incorrect columns length number! :" + part);
			}
			columnsByLines[part] = columns;
			return this;
		}

		public Rangement build() {
			for (int i = 0; i < nb_emplacement; i++) {
				Part part = new Part(i);
				partList.add(part);
				part.setRows(linesByEmplacement[i]);
				if (sameColumns) {
					for (Row row: part.getRows()) {
						row.setCol(nb_columnsByEmplacement[i]);
					}
				} else {
					for (Row row: part.getRows()) {
						row.setCol(columnsByLines[i][row.getNum()-1]);
					}
				}
			}
			return new Rangement(nom, partList);
		}
	}

	public static class CaisseBuilder {
		private final String nom;
		private int nb_emplacement;
		private int start_caisse;
		private boolean isLimit;
		private int limite_caisse;
		private boolean defaultPlace;

		public CaisseBuilder(String nom) {
			this.nom = nom;
			nb_emplacement = 1;
			start_caisse = 0;
			isLimit = false;
			limite_caisse = -1;
			defaultPlace = false;
		}

		public CaisseBuilder nb_emplacement(int nb_emplacement) {
			this.nb_emplacement = nb_emplacement;
			return this;
		}

		public CaisseBuilder start_caisse(int start_caisse) {
			this.start_caisse = start_caisse;
			return this;
		}

		public CaisseBuilder limit(boolean limit) {
			isLimit = limit;
			return this;
		}

		public CaisseBuilder limite_caisse(int limite_caisse) {
			this.limite_caisse = limite_caisse;
			return this;
		}

		public CaisseBuilder setDefaultPlace(boolean defaultPlace) {
			this.defaultPlace = defaultPlace;
			return this;
		}

		public Rangement build() {
			final Rangement rangement = new Rangement(nom, nb_emplacement, start_caisse, isLimit, limite_caisse);
			rangement.setDefaultPlace(defaultPlace);
			return rangement;
		}
	}
}

