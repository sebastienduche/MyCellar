package mycellar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.countries.Countries;
import mycellar.countries.Country;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jxl.Workbook;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 25.0
 * @since 13/07/17
 */
public class Rangement implements Serializable, Comparable<Rangement> {

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
						resul += 1;
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

		char virgule;
		String sVirgule;
		if( Program.hasConfigCaveKey("PRICE_SEPARATOR"))
		{
			sVirgule = Program.getCaveConfigString("PRICE_SEPARATOR","");
			virgule = sVirgule.charAt(0);
		}
		else
		{
			java.text.DecimalFormat df = new java.text.DecimalFormat();
			virgule = df.getDecimalFormatSymbols().getDecimalSeparator();
		}
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
						//Prix Max
						int prix_max = 0;
						String prix_tmp = Program.convertStringFromHTMLString(b.getPrix());
						int ind_prix = prix_tmp.indexOf(virgule);
						if (ind_prix > 0) {
							prix_tmp = prix_tmp.substring(0, ind_prix);
						}

						if (virgule == '.') {
							prix_tmp = prix_tmp.replace(',', ' ');
						}
						if (virgule == ',') {
							prix_tmp = prix_tmp.replace('.', ' ');
						}
						int index = prix_tmp.indexOf(' ');
						while (index != -1) {
							prix_tmp = prix_tmp.substring(0, index) + prix_tmp.substring(index + 1);
							index = prix_tmp.indexOf(' ');
						}

						try {
							prix_max = Integer.parseInt(prix_tmp);
							prix_max++;
						}
						catch (NumberFormatException nfe) {
							prix_max=0;
						}

						if (Bouteille.prix_max < prix_max) {
							Bouteille.prix_max = prix_max;

						}
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
						//Prix Max
						int prix_max = 0;
						String prix_tmp = Program.convertStringFromHTMLString(b.getPrix());
						int ind_prix = prix_tmp.indexOf(virgule);
						if (ind_prix > 0) {
							prix_tmp = prix_tmp.substring(0, ind_prix);
						}
						if (virgule == '.') {
							prix_tmp = prix_tmp.replace(',', ' ');
						}
						if (virgule == ',') {
							prix_tmp = prix_tmp.replace('.', ' ');
						}
						int index = prix_tmp.indexOf(' ');
						while (index != -1) {
							prix_tmp = prix_tmp.substring(0, index) + prix_tmp.substring(index + 1);
							index = prix_tmp.indexOf(' ');
						}
						try {
							prix_max = Integer.parseInt(prix_tmp);
							prix_max++;
						}
						catch (NumberFormatException nfe) {}
						if (Bouteille.prix_max < prix_max) {
							Bouteille.prix_max = prix_max;
						}

						// Positionnement de la bouteille dans le stock
						if (getNom().equals(tmp_nom)) {
							int nb_vin = getNbCaseUse(b.getNumLieu()-start_caisse);
							if(limite && nb_vin == stock_nbcol)
								out.add(b);
							else
								storageCaisse.get(b.getNumLieu()).add(b);
						}

						/*if (getNom().equals(tmp_nom)) {
							//Le plus grand compteur??
							int max_cpt = 0;
							//Ecriture du vin
							try {
								max_cpt = cpt1[tmp_num_empl];
							}
							catch (Exception oobe) { //Nombre de partie pas assez grand
								if (tmp_num_empl > max_partie) {
									max_partie = tmp_num_empl;
									errors.add(new MyCellarError(0, Program.getError("Error075") + " " + tmp_nom.trim() + " " + Program.getError("Error080"), Program.getError("Error119") + " " + (tmp_num_empl + 1 - start_caisse)));
								}
								if (tmp_num_empl < 0) {
									errors.add(new MyCellarError(0, Program.getError("Error172"), Program.getError("Error173") + " " + start_caisse));
								}
								isout = true;
							}

							if (max_cpt < stock_nbcol) {
								cpt = max_cpt;
								if (!isout) { //Ajout de la bouteille
									if (stockage[tmp_num_empl][0][cpt] == null) {
										stockage[tmp_num_empl][0][cpt] = b;
									}
									else {
										isout = true;
									}
									cpt1[tmp_num_empl]++; //cpt++;
								}

							}
							else { // Plus de bouteille que défini par la variable stock
								if (!limite) { //Agrandissement de la caisse permis
									//Agrandissement du tableau de stockage
									cpt = cpt1[tmp_num_empl];
									Bouteille stockage2[][][] = new Bouteille[nb_emplacements][1][stock_nbcol * 2];
									for (int z = 0; z < nb_emplacements; z++) {
										for (int j = 0; j < stock_nbcol; j++) {
											stockage2[z][0][j] = stockage[z][0][j];
										}
									}
									stockage = stockage2;
									if (stockage[tmp_num_empl][0][cpt] == null) {
										stockage[tmp_num_empl][0][cpt] = b;
									}
									else {
										isout = true;
									}

									cpt1[tmp_num_empl]++;
									stock_nbcol *= 2;
								}
								else { //Agrandissement non permis
									isout = true;
								}
							}
						}
						if (isout) {
							isout = false;
							//Ajout de la bouteille dans Out
							out.add(b);
						}*/
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

