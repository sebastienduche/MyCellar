package mycellar;

import jxl.Workbook;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.0
 * @since 08/06/18
 */
public class RangementUtils {

	private RangementUtils() {}

	/**
	 * write_CSV: Ecriture d'un fichier CSV
	 *
	 * @param fichier String: fichier CSV à écrire
	 * @param all List<Bouteille>: stock de bouteille
	 *
	 * @return int
	 */
	static boolean write_CSV(String fichier, List<Bouteille> all) {

		String separator = Program.getCaveConfigString("SEPARATOR_DEFAULT", ";");

		String cle0 = Program.getCaveConfigString("SIZE_COL0EXPORT_CSV", "1");
		String cle1 = Program.getCaveConfigString("SIZE_COL1EXPORT_CSV", "1");
		String cle2 = Program.getCaveConfigString("SIZE_COL2EXPORT_CSV", "1");
		String cle3 = Program.getCaveConfigString("SIZE_COL3EXPORT_CSV", "1");
		String cle4 = Program.getCaveConfigString("SIZE_COL4EXPORT_CSV", "1");
		String cle5 = Program.getCaveConfigString("SIZE_COL5EXPORT_CSV", "1");
		String cle6 = Program.getCaveConfigString("SIZE_COL6EXPORT_CSV", "1");
		String cle7 = Program.getCaveConfigString("SIZE_COL7EXPORT_CSV", "1");
		String cle8 = Program.getCaveConfigString("SIZE_COL8EXPORT_CSV", "1");

		File f = new File(fichier);

		try (FileWriter ficout = new FileWriter(f)){
			ficout.flush();

			for (Bouteille b : all) {
				if (cle0.equals("1")) {
					String name = Program.convertStringFromHTMLString(b.getNom());
					name = name.replaceAll("\"", "\"\"");
					ficout.write("\"" + name + "\"" + separator);
					ficout.flush();
				}
				if (cle1.equals("1")) {
					String year = b.getAnnee();
					year = year.replaceAll("\"", "\"\"");
					ficout.write("\"" + year + "\"" + separator);
					ficout.flush();
				}
				if (cle2.equals("1")) {
					String half = Program.convertStringFromHTMLString(b.getType());
					half = half.replaceAll("\"", "\"\"");
					ficout.write("\"" + half + "\"" + separator);
					ficout.flush();
				}
				if (cle3.equals("1")) {
					String place = Program.convertStringFromHTMLString(b.getEmplacement());
					place = place.replaceAll("\"", "\"\"");
					ficout.write("\"" + place + "\"" + separator);
					ficout.flush();
				}
				if (cle4.equals("1")) {
					ficout.write("\"" + b.getNumLieu() + "\"" + separator);
					ficout.flush();
				}
				if (cle5.equals("1")) {
					ficout.write("\"" + b.getLigne() + "\"" + separator);
					ficout.flush();
				}
				if (cle6.equals("1")) {
					ficout.write("\"" + b.getColonne() + "\"" + separator);
					ficout.flush();
				}
				if (cle7.equals("1")) {
					String price = Program.convertStringFromHTMLString(b.getPrix());
					price = price.replaceAll("\"", "\"\"");
					ficout.write("\"" + price + "\"" + separator);
					ficout.flush();
				}
				if (cle8.equals("1")) {
					String comment = Program.convertStringFromHTMLString(b.getComment());
					comment = comment.replaceAll("\"", "\"\"");
					ficout.write("\"" + comment + "\"" + separator);
					ficout.flush();
				}
				ficout.flush();
				ficout.write('\n');
				ficout.flush();
			}
			ficout.flush();
		}
		catch (IOException ioe) {
			Erreur.showSimpleErreur(Program.getError("Error120"), Program.getError("Error161"));
			return false;
		}
		return true;
	}

