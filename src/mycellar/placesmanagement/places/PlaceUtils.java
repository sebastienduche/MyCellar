package mycellar.placesmanagement.places;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.core.exceptions.MyCellarException;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.RangementCreationDialog;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.Program.getAbstractPlaces;
import static mycellar.Program.throwNotImplementedIfNotFor;
import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.ProgramConstants.DEFAULT_STORAGE_EN;
import static mycellar.ProgramConstants.DEFAULT_STORAGE_FR;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.TEMP_PLACE;
import static mycellar.core.MyCellarError.ID.CELL_FULL;
import static mycellar.core.MyCellarError.ID.FULL_BOX;
import static mycellar.core.MyCellarError.ID.INEXISTING_CELL;
import static mycellar.core.MyCellarError.ID.INEXISTING_NUM_PLACE;
import static mycellar.core.MyCellarError.ID.INEXISTING_PLACE;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR120;
import static mycellar.general.ResourceErrorKey.ERROR161;
import static mycellar.general.ResourceKey.MAIN_HTMLEXPORT;
import static mycellar.general.ResourceKey.MAIN_NOTITLE;
import static mycellar.general.ResourceKey.MYCELLAR;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2017
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 6.1
 * @since 21/03/25
 */
public final class PlaceUtils {

  public static final SimplePlace STOCK_PLACE = new SimplePlaceBuilder(TEMP_PLACE).build();

  private PlaceUtils() {
  }

  public static void replaceMyCellarObject(IMyCellarObject oldObject, IMyCellarObject newObject, PlacePosition newObjectPreviousPlace) throws MyCellarException {
    Debug("Replace objet '" + oldObject + "' by '" + newObject + "' previous place: " + newObjectPreviousPlace + " current name " + newObject.getPlacePosition());
    Program.getStorage().addHistory(HistoryState.DEL, oldObject);
    Program.getStorage().deleteWine(oldObject);

    if (newObjectPreviousPlace != null) {
      newObjectPreviousPlace.getAbstractPlace().clearStorage(newObject, newObjectPreviousPlace);
    }

    ProgramPanels.getSearch().ifPresent(search -> search.removeObject(oldObject));

    final AbstractPlace abstractPlace = newObject.getAbstractPlace();
    if (abstractPlace.isComplexPlace()) {
      abstractPlace.updateToStock(newObject);
    }
    Debug("Replace object Done");
  }

  /**
   * Write a CSV file
   *
   * @return int
   */
  public static boolean writeCSV(final File file, final List<? extends IMyCellarObject> myCellarObjects, final JProgressBar progressBar) {

    Debug("writeCSV: writing file: " + file.getAbsolutePath());
    final String separator = Program.getCaveConfigString(MyCellarSettings.SEPARATOR_DEFAULT, COLUMNS_SEPARATOR);

    final EnumMap<MyCellarFields, Boolean> map = new EnumMap<>(MyCellarFields.class);
    for (var field : Objects.requireNonNull(MyCellarFields.getFieldsList())) {
      map.put(field, Program.getCaveConfigBool(MyCellarSettings.EXPORT_CSV + field.name(), false));
    }

    progressBar.setMaximum(myCellarObjects.size());
    progressBar.setMinimum(0);

    try (var fileWriter = new FileWriter(file)) {

      final StringBuilder titleLine = new StringBuilder();
      // Title line
      for (var field : map.keySet()) {
        if (map.get(field)) {
          titleLine.append(field).append(separator);
        }
      }
      titleLine.append('\n');
      fileWriter.write(titleLine.toString());

      int i = 0;
      for (IMyCellarObject myCellarObject : myCellarObjects) {
        progressBar.setValue(i++);
        StringBuilder line = new StringBuilder();
        final String doubleCote = "\"";
        final String escapedDoubleCote = "\"\"";
        for (var field : map.keySet()) {
          if (map.get(field)) {
            String value;
            if (MyCellarFields.hasSpecialHTMLCharacters(field)) {
              value = MyCellarUtils.convertStringFromHTMLString(MyCellarFields.getValue(field, myCellarObject));
            } else {
              value = MyCellarFields.getValue(field, myCellarObject);
            }
            line.append(doubleCote).append(value.replaceAll(doubleCote, escapedDoubleCote)).append(doubleCote).append(separator);
          }
        }
        line.append('\n');
        fileWriter.write(line.toString());
        fileWriter.flush();
      }
      fileWriter.flush();
      progressBar.setValue(progressBar.getMaximum());
    } catch (IOException ioe) {
      Debug("ERROR: Error writing CSV \n" + ioe);
      Erreur.showSimpleErreur(ERROR120, ERROR161);
      return false;
    }
    return true;
  }

