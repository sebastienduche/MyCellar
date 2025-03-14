package mycellar.frame;

import mycellar.APropos;
import mycellar.AddVin;
import mycellar.Erreur;
import mycellar.Filtre;
import mycellar.MoveLine;
import mycellar.MyCellarControl;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.ProgramType;
import mycellar.ShowHistory;
import mycellar.Stat;
import mycellar.actions.ExportPDFAction;
import mycellar.actions.OpenWorkSheetAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IPlacePosition;
import mycellar.core.MyCellarFile;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarVersion;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.exceptions.UnableToOpenFileException;
import mycellar.core.exceptions.UnableToOpenMyCellarFileException;
import mycellar.core.storage.ListeBouteille;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.Language;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.core.uicomponents.MyCellarAction;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarMenuItem;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.general.ProgramPanels;
import mycellar.general.XmlUtils;
import mycellar.launcher.MyCellarServer;
import mycellar.placesmanagement.CellarOrganizerPanel;
import mycellar.placesmanagement.Creer_Rangement;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.showfile.TrashPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

import static mycellar.Filtre.EXTENSION_SINFO;
import static mycellar.MyCellarUtils.getShortFilename;
import static mycellar.MyCellarUtils.isDefined;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.Program.getGlobalConfigString;
import static mycellar.ProgramConstants.DASH;
import static mycellar.ProgramConstants.FR;
import static mycellar.ProgramConstants.INFOS_VERSION;
import static mycellar.ProgramConstants.INTERNAL_VERSION;
import static mycellar.ProgramConstants.MAIN_VERSION;
import static mycellar.ProgramConstants.ONE_DOT;
import static mycellar.ProgramConstants.UNTITLED;
import static mycellar.core.MyCellarSettings.DIR;
import static mycellar.core.MyCellarSettings.PROGRAM_TYPE;
import static mycellar.core.text.LabelProperty.A_SINGLE;
import static mycellar.core.text.LabelProperty.PLURAL;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ProgramPanels.selectOrAddTab;
import static mycellar.general.ResourceErrorKey.ERROR_FILENOTFOUND;
import static mycellar.general.ResourceErrorKey.ERROR_LOADINGFILE;
import static mycellar.general.ResourceKey.AJOUTERR;
import static mycellar.general.ResourceKey.AJOUTERV;
import static mycellar.general.ResourceKey.CREATESTORAGE_TITLE;
import static mycellar.general.ResourceKey.EXPORT;
import static mycellar.general.ResourceKey.HISTORY;
import static mycellar.general.ResourceKey.IMPORT;
import static mycellar.general.ResourceKey.IMPORT_TITLE;
import static mycellar.general.ResourceKey.MAIN_ABOUT;
import static mycellar.general.ResourceKey.MAIN_ADD;
import static mycellar.general.ResourceKey.MAIN_CHECKUPDATE;
import static mycellar.general.ResourceKey.MAIN_CHOOSECELL;
import static mycellar.general.ResourceKey.MAIN_CLOSE;
import static mycellar.general.ResourceKey.MAIN_COPY;
import static mycellar.general.ResourceKey.MAIN_CREATETABLE;
import static mycellar.general.ResourceKey.MAIN_CUT;
import static mycellar.general.ResourceKey.MAIN_DELETE;
import static mycellar.general.ResourceKey.MAIN_DELETESTORAGE;
import static mycellar.general.ResourceKey.MAIN_EDIT;
import static mycellar.general.ResourceKey.MAIN_EXIT;
import static mycellar.general.ResourceKey.MAIN_EXPORT;
import static mycellar.general.ResourceKey.MAIN_EXPORTFILE;
import static mycellar.general.ResourceKey.MAIN_EXPORTXMLPLACES;
import static mycellar.general.ResourceKey.MAIN_FILE;
import static mycellar.general.ResourceKey.MAIN_FILECONTENT;
import static mycellar.general.ResourceKey.MAIN_FILEEXPORT;
import static mycellar.general.ResourceKey.MAIN_HELP;
import static mycellar.general.ResourceKey.MAIN_HISTORY;
import static mycellar.general.ResourceKey.MAIN_IMPORTXMLPLACES;
import static mycellar.general.ResourceKey.MAIN_ITEM;
import static mycellar.general.ResourceKey.MAIN_MANAGEPLACE;
import static mycellar.general.ResourceKey.MAIN_MODIFY;
import static mycellar.general.ResourceKey.MAIN_MODIFYSTORAGE;
import static mycellar.general.ResourceKey.MAIN_NEW;
import static mycellar.general.ResourceKey.MAIN_OPEN;
import static mycellar.general.ResourceKey.MAIN_PASTE;
import static mycellar.general.ResourceKey.MAIN_SAVE;
import static mycellar.general.ResourceKey.MAIN_SAVEAS;
import static mycellar.general.ResourceKey.MAIN_SETTINGS;
import static mycellar.general.ResourceKey.MAIN_SHOWFILE;
import static mycellar.general.ResourceKey.MAIN_SHOWTRASH;
import static mycellar.general.ResourceKey.MAIN_STATISTICS;
import static mycellar.general.ResourceKey.MAIN_STORAGE;
import static mycellar.general.ResourceKey.MAIN_STORAGETOCREATE;
import static mycellar.general.ResourceKey.MAIN_TABADD;
import static mycellar.general.ResourceKey.MAIN_TABSEARCH;
import static mycellar.general.ResourceKey.MAIN_TABSEARCHBUTTON;
import static mycellar.general.ResourceKey.MAIN_TABSEARCHSIMPLE;
import static mycellar.general.ResourceKey.MAIN_TOOLS;
import static mycellar.general.ResourceKey.MAIN_UPDATEAVAILABLE;
import static mycellar.general.ResourceKey.MAIN_VINEYARDMANAGEMENT;
import static mycellar.general.ResourceKey.MAIN_WHATSNEW;
import static mycellar.general.ResourceKey.MAIN_XMLEXPORT;
import static mycellar.general.ResourceKey.MODIF;
import static mycellar.general.ResourceKey.MONTHVERSION;
import static mycellar.general.ResourceKey.MOVELINE_TITLE;
import static mycellar.general.ResourceKey.MYCELLAR;
import static mycellar.general.ResourceKey.NEW;
import static mycellar.general.ResourceKey.PARAMETERS_TYPELABEL;
import static mycellar.general.ResourceKey.PARAMETER_CAPACITIESMANAGEMENT;
import static mycellar.general.ResourceKey.PROGRAM_DEFAULTPLACE;
import static mycellar.general.ResourceKey.QUITTER;
import static mycellar.general.ResourceKey.RECHERCHE;
import static mycellar.general.ResourceKey.SAVE;
import static mycellar.general.ResourceKey.SHOWFILE_WORKSHEET;
import static mycellar.general.ResourceKey.START_KEY;
import static mycellar.general.ResourceKey.START_MODIFIED;
import static mycellar.general.ResourceKey.START_MODIFYPARAMETER;
import static mycellar.general.ResourceKey.START_NEWVERSION;
import static mycellar.general.ResourceKey.START_NOUPDATE;
import static mycellar.general.ResourceKey.START_PARAMETERTOMODIFY;
import static mycellar.general.ResourceKey.START_SELECTTYPEOBJECT;
import static mycellar.general.ResourceKey.START_VALUE;
import static mycellar.general.ResourceKey.STAT;
import static mycellar.general.ResourceKey.SUPPR;
import static mycellar.general.ResourceKey.TABLEAUX;
import static mycellar.general.ResourceKey.VISUAL;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 12/03/25
 */
