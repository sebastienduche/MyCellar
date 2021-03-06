package mycellar.placesmanagement;

import mycellar.BottleColor;
import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Program;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarSettings;
import mycellar.core.datas.jaxb.CountryListJaxb;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.w3c.dom.Element;

import javax.swing.JProgressBar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static mycellar.Program.toCleanString;
import static mycellar.core.MyCellarError.ID.CELL_FULL;
import static mycellar.core.MyCellarError.ID.FULL_BOX;
import static mycellar.core.MyCellarError.ID.INEXISTING_CELL;
import static mycellar.core.MyCellarError.ID.INEXISTING_NUM_PLACE;
import static mycellar.core.MyCellarError.ID.INEXISTING_PLACE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.2
 * @since 29/01/21
 */
public final class RangementUtils {

	private RangementUtils() {}

	/**
	 * write_CSV: Ecriture d'un fichier CSV
	 *
	 * @param fichier String: fichier CSV a ecrire
	 * @param all List<Bouteille>: stock de bouteille
	 * @param progressBar
	 * @return int
	 */
	public static boolean write_CSV(final File fichier, final List<Bouteille> all, final JProgressBar progressBar) {

		String separator = Program.getCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, ";");

		final EnumMap<MyCellarFields, Boolean> map = new EnumMap<>(MyCellarFields.class);
		for (var field : MyCellarFields.getFieldsList()) {
			map.put(field, Program.getCaveConfigBool(MyCellarSettings.EXPORT_CSV + field.name(), false));
		}

		if (progressBar != null) {
			progressBar.setMaximum(all.size());
			progressBar.setMinimum(0);
		}

