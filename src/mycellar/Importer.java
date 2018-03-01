package mycellar;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 11.2
 * @since 01/03/18
 */
public class Importer extends JPanel implements ITabListener, Runnable {

	private final MyCellarButton importe = new MyCellarButton();
	private final MyCellarRadioButton type_txt = new MyCellarRadioButton();
	private final MyCellarRadioButton type_xls = new MyCellarRadioButton();
	private final MyCellarRadioButton type_xml = new MyCellarRadioButton();
	private final ButtonGroup checkboxGroup1 = new ButtonGroup();
	private final MyCellarButton parcourir = new MyCellarButton();
	private char IMPORT = Program.getLabel("IMPORT").charAt(0);
	private char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private final MyCellarButton openit = new MyCellarButton();
	private final MyCellarComboBox<MyCellarFields> choix1 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix2 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix3 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix4 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix5 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix6 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix7 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix8 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix9 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix10 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix11 = new MyCellarComboBox<>();
	private final MyCellarComboBox<MyCellarFields> choix12 = new MyCellarComboBox<>();
	private final MyCellarCheckBox titre = new MyCellarCheckBox();
	private final MyCellarLabel textControl2 = new MyCellarLabel();
	private final MyCellarLabel label_progression = new MyCellarLabel();
	private final MyCellarLabel label2 = new MyCellarLabel();
	private final MyCellarComboBox<String> separateur = new MyCellarComboBox<>();
	private final MyCellarLabel label1 = new MyCellarLabel();
	private final JTextField file = new JTextField();
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private final JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private final JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private final JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private final JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private final JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private final MyClipBoard clipboard = new MyClipBoard();
	private final JMenuItem quitter = new JMenuItem(Program.getLabel("Infos003"));
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
		openit.setMnemonic(OUVRIR);
		importe.setText(Program.getLabel("Infos036")); //"Importer");
		importe.addActionListener(this::importe_actionPerformed); //"Sélectionner les différents champs présents dans le fichier (de gauche " + "� droite)");
		type_txt.setText(Program.getLabel("Infos040")); //"Fichier TXT ou CSV");
		titre.setHorizontalTextPosition(SwingConstants.LEFT);
		titre.setText(Program.getLabel("Infos038"));
		textControl2.setText(Program.getLabel("Infos037"));
		label_progression.setForeground(Color.red);
		label_progression.setFont(new Font("Dialog", Font.BOLD, 12));
		label_progression.setHorizontalAlignment(SwingConstants.CENTER);
		label2.setText(Program.getLabel("Infos034"));
		label1.setText(Program.getLabel("Infos033"));
		checkboxGroup1.add(type_txt);
		checkboxGroup1.add(type_xls);
		checkboxGroup1.add(type_xml);
		type_txt.addItemListener(this::type_itemStateChanged);
		type_xls.setText(Program.getLabel("Infos041")); //"Fichier Excel");
		parcourir.setText("...");
		openit.setText(Program.getLabel("Infos152")); //"Ouvrir le fichier");
		openit.addActionListener(this::openit_actionPerformed);
		parcourir.addActionListener(this::parcourir_actionPerformed);
		type_xls.addItemListener(this::type_itemStateChanged);
		type_txt.setSelected(true);
		
		type_xml.addItemListener(this::type_itemStateChanged);
		type_xml.setText(Program.getLabel("Infos203")); //"Fichier XML");

