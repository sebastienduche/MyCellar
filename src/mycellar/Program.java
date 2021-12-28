package mycellar;

import mycellar.actions.OpenAddVinAction;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.Grammar;
import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFile;
import mycellar.core.MyCellarLabelManagement;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyLinkedHashMap;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.music.MyCellarMusicSupport;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.worksheet.WorkSheetList;
import mycellar.core.exceptions.UnableToOpenFileException;
import mycellar.core.exceptions.UnableToOpenMyCellarFileException;
import mycellar.core.language.Language;
import mycellar.core.language.LanguageFileLoader;
import mycellar.core.storage.ListeBouteille;
import mycellar.core.storage.SerializedStorage;
import mycellar.core.storage.Storage;
import mycellar.general.ProgramPanels;
import mycellar.general.XmlUtils;
import mycellar.pdf.PDFColumn;
import mycellar.pdf.PDFProperties;
import mycellar.pdf.PDFRow;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import mycellar.vignobles.CountryVignobleController;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.commons.text.StringEscapeUtils;
import org.kohsuke.github.GHGistBuilder;
import org.kohsuke.github.GitHub;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import static mycellar.Filtre.EXTENSION_SINFO;
import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.ProgramConstants.BOUTEILLES_XML;
import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.ProgramConstants.CONFIG_INI;
import static mycellar.ProgramConstants.DATE_FORMATER_DD_MM_YYYY;
import static mycellar.ProgramConstants.DEFAULT_STORAGE_EN;
import static mycellar.ProgramConstants.DEFAULT_STORAGE_FR;
import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.ProgramConstants.EURO;
import static mycellar.ProgramConstants.FRA;
import static mycellar.ProgramConstants.INTERNAL_VERSION;
import static mycellar.ProgramConstants.KEY_TYPE;
import static mycellar.ProgramConstants.MY_CELLAR_XML;
import static mycellar.ProgramConstants.ON;
import static mycellar.ProgramConstants.ONE;
import static mycellar.ProgramConstants.ONE_DOT;
import static mycellar.ProgramConstants.PREVIEW_HTML;
import static mycellar.ProgramConstants.PREVIEW_XML;
import static mycellar.ProgramConstants.SLASH;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.TEMP_PLACE;
import static mycellar.ProgramConstants.THREE_DOTS;
import static mycellar.ProgramConstants.TIMESTAMP_PATTERN;
import static mycellar.ProgramConstants.TYPES_MUSIC_XML;
import static mycellar.ProgramConstants.TYPES_XML;
import static mycellar.ProgramConstants.UNTITLED1_SINFO;
import static mycellar.ProgramConstants.VERSION;
import static mycellar.ProgramConstants.ZERO;
import static mycellar.core.MyCellarSettings.PROGRAM_TYPE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 27.4
 * @since 27/12/21
 */

public final class Program {

  public static final Rangement DEFAULT_PLACE = new Rangement.SimplePlaceBuilder("").setDefaultPlace(true).build();
  public static final Rangement EMPTY_PLACE = new Rangement.SimplePlaceBuilder("").build();
  public static final Rangement STOCK_PLACE = new Rangement.SimplePlaceBuilder(TEMP_PLACE).build();

  public static final CountryJaxb FRANCE = new CountryJaxb(FRA, ProgramConstants.FRANCE);
  public static final CountryJaxb NO_COUNTRY = new CountryJaxb("");
  public static final CountryVignobleJaxb NO_VIGNOBLE = new CountryVignobleJaxb();
  public static final AppelationJaxb NO_APPELATION = new AppelationJaxb();
  public static final MyClipBoard CLIPBOARD = new MyClipBoard();

  // Manage global config
  private static final MyLinkedHashMap CONFIG_GLOBAL = new MyLinkedHashMap();
  private static final List<Rangement> PLACES = new LinkedList<>();
  private static final List<MyCellarObject> TRASH = new LinkedList<>();
  private static final List<MyCellarError> ERRORS = new LinkedList<>();
  private static final List<File> DIR_TO_DELETE = new LinkedList<>();
  private static Type programType = Type.WINE;
  private static MyCellarFile myCellarFile = null;
  private static FileWriter oDebugFile = null;
  private static File debugFile = null;
  private static String workDir = null;
  private static String globalDir = null;
  private static boolean workDirCalculated = false;
  private static boolean globalDirCalculated = false;
  private static boolean modified = false;
  private static boolean listCaveModified = false;
  private static int nextID = -1;

  public static void start() throws UnableToOpenFileException {
    Debug("===================================================");
    Debug("Starting MyCellar version: " + VERSION + " Internal: " + INTERNAL_VERSION);
    // Initialisation du repertoire de travail
    getWorkDir(false);
    loadGlobalProperties();
    LanguageFileLoader.getInstance().loadLanguageFiles(Language.ENGLISH);
  }

  static void loadPropertiesAndSetProgramType() {
    try {
      Debug("Program: Initializing Configuration files and Program type");
      if (loadProperties()) {
        setProgramType(Program.Type.typeOf(getCaveConfigString(PROGRAM_TYPE, Program.Type.WINE.name())));
      } else {
        setProgramType(Program.Type.typeOf(getGlobalConfigString(PROGRAM_TYPE, Program.Type.WINE.name())));
      }
    } catch (UnableToOpenFileException e) {
      showException(e);
    }
    String thelangue = getGlobalConfigString(MyCellarSettings.LANGUAGE, "" + Language.FRENCH.getLanguage());
    Debug("Program: Type of managed object: " + programType);
    setLanguage(Language.getLanguage(thelangue.charAt(0)));
  }

