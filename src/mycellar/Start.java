package mycellar;

import mycellar.actions.ExportPDFAction;
import mycellar.actions.OpenWorkSheetAction;
import mycellar.capacity.CapacityPanel;
import mycellar.core.Grammar;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IPlace;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarAction;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarLabelManagement;
import mycellar.core.MyCellarMenuItem;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarVersion;
import mycellar.core.UnableToOpenFileException;
import mycellar.core.UnableToOpenMyCellarFileException;
import mycellar.general.ProgramPanels;
import mycellar.general.XmlUtils;
import mycellar.importer.Importer;
import mycellar.launcher.Server;
import mycellar.placesmanagement.Creer_Rangement;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import mycellar.placesmanagement.Supprimer_Rangement;
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
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

import static mycellar.Program.toCleanString;
import static mycellar.core.MyCellarSettings.PROGRAM_TYPE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 28.5
 * @since 07/05/21
 */
public class Start extends JFrame implements Thread.UncaughtExceptionHandler {

	private static final String RESTART_COMMAND = "restart";
	private static final String DOWNLOAD_COMMAND = "download";
	private static final String OPTIONS_PARAM = "-opts=";

	private final JButton m_oSupprimerButton = new JButton();
	private final JButton m_oAjouterButton = new JButton();
	private final JButton m_oRechercherButton = new JButton();
	private final JButton m_oTableauxButton = new JButton();
	private final JButton m_oExportButton = new JButton();
	private final JButton m_oStatsButton = new JButton();
	private final JButton m_oManagePlaceButton = new JButton();
	private final JButton m_oWorksheetButton = new JButton();
	private final JButton m_oCreerButton = new JButton();
	private final JButton m_oImporterButton = new JButton();
	private final JButton m_oModifierButton = new JButton();
	private final JButton m_oShowFileButton = new JButton();
	private final JButton m_oShowTrashButton = new JButton();
	private final JButton m_oCutButton = new JButton();
	private final JButton m_oCopyButton = new JButton();
	private final JButton m_oPasteButton = new JButton();

	private final JButton buttonSave = new JButton();
	private final JButton buttonPdf = new JButton();
	private final JButton newButton = new JButton();
	private final JButton openButton = new JButton();

	private final MyCellarLabel update = new MyCellarLabel("");
	private final MyCellarLabel version = new MyCellarLabel("");

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

	// differents menus
	private final JMenu menuFile = new JMenu();
	private final JMenu menuPlace = new JMenu();
	private final JMenu menuEdition = new JMenu();
	private final JMenu menuWine = new JMenu();
	private final JMenu menuAbout = new JMenu("?");
	final JMenu menuTools = new JMenu();

	// differents choix de chaque menu
	private final JMenuItem importation = new JMenuItem();
	private final JMenuItem quit = new JMenuItem();
	private final JMenuItem exportation = new JMenuItem();
	private final JMenuItem statistiques = new JMenuItem();
	private final JMenuItem tableau = new JMenuItem();
	private final JMenuItem addPlace = new JMenuItem();
	private final MyCellarMenuItem modifPlace = new MyCellarMenuItem(new ModifyPlaceAction());
	private final MyCellarMenuItem delPlace = new MyCellarMenuItem(new DeletePlaceAction());
	private final JMenuItem showFile = new JMenuItem();
	private final JMenuItem showWorksheet = new JMenuItem();
	private final JMenuItem addWine = new JMenuItem();
	private final JMenuItem searchWine = new JMenuItem();
	private final JMenuItem Aide = new JMenuItem();
	private final MyCellarMenuItem parameter = new MyCellarMenuItem(new ParametersAction());
	private final JMenuItem about = new JMenuItem();
	private final JMenuItem tocreate = new JMenuItem();
	private final JMenuItem news = new JMenuItem();
	private final MyCellarMenuItem history = new MyCellarMenuItem(new ShowHistoryAction());
	private final JMenuItem vignobles = new JMenuItem();
	private final JMenuItem bottleCapacity = new JMenuItem();
	private final JMenuItem newFile = new JMenuItem();
	private final JMenuItem save = new JMenuItem();
	private final JMenuItem saveAs = new JMenuItem();
	private final JMenuItem jMenuImportXmlPlaces = new JMenuItem();
	private final JMenuItem jMenuExportXmlPlaces = new JMenuItem();
	private final JMenuItem jMenuExportXml = new JMenuItem();
	private final JMenuItem openFile = new JMenuItem();
	private final JMenuItem jMenuCloseFile = new JMenuItem();
	private final MyCellarMenuItem jMenuSetConfig = new MyCellarMenuItem(LabelType.INFO, "374", LabelProperty.SINGLE.withThreeDashes());
	private final JMenuItem jMenuReopen1 = new JMenuItem();
	private final JMenuItem jMenuReopen2 = new JMenuItem();
	private final JMenuItem jMenuReopen3 = new JMenuItem();
	private final JMenuItem jMenuReopen4 = new JMenuItem();
	private final JMenuItem jMenuCheckUpdate = new JMenuItem();
	private final JMenuItem jMenuCut = new JMenuItem();
	private final JMenuItem jMenuCopy = new JMenuItem();
	private final JMenuItem jMenuPaste = new JMenuItem();
	static final long serialVersionUID = 501073;
	private boolean m_bHasListener = false;
	private boolean m_bHasFrameBuilded = false;

	private Preferences prefs;

	private static final Start INSTANCE = new Start();

	private Start() {}