	/**
	 * write_CSV: Ecriture d'un fichier CSV
	 *
	 * @param fichier String: fichier CSV à écrire
	 * @param all LinkedList<Bouteille>: stock de bouteille
	 * @param all_nblign int: nombre de bouteilles dans le stock
	 *
	 * @return int
	 */
	public static int write_CSV(String fichier, LinkedList<Bouteille> all) {

		int resul = 0;
		String name = null;
		String year = null;
		String half = null;
		String place = null;
		String num_place = null;
		String line = null;
		String column = null;
		String price = null;
		String comment = null;
		String cle0, cle1, cle2, cle3, cle4, cle5, cle6, cle7, cle8;
		String separator = null;

		separator = Program.getCaveConfigString("SEPARATOR_DEFAULT", ";");

		cle0 = cle1 = cle2 = cle3 = cle4 = cle5 = cle6 = cle7 = cle8 = null;
		cle0 = Program.getCaveConfigString("SIZE_COL0EXPORT_CSV", "1");
		cle1 = Program.getCaveConfigString("SIZE_COL1EXPORT_CSV", "1");
		cle2 = Program.getCaveConfigString("SIZE_COL2EXPORT_CSV", "1");
		cle3 = Program.getCaveConfigString("SIZE_COL3EXPORT_CSV", "1");
		cle4 = Program.getCaveConfigString("SIZE_COL4EXPORT_CSV", "1");
		cle5 = Program.getCaveConfigString("SIZE_COL5EXPORT_CSV", "1");
		cle6 = Program.getCaveConfigString("SIZE_COL6EXPORT_CSV", "1");
		cle7 = Program.getCaveConfigString("SIZE_COL7EXPORT_CSV", "1");
		cle8 = Program.getCaveConfigString("SIZE_COL8EXPORT_CSV", "1");

		File f = new File(fichier);
		FileWriter ficout = null;

		try {
			ficout = new FileWriter(f);
			ficout.flush();

			if (resul == 0) {
				for (Bouteille b : all) {
					//if (all[i] != null) {
					if (cle0.equals("1")) {
						name = Program.convertStringFromHTMLString(b.getNom());
						name = name.replaceAll("\"", "\"\"");
						ficout.write("\"" + name + "\"" + separator);
						ficout.flush();
					}
					if (cle1.equals("1")) {
						try {
							year = b.getAnnee();
							year = year.replaceAll("\"", "\"\"");
							ficout.write("\"" + year + "\"" + separator);
							ficout.flush();
						}
						catch (NullPointerException npe) {}
					}
					if (cle2.equals("1")) {
						half = Program.convertStringFromHTMLString(b.getType());
						half = half.replaceAll("\"", "\"\"");
						ficout.write("\"" + half + "\"" + separator);
						ficout.flush();
					}
					if (cle3.equals("1")) {
						place = Program.convertStringFromHTMLString(b.getEmplacement());
						place = place.replaceAll("\"", "\"\"");
						ficout.write("\"" + place + "\"" + separator);
						ficout.flush();
					}
					if (cle4.equals("1")) {
						num_place = Integer.toString(b.getNumLieu());
						ficout.write("\"" + num_place + "\"" + separator);
						ficout.flush();
					}
					if (cle5.equals("1")) {
						line = Integer.toString(b.getLigne());
						ficout.write("\"" + line + "\"" + separator);
						ficout.flush();
					}
					if (cle6.equals("1")) {
						column = Integer.toString(b.getColonne());
						ficout.write("\"" + column + "\"" + separator);
						ficout.flush();
					}
					if (cle7.equals("1")) {
						price = Program.convertStringFromHTMLString(b.getPrix());
						price = price.replaceAll("\"", "\"\"");
						ficout.write("\"" + price + "\"" + separator);
						ficout.flush();
					}
					if (cle8.equals("1")) {
						comment = Program.convertStringFromHTMLString(b.getComment());
						comment = comment.replaceAll("\"", "\"\"");
						ficout.write("\"" + comment + "\"" + separator);
						ficout.flush();
					}
					ficout.flush();
					ficout.write('\n');
					ficout.flush();
					//}
				}
			}
			ficout.flush();
			ficout.close();

		}
		catch (IOException ioe) {
			new Erreur(Program.getError("Error120"), Program.getError("Error161"));
			resul = -2;
		}
		finally {
			try {
				ficout.close();
			}
			catch (IOException ioe1) {}
			catch (NullPointerException ioe1) {}
		}
		return resul;
	}

