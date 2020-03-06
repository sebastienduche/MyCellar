package mycellar;

import mycellar.actions.ExportPDFAction;
import mycellar.actions.OpenWorkSheetAction;
import mycellar.core.IAddVin;
import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarVersion;
import mycellar.core.UnableToOpenFileException;
import mycellar.launcher.Server;
import mycellar.showfile.ShowFile;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
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
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.prefs.Preferences;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 26.2
 * @since 06/03/20
 */
public class Start extends JFrame implements Thread.UncaughtExceptionHandler {

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
	
	private final MyCellarLabel copyright = new MyCellarLabel();
	private final MyCellarLabel update = new MyCellarLabel();
	private static final String INFOS_VERSION = " 2019 v";
	private final MyCellarLabel version = new MyCellarLabel();
	
	private char QUITTER;
	private char IMPORT;
	private char AJOUTERV;
	private char AJOUTERR;
	private char EXPORT;
	private char TABLEAUX;
	private char STAT;
	private char MODIF;
	private char RECHERCHE;
	private char SUPPR;
	private char VISUAL;
	private char HISTORY;
	private char SAVE;
	private char NEW;
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
	private final JMenuItem modifPlace = new JMenuItem();
	private final JMenuItem delPlace = new JMenuItem();
	private final JMenuItem showFile = new JMenuItem();
	private final JMenuItem showWorksheet = new JMenuItem();
	private final JMenuItem addWine = new JMenuItem();
	private final JMenuItem searchWine = new JMenuItem();
	private final JMenuItem Aide = new JMenuItem();
	private final JMenuItem parameter = new JMenuItem();
	private final JMenuItem about = new JMenuItem();
	private final JMenuItem tocreate = new JMenuItem();
	private final JMenuItem news = new JMenuItem();
	private final JMenuItem history = new JMenuItem();
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
	private final JMenuItem jMenuSetConfig = new JMenuItem();
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
			int nIndex = parameters.indexOf("-opts=");
			if (nIndex == -1) {
				// demarrage sans options
				Program.setNewFile(parameters.trim());
			} else {
				// demarrage avec options
				// ______________________
				String tmp = parameters.substring(0, nIndex);
				// Recuperation du nom du fichier
				if (tmp.contains(Program.EXTENSION)) {
					Program.setNewFile(tmp.trim());
				} else {
					// On prend tous ce qu'il y a apres -opts
					tmp = parameters.substring(nIndex);
					if (tmp.contains(Program.EXTENSION)) {
						// Si l'on trouve l'extension du fichier
						// on cherche le caractere ' ' qui va separer les
						// options du nom du fichier
						String tmp2 = tmp.trim();
						tmp2 = tmp2.substring(tmp2.indexOf(" "));
						Program.setNewFile(tmp2.trim());
					}
				}
				// Recuperation des options
				tmp = parameters.substring(nIndex + 6).trim();
				tmp = tmp.substring(0, tmp.indexOf(" ")).trim();
				// Options a gerer
				if ("restart".equals(tmp)) {
					// Demarrage avec une nouvelle cave
					Program.putGlobalConfigBool(MyCellarSettings.STARTUP, false);
					Program.putCaveConfigBool(MyCellarSettings.ANNEE_CTRL, true);
					Program.putCaveConfigBool(MyCellarSettings.FIC_EXCEL, false);
				}
			}

			Thread.setDefaultUncaughtExceptionHandler((t, e) -> Program.showException(e, true));

			while (splashscreen.isRunning()) {}

			getInstance().startup();
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

		// Initialisation du mode Debug
		// ____________________________

		if (Program.getGlobalConfigBool(MyCellarSettings.DEBUG, false)) {
			Program.setDebug(true);
		}

		// Controle des MAJ
		// Appel serveur pour alimenter la derniere version en ligne
		Server.getInstance().getServerVersion();

		// Demarrage
		// _________

		if (!Program.hasFile() && !Program.getGlobalConfigBool(MyCellarSettings.STARTUP, false)) {
			// Language au premier demarrage
			String lang = System.getProperty("user.language");
			if("fr".equalsIgnoreCase(lang)) {
				lang = "F";
			} else {
				lang = "U";
			}
			Program.putGlobalConfigString(MyCellarSettings.LANGUAGE, lang);
			
			updateFrame(true);
			Program.putGlobalConfigBool(MyCellarSettings.STARTUP, true);
		}

