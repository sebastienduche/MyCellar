package mycellar;

import mycellar.actions.ExportPDFAction;
import mycellar.actions.OpenWorkSheetAction;
import mycellar.capacity.CapacityPanel;
import mycellar.core.Grammar;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IPlace;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarVersion;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.exceptions.UnableToOpenFileException;
import mycellar.core.exceptions.UnableToOpenMyCellarFileException;
import mycellar.core.language.Language;
import mycellar.core.storage.ListeBouteille;
import mycellar.core.uicomponents.MyCellarAction;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarMenuItem;
import mycellar.general.ProgramPanels;
import mycellar.general.XmlUtils;
import mycellar.launcher.MyCellarServer;
import mycellar.placesmanagement.CellarOrganizerPanel;
import mycellar.placesmanagement.Creer_Rangement;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import mycellar.showfile.ShowFile;
import mycellar.vignobles.VineyardPanel;
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
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

import static mycellar.Filtre.EXTENSION_SINFO;
import static mycellar.Program.toCleanString;
import static mycellar.ProgramConstants.CHAR_C;
import static mycellar.ProgramConstants.CHAR_O;
import static mycellar.ProgramConstants.CHAR_V;
import static mycellar.ProgramConstants.CHAR_X;
import static mycellar.ProgramConstants.DOWNLOAD_COMMAND;
import static mycellar.ProgramConstants.FR;
import static mycellar.ProgramConstants.INFOS_VERSION;
import static mycellar.ProgramConstants.INTERNAL_VERSION;
import static mycellar.ProgramConstants.MAIN_VERSION;
import static mycellar.ProgramConstants.ONE_DOT;
import static mycellar.ProgramConstants.OPTIONS_PARAM;
import static mycellar.ProgramConstants.RESTART_COMMAND;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.STAR;
import static mycellar.core.MyCellarSettings.PROGRAM_TYPE;
import static mycellar.general.ProgramPanels.selectOrAddTab;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 30.7
 * @since 01/01/22
 */
public final class Start extends JFrame implements Thread.UncaughtExceptionHandler {

  static final long serialVersionUID = 501073;
  private static final Start INSTANCE = new Start();
  private final JButton m_oSupprimerButton = new JButton();
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
  private final MyCellarLabel update = new MyCellarLabel("");
  private final MyCellarLabel version = new MyCellarLabel("");
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
  private final MyCellarMenuItem menuSetConfig = new MyCellarMenuItem(LabelType.INFO, "374", LabelProperty.SINGLE.withThreeDashes());
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
  private JMenuBar m_oMenuBar = new JMenuBar();
  private boolean m_bHasListener = false;
  private boolean m_bHasFrameBuilded = false;
  private Preferences prefs;

  private Start() {
  }

