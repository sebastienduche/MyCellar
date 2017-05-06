package mycellar;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import net.miginfocom.swing.MigLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 4.8
 * @since 17/01/17
 */
public class Creer_Tableaux extends JPanel implements ITabListener {
	private MyCellarLabel label2 = new MyCellarLabel();
	private JTextField name = new JTextField();
	private MyCellarButton browse = new MyCellarButton();
	private MyCellarButton parameter = new MyCellarButton();
	private MyCellarLabel label3 = new MyCellarLabel();
	private JScrollPane scrollPane1 = new JScrollPane();
	private JButton create = new JButton();
	private ButtonGroup checkboxGroup1 = new ButtonGroup();
	private MyCellarRadioButton type_XML = new MyCellarRadioButton();
	private MyCellarRadioButton type_HTML = new MyCellarRadioButton();
	private MyCellarRadioButton type_XLS = new MyCellarRadioButton();
	private JTable table;
	private TableauValues tv = new TableauValues();
	private MyCellarLabel end = new MyCellarLabel();
	private MyCellarButton preview = new MyCellarButton();
	private char CREER = Program.getLabel("CREER").charAt(0);
	private char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private MyCellarCheckBox selectall = new MyCellarCheckBox();
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private MyClipBoard clipboard = new MyClipBoard();
	private JMenuItem quitter = new JMenuItem(Program.getLabel("Infos003"));
	private JMenuItem param = new JMenuItem(Program.getLabel("Infos156"));
	private Component objet1 = null;
	private boolean isJFile = false;
	private MyCellarCheckBox m_jcb_options = new MyCellarCheckBox(Program.getLabel("Infos193") + "...");
	private XLSTabOptions oXLSTabOptions = null;
	static final long serialVersionUID = 260706;

