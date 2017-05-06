package mycellar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
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
 * @version 24.3
 * @since 01/05/17
 */
public class Rangement implements Serializable, Comparable<Rangement> {

	private String nom;
	private int nb_emplacements; //Nombre de cases dans nb_lignes[] et nombre d'emplacement dans stockage
	private int nb_lignes[];
	private int nb_colonnes[];
	private int nbc; //Nombre de cases dans nb_colonnes[] = nombre de lignes
	private int stock_nbcol; //Nombre max de colonnes dans stockage par emplacement
	private int stock_nblign; //Nombre max de lignes dans stockage par emplacement
	private boolean caisse; //indique si le rangement est une caisse (pas de limite de taille)
	private int start_caisse; //Indique l'indice de démarrage des caisses
	private Bouteille stockage[][][]; //Stocke les vins du rangement: stockage[nb_emplacements][stock_nblign][stock_nbcol]
	private static int MAX_ROW = 10;
	private transient LinkedList<Bouteille> out; //Tableau des bouteilles hors du rangement
	private boolean limite; //Indique si une limite de caisse est activée
	private LinkedList<Part> listePartie = null;
	static final long serialVersionUID = 5012007;


	/**
	 * Contructeur de Rangement par copie avec modification du nom
	 * @param r
	 * @param _nom
	 */
	public Rangement(Rangement r, String _nom)
	{
		nom = _nom.trim();
		nb_emplacements = r.getNbEmplacements();
		nb_lignes = r.getNbLignes();
		nb_colonnes = r.getNbColonnes();
		nbc = r.getNbColonnesTotal();
		stock_nbcol = r.getStockNbcol();
		stock_nblign = r.getStockNbligne();
		caisse = r.isCaisse();
		start_caisse = r.getStartCaisse();
		stockage = r.getStockage();
		limite = r.isLimited();
		listePartie = r.getPlace();
	}

	/**
	 * Rangement: Constructeur de création d'un rangement de type Armoire
	 *
	 * @param nom1 String: nom du rangement
	 * @param nb_emplacements1 int: nombre d'emplacement
	 * @param nb_lignes1 int[]: tableau contenant le nombre de ligne par
	 *   emplacement
	 * @param nb_colonnes1 int[]: tableau contenant le nombre de colonnes par
	 *   ligne
	 */
	public Rangement(String nom1, int nb_emplacements1, int nb_lignes1[], int nb_colonnes1[]) {

		int i, j;
		int int_tmp2, nb_col, nb_lign;
		nbc = 0;
		int_tmp2 = 0;
		nom = nom1.trim();
		nb_emplacements = nb_emplacements1;
		nb_lignes = new int[nb_emplacements1];
		start_caisse = 0;
		limite = false;

		for (i = 0; i < nb_emplacements1; i++) {
			nb_lignes[i] = nb_lignes1[i];
			nbc += nb_lignes[i];
		}
		nb_colonnes = new int[nbc];
		for (i = 0; i < nbc; i++) {
			nb_colonnes[i] = nb_colonnes1[i];
		}
		//Récupération du nombre de lignes max
		nb_col = 0;
		nb_lign = 0;
		for (i = 0; i < nb_emplacements; i++) {
			if (nb_lignes[i] > nb_lign) {
				nb_lign = nb_lignes[i];
			}
		}
		//Récupération du nombre de colonnes max
		for (i = 0; i < nb_emplacements; i++) {
			for (j = 0; j < nb_lignes[i]; j++) {
				int_tmp2 = this.getNbColonnes(i, j);
				if (int_tmp2 > nb_col) {
					nb_col = int_tmp2;
				}
			}
		}

		stockage = new Bouteille[nb_emplacements][nb_lign][nb_col];
		stock_nbcol = nb_col;
		stock_nblign = nb_lign;

		caisse = false;
	}
	
