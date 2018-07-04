package mycellar;

import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSpinner;
import mycellar.core.PopupListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 11.1
 * @since 04/07/18
 */
public class Parametres extends JPanel implements ITabListener, ICutCopyPastable {

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

		PopupListener popup_l = new PopupListener();
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
		parcourir_excel.setToolTipText(Program.getLabel("Infos157"));
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
				Program.putCaveConfigInt("FIC_EXCEL", 1);
				String fic = file_bak.getText().trim();
				if (Program.checkXLSExtenstion(fic)) {
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
				Program.putCaveConfigInt("FIC_EXCEL", 0);

			if (result) {
				Program.putCaveConfigString("DEVISE", devise.getText().trim());
				try {
					int val = Integer.parseInt(annee.getValue().toString());
					Program.putCaveConfigInt("ANNEE", val);
				}
				catch (NumberFormatException ignored) {}
				try {
					int val = Integer.parseInt(siecle.getValue().toString());
					Program.putCaveConfigInt("SIECLE", val);
				}
				catch (NumberFormatException ignored) {}

				Program.setYearControl(jcb_annee_control.isSelected());

				//Program.write_XSL();
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
			cut();
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			copy();
		}
		if (e.getKeyCode() == KeyEvent.VK_V) {
			paste();
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
		if (retour_jfc == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			if (nomFichier == null) {
				setCursor(Cursor.getDefaultCursor());
				Erreur.showSimpleErreur(Program.getError("FileNotFound"));
				Program.Debug("ERROR: parcourir_excel: File not found during Opening!");
				return;
			}
			String fic = nomFichier.getAbsolutePath();
			fic = fic.trim();
			boolean extension = Program.checkXLSExtenstion(fic);
			Filtre filtre = (Filtre)boiteFichier.getFileFilter();
			if (!extension) {
				if (filtre.toString().equals("xls"))
					fic = fic.concat(".xls");
				if (filtre.toString().equals("ods"))
					fic = fic.concat(".ods");
			}
			file_bak.setText(fic);
			Program.putCaveConfigString("FILE_EXCEL", fic);
			Program.putCaveConfigString("DIR", boiteFichier.getCurrentDirectory().toString());
		}

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
			if(thelangue.equals(currentLanguage)) {
				return;
			}
			Program.putGlobalConfigString("LANGUAGE", thelangue);
			boolean ok = Program.setLanguage(thelangue);
			if (ok) {
				if (Program.getLabel("Infos159") == null) {
					ok = Program.setLanguage("F");
					langue.setSelectedIndex(0);
				}
				if(ok) {
					setLabels();
				}
			}
			else {
				langue.setSelectedIndex(0);
				Program.setLanguage("F");
				JOptionPane.showMessageDialog(null, "Language corrupted, Default French language selected.\nReinstall your language.", "Error", JOptionPane.ERROR_MESSAGE);
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


	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
		Program.parametres = null;
	}

	@Override
	public void cut() {
		String text = file_bak.getSelectedText();
		String fullText = file_bak.getText();
		if(text != null) {
			file_bak.setText(fullText.substring(0, file_bak.getSelectionStart()) + fullText.substring(file_bak.getSelectionEnd()));
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void copy() {
		String text = file_bak.getSelectedText();
		if(text != null) {
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void paste() {
		String fullText = file_bak.getText();
		file_bak.setText(fullText.substring(0, file_bak.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(file_bak.getSelectionEnd()));
	}
}
