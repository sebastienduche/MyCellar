package mycellar;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import net.miginfocom.swing.MigLayout;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 10.7
 * @since 25/07/17
 */
public class Importer extends JPanel implements ITabListener, Runnable {

	private MyCellarButton importe = new MyCellarButton();
	private MyCellarRadioButton type_txt = new MyCellarRadioButton();
	private MyCellarRadioButton type_xls = new MyCellarRadioButton();
	private MyCellarRadioButton type_xml = new MyCellarRadioButton();
	private ButtonGroup checkboxGroup1 = new ButtonGroup();
	private int bool_name = 0;
	private int bool_year = 0;
	private int bool_half = 0;
	private int bool_plac = 0;
	private int bool_nump = 0;
	private int bool_line = 0;
	private int bool_colu = 0;
	private int bool_pric = 0;
	private int bool_comm = 0;
	private int bool_othe1 = 0;
	private int bool_othe2 = 0;
	private int bool_othe3 = 0;
	private MyCellarButton parcourir = new MyCellarButton();
	private char IMPORT = Program.getLabel("IMPORT").charAt(0);
	private char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private MyCellarButton openit = new MyCellarButton();
	private MyCellarComboBox<String> choix1 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix2 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix3 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix4 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix5 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix6 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix7 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix8 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix9 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix10 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix11 = new MyCellarComboBox<String>();
	private MyCellarComboBox<String> choix12 = new MyCellarComboBox<String>();
	private MyCellarCheckBox titre = new MyCellarCheckBox();
	private MyCellarLabel textControl2 = new MyCellarLabel();
	private MyCellarLabel label_progression = new MyCellarLabel();
	private MyCellarLabel label2 = new MyCellarLabel();
	private MyCellarComboBox<String> separateur = new MyCellarComboBox<String>();
	private MyCellarLabel label1 = new MyCellarLabel();
	private JTextField file = new JTextField();
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private MyClipBoard clipboard = new MyClipBoard();
	private JMenuItem quitter = new JMenuItem(Program.getLabel("Infos003"));
	private Component objet1 = null;
	static final long serialVersionUID = 280706;