  /**
   * @param file       String: fichier HTML a ecrire
   * @param bouteilles List<Bouteille>: stock de bouteilles
   * @return int
   */
  public static boolean writeHTML(final File file, final List<? extends IMyCellarObject> bouteilles, List<MyCellarFields> fields) {

    Debug("writeHTML: writing file: " + file.getAbsolutePath());
    try {
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
      title.appendChild(doc.createTextNode(getLabel(MAIN_HTMLEXPORT)));
      Element body = doc.createElement("body");
      root.appendChild(body);
      Element table = doc.createElement("table");
      body.appendChild(table);
      Element thead = doc.createElement("thead");
      table.appendChild(thead);
      if (fields.isEmpty()) {
        fields = Objects.requireNonNull(MyCellarFields.getFieldsList());
      }
      for (MyCellarFields field : fields) {
        Element td = doc.createElement("td");
        thead.appendChild(td);
        td.appendChild(doc.createTextNode(field.toString()));
      }

      Element tbody = doc.createElement("tbody");
      table.appendChild(tbody);

      for (IMyCellarObject myCellarObject : bouteilles) {
        throwNotImplementedIfNotFor(myCellarObject, Bouteille.class);
        Bouteille b = (Bouteille) myCellarObject;
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
            td.appendChild(doc.createTextNode(b.getKind()));
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
      var result = new StreamResult(file);
      transformer.transform(source, result);
    } catch (ParserConfigurationException | TransformerException e) {
      Program.showException(e, false);
      return false;
    }
    return true;
  }

  /**
   * write_XLS: Fonction d'ecriture du ficher Excel
   *
   * @param file        String: Fichier a ecrire.
   * @param iMyCellarObjects  List<Bouteille>: Tableau de bouteilles a ecrire
   * @param isExit      boolean: True si appel pour la creation automatique d'une sauvegarde Excel
   * @param progressBar JProgressBar
   * @return boolean
   */
  public static boolean writeXLS(final File file, final List<? extends IMyCellarObject> iMyCellarObjects, boolean isExit, JProgressBar progressBar) {

    Debug("writeXLS: writing file: " + file.getAbsolutePath());

    try {
      String sDir = file.getParent();
      if (null != sDir) {
        File f = new File(sDir);
        if (!f.exists()) {
          Debug("writeXLS: ERROR: directory " + sDir + " doesn't exist.");
          Debug("writeXLS: ERROR: Unable to write XLS file");
          return false;
        }
      }
    } catch (RuntimeException e) {
      Program.showException(e, false);
      Debug("writeXLS: ERROR: with file " + file);
      return false;
    }

    // Columns to export
    List<MyCellarFields> fields = Objects.requireNonNull(MyCellarFields.getFieldsList());
    EnumMap<MyCellarFields, Boolean> mapCle = new EnumMap<>(MyCellarFields.class);
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
      for (i = 0; i < fields.size(); i++) {
        mapColumnNumber.put(i, i);
      }
    } else { // Export XLS
      title = Program.getCaveConfigString(MyCellarSettings.XLS_TITLE, ""); //Recuperation du titre du XLS

      num_ligne = 2; //Affectation des numeros de colonnes
      i = 0;
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
        sheet_title = getLabel(MAIN_NOTITLE);
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
      } else {
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
        progressBar.setMaximum(iMyCellarObjects.size());
        progressBar.setMinimum(0);
      }
      i = 0;
      for (IMyCellarObject b : iMyCellarObjects) {
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
      for (i = 0; i < columnsCount; i++) {
        sheet.autoSizeColumn(i);
      }
      workbook.write(output);
      if (progressBar != null) {
        progressBar.setValue(progressBar.getMaximum());
      }
    } catch (FileNotFoundException e) {
      Debug("ERROR: File not found : " + e.getMessage());
      Program.showException(e, false);
      return false;
    } catch (IOException ex) {
      Debug("ERROR: " + ex.getMessage());
      Program.showException(ex, false);
      return false;
    }
    return true;
  }

