package mycellar;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarRadioButton;
import mycellar.core.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;



/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 11.5
 * @since 18/07/17
 */
public class Creer_Rangement extends JPanel implements ITabListener {

	private final MyCellarButton createButton = new MyCellarButton(MyCellarImage.ADD);
	private final MyCellarLabel labelName = new MyCellarLabel();
	private final MyCellarLabel labelModify = new MyCellarLabel();
	private final MyCellarComboBox<String> comboPlace = new MyCellarComboBox<String>();
	private final JTextField nom_obj = new JTextField();
	private final ButtonGroup cbg = new ButtonGroup();
	private final MyCellarRadioButton m_jrb_same_column_number = new MyCellarRadioButton(Program.getLabel("Infos012"), true); //"Toutes les lignes ont le m�me nombre de colonnes"
	private final MyCellarRadioButton m_jrb_dif_column_number = new MyCellarRadioButton(Program.getLabel("Infos013"), false); //"Toutes les lignes n'ont pas le m�me nombre de colonnes"
	private final MyCellarCheckBox checkLimite = new MyCellarCheckBox(); //limite
	private final MyCellarLabel label_limite = new MyCellarLabel();
	private final MyCellarSpinner nb_limite = new MyCellarSpinner();
	private boolean islimited = false;
	private int limite = 0;
	private final MyCellarSpinner nb_parties = new MyCellarSpinner();
	private LinkedList<Part> listPart = new LinkedList<Part>();
	private String erreur_txt1; //Texte de l'erreur
	private char CREER = Program.getLabel("CREER").charAt(0);
	private char PREVIEW = Program.getLabel("PREVIEW").charAt(0);
	private final MyCellarSpinner nb_start_caisse = new MyCellarSpinner();
	private final MyCellarCheckBox m_caisse_chk = new MyCellarCheckBox(); //Caisse
	private final MyCellarLabel label_cree = new MyCellarLabel();
	private final MyCellarButton preview = new MyCellarButton();
	private int start_caisse = 0;
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem couper = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private final JMenuItem copier = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private final JMenuItem coller = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private final JMenuItem cut = new JMenuItem(Program.getLabel("Infos241"), new ImageIcon("./resources/Cut16.gif"));
	private final JMenuItem copy = new JMenuItem(Program.getLabel("Infos242"), new ImageIcon("./resources/Copy16.gif"));
	private final JMenuItem paste = new JMenuItem(Program.getLabel("Infos243"), new ImageIcon("./resources/Paste16.gif"));
	private final MyClipBoard clipboard = new MyClipBoard();
	private final MouseListener popup_l = new PopupListener();
	private Component objet1 = null;
	private JPanel panelType;
	private JPanel panelModify;
	private JPanel panelPartie;
	private JPanel panelStartCaisse;
	private JPanel panelLimite;
	private JTable tableParties;
	private JScrollPane scrollPaneTable;
	private JPanel panelTable;
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
		