	/**
	 * write_HTML: Ecriture du fichier HTML
	 *
	 * @param fichier String: fichier HTML à écrire
	 * @param all List<Bouteille>: stock de bouteilles
	 * @param fields 
	 *
	 *  @return int
	 */
	static boolean write_HTML(String fichier, List<Bouteille> all, List<MyCellarFields> fields) {

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

			TransformerFactory transformerFactory =	TransformerFactory.newInstance();
			Transformer transformer =	transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result =	new StreamResult(new File(fichier));
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
	 * @param all List<Bouteille>: Tableau de bouteilles à écrire
	 * @param isExit boolean: True si appel pour la création automatique d'une sauvegarde Excel
	 *
	 * @return boolean
	 */
	static boolean write_XLS(String file, List<Bouteille> all, boolean isExit) {

		Debug( "write_XLS: writing file: "+file );
		if(file.isEmpty()) {
			Debug( "write_XLS: ERROR: File not defined!" );
			return false;
		}
		try {
			File f = new File(file);
			String sDir = f.getParent();
			if(null != sDir) {
				f = new File(sDir);
				if(!f.exists()) {
					Debug( "write_XLS: ERROR: directory "+sDir+" don't exist." );
					Debug( "write_XLS: ERROR: Unable to write XLS file" );
					return false;
				}
			}
		} catch(Exception e) {
			Program.showException(e, false);
			Debug( "write_XLS: ERROR: with file " + file );
			return false;
		}
		boolean resul = true;

		EnumMap<MyCellarFields, Boolean> mapCle = new EnumMap<>(MyCellarFields.class);

		//Récupération des colonnes à exporter
		ArrayList<MyCellarFields> fields = MyCellarFields.getFieldsList();
		int i=0;
		for(MyCellarFields field : fields) {
			mapCle.put(field, Program.getCaveConfigInt("SIZE_COL"+i+"EXPORT_XLS", 1) == 1);
			i++;
		}

		HashMap<Integer, Integer> mapColumnNumber = new HashMap<>();
		int num_ligne;
		String title = "";
		if (isExit) { //Cas sauvegarde XLS Backup
			num_ligne = 0;
			for(i= 0; i<fields.size(); i++) {
				mapColumnNumber.put(i, i);
			}
		}
		else { // Export XLS
			title = Program.getCaveConfigString("XLS_TITLE", ""); //Récupération du titre du XLS

			num_ligne = 2; //Affectation des numéros de colonnes
			i=0;
			int value = 0;
			for(MyCellarFields field : fields) {
				if(mapCle.get(field)) {
					mapColumnNumber.put(i, value);
					value++;
				}
				i++;
			}
		}

		try { //Création du fichier
			WritableWorkbook workbook = Workbook.createWorkbook(new File(file));
			String sheet_title = title;
			if (sheet_title.isEmpty()) {
				sheet_title = Program.getCaveConfigString("XML_TYPE","");
			}
			if( sheet_title.isEmpty() )
				sheet_title = Program.getLabel("Infos389");
			WritableSheet sheet = workbook.createSheet(sheet_title, 0);

			if (!isExit) { //Export XLS
				//Taille du titre
				int size = Program.getCaveConfigInt("TITLE_SIZE_XLS", 10);
				WritableFont cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.NO_BOLD, false);
				if ("bold".equals(Program.getCaveConfigString("BOLD_XLS", ""))) {
					cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.BOLD, false);
				}
				WritableCellFormat cellformat = new WritableCellFormat(cellfont);

				Label titre0 = new Label(0, 0, title, cellformat); //Ajout du titre
				try {
					sheet.addCell(titre0);
				}
				catch (WriteException ex3) {
					Program.showException(ex3, false);
					resul = false;
				}
			}

			WritableFont cellfont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
			if (!isExit) { //Export XLS
				//propriétés du texte
				int size = Program.getCaveConfigInt("TEXT_SIZE_XLS", 10);
				cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.NO_BOLD, false);
			}
			WritableCellFormat cellformat = new WritableCellFormat(cellfont);
			//Ajout titre colonne
			EnumMap<MyCellarFields, Integer> mapColumnWidth = new EnumMap<>(MyCellarFields.class);