public final class MainFrame extends JFrame implements Thread.UncaughtExceptionHandler {

  private final JButton deleteButton = new JButton();
  private final JButton addButton = new JButton();
  private final JButton searchButton = new JButton();
  private final JButton tableButton = new JButton();
  private final JButton exportButton = new JButton();
  private final JButton statsButton = new JButton();
  private final JButton managePlaceButton = new JButton();
  private final JButton worksheetButton = new JButton();
  private final JButton createButton = new JButton();
  private final JButton importButton = new JButton();
  private final JButton modifyButton = new JButton();
  private final JButton showFileButton = new JButton();
  private final JButton showTrashButton = new JButton();
  private final JButton cutButton = new JButton();
  private final JButton copyButton = new JButton();
  private final JButton pasteButton = new JButton();
  private final JButton buttonSave = new JButton();
  private final JButton buttonPdf = new JButton();
  private final JButton newButton = new JButton();
  private final JButton openButton = new JButton();
  private final MyCellarSimpleLabel update = new MyCellarSimpleLabel();
  private final MyCellarSimpleLabel version = new MyCellarSimpleLabel();
  // differents menus
  private final JMenu menuFile = new JMenu();
  private final JMenu menuPlace = new JMenu();
  private final JMenu menuEdition = new JMenu();
  private final JMenu menuWine = new JMenu();
  private final JMenu menuTools = new JMenu();
  private final JMenu menuAbout = new JMenu("?");
  // differents choix de chaque menu
  private final JMenuItem menuImport = new JMenuItem();
  private final JMenuItem menuQuit = new JMenuItem();
  private final JMenuItem menuExport = new JMenuItem();
  private final JMenuItem menuStats = new JMenuItem();
  private final JMenuItem menuTable = new JMenuItem();
  private final JMenuItem menuAddPlace = new JMenuItem();
  private final MyCellarMenuItem menuModifPlace = new MyCellarMenuItem(new ModifyPlaceAction());
  private final MyCellarMenuItem menuDelPlace = new MyCellarMenuItem(new DeletePlaceAction());
  private final JMenuItem movePlaceLine = new JMenuItem(new PlaceMoveLineAction());
  private final JMenuItem menuShowFile = new JMenuItem();
  private final JMenuItem menuShowWorksheet = new JMenuItem();
  private final JMenuItem menuAddObject = new JMenuItem();
  private final JMenuItem menuSearch = new JMenuItem();
  private final JMenuItem menuHelp = new JMenuItem();
  private final MyCellarMenuItem menuParameter = new MyCellarMenuItem(new ParametersAction());
  private final JMenuItem about = new JMenuItem();
  private final JMenuItem menuToCreate = new JMenuItem();
  private final JMenuItem menuNews = new JMenuItem();
  private final MyCellarMenuItem menuHistory = new MyCellarMenuItem(new ShowHistoryAction());
  private final JMenuItem menuVignobles = new JMenuItem();
  private final JMenuItem menuBottleCapacity = new JMenuItem();
  private final JMenuItem menuNewFile = new JMenuItem();
  private final JMenuItem menuSave = new JMenuItem();
  private final JMenuItem menuSaveAs = new JMenuItem();
  private final JMenuItem menuImportXmlPlaces = new JMenuItem();
  private final JMenuItem menuExportXmlPlaces = new JMenuItem();
  private final JMenuItem menuExportXml = new JMenuItem();
  private final JMenuItem menuOpenFile = new JMenuItem();
  private final JMenuItem menuCloseFile = new JMenuItem();
  private final JMenuItem menuReopen1 = new JMenuItem();
  private final JMenuItem menuReopen2 = new JMenuItem();
  private final JMenuItem menuReopen3 = new JMenuItem();
  private final JMenuItem menuReopen4 = new JMenuItem();
  private final JMenuItem menuCheckUpdate = new JMenuItem();
  private final JMenuItem menuCut = new JMenuItem();
  private final JMenuItem menuCopy = new JMenuItem();
  private final JMenuItem menuPaste = new JMenuItem();
  private char quitChar;
  private char importChar;
  private char addWineChar;
  private char addPlaceChar;
  private char exportChar;
  private char tableChar;
  private char statChar;
  private char modifyChar;
  private char searchChar;
  private char deleteChar;
  private char viewChar;
  private char historyChar;
  private char saveChar;
  private char newChar;
  private Preferences prefs;

  private static final MainFrame INSTANCE = new MainFrame();


  private MainFrame() {
  }

  public static MainFrame getInstance() {
    return INSTANCE;
  }

  public void initFrame() {
    Thread.currentThread().setUncaughtExceptionHandler(this);
    prefs = Preferences.userNodeForPackage(getClass());

    // Check for updates
    // Get the latest version from server
    MyCellarServer.getInstance().getServerVersion();

    Program.initializeLanguageProgramType();
    boolean hasFile = Program.hasOpenedFile();
    if (!hasFile && !Program.getGlobalConfigBool(MyCellarSettings.GLOBAL_STARTUP, false)) {
      // Langue au premier demarrage
      String lang = System.getProperty("user.language");
      if (FR.equalsIgnoreCase(lang)) {
        lang = Language.FRENCH.toString();
      } else {
        lang = Language.ENGLISH.toString();
      }
      Program.putGlobalConfigString(MyCellarSettings.GLOBAL_LANGUAGE, lang);
      Program.putGlobalConfigBool(MyCellarSettings.GLOBAL_STARTUP, true);
    }

    displayFrame();

    if (hasFile) {
      Program.loadPropertiesAndSetProgramType();
      Program.addDefaultPlaceIfNeeded();
    }
    enableAll(hasFile);
    setVisible(true);
  }

