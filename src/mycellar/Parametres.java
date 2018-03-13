package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.MessageFormat;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 10.5
 * @since 13/03/18
 */
public class Parametres extends JPanel implements ITabListener {

	private static final long serialVersionUID = -4208146070057957967L;
	private final MyCellarLabel label_fic_bak = new MyCellarLabel();
	private final MyCellarLabel label_langue = new MyCellarLabel();
	private final MyCellarLabel label_devise = new MyCellarLabel();
	private final MyCellarComboBox<String> langue = new MyCellarComboBox<>();
	private final MyCellarButton valider = new MyCellarButton();
	private final MyCellarButton parcourir_excel = new MyCellarButton("..."); //Parcourir
	private final JTextField file_bak = new JTextField();
	private final JTextField devise = new JTextField();
	private final MyCellarCheckBox jcb_excel = new MyCellarCheckBox(Program.getLabel("Infos234"), false);
	private final MyCellarButton buttonResetMessageDialog = new MyCellarButton();
	private final MyCellarButton buttonManageContenance = new MyCellarButton();
	private final MyCellarCheckBox jcb_half_auto = new MyCellarCheckBox();
	private final MyCellarCheckBox m_jcb_debug = new MyCellarCheckBox();
	private final MyCellarCheckBox jcb_annee_control = new MyCellarCheckBox(Program.getLabel("Infos169"), false);
	private final MyCellarLabel label_annee = new MyCellarLabel();
	private final MyCellarLabel label_annee2 = new MyCellarLabel();
	private final MyCellarLabel label_siecle = new MyCellarLabel();
	private final MyCellarSpinner annee = new MyCellarSpinner();
	private final MyCellarSpinner siecle = new MyCellarSpinner();
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), MyCellarImage.CUT);
	private final JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), MyCellarImage.COPY);
	private final JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), MyCellarImage.PASTE);
	private final JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), MyCellarImage.CUT);
	private final JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), MyCellarImage.COPY);
	private final JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), MyCellarImage.PASTE);
	private final MyClipBoard clipboard = new MyClipBoard();
	private final JMenu edition = new JMenu(Program.getLabel("Infos245"));
	private Component objet1 = null;

	/**
	 * Parametres: Constructeur: pour la fenêtre des paramètres.
	 *
	 */
	public Parametres() {
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
		couper.addActionListener(this::couper_actionPerformed);
		cut.addActionListener(this::couper_actionPerformed);
		copier.addActionListener(this::copier_actionPerformed);
		copy.addActionListener(this::copier_actionPerformed);
		coller.addActionListener(this::coller_actionPerformed);
		paste.addActionListener(this::coller_actionPerformed);

		couper.setEnabled(false);
		copier.setEnabled(false);
		popup.add(couper);
		popup.add(copier);
		popup.add(coller);
		MouseListener popup_l = new PopupListener();
		file_bak.addMouseListener(popup_l);
		devise.addMouseListener(popup_l);
		valider.setText(Program.getLabel("Infos315"));
		file_bak.setText(Program.getCaveConfigString("FILE_EXCEL",""));
		
		annee.setValue(Program.getCaveConfigInt("ANNEE", 50));
		siecle.setValue(Program.getCaveConfigInt("SIECLE", 19));

		if ( Program.getGlobalConfigInt("DEBUG", 0) == 1 )
			m_jcb_debug.setSelected(true);

		devise.setText(Program.getCaveConfigString("DEVISE",""));
		String language = Program.getLanguage("Language1");
		int i = 1;
		while (language != null) {
			langue.addItem(language);
			i++;
			language = Program.getLanguage("Language" + i);
		}
		
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

		valider.addActionListener(this::valider_actionPerformed);
		parcourir_excel.addActionListener(this::parcourir_excel_actionPerformed);
		jcb_excel.addActionListener(this::jcb_excel_actionPerformed);
		buttonResetMessageDialog.addActionListener(this::jcb_message_actionPerformed);
		buttonManageContenance.addActionListener(this::buttonManageContenance_actionPerformed);
		jcb_half_auto.addActionListener(this::jcb_half_auto_actionPerformed);
		m_jcb_debug.addActionListener(this::activate_debug_actionPerformed);

		annee.addChangeListener((e) -> {
				if (Integer.parseInt(annee.getValue().toString()) < 0) {
					annee.setValue(0);
				} else if (Integer.parseInt(annee.getValue().toString()) > 99) {
					annee.setValue(99);
				}
		});

		siecle.addChangeListener((e) -> {
			if (Integer.parseInt(siecle.getValue().toString()) < 18) {
				siecle.setValue(18);
			}
			else if (Integer.parseInt(siecle.getValue().toString()) > 99) {
				siecle.setValue(99);
			}
		});

		JPanel dateControlPanel = new JPanel();
		JPanel generalPanel = new JPanel();
		JPanel excelPanel = new JPanel();
		JPanel otherPanel = new JPanel();
		dateControlPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos294")));
		generalPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Main.General")));
		excelPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos234")));
		buttonResetMessageDialog.setText(Program.getLabel("Infos160"));
		otherPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Parameters.Others")));
		cut.setEnabled(false);
		copy.setEnabled(false);
		edition.add(cut);
		edition.add(copy);
		edition.add(paste);
		edition.addMouseListener(popup_l);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
		generalPanel.setLayout(new MigLayout("","[][]30px[][]",""));
		generalPanel.add(label_langue);
		generalPanel.add(langue, "gapleft 10");
		generalPanel.add(label_devise);
		generalPanel.add(devise, "w 100:100:100");
		add(generalPanel, "grow, wrap");
		excelPanel.setLayout(new MigLayout("","[100:100:100][][grow]",""));
		excelPanel.add(jcb_excel);
		excelPanel.add(label_fic_bak);
		excelPanel.add(file_bak, "grow, split 2");
		excelPanel.add(parcourir_excel);
		add(excelPanel, "grow, wrap");
		dateControlPanel.setLayout(new MigLayout("","[100:100:100][][][][][]",""));
		dateControlPanel.add(jcb_annee_control);
		dateControlPanel.add(label_annee);
		dateControlPanel.add(annee);
		dateControlPanel.add(label_annee2);
		dateControlPanel.add(siecle);
		dateControlPanel.add(label_siecle);
		add(dateControlPanel, "grow, wrap");
		otherPanel.setLayout(new MigLayout("","[][]",""));
		otherPanel.add(buttonResetMessageDialog, "span 2, wrap");
		otherPanel.add(buttonManageContenance);
		otherPanel.add(jcb_half_auto, "wrap");
		otherPanel.add(m_jcb_debug,"wrap");
		add(otherPanel, "grow, wrap");

		add(valider, "gaptop 15px, center");

		
		int val = Program.getCaveConfigInt("FIC_EXCEL", 0);
		file_bak.setEnabled(val == 1);
		label_fic_bak.setEnabled(val == 1);
		jcb_excel.setSelected(val == 1);
		parcourir_excel.setEnabled(val == 1);
	
		if (Program.getCaveConfigInt("ANNEE_CTRL", 0) == 1) {
			jcb_annee_control.setSelected(true);
		}
		label_annee.setEnabled(jcb_annee_control.isSelected());
		label_annee2.setEnabled(jcb_annee_control.isSelected());
		label_siecle.setEnabled(jcb_annee_control.isSelected());
		annee.setEnabled(jcb_annee_control.isSelected());
		siecle.setEnabled(jcb_annee_control.isSelected());
	}

	private void setLabels() {
		label_fic_bak.setText(Program.getLabel("Infos162")); //"Nom du fichier Excel:");
		label_langue.setText(Program.getLabel("Infos231")); //"Choix de la langue:");
		label_devise.setText(Program.getLabel("Infos163"));
		label_annee.setText(Program.getLabel("Infos292"));
		label_annee2.setText(Program.getLabel("Infos293"));
		label_siecle.setText(Program.getLabel("Infos295"));

		jcb_excel.setText(Program.getLabel("Infos169"));
		buttonResetMessageDialog.setText(Program.getLabel("Infos160"));
		buttonManageContenance.setText(Program.getLabel("Infos400"));
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
		valider.setText(Program.getLabel("Main.OK"));
	}

	/**
	 * valider_actionPerformed: Valider les modifications et quitter.
	 *
	 * @param e ActionEvent
	 */
	private void valider_actionPerformed(ActionEvent e) {
		try {
			modifyLanguage();
			boolean result = true;
			if (jcb_excel.isSelected()) {
				Program.putCaveConfigString("FIC_EXCEL", "1");
				String fic = file_bak.getText().trim();
				if (fic.toLowerCase().endsWith(".xls") || fic.toLowerCase().endsWith(".ods")) {
					Program.putCaveConfigString("FILE_EXCEL", fic);
				}
				else {
					String tmp1 = "";
					if (fic.length() >= 3) {
						tmp1 = fic.substring(fic.length() - 3);
					}
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), tmp1), Program.getError("Error035"));
					result = false;
				}
			}
			else
				Program.putCaveConfigString("FIC_EXCEL", "0");

			if (result) {
				Program.putCaveConfigString("DEVISE", devise.getText().trim());
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
	}


	/**
	 * parcourir_excel_actionPerformed: Boite Parcourir.
	 *
	 * @param e ActionEvent
	 */
	private void parcourir_excel_actionPerformed(ActionEvent e) {

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
	private void jcb_excel_actionPerformed(ActionEvent e) {
		if (jcb_excel.isSelected()) {
			file_bak.setEnabled(true);
			label_fic_bak.setEnabled(true);
			parcourir_excel.setEnabled(true);
		}
		else {
			file_bak.setEnabled(false);
			label_fic_bak.setEnabled(false);
			parcourir_excel.setEnabled(false);
		}
	}


	/**
	 * Modification de la langue à la fermeture de la boite de dialogue
	 */
	private void modifyLanguage() {
		try {
			String thelangue = Program.getLanguage("CodeLang" + (langue.getSelectedIndex() + 1));
			String currentLanguage = Program.getGlobalConfigString("LANGUAGE", "F");
			if(thelangue.equals(currentLanguage))
				return;
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
	private void jcb_message_actionPerformed(ActionEvent e) {
		Program.putCaveConfigString("DONT_SHOW_INFO", "0");
		Program.putCaveConfigString("DONT_SHOW_TAB_MESS", "0");
		Program.putCaveConfigString("DONT_SHOW_CREATE_MESS", "0");
		buttonResetMessageDialog.setEnabled(false);
	}

	/**
	 * couper_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	private void couper_actionPerformed(ActionEvent e) {
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
	private void copier_actionPerformed(ActionEvent e) {
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
	private void coller_actionPerformed(ActionEvent e) {

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
	 * Gestion des contenus
	 *
	 * @param e ActionEvent
	 */
	private void buttonManageContenance_actionPerformed(ActionEvent e) {
		new ManageList();
		if(Program.addWine != null) {
			Program.addWine.setUpdateView();
			Program.addWine.updateView();
		}
	}

	/**
	 * jcb_half_auto_actionPerformed: Case à cocher.
	 *
	 * @param e ActionEvent
	 */
	private void jcb_half_auto_actionPerformed(ActionEvent e) {

		if (jcb_half_auto.isSelected()) {
			Program.putCaveConfigString("TYPE_AUTO", "ON");
		}
		else {
			Program.putCaveConfigString("TYPE_AUTO", "OFF");
		}
	}

	/**
	 * activate_debug_actionPerformed: Case à cocher.
	 *
	 * @param e ActionEvent
	 */
	private void activate_debug_actionPerformed(ActionEvent e) {

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

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			maybeShowPopup(e);
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
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

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
		Program.parametres = null;
	}
}