	/**
	 * write_HTML: Ecriture du fichier HTML
	 *
	 * @param fichier String: fichier HTML à écrire
	 * @param all LinkedList<Bouteille>: stock de bouteilles
	 * @param fields 
	 * @param all_nblign int: nombre de bouteille dans le stock
	 *
	 * @return int
	 */
	public static boolean write_HTML(String fichier, LinkedList<Bouteille> all, LinkedList<MyCellarFields> fields) {

		try{
			DocumentBuilderFactory dbFactory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = 
					dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root element
			Element root = doc.createElement("html");
			doc.appendChild(root);
			Element title = doc.createElement("title");
			root.appendChild(title);
			Element style = doc.createElement("style");
			style.appendChild(doc.createTextNode("table, td, th { border: 1px solid black; border-collapse:collapse} "
					+ "tr:nth-child(even) {background-color: #f2f2f2} "));
			root.appendChild(style);
			title.appendChild(doc.createTextNode(Program.getLabel("Infos207")));
			Element body = doc.createElement("body");
			root.appendChild(body);
			Element table = doc.createElement("table");
			body.appendChild(table);
			Element thead = doc.createElement("thead");
			table.appendChild(thead);
			if(fields.isEmpty())
				fields = MyCellarFields.getFieldsList();
			for(MyCellarFields field : fields){
				Element td = doc.createElement("td");
				thead.appendChild(td);
				td.appendChild(doc.createTextNode(field.toString()));
			}

			Element tbody = doc.createElement("tbody");
			table.appendChild(tbody);

			for (Bouteille b : all) {
				Element tr = doc.createElement("tr");
				tbody.appendChild(tr);
				for(MyCellarFields field : fields){
					Element td = doc.createElement("td");
					tr.appendChild(td);
					if(field == MyCellarFields.NAME)
						td.appendChild(doc.createTextNode(b.getNom()));
					else if(field == MyCellarFields.YEAR)
						td.appendChild(doc.createTextNode(b.getAnnee()));
					else if(field == MyCellarFields.TYPE)
						td.appendChild(doc.createTextNode(b.getType()));
					else if(field == MyCellarFields.PLACE)
						td.appendChild(doc.createTextNode(b.getEmplacement()));
					else if(field == MyCellarFields.NUM_PLACE)
						td.appendChild(doc.createTextNode(Integer.toString(b.getNumLieu())));
					else if(field == MyCellarFields.LINE)
						td.appendChild(doc.createTextNode(Integer.toString(b.getLigne())));
					else if(field == MyCellarFields.COLUMN)
						td.appendChild(doc.createTextNode(Integer.toString(b.getColonne())));
					else if(field == MyCellarFields.PRICE)
						td.appendChild(doc.createTextNode(b.getPrix()));
					else if(field == MyCellarFields.COMMENT)
						td.appendChild(doc.createTextNode(b.getComment()));
					else if(field == MyCellarFields.MATURITY)
						td.appendChild(doc.createTextNode(b.getMaturity()));
					else if(field == MyCellarFields.PARKER)
						td.appendChild(doc.createTextNode(b.getParker()));
					else if(field == MyCellarFields.COLOR)
						td.appendChild(doc.createTextNode(BottleColor.getColor(b.getColor()).toString()));
					else if(field == MyCellarFields.COUNTRY) {
						if(b.getVignoble() != null) {
							Country c = Countries.find(b.getVignoble().getCountry());
							if(c != null)
								td.appendChild(doc.createTextNode(c.toString()));
						}
						else
							td.appendChild(doc.createTextNode(""));
					}
					else if(field == MyCellarFields.VINEYARD) {
						if(b.getVignoble() != null)
							td.appendChild(doc.createTextNode(b.getVignoble().getName()));
						else
							td.appendChild(doc.createTextNode(""));
					}
					else if(field == MyCellarFields.AOC) {
						if(b.getVignoble() != null && b.getVignoble().getAOC() != null)
							td.appendChild(doc.createTextNode(b.getVignoble().getAOC()));
						else
							td.appendChild(doc.createTextNode(""));
					}
					else if(field == MyCellarFields.IGP) {
						if(b.getVignoble() != null && b.getVignoble().getIGP() != null)
							td.appendChild(doc.createTextNode(b.getVignoble().getIGP()));
						else
							td.appendChild(doc.createTextNode(""));
					}

				}
			}

			TransformerFactory transformerFactory =
					TransformerFactory.newInstance();
			Transformer transformer =
					transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result =
					new StreamResult(new File(fichier));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			Debug("ParserConfigurationException");
			Program.showException(e, false);
			return false;
		} catch (TransformerException e) {
			Debug("TransformerException");
			Program.showException(e, false);
			return false;
		}
		return true;
	}