  /**
   * Enable / Disable all menus / buttons
   */
  public void enableAll(boolean enable) {
    menuCloseFile.setEnabled(enable);
    exportButton.setEnabled(enable);
    statsButton.setEnabled(enable);
    managePlaceButton.setEnabled(enable && Program.hasComplexPlace());
    worksheetButton.setEnabled(enable);
    tableButton.setEnabled(enable);
    deleteButton.setEnabled(enable);
    addButton.setEnabled(enable);
    searchButton.setEnabled(enable);
    menuExport.setEnabled(enable);
    menuStats.setEnabled(enable);
    menuTable.setEnabled(enable);
    menuAddObject.setEnabled(enable);
    menuModifPlace.setEnabled(enable);
    menuDelPlace.setEnabled(enable);
    movePlaceLine.setEnabled(enable);
    menuSearch.setEnabled(enable);
    modifyButton.setEnabled(enable);
    importButton.setEnabled(enable);
    showFileButton.setEnabled(enable);
    showTrashButton.setEnabled(enable);
    menuImport.setEnabled(enable);
    createButton.setEnabled(enable);
    menuSave.setEnabled(Program.isFileSavable());
    buttonSave.setEnabled(Program.isFileSavable());
    buttonPdf.setEnabled(enable);
    menuSaveAs.setEnabled(enable);
    menuAddPlace.setEnabled(enable);
    menuExportXmlPlaces.setEnabled(enable);
    menuImportXmlPlaces.setEnabled(enable);
    menuExportXml.setEnabled(enable);
    menuShowFile.setEnabled(enable);
    menuShowWorksheet.setEnabled(enable);
    menuToCreate.setEnabled(enable);
    menuHistory.setEnabled(enable);
    menuVignobles.setEnabled(enable);
    menuBottleCapacity.setEnabled(enable);
    menuParameter.setEnabled(enable);
    menuCut.setEnabled(enable);
    menuCopy.setEnabled(enable);
    menuPaste.setEnabled(enable);
    cutButton.setEnabled(enable);
    copyButton.setEnabled(enable);
    pasteButton.setEnabled(enable);
  }

  /**
   * Exit the program
   */
  private void exit(ActionEvent event) {
    if (!ProgramPanels.runExit()) {
      Debug("Exiting the program cancelled!");
      return;
    }
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Program.closeFile();
    prefs.putInt("Start.x", getLocation().x);
    prefs.putInt("Start.y", getLocation().y);
    prefs.putInt("Start.width", getSize().width);
    prefs.putInt("Start.height", getSize().height);
    setCursor(Cursor.getDefaultCursor());
    dispose();

    Program.exit();
    System.exit(0);
  }

  /**
   * Import a list of places from XML file
   */
  private void importXmlPlace(ActionEvent actionEvent) {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    int retour_jfc = boiteFichier.showOpenDialog(this);
    if (retour_jfc == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      if (nomFichier == null) {
        setCursor(Cursor.getDefaultCursor());
        Erreur.showSimpleErreur(getError(ERROR_FILENOTFOUND, ""));
        Debug("ERROR: ImportXmlPlace: File not found during Opening!");
        return;
      }
      String fic = nomFichier.getAbsolutePath();
      int index = fic.indexOf(ONE_DOT);
      if (index == -1) {
        fic = fic.concat(Filtre.EXTENSION_XML);
      }
      File f = new File(fic);
      LinkedList<AbstractPlace> abstractPlaces = new LinkedList<>();
      if (f.exists() && XmlUtils.readMyCellarXml(fic, abstractPlaces)) {
        XmlUtils.writeMyCellarXml(abstractPlaces, "");
        Program.loadData();
      }
    }
  }

  /**
   * Actions to do after opening a file
   */
  private void postOpenFile() {
    Program.loadPropertiesAndSetProgramType();
    Program.addDefaultPlaceIfNeeded();
    enableAll(true);
    ProgramPanels.updateAllPanels();
    ProgramPanels.PANEL_INFOS.setEnable(true);
    ProgramPanels.PANEL_INFOS.refresh();
    setApplicationTitle(Program.getShortFilename(), false);
  }

  private static void setApplicationTitle(String filename, boolean modified) {
    String modifyLabel = modified ? getLabel(START_MODIFIED) : "";
    if (filename.isEmpty()) {
      INSTANCE.setTitle(getLabel(MYCELLAR) + " " + modifyLabel);
    } else {
      INSTANCE.setTitle(getLabel(MYCELLAR) + " - [" + filename + "] " + modifyLabel);
    }
  }

  public static void setApplicationTitleModified() {
    setApplicationTitle(Program.getShortFilename(), true);
  }

  public static void setApplicationTitleUnmodified() {
    setApplicationTitle(Program.getShortFilename(), false);
  }