	public static void main(String[] args) {
		try {
			final SplashScreen splashscreen = new SplashScreen();
			// initialisation
			Program.start();
			// Lecture des parametres
			// ______________________

			String parameters = "";

			for (String arg : args) {
				parameters = parameters.concat(arg + " ");
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
					if (tmp.contains(Program.EXTENSION)) {
						Program.setNewFile(tmp.strip());
					} else {
						// On prend tous ce qu'il y a apres -opts
						tmp = parameters.substring(nIndex);
						if (tmp.contains(Program.EXTENSION)) {
							// Si l'on trouve l'extension du fichier
							// on cherche le caractere ' ' qui va separer les
							// options du nom du fichier
							String tmp2 = tmp.strip();
							tmp2 = tmp2.substring(tmp2.indexOf(" "));
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
						Server.getInstance().downloadVersion();
						System.exit(3);
					}
				}
			}

			Thread.setDefaultUncaughtExceptionHandler((t, e) -> Program.showException(e, true));

			while (splashscreen.isRunning()) {}

			SwingUtilities.invokeLater(() -> getInstance().startup());
		} catch (Exception e) {
			Program.showException(e);
			System.exit(998);
		} catch (ExceptionInInitializerError a) {
			JOptionPane.showMessageDialog(null, "Error during program initialisation!!\nProgram files corrupted!!\nPlease reinstall program.",
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(999);
		}
	}

	public static Start getInstance() {
		return INSTANCE;
	}

	/**
	 * Fonction d'initialisation de l'application
	 */
	private void startup() {
		Thread.currentThread().setUncaughtExceptionHandler(this);
		prefs = Preferences.userNodeForPackage(getClass());

		// Controle des MAJ
		// Appel serveur pour alimenter la derniere version en ligne
		Server.getInstance().getServerVersion();

		// Demarrage
		// _________

		if (!Program.hasFile() && !Program.getGlobalConfigBool(MyCellarSettings.STARTUP, false)) {
			// Language au premier demarrage
			String lang = System.getProperty("user.language");
			if(Program.FR.equalsIgnoreCase(lang)) {
				lang = "F";
			} else {
				lang = "U";
			}
			Program.putGlobalConfigString(MyCellarSettings.LANGUAGE, lang);

			updateFrame(true);
			Program.putGlobalConfigBool(MyCellarSettings.STARTUP, true);
		}

		if (Program.hasFile()) {
			loadFile();
		} else {
			updateFrame(false);
			afficheFrame();
			enableAll(false);
		}
		setVisible(true);
	}

	/**
	 * Permet de charger un fichier sans avoir a recharger la Frame
	 */
	private void loadFile() {
		updateFrame(true);

		// Contruction de la Frame
		Debug("Showing Frame");
		afficheFrame();

		if (!Program.hasFile()) {
			Debug("ERROR: Unable to Load Empty File: Use Load command");
			m_oModifierButton.setEnabled(false);
			m_oImporterButton.setEnabled(false);
			m_oShowFileButton.setEnabled(false);
			m_oShowTrashButton.setEnabled(false);
			importation.setEnabled(false);
			m_oCreerButton.setEnabled(false);
			save.setEnabled(false);
			buttonSave.setEnabled(false);
			buttonPdf.setEnabled(false);
			saveAs.setEnabled(false);
			addPlace.setEnabled(false);
			jMenuExportXmlPlaces.setEnabled(false);
			jMenuImportXmlPlaces.setEnabled(false);
			jMenuExportXml.setEnabled(false);
			showFile.setEnabled(false);
			showWorksheet.setEnabled(false);
		} else if (Program.getCaveLength() == 0) {
			Program.addCave(Program.DEFAULT_PLACE);
		}
		enableAll(true);
	}

	/**
	 * Fonction pour desactiver ou activer toutes les options ou boutons
	 */
	public void enableAll(boolean enable) {
		jMenuCloseFile.setEnabled(enable);
		m_oExportButton.setEnabled(enable);
		m_oStatsButton.setEnabled(enable);
		m_oManagePlaceButton.setEnabled(enable && Program.getCave().stream().anyMatch(Predicate.not(Rangement::isCaisse)));
		m_oWorksheetButton.setEnabled(enable);
		m_oTableauxButton.setEnabled(enable);
		m_oSupprimerButton.setEnabled(enable);
		m_oAjouterButton.setEnabled(enable);
		m_oRechercherButton.setEnabled(enable);
		exportation.setEnabled(enable);
		statistiques.setEnabled(enable);
		tableau.setEnabled(enable);
		addWine.setEnabled(enable);
		modifPlace.setEnabled(enable);
		delPlace.setEnabled(enable);
		searchWine.setEnabled(enable);
		m_oModifierButton.setEnabled(enable);
		m_oImporterButton.setEnabled(enable);
		m_oShowFileButton.setEnabled(enable);
		m_oShowTrashButton.setEnabled(enable);
		importation.setEnabled(enable);
		m_oCreerButton.setEnabled(enable);
		save.setEnabled(Program.isFileSavable());
		buttonSave.setEnabled(Program.isFileSavable());
		buttonPdf.setEnabled(enable);
		saveAs.setEnabled(enable);
		addPlace.setEnabled(enable);
		jMenuExportXmlPlaces.setEnabled(enable);
		jMenuImportXmlPlaces.setEnabled(enable);
		jMenuExportXml.setEnabled(enable);
		showFile.setEnabled(enable);
		showWorksheet.setEnabled(enable);
		tocreate.setEnabled(enable);
		history.setEnabled(enable);
		vignobles.setEnabled(enable);
		bottleCapacity.setEnabled(enable);
		parameter.setEnabled(enable);
		jMenuCut.setEnabled(enable);
		jMenuCopy.setEnabled(enable);
		jMenuPaste.setEnabled(enable);
		m_oCutButton.setEnabled(enable);
		m_oCopyButton.setEnabled(enable);
		m_oPasteButton.setEnabled(enable);
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

	/**
	 * about_actionPerformed: Appelle la fenetre d'A Propos.
	 */
	private void about_actionPerformed() {
		new APropos().setVisible(true);
	}

	/**
	 * news_actionPerformed: Affiche les nouveautes de la release
	 */
	private void news_actionPerformed() {
		Program.open(new File("Finish.html"));
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
			int index = fic.indexOf(".");
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
	 * Actions realises apres l'ouverture d'un fichier
	 */
	private void postOpenFile() {
		loadFile();
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
	 * exportXml_actionPerformed: Permet d'exporter la liste des vins au format
	 * xml
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

	/**
	 * updateFrame: Met a jour tous les champs avec la langue selectionnee. Met
	 * a jour tous les parametres suite au chargement d'un fichier
	 *
	 * @param toverify boolean
	 */
	private void updateFrame(boolean toverify) {
		Debug("UpdateFrame: toVerify=" + toverify);
		try {
			boolean bHasVersion = false;
			if ((null != Program.getCaveConfig() && Program.hasConfigCaveKey(MyCellarSettings.VERSION))
					|| Program.hasConfigGlobalKey(MyCellarSettings.VERSION)) {
				bHasVersion = true;
			}

			if (!bHasVersion || toverify) {
				Program.initConf();
			}
			String thelangue = Program.getGlobalConfigString(MyCellarSettings.LANGUAGE, "F");
			Program.setProgramType(Program.Type.valueOf(Program.getCaveConfigString(PROGRAM_TYPE, Program.getGlobalConfigString(PROGRAM_TYPE, Program.Type.WINE.name()))));
			Program.setLanguage(LanguageFileLoader.getLanguage(thelangue.charAt(0)));
			updateLabels();
			Debug("UpdateFrame: Loading Frame ended");
		} catch (RuntimeException e) {
			Program.showException(e);
		}
	}

	void updateLabels() {
		final String quitter = Program.getLabel("QUITTER");
		if (quitter == null || quitter.isEmpty()) {
			Program.setLanguage(LanguageFileLoader.Language.FRENCH);
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

		MyCellarLabelManagement.updateLabels();
		// differents menus
		menuFile.setText(Program.getLabel("Infos104")); // Fichier
		menuPlace.setText(Program.getLabel("Infos081"));
		menuWine.setText(Program.getLabel("Main.Item", LabelProperty.SINGLE.withCapital())); // Vin
		menuTools.setText(Program.getLabel("Infos246"));
		menuEdition.setText(Program.getLabel("Infos245"));

		// differents choix de chaque menu
		importation.setText(Program.getLabel("Infos107")); // Import...
		quit.setText(Program.getLabel("Infos003")); // Quitter
		exportation.setText(Program.getLabel("Infos108")); // Export...
		statistiques.setText(Program.getLabel("Infos009")); // Statistiques
		tableau.setText(Program.getLabel("Infos093")); // Tableaux...
		addPlace.setText(Program.getLabel("Infos109")); // Ajouter...
		addWine.setText(Program.getLabel("Infos109")); // Ajouter...
		Aide.setText(Program.getLabel("Infos111")); // Aide Contextuelle...
		saveAs.setText(Program.getLabel("Infos371")); // Sauvegarder
		newFile.setText(Program.getLabel("Infos378"));
		openFile.setText(Program.getLabel("Infos372"));
		save.setText(Program.getLabel("Infos326"));
		showFile.setText(Program.getLabel("Infos324"));
		showWorksheet.setText(Program.getLabel("ShowFile.Worksheet"));
		searchWine.setText(Program.getLabel("Main.tabSearchButton"));

		about.setText(Program.getLabel("Infos199")); // A Propos
		news.setText(Program.getLabel("Infos330")); // Nouveautes
		tocreate.setText(Program.getLabel("Infos267")); // Rangement a creer
		vignobles.setText(Program.getLabel("Infos165") + "..."); // Vignobles
		bottleCapacity.setText(Program.getLabel("Infos400") + "..."); // Contenance
		jMenuImportXmlPlaces.setText(Program.getLabel("Infos367")); // Importer des rangements xml
		jMenuExportXmlPlaces.setText(Program.getLabel("Infos368")); // Exporter des rangements xml
		jMenuExportXml.setText(Program.getLabel("Infos408")); // Exporter au format xml
		jMenuCloseFile.setText(Program.getLabel("Infos019")); // Fermer...
		jMenuCheckUpdate.setText(Program.getLabel("Infos379")); // Verifier mise a jour...
		jMenuReopen1.setText("1 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, "")) + Program.EXTENSION);
		jMenuReopen2.setText("2 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, "")) + Program.EXTENSION);
		jMenuReopen3.setText("3 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, "")) + Program.EXTENSION);
		jMenuReopen4.setText("4 - " + Program.getShortFilename(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, "")) + Program.EXTENSION);
		jMenuReopen1.setAccelerator(KeyStroke.getKeyStroke('1', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen2.setAccelerator(KeyStroke.getKeyStroke('2', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen3.setAccelerator(KeyStroke.getKeyStroke('3', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen4.setAccelerator(KeyStroke.getKeyStroke('4', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen1.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, ""));
		jMenuReopen2.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, ""));
		jMenuReopen3.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, ""));
		jMenuReopen4.setToolTipText(Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, ""));

		jMenuCut.setText(Program.getLabel("Infos241"));
		jMenuCopy.setText(Program.getLabel("Infos242"));
		jMenuPaste.setText(Program.getLabel("Infos243"));

		m_oImporterButton.setText(Program.getLabel("Infos011")); // Importer
		m_oExportButton.setText(Program.getLabel("Infos125"));
		m_oCreerButton.setText(Program.getLabel("Infos010"));
		m_oStatsButton.setText(Program.getLabel("Infos009"));
		m_oManagePlaceButton.setText(Program.getLabel("Main.ManagePlace"));
		m_oWorksheetButton.setText(Program.getLabel("ShowFile.Worksheet"));
		m_oModifierButton.setText(Program.getLabel("Infos007"));
		m_oShowFileButton.setText(Program.getLabel("Infos324"));
		m_oTableauxButton.setText(Program.getLabel("Infos008"));
		m_oAjouterButton.setText(Program.getLabel("Main.tabAdd", LabelProperty.SINGLE));
		m_oRechercherButton.setText(Program.getLabel("Main.tabSearchButton"));
		m_oSupprimerButton.setText(Program.getLabel("Infos004"));
		version.setText(Program.getLabel("MonthVersion") + Program.INFOS_VERSION + MyCellarVersion.MAIN_VERSION);
		addWine.setAccelerator(KeyStroke.getKeyStroke(addWineChar, InputEvent.CTRL_DOWN_MASK));
		addPlace.setAccelerator(KeyStroke.getKeyStroke(addPlaceChar, InputEvent.CTRL_DOWN_MASK));
		delPlace.setAccelerator(KeyStroke.getKeyStroke(deleteChar, InputEvent.CTRL_DOWN_MASK));
		history.setAccelerator(KeyStroke.getKeyStroke(historyChar, InputEvent.CTRL_DOWN_MASK));
		tableau.setAccelerator(KeyStroke.getKeyStroke(tableChar, InputEvent.CTRL_DOWN_MASK));
		statistiques.setAccelerator(KeyStroke.getKeyStroke(statChar, InputEvent.CTRL_DOWN_MASK));
		importation.setAccelerator(KeyStroke.getKeyStroke(importChar, InputEvent.CTRL_DOWN_MASK));
		exportation.setAccelerator(KeyStroke.getKeyStroke(exportChar, InputEvent.CTRL_DOWN_MASK));
		modifPlace.setAccelerator(KeyStroke.getKeyStroke(modifyChar, InputEvent.CTRL_DOWN_MASK));
		quit.setAccelerator(KeyStroke.getKeyStroke(this.quitChar, InputEvent.CTRL_DOWN_MASK));
		SwingUtilities.updateComponentTreeUI(this);
		String tmp = Program.getShortFilename();
		Program.DEFAULT_PLACE.setNom(Program.getLabel("Program.DefaultPlace"));
		if (tmp.isEmpty()) {
			setTitle(Program.getLabel("Infos001"));
		} else {
			setTitle(Program.getLabel("Infos001") + " - [" + tmp + "]");
		}
	}

	/**
	 * aide_actionPerformed: Aide
	 */
	private void aide_actionPerformed() {
		Program.getAide();
	}

	/**
	 * afficheFrame: Affiche la fenetre principale
	 */
	private void afficheFrame() {
		if (m_bHasFrameBuilded) {
			// On ne recontruit que le menu Fichier pour remettre a jour la
			// liste des fichiers ouverts recement
			menuFile.removeAll();
			menuFile.add(newFile);
			menuFile.add(openFile);
			menuFile.add(jMenuCloseFile);
			menuFile.addSeparator();
			menuFile.add(save);
			menuFile.add(saveAs);
			menuFile.addSeparator();
			menuFile.add(importation);
			menuFile.add(exportation);
			menuFile.addSeparator();
			menuFile.add(statistiques);
			menuFile.add(tableau);
			menuFile.add(showFile);
			if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, "").isEmpty()) {
				menuFile.addSeparator();
				menuFile.add(jMenuReopen1);
			}
			if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, "").isEmpty()) {
				menuFile.add(jMenuReopen2);
			}
			if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, "").isEmpty()) {
				menuFile.add(jMenuReopen3);
			}
			if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, "").isEmpty()) {
				menuFile.add(jMenuReopen4);
			}
			menuFile.addSeparator();
			menuFile.add(quit);
			return;
		}

		NewAction newAction = new NewAction();
		OpenAction openAction = new OpenAction();
		SaveAction saveAction = new SaveAction();
		SaveAsAction saveAsAction = new SaveAsAction();
		SearchAction searchAction = new SearchAction();
		CutAction cutAction = new CutAction();
		CopyAction copyAction = new CopyAction();
		PasteAction pasteAction = new PasteAction();
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

		m_oAjouterButton.setAction(addWineAction);
		addWine.setAction(addWineAction);
		newButton.setAction(newAction);
		newButton.setText("");
		openButton.setAction(openAction);
		openButton.setText("");
		buttonSave.setAction(saveAction);
		buttonSave.setText("");
		buttonPdf.setAction(new ExportPDFAction());
		buttonPdf.setText("");
		m_oCutButton.setAction(cutAction);
		m_oCopyButton.setAction(copyAction);
		m_oPasteButton.setAction(pasteAction);
		m_oCutButton.setText("");
		m_oCopyButton.setText("");
		m_oPasteButton.setText("");
		m_oRechercherButton.setAction(searchAction);
		m_oCreerButton.setAction(addPlaceAction);
		m_oModifierButton.setAction(new ModifyPlaceAction());
		m_oSupprimerButton.setAction(new DeletePlaceAction());
		m_oShowFileButton.setAction(showFileAction);
		m_oTableauxButton.setAction(createTabAction);
		m_oStatsButton.setAction(statAction);
		m_oImporterButton.setAction(importFileAction);
		m_oExportButton.setAction(exportFileAction);
		m_oManagePlaceButton.setAction(managePlaceAction);
		m_oWorksheetButton.setAction(new OpenWorkSheetAction());
		m_oShowTrashButton.setAction(showTrashAction);
		addPlace.setAction(addPlaceAction);
		addWine.setAction(addWineAction);
		searchWine.setAction(searchAction);
		newFile.setAction(newAction);
		openFile.setAction(openAction);
		save.setAction(saveAction);
		saveAs.setAction(saveAsAction);
		importation.setAction(importFileAction);
		exportation.setAction(exportFileAction);
		statistiques.setAction(statAction);
		tableau.setAction(createTabAction);
		showFile.setAction(showFileAction);
		jMenuCut.setAction(cutAction);
		jMenuCopy.setAction(copyAction);
		jMenuPaste.setAction(pasteAction);
		showWorksheet.setAction(new OpenWorkSheetAction());

		JToolBar toolBar = new JToolBar();
		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(buttonSave);
		toolBar.add(buttonPdf);
		toolBar.addSeparator();
		toolBar.add(m_oCutButton);
		toolBar.add(m_oCopyButton);
		toolBar.add(m_oPasteButton);
		toolBar.addSeparator();
		toolBar.add(m_oAjouterButton);
		toolBar.add(m_oRechercherButton);
		toolBar.add(m_oShowFileButton);
		toolBar.add(m_oTableauxButton);
		toolBar.add(m_oStatsButton);
		toolBar.add(m_oExportButton);
		toolBar.add(m_oManagePlaceButton);
		toolBar.add(m_oWorksheetButton);
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(m_oShowTrashButton);
		toolBar.setFloatable(true);
		add(toolBar, BorderLayout.NORTH);

		if (MyCellarImage.ICON != null) {
			setIconImage(MyCellarImage.ICON.getImage());
		}

		boolean bUpdateAvailable = Server.getInstance().hasAvailableUpdate();

		// Ajout du Menu
		Aide.setAccelerator(KeyStroke.getKeyStroke("F1"));

		// Ajouter les choix au menu
		menuFile.removeAll();
		menuPlace.removeAll();
		menuEdition.removeAll();
		menuWine.removeAll();
		menuAbout.removeAll();
		menuTools.removeAll();
		menuFile.add(newFile);
		menuFile.add(openFile);
		menuFile.add(jMenuCloseFile);
		menuFile.addSeparator();
		menuFile.add(save);
		menuFile.add(saveAs);
		menuFile.addSeparator();
		menuFile.add(importation);
		menuFile.add(exportation);
		menuFile.addSeparator();
		menuFile.add(statistiques);
		menuFile.add(tableau);
		menuFile.add(showFile);
		if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN1, "").isEmpty()) {
			menuFile.addSeparator();
			menuFile.add(jMenuReopen1);
		}
		if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN2, "").isEmpty()) {
			menuFile.add(jMenuReopen2);
		}
		if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN3, "").isEmpty()) {
			menuFile.add(jMenuReopen3);
		}
		if (!Program.getGlobalConfigString(MyCellarSettings.LAST_OPEN4, "").isEmpty()) {
			menuFile.add(jMenuReopen4);
		}
		menuFile.addSeparator();
		menuFile.add(quit);
		menuPlace.add(addPlace);
		menuPlace.add(modifPlace);
		menuPlace.add(delPlace);
		menuWine.add(addWine);
		menuWine.add(searchWine);
		menuEdition.add(jMenuCut);
		menuEdition.add(jMenuCopy);
		menuEdition.add(jMenuPaste);
		menuAbout.add(Aide);
		menuAbout.addSeparator();
		menuAbout.add(jMenuCheckUpdate);
		menuAbout.addSeparator();
		menuAbout.add(news);
		menuTools.add(parameter);
		menuTools.add(showWorksheet);
		menuTools.add(vignobles);
		menuTools.add(bottleCapacity);
		menuTools.add(history);
		menuTools.add(tocreate);
		menuTools.add(jMenuImportXmlPlaces);
		menuTools.add(jMenuExportXmlPlaces);
		menuTools.add(jMenuExportXml);
		menuTools.add(jMenuSetConfig);
		menuAbout.add(about);
		addWine.setAccelerator(KeyStroke.getKeyStroke(addWineChar, InputEvent.CTRL_DOWN_MASK));
		addPlace.setAccelerator(KeyStroke.getKeyStroke(addPlaceChar, InputEvent.CTRL_DOWN_MASK));
		delPlace.setAccelerator(KeyStroke.getKeyStroke(deleteChar, InputEvent.CTRL_DOWN_MASK));
		showFile.setAccelerator(KeyStroke.getKeyStroke(viewChar, InputEvent.CTRL_DOWN_MASK));
		history.setAccelerator(KeyStroke.getKeyStroke(historyChar, InputEvent.CTRL_DOWN_MASK));
		searchWine.setAccelerator(KeyStroke.getKeyStroke(searchChar, InputEvent.CTRL_DOWN_MASK));
		tableau.setAccelerator(KeyStroke.getKeyStroke(tableChar, InputEvent.CTRL_DOWN_MASK));
		statistiques.setAccelerator(KeyStroke.getKeyStroke(statChar, InputEvent.CTRL_DOWN_MASK));
		importation.setAccelerator(KeyStroke.getKeyStroke(importChar, InputEvent.CTRL_DOWN_MASK));
		exportation.setAccelerator(KeyStroke.getKeyStroke(exportChar, InputEvent.CTRL_DOWN_MASK));
		modifPlace.setAccelerator(KeyStroke.getKeyStroke(modifyChar, InputEvent.CTRL_DOWN_MASK));
		quit.setAccelerator(KeyStroke.getKeyStroke(quitChar, InputEvent.CTRL_DOWN_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(saveChar, InputEvent.CTRL_DOWN_MASK));
		newFile.setAccelerator(KeyStroke.getKeyStroke(newChar, InputEvent.CTRL_DOWN_MASK));
		openFile.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		jMenuCut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
		jMenuCopy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
		jMenuPaste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
		// Ajouter les menu sur la bar de menu
		m_oMenuBar.add(menuFile);
		m_oMenuBar.add(menuEdition);
		m_oMenuBar.add(menuPlace);
		m_oMenuBar.add(menuWine);
		m_oMenuBar.add(menuTools);
		m_oMenuBar.add(menuAbout);
		// Ajouter la bar du menu a la frame
		setJMenuBar(m_oMenuBar);

		// Chargement du Frame
		if (!m_bHasListener) {
			setListeners();
		}

		update.setVisible(bUpdateAvailable);
		if (bUpdateAvailable) {
			update.setText(MessageFormat.format(Program.getLabel("Infos385"), Server.getInstance().getAvailableVersion(), MyCellarVersion.MAIN_VERSION + "-" + Program.INTERNAL_VERSION));
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(() -> update.setVisible(false));
						}
					},
					30000
			);
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
			ProgramPanels.TABBED_PANE.getSelectedComponent();
		});

		quit.addActionListener((e) -> quitter_actionPerformed());
		about.addActionListener((e) -> about_actionPerformed());
		news.addActionListener((e) -> news_actionPerformed());
		vignobles.setAction(new VignoblesAction());
		bottleCapacity.setAction(new CapacityAction());

		jMenuImportXmlPlaces.addActionListener((e) -> importXmlPlace_actionPerformed());
		jMenuExportXmlPlaces.addActionListener((e) -> exportXmlPlace_actionPerformed());
		jMenuExportXml.addActionListener((e) -> exportXml_actionPerformed());
		jMenuCloseFile.addActionListener((e) -> closeFile_actionPerformed());
		tocreate.addActionListener((e) -> RangementUtils.findRangementToCreate());
		jMenuReopen1.addActionListener((e) -> reopen1_actionPerformed());
		jMenuReopen2.addActionListener((e) -> reopen2_actionPerformed());
		jMenuReopen3.addActionListener((e) -> reopen3_actionPerformed());
		jMenuReopen4.addActionListener((e) -> reopen4_actionPerformed());
		Aide.addActionListener((e) -> aide_actionPerformed());
		jMenuSetConfig.addActionListener((e) -> menuSetConfig_actionPerformed());
		jMenuCheckUpdate.addActionListener((e) -> menuCheckUpdate_actionPerformed());

		m_bHasListener = true;
	}

	private static void Debug(String sText) {
		Program.Debug("Start: " + sText);
	}

	public void updateMainPanel() {
		int count = ProgramPanels.TABBED_PANE.getTabCount();
		ProgramPanels.PANEL_INFOS.setVisible(count == 0);
		ProgramPanels.TABBED_PANE.setVisible(count > 0);
		if (count == 0) {
			ProgramPanels.PANEL_INFOS.refresh();
		}
		boolean foundArmoire = Program.getCave().stream().anyMatch(Predicate.not(Rangement::isCaisse));
		m_oManagePlaceButton.setEnabled(foundArmoire);
	}

	/**
	 * menuSaveAs_actionPerformed: Sauvegarde sous
	 */
	private void menuSaveAs_actionPerformed() {
		try {
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
		} catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * menuSetConfig_actionPerformed: Modification des parametres internes
	 */
	private void menuSetConfig_actionPerformed() {
		try {
			List<String> type_objet = Collections.singletonList("JTextField");
			String titre = Program.getLabel("Infos374");
			String message1 = Program.getLabel("Infos375");
			MyOptions myoptions = new MyOptions(titre, message1, type_objet, "",
					Program.getCaveConfig(), true, true);
			myoptions.setVisible(true);
		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * menuCheckUpdate_actionPerformed: Recherche de mises a jour
	 */
	private void menuCheckUpdate_actionPerformed() {
		if (Server.getInstance().hasAvailableUpdate()) {
			Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Infos384"), Server.getInstance().getAvailableVersion(), Program.INTERNAL_VERSION), true);
		} else {
			Erreur.showSimpleErreur(Program.getLabel("Infos388"), true);
		}
	}

	void removeCurrentTab() {
		ProgramPanels.TABBED_PANE.removeTabAt(ProgramPanels.TABBED_PANE.getSelectedIndex());
		updateMainPanel();
	}

	public static void setPaneModified(boolean modify) {
		if (ProgramPanels.TABBED_PANE.getSelectedComponent() != null) {
			int index = ProgramPanels.TABBED_PANE.getSelectedIndex();
			String title = ProgramPanels.TABBED_PANE.getTitleAt(index);
			if (modify) {
				if (!title.endsWith("*")) {
					ProgramPanels.TABBED_PANE.setTitleAt(index, title + "*");
				}
			} else {
				if (title.endsWith("*")) {
					title = title.substring(0, title.length() - 1);
				}
				ProgramPanels.TABBED_PANE.setTitleAt(index, title);
			}
		}
	}

	final class OpenAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private OpenAction() {
			super(Program.getLabel("Infos372"), MyCellarImage.OPEN);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos372"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR, ""));
				boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
				boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
				int retour_jfc = boiteFichier.showOpenDialog(null);
				if (retour_jfc == JFileChooser.APPROVE_OPTION) {
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
			}	finally {
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	final class NewAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private NewAction() {
			super(Program.getLabel("Infos378"), MyCellarImage.NEW);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos378"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Debug("newFileAction: Creating a new file...");
			PanelObjectType panelObjectType = new PanelObjectType();
			if (JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(getInstance(), panelObjectType,
					"",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)) {
				return;
			}
			Program.putGlobalConfigString(PROGRAM_TYPE, panelObjectType.getSelectedType().name());
			Program.newFile();
			postOpenFile();
			Debug("newFileAction: Creating a new file OK");
		}
	}

	final class SaveAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private SaveAction() {
			super(Program.getLabel("Infos326"), MyCellarImage.SAVE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos326"));
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
						if (ProgramPanels.TABBED_PANE.getTitleAt(i).endsWith("*")) {
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
			} catch (RuntimeException e) {
				Program.showException(e);
			} finally {
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	final class SaveAsAction extends AbstractAction {
		private static final long serialVersionUID = -2340786091568284033L;

		private SaveAsAction() {
			super(Program.getLabel("Infos371"), MyCellarImage.SAVEAS);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos371"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			menuSaveAs_actionPerformed();
		}
	}

	final class AddWineAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private AddWineAction() {
			super(Program.getLabel("Main.tabAdd", LabelProperty.SINGLE), MyCellarImage.WINE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.tabAdd", LabelProperty.SINGLE));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(addPlaceChar, InputEvent.CTRL_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getAddVin() == null) {
				try {
					final AddVin addVin = ProgramPanels.createAddVin();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.tabAdd", LabelProperty.SINGLE), addVin);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.WINE);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, addVin);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getAddVin());
			} catch (IllegalArgumentException e) {
				final AddVin addVin = ProgramPanels.getAddVin();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.tabAdd", LabelProperty.SINGLE), addVin);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.WINE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, addVin);
				ProgramPanels.TABBED_PANE.setSelectedComponent(addVin);
			}
			ProgramPanels.getAddVin().reInit();
			updateMainPanel();
		}
	}

	final class AddPlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private AddPlaceAction() {
			super(Program.getLabel("Infos109"), MyCellarImage.PLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos010"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getCreerRangement() == null) {
				try {
					final Creer_Rangement creerRangement = ProgramPanels.createCreerRangement();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos010"), creerRangement);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, creerRangement);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getCreerRangement());
			} catch (IllegalArgumentException e) {
				final Creer_Rangement creerRangement = ProgramPanels.createCreerRangement();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos010"), creerRangement);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, creerRangement);
				ProgramPanels.TABBED_PANE.setSelectedComponent(creerRangement);
			}
			updateMainPanel();
		}
	}

	final class DeletePlaceAction extends MyCellarAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private DeletePlaceAction() {
			super(LabelType.INFO_OTHER, "Main.Delete", LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.DELPLACE);
			setDescriptionLabelCode("Infos004");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getSupprimerRangement() == null) {
				try {
					final Supprimer_Rangement supprimerRangement = ProgramPanels.createSupprimerRangement();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos004"), supprimerRangement);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.DELPLACE);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, supprimerRangement);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getSupprimerRangement());
			} catch (IllegalArgumentException e) {
				final Supprimer_Rangement supprimerRangement = ProgramPanels.createSupprimerRangement();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos004"), supprimerRangement);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.DELPLACE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, supprimerRangement);
				ProgramPanels.TABBED_PANE.setSelectedComponent(supprimerRangement);
			}
			updateMainPanel();
		}
	}

	final class ModifyPlaceAction extends MyCellarAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ModifyPlaceAction() {
			super(LabelType.INFO, "079", LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.MODIFYPLACE);
			setDescriptionLabelCode("Infos007");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getModifierRangement() == null) {
				try {
					final Creer_Rangement modifierRangement = ProgramPanels.createModifierRangement();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos007"), modifierRangement);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, modifierRangement);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			ProgramPanels.getModifierRangement().updateView();
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getModifierRangement());
			} catch (IllegalArgumentException e) {
				final Creer_Rangement modifierRangement = ProgramPanels.createModifierRangement();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos007"), modifierRangement);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, modifierRangement);
				ProgramPanels.TABBED_PANE.setSelectedComponent(modifierRangement);
			}
			updateMainPanel();
		}
	}

	final class SearchAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private SearchAction() {
			super(Program.getLabel("Main.tabSearchButton", LabelProperty.SINGLE), MyCellarImage.SEARCH);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.tabSearch", LabelProperty.SINGLE));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getSearch().isEmpty()) {
				final Search search = ProgramPanels.createSearch();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.tabSearchSimple"), search);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, search);
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getSearch().get());
			} catch (IllegalArgumentException e) {
				final Search search = ProgramPanels.createSearch();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.tabSearchSimple"), search);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, search);
				ProgramPanels.TABBED_PANE.setSelectedComponent(search);
			}
			updateMainPanel();
		}
	}

	final class CreateTabAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private CreateTabAction() {
			super(Program.getLabel("Infos093"), MyCellarImage.TABLE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos008"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getCreerTableaux() == null) {
				final Creer_Tableaux creerTableaux = ProgramPanels.createCreerTableaux();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos008"), creerTableaux);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, creerTableaux);
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getCreerTableaux());
			} catch (IllegalArgumentException e) {
				final Creer_Tableaux creerTableaux = ProgramPanels.createCreerTableaux();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos008"), creerTableaux);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, creerTableaux);
				ProgramPanels.TABBED_PANE.setSelectedComponent(creerTableaux);
			}
			updateMainPanel();
		}
	}

	final class ImportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ImportFileAction() {
			super(Program.getLabel("Infos107"), MyCellarImage.IMPORT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos011"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (ProgramPanels.getImporter() == null) {
				final Importer importer = ProgramPanels.createImporter();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos011"), importer);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, importer);
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getImporter());
			} catch (IllegalArgumentException e) {
				final Importer importer = ProgramPanels.createImporter();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos011"), importer);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, importer);
				ProgramPanels.TABBED_PANE.setSelectedComponent(importer);
			}
			updateMainPanel();
		}
	}

	final class ExportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ExportFileAction() {
			super(Program.getLabel("Infos108"), MyCellarImage.EXPORT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos125"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getExport() == null) {
				try {
					final Export export = ProgramPanels.createExport();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos148"), export);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.EXPORT);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, export);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getExport());
			} catch (IllegalArgumentException e) {
				final Export export = ProgramPanels.createExport();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos148"), export);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.EXPORT);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, export);
				ProgramPanels.TABBED_PANE.setSelectedComponent(export);
			}
			updateMainPanel();
		}
	}

	final class StatAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private StatAction() {
			super(Program.getLabel("Infos009"), MyCellarImage.STATS);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos009"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getStat() == null) {
				try {
					final Stat stat = ProgramPanels.createStat();
					stat.setVisible(true);
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos009"), stat);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.STATS);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, stat);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getStat());
			} catch (IllegalArgumentException e) {
				final Stat stat = ProgramPanels.createStat();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos009"), stat);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.STATS);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, stat);
				ProgramPanels.TABBED_PANE.setSelectedComponent(stat);
			}
			ProgramPanels.getStat().updateView();
			updateMainPanel();
		}
	}

	class ShowHistoryAction extends MyCellarAction {
		private static final long serialVersionUID = -2981766233846291757L;

		private ShowHistoryAction() {
			super(LabelType.INFO, "341", LabelProperty.SINGLE.withThreeDashes());
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getShowHistory() == null) {
				try {
					final ShowHistory showHistory = ProgramPanels.createShowHistory();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos341"), showHistory);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, null);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, showHistory);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getShowHistory());
			} catch (IllegalArgumentException e) {
				final ShowHistory showHistory = ProgramPanels.createShowHistory();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos341"), showHistory);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, null);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, showHistory);
				ProgramPanels.TABBED_PANE.setSelectedComponent(showHistory);
			}
			ProgramPanels.getShowHistory().refresh();
			updateMainPanel();
		}
	}

	class VignoblesAction extends AbstractAction {

		private static final long serialVersionUID = -7956676252030557402L;

		private VignoblesAction() {
			super(Program.getLabel("Infos165") + "...");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			openVineyardPanel();
		}
	}

	public void openVineyardPanel() {
		if (ProgramPanels.getVineyardPanel() == null) {
			try {
				final VineyardPanel vineyardPanel = ProgramPanels.createVineyardPanel();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos165"), vineyardPanel);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, null);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, vineyardPanel);
			} catch (RuntimeException e) {
				Program.showException(e);
			}
		}
		try {
			ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getVineyardPanel());
		} catch (IllegalArgumentException e) {
			final VineyardPanel vineyardPanel = ProgramPanels.createVineyardPanel();
			ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos165"), vineyardPanel);
			ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, null);
			Utils.addCloseButton(ProgramPanels.TABBED_PANE, vineyardPanel);
			ProgramPanels.TABBED_PANE.setSelectedComponent(vineyardPanel);
		}
		updateMainPanel();
	}

	class CapacityAction extends AbstractAction {

		private static final long serialVersionUID = -7204054967253027549L;

		private CapacityAction() {
			super(Program.getLabel("Infos400") + "...");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			openCapacityPanel();
			if (ProgramPanels.getAddVin() != null) {
				ProgramPanels.getAddVin().updateView();
			}
		}
	}

	public void openCapacityPanel() {
		if (ProgramPanels.getCapacityPanel() == null) {
			try {
				final CapacityPanel capacityPanel = ProgramPanels.createCapacityPanel();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos400"), capacityPanel);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, null);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, capacityPanel);
			} catch (RuntimeException e) {
				Program.showException(e);
			}
		}
		try {
			ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getCapacityPanel());
		} catch (IllegalArgumentException e) {
			final CapacityPanel capacityPanel = ProgramPanels.createCapacityPanel();
			ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos400"), capacityPanel);
			ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, null);
			Utils.addCloseButton(ProgramPanels.TABBED_PANE, capacityPanel);
			ProgramPanels.TABBED_PANE.setSelectedComponent(capacityPanel);
		}
		updateMainPanel();
	}

	final class ShowFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ShowFileAction() {
			super(Program.getLabel("Infos324"), MyCellarImage.SHOW);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos324"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getShowFile() == null) {
				final ShowFile showfile = ProgramPanels.createShowFile();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos325"), showfile);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, showfile);
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getShowFile());
			} catch (IllegalArgumentException e) {
				final ShowFile showfile = ProgramPanels.createShowFile();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos325"), showfile);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, showfile);
				ProgramPanels.TABBED_PANE.setSelectedComponent(showfile);
			}
			updateMainPanel();
		}
	}

	final class ShowTrashAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ShowTrashAction() {
			super("", MyCellarImage.TRASH);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.ShowTrash"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getShowTrash() == null) {
				final ShowFile showTrash = ProgramPanels.createShowTrash();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.ShowTrash"), showTrash);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, showTrash);
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getShowTrash());
			} catch (IllegalArgumentException e) {
				final ShowFile showTrash = ProgramPanels.createShowTrash();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.ShowTrash"), showTrash);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, showTrash);
				ProgramPanels.TABBED_PANE.setSelectedComponent(showTrash);
			}
			ProgramPanels.getShowTrash().updateView();
			updateMainPanel();
		}
	}

	final class CutAction extends AbstractAction {
		private static final long serialVersionUID = -8024045169612180263L;

		private CutAction() {
			super(Program.getLabel("Infos241"), MyCellarImage.CUT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos241"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.isCutCopyPastTab()) {
				ProgramPanels.getSelectedComponent(ICutCopyPastable.class).cut();
			}
		}
	}

	final class CopyAction extends AbstractAction {
		private static final long serialVersionUID = -4416042464174203695L;

		private CopyAction() {
			super(Program.getLabel("Infos242"), MyCellarImage.COPY);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos242"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.isCutCopyPastTab()) {
				ProgramPanels.getSelectedComponent(ICutCopyPastable.class).copy();
			}
		}
	}

	final class PasteAction extends AbstractAction {
		private static final long serialVersionUID = 7152419581737782003L;

		private PasteAction() {
			super(Program.getLabel("Infos243"), MyCellarImage.PASTE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos243"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.isCutCopyPastTab()) {
				ProgramPanels.getSelectedComponent(ICutCopyPastable.class).paste();
			}
		}
	}

	final class ManagePlaceAction extends AbstractAction {

		private static final long serialVersionUID = -5144284671743409095L;

		private ManagePlaceAction() {
			super(Program.getLabel("Main.ManagePlace"), MyCellarImage.PLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.ManagePlace"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getCellarOrganizerPanel() == null) {
				try {
					final CellarOrganizerPanel cellarOrganizerPanel = ProgramPanels.createCellarOrganizerPanel();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.ManagePlace"), cellarOrganizerPanel);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, cellarOrganizerPanel);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getCellarOrganizerPanel());
			} catch (IllegalArgumentException e) {
				final CellarOrganizerPanel cellarOrganizerPanel = ProgramPanels.createCellarOrganizerPanel();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.ManagePlace"), cellarOrganizerPanel);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, cellarOrganizerPanel);
				ProgramPanels.TABBED_PANE.setSelectedComponent(cellarOrganizerPanel);
			}
			updateMainPanel();
		}
	}

	final class ParametersAction extends MyCellarAction {

		private static final long serialVersionUID = -5144284671743409095L;

		private ParametersAction() {
			super(LabelType.INFO, "156", LabelProperty.SINGLE.withThreeDashes(), MyCellarImage.PARAMETER);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (ProgramPanels.getParametres() == null) {
				try {
					final Parametres parametres = ProgramPanels.createParametres();
					ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos156"), parametres);
					ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PARAMETER);
					Utils.addCloseButton(ProgramPanels.TABBED_PANE, parametres);
				} catch (RuntimeException e) {
					Program.showException(e);
				}
			}
			try {
				ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getParametres());
			} catch (IllegalArgumentException e) {
				final Parametres parametres = ProgramPanels.createParametres();
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Infos156"), parametres);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PARAMETER);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, parametres);
				ProgramPanels.TABBED_PANE.setSelectedComponent(parametres);
			}
			updateMainPanel();
		}
	}

	public void openCellChooserPanel(IPlace iPlace) {
		if (ProgramPanels.getCellChoosePanel() == null) {
			try {
				final CellarOrganizerPanel chooseCellPanel = ProgramPanels.createChooseCellPanel(iPlace);
				ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.ChooseCell"), chooseCellPanel);
				ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(ProgramPanels.TABBED_PANE, chooseCellPanel);
			} catch (RuntimeException e) {
				Program.showException(e);
			}
		}
		try {
			ProgramPanels.TABBED_PANE.setSelectedComponent(ProgramPanels.getCellChoosePanel());
			ProgramPanels.getCellChoosePanel().setIPlace(iPlace);
		} catch (IllegalArgumentException e) {
			final CellarOrganizerPanel chooseCellPanel = ProgramPanels.createChooseCellPanel(iPlace);
			ProgramPanels.TABBED_PANE.add(Program.getLabel("Main.ChooseCell"), chooseCellPanel);
			ProgramPanels.TABBED_PANE.setIconAt(ProgramPanels.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
			Utils.addCloseButton(ProgramPanels.TABBED_PANE, chooseCellPanel);
			ProgramPanels.TABBED_PANE.setSelectedComponent(chooseCellPanel);
		}
		updateMainPanel();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Program.showException(e, true);
	}

	private final class PanelObjectType extends JPanel {

		private final MyCellarComboBox<ObjectType> types = new MyCellarComboBox<>();
		MyCellarLabel label_objectType = new MyCellarLabel(LabelType.INFO_OTHER, "Parameters.typeLabel");
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
			add(label_objectType);
			add(types);
		}

		private ObjectType findObjectType(Program.Type type) {
			final Optional<ObjectType> first = objectTypes.stream().filter(objectType -> objectType.getType() == type).findFirst();
			return first.orElse(null);
		}

		public Program.Type getSelectedType() {
			return ((ObjectType) Objects.requireNonNull(types.getSelectedItem())).getType();
		}
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
}
