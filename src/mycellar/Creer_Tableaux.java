package mycellar;

import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
import mycellar.xls.XLSTabOptions;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 7.5
 * @since 24/12/20
 */
public final class Creer_Tableaux extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {
	private final JTextField name = new JTextField();
	private final MyCellarRadioButton type_XML = new MyCellarRadioButton(LabelType.INFO, "210", false);
	private final MyCellarRadioButton type_HTML = new MyCellarRadioButton(LabelType.INFO, "211", true);
	private final MyCellarRadioButton type_XLS = new MyCellarRadioButton(LabelType.INFO, "233", false);
	private JTable table;
	private final TableauValues tv = new TableauValues();
	@SuppressWarnings("deprecation")
	private final MyCellarLabel end = new MyCellarLabel();
	private final MyCellarButton preview = new MyCellarButton(LabelType.INFO, "152");
	private final char creerChar = Program.getLabel("CREER").charAt(0);
	private final char ouvrirChar = Program.getLabel("OUVRIR").charAt(0);
	private final MyCellarCheckBox selectall = new MyCellarCheckBox(LabelType.INFO, "126");
	private final MyCellarButton m_jcb_options = new MyCellarButton(LabelType.INFO, "156", LabelProperty.SINGLE.withThreeDashes());
	static final long serialVersionUID = 260706;

	/**
	 * Creer_Tableaux: Constructeur pour la creation des tableaux.
	 *
	 */
	public Creer_Tableaux() {
		Debug("Constructor");
		try {
			final MyCellarLabel fileLabel = new MyCellarLabel(Program.getLabel("Infos095")); //"Nom du fichier genere:");
			m_jcb_options.addActionListener(this::options_actionPerformed);
			final MyCellarButton browse = new MyCellarButton("...");
			browse.addActionListener(this::browse_actionPerformed);
			final MyCellarButton parameter = new MyCellarButton(Program.getLabel("Main.Parameters"));
			parameter.addActionListener(this::param_actionPerformed);
			final MyCellarLabel chooseLabel = new MyCellarLabel(Program.getLabel("Infos096")); //"Selectionner les rangements a generer:");
			final MyCellarButton create = new MyCellarButton(Program.getLabel("Infos018")); //"Creer");
			create.setMnemonic(creerChar);

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
			preview.setMnemonic(ouvrirChar);
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
		} catch (RuntimeException e) {
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
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
		} else if (type_HTML.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		} else if (type_XLS.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
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
	 * create_actionPerformed: Fonction de creation des tableaux.
	 *
	 * @param e ActionEvent
	 */
	private void create_actionPerformed(ActionEvent e) {
		try {
			Debug("create_actionPerforming...");
			String nom = name.getText().strip();

			if (!MyCellarControl.controlPath(nom)) {
				return;
			}

			File path = new File(nom);
			name.setText(path.getAbsolutePath());

			//Verify file type. Is it XML File?
			if (type_XML.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Collections.singletonList(Filtre.FILTRE_XML.toString()))) {
					Debug("ERROR: Not a XML File");
					//"Le fichier saisie ne possede pas une extension XML: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error087"), nom));
					return;
				}
			} else if (type_HTML.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Collections.singletonList(Filtre.FILTRE_HTML.toString()))) {
					Debug("ERROR: Not a HTML File");
					//"Le fichier saisie ne possede pas une extension HTML: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error107"), nom));
					return;
				}
			} else if (type_XLS.isSelected()) {
				if (MyCellarControl.hasInvalidExtension(nom, Arrays.asList(Filtre.FILTRE_XLSX.toString(), Filtre.FILTRE_XLS.toString(), Filtre.FILTRE_ODS.toString()))) {
					Debug("ERROR: Not a XLS File");
					//"Le fichier saisie ne possede pas une extension Excel: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), nom));
					return;
				}
			}
			int count = 0;
			int max_row = tv.getRowCount();
			int row = 0;
			do {
				if (tv.getValueAt(row, TableauValues.ETAT).toString().equals("true")) {
					count++;
				}
				row++;
			}	while (row < max_row);

			if (count == 0) {
				Debug("ERROR: No place selected");
				//"Aucun rangement selectionne!");
				//"Veuillez selectionner les rangements a generer.");
				Erreur.showSimpleErreur(Program.getError("Error089"), Program.getError("Error090"), true);
				return;
			}
			row = 0;
			LinkedList<Rangement> rangements = new LinkedList<>();
			do {
				if (tv.getValueAt(row, TableauValues.ETAT).toString().equals("true")) {
					rangements.add(tv.getRangementAt(row));
				}
				row++;
			}	while (row < max_row);