  /**
   * Open a file
   */
  private void reOpenFile(String file) {
    try {
      enableAll(false);
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if (!file.isEmpty()) {
        openFile(file);
      } else {
        enableAll(false);
        ProgramPanels.updateAllPanels();
        setApplicationTitle("", false);
      }
      updateManagePlaceButton();
    } catch (UnableToOpenFileException e) {
      if (!(e instanceof UnableToOpenMyCellarFileException)) {
        Erreur.showSimpleErreur(getError(ERROR_LOADINGFILE));
      }
      Program.showException(e, false);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  private void openFile(String file) throws UnableToOpenFileException {
    final MyCellarFile myCellarFile = new MyCellarFile(file);
    myCellarFile.setNewFile(false);
    Program.openaFile(myCellarFile);
    postOpenFile();
  }

  private void reopenFile1(ActionEvent e) {
    String sFile = getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN1);
    Debug("Reopen1FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  private void reopenFile2(ActionEvent e) {
    String sFile = getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN2);
    Debug("Reopen2FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  private void reopenFile3(ActionEvent e) {
    String sFile = getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN3);
    Debug("Reopen3FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  private void reopenFile4(ActionEvent e) {
    String sFile = getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN4);
    Debug("Reopen4FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  /**
   * Close a File
   */
  private void closeFile(ActionEvent e) {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Program.closeFile();
    enableAll(false);
    ProgramPanels.PANEL_INFOS.setEnable(false);
    ProgramPanels.PANEL_INFOS.refresh();
    setApplicationTitle("", false);
    setCursor(Cursor.getDefaultCursor());
  }

  /**
   * Export places to an XML file
   */
  private void exportXmlPlace(ActionEvent actionEvent) {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      String filename = boiteFichier.getSelectedFile().getAbsolutePath();
      filename = MyCellarControl.controlAndUpdateExtension(filename, Filtre.FILTRE_XML);
      XmlUtils.writeMyCellarXml(Program.getAbstractPlaces(), filename);
    }
  }

  /**
   * Export all objets in an XML File
   */
  private void exportXml(ActionEvent actionEvent) {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      String fic = nomFichier.getAbsolutePath();
      fic = MyCellarControl.controlAndUpdateExtension(fic, Filtre.FILTRE_XML);
      ListeBouteille.writeXML(new File(fic));
    }
  }

  public void updateLabels() {
    final String quitter = getLabel(QUITTER);
    if (quitter == null || quitter.isEmpty()) {
      Program.setLanguage(Language.FRENCH);
      quitChar = getLabel(QUITTER).charAt(0);
    } else {
      quitChar = quitter.charAt(0);
    }

    importChar = getLabel(IMPORT).charAt(0);
    addWineChar = getLabel(AJOUTERV).charAt(0);
    addPlaceChar = getLabel(AJOUTERR).charAt(0);
    exportChar = getLabel(EXPORT).charAt(0);
    tableChar = getLabel(TABLEAUX).charAt(0);
    statChar = getLabel(STAT).charAt(0);
    modifyChar = getLabel(MODIF).charAt(0);
    searchChar = getLabel(RECHERCHE).charAt(0);
    deleteChar = getLabel(SUPPR).charAt(0);
    viewChar = getLabel(VISUAL).charAt(0);
    historyChar = getLabel(HISTORY).charAt(0);
    saveChar = getLabel(SAVE).charAt(0);
    newChar = getLabel(NEW).charAt(0);

    // differents menus
    menuFile.setText(getLabel(MAIN_FILE));
    menuPlace.setText(getLabel(MAIN_STORAGE));
    menuWine.setText(getLabelWithProperty(MAIN_ITEM, LabelProperty.SINGLE.withCapital()));
    menuTools.setText(getLabel(MAIN_TOOLS));
    menuEdition.setText(getLabel(MAIN_EDIT));

    // differents choix de chaque menu
    menuShowWorksheet.setText(getLabel(SHOWFILE_WORKSHEET));
    menuSearch.setText(getLabel(MAIN_TABSEARCHBUTTON));
    menuQuit.setText(getLabel(MAIN_EXIT));
    menuHelp.setText(getLabel(MAIN_HELP));

    about.setText(getLabel(MAIN_ABOUT) + "...");
    menuNews.setText(getLabel(MAIN_WHATSNEW));
    menuToCreate.setText(getLabel(MAIN_STORAGETOCREATE));
    menuVignobles.setText(getLabel(MAIN_VINEYARDMANAGEMENT) + "...");
    menuBottleCapacity.setText(getLabel(PARAMETER_CAPACITIESMANAGEMENT) + "...");
    menuImportXmlPlaces.setText(getLabel(MAIN_IMPORTXMLPLACES));
    menuExportXmlPlaces.setText(getLabel(MAIN_EXPORTXMLPLACES));
    menuExportXml.setText(getLabel(MAIN_XMLEXPORT));
    menuCloseFile.setText(getLabel(MAIN_CLOSE));
    menuCheckUpdate.setText(getLabel(MAIN_CHECKUPDATE));
    menuReopen1.setText("1 - " + getShortFilename(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN1)) + ONE_DOT + EXTENSION_SINFO);
    menuReopen2.setText("2 - " + getShortFilename(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN2)) + ONE_DOT + EXTENSION_SINFO);
    menuReopen3.setText("3 - " + getShortFilename(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN3)) + ONE_DOT + EXTENSION_SINFO);
    menuReopen4.setText("4 - " + getShortFilename(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN4)) + ONE_DOT + EXTENSION_SINFO);
    menuReopen1.setAccelerator(KeyStroke.getKeyStroke('1', InputEvent.CTRL_DOWN_MASK));
    menuReopen2.setAccelerator(KeyStroke.getKeyStroke('2', InputEvent.CTRL_DOWN_MASK));
    menuReopen3.setAccelerator(KeyStroke.getKeyStroke('3', InputEvent.CTRL_DOWN_MASK));
    menuReopen4.setAccelerator(KeyStroke.getKeyStroke('4', InputEvent.CTRL_DOWN_MASK));
    menuReopen1.setToolTipText(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN1));
    menuReopen2.setToolTipText(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN2));
    menuReopen3.setToolTipText(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN3));
    menuReopen4.setToolTipText(getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN4));

    menuCut.setText(getLabel(MAIN_CUT));
    menuCopy.setText(getLabel(MAIN_COPY));
    menuPaste.setText(getLabel(MAIN_PASTE));

    importButton.setText(getLabel(IMPORT_TITLE));
    exportButton.setText(getLabel(MAIN_EXPORTFILE));
    createButton.setText(getLabel(CREATESTORAGE_TITLE));
    statsButton.setText(getLabel(MAIN_STATISTICS));
    managePlaceButton.setText(getLabel(MAIN_MANAGEPLACE));
    worksheetButton.setText(getLabel(SHOWFILE_WORKSHEET));
    modifyButton.setText(getLabel(MAIN_MODIFYSTORAGE));
    showFileButton.setText(getLabel(MAIN_SHOWFILE));
    tableButton.setText(getLabel(MAIN_CREATETABLE));
    addButton.setText(getLabelWithProperty(MAIN_TABADD, LabelProperty.SINGLE));
    searchButton.setText(getLabel(MAIN_TABSEARCHBUTTON));
    deleteButton.setText(getLabel(MAIN_DELETESTORAGE));
    version.setText(getLabel(MONTHVERSION) + INFOS_VERSION + MAIN_VERSION);
    menuAddObject.setAccelerator(KeyStroke.getKeyStroke(addWineChar, InputEvent.CTRL_DOWN_MASK));
    menuAddPlace.setAccelerator(KeyStroke.getKeyStroke(addPlaceChar, InputEvent.CTRL_DOWN_MASK));
    menuDelPlace.setAccelerator(KeyStroke.getKeyStroke(deleteChar, InputEvent.CTRL_DOWN_MASK));
    menuHistory.setAccelerator(KeyStroke.getKeyStroke(historyChar, InputEvent.CTRL_DOWN_MASK));
    menuTable.setAccelerator(KeyStroke.getKeyStroke(tableChar, InputEvent.CTRL_DOWN_MASK));
    menuStats.setAccelerator(KeyStroke.getKeyStroke(statChar, InputEvent.CTRL_DOWN_MASK));
    menuImport.setAccelerator(KeyStroke.getKeyStroke(importChar, InputEvent.CTRL_DOWN_MASK));
    menuExport.setAccelerator(KeyStroke.getKeyStroke(exportChar, InputEvent.CTRL_DOWN_MASK));
    menuModifPlace.setAccelerator(KeyStroke.getKeyStroke(modifyChar, InputEvent.CTRL_DOWN_MASK));
    menuQuit.setAccelerator(KeyStroke.getKeyStroke(quitChar, InputEvent.CTRL_DOWN_MASK));
    SwingUtilities.updateComponentTreeUI(this);
    Program.DEFAULT_PLACE.setName(getLabel(PROGRAM_DEFAULTPLACE));
    setApplicationTitle(Program.getShortFilename(), false);
  }

  /**
   * Display the main frame
   */
  private void displayFrame() {
    menuFile.removeAll();
    menuFile.add(menuNewFile);
    menuFile.add(menuOpenFile);
    menuFile.add(menuCloseFile);
    menuFile.addSeparator();
    menuFile.add(menuSave);
    menuFile.add(menuSaveAs);
    menuFile.addSeparator();
    menuFile.add(menuImport);
    menuFile.add(menuExport);
    menuFile.addSeparator();
    menuFile.add(menuStats);
    menuFile.add(menuTable);
    menuFile.add(menuShowFile);
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN1).isEmpty()) {
      menuFile.addSeparator();
      menuFile.add(menuReopen1);
    }
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN2).isEmpty()) {
      menuFile.add(menuReopen2);
    }
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN3).isEmpty()) {
      menuFile.add(menuReopen3);
    }
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN4).isEmpty()) {
      menuFile.add(menuReopen4);
    }
    menuFile.addSeparator();
    menuFile.add(menuQuit);

    SaveAsAction saveAsAction = new SaveAsAction();
    SearchAction searchAction = new SearchAction();
    AddPlaceAction addPlaceAction = new AddPlaceAction();
    ShowFileAction showFileAction = new ShowFileAction();
    CreateTabAction createTabAction = new CreateTabAction();
    StatAction statAction = new StatAction();
    ImportFileAction importFileAction = new ImportFileAction();
    ExportFileAction exportFileAction = new ExportFileAction();
    ManagePlaceAction managePlaceAction = new ManagePlaceAction();
    ShowTrashAction showTrashAction = new ShowTrashAction();
    AddWineAction addWineAction = new AddWineAction();

    setApplicationTitle(Program.getShortFilename(), false);
    setResizable(true);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(prefs.getInt("Start.x", 0), prefs.getInt("Start.y", 0));
    int w = prefs.getInt("Start.width", -1);
    int h = prefs.getInt("Start.height", -1);
    if (w >= 0 && h >= 0) {
      setSize(w, h);
    } else {
      setSize(screenSize.width, screenSize.height);
    }
    setLayout(new MigLayout("", "[grow]", "[][grow][]"));

    MyCellarSimpleLabel copyright = new MyCellarSimpleLabel("Copyright S\u00e9bastien D.");
    copyright.setFont(new Font("Dialog", Font.PLAIN, 10));
    version.setFont(new Font("Dialog", Font.PLAIN, 10));
    update.setFont(new Font("Dialog", Font.PLAIN, 10));
    update.setBorder(BorderFactory.createEtchedBorder());
    update.setBackground(Color.LIGHT_GRAY);
    add(update, "gapleft 20, gaptop 10, hidemode 1, wrap");
    add(ProgramPanels.getTabbedPane(), "grow, hidemode 3, wrap");
    add(ProgramPanels.PANEL_INFOS, "grow, hidemode 3, wrap");
    add(copyright, "align right, gapright 10, wrap");
    add(version, "align right, gapright 10, gapbottom 10");
    ProgramPanels.getTabbedPane().setVisible(false);

    addButton.setAction(addWineAction);
    menuAddObject.setAction(addWineAction);
    newButton.setAction(new NewAction(false));
    openButton.setAction(new OpenAction(false));
    buttonSave.setAction(new SaveAction(false));
    buttonPdf.setAction(new ExportPDFAction());
    cutButton.setAction(new CutAction(false));
    copyButton.setAction(new CopyAction(false));
    pasteButton.setAction(new PasteAction(false));
    searchButton.setAction(searchAction);
    createButton.setAction(addPlaceAction);
    modifyButton.setAction(new ModifyPlaceAction());
    deleteButton.setAction(new DeletePlaceAction());
    showFileButton.setAction(showFileAction);
    tableButton.setAction(createTabAction);
    statsButton.setAction(statAction);
    importButton.setAction(importFileAction);
    exportButton.setAction(exportFileAction);
    managePlaceButton.setAction(managePlaceAction);
    worksheetButton.setAction(new OpenWorkSheetAction());
    showTrashButton.setAction(showTrashAction);
    menuAddPlace.setAction(addPlaceAction);
    menuAddObject.setAction(addWineAction);
    menuSearch.setAction(searchAction);
    menuNewFile.setAction(new NewAction(true));
    menuOpenFile.setAction(new OpenAction(true));
    menuSave.setAction(new SaveAction(true));
    menuSaveAs.setAction(saveAsAction);
    menuImport.setAction(importFileAction);
    menuExport.setAction(exportFileAction);
    menuStats.setAction(statAction);
    menuTable.setAction(createTabAction);
    menuShowFile.setAction(showFileAction);
    menuCut.setAction(new CutAction(true));
    menuCopy.setAction(new CopyAction(true));
    menuPaste.setAction(new PasteAction(true));
    menuShowWorksheet.setAction(new OpenWorkSheetAction());

    JToolBar toolBar = new JToolBar();
    toolBar.add(newButton);
    toolBar.add(openButton);
    toolBar.add(buttonSave);
    toolBar.add(buttonPdf);
    toolBar.addSeparator();
    toolBar.add(cutButton);
    toolBar.add(copyButton);
    toolBar.add(pasteButton);
    toolBar.addSeparator();
    toolBar.add(addButton);
    toolBar.add(searchButton);
    toolBar.add(showFileButton);
    toolBar.add(tableButton);
    toolBar.add(statsButton);
    toolBar.add(exportButton);
    toolBar.add(managePlaceButton);
    toolBar.add(worksheetButton);
    toolBar.add(Box.createHorizontalGlue());
    toolBar.add(showTrashButton);
    toolBar.setFloatable(true);
    add(toolBar, BorderLayout.NORTH);

    if (MyCellarImage.ICON != null) {
      setIconImage(MyCellarImage.ICON.getImage());
    }

    // Ajout du Menu
    menuHelp.setAccelerator(KeyStroke.getKeyStroke("F1"));

    // Ajouter les choix au menu
    menuFile.removeAll();
    menuPlace.removeAll();
    menuEdition.removeAll();
    menuWine.removeAll();
    menuAbout.removeAll();
    menuTools.removeAll();
    menuFile.add(menuNewFile);
    menuFile.add(menuOpenFile);
    menuFile.add(menuCloseFile);
    menuFile.addSeparator();
    menuFile.add(menuSave);
    menuFile.add(menuSaveAs);
    menuFile.addSeparator();
    menuFile.add(menuImport);
    menuFile.add(menuExport);
    menuFile.addSeparator();
    menuFile.add(menuStats);
    menuFile.add(menuTable);
    menuFile.add(menuShowFile);
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN1).isEmpty()) {
      menuFile.addSeparator();
      menuFile.add(menuReopen1);
    }
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN2).isEmpty()) {
      menuFile.add(menuReopen2);
    }
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN3).isEmpty()) {
      menuFile.add(menuReopen3);
    }
    if (!getGlobalConfigString(MyCellarSettings.GLOBAL_LAST_OPEN4).isEmpty()) {
      menuFile.add(menuReopen4);
    }
    menuFile.addSeparator();
    menuFile.add(menuQuit);
    menuPlace.add(menuAddPlace);
    menuPlace.add(menuModifPlace);
    menuPlace.add(menuDelPlace);
    menuPlace.addSeparator();
    menuPlace.add(movePlaceLine);
    menuWine.add(menuAddObject);
    menuWine.add(menuSearch);
    menuEdition.add(menuCut);
    menuEdition.add(menuCopy);
    menuEdition.add(menuPaste);
    menuAbout.add(menuHelp);
    menuAbout.addSeparator();
    menuAbout.add(menuCheckUpdate);
    menuAbout.addSeparator();
    menuAbout.add(menuNews);
    menuTools.add(menuParameter);
    menuTools.add(menuShowWorksheet);
    menuTools.add(menuVignobles);
    menuTools.add(menuBottleCapacity);
    menuTools.add(menuHistory);
    menuTools.add(menuToCreate);
    menuTools.add(menuImportXmlPlaces);
    menuTools.add(menuExportXmlPlaces);
    menuTools.add(menuExportXml);
    menuTools.add(new MyCellarMenuItem(new SetConfigAction()));
    menuAbout.add(about);
    menuAddObject.setAccelerator(KeyStroke.getKeyStroke(addWineChar, InputEvent.CTRL_DOWN_MASK));
    menuAddPlace.setAccelerator(KeyStroke.getKeyStroke(addPlaceChar, InputEvent.CTRL_DOWN_MASK));
    menuDelPlace.setAccelerator(KeyStroke.getKeyStroke(deleteChar, InputEvent.CTRL_DOWN_MASK));
    menuShowFile.setAccelerator(KeyStroke.getKeyStroke(viewChar, InputEvent.CTRL_DOWN_MASK));
    menuHistory.setAccelerator(KeyStroke.getKeyStroke(historyChar, InputEvent.CTRL_DOWN_MASK));
    menuSearch.setAccelerator(KeyStroke.getKeyStroke(searchChar, InputEvent.CTRL_DOWN_MASK));
    menuTable.setAccelerator(KeyStroke.getKeyStroke(tableChar, InputEvent.CTRL_DOWN_MASK));
    menuStats.setAccelerator(KeyStroke.getKeyStroke(statChar, InputEvent.CTRL_DOWN_MASK));
    menuImport.setAccelerator(KeyStroke.getKeyStroke(importChar, InputEvent.CTRL_DOWN_MASK));
    menuExport.setAccelerator(KeyStroke.getKeyStroke(exportChar, InputEvent.CTRL_DOWN_MASK));
    menuModifPlace.setAccelerator(KeyStroke.getKeyStroke(modifyChar, InputEvent.CTRL_DOWN_MASK));
    menuQuit.setAccelerator(KeyStroke.getKeyStroke(quitChar, InputEvent.CTRL_DOWN_MASK));
    menuSave.setAccelerator(KeyStroke.getKeyStroke(saveChar, InputEvent.CTRL_DOWN_MASK));
    menuNewFile.setAccelerator(KeyStroke.getKeyStroke(newChar, InputEvent.CTRL_DOWN_MASK));
    menuOpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
    menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
    menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
    menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menuFile);
    menuBar.add(menuEdition);
    menuBar.add(menuPlace);
    menuBar.add(menuWine);
    menuBar.add(menuTools);
    menuBar.add(menuAbout);
    setJMenuBar(menuBar);

    setListeners();

    boolean bUpdateAvailable = MyCellarServer.getInstance().hasAvailableUpdate(MyCellarVersion.getLocalVersion());
    update.setVisible(bUpdateAvailable);
    if (bUpdateAvailable) {
      update.setText(getLabel(MAIN_UPDATEAVAILABLE, MyCellarServer.getInstance().getAvailableVersion(), MAIN_VERSION + DASH + INTERNAL_VERSION), true, 30000, false);
    }
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    Debug("Display Frame ended");
  }

  private void setListeners() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        exit(null);
      }
    });

    ProgramPanels.getTabbedPane().addChangeListener((arg) -> ProgramPanels.updateSelectedTab());

    menuQuit.addActionListener(this::exit);
    about.addActionListener((e) -> new APropos().setVisible(true));
    menuNews.addActionListener((e) -> Program.open("Finish.html", false));
    menuVignobles.setAction(new VignoblesAction());
    menuBottleCapacity.setAction(new CapacityAction());

    menuImportXmlPlaces.addActionListener(this::importXmlPlace);
    menuExportXmlPlaces.addActionListener(this::exportXmlPlace);
    menuExportXml.addActionListener(this::exportXml);
    menuCloseFile.addActionListener(this::closeFile);
    menuToCreate.addActionListener((e) -> PlaceUtils.findRangementToCreate());
    menuReopen1.addActionListener(this::reopenFile1);
    menuReopen2.addActionListener(this::reopenFile2);
    menuReopen3.addActionListener(this::reopenFile3);
    menuReopen4.addActionListener(this::reopenFile4);
    menuHelp.addActionListener((e) -> Program.getAide());
    menuCheckUpdate.addActionListener(this::checkUpdate);
  }

  public static void updateManagePlaceButton() {
    INSTANCE.managePlaceButton.setEnabled(Program.hasComplexPlace());
  }

  private void saveAs() {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
    if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      if (nomFichier == null) {
        setCursor(Cursor.getDefaultCursor());
        Erreur.showSimpleErreur(getError(ERROR_FILENOTFOUND, ""));
        Debug("ERROR: menuSaveAs: File not found during Opening!");
        return;
      }

      setEnabled(false);
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Program.saveAs(new File(MyCellarControl.controlAndUpdateExtension(nomFichier.getAbsolutePath(), Filtre.FILTRE_SINFO)));
      setCursor(Cursor.getDefaultCursor());
      setApplicationTitle(Program.getShortFilename(), false);
      setEnabled(true);
    }
  }

  private void checkUpdate(ActionEvent e) {
    if (MyCellarServer.getInstance().hasAvailableUpdate(MyCellarVersion.getLocalVersion())) {
      Erreur.showInformationMessage(getLabel(START_NEWVERSION, MyCellarServer.getInstance().getAvailableVersion(), INTERNAL_VERSION));
    } else {
      Erreur.showInformationMessage(getLabel(START_NOUPDATE));
    }
  }

  public void removeCurrentTab() {
    ProgramPanels.removeSelectedTab();
  }

  public void openVineyardPanel() {
    ProgramPanels.addTab(getLabel(MAIN_VINEYARDMANAGEMENT), null, ProgramPanels.createVineyardPanel());
  }

  public static void openCapacityPanel() {
    ProgramPanels.addTab(getLabel(PARAMETER_CAPACITIESMANAGEMENT), null, ProgramPanels.createCapacityPanel());
  }

  public void openCellChooserPanel(IPlacePosition iPlace) {
    final int selectedTabIndex = ProgramPanels.getSelectedTabIndex() + 1;
    final CellarOrganizerPanel chooseCellPanel = ProgramPanels.createChooseCellPanel(iPlace);
    ProgramPanels.insertTab(getLabel(MAIN_CHOOSECELL), MyCellarImage.PLACE, chooseCellPanel, selectedTabIndex);
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    Program.showException(e, true);
  }

  static class ObjectType {
    private final ProgramType type;

    public ObjectType(ProgramType type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return MyCellarLabelManagement.getLabelForType(type, PLURAL.withCapital());
    }

    public ProgramType getType() {
      return type;
    }
  }

  static final class CutAction extends MyCellarAction {

    private CutAction(boolean withText) {
      super(MAIN_CUT, LabelProperty.SINGLE, MyCellarImage.CUT);
      setDescriptionLabel(MAIN_CUT, LabelProperty.SINGLE);
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (ProgramPanels.isCutCopyPastTab()) {
        ProgramPanels.getSelectedComponent(ICutCopyPastable.class).cut();
      }
    }
  }

  static final class CopyAction extends MyCellarAction {

    private CopyAction(boolean withText) {
      super(MAIN_COPY, LabelProperty.SINGLE, MyCellarImage.COPY);
      setDescriptionLabel(MAIN_COPY, LabelProperty.SINGLE);
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (ProgramPanels.isCutCopyPastTab()) {
        ProgramPanels.getSelectedComponent(ICutCopyPastable.class).copy();
      }
    }
  }

  static final class PasteAction extends MyCellarAction {

    private PasteAction(boolean withText) {
      super(MAIN_PASTE, LabelProperty.SINGLE, MyCellarImage.PASTE);
      setDescriptionLabel(MAIN_PASTE, LabelProperty.SINGLE);
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (ProgramPanels.isCutCopyPastTab()) {
        ProgramPanels.getSelectedComponent(ICutCopyPastable.class).paste();
      }
    }
  }

  private static final class PanelObjectType extends JPanel {

    private final MyCellarComboBox<ObjectType> types = new MyCellarComboBox<>();
    private final List<ObjectType> objectTypes = new ArrayList<>();

    private PanelObjectType() {
      Arrays.stream(ProgramType.values())
          .filter(type -> !type.equals(ProgramType.BOOK))
          .forEach(type -> {
            final ObjectType type1 = new ObjectType(type);
            objectTypes.add(type1);
            types.addItem(type1);
          });

      ObjectType objectType = findObjectType(ProgramType.valueOf(Program.getCaveConfigString(PROGRAM_TYPE, getGlobalConfigString(PROGRAM_TYPE, ProgramType.WINE.name()))));
      types.setSelectedItem(objectType);

      setLayout(new MigLayout("", "[grow]", "[]25px[]"));
      add(new MyCellarLabel(START_SELECTTYPEOBJECT), "span 2, wrap");
      add(new MyCellarLabel(PARAMETERS_TYPELABEL));
      add(types);
    }

    private ObjectType findObjectType(ProgramType type) {
      return objectTypes.stream().filter(objectType -> objectType.getType() == type).findFirst().orElse(null);
    }

    public ProgramType getSelectedType() {
      return ((ObjectType) Objects.requireNonNull(types.getSelectedItem())).getType();
    }
  }

  final class OpenAction extends MyCellarAction {

    private OpenAction(boolean withText) {
      super(MAIN_OPEN, LabelProperty.SINGLE, MyCellarImage.OPEN);
      setDescriptionLabel(MAIN_OPEN, LabelProperty.SINGLE);
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      try {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(DIR));
        boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
        boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
        if (JFileChooser.APPROVE_OPTION == boiteFichier.showOpenDialog(null)) {
          File file = boiteFichier.getSelectedFile();
          if (file == null) {
            setCursor(Cursor.getDefaultCursor());
            Erreur.showSimpleErreur(getError(ERROR_FILENOTFOUND, ""));
            Debug("ERROR: OpenAction: File not found during Opening!");
            ProgramPanels.updateAllPanels();
            setApplicationTitle("", false);
            return;
          }
          openFile(MyCellarControl.controlAndUpdateExtension(file.getAbsolutePath(), Filtre.FILTRE_SINFO));
        }
      } catch (UnableToOpenFileException e) {
        Erreur.showSimpleErreur(getError(ERROR_LOADINGFILE));
        Program.showException(e, false);
      } finally {
        setCursor(Cursor.getDefaultCursor());
      }
    }
  }

  final class NewAction extends MyCellarAction {

    private NewAction(boolean withText) {
      super(MAIN_NEW, MyCellarImage.NEW);
      setDescriptionLabel(MAIN_NEW);
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Debug("newFileAction: Creating a new file...");
//			PanelObjectType panelObjectType = new PanelObjectType();
//			if (JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(getInstance(), panelObjectType,
//					"",
//					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)) {
//				return;
//			}
//			Program.putGlobalConfigString(PROGRAM_TYPE, panelObjectType.getSelectedType().name());
      Program.createNewFile();
      postOpenFile();
      Debug("newFileAction: Creating a new file OK");
    }
  }

  final class SaveAction extends MyCellarAction {

    private SaveAction(boolean withText) {
      super(MAIN_SAVE, MyCellarImage.SAVE);
      setDescriptionLabel(MAIN_SAVE);
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (Program.getShortFilename().startsWith(UNTITLED)) {
        saveAs();
        return;
      }
      try {
        if (!ProgramPanels.saveObjects()) {
          return;
        }
        setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Program.save();
        setEnabled(true);
      } catch (RuntimeException | MyCellarException e) {
        Program.showException(e);
      } finally {
        setCursor(Cursor.getDefaultCursor());
      }
    }
  }

  final class SaveAsAction extends MyCellarAction {

    private SaveAsAction() {
      super(MAIN_SAVEAS, MyCellarImage.SAVEAS);
      setDescriptionLabel(MAIN_SAVEAS);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      saveAs();
    }
  }

  final class AddWineAction extends MyCellarAction {

    private AddWineAction() {
      super(MAIN_TABADD, MyCellarImage.WINE);
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(addPlaceChar, InputEvent.CTRL_DOWN_MASK));
      setDescriptionLabel(MAIN_TABADD, A_SINGLE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final AddVin addVin = ProgramPanels.createAddVin();
      selectOrAddTab(addVin, MAIN_TABADD, MyCellarImage.WINE);
      addVin.reInit();
    }
  }

  static final class AddPlaceAction extends MyCellarAction {

    private AddPlaceAction() {
      super(MAIN_ADD, LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.PLACE);
      setDescriptionLabel(CREATESTORAGE_TITLE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createCreerRangement(), CREATESTORAGE_TITLE, MyCellarImage.PLACE);
    }
  }

  static final class DeletePlaceAction extends MyCellarAction {

    private DeletePlaceAction() {
      super(MAIN_DELETE, LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.DELPLACE);
      setDescriptionLabel(MAIN_DELETESTORAGE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createSupprimerRangement(), MAIN_DELETESTORAGE, MyCellarImage.DELPLACE);
    }
  }

  static final class PlaceMoveLineAction extends MyCellarAction {

    private PlaceMoveLineAction() {
      super(MOVELINE_TITLE, LabelProperty.SINGLE.withThreeDashes());
      setDescriptionLabel(MOVELINE_TITLE, LabelProperty.SINGLE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      new MoveLine();
    }
  }

  static final class ModifyPlaceAction extends MyCellarAction {

    private ModifyPlaceAction() {
      super(MAIN_MODIFY, LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.MODIFYPLACE);
      setDescriptionLabel(MAIN_MODIFYSTORAGE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final Creer_Rangement modifierRangement = ProgramPanels.createModifierRangement();
      selectOrAddTab(modifierRangement, MAIN_MODIFYSTORAGE, MyCellarImage.MODIFYPLACE);
      modifierRangement.updateView();
    }
  }

  static final class SearchAction extends MyCellarAction {

    private SearchAction() {
      super(MAIN_TABSEARCHBUTTON, MyCellarImage.SEARCH);
      setDescriptionLabel(MAIN_TABSEARCH, A_SINGLE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createSearch(), MAIN_TABSEARCHSIMPLE, MyCellarImage.SEARCH);
    }
  }

  static final class CreateTabAction extends MyCellarAction {

    private CreateTabAction() {
      super(MAIN_CREATETABLE, MyCellarImage.TABLE);
      setDescriptionLabel(MAIN_CREATETABLE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createCreateTable(), MAIN_CREATETABLE, MyCellarImage.TABLE);
    }
  }

  static final class ImportFileAction extends MyCellarAction {

    private ImportFileAction() {
      super(IMPORT_TITLE, LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.IMPORT);
      setDescriptionLabel(IMPORT_TITLE);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createImporter(), IMPORT_TITLE, MyCellarImage.IMPORT);
    }
  }

  static final class ExportFileAction extends MyCellarAction {

    private ExportFileAction() {
      super(MAIN_EXPORT, LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.EXPORT);
      setDescriptionLabel(MAIN_FILEEXPORT);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createExport(), MAIN_FILEEXPORT, MyCellarImage.EXPORT);
    }
  }

  static final class StatAction extends MyCellarAction {

    private StatAction() {
      super(MAIN_STATISTICS, MyCellarImage.STATS);
      setDescriptionLabel(MAIN_STATISTICS);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final Stat stat = ProgramPanels.createStat();
      selectOrAddTab(stat, MAIN_STATISTICS, MyCellarImage.STATS);
      stat.updateView();
    }
  }

  static class ShowHistoryAction extends MyCellarAction {

    private ShowHistoryAction() {
      super(MAIN_HISTORY, LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final ShowHistory showHistory = ProgramPanels.createShowHistory();
      selectOrAddTab(showHistory, MAIN_HISTORY, null);
      showHistory.refresh();
    }
  }

  class VignoblesAction extends MyCellarAction {

    private VignoblesAction() {
      super(MAIN_VINEYARDMANAGEMENT, LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      openVineyardPanel();
    }
  }

  static class CapacityAction extends MyCellarAction {

    private CapacityAction() {
      super(PARAMETER_CAPACITIESMANAGEMENT, LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      openCapacityPanel();
      ProgramPanels.createAddVin().updateView();
    }
  }

  static final class ShowFileAction extends MyCellarAction {

    private ShowFileAction() {
      super(MAIN_SHOWFILE, MyCellarImage.SHOW);
      setDescriptionLabel(MAIN_FILECONTENT);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createShowFile(), MAIN_FILECONTENT, MyCellarImage.SHOW);
    }
  }

  static final class ShowTrashAction extends AbstractAction {

    private ShowTrashAction() {
      super("", MyCellarImage.TRASH);
      putValue(SHORT_DESCRIPTION, getLabel(MAIN_SHOWTRASH));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final TrashPanel showTrash = ProgramPanels.createShowTrash();
      selectOrAddTab(showTrash, MAIN_SHOWTRASH, MyCellarImage.TRASH);
      showTrash.updateView();
    }
  }

  static final class ManagePlaceAction extends MyCellarAction {

    private ManagePlaceAction() {
      super(MAIN_MANAGEPLACE, MyCellarImage.PLACE);
      putValue(SHORT_DESCRIPTION, getLabel(MAIN_MANAGEPLACE));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createCellarOrganizerPanel(), MAIN_MANAGEPLACE, MyCellarImage.PLACE);
    }
  }

  static final class ParametersAction extends MyCellarAction {

    private ParametersAction() {
      super(MAIN_SETTINGS, LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.PARAMETER);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createParametres(), MAIN_SETTINGS, MyCellarImage.PARAMETER);
    }
  }

  static final class SetConfigAction extends MyCellarAction {

    private SetConfigAction() {
      super(START_MODIFYPARAMETER, LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow"));
      JTextField key = new JTextField();
      JTextField value = new JTextField();
      panel.add(new MyCellarLabel(START_PARAMETERTOMODIFY), "grow, wrap");
      panel.add(new MyCellarLabel(START_KEY), "split 2");
      panel.add(key, "grow, wrap");
      panel.add(new MyCellarLabel(START_VALUE), "split 2");
      panel.add(value, "grow");
      if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(getInstance(), panel, getLabel(START_MODIFYPARAMETER), JOptionPane.OK_CANCEL_OPTION)) {
        final String parameter = toCleanString(key.getText());
        final String parameterValue = toCleanString(value.getText());
        if (isDefined(parameter)) {
          Program.putGlobalConfigString(parameter, parameterValue);
          Program.saveGlobalProperties();
        }
      }
    }
  }

  private static void Debug(String sText) {
    Program.Debug("MainFrame: " + sText);
  }
}