	/**
	 * write_XLS: Fonction d'écriture du ficher Excel
	 *
	 * @param file String: Fichier à écrire.
	 * @param all1 LinkedList<Bouteille>: Tableau de bouteilles à écrire
	 * @param isExit boolean: True si appel pour la création automatique d'une sauvegarde Excel
	 *
	 * @return int
	 */
	public static int write_XLS(String file, LinkedList<Bouteille> all, boolean isExit) {

		Debug( "write_XLS: writing file: "+file );
		File f;
		try {
			f = new File(file);
			String sDir = f.getParent();
			if(null != sDir) {
				f = new File(sDir);
				if(!f.exists()) {
					Debug( "write_XLS: ERROR: directory "+sDir+" don't exist." );
					Debug( "write_XLS: ERROR: Unable to write XLS file" );
					return -1;
				}
			}
		} catch(Exception e) {
			Program.showException(e, false);
			Debug( "write_XLS: ERROR: with file " + file );
			return -1;
		}
		int resul = 0;
		int num_ligne = 0;
		String title = "";

		HashMap<MyCellarFields, Integer> mapCle = new HashMap<MyCellarFields, Integer>();
		HashMap<Integer, Integer> mapColumnNumber = new HashMap<Integer, Integer>();

		//Récupération des colonnes à exporter
		LinkedList<MyCellarFields> fields = MyCellarFields.getFieldsList();
		int i=0;
		for(MyCellarFields field : fields) {
			mapCle.put(field, Program.getCaveConfigInt("SIZE_COL"+i+"EXPORT_XLS", 1));
			i++;
		}

		if (isExit) { //Cas sauvegarde XLS Backup
			num_ligne = 0;
			i=0;
			for(MyCellarFields field : fields) {
				mapColumnNumber.put(i, i);
				i++;
			}
		}
		else { // Export XLS
			title = Program.getCaveConfigString("XLS_TITLE", ""); //Récupération du titre du XLS

			num_ligne = 2; //Affectation des numéros de colonnes
			i=0;
			int value = 0;
			for(MyCellarFields field : fields) {
				if(mapCle.get(field) == 1) {
					mapColumnNumber.put(i, value);
					value++;
				}
				i++;
			}
		}

		try { //Création du fichier
			WritableWorkbook workbook = Workbook.createWorkbook(new File(file));
			String sheet_title = title;
			if (sheet_title.length() == 0) {
				sheet_title = Program.getCaveConfigString("XML_TYPE","");
			}
			if( sheet_title.isEmpty() )
				sheet_title = Program.getLabel("Infos389");
			WritableSheet sheet = workbook.createSheet(sheet_title, 0);

			if (!isExit) { //Export XLS
				int size = 0;
				//Taille du titre
				size = Program.getCaveConfigInt("TITLE_SIZE_XLS", 10);
				WritableFont cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.NO_BOLD, false);
				String bold = "";
				boolean isBold = false;
				bold = Program.getCaveConfigString("BOLD_XLS", "");
				if (bold.equals("bold")) {
					isBold = true;
				}
				if (isBold) {
					cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.BOLD, false);
				}
				WritableCellFormat cellformat = new WritableCellFormat(cellfont);

				Label titre0 = new Label(0, 0, title, cellformat); //Ajout du titre
				try {
					sheet.addCell(titre0);
				}
				catch (WriteException ex3) {
					resul = -2;
				}
			}

