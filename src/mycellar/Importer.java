package mycellar;


import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
import net.miginfocom.swing.MigLayout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 12.6
 * @since 09/01/19
 */
public class Importer extends JPanel implements ITabListener, Runnable, ICutCopyPastable {

	private static final int COUNT = 18;
	private final MyCellarButton importe = new MyCellarButton();
	private final MyCellarRadioButton type_txt = new MyCellarRadioButton();
	private final MyCellarRadioButton type_xls = new MyCellarRadioButton();
	private final MyCellarRadioButton type_xml = new MyCellarRadioButton();
	private final char IMPORT = Program.getLabel("IMPORT").charAt(0);
	private final char OUVRIR = Program.getLabel("OUVRIR").charAt(0);
	private final List<MyCellarComboBox<MyCellarFields>> comboBoxList = new ArrayList<>(COUNT);
	private final MyCellarCheckBox titre = new MyCellarCheckBox();
	private final MyCellarLabel textControl2 = new MyCellarLabel();
	private final MyCellarLabel label_progression = new MyCellarLabel();
	private final MyCellarLabel label2 = new MyCellarLabel();
	private final MyCellarComboBox<String> separateur = new MyCellarComboBox<>();
	private final JTextField file = new JTextField();
	static final long serialVersionUID = 280706;