		this.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
			@Override
			public void keyTyped(KeyEvent e) {}
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
		file.addMouseListener(popup_l);
		cut.setEnabled(false);
		copy.setEnabled(false);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
		quitter.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.CTRL_DOWN_MASK));

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
		choix1.addItem(MyCellarFields.EMPTY);
		choix2.addItem(MyCellarFields.EMPTY);
		choix3.addItem(MyCellarFields.EMPTY);
		choix4.addItem(MyCellarFields.EMPTY);
		choix5.addItem(MyCellarFields.EMPTY);
		choix6.addItem(MyCellarFields.EMPTY);
		choix7.addItem(MyCellarFields.EMPTY);
		choix8.addItem(MyCellarFields.EMPTY);
		choix9.addItem(MyCellarFields.EMPTY);
		choix10.addItem(MyCellarFields.EMPTY);
		choix11.addItem(MyCellarFields.EMPTY);
		choix12.addItem(MyCellarFields.EMPTY);
		for(MyCellarFields field : list)
		{
			choix1.addItem(field);
			choix2.addItem(field);
			choix3.addItem(field);
			choix4.addItem(field);
			choix5.addItem(field);
			choix6.addItem(field);
			choix7.addItem(field);
			choix8.addItem(field);
			choix9.addItem(field);
			choix10.addItem(field);
			choix11.addItem(field);
			choix12.addItem(field);
		}
		// On ajoute la ligne "Ignorer"
		choix1.addItem(MyCellarFields.USELESS);
		choix2.addItem(MyCellarFields.USELESS);
		choix3.addItem(MyCellarFields.USELESS);
		choix4.addItem(MyCellarFields.USELESS);
		choix5.addItem(MyCellarFields.USELESS);
		choix6.addItem(MyCellarFields.USELESS);
		choix7.addItem(MyCellarFields.USELESS);
		choix8.addItem(MyCellarFields.USELESS);
		choix9.addItem(MyCellarFields.USELESS);
		choix10.addItem(MyCellarFields.USELESS);
		choix11.addItem(MyCellarFields.USELESS);
		choix12.addItem(MyCellarFields.USELESS);

		separateur.addItem(Program.getLabel("Infos042"));
		separateur.addItem(Program.getLabel("Infos043"));
		separateur.addItem(Program.getLabel("Infos044"));
		separateur.addItem(Program.getLabel("Infos002"));

		choix1.addActionListener(this::choix1_actionPerformed);
		choix2.addActionListener(this::choix2_actionPerformed);
		choix3.addActionListener(this::choix3_actionPerformed);
		choix4.addActionListener(this::choix4_actionPerformed);
		choix5.addActionListener(this::choix5_actionPerformed);
		choix6.addActionListener(this::choix6_actionPerformed);
		choix7.addActionListener(this::choix7_actionPerformed);
		choix8.addActionListener(this::choix8_actionPerformed);
		choix9.addActionListener(this::choix9_actionPerformed);
		choix10.addActionListener(this::choix10_actionPerformed);
		choix11.addActionListener(this::choix11_actionPerformed);
		
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
	private void type_itemStateChanged(ItemEvent e) {

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
	private void importe_actionPerformed(ActionEvent e) {
		new Thread(this).start();
	}

	/**
	 * choix1_actionPerformed: Choix
	 *
	 * @param e ActionEvent
	 */
	private void choix1_actionPerformed(ActionEvent e) {
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
	private void choix2_actionPerformed(ActionEvent e) {
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
	private void choix3_actionPerformed(ActionEvent e) {
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
	private void choix4_actionPerformed(ActionEvent e) {
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
	private void choix5_actionPerformed(ActionEvent e) {
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
	private void choix6_actionPerformed(ActionEvent e) {
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
	private void choix7_actionPerformed(ActionEvent e) {
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
	private void choix8_actionPerformed(ActionEvent e) {
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
	private void choix9_actionPerformed(ActionEvent e) {
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
	private void choix10_actionPerformed(ActionEvent e) {
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
	private void choix11_actionPerformed(ActionEvent e) {
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
	private String[] readXLS(Sheet sheet, int nb_lign_xls, int column) throws ArrayIndexOutOfBoundsException {

		Cell []xls = sheet.getColumn(column);
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
	private void parcourir_actionPerformed(ActionEvent e) {

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
					if (!boiteFichier.getFileFilter().getDescription().contains("CSV")) {
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
	private void openit_actionPerformed(ActionEvent e) {

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
				//Fichier non trouvé
				//"Vérifier le chemin");
				Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error020"), nom), Program.getError("Error022"));
				return;
			}
			Program.open(f);
		}
	}

	/**
	 * run: Fonction d'import
	 */
	@Override
	public void run() {
		try {
			Debug("Running...");
			Debug("Importing...");
			importe.setEnabled(false);
			
			String nom = file.getText().trim();
			if (nom.isEmpty()) {
				//Erreur le nom ne doit pas être vide
				Debug("ERROR: filename cannot be empty");
				label_progression.setText("");
				Erreur.showSimpleErreur(Program.getError("Error019"));
				importe.setEnabled(true);
				return;
			}
			int nb_choix = 0;

			int bool_name = 0;
			int bool_year = 0;
			int bool_half = 0;
			int bool_plac = 0;
			int bool_nump = 0;
			int bool_line = 0;
			int bool_colu = 0;
			int bool_pric = 0;
			int bool_comm = 0;
			int bool_othe1 = 0;
			int bool_othe2 = 0;
			int bool_othe3 = 0;
			Rangement new_rangement = null;

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
			int choix_val;
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
			int nom_length = nom.length();
			if(!f.exists()) {
				//Insertion classe Erreur
				label_progression.setText("");
				Debug("ERROR: File not found: "+nom);
				//Fichier non trouvé
				//"Vérifier le chemin");
				Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error020"), nom), Program.getError("Error022"));
				importe.setEnabled(true);
				return;
			}
			
			if (!type_xml.isSelected() && nb_choix == 0) {
				label_progression.setText("");
				Debug("ERROR: No field selected");
				//"Aucun champs sélectionnés");
				//"Veuillez sélectionner des champs pour que les donn�es soient trait�es");
				Erreur.showSimpleErreur(Program.getError("Error025"), Program.getError("Error026"));
				importe.setEnabled(true);
				return;
			}

			boolean resul = true;
			if (nom_length >= 3) {
				String str_tmp3 = nom.substring(nom_length - 3);
				if (type_xls.isSelected()) {
					if (str_tmp3.compareToIgnoreCase("xls") != 0 && str_tmp3.compareToIgnoreCase("ods") != 0) {
						label_progression.setText("");
						Debug("ERROR: Not a XLS File");
						//"Le fichier saisie ne possède pas une extension Excel: " + str_tmp3);
						resul = false;
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), str_tmp3), Program.getError("Error035"));
					}
				}
				else if (type_txt.isSelected()){
					if (str_tmp3.compareToIgnoreCase("txt") != 0 && str_tmp3.compareToIgnoreCase("csv") != 0) {
						label_progression.setText("");
						Debug("ERROR: Not a TXT File");
						//"Le fichier saisie ne possède pas une extension Texte: " + str_tmp3);
						//"Veuillez saisir le nom d'un fichier TXT ou CSV.");
						resul = false;
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error023"), str_tmp3), Program.getError("Error024"));
					}
				}
				else {
					if (str_tmp3.compareToIgnoreCase("xml") != 0) {
						label_progression.setText("");
						Debug("ERROR: Not a XML File");
						//"Le fichier saisie ne possède pas une extension Xml: " + str_tmp3);
						//"Veuillez saisir le nom d'un fichier XML.");
						resul = false;
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error204"), str_tmp3), Program.getError("Error205"));
					}
				}
			}
			
			if(type_xml.isSelected()) {
				label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
				ListeBouteille.loadXML(f);
				importe.setEnabled(true);
				label_progression.setText(Program.getLabel("Infos035")); //"Import Terminé");
				new Timer().schedule(
						new TimerTask() {
								@Override
								public void run() {
									SwingUtilities.invokeLater(() -> label_progression.setText(""));
								}
						},
						5000
				);
				return;
			}

			if (bool_name > 1 || bool_year > 1 || bool_half > 1 || bool_plac > 1 || bool_nump > 1 || bool_line > 1 || bool_colu > 1 || bool_pric > 1 || bool_comm > 1 || bool_othe1 > 1 || bool_othe2 > 1 ||
					bool_othe3 > 1 && resul) {

				label_progression.setText("");
				Debug("ERROR: fields cannot be selected more than one time");
				//"Un champ ne doit pas être sélectionné 2 fois.");
				//"Veuillez choisir un champ différent pour chaque colonne.");
				Erreur.showSimpleErreur(Program.getError("Error017"), Program.getError("Error018"));
				resul = false;
			}
			else if( bool_name == 0) {
				label_progression.setText("");
				Debug("ERROR: No column for wine name");
				//"Aucune colonne n'indique le nom du vin.
				//"Veuillez sélectionner une colonne avec le nom du vin
				Erreur.showSimpleErreur(Program.getError("Error142"), Program.getError("Error143"));
				resul = false;
			}
			else if( bool_plac == 0) {
				label_progression.setText("");
				Debug("ERROR: No place defined, a place will be create");
				//Il n'y a pas de rangements définis dans le fichier.
				//Un rangement par défaut va être créé.
				Erreur.showSimpleErreur(Program.getError("Error140"), Program.getError("Error141"), true);

				int nb_caisse = 0;
				for (Rangement cave : Program.getCave()) {
					if (cave.isCaisse()) {
						nb_caisse++;
					}
				}

				String title = Program.getLabel("Infos010");
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
				MyOptions myoptions = new MyOptions(title, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), false);
				myoptions.setVisible(true);
				int num_r = Program.getCaveConfigInt("RANGEMENT_DEFAULT",-1);
				if (num_r == Program.GetCaveLength()) {
					String nom1 = Program.getCaveConfigString("RANGEMENT_NAME",""); //Program.options.getValue();
					do {
						do {
							// Controle sur le nom
							resul = true;
							if (nom1.contains("\"") || nom1.contains(";") || nom1.contains("<") || nom1.contains(">") || nom1.contains("?") || nom1.contains("\\") ||
									nom1.contains("/") || nom1.contains("|") || nom1.contains("*")) {
								Program.options = new Options(Program.getLabel("Infos020"), Program.getLabel("Infos230"), Program.getLabel("Infos020"), "", nom1,
										Program.getError("Error126"), false);
								Program.options.setVisible(true);
								nom1 = Program.options.getValue();
								Program.options = null;
								resul = false;
							}
						}
						while (!resul);
						if (resul) {
							do {
								// Controle sur la longueur du nom
								if (nom1.isEmpty()) {
									Program.options = new Options(Program.getLabel("Infos020"), Program.getLabel("Infos230"), Program.getLabel("Infos020"), "", "",
											Program.getError("Error010"), false);
									Program.options.setVisible(true);
									nom1 = Program.options.getValue();
									Program.options = null;
									resul = false;
								}
							}
							while (nom1.isEmpty());
						}
						if (resul) {
							Rangement rangement;
							do {
								// Controle de l'existance du rangement
								rangement = null;
								if (!nom1.isEmpty()) {
									rangement = Program.getCave(nom1);
									Program.options = new Options(Program.getLabel("Infos020"), Program.getLabel("Infos230"), Program.getLabel("Infos020"), "", nom1,
											Program.getError("Error037"), false);
									Program.options.setVisible(true);
									nom1 = Program.options.getValue();
									Program.options = null;
									resul = false;
								}
							}
							while (rangement != null);
						}
					}
					while (!resul);
					resul = true;
					Debug("Creating new place with name: "+nom1);
					new_rangement = new Rangement(nom1, 1, 0, false, -1);
					Program.addCave(new_rangement);
				}
				else {
					new_rangement = Program.getCave(num_r);
				}
			}
			if (type_txt.isSelected()) {
				String separe;
				//Cas des fichiers TXT
				Debug("Importing Text File...");
				switch (separateur.getSelectedIndex()) {
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
					default:
						separe = ";";
				}

				if (resul) {
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = reader.readLine();
					if(line != null) {
						String[] tab = line.split(separe);
						if(tab == null || tab.length <= 1) {
							label_progression.setText("");
							Debug("ERROR: No separator found");
							//"Le séparateur sélectionné n'a pas été trouvé.");
							//"Veuillez sélectionner le s�parateur utilis� dans votre fichier.");
							resul = false;
							Erreur.showSimpleErreur(Program.getError("Error042"), Program.getError("Error043"));
						}
					}
					if(titre.isSelected())
						line = reader.readLine();
					if(resul) {
						label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
						int maxNumPlace = 0;
						while (line != null) {
							String []lu = line.split(separe);
							Bouteille bottle = new Bouteille();
							bottle.updateID();
							for (int i = 0; i < lu.length; i++) {
								String value = lu[i];
								if(value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length()-1) == '"')
									value = value.substring(1, value.length() - 1);
								value = Program.convertToHTMLString(value);
								MyCellarFields selectedField = MyCellarFields.USELESS;
								switch (i) {
								case 0:
									selectedField = (MyCellarFields)choix1.getSelectedItem();
									break;
								case 1:
									selectedField = (MyCellarFields)choix2.getSelectedItem();
									break;
								case 2:
									selectedField = (MyCellarFields)choix3.getSelectedItem();
									break;
								case 3:
									selectedField = (MyCellarFields)choix4.getSelectedItem();
									break;
								case 4:
									selectedField = (MyCellarFields)choix5.getSelectedItem();
									break;
								case 5:
									selectedField = (MyCellarFields)choix6.getSelectedItem();
									break;
								case 6:
									selectedField = (MyCellarFields)choix7.getSelectedItem();
									break;
								case 7:
									selectedField = (MyCellarFields)choix8.getSelectedItem();
									break;
								case 8:
									selectedField = (MyCellarFields)choix9.getSelectedItem();
									break;
								case 9:
									selectedField = (MyCellarFields)choix10.getSelectedItem();
									break;
								case 10:
									selectedField = (MyCellarFields)choix11.getSelectedItem();
									break;
								case 11:
									selectedField = (MyCellarFields)choix12.getSelectedItem();
									break;
								}

								//Ajout des valeurs d'une bouteille 
								switch (selectedField) {
								case NAME:
									bottle.setNom(value);
									break;
								case YEAR:
									bottle.setAnnee(value);
									break;
								case TYPE:
									bottle.setType(value);
									break;
								case PLACE:
									bottle.setEmplacement(value);
									break;
								case NUM_PLACE:
									bottle.setNumLieu(Integer.parseInt(value));
									if(maxNumPlace < bottle.getNumLieu())
										maxNumPlace = bottle.getNumLieu();
									break;
								case LINE:
									bottle.setLigne(Integer.parseInt(value));
									break;
								case COLUMN:
									bottle.setColonne(Integer.parseInt(value));
									break;
								case PRICE:
									bottle.setPrix(value);
									break;
								case COMMENT:
									bottle.setComment(value);
									break;
								case MATURITY:
									bottle.setMaturity(value);
									break;
								case PARKER:
									bottle.setParker(value);
									break;
								case VINEYARD:
									bottle.setAppellation(value);
									break;
								case COLOR:
									bottle.setColor(value);
									break;
								case COUNTRY:
									bottle.getVignoble().setCountry(value);
									break;
								case AOC:
									bottle.getVignoble().setAOC(value);
									break;
								case IGP:
									bottle.getVignoble().setIGP(value);
									break;
									default:
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
				if (resul) {
					label_progression.setText(Program.getLabel("Infos200"));
					new Timer().schedule(
					        new java.util.TimerTask() {
					            @Override
					            public void run() {
					            	SwingUtilities.invokeLater(() -> label_progression.setText(""));
					            }
					        }, 
					        5000 
					);
					Debug("Import OK.");
				}
			}
			else { //Excel File
				Debug("Importing XLS file...");

				if (resul) {
					label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
					//Ouverture du fichier Excel
					try {

						Workbook workbook = Workbook.getWorkbook(new File(nom));
						//Sélection de la feuille
						Sheet sheet = workbook.getSheet(0);
						//Lecture de cellules
						int nb_lign_xls = sheet.getRows();
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
						while (resul_tmp < nb_lign_xls && !bool_resul);
						//Number of columns found in Excel File
						int nbcol_lu = resul_tmp;

						if (resul) {
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
									boolean skipTitle = false;
									if(lu_length > 0 && titre.isSelected()) {
										Debug("Skipping title line");
										skipTitle = true;
										continue;
									}
								}
								catch (NullPointerException npe) {}
								if (resul) {
									int maxNumPlace = 0;
									if (lu_length != 0) {
										Bouteille bottle = new Bouteille();
										bottle.updateID();
										for (int i = 0; i < nbcol_lu; i++) {
											//Verify specials characters
											try {
												cell_tmp[i][j].length();
											}
											catch (NullPointerException npe) {
												cell_tmp[i][j] = "";
											}

											cell_tmp[i][j] = Program.convertToHTMLString(cell_tmp[i][j]);

											//Récupération des champs sélectionnés
											MyCellarFields selectedField = MyCellarFields.USELESS;
											switch (i) {
											case 0:
												selectedField = (MyCellarFields)choix1.getSelectedItem();
												break;
											case 1:
												selectedField = (MyCellarFields)choix2.getSelectedItem();
												break;
											case 2:
												selectedField = (MyCellarFields)choix3.getSelectedItem();
												break;
											case 3:
												selectedField = (MyCellarFields)choix4.getSelectedItem();
												break;
											case 4:
												selectedField = (MyCellarFields)choix5.getSelectedItem();
												break;
											case 5:
												selectedField = (MyCellarFields)choix6.getSelectedItem();
												break;
											case 6:
												selectedField = (MyCellarFields)choix7.getSelectedItem();
												break;
											case 7:
												selectedField = (MyCellarFields)choix8.getSelectedItem();
												break;
											case 8:
												selectedField = (MyCellarFields)choix9.getSelectedItem();
												break;
											case 9:
												selectedField = (MyCellarFields)choix10.getSelectedItem();
												break;
											case 10:
												selectedField = (MyCellarFields)choix11.getSelectedItem();
												break;
											case 11:
												selectedField = (MyCellarFields)choix12.getSelectedItem();
												break;
											}
											//Alimentation de la HashMap
											Debug("Write "+ selectedField +"->"+cell_tmp[i][j]);
											switch (selectedField) {
											case NAME:
												bottle.setNom(cell_tmp[i][j]);
												break;
											case YEAR:
												bottle.setAnnee(cell_tmp[i][j]);
												break;
											case TYPE:
												bottle.setType(cell_tmp[i][j]);
												break;
											case PLACE:
												bottle.setEmplacement(cell_tmp[i][j]);
												break;
											case NUM_PLACE:
												bottle.setNumLieu(Integer.parseInt(cell_tmp[i][j]));
												if(maxNumPlace < bottle.getNumLieu())
													maxNumPlace = bottle.getNumLieu();
												break;
											case LINE:
												bottle.setLigne(Integer.parseInt(cell_tmp[i][j]));
												break;
											case COLUMN:
												bottle.setColonne(Integer.parseInt(cell_tmp[i][j]));
												break;
											case PRICE:
												bottle.setPrix(cell_tmp[i][j]);
												break;
											case COMMENT:
												bottle.setComment(cell_tmp[i][j]);
												break;
											case MATURITY:
												bottle.setMaturity(cell_tmp[i][j]);
												break;
											case PARKER:
												bottle.setParker(cell_tmp[i][j]);
												break;
											case VINEYARD:
												bottle.setAppellation(cell_tmp[i][j]);
												break;
											case COLOR:
												bottle.setColor(cell_tmp[i][j]);
												break;
											case COUNTRY:
												bottle.getVignoble().setCountry(cell_tmp[i][j]);
												break;
											case AOC:
												bottle.getVignoble().setAOC(cell_tmp[i][j]);
												break;
											case IGP:
												bottle.getVignoble().setIGP(cell_tmp[i][j]);
												break;
												default:
													break;
											}
										}
										Program.getStorage().addWine(bottle);
									}
									
								} //End if resul
							}
						} //End if resul
					}
					catch (IOException | BiffException e) {
						label_progression.setText("");
						Debug("ERROR: File not found (IO): "+nom);
						//Fichier non trouvé
						//"Vérifier le chemin");
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error020"), nom), Program.getError("Error022"));
						importe.setEnabled(true);
						return;
					}
					catch (Exception e) {
						Program.showException(e, false);
						label_progression.setText("");
						Debug("ERROR: "+e.toString());
						Erreur.showSimpleErreur(Program.getError("Error082"));
						importe.setEnabled(true);
						return;
					}
				} //End if resul

				if (resul) {
					label_progression.setText(Program.getLabel("Infos200"));
					new Timer().schedule(
					        new TimerTask() {
					            @Override
					            public void run() {
					            	SwingUtilities.invokeLater(() -> label_progression.setText(""));
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
	private void couper_actionPerformed(ActionEvent e) {
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
	private void copier_actionPerformed(ActionEvent e) {
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
	private void coller_actionPerformed(ActionEvent e) {

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