			WritableFont cellfont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
			if (!isExit) { //Export XLS
				int size = 0;
				//propriétés du texte
				size = Program.getCaveConfigInt("TEXT_SIZE_XLS", 10);
				cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.NO_BOLD, false);
			}
			WritableCellFormat cellformat = new WritableCellFormat(cellfont);
			//Ajout titre colonne
			i=0;
			HashMap<MyCellarFields, Label> listLabels = new HashMap<MyCellarFields, Label>();
			HashMap<MyCellarFields, Integer> mapColumnWidth = new HashMap<MyCellarFields, Integer>();

			for(MyCellarFields field : fields) {
				Label label;
				listLabels.put(field, label = new Label(i, num_ligne, field.toString(), cellformat));
				mapColumnWidth.put(field, label.getContents().length());
				i++;
			}

			try {
				//Ajout Titre
				if(isExit) {
					for(MyCellarFields field : fields) {
						sheet.addCell(listLabels.get(field));
					}
				}
				else {
					for(MyCellarFields field : fields) {
						if(mapCle.get(field) == 1)
							sheet.addCell(listLabels.get(field));
					}
				}
			}
			catch (WriteException ex3) {
				resul = -2;
			}

			i = 0;
			for (Bouteille b : all) {
				listLabels.clear();
				int j = 0;
				try {
					for(MyCellarFields field : fields) {
						String value = "";
						if(field == MyCellarFields.NAME)
							value = b.getNom();
						else if(field == MyCellarFields.YEAR)
							value = b.getAnnee();
						else if(field == MyCellarFields.TYPE)
							value = b.getType();
						else if(field == MyCellarFields.PLACE)
							value = b.getEmplacement();
						else if(field == MyCellarFields.NUM_PLACE)
							value = Integer.toString(b.getNumLieu());
						else if(field == MyCellarFields.LINE)
							value = Integer.toString(b.getLigne());
						else if(field == MyCellarFields.COLUMN)
							value = Integer.toString(b.getColonne());
						else if(field == MyCellarFields.PRICE)
							value = b.getPrix();
						else if(field == MyCellarFields.COMMENT)
							value = b.getComment();
						else if(field == MyCellarFields.MATURITY)
							value = b.getMaturity();
						else if(field == MyCellarFields.PARKER)
							value = b.getParker();
						else if(field == MyCellarFields.COLOR)
							value = b.getColor();
						else if(field == MyCellarFields.COUNTRY) {
							if(b.getVignoble() != null) {
								Country c = Countries.find(b.getVignoble().getCountry());
								if(c != null)
									value = c.toString();
							}
						}
						else if(field == MyCellarFields.VINEYARD) {
							if(b.getVignoble() != null)
								value = b.getVignoble().getName();
						}
						else if(field == MyCellarFields.AOC) {
							if(b.getVignoble() != null && b.getVignoble().getAOC() != null)
								value = b.getVignoble().getAOC();
						}
						else if(field == MyCellarFields.IGP) {
							if(b.getVignoble() != null && b.getVignoble().getIGP() != null)
								value = b.getVignoble().getIGP();
						}
						Label label;
						if (isExit || mapCle.get(field) == 1) {
							label = new Label(mapColumnNumber.get(j), i + num_ligne + 1, value, cellformat);
							int width = label.getContents().length();
							if(mapColumnWidth.get(field) < width)
								mapColumnWidth.put(field, width);
							else 
								width = mapColumnWidth.get(field);

							if(field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN)
								sheet.addCell(new jxl.write.Number(mapColumnNumber.get(j), i + num_ligne + 1, Integer.parseInt(value), cellformat));
							else
								sheet.addCell(label);
							sheet.setColumnView(mapColumnNumber.get(j++), width + 1);
						}
					}
				}
				catch (WriteException ex1) {
					resul = -2;
				}
				i++;
			}

			workbook.write();
			try {
				workbook.close();
			}
			catch (WriteException ex2) {
				resul = -2;
			}
		}
		catch (IOException ex) {
			resul = -2;
		}
		return resul;
	}

	/**
	 * write_XLSTab: Fonction d'écriture du ficher Excel des tableaux
	 *
	 * @param file String: Fichier à écrire.
	 * @param _oPlace LinkedList: liste de rangements à écrire
	 *
	 * @return int
	 */
	public static int write_XLSTab(String file, LinkedList<Rangement> _oPlace) {

		int resul = 0;
		int i = 0;
		String title = "";
		int nNbCol = 0;

		try { //Création du fichier
			title = Program.getCaveConfigString("XLS_TAB_TITLE","");
			WritableWorkbook workbook = Workbook.createWorkbook(new File(file));
			String sheet_title = title;
			if (title.length() == 0) {
				sheet_title = Program.getCaveConfigString("XML_TYPE","");
			}
			WritableSheet sheet = workbook.createSheet(sheet_title, 0);

			int size = 0;
			// Titre
			size = Program.getCaveConfigInt("TITLE_TAB_SIZE_XLS", 10);
			WritableFont cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.NO_BOLD, false);
			String bold = "";
			boolean isBold = false;
			bold = Program.getCaveConfigString("BOLD_TAB_XLS", "");
			if (bold.equals("bold")) {
				isBold = true;
			}
			if (isBold) {
				cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.BOLD, false);
			}
			WritableCellFormat cellformat = new WritableCellFormat(cellfont);

			Label titre0 = new Label(0, 0, title, cellformat); //Ajout du titre
			try {
				sheet.addCell(titre0);
			}
			catch (WriteException ex3) {
				resul = -2;
			}

			//propriétés du texte
			size = Program.getCaveConfigInt("TEXT_TAB_SIZE_XLS", 10);
			cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.NO_BOLD, false);

			int nNbLinePart = Program.getCaveConfigInt("EMPTY_LINE_PART_XLS", 1);
			int nNbLinePlace = Program.getCaveConfigInt("EMPTY_LINE_PLACE_XLS", 3);
			try {
				WritableCellFormat cellFormat = new WritableCellFormat(cellfont);
				cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
				cellFormat.setWrap(true);

				boolean not_null = true;

				int nLine = 3;
				for (i = 0; i < _oPlace.size(); i++) {
					not_null = true;
					Rangement place = (Rangement) _oPlace.get(i);
					if (place == null) {
						not_null = false;
					}
					if (not_null) {
						nLine += nNbLinePlace;
						WritableFont titleFont = new WritableFont( WritableFont.ARIAL, 12, WritableFont.BOLD, false );
						WritableCellFormat cellTitle = new WritableCellFormat( titleFont );
						Label aTitle = new Label( 1, nLine, Program.convertStringFromHTMLString( place.getNom() ), cellTitle );
						sheet.addCell( aTitle );
						for (int j = 1; j <= place.getNbEmplacements(); j++) {
							if ( j == 1 ){
								nLine++;
							}else{
								nLine += nNbLinePart;
							}
							if (place.isCaisse())
							{
								for (int k=0; k<place.getNbCaseUse(j - 1); k++)
								{
									nLine++;
									Bouteille b = place.getBouteilleCaisseAt(j - 1, k);
									String sTitle = "";
									if (b != null) {
										// Contenu de la cellule
										if (Program.getCaveConfigInt("XLSTAB_COL0", 1) == 1)
											sTitle += b.getNom();
										if (Program.getCaveConfigInt("XLSTAB_COL1", 0) == 1)
											sTitle += " " + b.getAnnee();
										if (Program.getCaveConfigInt("XLSTAB_COL2", 0) == 1)
											sTitle += " " + b.getType();
										if (Program.getCaveConfigInt("XLSTAB_COL3", 0) == 1)
											sTitle += " " + b.getPrix() + Program.getCaveConfigString("DEVISE","");
										sTitle.trim();
									}
									Label aLabel = new Label(1, nLine, Program.convertStringFromHTMLString(sTitle), cellFormat);
									sheet.addCell(aLabel);
								}
							}else{
								for (int k = 1; k <= place.getNbLignes(j - 1); k++) {
									nLine++;
									int nCol = place.getNbColonnes(j - 1, k - 1);
									if (nCol > nNbCol)
										nNbCol = nCol;
									for (int l = 1; l <= nCol; l++) {
										Bouteille b = place.getBouteille(j - 1, k - 1, l - 1);
										String sTitle = "";
										if (b != null) {
											// Contenu de la cellule
											if (Program.getCaveConfigInt("XLSTAB_COL0", 1) == 1)
												sTitle += b.getNom();
											if (Program.getCaveConfigInt("XLSTAB_COL1", 0) == 1)
												sTitle += " " + b.getAnnee();
											if (Program.getCaveConfigInt("XLSTAB_COL2", 0) == 1)
												sTitle += " " + b.getType();
											if (Program.getCaveConfigInt("XLSTAB_COL3", 0) == 1)
												sTitle += " " + b.getPrix() + Program.getCaveConfigString("DEVISE","");
											sTitle.trim();
										}
										Label aLabel = new Label(l, nLine, Program.convertStringFromHTMLString(sTitle), cellFormat);
										sheet.addCell(aLabel);
									}
								}
							}
						}
					}
				}
			}
			catch (WriteException ex3) {
				resul = -2;
			}
			int nWidth = Program.getCaveConfigInt("COLUMN_TAB_WIDTH_XLS", 10);
			for (i = 1; i <= nNbCol; i++ )
			{
				sheet.setColumnView( i, nWidth );
			}

			workbook.write();
			try {
				workbook.close();
			}
			catch (WriteException ex2) {
				resul = -2;
			}
		}
		catch (IOException ex) {
			resul = -2;
		}
		return resul;
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
	 * findRangementToCreate
	 */
	public static void findRangementToCreate() {

		StringBuilder html = new StringBuilder();

		html.append("<html><body><p align=center><font size=4pt><b>");
		html.append(Program.convertToHTMLString(Program.getLabel("Infos266")));
		html.append("</b></font></p><p><ul>");
		LinkedList<String> missingPlace = new LinkedList<String>();
		for( Bouteille bottle: Program.getStorage().getAllList() )
		{
			String place = bottle.getEmplacement();
			if (place != null && !place.isEmpty() && Program.getCave(place) == null && !missingPlace.contains(place))
				missingPlace.add(place);
		}
		for(String s: missingPlace)
			html.append("<li>" + s);

		html.append("</ul></p></body></html>");
		if (missingPlace.isEmpty()) { //Pas de rangement à créer
			html.append("<html><body><p align=center><font size=4pt><b>");
			html.append(Program.convertToHTMLString(Program.getLabel("Infos265")));
			html.append("</b></font></p></body></html>");
		}
		try {
			File file = File.createTempFile("MyCellar", "html");
			file.deleteOnExit();
			FileWriter f = new FileWriter(file);
			f.write(html.toString());
			f.close();
			Program.open(file);
		}
		catch (IOException ioe) {
			Program.showException(ioe, false);
		}
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

		String sText = "";
		if (isCaisse()) {
			sText = "<place name=\"\" IsCaisse=\"true\" NbPlace=\"" + getNbEmplacements() + "\" NumStart=\""+getStartCaisse()+"\"";
			if ( isLimited() )
				sText = sText.concat(" NbLimit=\""+this.getNbColonnesStock()+"\">");
			else
				sText = sText.concat(" NbLimit=\"0\">");
			sText += "<name>" + "<![CDATA["+getNom()+"]]></name>";
			sText += "</place>";
		}else{
			sText = "<place name=\"\" IsCaisse=\"false\" NbPlace=\"" + getNbEmplacements() + "\" >\n";
			for ( int i=0; i<getNbEmplacements(); i++)
			{
				sText = sText + "<internal-place NbLine=\""+getNbLignes(i)+"\">\n";
				for ( int j=0; j<getNbLignes(i); j++)
					sText = sText + "<line NbColumn=\""+getNbColonnes(i,j)+"\"/>\n";
				sText = sText + "</internal-place>\n";
			}
			sText += "<name>" + "<![CDATA["+getNom()+"]]></name>";
			sText = sText + "</place>";
		}
		return sText;
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
			if ( hasFreeSpaceInCaisse(i) )
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