  static void initializeLanguageProgramType() {
    try {
      Debug("Program: Initializing Language and Program type");
      LanguageFileLoader.getInstance().loadLanguageFiles(Language.ENGLISH);

      if (!hasConfigGlobalKey(MyCellarSettings.LANGUAGE) || getGlobalConfigString(MyCellarSettings.LANGUAGE, "").isEmpty()) {
        putGlobalConfigString(MyCellarSettings.LANGUAGE, "" + Language.FRENCH.getLanguage());
      }

      String thelangue = getGlobalConfigString(MyCellarSettings.LANGUAGE, "" + Language.FRENCH.getLanguage());
      Debug("Program: Type of managed object: " + programType);
      setLanguage(Language.getLanguage(thelangue.charAt(0)));
      cleanAndUpgrade();
    } catch (RuntimeException e) {
      showException(e);
    }
  }

  static void setProgramType(Type value) {
    programType = value;
  }

  public static boolean isMusicType() {
    return programType == Type.MUSIC;
  }

  public static boolean isWineType() {
    return programType == Type.WINE;
  }

  private static String getLabelForType(boolean plural, boolean firstLetterUppercase, Grammar grammar) {
    return getLabelForType(programType, plural, firstLetterUppercase, grammar);
  }

  public static String getLabelForType(Type theType, boolean plural, boolean firstLetterUppercase, Grammar grammar) {
    String value;
    String prefix;
    String postfix = plural ? "s" : "";
    switch (grammar) {
      case SINGLE:
        prefix = plural ? "more" : "one";
        break;
      case THE:
        prefix = "the";
        break;
      case OF_THE:
        prefix = "ofthe";
        break;
      case NONE:
      default:
        prefix = "";
        break;
    }
    switch (theType) {
      case BOOK:
        value = getLabel("Program." + prefix + "book" + postfix);
        break;
      case MUSIC:
        value = getLabel("Program." + prefix + "disc" + postfix);
        break;
      case WINE:
      default:
        value = getLabel("Program." + prefix + "wine" + postfix);
    }
    if (firstLetterUppercase) {
      value = StringUtils.capitalize(value);
    }
    return value;
  }

  static void setNewFile(String file) {
    myCellarFile = new MyCellarFile(new File(file));
  }

  public static boolean hasFile() {
    return myCellarFile != null;
  }

  public static String getConfigFilePath() {
    return getWorkDir(true) + CONFIG_INI;
  }

  private static String getGlobalConfigFilePath() {
    return getGlobalDir() + CONFIG_INI;
  }

  public static List<MyCellarObject> getTrash() {
    return TRASH;
  }

  public static void setToTrash(MyCellarObject b) {
    TRASH.add(b);
  }

  public static List<MyCellarError> getErrors() {
    return ERRORS;
  }

  private static boolean loadProperties() throws UnableToOpenFileException {
    try {
      String configFilePath = getConfigFilePath();
      File f = new File(configFilePath);
      if (!f.exists()) {
        if (!f.createNewFile()) {
          Debug("Program: ERROR: Unable to create file " + f.getAbsolutePath());
          throw new UnableToOpenFileException("Unable to create file " + f.getAbsolutePath());
        }
        return false;
      }
      FileInputStream inputStream = new FileInputStream(configFilePath);
      Properties properties = new Properties();
      properties.load(inputStream);
      inputStream.close();
      properties.forEach((key, value) -> putCaveConfigString(key.toString(), value.toString()));
      if (properties.isEmpty()) {
        // Initialisation de la devise pour les nouveaux fichiers
        putCaveConfigString(MyCellarSettings.DEVISE, EURO);
      }
      Debug("Program: Properties loaded: " + configFilePath);
    } catch (FileNotFoundException e) {
      throw new UnableToOpenFileException("File not found: " + e.getMessage());
    } catch (IOException e) {
      throw new UnableToOpenFileException("Load properties failed: " + e.getMessage());
    }
    return true;
  }

  private static void loadGlobalProperties() throws UnableToOpenFileException {
    try {
      Debug("Program: Initializing Global Configuration files...");
      File fileIni = new File(getGlobalConfigFilePath());
      if (!fileIni.exists() && !fileIni.createNewFile()) {
        Debug("Program: ERROR: Unable to create file " + fileIni.getAbsolutePath());
        throw new UnableToOpenFileException("Unable to create file " + fileIni.getAbsolutePath());
      }
      FileInputStream inputStream = new FileInputStream(fileIni);
      Properties properties = new Properties();
      properties.load(inputStream);
      inputStream.close();
      //Initialisation de la Map contenant config
      properties.forEach((key, value) -> putGlobalConfigString(key.toString(), value.toString()));
      Debug("Program: Global Properties loaded: " + fileIni);
    } catch (FileNotFoundException e) {
      throw new UnableToOpenFileException("File not found: " + e.getMessage());
    } catch (IOException e) {
      throw new UnableToOpenFileException("Load properties failed: " + e.getMessage());
    }
  }

  /**
   * cleanAndUpgrade
   * <p>
   * Pour nettoyer et mettre a jour le programme
   */
  private static void cleanAndUpgrade() {
    if (!hasFile()) {
      return;
    }
    Debug("Program: clean and upgrade...");
    String sVersion = getCaveConfigString(MyCellarSettings.VERSION, "");
    if (sVersion.isEmpty() || sVersion.contains(ONE_DOT)) {
      putCaveConfigInt(MyCellarSettings.VERSION, VERSION);
    }
    int currentVersion = getCaveConfigInt(MyCellarSettings.VERSION, VERSION);
    Debug("Program: internal file version: " + currentVersion);

    final String type = getCaveConfigString(PROGRAM_TYPE, "");
    if (type.isBlank()) {
      putCaveConfigString(PROGRAM_TYPE, Type.WINE.name());
    }

    if (currentVersion < 71) {
      getCave().stream().filter(rangement ->
              isDefaultStorageName(rangement, rangement.getName()))
          .findFirst()
          .ifPresent(rangement -> rangement.setDefaultPlace(true));
    }
    Debug("Program: clean and upgrade... Done");
  }

