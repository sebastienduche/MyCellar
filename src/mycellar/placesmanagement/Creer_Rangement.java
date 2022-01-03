package mycellar.placesmanagement;

import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarControl;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarSettings;
import mycellar.core.UpdateViewType;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
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
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static mycellar.Program.toCleanString;
import static mycellar.ProgramConstants.FONT_DIALOG_SMALL;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.LabelType.INFO;
import static mycellar.core.LabelType.INFO_OTHER;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 15.5
 * @since 03/01/22
 */
public final class Creer_Rangement extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {

  static final long serialVersionUID = 280706;
  private static final char CREER = Program.getLabel("CREER").charAt(0);
  private static final char PREVIEW = Program.getLabel("PREVIEW").charAt(0);
  private final MyCellarComboBox<Rangement> comboPlace = new MyCellarComboBox<>();
  private final JTextField nom_obj = new JTextField();
  private final MyCellarRadioButton m_jrb_same_column_number = new MyCellarRadioButton(INFO, "012", true); //"Toutes les lignes ont le meme nombre de colonnes"
  private final MyCellarRadioButton m_jrb_dif_column_number = new MyCellarRadioButton(INFO, "013", false); //"Toutes les lignes n'ont pas le meme nombre de colonnes"
  private final MyCellarCheckBox checkLimite = new MyCellarCheckBox(INFO, "238"); //limite
  private final MyCellarLabel label_limite = new MyCellarLabel(INFO_OTHER, "Main.Item", LabelProperty.SINGLE);
  private final MyCellarSpinner nb_limite = new MyCellarSpinner(1, 999);
  private final MyCellarSpinner nb_parties = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner nb_start_caisse = new MyCellarSpinner(0, 99);
  private final MyCellarCheckBox m_caisse_chk = new MyCellarCheckBox(INFO, "024"); //Caisse
  private final MyCellarLabel label_cree = new MyCellarLabel();
  private final MyCellarButton preview = new MyCellarButton(INFO, "155");
  private final JPanel panelType;
  private final JPanel panelStartCaisse;
  private final JPanel panelLimite;
  private final JPanel panelTable;
  private final CreerRangementTableModel model;
  private final boolean modify;
  private boolean islimited = false;
  private int limite = 0;
  private LinkedList<Part> listPart = new LinkedList<>();
  private int start_caisse = 0;
  private UpdateViewType updateViewType;

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
    label_cree.setFont(FONT_DIALOG_SMALL);
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
      MyCellarLabel labelModify = new MyCellarLabel(INFO, "226"); //"Selec the place to modify"
      JPanel panelModify = new JPanel();
      panelModify.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "", 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
      panelModify.setLayout(new MigLayout("", "[]", "[]"));
      panelModify.add(labelModify, "split 2");
      panelModify.add(comboPlace);
      add(panelModify, "span 2, wrap");
    }
    MyCellarLabel labelName = new MyCellarLabel(INFO, "020"); //"Nom du rangement");
    add(labelName, "span 2, split 3");
    add(nom_obj, "growx");
    add(m_caisse_chk, "wrap");

    panelType = new JPanel();
    panelType.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), Program.getLabel("Infos021"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
    panelType.setLayout(new GridLayout(0, 2));
    panelType.add(m_jrb_same_column_number);
    panelType.add(m_jrb_dif_column_number);

    panelStartCaisse = new JPanel();
    panelStartCaisse.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), Program.getLabel("Infos272"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
    panelStartCaisse.setLayout(new MigLayout("", "[]", "[]"));
    panelStartCaisse.add(nb_start_caisse, "wmin 50");

    panelLimite = new JPanel();
    panelLimite.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), Program.getLabel("Infos274"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
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
    panelPartie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), Program.getLabel("Infos023"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
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

  private static void Debug(String sText) {
    Program.Debug("Creer_Rangement: " + sText);
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
    label_cree.setText("");
    if (Program.EMPTY_PLACE.equals(rangement)) {
      nom_obj.setText("");
      model.setValues(new LinkedList<>());
      enableAll(false);
      return;
    }

    enableAll(true);
    nom_obj.setText(rangement.getName());
    m_caisse_chk.setSelected(rangement.isSimplePlace());
    m_caisse_chk.setEnabled(false);
    if (rangement.isSimplePlace()) {
      checkLimite.setSelected(rangement.isSimplePlaceLimited());
      if (rangement.isSimplePlaceLimited()) {
        nb_limite.setValue(rangement.getNbColumnsStock());
      }
      nb_parties.setValue(rangement.getNbParts());
      nb_start_caisse.setValue(rangement.getStartSimplePlace());
    } else {
      m_jrb_same_column_number.setSelected(rangement.isSameColumnNumber());
      m_jrb_dif_column_number.setSelected(!rangement.isSameColumnNumber());
      listPart = rangement.getPlace();
      model.setValues(listPart);
      nb_parties.setValue(rangement.getNbParts());
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
      modifySimplePlace(rangement, nom);
    } else {
      modifyComplexPlace(rangement, nom);
    }
  }

  private void modifySimplePlace(Rangement rangement, String nom) {
    Debug("Modifying Simple place...");
    //Modification d'un rangement de type "Caisse"
    start_caisse = nb_start_caisse.getIntValue();
    islimited = checkLimite.isSelected();
    limite = nb_limite.getIntValue();
    int nbPart = nb_parties.getIntValue();

    if (rangement.getNbParts() > nbPart) {
      final Map<Integer, Integer> numberOfBottlesPerPlace = rangement.getNumberOfBottlesPerPlace();

      for (int i = nbPart; i < rangement.getNbParts(); i++) {
        if (numberOfBottlesPerPlace.get(i) > 0) {
          Debug("ERROR: Unable to delete simple place part with bottles!");
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("CreerRangement.CantDeletePartCaisse"), (i + rangement.getStartSimplePlace())));
          return;
        }
      }
    }

    int nb_bottle = rangement.getTotalCountCellUsed();
    if (nb_bottle > 0) {
      String name = rangement.getName();
      if (!name.equals(nom)) {
        String erreur_txt1, erreur_txt2;
        if (nb_bottle == 1) {
          Debug("MESSAGE: 1 bottle in this place, modify?");
          erreur_txt1 = Program.getError("Error136", LabelProperty.SINGLE); //"1 bouteille est presente dans ce rangement.");
          erreur_txt2 = Program.getError("Error137", LabelProperty.SINGLE); // Change the place of this object;
        } else {
          Debug("MESSAGE: " + nb_bottle + " bottles in this place, Modify?");
          erreur_txt1 = MessageFormat.format(Program.getError("Error094", LabelProperty.PLURAL), nb_bottle); //bouteilles sont presentes dans ce rangement.");
          erreur_txt2 = Program.getError("Error095", LabelProperty.PLURAL); //"Change the place of these objects");
        }
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + SPACE + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          //Modify Name of place
          Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setEmplacement(nom));
        }
      } else if (rangement.getStartSimplePlace() != start_caisse) {
        // Le numero de la premiere partie a change, renumeroter
        String erreur_txt1 = MessageFormat.format(Program.getError("CreerRangement.UpdatedBottlePart"), start_caisse, rangement.getStartSimplePlace());
        String erreur_txt2 = Program.getError("CreerRangement.AskUpdateBottlePart", LabelProperty.PLURAL);

        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + SPACE + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          //Modify start part number
          final int difference = start_caisse - rangement.getStartSimplePlace();
          Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setNumLieu(b.getNumLieu() + difference));
        }
      }
    }
    nom_obj.setText("");
    updatePlace(nom, nbPart, rangement);
    updateView();
    ProgramPanels.updateAllPanelsForUpdatingPlaces();
    Debug("Modifications completed");
    label_cree.setText(Program.getError("Error123"), true); //"Rangement modifie.
  }

  private void modifyComplexPlace(Rangement rangement, String nom) {
    // Rangement complexe
    Debug("Modifying complex place...");
    int nbBottles = rangement.getTotalCountCellUsed();
    for (Part p : listPart) {
      if (p.getRows().isEmpty()) {
        Debug("ERROR: Wrong number of lines on part: " + p.getNum());
        Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum())); //"Erreur nombre de lignes incorrectes sur la partie
        return;
      }
      for (Row r : p.getRows()) {
        if (r.getCol() == 0) {
          Debug("ERROR: Wrong number of columns on part:  " + p.getNum());
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error004"), p.getNum()));//"Erreur nombre de colonnes incorrectes sur la partie
          return;
        }
      }
    }

    if (nbBottles == 0) {
      rangement.setName(nom);
      rangement.updatePlace(listPart);
      putTabStock();
      nom_obj.setText("");
      updateView();
      ProgramPanels.updateAllPanelsForUpdatingPlaces();
      label_cree.setText(Program.getError("Error123"), true);
    } else {
      if (rangement.getNbParts() > listPart.size()) {
        int nb = 0;
        for (int i = listPart.size(); i < rangement.getNbParts(); i++) {
          nb += rangement.getTotalCellUsed(i);
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
        if (i < rangement.getNbParts()) {
          nbRow = rangement.getLineCountAt(i);
        }
        int newNbRow = part.getRowSize();
        if (nbRow > newNbRow) {
          int nb = 0;
          for (int j = newNbRow; j < nbRow; j++) {
            nb += rangement.getNbCaseUseInLine(i, j);
          }
          if (nb > 0) {
            bResul = false;
            Debug("ERROR: Unable to reduce the number of row");
            Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error202"), Integer.toString(i + 1)));
            // Impossible de reduire le nombre de lignes de la partie, bouteilles presentes
          }
        }
        if (bResul) {
          for (int j = 0; j < part.getRowSize(); j++) {
            if (!bResul) {
              Debug("ERROR: bResul false, skipping row");
              break;
            }
            int nbCol = -1;
            if (i < rangement.getNbParts() && j < rangement.getLineCountAt(i)) {
              nbCol = rangement.getColumnCountAt(i, j);
            }
            int newNbCol = part.getRow(j).getCol();
            if (nbCol > newNbCol) {
              for (int k = newNbCol; k < nbCol; k++) {
                if (!bResul) {
                  Debug("ERROR: bResul false, skipping column");
                  break;
                }
                if (rangement.getObject(i, j, k).isPresent()) {
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

      if (!bResul) {
        return;
      }

      Debug("Updating complex place: " + rangement.getName());
      String name = rangement.getName();
      if (!name.equalsIgnoreCase(nom)) {
        String erreur_txt1 = Program.getError("Error136", LabelProperty.SINGLE); //"1 bouteille est presente dans ce rangement.");
        String erreur_txt2 = Program.getError("Error137", LabelProperty.SINGLE); //"Voulez-vous changer l'emplacement de cette bouteille?");
        if (nbBottles == 1) {
          Debug("MESSAGE: 1 bottle in this place, modify?");
        } else {
          Debug("MESSAGE: " + nbBottles + " bottles in this place, Modify?");
          erreur_txt1 = MessageFormat.format(Program.getError("Error094", LabelProperty.PLURAL), nbBottles); //bouteilles sont presentes dans ce rangement.");
          erreur_txt2 = Program.getError("Error095", LabelProperty.PLURAL); //"Voulez-vous changer l'emplacement de ces bouteilles?");
        }
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + SPACE + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          //Modify Name of place
          rangement.setName(nom);
          rangement.updatePlace(listPart);
          Program.getStorage().getAllList().stream().filter(b -> b.getEmplacement().equals(name)).forEach(b -> b.setEmplacement(nom));
          nom_obj.setText("");
        } else {
          rangement.setName(nom);
          rangement.updatePlace(listPart);
        }
      } else {
        rangement.updatePlace(listPart);
      }
      putTabStock();
      updateView();
      ProgramPanels.updateAllPanelsForUpdatingPlaces();
      comboPlace.setSelectedIndex(0);
      label_cree.setText(Program.getError("Error123"), true);
    }
  }

  private void updatePlace(String nom, int nbPart, Rangement rangement) {
    rangement.setName(nom);
    rangement.setSimplePlaceLimited(islimited);
    rangement.setStartSimplePlace(start_caisse);
    rangement.setNbObjectInSimplePlace(limite);
    rangement.updateSimplePlace(nbPart);
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
        final Rangement caisse = new Rangement.SimplePlaceBuilder(nom)
            .nbParts(nbPart)
            .startSimplePlace(start_caisse)
            .limited(islimited)
            .limit(limite).build();
        Program.addCave(caisse);
        Debug("Creation of '" + nom + "' completed.");
        nom_obj.setText("");
        label_cree.setText(Program.getLabel("Infos090"), true); //"Rangement cree.");
        ProgramPanels.updateAllPanelsForUpdatingPlaces();
      }
    } else {
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
          label_cree.setText(Program.getLabel("Infos090"), true); //"Rangement cree.
          nom_obj.setText("");
          ProgramPanels.updateAllPanelsForUpdatingPlaces();
        }
        //Fin test check
      } else { // Si check1
        Debug("Creating place with same column number");
        // Recuperation du nombre de ligne par partie
        if (bResul) {
          Program.addCave(new Rangement(nom, listPart));
          Debug("Creating '" + nom + "' completed.");
          label_cree.setText(Program.getLabel("Infos090"), true); //"Rangement cree.
          nom_obj.setText("");
          ProgramPanels.updateAllPanelsForUpdatingPlaces();
        }
      }
    }
    if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_CREATE_MESS, false) && bResul) {
      Erreur.showInformationMessageWithKey(Program.getError("Error164"), "", MyCellarSettings.DONT_SHOW_CREATE_MESS);
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
    } else {
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

  private void preview_actionPerformed(ActionEvent e) {
    if (!m_caisse_chk.isSelected()) {
      // Controle du nom
      String nom = toCleanString(nom_obj.getText());
      if (!MyCellarControl.ctrlName(nom)) {
        return;
      }

      for (Part p : listPart) {
        if (p.getRows().isEmpty()) {
          // Wrong lines number in part
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error009"), p.getNum()), Program.getError("Error109"));
          return;
        }
        for (Row r : p.getRows()) {
          if (r.getCol() == 0) {
            //  Wrong columns number in part
            Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error004"), p.getNum()), Program.getError("Error109"));
            return;
          }
        }
      }

      // Creation du rangement
      XmlUtils.writeRangements("", List.of(new Rangement(nom, listPart)), true);
      Program.open(Program.getPreviewXMLFileName(), false);
    }
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if ((e.getKeyCode() == CREER && e.isControlDown()) || e.getKeyCode() == KeyEvent.VK_ENTER) {
      create_actionPerformed();
    }
    if (e.getKeyCode() == PREVIEW && e.isControlDown() && preview.isEnabled()) {
      preview_actionPerformed(null);
    }
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    if (!toCleanString(nom_obj.getText()).isEmpty()) {
      String label = Program.getError("Error146");
      if (modify) {
        label = Program.getError("Error147");
      }
      if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, label + SPACE + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
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
  public void setUpdateView(UpdateViewType updateViewType) {
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    if (updateViewType == UpdateViewType.PLACE) {
      comboPlace.removeAllItems();
      comboPlace.addItem(Program.EMPTY_PLACE);
      Program.getCave().forEach(comboPlace::addItem);
      updateViewType = null;
    }
  }

  @Override
  public void cut() {
    String text = nom_obj.getSelectedText();
    if (text != null) {
      String fullText = nom_obj.getText();
      nom_obj.setText(fullText.substring(0, nom_obj.getSelectionStart()) + fullText.substring(nom_obj.getSelectionEnd()));
      Program.CLIPBOARD.copy(text);
    }
  }

  @Override
  public void copy() {
    Program.CLIPBOARD.copy(nom_obj.getSelectedText());
  }

  @Override
  public void paste() {
    String fullText = nom_obj.getText();
    nom_obj.setText(fullText.substring(0, nom_obj.getSelectionStart()) + Program.CLIPBOARD.paste() + fullText.substring(nom_obj.getSelectionEnd()));
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
