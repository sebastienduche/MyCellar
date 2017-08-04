package mycellar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mycellar.actions.ExportPDFAction;
import mycellar.core.IAddVin;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarVersion;
import mycellar.launcher.Server;
import mycellar.showfile.ShowFile;
import mycellar.showfile.ShowFile.ShowType;
import net.miginfocom.swing.MigLayout;

/**
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Société : Seb Informatique
 * 
 * @author Sébastien Duché
 * @version 22.6
 * @since 04/08/17
 */
public class Start extends JFrame implements Thread.UncaughtExceptionHandler {

	private static JButton m_oSupprimerButton = new JButton();
	private static JButton m_oAjouterButton = new JButton();
	private static JButton m_oRechercherButton = new JButton();
	private static JButton m_oTableauxButton = new JButton();
	private static JButton m_oExportButton = new JButton();
	private static JButton m_oStatsButton = new JButton();
	private static JButton m_oManagePlaceButton = new JButton();
	private static MyCellarLabel copyright = new MyCellarLabel();
	private static MyCellarLabel update = new MyCellarLabel();
	private static String infos_version = " 2017 v";
	private static MyCellarLabel version = new MyCellarLabel("Mai" + infos_version + MyCellarVersion.mainVersion);
	private static JButton m_oCreerButton = new JButton();
	private static JButton m_oImporterButton = new JButton();
	private static JButton m_oModifierButton = new JButton();
	private static JButton m_oShowFileButton = new JButton();
	private static JButton m_oShowTrashButton = new JButton();
	private static JButton m_oCutButton = new JButton();
	private static JButton m_oCopyButton = new JButton();
	private static JButton m_oPasteButton = new JButton();
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
	private static JMenu menuFile = new JMenu();
	private static JMenu menuPlace = new JMenu();
	private static JMenu menuEdition = new JMenu();
	private static JMenu menuWine = new JMenu();
	private static JMenu menuAbout = new JMenu("?");
	public static JMenu menuTools = new JMenu();

	// differents choix de chaque menu
	private static JMenuItem importation = new JMenuItem();
	private static JMenuItem quit = new JMenuItem();
	private static JMenuItem exportation = new JMenuItem();
	private static JMenuItem statistiques = new JMenuItem();
	private static JMenuItem tableau = new JMenuItem();
	private static JMenuItem addPlace = new JMenuItem();
	private static JMenuItem modifPlace = new JMenuItem();
	private static JMenuItem delPlace = new JMenuItem();
	private static JMenuItem showFile = new JMenuItem();
	private static JMenuItem addWine = new JMenuItem();
	private static JMenuItem searchWine = new JMenuItem();
	private static JMenuItem Aide = new JMenuItem();
	private static JMenuItem param = new JMenuItem();
	private static JMenuItem about = new JMenuItem();
	private static JMenuItem tocreate = new JMenuItem();
	private static JMenuItem news = new JMenuItem();
	private static JMenuItem history = new JMenuItem();
	private static JMenuItem vignobles = new JMenuItem();
	private static JMenuItem bottleCapacity = new JMenuItem();
	private static JMenuItem newFile = new JMenuItem();
	private static JMenuItem save = new JMenuItem();
	private static JMenuItem saveAs = new JMenuItem();
	private static JMenuItem jMenuImportXmlPlaces = new JMenuItem();
	private static JMenuItem jMenuExportXmlPlaces = new JMenuItem();
	private static JMenuItem jMenuExportXml = new JMenuItem();
	private static JMenuItem openFile = new JMenuItem();
	private static JMenuItem jMenuCloseFile = new JMenuItem();
	private static JMenuItem jMenuSetConfig = new JMenuItem();
	static final long serialVersionUID = 501073;
	private static boolean m_bHasListener = false;
	private static boolean m_bHasFrameBuilded = false;
	private static JMenuItem jMenuReopen1 = new JMenuItem();
	private static JMenuItem jMenuReopen2 = new JMenuItem();
	private static JMenuItem jMenuReopen3 = new JMenuItem();
	private static JMenuItem jMenuReopen4 = new JMenuItem();
	private static JMenuItem jMenuCheckUpdate = new JMenuItem();
	private static JMenuItem jMenuCut = new JMenuItem();
	private static JMenuItem jMenuCopy = new JMenuItem();
	private static JMenuItem jMenuPaste = new JMenuItem();
	private static JButton buttonSave, buttonPdf;

	private Preferences prefs;

	/**
	 * Start: Constructeur pour démarrer l'application
	 */
	public Start() {
		try {
			startup();
		} catch (Exception e) {
			Program.showException(e);
		}
	}

