package mycellar;

import mycellar.actions.ExportPDFAction;
import mycellar.core.IAddVin;
import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarVersion;
import mycellar.launcher.Server;
import mycellar.showfile.ShowFile;
import mycellar.showfile.ShowFile.ShowType;
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
import java.util.prefs.Preferences;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 24.7
 * @since 04/07/18
 */
public class Start extends JFrame implements Thread.UncaughtExceptionHandler {

	private final JButton m_oSupprimerButton = new JButton();
	private final JButton m_oAjouterButton = new JButton();
	private final JButton m_oRechercherButton = new JButton();
	private final JButton m_oTableauxButton = new JButton();
	private final JButton m_oExportButton = new JButton();
	private final JButton m_oStatsButton = new JButton();
	private final JButton m_oManagePlaceButton = new JButton();
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
	private static final String INFOS_VERSION = " 2018 v";
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

	// différents menus
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

	/**
	 * Start: Constructeur pour démarrer l'application
	 */
	private Start() {}

	public static void main(String[] args) {

		final SplashScreen splashscreen = new SplashScreen();
		
		while (splashscreen.isRunning()) {}
		
		// initialisation
		Program.init();

		// Lecture des paramètres
		// ______________________

		try {
			String parameters = "";
			
			for (String arg : args) {
				parameters = parameters.concat(arg + " ");
			}
			int nIndex = parameters.indexOf("-opts=");
			if (nIndex == -1) {
				// démarrage sans options
				Program.setArchive(parameters.trim());
			} else {
				// démarrage avec options
				// ______________________
				String tmp = parameters.substring(0, nIndex);
				// Récupération du nom du fichier
				if (tmp.contains(".sinfo")) {
					Program.setArchive(tmp.trim());
				} else {
					// On prend tous ce qu'il y a après -opts
					tmp = parameters.substring(nIndex);
					if (tmp.contains(".sinfo")) {
						// Si l'on trouve l'extension du fichier
						// on cherche le caractère ' ' qui va séparer les
						// options du nom du fichier
						String tmp2 = tmp.trim();
						tmp2 = tmp2.substring(tmp2.indexOf(" "));
						Program.setArchive(tmp2.trim());
					}
				}
				// Récupération des options
				tmp = parameters.substring(nIndex + 6).trim();
				tmp = tmp.substring(0, tmp.indexOf(" ")).trim();
				// Options à gérer
				if ("restart".equals(tmp)) {
					// Démarrage avec une nouvelle cave
					Program.putGlobalConfigInt("STARTUP", 0);
					Program.putCaveConfigInt("ANNEE_CTRL", 1);
					Program.putCaveConfigInt("FIC_EXCEL", 0);
				}
			}
		} catch (Exception e) {
		}

		try {
			Thread.setDefaultUncaughtExceptionHandler((t, e) -> Program.showException(e, true));
			getInstance().startup();
		} catch (Exception e) {
			Program.showException(e);
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
	 * jbInit: Fonction d'initialisation de l'application
	 */
	private void startup() {
		Debug("Starting MyCellar version: "+MyCellarVersion.VERSION);
		Thread.currentThread().setUncaughtExceptionHandler(this);
		prefs = Preferences.userNodeForPackage(getClass());

		// Initialisation du mode Debug
		// ____________________________

		if (Program.getGlobalConfigInt("DEBUG", 0) == 1) {
			Program.setDebug(true);
		}

		// Contrôle des MAJ
		// Appel serveur pour alimenter la dernière version en ligne
		Server.getInstance().getServerVersion();

		// Démarrage
		// _________

		if (Program.getArchive().isEmpty() && Program.getGlobalConfigInt("STARTUP", 0) == 0) {
			// Language au premier démarrage
			String lang = System.getProperty("user.language");
			if("fr".equalsIgnoreCase(lang)) {
				lang = "F";
			} else {
				lang = "U";
			}
			Program.putGlobalConfigString("LANGUAGE", lang);
			
			updateFrame(true);
			Program.putGlobalConfigInt("STARTUP", 1);
		}

		// Paramètrage
		if (!Program.getArchive().isEmpty()) {
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

		if (Program.getArchive().isEmpty()) {
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
		m_oManagePlaceButton.setEnabled(enable);
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
	 * quitter_actionPerformed: Fonction appellé lorsque l'on quitte le
	 * programme.
	 * 
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
	 * about_actionPerformed: Appelle la fenêtre d'A Propos.
	 */
	private void about_actionPerformed() {
		try {
			new APropos().setVisible(true);
		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * news_actionPerformed: Affiche les nouveautés de la release
	 */
	private void news_actionPerformed() {
		Program.open(new File("Finish.html"));
	}

	/**
	 * tocreate_actionPerformed: Appelle la fenêtre de Bienvenue.
	 */
	private void tocreate_actionPerformed() {
		RangementUtils.findRangementToCreate();
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
	 * Actions réalisés après l'ouverture d'un fichier
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
	 * Ouverture d'un fichier déjà référencé
	 * 
	 * @param sFile
	 */
	private void reOpenFile(String sFile) {
		try{
    		enableAll(false);
    		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		if (!sFile.isEmpty() && Program.openaFile(new File(sFile))) {
					postOpenFile();
				} else {
					enableAll(false);
				}
		}catch(Exception e) {
			Program.showException(e);
		}
		finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * reopen1_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	private void reopen1_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN1", "");
		Debug("Reopen1FileAction: Restart with file " + sFile);
		reOpenFile(sFile);
	}

	/**
	 * reopen2_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	private void reopen2_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN2", "");
		Debug("Reopen2FileAction: Restart with file " + sFile);
		reOpenFile(sFile);
	}

	/**
	 * reopen3_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	private void reopen3_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN3", "");
		Debug("Reopen3FileAction: Restart with file " + sFile);
		reOpenFile(sFile);
	}

	/**
	 * reopen4_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	private void reopen4_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN4", "");
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
			if(!fic.trim().toLowerCase().endsWith(".xml")) {
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
			if(!fic.trim().toLowerCase().endsWith(".xml")) {
				fic = fic.concat(".xml");
			}
			ListeBouteille.writeXML(new File(fic));
		}
	}

	/**
	 * updateFrame: Met à jour tous les champs avec la langue sélectionnée. Met
	 * à jour tous les paramêtres suite au chargement d'un fichier
	 * 
	 * @param toverify
	 *            boolean
	 */
	private void updateFrame(boolean toverify) {

		try {

			boolean bHasVersion = false;
			if (null != Program.getCaveConfig() && Program.hasConfigCaveKey("VERSION")) {
				bHasVersion = true;
			} else if (Program.hasConfigGlobalKey("VERSION")) {
				bHasVersion = true;
			}

			String thelangue = Program.getGlobalConfigString("LANGUAGE", "F");
			if (!bHasVersion || toverify) {
				Program.initConf();
			}
			Program.setLanguage(thelangue);
			updateLabels();
			Debug("Loading Frame ended");
		} catch (Exception e) {
			Program.showException(e);
		}
	}

	void updateLabels() {
		try {
			QUITTER = Program.getLabel("QUITTER").charAt(0);
		} catch (NullPointerException npe) {
			Program.setLanguage("F");
			QUITTER = Program.getLabel("QUITTER").charAt(0);
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

		// différents menus
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
		modifPlace.setText(Program.getLabel("Infos079") + "..."); // Modifier...
		delPlace.setText(Program.getLabel("Infos051") + "..."); // Supprimer...
		addWine.setText(Program.getLabel("Infos109")); // Ajouter...
		Aide.setText(Program.getLabel("Infos111")); // Aide Contextuelle...
		saveAs.setText(Program.getLabel("Infos371")); // Sauvegarder
		newFile.setText(Program.getLabel("Infos378"));
		openFile.setText(Program.getLabel("Infos372"));
		save.setText(Program.getLabel("Infos326"));
		showFile.setText(Program.getLabel("Infos324"));
		searchWine.setText(Program.getLabel("Infos006"));

		// sous
		parameter.setText(Program.getLabel("Infos156")); // Paramêtres
		about.setText(Program.getLabel("Infos199")); // A Propos
		news.setText(Program.getLabel("Infos330")); // Nouveautés
		tocreate.setText(Program.getLabel("Infos267")); // Rangement à créer
		history.setText(Program.getLabel("Infos341")); // Historique
		vignobles.setText(Program.getLabel("Infos165")); // Vignobles
		bottleCapacity.setText(Program.getLabel("Infos400")); // Contenance
		jMenuImportXmlPlaces.setText(Program.getLabel("Infos367")); // Importer
		// des
		// rangements
		// xml
		jMenuExportXmlPlaces.setText(Program.getLabel("Infos368")); // Exporter
		// des
		// rangements
		// xml
		jMenuExportXml.setText(Program.getLabel("Infos408")); // Exporter au
		// format xml
		jMenuCloseFile.setText(Program.getLabel("Infos019")); // Fermer...
		jMenuSetConfig.setText(Program.getLabel("Infos373")); // Modifier
		// les
		// paramètres...
		jMenuCheckUpdate.setText(Program.getLabel("Infos379")); // Vérifier
		// mise à jour...
		jMenuReopen1.setText("1 - " + Program.getShortFilename(Program.getGlobalConfigString("LAST_OPEN1", "")) + ".sinfo");
		jMenuReopen2.setText("2 - " + Program.getShortFilename(Program.getGlobalConfigString("LAST_OPEN2", "")) + ".sinfo");
		jMenuReopen3.setText("3 - " + Program.getShortFilename(Program.getGlobalConfigString("LAST_OPEN3", "")) + ".sinfo");
		jMenuReopen4.setText("4 - " + Program.getShortFilename(Program.getGlobalConfigString("LAST_OPEN4", "")) + ".sinfo");
		jMenuReopen1.setAccelerator(KeyStroke.getKeyStroke('1', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen2.setAccelerator(KeyStroke.getKeyStroke('2', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen3.setAccelerator(KeyStroke.getKeyStroke('3', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen4.setAccelerator(KeyStroke.getKeyStroke('4', InputEvent.CTRL_DOWN_MASK));
		jMenuReopen1.setToolTipText(Program.getGlobalConfigString("LAST_OPEN1", ""));
		jMenuReopen2.setToolTipText(Program.getGlobalConfigString("LAST_OPEN2", ""));
		jMenuReopen3.setToolTipText(Program.getGlobalConfigString("LAST_OPEN3", ""));
		jMenuReopen4.setToolTipText(Program.getGlobalConfigString("LAST_OPEN4", ""));

		jMenuCut.setText(Program.getLabel("Infos241"));
		jMenuCopy.setText(Program.getLabel("Infos242"));
		jMenuPaste.setText(Program.getLabel("Infos243"));

		m_oImporterButton.setText(Program.getLabel("Infos011")); // Importer
		m_oExportButton.setText(Program.getLabel("Infos125"));
		m_oCreerButton.setText(Program.getLabel("Infos010"));
		m_oStatsButton.setText(Program.getLabel("Infos009"));
		m_oManagePlaceButton.setText(Program.getLabel("Main.ManagePlace"));
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
	 * afficheFrame: Affiche la fenêtre principale
	 */
	private void afficheFrame() {
		if (m_bHasFrameBuilded) {
			// On ne recontruit que le menu Fichier pour remettre a jour la
			// liste des fichiers ouverts récement
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
			if (!Program.getGlobalConfigString("LAST_OPEN1", "").isEmpty()) {
				menuFile.addSeparator();
				menuFile.add(jMenuReopen1);
			}
			if (!Program.getGlobalConfigString("LAST_OPEN2", "").isEmpty()) {
				menuFile.add(jMenuReopen2);
			}
			if (!Program.getGlobalConfigString("LAST_OPEN3", "").isEmpty()) {
				menuFile.add(jMenuReopen3);
			}
			if (!Program.getGlobalConfigString("LAST_OPEN4", "").isEmpty()) {
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
		copyright.setText("Copyright Sébastien D.");
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
		if (!Program.getGlobalConfigString("LAST_OPEN1", "").isEmpty()) {
			menuFile.addSeparator();
			menuFile.add(jMenuReopen1);
		}
		if (!Program.getGlobalConfigString("LAST_OPEN2", "").isEmpty()) {
			menuFile.add(jMenuReopen2);
		}
		if (!Program.getGlobalConfigString("LAST_OPEN3", "").isEmpty()) {
			menuFile.add(jMenuReopen3);
		}
		if (!Program.getGlobalConfigString("LAST_OPEN4", "").isEmpty()) {
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
		// Ajouter la bar du menu à la frame
		setJMenuBar(m_oMenuBar);

		// Chargement du Frame
		if (!m_bHasListener) {
			setListeners();
		}

		if (bUpdateAvailable) {
			String sText = MessageFormat.format(Program.getLabel("Infos385"), Server.getInstance().getAvailableVersion(), MyCellarVersion.MAIN_VERSION + "-" + MyCellarVersion.VERSION);
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
				checkSelectedTab();
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
		tocreate.addActionListener((e) -> tocreate_actionPerformed());
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
		boolean foundArmoire = false;
		for (Rangement r : Program.getCave()) {
			if (!r.isCaisse()) {
				foundArmoire = true;
				break;
			}
		}
		m_oManagePlaceButton.setEnabled(foundArmoire);
	}

	/**
	 * menuSaveAs_actionPerformed: Sauvegarde sous la cave
	 */
	private void menuSaveAs_actionPerformed() {
		try {
			JFileChooser boiteFichier = new JFileChooser();
			boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
			int retour_jfc = boiteFichier.showSaveDialog(this);
			if (retour_jfc == JFileChooser.APPROVE_OPTION) {
				Program.setFileSavable(true);
				File nomFichier = boiteFichier.getSelectedFile();
				if (nomFichier == null) {
					setCursor(Cursor.getDefaultCursor());
					Erreur.showSimpleErreur(Program.getError("FileNotFound"));
					Debug("ERROR: menuSaveAs: File not found during Opening!");
					return;
				}
				String fic = nomFichier.getAbsolutePath();
				int index = fic.indexOf(".");
				if (index == -1) {
					fic = fic.concat(".sinfo");
				}

				setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Program.saveAs(fic);
				setCursor(Cursor.getDefaultCursor());
				fic = fic.replaceAll("\\\\", "/");
				int ind1 = fic.lastIndexOf("/");
				int ind2 = fic.indexOf(".sinfo");
				if(ind1 != -1 && ind2 != -1) {
					fic = fic.substring(ind1 + 1, ind2);
				}
				setTitle(Program.getLabel("Infos001") + " - [" + fic + "]");
				setEnabled(true);
			}
		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * menuSetConfig_actionPerformed: Modification des paramètres internes
	 */
	private void menuSetConfig_actionPerformed() {
		try {
			String type_objet[] = { "JTextField" };
			String titre = Program.getLabel("Infos374");
			String message1 = Program.getLabel("Infos375");
			String titre_properties[] = { "" };
			String default_value[] = { "" };
			String key_properties[] = { "" };
			MyOptions myoptions = new MyOptions(titre, message1, "", titre_properties, default_value, key_properties, type_objet, "",
					Program.getCaveConfig(), true, true);
			myoptions.setVisible(true);

		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * menuCheckUpdate_actionPerformed: Recherche de mises à jour
	 */
	private void menuCheckUpdate_actionPerformed() {
		if (Server.getInstance().hasAvailableUpdate()) {
			Erreur.showSimpleErreur(MessageFormat.format(Program.getLabel("Infos384"), Server.getInstance().getAvailableVersion(), MyCellarVersion.VERSION), true);
		} else {
			Erreur.showSimpleErreur(Program.getLabel("Infos388"), true);
		}
	}

	public void showBottle(Bouteille bottle, boolean edit) {
		for (int i = 0; i < Program.TABBED_PANE.getTabCount(); i++) {
			Component tab = Program.TABBED_PANE.getComponentAt(i);
			if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(bottle)) {
				Program.TABBED_PANE.setSelectedIndex(i);
				return;
			}
		}
		ManageBottle manage = new ManageBottle(bottle);
		manage.enableAll(edit);
		Program.TABBED_PANE.addTab(bottle.getNom(), MyCellarImage.WINE, manage);
		Program.TABBED_PANE.setSelectedIndex(Program.TABBED_PANE.getTabCount() - 1);
		Utils.addCloseButton(Program.TABBED_PANE, manage);
		updateMainPanel();
	}

	static void removeBottleTab(Bouteille bottle) {
		for (int i = 0; i < Program.TABBED_PANE.getTabCount(); i++) {
			Component tab = Program.TABBED_PANE.getComponentAt(i);
			if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(bottle)) {
				Program.TABBED_PANE.removeTabAt(i);
				return;
			}
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
				if (!title.endsWith("*"))
					Program.TABBED_PANE.setTitleAt(index, title + "*");
			} else {
				if (title.endsWith("*"))
					title = title.substring(0, title.length() - 1);
				Program.TABBED_PANE.setTitleAt(index, title);
			}
		}
	}

	/**
	 */
	private void checkSelectedTab() {
		if (Program.isSelectedTab(Program.addWine))
			Program.addWine.updateView();
		if (Program.isSelectedTab(Program.search))
			Program.search.updateView();
		if (Program.isSelectedTab(Program.deletePlace))
			Program.deletePlace.updateView();
		if (Program.isSelectedTab(Program.showfile)) {
			Program.showfile.refresh();
			Program.showfile.updateView();
		}
		if (Program.isSelectedTab(Program.showtrash)) {
			Program.showtrash.refresh();
			Program.showtrash.updateView();
		}
		if (Program.isSelectedTab(Program.showerrors)) {
			Program.showerrors.updateView();
		}
		if (Program.isSelectedTab(Program.stat))
			Program.stat.updateView();
		if (Program.isSelectedTab(Program.creer_tableau))
			Program.creer_tableau.updateView();
		if (Program.isSelectedTab(Program.chooseCell))
			Program.chooseCell.updateView();
		if (Program.isSelectedTab(Program.managePlace))
			Program.managePlace.updateView();
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
    			JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR", ""));
    			boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
    			int retour_jfc = boiteFichier.showOpenDialog(null);
    			if (retour_jfc == JFileChooser.APPROVE_OPTION) {
    				File file = boiteFichier.getSelectedFile();
    				if (file == null) {
    					setCursor(Cursor.getDefaultCursor());
    					Erreur.showSimpleErreur(Program.getError("FileNotFound"));
    					Debug("ERROR: OpenAction: File not found during Opening!");
    					return;
						}
    				String fic = file.getAbsolutePath();
    				int index = fic.indexOf(".");
    				if (index == -1) {
    					fic = fic.concat(".sinfo");
    				}
    				if (Program.openaFile(new File(fic))) {
							postOpenFile();
						}
    			}
			}catch(Exception e) {
				Program.showException(e);
			}
			finally {
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
			try {
				Debug("newFileAction: Creating a new file...");
    			if (Program.newFile()) {
    				postOpenFile();
    				Debug("newFileAction: Creating a new file OK");
    			}
			}catch(Exception e) {
				Program.showException(e);
			}
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
						if (Program.TABBED_PANE.getTitleAt(i).endsWith("*"))
							Program.TABBED_PANE.setSelectedIndex(i);
						if (!((ManageBottle) tab).save())
							return;
					}
				}
				setEnabled(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Program.save();
				this.setEnabled(true);
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
			if (Program.addWine == null) {
				try {
					Program.addWine = new AddVin();
					Program.TABBED_PANE.add(Program.getLabel("Infos005"), Program.addWine);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.WINE);
					Utils.addCloseButton(Program.TABBED_PANE, Program.addWine);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.addWine);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos005"), Program.addWine);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.WINE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.addWine);
				Program.TABBED_PANE.setSelectedComponent(Program.addWine);
			}
			Program.addWine.reInit();
			updateMainPanel();
		}
	}

	class AddPlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private AddPlaceAction() {
			super(Program.getLabel("Infos010"), MyCellarImage.PLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos010"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.createPlace == null) {
				try {
					Program.createPlace = new Creer_Rangement(false);
					Program.TABBED_PANE.add(Program.getLabel("Infos010"), Program.createPlace);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(Program.TABBED_PANE, Program.createPlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.createPlace);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos010"), Program.createPlace);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.createPlace);
				Program.TABBED_PANE.setSelectedComponent(Program.createPlace);
			}
			updateMainPanel();
		}
	}

	class DeletePlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private DeletePlaceAction() {
			super(Program.getLabel("Infos004"), MyCellarImage.DELPLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos004"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.deletePlace == null) {
				try {
					Program.deletePlace = new Supprimer_Rangement();
					Program.TABBED_PANE.add(Program.getLabel("Infos004"), Program.deletePlace);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.DELPLACE);
					Utils.addCloseButton(Program.TABBED_PANE, Program.deletePlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.deletePlace);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos004"), Program.deletePlace);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.DELPLACE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.deletePlace);
				Program.TABBED_PANE.setSelectedComponent(Program.deletePlace);
			}
			updateMainPanel();
		}
	}

	class ModifyPlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ModifyPlaceAction() {
			super(Program.getLabel("Infos007"), MyCellarImage.MODIFYPLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos007"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.modifyPlace == null) {
				try {
					Program.modifyPlace = new Creer_Rangement(true);
					Program.TABBED_PANE.add(Program.getLabel("Infos007"), Program.modifyPlace);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
					Utils.addCloseButton(Program.TABBED_PANE, Program.modifyPlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			Program.modifyPlace.updateView();
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.modifyPlace);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos007"), Program.modifyPlace);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.modifyPlace);
				Program.TABBED_PANE.setSelectedComponent(Program.modifyPlace);
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
			if (Program.search == null) {
				Program.search = new Search();
				Program.TABBED_PANE.add(Program.getLabel("Infos221"), Program.search);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(Program.TABBED_PANE, Program.search);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.search);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos221"), Program.search);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(Program.TABBED_PANE, Program.search);
				Program.TABBED_PANE.setSelectedComponent(Program.search);
			}
			checkSelectedTab();
			updateMainPanel();
		}
	}

	class CreateTabAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private CreateTabAction() {
			super(Program.getLabel("Infos008"), MyCellarImage.TABLE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos008"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.creer_tableau == null) {
				Program.creer_tableau = new Creer_Tableaux();
				Program.TABBED_PANE.add(Program.getLabel("Infos008"), Program.creer_tableau);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.creer_tableau);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.creer_tableau);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos008"), Program.creer_tableau);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.creer_tableau);
				Program.TABBED_PANE.setSelectedComponent(Program.creer_tableau);
			}
			checkSelectedTab();
			updateMainPanel();
		}
	}

	class ImportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ImportFileAction() {
			super(Program.getLabel("Infos011"), MyCellarImage.IMPORT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos011"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (Program.importer == null) {
				Program.importer = new Importer();
				Program.TABBED_PANE.add(Program.getLabel("Infos011"), Program.importer);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(Program.TABBED_PANE, Program.importer);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.importer);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos011"), Program.importer);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(Program.TABBED_PANE, Program.importer);
				Program.TABBED_PANE.setSelectedComponent(Program.importer);
			}
			checkSelectedTab();
			updateMainPanel();
		}
	}

	class ExportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		private ExportFileAction() {
			super(Program.getLabel("Infos125"), MyCellarImage.EXPORT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos125"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.export == null) {
				try {
					Program.export = new Export();
					Program.TABBED_PANE.add(Program.getLabel("Infos148"), Program.export);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.EXPORT);
					Utils.addCloseButton(Program.TABBED_PANE, Program.export);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.export);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos148"), Program.export);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.EXPORT);
				Utils.addCloseButton(Program.TABBED_PANE, Program.export);
				Program.TABBED_PANE.setSelectedComponent(Program.export);
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
			if (Program.stat == null) {
				try {
					Program.stat = new Stat();
					Program.stat.setVisible(true);
					Program.TABBED_PANE.add(Program.getLabel("Infos009"), Program.stat);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.STATS);
					Utils.addCloseButton(Program.TABBED_PANE, Program.stat);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.stat);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos009"), Program.stat);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.STATS);
				Utils.addCloseButton(Program.TABBED_PANE, Program.stat);
				Program.TABBED_PANE.setSelectedComponent(Program.stat);
			}
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
			if (Program.history == null) {
				try {
					Program.history = new ShowHistory();
					Program.TABBED_PANE.add(Program.getLabel("Infos341"), Program.history);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
					Utils.addCloseButton(Program.TABBED_PANE, Program.history);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.history);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos341"), Program.history);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
				Utils.addCloseButton(Program.TABBED_PANE, Program.history);
				Program.TABBED_PANE.setSelectedComponent(Program.history);
			}
			Program.history.refresh();
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
			if(Program.addWine != null)
				Program.addWine.updateView();
		}
	}

	public void openVineyardPanel() {
		if (Program.vignobles == null) {
			try {
				Program.vignobles = new VineyardPanel();
				Program.TABBED_PANE.add(Program.getLabel("Infos165"), Program.vignobles);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
				Utils.addCloseButton(Program.TABBED_PANE, Program.vignobles);
			} catch (Exception e1) {
				Program.showException(e1);
			}
		}
		try {
			Program.TABBED_PANE.setSelectedComponent(Program.vignobles);
		} catch (IllegalArgumentException e) {
			Program.TABBED_PANE.add(Program.getLabel("Infos165"), Program.vignobles);
			Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, null);
			Utils.addCloseButton(Program.TABBED_PANE, Program.vignobles);
			Program.TABBED_PANE.setSelectedComponent(Program.vignobles);
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
			if (Program.showfile == null) {
				Program.showfile = new ShowFile();
				Program.TABBED_PANE.add(Program.getLabel("Infos325"), Program.showfile);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(Program.TABBED_PANE, Program.showfile);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.showfile);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos325"), Program.showfile);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(Program.TABBED_PANE, Program.showfile);
				Program.TABBED_PANE.setSelectedComponent(Program.showfile);
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
			if (Program.showtrash == null) {
				Program.showtrash = new ShowFile(ShowType.TRASH);
				Program.TABBED_PANE.add(Program.getLabel("Main.ShowTrash"), Program.showtrash);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(Program.TABBED_PANE, Program.showtrash);
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.showtrash);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Main.ShowTrash"), Program.showtrash);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(Program.TABBED_PANE, Program.showtrash);
				Program.TABBED_PANE.setSelectedComponent(Program.showtrash);
			}
			Program.showtrash.refresh();
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
			if (Program.managePlace == null) {
				try {
					Program.managePlace = new CellarOrganizerPanel();
					Program.TABBED_PANE.add(Program.getLabel("Main.ManagePlace"), Program.managePlace);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(Program.TABBED_PANE, Program.managePlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.managePlace);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Main.ManagePlace"), Program.managePlace);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.managePlace);
				Program.TABBED_PANE.setSelectedComponent(Program.managePlace);
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
			if (Program.parametres == null) {
				try {
					Program.parametres = new Parametres();
					Program.TABBED_PANE.add(Program.getLabel("Infos193"), Program.parametres);
					Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PARAMETER);
					Utils.addCloseButton(Program.TABBED_PANE, Program.parametres);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.TABBED_PANE.setSelectedComponent(Program.parametres);
			} catch (IllegalArgumentException e) {
				Program.TABBED_PANE.add(Program.getLabel("Infos193"), Program.parametres);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PARAMETER);
				Utils.addCloseButton(Program.TABBED_PANE, Program.parametres);
				Program.TABBED_PANE.setSelectedComponent(Program.parametres);
			}
			updateMainPanel();
		}
	}
	
	public void openCellChooserPanel(IAddVin addvin) {
		if (Program.chooseCell == null) {
			try {
				Program.chooseCell = new CellarOrganizerPanel(true, addvin);
				Program.TABBED_PANE.add(Program.getLabel("Main.ChooseCell"), Program.chooseCell);
				Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.TABBED_PANE, Program.chooseCell);
			} catch (Exception e1) {
				Program.showException(e1);
			}
		}
		try {
			Program.TABBED_PANE.setSelectedComponent(Program.chooseCell);
			Program.chooseCell.setAddVin(addvin);
		} catch (IllegalArgumentException e) {
			Program.TABBED_PANE.add(Program.getLabel("Main.ChooseCell"), Program.chooseCell);
			Program.TABBED_PANE.setIconAt(Program.TABBED_PANE.getTabCount() - 1, MyCellarImage.PLACE);
			Utils.addCloseButton(Program.TABBED_PANE, Program.chooseCell);
			Program.TABBED_PANE.setSelectedComponent(Program.chooseCell);
		}
		updateMainPanel();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Program.showException(e, true);
	}
}