  public static void main(String[] args) {
    try {
      final SplashScreen splashscreen = new SplashScreen();
      // initialisation
      Program.start();
      // Lecture des parametres
      // ______________________

      String parameters = "";

      for (String arg : args) {
        parameters = parameters.concat(arg + SPACE);
      }
      if (!parameters.isBlank()) {
        int nIndex = parameters.indexOf(OPTIONS_PARAM);
        if (nIndex == -1) {
          // demarrage sans options
          Program.setNewFile(toCleanString(parameters));
        } else {
          // demarrage avec options
          // ______________________
          String tmp = parameters.substring(0, nIndex);
          // Recuperation du nom du fichier
          if (tmp.contains(ONE_DOT + EXTENSION_SINFO)) {
            Program.setNewFile(tmp.strip());
          } else {
            // On prend tout ce qu'il y a apres -opts
            tmp = parameters.substring(nIndex);
            if (tmp.contains(ONE_DOT + EXTENSION_SINFO)) {
              // Si l'on trouve l'extension du fichier
              // on cherche le caractere ' ' qui va separer les
              // options du nom du fichier
              String tmp2 = tmp.strip();
              tmp2 = tmp2.substring(tmp2.indexOf(SPACE));
              Program.setNewFile(tmp2.strip());
            }
          }
          // Recuperation des options
          tmp = parameters.substring(nIndex + OPTIONS_PARAM.length()).strip().toLowerCase();
          if (tmp.indexOf(' ') != -1) {
            tmp = tmp.substring(0, tmp.indexOf(' ')).strip();
          }
          // Options a gerer
          if (RESTART_COMMAND.equals(tmp)) {
            // Demarrage avec une nouvelle cave
            Program.putGlobalConfigBool(MyCellarSettings.STARTUP, false);
            Program.putCaveConfigBool(MyCellarSettings.ANNEE_CTRL, true);
            Program.putCaveConfigBool(MyCellarSettings.FIC_EXCEL, false);
          } else if (DOWNLOAD_COMMAND.equals(tmp)) {
            Debug("Download a new version and exit");
            MyCellarServer.getInstance().downloadVersion();
            System.exit(3);
          }
        }
      }

      Thread.setDefaultUncaughtExceptionHandler((t, e) -> Program.showException(e, true));

      while (splashscreen.isRunning()) {
      }

      SwingUtilities.invokeLater(() -> getInstance().startup());
    } catch (UnableToOpenFileException e) {
      Program.showException(e);
      System.exit(998);
    } catch (ExceptionInInitializerError a) {
      JOptionPane.showMessageDialog(null, "Error during program initialisation!!\nProgram files corrupted!!\nPlease reinstall the program.",
          "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(999);
    }
  }

  public static Start getInstance() {
    return INSTANCE;
  }

  private static void Debug(String sText) {
    Program.Debug("Start: " + sText);
  }

  /**
   * Fonction d'initialisation de l'application
   */
  private void startup() {
    Thread.currentThread().setUncaughtExceptionHandler(this);
    prefs = Preferences.userNodeForPackage(getClass());

    // Controle des MAJ
    // Appel serveur pour alimenter la derniere version en ligne
    MyCellarServer.getInstance().getServerVersion();

    // Demarrage
    // _________

    Program.initializeLanguageProgramType();
    boolean hasFile = Program.hasFile();
    if (!hasFile && !Program.getGlobalConfigBool(MyCellarSettings.STARTUP, false)) {
      // Langue au premier demarrage
      String lang = System.getProperty("user.language");
      if (FR.equalsIgnoreCase(lang)) {
        lang = "F";
      } else {
        lang = "U";
      }
      Program.putGlobalConfigString(MyCellarSettings.LANGUAGE, lang);
      Program.putGlobalConfigBool(MyCellarSettings.STARTUP, true);
    }

    afficheFrame();
    if (hasFile) {
      Program.loadPropertiesAndSetProgramType();
      if (Program.getCaveLength() == 0) {
        Program.addCave(Program.DEFAULT_PLACE);
      }
    }
    enableAll(hasFile);
    setVisible(true);
  }

  /**
   * Fonction pour desactiver ou activer toutes les options ou boutons
   */
  public void enableAll(boolean enable) {
    menuCloseFile.setEnabled(enable);
    exportButton.setEnabled(enable);
    statsButton.setEnabled(enable);
    managePlaceButton.setEnabled(enable && Program.getCave().stream().anyMatch(Predicate.not(Rangement::isSimplePlace)));
    worksheetButton.setEnabled(enable);
    tableButton.setEnabled(enable);
    m_oSupprimerButton.setEnabled(enable);
    addButton.setEnabled(enable);
    searchButton.setEnabled(enable);
    menuExport.setEnabled(enable);
    menuStats.setEnabled(enable);
    menuTable.setEnabled(enable);
    menuAddObject.setEnabled(enable);
    menuModifPlace.setEnabled(enable);
    menuDelPlace.setEnabled(enable);
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
   * quitter_actionPerformed: Quitter le programme
   */
  private void quitter_actionPerformed() {
    for (Component c : ProgramPanels.TABBED_PANE.getComponents()) {
      if (c instanceof ITabListener) {
        if (!((ITabListener) c).tabWillClose(null)) {
          Debug("Exiting progam cancelled!");
          return;
        }
      }
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

  private void about_actionPerformed() {
    new APropos().setVisible(true);
  }

  private void news_actionPerformed() {
    Program.open("Finish.html", false);
  }

  /**
   * importXmlPlace_actionPerformed: Permet d'importer une liste de rangement
   * au format xml
   */
  private void importXmlPlace_actionPerformed() {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    int retour_jfc = boiteFichier.showOpenDialog(this);
    if (retour_jfc == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      if (nomFichier == null) {
        setCursor(Cursor.getDefaultCursor());
        Erreur.showSimpleErreur(Program.getError("FileNotFound"));
        Debug("ERROR: ImportXmlPlace: File not found during Opening!");
        return;
      }
      String fic = nomFichier.getAbsolutePath();
      int index = fic.indexOf(ONE_DOT);
      if (index == -1) {
        fic = fic.concat(".xml");
      }
      File f = new File(fic);
      LinkedList<Rangement> cave = new LinkedList<>();
      if (f.exists() && XmlUtils.readMyCellarXml(fic, cave)) {
        XmlUtils.writeMyCellarXml(cave, "");
        Program.loadObjects();
      }
    }
  }

  /**
   * Actions realisees apres l'ouverture d'un fichier
   */
  private void postOpenFile() {
    Program.loadPropertiesAndSetProgramType();
    if (Program.getCaveLength() == 0) {
      Program.addCave(Program.DEFAULT_PLACE);
    }
    enableAll(true);
    ProgramPanels.updateAllPanels();
    updateMainPanel();
    ProgramPanels.PANEL_INFOS.setEnable(true);
    ProgramPanels.PANEL_INFOS.refresh();
    String tmp = Program.getShortFilename();
    if (tmp.isEmpty()) {
      setTitle(Program.getLabel("Infos001"));
    } else {
      setTitle(Program.getLabel("Infos001") + " - [" + tmp + "]");
    }
  }

  /**
   * Ouverture d'un fichier
   *
   * @param file
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
        updateMainPanel();
        setTitle(Program.getLabel("Infos001"));
      }
    } catch (UnableToOpenFileException e) {
      if (!(e instanceof UnableToOpenMyCellarFileException)) {
        Erreur.showSimpleErreur(Program.getError("Error.LoadingFile"));
      }
      Program.showException(e, false);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  private void openFile(String file) throws UnableToOpenFileException {
    Program.openaFile(new File(file));
    postOpenFile();
  }

  /**
   * reopen1_actionPerformed: Ouvre un fichier precedement ouvert
   */
  private void reopen1_actionPerformed() {
    String sFile = Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, "");
    Debug("Reopen1FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  /**
   * reopen2_actionPerformed: Ouvre un fichier precedement ouvert
   */
  private void reopen2_actionPerformed() {
    String sFile = Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, "");
    Debug("Reopen2FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  /**
   * reopen3_actionPerformed: Ouvre un fichier precedement ouvert
   */
  private void reopen3_actionPerformed() {
    String sFile = Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, "");
    Debug("Reopen3FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  /**
   * reopen4_actionPerformed: Ouvre un fichier precedement ouvert
   */
  private void reopen4_actionPerformed() {
    String sFile = Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, "");
    Debug("Reopen4FileAction: Restart with file " + sFile);
    reOpenFile(sFile);
  }

  /**
   * closeFile_actionPerformed: Ferme un fichier
   */
  private void closeFile_actionPerformed() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    Program.closeFile();
    enableAll(false);
    ProgramPanels.PANEL_INFOS.setEnable(false);
    ProgramPanels.PANEL_INFOS.refresh();
    updateMainPanel();
    setTitle(Program.getLabel("Infos001"));
    setCursor(Cursor.getDefaultCursor());
  }

  /**
   * exportXmlPlace_actionPerformed: Permet d'exporter la liste des rangements
   * au format xml
   */
  private void exportXmlPlace_actionPerformed() {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      String fic = nomFichier.getAbsolutePath();
      fic = MyCellarControl.controlAndUpdateExtension(fic, ".xml");
      XmlUtils.writeMyCellarXml(Program.getCave(), fic);
    }
  }

  /**
   * exportXml_actionPerformed: Permet d'exporter la liste des objets au format xml
   */
  private void exportXml_actionPerformed() {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      String fic = nomFichier.getAbsolutePath();
      fic = MyCellarControl.controlAndUpdateExtension(fic, ".xml");
      ListeBouteille.writeXML(new File(fic));
    }
  }

  void updateLabels() {
    final String quitter = Program.getLabel("QUITTER");
    if (quitter == null || quitter.isEmpty()) {
      Program.setLanguage(Language.FRENCH);
      quitChar = Program.getLabel("QUITTER").charAt(0);
    } else {
      quitChar = quitter.charAt(0);
    }

    importChar = Program.getLabel("IMPORT").charAt(0);
    addWineChar = Program.getLabel("AJOUTERV").charAt(0);
    addPlaceChar = Program.getLabel("AJOUTERR").charAt(0);
    exportChar = Program.getLabel("EXPORT").charAt(0);
    tableChar = Program.getLabel("TABLEAUX").charAt(0);
    statChar = Program.getLabel("STAT").charAt(0);
    modifyChar = Program.getLabel("MODIF").charAt(0);
    searchChar = Program.getLabel("RECHERCHE").charAt(0);
    deleteChar = Program.getLabel("SUPPR").charAt(0);
    viewChar = Program.getLabel("VISUAL").charAt(0);
    historyChar = Program.getLabel("HISTORY").charAt(0);
    saveChar = Program.getLabel("SAVE").charAt(0);
    newChar = Program.getLabel("NEW").charAt(0);
    m_oMenuBar = new JMenuBar();

    // differents menus
    menuFile.setText(Program.getLabel("Infos104")); // Fichier
    menuPlace.setText(Program.getLabel("Infos081"));
    menuWine.setText(Program.getLabel("Main.Item", LabelProperty.SINGLE.withCapital())); // Vin
    menuTools.setText(Program.getLabel("Infos246"));
    menuEdition.setText(Program.getLabel("Infos245"));

    // differents choix de chaque menu
    menuShowWorksheet.setText(Program.getLabel("ShowFile.Worksheet"));
    menuSearch.setText(Program.getLabel("Main.tabSearchButton"));
    menuQuit.setText(Program.getLabel("Infos003"));
    menuHelp.setText(Program.getLabel("Infos111"));

    about.setText(Program.getLabel("Infos199")); // A Propos
    menuNews.setText(Program.getLabel("Infos330")); // Nouveautes
    menuToCreate.setText(Program.getLabel("Infos267")); // Rangement a creer
    menuVignobles.setText(Program.getLabel("Infos165") + "..."); // Vignobles
    menuBottleCapacity.setText(Program.getLabel("Infos400") + "..."); // Contenance
    menuImportXmlPlaces.setText(Program.getLabel("Infos367")); // Importer des rangements xml
    menuExportXmlPlaces.setText(Program.getLabel("Infos368")); // Exporter des rangements xml
    menuExportXml.setText(Program.getLabel("Infos408")); // Exporter au format xml
    menuCloseFile.setText(Program.getLabel("Infos019")); // Fermer...
    menuCheckUpdate.setText(Program.getLabel("Infos379")); // Check update
    menuReopen1.setText("1 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, "")) + ONE_DOT + EXTENSION_SINFO);
    menuReopen2.setText("2 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, "")) + ONE_DOT + EXTENSION_SINFO);
    menuReopen3.setText("3 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, "")) + ONE_DOT + EXTENSION_SINFO);
    menuReopen4.setText("4 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, "")) + ONE_DOT + EXTENSION_SINFO);
    menuReopen1.setAccelerator(KeyStroke.getKeyStroke('1', InputEvent.CTRL_DOWN_MASK));
    menuReopen2.setAccelerator(KeyStroke.getKeyStroke('2', InputEvent.CTRL_DOWN_MASK));
    menuReopen3.setAccelerator(KeyStroke.getKeyStroke('3', InputEvent.CTRL_DOWN_MASK));
    menuReopen4.setAccelerator(KeyStroke.getKeyStroke('4', InputEvent.CTRL_DOWN_MASK));
    menuReopen1.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, ""));
    menuReopen2.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, ""));
    menuReopen3.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, ""));
    menuReopen4.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, ""));

