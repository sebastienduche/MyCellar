package mycellar;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 9.8
 * @since 09/05/17
 */
public class Parametres extends JDialog {
	private MyCellarLabel label_fic_bak = new MyCellarLabel();
	private MyCellarLabel label_langue = new MyCellarLabel();
	private MyCellarLabel label_devise = new MyCellarLabel();
	private MyCellarLabel label_LF = new MyCellarLabel();
	private MyCellarComboBox<String> langue = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> lF = new MyCellarComboBox<String>();
	private MyCellarButton valider = new MyCellarButton();
	private MyCellarButton annuler_btn = new MyCellarButton();
	private MyCellarButton parcourir_excel = new MyCellarButton("..."); //Parcourir
	private int LARGEUR = 750;
	private int HAUTEUR = 520;
	private JTextField file_bak = new JTextField();
	private JTextField devise = new JTextField();
	private MyCellarCheckBox jcb_excel = new MyCellarCheckBox(Program.getLabel("Infos234"), false);
	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel4 = new JPanel();
	private MyCellarButton rebuild = new MyCellarButton(new RebuildStatsAction(Program.getLabel("Infos396"),null,"",null));

	private MyCellarButton jcb_message = new MyCellarButton();
	private MyCellarButton jcb_half_del = new MyCellarButton(MyCellarImage.DELETE);
	private MyCellarCheckBox jcb_half_auto = new MyCellarCheckBox();
	private MyCellarCheckBox m_jcb_debug = new MyCellarCheckBox();
	private JPanel jPanel5 = new JPanel();
	private JPanel jPanel7 = new JPanel();
	private JPanel jPanel8 = new JPanel();
	private JPanel jPanel9 = new JPanel();
	private MyCellarCheckBox jcb_annee_control = new MyCellarCheckBox(Program.getLabel("Infos169"), false);
	private MyCellarLabel label_annee = new MyCellarLabel();
	private MyCellarLabel label_annee2 = new MyCellarLabel();
	private MyCellarLabel label_siecle = new MyCellarLabel();
	private MyCellarSpinner annee = new MyCellarSpinner();
	private MyCellarSpinner siecle = new MyCellarSpinner();
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private MyClipBoard clipboard = new MyClipBoard();
	private JMenu edition = new JMenu(Program.getLabel("Infos245"));
	private Component objet1 = null;
	private Start start = null;
	static final long serialVersionUID = 280706;

	/**
	 * Parametres: Constructeur: pour la fenêtre des paramètres.
	 *
	 */
	public Parametres(Start start) {
		this.start = start;
		try {
			jbInit();
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * jbInit: Fonction d'initialisation.
	 *
	 * @throws Exception
	 */
	private void jbInit() throws Exception {

		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle(Program.getLabel("Infos193")); //Options
		setLayout(new MigLayout("","grow",""));
		setLabels();

		jcb_annee_control.addActionListener((e) -> {
			label_annee.setEnabled(jcb_annee_control.isSelected());
			label_annee2.setEnabled(jcb_annee_control.isSelected());
			label_siecle.setEnabled(jcb_annee_control.isSelected());
			annee.setEnabled(jcb_annee_control.isSelected());
			siecle.setEnabled(jcb_annee_control.isSelected());
		});

		//Menu Contextuel
		couper.addActionListener((e) -> couper_actionPerformed(e));
		cut.addActionListener((e) -> couper_actionPerformed(e));
		copier.addActionListener((e) -> copier_actionPerformed(e));
		copy.addActionListener((e) -> copier_actionPerformed(e));
		coller.addActionListener((e) -> coller_actionPerformed(e));
		paste.addActionListener((e) -> coller_actionPerformed(e));

		couper.setEnabled(false);
		copier.setEnabled(false);
		popup.add(couper);
		popup.add(copier);
		popup.add(coller);
		MouseListener popup_l = new PopupListener();
		file_bak.addMouseListener(popup_l);
		devise.addMouseListener(popup_l);
		valider.setText("OK");
		annuler_btn.setText(Program.getLabel("Infos055"));
		file_bak.setText(Program.getCaveConfigString("FILE_EXCEL",""));
		
		annee.setValue(new Integer(Program.getCaveConfigInt("ANNEE", 50)));
		siecle.setValue(new Integer(Program.getCaveConfigInt("SIECLE", 19)));

		if ( Program.getGlobalConfigInt("DEBUG", 0) == 1 )
			m_jcb_debug.setSelected(true);

		devise.setText(Program.getCaveConfigString("DEVISE",""));
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < info.length; i++) {
			lF.addItem(info[i].getName());
		}
		LookAndFeel lf = UIManager.getLookAndFeel();
		lF.setSelectedItem(lf.getName());
		String language = Program.getLanguage("Language1");
		int i = 1;
		while (language != null) {
			langue.addItem(language);
			i++;
			language = Program.getLanguage("Language" + i);
		}
		langue.addActionListener((e) -> langue_actionPerformed(e));
		lF.addActionListener((e) -> lF_actionPerformed(e));
		i = 1;
		language = Program.getLanguage("CodeLang1");
		String the_language = Program.getGlobalConfigString("LANGUAGE","");
		while (language != null && language.compareTo(the_language) != 0) {
			i++;
			language = Program.getLanguage("CodeLang" + i);
		}
		langue.setSelectedIndex(i - 1);

		String auto = Program.getCaveConfigString("TYPE_AUTO", "OFF");
		if (auto.equals("ON")) {
			jcb_half_auto.setSelected(true);
		}

		valider.addActionListener((e) -> valider_actionPerformed(e));
		annuler_btn.addActionListener((e) -> annuler_actionPerformed(e));
		parcourir_excel.addActionListener((e) -> parcourir_excel_actionPerformed(e));
		jcb_excel.addActionListener((e) -> jcb_excel_actionPerformed(e));
		jcb_message.addActionListener((e) -> jcb_message_actionPerformed(e));
		jcb_half_del.addActionListener((e) -> jcb_half_del_actionPerformed(e));
		jcb_half_auto.addActionListener((e) -> jcb_half_auto_actionPerformed(e));
		m_jcb_debug.addActionListener((e) -> activate_debug_actionPerformed(e));

		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
		});