		// Parametrage
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
		} else if (Program.GetCaveLength() == 0) {
			Program.getCave().add(Program.DEFAULT_PLACE);
		}
		enableAll(true);
	}

	/**
	 * Fonction pour desactiver ou activer toutes les options ou boutons
	 */
	void enableAll(boolean enable) {
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
		for(Component c : Program.TABBED_PANE.getComponents()) {
			if(c instanceof ITabListener) {
				if(!((ITabListener) c).tabWillClose(null)) {
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
		try {
			new APropos().setVisible(true);
		} catch (Exception e) {
			Program.showException(e);
		}
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
			if (f.exists() && MyXmlDom.readMyCellarXml(fic, cave)) {
				MyXmlDom.writeMyCellarXml(cave, "");
				Program.loadObjects();
			}
		}
	}

	/**
	 * Actions realises apres l'ouverture d'un fichier
	 */
	private void postOpenFile() {
		try {
			loadFile();
		} catch (Exception e) {
			Program.showException(e);
		}
		Program.updateAllPanels();
		updateMainPanel();
		Program.PANEL_INFOS.setEnable(true);
		Program.PANEL_INFOS.refresh();
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
	 * @param sFile
	 */
	private void reOpenFile(String sFile) {
		try{
    		enableAll(false);
    		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		if (!sFile.isEmpty()) {
					Program.openaFile(new File(sFile));
					postOpenFile();
				} else {
					enableAll(false);
					Program.updateAllPanels();
					updateMainPanel();
					setTitle(Program.getLabel("Infos001"));
				}
		} catch(UnableToOpenFileException e) {
			Erreur.showSimpleErreur(Program.getError("Error.LoadingFile"));
			Program.showException(e, false);
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
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
		Program.PANEL_INFOS.setEnable(false);
		Program.PANEL_INFOS.refresh();
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
			if (!fic.trim().toLowerCase().endsWith(".xml")) {
				fic = fic.concat(".xml");
			}
			MyXmlDom.writeMyCellarXml(Program.getCave(), fic);
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
			if (!fic.trim().toLowerCase().endsWith(".xml")) {
				fic = fic.concat(".xml");
			}
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
			Program.setLanguage(LanguageFileLoader.getLanguage(thelangue.charAt(0)));
			updateLabels();
			Debug("Loading Frame ended");
		} catch (Exception e) {
			Program.showException(e);
		}
	}

	void updateLabels() {
		final String quitter = Program.getLabel("QUITTER");
		if (quitter == null || quitter.isEmpty()) {
			Program.setLanguage(LanguageFileLoader.Language.FRENCH);
			QUITTER = Program.getLabel("QUITTER").charAt(0);
		} else {
			QUITTER = quitter.charAt(0);
		}

		IMPORT = Program.getLabel("IMPORT").charAt(0);
		AJOUTERV = Program.getLabel("AJOUTERV").charAt(0);
		AJOUTERR = Program.getLabel("AJOUTERR").charAt(0);
		EXPORT = Program.getLabel("EXPORT").charAt(0);
		TABLEAUX = Program.getLabel("TABLEAUX").charAt(0);
		STAT = Program.getLabel("STAT").charAt(0);
		MODIF = Program.getLabel("MODIF").charAt(0);
		RECHERCHE = Program.getLabel("RECHERCHE").charAt(0);
		SUPPR = Program.getLabel("SUPPR").charAt(0);
		VISUAL = Program.getLabel("VISUAL").charAt(0);
		HISTORY = Program.getLabel("HISTORY").charAt(0);
		SAVE = Program.getLabel("SAVE").charAt(0);
		NEW = Program.getLabel("NEW").charAt(0);
		m_oMenuBar = new JMenuBar();

		// differents menus
		menuFile.setText(Program.getLabel("Infos104")); // Fichier
		menuPlace.setText(Program.getLabel("Infos105"));
		menuWine.setText(Program.getLabel("Infos106")); // Vin
		menuTools.setText(Program.getLabel("Infos246"));
		menuEdition.setText(Program.getLabel("Infos245"));

		// differents choix de chaque menu
		importation.setText(Program.getLabel("Infos107")); // Import...
		quit.setText(Program.getLabel("Infos003")); // Quitter
		exportation.setText(Program.getLabel("Infos108")); // Export...
		statistiques.setText(Program.getLabel("Infos009")); // Statistiques
		tableau.setText(Program.getLabel("Infos093")); // Tableaux...
		addPlace.setText(Program.getLabel("Infos109")); // Ajouter...
		modifPlace.setText(Program.getLabel("Infos086")); // Modifier...
		delPlace.setText(Program.getLabel("Infos052")); // Supprimer...
		addWine.setText(Program.getLabel("Infos109")); // Ajouter...
		Aide.setText(Program.getLabel("Infos111")); // Aide Contextuelle...
		saveAs.setText(Program.getLabel("Infos371")); // Sauvegarder
		newFile.setText(Program.getLabel("Infos378"));
		openFile.setText(Program.getLabel("Infos372"));
		save.setText(Program.getLabel("Infos326"));
		showFile.setText(Program.getLabel("Infos324"));
		showWorksheet.setText(Program.getLabel("ShowFile.Worksheet"));
		searchWine.setText(Program.getLabel("Infos006"));

		parameter.setText(Program.getLabel("Infos156")); // Parametres
		about.setText(Program.getLabel("Infos199")); // A Propos
		news.setText(Program.getLabel("Infos330")); // Nouveautes
		tocreate.setText(Program.getLabel("Infos267")); // Rangement a creer
		history.setText(Program.getLabel("Infos341")); // Historique
		vignobles.setText(Program.getLabel("Infos165")); // Vignobles
		bottleCapacity.setText(Program.getLabel("Infos400")); // Contenance
		jMenuImportXmlPlaces.setText(Program.getLabel("Infos367")); // Importer des rangements xml
		jMenuExportXmlPlaces.setText(Program.getLabel("Infos368")); // Exporter des rangements xml
		jMenuExportXml.setText(Program.getLabel("Infos408")); // Exporter au format xml
		jMenuCloseFile.setText(Program.getLabel("Infos019")); // Fermer...
		jMenuSetConfig.setText(Program.getLabel("Infos373")); // Modifier les parametres...
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
		m_oAjouterButton.setText(Program.getLabel("Infos005"));
		m_oRechercherButton.setText(Program.getLabel("Infos006"));
		m_oSupprimerButton.setText(Program.getLabel("Infos004"));
		version.setText(Program.getLabel("MonthVersion") + INFOS_VERSION + MyCellarVersion.MAIN_VERSION);
		addWine.setAccelerator(KeyStroke.getKeyStroke(AJOUTERV, InputEvent.CTRL_DOWN_MASK));
		addPlace.setAccelerator(KeyStroke.getKeyStroke(AJOUTERR, InputEvent.CTRL_DOWN_MASK));
		delPlace.setAccelerator(KeyStroke.getKeyStroke(SUPPR, InputEvent.CTRL_DOWN_MASK));
		history.setAccelerator(KeyStroke.getKeyStroke(HISTORY, InputEvent.CTRL_DOWN_MASK));
		tableau.setAccelerator(KeyStroke.getKeyStroke(TABLEAUX, InputEvent.CTRL_DOWN_MASK));
		statistiques.setAccelerator(KeyStroke.getKeyStroke(STAT, InputEvent.CTRL_DOWN_MASK));
		importation.setAccelerator(KeyStroke.getKeyStroke(IMPORT, InputEvent.CTRL_DOWN_MASK));
		exportation.setAccelerator(KeyStroke.getKeyStroke(EXPORT, InputEvent.CTRL_DOWN_MASK));
		modifPlace.setAccelerator(KeyStroke.getKeyStroke(MODIF, InputEvent.CTRL_DOWN_MASK));
		quit.setAccelerator(KeyStroke.getKeyStroke(QUITTER, InputEvent.CTRL_DOWN_MASK));
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
		ParametersAction parameterAction = new ParametersAction();
		AddPlaceAction addPlaceAction = new AddPlaceAction();
		ModifyPlaceAction modifyPlaceAction = new ModifyPlaceAction();
		DeletePlaceAction deletePlaceAction = new DeletePlaceAction();
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
		setLayout(new MigLayout("", "[grow]", "[grow][][]"));

		copyright.setFont(new Font("Dialog", Font.PLAIN, 10));
		copyright.setText("Copyright S\u00e9bastien D.");
		version.setFont(new Font("Dialog", Font.PLAIN, 10));
		update.setFont(new Font("Dialog", Font.PLAIN, 10));
		add(Program.TABBED_PANE, "grow, hidemode 3, wrap");
		add(Program.PANEL_INFOS, "grow, hidemode 3, wrap");
		add(update, "wrap");
		add(copyright, "align right, gapright 10, wrap");
		add(version, "align right, gapright 10, gapbottom 10");
		Program.TABBED_PANE.setVisible(false);

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
		m_oModifierButton.setAction(modifyPlaceAction);
		m_oSupprimerButton.setAction(deletePlaceAction);
		m_oShowFileButton.setAction(showFileAction);
		m_oTableauxButton.setAction(createTabAction);
		m_oStatsButton.setAction(statAction);
		m_oImporterButton.setAction(importFileAction);
		m_oExportButton.setAction(exportFileAction);
		m_oManagePlaceButton.setAction(managePlaceAction);
		m_oWorksheetButton.setAction(new OpenWorkSheetAction());
		m_oShowTrashButton.setAction(showTrashAction);
		parameter.setAction(parameterAction);
		addPlace.setAction(addPlaceAction);
		modifPlace.setAction(modifyPlaceAction);
		delPlace.setAction(deletePlaceAction);
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
		addWine.setAccelerator(KeyStroke.getKeyStroke(AJOUTERV, InputEvent.CTRL_DOWN_MASK));
		addPlace.setAccelerator(KeyStroke.getKeyStroke(AJOUTERR, InputEvent.CTRL_DOWN_MASK));
		delPlace.setAccelerator(KeyStroke.getKeyStroke(SUPPR, InputEvent.CTRL_DOWN_MASK));
		showFile.setAccelerator(KeyStroke.getKeyStroke(VISUAL, InputEvent.CTRL_DOWN_MASK));
		history.setAccelerator(KeyStroke.getKeyStroke(HISTORY, InputEvent.CTRL_DOWN_MASK));
		searchWine.setAccelerator(KeyStroke.getKeyStroke(RECHERCHE, InputEvent.CTRL_DOWN_MASK));
		tableau.setAccelerator(KeyStroke.getKeyStroke(TABLEAUX, InputEvent.CTRL_DOWN_MASK));
		statistiques.setAccelerator(KeyStroke.getKeyStroke(STAT, InputEvent.CTRL_DOWN_MASK));
		importation.setAccelerator(KeyStroke.getKeyStroke(IMPORT, InputEvent.CTRL_DOWN_MASK));
		exportation.setAccelerator(KeyStroke.getKeyStroke(EXPORT, InputEvent.CTRL_DOWN_MASK));
		modifPlace.setAccelerator(KeyStroke.getKeyStroke(MODIF, InputEvent.CTRL_DOWN_MASK));
		quit.setAccelerator(KeyStroke.getKeyStroke(QUITTER, InputEvent.CTRL_DOWN_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(SAVE, InputEvent.CTRL_DOWN_MASK));
		newFile.setAccelerator(KeyStroke.getKeyStroke(NEW, InputEvent.CTRL_DOWN_MASK));
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

		if (bUpdateAvailable) {
			String sText = MessageFormat.format(Program.getLabel("Infos385"), Server.getInstance().getAvailableVersion(), MyCellarVersion.MAIN_VERSION + "-" + Program.INTERNAL_VERSION);
			update.setText(sText);
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

		Program.TABBED_PANE.addChangeListener((arg) -> {
				Program.updateSelectedTab();
				Program.TABBED_PANE.getSelectedComponent();
		});

		quit.addActionListener((e) -> quitter_actionPerformed());
		about.addActionListener((e) -> about_actionPerformed());
		news.addActionListener((e) -> news_actionPerformed());
		history.setAction(new ShowHistoryAction());
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

	/**
	 * Debug
	 * 
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Start: " + sText);
	}

	public void updateMainPanel() {
		Debug("updateMainPanel: Trying to display PANEL_INFOS...");
		int count = Program.TABBED_PANE.getTabCount();
		Program.PANEL_INFOS.setVisible(count == 0);
		Program.TABBED_PANE.setVisible(count > 0);
		if (count == 0) {
			Program.PANEL_INFOS.refresh();
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
		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * menuSetConfig_actionPerformed: Modification des parametres internes
	 */
	private void menuSetConfig_actionPerformed() {
		try {
			String[] type_objet = { "JTextField" };
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
		Program.TABBED_PANE.removeTabAt(Program.TABBED_PANE.getSelectedIndex());
		updateMainPanel();
	}

	public static void setPaneModified(boolean modify) {
		if (Program.TABBED_PANE.getSelectedComponent() != null) {
			int index = Program.TABBED_PANE.getSelectedIndex();
			String title = Program.TABBED_PANE.getTitleAt(index);
			if (modify) {
				if (!title.endsWith("*")) {
					Program.TABBED_PANE.setTitleAt(index, title + "*");
				}
			} else {
				if (title.endsWith("*")) {
					title = title.substring(0, title.length() - 1);
				}
				Program.TABBED_PANE.setTitleAt(index, title);
			}
		}
	}

	class OpenAction extends AbstractAction {
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
							Program.updateAllPanels();
							updateMainPanel();
							setTitle(Program.getLabel("Infos001"));
    					return;
						}
    				String fic = file.getAbsolutePath();
    				fic = MyCellarControl.controlAndUpdateExtension(fic, Filtre.FILTRE_SINFO);
    				Program.openaFile(new File(fic));
    				postOpenFile();
    			}
			} catch(UnableToOpenFileException e) {
				Erreur.showSimpleErreur(Program.getError("Error.LoadingFile"));
				Program.showException(e, false);
			}	finally {
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	class NewAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private NewAction() {
			super(Program.getLabel("Infos378"), MyCellarImage.NEW);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos378"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Debug("newFileAction: Creating a new file...");
			Program.newFile();
			postOpenFile();
			Debug("newFileAction: Creating a new file OK");
		}
	}

	class SaveAction extends AbstractAction {
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
				for (int i = 0; i < Program.TABBED_PANE.getTabCount(); i++) {
					Component tab = Program.TABBED_PANE.getComponentAt(i);
					if (tab instanceof ManageBottle) {
						if (Program.TABBED_PANE.getTitleAt(i).endsWith("*")) {
							Program.TABBED_PANE.setSelectedIndex(i);
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
			} catch (Exception e3) {
				Program.showException(e3);
			} finally {
				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	class SaveAsAction extends AbstractAction {
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

	class AddWineAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private AddWineAction() {
			super(Program.getLabel("Infos005"), MyCellarImage.WINE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos005"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(AJOUTERR, InputEvent.CTRL_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getAddVin() == null) {
				try {
					final AddVin addVin = Program.createAddVin();
					Program.TABBED_PANE.add(Program.getLabel("Infos005"), addVin);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.WINE);
					Utils.addCloseButton(Program.TABBED_PANE, addVin);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getAddVin());
			} catch (IllegalArgumentException e) {
				final AddVin addVin = Program.getAddVin();
				Program.TABBED_PANE.add(Program.getLabel("Infos005"), addVin);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.WINE);
				Utils.addCloseButton(Program.TABBED_PANE, addVin);
				Program.TABBED_PANE.setSelectedComponent(addVin);
			}
			Program.getAddVin().reInit();
			updateMainPanel();
		}
	}

	class AddPlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private AddPlaceAction() {
			super(Program.getLabel("Infos109"), MyCellarImage.PLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos010"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getCreerRangement() == null) {
				try {
					final Creer_Rangement creerRangement = Program.createCreerRangement();
					Program.TABBED_PANE.add(Program.getLabel("Infos010"), creerRangement);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(Program.TABBED_PANE, creerRangement);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getCreerRangement());
			} catch (IllegalArgumentException e) {
				final Creer_Rangement creerRangement = Program.createCreerRangement();
				Program.TABBED_PANE.add(Program.getLabel("Infos010"), creerRangement);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.TABBED_PANE, creerRangement);
				Program.TABBED_PANE.setSelectedComponent(creerRangement);
			}
			updateMainPanel();
		}
	}

	class DeletePlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private DeletePlaceAction() {
			super(Program.getLabel("Infos052"), MyCellarImage.DELPLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos004"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getSupprimerRangement() == null) {
				try {
					final Supprimer_Rangement supprimerRangement = Program.createSupprimerRangement();
					Program.TABBED_PANE.add(Program.getLabel("Infos004"), supprimerRangement);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.DELPLACE);
					Utils.addCloseButton(Program.TABBED_PANE, supprimerRangement);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getSupprimerRangement());
			} catch (IllegalArgumentException e) {
				final Supprimer_Rangement supprimerRangement = Program.createSupprimerRangement();
				Program.TABBED_PANE.add(Program.getLabel("Infos004"), supprimerRangement);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.DELPLACE);
				Utils.addCloseButton(Program.TABBED_PANE, supprimerRangement);
				Program.TABBED_PANE.setSelectedComponent(supprimerRangement);
			}
			updateMainPanel();
		}
	}

	class ModifyPlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ModifyPlaceAction() {
			super(Program.getLabel("Infos086"), MyCellarImage.MODIFYPLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos007"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getModifierRangement() == null) {
				try {
					final Creer_Rangement modifierRangement = Program.createModifierRangement();
					Program.TABBED_PANE.add(Program.getLabel("Infos007"), modifierRangement);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
					Utils.addCloseButton(Program.TABBED_PANE, modifierRangement);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			Program.getModifierRangement().updateView();
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getModifierRangement());
			} catch (IllegalArgumentException e) {
				final Creer_Rangement modifierRangement = Program.createModifierRangement();
				Program.TABBED_PANE.add(Program.getLabel("Infos007"), modifierRangement);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
				Utils.addCloseButton(Program.TABBED_PANE, modifierRangement);
				Program.TABBED_PANE.setSelectedComponent(modifierRangement);
			}
			updateMainPanel();
		}
	}

	class SearchAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private SearchAction() {
			super(Program.getLabel("Infos006"), MyCellarImage.SEARCH);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos006"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getSearch() == null) {
				final Search search = Program.createSearch();
				Program.TABBED_PANE.add(Program.getLabel("Infos221"), search);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(Program.TABBED_PANE, search);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getSearch());
			} catch (IllegalArgumentException e) {
				final Search search = Program.createSearch();
				Program.TABBED_PANE.add(Program.getLabel("Infos221"), search);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(Program.TABBED_PANE, search);
				Program.TABBED_PANE.setSelectedComponent(search);
			}
			updateMainPanel();
		}
	}

	class CreateTabAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private CreateTabAction() {
			super(Program.getLabel("Infos093"), MyCellarImage.TABLE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos008"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getCreerTableaux() == null) {
				final Creer_Tableaux creerTableaux = Program.createCreerTableaux();
				Program.TABBED_PANE.add(Program.getLabel("Infos008"), creerTableaux);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(Program.TABBED_PANE, creerTableaux);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getCreerTableaux());
			} catch (IllegalArgumentException e) {
				final Creer_Tableaux creerTableaux = Program.createCreerTableaux();
				Program.TABBED_PANE.add(Program.getLabel("Infos008"), creerTableaux);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(Program.TABBED_PANE, creerTableaux);
				Program.TABBED_PANE.setSelectedComponent(creerTableaux);
			}
			updateMainPanel();
		}
	}

	class ImportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ImportFileAction() {
			super(Program.getLabel("Infos107"), MyCellarImage.IMPORT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos011"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (Program.getImporter() == null) {
				final Importer importer = Program.createImporter();
				Program.TABBED_PANE.add(Program.getLabel("Infos011"), importer);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(Program.TABBED_PANE, importer);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getImporter());
			} catch (IllegalArgumentException e) {
				final Importer importer = Program.createImporter();
				Program.TABBED_PANE.add(Program.getLabel("Infos011"), importer);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(Program.TABBED_PANE, importer);
				Program.TABBED_PANE.setSelectedComponent(importer);
			}
			updateMainPanel();
		}
	}

	class ExportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ExportFileAction() {
			super(Program.getLabel("Infos108"), MyCellarImage.EXPORT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos125"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getExport() == null) {
				try {
					final Export export = Program.createExport();
					Program.TABBED_PANE.add(Program.getLabel("Infos148"), export);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.EXPORT);
					Utils.addCloseButton(Program.TABBED_PANE, export);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getExport());
			} catch (IllegalArgumentException e) {
				final Export export = Program.createExport();
				Program.TABBED_PANE.add(Program.getLabel("Infos148"), export);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.EXPORT);
				Utils.addCloseButton(Program.TABBED_PANE, export);
				Program.TABBED_PANE.setSelectedComponent(export);
			}
			updateMainPanel();
		}
	}

	class StatAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private StatAction() {
			super(Program.getLabel("Infos009"), MyCellarImage.STATS);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos009"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getStat() == null) {
				try {
					final Stat stat = Program.createStat();
					stat.setVisible(true);
					Program.TABBED_PANE.add(Program.getLabel("Infos009"), stat);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.STATS);
					Utils.addCloseButton(Program.TABBED_PANE, stat);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getStat());
			} catch (IllegalArgumentException e) {
				final Stat stat = Program.createStat();
				Program.TABBED_PANE.add(Program.getLabel("Infos009"), stat);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.STATS);
				Utils.addCloseButton(Program.TABBED_PANE, stat);
				Program.TABBED_PANE.setSelectedComponent(stat);
			}
			Program.getStat().updateView();
			updateMainPanel();
		}
	}

	class ShowHistoryAction extends AbstractAction {
		private static final long serialVersionUID = -2981766233846291757L;

		private ShowHistoryAction() {
			super(Program.getLabel("Infos341"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getShowHistory() == null) {
				try {
					final ShowHistory showHistory = Program.createShowHistory();
					Program.TABBED_PANE.add(Program.getLabel("Infos341"), showHistory);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
					Utils.addCloseButton(Program.TABBED_PANE, showHistory);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getShowHistory());
			} catch (IllegalArgumentException e) {
				final ShowHistory showHistory = Program.createShowHistory();
				Program.TABBED_PANE.add(Program.getLabel("Infos341"), showHistory);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
				Utils.addCloseButton(Program.TABBED_PANE, showHistory);
				Program.TABBED_PANE.setSelectedComponent(showHistory);
			}
			Program.getShowHistory().refresh();
			updateMainPanel();
		}
	}
	
	class VignoblesAction extends AbstractAction {

		private static final long serialVersionUID = -7956676252030557402L;

		private VignoblesAction() {
			super(Program.getLabel("Infos165"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			openVineyardPanel();
		}
	}
	
	class CapacityAction extends AbstractAction {

		private static final long serialVersionUID = -7204054967253027549L;

		private CapacityAction() {
			super(Program.getLabel("Infos400")+"...");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new ManageList();
			Program.updateAllPanels();
			if (Program.getAddVin() != null) {
				Program.getAddVin().updateView();
			}
		}
	}

	public void openVineyardPanel() {
		if (Program.getVineyardPanel() == null) {
			try {
				final VineyardPanel vineyardPanel = Program.createVineyardPanel();
				Program.TABBED_PANE.add(Program.getLabel("Infos165"), vineyardPanel);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
				Utils.addCloseButton(Program.TABBED_PANE, vineyardPanel);
			} catch (Exception e1) {
				Program.showException(e1);
			}
		}
		try {
			Program.TABBED_PANE.setSelectedComponent(Program.getVineyardPanel());
		} catch (IllegalArgumentException e) {
			final VineyardPanel vineyardPanel = Program.createVineyardPanel();
			Program.TABBED_PANE.add(Program.getLabel("Infos165"), vineyardPanel);
			Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
			Utils.addCloseButton(Program.TABBED_PANE, vineyardPanel);
			Program.TABBED_PANE.setSelectedComponent(vineyardPanel);
		}
		updateMainPanel();
	}

	class ShowFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ShowFileAction() {
			super(Program.getLabel("Infos324"), MyCellarImage.SHOW);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos324"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getShowFile() == null) {
				final ShowFile showFile = Program.createShowFile();
				Program.TABBED_PANE.add(Program.getLabel("Infos325"), showFile);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(Program.TABBED_PANE, showFile);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getShowFile());
			} catch (IllegalArgumentException e) {
				final ShowFile showFile = Program.createShowFile();
				Program.TABBED_PANE.add(Program.getLabel("Infos325"), showFile);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(Program.TABBED_PANE, showFile);
				Program.TABBED_PANE.setSelectedComponent(showFile);
			}
			updateMainPanel();
		}
	}

	class ShowTrashAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ShowTrashAction() {
			super("", MyCellarImage.TRASH);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.ShowTrash"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getShowTrash() == null) {
				final ShowFile showTrash = Program.createShowTrash();
				Program.TABBED_PANE.add(Program.getLabel("Main.ShowTrash"), showTrash);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(Program.TABBED_PANE, showTrash);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getShowTrash());
			} catch (IllegalArgumentException e) {
				final ShowFile showTrash = Program.createShowTrash();
				Program.TABBED_PANE.add(Program.getLabel("Main.ShowTrash"), showTrash);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(Program.TABBED_PANE, showTrash);
				Program.TABBED_PANE.setSelectedComponent(showTrash);
			}
			Program.getShowTrash().updateView();
			updateMainPanel();
		}
	}

	class CutAction extends AbstractAction {
		private static final long serialVersionUID = -8024045169612180263L;

		private CutAction() {
			super(Program.getLabel("Infos241"), MyCellarImage.CUT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos241"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.isCutCopyPastTab()) {
				Program.getSelectedComponent(ICutCopyPastable.class).cut();
			}
		}
	}

	class CopyAction extends AbstractAction {
		private static final long serialVersionUID = -4416042464174203695L;

		private CopyAction() {
			super(Program.getLabel("Infos242"), MyCellarImage.COPY);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos242"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.isCutCopyPastTab()) {
				Program.getSelectedComponent(ICutCopyPastable.class).copy();
			}
		}
	}

	class PasteAction extends AbstractAction {
		private static final long serialVersionUID = 7152419581737782003L;

		private PasteAction() {
			super(Program.getLabel("Infos243"), MyCellarImage.PASTE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos243"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.isCutCopyPastTab()) {
				Program.getSelectedComponent(ICutCopyPastable.class).paste();
			}
		}
	}

	class ManagePlaceAction extends AbstractAction {

		private static final long serialVersionUID = -5144284671743409095L;

		private ManagePlaceAction() {
			super(Program.getLabel("Main.ManagePlace"), MyCellarImage.PLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.ManagePlace"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getCellarOrganizerPanel() == null) {
				try {
					final CellarOrganizerPanel cellarOrganizerPanel = Program.createCellarOrganizerPanel();
					Program.TABBED_PANE.add(Program.getLabel("Main.ManagePlace"), cellarOrganizerPanel);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(Program.TABBED_PANE, cellarOrganizerPanel);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getCellarOrganizerPanel());
			} catch (IllegalArgumentException e) {
				final CellarOrganizerPanel cellarOrganizerPanel = Program.createCellarOrganizerPanel();
				Program.TABBED_PANE.add(Program.getLabel("Main.ManagePlace"), cellarOrganizerPanel);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.TABBED_PANE, cellarOrganizerPanel);
				Program.TABBED_PANE.setSelectedComponent(cellarOrganizerPanel);
			}
			updateMainPanel();
		}
	}
	
	class ParametersAction extends AbstractAction {

		private static final long serialVersionUID = -5144284671743409095L;

		private ParametersAction() {
			super(Program.getLabel("Infos156"), MyCellarImage.PARAMETER);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos156"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getParametres() == null) {
				try {
					final Parametres parametres = Program.createParametres();
					Program.TABBED_PANE.add(Program.getLabel("Infos193"), parametres);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PARAMETER);
					Utils.addCloseButton(Program.TABBED_PANE, parametres);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.getParametres());
			} catch (IllegalArgumentException e) {
				final Parametres parametres = Program.createParametres();
				Program.TABBED_PANE.add(Program.getLabel("Infos193"), parametres);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PARAMETER);
				Utils.addCloseButton(Program.TABBED_PANE, parametres);
				Program.TABBED_PANE.setSelectedComponent(parametres);
			}
			updateMainPanel();
		}
	}
	
	public void openCellChooserPanel(IAddVin addvin) {
		if (Program.getCellChoosePanel() == null) {
			try {
				final CellarOrganizerPanel chooseCellPanel = Program.createChooseCellPanel(addvin);
				Program.TABBED_PANE.add(Program.getLabel("Main.ChooseCell"), chooseCellPanel);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.TABBED_PANE, chooseCellPanel);
			} catch (Exception e1) {
				Program.showException(e1);
			}
		}
		try {
			Program.TABBED_PANE.setSelectedComponent(Program.getCellChoosePanel());
			Program.getCellChoosePanel().setAddVin(addvin);
		} catch (IllegalArgumentException e) {
			final CellarOrganizerPanel chooseCellPanel = Program.createChooseCellPanel(addvin);
			Program.TABBED_PANE.add(Program.getLabel("Main.ChooseCell"), chooseCellPanel);
			Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
			Utils.addCloseButton(Program.TABBED_PANE, chooseCellPanel);
			Program.TABBED_PANE.setSelectedComponent(chooseCellPanel);
		}
		updateMainPanel();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Program.showException(e, true);
	}
}