	/**
	 * Importer: Constructeur
	 */
	public Importer() {
		Debug("Constructor");
		importe.setToolTipText(Program.getLabel("Infos011"));
		MyCellarButton openit = new MyCellarButton();
		openit.setToolTipText(Program.getLabel("Infos152"));
		MyCellarButton parcourir = new MyCellarButton();
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
		MyCellarLabel label1 = new MyCellarLabel(Program.getLabel("Infos033"));
		ButtonGroup checkboxGroup1 = new ButtonGroup();
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

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
		});

		file.addMouseListener(new PopupListener());
		JMenuItem quitter = new JMenuItem(Program.getLabel("Infos003"));
		quitter.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.CTRL_DOWN_MASK));

		setLayout(new MigLayout("","grow",""));
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
		JPanel panelFile = new JPanel();
		panelFile.setLayout(new MigLayout("","[grow][][]","[]"));
		panelFile.add(label1, "wrap");
		panelFile.add(file, "grow");
		panelFile.add(parcourir);
		panelFile.add(openit);
		add(panelFile,"grow,wrap");
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("","",""));
		panel.add(titre, "");
		add(panel, "wrap");
		JPanel panelChoix = new JPanel();
		panelChoix.setLayout(new MigLayout("","[][][][]",""));
		panelChoix.add(textControl2, "span 4,wrap");
		for (int i=0; i<COUNT; i++) {
			MyCellarComboBox<MyCellarFields> combo = new MyCellarComboBox<>();
			combo.addItem(MyCellarFields.EMPTY);
			for(MyCellarFields field : MyCellarFields.getFieldsList()) {
				combo.addItem(field);
			}
			if (i < COUNT - 1) {
				int index = i + 1;
				combo.addActionListener((e) -> updateCombo(e, index));
			}
			combo.addItem(MyCellarFields.USELESS);
			comboBoxList.add(combo);
			panelChoix.add(combo, i % 6 == 5 ? "wrap" : "");
			if (i > 0) {
				combo.setEnabled(false);
			}
		}
		add(panelChoix, "grow, wrap");
		add(label_progression, "grow, center, hidemode 3, wrap");
		add(importe, "center");

		separateur.addItem(Program.getLabel("Infos042"));
		separateur.addItem(Program.getLabel("Infos043"));
		separateur.addItem(Program.getLabel("Infos044"));
		separateur.addItem(Program.getLabel("Infos002"));

		Debug("Constructor OK");
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
		boolean typeXml = type_xml.isSelected();
		for (var combo: comboBoxList) {
			combo.setVisible(!typeXml);
		}
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
	 * updateCombo: Choix
	 *
	 * @param e ActionEvent
	 * @param  index int
	 */
	private void updateCombo(ActionEvent e, int index) {
		if (((MyCellarComboBox)e.getSource()).getSelectedIndex() == 0) {
			for (int i=index; i<COUNT; i++) {
				final var comboBox = comboBoxList.get(i);
				comboBox.setEnabled(false);
				comboBox.setSelectedIndex(0);
			}
		} else {
			comboBoxList.get(index).setEnabled(true);
		}
	}

	/**
	 * Réalise la lecture d'une ligne d'un fichier XLS
	 *
	 * @param row Row: Ligne d'une feuille Excel
	 * @return LinkedList<String>
	 */
	private LinkedList<String> readRow(Row row) {
		final Iterator<Cell> cellIterator = row.cellIterator();
		LinkedList<String> bottle = new LinkedList<>();
		while (cellIterator.hasNext()) {
			final Cell cell = cellIterator.next();
			if (cell.getCellType() == CellType.NUMERIC) {
				bottle.add(Double.toString(cell.getNumericCellValue()));
			} else if (cell.getCellType() == CellType.STRING) {
				bottle.add(cell.getStringCellValue());
			} else {
				throw new UnsupportedOperationException(Program.getError(MessageFormat.format(Program.getError("Importer.unknownCellType"), cell.getCellType())));
			}
		}
		return bottle;
	}

	/**
	 * parcourir_actionPerformed: Permet de parcourir les répertoires pour trouver
	 * le fichier à importer
	 *
	 * @param e ActionEvent
	 */
	private void parcourir_actionPerformed(ActionEvent e) {

		Debug("parcourir_actionPerforming...");
		JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR,""));
		boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
		if (type_txt.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_TXT);
		}	else if (type_xls.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
		}	else if (type_xml.isSelected()) {
			boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
		}
		
		if (boiteFichier.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File nomFichier = boiteFichier.getSelectedFile();
			if (nomFichier == null) {
				setCursor(Cursor.getDefaultCursor());
				Erreur.showSimpleErreur(Program.getError("FileNotFound"));
				Debug("ERROR: parcourir: File not found during Opening!");
				return;
			}
			Program.putCaveConfigString(MyCellarSettings.DIR, boiteFichier.getCurrentDirectory().toString());
			String fic = nomFichier.getAbsolutePath();
			Filtre filtre = (Filtre) boiteFichier.getFileFilter();
			fic = MyCellarControl.controlAndUpdateExtension(fic, filtre);
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
			for (var combo : comboBoxList) {
				if (combo.getSelectedIndex() != 0) {
					nb_choix++;
				}
			}

			HashMap<MyCellarFields, Integer> mapFieldCount = new HashMap<>();
			for (int i = 0; i < nb_choix; i++) {
				final var comboBox = comboBoxList.get(i);
				final MyCellarFields selectedField = (MyCellarFields) comboBox.getSelectedItem();
				if (selectedField != null && MyCellarFields.isRealField(selectedField)) {
					mapFieldCount.put(selectedField, mapFieldCount.getOrDefault(selectedField, 0) + 1);
				}
			}
 
			//Ouverture du fichier à importer
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

			if (type_xls.isSelected()) {
				if (!MyCellarControl.controlExtension(nom, Arrays.asList(Filtre.FILTRE_XLS.toString(), Filtre.FILTRE_ODS.toString()))) {
					label_progression.setText("");
					Debug("ERROR: Not a XLS File");
					//"Le fichier saisie ne possède pas une extension Excel: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), nom), Program.getError("Error035"));
					importe.setEnabled(true);
					return;
				}
			} else if (type_txt.isSelected()) {
				if (!MyCellarControl.controlExtension(nom, Arrays.asList(Filtre.FILTRE_TXT.toString(), Filtre.FILTRE_CSV.toString()))) {
					label_progression.setText("");
					Debug("ERROR: Not a Text File");
					//"Le fichier saisie ne possède pas une extension Excel: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error023"), nom), Program.getError("Error024"));
					importe.setEnabled(true);
					return;
				}
			} else {
				if (!MyCellarControl.controlExtension(nom, Arrays.asList(Filtre.FILTRE_XML.toString()))) {
					label_progression.setText("");
					Debug("ERROR: Not a XML File");
					//"Le fichier saisie ne possède pas une extension Excel: " + str_tmp3);
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error204"), nom), Program.getError("Error205"));
					importe.setEnabled(true);
					return;
				}
			}

			if(type_xml.isSelected()) {
				importFromXML(f);
				return;
			}

			boolean isMoreThanOne = false;
			for (var key: mapFieldCount.keySet()) {
				if (mapFieldCount.get(key) > 1) {
					isMoreThanOne = true;
					break;
				}
			}
			Rangement new_rangement = null;
			if (isMoreThanOne) {
				label_progression.setText("");
				Debug("ERROR: fields cannot be selected more than one time");
				//"Un champ ne doit pas être sélectionné 2 fois.");
				//"Veuillez choisir un champ différent pour chaque colonne.");
				Erreur.showSimpleErreur(Program.getError("Error017"), Program.getError("Error018"));
				importe.setEnabled(true);
				return;
			}	else if(mapFieldCount.get(MyCellarFields.NAME) == null) {
				label_progression.setText("");
				Debug("ERROR: No column for wine name");
				//"Aucune colonne n'indique le nom du vin.
				//"Veuillez sélectionner une colonne avec le nom du vin
				Erreur.showSimpleErreur(Program.getError("Error142"), Program.getError("Error143"));
				importe.setEnabled(true);
				return;
			}	else if(mapFieldCount.get(MyCellarFields.PLACE) == null) {
				label_progression.setText("");
				Debug("ERROR: No place defined, a place will be create");
				//Il n'y a pas de rangements définis dans le fichier.
				//Un rangement par défaut va être créé.
				Erreur.showSimpleErreur(Program.getError("Error140"), Program.getError("Error141"), true);

				int nb_caisse = (int)Program.getCave().stream().filter(Rangement::isCaisse).count();

				String title = Program.getLabel("Infos010");
				String message2 = Program.getLabel("Infos308");
				String []titre_properties = new String[nb_caisse + 2];
				String []default_value = new String[nb_caisse + 2];
				String []key_properties = new String[nb_caisse + 2];
				String []type_objet = new String[nb_caisse + 2];
				int j = 0;
				for (Rangement cave : Program.getCave()) {
					if (cave.isCaisse()) {
						titre_properties[j] = cave.getNom();
						key_properties[j] = MyCellarSettings.RANGEMENT_DEFAULT;
						default_value[j] = "false";
						type_objet[j] = "MyCellarRadioButton";
						j++;
					}
				}
				titre_properties[nb_caisse] = Program.getLabel("Infos289");
				key_properties[nb_caisse] = MyCellarSettings.RANGEMENT_DEFAULT;
				default_value[nb_caisse] = "true";
				type_objet[nb_caisse] = "MyCellarRadioButton";
				titre_properties[nb_caisse + 1] = Program.getLabel("Infos307");
				key_properties[nb_caisse + 1] = MyCellarSettings.RANGEMENT_NAME;
				default_value[nb_caisse + 1] = "";
				type_objet[nb_caisse + 1] = "JTextField";
				MyOptions myoptions = new MyOptions(title, "", message2, titre_properties, default_value, key_properties, type_objet, Program.getCaveConfig(), false);
				myoptions.setVisible(true);
				int num_r = Program.getCaveConfigInt(MyCellarSettings.RANGEMENT_DEFAULT, -1);
				if (num_r == Program.GetCaveLength()) {
					String nom1 = Program.getCaveConfigString(MyCellarSettings.RANGEMENT_NAME,""); //Program.options.getValue();
					boolean resul;
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
									if (rangement != null) {
										Program.options = new Options(Program.getLabel("Infos020"), Program.getLabel("Infos230"), Program.getLabel("Infos020"), "", nom1,
												Program.getError("Error037"), false);
										Program.options.setVisible(true);
										nom1 = Program.options.getValue();
										Program.options = null;
										resul = false;
									}
								}
							}
							while (rangement != null);
						}
					}
					while (!resul);
					Debug("Creating new place with name: "+nom1);
					new_rangement = new Rangement(nom1, 1, 0, false, -1);
					Program.addCave(new_rangement);
				}	else {
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

				try(var reader = new BufferedReader(new FileReader(f))) {
					String line = reader.readLine();
					if (line != null) {
						if (line.split(separe).length <= 1) {
							label_progression.setText("");
							Debug("ERROR: No separator found");
							//"Le séparateur sélectionné n'a pas été trouvé.");
							//"Veuillez sélectionner le separateur utilise dans votre fichier.");
							Erreur.showSimpleErreur(Program.getError("Error042"), Program.getError("Error043"));
							importe.setEnabled(true);
							reader.close();
							return;
						}
					}
					if (titre.isSelected()) {
						line = reader.readLine();
					}
					label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
					int maxNumPlace = 0;
					while (line != null) {
						String []lu = line.split(separe);
						Bouteille bottle = new Bouteille();
						bottle.updateID();
						for (int i = 0; i < lu.length; i++) {
							String value = lu[i];
							if(value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length()-1) == '"') {
								value = value.substring(1, value.length() - 1);
							}
							value = Program.convertToHTMLString(value);
							MyCellarFields selectedField = getSelectedField(i);
							bottle.setValue(selectedField, value);
							if (selectedField.equals(MyCellarFields.NUM_PLACE) && maxNumPlace < bottle.getNumLieu()) {
								maxNumPlace = bottle.getNumLieu();
							}
						}
						if((bottle.getEmplacement() == null || bottle.getEmplacement().isEmpty()) && new_rangement != null) {
							bottle.setEmplacement(new_rangement.getNom());
							new_rangement.setNbEmplacements(maxNumPlace+1);
						}
						Program.getStorage().addWine(bottle);
						line = reader.readLine();
					}
				}
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
			else { //Excel File
				if (!importExcelFile(nom, new_rangement)) {
					return;
				}
			}
			importe.setEnabled(true);
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
		if(!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
	}

	private boolean importExcelFile(final String nom, final Rangement new_rangement) {
		Debug("Importing XLS file...");

		label_progression.setText(Program.getLabel("Infos089")); //"Import en cours...");
		//Ouverture du fichier Excel
		try (var workbook = new XSSFWorkbook(new FileInputStream(nom))) {

			//Sélection de la feuille
			var sheet = workbook.getSheetAt(0);
			//Lecture de cellules
			Iterator<Row> iterator = sheet.iterator();
			//Ecriture du vin pour chaque ligne
			boolean skipLine = titre.isSelected();
			int maxNumPlace = 0;
			while (iterator.hasNext()) {
				LinkedList<String> bottleValues = readRow(iterator.next());
				final long count = bottleValues.stream().filter(s -> !s.isEmpty()).count();
				if (skipLine && count > 0) {
					Debug("Skipping title line");
					skipLine = false;
					continue;
				}
				if (count > 0) {
					Bouteille bottle = new Bouteille();
					bottle.updateID();

					int i = 0;
					for (String value : bottleValues) {
						//Récupération des champs sélectionnés
						MyCellarFields selectedField = getSelectedField(i);
						//Alimentation de la HashMap
						Debug("Write " + selectedField + "->" + value);
						bottle.setValue(selectedField, value);
						if (selectedField.equals(MyCellarFields.NUM_PLACE) && maxNumPlace < bottle.getNumLieu()) {
							maxNumPlace = bottle.getNumLieu();
						}

						if((bottle.getEmplacement() == null || bottle.getEmplacement().isEmpty()) && new_rangement != null) {
							bottle.setEmplacement(new_rangement.getNom());
							new_rangement.setNbEmplacements(maxNumPlace+1);
						}
						i++;
					}
					Program.getStorage().addWine(bottle);
				}
			}
		}	catch (IOException e) {
			label_progression.setText("");
			Debug("ERROR: File not found (IO): "+nom);
			//Fichier non trouvé
			//"Vérifier le chemin");
			Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error020"), nom), Program.getError("Error022"));
			importe.setEnabled(true);
			return false;
		}
		catch (Exception e) {
			Program.showException(e, false);
			label_progression.setText("");
			Debug("ERROR: "+e.toString());
			Erreur.showSimpleErreur(Program.getError("Error082"));
			importe.setEnabled(true);
			return false;
		}

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
		importe.setEnabled(true);
		return true;
	}

	private void importFromXML(File f) {
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
		if (!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
	}

	private MyCellarFields getSelectedField(int i) {
		if (i < comboBoxList.size()) {
			return (MyCellarFields) comboBoxList.get(i).getSelectedItem();
		}
		return MyCellarFields.USELESS;
	}

	/**
	 * keylistener_actionPerformed: Ecouteur de touche
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
		if (e.getKeyCode() == IMPORT && e.isControlDown()) {
			importe_actionPerformed(null);
		}
		if (e.getKeyCode() == OUVRIR && e.isControlDown()) {
			openit_actionPerformed(null);
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Importer: " + sText);
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
	public void cut() {
		String text = file.getSelectedText();
		String fullText = file.getText();
		if(text != null) {
			file.setText(fullText.substring(0, file.getSelectionStart()) + fullText.substring(file.getSelectionEnd()));
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void copy() {
		String text = file.getSelectedText();
		if(text != null) {
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void paste() {
		String fullText = file.getText();
		file.setText(fullText.substring(0, file.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(file.getSelectionEnd()));
	}

}