		annee.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				if (Integer.parseInt(annee.getValue().toString()) < 0) {
					annee.setValue(new Integer(0));
				}
				if (Integer.parseInt(annee.getValue().toString()) > 99) {
					annee.setValue(new Integer(99));
				}
			}
		});

		siecle.addChangeListener((e) -> {
			if (Integer.parseInt(siecle.getValue().toString()) < 18) {
				siecle.setValue(new Integer(18));
			}
			else if (Integer.parseInt(siecle.getValue().toString()) > 99) {
				siecle.setValue(new Integer(99));
			}
		});

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowActivated(WindowEvent e) {}

			public void windowClosing(WindowEvent e) {annuler_actionPerformed(null);
			}
		});

		jPanel4.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos294")));
		jPanel7.setBorder(BorderFactory.createEtchedBorder());
		jPanel8.setBorder(BorderFactory.createEtchedBorder());
		jPanel9.setBorder(BorderFactory.createEtchedBorder());
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel2.setBorder(BorderFactory.createEtchedBorder());
		jcb_message.setText(Program.getLabel("Infos160"));
		jPanel5.setBorder(BorderFactory.createEtchedBorder());
		cut.setEnabled(false);
		copy.setEnabled(false);
		edition.add(cut);
		edition.add(copy);
		edition.add(paste);
		edition.addMouseListener(popup_l);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));
		jPanel1.setLayout(new MigLayout("","[][]30px[][]",""));
		jPanel1.add(label_langue);
		jPanel1.add(langue);
		jPanel1.add(label_LF);
		jPanel1.add(lF, "wrap");
		jPanel1.add(label_devise);
		jPanel1.add(devise, "w 100:100:100, wrap");
		add(jPanel1, "grow, wrap");
		jPanel2.setLayout(new MigLayout("","[][grow]",""));
		jPanel2.add(jcb_excel, "span 2, wrap");
		jPanel2.add(label_fic_bak);
		jPanel2.add(file_bak, "grow, split 2");
		jPanel2.add(parcourir_excel);
		add(jPanel2, "grow, wrap");
		jPanel4.setLayout(new MigLayout("","[][][][][][]",""));
		jPanel4.add(jcb_annee_control);
		jPanel4.add(label_annee);
		jPanel4.add(annee);
		jPanel4.add(label_annee2);
		jPanel4.add(siecle);
		jPanel4.add(label_siecle);
		add(jPanel4, "grow, wrap");
		jPanel5.setLayout(new MigLayout("","[][]",""));
		jPanel5.add(jcb_message, "span 2, wrap");
		jPanel5.add(jcb_half_del);
		jPanel5.add(jcb_half_auto, "wrap");
		add(jPanel5, "grow, wrap");

		add(m_jcb_debug,"split 2");
		add(rebuild, "gapleft 20px, wrap");
		add(valider, "split 2, gaptop 15px, center");
		add(annuler_btn);

		try {
			if (Program.getCaveConfigInt("FIC_EXCEL", 0) == 1) {
				file_bak.setVisible(true);
				label_fic_bak.setVisible(true);
				jcb_excel.setSelected(true);
				parcourir_excel.setVisible(true);
			}
			else {
				file_bak.setVisible(false);
				label_fic_bak.setVisible(false);
				jcb_excel.setSelected(false);
				parcourir_excel.setVisible(false);
			}
		}
		catch (NullPointerException npe) {
			file_bak.setVisible(false);
			label_fic_bak.setVisible(false);
			jcb_excel.setSelected(false);
			parcourir_excel.setVisible(false);
			Program.putCaveConfigString("FIC_EXCEL", "0");
		}

		if (Program.getCaveConfigInt("ANNEE_CTRL", 0) == 1) {
			jcb_annee_control.setSelected(true);
		}

		this.setSize(LARGEUR, HAUTEUR);
		setLocationRelativeTo(null);
	}

	private void setLabels() {
		label_fic_bak.setText(Program.getLabel("Infos162")); //"Nom du fichier Excel:");
		label_langue.setText(Program.getLabel("Infos231")); //"Choix de la langue:");
		label_devise.setText(Program.getLabel("Infos163"));
		label_annee.setText(Program.getLabel("Infos292"));
		label_annee2.setText(Program.getLabel("Infos293"));
		label_siecle.setText(Program.getLabel("Infos295"));
		label_LF.setText(Program.getLabel("Infos322"));

		jcb_excel.setText(Program.getLabel("Infos234"));
		jcb_message.setText(Program.getLabel("Infos160"));
		jcb_half_del.setText(Program.getLabel("Infos311"));
		jcb_half_auto.setText(Program.getLabel("Infos147"));
		couper.setText(Program.getLabel("Infos241"));
		copier.setText(Program.getLabel("Infos242"));
		coller.setText(Program.getLabel("Infos243"));
		parcourir_excel.setToolTipText(Program.getLabel("Infos157"));
		cut.setText(Program.getLabel("Infos241"));
		copy.setText(Program.getLabel("Infos242"));
		paste.setText(Program.getLabel("Infos243"));
		edition.setText(Program.getLabel("Infos245"));
		jcb_annee_control.setText(Program.getLabel("Infos169"));
		m_jcb_debug.setText(Program.getLabel("Infos337"));
	}

	/**
	 * valider_actionPerformed: Valider les modifications et quitter.
	 *
	 * @param e ActionEvent
	 */
	void valider_actionPerformed(ActionEvent e) {
		try {

			boolean result = true;
			if (jcb_excel.isSelected()) {
				String fic = file_bak.getText().trim();
				if (fic.toLowerCase().endsWith(".xls") || fic.toLowerCase().endsWith(".ods")) {
					Program.putCaveConfigString("FILE_EXCEL", fic);
				}
				else {
					String tmp1 = "";
					if (fic.length() >= 3) {
						tmp1 = fic.substring(fic.length() - 3);
					}
					new Erreur(Program.getError("Error034") + tmp1, Program.getError("Error035"));
					result = false;
				}
			}

			if (result) {
				Program.putCaveConfigString("DEVISE", devise.getText().trim());
				
				if (result) {
					Program.putCaveConfigString("MARK1_TITLE", Program.getLabel("Infos208"));
					Program.putCaveConfigString("MARK2_TITLE", Program.getLabel("Infos189"));
					Program.putCaveConfigString("MARK3_TITLE", Program.getLabel("Infos134"));
					Program.putCaveConfigString("MARK4_TITLE", Program.getLabel("Infos105"));
					Program.putCaveConfigString("MARK5_TITLE", Program.getLabel("Infos158"));
					Program.putCaveConfigString("MARK6_TITLE", Program.getLabel("Infos028"));
					Program.putCaveConfigString("MARK7_TITLE", Program.getLabel("Infos083"));
					Program.putCaveConfigString("MARK8_TITLE", Program.getLabel("Infos135"));
					Program.putCaveConfigString("MARK9_TITLE", Program.getLabel("Infos137"));
					Program.putCaveConfigString("ANNEE", annee.getValue().toString());
					try {
						Integer.parseInt(annee.getValue().toString());
					}
					catch (NumberFormatException nfe) {}
					Program.putCaveConfigString("SIECLE", siecle.getValue().toString());
					try {
						Integer.parseInt(siecle.getValue().toString());
					}
					catch (NumberFormatException nfe) {}

					Program.setYearControl(jcb_annee_control.isSelected());

					Program.write_XSL();
					Program.saveGlobalProperties();
					if(start != null)
						start.updateFrame(false);
					this.dispose();
				}
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	

	/**
	 * keylistener_actionPerformed: Ecoute clavier.
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == 'o' || e.getKeyCode() == 'O' || e.getKeyCode() == KeyEvent.VK_ENTER) {
			valider_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_F1) {
		}
		if (e.getKeyCode() == KeyEvent.VK_C) {
			copier_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			couper_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_V) {
			coller_actionPerformed(null);
		}
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			annuler_actionPerformed(null);
		}

	}


	/**
	 * parcourir_excel_actionPerformed: Boite Parcourir.
	 *
	 * @param e ActionEvent
	 */
	void parcourir_excel_actionPerformed(ActionEvent e) {

		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR",""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
		int retour_jfc = boiteFichier.showOpenDialog(this);
		File nomFichier = new File("");
		String fic = "";
		if (retour_jfc == JFileChooser.APPROVE_OPTION) {
			nomFichier = boiteFichier.getSelectedFile();
			fic = nomFichier.getAbsolutePath();
			fic = fic.trim();
			boolean extension = fic.toLowerCase().endsWith(".xls") || fic.toLowerCase().endsWith(".ods");
			Filtre filtre = (Filtre)boiteFichier.getFileFilter();
			if (!extension) {
				if (filtre.toString().equals("xls"))
					fic = fic.concat(".xls");
				if (filtre.toString().equals("ods"))
					fic = fic.concat(".ods");
			}
			file_bak.setText(fic);
		}
		Program.putCaveConfigString("FILE_EXCEL", fic);
		Program.putCaveConfigString("DIR", boiteFichier.getCurrentDirectory().toString());

	}

	/**
	 * jcb_excel_actionPerformed: Case à cocher.
	 *
	 * @param e ActionEvent
	 */
	void jcb_excel_actionPerformed(ActionEvent e) {
		if (jcb_excel.isSelected()) {
			Program.putCaveConfigString("FIC_EXCEL", "1");
			file_bak.setVisible(true);
			label_fic_bak.setVisible(true);
			parcourir_excel.setVisible(true);
		}
		else {
			Program.putCaveConfigString("FIC_EXCEL", "0");
			file_bak.setVisible(false);
			label_fic_bak.setVisible(false);
			parcourir_excel.setVisible(false);
		}
	}


	/**
	 * langue_actionPerformed: Choix de la langue.
	 *
	 * @param e ActionEvent
	 */
	void langue_actionPerformed(ActionEvent e) {
		try {
			String thelangue = Program.getLanguage("CodeLang" + (langue.getSelectedIndex() + 1));
			Program.putGlobalConfigString("LANGUAGE", thelangue);
			boolean ok = Program.setLanguage(thelangue);
			if (ok) {
				if (Program.getLabel("Infos159") == null) {
					thelangue = "F";
					ok = Program.setLanguage(thelangue);
					langue.setSelectedIndex(0);
				}
				if(ok) {
					setLabels();
				}
			}
			else {
				langue.setSelectedIndex(0);
				Program.setLanguage("F");
				javax.swing.JOptionPane.showMessageDialog(null, "Language corrupted, Default French language selected.\nReinstall your language.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * jcb_message_actionPerformed: Case à cocher.
	 *
	 * @param e ActionEvent
	 */
	void jcb_message_actionPerformed(ActionEvent e) {

		Program.putCaveConfigString("DONT_SHOW_*&?_MESS", "0");
		Program.putCaveConfigString("DONT_SHOW_INFO", "0");
		Program.putCaveConfigString("DONT_SHOW_TAB_MESS", "0");
		Program.putCaveConfigString("DONT_SHOW_CREATE_MESS", "0");
		Program.putCaveConfigString("DONT_SHOW_VIABADPLACE_MESS", "0");
		Program.putCaveConfigString("DONT_SHOW_NUMERIC_MESS", "0");
		jcb_message.setEnabled(false);
	}

	/**
	 * quitter_actionPerformed: Fonction pour quitter.
	 *
	 * @param e ActionEvent
	 */
	void annuler_actionPerformed(ActionEvent e) {
		this.dispose();
	}


	/**
	 * couper_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	void couper_actionPerformed(ActionEvent e) {
		String txt = "";
		JTextField jtf = (JTextField) objet1;
		txt = jtf.getSelectedText();
		jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
		clipboard.copier(txt);
	}

	/**
	 * copier_actionPerformed: Copier
	 *
	 * @param e ActionEvent
	 */
	void copier_actionPerformed(ActionEvent e) {
		String txt = "";
		JTextField jtf = (JTextField) objet1;
		txt = jtf.getSelectedText();
		clipboard.copier(txt);
	}

	/**
	 * coller_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	void coller_actionPerformed(ActionEvent e) {

		JTextField jtf = (JTextField) objet1;
		try {
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
	}

	/**
	 * aide_actionPerformed: Aide
	 *
	 * @param e ActionEvent
	 */
	void aide_actionPerformed(ActionEvent e) {
		Program.getAide();
	}

	/**
	 * Vidage de la liste des types
	 *
	 * @param e ActionEvent
	 */
	void jcb_half_del_actionPerformed(ActionEvent e) {
		Program.setXMLTypesFileToDelete();
		Program.half.clear();
		Program.half.add("75cl");
		Program.half.add("37.5cl");
		Program.defaut_half = "75cl";
		jcb_half_del.setEnabled(false);
		if(Program.addWine != null)
			Program.addWine.updateView();
	}

	/**
	 * jcb_half_auto_actionPerformed: Case à cocher.
	 *
	 * @param e ActionEvent
	 */
	void jcb_half_auto_actionPerformed(ActionEvent e) {

		if (jcb_half_auto.isSelected()) {
			Program.putCaveConfigString("TYPE_AUTO", "ON");
		}
		else {
			Program.putCaveConfigString("TYPE_AUTO", "OFF");
		}
	}

	/**
	 * lF_actionPerformed: Choix de la langue.
	 *
	 * @param A ActionEvent
	 */
	void lF_actionPerformed(ActionEvent A) {
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		String className = info[lF.getSelectedIndex()].getClassName();
		try {
			UIManager.setLookAndFeel(className);
			for(Window window: Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
			Program.putCaveConfigString("LOOK&FEEL", className);
		}
		catch (InstantiationException e) {
		}
		catch (ClassNotFoundException e) {
		}
		catch (UnsupportedLookAndFeelException e) {
		}
		catch (IllegalAccessException e) {
		}
	}

	/**
	 * activate_debug_actionPerformed: Case à cocher.
	 *
	 * @param e ActionEvent
	 */
	void activate_debug_actionPerformed(ActionEvent e) {

		if (m_jcb_debug.isSelected()) {
			Program.putGlobalConfigString("DEBUG", "1");
			Program.setDebug(true);
		}
		else {
			Program.putGlobalConfigString("DEBUG", "0");
			Program.setDebug(false);
		}
	}


	/**
	 * <p>Titre : Cave à vin</p>
	 * <p>Description : Votre description</p>
	 * <p>Copyright : Copyright (c) 1998</p>
	 * <p>Société : Seb Informatique</p>
	 * @author Sébastien Duché
	 * @version 0.1
	 * @since 17/04/05
	 */
	class PopupListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		private void maybeShowPopup(MouseEvent e) {
			try {
				JTextField jtf = (JTextField) e.getComponent();
				if (jtf.isEnabled() && jtf.isVisible()) {
					objet1 = e.getComponent();
				}
			}
			catch (Exception ee) {}
			if (e.getButton() == MouseEvent.BUTTON3) {
				if (e.getComponent().isFocusable() && e.getComponent().isEnabled()) {
					e.getComponent().requestFocus();
					JTextField jtf = (JTextField) objet1;
					if (jtf == null || jtf.getSelectedText() == null) {
						couper.setEnabled(false);
						copier.setEnabled(false);
					}
					else {
						couper.setEnabled(true);
						copier.setEnabled(true);
					}
					if (jtf.isEnabled() && jtf.isVisible()) {
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getComponent().isFocusable() && e.getComponent().isEnabled()) {
					e.getComponent().requestFocus();
					JTextField jtf = (JTextField) objet1;
					if (jtf == null || jtf.getSelectedText() == null) {
						cut.setEnabled(false);
						copy.setEnabled(false);
					}
					else {
						cut.setEnabled(true);
						copy.setEnabled(true);
					}
				}
			}
		}
	}

	class RebuildStatsAction extends AbstractAction {

		private static final long serialVersionUID = -5234123562180384003L;
		public RebuildStatsAction(String text, ImageIcon icon,
				String desc, Integer mnemonic) {
			super(text, icon);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			Program.rebuildStats();
		}
	}
}