	public static void main(String[] args) {

		final SplashScreen splashscreen = new SplashScreen();
		
		while (splashscreen.isRunning()) {
			;
		}
		// initialisation
		Program.init();

		// Lecture des paramètres
		// ______________________

		try {
			String parameters = "";
			for (int i = 0; i < args.length; i++) {
				parameters = parameters.concat(args[i] + " ");
			}
			int nIndex = parameters.indexOf("-opts=");
			if (nIndex == -1) {
				// démarrage sans options
				Program.archive = parameters.trim();
			} else {
				// démarrage avec options
				// ______________________
				String tmp = parameters.substring(0, nIndex);
				// Récupération du nom du fichier
				if (tmp.indexOf(".sinfo") != -1) {
					Program.archive = tmp.trim();
				} else {
					// On prend tous ce qu'il y a après -opts
					tmp = parameters.substring(nIndex);
					if (tmp.indexOf(".sinfo") != -1) {
						// Si l'on trouve l'extension du fichier
						// on cherche le caractère ' ' qui va séparer les
						// options du nom du fichier
						String tmp2 = tmp.trim();
						tmp2 = tmp2.substring(tmp2.indexOf(" "));
						Program.archive = tmp2.trim();
					}
				}
				// Récupération des options
				tmp = parameters.substring(nIndex + 6).trim();
				tmp = tmp.substring(0, tmp.indexOf(" ")).trim();
				// Options à gérer
				beforeStart(tmp);
			}
		} catch (Exception e) {
		}

		try {
			UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
				
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					Program.showException(e, true);
				}
			};
			Thread.setDefaultUncaughtExceptionHandler(handler);
			new Start();
		} catch (Exception e) {
			Program.showException(e);
		} catch (ExceptionInInitializerError a) {
			javax.swing.JOptionPane.showMessageDialog(null, "Error during program initialisation!!\nProgram files corrupted!!\nPlease reinstall program.",
					"Error", javax.swing.JOptionPane.ERROR_MESSAGE);
			System.exit(999);
		}
	}

	/**
	 * beforeStart Fonction pour exécuter quelques actions avant le démarrage
	 * 
	 * @param parameter
	 *            String
	 */
	private static void beforeStart(String parameter) {

		if (parameter.equals("restart")) {
			// Démarrage avec une nouvelle cave
			Program.putGlobalConfigString("STARTUP", "0");
			Program.putCaveConfigString("ANNEE_CTRL", "1");
			Program.putCaveConfigString("FILE_SRC", "");
			Program.putCaveConfigString("FIC_EXCEL", "0");
			Program.putCaveConfigString("SAVE", "KO");
		}

		if (parameter.equals("rebuild-stats")) {
			Program.putCaveConfigString("REBUILD_STATISTICS", "1");
		}

		if (parameter.equals("debug")) {
			Program.setDebug(true);
		}
	}

	/**
	 * jbInit: Fonction d'initialisation de l'application
	 * 
	 * @throws Exception
	 */
	private void startup() throws Exception {

		Debug("Starting MyCellar version: "+MyCellarVersion.version);
		Thread.currentThread().setUncaughtExceptionHandler(this);
		prefs = Preferences.userNodeForPackage(getClass());

		// Initialisation du mode Debug
		// ____________________________

		if (Program.getGlobalConfigInt("DEBUG", 0) == 1)
			Program.setDebug(true);

		// Contrôle des MAJ
		// Appel serveur pour alimenter la dernière version en ligne
		Server.getInstance().getServerVersion();

		Program.verifyConfigFile();

		// Démarrage
		// _________

		if (Program.archive.isEmpty() && Program.getGlobalConfigInt("STARTUP", 0) == 0) {
			// Language au premier démarrage
			String lang = System.getProperty("user.language");
			if(lang.equalsIgnoreCase("fr"))
				lang = "F";
			else
				lang = "U";
			Program.putGlobalConfigString("LANGUAGE", lang);
			
			updateFrame(true);
			Program.putGlobalConfigInt("STARTUP", 1);
		}

		// Paramètrage
		if (!Program.archive.isEmpty()) {
			loadFile();
		} else {
			updateFrame(false);
			afficheFrame();	
			enableAll(false);
		}
		this.setVisible(true);
	}

	/**
	 * Permet de charger un fichier sans avoir a recharger la Frame
	 */
	private void loadFile() {

		updateFrame(true);

		// Contruction de la Frame
		Debug("Showing Frame");
		afficheFrame();

		try {
			UIManager.setLookAndFeel(Program.getCaveConfigString("LOOK&FEEL", UIManager.getCrossPlatformLookAndFeelClassName()));
            for(Window window: Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
		} catch (Exception exc) {
		}

		if (Program.archive.isEmpty()) {
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
			Program.getCave().add(Program.defaultPlace);
		}
		enableAll(true);
	}

	/**
	 * Fonction pour desactiver ou activer toutes les options ou boutons
	 */
	public static void enableAll(boolean _bEnable) {
		jMenuCloseFile.setEnabled(_bEnable);
		m_oExportButton.setEnabled(_bEnable);
		m_oStatsButton.setEnabled(_bEnable);
		m_oManagePlaceButton.setEnabled(_bEnable);
		m_oTableauxButton.setEnabled(_bEnable);
		m_oSupprimerButton.setEnabled(_bEnable);
		m_oAjouterButton.setEnabled(_bEnable);
		m_oRechercherButton.setEnabled(_bEnable);
		exportation.setEnabled(_bEnable);
		statistiques.setEnabled(_bEnable);
		tableau.setEnabled(_bEnable);
		addWine.setEnabled(_bEnable);
		modifPlace.setEnabled(_bEnable);
		delPlace.setEnabled(_bEnable);
		searchWine.setEnabled(_bEnable);
		m_oModifierButton.setEnabled(_bEnable);
		m_oImporterButton.setEnabled(_bEnable);
		m_oShowFileButton.setEnabled(_bEnable);
		m_oShowTrashButton.setEnabled(_bEnable);
		importation.setEnabled(_bEnable);
		m_oCreerButton.setEnabled(_bEnable);
		save.setEnabled(Program.isFileSavable());
		buttonSave.setEnabled(Program.isFileSavable());
		buttonPdf.setEnabled(_bEnable);
		saveAs.setEnabled(_bEnable);
		addPlace.setEnabled(_bEnable);
		jMenuExportXmlPlaces.setEnabled(_bEnable);
		jMenuImportXmlPlaces.setEnabled(_bEnable);
		jMenuExportXml.setEnabled(_bEnable);
		showFile.setEnabled(_bEnable);
		tocreate.setEnabled(_bEnable);
		history.setEnabled(_bEnable);
		vignobles.setEnabled(_bEnable);
		bottleCapacity.setEnabled(_bEnable);
		param.setEnabled(_bEnable);
		jMenuCut.setEnabled(_bEnable);
		jMenuCopy.setEnabled(_bEnable);
		jMenuPaste.setEnabled(_bEnable);
		m_oCutButton.setEnabled(_bEnable);
		m_oCopyButton.setEnabled(_bEnable);
		m_oPasteButton.setEnabled(_bEnable);
	}

	/**
	 * quitter_actionPerformed: Fonction appellé lorsque l'on quitte le
	 * programme.
	 * 
	 * @param e ActionEvent
	 */
	void quitter_actionPerformed() {
		for(Component c : Program.tabbedPane.getComponents()) {
			if(c instanceof ITabListener) {
				if(!((ITabListener) c).tabWillClose(null))
					return;
			}
		}
		Start.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Program.closeFile();
		prefs.putInt("Start.x", getLocation().x);
		prefs.putInt("Start.y", getLocation().y);
		prefs.putInt("Start.width", getSize().width);
		prefs.putInt("Start.height", getSize().height);
		Start.this.setCursor(Cursor.getDefaultCursor());
		this.dispose();
		Program.deleteTempFiles();
		Program.cleanDebugFiles();
		Debug("MyCellar End");
		System.exit(0);
	}

	/**
	 * about_actionPerformed: Appelle la fenêtre d'A Propos.
	 */
	void about_actionPerformed() {
		try {
			new APropos().setVisible(true);
		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * news_actionPerformed: Affiche les nouveautés de la release
	 */
	void news_actionPerformed() {
		Program.open(new File("Finish.html"));
	}

	/**
	 * tocreate_actionPerformed: Appelle la fenêtre de Bienvenue.
	 */
	void tocreate_actionPerformed() {
		RangementUtils.findRangementToCreate();
	}

	/**
	 * parametre_actionPerformed: Appelle la fenêtre des paramètres.
	 */
	void parametre_actionPerformed() {
		try {
			Program.parametres = new Parametres();
			Program.parametres.setVisible(true);
			Program.parametres = null;
		} catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * importXmlPlace_actionPerformed: Permet d'importer une liste de rangement
	 * au format xml
	 */
	void importXmlPlace_actionPerformed() {
		JFileChooser boiteFichier = new JFileChooser();
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		int retour_jfc = boiteFichier.showOpenDialog(this);
		File nomFichier = new File("");
		String fic = "";
		if (retour_jfc == JFileChooser.APPROVE_OPTION) {
			nomFichier = boiteFichier.getSelectedFile();
			fic = nomFichier.getAbsolutePath();
			int index = fic.indexOf(".");
			if (index == -1) {
				fic = fic.concat(".xml");
			}
			File f = new File(fic);
			LinkedList<Rangement> cave = null;
			if (f.exists()) {
				cave = MyXmlDom.readMyCellarXml(fic);
			}
			if (cave != null) {
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
		Program.panelInfos.setEnable(true);
		Program.panelInfos.refresh();
		String tmp = Program.getShortFilename();
		if (tmp.isEmpty())
			this.setTitle(Program.getLabel("Infos001"));
		else
			this.setTitle(Program.getLabel("Infos001") + " - [" + tmp + "]");
	}

	/**
	 * Ouverture d'un fichier déjà référencé
	 * 
	 * @param sFile
	 */
	private void reOpenFile(String sFile) {
		try{
    		enableAll(false);
    		Start.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    		if (!sFile.isEmpty() && Program.openaFile(new File(sFile)))
    			postOpenFile();
    		else
    			enableAll(false);
		}catch(Exception e) {
			Program.showException(e);
		}
		finally {
			Start.this.setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * reopen1_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	void reopen1_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN1", "");
		Debug("Reopen1FileAction: Restart with file " + sFile);
		reOpenFile(sFile);
	}

	/**
	 * reopen2_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	void reopen2_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN2", "");
		Debug("Reopen2FileAction: Restart with file " + sFile);
		reOpenFile(sFile);
	}

	/**
	 * reopen3_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	void reopen3_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN3", "");
		Debug("Reopen3FileAction: Restart with file " + sFile);
		reOpenFile(sFile);
	}

	/**
	 * reopen4_actionPerformed: Ouvre un fichier précédement ouvert
	 */
	void reopen4_actionPerformed() {
		String sFile = Program.getGlobalConfigString("LAST_OPEN4", "");
		Debug("Reopen4FileAction: Restart with file " + sFile);
		reOpenFile(sFile);
	}

	/**
	 * closeFile_actionPerformed: Ferme un fichier
	 */
	void closeFile_actionPerformed() {
		Start.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Program.closeFile();
		enableAll(false);
		Program.panelInfos.setEnable(false);
		Program.panelInfos.refresh();
		updateMainPanel();
		Start.this.setCursor(Cursor.getDefaultCursor());
		this.setTitle(Program.getLabel("Infos001"));
	}

	/**
	 * exportXmlPlace_actionPerformed: Permet d'exporter la liste des rangements
	 * au format xml
	 */
	void exportXmlPlace_actionPerformed() {
		JFileChooser boiteFichier = new JFileChooser();
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			String fic = nomFichier.getAbsolutePath();
			int index = fic.indexOf(".");
			if (index == -1) {
				fic = fic.concat(".xml");
			}
			File f = new File(fic);
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					Program.showException(e);
				}
			}
			MyXmlDom.writeMyCellarXml(Program.getCave(), fic);
		}
	}

	/**
	 * exportXml_actionPerformed: Permet d'exporter la liste des vins au format
	 * xml
	 */
	void exportXml_actionPerformed() {
		JFileChooser boiteFichier = new JFileChooser();
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			String fic = nomFichier.getAbsolutePath();
			int index = fic.indexOf(".");
			if (index == -1) {
				fic = fic.concat(".xml");
			}
			File f = new File(fic);
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					Program.showException(e);
				}
			}
			ListeBouteille.writeXML(f);
		}
	}

	/**
	 * this_windowActivated: Mis au premier plan des boites de dialogue.
	 * 
	 * @param e
	 *            WindowEvent
	 */
	void this_windowActivated(WindowEvent e) {

		if (Program.parametres != null) {
			Program.parametres.toFront();
		}
		if (Program.Morehistory != null) {
			Program.Morehistory.toFront();
		}

	}

	/**
	 * updateFrame: Met à jour tous les champs avec la langue sélectionnée. Met
	 * à jour tous les paramêtres suite au chargement d'un fichier
	 * 
	 * @param toverify
	 *            boolean
	 */
	public void updateFrame(boolean toverify) {

		try {

			boolean bHasVersion = false;
			if (null != Program.getCaveConfig() && Program.hasConfigCaveKey("VERSION")) {
				bHasVersion = true;
			} else if (Program.hasConfigGlobalKey("VERSION")) {
				bHasVersion = true;
			}

			String thelangue = Program.getGlobalConfigString("LANGUAGE", "F");
			if (!bHasVersion || toverify == true) {
				Program.initConf();
				if (!bHasVersion) {
					Program.putCaveConfigString("FILE_SRC", "MyCellar.sinfo");
				}
			}
			Program.setLanguage(thelangue);
			try {
				QUITTER = Program.getLabel("QUITTER").charAt(0);
			} catch (NullPointerException npe) {
				thelangue = "F";
				Program.setLanguage(thelangue);
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
			param.setText(Program.getLabel("Infos156")); // Paramêtres
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
			jMenuReopen1.setAccelerator(KeyStroke.getKeyStroke('1', ActionEvent.CTRL_MASK));
			jMenuReopen2.setAccelerator(KeyStroke.getKeyStroke('2', ActionEvent.CTRL_MASK));
			jMenuReopen3.setAccelerator(KeyStroke.getKeyStroke('3', ActionEvent.CTRL_MASK));
			jMenuReopen4.setAccelerator(KeyStroke.getKeyStroke('4', ActionEvent.CTRL_MASK));
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
			version.setText(Program.getLabel("Infos296") + infos_version + MyCellarVersion.mainVersion);
			addWine.setAccelerator(KeyStroke.getKeyStroke(AJOUTERV, ActionEvent.CTRL_MASK));
			addPlace.setAccelerator(KeyStroke.getKeyStroke(AJOUTERR, ActionEvent.CTRL_MASK));
			delPlace.setAccelerator(KeyStroke.getKeyStroke(SUPPR, ActionEvent.CTRL_MASK));
			history.setAccelerator(KeyStroke.getKeyStroke(HISTORY, ActionEvent.CTRL_MASK));
			tableau.setAccelerator(KeyStroke.getKeyStroke(TABLEAUX, ActionEvent.CTRL_MASK));
			statistiques.setAccelerator(KeyStroke.getKeyStroke(STAT, ActionEvent.CTRL_MASK));
			importation.setAccelerator(KeyStroke.getKeyStroke(IMPORT, ActionEvent.CTRL_MASK));
			exportation.setAccelerator(KeyStroke.getKeyStroke(EXPORT, ActionEvent.CTRL_MASK));
			modifPlace.setAccelerator(KeyStroke.getKeyStroke(MODIF, ActionEvent.CTRL_MASK));
			quit.setAccelerator(KeyStroke.getKeyStroke(QUITTER, ActionEvent.CTRL_MASK));
			SwingUtilities.updateComponentTreeUI(this);
			String tmp = Program.getShortFilename();
			Program.defaultPlace.setNom(Program.getLabel("Program.DefaultPlace"));
			if (tmp.isEmpty())
				this.setTitle(Program.getLabel("Infos001"));
			else
				this.setTitle(Program.getLabel("Infos001") + " - [" + tmp + "]");
			Debug("Loading Frame ended");
		} catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * aide_actionPerformed: Aide
	 */
	void aide_actionPerformed() {
		Program.getAide();
	}

	/**
	 * afficheFrame: Affiche la fenêtre principale
	 */
	void afficheFrame() {

		NewAction newAction = new NewAction("Infos378", MyCellarImage.NEW, "Infos378", null);
		OpenAction openAction = new OpenAction("Infos372", MyCellarImage.OPEN, "Infos372", null);
		SaveAction saveAction = new SaveAction("Infos326", MyCellarImage.SAVE, "Infos326", null);
		SaveAsAction saveAsAction = new SaveAsAction("Infos371", MyCellarImage.SAVEAS, "Infos371", null);
		SearchAction searchAction = new SearchAction("Infos006", MyCellarImage.SEARCH, "Infos006", null);
		CutAction cutAction = new CutAction();
		CopyAction copyAction = new CopyAction();
		PasteAction pasteAction = new PasteAction();

		if (Program.panelInfos == null)
			Program.panelInfos = new PanelInfos();

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
			if (!Program.getGlobalConfigString("LAST_OPEN2", "").isEmpty())
				menuFile.add(jMenuReopen2);
			if (!Program.getGlobalConfigString("LAST_OPEN3", "").isEmpty())
				menuFile.add(jMenuReopen3);
			if (!Program.getGlobalConfigString("LAST_OPEN4", "").isEmpty())
				menuFile.add(jMenuReopen4);
			menuFile.addSeparator();
			menuFile.add(quit);
			return;
		}

		String tmp = Program.archive;
		tmp = tmp.replaceAll("\\\\", "/");
		int ind1 = tmp.lastIndexOf("/");
		int ind2 = tmp.indexOf(".sinfo");
		try {
			tmp = tmp.substring(ind1 + 1, ind2);
		} catch (Exception ex) {
		}
		if (tmp.isEmpty())
			this.setTitle(Program.getLabel("Infos001"));
		else
			this.setTitle(Program.getLabel("Infos001") + " - [" + tmp + "]");
		this.setResizable(true);
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = prefs.getInt("Start.x", -1);
		int y = prefs.getInt("Start.y", -1);
		if (x >= 0 && y >= 0)
			setLocation(x, y);
		else
			this.setLocation(0, 0);
		int w = prefs.getInt("Start.width", -1);
		int h = prefs.getInt("Start.height", -1);
		if (w >= 0 && h >= 0)
			setSize(w, h);
		else
			setSize(screenSize.width, screenSize.height);
		this.setLayout(new MigLayout("", "[grow]", "[grow][][]"));

		copyright.setFont(new Font("Dialog", 0, 10));
		copyright.setText("Copyright Sébastien D.");
		version.setFont(new Font("Dialog", 0, 10));
		update.setFont(new Font("Dialog", 0, 10));
		add(Program.tabbedPane, "grow, hidemode 3, wrap");
		add(Program.panelInfos, "grow, hidemode 3, wrap");
		add(update, "wrap");
		add(copyright, "align right, gapright 10, wrap");
		add(version, "align right, gapright 10, gapbottom 10 ");
		Program.tabbedPane.setVisible(false);

		JToolBar toolBar = new JToolBar();
		AddWineAction addWineAction = new AddWineAction("Infos005", MyCellarImage.WINE, "Infos005", null);
		m_oAjouterButton = new JButton(addWineAction);
		addWine = new JMenuItem(addWineAction);
		JButton newButton = new JButton(newAction);
		newButton.setText("");
		JButton openButton = new JButton(openAction);
		openButton.setText("");
		buttonSave = new JButton(saveAction);
		buttonSave.setText("");
		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.add(buttonSave);
		buttonPdf = new JButton(new ExportPDFAction());
		buttonPdf.setText("");
		toolBar.add(buttonPdf);
		toolBar.addSeparator();
		m_oCutButton = new JButton(new CutAction());
		m_oCopyButton = new JButton(new CopyAction());
		m_oPasteButton = new JButton(new PasteAction());
		m_oCutButton.setText("");
		m_oCopyButton.setText("");
		m_oPasteButton.setText("");
		toolBar.add(m_oCutButton);
		toolBar.add(m_oCopyButton);
		toolBar.add(m_oPasteButton);
		toolBar.addSeparator();
		toolBar.add(m_oAjouterButton);
		m_oRechercherButton = new JButton(searchAction);
		toolBar.add(m_oRechercherButton);
		AddPlaceAction addPlaceAction = new AddPlaceAction("Infos010", MyCellarImage.PLACE, "Infos010", null);
		m_oCreerButton = new JButton(addPlaceAction);
		ModifyPlaceAction modifyPlaceAction = new ModifyPlaceAction("Infos007", MyCellarImage.MODIFYPLACE, "Infos007", null);
		m_oModifierButton = new JButton(modifyPlaceAction);
		DeletePlaceAction deletePlaceAction = new DeletePlaceAction("Infos004", MyCellarImage.DELPLACE, "Infos004", null);
		m_oSupprimerButton = new JButton(deletePlaceAction);
		ShowFileAction showFileAction = new ShowFileAction("Infos324", MyCellarImage.SHOW, "Infos324", null);
		m_oShowFileButton = new JButton(showFileAction);
		toolBar.add(m_oShowFileButton);
		CreateTabAction createTabAction = new CreateTabAction("Infos008", MyCellarImage.TABLE, "Infos008", null);
		m_oTableauxButton = new JButton(createTabAction);
		toolBar.add(m_oTableauxButton);
		StatAction statAction = new StatAction("Infos009", MyCellarImage.STATS, "Infos009", null);
		m_oStatsButton = new JButton(statAction);
		toolBar.add(m_oStatsButton);
		ImportFileAction importFileAction = new ImportFileAction("Infos011", MyCellarImage.IMPORT, "Infos011", null);
		m_oImporterButton = new JButton(importFileAction);
		ExportFileAction exportFileAction = new ExportFileAction("Infos125", MyCellarImage.EXPORT, "Infos125", null);
		m_oExportButton = new JButton(exportFileAction);
		toolBar.add(m_oExportButton);
		ManagePlaceAction managePlaceAction = new ManagePlaceAction(null);
		m_oManagePlaceButton = new JButton(managePlaceAction);
		toolBar.add(m_oManagePlaceButton);
		toolBar.add(Box.createHorizontalGlue());
		ShowTrashAction showTrashAction = new ShowTrashAction();
		m_oShowTrashButton = new JButton(showTrashAction);
		toolBar.add(m_oShowTrashButton);
		toolBar.setFloatable(true);
		add(toolBar, BorderLayout.NORTH);

		this.setIconImage(MyCellarImage.ICON.getImage());

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
		menuFile.add(newFile = new JMenuItem(newAction));
		menuFile.add(openFile = new JMenuItem(openAction));
		menuFile.add(jMenuCloseFile);
		menuFile.addSeparator();
		menuFile.add(save = new JMenuItem(saveAction));
		menuFile.add(saveAs = new JMenuItem(saveAsAction));
		menuFile.addSeparator();
		menuFile.add(importation = new JMenuItem(importFileAction));
		menuFile.add(exportation = new JMenuItem(exportFileAction));
		menuFile.addSeparator();
		menuFile.add(statistiques = new JMenuItem(statAction));
		menuFile.add(tableau = new JMenuItem(createTabAction));
		menuFile.add(showFile = new JMenuItem(showFileAction));
		if (!Program.getGlobalConfigString("LAST_OPEN1", "").isEmpty()) {
			menuFile.addSeparator();
			menuFile.add(jMenuReopen1);
		}
		if (!Program.getGlobalConfigString("LAST_OPEN2", "").isEmpty())
			menuFile.add(jMenuReopen2);
		if (!Program.getGlobalConfigString("LAST_OPEN3", "").isEmpty())
			menuFile.add(jMenuReopen3);
		if (!Program.getGlobalConfigString("LAST_OPEN4", "").isEmpty())
			menuFile.add(jMenuReopen4);
		menuFile.addSeparator();
		menuFile.add(quit);
		menuPlace.add(addPlace = new JMenuItem(addPlaceAction));
		menuPlace.add(modifPlace = new JMenuItem(modifyPlaceAction));
		menuPlace.add(delPlace = new JMenuItem(deletePlaceAction));
		menuWine.add(addWine = new JMenuItem(addWineAction));
		menuWine.add(searchWine = new JMenuItem(searchAction));
		menuEdition.add(jMenuCut = new JMenuItem(cutAction));
		menuEdition.add(jMenuCopy = new JMenuItem(copyAction));
		menuEdition.add(jMenuPaste = new JMenuItem(pasteAction));
		menuAbout.add(Aide);
		menuAbout.addSeparator();
		menuAbout.add(jMenuCheckUpdate);
		menuAbout.addSeparator();
		menuAbout.add(news);
		menuTools.add(param);
		menuTools.add(vignobles);
		menuTools.add(bottleCapacity);
		menuTools.add(history);
		menuTools.add(tocreate);
		menuTools.add(jMenuImportXmlPlaces);
		menuTools.add(jMenuExportXmlPlaces);
		menuTools.add(jMenuExportXml);
		menuTools.add(jMenuSetConfig);
		menuAbout.add(about);
		addWine.setAccelerator(KeyStroke.getKeyStroke(AJOUTERV, ActionEvent.CTRL_MASK));
		addPlace.setAccelerator(KeyStroke.getKeyStroke(AJOUTERR, ActionEvent.CTRL_MASK));
		delPlace.setAccelerator(KeyStroke.getKeyStroke(SUPPR, ActionEvent.CTRL_MASK));
		showFile.setAccelerator(KeyStroke.getKeyStroke(VISUAL, ActionEvent.CTRL_MASK));
		history.setAccelerator(KeyStroke.getKeyStroke(HISTORY, ActionEvent.CTRL_MASK));
		searchWine.setAccelerator(KeyStroke.getKeyStroke(RECHERCHE, ActionEvent.CTRL_MASK));
		tableau.setAccelerator(KeyStroke.getKeyStroke(TABLEAUX, ActionEvent.CTRL_MASK));
		statistiques.setAccelerator(KeyStroke.getKeyStroke(STAT, ActionEvent.CTRL_MASK));
		importation.setAccelerator(KeyStroke.getKeyStroke(IMPORT, ActionEvent.CTRL_MASK));
		exportation.setAccelerator(KeyStroke.getKeyStroke(EXPORT, ActionEvent.CTRL_MASK));
		modifPlace.setAccelerator(KeyStroke.getKeyStroke(MODIF, ActionEvent.CTRL_MASK));
		quit.setAccelerator(KeyStroke.getKeyStroke(QUITTER, ActionEvent.CTRL_MASK));
		save.setAccelerator(KeyStroke.getKeyStroke(SAVE, ActionEvent.CTRL_MASK));
		newFile.setAccelerator(KeyStroke.getKeyStroke(NEW, ActionEvent.CTRL_MASK));
		openFile.setAccelerator(KeyStroke.getKeyStroke('O', ActionEvent.CTRL_MASK));
		jMenuCut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
		jMenuCopy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
		jMenuPaste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));
		// Ajouter les menu sur la bar de menu
		m_oMenuBar.add(menuFile);
		m_oMenuBar.add(menuEdition);
		m_oMenuBar.add(menuPlace);
		m_oMenuBar.add(menuWine);
		m_oMenuBar.add(menuTools);
		m_oMenuBar.add(menuAbout);
		// Ajouter la bar du menu à la frame
		this.setJMenuBar(m_oMenuBar);

		// Chargement du Frame
		if (!m_bHasListener)
			setListeners();

		if (bUpdateAvailable) {
			String sText = MessageFormat.format(Program.getLabel("Infos385"), Server.getInstance().getAvailableVersion(), MyCellarVersion.mainVersion + "-" + MyCellarVersion.version);
			update.setText(sText);
		}
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		m_bHasFrameBuilded = true;

		try {
			UIManager.setLookAndFeel(Program.getCaveConfigString("LOOK&FEEL", UIManager.getCrossPlatformLookAndFeelClassName()));
			for(Window window: Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
		} catch (Exception exc) {
		}
		Debug("Display Frame ended");
	}

	private void setListeners() {
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				this_windowActivated(e);
			}

			public void windowClosing(WindowEvent e) {
				quitter_actionPerformed();
			}
		});

		Program.tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				checkSelectedTab();
				Program.tabbedPane.getSelectedComponent();
			}

		});

		quit.addActionListener((e) -> quitter_actionPerformed());
		about.addActionListener((e) -> about_actionPerformed());
		param.addActionListener((e) -> parametre_actionPerformed());
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
	public static void Debug(String sText) {
		Program.Debug("Start: " + sText);
	}

	public static void updateMainPanel() {
		Debug("updateMainPanel: Trying to display panelInfos...");
		int count = Program.tabbedPane.getTabCount();
		Program.panelInfos.setVisible(count == 0);
		Program.tabbedPane.setVisible(count > 0);
		if (count == 0)
			Program.panelInfos.refresh();
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
	void menuSaveAs_actionPerformed() {
		try {
			JFileChooser boiteFichier = new JFileChooser();
			boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
			int retour_jfc = boiteFichier.showSaveDialog(this);
			if (retour_jfc == JFileChooser.APPROVE_OPTION) {
				Program.setFileSavable(true);
				File nomFichier = boiteFichier.getSelectedFile();
				String fic = nomFichier.getAbsolutePath();
				int index = fic.indexOf(".");
				if (index == -1) {
					fic = fic.concat(".sinfo");
				}

				this.setEnabled(false);
				Start.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Program.saveAs(fic);
				Start.this.setCursor(Cursor.getDefaultCursor());
				Program.archive = fic;
				fic = Program.archive;
				fic = fic.replaceAll("\\\\", "/");
				int ind1 = fic.lastIndexOf("/");
				int ind2 = fic.indexOf(".sinfo");
				try {
					fic = fic.substring(ind1 + 1, ind2);
				} catch (Exception ex) {
				}
				this.setTitle(Program.getLabel("Infos001") + " - [" + fic + "]");
				this.setEnabled(true);
			}
		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * menuSetConfig_actionPerformed: Modification des paramètres internes
	 */
	void menuSetConfig_actionPerformed() {
		try {
			String type_objet[] = { "JTextField" };
			String titre = Program.getLabel("Infos374");
			String message1 = Program.getLabel("Infos375");
			String message2 = "";
			String titre_properties[] = { "" };
			String default_value[] = { "" };
			String key_properties[] = { "" };
			String erreur = "";
			MyOptions myoptions = new MyOptions(titre, message1, message2, titre_properties, default_value, key_properties, type_objet, erreur,
					Program.getCaveConfig(), true, true);
			myoptions.setVisible(true);

		} catch (Exception e3) {
			Program.showException(e3);
		}
	}

	/**
	 * menuCheckUpdate_actionPerformed: Recherche de mises à jour
	 */
	void menuCheckUpdate_actionPerformed() {
		if (Server.getInstance().hasAvailableUpdate()) {
			new Erreur(MessageFormat.format(Program.getLabel("Infos384"), Server.getInstance().getAvailableVersion(), MyCellarVersion.version), true);
		} else {
			new Erreur(Program.getLabel("Infos388"), true);
		}
	}

	public static void showBottle(Bouteille bottle) {
		for (int i = 0; i < Program.tabbedPane.getTabCount(); i++) {
			Component tab = Program.tabbedPane.getComponentAt(i);
			if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(bottle)) {
				Program.tabbedPane.setSelectedIndex(i);
				return;
			}
		}
		ManageBottle manage = new ManageBottle(bottle);
		Program.tabbedPane.addTab(bottle.getNom(), MyCellarImage.WINE, manage);
		Program.tabbedPane.setSelectedIndex(Program.tabbedPane.getTabCount() - 1);
		Utils.addCloseButton(Program.tabbedPane, manage);
	}

	public static void removeBottleTab(Bouteille bottle) {
		for (int i = 0; i < Program.tabbedPane.getTabCount(); i++) {
			Component tab = Program.tabbedPane.getComponentAt(i);
			if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(bottle)) {
				Program.tabbedPane.removeTabAt(i);
				return;
			}
		}
	}
	
	public static void removeCurrentTab() {
		Program.tabbedPane.removeTabAt(Program.tabbedPane.getSelectedIndex());
		updateMainPanel();
	}

	public static void setPaneModified(boolean modify) {
		if (Program.tabbedPane.getSelectedComponent() != null) {
			int index = Program.tabbedPane.getSelectedIndex();
			String title = Program.tabbedPane.getTitleAt(index);
			if (modify) {
				if (title.charAt(title.length() - 1) != '*')
					Program.tabbedPane.setTitleAt(index, title + "*");
			} else {
				if (title.charAt(title.length() - 1) == '*')
					title = title.substring(0, title.length() - 1);
				Program.tabbedPane.setTitleAt(index, title);
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

		public OpenAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
    			Start.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    			JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR", ""));
    			boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_SINFO);
    			int retour_jfc = boiteFichier.showOpenDialog(null);
    			File nomFichier = new File("");
    			String fic = "";
    			if (retour_jfc == JFileChooser.APPROVE_OPTION) {
    				nomFichier = boiteFichier.getSelectedFile();
    				fic = nomFichier.getAbsolutePath();
    				int index = fic.indexOf(".");
    				if (index == -1) {
    					fic = fic.concat(".sinfo");
    				}
    				if (Program.openaFile(new File(fic)))
    					postOpenFile();
    			}
			}catch(Exception e) {
				Program.showException(e);
			}
			finally {
				Start.this.setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	class NewAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public NewAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		public NewAction() {
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
    			if (Program.newFile()) {
    				Debug("newFileAction: Restart with new file...");
    				postOpenFile();
    			}
			}catch(Exception e) {
				Program.showException(e);
			}
		}
	}

	class SaveAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public SaveAction() {
			
		}
		public SaveAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.getShortFilename().startsWith("Untitled")) {
				menuSaveAs_actionPerformed();
				return;
			}
			try {
				for (int i = 0; i < Program.tabbedPane.getTabCount(); i++) {
					Component tab = Program.tabbedPane.getComponentAt(i);
					if (tab instanceof ManageBottle) {
						if (Program.tabbedPane.getTitleAt(i).endsWith("*"))
							Program.tabbedPane.setSelectedIndex(i);
						if (!((ManageBottle) tab).save())
							return;
					}
				}
				this.setEnabled(false);
				Start.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Program.save();
				this.setEnabled(true);
			} catch (Exception e3) {
				Program.showException(e3);
			} finally {
				Start.this.setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	class SaveAsAction extends AbstractAction {
		private static final long serialVersionUID = -2340786091568284033L;

		public SaveAsAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			menuSaveAs_actionPerformed();
		}
	}

	class AddWineAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public AddWineAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.addWine == null) {
				try {
					Program.addWine = new AddVin();
					Program.tabbedPane.add(Program.getLabel("Infos005"), Program.addWine);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.WINE);
					Utils.addCloseButton(Program.tabbedPane, Program.addWine);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.addWine);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos005"), Program.addWine);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.WINE);
				Utils.addCloseButton(Program.tabbedPane, Program.addWine);
				Program.tabbedPane.setSelectedComponent(Program.addWine);
			}
			updateMainPanel();
		}
	}

	class AddPlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public AddPlaceAction() {
			
		}
		public AddPlaceAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.createPlace == null) {
				try {
					Program.createPlace = new Creer_Rangement(false);
					Program.tabbedPane.add(Program.getLabel("Infos010"), Program.createPlace);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(Program.tabbedPane, Program.createPlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.createPlace);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos010"), Program.createPlace);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.tabbedPane, Program.createPlace);
				Program.tabbedPane.setSelectedComponent(Program.createPlace);
			}
			updateMainPanel();
		}
	}

	class DeletePlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public DeletePlaceAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.deletePlace == null) {
				try {
					Program.deletePlace = new Supprimer_Rangement();
					Program.tabbedPane.add(Program.getLabel("Infos004"), Program.deletePlace);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.DELPLACE);
					Utils.addCloseButton(Program.tabbedPane, Program.deletePlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.deletePlace);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos004"), Program.deletePlace);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.DELPLACE);
				Utils.addCloseButton(Program.tabbedPane, Program.deletePlace);
				Program.tabbedPane.setSelectedComponent(Program.deletePlace);
			}
			updateMainPanel();
		}
	}

	class ModifyPlaceAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public ModifyPlaceAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.modifyPlace == null) {
				try {
					Program.modifyPlace = new Creer_Rangement(true);
					Program.tabbedPane.add(Program.getLabel("Infos007"), Program.modifyPlace);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
					Utils.addCloseButton(Program.tabbedPane, Program.modifyPlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			Program.modifyPlace.updateView();
			try {
				Program.tabbedPane.setSelectedComponent(Program.modifyPlace);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos007"), Program.modifyPlace);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.MODIFYPLACE);
				Utils.addCloseButton(Program.tabbedPane, Program.modifyPlace);
				Program.tabbedPane.setSelectedComponent(Program.modifyPlace);
			}
			updateMainPanel();
		}
	}

	class SearchAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public SearchAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.search == null) {
				Program.search = new Search();
				Program.tabbedPane.add(Program.getLabel("Infos221"), Program.search);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(Program.tabbedPane, Program.search);
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.search);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos221"), Program.search);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.SEARCH);
				Utils.addCloseButton(Program.tabbedPane, Program.search);
				Program.tabbedPane.setSelectedComponent(Program.search);
			}
			checkSelectedTab();
			updateMainPanel();
		}
	}

	class CreateTabAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public CreateTabAction() {
		}

		public CreateTabAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.creer_tableau == null) {
				Program.creer_tableau = new Creer_Tableaux();
				Program.tabbedPane.add(Program.getLabel("Infos008"), Program.creer_tableau);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(Program.tabbedPane, Program.creer_tableau);
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.creer_tableau);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos008"), Program.creer_tableau);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.TABLE);
				Utils.addCloseButton(Program.tabbedPane, Program.creer_tableau);
				Program.tabbedPane.setSelectedComponent(Program.creer_tableau);
			}
			checkSelectedTab();
			updateMainPanel();
		}
	}

	class ImportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public ImportFileAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (Program.importer == null) {
				Program.importer = new Importer();
				Program.tabbedPane.add(Program.getLabel("Infos011"), Program.importer);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(Program.tabbedPane, Program.importer);
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.importer);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos011"), Program.importer);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.IMPORT);
				Utils.addCloseButton(Program.tabbedPane, Program.importer);
				Program.tabbedPane.setSelectedComponent(Program.importer);
			}
			checkSelectedTab();
			updateMainPanel();
		}
	}

	class ExportFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public ExportFileAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.export == null) {
				try {
					Program.export = new Export();
					Program.tabbedPane.add(Program.getLabel("Infos148"), Program.export);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.EXPORT);
					Utils.addCloseButton(Program.tabbedPane, Program.export);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.export);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos148"), Program.export);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.EXPORT);
				Utils.addCloseButton(Program.tabbedPane, Program.export);
				Program.tabbedPane.setSelectedComponent(Program.export);
			}
			updateMainPanel();
		}
	}

	class StatAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public StatAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.stat == null) {
				try {
					Program.stat = new Stat();
					Program.stat.setVisible(true);
					Program.tabbedPane.add(Program.getLabel("Infos009"), Program.stat);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.STATS);
					Utils.addCloseButton(Program.tabbedPane, Program.stat);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.stat);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos009"), Program.stat);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.STATS);
				Utils.addCloseButton(Program.tabbedPane, Program.stat);
				Program.tabbedPane.setSelectedComponent(Program.stat);
			}
			updateMainPanel();
		}
	}

	class ShowHistoryAction extends AbstractAction {
		private static final long serialVersionUID = -2981766233846291757L;

		public ShowHistoryAction() {
			super(Program.getLabel("Infos341"), null);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.history == null) {
				try {
					Program.history = new ShowHistory();
					Program.tabbedPane.add(Program.getLabel("Infos341"), Program.history);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, null);
					Utils.addCloseButton(Program.tabbedPane, Program.history);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.history);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos341"), Program.history);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, null);
				Utils.addCloseButton(Program.tabbedPane, Program.history);
				Program.tabbedPane.setSelectedComponent(Program.history);
			}
			Program.history.refresh();
			updateMainPanel();
		}
	}
	
	class VignoblesAction extends AbstractAction {

		private static final long serialVersionUID = -7956676252030557402L;

		public VignoblesAction() {
			super(Program.getLabel("Infos165"), null);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			openVineyardPanel();
		}
	}
	
	class CapacityAction extends AbstractAction {

		private static final long serialVersionUID = -7204054967253027549L;

		public CapacityAction() {
			super(Program.getLabel("Infos400")+"...", null);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new ManageList(Program.half, Program.getLabel("Infos400"));
			Program.updateAllPanels();
			if(Program.addWine != null)
				Program.addWine.updateView();
		}
	}

	public static void openVineyardPanel() {
		if (Program.vignobles == null) {
			try {
				Program.vignobles = new VineyardPanel();
				Program.tabbedPane.add(Program.getLabel("Infos165"), Program.vignobles);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, null);
				Utils.addCloseButton(Program.tabbedPane, Program.vignobles);
			} catch (Exception e1) {
				Program.showException(e1);
			}
		}
		try {
			Program.tabbedPane.setSelectedComponent(Program.vignobles);
		} catch (IllegalArgumentException e) {
			Program.tabbedPane.add(Program.getLabel("Infos165"), Program.vignobles);
			Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, null);
			Utils.addCloseButton(Program.tabbedPane, Program.vignobles);
			Program.tabbedPane.setSelectedComponent(Program.vignobles);
		}
		updateMainPanel();
	}

	class ShowFileAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public ShowFileAction(String text, ImageIcon icon, String description, Integer mnemonic) {
			super(Program.getLabel(text), icon);
			if (null != description)
				putValue(SHORT_DESCRIPTION, Program.getLabel(description));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.showfile == null) {
				Program.showfile = new ShowFile();
				Program.tabbedPane.add(Program.getLabel("Infos325"), Program.showfile);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(Program.tabbedPane, Program.showfile);
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.showfile);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Infos325"), Program.showfile);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.SHOW);
				Utils.addCloseButton(Program.tabbedPane, Program.showfile);
				Program.tabbedPane.setSelectedComponent(Program.showfile);
			}
			updateMainPanel();
		}
	}

	class ShowTrashAction extends AbstractAction {
		private static final long serialVersionUID = -3212527164505184899L;

		public ShowTrashAction() {
			super("", MyCellarImage.TRASH);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.ShowTrash"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.showtrash == null) {
				Program.showtrash = new ShowFile(ShowType.TRASH);
				Program.tabbedPane.add(Program.getLabel("Main.ShowTrash"), Program.showtrash);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(Program.tabbedPane, Program.showtrash);
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.showtrash);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Main.ShowTrash"), Program.showtrash);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.TRASH);
				Utils.addCloseButton(Program.tabbedPane, Program.showtrash);
				Program.tabbedPane.setSelectedComponent(Program.showtrash);
			}
			Program.showtrash.refresh();
			updateMainPanel();
		}
	}

	class CutAction extends AbstractAction {
		private static final long serialVersionUID = -8024045169612180263L;

		public CutAction() {
			super(Program.getLabel("Infos241"), MyCellarImage.CUT);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos241"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			/*if (Program.isSelectedTab(Program.addWine))
				Program.addWine.cut();
			else*/ if (Program.isSelectedTab(Program.search))
				Program.search.cut();
		}
	}

	class CopyAction extends AbstractAction {
		private static final long serialVersionUID = -4416042464174203695L;

		public CopyAction() {
			super(Program.getLabel("Infos242"), MyCellarImage.COPY);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos242"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			/*if (Program.isSelectedTab(Program.addWine))
				Program.addWine.copy();
			else*/ if (Program.isSelectedTab(Program.search))
				Program.search.copy();
		}
	}

	class PasteAction extends AbstractAction {
		private static final long serialVersionUID = 7152419581737782003L;

		public PasteAction() {
			super(Program.getLabel("Infos243"), MyCellarImage.PASTE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Infos243"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			/*if (Program.isSelectedTab(Program.addWine))
				Program.addWine.paste();
			else*/ if (Program.isSelectedTab(Program.search))
				Program.search.paste();
		}
	}

	class ManagePlaceAction extends AbstractAction {

		private static final long serialVersionUID = -5144284671743409095L;

		public ManagePlaceAction(Integer mnemonic) {
			super(Program.getLabel("Main.ManagePlace"), MyCellarImage.PLACE);
			putValue(SHORT_DESCRIPTION, Program.getLabel("Main.ManagePlace"));
			if (null != mnemonic)
				putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (Program.managePlace == null) {
				try {
					Program.managePlace = new CellarOrganizerPanel();
					Program.tabbedPane.add(Program.getLabel("Main.ManagePlace"), Program.managePlace);
					Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.PLACE);
					Utils.addCloseButton(Program.tabbedPane, Program.managePlace);
				} catch (Exception e1) {
					Program.showException(e1);
				}
			}
			try {
				Program.tabbedPane.setSelectedComponent(Program.managePlace);
			} catch (IllegalArgumentException e) {
				Program.tabbedPane.add(Program.getLabel("Main.ManagePlace"), Program.managePlace);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.tabbedPane, Program.managePlace);
				Program.tabbedPane.setSelectedComponent(Program.managePlace);
			}
			updateMainPanel();
		}
	}
	
	public static void openCellChooserPanel(IAddVin addvin) {
		if (Program.chooseCell == null) {
			try {
				Program.chooseCell = new CellarOrganizerPanel(true, addvin);
				Program.tabbedPane.add(Program.getLabel("Main.ChooseCell"), Program.chooseCell);
				Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.PLACE);
				Utils.addCloseButton(Program.tabbedPane, Program.chooseCell);
			} catch (Exception e1) {
				Program.showException(e1);
			}
		}
		try {
			Program.tabbedPane.setSelectedComponent(Program.chooseCell);
			Program.chooseCell.setAddVin(addvin);
		} catch (IllegalArgumentException e) {
			Program.tabbedPane.add(Program.getLabel("Main.ChooseCell"), Program.chooseCell);
			Program.tabbedPane.setIconAt(Program.tabbedPane.getTabCount() - 1, MyCellarImage.PLACE);
			Utils.addCloseButton(Program.tabbedPane, Program.chooseCell);
			Program.tabbedPane.setSelectedComponent(Program.chooseCell);
		}
		updateMainPanel();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		Program.showException(e, true);
	}
}