	/**
	 * Importer: Constructeur
	 */
	public Importer() {
		Debug("Constructor");
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

		Debug("jbInit");
		importe.setToolTipText(Program.getLabel("Infos011"));
		openit.setToolTipText(Program.getLabel("Infos152"));
		parcourir.setToolTipText(Program.getLabel("Infos157"));
		importe.setMnemonic(IMPORT);
		openit.setDebugGraphicsOptions(0);
		openit.setMnemonic(OUVRIR);
		importe.setText(Program.getLabel("Infos036")); //"Importer");
		importe.addActionListener((e) -> importe_actionPerformed(e)); //"Sélectionner les diff�rents champs pr�sents dans le fichier (de gauche " + "� droite)");
		type_txt.setText(Program.getLabel("Infos040")); //"Fichier TXT ou CSV");
		titre.setHorizontalTextPosition(2);
		titre.setText(Program.getLabel("Infos038"));
		textControl2.setText(Program.getLabel("Infos037"));
		label_progression.setForeground(Color.red);
		label_progression.setFont(new java.awt.Font("Dialog", 1, 12));
		label_progression.setHorizontalAlignment(0);
		label2.setText(Program.getLabel("Infos034"));
		label1.setText(Program.getLabel("Infos033"));
		checkboxGroup1.add(type_txt);
		checkboxGroup1.add(type_xls);
		checkboxGroup1.add(type_xml);
		type_txt.addItemListener((e) -> type_itemStateChanged(e));
		type_xls.setText(Program.getLabel("Infos041")); //"Fichier Excel");
		parcourir.setText("...");
		openit.setText(Program.getLabel("Infos152")); //"Ouvrir le fichier");
		openit.addActionListener((e) -> openit_actionPerformed(e));
		parcourir.addActionListener((e) -> parcourir_actionPerformed(e));
		type_xls.addItemListener((e) -> type_itemStateChanged(e));
		type_txt.setSelected(true);
		
		type_xml.addItemListener((e) -> type_itemStateChanged(e));
		type_xml.setText(Program.getLabel("Infos203")); //"Fichier XML");

		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
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
		file.addMouseListener(popup_l);
		cut.setEnabled(false);
		copy.setEnabled(false);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));
		quitter.setAccelerator(KeyStroke.getKeyStroke('Q', ActionEvent.CTRL_MASK));

		setLayout(new MigLayout("","grow",""));
		JPanel panelFile = new JPanel();
		panelFile.setLayout(new MigLayout("","[grow][][]","[]"));
		panelFile.add(label1, "wrap");
		panelFile.add(file, "grow");
		panelFile.add(parcourir);
		panelFile.add(openit);
		add(panelFile,"grow,wrap");
		JPanel panelType = new JPanel();
		panelType.setLayout(new MigLayout("","[][]","[]"));
		JPanel panelFileType = new JPanel();
		panelFileType.setLayout(new MigLayout("","",""));
		panelFileType.add(type_txt);
		panelFileType.add(type_xls, "gapleft 15px");
		panelFileType.add(type_xml, "gapleft 15px");
		panelFileType.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos039")));
		panelType.add(panelFileType);
		JPanel panelSeparator = new JPanel();
		panelSeparator.setLayout(new MigLayout("","",""));
		panelSeparator.add(label2);
		panelSeparator.add(separateur, "gapleft 10px");
		panelType.add(panelSeparator);
		add(panelType, "grow, wrap");
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("","",""));
		panel.add(titre, "");
		add(panel, "wrap");
		JPanel panelChoix = new JPanel();
		panelChoix.setLayout(new MigLayout("","[][][][]",""));
		panelChoix.add(textControl2, "span 4,wrap");
		panelChoix.add(choix1);
		panelChoix.add(choix2);
		panelChoix.add(choix3);
		panelChoix.add(choix4, "wrap");
		panelChoix.add(choix5);
		panelChoix.add(choix6);
		panelChoix.add(choix7);
		panelChoix.add(choix8, "wrap");
		panelChoix.add(choix9);
		panelChoix.add(choix10);
		panelChoix.add(choix11);
		panelChoix.add(choix12);
		add(panelChoix, "grow, wrap");
		add(label_progression, "grow, center, hidemode 3, wrap");
		add(importe, "center");

		ArrayList<MyCellarFields> list = MyCellarFields.getFieldsList();
		choix1.addItem("");
		choix2.addItem("");
		choix3.addItem("");
		choix4.addItem("");
		choix5.addItem("");
		choix6.addItem("");
		choix7.addItem("");
		choix8.addItem("");
		choix9.addItem("");
		choix10.addItem("");
		choix11.addItem("");
		choix12.addItem("");
		for( int i=0; i<list.size(); i++)
		{
			String value = list.get(i).toString();
			choix1.addItem(value);
			choix2.addItem(value);
			choix3.addItem(value);
			choix4.addItem(value);
			choix5.addItem(value);
			choix6.addItem(value);
			choix7.addItem(value);
			choix8.addItem(value);
			choix9.addItem(value);
			choix10.addItem(value);
			choix11.addItem(value);
			choix12.addItem(value);
		}
		// On ajoute la ligne "Ignorer"
		String ignore = Program.getLabel("Infos271");
		choix1.addItem(ignore);
		choix2.addItem(ignore);
		choix3.addItem(ignore);
		choix4.addItem(ignore);
		choix5.addItem(ignore);
		choix6.addItem(ignore);
		choix7.addItem(ignore);
		choix8.addItem(ignore);
		choix9.addItem(ignore);
		choix10.addItem(ignore);
		choix11.addItem(ignore);
		choix12.addItem(ignore);

		separateur.addItem(Program.getLabel("Infos042"));
		separateur.addItem(Program.getLabel("Infos043"));
		separateur.addItem(Program.getLabel("Infos044"));
		separateur.addItem(Program.getLabel("Infos002"));

		choix1.addActionListener((e) -> choix1_actionPerformed(e));
		choix2.addActionListener((e) -> choix2_actionPerformed(e));
		choix3.addActionListener((e) -> choix3_actionPerformed(e));
		choix4.addActionListener((e) -> choix4_actionPerformed(e));
		choix5.addActionListener((e) -> choix5_actionPerformed(e));
		choix6.addActionListener((e) -> choix6_actionPerformed(e));
		choix7.addActionListener((e) -> choix7_actionPerformed(e));
		choix8.addActionListener((e) -> choix8_actionPerformed(e));
		choix9.addActionListener((e) -> choix9_actionPerformed(e));
		choix10.addActionListener((e) -> choix10_actionPerformed(e));
		choix11.addActionListener((e) -> choix11_actionPerformed(e));
		
		choix2.setEnabled(false);
		choix3.setEnabled(false);
		choix4.setEnabled(false);
		choix5.setEnabled(false);
		choix6.setEnabled(false);
		choix7.setEnabled(false);
		choix8.setEnabled(false);
		choix9.setEnabled(false);
		choix10.setEnabled(false);
		choix11.setEnabled(false);
		choix12.setEnabled(false);
		Debug("jbInit OK");
	}
	
	/**
	 * type_txt_itemStateChanged: Sélection d'un type de fichier
	 *
	 * @param e ItemEvent
	 */
	void type_itemStateChanged(ItemEvent e) {

		label_progression.setText("");
		label2.setVisible(type_txt.isSelected());
		separateur.setVisible(type_txt.isSelected());
		choix1.setVisible(!type_xml.isSelected());
		choix2.setVisible(!type_xml.isSelected());
		choix3.setVisible(!type_xml.isSelected());
		choix4.setVisible(!type_xml.isSelected());
		choix5.setVisible(!type_xml.isSelected());
		choix6.setVisible(!type_xml.isSelected());
		choix7.setVisible(!type_xml.isSelected());
		choix8.setVisible(!type_xml.isSelected());
		choix9.setVisible(!type_xml.isSelected());
		choix10.setVisible(!type_xml.isSelected());
		choix11.setVisible(!type_xml.isSelected());
		choix12.setVisible(!type_xml.isSelected());
		titre.setVisible(!type_xml.isSelected());
		textControl2.setVisible(!type_xml.isSelected());
	}

	/**
	 * importe_actionPerformed: Exécuter une Importation de données
	 *
	 * @param e ActionEvent
	 */
	void importe_actionPerformed(ActionEvent e) {
		new Thread(this).start();
	}

	/**
	 * choix1_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix1_actionPerformed(ActionEvent e) {
		if (choix1.getSelectedIndex() == 0) {
			choix2.setEnabled(false);
			choix3.setEnabled(false);
			choix4.setEnabled(false);
			choix5.setEnabled(false);
			choix6.setEnabled(false);
			choix7.setEnabled(false);
			choix8.setEnabled(false);
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix2.setSelectedIndex(0);
			choix3.setSelectedIndex(0);
			choix4.setSelectedIndex(0);
			choix5.setSelectedIndex(0);
			choix6.setSelectedIndex(0);
			choix7.setSelectedIndex(0);
			choix8.setSelectedIndex(0);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix2.setEnabled(true);
		}
	}

	/**
	 * choix2_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix2_actionPerformed(ActionEvent e) {
		if (choix2.getSelectedIndex() == 0) {
			choix3.setEnabled(false);
			choix4.setEnabled(false);
			choix5.setEnabled(false);
			choix6.setEnabled(false);
			choix7.setEnabled(false);
			choix8.setEnabled(false);
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix3.setSelectedIndex(0);
			choix4.setSelectedIndex(0);
			choix5.setSelectedIndex(0);
			choix6.setSelectedIndex(0);
			choix7.setSelectedIndex(0);
			choix8.setSelectedIndex(0);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix3.setEnabled(true);
		}
	}

	/**
	 * choix3_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix3_actionPerformed(ActionEvent e) {
		if (choix3.getSelectedIndex() == 0) {
			choix4.setEnabled(false);
			choix5.setEnabled(false);
			choix6.setEnabled(false);
			choix7.setEnabled(false);
			choix8.setEnabled(false);
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix4.setSelectedIndex(0);
			choix5.setSelectedIndex(0);
			choix6.setSelectedIndex(0);
			choix7.setSelectedIndex(0);
			choix8.setSelectedIndex(0);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix4.setEnabled(true);
		}
	}

	/**
	 * choix4_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix4_actionPerformed(ActionEvent e) {
		if (choix4.getSelectedIndex() == 0) {
			choix5.setEnabled(false);
			choix6.setEnabled(false);
			choix7.setEnabled(false);
			choix8.setEnabled(false);
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix5.setSelectedIndex(0);
			choix6.setSelectedIndex(0);
			choix7.setSelectedIndex(0);
			choix8.setSelectedIndex(0);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix5.setEnabled(true);
		}
	}

	/**
	 * choix5_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix5_actionPerformed(ActionEvent e) {
		if (choix5.getSelectedIndex() == 0) {
			choix6.setEnabled(false);
			choix7.setEnabled(false);
			choix8.setEnabled(false);
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix6.setSelectedIndex(0);
			choix7.setSelectedIndex(0);
			choix8.setSelectedIndex(0);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix6.setEnabled(true);
		}
	}

	/**
	 * choix6_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix6_actionPerformed(ActionEvent e) {
		if (choix6.getSelectedIndex() == 0) {
			choix7.setEnabled(false);
			choix8.setEnabled(false);
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix7.setSelectedIndex(0);
			choix8.setSelectedIndex(0);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix7.setEnabled(true);
		}
	}

	/**
	 * choix7_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix7_actionPerformed(ActionEvent e) {
		if (choix7.getSelectedIndex() == 0) {
			choix8.setEnabled(false);
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix8.setSelectedIndex(0);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix8.setEnabled(true);
		}
	}

	/**
	 * choix8_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix8_actionPerformed(ActionEvent e) {
		if (choix8.getSelectedIndex() == 0) {
			choix9.setEnabled(false);
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix9.setSelectedIndex(0);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix9.setEnabled(true);
		}
	}

	/**
	 * choix9_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix9_actionPerformed(ActionEvent e) {
		if (choix9.getSelectedIndex() == 0) {
			choix10.setEnabled(false);
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix10.setSelectedIndex(0);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix10.setEnabled(true);
		}
	}

	/**
	 * choix10_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix10_actionPerformed(ActionEvent e) {
		if (choix10.getSelectedIndex() == 0) {
			choix11.setEnabled(false);
			choix12.setEnabled(false);
			choix11.setSelectedIndex(0);
			choix12.setSelectedIndex(0);
		}
		else {
			choix11.setEnabled(true);
		}
	}

	/**
	 * choix11_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	void choix11_actionPerformed(ActionEvent e) {
		if (choix11.getSelectedIndex() == 0) {
			choix12.setEnabled(false);
			choix12.setSelectedIndex(0);
		}
		else {
			choix12.setEnabled(true);
		}
	}

	/**
	 * Réalise la lecture d'un fichier XLS
	 *
	 * @param sheet Sheet: Feuille Excel
	 * @param nb_lign_xls int: Nombre de ligne du fichier Excel
	 * @param column int: Numéro de la colonne à lire
	 * @throws ArrayIndexOutOfBoundsException
	 * @return String[]
	 */
	public String[] readXLS(Sheet sheet, int nb_lign_xls, int column) throws ArrayIndexOutOfBoundsException {

		Cell xls[] = new Cell[nb_lign_xls];
		xls = sheet.getColumn(column);
		String cell[] = new String[nb_lign_xls];
		int i = 0;
		try {
			for (i = 0; i < nb_lign_xls; i++) {
				if (xls[i] == null) {
					cell[i] = "";
				}
				else {
					cell[i] = xls[i].getContents();
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException aioobe) {}
		if (i == 0) {
			throw new ArrayIndexOutOfBoundsException();
		}

		return cell;
	}

	/**
	 * parcourir_actionPerformed: Permet de parcourir les répertoires pour trouver
	 * le fichier à importer
	 *
	 * @param e ActionEvent
	 */
	void parcourir_actionPerformed(ActionEvent e) {

		Debug("parcourir_actionPerforming...");
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR",""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		if (type_txt.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_TXT);
		}
		else if (type_xls.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		}
		else if (type_xml.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		}
		
		if (boiteFichier.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			Program.putCaveConfigString("DIR", boiteFichier.getCurrentDirectory().toString());
			String fic = nomFichier.getAbsolutePath();
			int index = fic.indexOf(".");
			if (index == -1) {
				if (type_xls.isSelected()) {
					Filtre filtre = (Filtre) boiteFichier.getFileFilter();
					if (filtre.toString().equals("xls"))
						fic = fic.concat(".xls");
					if (filtre.toString().equals("ods"))
						fic = fic.concat(".ods");
				}
				if (type_xml.isSelected()) {
					Filtre filtre = (Filtre) boiteFichier.getFileFilter();
					if (filtre.toString().equals("xml"))
						fic = fic.concat(".xml");
				}
				else {
					if (boiteFichier.getFileFilter().getDescription().indexOf("CSV") == -1) {
						fic = fic.concat(".txt");
					}
					else {
						fic = fic.concat(".csv");
					}
				}
			}
			file.setText(fic);
		}
	}

	/**
	 * openit_actionPerformed: Ouverture du fichier à importer
	 *
	 * @param e ActionEvent
	 */
	void openit_actionPerformed(ActionEvent e) {

		Debug("openit_actionPerforming...");
		String nom = file.getText().trim();
		if (!nom.isEmpty()) {
			File f = new File(nom);
			file.setText(f.getAbsolutePath());
			nom = f.getAbsolutePath();
			if(!f.exists()) {
				//Insertion classe Erreur
				label_progression.setText("");
				Debug("ERROR: File not found: "+nom);
				String erreur_txt1 = new String(Program.getError("Error020") + " " + nom + " " + Program.getError("Error021")); //"Fichier " + nom + " non trouvé");
				String erreur_txt2 = Program.getError("Error022"); //"Vérifier le chemin");
				new Erreur(erreur_txt1, erreur_txt2);
				return;
			}
			Program.open(f);
		}
	}

	/**
	 * run: Fonction d'import
	 */
	public void run() {
		try {
			Debug("Running...");
			Debug("Importing...");
			importe.setEnabled(false);
			
			String nom = file.getText().trim();
			int nom_length = nom.length();
			if (nom_length == 0) {
				//Erreur le nom ne doit pas être vide
				Debug("ERROR: filename cannot be empty");
				label_progression.setText("");
				new Erreur(Program.getError("Error019"));
				importe.setEnabled(true);
				return;
			}
			String separe = "";
			int resul = 0;
			int nb_choix = 0;
			String lu[] = new String[15];
			Rangement new_rangement = null;
			int choix_val = 0;
			String XML_NAME = Program.getCaveConfigString("XML_MARK1","wine");
			String XML_YEAR = Program.getCaveConfigString("XML_MARK2","year");
			String XML_TYPE = Program.getCaveConfigString("XML_MARK3","type");
			String XML_PLACE = Program.getCaveConfigString("XML_MARK4","place");
			String XML_NUM_PLACE = Program.getCaveConfigString("XML_MARK5","numplace");
			String XML_LINE = Program.getCaveConfigString("XML_MARK6","line");
			String XML_COLUMN = Program.getCaveConfigString("XML_MARK7","column");
			String XML_PRICE = Program.getCaveConfigString("XML_MARK8","price");
			String XML_COMMENT = Program.getCaveConfigString("XML_MARK9","comment");
			String XML_PARKER = "parker";
			String XML_MATURITY = "maturity";
			String XML_APPELATION = "appelation";

			bool_name = 0;
			bool_year = 0;
			bool_half = 0;
			bool_plac = 0;
			bool_nump = 0;
			bool_line = 0;
			bool_colu = 0;
			bool_pric = 0;
			bool_comm = 0;
			bool_othe1 = 0;
			bool_othe2 = 0;
			bool_othe3 = 0;

			if (choix1.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix2.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix3.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix4.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix5.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix6.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix7.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix8.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix9.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix10.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix11.getSelectedIndex() != 0) {
				nb_choix++;
			}
			if (choix12.getSelectedIndex() != 0) {
				nb_choix++;

				//Verify only 1 use of each field
			}
			for (int i = 0; i < nb_choix; i++) {
				choix_val = 0;
				switch (i) {
				case 0:
					choix_val = choix1.getSelectedIndex();
					break;
				case 1:
					choix_val = choix2.getSelectedIndex();
					break;
				case 2:
					choix_val = choix3.getSelectedIndex();
					break;
				case 3:
					choix_val = choix4.getSelectedIndex();
					break;
				case 4:
					choix_val = choix5.getSelectedIndex();
					break;
				case 5:
					choix_val = choix6.getSelectedIndex();
					break;
				case 6:
					choix_val = choix7.getSelectedIndex();
					break;
				case 7:
					choix_val = choix8.getSelectedIndex();
					break;
				case 8:
					choix_val = choix9.getSelectedIndex();
					break;
				case 9:
					choix_val = choix10.getSelectedIndex();
					break;
				case 10:
					choix_val = choix11.getSelectedIndex();
					break;
				case 11:
					choix_val = choix12.getSelectedIndex();
					break;
				}
				switch (choix_val) {
				case 1:
					bool_name++;
					break;
				case 2:
					bool_year++;
					break;
				case 3:
					bool_half++;
					break;
				case 4:
					bool_plac++;
					break;
				case 5:
					bool_nump++;
					break;
				case 6:
					bool_line++;
					break;
				case 7:
					bool_colu++;
					break;
				case 8:
					bool_pric++;
					break;
				case 9:
					bool_comm++;
					break;
				case 11:
					bool_othe1++;
					break;
				case 12:
					bool_othe2++;
					break;
				case 13:
					bool_othe3++;
					break;
				}
			}
 
			//Ouverture du fichier à importer
			File f = new File(nom);
			file.setText(f.getAbsolutePath());
			nom = f.getAbsolutePath();
			nom_length = nom.length();
			if(!f.exists()) {
				//Insertion classe Erreur
				label_progression.setText("");
				Debug("ERROR: File not found: "+nom);
				String erreur_txt1 = new String(Program.getError("Error020") + " " + nom + " " + Program.getError("Error021")); //"Fichier " + nom + " non trouv�");
				String erreur_txt2 = Program.getError("Error022"); //"Vérifier le chemin");
				new Erreur(erreur_txt1, erreur_txt2);
				importe.setEnabled(true);
				return;
			}
			
			if (!type_xml.isSelected() && nb_choix == 0) {
				label_progression.setText("");
				Debug("ERROR: No field selected");
				String erreur_txt1 = Program.getError("Error025"); //"Aucun champs sélectionnés");
				String erreur_txt2 = Program.getError("Error026"); //"Veuillez sélectionner des champs pour que les donn�es soient trait�es");
				new Erreur(erreur_txt1, erreur_txt2);
				importe.setEnabled(true);
				return;
			}

			if (nom_length >= 3) {
				String str_tmp3 = nom.substring(nom_length - 3);
				if (type_xls.isSelected()) {
					if (str_tmp3.compareToIgnoreCase("xls") != 0 && str_tmp3.compareToIgnoreCase("ods") != 0) {
						label_progression.setText("");
						Debug("ERROR: Not a XLS File");
						String erreur_txt1 = new String(Program.getError("Error034") + " " + str_tmp3); //"Le fichier saisie ne possède pas une extension Excel: " + str_tmp3);
						String erreur_txt2 = Program.getError("Error035"); //"Veuillez saisir le nom d'un fichier XLS.");
						resul = 1;
						new Erreur(erreur_txt1, erreur_txt2);
					}
				}
				else if (type_txt.isSelected()){
					if (str_tmp3.compareToIgnoreCase("txt") != 0 && str_tmp3.compareToIgnoreCase("csv") != 0) {
						label_progression.setText("");
						Debug("ERROR: Not a TXT File");
						String erreur_txt1 = new String(Program.getError("Error023") + " " + str_tmp3); //"Le fichier saisie ne possède pas une extension Texte: " + str_tmp3);
						String erreur_txt2 = Program.getError("Error024"); //"Veuillez saisir le nom d'un fichier TXT ou CSV.");
						resul = 1;
						new Erreur(erreur_txt1, erreur_txt2);
					}
				}
				else {
					if (str_tmp3.compareToIgnoreCase("xml") != 0) {
						label_progression.setText("");
						Debug("ERROR: Not a XML File");
						String erreur_txt1 = new String(Program.getError("Error204") + " " + str_tmp3); //"Le fichier saisie ne poss�de pas une extension Xml: " + str_tmp3);
						String erreur_txt2 = Program.getError("Error205"); //"Veuillez saisir le nom d'un fichier XML.");
						resul = 1;
						new Erreur(erreur_txt1, erreur_txt2);
					}
				}
			}
			
			if(type_xml.isSelected()) {
				label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
				ListeBouteille.loadXML(f);
				importe.setEnabled(true);
				label_progression.setText(Program.getLabel("Infos035")); //"Import Terminé");
				new java.util.Timer().schedule( 
				        new java.util.TimerTask() {
				            @Override
				            public void run() {
				            	SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										label_progression.setText("");
									}
				            	});
				            }
				        }, 
				        5000 
				);
				return;
			}

			if (bool_name > 1 || bool_year > 1 || bool_half > 1 || bool_plac > 1 || bool_nump > 1 || bool_line > 1 || bool_colu > 1 || bool_pric > 1 || bool_comm > 1 || bool_othe1 > 1 || bool_othe2 > 1 ||
					bool_othe3 > 1 && resul == 0) {

				label_progression.setText("");
				Debug("ERROR: fields cannot be selected more than one time");
				String erreur_txt1 = Program.getError("Error017"); //"Un champ ne doit pas être sélectionné 2 fois.");
				String erreur_txt2 = Program.getError("Error018"); //"Veuillez choisir un champ différent pour chaque colonne.");
				new Erreur(erreur_txt1, erreur_txt2);
				resul = 1;
			}
			else if( bool_name == 0) {
				label_progression.setText("");
				Debug("ERROR: No column for wine name");
				String erreur_txt1 = Program.getError("Error142"); //"Aucune colonne n'indique le nom du vin.
				String erreur_txt2 = Program.getError("Error143"); //"Veuillez sélectionner une colonne avec le nom du vin
				new Erreur(erreur_txt1, erreur_txt2);
				resul = 1;
			}
			else if( bool_plac == 0) {
				label_progression.setText("");
				Debug("ERROR: No place defined, a place will be create");
				String erreur_txt1 = Program.getError("Error140"); //Il n'y a pas de rangements définis dans le fichier.
				String erreur_txt2 = Program.getError("Error141"); //Un rangement par défaut va être créé.
				new Erreur(erreur_txt1, erreur_txt2, true);

				int nb_caisse = 0;
				for (Rangement cave : Program.getCave()) {
					if (cave.isCaisse()) {
						nb_caisse++;
					}
				}

				String titre = Program.getLabel("Infos010");
				String message2 = Program.getLabel("Infos308");
				String titre_properties[] = new String[nb_caisse + 2];
				String default_value[] = new String[nb_caisse + 2];
				String key_properties[] = new String[nb_caisse + 2];
				String type_objet[] = new String[nb_caisse + 2];
				int j = 0;
				for (Rangement cave : Program.getCave()) {
					if (cave.isCaisse()) {
						titre_properties[j] = cave.getNom();
						key_properties[j] = "RANGEMENT_DEFAULT";
						default_value[j] = "false";
						type_objet[j] = "MyCellarRadioButton";
						j++;
					}
				}
				titre_properties[nb_caisse] = Program.getLabel("Infos289");
				key_properties[nb_caisse] = "RANGEMENT_DEFAULT";
				default_value[nb_caisse] = "true";
				type_objet[nb_caisse] = "MyCellarRadioButton";
				titre_properties[nb_caisse + 1] = Program.getLabel("Infos307");
				key_properties[nb_caisse + 1] = "RANGEMENT_NAME";
				default_value[nb_caisse + 1] = "";
				type_objet[nb_caisse + 1] = "JTextField";
				MyOptions myoptions = new MyOptions(titre, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), false);
				myoptions.setVisible(true);
				int num_r = Program.getCaveConfigInt("RANGEMENT_DEFAULT",-1);
				if (num_r == Program.GetCaveLength()) {
					String nom1 = Program.getCaveConfigString("RANGEMENT_NAME",""); //Program.options.getValue();
					do {
						do {
							// Contr�le sur le nom
							resul = 0;
							if (nom1.indexOf("\"") != -1 || nom1.indexOf(";") != -1 || nom1.indexOf("<") != -1 || nom1.indexOf(">") != -1 || nom1.indexOf("?") != -1 || nom1.indexOf("\\") != -1 ||
									nom1.indexOf("/") != -1 || nom1.indexOf("|") != -1 || nom1.indexOf("*") != -1) {
								Program.options = new Options(Program.getLabel("Infos020"), Program.getLabel("Infos230"), Program.getLabel("Infos020"), "", nom1,
										Program.getError("Error126"), false);
								Program.options.setVisible(true);
								nom1 = Program.options.getValue();
								Program.options = null;
								resul = 1;
							}
						}
						while (resul != 0);
						if (resul == 0) {
							do {
								// Contr�le sur la longueur du nom
								if (nom1.length() == 0) {
									Program.options = new Options(Program.getLabel("Infos020"), Program.getLabel("Infos230"), Program.getLabel("Infos020"), "", "",
											Program.getError("Error010"), false);
									Program.options.setVisible(true);
									nom1 = Program.options.getValue();
									Program.options = null;
									resul = 1;
								}
							}
							while (nom1.length() == 0);
						}
						if (resul == 0) {
							Rangement rangement;
							do {
								// Contr�le de l'existance du rangement
								rangement = null;
								if (nom1.length() > 0) {
									rangement = Program.getCave(nom1);
								}
								if (rangement != null) {
									Program.options = new Options(Program.getLabel("Infos020"), Program.getLabel("Infos230"), Program.getLabel("Infos020"), "", nom1,
											Program.getError("Error037"), false);
									Program.options.setVisible(true);
									nom1 = Program.options.getValue();
									Program.options = null;
									resul = 1;
								}
							}
							while (rangement != null);
						}
					}
					while (resul != 0);
					resul = 0;
					Debug("Creating new place with name: "+nom1);
					new_rangement = new Rangement(nom1, 1, 0, false, -1);
					Program.addCave(new_rangement);
				}
				else {
					new_rangement = Program.getCave(num_r);
				}
			}
			if (type_txt.isSelected()) {
				//Cas des fichiers TXT
				Debug("Importing Text File...");
				switch (separateur.getSelectedIndex())
				{
				case 0:
					separe = ";";
					break;
				case 1:
					separe = ":";
					break;
				case 2:
					separe = "/";
					break;
				case 3:
					separe = ",";
					break;
				}

				if (resul == 0) {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = reader.readLine();
					if(line != null) {
						String[] tab = line.split(separe);
						if(tab == null || tab.length <= 1) {
							label_progression.setText("");
							Debug("ERROR: No separator found");
							String erreur_txt1 = Program.getError("Error042"); //"Le s�parateur s�lectionn� n'a pas �t� trouv�.");
							String erreur_txt2 = Program.getError("Error043"); //"Veuillez s�lectionner le s�parateur utilis� dans votre fichier.");
							resul = 1;
							new Erreur(erreur_txt1, erreur_txt2);
						}
					}
					if(titre.isSelected())
						line = reader.readLine();
					if(resul == 0) {
						label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
						int maxNumPlace = 0;
						while (line != null) {
							lu = line.split(separe);
							Bouteille bottle = new Bouteille();
							for (int i = 0; i < lu.length; i++) {
								String value = lu[i];
								if(value.charAt(0) == '"' && value.charAt(value.length()-1) == '"')
									value = value.substring(1, value.length() - 1);
								value = Program.convertToHTMLString(value);
								choix_val = 0;
								switch (i) {
								case 0:
									choix_val = choix1.getSelectedIndex();
									break;
								case 1:
									choix_val = choix2.getSelectedIndex();
									break;
								case 2:
									choix_val = choix3.getSelectedIndex();
									break;
								case 3:
									choix_val = choix4.getSelectedIndex();
									break;
								case 4:
									choix_val = choix5.getSelectedIndex();
									break;
								case 5:
									choix_val = choix6.getSelectedIndex();
									break;
								case 6:
									choix_val = choix7.getSelectedIndex();
									break;
								case 7:
									choix_val = choix8.getSelectedIndex();
									break;
								case 8:
									choix_val = choix9.getSelectedIndex();
									break;
								case 9:
									choix_val = choix10.getSelectedIndex();
									break;
								case 10:
									choix_val = choix11.getSelectedIndex();
									break;
								case 11:
									choix_val = choix12.getSelectedIndex();
									break;
								}

								//Ajout des valeurs d'une bouteille dans la Map
								switch (choix_val) { //Insert Keywords from file
								case 1:
									bottle.setNom(value);
									break;
								case 2:
									bottle.setAnnee(value);
									break;
								case 3:
									bottle.setType(value);
									break;
								case 4:
									bottle.setEmplacement(value);
									break;
								case 5:
									bottle.setNumLieu(Integer.parseInt(value));
									if(maxNumPlace < bottle.getNumLieu())
										maxNumPlace = bottle.getNumLieu();
									break;
								case 6:
									bottle.setLigne(Integer.parseInt(value));
									break;
								case 7:
									bottle.setColonne(Integer.parseInt(value));
									break;
								case 8:
									bottle.setPrix(value);
									break;
								case 9:
									bottle.setComment(value);
									break;
								case 10:
									bottle.setMaturity(value);
									break;
								case 11:
									bottle.setParker(value);
									break;
								case 12:
									bottle.setAppellation(value);
									break;
								case 13:
									break;
								}
							}
							if((bottle.getEmplacement() == null || bottle.getEmplacement().isEmpty()) && new_rangement != null ) {
								bottle.setEmplacement(new_rangement.getNom());
								new_rangement.setNbEmplacements(maxNumPlace+1);
							}
							Program.getStorage().addWine(bottle);
							line = reader.readLine();
						}
					}
					reader.close();
				}
				if (resul == 0) {
					label_progression.setText(Program.getLabel("Infos200"));
					new java.util.Timer().schedule( 
					        new java.util.TimerTask() {
					            @Override
					            public void run() {
					            	SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											label_progression.setText("");
										}
					            	});
					            }
					        }, 
					        5000 
					);
					Debug("Import OK.");
				}
			}
			else { //Excel File
				Debug("Importing XLS file...");
				int nb_lign_xls = 0;
				boolean skipTitle = false;
				int max_num_place = 0;

				if (resul == 0) {
					label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
					//Ouverture du fichier Excel
					try {

						Workbook workbook = Workbook.getWorkbook(new File(nom));
						//Sélection de la feuille
						Sheet sheet = workbook.getSheet(0);
						//Lecture de cellules
						nb_lign_xls = sheet.getRows();
						//Get number of column in excel File
						int resul_tmp = 0;
						boolean bool_resul = false;
						do {
							try {
								readXLS(sheet, nb_lign_xls, resul_tmp);
								resul_tmp++;
							}
							catch (ArrayIndexOutOfBoundsException aioobe) {
								bool_resul = true;
							}
						}
						while (resul_tmp < nb_lign_xls && bool_resul != true);
						//Number of columns found in Excel File
						int nbcol_lu = resul_tmp;

						if (resul == 0) {
							//Reading Excel File
							String cell_tmp[][] = new String[nbcol_lu][nb_lign_xls];
							for (int k = 0; k < nbcol_lu; k++) {
								cell_tmp[k] = readXLS(sheet, nb_lign_xls, k);
							}
							//Ecriture du vin pour chaque ligne
							for (int j = 0; j < nb_lign_xls; j++) {
								Debug("Read line :" + j);
								int lu_length = 0;
								try {
									for (int z = 0; z < cell_tmp.length; z++) {
										try {
											lu_length += cell_tmp[z][j].trim().length();
										}
										catch (NullPointerException npe) {}
									}
									if(lu_length > 0 && titre.isSelected() && !skipTitle) {
										Debug("Skipping title line");
										skipTitle = true;
										continue;
									}
								}
								catch (NullPointerException npe) {}
								if (resul == 0) {
									//On met toutes les valeurs r�cup�r�es pour un vin dans une HashMap
									java.util.HashMap<String, String> le_vin = new java.util.HashMap<String, String>(20);
									
									if (lu_length != 0) {
										Bouteille bottle = new Bouteille();
										for (int i = 0; i < nbcol_lu; i++) {
											choix_val = 0;
											//Verify specials characters
											try {
												cell_tmp[i][j].length();
											}
											catch (NullPointerException npe) {
												cell_tmp[i][j] = "";
											}

											cell_tmp[i][j] = Program.convertToHTMLString(cell_tmp[i][j]);

											//Récupération des champs sélectionnés
											switch (i) {
											case 0:
												choix_val = choix1.getSelectedIndex();
												break;
											case 1:
												choix_val = choix2.getSelectedIndex();
												break;
											case 2:
												choix_val = choix3.getSelectedIndex();
												break;
											case 3:
												choix_val = choix4.getSelectedIndex();
												break;
											case 4:
												choix_val = choix5.getSelectedIndex();
												break;
											case 5:
												choix_val = choix6.getSelectedIndex();
												break;
											case 6:
												choix_val = choix7.getSelectedIndex();
												break;
											case 7:
												choix_val = choix8.getSelectedIndex();
												break;
											case 8:
												choix_val = choix9.getSelectedIndex();
												break;
											case 9:
												choix_val = choix10.getSelectedIndex();
												break;
											case 10:
												choix_val = choix11.getSelectedIndex();
												break;
											case 11:
												choix_val = choix12.getSelectedIndex();
												break;
											}
											//Alimentation de la HashMap
											Debug("Write "+ choix_val +"->"+cell_tmp[i][j]);
											switch (choix_val) { //Insert Keywords from file
											case 1:
												le_vin.put(XML_NAME, cell_tmp[i][j]);
												break;
											case 2:
												le_vin.put(XML_YEAR, cell_tmp[i][j]);
												break;
											case 3:
												le_vin.put(XML_TYPE, cell_tmp[i][j]);
												break;
											case 4:
												le_vin.put(XML_PLACE, cell_tmp[i][j]);
												break;
											case 5:
												le_vin.put(XML_NUM_PLACE, cell_tmp[i][j]);
												break;
											case 6:
												le_vin.put(XML_LINE, cell_tmp[i][j]);
												break;
											case 7:
												le_vin.put(XML_COLUMN, cell_tmp[i][j]);
												break;
											case 8:
												le_vin.put(XML_PRICE, cell_tmp[i][j]);
												break;
											case 9:
												le_vin.put(XML_COMMENT, cell_tmp[i][j]);
												break;
											case 10:
												le_vin.put(XML_MATURITY, cell_tmp[i][j]);
												break;
											case 11:
												le_vin.put(XML_PARKER, cell_tmp[i][j]);
												break;
											case 12:
												le_vin.put(XML_APPELATION, cell_tmp[i][j]);
												break;
											case 13:
												break;
											}
										}
										
										//Ecriture du fichier XML
										if (!le_vin.get(XML_NAME).trim().isEmpty()) {

											if (le_vin.containsKey(XML_NAME)) {
												bottle.setNom(le_vin.get(XML_NAME));
											}
											if (le_vin.containsKey(XML_YEAR)) {
												bottle.setAnnee(le_vin.get(XML_YEAR));
											}
											if (le_vin.containsKey(XML_TYPE)) {
												bottle.setType(le_vin.get(XML_TYPE));
											}
											if (le_vin.containsKey(XML_PLACE)) {
												bottle.setEmplacement(le_vin.get(XML_PLACE));
											}
											else {
												//On met toutes les bouteilles dans le nouveau rangement
												bottle.setEmplacement(new_rangement.getNom());
											}
											if (le_vin.containsKey(XML_NUM_PLACE)) {
												bottle.setNumLieu(Integer.parseInt(le_vin.get(XML_NUM_PLACE)));
												String num = le_vin.get(XML_NUM_PLACE);
												if (!le_vin.containsKey(XML_PLACE)) {
													//Si toutes les bouteilles vont dans le nouveau rangement, on calcule le nombre de parties n�cessaires
													try {
														if (Integer.parseInt(num) > max_num_place) {
															new_rangement.setNbEmplacements(Integer.parseInt(num)+1);
														}
													}
													catch (NumberFormatException nfe) {}
												}
											}
											else {
												bottle.setNumLieu(0);
											}
											if (le_vin.containsKey(XML_LINE)) {
												bottle.setLigne(Integer.parseInt(le_vin.get(XML_LINE)));
											}
											else {
												bottle.setLigne(0);
											}
											if (le_vin.containsKey(XML_COLUMN)) {
												bottle.setColonne(Integer.parseInt(le_vin.get(XML_COLUMN)));
											}
											else {
												bottle.setColonne(0);
											}
											if (le_vin.containsKey(XML_PRICE)) {
												bottle.setPrix(le_vin.get(XML_PRICE));
											}
											if (le_vin.containsKey(XML_COMMENT)) {
												bottle.setComment(le_vin.get(XML_COMMENT));
											}
											if (le_vin.containsKey(XML_MATURITY)) {
												bottle.setMaturity(le_vin.get(XML_MATURITY));
											}
											if (le_vin.containsKey(XML_PARKER)) {
												bottle.setParker(le_vin.get(XML_PARKER));
											}
											if (le_vin.containsKey(XML_APPELATION)) {
												bottle.setAppellation(le_vin.get(XML_APPELATION));
											}
										}
										Program.getStorage().addWine(bottle);
									}
									
								} //End if resul
							}
						} //End if resul
					}
					catch (IOException | jxl.read.biff.BiffException ioe1) {
						label_progression.setText("");
						Debug("ERROR: File not found (IO): "+nom);
						String erreur_txt1 = new String(Program.getError("Error020") + " " + nom + " " + Program.getError("Error021")); //"Fichier " + nom + " non trouv�");
						String erreur_txt2 = Program.getError("Error022"); //"Vérifier le chemin");
						new Erreur(erreur_txt1, erreur_txt2);
						importe.setEnabled(true);
						return;
					}
					catch (Exception e) {
						label_progression.setText("");
						Debug("ERROR: "+e.toString());
						new Erreur(Program.getError("Error082"));
						importe.setEnabled(true);
						return;
					}
				} //End if resul

				if (resul == 0) {
					label_progression.setText(Program.getLabel("Infos200"));
					new java.util.Timer().schedule( 
					        new java.util.TimerTask() {
					            @Override
					            public void run() {
					            	SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
											label_progression.setText("");
										}
					            	});
					            }
					        }, 
					        5000 
					);
					Debug("Import OK.");
				}
			}
			importe.setEnabled(true);
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
		RangementUtils.putTabStock();
		if(!Program.getErrors().isEmpty())
			new OpenShowErrorsAction().actionPerformed(null);
	}

	/**
	 * keylistener_actionPerformed: Ecouteur de touche
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == IMPORT && e.isControlDown()) {
			importe_actionPerformed(null);
		}
		if (e.getKeyCode() == OUVRIR && e.isControlDown()) {
			openit_actionPerformed(null);
		}
	}

	/**
	 * couper_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	void couper_actionPerformed(ActionEvent e) {
		String txt = "";
		try {
			JTextField jtf = (JTextField) objet1;
			txt = jtf.getSelectedText();
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
		clipboard.copier(txt);
	}

	/**
	 * copier_actionPerformed: Copier
	 *
	 * @param e ActionEvent
	 */
	void copier_actionPerformed(ActionEvent e) {
		String txt = "";
		try {
			JTextField jtf = (JTextField) objet1;
			txt = jtf.getSelectedText();
		}
		catch (Exception e1) {}
		clipboard.copier(txt);
	}

	/**
	 * coller_actionPerformed: Couper
	 *
	 * @param e ActionEvent
	 */
	void coller_actionPerformed(ActionEvent e) {

		try {
			JTextField jtf = (JTextField) objet1;
			jtf.setText(jtf.getText().substring(0, jtf.getSelectionStart()) + clipboard.coller() + jtf.getText().substring(jtf.getSelectionEnd()));
		}
		catch (Exception e1) {}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("Importer: " + sText);
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
			JTextField jtf = null;
			try {
				jtf = (JTextField) e.getComponent();
				if (jtf.isEnabled() && jtf.isVisible()) {
					objet1 = e.getComponent();
				}
			}
			catch (Exception ee) {}
			;
			try {
				jtf = (JTextField) objet1;
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (jtf.isFocusable() && jtf.isEnabled()) {
						jtf.requestFocus();
						if (jtf.getSelectedText() == null) {
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
					if (jtf.isFocusable() && jtf.isEnabled()) {
						jtf.requestFocus();
						if (jtf.getSelectedText() == null) {
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
			catch (Exception ee) {}
		}
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}

}
