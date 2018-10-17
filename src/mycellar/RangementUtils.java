package mycellar;

import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.4
 * @since 17/10/18
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

		int cle0 = Program.getCaveConfigInt("SIZE_COL0EXPORT_CSV", 1);
		int cle1 = Program.getCaveConfigInt("SIZE_COL1EXPORT_CSV", 1);
		int cle2 = Program.getCaveConfigInt("SIZE_COL2EXPORT_CSV", 1);
		int cle3 = Program.getCaveConfigInt("SIZE_COL3EXPORT_CSV", 1);
		int cle4 = Program.getCaveConfigInt("SIZE_COL4EXPORT_CSV", 1);
		int cle5 = Program.getCaveConfigInt("SIZE_COL5EXPORT_CSV", 1);
		int cle6 = Program.getCaveConfigInt("SIZE_COL6EXPORT_CSV", 1);
		int cle7 = Program.getCaveConfigInt("SIZE_COL7EXPORT_CSV", 1);
		int cle8 = Program.getCaveConfigInt("SIZE_COL8EXPORT_CSV", 1);

		File f = new File(fichier);

		try (FileWriter ficout = new FileWriter(f)){

			for (Bouteille b : all) {
				if (cle0 == 1) {
					String name = Program.convertStringFromHTMLString(b.getNom());
					name = name.replaceAll("\"", "\"\"");
					ficout.write("\"" + name + "\"" + separator);
				}
				if (cle1 == 1) {
					String year = b.getAnnee();
					year = year.replaceAll("\"", "\"\"");
					ficout.write("\"" + year + "\"" + separator);
				}
				if (cle2 == 1) {
					String half = Program.convertStringFromHTMLString(b.getType());
					half = half.replaceAll("\"", "\"\"");
					ficout.write("\"" + half + "\"" + separator);
				}
				if (cle3 == 1) {
					String place = Program.convertStringFromHTMLString(b.getEmplacement());
					place = place.replaceAll("\"", "\"\"");
					ficout.write("\"" + place + "\"" + separator);
				}
				if (cle4 == 1) {
					ficout.write("\"" + b.getNumLieu() + "\"" + separator);
				}
				if (cle5 == 1) {
					ficout.write("\"" + b.getLigne() + "\"" + separator);
				}
				if (cle6 == 1) {
					ficout.write("\"" + b.getColonne() + "\"" + separator);
				}
				if (cle7 == 1) {
					String price = Program.convertStringFromHTMLString(b.getPrix());
					price = price.replaceAll("\"", "\"\"");
					ficout.write("\"" + price + "\"" + separator);
				}
				if (cle8 == 1) {
					String comment = Program.convertStringFromHTMLString(b.getComment());
					comment = comment.replaceAll("\"", "\"\"");
					ficout.write("\"" + comment + "\"" + separator);
				}
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
	static boolean write_XLS(final String file, final List<Bouteille> all, boolean isExit) {

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

		try (Workbook workbook = new SXSSFWorkbook(100)) { //Création du fichier
			int columnsCount = 0;
			String sheet_title = title;
			if (sheet_title.isEmpty()) {
				sheet_title = Program.getCaveConfigString("XML_TYPE","");
			}
			if (sheet_title.isEmpty()) {
				sheet_title = Program.getLabel("Infos389");
			}
			SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();
			workbook.setSheetName(0, sheet_title);

			if (!isExit) { //Export XLS
				//Taille du titre
				int size = Program.getCaveConfigInt("TITLE_SIZE_XLS", 10);
				Font cellfont = workbook.createFont();
				cellfont.setFontName("Arial");
				cellfont.setFontHeightInPoints((short) size);
				cellfont.setBold("bold".equals(Program.getCaveConfigString("BOLD_XLS", "")));
				XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
				cellStyle.setFont(cellfont);

				final SXSSFRow row = sheet.createRow(0);
				final Cell cell = row.createCell(0);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(title);
			}

			final Font cellfont = workbook.createFont();
			cellfont.setFontName("Arial");
			if (!isExit) { //Export XLS
				cellfont.setFontHeightInPoints((short) Program.getCaveConfigInt("TEXT_SIZE_XLS", 10));
			} else {
				cellfont.setFontHeightInPoints((short) 10);
			}
			final XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
			cellStyle.setFont(cellfont);

			i = 0;
			if(isExit) {
				final SXSSFRow row = sheet.createRow(num_ligne);
				for(MyCellarFields field : fields) {
					columnsCount++;
					sheet.trackColumnForAutoSizing(i);
					final Cell cell = row.createCell(i++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(field.toString());
				}
			}	else {
				final SXSSFRow row = sheet.createRow(num_ligne);
				for(MyCellarFields field : fields) {
					if(mapCle.get(field)) {
						columnsCount++;
						sheet.trackColumnForAutoSizing(i);
						final Cell cell = row.createCell(i++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(field.toString());
					}
				}
			}

			i = 0;
			for (Bouteille b : all) {
				int j = 0;
				org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + num_ligne + 1);
				row.setRowStyle(cellStyle);
				for(MyCellarFields field : fields) {
					String value = MyCellarFields.getValue(field, b);
					if (isExit || mapCle.get(field)) {
						final Cell cell = row.createCell(mapColumnNumber.get(j));
						if(field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN) {
							cell.setCellValue(Integer.parseInt(value));
						} else {
							cell.setCellValue(value);
						}
					}
					j++;
				}
				i++;
			}
			for (i=0; i<columnsCount; i++) {
				sheet.autoSizeColumn(i);
			}
			workbook.write(new FileOutputStream(new File(file)));
		}
		catch (IOException ex) {
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

		try (Workbook workbook = new SXSSFWorkbook(100)) { //Création du fichier
			String title = Program.getCaveConfigString("XLS_TAB_TITLE", Program.getCaveConfigString("XML_TYPE",""));
			boolean onePlacePerSheet = 1 == Program.getCaveConfigInt("ONE_PER_SHEET_XLS", 0);

			if (title.isEmpty()) {
				title = Program.getLabel("Infos001");
			}
			int count = 0;
			SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();
			workbook.setSheetName(count++, title);

			// Titre
			int size = Program.getCaveConfigInt("TITLE_TAB_SIZE_XLS", 10);

			Font cellfont = workbook.createFont();
			cellfont.setFontName("Arial");
			cellfont.setFontHeightInPoints((short) size);
			cellfont.setBold("bold".equals(Program.getCaveConfigString("BOLD_XLS", "")));
			CellStyle cellStyleTitle = workbook.createCellStyle();
			cellStyleTitle.setFont(cellfont);

			final SXSSFRow row = sheet.createRow(0);
			final Cell cell = row.createCell(0);
			cell.setCellStyle(cellStyleTitle);
			cell.setCellValue(title);

			//propriétés du texte
			size = Program.getCaveConfigInt("TEXT_TAB_SIZE_XLS", 10);

			int nNbCol = 0;
			int nNbLinePart = Program.getCaveConfigInt("EMPTY_LINE_PART_XLS", 1);
			int nNbLinePlace = Program.getCaveConfigInt("EMPTY_LINE_PLACE_XLS", 3);

			cellfont.setFontHeightInPoints((short) size);
			cellfont.setBold(false);
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(cellfont);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setWrapText(true);

			int nLine = 3;
			boolean firstSheet = true;
			for (Rangement place : _oPlace) {
				if (onePlacePerSheet) {
					if (firstSheet) {
						workbook.setSheetName(0, place.getNom());
						firstSheet = false;
					} else {
						sheet = (SXSSFSheet) workbook.createSheet();
						workbook.setSheetName(count++, place.getNom());
					}
					nLine = 0;
				}
				nLine += nNbLinePlace;
				final SXSSFRow rowPlace = sheet.createRow(nLine);
				final Cell cellPlace = rowPlace.createCell(1);
				cellPlace.setCellStyle(cellStyle);
				cellPlace.setCellValue(place.getNom());
				for (int j = 1; j <= place.getNbEmplacements(); j++) {
					if (j == 1){
						nLine++;
					} else {
						nLine += nNbLinePart;
					}
					if (place.isCaisse()) {
						for (int k=0; k<place.getNbCaseUse(j - 1); k++) {
							nLine++;
							final Bouteille b = place.getBouteilleCaisseAt(j - 1, k);
							if (b != null) {
								// Contenu de la cellule
								final SXSSFRow rowBottle = sheet.createRow(nLine);
								final Cell cellBottle = rowBottle.createCell(1);
								cellBottle.setCellValue(getLabelToDisplay(b));
								cellBottle.setCellStyle(cellStyle);
							}
						}
					} else {
						for (int k = 1; k <= place.getNbLignes(j - 1); k++) {
							nLine++;
							int nCol = place.getNbColonnes(j - 1, k - 1);
							if (nCol > nNbCol) {
								nNbCol = nCol;
							}
							final SXSSFRow rowBottle = sheet.createRow(nLine);
							for (int l = 1; l <= nCol; l++) {
								final Bouteille b = place.getBouteille(j - 1, k - 1, l - 1);
								final Cell cellBottle = rowBottle.createCell(l);
								cellBottle.setCellValue(getLabelToDisplay(b));
								cellBottle.setCellStyle(cellStyle);
							}
						}
					}
				}
				int nWidth = Program.getCaveConfigInt("COLUMN_TAB_WIDTH_XLS", 10) * 400;
				for (int i = 1; i <= nNbCol; i++) {
					sheet.setColumnWidth(i, nWidth);
				}
			}

			workbook.write(new FileOutputStream(new File(file)));
		}	catch (IOException ex) {
			Program.showException(ex, false);
		}
	}

	private static String getLabelToDisplay(Bouteille b) {
		if (b == null) {
			return "";
		}
		StringBuilder sTitle = new StringBuilder();
		// Contenu de la cellule
		if (1 == Program.getCaveConfigInt("XLSTAB_COL0", 1)) {
			sTitle.append(b.getNom());
		}
		if (1 == Program.getCaveConfigInt("XLSTAB_COL1", 0)) {
			sTitle.append(" ").append(b.getAnnee());
		}
		if (1 == Program.getCaveConfigInt("XLSTAB_COL2", 0)) {
			sTitle.append(" ").append(b.getType());
		}
		if (1 == Program.getCaveConfigInt("XLSTAB_COL3", 0)) {
			sTitle.append(" ").append(b.getPrix()).append(Program.getCaveConfigString("DEVISE", ""));
		}
		return sTitle.toString().trim();
	}

	/**
	 * findRangementToCreate
	 */
	static void findRangementToCreate() {

		final Map<String, LinkedList<Part>> rangements = new HashMap<>();
		for( Bouteille bottle: Program.getStorage().getAllList() ) {
			updatePlaceMapToCreate(rangements, bottle);
		}
		for (MyCellarError error : Program.getErrors()) {
			final Bouteille bottle = error.getBottle();
			updatePlaceMapToCreate(rangements, bottle);
		}

		new RangementCreationDialog(rangements);
	}

	private static void updatePlaceMapToCreate(Map<String, LinkedList<Part>> rangements, Bouteille bottle) {
		final String place = bottle.getEmplacement();
		if (place != null && !place.isEmpty() && Program.getCave(place) == null) {
			if (!rangements.containsKey(place)) {
				rangements.put(place, new LinkedList<>());
			} else {
				LinkedList<Part> rangement = rangements.get(place);
				while (rangement.size() <= bottle.getNumLieu()) {
					rangement.add(new Part(rangement.size() + 1));
				}
				final Part part = rangement.get(bottle.getNumLieu() == 0 ? 0 : bottle.getNumLieu() - 1);
				if (part.getRowSize() < bottle.getLigne()) {
					part.setRows(bottle.getLigne());
				}
				if (bottle.getLigne() > 0) {
					final Row row = part.getRow(bottle.getLigne() - 1);
					if (row.getCol() < bottle.getColonne()) {
						row.setCol(bottle.getColonne());
					}
				}
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