    menuCut.setText(Program.getLabel("Infos241"));
    menuCopy.setText(Program.getLabel("Infos242"));
    menuPaste.setText(Program.getLabel("Infos243"));

    importButton.setText(Program.getLabel("Infos011")); // Importer
    exportButton.setText(Program.getLabel("Infos125"));
    createButton.setText(Program.getLabel("Infos010"));
    statsButton.setText(Program.getLabel("Infos009"));
    managePlaceButton.setText(Program.getLabel("Main.ManagePlace"));
    worksheetButton.setText(Program.getLabel("ShowFile.Worksheet"));
    modifyButton.setText(Program.getLabel("Infos007"));
    showFileButton.setText(Program.getLabel("Infos324"));
    tableButton.setText(Program.getLabel("Infos008"));
    addButton.setText(Program.getLabel("Main.tabAdd", LabelProperty.SINGLE));
    searchButton.setText(Program.getLabel("Main.tabSearchButton"));
    m_oSupprimerButton.setText(Program.getLabel("Infos004"));
    version.setText(Program.getLabel("MonthVersion") + INFOS_VERSION + MAIN_VERSION);
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
    String tmp = Program.getShortFilename();
    Program.DEFAULT_PLACE.setName(Program.getLabel("Program.DefaultPlace"));
    if (tmp.isEmpty()) {
      setTitle(Program.getLabel("Infos001"));
    } else {
      setTitle(Program.getLabel("Infos001") + " - [" + tmp + "]");
    }
  }

  private void aide_actionPerformed() {
    Program.getAide();
  }

  /**
   * afficheFrame: Affiche la fenetre principale
   */
  private void afficheFrame() {
    if (m_bHasFrameBuilded) {
      // On ne reconstruit que le menu Fichier pour remettre a jour la
      // liste des fichiers ouverts recement
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
      if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, "").isEmpty()) {
        menuFile.addSeparator();
        menuFile.add(menuReopen1);
      }
      if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, "").isEmpty()) {
        menuFile.add(menuReopen2);
      }
      if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, "").isEmpty()) {
        menuFile.add(menuReopen3);
      }
      if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, "").isEmpty()) {
        menuFile.add(menuReopen4);
      }
      menuFile.addSeparator();
      menuFile.add(menuQuit);
      return;
    }

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

    final String tmp = Program.getShortFilename();
    if (tmp.isEmpty()) {
      setTitle(Program.getLabel("Infos001"));
    } else {
      setTitle(Program.getLabel("Infos001") + " - [" + tmp + "]");
    }
    setResizable(true);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = prefs.getInt("Start.x", -1);
    int y = prefs.getInt("Start.y", -1);
    if (x >= 0 && y >= 0) {
      setLocation(x, y);
    } else {
      setLocation(0, 0);
    }
    int w = prefs.getInt("Start.width", -1);
    int h = prefs.getInt("Start.height", -1);
    if (w >= 0 && h >= 0) {
      setSize(w, h);
    } else {
      setSize(screenSize.width, screenSize.height);
    }
    setLayout(new MigLayout("", "[grow]", "[][grow][]"));

    MyCellarLabel copyright = new MyCellarLabel("Copyright S\u00e9bastien D.");
    copyright.setFont(new Font("Dialog", Font.PLAIN, 10));
    version.setFont(new Font("Dialog", Font.PLAIN, 10));
    update.setFont(new Font("Dialog", Font.PLAIN, 10));
    update.setBorder(BorderFactory.createEtchedBorder());
    update.setBackground(Color.LIGHT_GRAY);
    add(update, "gapleft 20, gaptop 10, hidemode 1, wrap");
    add(ProgramPanels.TABBED_PANE, "grow, hidemode 3, wrap");
    add(ProgramPanels.PANEL_INFOS, "grow, hidemode 3, wrap");
    add(copyright, "align right, gapright 10, wrap");
    add(version, "align right, gapright 10, gapbottom 10");
    ProgramPanels.TABBED_PANE.setVisible(false);

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
    m_oSupprimerButton.setAction(new DeletePlaceAction());
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

    boolean bUpdateAvailable = MyCellarServer.getInstance().hasAvailableUpdate(MyCellarVersion.getLocalVersion());

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
    if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, "").isEmpty()) {
      menuFile.addSeparator();
      menuFile.add(menuReopen1);
    }
    if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, "").isEmpty()) {
      menuFile.add(menuReopen2);
    }
    if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, "").isEmpty()) {
      menuFile.add(menuReopen3);
    }
    if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, "").isEmpty()) {
      menuFile.add(menuReopen4);
    }
    menuFile.addSeparator();
    menuFile.add(menuQuit);
    menuPlace.add(menuAddPlace);
    menuPlace.add(menuModifPlace);
    menuPlace.add(menuDelPlace);
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
    menuTools.add(menuSetConfig);
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
    menuOpenFile.setAccelerator(KeyStroke.getKeyStroke(CHAR_O, InputEvent.CTRL_DOWN_MASK));
    menuCut.setAccelerator(KeyStroke.getKeyStroke(CHAR_X, InputEvent.CTRL_DOWN_MASK));
    menuCopy.setAccelerator(KeyStroke.getKeyStroke(CHAR_C, InputEvent.CTRL_DOWN_MASK));
    menuPaste.setAccelerator(KeyStroke.getKeyStroke(CHAR_V, InputEvent.CTRL_DOWN_MASK));
    m_oMenuBar.add(menuFile);
    m_oMenuBar.add(menuEdition);
    m_oMenuBar.add(menuPlace);
    m_oMenuBar.add(menuWine);
    m_oMenuBar.add(menuTools);
    m_oMenuBar.add(menuAbout);
    setJMenuBar(m_oMenuBar);

    if (!m_bHasListener) {
      setListeners();
    }

    update.setVisible(bUpdateAvailable);
    if (bUpdateAvailable) {
      update.setText(MessageFormat.format(Program.getLabel("Infos385"), MyCellarServer.getInstance().getAvailableVersion(), MAIN_VERSION + "-" + INTERNAL_VERSION), true, 30000, false);
    }
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    m_bHasFrameBuilded = true;

    Debug("Display Frame ended");
  }

  private void setListeners() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        quitter_actionPerformed();
      }
    });

    ProgramPanels.TABBED_PANE.addChangeListener((arg) -> {
      ProgramPanels.updateSelectedTab();
    });

    menuQuit.addActionListener((e) -> quitter_actionPerformed());
    about.addActionListener((e) -> about_actionPerformed());
    menuNews.addActionListener((e) -> news_actionPerformed());
    menuVignobles.setAction(new VignoblesAction());
    menuBottleCapacity.setAction(new CapacityAction());

    menuImportXmlPlaces.addActionListener((e) -> importXmlPlace_actionPerformed());
    menuExportXmlPlaces.addActionListener((e) -> exportXmlPlace_actionPerformed());
    menuExportXml.addActionListener((e) -> exportXml_actionPerformed());
    menuCloseFile.addActionListener((e) -> closeFile_actionPerformed());
    menuToCreate.addActionListener((e) -> RangementUtils.findRangementToCreate());
    menuReopen1.addActionListener((e) -> reopen1_actionPerformed());
    menuReopen2.addActionListener((e) -> reopen2_actionPerformed());
    menuReopen3.addActionListener((e) -> reopen3_actionPerformed());
    menuReopen4.addActionListener((e) -> reopen4_actionPerformed());
    menuHelp.addActionListener((e) -> aide_actionPerformed());
    menuSetConfig.addActionListener((e) -> menuSetConfig_actionPerformed());
    menuCheckUpdate.addActionListener((e) -> menuCheckUpdate_actionPerformed());

    m_bHasListener = true;
  }

  public void updateMainPanel() {
    managePlaceButton.setEnabled(Program.getCave().stream().anyMatch(Predicate.not(Rangement::isSimplePlace)));
  }

  /**
   * menuSaveAs_actionPerformed: Sauvegarde sous
   */
  private void menuSaveAs_actionPerformed() {
    JFileChooser boiteFichier = new JFileChooser();
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
    int retour_jfc = boiteFichier.showSaveDialog(this);
    if (retour_jfc == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      if (nomFichier == null) {
        setCursor(Cursor.getDefaultCursor());
        Erreur.showSimpleErreur(Program.getError("FileNotFound"));
        Debug("ERROR: menuSaveAs: File not found during Opening!");
        return;
      }
      String fic = nomFichier.getAbsolutePath();
      fic = MyCellarControl.controlAndUpdateExtension(fic, Filtre.FILTRE_SINFO);

      setEnabled(false);
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Program.saveAs(new File(fic));
      setCursor(Cursor.getDefaultCursor());
      setTitle(Program.getLabel("Infos001") + " - [" + Program.getShortFilename(fic) + "]");
      setEnabled(true);
    }
  }

  /**
   * menuSetConfig_actionPerformed: Modification des parametres internes
   */
  private void menuSetConfig_actionPerformed() {
    MyOptions myoptions = new MyOptions(Program.getLabel("Infos374"), Program.getLabel("Infos375"), Collections.singletonList("JTextField"), "",
        Program.getCaveConfig(), true, true);
    myoptions.setVisible(true);
  }

  private void menuCheckUpdate_actionPerformed() {
    if (MyCellarServer.getInstance().hasAvailableUpdate(MyCellarVersion.getLocalVersion())) {
      Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Infos384"), MyCellarServer.getInstance().getAvailableVersion(), INTERNAL_VERSION), true);
    } else {
      Erreur.showSimpleErreur(Program.getLabel("Infos388"), true);
    }
  }

  void removeCurrentTab() {
    ProgramPanels.removeSelectedTab();
    updateMainPanel();
  }

  public void openVineyardPanel() {
    final VineyardPanel vineyardPanel = ProgramPanels.createVineyardPanel();
    try {
      final String label = Program.getLabel("Infos165");
      final int index = ProgramPanels.TABBED_PANE.getTabCount() - 1;
      ProgramPanels.TABBED_PANE.add(label, vineyardPanel);
      ProgramPanels.TABBED_PANE.setIconAt(index, null);
      ProgramPanels.addTabLabel(index, label);
      Utils.addCloseButton(ProgramPanels.TABBED_PANE, vineyardPanel);
      ProgramPanels.TABBED_PANE.setSelectedComponent(vineyardPanel);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    updateMainPanel();
  }

  public void openCapacityPanel() {
    final CapacityPanel capacityPanel = ProgramPanels.createCapacityPanel();
    try {
      final int index = ProgramPanels.TABBED_PANE.getTabCount() - 1;
      final String label = Program.getLabel("Infos400");
      ProgramPanels.TABBED_PANE.add(label, capacityPanel);
      ProgramPanels.TABBED_PANE.setIconAt(index, null);
      ProgramPanels.addTabLabel(index, label);
      Utils.addCloseButton(ProgramPanels.TABBED_PANE, capacityPanel);
      ProgramPanels.TABBED_PANE.setSelectedComponent(capacityPanel);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    updateMainPanel();
  }

  public void openCellChooserPanel(IPlace iPlace) {
    final int selectedTabIndex = ProgramPanels.getSelectedTabIndex() + 1;
    final CellarOrganizerPanel chooseCellPanel = ProgramPanels.createChooseCellPanel(iPlace);
    try {
      final String label = Program.getLabel("Main.ChooseCell");
      ProgramPanels.TABBED_PANE.insertTab(label, null, chooseCellPanel, null, selectedTabIndex);
      ProgramPanels.TABBED_PANE.setIconAt(selectedTabIndex, MyCellarImage.PLACE);
      ProgramPanels.insertTabLabel(selectedTabIndex, label);
      Utils.addCloseButton(ProgramPanels.TABBED_PANE, chooseCellPanel, ProgramPanels.getSelectedTabIndex());
      ProgramPanels.TABBED_PANE.setSelectedComponent(chooseCellPanel);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
    updateMainPanel();
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    Program.showException(e, true);
  }

  public void addToMenuTools(JMenuItem menuItem) {
    menuTools.add(menuItem);
  }

  public void removeFromMenuTools(JMenuItem menuItem) {
    menuTools.remove(menuItem);
  }

  static class ObjectType {
    private final Program.Type type;

    public ObjectType(Program.Type type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return Program.getLabelForType(type, true, true, Grammar.NONE);
    }

    public Program.Type getType() {
      return type;
    }
  }

  static final class CutAction extends MyCellarAction {
    private static final long serialVersionUID = -8024045169612180263L;

    private CutAction(boolean withText) {
      super(LabelType.INFO, "241", LabelProperty.SINGLE, MyCellarImage.CUT);
      setDescriptionLabelCode("Infos241");
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
    private static final long serialVersionUID = -4416042464174203695L;

    private CopyAction(boolean withText) {
      super(LabelType.INFO, "242", LabelProperty.SINGLE, MyCellarImage.COPY);
      setDescriptionLabelCode("Infos242");
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
    private static final long serialVersionUID = 7152419581737782003L;

    private PasteAction(boolean withText) {
      super(LabelType.INFO, "243", LabelProperty.SINGLE, MyCellarImage.PASTE);
      setDescriptionLabelCode("Infos243");
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
      Arrays.stream(Program.Type.values())
          .filter(type -> !type.equals(Program.Type.BOOK))
          .forEach(type -> {
            final ObjectType type1 = new ObjectType(type);
            objectTypes.add(type1);
            types.addItem(type1);
          });

      ObjectType objectType = findObjectType(Program.Type.valueOf(Program.getCaveConfigString(PROGRAM_TYPE, Program.getGlobalConfigString(PROGRAM_TYPE, Program.Type.WINE.name()))));
      types.setSelectedItem(objectType);

      setLayout(new MigLayout("", "[grow]", "[]25px[]"));
      add(new MyCellarLabel(Program.getLabel("Start.selectTypeObject")), "span 2, wrap");
      add(new MyCellarLabel(LabelType.INFO_OTHER, "Parameters.typeLabel"));
      add(types);
    }

    private ObjectType findObjectType(Program.Type type) {
      return objectTypes.stream().filter(objectType -> objectType.getType() == type).findFirst().orElse(null);
    }

    public Program.Type getSelectedType() {
      return ((ObjectType) Objects.requireNonNull(types.getSelectedItem())).getType();
    }
  }

  final class OpenAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;

    private OpenAction(boolean withText) {
      super(LabelType.INFO, "372", LabelProperty.SINGLE, MyCellarImage.OPEN);
      setDescriptionLabelCode("Infos372");
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      try {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR, ""));
        boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
        boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
        if (JFileChooser.APPROVE_OPTION == boiteFichier.showOpenDialog(null)) {
          File file = boiteFichier.getSelectedFile();
          if (file == null) {
            setCursor(Cursor.getDefaultCursor());
            Erreur.showSimpleErreur(Program.getError("FileNotFound"));
            Debug("ERROR: OpenAction: File not found during Opening!");
            ProgramPanels.updateAllPanels();
            updateMainPanel();
            setTitle(Program.getLabel("Infos001"));
            return;
          }
          String fic = MyCellarControl.controlAndUpdateExtension(file.getAbsolutePath(), Filtre.FILTRE_SINFO);
          openFile(fic);
        }
      } catch (UnableToOpenFileException e) {
        Erreur.showSimpleErreur(Program.getError("Error.LoadingFile"));
        Program.showException(e, false);
      } finally {
        setCursor(Cursor.getDefaultCursor());
      }
    }
  }

  final class NewAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;

    private NewAction(boolean withText) {
      super(LabelType.INFO, "378", MyCellarImage.NEW);
      setDescriptionLabelCode("Infos378");
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
      Program.newFile();
      postOpenFile();
      Debug("newFileAction: Creating a new file OK");
    }
  }

  final class SaveAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;

    private SaveAction(boolean withText) {
      super(LabelType.INFO, "326", MyCellarImage.SAVE);
      setDescriptionLabelCode("Infos326");
      setWithText(withText);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (Program.getShortFilename().startsWith("Untitled")) {
        menuSaveAs_actionPerformed();
        return;
      }
      try {
        for (int i = 0; i < ProgramPanels.TABBED_PANE.getTabCount(); i++) {
          Component tab = ProgramPanels.TABBED_PANE.getComponentAt(i);
          if (tab instanceof ManageBottle) {
            if (ProgramPanels.TABBED_PANE.getTitleAt(i).endsWith(STAR)) {
              ProgramPanels.TABBED_PANE.setSelectedIndex(i);
            }
            if (!((ManageBottle) tab).save()) {
              return;
            }
          }
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
    private static final long serialVersionUID = -2340786091568284033L;

    private SaveAsAction() {
      super(LabelType.INFO, "371", MyCellarImage.SAVEAS);
      setDescriptionLabelCode("Infos371");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      menuSaveAs_actionPerformed();
    }
  }

  final class AddWineAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Main.tabAdd";

    private AddWineAction() {
      super(LabelType.INFO_OTHER, LABEL, MyCellarImage.WINE);
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(addPlaceChar, InputEvent.CTRL_DOWN_MASK));
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final AddVin addVin = ProgramPanels.createAddVin();
      selectOrAddTab(addVin, LABEL, MyCellarImage.WINE);
      addVin.reInit();
      updateMainPanel();
    }
  }

  final class AddPlaceAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos010";

    private AddPlaceAction() {
      super(LabelType.INFO, "109", MyCellarImage.PLACE);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createCreerRangement(), LABEL, MyCellarImage.PLACE);
      updateMainPanel();
    }
  }

  final class DeletePlaceAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos004";

    private DeletePlaceAction() {
      super(LabelType.INFO_OTHER, "Main.Delete", LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.DELPLACE);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createSupprimerRangement(), LABEL, MyCellarImage.DELPLACE);
      updateMainPanel();
    }
  }

  final class ModifyPlaceAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos007";

    private ModifyPlaceAction() {
      super(LabelType.INFO, "079", LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.MODIFYPLACE);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final Creer_Rangement modifierRangement = ProgramPanels.createModifierRangement();
      selectOrAddTab(modifierRangement, LABEL, MyCellarImage.MODIFYPLACE);
      modifierRangement.updateView();
      updateMainPanel();
    }
  }

  final class SearchAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Main.tabSearchSimple";

    private SearchAction() {
      super(LabelType.INFO_OTHER, "Main.tabSearchButton", MyCellarImage.SEARCH);
      setDescriptionLabelCode("Main.tabSearch");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createSearch(), LABEL, MyCellarImage.SEARCH);
      updateMainPanel();
    }
  }

  final class CreateTabAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos008";

    private CreateTabAction() {
      super(LabelType.INFO, "008", MyCellarImage.TABLE);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createCreerTableaux(), LABEL, MyCellarImage.TABLE);
      updateMainPanel();
    }
  }

  final class ImportFileAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos011";

    private ImportFileAction() {
      super(LabelType.INFO, "107", MyCellarImage.IMPORT);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createImporter(), LABEL, MyCellarImage.IMPORT);
      updateMainPanel();
    }
  }

  final class ExportFileAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos148";

    private ExportFileAction() {
      super(LabelType.INFO, "108", LabelProperty.SINGLE, MyCellarImage.EXPORT);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createExport(), LABEL, MyCellarImage.EXPORT);
      updateMainPanel();
    }
  }

  final class StatAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos009";

    private StatAction() {
      super(LabelType.INFO, "009", MyCellarImage.STATS);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final Stat stat = ProgramPanels.createStat();
      selectOrAddTab(stat, LABEL, MyCellarImage.STATS);
      stat.updateView();
      updateMainPanel();
    }
  }

  class ShowHistoryAction extends MyCellarAction {
    private static final long serialVersionUID = -2981766233846291757L;
    private static final String LABEL = "Infos341";

    private ShowHistoryAction() {
      super(LabelType.INFO, "341", LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final ShowHistory showHistory = ProgramPanels.createShowHistory();
      selectOrAddTab(showHistory, LABEL, null);
      showHistory.refresh();
      updateMainPanel();
    }
  }

  class VignoblesAction extends MyCellarAction {

    private static final long serialVersionUID = -7956676252030557402L;

    private VignoblesAction() {
      super(LabelType.INFO, "165", LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      openVineyardPanel();
    }
  }

  class CapacityAction extends MyCellarAction {

    private static final long serialVersionUID = -7204054967253027549L;

    private CapacityAction() {
      super(LabelType.INFO, "400", LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      openCapacityPanel();
      ProgramPanels.createAddVin().updateView();
    }
  }

  final class ShowFileAction extends MyCellarAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Infos325";

    private ShowFileAction() {
      super(LabelType.INFO, "324", MyCellarImage.SHOW);
      setDescriptionLabelCode(LABEL);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createShowFile(), LABEL, MyCellarImage.SHOW);
      updateMainPanel();
    }
  }

  final class ShowTrashAction extends AbstractAction {
    private static final long serialVersionUID = -3212527164505184899L;
    private static final String LABEL = "Main.ShowTrash";

    private ShowTrashAction() {
      super("", MyCellarImage.TRASH);
      putValue(SHORT_DESCRIPTION, Program.getLabel(LABEL));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final ShowFile showTrash = ProgramPanels.createShowTrash();
      selectOrAddTab(showTrash, LABEL, MyCellarImage.TRASH);
      showTrash.updateView();
      updateMainPanel();
    }
  }

  final class ManagePlaceAction extends MyCellarAction {
    private static final long serialVersionUID = -5144284671743409095L;
    private static final String LABEL = "Main.ManagePlace";

    private ManagePlaceAction() {
      super(LabelType.INFO_OTHER, "Main.ManagePlace", MyCellarImage.PLACE);
      putValue(SHORT_DESCRIPTION, Program.getLabel(LABEL));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createCellarOrganizerPanel(), LABEL, MyCellarImage.PLACE);
      updateMainPanel();
    }
  }

  final class ParametersAction extends MyCellarAction {
    private static final long serialVersionUID = -5144284671743409095L;
    private static final String LABEL = "Infos156";

    private ParametersAction() {
      super(LabelType.INFO, "156", LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.PARAMETER);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      selectOrAddTab(ProgramPanels.createParametres(), LABEL, MyCellarImage.PARAMETER);
      updateMainPanel();
    }
  }
}