  private static void checkFileVersion() throws UnableToOpenMyCellarFileException {
    if (!hasFile()) {
      return;
    }
    int currentVersion = getCaveConfigInt(MyCellarSettings.VERSION, VERSION);
    if (currentVersion > VERSION) {
      Erreur.showSimpleErreur(getError("Program.NotSupportedVersion"));
      throw new UnableToOpenMyCellarFileException("The file version '" + currentVersion + "' is not supported by this program version: " + VERSION);
    }
  }

  static void setLanguage(Language lang) {
    Debug("Program: Set Language: " + lang);
    MyCellarLabelManagement.updateLabels();
    ProgramPanels.TABBED_PANE.removeAll();
    ProgramPanels.clearObjectsVariables();
    LanguageFileLoader.getInstance().loadLanguageFiles(lang);
    ProgramPanels.PANEL_INFOS.setLabels();
    Start.getInstance().updateLabels();
    Start.getInstance().updateMainPanel();
  }

  public static void showException(Exception e) {
    showException(e, true);
  }

  public static void showException(Throwable e, boolean _bShowWindowErrorAndExit) {
    StackTraceElement[] st = e.getStackTrace();
    String error = "";
    for (StackTraceElement s : st) {
      error = error.concat("\n" + s);
    }

    if (error.contains("BasicTabbedPaneUI.tabForCoordinate")) {
      _bShowWindowErrorAndExit = false;
    }

    if (_bShowWindowErrorAndExit) {
      JOptionPane.showMessageDialog(Start.getInstance(), e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    Debug("Program: ERROR:");
    Debug("Program: " + e);
    Debug("Program: " + error);
    e.printStackTrace();
    if (debugFile != null) {
      try {
        oDebugFile.flush();
        oDebugFile.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      try {
        sendErrorToGitHub(e.toString(), debugFile);
      } catch (IOException ignored) {
      }
      oDebugFile = null;
    }

    if (_bShowWindowErrorAndExit) {
      System.exit(999);
    }
  }

  private static void sendErrorToGitHub(String title, File file) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    try (var scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        stringBuilder.append(toCleanString(scanner.nextLine())).append("\n");
      }
    } catch (FileNotFoundException e) {
      Debug("Program: ERROR Unable to send file to GitHub: " + e.getMessage());
      return;
    }
    try (var stream = new InputStreamReader(Program.class.getClassLoader().getResourceAsStream("resources/MyCellar.dat"));
         var reader = new BufferedReader(stream)) {
      String line = reader.readLine();
      reader.close();
      stream.close();
      String decoded = new String(Base64.decodeBase64(line.getBytes()));
      final String[] values = decoded.split(SLASH);

      final GitHub gitHub = GitHub.connect(values[0], values[1]);
      final GHGistBuilder gist = gitHub.createGist();
      gist.description(title)
          .file("Debug.log", stringBuilder.toString())
          .create();
    } catch (IOException | RuntimeException e) {
      Debug("Program: ERROR while creating a Gist: " + e.getMessage());
    }
  }

  public static void addError(MyCellarError error) {
    ERRORS.add(error);
  }

  public static String convertToHTMLString(String s) {
    return StringEscapeUtils.escapeHtml4(s);
  }

  public static String convertStringFromHTMLString(String s) {
    return StringEscapeUtils.unescapeHtml4(s);
  }

  public static String removeAccents(String s) {
    s = Normalizer.normalize(s, Normalizer.Form.NFD);
    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    return s;
  }

  public static String toCleanString(final Object o) {
    if (o == null) {
      return "";
    }
    String value = o.toString();
    return value == null ? "" : value.strip();
  }

  /**
   * Chargement des donnees XML (Bouteilles et Rangement) ou des donnees serialisees en cas de pb
   */
  static boolean loadObjects() {
    PLACES.clear();
    boolean load = XmlUtils.readMyCellarXml("", PLACES);
    if (!load || PLACES.isEmpty()) {
      PLACES.clear();
      PLACES.add(DEFAULT_PLACE);
    }
    getStorage().loadHistory();
    getStorage().loadWorksheet();
    return ListeBouteille.loadXML();
  }

  static int getMaxPrice() {
    return (int) getStorage().getAllList().stream().mapToDouble(IMyCellarObject::getPriceDouble).max().orElse(0);
  }

  public static int getCellarValue() {
    return (int) getStorage().getAllList().stream().mapToDouble(IMyCellarObject::getPriceDouble).sum();
  }

  static int getNbItems() {
    return getStorage().getAllList().size();
  }

  static int getTotalObjectForYear(int year) {
    return (int) getStorage().getAllList().stream().filter(myCellarObject -> myCellarObject.getAnneeInt() == year).count();
  }

  static int[] getYearsArray() {
    return getStorage().getAllList().stream().mapToInt(IMyCellarObject::getAnneeInt).distinct().sorted().toArray();
  }

  static int getTotalOtherYears() {
    return (int) getStorage().getAllList().stream()
        .filter(myCellarObject -> myCellarObject.getAnneeInt() < 1000).count();
  }

  static int getNbNonVintage() {
    return (int) getStorage().getAllList().stream()
        .filter(bouteille -> Bouteille.isNonVintageYear(bouteille.getAnnee())).count();
  }

  public static void getAide() {
    File f = new File("./Help/MyCellar.hs");
    if (f.exists()) {
      try {
        Runtime.getRuntime().exec("java -jar ./Help/hsviewer.jar -hsURL \"file:./Help/MyCellar.hs\"");
      } catch (IOException ignored) {
      }
    } else {
      Erreur.showSimpleErreur(getError("Error162"));
    }
  }

  public static char getDecimalSeparator() {
    DecimalFormatSymbols symbols = ((DecimalFormat) NumberFormat.getInstance()).getDecimalFormatSymbols();
    return symbols.getDecimalSeparator();
  }

  /**
   * Sauvegarde le fichier
   */
  public static void save() {
    Debug("Program: Saving...");
    saveAs(myCellarFile.getFile());
  }

  /**
   * saveAs
   *
   * @param file File
   */
  static void saveAs(File file) {
    Debug("Program: -------------------");
    Debug("Program: Saving all files...");
    Debug("Program: -------------------");

    saveGlobalProperties();

    if (isListCaveModified()) {
      XmlUtils.writeMyCellarXml(PLACES, "");
    }

    getStorage().saveHistory();
    getStorage().saveWorksheet();
    CountryVignobleController.save();
    CountryListJaxb.save();
    ListeBouteille.writeXML();

    myCellarFile.saveAs(file);

    modified = false;
    listCaveModified = false;
    ProgramPanels.setAllPanesModified(false);
    Debug("Program: -------------------");
    Debug("Program: Saving all files OK");
    Debug("Program: -------------------");
  }

  public static void Debug(String sText) {
    try {
      if (oDebugFile == null) {
        String sDir = System.getProperty("user.home");
        if (!sDir.isEmpty()) {
          sDir += File.separator + "MyCellarDebug";
        }
        File f_obj = new File(sDir);
        if (f_obj.exists() || f_obj.mkdir()) {
          String sDate = LocalDate.now().format(DATE_FORMATER_DD_MM_YYYY);
          debugFile = new File(sDir, "Debug-" + sDate + ".log");
          oDebugFile = new FileWriter(debugFile, true);
        }
      }
      oDebugFile.write("[" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "]: " + sText + "\n");
      oDebugFile.flush();
    } catch (IOException ignored) {
    }
  }

  private static void closeDebug() {
    if (oDebugFile == null) {
      return;
    }

    try {
      oDebugFile.flush();
      oDebugFile.close();
    } catch (IOException ignored) {
    }

  }

  public static List<Rangement> getCave() {
    return PLACES;
  }

  /**
   * GetCave
   *
   * @param name String
   * @return Rangement
   */
  public static Rangement getCave(final String name) {
    final String placeName = name.strip();
    if (TEMP_PLACE.equals(placeName)) {
      return STOCK_PLACE;
    }
    final List<Rangement> list = PLACES.stream().filter(rangement -> filterOnPlaceName(rangement, placeName)).collect(Collectors.toList());
    return list.get(0);
  }

  private static boolean filterOnPlaceName(Rangement rangement, String placeName) {
    return rangement.getName().equals(placeName) || isDefaultStorageName(rangement, placeName);
  }

  private static boolean isDefaultStorageName(Rangement rangement, String placeName) {
    return rangement.isDefaultPlace() &&
        (rangement.getName().equals(DEFAULT_STORAGE_EN) || rangement.getName().equals(DEFAULT_STORAGE_FR)) &&
        (placeName.equals(DEFAULT_STORAGE_EN) || placeName.equals(DEFAULT_STORAGE_FR));
  }

  /**
   * addCave
   *
   * @param rangement Rangement
   */
  public static void addCave(Rangement rangement) {
    if (rangement == null) {
      return;
    }
    PLACES.add(rangement);
    setListCaveModified();
    setModified();
    Collections.sort(PLACES);
  }

  /**
   * removeCave
   *
   * @param rangement Rangement
   */
  public static void removeCave(Rangement rangement) {
    if (rangement == null) {
      return;
    }
    PLACES.remove(rangement);
    setModified();
    setListCaveModified();
  }

  public static int getCaveLength() {
    return PLACES.size();
  }

  public static boolean hasComplexPlace() {
    return PLACES.stream().anyMatch(rangement -> !rangement.isSimplePlace());
  }

  /**
   * newFile: Create a new file.
   */
  static void newFile() {
    final File file = new File(getWorkDir(true) + UNTITLED1_SINFO);
    if (file.exists()) {
      FileUtils.deleteQuietly(file);
    }
    try {
      if (!file.createNewFile()) {
        Debug("ERROR: Unable to create file: " + file.getAbsolutePath());
      }
    } catch (IOException e) {
      showException(e);
    }
    try {
      openaFile(file, true);
    } catch (UnableToOpenFileException e) {
      showException(e);
    }
  }

  /**
   * openaFile: Ouvre un fichier
   *
   * @param file File
   */
  static void openaFile(File file) throws UnableToOpenFileException {
    openaFile(file, false);
  }

  private static void openaFile(File file, boolean isNewFile) throws UnableToOpenFileException {
    LinkedList<String> list = new LinkedList<>();
    list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN1, ""));
    list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN2, ""));
    list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN3, ""));
    list.addLast(getGlobalConfigString(MyCellarSettings.LAST_OPEN4, ""));
    Debug("Program: -------------------");
    if (isNewFile) {
      Debug("Program: openFile: Creating new file");
    } else {
      Debug("Program: openFile: Opening file: " + file.getAbsolutePath());
      list.remove(file.getAbsolutePath());
    }
    Debug("Program: -------------------");

    // Sauvegarde avant de charger le nouveau fichier
    closeFile();

    CountryVignobleController.init();

    if (isNewFile) {
      // Nouveau fichier de bouteilles
      ListeBouteille.writeXML();
    }

    if (!file.exists()) {
      Erreur.showSimpleErreur(MessageFormat.format(getError("Error020"), file.getAbsolutePath())); // File not found

      putGlobalConfigString(MyCellarSettings.LAST_OPEN1, list.pop());
      putGlobalConfigString(MyCellarSettings.LAST_OPEN2, list.pop());
      putGlobalConfigString(MyCellarSettings.LAST_OPEN3, list.pop());
      // On a deja enleve un element de la liste
      putGlobalConfigString(MyCellarSettings.LAST_OPEN4, "");
      saveGlobalProperties();
      throw new UnableToOpenMyCellarFileException("File not found: " + file.getAbsolutePath());
    }

    myCellarFile = new MyCellarFile(file);
    myCellarFile.unzip();
    loadPropertiesAndSetProgramType();
    checkFileVersion();

    CountryListJaxb.init();

    //Chargement des objets Rangement, Bouteilles et History
    Debug("Program: Reading Places, Bottles & History");
    if (!loadObjects()) {
      Debug("Program: ERROR Reading Objects KO");
      throw new UnableToOpenFileException("Error while reading objects.");
    }

    if (isWineType()) {
      MyCellarBottleContenance.load();
      CountryVignobleController.load();
    } else if (isMusicType()) {
      MyCellarMusicSupport.load();
    }

    RangementUtils.putTabStock();
    if (!getErrors().isEmpty()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }

    if (myCellarFile.isFileSavable()) {
      list.addFirst(file.getAbsolutePath());
    }

    putGlobalConfigString(MyCellarSettings.LAST_OPEN1, list.pop());
    putGlobalConfigString(MyCellarSettings.LAST_OPEN2, list.pop());
    putGlobalConfigString(MyCellarSettings.LAST_OPEN3, list.pop());
    putGlobalConfigString(MyCellarSettings.LAST_OPEN4, list.pop());

    putCaveConfigString(MyCellarSettings.DIR, file.getParent());

    saveGlobalProperties();
    modified = false;
    listCaveModified = false;
    Debug("Program: ----------------");
    Debug("Program: Open a File Done");
    Debug("Program: ----------------");
  }

  /**
   * closeFile: Fermeture du fichier.
   */
  static void closeFile() {
    if (!hasFile()) {
      Debug("Program: closeFile: File already closed!");
      return;
    }
    Debug("Program: closeFile: Closing file...");
    boolean bSave = false;
    File newFile = null;
    if (myCellarFile.exists() && isModified()) {
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, getError("Error199"), getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
        bSave = true;
        if (!myCellarFile.isFileSavable()) {
          JFileChooser boiteFichier = new JFileChooser();
          boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
          boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
          if (boiteFichier.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File nomFichier = boiteFichier.getSelectedFile();
            newFile = new File(MyCellarControl.controlAndUpdateExtension(nomFichier.getAbsolutePath(), Filtre.FILTRE_SINFO));
          }
        }
      }

      putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false);
    }

    File f = new File(getPreviewXMLFileName());
    if (f.exists()) {
      FileUtils.deleteQuietly(f);
    }
    f = new File(getPreviewHTMLFileName());
    if (f.exists()) {
      FileUtils.deleteQuietly(f);
    }

    getErrors().clear();

    //Tri du tableau et ecriture du fichier XML
    if (bSave) {
      if (!ListeBouteille.writeXML()) {
        return;
      }

      if (isListCaveModified()) {
        XmlUtils.writeMyCellarXml(PLACES, "");
      }

      if (!PLACES.isEmpty()) {
        getStorage().saveHistory();
        getStorage().saveWorksheet();
        CountryVignobleController.save();
        if (newFile != null) {
          myCellarFile.saveAs(newFile);
        } else {
          myCellarFile.save();
        }
      }
    }

    if (myCellarFile.exists()) {
      // Sauvegarde des proprietes globales
      saveGlobalProperties();

      if (getCaveConfigBool(MyCellarSettings.FIC_EXCEL, false)) {
        //Ecriture Excel
        final String file_excel = getCaveConfigString(MyCellarSettings.FILE_EXCEL, "");
        Debug("Program: Writing backup Excel file: " + file_excel);
        final List<IMyCellarObject> bouteilles = Collections.unmodifiableList(getStorage().getAllList());
        Thread writingExcel = new Thread(() -> RangementUtils.write_XLS(new File(file_excel), bouteilles, true, null));
        Runtime.getRuntime().addShutdownHook(writingExcel);
      }
    }

    ProgramPanels.TABBED_PANE.removeAll();
    if (myCellarFile.exists()) {
      getStorage().close();
      CountryVignobleController.close();
      CountryListJaxb.close();
      ProgramPanels.getSearch().ifPresent(Search::clearResults);
    }
    ProgramPanels.clearObjectsVariables();
    workDirCalculated = false;
    TRASH.clear();
    modified = false;
    listCaveModified = false;
    PLACES.clear();
    DEFAULT_PLACE.resetStock();
    EMPTY_PLACE.resetStock();
    myCellarFile = null;
    Debug("Program: closeFile: Closing file Ended");
  }

  private static void deleteTempFiles() {
    for (File f : DIR_TO_DELETE) {
      if (!f.exists() || f.getName().equalsIgnoreCase("Global")) {
        continue;
      }
      try {
        Debug("Program: closeFile: Deleting work directory: " + f.getAbsolutePath());
        FileUtils.deleteDirectory(f);
      } catch (IOException e) {
        Debug("Program: Error deleting " + f.getAbsolutePath());
        Debug("Program: " + e.getMessage());
      }
    }
  }

  /**
   * Save global properties
   */
  static void saveGlobalProperties() {
    Debug("Program: Saving Global Properties");
    saveProperties(CONFIG_GLOBAL, getGlobalConfigFilePath());
    Debug("Program: Saving Global Properties Done");
  }

  public static void saveProperties(final MyLinkedHashMap map, final String file) {
    Object[] val = map.keySet().toArray();
    final Properties properties = new Properties();
    for (Object o : val) {
      String key = o.toString();
      properties.put(key, map.getString(key));
    }
    try (var outputStream = new FileOutputStream(file)) {
      properties.store(outputStream, null);
    } catch (IOException e) {
      showException(e);
    }
  }

  /**
   * Retourne le nom du repertoire des proprietes globales.
   */
  private static String getGlobalDir() {
    if (globalDirCalculated) {
      return globalDir + File.separator;
    }
    globalDirCalculated = true;
    String sDir = System.getProperty("user.home");
    if (sDir.isEmpty()) {
      globalDir = "./Object/Global";
    } else {
      globalDir = sDir + "/MyCellar/Global";
    }
    File file = new File(globalDir);
    if (!file.exists()) {
      if (!file.mkdir()) {
        Debug("ERROR: Unable to create directoy: " + file.getAbsolutePath());
      }
    }

    return globalDir + File.separator;
  }

  public static boolean hasWorkDir() {
    return workDirCalculated;
  }

  /**
   * Retourne le nom du repertoire de travail.
   *
   * @param withEndSlash
   */
  public static String getWorkDir(boolean withEndSlash) {
    if (workDirCalculated) {
      if (withEndSlash) {
        return workDir + File.separator;
      }
      return workDir;
    }
    workDirCalculated = true;
    Debug("Program: Calculating work directory.");
    String sDir = System.getProperty("user.home");
    if (sDir.isEmpty()) {
      workDir = ONE_DOT + File.separator + "Object";
    } else {
      workDir = sDir + File.separator + "MyCellar";
    }
    File file = new File(workDir);
    if (!file.exists()) {
      if (!file.mkdir()) {
        Debug("ERROR: Unable to create directoy: " + file.getAbsolutePath());
      }
    }

    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    workDir += File.separator + time;

    file = new File(workDir);
    if (!file.exists()) {
      if (!file.mkdir()) {
        Debug("ERROR: Unable to create directoy: " + file.getAbsolutePath());
      }
    }

    Debug("Program: work directory: " + workDir);
    DIR_TO_DELETE.add(new File(workDir));

    if (withEndSlash) {
      return workDir + File.separator;
    }
    return workDir;
  }

  static String getShortFilename() {
    if (hasFile()) {
      return getShortFilename(myCellarFile.getFile().getAbsolutePath());
    }
    return "";
  }

  static String getShortFilename(String sFilename) {
    String tmp = sFilename.replaceAll("\\\\", SLASH);
    int ind1 = tmp.lastIndexOf(SLASH);
    int ind2 = tmp.indexOf(ONE_DOT + EXTENSION_SINFO);
    if (ind1 != -1 && ind2 != -1) {
      tmp = tmp.substring(ind1 + 1, ind2);
    }
    return tmp;
  }

  static String getGlobalConfigString(String key, String defaultValue) {
    return CONFIG_GLOBAL.getString(key, defaultValue);
  }

  public static String getCaveConfigString(String key, String defaultValue) {
    if (null != getCaveConfig()) {
      return getCaveConfig().getString(key, defaultValue);
    }
    Debug("Program: ERROR: Calling null configCave for key '" + key + "' and default value '" + defaultValue + "'");
    return defaultValue;
  }

  static boolean getGlobalConfigBool(String key, boolean defaultValue) {
    return 1 == CONFIG_GLOBAL.getInt(key, defaultValue ? 1 : 0);
  }

  public static boolean getCaveConfigBool(String key, boolean defaultValue) {
    if (null != getCaveConfig()) {
      final String value = getCaveConfig().getString(key, defaultValue ? ONE : ZERO);
      return (ONE.equals(value) || ON.equalsIgnoreCase(value));
    }
    Debug("Program: ERROR: Calling null configCave for key '" + key + "' and default value '" + defaultValue + "'");
    return defaultValue;
  }

  public static int getCaveConfigInt(String key, int defaultValue) {
    if (null != getCaveConfig()) {
      return getCaveConfig().getInt(key, defaultValue);
    }
    Debug("Program: ERROR: Calling null configCave for key '" + key + "' and default value '" + defaultValue + "'");
    return defaultValue;
  }

  static void putGlobalConfigString(String key, String value) {
    CONFIG_GLOBAL.put(key, value);
  }

  public static void putCaveConfigString(String key, String value) {
    if (null != getCaveConfig()) {
      getCaveConfig().put(key, value);
    } else {
      Debug("Program: ERROR: Unable to put value in configCave: [" + key + " - " + value + "]");
    }
  }

  static void putGlobalConfigBool(String key, boolean value) {
    CONFIG_GLOBAL.put(key, value ? ONE : ZERO);
  }

  public static void putCaveConfigBool(String key, boolean value) {
    if (null != getCaveConfig()) {
      getCaveConfig().put(key, value ? ONE : ZERO);
    } else {
      Debug("Program: ERROR: Unable to put value in configCave: [" + key + " - " + value + "]");
    }
  }

  public static void putCaveConfigInt(String key, int value) {
    Objects.requireNonNull(getCaveConfig()).put(key, value);
  }

  public static MyLinkedHashMap getCaveConfig() {
    return hasFile() ? myCellarFile.getCaveConfig() : null;
  }

  public static boolean hasConfigCaveKey(String key) {
    return null != getCaveConfig() && getCaveConfig().containsKey(key);
  }

  static boolean hasConfigGlobalKey(String key) {
    return CONFIG_GLOBAL.containsKey(key);
  }

  public static String getXMLPlacesFileName() {
    return getWorkDir(true) + MY_CELLAR_XML;
  }

  public static String getXMLTypesFileName() {
    return getWorkDir(true) + TYPES_XML;
  }

  public static String getXMLMusicTypesFileName() {
    return getWorkDir(true) + TYPES_MUSIC_XML;
  }

  public static String getXMLBottlesFileName() {
    return getWorkDir(true) + BOUTEILLES_XML;
  }

  public static String getPreviewXMLFileName() {
    return getGlobalDir() + PREVIEW_XML;
  }

  private static String getPreviewHTMLFileName() {
    return getGlobalDir() + PREVIEW_HTML;
  }

  public static Storage getStorage() {
    return SerializedStorage.getInstance();
  }

  public static String getLabel(String id) {
    return getLabel(id, true);
  }

  public static String getLabel(String id, LabelProperty labelProperty) {
    if (labelProperty == null) {
      return getLabel(id, true);
    }
    String label = getLabel(id, true);
    label = label.replaceAll(KEY_TYPE, getLabelForType(labelProperty.isPlural(), labelProperty.isUppercaseFirst(), labelProperty.getGrammar()));
    if (labelProperty.isThreeDashes()) {
      label += THREE_DOTS;
    }
    if (labelProperty.isDoubleQuote()) {
      label += LanguageFileLoader.isFrench() ? SPACE + DOUBLE_DOT : DOUBLE_DOT;
    }
    return label;
  }

  public static String getLabel(String id, boolean displayError) {
    try {
      return LanguageFileLoader.getLabel(id);
    } catch (MissingResourceException e) {
      if (displayError) {
        Debug("Program: ERROR: Missing Label " + id);
        JOptionPane.showMessageDialog(null, "Missing Label " + id, "Error", JOptionPane.ERROR_MESSAGE);
      }
      return id;
    }
  }

  public static String getError(String id, LabelProperty labelProperty) {
    if (labelProperty == null) {
      return getError(id);
    }
    String label = getError(id);
    return label.replaceAll(KEY_TYPE, getLabelForType(labelProperty.isPlural(), labelProperty.isUppercaseFirst(), labelProperty.getGrammar()));
  }

  public static String getError(String id) {
    try {
      return LanguageFileLoader.getError(id);
    } catch (MissingResourceException e) {
      JOptionPane.showMessageDialog(null, "Missing Error " + id, "Error", JOptionPane.ERROR_MESSAGE);
      return id;
    }
  }

  static List<String> getLanguages() {
    return LanguageFileLoader.getLanguages();
  }

  static int getLanguageIndex(String language) {
    return LanguageFileLoader.getLanguageIndex(language);
  }

  static String getLanguage(int val) {
    return LanguageFileLoader.getLanguageFromIndex(val);
  }

  public static boolean open(String filename, boolean check) {
    if (isNullOrEmpty(filename)) {
      return false;
    }
    File file = new File(filename.trim());
    if (check) {
      if (!file.exists() || file.isDirectory()) {
        //Fichier non trouve Verifier le chemin
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error020"), filename), getError("Error022"));
        return false;
      }
    }

    try {
      if (System.getProperty("os.name").startsWith("Mac")) {
        Runtime.getRuntime().exec("/usr/bin/open " + file.getAbsolutePath());
      } else {
        Desktop.getDesktop().browse(file.toURI());
      }
    } catch (IOException e) {
      showException(e, true);
    }
    return true;
  }

  public static boolean hasYearControl() {
    return getCaveConfigBool(MyCellarSettings.ANNEE_CTRL, false);
  }

  static void setYearControl(boolean yearControl) {
    putCaveConfigBool(MyCellarSettings.ANNEE_CTRL, yearControl);
  }

  public static List<CountryJaxb> getCountries() {
    return CountryListJaxb.getInstance().getCountries();
  }

  public static void setModified() {
    modified = true;
  }

  private static boolean isModified() {
    return modified;
  }

  public static void setListCaveModified() {
    listCaveModified = true;
  }

  private static boolean isListCaveModified() {
    return listCaveModified;
  }

  public static PDFProperties getPDFProperties() {
    String title = getCaveConfigString(MyCellarSettings.PDF_TITLE, "");
    int titleSize = getCaveConfigInt(MyCellarSettings.TITLE_SIZE, 10);
    int textSize = getCaveConfigInt(MyCellarSettings.TEXT_SIZE, 10);
    final boolean border = getCaveConfigBool(MyCellarSettings.BORDER, true);
    boolean boldTitle = getCaveConfigBool(MyCellarSettings.BOLD, false);

    PDFProperties properties = new PDFProperties(title, titleSize, textSize, border, boldTitle);

    int nbCol = Objects.requireNonNull(MyCellarFields.getFieldsList()).size();
    int countColumn = 0;
    for (int i = 0; i < nbCol; i++) {
      int export = getCaveConfigInt(MyCellarSettings.SIZE_COL + i + "EXPORT", 0);
      if (export == 1) {
        countColumn++;
        int sizeCol = getCaveConfigInt(MyCellarSettings.SIZE_COL + i, 5);
        properties.addColumn(MyCellarFields.getFieldsList().get(i), i, sizeCol, MyCellarFields.getFieldsList().get(i).toString());
      }
    }
    if (countColumn == 0) {
      properties.addColumn(MyCellarFields.getFieldsList().get(0), 0, 10, MyCellarFields.getFieldsList().get(0).toString());
      properties.addColumn(MyCellarFields.getFieldsList().get(1), 1, 2, MyCellarFields.getFieldsList().get(1).toString());
      properties.addColumn(MyCellarFields.getFieldsList().get(3), 3, 5, MyCellarFields.getFieldsList().get(3).toString());
      properties.addColumn(MyCellarFields.getFieldsList().get(7), 7, 2, MyCellarFields.getFieldsList().get(7).toString());
    }
    return properties;
  }

  static List<PDFRow> getPDFRows(List<? extends MyCellarObject> list, PDFProperties properties) {
    LinkedList<PDFRow> rows = new LinkedList<>();
    LinkedList<PDFColumn> columns = properties.getColumns();
    for (MyCellarObject myCellarObject : list) {
      PDFRow row = new PDFRow();
      for (PDFColumn column : columns) {
        row.addCell(MyCellarFields.getValue(column.getField(), myCellarObject));
      }
      rows.add(row);
    }
    return rows;
  }

  static PDFRow getPDFHeader(PDFProperties properties) {
    LinkedList<PDFColumn> columns = properties.getColumns();
    PDFRow row = new PDFRow();
    for (PDFColumn column : columns) {
      row.addCell(column.getTitle());
    }
    return row;
  }

  private static void cleanDebugFiles() {
    String sDir = System.getProperty("user.home") + File.separator + "MyCellarDebug";
    File f = new File(sDir);
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime monthsAgo = LocalDateTime.now().minusMonths(2);
    String[] files = f.list((dir, name) -> {
      String date = "";
      if (name.startsWith("Debug-") && name.endsWith(".log")) {
        date = name.substring(6, name.indexOf(".log"));
      }
      if (name.startsWith("DebugFtp-") && name.endsWith(".log")) {
        date = name.substring(9, name.indexOf(".log"));
      }
      if (!date.isEmpty()) {
        String[] fields = date.split("-");
        LocalDateTime dateTime = now.withMonth(Integer.parseInt(fields[1])).withDayOfMonth(Integer.parseInt(fields[0])).withYear(Integer.parseInt(fields[2]));
        return dateTime.isBefore(monthsAgo);
      }
      return false;
    });

    if (files != null) {
      for (String file : files) {
        f = new File(sDir, file);
        Debug("Program: Deleting file " + f.getAbsolutePath());
        f.deleteOnExit();
      }
    }
  }

  private static void cleanTempDirs() {
    String sDir = System.getProperty("user.home") + File.separator + "MyCellar";

    File file = new File(sDir);
    if (!file.exists()) {
      return;
    }

    long time = Long.parseLong(LocalDateTime.now().minusMonths(2).format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)));

    final String[] list = file.list();
    if (list != null) {
      List<Long> oldTime = Arrays.stream(list)
          .filter(StringUtils::isNumeric)
          .map(Long::parseLong)
          .filter(value -> value < time).collect(Collectors.toList());

      oldTime.forEach(value -> DIR_TO_DELETE.add(new File(file + File.separator + value)));
    }
  }

  public static void saveShowColumns(String value) {
    putCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN, value);
  }

  public static String getShowColumns() {
    return getCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN, "");
  }

  public static void saveShowColumnsWork(String value) {
    putCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN_WORK, value);
  }

  public static String getShowColumnsWork() {
    return getCaveConfigString(MyCellarSettings.SHOWFILE_COLUMN_WORK, "");
  }

  static void saveHTMLColumns(List<MyCellarFields> cols) {
    StringBuilder s = new StringBuilder();
    for (MyCellarFields f : cols) {
      if (s.length() != 0) {
        s.append(COLUMNS_SEPARATOR);
      }
      s.append(f.name());
    }
    putCaveConfigString(MyCellarSettings.HTMLEXPORT_COLUMN, s.toString());
  }

  static ArrayList<MyCellarFields> getHTMLColumns() {
    ArrayList<MyCellarFields> cols = new ArrayList<>();
    String s = getCaveConfigString(MyCellarSettings.HTMLEXPORT_COLUMN, "");
    String[] fields = s.split(COLUMNS_SEPARATOR);
    for (String field : fields) {
      List<MyCellarFields> fieldsList = MyCellarFields.getFieldsList();
      if (null != fieldsList) {
        for (MyCellarFields f : fieldsList) {
          if (f.name().equals(field)) {
            cols.add(f);
            break;
          }
        }
      }
    }
    return cols;
  }

  public static String readFirstLineText(final File f) {
    if (f == null || !f.exists()) {
      return "";
    }
    if (!f.getName().toLowerCase().endsWith(".txt")) {
      return "";
    }
    Debug("Program: Reading first line of file " + f.getName());
    try (var scanner = new Scanner(f)) {
      if (scanner.hasNextLine()) {
        return toCleanString(scanner.nextLine());
      }
    } catch (FileNotFoundException e) {
      showException(e, true);
    }
    return "";
  }

  public static HistoryList getHistoryList() {
    return getStorage().getHistoryList();
  }

  public static WorkSheetList getWorksheetList() {
    return getStorage().getWorksheetList();
  }

  public static List<History> getHistory() {
    return Collections.unmodifiableList(getStorage().getHistoryList().getHistory());
  }

  public static BigDecimal safeStringToBigDecimal(final String value, BigDecimal defaultValue) {
    try {
      return stringToBigDecimal(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static BigDecimal stringToBigDecimal(final String value) throws NumberFormatException {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c == ' ') {
        continue;
      }
      if (c == ',' || c == '.') {
        buf.append('.');
      }
      if (Character.isDigit(c)) {
        buf.append(c);
      }
    }
    return new BigDecimal(buf.toString()).setScale(2, RoundingMode.HALF_UP);
  }

  static int getNewID() {
    if (nextID == -1) {
      nextID = getStorage().getBottlesCount();
    }
    do {
      ++nextID;
    } while (getStorage().getAllList().stream().anyMatch(bouteille -> bouteille.getId() == nextID));
    return nextID;
  }

  public static void modifyBottles(LinkedList<MyCellarObject> listToModify) {
    if (listToModify == null || listToModify.isEmpty()) {
      return;
    }
    if (listToModify.size() == 1) {
      ProgramPanels.showBottle(listToModify.getFirst(), true);
    } else {
      new OpenAddVinAction(listToModify).actionPerformed(null);
    }
  }

  public static List<MyCellarObject> getExistingBottles(List<Integer> bouteilles) {
    return getStorage().getAllList().stream().filter(bouteille -> bouteilles.contains(bouteille.getId())).collect(Collectors.toList());
  }

  public static boolean isExistingBottle(MyCellarObject bouteille) {
    return getStorage().getAllList().stream().anyMatch(bouteille1 -> bouteille1.getId() == bouteille.getId());
  }

  public static int safeParseInt(String value, int defaultValue) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ignored) {
      return defaultValue;
    }
  }

  static void exit() {
    cleanTempDirs();
    deleteTempFiles();
    cleanDebugFiles();
    Debug("Program: MyCellar End");
    closeDebug();
  }

  static boolean isFileSavable() {
    return myCellarFile != null && myCellarFile.isFileSavable();
  }

  public static void throwNotImplementedIfNotFor(MyCellarObject myCellarObject, Class<?> aClass) {
    if (!aClass.isInstance(myCellarObject)) {
      throw new NotImplementedException("Not implemented For " + aClass);
    }
  }

  public static void throwNotImplementedForNewType() {
    throw new NotImplementedException("Not implemented For New Type");
  }

  public static void throwNotImplemented() {
    throw new NotImplementedException("Not implemented yet!");
  }

  enum Type {
    WINE,
    BOOK,
    MUSIC;

    static Type typeOf(String value) {
      try {
        return valueOf(value);
      } catch (IllegalArgumentException e) {
        return WINE;
      }
    }
  }
}