			try {
				//Ajout Titre
				if(isExit) {
					i=0;
					for(MyCellarFields field : fields) {
						Label label;
						sheet.addCell(label = new Label(i++, num_ligne, field.toString(), cellformat));
						mapColumnWidth.put(field, label.getContents().length());
					}
				}
				else {
					i=0;
					for(MyCellarFields field : fields) {
						if(mapCle.get(field)) {
							Label label;
							sheet.addCell(label = new Label(i++, num_ligne, field.toString(), cellformat));
							mapColumnWidth.put(field, label.getContents().length());
						}
					}
				}
			}
			catch (WriteException ex3) {
				Program.showException(ex3, false);
				resul = false;
			}

			i = 0;
			for (Bouteille b : all) {
				int j = 0;
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
							if(c != null) {
								value = c.toString();
							}
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
					if (isExit || mapCle.get(field)) {
						if(value == null) {
							value = "";
						}
						label = new Label(mapColumnNumber.get(j), i + num_ligne + 1, value, cellformat);
						int width = label.getContents().length();
						if(mapColumnWidth.get(field) < width) {
							mapColumnWidth.put(field, width);
						} else {
							width = mapColumnWidth.get(field);
						}

						if(field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN) {
							sheet.addCell(new jxl.write.Number(mapColumnNumber.get(j), i + num_ligne + 1, Integer.parseInt(value), cellformat));
						} else {
							sheet.addCell(label);
						}
						sheet.setColumnView(mapColumnNumber.get(j), width + 1);
					}
					j++;
				}
				i++;
			}

			workbook.write();
			workbook.close();
		}
		catch (IOException | WriteException ex) {
			Program.showException(ex, false);
			resul = false;
		}
		return resul;
	}

	/**
	 * write_XLSTab: Fonction d'écriture du ficher Excel des tableaux
	 *
	 * @param file String: Fichier à écrire.
	 * @param _oPlace LinkedList: liste de rangements à écrire
	 *
	 */
	static void write_XLSTab(String file, List<Rangement> _oPlace) {

		try { //Création du fichier
			String title = Program.getCaveConfigString("XLS_TAB_TITLE", Program.getCaveConfigString("XML_TYPE",""));
			boolean onePlacePerSheet = 1 == Program.getCaveConfigInt("ONE_PER_SHEET_XLS", 0);
			WritableWorkbook workbook = Workbook.createWorkbook(new File(file));
			if (title.isEmpty()) {
				title = Program.getLabel("Infos001");
			}
			int count = 0;
			WritableSheet sheet = workbook.createSheet(title, count++);

			// Titre
			int size = Program.getCaveConfigInt("TITLE_TAB_SIZE_XLS", 10);
			boolean isBold = "bold".equals(Program.getCaveConfigString("BOLD_TAB_XLS", ""));
			WritableFont cellfont = new WritableFont(WritableFont.ARIAL, size, isBold ? WritableFont.BOLD : WritableFont.NO_BOLD, false);

			try {
				WritableCellFormat cellformat = new WritableCellFormat(cellfont);
				Label titre0 = new Label(0, 0, title, cellformat); //Ajout du titre
				sheet.addCell(titre0);
			}
			catch (WriteException ex3) {
				Program.showException(ex3, false);
			}

			//propriétés du texte
			size = Program.getCaveConfigInt("TEXT_TAB_SIZE_XLS", 10);
			cellfont = new WritableFont(WritableFont.ARIAL, size, WritableFont.NO_BOLD, false);

			int nNbCol = 0;
			int nNbLinePart = Program.getCaveConfigInt("EMPTY_LINE_PART_XLS", 1);
			int nNbLinePlace = Program.getCaveConfigInt("EMPTY_LINE_PLACE_XLS", 3);
			WritableCellFormat cellFormat = new WritableCellFormat(cellfont);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			cellFormat.setWrap(true);

			int nLine = 3;
			WritableFont titleFont = new WritableFont( WritableFont.ARIAL, 12, WritableFont.BOLD, false );
			WritableCellFormat cellTitle = new WritableCellFormat( titleFont );
			boolean firstSheet = true;
			for (Rangement place : _oPlace) {
				if (onePlacePerSheet) {
					if (firstSheet) {
						sheet.setName(place.getNom());
						firstSheet = false;
					} else {
						sheet = workbook.createSheet(place.getNom(), count++);
					}
					nLine = 0;
				}
				nLine += nNbLinePlace;
				sheet.addCell(new Label( 1, nLine, Program.convertStringFromHTMLString(place.getNom()), cellTitle ));
				for (int j = 1; j <= place.getNbEmplacements(); j++) {
					if (j == 1){
						nLine++;
					} else {
						nLine += nNbLinePart;
					}
					if (place.isCaisse()) {
						for (int k=0; k<place.getNbCaseUse(j - 1); k++) {
							nLine++;
							Bouteille b = place.getBouteilleCaisseAt(j - 1, k);
							if (b != null) {
								// Contenu de la cellule
								sheet.addCell(new Label(1, nLine, Program.convertStringFromHTMLString(getLabelToDisplay(b)), cellFormat));
							}
						}
					}else{
						for (int k = 1; k <= place.getNbLignes(j - 1); k++) {
							nLine++;
							int nCol = place.getNbColonnes(j - 1, k - 1);
							if (nCol > nNbCol) {
								nNbCol = nCol;
							}
							for (int l = 1; l <= nCol; l++) {
								Bouteille b = place.getBouteille(j - 1, k - 1, l - 1);
								if (b != null) {
									sheet.addCell(new Label(l, nLine, Program.convertStringFromHTMLString(getLabelToDisplay(b)), cellFormat));
								}
							}
						}
					}
				}
			}

			int nWidth = Program.getCaveConfigInt("COLUMN_TAB_WIDTH_XLS", 10);
			for (int i = 1; i <= nNbCol; i++) {
				sheet.setColumnView(i, nWidth);
			}

			workbook.write();
			workbook.close();
		}	catch (IOException | WriteException ex) {
			Program.showException(ex, false);
		}
	}

	private static String getLabelToDisplay(Bouteille b) {
		StringBuilder sTitle = new StringBuilder();
		// Contenu de la cellule
		if (1 == Program.getCaveConfigInt("XLSTAB_COL0", 1)) {
			sTitle.append(b.getNom());
		}
		if (Program.getCaveConfigInt("XLSTAB_COL1", 0) == 1) {
			sTitle.append(" ").append(b.getAnnee());
		}
		if (Program.getCaveConfigInt("XLSTAB_COL2", 0) == 1) {
			sTitle.append(" ").append(b.getType());
		}
		if (Program.getCaveConfigInt("XLSTAB_COL3", 0) == 1) {
			sTitle.append(" ").append(b.getPrix()).append(Program.getCaveConfigString("DEVISE", ""));
		}
		return sTitle.toString().trim();
	}

	/**
	 * findRangementToCreate
	 */
	static void findRangementToCreate() {

		StringBuilder html = new StringBuilder();

		html.append("<html><body><p align=center><font size=4pt><b>");
		html.append(Program.convertToHTMLString(Program.getLabel("Infos266")));
		html.append("</b></font></p><p><ul>");
		LinkedList<String> missingPlace = new LinkedList<>();
		for( Bouteille bottle: Program.getStorage().getAllList() ) {
			String place = bottle.getEmplacement();
			if (place != null && !place.isEmpty() && Program.getCave(place) == null && !missingPlace.contains(place))
				missingPlace.add(place);
		}
		for(String s: missingPlace) {
			html.append("<li>").append(s);
		}

		html.append("</ul></p></body></html>");
		if (missingPlace.isEmpty()) { //Pas de rangement à créer
			html.append("<html><body><p align=center><font size=4pt><b>");
			html.append(Program.convertToHTMLString(Program.getLabel("Infos265")));
			html.append("</b></font></p></body></html>");
		}
		File file = null;
		try {
			file = File.createTempFile("MyCellar", "html");
			file.deleteOnExit();
		} catch (IOException e) {
			Program.showException(e, false);
		}
		if (file != null) {
			try (FileWriter f = new FileWriter(file)){
				f.write(html.toString());
				f.close();
				Program.open(file);
			}
			catch (IOException ioe) {
				Program.showException(ioe, false);
			}
		}
	}
	
	public static void putTabStock() {
		Debug("putTabStock ...");
		for (MyCellarError error : Program.getErrors()) {
			Program.getStorage().getAllList().add(error.getBottle());
		}
		Program.getErrors().clear();
		for(Rangement rangement : Program.getCave())
			rangement.resetStock();
		
		for(Bouteille b : Program.getStorage().getAllList()) {
			Rangement rangement = Program.getCave(b.getEmplacement());
			if(rangement == null) {
				// Rangement inexistant
				Debug("ERROR: Inexisting place: " + b.getNom() + " place: "+b.getEmplacement());
				Program.addError(new MyCellarError(MyCellarError.ID.INEXISTING_PLACE, b, b.getEmplacement()));
				continue;
			}
			if(rangement.isCaisse()) {
				if(!rangement.isExistingNumPlace(b.getNumLieu())) {
					// Numero de rangement inexistant
					Debug("ERROR: Inexisting numplace: " + b.getNom() + " numplace: "+b.getNumLieu() + " for place "+b.getEmplacement());
					Program.addError(new MyCellarError(MyCellarError.ID.INEXISTING_NUM_PLACE, b, b.getEmplacement(), b.getNumLieu()));
					continue;
				}
				if(rangement.hasFreeSpaceInCaisse(b.getNumLieu()))
					rangement.updateToStock(b);
				else {
					// Caisse pleine
					Debug("ERROR: simple place full for numplace: " + b.getNom() + " numplace: "+b.getNumLieu() + " for place "+b.getEmplacement());
					Program.addError(new MyCellarError(MyCellarError.ID.FULL_BOX, b, b.getEmplacement(), b.getNumLieu()));
				}
			}
			else {
				Bouteille bottle;
				if(!rangement.isExistingNumPlace(b.getNumLieu() - 1)) {
					// Numero de rangement inexistant
					Debug("ERROR: Inexisting numplace: " + b.getNom() + " numplace: "+ (b.getNumLieu()-1) + " for place "+b.getEmplacement());
					Program.addError(new MyCellarError(MyCellarError.ID.INEXISTING_NUM_PLACE, b, b.getEmplacement()));
					continue;
				}
				if(!rangement.isExistingCell(b.getNumLieu() - 1, b.getLigne() - 1, b.getColonne() - 1)) {
					// Cellule inexistante
					Debug("ERROR: Inexisting cell: " + b.getNom() + " numplace: "+(b.getNumLieu()-1)+ ", line: " + (b.getLigne()-1) + ", column:" + (b.getColonne()-1) + " for place "+b.getEmplacement());
					Program.addError(new MyCellarError(MyCellarError.ID.INEXISTING_CELL, b, b.getEmplacement(), b.getNumLieu()));
				}
				else if((bottle = rangement.getBouteille(b.getNumLieu() - 1, b.getLigne() - 1, b.getColonne() - 1)) != null && !bottle.equals(b)){
					// Cellule occupée
					Debug("ERROR: Already occupied: " + b.getNom() + " numplace: "+(b.getNumLieu()-1)+ ", line: " + (b.getLigne()-1) + ", column:" + (b.getColonne()-1) + " for place "+b.getEmplacement());
					Program.addError(new MyCellarError(MyCellarError.ID.CELL_FULL, b, b.getEmplacement(), b.getNumLieu()));
				}
				else
					rangement.updateToStock(b);
			}
		}
		// Suppression des bouteilles posant problème
		for(MyCellarError error : Program.getErrors()) {
			Program.getStorage().deleteWine(error.getBottle());
		}
		Debug("putTabStock Done");
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("RangementUtils: " + sText);
	}
}