	/**
	 * Rangement: Constructeur de création d'un rangement de type Armoire
	 *
	 * @param nom1 String: nom du rangement
	 * @param listPart LinkedList<Part>: liste des parties
	 */
	public Rangement(String nom1, LinkedList<Part> listPart) {
		
		listePartie = new LinkedList<Part>();
		for(int i=0;i<listPart.size(); i++)
		{
			Part part = new Part(listPart.get(i).getNum());
			listePartie.add(part);
			for(int j=0; j<listPart.get(i).getRowSize(); j++)
			{
				part.setRows(listPart.get(i).getRowSize());
				part.getRow(j).setCol(listPart.get(i).getRow(j).getCol());
			}
		}
		int i, j;
		int nb_col, nb_lign;
		nbc = 0;
		nom = nom1.trim();
		nb_emplacements = listePartie.size();
		nb_lignes = new int[nb_emplacements];
		start_caisse = 0;
		limite = false;
		caisse = false;
		nb_col = 0;
		nb_lign = 0;

		for (i = 0; i < nb_emplacements; i++) {
			nb_lignes[i] = listPart.get(i).getRowSize();
			nbc += nb_lignes[i];
			if (nb_lignes[i] > nb_lign) {
				nb_lign = nb_lignes[i];
			}
		}
		nb_colonnes = new int[nbc];
		int index = 0;
		for (i = 0; i < nb_emplacements; i++) {
			for (j = 0; j < listPart.get(i).getRowSize(); j++) {
				nb_colonnes[index] = listPart.get(i).getRow(j).getCol();
				if (nb_colonnes[index] > nb_col) {
					nb_col = nb_colonnes[index];
				}
				index++;
			}
			
		}

		stockage = new Bouteille[nb_emplacements][nb_lign][nb_col];
		stock_nbcol = nb_col;
		stock_nblign = nb_lign;
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
	 * @param nom1 String: nom du rangement
	 * @param nb_emplacement1 int: nombre d'emplacement
	 * @param start_caisse1 int: Numéro de démarrage de l'indice des caisses
	 * @param isLimit boolean: Limite de caisse activée?
	 * @param limite_caisse int: Capacité pour la limite
	 */
	public Rangement(String nom1, int nb_emplacement1, int start_caisse1, boolean isLimit, int limite_caisse) {
		nom = nom1.trim();
		nb_emplacements = nb_emplacement1;
		start_caisse = start_caisse1;

		limite = isLimit;
		if (limite) {
			stock_nbcol = limite_caisse;
		}
		else {
			stock_nbcol = MAX_ROW;
		}

		stock_nblign = 1;
		nbc = 1;
		caisse = true;
		nb_lignes = new int[nb_emplacement1];
		nb_colonnes = new int[2];

		stockage = new Bouteille[nb_emplacements][1][stock_nbcol];
		Program.getStorage().initialize();
	}

	/**
	 * getNom: retourne le nom du rangement
	 *
	 * @return String
	 */
	public String getNom() {
		return nom.trim();
	}

	/**
	 * setNom: Met à jour le nom du rangement
	 *
	 * @param name String
	 */
	public void setNom(String name) {
		nom = name.trim();
	}
	
	public int[] getNbLignes(){
		return nb_lignes;
	}
	
	public int[] getNbColonnes(){
		return nb_colonnes;
	}
	
	public Bouteille[][][] getStockage(){
		return stockage;
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
		if(listePartie != null)
			return listePartie.size();
		return nb_emplacements;
	}

	/**
	 * getNbColonnesTotal: retourne le nombre total de colonnes dans un rangement
	 *
	 * @return int
	 */
	public int getNbColonnesTotal() {
		return nbc;
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
	 * desactivateLimit: Désactive la limite d'une caisse
	 */
	public void desactivateLimit() {
		limite = false;
	}

	/**
	 * getNbLignes: retourne le nombre de lignes d'un emplacement
	 *
	 * @param emplacement int: numéro de l'emplacement (0...n)
	 * @return int
	 */
	public int getNbLignes(int emplacement) { //Renvoie le nombre de lignes d'un emplacement

		if(listePartie != null)
			return listePartie.get(emplacement).getRowSize();
		int lignes;
		try {
			lignes = nb_lignes[emplacement];
		}
		catch (ArrayIndexOutOfBoundsException aiiobe) {
			lignes = -1;
		}
		return lignes;
	}
	
	/**
	 * Indique si la celluce demandée existe
	 * 
	 * @param emplacement
	 * @param ligne
	 * @param col
	 * @return
	 */
	public boolean isExistingCell(int emplacement, int ligne, int col) {
		
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
	public int getNbColonnes(int emplacement, int ligne) { //Nombre de colonne sur une ligne d'un emplacement
		
		if(listePartie != null && !listePartie.isEmpty() && listePartie.size() > emplacement)
			return listePartie.get(emplacement).getRow(ligne).getCol();

		int colonnes;
		int i, j;
		j = 0;

		try {
			for (i = 0; i < emplacement; i++) {
				j += nb_lignes[i];
			}
			i = j + ligne;
			colonnes = nb_colonnes[i];
		}
		catch (ArrayIndexOutOfBoundsException aioobe) {
			colonnes = -1;
		}
		return colonnes;
	}
	
	/**
	 * getNbColonnesMax: retourne le nombre maximal de colonnes d'un emplacement
	 *
	 * @param emplacement int: numéro d'emplacement (0...n)
	 * @return int
	 */
	public int getNbColonnesMax(int emplacement) {
		
		if(listePartie != null && !listePartie.isEmpty() && listePartie.size() > emplacement) {
			int max = 0;
			for(Row row : listePartie.get(emplacement).getRows()) {
				if(max < row.getCol())
					max = row.getCol();
			}
			return max;
		}

		int i, j;
		j = 0;
		int max = 0;

		try {
			for (i = 0; i < emplacement; i++) {
				j += nb_lignes[i];
			}
			i = j;
			for(int k=0; k<getNbLignes(emplacement); k++) {
				if(max < nb_colonnes[i + k])
					max = nb_colonnes[i + k];
			}
		}
		catch (ArrayIndexOutOfBoundsException aioobe) {
			max = -1;
		}
		return max;
	}

	/**
	 * getNbColonnesMax: retourne le nombre maximal de colonnes du rangement
	 *
	 * @return int
	 */
	public int getNbColonnesMax() {
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
		Bouteille b = null;
		try {
			for (int i = 0; i < Program.getStorage().getAllNblign(); i++) {
				b = Program.getStorage().getAllAt(i);
				if (b != null) {
					String tmp = b.getEmplacement();
					if (tmp.equals(getNom())) {
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
	 * getNbCaseUseCaisse: retourne le nombre de bouteille dans l'emplacement
	 * d'une caisse
	 *
	 * @param num_empl int: numéro d'emplacement (0...n)
	 * @return int
	 */
	public int getNbCaseUseCaisse(int num_empl) {

		num_empl -= start_caisse;
		int nLength = stockage[num_empl][0].length;
		int nb_vin = 0;

		try {
			for (int i = 0; i < nLength; i++) {
				if (stockage[num_empl][0][i] != null) {
					nb_vin++;
				}
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return nb_vin;
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
			out = new LinkedList<Bouteille>();
			Bouteille b = null;
			try {
				b = Program.getStorage().getAllAt(0);
			}
			catch (NullPointerException npe) {
				b = null;
			}
			if (b != null) {
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
					stock_nbcol = getStockNbcol();
					stockage = new Bouteille[nb_emplacements][1][stock_nbcol];
					int cpt1[] = new int[nb_emplacements];

					for (int z = 0; z < nb_emplacements; z++) {
						cpt1[z] = 0;
					}
					int cpt = 0;
					int max_partie = 0; //Permet d'avoir le message d'erreur sur le nombre de partie maximal
					for (int i = 0; i < Program.getStorage().getAllNblign(); i++) {
						b = Program.getStorage().getAllAt(i);
						if (b != null) {
							int tmp_num_empl = b.getNumLieu();
							tmp_num_empl -= start_caisse;
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

							if (getNom().equals(tmp_nom)) {
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
							}
						}
					}
				}
			}
			else {
				if (!caisse) { //Initialisation du tableau de stockage
					stockage = new Bouteille[nb_emplacements][stock_nblign][stock_nbcol];
				}
				else {
					stockage = new Bouteille[nb_emplacements][1][stock_nbcol];
				}
			}
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return errors;
	}


	/**
	 * putWine: Ajout d'une bouteille
	 *
	 * @param wine Bouteille: bouteille à ajouter
	 *
	 * @return int
	 */
	public int addWine(Bouteille wine) {
		if(isCaisse())
			return putWineCaisse(wine);
		return putWineStandard(wine);
	}

	/**
	 * putWine: Ajout d'une bouteille dans une caisse
	 *
	 * @param wine Bouteille: bouteille à ajouter
	 *
	 * @return int
	 */
	private int putWineCaisse(Bouteille wine) { //pour les caisse A modifier
		int resul = 0;
		int num_empl = wine.getNumLieu();
		wine.setLigne(0);
		wine.setColonne(0);
		
		Debug("putWineCaisse: "+wine.getNom()+" "+wine.getEmplacement()+" "+wine.getNumLieu());

		try {
			int nb_vin = this.getNbCaseUseCaisse(num_empl);
			num_empl -= start_caisse;
			int nLength = stockage[num_empl][0].length;
			if (nb_vin < nLength) {
				stockage[num_empl][0][nb_vin] = wine;
				nb_vin++;
			}
			else {
				if (!limite) {
					//Agrandissement du tableau de stockage
					int j;
					Bouteille stockage2[][][] = new Bouteille[nb_emplacements][1][nLength * 2];
					for (int z = 0; z < nb_emplacements; z++) {
						for (j = 0; j < stock_nbcol; j++) {
							stockage2[z][0][j] = stockage[z][0][j];
						}
					}
					stockage = stockage2;

					stockage[num_empl][0][nb_vin] = wine;
					nb_vin++;
					stock_nbcol = nLength * 2;
				}
				else {
					resul = -1;
				}
			}
			Program.getStorage().addWine(wine);
		}
		catch (Exception e) {
			Program.showException(e);
		}
		return resul;
	}

	/**
	 * putWineStandard: Ajout d'une bouteille dans un rangement non caisse
	 *
	 * @param wine Bouteille: Bouteille à ajouter
	 */
	private int putWineStandard(Bouteille wine) {
		
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
		}
		return 0;
	}
	
	/**
	 * replaceWine: Remplacement d'une bouteille dans un rangement non caisse
	 *
	 * @param wine Bouteille: Bouteille à remplacer
	 */
	public boolean replaceWine(Bouteille wine) {
		
		Debug("replaceWine: "+wine.getNom()+" "+wine.getEmplacement()+" "+wine.getNumLieu()+" "+wine.getLigne()+" "+wine.getColonne());

		int num_empl = wine.getNumLieu();
		int line = wine.getLigne();
		int column = wine.getColonne();
		try {
			stockage[num_empl - 1][line - 1][column - 1] = wine;
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
	 * moveWine: Déplacement d'une bouteille dans un rangement
	 *
	 * @param bottle Bouteille: Bouteille à déplacer
	 * @param isCaisse boolean: indique si le rangement est une caisse
	 */
	public void moveLineWine(Bouteille bottle, int nNewLine) {
		Program.getStorage().deleteWine(bottle);
		clearStock(bottle);
		bottle.setLigne(nNewLine);
		addWine( bottle );
	}
	
	/**
	 * getBouteille0: retourne la bouteille se trouvant à un emplacement précis
	 *
	 * @param num_empl int: numéro d'emplacement (0...n)
	 * @param line int: numéro de ligne (0...n)
	 * @param column int: numéro de colonne (0...n)
	 * @return Bouteille
	 */
	public Bouteille getBouteille(int num_empl, int line, int column) {
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
		if(isCaisse())
			return;
		try {
			stockage[bottle.getNumLieu() - 1][bottle.getLigne() - 1][bottle.getColonne() - 1] = null;
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * getBouteilleCaisse: retourne la bouteille se trouvant à un emplacement précis
	 *
	 * @param num_empl int: numéro d'emplacement (0...n)
	 * @param column int: numéro de colonne (0...n)
	 * @return Bouteille
	 */

	public Bouteille getBouteilleCaisse(int num_empl, int column) {
		try {
			return stockage[num_empl][0][column];
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
	 * isCaisse: retourne true si c'est une caisse
	 *
	 * @return boolean
	 */
	public static boolean isCaisse(String empl) {
		return Program.getCave().get(convertNom_Int(empl)).isCaisse();
	}

	/**
	 * init_W_XML: Initialise le fichier XML
	 *
	 * @param file_xml FileWriter: Fichier XML
	 * @param XSL_file String: nom du fichier XSL
	 * @param XML_balise String: nom de la balise
	 * @param UTF8 boolean: Format UTF-8
	 * @return int
	 */
	public static int init_W_XML(FileWriter file_xml, String XSL_file, String XML_balise, boolean UTF8) {

		String tmp_XML = null;
		int resul = 0;
		try {
			file_xml.flush();
			if( UTF8 )
				tmp_XML = new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			else
				tmp_XML = new String("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			file_xml.write(tmp_XML);
			file_xml.flush();
			String dir = Program.convertToHTMLString(System.getProperty("user.dir"));

			file_xml.write("<?xml-stylesheet type=\"text/xsl\" href=\"" + dir + "/" + XSL_file + "\"?>\n");
			file_xml.flush();
			file_xml.write("<" + XML_balise + ">\n");
			file_xml.flush();
		}
		catch (IOException ioe1) {
			new Erreur(Program.getError("Error031"), "");
			resul = 1;
		}
		return resul;
	}
	

	/**
	 * convertNom_Int: Convertir un nom de rangement en son numéro dans le
	 * programme
	 *
	 * @param r LinkedList<Rangement>: liste des rangements
	 * @param nom1 String: nom du rangement à convertir
	 * @return int
	 */
	public static int convertNom_Int(String nom1) {

		int resul = 1;
		int i = 0;
		int val = -1;

		if (nom1 == null) {
			return -1;
		}
		nom1 = nom1.trim();
		try {
			do {
				String tmp = Program.getCave(i).getNom();
				if (nom1.compareTo(tmp) == 0) {
					resul = 0;
					val = i;
				}
				i++;
			}
			while (resul != 0 && i < Program.GetCaveLength());
		}
		catch (Exception npe) {
			val = -1;
		}

		return val;
	}

	/**
	 * getOut: retourne le tableau de bouteilles hors rangement
	 *
	 * @return LinkedList<Bouteille>
	 */
	public LinkedList<Bouteille> getOut() {
		return out;
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
								for (int k=0; k<place.getNbCaseUseCaisse(j - 1 + place.getStartCaisse()); k++)
								{
									nLine++;
									Bouteille b = place.getBouteilleCaisse(j - 1, k);
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

	/**
	 * getStockNbcol: Retourne la capacité du lieu.
	 *
	 * @return int
	 */
	public int getStockNbcol() {

		if (!limite) {
			stock_nbcol = MAX_ROW;
		}

		return stock_nbcol;
	}
	
	/**
	 * getStockNbligne: Retourne la capacité du lieu.
	 *
	 * @return int
	 */
	public int getStockNbligne() {
		return stock_nblign;
	}
	
	public boolean isSameColumnNumber(){
		for( int i=0; i<nb_emplacements; i++){
			int nbCol = 0;
			for(int j=0; j<nb_lignes[i];j++){
				if(nbCol == 0)
					nbCol = getNbColonnes(i, j);
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

		String html = "";
		boolean resul = true;

		html = "<html><body><p align=center><font size=4><b>" + Program.convertToHTMLString(Program.getLabel("Infos266")) + "</b></font></p><p><ul>";
		LinkedList<String> missingPlace = new LinkedList<String>();
		for( Bouteille bottle: Program.getStorage().getAllList() )
		{
			String place = bottle.getEmplacement();
			if (place != null && Rangement.convertNom_Int(place) == -1 && place.compareTo("") != 0 && !missingPlace.contains(place))
				missingPlace.add(place);
		}
		for(String s: missingPlace)
			html = html.concat("<li>" + s);

		html = html.concat("</ul></p></body></html>");
		if (missingPlace.isEmpty()) { //Pas de rangement à créer
			html = "<html><body><p align=center><font size=4><b>" + Program.convertToHTMLString(Program.getLabel("Infos265")) + "</b></font></p></body></html>";
		}
		File file = null;
		try {
			file = File.createTempFile("MyCellar", "html");
			file.deleteOnExit();
			FileWriter f = new FileWriter(file);
			f.write(html);
			f.close();
		}
		catch (IOException ioe) {
			Program.showException(ioe, false);
			resul = false;
		}
		if (resul) {
			Program.open(file);
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
				sText = sText.concat(" NbLimit=\""+this.getStockNbcol()+"\">");
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
	 * @param _nEmpl Numero d'emplacement
	 * @param _nLine Numero de ligne
	 * @param _nCol Numero de colonne
	 * @return
	 */
	public boolean canAddBottle(int _nEmpl, int _nLine, int _nCol) {
		if (isCaisse()) {
			if (_nEmpl < getStartCaisse())
				return false;
			if( isLimited() && !hasFreeSpaceInCaisse(_nEmpl))
				return false;
			return true;
		}

		if (_nEmpl == 0 || _nEmpl > getNbEmplacements())
			return false;
		if (_nLine == 0 || _nLine > getNbLignes(_nEmpl))
			return false;
		if (_nCol == 0 || _nCol > getNbColonnes(_nEmpl, _nLine))
			return false;

		return true;
	}


	/**
	 * HasFreeSpaceInCaisse Indique si l'on peut encore ajouter des
	 * bouteilles dans une caisse
	 * 
	 * @param _nEmpl
	 * @return
	 */
	public boolean hasFreeSpaceInCaisse(int _nEmpl) {
		if(!isCaisse())
			return false;

		if(!isLimited())
			return true;

		if(getNbCaseUseCaisse(_nEmpl) == getStockNbcol())
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

		int nStart = getStartCaisse();
		for( int i=nStart; i< (nStart+getNbEmplacements()); i++)
		{
			if ( hasFreeSpaceInCaisse(i) )
				return i;
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
	 * Rangement: Constructeur de création d'un rangement
	 *
	 * @param nom1 String: nom du rangement
	 * @param listPart LinkedList<Part>: liste des parties
	 */
	public void setPlace(LinkedList<Part> listPart) {
		
		listePartie = new LinkedList<Part>();
		for(int i=0;i<listPart.size(); i++)
		{
			Part part = new Part(listPart.get(i).getNum());
			listePartie.add(part);
			for(int j=0; j<listPart.get(i).getRowSize(); j++)
			{
				part.setRows(listPart.get(i).getRowSize());
				part.getRow(j).setCol(listPart.get(i).getRow(j).getCol());
			}
		}
		int i, j;
		int nb_col, nb_lign;
		nbc = 0;
		nb_emplacements = listePartie.size();
		nb_lignes = new int[nb_emplacements];
		start_caisse = 0;
		limite = false;
		caisse = false;
		nb_col = 0;
		nb_lign = 0;

		for (i = 0; i < nb_emplacements; i++) {
			nb_lignes[i] = listPart.get(i).getRowSize();
			nbc += nb_lignes[i];
			if (nb_lignes[i] > nb_lign) {
				nb_lign = nb_lignes[i];
			}
		}
		nb_colonnes = new int[nbc];
		int index = 0;
		for (i = 0; i < nb_emplacements; i++) {
			for (j = 0; j < listPart.get(i).getRowSize(); j++) {
				nb_colonnes[index] = listPart.get(i).getRow(j).getCol();
				if (nb_colonnes[index] > nb_col) {
					nb_col = nb_colonnes[index];
				}
				index++;
			}
			
		}

		stockage = new Bouteille[nb_emplacements][nb_lign][nb_col];
		stock_nbcol = nb_col;
		stock_nblign = nb_lign;
	}
	
	public LinkedList<Part> getPlace() {
		if( listePartie == null || listePartie.isEmpty())
		{
			listePartie = new LinkedList<Part>();
			for( int i=0; i<nb_emplacements; i++)
			{
				Part part = new Part(i+1);
				for(int j= 0; j<nb_lignes[i]; j++)
				{
					Row row = new Row(j+1);
					row.setCol(getNbColonnes(i, j));
					part.getRows().add(row);
				}
				listePartie.add(part);
			}
		}
		
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

}