	/**
	 * Creer_Tableaux: Constructeur pour la création des tableaux.
	 *
	 */
	public Creer_Tableaux() {
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
		Debug("jbInit with Rangement[]");
		label2.setText(Program.getLabel("Infos095")); //"Nom du fichier généré:");
		m_jcb_options.addActionListener((e) -> options_actionPerformed(e));
		browse.setText("...");
		browse.addActionListener((e) -> browse_actionPerformed(e));
		parameter.setText(Program.getLabel("Main.Parameters"));
		parameter.addActionListener((e) -> param_actionPerformed(e));
		label3.setText(Program.getLabel("Infos096")); //"Sélectionner les rangements à générer:");
		create.setText(Program.getLabel("Infos018")); //"Créer");
		create.setMnemonic(CREER);
		
		type_XML.setText(Program.getLabel("Infos210"));
		type_HTML.setText(Program.getLabel("Infos211"));
		type_XLS.setText(Program.getLabel("Infos233"));
		checkboxGroup1.add(type_HTML);
		checkboxGroup1.add(type_XML);
		checkboxGroup1.add(type_XLS);
		table = new JTable(tv);
		TableColumnModel tcm = table.getColumnModel();
		TableColumn tc = tcm.getColumn(TableauValues.ETAT);
		tc.setCellRenderer(new StateRenderer());
		tc.setCellEditor(new StateEditor());
		tc.setMinWidth(25);
		tc.setMaxWidth(25);

		type_XML.addActionListener((e) -> jradio_actionPerformed(e));
		type_HTML.addActionListener((e) -> jradio_actionPerformed(e));
		type_XLS.addActionListener((e) -> jradio_actionPerformed(e));

		for (int i = 0; i < Program.GetCaveLength(); i++) {
			tv.addRangement(Program.getCave(i));
		}
		scrollPane1 = new JScrollPane(table);
		end.setHorizontalAlignment(0);
		end.setForeground(Color.red);
		end.setFont(Program.font_dialog_small);
		preview.setText(Program.getLabel("Infos152")); //"Ouvrir le fichier");
		preview.setMnemonic(OUVRIR);
		selectall.setText(Program.getLabel("Infos126")); //"Tout sélectionner");
		selectall.setHorizontalAlignment(4);
		selectall.setHorizontalTextPosition(2);
		selectall.addActionListener((e) -> selectall_actionPerformed(e));
		preview.addActionListener((e) -> preview_actionPerformed(e));
		create.addActionListener((e) -> create_actionPerformed(e));
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
		param.addActionListener((e) -> param_actionPerformed(e));
		couper.setEnabled(false);
		copier.setEnabled(false);
		popup.add(couper);
		popup.add(copier);
		popup.add(coller);
		MouseListener popup_l = new PopupListener();
		name.addMouseListener(popup_l);
		cut.setEnabled(false);
		copy.setEnabled(false);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));
		quitter.setAccelerator(KeyStroke.getKeyStroke('Q', ActionEvent.CTRL_MASK));

		m_jcb_options.setEnabled(false);
		switch ( Program.getCaveConfigInt("CREATE_TAB_DEFAULT", 1) ) {
		case 0:
			type_XML.setSelected(true);
			break;
		case 1:
			type_HTML.setSelected(true);
			break;
		case 2:
			type_XLS.setSelected(true);
			m_jcb_options.setEnabled(true);
			break;
		}

		setLayout(new MigLayout("","grow","[][][grow]"));
		JPanel panelFile = new JPanel();
		panelFile.setLayout(new MigLayout("","grow",""));
		panelFile.add(label2, "wrap");
		panelFile.add(name, "grow, split 3");
		panelFile.add(browse);
		panelFile.add(parameter, "push");
		add(panelFile, "grow, wrap");
		JPanel panelType = new JPanel();
		panelType.setLayout(new MigLayout("","[grow][grow][grow]",""));
		panelType.add(type_XML);
		panelType.add(type_HTML);
		panelType.add(type_XLS);
		panelType.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos151")));
		add(panelType, "grow, wrap");
		JPanel panelTable = new JPanel();
		panelTable.setLayout(new MigLayout("","grow","grow"));
		panelTable.add(label3,"wrap");
		panelTable.add(scrollPane1, "grow, wrap");
		panelTable.add(m_jcb_options, "split 2");
		panelTable.add(selectall, "grow, push, wrap");
		panelTable.add(end, "grow, center, hidemode 3, wrap");
		panelTable.add(create, "gaptop 15px, split 2, center");
		panelTable.add(preview);
		add(panelTable, "grow");
		preview.setEnabled(false);
		Debug("jbInit OK");
	}

	/**
	 * browse_actionPerformed: Bouton parcourir.
	 *
	 * @param e ActionEvent
	 */
	void browse_actionPerformed(ActionEvent e) {

		Debug("browse_actionPerforming...");
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString("DIR",""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		if (type_XML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
		}
		if ( type_HTML.isSelected() ) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
		}
		if ( type_XLS.isSelected() ) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTM);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		}

		String fic = "";
		if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			String nom = boiteFichier.getSelectedFile().getName();
			Program.putCaveConfigString("DIR", boiteFichier.getCurrentDirectory().toString());
			Filtre filtre = (Filtre) boiteFichier.getFileFilter();
			//Erreur utilisation de caractères interdits
			if (nom.indexOf("\"") != -1 || nom.indexOf(";") != -1 || nom.indexOf("<") != -1 || nom.indexOf(">") != -1 || nom.indexOf("?") != -1 || nom.indexOf("\\") != -1 || nom.indexOf("/") != -1 ||
					nom.indexOf("|") != -1 || nom.indexOf("*") != -1) {
				new Erreur(Program.getError("Error126"), "");
			}
			else {
				fic = nomFichier.getAbsolutePath();
				int index = fic.indexOf(".");
				if (index == -1) {
					if (type_XML.isSelected()) {
						fic = fic.concat(".xml");
					}
					if ( type_HTML.isSelected() ) {
						fic = fic.concat(".htm");
						if (filtre.toString().equals("html"))
							fic = fic.concat("l");
					}
					if (type_XLS.isSelected()) {
						if (filtre.toString().equals("xls"))
							fic = fic.concat(".xls");
						if (filtre.toString().equals("ods"))
							fic = fic.concat(".ods");
					}
				}
				name.setText(fic);
				isJFile = true;
			}
			end.setText("");
		}
	}

	/**
	 * create_actionPerformed: Fonction de création des tableaux.
	 *
	 * @param e ActionEvent
	 */
	void create_actionPerformed(ActionEvent e) {
		try {
			Debug("create_actionPerforming...");
			String nom = name.getText();
			nom = nom.trim();
			int nom_length = nom.length();
			String erreur_txt1 = "";
			String erreur_txt2 = "";
			int resul = 0;

			if (nom_length == 0) {
				Debug("ERROR: file empty");
				erreur_txt1 = Program.getError("Error019"); //"Le nom du fichier ne doit pas être vide");
				resul = 1;
				new Erreur(erreur_txt1, "");
			}

			if (resul == 0 && !isJFile) {
				//Erreur utilisation de caractères interdits
				if (nom.indexOf("\"") != -1 || nom.indexOf(";") != -1 || nom.indexOf("<") != -1 || nom.indexOf(">") != -1 ||
						nom.indexOf("?") != -1 || nom.indexOf("|") != -1 || nom.indexOf("*") != -1) {
					Debug("ERROR: Forbidden characters");
					erreur_txt1 = Program.getError("Error126");
					resul = 1;
					new Erreur(erreur_txt1, "");
				}
			}

			if (resul == 0) {
				nom.trim();
				java.io.File path = new java.io.File(nom);
				name.setText(path.getAbsolutePath());

				//Verify file type. Is it XML File?
				if (type_XML.isSelected()) {
					if ( !nom.toLowerCase().endsWith(".xml") ) {
						Debug("ERROR: Not a XML File");
						erreur_txt1 = new String(Program.getError("Error087")); //Non XML File
						erreur_txt2 = Program.getError("Error088"); //"Veuillez saisir le nom d'un fichier XML.");
						resul = 1;
						new Erreur(erreur_txt1, erreur_txt2);
					}
				}
				if ( type_HTML.isSelected() ) {
					if ( !nom.toLowerCase().endsWith(".html") && !nom.toLowerCase().endsWith(".htm") ) {
						Debug("ERROR: Not a HTML File");
						erreur_txt1 = new String(Program.getError("Error107")); //Non XML File
						resul = 1;
						new Erreur(erreur_txt1, "");
					}
				}
				if ( type_XLS.isSelected() ) {
					if ( !nom.toLowerCase().endsWith(".xls") && !nom.toLowerCase().endsWith(".ods") ) {
						Debug("ERROR: Not a XLS File");
						erreur_txt1 = new String(Program.getError("Error034")); //Non XLS File
						resul = 1;
						new Erreur(erreur_txt1, "");
					}
				}
			}
			int listToGen[] = new int[1];
			int k = 0;
			if (resul == 0) {
				int count = 0;
				int max_row = tv.getRowCount();
				int row = 0;
				do {
					if (tv.getValueAt(row, 3).toString() == "true") {
						count++;
					}
					row++;
				}
				while (row < max_row);

				if (count == 0) {
					Debug("ERROR: No place selected");
					erreur_txt1 = Program.getError("Error089"); //"Aucun rangement sélectionné!");
					erreur_txt2 = Program.getError("Error090"); //"Veuillez sélectionner les rangements à générer.");
					resul = 1;
					new Erreur(erreur_txt1, erreur_txt2, true);
				}
				else {
					listToGen = new int[count];
					row = 0;
					k = 0;
					do {
						if (tv.getValueAt(row, 3).toString() == "true") {
							listToGen[k] = row;
							k++;
						}
						row++;
					}
					while (row < max_row);
				}
			}
			if (resul == 0) {

				int caisse_select = 0;

				// Export XML
				if (type_XML.isSelected()) {
					Debug("Exporting in XML in progress...");
					LinkedList<Rangement> rangements = new LinkedList<Rangement>();
					for (int j = 0; j < listToGen.length; j++)
						rangements.add(Program.getCave(listToGen[j]));
					MyXmlDom.writeRangements(nom, rangements, false);
				}

				// Export HTML
				if ( type_HTML.isSelected() ) {
					Debug("Exporting in HTML in progress...");
					LinkedList<Rangement> rangements = new LinkedList<Rangement>();
					for (int j = 0; j < listToGen.length; j++)
						rangements.add(Program.getCave(listToGen[j]));
					MyXmlDom.writeRangements(Program.getPreviewXMLFileName(), rangements, false);
			
				    TransformerFactory tFactory = TransformerFactory.newInstance();

		            Source xslDoc = new StreamSource("resources/Rangement.xsl");
		            Source xmlDoc = new StreamSource(Program.getPreviewXMLFileName());

		            OutputStream htmlFile = new FileOutputStream(nom);

		            Transformer transformer = tFactory.newTransformer(xslDoc);
		            transformer.transform(xmlDoc, new StreamResult(htmlFile));
				}

				//Export XLS
				if ( type_XLS.isSelected() ) {
					Debug("Exporting in XLS in progress...");
					java.util.LinkedList<Rangement> oList = new java.util.LinkedList<Rangement>();
					for ( int j = 0; j < listToGen.length; j++ )
					{
						oList.add( Program.getCave(listToGen[j]) );
						if ( Program.getCave(listToGen[j]).isCaisse() )
							caisse_select++;
					}
					Rangement.write_XLSTab( nom, oList );
				}

				int key = Program.getCaveConfigInt("DONT_SHOW_TAB_MESS", 0);

				if (key == 0) {
					if (caisse_select >= 1) {
						if ( caisse_select == 1){
							erreur_txt1 = Program.getError("Error091"); //"Vous avez sélectionné un rangement de type Caisse");
							erreur_txt2 = Program.getError("Error092"); //"Une liste des vins de ce rangement a été générée.");
						}else{
							erreur_txt1 = Program.getError("Error127"); //"Vous avez sélectionné des rangements de type Caisse");
							erreur_txt2 = Program.getError("Error128"); //"Une liste des vins de ces rangements a été générée.");
						}
						new Erreur(erreur_txt1, erreur_txt2, true, "", true, "DONT_SHOW_TAB_MESS");
					}
				}
				end.setText(Program.getLabel("Infos097")); //"Fichier généré.");
				preview.setEnabled(true);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * preview_actionPerformed: Visualiser les rangements.
	 *
	 * @param e ActionEvent
	 */
	void preview_actionPerformed(ActionEvent e) {
		Debug("preview_actionPerforming...");
		String path = name.getText();
		Program.open( new File(path) );
	}

	/**
	 * keylistener_actionPerformed: Foncion d'écoute des touches.
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == CREER && e.isControlDown()) {
			create_actionPerformed(null);
		}
		if (e.getKeyCode() == OUVRIR && e.isControlDown() && preview.isEnabled()) {
			preview_actionPerformed(null);
		}
	}

	/**
	 * this_windowActivated: Mis au premier plan de l'Program.erreur.
	 *
	 * @param e WindowEvent
	 */
	void this_windowActivated(WindowEvent e) {
		if ( oXLSTabOptions != null ){
			oXLSTabOptions.toFront();
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
	 * selectall_actionPerformed: Permet de sélectionner toutes les lignes de la
	 * JTable
	 *
	 * @param e ActionEvent
	 */
	void selectall_actionPerformed(ActionEvent e) {
		end.setText("");
		if (selectall.isSelected() == true) {
			for (int i = 0; i < tv.getRowCount(); i++) {
				tv.setValueAt(new Boolean(true), i, 3);
			}
		}
		else {
			for (int i = 0; i < tv.getRowCount(); i++) {
				tv.setValueAt(new Boolean(false), i, 3);
			}
		}
		//scrollPane1.updateUI();
	}

	/**
	 * options_actionPerformed: Appel de la fenêtre d'options.
	 *
	 * @param e ActionEvent
	 */
	void options_actionPerformed(ActionEvent e) {

		end.setText("");
		oXLSTabOptions = new XLSTabOptions();
		oXLSTabOptions.setVisible(true);
		oXLSTabOptions = null;
		m_jcb_options.setSelected(false);
	}

	/**
	 * jradio_actionPerformed: Bouton radio.
	 *
	 * @param e ActionEvent
	 */
	void jradio_actionPerformed(ActionEvent e) {
		end.setText("");
		if ( type_XLS.isSelected() ) {
			m_jcb_options.setEnabled( true );
		}
		else {
			m_jcb_options.setEnabled( false );
		}
	}

	/**
	 * param_actionPerformed: Appelle la fenêtre de paramètres.
	 *
	 * @param e ActionEvent
	 */
	void param_actionPerformed(ActionEvent e) {
		Debug("param_actionPerforming...");
		String titre = Program.getLabel("Infos310");
		String message2 = Program.getLabel("Infos309");
		String titre_properties[] = new String[3];
		titre_properties[0] = Program.getLabel("Infos210");
		titre_properties[1] = Program.getLabel("Infos211");
		titre_properties[2] = Program.getLabel("Infos233");
		String default_value[] = new String[3];
		String key_properties[] = new String[3];
		key_properties[0] = "CREATE_TAB_DEFAULT";
		key_properties[1] = "CREATE_TAB_DEFAULT";
		key_properties[2] = "CREATE_TAB_DEFAULT";
		String val = Program.getCaveConfigString(key_properties[0], "1");

		default_value[0] = "false";
		default_value[1] = "false";
		default_value[2] = "false";
		if (val.equals("0")) {
			default_value[0] = "true";
		}
		if (val.equals("1")) {
			default_value[1] = "true";
		}
		if (val.equals("2")) {
			default_value[2] = "true";
		}
		String type_objet[] = {"MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton"};
		MyOptions myoptions = new MyOptions(titre, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), false);
		myoptions.setVisible(true);
	}


	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("Creer_Tableaux: " + sText);
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

	public void updateView() {
		SwingUtilities.invokeLater(() -> {
			tv.removeAll();
			for (int i = 0; i < Program.GetCaveLength(); i++) {
				tv.addRangement(Program.getCave(i));
			}
		});
	}
}
