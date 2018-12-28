package mycellar;

import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 6.5
 * @since 28/12/18
 */
public class Creer_Tableaux extends JPanel implements ITabListener, ICutCopyPastable {
	private final JTextField name = new JTextField();
	private final MyCellarRadioButton type_XML = new MyCellarRadioButton();
	private final MyCellarRadioButton type_HTML = new MyCellarRadioButton();
	private final MyCellarRadioButton type_XLS = new MyCellarRadioButton();
	private JTable table;
	private final TableauValues tv = new TableauValues();
	private final MyCellarLabel end = new MyCellarLabel();
	private final MyCellarButton preview = new MyCellarButton();
	private final char CREER = Program.getLabel("CREER").charAt(0);
	private final char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private final MyCellarCheckBox selectall = new MyCellarCheckBox();
	private final MyCellarButton m_jcb_options = new MyCellarButton(Program.getLabel("Infos193") + "...");
	static final long serialVersionUID = 260706;

	/**
	 * Creer_Tableaux: Constructeur pour la création des tableaux.
	 *
	 */
	public Creer_Tableaux() {
		Debug("Constructor");
		try {
			final MyCellarLabel fileLabel = new MyCellarLabel(Program.getLabel("Infos095")); //"Nom du fichier généré:");
			m_jcb_options.addActionListener(this::options_actionPerformed);
			final MyCellarButton browse = new MyCellarButton("...");
			browse.addActionListener(this::browse_actionPerformed);
			final MyCellarButton parameter = new MyCellarButton(Program.getLabel("Main.Parameters"));
			parameter.addActionListener(this::param_actionPerformed);
			final MyCellarLabel chooseLabel = new MyCellarLabel(Program.getLabel("Infos096")); //"Sélectionner les rangements à générer:");
			final MyCellarButton create = new MyCellarButton(Program.getLabel("Infos018")); //"Créer");
			create.setMnemonic(CREER);

			type_XML.setText(Program.getLabel("Infos210"));
			type_HTML.setText(Program.getLabel("Infos211"));
			type_XLS.setText(Program.getLabel("Infos233"));
			final ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(type_HTML);
			buttonGroup.add(type_XML);
			buttonGroup.add(type_XLS);
			table = new JTable(tv);
			table.setAutoCreateRowSorter(true);
			TableColumnModel tcm = table.getColumnModel();
			TableColumn tc = tcm.getColumn(TableauValues.ETAT);
			tc.setCellRenderer(new StateRenderer());
			tc.setCellEditor(new StateEditor());
			tc.setMinWidth(25);
			tc.setMaxWidth(25);

			type_XML.addActionListener(this::jradio_actionPerformed);
			type_HTML.addActionListener(this::jradio_actionPerformed);
			type_XLS.addActionListener(this::jradio_actionPerformed);

			for (Rangement r : Program.getCave()) {
				tv.addRangement(r);
			}
			JScrollPane jScrollPane = new JScrollPane(table);
			end.setHorizontalAlignment(SwingConstants.CENTER);
			end.setForeground(Color.red);
			end.setFont(Program.FONT_DIALOG_SMALL);
			preview.setText(Program.getLabel("Infos152")); //"Ouvrir le fichier");
			preview.setMnemonic(OUVRIR);
			selectall.setText(Program.getLabel("Infos126")); //"Tout sélectionner");
			selectall.setHorizontalAlignment(SwingConstants.RIGHT);
			selectall.setHorizontalTextPosition(SwingConstants.LEFT);
			selectall.addActionListener(this::selectall_actionPerformed);
			preview.addActionListener(this::preview_actionPerformed);
			create.addActionListener(this::create_actionPerformed);
			addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					keylistener_actionPerformed(e);
				}
			});

			name.addMouseListener(new PopupListener());

			m_jcb_options.setEnabled(false);
			switch (Program.getCaveConfigInt(MyCellarSettings.CREATE_TAB_DEFAULT, 1)) {
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
			final JPanel panelFile = new JPanel();
			panelFile.setLayout(new MigLayout("","grow",""));
			panelFile.add(fileLabel, "wrap");
			panelFile.add(name, "grow, split 3");
			panelFile.add(browse);
			panelFile.add(parameter, "push");
			add(panelFile, "grow, wrap");
			final JPanel panelType = new JPanel();
			panelType.setLayout(new MigLayout("","[grow][grow][grow]",""));
			panelType.add(type_XML);
			panelType.add(type_HTML);
			panelType.add(type_XLS, "split 2");
			panelType.add(m_jcb_options, "push");
			panelType.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos151")));
			add(panelType, "grow, wrap");
			final JPanel panelTable = new JPanel();
			panelTable.setLayout(new MigLayout("","grow","grow"));
			panelTable.add(chooseLabel,"wrap");
			panelTable.add(jScrollPane, "grow, wrap");
			panelTable.add(selectall, "grow, push, wrap");
			panelTable.add(end, "grow, center, hidemode 3, wrap");
			panelTable.add(create, "gaptop 15px, split 2, center");
			panelTable.add(preview);
			add(panelTable, "grow");
			preview.setEnabled(false);
			Debug("Constructor OK");
		}
		catch (Exception e) {
			Program.showException(e);
		}
	}

	/**
	 * browse_actionPerformed: Bouton parcourir.
	 *
	 * @param e ActionEvent
	 */
	private void browse_actionPerformed(ActionEvent e) {

		Debug("browse_actionPerforming...");
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR,""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		if (type_XML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
		} else if (type_HTML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		} else if (type_XLS.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		}

		if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = boiteFichier.getSelectedFile();
			String nom = file.getAbsolutePath();
			Program.putCaveConfigString(MyCellarSettings.DIR, boiteFichier.getCurrentDirectory().toString());
			Filtre filtre = (Filtre) boiteFichier.getFileFilter();
			nom = MyCellarControl.controlAndUpdateExtension(nom, filtre);
			name.setText(nom);
		}
	}

	/**
	 * create_actionPerformed: Fonction de création des tableaux.
	 *
	 * @param e ActionEvent
	 */
	private void create_actionPerformed(ActionEvent e) {
		try {
			Debug("create_actionPerforming...");
			String nom = name.getText().trim();

			if (!MyCellarControl.controlPath(nom)) {
				return;
			}

			File path = new File(nom.trim());
			name.setText(path.getAbsolutePath());

			//Verify file type. Is it XML File?
			if (type_XML.isSelected()) {
				if (!MyCellarControl.controlExtension(nom, Arrays.asList(Filtre.FILTRE_XML.toString()))) {
					Debug("ERROR: Not a XML File");
					//"Le fichier saisie ne possède pas une extension XML: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error087"), nom));
					return;
				}
			} else if (type_HTML.isSelected()) {
				if (!MyCellarControl.controlExtension(nom, Arrays.asList(Filtre.FILTRE_HTML.toString()))) {
					Debug("ERROR: Not a HTML File");
					//"Le fichier saisie ne possède pas une extension HTML: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error107"), nom));
					return;
				}
			} else if (type_XLS.isSelected()) {
				if (!MyCellarControl.controlExtension(nom, Arrays.asList(Filtre.FILTRE_XLS.toString(), Filtre.FILTRE_ODS.toString()))) {
					Debug("ERROR: Not a XLS File");
					//"Le fichier saisie ne possède pas une extension Excel: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error34"), nom));
					return;
				}
			}
//
//			if (type_XML.isSelected()) {
//				if (!nom.toLowerCase().endsWith(".xml")) {
//					Debug("ERROR: Not a XML File");
//					//Non XML File
//					//"Veuillez saisir le nom d'un fichier XML.");
//					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error087"), nom), Program.getError("Error088"));
//					return;
//				}
//			} else if (type_HTML.isSelected()) {
//				if (!nom.toLowerCase().endsWith(".html") && !nom.toLowerCase().endsWith(".htm")) {
//					Debug("ERROR: Not a HTML File");
//					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error107"), nom));
//					return;
//				}
//			} else if (type_XLS.isSelected()) {
//				if (!Program.checkXLSExtenstion(nom)) {
//					Debug("ERROR: Not a XLS File");
//					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), nom));
//					return;
//				}
//			}
			int count = 0;
			int max_row = tv.getRowCount();
			int row = 0;
			do {
				if (tv.getValueAt(row, TableauValues.ETAT).toString().equals("true")) {
					count++;
				}
				row++;
			}
			while (row < max_row);

			if (count == 0) {
				Debug("ERROR: No place selected");
				//"Aucun rangement sélectionné!");
				//"Veuillez sélectionner les rangements à générer.");
				Erreur.showSimpleErreur(Program.getError("Error089"), Program.getError("Error090"), true);
				return;
			}
			int[] listToGen = new int[count];
			row = 0;
			int k = 0;
			do {
				if (tv.getValueAt(row, TableauValues.ETAT).toString().equals("true")) {
					listToGen[k] = row;
					k++;
				}
				row++;
			}
			while (row < max_row);

			int caisse_select = 0;
			// Export XML
			if (type_XML.isSelected()) {
				Debug("Exporting in XML in progress...");
				LinkedList<Rangement> rangements = new LinkedList<>();
				for (int j : listToGen) {
					rangements.add(Program.getCave(j));
				}
				MyXmlDom.writeRangements(nom, rangements, false);
			} else if (type_HTML.isSelected()) {
				Debug("Exporting in HTML in progress...");
				LinkedList<Rangement> rangements = new LinkedList<>();
				for (int j : listToGen) {
					rangements.add(Program.getCave(j));
				}
				MyXmlDom.writeRangements(Program.getPreviewXMLFileName(), rangements, false);

				TransformerFactory tFactory = TransformerFactory.newInstance();

				StreamSource xslDoc = new StreamSource("resources/Rangement.xsl");
				StreamSource xmlDoc = new StreamSource(Program.getPreviewXMLFileName());

				try(var htmlFile = new FileOutputStream(nom)) {
					var transformer = tFactory.newTransformer(xslDoc);
					transformer.transform(xmlDoc, new StreamResult(htmlFile));
				} catch (Exception e1) {
					Program.showException(e1);
				}
			} else if (type_XLS.isSelected()) {
				Debug("Exporting in XLS in progress...");
				LinkedList<Rangement> oList = new LinkedList<>();
				for (int j : listToGen) {
					Rangement r = Program.getCave(j);
					if (r != null) {
						oList.add(r);
						if (r.isCaisse()) {
							caisse_select++;
						}
					}
				}
				RangementUtils.write_XLSTab( nom, oList );
			}

			if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_TAB_MESS, false)) {
				if (caisse_select >= 1) {
					String erreur_txt1, erreur_txt2;
					if (caisse_select == 1){
						erreur_txt1 = Program.getError("Error091"); //"Vous avez sélectionné un rangement de type Caisse");
						erreur_txt2 = Program.getError("Error092"); //"Une liste des vins de ce rangement a été générée.");
					}else{
						erreur_txt1 = Program.getError("Error127"); //"Vous avez sélectionné des rangements de type Caisse");
						erreur_txt2 = Program.getError("Error128"); //"Une liste des vins de ces rangements a été générée.");
					}
					Erreur.showKeyErreur(erreur_txt1, erreur_txt2, MyCellarSettings.DONT_SHOW_TAB_MESS);
				}
			}
			end.setText(Program.getLabel("Infos097")); //"Fichier généré.");
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(() -> {
								end.setText("");
							});
						}
					},
					5000
			);
			preview.setEnabled(true);
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
	private void preview_actionPerformed(ActionEvent e) {
		Debug("preview_actionPerforming...");
		String path = name.getText();
		Program.open( new File(path) );
	}

	/**
	 * keylistener_actionPerformed: Foncion d'écoute des touches.
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == CREER && e.isControlDown()) {
			create_actionPerformed(null);
		}
		if (e.getKeyCode() == OUVRIR && e.isControlDown() && preview.isEnabled()) {
			preview_actionPerformed(null);
		}
	}

	/**
	 * selectall_actionPerformed: Permet de sélectionner toutes les lignes de la
	 * JTable
	 *
	 * @param e ActionEvent
	 */
	private void selectall_actionPerformed(ActionEvent e) {
		for (int i = 0; i < tv.getRowCount(); i++) {
			tv.setValueAt(selectall.isSelected(), i, 0);
		}
		table.updateUI();
	}

	/**
	 * options_actionPerformed: Appel de la fenêtre d'options.
	 *
	 * @param e ActionEvent
	 */
	private void options_actionPerformed(ActionEvent e) {

		XLSTabOptions oXLSTabOptions = new XLSTabOptions();
		oXLSTabOptions.setVisible(true);
		m_jcb_options.setSelected(false);
	}

	/**
	 * jradio_actionPerformed: Bouton radio.
	 *
	 * @param e ActionEvent
	 */
	private void jradio_actionPerformed(ActionEvent e) {
		m_jcb_options.setEnabled(type_XLS.isSelected());
	}

	/**
	 * param_actionPerformed: Appelle la fenêtre de paramètres.
	 *
	 * @param e ActionEvent
	 */
	private void param_actionPerformed(ActionEvent e) {
		Debug("param_actionPerforming...");
		String titre = Program.getLabel("Infos310");
		String message2 = Program.getLabel("Infos309");
		String titre_properties[] = new String[3];
		titre_properties[0] = Program.getLabel("Infos210");
		titre_properties[1] = Program.getLabel("Infos211");
		titre_properties[2] = Program.getLabel("Infos233");
		String default_value[] = new String[3];
		String key_properties[] = new String[3];
		key_properties[0] = MyCellarSettings.CREATE_TAB_DEFAULT;
		key_properties[1] = MyCellarSettings.CREATE_TAB_DEFAULT;
		key_properties[2] = MyCellarSettings.CREATE_TAB_DEFAULT;
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
	private static void Debug(String sText) {
		Program.Debug("Creer_Tableaux: " + sText);
	}

	@Override
	public void cut() {
		String text = name.getSelectedText();
		String fullText = name.getText();
		if(text != null) {
			name.setText(fullText.substring(0, name.getSelectionStart()) + fullText.substring(name.getSelectionEnd()));
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void copy() {
		String text = name.getSelectedText();
		if(text != null) {
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void paste() {
		String fullText = name.getText();
		name.setText(fullText.substring(0,  name.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(name.getSelectionEnd()));
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

	public void updateView() {
		SwingUtilities.invokeLater(() -> {
			tv.removeAll();
			for (Rangement r : Program.getCave()) {
				tv.addRangement(r);
			}
		});
	}
}