  /**
   * Fonction d'ecriture du fichier Excel des tableaux
   *
   * @param file      String: Fichier a ecrire.
   * @param placeList LinkedList: liste de rangements a ecrire
   */
  public static void writeXLSTable(final String file, final List<AbstractPlace> placeList) {

    Debug("writeXLSTable: writing file: " + file);
    try (var workbook = new SXSSFWorkbook(100);
         var output = new FileOutputStream(file)) { //Creation du fichier
      String title = Program.getCaveConfigString(MyCellarSettings.XLS_TAB_TITLE, "");
      boolean onePlacePerSheet = Program.getCaveConfigBool(MyCellarSettings.ONE_PER_SHEET_XLS, false);

      if (title.isEmpty()) {
        title = getLabel(MYCELLAR);
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
      int nNbCol = 0;
      for (AbstractPlace place : placeList) {
        if (onePlacePerSheet) {
          if (firstSheet) {
            workbook.setSheetName(0, place.getName());
            firstSheet = false;
          } else {
            sheet = workbook.createSheet();
            workbook.setSheetName(count++, place.getName());
          }
          nLine = 0;
        }
        nLine += nNbLinePlace;
        final SXSSFRow rowPlace = sheet.createRow(nLine);
        final Cell cellPlace = rowPlace.createCell(1);
        cellPlace.setCellStyle(cellStyle);
        cellPlace.setCellValue(place.getName());
        // TODO ZERO BASED
        for (int j = 1; j <= place.getPartCount(); j++) {
          if (j == 1) {
            nLine++;
          } else {
            nLine += nNbLinePart;
          }
          if (place.isSimplePlace()) {
            assert place instanceof SimplePlace;
            for (int k = 0; k < place.getCountCellUsed(j - 1); k++) {
              nLine++;
              final IMyCellarObject b = ((SimplePlace) place).getObjectAt(j - 1, k);
              if (b != null) {
                // Contenu de la cellule
                final SXSSFRow rowBottle = sheet.createRow(nLine);
                final Cell cellBottle = rowBottle.createCell(1);
                cellBottle.setCellValue(getLabelToDisplay(b));
                cellBottle.setCellStyle(cellStyle);
              }
            }
          } else {
            assert place instanceof ComplexPlace;
            ComplexPlace complexPlace = (ComplexPlace) place;
            for (int k = 1; k <= complexPlace.getLineCountAt(j - 1); k++) {
              nLine++;
              int nCol = complexPlace.getColumnCountAt(j - 1, k - 1);
              if (nCol > nNbCol) {
                nNbCol = nCol;
              }
              final SXSSFRow rowBottle = sheet.createRow(nLine);
              for (int l = 1; l <= nCol; l++) {
                int finalL = l;
                complexPlace.getObject(new PlacePosition.PlacePositionBuilder(place)
                    .withNumPlace(j)
                    .withLine(k)
                    .withColumn(l)
                    .build()).ifPresent(bouteille -> {
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
    } catch (FileNotFoundException e) {
      Debug("ERROR: File not found : " + e.getMessage());
      Program.showException(e, false);
    } catch (IOException ex) {
      Debug("ERROR: " + ex.getMessage());
      Program.showException(ex, false);
    }
  }

  private static String getLabelToDisplay(final IMyCellarObject b) {
    if (b == null) {
      return "";
    }
    StringBuilder sTitle = new StringBuilder();
    // Contenu de la cellule
    if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL0, true)) {
      sTitle.append(b.getNom());
    }
    if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL1, false)) {
      sTitle.append(SPACE).append(b.getAnnee());
    }
    if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL2, false)) {
      sTitle.append(SPACE).append(b.getKind());
    }
    if (Program.getCaveConfigBool(MyCellarSettings.XLSTAB_COL3, false)) {
      sTitle.append(SPACE).append(b.getPrix()).append(Program.getCaveConfigString(MyCellarSettings.DEVISE, ""));
    }
    return toCleanString(sTitle);
  }

  public static void findRangementToCreate() {

    final Map<String, LinkedList<Part>> rangements = new HashMap<>();
    for (var bottle : Program.getStorage().getAllList()) {
      updatePlaceMapToCreate(rangements, bottle);
    }
    for (var error : Program.getErrors()) {
      final IMyCellarObject bottle = error.getMyCellarObject();
      updatePlaceMapToCreate(rangements, bottle);
    }

    new RangementCreationDialog(rangements);
  }

  private static void updatePlaceMapToCreate(final Map<String, LinkedList<Part>> rangements, final IMyCellarObject bottle) {
    final String place = bottle.getEmplacement();
    if (place != null && !place.isEmpty() && !isExistingPlace(place)) {
      if (!rangements.containsKey(place)) {
        rangements.put(place, new LinkedList<>());
      } else {
        LinkedList<Part> rangement = rangements.get(place);
        while (rangement.size() <= bottle.getNumLieu()) {
          rangement.add(new Part(rangement.size(), new LinkedList<>()));
        }
        final Part part = rangement.get(bottle.getNumLieu() == 0 ? 0 : bottle.getNumLieu() - 1);
        if (part.rows().size() < bottle.getLigne()) {
          part.increaseRows(bottle.getLigne());
        }
        if (bottle.getLigne() > 0) {
          final Row row = part.getRowAt(bottle.getLigne() - 1);
          if (row.getColumnCount() < bottle.getColonne()) {
            row.setColumnCount(bottle.getColonne());
          }
        }
      }
    }
  }

  public static boolean isExistingPlace(final String name) {
    if (name == null || name.isBlank()) {
      return false;
    }

    final String placeName = name.strip();
    if (placeName.equals(DEFAULT_STORAGE_EN) || placeName.equals(DEFAULT_STORAGE_FR)) {
      return true;
    }
    return getAbstractPlaces().stream().anyMatch(rangement -> rangement.getName().equals(placeName));
  }

  public static AbstractPlace getPlaceByName(final String name) {
    final String placeName = name.strip();
    if (TEMP_PLACE.equals(placeName)) {
      return STOCK_PLACE;
    }
    return getAbstractPlaces()
        .stream()
        .filter(rangement -> filterOnAbstractPlaceName(rangement, placeName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Place not found with name: " + name));
  }

  private static boolean filterOnAbstractPlaceName(AbstractPlace rangement, String placeName) {
    return rangement.getName().equals(placeName) || isDefaultAbstractPlaceName(rangement, placeName);
  }

  private static boolean isDefaultAbstractPlaceName(AbstractPlace rangement, String placeName) {
    return rangement.isDefaultPlace() &&
        (rangement.getName().equals(DEFAULT_STORAGE_EN) || rangement.getName().equals(DEFAULT_STORAGE_FR)) &&
        (placeName.equals(DEFAULT_STORAGE_EN) || placeName.equals(DEFAULT_STORAGE_FR));
  }

  /**
   * Set all objects in the places
   * - In case of collision or others kind of errors, a list of errors is filled.
   * - Objects with errors are removed from the main list
   *
   * @return boolean: Successful?
   */
  public static boolean putTabStock() {
    Debug("putTabStock...");
    for (MyCellarError error : Program.getErrors()) {
      if (!error.isSolved()) {
        if (!Program.getStorage().getAllList().contains(error.getMyCellarObject())) {
          Program.getStorage().add(error.getMyCellarObject());
        }
      }
    }
    Program.getErrors().clear();
    Program.getAbstractPlaces().forEach(AbstractPlace::resetStockage);
    Program.getStorage().updateDistinctNames();

    for (var bouteille : Program.getStorage().getAllList()) {
      // On ignore les bouteilles qui sont dans le stock temporairement
      if (bouteille.isInTemporaryStock()) {
        continue;
      }
      if (!bouteille.isInExistingPlace()) {
        Debug("ERROR: Inexisting place: " + bouteille.getNom() + " place: " + bouteille.getEmplacement());
        Program.addError(new MyCellarError(INEXISTING_PLACE, bouteille, bouteille.getEmplacement()));
        continue;
      }
      final AbstractPlace rangement = bouteille.getAbstractPlace();
      if (rangement.isSimplePlace()) {
        if (rangement.isIncorrectNumPlace(bouteille.getNumLieu())) {
          // Numero de rangement inexistant
          Debug("ERROR: Inexisting numplace: " + bouteille.getNom() + " numplace: " + bouteille.getNumLieu() + " for place " + bouteille.getEmplacement());
          Program.addError(new MyCellarError(INEXISTING_NUM_PLACE, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
          continue;
        }
        if (((SimplePlace) rangement).hasFreeSpace(bouteille.getPlacePosition())) {
          rangement.updateToStock(bouteille);
        } else {
          // Caisse pleine
          Debug("ERROR: simple place full for bottle: " + bouteille.getNom() + " numplace: " + bouteille.getNumLieu() + " for place " + bouteille.getEmplacement() + " ");
          Program.addError(new MyCellarError(FULL_BOX, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
        }
      } else {
        ComplexPlace complexPlace = (ComplexPlace) rangement;
        if (rangement.isIncorrectNumPlace(bouteille.getNumLieu() - 1)) {
          // Numero de rangement inexistant
          Debug("ERROR: Inexisting numplace: " + bouteille.getNom() + " numplace: " + (bouteille.getNumLieu() - 1) + " for place " + bouteille.getEmplacement());
          Program.addError(new MyCellarError(INEXISTING_NUM_PLACE, bouteille, bouteille.getEmplacement()));
          continue;
        }
        if (!complexPlace.isExistingCell(bouteille.getNumLieu() - 1, bouteille.getLigne() - 1, bouteille.getColonne() - 1)) {
          // Cellule inexistante
          Debug("ERROR: Inexisting cell: " + bouteille.getNom() + " numplace: " + (bouteille.getNumLieu() - 1) + ", line: " + (bouteille.getLigne() - 1) + ", column:" + (bouteille.getColonne() - 1) + " for place " + bouteille.getEmplacement());
          Program.addError(new MyCellarError(INEXISTING_CELL, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
        } else {
          final IMyCellarObject myCellarObject = complexPlace.getObject(bouteille.getPlacePosition()).orElse(null);
          if (myCellarObject != null && !myCellarObject.equals(bouteille)) {
            // Cellule occupee
            Debug("ERROR: Already occupied: " + bouteille.getNom() + " numplace: " + (bouteille.getNumLieu() - 1) + ", line: " + (bouteille.getLigne() - 1) + ", column:" + (bouteille.getColonne() - 1) + " for place " + bouteille.getEmplacement());
            Program.addError(new MyCellarError(CELL_FULL, bouteille, bouteille.getEmplacement(), bouteille.getNumLieu()));
          } else {
            rangement.updateToStock(bouteille);
          }
        }
      }
    }
    if (!Program.getErrors().isEmpty()) {
      Debug("List of objects with errors:");
    }
    for (var error : Program.getErrors()) {
      Debug("Error: " + error.getMyCellarObject());
      Program.getStorage().getAllList().remove(error.getMyCellarObject());
    }
    Debug("putTabStock Done");
    return Program.getErrors().isEmpty();
  }

  private static void Debug(String sText) {
    Program.Debug("PlaceUtils: " + sText);
  }

  public static boolean isTemporaryPlace(String place) {
    return TEMP_PLACE.equalsIgnoreCase(place);
  }
}

