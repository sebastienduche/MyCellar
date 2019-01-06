package mycellar;

import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSpinner;
import mycellar.core.PopupListener;
import net.miginfocom.swing.MigLayout;

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
import java.util.Timer;
import java.util.TimerTask;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 12.9
 * @since 06/01/19
 */
public class Creer_Rangement extends JPanel implements ITabListener, ICutCopyPastable {

	private final MyCellarComboBox<String> comboPlace = new MyCellarComboBox<>();
	private final JTextField nom_obj = new JTextField();
	private final MyCellarRadioButton m_jrb_same_column_number = new MyCellarRadioButton(Program.getLabel("Infos012"), true); //"Toutes les lignes ont le m�me nombre de colonnes"
	private final MyCellarRadioButton m_jrb_dif_column_number = new MyCellarRadioButton(Program.getLabel("Infos013"), false); //"Toutes les lignes n'ont pas le m�me nombre de colonnes"
	private final MyCellarCheckBox checkLimite = new MyCellarCheckBox(); //limite
	private final MyCellarLabel label_limite = new MyCellarLabel();
	private final MyCellarSpinner nb_limite = new MyCellarSpinner();
	private boolean islimited = false;
	private int limite = 0;
	private final MyCellarSpinner nb_parties = new MyCellarSpinner();
	private LinkedList<Part> listPart = new LinkedList<>();
	private static final char CREER = Program.getLabel("CREER").charAt(0);
	private static final char PREVIEW = Program.getLabel("PREVIEW").charAt(0);
	private final MyCellarSpinner nb_start_caisse = new MyCellarSpinner();
	private final MyCellarCheckBox m_caisse_chk = new MyCellarCheckBox(); //Caisse
	private final MyCellarLabel label_cree = new MyCellarLabel();
	private final MyCellarButton preview = new MyCellarButton();
	private int start_caisse = 0;
	private final JPanel panelType;
	private final JPanel panelStartCaisse;
	private final JPanel panelLimite;
	private final JPanel panelTable;
	private CreerRangementTableModel model;
	private boolean modify;
	static final long serialVersionUID = 280706;

