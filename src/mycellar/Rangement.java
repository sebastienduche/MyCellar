package mycellar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import mycellar.core.MyCellarError;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 25.2
 * @since 16/07/17
 */
public class Rangement implements Comparable<Rangement> {

	private String nom;
	private int nb_emplacements; //Nombre d'emplacements
	private int stock_nbcol; //Nombre max de colonnes pour tous les emplacements
	private int stock_nblign; //Nombre max de lignes pour tous les emplacements
	private boolean caisse; //Indique si le rangement est une caisse
	private int start_caisse; //Indique l'indice de démarrage des caisses
	private Bouteille stockage[][][]; //Stocke les vins du rangement: stockage[nb_emplacements][stock_nblign][stock_nbcol]
	private boolean limite; //Indique si une limite de caisse est activée
	private LinkedList<Part> listePartie = null;
	static final long serialVersionUID = 5012007;
	private HashMap<Integer, ArrayList<Bouteille>> storageCaisse;

	/**
	 * Rangement: Constructeur de création d'un rangement de type Armoire
	 *
	 * @param nom1 String: nom du rangement
	 * @param listPart LinkedList<Part>: liste des parties
	 */
	public Rangement(String nom, LinkedList<Part> listPart) {

		this.nom = nom.trim();
		setPlace(listPart);
	}