		try (var fileWriter = new FileWriter(fichier)){

			StringBuilder line = new StringBuilder();
			// Title line
			for (var field : map.keySet()) {
				if (map.get(field)) {
					line.append(field).append(separator);
				}
			}
			line.append('\n');
			fileWriter.write(line.toString());
			int i = 0;
			for (Bouteille b : all) {
				if (progressBar != null) {
					progressBar.setValue(i++);
				}
				line = new StringBuilder();
				final String doubleCote = "\"";
				final String escapedDoubleCote = "\"\"";
				for (var field : map.keySet()) {
					if (map.get(field)) {
						String value = MyCellarFields.getValue(field, b);
						if(MyCellarFields.hasSpecialHTMLCharacters(field)) {
							value = Program.convertStringFromHTMLString(value);
						}
						line.append(doubleCote).append(value.replaceAll(doubleCote, escapedDoubleCote)).append(doubleCote).append(separator);
					}
				}
				line.append('\n');
				fileWriter.write(line.toString());
				fileWriter.flush();
			}
			fileWriter.flush();
			if (progressBar != null) {
				progressBar.setValue(progressBar.getMaximum());
			}
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
	 * @param fichier String: fichier HTML a ecrire
	 * @param bouteilles List<Bouteille>: stock de bouteilles
	 * @param fields 
	 *
	 *  @return int
	 */
	public static boolean write_HTML(final File fichier, final List<Bouteille> bouteilles, List<MyCellarFields> fields) {

		Debug("write_HTML: writing file: " + fichier.getAbsolutePath());
		try{
			var dbFactory = DocumentBuilderFactory.newInstance();
			var dBuilder = dbFactory.newDocumentBuilder();
			var doc = dBuilder.newDocument();
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
			if (fields.isEmpty()) {
				fields = MyCellarFields.getFieldsList();
			}
			for (MyCellarFields field : fields) {
				Element td = doc.createElement("td");
				thead.appendChild(td);
				td.appendChild(doc.createTextNode(field.toString()));
			}

			Element tbody = doc.createElement("tbody");
			table.appendChild(tbody);

			for (Bouteille b : bouteilles) {
				Element tr = doc.createElement("tr");
				tbody.appendChild(tr);
				for (MyCellarFields field : fields) {
					Element td = doc.createElement("td");
					tr.appendChild(td);
					if (field == MyCellarFields.NAME) {
						td.appendChild(doc.createTextNode(b.getNom()));
					} else if (field == MyCellarFields.YEAR) {
						td.appendChild(doc.createTextNode(b.getAnnee()));
					} else if (field == MyCellarFields.TYPE) {
						td.appendChild(doc.createTextNode(b.getType()));
					} else if (field == MyCellarFields.PLACE) {
						td.appendChild(doc.createTextNode(b.getEmplacement()));
					} else if (field == MyCellarFields.NUM_PLACE) {
						td.appendChild(doc.createTextNode(Integer.toString(b.getNumLieu())));
					} else if (field == MyCellarFields.LINE) {
						td.appendChild(doc.createTextNode(Integer.toString(b.getLigne())));
					} else if (field == MyCellarFields.COLUMN) {
						td.appendChild(doc.createTextNode(Integer.toString(b.getColonne())));
					} else if (field == MyCellarFields.PRICE) {
						td.appendChild(doc.createTextNode(b.getPrix()));
					} else if (field == MyCellarFields.COMMENT) {
						td.appendChild(doc.createTextNode(b.getComment()));
					} else if (field == MyCellarFields.MATURITY) {
						td.appendChild(doc.createTextNode(b.getMaturity()));
					} else if (field == MyCellarFields.PARKER) {
						td.appendChild(doc.createTextNode(b.getParker()));
					} else if (field == MyCellarFields.COLOR) {
						td.appendChild(doc.createTextNode(BottleColor.getColor(b.getColor()).toString()));
					} else if (field == MyCellarFields.COUNTRY) {
						if (b.getVignoble() != null) {
							CountryListJaxb.findbyId(b.getVignoble().getCountry()).ifPresent(countryJaxb -> td.appendChild(doc.createTextNode(countryJaxb.toString())));
						} else {
							td.appendChild(doc.createTextNode(""));
						}
					} else if (field == MyCellarFields.VINEYARD) {
						if (b.getVignoble() != null) {
							td.appendChild(doc.createTextNode(b.getVignoble().getName()));
						} else {
							td.appendChild(doc.createTextNode(""));
						}
					} else if (field == MyCellarFields.AOC) {
						if (b.getVignoble() != null && b.getVignoble().getAOC() != null) {
							td.appendChild(doc.createTextNode(b.getVignoble().getAOC()));
						} else {
							td.appendChild(doc.createTextNode(""));
						}
					} else if (field == MyCellarFields.IGP) {
						if (b.getVignoble() != null && b.getVignoble().getIGP() != null) {
							td.appendChild(doc.createTextNode(b.getVignoble().getIGP()));
						} else {
							td.appendChild(doc.createTextNode(""));
						}
					}
				}
			}

			var transformerFactory = TransformerFactory.newInstance();
			var transformer = transformerFactory.newTransformer();
			var source = new DOMSource(doc);
			var result = new StreamResult(fichier);
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
	 * write_XLS: Fonction d'ecriture du ficher Excel
	 *
	 * @param file String: Fichier a ecrire.
	 * @param bouteilles List<Bouteille>: Tableau de bouteilles a ecrire
	 * @param isExit boolean: True si appel pour la creation automatique d'une sauvegarde Excel
	 * @param progressBar JProgressBar
	 *
	 * @return boolean
	 */
	public static boolean write_XLS(final File file, final List<Bouteille> bouteilles, boolean isExit, JProgressBar progressBar) {

		Debug("write_XLS: writing file: " + file);

		try {
			String sDir = file.getParent();
			if (null != sDir) {
				File f = new File(sDir);
				if (!f.exists()) {
					Debug("write_XLS: ERROR: directory " + sDir + " doesn't exist.");
					Debug("write_XLS: ERROR: Unable to write XLS file");
					return false;
				}
			}
		} catch (Exception e) {
			Program.showException(e, false);
			Debug( "write_XLS: ERROR: with file " + file);
			return false;
		}

		EnumMap<MyCellarFields, Boolean> mapCle = new EnumMap<>(MyCellarFields.class);

		//Recuperation des colonnes a exporter
		List<MyCellarFields> fields = MyCellarFields.getFieldsList();
		int i = 0;
		for (MyCellarFields field : fields) {
			mapCle.put(field, Program.getCaveConfigBool(MyCellarSettings.SIZE_COL + i + "EXPORT_XLS", true));
			i++;
		}

		HashMap<Integer, Integer> mapColumnNumber = new HashMap<>();
		int num_ligne;
		String title = "";
		if (isExit) { //Cas sauvegarde XLS Backup
			num_ligne = 0;
			for (i= 0; i<fields.size(); i++) {
				mapColumnNumber.put(i, i);
			}
		}
		else { // Export XLS
			title = Program.getCaveConfigString(MyCellarSettings.XLS_TITLE, ""); //Recuperation du titre du XLS

			num_ligne = 2; //Affectation des numeros de colonnes
			i=0;
			int value = 0;
			for (MyCellarFields field : fields) {
				if (mapCle.get(field)) {
					mapColumnNumber.put(i, value);
					value++;
				}
				i++;
			}
		}

		try (var workbook = new SXSSFWorkbook(100);
				var output = new FileOutputStream(file)) { //Creation du fichier
			String sheet_title = title;
			if (sheet_title.isEmpty()) {
				sheet_title = Program.getLabel("Infos389");
			}
			SXSSFSheet sheet = workbook.createSheet();
			workbook.setSheetName(0, sheet_title);

			if (!isExit) { //Export XLS
				//Taille du titre
				int size = Program.getCaveConfigInt(MyCellarSettings.TITLE_SIZE_XLS, 10);
				Font cellfont = workbook.createFont();
				cellfont.setFontName("Arial");
				cellfont.setFontHeightInPoints((short) size);
				cellfont.setBold(Program.getCaveConfigBool(MyCellarSettings.BOLD_XLS, false));
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
				cellfont.setFontHeightInPoints((short) Program.getCaveConfigInt(MyCellarSettings.TEXT_SIZE_XLS, 10));
			} else {
				cellfont.setFontHeightInPoints((short) 10);
			}
			final XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
			cellStyle.setFont(cellfont);

			i = 0;
			int columnsCount = 0;
			SXSSFRow row = sheet.createRow(num_ligne);
			if (isExit) {
				for (MyCellarFields field : fields) {
					columnsCount++;
					sheet.trackColumnForAutoSizing(i);
					final Cell cell = row.createCell(i++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(field.toString());
				}
			}	else {
				for (MyCellarFields field : fields) {
					if (mapCle.get(field)) {
						columnsCount++;
						sheet.trackColumnForAutoSizing(i);
						final Cell cell = row.createCell(i++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(field.toString());
					}
				}
			}

			if (progressBar != null) {
				progressBar.setMaximum(bouteilles.size());
				progressBar.setMinimum(0);
			}
			i = 0;
			for (Bouteille b : bouteilles) {
				int j = 0;
				if (progressBar != null) {
					progressBar.setValue(i);
				}
				row = sheet.createRow(i + num_ligne + 1);
				row.setRowStyle(cellStyle);
				for (MyCellarFields field : fields) {
					String value = MyCellarFields.getValue(field, b);
					if (isExit || mapCle.get(field)) {
						final Cell cell = row.createCell(mapColumnNumber.get(j));
						if (field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN) {
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
			workbook.write(output);
			if (progressBar != null) {
				progressBar.setValue(progressBar.getMaximum());
			}
		}
		catch (IOException ex) {
			Program.showException(ex, false);
			return false;
		}
		return true;
	}

	/**
	 * write_XLSTab: Fonction d'ecriture du ficher Excel des tableaux
	 *
	 * @param file String: Fichier a ecrire.
	 * @param _oPlace LinkedList: liste de rangements a ecrire
	 *
	 */
	public static void write_XLSTab(final String file, final List<Rangement> _oPlace) {

		Debug("write_XLSTab: writing file: " + file);
		try (var workbook = new SXSSFWorkbook(100);
				 var output = new FileOutputStream(new File(file))) { //Creation du fichier
			String title = Program.getCaveConfigString(MyCellarSettings.XLS_TAB_TITLE, "");
			boolean onePlacePerSheet = Program.getCaveConfigBool(MyCellarSettings.ONE_PER_SHEET_XLS, false);

			if (title.isEmpty()) {
				title = Program.getLabel("Infos001");
			}
			int count = 0;
			SXSSFSheet sheet = workbook.createSheet();
			workbook.setSheetName(count++, title);

			// Titre
			int size = Program.getCaveConfigInt(MyCellarSettings.TITLE_TAB_SIZE_XLS, 10);

			Font cellfont = workbook.createFont();
			cellfont.setFontName("Arial");
			cellfont.setFontHeightInPoints((short) size);
			cellfont.setBold(Program.getCaveConfigBool(MyCellarSettings.BOLD_XLS, false));
			CellStyle cellStyleTitle = workbook.createCellStyle();
			cellStyleTitle.setFont(cellfont);

			final SXSSFRow row = sheet.createRow(0);
			final Cell cell = row.createCell(0);
			cell.setCellStyle(cellStyleTitle);
			cell.setCellValue(title);

			//proprietes du texte
			size = Program.getCaveConfigInt(MyCellarSettings.TEXT_TAB_SIZE_XLS, 10);

			int nNbCol = 0;
			int nNbLinePart = Program.getCaveConfigInt(MyCellarSettings.EMPTY_LINE_PART_XLS, 1);
			int nNbLinePlace = Program.getCaveConfigInt(MyCellarSettings.EMPTY_LINE_PLACE_XLS, 3);

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
						sheet = workbook.createSheet();
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
								final Optional<Bouteille> b = place.getBouteille(j - 1, k - 1, l - 1);
								int finalL = l;
								b.ifPresent(bouteille -> {
									final Cell cellBottle = rowBottle.createCell(finalL);
									cellBottle.setCellValue(getLabelToDisplay(bouteille));
									cellBottle.setCellStyle(cellStyle);
								});
							}
						}
					}
				}
				int nWidth = Program.getCaveConfigInt(MyCellarSettings.COLUMN_TAB_WIDTH_XLS, 10) * 400;
				for (int i = 1; i <= nNbCol; i++) {
					sheet.setColumnWidth(i, nWidth);
				}
			}

			workbook.write(output);
		}	catch (IOException ex) {
			Program.showException(ex, false);
		}
	}

	private static String getLabelToDisplay(final Bouteille b) {
		if (b == null) {
			return "";
		}
		StringBuilder sTitle = new StringBuilder();
		// Contenu de la cellule
		if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL0, true)) {
			sTitle.append(b.getNom());
		}
		if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL1, false)) {
			sTitle.append(" ").append(b.getAnnee());
		}
		if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL2, false)) {
			sTitle.append(" ").append(b.getType());
		}
		if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL3, false)) {
			sTitle.append(" ").append(b.getPrix()).append(Program.getCaveConfigString(MyCellarSettings.DEVISE, ""));
		}
		return toCleanString(sTitle);
	}

	/**
	 * findRangementToCreate
	 */
	public static void findRangementToCreate() {

		final Map<String, LinkedList<Part>> rangements = new HashMap<>();
		for (var bottle: Program.getStorage().getAllList()) {
			updatePlaceMapToCreate(rangements, bottle);
		}
		for (var error : Program.getErrors()) {
			final Bouteille bottle = error.getBottle();
			updatePlaceMapToCreate(rangements, bottle);
		}

		new RangementCreationDialog(rangements);
	}

	private static void updatePlaceMapToCreate(final Map<String, LinkedList<Part>> rangements, final Bouteille bottle) {
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

	/**
	 * Positionne toutes les bouteilles dans les differents rangements
	 * - Une liste d'erreurs est cree
	 * - Les bouteilles en erreurs sont supprimees de la liste principale pour correction
	 *
	 * @return boolean: false si des erreurs existent
	 */
	public static boolean putTabStock() {
		Debug("putTabStock...");
		for (MyCellarError error : Program.getErrors()) {
			if (!error.isSolved()) {
				Program.getStorage().getAllList().add(error.getBottle());
			}
		}
		Program.getErrors().clear();
		for (Rangement rangement : Program.getCave()) {
			rangement.resetStock();
		}
		
		for (var bouteille : Program.getStorage().getAllList()) {
			// On ignore les bouteilles qui sont dans le stock temporairement
			if (bouteille.isInTemporaryStock()) {
				continue;
			}
			Rangement rangement = Program.getCave(bouteille.getEmplacement());
			if (rangement == null) {
				// Rangement inexistant
				Debug("ERROR: Inexisting place: " + bouteille.getNom() + " place: " + bouteille.getEmplacement());
				Program.addError(new MyCellarError(INEXISTING_PLACE, bouteille, bouteille.getEmplacement()));
				continue;
			}
			if (rangement.isCaisse()) {
				if (rangement.isInexistingNumPlace(bouteille.getNumLieu())) {
					// Numero de rangement inexistant
					Debug("ERROR: Inexisting numplace: " + bouteille.getNom() + " numplace: " + bouteille.getNumLieu() + " for place " + bouteille.getEmplacement());
					Program.addError(new MyCellarError(INEXISTING_NUM_PLACE, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
					continue;
				}
				if (rangement.hasFreeSpaceInCaisse(bouteille.getNumLieu() - rangement.getStartCaisse())) {
					rangement.updateToStock(bouteille);
				} else {
					// Caisse pleine
					Debug("ERROR: simple place full for bottle: " + bouteille.getNom() + " numplace: " + bouteille.getNumLieu() + " for place " + bouteille.getEmplacement() + " ");
					Program.addError(new MyCellarError(FULL_BOX, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
				}
			} else {
				if (rangement.isInexistingNumPlace(bouteille.getNumLieu() - 1)) {
					// Numero de rangement inexistant
					Debug("ERROR: Inexisting numplace: " + bouteille.getNom() + " numplace: "+ (bouteille.getNumLieu() - 1) + " for place " + bouteille.getEmplacement());
					Program.addError(new MyCellarError(INEXISTING_NUM_PLACE, bouteille, bouteille.getEmplacement()));
					continue;
				}
				Optional<Bouteille> bottle;
				if (!rangement.isExistingCell(bouteille.getNumLieu() - 1, bouteille.getLigne() - 1, bouteille.getColonne() - 1)) {
					// Cellule inexistante
					Debug("ERROR: Inexisting cell: " + bouteille.getNom() + " numplace: " + (bouteille.getNumLieu() - 1) + ", line: " + (bouteille.getLigne() - 1) + ", column:" + (bouteille.getColonne() - 1) + " for place "+bouteille.getEmplacement());
					Program.addError(new MyCellarError(INEXISTING_CELL, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
				}	else if ((bottle = rangement.getBouteille(bouteille)).isPresent() && !bottle.get().equals(bouteille)){
					// Cellule occupee
					Debug("ERROR: Already occupied: " + bouteille.getNom() + " numplace: " + (bouteille.getNumLieu() - 1) + ", line: " + (bouteille.getLigne() - 1) + ", column:" + (bouteille.getColonne() - 1) + " for place "+bouteille.getEmplacement());
					Program.addError(new MyCellarError(CELL_FULL, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
				}	else {
					rangement.updateToStock(bouteille);
				}
			}
		}
		// Suppression des bouteilles posant probleme
		for (var error : Program.getErrors()) {
			Debug("Error putTabStock: " + error.getBottle());
		}
		Debug("putTabStock Done");
		return Program.getErrors().isEmpty();
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