	/**
	 * Creer_Rangement: Création d'un rangement
	 *
	 * @param modify boolean: Indique si l'appel est pour modifier
	 */
	public Creer_Rangement(final boolean modify) {
		Debug("Constructor for Modify? "+modify);
		this.modify = modify;

		MyCellarButton createButton = new MyCellarButton(MyCellarImage.ADD);
		if(modify) {
			createButton.setText(Program.getLabel("Infos079")); //"Modifier");
			createButton.addActionListener((e) -> modifyPlace());
		} else {
			createButton.setText(Program.getLabel("Infos018")); //"Créer");
			createButton.addActionListener(this::create_actionPerformed);
		}

		createButton.setMnemonic(CREER);
		preview.setMnemonic(PREVIEW);
		MyCellarLabel labelName = new MyCellarLabel(Program.getLabel("Infos020")); //"Nom du rangement:");
		MyCellarLabel labelModify = new MyCellarLabel(Program.getLabel("Infos226")); //"Sélectionner le rangement à modifier:"
		comboPlace.addItem("");
		for( Rangement r: Program.getCave()) {
			comboPlace.addItem(r.getNom());
		}
		comboPlace.addItemListener(this::comboPlace_itemStateChanged);
		ButtonGroup cbg = new ButtonGroup();
		cbg.add(m_jrb_same_column_number);
		cbg.add(m_jrb_dif_column_number);
		m_jrb_same_column_number.addItemListener((e) -> model.setSameColumnNumber(m_jrb_same_column_number.isSelected()));
		m_caisse_chk.setText(Program.getLabel("Infos024")); //"Rangement de type Caisse");
		checkLimite.setText(Program.getLabel("Infos238")); //Limite de caisse
		label_cree.setForeground(Color.red);
		label_cree.setFont(Program.FONT_DIALOG_SMALL);
		label_cree.setText("");
		label_cree.setHorizontalAlignment(SwingConstants.CENTER);
		preview.setText(Program.getLabel("Infos155")); //"Prévisualiser le rangement");
		
		preview.addActionListener(this::preview_actionPerformed);
		m_caisse_chk.addItemListener(this::checkbox1_itemStateChanged);
		checkLimite.addItemListener(this::checkbox2_itemStateChanged);

		nom_obj.addActionListener((e) -> label_cree.setText(""));
		nom_obj.addMouseListener(new PopupListener());

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keylistener_actionPerformed(e);
			}
		});


		label_limite.setText(Program.getLabel("Infos177"));

		nb_parties.addChangeListener((e) -> {
			if (Integer.parseInt(nb_parties.getValue().toString()) <= 0) {
				nb_parties.setValue(1);
			}
			if (Integer.parseInt(nb_parties.getValue().toString()) > 99) {
				nb_parties.setValue(99);
			}
			if (!m_caisse_chk.isSelected()) {
				int top = Integer.parseInt(nb_parties.getValue().toString());
				if (top > listPart.size()) {
					while (listPart.size() < top) {
						Part part = new Part(listPart.size() + 1);
						listPart.add(part);
						if (m_jrb_dif_column_number.isSelected()) {
							part.setRows(1);
						}
					}
				} else {
					while (listPart.size() > top) {
						listPart.removeLast();
					}
				}
				if (model != null) {
					model.setValues(listPart);
				}
			}
		});

		nb_start_caisse.addChangeListener((e) -> {
				if (Integer.parseInt(nb_start_caisse.getValue().toString()) < 0) {
					nb_start_caisse.setValue(0);
				}
				if (Integer.parseInt(nb_start_caisse.getValue().toString()) > 99) {
					nb_start_caisse.setValue(99);
				}
			});

		nb_limite.addChangeListener((e) -> {
				if (Integer.parseInt(nb_limite.getValue().toString()) <= 0) {
					nb_limite.setValue(1);
				}
				if (Integer.parseInt(nb_limite.getValue().toString()) > 999) {
					nb_limite.setValue(999);
				}
				if (Integer.parseInt(nb_limite.getValue().toString()) == 1) {
					label_limite.setText(Program.getLabel("Infos177"));
				}
				else {
					label_limite.setText(Program.getLabel("Infos178"));
				}
			});

		// Alimentation de la liste déroulante du nombre de parties
		nb_parties.setValue(1);
		//Alimentation du Spinner start_caisse
		nb_start_caisse.setValue(0);
		nb_start_caisse.setVisible(false);
		//Alimentation du Spinner limite_caisse
		nb_limite.setValue(1);
		
		setLayout(new MigLayout("","[grow][grow]","[][]"));

		JPanel panelModify = new JPanel();
		panelModify.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),"",0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelModify.setLayout(new MigLayout("","[]","[]"));
		panelModify.add(labelModify, "split 2");
		panelModify.add(comboPlace, "");
		
		if(modify) {
			add(panelModify, "span 2, wrap");
		}
		add(labelName, "span 2, split 3");
		add(nom_obj,"growx");
		add(m_caisse_chk, "wrap");

		panelType = new JPanel();
		panelType.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos021"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelType.setLayout(new GridLayout(0,2));
		panelType.add(m_jrb_same_column_number);
		panelType.add(m_jrb_dif_column_number);
		
		JPanel panelPartie = new JPanel();
		panelPartie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos023"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelPartie.setLayout(new MigLayout("","[]","[]"));
		panelPartie.add(nb_parties, "wmin 50");
		
		panelStartCaisse = new JPanel();
		panelStartCaisse.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos272"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelStartCaisse.setLayout(new MigLayout("","[]","[]"));
		panelStartCaisse.add(nb_start_caisse, "wmin 50");
		
		panelLimite = new JPanel();
		panelLimite.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos274"),0,0,Program.FONT_PANEL), BorderFactory.createEmptyBorder()));
		panelLimite.setLayout(new MigLayout("","[][]","[]"));
		panelLimite.add(checkLimite, "gapright 10");
		panelLimite.add(nb_limite, "split 2, wmin 50, hidemode 3");
		panelLimite.add(label_limite, "hidemode 3");
		
		model = new CreerRangementTableModel();
		JTable tableParties = new JTable(model);
		model.setValues(listPart);

		panelTable = new JPanel();
		panelTable.setLayout(new MigLayout("","[grow]","[grow]"));
		panelTable.add(new JScrollPane(tableParties), "grow");
		
		JPanel panelPartiesConfig = new JPanel();
		panelPartiesConfig.setLayout(new MigLayout("","[][]","[grow]"));
		panelPartiesConfig.add(panelType,"growy, hidemode 3, gapright 30");
		panelPartiesConfig.add(panelPartie,"growx, wmin 150, gapright 30");
		panelPartiesConfig.add(panelStartCaisse,"growx, wmin 250, hidemode 3, gapright 30");
		panelPartiesConfig.add(panelLimite,"growx, wmin 250, hidemode 3, gapright 30");

		add(panelPartiesConfig, "span 2, wrap, hidemode 3");
		add(panelTable, "span 2, grow, wrap, hidemode 3");
		add(label_cree, "center, span 2, wrap");
		add(createButton, "span 2, split 2, center");
		add(preview,"");
		
		m_jrb_dif_column_number.addItemListener((e) -> model.setSameColumnNumber(!m_jrb_dif_column_number.isSelected()));

		model.setSameColumnNumber(true);

		if(modify) {
			enableAll(false);
		}

		m_caisse_chk.setSelected(true);
		setVisible(true);

	}

	private void comboPlace_itemStateChanged(ItemEvent e) {
		int nCave = comboPlace.getSelectedIndex();
		Rangement r;
		if(nCave > 0) {
			r = Program.getCave(nCave - 1);
		} else {
			nom_obj.setText("");
			label_cree.setText("");
			model.setValues(new LinkedList<>());
			enableAll(false);
			return;
		}
		if (r != null) {
			enableAll(true);
			label_cree.setText("");
			nom_obj.setText(r.getNom());
			m_caisse_chk.setSelected(r.isCaisse());
			m_caisse_chk.setEnabled(false);
			if (r.isCaisse()) {
				checkLimite.setSelected(r.isLimited());
				if (r.isLimited())
					nb_limite.setValue(r.getNbColonnesStock());
				nb_parties.setValue(r.getNbEmplacements());
				nb_start_caisse.setValue(r.getStartCaisse());
			} else {
				m_jrb_same_column_number.setSelected(r.isSameColumnNumber());
				m_jrb_dif_column_number.setSelected(!r.isSameColumnNumber());
				listPart = r.getPlace();
				model.setValues(listPart);
				nb_parties.setValue(r.getNbEmplacements());
			}
		}
	}

	private void enableAll(boolean b) {
		nom_obj.setEnabled(b);
		m_caisse_chk.setEnabled(b);
		nb_limite.setEnabled(b);
		checkLimite.setEnabled(b);
		nb_parties.setEnabled(b);
		nb_start_caisse.setEnabled(b);
	}

	private void modifyPlace() {
		try {
			Debug("modify_actionPerforming...");

			int num_rang = comboPlace.getSelectedIndex();
			if (num_rang == 0) {
				Debug("ERROR: Please select a place");
				Erreur.showSimpleErreur(Program.getError("Error093")); //"Veuillez sélectionner un rangement")
				return;
			}
			num_rang--;

			final String nom = nom_obj.getText().trim();
			// Contrôle sur le nom
			if(!MyCellarControl.ctrl_Name(nom)) {
				return;
			}

			boolean bResul = true;
			Debug("Advanced modifying...");
			if (m_caisse_chk.isSelected()) {
				Debug("Modifying Caisse...");
				//Modification d'un rangement de type "Caisse"
				start_caisse = nb_start_caisse.getIntValue();
				islimited = checkLimite.isSelected();
				limite = nb_limite.getIntValue();
				int nbPart = nb_parties.getIntValue();

				Rangement rangement = Program.getCave(num_rang);
				if (rangement != null) {
					int nb_bottle = rangement.getNbCaseUseAll();
					String name = rangement.getNom();
					if (rangement.getNbEmplacements() > nbPart) {
						// Contrôle que les emplacements supprimés sont vides
						for (int i = nbPart; i < rangement.getNbEmplacements(); i++) {
							if (rangement.getNbCaseUse(nbPart) > 0) {
								Debug("ERROR: Unable to delete simple place part with bottles!");
								Erreur.showSimpleErreur(MessageFormat.format(Program.getError("CreerRangement.CantDeletePartCaisse"), (i + rangement.getStartCaisse())));
								return;
							}
						}
					}

					if (nb_bottle > 0) {
						if (name.compareTo(nom) != 0) {
							String erreur_txt1, erreur_txt2;
							if (nb_bottle == 1) {
								Debug("MESSAGE: 1 bottle in this place, modify?");
								erreur_txt1 = Program.getError("Error136"); //"1 bouteille est présente dans ce rangement.");
								erreur_txt2 = Program.getError("Error137"); //"Voulez vous changer l'emplacement de cette bouteille?");
							} else {
								Debug("MESSAGE: " + nb_bottle + " bottles in this place, Modify?");
								erreur_txt1 = MessageFormat.format(Program.getError("Error094"), nb_bottle); //bouteilles sont présentes dans ce rangement.");
								erreur_txt2 = Program.getError("Error095"); //"Voulez vous changer l'emplacement de ces bouteilles?");
							}
							if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
								//Modify Name of place
								Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setEmplacement(nom));

								rangement.setNom(nom);
								rangement.setLimited(islimited);
								rangement.setStartCaisse(start_caisse);
								rangement.setNbBottleInCaisse(limite);
								rangement.updateCaisse(nbPart);
								Program.setListCaveModified();
								Program.setModified();
								putTabStock();

								nom_obj.setText("");
								label_cree.setText(Program.getError("Error123"));

								updateView();
								Program.updateAllPanels();
							} else {
								rangement.setNom(nom);
								rangement.setLimited(islimited);
								rangement.setStartCaisse(start_caisse);
								rangement.setNbBottleInCaisse(limite);
								rangement.updateCaisse(nbPart);
								Program.setListCaveModified();
								Program.setModified();
								putTabStock();

								updateView();
								Program.updateAllPanels();
							}
						} else if (rangement.getStartCaisse() != start_caisse) {
							// Le numero de la premiere partie a change, renumroter
							String erreur_txt1 = MessageFormat.format(Program.getError("CreerRangement.UpdatedBottlePart"), start_caisse, rangement.getStartCaisse());
							String erreur_txt2 = Program.getError("CreerRangement.AskUpdateBottlePart");

							if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
								//Modify start part number
								final int difference = start_caisse - rangement.getStartCaisse();
								Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setNumLieu(b.getNumLieu() + difference));

								rangement.setNom(nom);
								rangement.setLimited(islimited);
								rangement.setStartCaisse(start_caisse);
								rangement.setNbBottleInCaisse(limite);
								rangement.updateCaisse(nbPart);
								Program.setListCaveModified();
								Program.setModified();
								putTabStock();

								nom_obj.setText("");
								label_cree.setText(Program.getError("Error123"));

								updateView();
								Program.updateAllPanels();
							} else {
								rangement.setNom(nom);
								rangement.setLimited(islimited);
								rangement.setStartCaisse(start_caisse);
								rangement.setNbBottleInCaisse(limite);
								rangement.updateCaisse(nbPart);
								Program.setListCaveModified();
								Program.setModified();
								putTabStock();

								updateView();
								Program.updateAllPanels();
							}
						}
					} else {
						// Pas de bouteilles à modifier
						nom_obj.setText("");
						rangement.setNom(nom);
						rangement.setLimited(islimited);
						rangement.setStartCaisse(start_caisse);
						rangement.setNbBottleInCaisse(limite);
						rangement.updateCaisse(nbPart);
						Program.setListCaveModified();
						Program.setModified();
						putTabStock();

						label_cree.setText(Program.getError("Error123"));

						updateView();
						Program.updateAllPanels();
					}
					modify = true;
					Debug("Modify completed");
					label_cree.setText(Program.getError("Error123")); //"Rangement modifié.");
				}
			}	else {
				// Rangement complexe
				Debug("Modifying complex place...");
				Rangement rangement = Program.getCave(comboPlace.getSelectedIndex() - 1);
				if (rangement != null) {
					int nbBottles = rangement.getNbCaseUseAll();
					for (Part p : listPart) {
						if (p.getRows().isEmpty()) {
							Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum())); //"Erreur nombre de lignes incorrect sur la partie
							return;
						}
						for (Row r : p.getRows()) {
							if (r.getCol() == 0) {
								Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error004"), p.getNum()));//"Erreur nombre de colonnes incorrect sur la partie
								return;
							}
						}
					}

					if (nbBottles == 0) {
						rangement.setNom(nom);
						rangement.setPlace(listPart);
						Program.setListCaveModified();
						putTabStock();
						nom_obj.setText("");
						comboPlace.removeAllItems();
						comboPlace.addItem("");

						for (Rangement r : Program.getCave()) {
							comboPlace.addItem(r.getNom());
						}
						Program.updateAllPanels();

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
						for (int i = 0; i < listPart.size(); i++) {
							if (!bResul) {
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
									// Impossible de réduire le nombre de ligne de la partie, bouteilles présentes
								}
							}
							if (bResul) {
								for (int j = 0; j < part.getRowSize(); j++) {
									if (!bResul)
										break;
									int nbCol = -1;
									if (i < rangement.getNbEmplacements() && j < rangement.getNbLignes(i)) {
										nbCol = rangement.getNbColonnes(i, j);
									}
									int newNbCol = part.getRow(j).getCol();
									if (nbCol > newNbCol) {
										for (int k = newNbCol; k < nbCol; k++) {
											if (!bResul)
												break;
											if (rangement.getBouteille(i, j, k) != null) {
												bResul = false;
												Debug("ERROR: Unable to reduce the size of the number of column");
												Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error203"), Integer.toString(j + 1), Integer.toString(i + 1)));
												// Impossible de réduire le nombre de colonne de la ligne de la partie, bouteilles présentes
											}
										}
									}
								}
							}
						}

						if (bResul) {
							String name = rangement.getNom();
							if (name.compareTo(nom) != 0) {
								String erreur_txt1 = Program.getError("Error136"); //"1 bouteille est présente dans ce rangement.");
								String erreur_txt2 = Program.getError("Error137"); //"Voulez vous changer l'emplacement de cette bouteille?");
								if (nbBottles == 1) {
									Debug("MESSAGE: 1 bottle in this place, modify?");
								} else {
									Debug("MESSAGE: " + nbBottles + " bottles in this place, Modify?");
									erreur_txt1 = MessageFormat.format(Program.getError("Error094"), nbBottles); //bouteilles sont présentes dans ce rangement.");
									erreur_txt2 = Program.getError("Error095"); //"Voulez vous changer l'emplacement de ces bouteilles?");
								}
								if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
									//Modify Name of place
									rangement.setNom(nom);
									rangement.setPlace(listPart);
									Program.setListCaveModified();
									Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setEmplacement(nom));
									nom_obj.setText("");
									comboPlace.removeAllItems();
									comboPlace.addItem("");

									for (Rangement r : Program.getCave()) {
										comboPlace.addItem(r.getNom());
									}
									Program.updateAllPanels();
								} else {
									rangement.setNom(nom);
									rangement.setPlace(listPart);
									Program.setListCaveModified();
									Program.setModified();
								}
							} else {
								rangement.setPlace(listPart);
								Program.setListCaveModified();
								Program.setModified();
							}
							putTabStock();
						}
						if (bResul) {
							comboPlace.setSelectedIndex(0);
							label_cree.setText(Program.getError("Error123"));
						}
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

	private void putTabStock() {
		if(!RangementUtils.putTabStock()) {
			new OpenShowErrorsAction().actionPerformed(null);
		}
	}

	/**
	 * button1_actionPerformed: Boutton Créer
	 *
	 * @param e ActionEvent
	 */
	private void create_actionPerformed(ActionEvent e) {
		try {
			Debug("create_actionPerforming...");
			String nom = nom_obj.getText().trim();

			//Contrôle si le nom est déjà utilisé
			boolean bResul = MyCellarControl.ctrl_existingName(nom);
			// Contrôles sur le nom (format, longueur...)
			bResul = bResul && MyCellarControl.ctrl_Name(nom);

			if (m_caisse_chk.isSelected()) {
				Debug("Creating a box...");
				//Création d'un rangement de type "Caisse"
				int nbPart = Integer.parseInt(nb_parties.getValue().toString());
				start_caisse = Integer.parseInt(nb_start_caisse.getValue().toString());
				islimited = checkLimite.isSelected();
				limite = Integer.parseInt(nb_limite.getValue().toString());

				if (bResul) {
					Debug("Creating...");
					Rangement r = new Rangement(nom, nbPart, start_caisse, islimited, limite);
					Program.addCave(r);
					Debug("Creation "+ nom +" completed.");
					nom_obj.setText("");
					label_cree.setText(Program.getLabel("Infos090")); //"Rangement créé.");
					Program.updateAllPanels();
				}
			}	else {
				Debug("Creating complex place...");
				for(Part p: listPart) {
					if(p.getRows().isEmpty()) {
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum())); //"Erreur nombre de lignes incorrect sur la partie
						bResul = false;
					}
					for(Row r: p.getRows()) {
						if(r.getCol() == 0) {
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
						Debug("Creating "+nom+" completed.");
						label_cree.setText(Program.getLabel("Infos090")); //"Rangement créé.");
						nom_obj.setText("");
						Program.updateAllPanels();
					}
					//Fin test check
				}
				else { // Si check1
					Debug("Creating place with same column number");
					// Récupération du nombre de ligne par partie
					if(bResul) {
						Program.addCave(new Rangement(nom, listPart));
						Debug("Creating "+nom+" completed.");
						label_cree.setText(Program.getLabel("Infos090")); //"Rangement créé.");
						nom_obj.setText("");
						Program.updateAllPanels();
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
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	//Caisse ou non?
	/**
	 * checkbox1_itemStateChanged: Case à cocher pour choisir un rangement de type
	 * Caisse ou non
	 *
	 * @param e ItemEvent
	 */
	private void checkbox1_itemStateChanged(ItemEvent e) {
		label_cree.setText("");
		try {
			if (m_caisse_chk.isSelected()) {
				checkLimite.setVisible(true);
				if (checkLimite.isSelected()) {
					label_limite.setVisible(true);
					nb_limite.setVisible(true);
				}	else {
					nb_limite.setVisible(false);
					label_limite.setVisible(false);
				}
				nb_start_caisse.setVisible(true);
				panelType.setVisible(false);
				panelStartCaisse.setVisible(true);
				panelLimite.setVisible(true);
				panelTable.setVisible(false);
				preview.setEnabled(false);
			}	else {
				panelStartCaisse.setVisible(false);
				panelLimite.setVisible(false);
				panelTable.setVisible(true);
				nb_start_caisse.setVisible(false);
				panelType.setVisible(true);
				checkLimite.setVisible(true);
				preview.setEnabled(true);
				nb_parties.setValue(1);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * checkbox2_itemStateChanged: Case à cocher pour activer la limite
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
	 * preview_actionPerformed: Permet de prévisualiser un rangement avant de le
	 * créer.
	 *
	 * @param e ActionEvent
	 */
	private void preview_actionPerformed(ActionEvent e) {
		try {

			if (!m_caisse_chk.isSelected()) {
				// Contrôle du nom
				String nom = nom_obj.getText().trim();
				if(!MyCellarControl.ctrl_Name( nom )) {
					return;
				}

				for(Part p: listPart) {
					if(p.getRows().isEmpty()) {
						//"Erreur nombre de lignes incorrect sur la partie
						Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum()), Program.getError("Error109"));
						return;
					}
					for(Row r: p.getRows()) {
						if(r.getCol() == 0) {
							//"Erreur nombre de colonnes incorrect sur la partie
							Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error004"), p.getNum()), Program.getError("Error109"));
							return;
						}
					}
				}

				// Création du rangement
				Rangement r = new Rangement(nom, listPart);
				LinkedList<Rangement> rangements = new LinkedList<>();
				rangements.add(r);
				MyXmlDom.writeRangements("", rangements, true);
				Program.open( new File(Program.getPreviewXMLFileName()) );
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * keylistener_actionPerformed: Raccourcis clavier
	 *
	 * @param e KeyEvent
	 */
	private void keylistener_actionPerformed(KeyEvent e) {
		
		if ( (e.getKeyCode() == CREER && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
			create_actionPerformed(null);
		}
		if (e.getKeyCode() == PREVIEW && e.isControlDown() && preview.isEnabled()) {
			preview_actionPerformed(null);
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Creer_Rangement: " + sText);
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		if (!nom_obj.getText().trim().isEmpty()) {
			String label = Program.getError("Error146");
			if(modify) {
				label = Program.getError("Error147");
			}
			if(JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, label + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
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

	public void updateView() {
		comboPlace.removeAllItems();
		comboPlace.addItem("");
		for(Rangement r: Program.getCave()) {
			comboPlace.addItem(r.getNom());
		}
	}

	@Override
	public void cut() {
		String text = nom_obj.getSelectedText();
		String fullText = nom_obj.getText();
		if(text != null) {
			nom_obj.setText(fullText.substring(0, nom_obj.getSelectionStart()) + fullText.substring(nom_obj.getSelectionEnd()));
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void copy() {
		String text = nom_obj.getSelectedText();
		if(text != null) {
			Program.CLIPBOARD.copier(text);
		}
	}

	@Override
	public void paste() {
		String fullText = nom_obj.getText();
		nom_obj.setText(fullText.substring(0, nom_obj.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(nom_obj.getSelectionEnd()));
	}

}