		if(modify)
			createButton.setText(Program.getLabel("Infos079")); //"Modifier");
		else
			createButton.setText(Program.getLabel("Infos018")); //"Créer");
		createButton.addActionListener((e) -> {
			if(modify)
				modifyPlace();
			else
				create_actionPerformed(e);
		});
		createButton.setMnemonic(CREER);
		preview.setMnemonic(PREVIEW);
		labelName.setText(Program.getLabel("Infos020")); //"Nom du rangement:");
		labelModify.setText(Program.getLabel("Infos226")); //"Sélectionner le rangement à modifier:"
		comboPlace.addItem("");
		for( Rangement r: Program.getCave())
			comboPlace.addItem(r.getNom());
		comboPlace.addItemListener((e) -> comboPlace_itemStateChanged(e));
		cbg.add(m_jrb_same_column_number);
		cbg.add(m_jrb_dif_column_number);
		m_jrb_same_column_number.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				model.setSameColumnNumber(m_jrb_same_column_number.isSelected());
			}
		});
		m_caisse_chk.setText(Program.getLabel("Infos024")); //"Rangement de type Caisse");
		checkLimite.setText(Program.getLabel("Infos238")); //Limite de caisse
		label_cree.setForeground(Color.red);
		label_cree.setFont(Program.font_dialog_small);
		label_cree.setText("");
		label_cree.setHorizontalAlignment(0);
		preview.setText(Program.getLabel("Infos155")); //"Prévisualiser le rangement");
		
		preview.addActionListener((e) -> preview_actionPerformed(e));
		m_caisse_chk.addItemListener((e) -> checkbox1_itemStateChanged(e));
		checkLimite.addItemListener((e) -> checkbox2_itemStateChanged(e));

		nom_obj.addActionListener((e) -> nom_obj_textValueChanged(e));

		this.addKeyListener(new java.awt.event.KeyListener() {
			public void keyReleased(java.awt.event.KeyEvent e) {}

			public void keyPressed(java.awt.event.KeyEvent e) {
				keylistener_actionPerformed(e);
			}

			public void keyTyped(java.awt.event.KeyEvent e) {}
		});

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
		nom_obj.addMouseListener(popup_l);
		cut.setEnabled(false);
		copy.setEnabled(false);
		cut.setAccelerator(KeyStroke.getKeyStroke('X', ActionEvent.CTRL_MASK));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', ActionEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', ActionEvent.CTRL_MASK));

		label_limite.setText(Program.getLabel("Infos177"));

		nb_parties.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				if (Integer.parseInt(nb_parties.getValue().toString()) <= 0) {
					nb_parties.setValue(new Integer(1));
				}
				if (Integer.parseInt(nb_parties.getValue().toString()) > 99) {
					nb_parties.setValue(new Integer(99));
				}
				if (!m_caisse_chk.isSelected()) {
					int top = Integer.parseInt(nb_parties.getValue().toString());
					if(top > listPart.size())
					{
						while(listPart.size() < top)
						{
							Part part = new Part(listPart.size()+1);
							listPart.add(part);
							if(m_jrb_dif_column_number.isSelected())
								part.setRows(1);
						}
					}
					else
					{
						while(listPart.size() > top)
							listPart.removeLast();
					}
					if(model != null)
						model.setValues(listPart);
				}
			}
		});

		nb_start_caisse.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				if (Integer.parseInt(nb_start_caisse.getValue().toString()) < 0) {
					nb_start_caisse.setValue(new Integer(0));
				}
				if (Integer.parseInt(nb_start_caisse.getValue().toString()) > 99) {
					nb_start_caisse.setValue(new Integer(99));
				}
			}
		});

		nb_limite.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				if (Integer.parseInt(nb_limite.getValue().toString()) <= 0) {
					nb_limite.setValue(new Integer(1));
				}
				if (Integer.parseInt(nb_limite.getValue().toString()) > 999) {
					nb_limite.setValue(new Integer(999));
				}
				if (Integer.parseInt(nb_limite.getValue().toString()) == 1) {
					label_limite.setText(Program.getLabel("Infos177"));
				}
				else {
					label_limite.setText(Program.getLabel("Infos178"));
				}
			}
		});

		// Alimentation de la liste d�roulante du nombre de parties
		nb_parties.setValue(new Integer(1));
		//Alimentation du Spinner start_caisse
		nb_start_caisse.setValue(new Integer(0));
		nb_start_caisse.setVisible(false);
		//Alimentation du Spinner limite_caisse
		nb_limite.setValue(new Integer(1));
		
		this.setLayout(new MigLayout("","[grow][grow]","[][]"));

		panelModify = new JPanel();
		panelModify.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),"",0,0,Program.font_panel), BorderFactory.createEmptyBorder()));
		panelModify.setLayout(new MigLayout("","[]","[]"));
		panelModify.add(labelModify, "split 2");
		panelModify.add(comboPlace, "");
		
		if(modify)
			add(panelModify, "span 2, wrap");
		add(labelName, "span 2, split 3");
		add(nom_obj,"growx");
		add(m_caisse_chk, "wrap");

		panelType = new JPanel();
		panelType.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos021"),0,0,Program.font_panel), BorderFactory.createEmptyBorder()));
		panelType.setLayout(new GridLayout(0,2));
		panelType.add(m_jrb_same_column_number);
		panelType.add(m_jrb_dif_column_number);
		
		panelPartie = new JPanel();
		panelPartie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos023"),0,0,Program.font_panel), BorderFactory.createEmptyBorder()));
		panelPartie.setLayout(new MigLayout("","[]","[]"));
		panelPartie.add(nb_parties, "wmin 50");
		
		panelStartCaisse = new JPanel();
		panelStartCaisse.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos272"),0,0,Program.font_panel), BorderFactory.createEmptyBorder()));
		panelStartCaisse.setLayout(new MigLayout("","[]","[]"));
		panelStartCaisse.add(nb_start_caisse, "wmin 50");
		
		panelLimite = new JPanel();
		panelLimite.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),Program.getLabel("Infos274"),0,0,Program.font_panel), BorderFactory.createEmptyBorder()));
		panelLimite.setLayout(new MigLayout("","[][]","[]"));
		panelLimite.add(checkLimite, "gapright 10");
		panelLimite.add(nb_limite, "split 2, wmin 50, hidemode 3");
		panelLimite.add(label_limite, "hidemode 3");
		
		model = new CreerRangementTableModel();
		tableParties = new JTable(model);
		scrollPaneTable = new JScrollPane(tableParties);
		model.setValues(listPart);

		panelTable = new JPanel();
		panelTable.setLayout(new MigLayout("","[grow]","[grow]"));
		panelTable.add(scrollPaneTable, "grow");
		
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
		
		m_jrb_dif_column_number.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				model.setSameColumnNumber(!m_jrb_dif_column_number.isSelected());
			}
		});

		model.setSameColumnNumber(true);

		if(modify)
			enableAll(false);

		int val = Program.getCaveConfigInt("CREER_R_DEFAULT", 0);
		if (val == 0) {
			m_caisse_chk.setSelected(true);
		}
		this.setVisible(true);

	}

	protected void comboPlace_itemStateChanged(ItemEvent e) {
		int nCave = comboPlace.getSelectedIndex();
		Rangement r = Program.getCave(nCave-1);
		if(nCave == 0 || r == null)
		{
			nom_obj.setText("");
			label_cree.setText("");
			model.setValues(new LinkedList<Part>());
			enableAll(false);
			return;
		}
		enableAll(true);
		label_cree.setText("");
		nom_obj.setText(r.getNom());
		m_caisse_chk.setSelected(r.isCaisse());
		m_caisse_chk.setEnabled(false);
		if(r.isCaisse())
		{
			checkLimite.setSelected(r.isLimited());
			if(r.isLimited())
				nb_limite.setValue(r.getNbColonnesStock());
			nb_parties.setValue(r.getNbEmplacements());
			nb_start_caisse.setValue(r.getStartCaisse());
		}
		else
		{
			m_jrb_same_column_number.setSelected(r.isSameColumnNumber());
			m_jrb_dif_column_number.setSelected(!r.isSameColumnNumber());
			listPart = r.getPlace();
			model.setValues(listPart);
			nb_parties.setValue(r.getNbEmplacements());
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

	protected void modifyPlace() {
		try {
			Debug("modify_actionPerforming...");
			boolean bResul = true;
			String nom = ""; //Nom de l'emplacement

			int num_rang = comboPlace.getSelectedIndex();
			if (num_rang == 0) {
				Debug("ERROR: Please select a place");
				new Erreur(Program.getError("Error093"), ""); //"Veuillez sélectionner un rangement")
				return;
			}
			num_rang--;

			nom = new String(nom_obj.getText().trim());
			// Contrôle sur le nom
			if(!MyCellarControl.ctrl_Name( nom ))
				return;

			Debug("Advanced modifying...");
			if (m_caisse_chk.isSelected()) {
				Debug("Modifying Caisse...");
				//Modification d'un rangement de type "Caisse"
				nom = new String(nom_obj.getText().trim());
				start_caisse = Integer.parseInt(nb_start_caisse.getValue().toString());
				islimited = checkLimite.isSelected();
				limite = Integer.parseInt(nb_limite.getValue().toString());
				int nbPart = Integer.parseInt(nb_parties.getValue().toString());

				Rangement rangement = Program.getCave(num_rang);
				int nb_bottle = rangement.getNbCaseUseAll();
				String name = rangement.getNom();
				if(rangement.getNbEmplacements() > nbPart) {
					// Contrôle que les emplacements supprimés sont vides
					for(int i=nbPart; i<rangement.getNbEmplacements(); i++) {
						if(rangement.getNbCaseUse(nbPart) > 0) {
							Debug("ERROR: Unable to delete simple place part with bottles!");
							new Erreur(MessageFormat.format(Program.getError("CreerRangement.CantDeletePartCaisse"), (i+rangement.getStartCaisse())));
							return;
						}
					}
				}

				if (nb_bottle > 0 && name.compareTo(nom) != 0) {
					String erreur_txt1 = Program.getError("Error136"); //"1 bouteille est pr�sente dans ce rangement.");
					String erreur_txt2 = Program.getError("Error137"); //"Voulez vous changer l'emplacement de cette bouteille?");
					if (nb_bottle == 1) {
						Debug("MESSAGE: 1 bottle in this place, modify?");
					}
					else {
						Debug("MESSAGE: "+nb_bottle+" bottles in this place, Modify?");
						erreur_txt1 = new String(nb_bottle + " " + Program.getError("Error094")); //bouteilles sont pr�sentes dans ce rangement.");
						erreur_txt2 = Program.getError("Error095"); //"Voulez vous changer l'emplacement de ces bouteilles?");
					}
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						//Modify Name of place
						rangement.setNom(nom);
						rangement.setLimited(islimited);
						rangement.setStartCaisse(start_caisse);
						rangement.setNbBottleInCaisse(limite);
						rangement.updateCaisse(nbPart);
						int nb_all = Program.getStorage().getAllNblign();
						for (int i = 0; i < nb_all; i++) {
							Bouteille b = Program.getStorage().getAllAt(i);

							String empla1 = b.getEmplacement();
							if (empla1.equals(name)) {
								b.setEmplacement(nom);
							}
						}
						//rangement.putTabStock();
						RangementUtils.putTabStock1();

						nom_obj.setText("");
						label_cree.setText(Program.getError("Error123"));

						updateView();
						Program.updateAllPanels();
					}
					else {
						rangement.setNom(nom);
						rangement.setLimited(islimited);
						rangement.setStartCaisse(start_caisse);
						rangement.setNbBottleInCaisse(limite);
						rangement.updateCaisse(nbPart);
						//rangement.putTabStock();
						RangementUtils.putTabStock1();
						updateView();
						Program.updateAllPanels();
					}
				}
				else {
					// Pas de bouteilles à modifier
					rangement.setNom(nom);
					nom_obj.setText("");
					rangement.setLimited(islimited);
					rangement.setStartCaisse(start_caisse);
					rangement.setNbBottleInCaisse(limite);
					rangement.updateCaisse(nbPart);
					//rangement.putTabStock();
					RangementUtils.putTabStock1();

					label_cree.setText(Program.getError("Error123"));

					updateView();
					Program.updateAllPanels();
				}
				if (bResul) {
					modify = true;
					Debug("Modify completed");
					label_cree.setText(Program.getError("Error123")); //"Rangement modifi�.");
				}
			}
			else {
				// Rangement complexe
				Debug("Modifying complex place...");
				Rangement rangement = Program.getCave(comboPlace.getSelectedIndex()-1);
				int nbBottles = rangement.getNbCaseUseAll();
				for(Part p: listPart) {
					if(p.getRows().size() == 0) {
						erreur_txt1 = new String(Program.getError("Error009") + " " + p.getNum() + "."); //"Erreur nombre de lignes incorrect sur la partie
						new Erreur(erreur_txt1);
						bResul = false;
					}
					for(Row r: p.getRows()) {
						if(r.getCol() == 0) {
							erreur_txt1 = new String(Program.getError("Error004") + " " + p.getNum() + "."); //"Erreur nombre de colonnes incorrect sur la partie
							new Erreur(erreur_txt1);
							bResul = false;
						}
					}
				}
				if(bResul) {
					if(nbBottles == 0) {
						rangement.setNom(nom);
						rangement.setPlace(listPart);
						//rangement.putTabStock();
						RangementUtils.putTabStock1();
						nom_obj.setText("");
						comboPlace.removeAllItems();
						comboPlace.addItem("");

						for (int z = 0; z < Program.GetCaveLength(); z++) {
							comboPlace.addItem(Program.getCave(z).getNom());
						}
						Program.updateAllPanels();

						label_cree.setText(Program.getError("Error123"));
					}
					else
					{
						if(rangement.getNbEmplacements() > listPart.size()) {
							int nb = 0;
							for(int i=listPart.size(); i<rangement.getNbEmplacements(); i++) {
								nb += rangement.getNbCaseUse(i);
							}
							if(nb > 0) {
								bResul = false;
								Debug("ERROR: Unable to reduce the number of place");
								new Erreur(Program.getError("Error201"),"");
							}
						}
						if(bResul) {
							for(int i=0; i<listPart.size(); i++) {
								if(!bResul)
									continue;
								Part part = listPart.get(i);
								int nbRow = -1;
								if(i<rangement.getNbEmplacements())
									nbRow = rangement.getNbLignes(i);
								int newNbRow = part.getRowSize();
								if(nbRow > newNbRow) {
									int nb = 0;
									for(int j=newNbRow; j<nbRow;j++) {
										nb += rangement.getNbCaseUseLigne(i, j);
									}
									if(nb > 0) {
										bResul = false;
										String sText = Program.getError("Error202");
										sText = sText.replaceFirst("A1", Integer.toString(i+1));
										Debug("ERROR: Unable to reduce the number of row");
										new Erreur(sText,"");
										// Impossible de réduire le nombre de ligne de la partie, bouteilles présentes
									}
								}
								if(bResul) {
									for(int j=0; j<part.getRowSize(); j++) {
										if(!bResul)
											break;
										int nbCol = -1;
										if(i<rangement.getNbEmplacements())
											nbCol = rangement.getNbColonnes(i, j);
										int newNbCol = part.getRow(j).getCol();
										if(nbCol > newNbCol) {
											for(int k=newNbCol; k<nbCol; k++) {
												if(!bResul)
													break;
												if(rangement.getBouteille(i, j, k) != null) {
													bResul = false;
													String sText = Program.getError("Error203");
													sText = sText.replaceFirst("A1", Integer.toString(j+1));
													sText = sText.replaceFirst("A2", Integer.toString(i+1));
													Debug("ERROR: Unable to reduce the size of the number of column");
													new Erreur(sText);
													// Impossible de réduire le nombre de colonne de la ligne de la partie, bouteilles présentes
												}
											}
										}
									}
								}
							}
						
							String name = rangement.getNom();
							if (name.compareTo(nom) != 0) {
								String erreur_txt1 = Program.getError("Error136"); //"1 bouteille est pr�sente dans ce rangement.");
								String erreur_txt2 = Program.getError("Error137"); //"Voulez vous changer l'emplacement de cette bouteille?");
								if (nbBottles == 1) {
									Debug("MESSAGE: 1 bottle in this place, modify?");
								}
								else {
									Debug("MESSAGE: "+nbBottles+" bottles in this place, Modify?");
									erreur_txt1 = new String(nbBottles + " " + Program.getError("Error094")); //bouteilles sont pr�sentes dans ce rangement.");
									erreur_txt2 = Program.getError("Error095"); //"Voulez vous changer l'emplacement de ces bouteilles?");
								}
								if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
									//Modify Name of place
									rangement.setNom(nom);
									rangement.setPlace(listPart);
									int nb_all = Program.getStorage().getAllNblign();
									for (int i = 0; i < nb_all; i++) {
										Bouteille b = Program.getStorage().getAllAt(i);

										String empla1 = b.getEmplacement();
										if (empla1.equals(name)) {
											b.setEmplacement(nom);
										}
									}
									//rangement.putTabStock();
									nom_obj.setText("");
									comboPlace.removeAllItems();
									comboPlace.addItem("");

									for (int z = 0; z < Program.GetCaveLength(); z++) {
										comboPlace.addItem(Program.getCave(z).getNom());
									}
									Program.updateAllPanels();
								}
								else {
									rangement.setNom(nom);
									rangement.setPlace(listPart);
									//rangement.putTabStock();
								}
							}
							else {
								rangement.setPlace(listPart);
								//rangement.putTabStock();
							}
							RangementUtils.putTabStock1();
							if(bResul)
								label_cree.setText(Program.getError("Error123"));
						}
					}
				}
			}
			new java.util.Timer().schedule( 
			        new java.util.TimerTask() {
			            @Override
			            public void run() {
			            	SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									label_cree.setText("");
								}
			            	});
			            }
			        }, 
			        5000 
			);
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * button1_actionPerformed: Boutton Créer
	 *
	 * @param e ActionEvent
	 */
	void create_actionPerformed(ActionEvent e) {
		try {
			Debug("create_actionPerforming...");
			boolean bResul = true;
			String nom; //Nom de l'emplacement
			nom = nom_obj.getText().trim();

			//Contrôle si le nom est déjà utilisé
			bResul = MyCellarControl.ctrl_existingName(nom);
			// Contrôles sur le nom (format, longueur...)
			if ( bResul )
				bResul = MyCellarControl.ctrl_Name(nom);

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
			}
			else {
				Debug("Creating complex place...");
				for(Part p: listPart) {
					if(p.getRows().size() == 0) {
						erreur_txt1 = new String(Program.getError("Error009") + " " + p.getNum() + "."); //"Erreur nombre de lignes incorrect sur la partie
						new Erreur(erreur_txt1, "");
						bResul = false;
					}
					for(Row r: p.getRows()) {
						if(r.getCol() == 0) {
							erreur_txt1 = new String(Program.getError("Error004") + " " + p.getNum() + "."); //"Erreur nombre de colonnes incorrect sur la partie
							new Erreur(erreur_txt1, "");
							bResul = false;
						}
					}
				}
				//Type rangement
				if (m_jrb_dif_column_number.isSelected()) {
					Debug("Creating with different column number...");

					// Cr�ation du rangement
					if (bResul) {
						Debug("Creating place...");
						Rangement r;
						r = new Rangement(nom, listPart);
						Program.addCave(r);
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
					if(bResul)
					{
						Rangement r;
						r = new Rangement(nom, listPart);
						Program.addCave(r);
						Debug("Creating "+nom+" completed.");
						label_cree.setText(Program.getLabel("Infos090")); //"Rangement créé.");
						nom_obj.setText("");
						Program.updateAllPanels();
					}
				}
			}
			int key = Program.getCaveConfigInt("DONT_SHOW_CREATE_MESS", 0);
			if (key == 0 && bResul) {
				new Erreur(Program.getError("Error164"), "", true, "", true, "DONT_SHOW_CREATE_MESS");
			}
			if (bResul)
				Start.enableAll(true);
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
	void checkbox1_itemStateChanged(ItemEvent e) {
		label_cree.setText("");
		try {
			if (m_caisse_chk.isSelected()) {
				checkLimite.setVisible(true);
				if (checkLimite.isSelected()) {
					label_limite.setVisible(true);
					nb_limite.setVisible(true);
				}
				else {
					nb_limite.setVisible(false);
					label_limite.setVisible(false);
				}
				nb_start_caisse.setVisible(true);
				panelType.setVisible(false);
				panelStartCaisse.setVisible(true);
				panelLimite.setVisible(true);
				panelTable.setVisible(false);
				preview.setEnabled(false);
			}
			else {
				panelStartCaisse.setVisible(false);
				panelLimite.setVisible(false);
				panelTable.setVisible(true);
				nb_start_caisse.setVisible(false);
				panelType.setVisible(true);
				checkLimite.setVisible(true);
				preview.setEnabled(true);
				nb_parties.setValue(new Integer(1));
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
	void checkbox2_itemStateChanged(ItemEvent e) {
		label_cree.setText("");

		if (checkLimite.isSelected()) {
			nb_limite.setVisible(true);
			label_limite.setVisible(true);
		}
		else {
			nb_limite.setVisible(false);
			label_limite.setVisible(false);
		}
	}

	/**
	 * preview_actionPerformed: Permet de prévisualiser un rangement avant de le
	 * créer.
	 *
	 * @param e ActionEvent
	 */
	void preview_actionPerformed(ActionEvent e) {
		try {
			boolean bResul = true;
			String nom; //Nom de l'emplacement

			if (!m_caisse_chk.isSelected()) {
				// Contrôle du nom
				nom = nom_obj.getText().trim();
				bResul = MyCellarControl.ctrl_Name( nom );

				for(Part p: listPart)
				{
					if(p.getRows().size() == 0)
					{
						erreur_txt1 = new String(Program.getError("Error009") + " " + p.getNum() + "."); //"Erreur nombre de lignes incorrect sur la partie
						new Erreur(erreur_txt1, Program.getError("Error109"));
						bResul = false;
						return;
					}
					for(Row r: p.getRows())
					{
						if(r.getCol() == 0)
						{
							erreur_txt1 = new String(Program.getError("Error004") + " " + p.getNum() + "."); //"Erreur nombre de colonnes incorrect sur la partie
							new Erreur(erreur_txt1, Program.getError("Error109"));
							bResul = false;
							return;
						}
					}
				}

				// Création du rangement
				if (bResul) {
					Rangement r = new Rangement(nom, listPart);
					LinkedList<Rangement> rangements = new LinkedList<Rangement>();
					rangements.add(r);
					MyXmlDom.writeRangements("", rangements, true);
					Program.open( new File(Program.getPreviewXMLFileName()) );
				}
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * nom_obj_textValueChanged: Remise à blanc du label de fin lors de la
	 * modification du nom.
	 *
	 * @param e ActionEvent
	 */
	void nom_obj_textValueChanged(ActionEvent e) {
		label_cree.setText("");
	}

	/**
	 * keylistener_actionPerformed: Raccourcis clavier
	 *
	 * @param e KeyEvent
	 */
	void keylistener_actionPerformed(KeyEvent e) {
		
		if ( (e.getKeyCode() == CREER && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
			create_actionPerformed(null);
		}
		if (e.getKeyCode() == PREVIEW && e.isControlDown() && preview.isEnabled()) {
			preview_actionPerformed(null);
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

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("Creer_Rangement: " + sText);
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		if (nom_obj.getText().trim().length() > 0) {
			String label = Program.getError("Error146");
			if(modify)
				label = Program.getError("Error147");
			if( JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, label + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE))        
				return false;
		}
		Debug("Quitting...");
		label_cree.setText("");
		comboPlace.setSelectedIndex(0);
		return true;
	}
	
	@Override
	public void tabClosed() {
		Start.updateMainPanel();
	}

	public void updateView() {
		comboPlace.removeAllItems();
		comboPlace.addItem("");
		for( Rangement r: Program.getCave())
			comboPlace.addItem(r.getNom());
	}

}