	/**
	 * Création d'un rangement de type Caisse sans limite
	 * @param nom
	 */
	public Rangement(String nom) {
		this(nom, 1, 0, false, -1);
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
	public Rangement(String nom, int nb_emplacement, int start_caisse, boolean isLimit, int limite_caisse) {
		this.nom = nom.trim();
		this.nb_emplacements = nb_emplacement;
		this.start_caisse = start_caisse;

		limite = isLimit;
		if (limite)
			stock_nbcol = limite_caisse;
		else
			stock_nbcol = -1;

		stock_nblign = 1;
		caisse = true;

		storageCaisse = new HashMap<Integer, ArrayList<Bouteille>>(nb_emplacements);
		for(int i=start_caisse; i<start_caisse+nb_emplacements; i++)
			storageCaisse.put(i, new ArrayList<Bouteille>());
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
		nom = name.trim();
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

	/**
	 * getNbColonnesStock: retourne le nombre de colonnes autorisé par le stock
	 *
	 * @return int
	 */
	public int getNbColonnesStock() {
		return stock_nbcol;
	}

	/**
	 * isLimited: retourne true si la caisse possède une limite
	 *
	 * @return boolean
	 */
	public boolean isLimited() {
		return limite;
	}

	/**
	 * setLimited: Positionne true si la caisse possède une limite
	 *
	 * @return boolean
	 */
	public void setLimited(boolean limite) {
		this.limite = limite;
	}

	/**
	 * setNbBottleInCaisse: Positionne le nombre de bouteilles disponible dans une Caisse si elle est limité
	 *
	 * @return boolean
	 */
	public void setNbBottleInCaisse(int nbBottle) {
		if(limite && nbBottle > 0)
			stock_nbcol = nbBottle;
	}

	/**
	 * getNbLignes: retourne le nombre de lignes d'un emplacement
	 *
	 * @param emplacement int: numéro de l'emplacement (0...n)
	 * @return int
	 */
	public int getNbLignes(int emplacement) {
		if(isCaisse())
			return -1;
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
		if(isCaisse()) {
			Debug("ERROR: Function isExistingCell can't be called on a simple place!");
			return false;
		}
		if(getNbLignes(emplacement) <= ligne)
			return false;
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
		if(isCaisse()) {
			Debug("ERROR: Function getNbColonnes can't be called on a simple place!");
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
		if(isCaisse()) {
			Debug("ERROR: Function getNbColonnesMax can't be called on a simple place!");
			return -1;
		}
		return listePartie.get(emplacement).getRows().stream().mapToInt(row -> row.getCol()).max().getAsInt();
	}

	/**
	 * getNbColonnesMax: retourne le nombre maximal de colonnes du rangement
	 *
	 * @return int
	 */
	public int getNbColonnesMax() {
		if(isCaisse()) {
			Debug("ERROR: Function getNbColonnesMax can't be called on a simple place!");
			return -1;
		}
		int max = 0;
		for(int i=0; i<getNbEmplacements(); i++) {
			int val = getNbColonnesMax(i);
			if(val > max)
				max = val;
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
		if(isCaisse()) {
			Debug("ERROR: Function getNbCaseUseLigne can't be called on a simple place!");
			return -1;
		}
		int resul = 0;
		int nb_colonne;
		try {
			nb_colonne = this.getNbColonnes(emplacement, ligne);
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
		if(isCaisse()) {
			Debug("ERROR: Function getNbCaseFreeCoteLigne can't be called on a simple place!");
			return -1;
		}
		int resul = 0;
		int nb_colonne;
		try {
			nb_colonne = this.getNbColonnes(emplacement, ligne);
			for (int i = colonne; i < nb_colonne; i++) {
				if (stockage[emplacement][ligne][i] == null) {
					resul++;
				}
				else
					return resul;
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
		
		if(isCaisse())
			return storageCaisse.get(emplacement + start_caisse).size();

		int resul = 0;
		int nb_colonne;
		int nb_ligne;

		try {
			nb_ligne = this.getNbLignes(emplacement);
			for (int j = 0; j < nb_ligne; j++) {
				nb_colonne = this.getNbColonnes(emplacement, j);
				for (int i = 0; i < nb_colonne; i++) {
					if (stockage[emplacement][j][i] != null) {
						resul++;
					}
				}
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return resul;
	}

	/**
	 * getNbCaseUseAll: Nombre de case utilisée dans toutes les lignes
	 *
	 * @return int
	 */
	public int getNbCaseUseAll() {

		int resul = 0;
		for(int i=0; i<nb_emplacements; i++)
			resul += getNbCaseUse(i);
		return resul;
	}

	/**
	 * putTabStock: Range les vins
	 *
	 * @return int
	 */
	public LinkedList<MyCellarError> putTabStock() {
		boolean isout = false;
		int tmp_num_max = 0;
		int tmp_lig_max = 0;
		int tmp_col_max = 0;
		LinkedList<MyCellarError> errors = new LinkedList<MyCellarError>();

		try {
			LinkedList<Bouteille> out = new LinkedList<Bouteille>();
			Bouteille b = null;
			if (!caisse) {
				stockage = new Bouteille[nb_emplacements][stock_nblign][stock_nbcol];
				//Copie dans le tableau stockage de ce rangement
				for (int i = 0; i < Program.getStorage().getAllNblign(); i++) {
					b = Program.getStorage().getAllAt(i);
					if (b != null) {
						String tmp_nom = b.getEmplacement();
						if (getNom().equals(tmp_nom)) {
							//Récupération du numéro d'emplacement, du numéro de ligne et de colonne
							int tmp_num = b.getNumLieu();
							int tmp_lig = b.getLigne();
							int tmp_col = b.getColonne();
							//Copie de la bouteille
							try {
								//En dehors du rangement
								if ( (tmp_num - 1) >= nb_emplacements || (tmp_lig - 1) >= getNbLignes(tmp_num - 1) || (tmp_col - 1) >= getNbColonnes(tmp_num - 1, tmp_lig - 1)) {
									throw new ArrayIndexOutOfBoundsException();
								}
								if (stockage[tmp_num - 1][tmp_lig - 1][tmp_col - 1] == null) { //Ajout de la bouteille
									stockage[tmp_num - 1][tmp_lig - 1][tmp_col - 1] = b; //-1 car ca commence � 0
								}
								else { //En dehors
									isout = true;
								}
							}
							catch (ArrayIndexOutOfBoundsException oobe1) {
								if (tmp_num > 0 && tmp_lig > 0 && tmp_col > 0) { //Si bouteille en dehors et non � 0
									if ( (tmp_num - 1) >= nb_emplacements || (tmp_lig - 1) >= getNbLignes(tmp_num - 1) || (tmp_col - 1) >= getNbColonnes(tmp_num - 1, tmp_lig - 1) /*stock_nbcol*/) {
										isout = true;
										if (tmp_num > tmp_num_max) {
											tmp_num_max = tmp_num;
										}
										if (tmp_lig > tmp_lig_max) {
											tmp_lig_max = tmp_lig;
										}
										if (tmp_col > tmp_col_max) {
											tmp_col_max = tmp_col;
										}
										errors.add(new MyCellarError(100, new String(Program.getError("Error075") + " " + getNom() + " " + Program.getError("Error080")), new String(Program.getError("Error079") + ": " + tmp_num_max + " " + Program.getError("Error116") + ": " + tmp_lig_max + " " +
												Program.getError("Error117") + ": " + tmp_col_max + " " + Program.getError("Error118"))));
									}
								}
								if ( (tmp_num - 1) < 0 || (tmp_lig - 1) < 0 || (tmp_col - 1) < 0) { //Si valeurs � 0
									isout = true;
									errors.add(new MyCellarError(2, Program.getError("Error082"), Program.getError("Error083")));
								}
							}
						}
						if (isout) {
							isout = false;
							//Ajout des bouteilles en dehors dans le tableau out
							out.add(b);
						}
					}
				}
			}
			else { //Pour la caisse
				updateCaisse(nb_emplacements);
				/*int cpt1[] = new int[nb_emplacements];

				for (int z = 0; z < nb_emplacements; z++) {
					cpt1[z] = 0;
				}*/
				//int cpt = 0;
				//int max_partie = 0; //Permet d'avoir le message d'erreur sur le nombre de partie maximal
				for (int i = 0; i < Program.getStorage().getAllNblign(); i++) {
					b = Program.getStorage().getAllAt(i);
					if (b != null) {
						String tmp_nom = b.getEmplacement();
						// Positionnement de la bouteille dans le stock
						if (getNom().equals(tmp_nom)) {
							int nb_vin = getNbCaseUse(b.getNumLieu()-start_caisse);
							if(limite && nb_vin == stock_nbcol)
								out.add(b);
							else
								storageCaisse.get(b.getNumLieu()).add(b);
						}
					}
				}
			}

		}
		catch (Exception e) {
			Program.showException(e);
		}
		return errors;
	}


	/**
	 * addWine: Ajout d'une bouteille
	 *
	 * @param wine Bouteille: bouteille à ajouter
	 *
	 * @return int
	 */
	public boolean addWine(Bouteille wine) {
		if(isCaisse())
			return putWineCaisse(wine);
		return putWineStandard(wine);
	}

	/**
	 * removeWine: Suppression d'une bouteille
	 *
	 * @param wine Bouteille: bouteille à supprimer
	 *
	 * @return int
	 */
	public boolean removeWine(Bouteille wine) {
		clearStock(wine);
		return Program.getStorage().deleteWine(wine);
	}

	/**
	 * putWine: Ajout d'une bouteille dans une caisse
	 *
	 * @param wine Bouteille: bouteille à ajouter
	 *
	 * @return int
	 */
	private boolean putWineCaisse(Bouteille wine) {

		int num_empl = wine.getNumLieu();
		wine.setLigne(0);
		wine.setColonne(0);

		Debug("putWineCaisse: "+wine.getNom()+" "+wine.getEmplacement()+" "+num_empl);

		try {
			int nb_vin = this.getNbCaseUse(num_empl-start_caisse);
			if(limite && nb_vin == stock_nbcol)
				return false;
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
	private boolean putWineStandard(Bouteille wine) {

		Debug("putWineStandard: "+wine.getNom()+" "+wine.getEmplacement()+" "+wine.getNumLieu()+" "+wine.getLigne()+" "+wine.getColonne());

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
	public void updateToStock(Bouteille wine) {
		if(isCaisse()) {
			Debug("ERROR: Function updateToStock can't be called on a simple place!");
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
	 * moveLineWine: Déplacement d'une bouteille dans un rangement
	 *
	 * @param bottle Bouteille: Bouteille à déplacer
	 * @param newLine int: nouveau numéro de ligne
	 */
	public void moveLineWine(Bouteille bottle, int nNewLine) {
		Program.getStorage().deleteWine(bottle);
		clearStock(bottle);
		bottle.setLigne(nNewLine);
		addWine( bottle );
	}

	/**
	 * getBouteille: retourne la bouteille se trouvant à un emplacement précis dans une armoire
	 *
	 * @param num_empl int: numéro d'emplacement (0...n)
	 * @param line int: numéro de ligne (0...n)
	 * @param column int: numéro de colonne (0...n)
	 * @return Bouteille
	 */
	public Bouteille getBouteille(int num_empl, int line, int column) {
		if(isCaisse()) {
			Debug("ERROR: Function getBouteille can't be called on a simple place!");
			return null;
		}
		try {
			return stockage[num_empl][line][column];
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return null;
	}

	/**
	 * Vide la case
	 *
	 * @param bottle Bouteille
	 */
	public void clearStock(Bouteille bottle) {
		if(isCaisse()) {
			storageCaisse.get(bottle.getNumLieu()).remove(bottle);
		}
		else {
			try {
				stockage[bottle.getNumLieu() - 1][bottle.getLigne() - 1][bottle.getColonne() - 1] = null;
			}
			catch (Exception e) {
				Program.showException(e);
			}
		}
	}

	/**
	 * getBouteilleCaisseAt: retourne la bouteille se trouvant à un emplacement précis
	 *
	 * @param num_empl int: numéro d'emplacement (0...n)
	 * @param index int: index de la bouteille (0...n)
	 * @return Bouteille
	 */
	public Bouteille getBouteilleCaisseAt(int num_empl, int index) {
		try {
			return storageCaisse.get(num_empl + start_caisse).get(index);
		}
		catch (Exception e) {
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

	public boolean isSameColumnNumber(){
		for( int i=0; i<nb_emplacements; i++){
			int nbCol = 0;
			for(int j=0; j<getNbLignes(i);j++){
				if(nbCol == 0) {
					nbCol = getNbColonnes(i, j);
					continue;
				}
				if(nbCol != getNbColonnes(i, j))
					return false;
			}
		}
		return true;
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {

		Program.Debug("Rangement: " + sText);
	}

	/**
	 * toXml Converti un rangement en Xml
	 *
	 * @return String
	 */
	public String toXml() {

		StringBuffer sText = new StringBuffer();
		if (isCaisse()) {
			sText.append("<place name=\"\" IsCaisse=\"true\" NbPlace=\"")
			.append(getNbEmplacements())
			.append("\" NumStart=\"")
			.append(getStartCaisse())
			.append("\"");
			if (isLimited())
				sText.append(" NbLimit=\"").append(this.getNbColonnesStock()).append("\">");
			else
				sText.append(" NbLimit=\"0\">");
		}else{
			sText.append("<place name=\"\" IsCaisse=\"false\" NbPlace=\"")
			.append(getNbEmplacements())
			.append("\">\n");
			for (int i=0; i<getNbEmplacements(); i++) {
				sText.append("<internal-place NbLine=\"").append(getNbLignes(i)).append("\">\n");
				for (int j=0; j<getNbLignes(i); j++)
					sText.append("<line NbColumn=\"").append(getNbColonnes(i,j)).append("\"/>\n");
				sText.append("</internal-place>\n");
			}
		}
		sText.append("<name><![CDATA[").append(getNom()).append("]]></name></place>");
		return sText.toString();
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
		
		if (_nEmpl < start_caisse || _nEmpl >= getNbEmplacements() + start_caisse)
			return false;
		if (isCaisse()) {
			if( isLimited() && !hasFreeSpaceInCaisse(_nEmpl))
				return false;
			return true;
		}

		if (_nLine < 0 || _nLine >= getNbLignes(_nEmpl))
			return false;
		if (_nCol < 0 || _nCol >= getNbColonnes(_nEmpl, _nLine))
			return false;

		return true;
	}


	/**
	 * HasFreeSpaceInCaisse Indique si l'on peut encore ajouter des
	 * bouteilles dans une caisse
	 * 
	 * @param _nEmpl (0...n)
	 * @return
	 */
	public boolean hasFreeSpaceInCaisse(int _nEmpl) {
		if(!isCaisse())
			return false;

		if(!isLimited())
			return true;

		if(getNbCaseUse(_nEmpl) == getNbColonnesStock())
			return false;

		return true;
	}


	/**
	 * Retourne le premier emplacement ou se trouve de la place ou -1
	 * @return
	 */
	public int getFreeNumPlaceInCaisse() {
		if (!isCaisse())
			return -1;

		for( int i = 0; i < getNbEmplacements(); i++) {
			if (hasFreeSpaceInCaisse(i))
				return i + start_caisse;
		}
		return -1;
	}

	/**
	 * Retourne le dernier emplacement utilisable
	 * 
	 * @return
	 */
	public int getLastNumEmplacement() {
		if( isCaisse())
			return getStartCaisse() + getNbEmplacements();
		return getNbEmplacements();
	}

	/**
	 * Réinitialisation du stockage pour les caisses
	 */
	public void updateCaisse(int nb_emplacements) {
		if(!isCaisse())
			return;
		this.nb_emplacements = nb_emplacements;
		storageCaisse = new HashMap<Integer, ArrayList<Bouteille>>(nb_emplacements);
		for(int i=start_caisse; i<start_caisse+nb_emplacements; i++)
			storageCaisse.put(i, new ArrayList<Bouteille>());
	}
	/**
	 * Rangement: Constructeur de création d'un rangement
	 *
	 * @param nom1 String: nom du rangement
	 * @param listPart LinkedList<Part>: liste des parties
	 */
	public void setPlace(LinkedList<Part> listPart) {

		stock_nbcol = 0;
		stock_nblign = 0;
		nb_emplacements = listPart.size();
		listePartie = new LinkedList<Part>();
		for(int i=0;i<nb_emplacements; i++)
		{
			Part part = new Part(listPart.get(i).getNum());
			listePartie.add(part);
			int rowSize = listPart.get(i).getRowSize();
			part.setRows(rowSize);
			if(rowSize > stock_nblign)
				stock_nblign = rowSize;
			for(int j=0; j<rowSize; j++)
			{
				int colSize = listPart.get(i).getRow(j).getCol();
				part.getRow(j).setCol(colSize);
				if(colSize > stock_nbcol)
					stock_nbcol = colSize;
			}
		}

		start_caisse = 0;
		limite = false;
		caisse = false;

		stockage = new Bouteille[nb_emplacements][stock_nblign][stock_nbcol];
	}

	public LinkedList<Part> getPlace() {

		LinkedList<Part> listPart = new LinkedList<Part>();
		for(int i=0;i<listePartie.size(); i++)
		{
			Part part = new Part(listePartie.get(i).getNum());
			listPart.add(part);
			for(int j=0; j<listePartie.get(i).getRowSize(); j++)
			{
				part.setRows(listePartie.get(i).getRowSize());
				part.getRow(j).setCol(listePartie.get(i).getRow(j).getCol());
			}
		}
		return listPart;
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
		if(!getNom().equals(r.getNom()))
			return false;
		if(getNbEmplacements() != r.getNbEmplacements())
			return false;
		if(isCaisse() != r.isCaisse())
			return false;
		if(!isCaisse()) {
			for(int i=0; i<getNbEmplacements(); i++) {
				int lignes = getNbLignes(i); 
				if(lignes != r.getNbLignes(i))
					return false;
				for(int j=0; j<lignes; j++) {
					if(getNbColonnes(i, j) != r.getNbColonnes(i, j))
						return false;
				}
			}
		}
		return true;
	}

}

