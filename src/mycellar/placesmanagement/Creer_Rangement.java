package mycellar.placesmanagement;

import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarControl;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.TabEvent;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSpinner;
import mycellar.core.PopupListener;
import mycellar.general.ProgramPanels;
import mycellar.general.XmlUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static mycellar.Program.toCleanString;
import static mycellar.core.LabelType.INFO;
import static mycellar.core.LabelType.INFO_OTHER;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 15.1
 * @since 16/03/21
 */
public final class Creer_Rangement extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

	private final MyCellarComboBox<Rangement> comboPlace = new MyCellarComboBox<>();
	private final JTextField nom_obj = new JTextField();
	private final MyCellarRadioButton m_jrb_same_column_number = new MyCellarRadioButton(INFO, "012", true); //"Toutes les lignes ont le meme nombre de colonnes"
	private final MyCellarRadioButton m_jrb_dif_column_number = new MyCellarRadioButton(INFO, "013", false); //"Toutes les lignes n'ont pas le meme nombre de colonnes"
	private final MyCellarCheckBox checkLimite = new MyCellarCheckBox(INFO, "238"); //limite
	private final MyCellarLabel label_limite = new MyCellarLabel(INFO_OTHER, "Main.Item", LabelProperty.SINGLE);
	private final MyCellarSpinner nb_limite = new MyCellarSpinner(1, 999);
	private boolean islimited = false;
	private int limite = 0;
	private final MyCellarSpinner nb_parties = new MyCellarSpinner(1, 99);
	private LinkedList<Part> listPart = new LinkedList<>();
	private static final char CREER = Program.getLabel("CREER").charAt(0);
	private static final char PREVIEW = Program.getLabel("PREVIEW").charAt(0);
	private final MyCellarSpinner nb_start_caisse = new MyCellarSpinner(0, 99);
	private final MyCellarCheckBox m_caisse_chk = new MyCellarCheckBox(INFO, "024"); //Caisse
	private final MyCellarLabel label_cree = new MyCellarLabel();
	private final MyCellarButton preview = new MyCellarButton(INFO, "155");
	private int start_caisse = 0;
	private final JPanel panelType;
	private final JPanel panelStartCaisse;
	private final JPanel panelLimite;
	private final JPanel panelTable;
	private final CreerRangementTableModel model;
	private final boolean modify;
	static final long serialVersionUID = 280706;

	/**
	 * Creer_Rangement: Creation d'un rangement
	 *
	 * @param modify boolean: Indique si l'appel est pour modifier
	 */
	public Creer_Rangement(final boolean modify) {
		Debug("Constructor for Modification ? " + modify);
		this.modify = modify;
		model = new CreerRangementTableModel();

		MyCellarButton createButton;
		if (modify) {
			createButton = new MyCellarButton(INFO, "079", new ModifyAction());
		} else {
			createButton = new MyCellarButton(INFO, "018", new CreateAction());
		}

		createButton.setMnemonic(CREER);
		preview.setMnemonic(PREVIEW);
		comboPlace.addItem(Program.EMPTY_PLACE);
		Program.getCave().forEach(comboPlace::addItem);
		comboPlace.addItemListener(this::comboPlace_itemStateChanged);
		ButtonGroup cbg = new ButtonGroup();
		cbg.add(m_jrb_same_column_number);
		cbg.add(m_jrb_dif_column_number);
		m_jrb_same_column_number.addItemListener((e) -> model.setSameColumnNumber(m_jrb_same_column_number.isSelected()));
		label_cree.setForeground(Color.red);
		label_cree.setFont(Program.FONT_DIALOG_SMALL);
		label_cree.setText("");
		label_cree.setHorizontalAlignment(SwingConstants.CENTER);

		preview.addActionListener(this::preview_actionPerformed);
		m_caisse_chk.addItemListener(this::simplePlace_itemStateChanged);
		checkLimite.addItemListener(this::checkbox2_itemStateChanged);

		nom_obj.addActionListener((e) -> label_cree.setText(""));
		nom_obj.addMouseListener(new PopupListener());

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
		});

		nb_parties.addChangeListener((e) -> updatePartList());

		nb_limite.addChangeListener((e) -> {
			final int count = Integer.parseInt(nb_limite.getValue().toString());
			label_limite.setText(Program.getLabel("Main.Item", new LabelProperty(count > 1)));
		});

		// Alimentation de la liste deroulante du nombre de parties
		nb_parties.setValue(1);
		//Alimentation du Spinner start_caisse
		nb_start_caisse.setValue(0);
		nb_start_caisse.setVisible(false);
		//Alimentation du Spinner limite_caisse
		nb_limite.setValue(1);

		setLayout(new MigLayout("", "[grow][grow]", "[][]"));

		if (modify) {
			MyCellarLabel labelModify = new MyCellarLabel(INFO, "226"); //"Selectionner le rangement a modifier:"
			JPanel panelModify = new JPanel();
			panelModify.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),"",0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
			panelModify.setLayout(new MigLayout("", "[]", "[]"));
			panelModify.add(labelModify, "split 2");
			panelModify.add(comboPlace);
			add(panelModify, "span 2, wrap");
		}
		MyCellarLabel labelName = new MyCellarLabel(INFO, "020"); //"Nom du rangement:");
		add(labelName, "span 2, split 3");
		add(nom_obj, "growx");
		add(m_caisse_chk, "wrap");

		panelType = new JPanel();
		panelType.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos021"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelType.setLayout(new GridLayout(0,2));
		panelType.add(m_jrb_same_column_number);
		panelType.add(m_jrb_dif_column_number);

		panelStartCaisse = new JPanel();
		panelStartCaisse.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos272"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelStartCaisse.setLayout(new MigLayout("", "[]", "[]"));
		panelStartCaisse.add(nb_start_caisse, "wmin 50");

		panelLimite = new JPanel();
		panelLimite.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos274"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelLimite.setLayout(new MigLayout("", "[][]", "[]"));
		panelLimite.add(checkLimite, "gapright 10");
		panelLimite.add(nb_limite, "split 2, wmin 50, hidemode 3");
		panelLimite.add(label_limite, "hidemode 3");

		JTable tableParties = new JTable(model);
		model.setValues(listPart);

		panelTable = new JPanel();
		panelTable.setLayout(new MigLayout("", "[grow]", "[grow]"));
		panelTable.add(new JScrollPane(tableParties), "grow");

		JPanel panelPartie = new JPanel();
		panelPartie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos023"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelPartie.setLayout(new MigLayout("", "[]", "[]"));
		panelPartie.add(nb_parties, "wmin 50");

		JPanel panelPartiesConfig = new JPanel();
		panelPartiesConfig.setLayout(new MigLayout("", "[][]", "[grow]"));
		panelPartiesConfig.add(panelType, "growy, hidemode 3, gapright 30");
		panelPartiesConfig.add(panelPartie, "growx, wmin 150, gapright 30");
		panelPartiesConfig.add(panelStartCaisse, "growx, wmin 250, hidemode 3, gapright 30");
		panelPartiesConfig.add(panelLimite, "growx, wmin 250, hidemode 3, gapright 30");

		add(panelPartiesConfig, "span 2, wrap, hidemode 3");
		add(panelTable, "span 2, grow, wrap, hidemode 3");
		add(label_cree, "center, span 2, wrap");
		add(createButton, "span 2, split 2, center");
		add(preview);

		m_jrb_dif_column_number.addItemListener((e) -> model.setSameColumnNumber(!m_jrb_dif_column_number.isSelected()));
		model.setSameColumnNumber(true);

		if (modify) {
			enableAll(false);
		}

		m_caisse_chk.setSelected(true);
		setVisible(true);
	}

	private void updatePartList() {
		if (m_caisse_chk.isSelected()) {
			return;
		}
		int newValue = Integer.parseInt(nb_parties.getValue().toString());
		if (newValue > listPart.size()) {
			while (listPart.size() < newValue) {
				Part part = new Part(listPart.size() + 1);
				listPart.add(part);
				if (m_jrb_dif_column_number.isSelected()) {
					part.setRows(1);
				}
			}
		} else {
			while (listPart.size() > newValue) {
				listPart.removeLast();
			}
		}
		model.setValues(listPart);
	}

	private void comboPlace_itemStateChanged(ItemEvent e) {
		if (!modify) {
			return;
		}
		final Rangement rangement = (Rangement) e.getItem();
		if (Program.EMPTY_PLACE.equals(rangement)) {
			nom_obj.setText("");
			label_cree.setText("");
			model.setValues(new LinkedList<>());
			enableAll(false);
			return;
		}

		enableAll(true);
		label_cree.setText("");
		nom_obj.setText(rangement.getNom());
		m_caisse_chk.setSelected(rangement.isCaisse());
		m_caisse_chk.setEnabled(false);
		if (rangement.isCaisse()) {
			checkLimite.setSelected(rangement.isLimited());
			if (rangement.isLimited()) {
				nb_limite.setValue(rangement.getNbColonnesStock());
			}
			nb_parties.setValue(rangement.getNbEmplacements());
			nb_start_caisse.setValue(rangement.getStartCaisse());
		} else {
			m_jrb_same_column_number.setSelected(rangement.isSameColumnNumber());
			m_jrb_dif_column_number.setSelected(!rangement.isSameColumnNumber());
			listPart = rangement.getPlace();
			model.setValues(listPart);
			nb_parties.setValue(rangement.getNbEmplacements());
		}
	}

	private void enableAll(boolean enable) {
		nom_obj.setEnabled(enable);
		m_caisse_chk.setEnabled(enable);
		nb_limite.setEnabled(enable);
		checkLimite.setEnabled(enable);
		nb_parties.setEnabled(enable);
		nb_start_caisse.setEnabled(enable);
	}

	private void modifyPlace() {
		try {
			Debug("modifyPlace...");

			final Rangement rangement = (Rangement) comboPlace.getSelectedItem();
			if (comboPlace.getSelectedIndex() == 0 || rangement == null) {
				Debug("ERROR: Please select a place");
				Erreur.showSimpleErreur(Program.getError("Error093")); //"Veuillez selectionner un rangement")
				return;
			}

			final String nom = toCleanString(nom_obj.getText());
			// Controle sur le nom
			if (!MyCellarControl.ctrlName(nom)) {
				return;
			}

			Debug("Advanced modifying...");
			if (m_caisse_chk.isSelected()) {
				Debug("Modifying Simple place...");
				//Modification d'un rangement de type "Caisse"
				start_caisse = nb_start_caisse.getIntValue();
				islimited = checkLimite.isSelected();
				limite = nb_limite.getIntValue();
				int nbPart = nb_parties.getIntValue();

				if (rangement.getNbEmplacements() > nbPart) {
					final Map<Integer, Integer> numberOfBottlesPerPlace = rangement.getNumberOfBottlesPerPlace();

					// Controle que les emplacements supprimes sont vides
					for (int i = nbPart; i < rangement.getNbEmplacements(); i++) {
						if (numberOfBottlesPerPlace.get(i) > 0) {
							Debug("ERROR: Unable to delete simple place part with bottles!");
							Erreur.showSimpleErreur(MessageFormat.format(Program.getError("CreerRangement.CantDeletePartCaisse"), (i + rangement.getStartCaisse())));
							return;
						}
					}
				}

				int nb_bottle = rangement.getNbCaseUseAll();
				if (nb_bottle > 0) {
					String name = rangement.getNom();
					if (!name.equals(nom)) {
						String erreur_txt1, erreur_txt2;
						if (nb_bottle == 1) {
							Debug("MESSAGE: 1 bottle in this place, modify?");
							erreur_txt1 = Program.getError("Error136", LabelProperty.SINGLE); //"1 bouteille est presente dans ce rangement.");
							erreur_txt2 = Program.getError("Error137", LabelProperty.SINGLE); //"Voulez vous changer l'emplacement de cette bouteille?");
						} else {
							Debug("MESSAGE: " + nb_bottle + " bottles in this place, Modify?");
							erreur_txt1 = MessageFormat.format(Program.getError("Error094", LabelProperty.PLURAL), nb_bottle); //bouteilles sont presentes dans ce rangement.");
							erreur_txt2 = Program.getError("Error095", LabelProperty.PLURAL); //"Voulez vous changer l'emplacement de ces bouteilles?");
						}
						if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
							//Modify Name of place
							Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setEmplacement(nom));
						}
					} else if (rangement.getStartCaisse() != start_caisse) {
						// Le numero de la premiere partie a change, renumeroter
						String erreur_txt1 = MessageFormat.format(Program.getError("CreerRangement.UpdatedBottlePart"), start_caisse, rangement.getStartCaisse());
						String erreur_txt2 = Program.getError("CreerRangement.AskUpdateBottlePart", LabelProperty.PLURAL);

						if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
							//Modify start part number
							final int difference = start_caisse - rangement.getStartCaisse();
							Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setNumLieu(b.getNumLieu() + difference));
						}
					}
				}
				nom_obj.setText("");
				updatePlace(nom, nbPart, rangement);
				updateView();
				ProgramPanels.updateAllPanels();
				Debug("Modifications completed");
				label_cree.setText(Program.getError("Error123")); //"Rangement modifie.");
			}	else {
				// Rangement complexe
				Debug("Modifying complex place...");
				int nbBottles = rangement.getNbCaseUseAll();
				for (Part p : listPart) {
					if (p.getRows().isEmpty()) {
						Debug("ERROR: Wrong number of lines on part: " + p.getNum());
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum())); //"Erreur nombre de lignes incorrect sur la partie
						return;
					}
					for (Row r : p.getRows()) {
						if (r.getCol() == 0) {
							Debug("ERROR: Wrong number of columns on part:  " + p.getNum());
							Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error004"), p.getNum()));//"Erreur nombre de colonnes incorrect sur la partie
							return;
						}
					}
				}

				if (nbBottles == 0) {
					rangement.setNom(nom);
					rangement.updatePlace(listPart);
					putTabStock();
					nom_obj.setText("");
					updateView();
					ProgramPanels.updateAllPanels();
					label_cree.setText(Program.getError("Error123"));
				} else {
					if (rangement.getNbEmplacements() > listPart.size()) {
						int nb = 0;
						for (int i = listPart.size(); i < rangement.getNbEmplacements(); i++) {
							nb += rangement.getNbCaseUse(i);
						}
						if (nb > 0) {
							Debug("ERROR: Unable to reduce the number of place");
							Erreur.showSimpleErreur(Program.getError("Error201"));
							return;
						}
					}
					boolean bResul = true;
					for (int i = 0; i < listPart.size(); i++) {
						if (!bResul) {
							Debug("ERROR: bResul false, skipping part");
							continue;
						}
						Part part = listPart.get(i);
						int nbRow = -1;
						if (i < rangement.getNbEmplacements()) {
							nbRow = rangement.getNbLignes(i);
						}
						int newNbRow = part.getRowSize();
						if (nbRow > newNbRow) {
							int nb = 0;
							for (int j = newNbRow; j < nbRow; j++) {
								nb += rangement.getNbCaseUseLigne(i, j);
							}
							if (nb > 0) {
								bResul = false;
								Debug("ERROR: Unable to reduce the number of row");
								Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error202"), Integer.toString(i + 1)));
								// Impossible de reduire le nombre de ligne de la partie, bouteilles presentes
							}
						}
						if (bResul) {
							for (int j = 0; j < part.getRowSize(); j++) {
								if (!bResul) {
									Debug("ERROR: bResul false, skipping row");
									break;
								}
								int nbCol = -1;
								if (i < rangement.getNbEmplacements() && j < rangement.getNbLignes(i)) {
									nbCol = rangement.getNbColonnes(i, j);
								}
								int newNbCol = part.getRow(j).getCol();
								if (nbCol > newNbCol) {
									for (int k = newNbCol; k < nbCol; k++) {
										if (!bResul) {
											Debug("ERROR: bResul false, skipping column");
											break;
										}
										if (rangement.getBouteille(i, j, k).isPresent()) {
											bResul = false;
											Debug("ERROR: Unable to reduce the size of the number of column");
											Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error203"), Integer.toString(j + 1), Integer.toString(i + 1)));
											// Impossible de reduire le nombre de colonne de la ligne de la partie, bouteilles presentes
										}
									}
								}
							}
						}
					}

					if (bResul) {
						Debug("Updating complex place: " + rangement.getNom());
						String name = rangement.getNom();
						if (!name.equalsIgnoreCase(nom)) {
							String erreur_txt1 = Program.getError("Error136", LabelProperty.SINGLE); //"1 bouteille est presente dans ce rangement.");
							String erreur_txt2 = Program.getError("Error137", LabelProperty.SINGLE); //"Voulez vous changer l'emplacement de cette bouteille?");
							if (nbBottles == 1) {
								Debug("MESSAGE: 1 bottle in this place, modify?");
							} else {
								Debug("MESSAGE: " + nbBottles + " bottles in this place, Modify?");
								erreur_txt1 = MessageFormat.format(Program.getError("Error094", LabelProperty.PLURAL), nbBottles); //bouteilles sont presentes dans ce rangement.");
								erreur_txt2 = Program.getError("Error095", LabelProperty.PLURAL); //"Voulez vous changer l'emplacement de ces bouteilles?");
							}
							if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
								//Modify Name of place
								rangement.setNom(nom);
								rangement.updatePlace(listPart);
								Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setEmplacement(nom));
								nom_obj.setText("");
								updateView();
								ProgramPanels.updateAllPanels();
							} else {
								rangement.setNom(nom);
								rangement.updatePlace(listPart);
							}
						} else {
							rangement.updatePlace(listPart);
						}
						putTabStock();
					}
					if (bResul) {
						comboPlace.setSelectedIndex(0);
						label_cree.setText(Program.getError("Error123"));
					}
				}
			}
			new Timer().schedule(
					new TimerTask() {
						@Override
						public void run() {
							SwingUtilities.invokeLater(() -> label_cree.setText(""));
						}
					},
					5000
			);
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private void updatePlace(String nom, int nbPart, Rangement rangement) {
		rangement.setNom(nom);
		rangement.setLimited(islimited);
		rangement.setStartCaisse(start_caisse);
		rangement.setNbBottleInCaisse(limite);
		rangement.updateCaisse(nbPart);
		Program.setListCaveModified();
		Program.setModified();
		putTabStock();
	}

	private void putTabStock() {
		if (!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
	}

	/**
	 * Boutton Creer
	 */
	private void create_actionPerformed() {
		Debug("create_actionPerforming...");
		String nom = toCleanString(nom_obj.getText());

		//Controle si le nom est deja utilise
		boolean bResul = MyCellarControl.ctrl_existingName(nom);
		// Controles sur le nom (format, longueur...)
		bResul &= MyCellarControl.ctrlName(nom);

		if (m_caisse_chk.isSelected()) {
			Debug("Creating a simple place...");
			//Creation d'un rangement de type "Caisse"
			int nbPart = Integer.parseInt(nb_parties.getValue().toString());
			start_caisse = Integer.parseInt(nb_start_caisse.getValue().toString());
			islimited = checkLimite.isSelected();
			limite = Integer.parseInt(nb_limite.getValue().toString());

			if (bResul) {
				Debug("Creating...");
				final Rangement caisse = new Rangement.CaisseBuilder(nom)
						.nb_emplacement(nbPart)
						.start_caisse(start_caisse)
						.limit(islimited)
						.limite_caisse(limite).build();
				Program.addCave(caisse);
				Debug("Creation of '" + nom + "' completed.");
				nom_obj.setText("");
				label_cree.setText(Program.getLabel("Infos090")); //"Rangement cree.");
				ProgramPanels.updateAllPanels();
			}
		}	else {
			Debug("Creating complex place...");
			for (Part p : listPart) {
				if (p.getRows().isEmpty()) {
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum())); //"Erreur nombre de lignes incorrect sur la partie
					bResul = false;
				}
				for (Row r : p.getRows()) {
					if (r.getCol() == 0) {
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error004"), p.getNum()));//"Erreur nombre de colonnes incorrect sur la partie
						bResul = false;
					}
				}
			}
			//Type rangement
			if (m_jrb_dif_column_number.isSelected()) {
				Debug("Creating with different column number...");

				// Creation du rangement
				if (bResul) {
					Debug("Creating place...");
					Program.addCave(new Rangement(nom, listPart));
					Debug("Creating " + nom + " completed.");
					label_cree.setText(Program.getLabel("Infos090")); //"Rangement cree.");
					nom_obj.setText("");
					ProgramPanels.updateAllPanels();
				}
				//Fin test check
			}	else { // Si check1
				Debug("Creating place with same column number");
				// Recuperation du nombre de ligne par partie
				if (bResul) {
					Program.addCave(new Rangement(nom, listPart));
					Debug("Creating '" + nom + "' completed.");
					label_cree.setText(Program.getLabel("Infos090")); //"Rangement cree.");
					nom_obj.setText("");
					ProgramPanels.updateAllPanels();
				}
			}
		}
		if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_CREATE_MESS, false) && bResul) {
			Erreur.showKeyErreur(Program.getError("Error164"), "", MyCellarSettings.DONT_SHOW_CREATE_MESS);
		}
		if (bResul) {
			Start.getInstance().enableAll(true);
		}
	}

	/**
	 * simplePlace_itemStateChanged: Case a cocher pour choisir un rangement de type
	 * Caisse ou non
	 *
	 * @param e ItemEvent
	 */
	private void simplePlace_itemStateChanged(ItemEvent e) {
		label_cree.setText("");
		boolean checked = m_caisse_chk.isSelected();
		preview.setEnabled(!checked);
		panelType.setVisible(!checked);
		panelTable.setVisible(!checked);
		nb_start_caisse.setVisible(checked);
		panelStartCaisse.setVisible(checked);
		panelLimite.setVisible(checked);
		checkLimite.setVisible(true);
		if (m_caisse_chk.isSelected()) {
			final boolean checkLimiteSelected = checkLimite.isSelected();
			label_limite.setVisible(checkLimiteSelected);
			nb_limite.setVisible(checkLimiteSelected);
		}	else {
			preview.setEnabled(true);
			nb_parties.setValue(1);
			updatePartList();
		}
	}

	/**
	 * checkbox2_itemStateChanged: Case a cocher pour activer la limite
	 * Caisse ou non
	 *
	 * @param e ItemEvent
	 */
	private void checkbox2_itemStateChanged(ItemEvent e) {
		label_cree.setText("");
		nb_limite.setVisible(checkLimite.isSelected());
		label_limite.setVisible(checkLimite.isSelected());
	}

	/**
	 * preview_actionPerformed: Permet de previsualiser un rangement avant de le creer.
	 *
	 * @param e ActionEvent
	 */
	private void preview_actionPerformed(ActionEvent e) {
		if (!m_caisse_chk.isSelected()) {
			// Controle du nom
			String nom = toCleanString(nom_obj.getText());
			if (!MyCellarControl.ctrlName(nom)) {
				return;
			}

			for (Part p : listPart) {
				if (p.getRows().isEmpty()) {
					//"Erreur nombre de lignes incorrect sur la partie
					Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum()), Program.getError("Error109"));
					return;
				}
				for (Row r : p.getRows()) {
					if (r.getCol() == 0) {
						//"Erreur nombre de colonnes incorrect sur la partie
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error004"), p.getNum()), Program.getError("Error109"));
						return;
					}
				}
			}

			// Creation du rangement
			XmlUtils.writeRangements("", List.of(new Rangement(nom, listPart)), true);
			Program.open(new File(Program.getPreviewXMLFileName()));
		}
	}

	/**
	 * keylistener_actionPerformed: Raccourcis clavier
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
		if ((e.getKeyCode() == CREER && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
			create_actionPerformed();
		}
		if (e.getKeyCode() == PREVIEW && e.isControlDown() && preview.isEnabled()) {
			preview_actionPerformed(null);
		}
	}

	private static void Debug(String sText) {
		Program.Debug("Creer_Rangement: " + sText);
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		if (!toCleanString(nom_obj.getText()).isEmpty()) {
			String label = Program.getError("Error146");
			if (modify) {
				label = Program.getError("Error147");
			}
			if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, label + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
				return false;
			}
		}
		Debug("Quitting...");
		label_cree.setText("");
		comboPlace.setSelectedIndex(0);
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

	@Override
	public void setUpdateView() {}

	@Override
	public void updateView() {
		comboPlace.removeAllItems();
		comboPlace.addItem(Program.EMPTY_PLACE);
		Program.getCave().forEach(comboPlace::addItem);
	}

	@Override
	public void cut() {
		String text = nom_obj.getSelectedText();
		if (text != null) {
			String fullText = nom_obj.getText();
			nom_obj.setText(fullText.substring(0, nom_obj.getSelectionStart()) + fullText.substring(nom_obj.getSelectionEnd()));
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void copy() {
		String text = nom_obj.getSelectedText();
		if (text != null) {
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void paste() {
		String fullText = nom_obj.getText();
		nom_obj.setText(fullText.substring(0, nom_obj.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(nom_obj.getSelectionEnd()));
	}

	class CreateAction extends AbstractAction {
		private static final long serialVersionUID = 3560817063990123326L;

		CreateAction() {
			super("", MyCellarImage.ADD);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			create_actionPerformed();
		}
	}

	class ModifyAction extends AbstractAction {
		private static final long serialVersionUID = 546778254003860608L;

		ModifyAction() {
			super("", MyCellarImage.ADD);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			modifyPlace();
		}
	}
}