			long caisseCount = 0;
			// Export XML
			if (type_XML.isSelected()) {
				Debug("Exporting in XML in progress...");
				MyXmlDom.writeRangements(nom, rangements, false);
			} else if (type_HTML.isSelected()) {
				Debug("Exporting in HTML in progress...");
				MyXmlDom.writeRangements(Program.getPreviewXMLFileName(), rangements, false);

				TransformerFactory tFactory = TransformerFactory.newInstance();

				StreamSource xslDoc = new StreamSource("resources/Rangement.xsl");
				StreamSource xmlDoc = new StreamSource(Program.getPreviewXMLFileName());

				try (var htmlFile = new FileOutputStream(nom)) {
					var transformer = tFactory.newTransformer(xslDoc);
					transformer.transform(xmlDoc, new StreamResult(htmlFile));
				} catch (Exception e1) {
					Program.showException(e1);
				}
			} else if (type_XLS.isSelected()) {
				Debug("Exporting in XLS in progress...");
				caisseCount = rangements.stream().filter(Rangement::isCaisse).count();
				RangementUtils.write_XLSTab( nom, rangements);
			}

			if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_TAB_MESS, false)) {
				if (caisseCount > 0) {
					String erreur_txt1, erreur_txt2;
					if (caisseCount == 1) {
						erreur_txt1 = Program.getError("Error091"); //"Vous avez selectionne un rangement de type Caisse");
						erreur_txt2 = Program.getError("Error092", LabelProperty.PLURAL); //"Une liste des vins de ce rangement a ete generee.");
					} else {
						erreur_txt1 = Program.getError("Error127"); //"Vous avez selectionne des rangements de type Caisse");
						erreur_txt2 = Program.getError("Error128", LabelProperty.PLURAL); //"Une liste des vins de ces rangements a ete generee.");
					}
					Erreur.showKeyErreur(erreur_txt1, erreur_txt2, MyCellarSettings.DONT_SHOW_TAB_MESS);
				}
			}
			end.setText(Program.getLabel("Infos097")); //"Fichier genere.");
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(() -> end.setText(""));
						}
					},
					5000
			);
			preview.setEnabled(true);
		}	catch (Exception exc) {
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
		Program.open(new File(path));
	}

	/**
	 * keylistener_actionPerformed: Foncion d'ecoute des touches.
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == creerChar && e.isControlDown()) {
			create_actionPerformed(null);
		}
		if (e.getKeyCode() == ouvrirChar && e.isControlDown() && preview.isEnabled()) {
			preview_actionPerformed(null);
		}
	}

	/**
	 * selectall_actionPerformed: Permet de selectionner toutes les lignes de la
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
	 * options_actionPerformed: Appel de la fenetre d'options.
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
	 * param_actionPerformed: Appelle la fenetre de parametres.
	 *
	 * @param e ActionEvent
	 */
	private void param_actionPerformed(ActionEvent e) {
		Debug("param_actionPerforming...");
		String titre = Program.getLabel("Infos310");
		String message2 = Program.getLabel("Infos309");
		String[] titre_properties = {
				Program.getLabel("Infos210"),
				Program.getLabel("Infos211"),
				Program.getLabel("Infos233") };
		String[] key_properties = {
				MyCellarSettings.CREATE_TAB_DEFAULT,
				MyCellarSettings.CREATE_TAB_DEFAULT,
				MyCellarSettings.CREATE_TAB_DEFAULT	};
		String val = Program.getCaveConfigString(key_properties[0], "1");
		String[] default_value = { "false", "false", "false" };
		if ("0".equals(val)) {
			default_value[0] = "true";
		}
		if ("1".equals(val)) {
			default_value[1] = "true";
		}
		if ("2".equals(val)) {
			default_value[2] = "true";
		}
		String[] type_objet = {"MyCellarRadioButton", "MyCellarRadioButton", "MyCellarRadioButton"};
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
		if (text != null) {
			name.setText(fullText.substring(0, name.getSelectionStart()) + fullText.substring(name.getSelectionEnd()));
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void copy() {
		String text = name.getSelectedText();
		if (text != null) {
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void paste() {
		String fullText = name.getText();
		name.setText(fullText.substring(0, name.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(name.getSelectionEnd()));
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

	@Override
	public void setUpdateView() {
	}

	@Override
	public void updateView() {
		SwingUtilities.invokeLater(() -> {
			tv.removeAll();
			for (Rangement r : Program.getCave()) {
				tv.addRangement(r);
			}
		});
	}
}
