package mycellar.placesmanagement;

import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarControl;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.UpdateViewType;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.JModifyTextField;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import mycellar.general.XmlUtils;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.Part;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.Row;
import mycellar.placesmanagement.places.SimplePlace;
import mycellar.placesmanagement.places.SimplePlaceBuilder;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
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

import static javax.swing.border.EtchedBorder.RAISED;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getErrorWithProperty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMQUIT;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_STORAGECREATIONINCOMPLETED;
import static mycellar.general.ResourceErrorKey.ERROR_STORAGEMODIFICATIONINCOMPLETED;
import static mycellar.general.ResourceKey.CREATESTORAGE_SIMPLESTORAGE;
import static mycellar.general.ResourceKey.MAIN_ASKCONFIRMATION;
import static mycellar.placesmanagement.places.ComplexPlace.copyParts;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 18.1
 * @since 08/03/25
 */
public final class Creer_Rangement extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {
  // TODO Can we manage the modified status correctly?
  private static final char CREER = getLabel("CREER").charAt(0);
  private static final char PREVIEW = getLabel("PREVIEW").charAt(0);
  private final MyCellarComboBox<AbstractPlace> comboPlace = new MyCellarComboBox<>();
  private final JModifyTextField nom_obj = new JModifyTextField();
  private final MyCellarRadioButton allLinesSameRadio = new MyCellarRadioButton("CreateStorage.AllLinesSame", true);
  private final MyCellarRadioButton notAllLinesSameRadio = new MyCellarRadioButton("CreateStorage.AllLinesNotSame", false);
  private final MyCellarCheckBox isSimplePlaceLimitedCheckbox = new MyCellarCheckBox("CreateStorage.ActivateLimit");
  private final MyCellarLabel label_limite = new MyCellarLabel("Main.Item", LabelProperty.SINGLE);
  private final MyCellarSpinner simplePlaceLimitSpinner = new MyCellarSpinner(1, 999);
  private final MyCellarSpinner partCountSpinner = new MyCellarSpinner(1, 99);
  private final MyCellarSpinner partIncrementSimplePlaceSpinner = new MyCellarSpinner(0, 99);
  private final MyCellarCheckBox isSimplePlaceCheckbox = new MyCellarCheckBox(CREATESTORAGE_SIMPLESTORAGE);
  private final MyCellarSimpleLabel labelCreated = new MyCellarSimpleLabel();
  private final MyCellarButton preview = new MyCellarButton("CreateStorage.Preview");
  private final JPanel panelType;
  private final JPanel panelStartCaisse;
  private final JPanel panelLimite;
  private final JPanel panelTable;
  private final CreerRangementTableModel model;
  private final boolean modify;
  private boolean islimited = false;
  private int simplePlaceLimit = 0;
  private LinkedList<Part> listPart = new LinkedList<>();
  private int partNumberIncrementSimplePlace = 0;
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
      createButton = new MyCellarButton("Main.Modify", new ModifyAction());
    } else {
      createButton = new MyCellarButton("Main.Create", new CreateAction());
    }

    createButton.setMnemonic(CREER);
    preview.setMnemonic(PREVIEW);
    initComboPlaces();
    comboPlace.addItemListener(this::comboPlace_itemStateChanged);
    ButtonGroup cbg = new ButtonGroup();
    cbg.add(allLinesSameRadio);
    cbg.add(notAllLinesSameRadio);
    allLinesSameRadio.addItemListener((e) -> model.setSameColumnNumber(allLinesSameRadio.isSelected()));
    labelCreated.setForeground(Color.red);
    labelCreated.setFont(FONT_DIALOG_BOLD);
    labelCreated.setText("");
    labelCreated.setHorizontalAlignment(SwingConstants.CENTER);

    preview.addActionListener(this::preview_actionPerformed);
    isSimplePlaceCheckbox.addItemListener(this::simplePlace_itemStateChanged);
    isSimplePlaceLimitedCheckbox.addItemListener(this::checkbox2_itemStateChanged);

    nom_obj.addActionListener((e) -> labelCreated.setText(""));
    nom_obj.addMouseListener(new PopupListener());

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });

    partCountSpinner.addChangeListener((e) -> updatePartList());

    simplePlaceLimitSpinner.addChangeListener((e) -> {
      final int count = Integer.parseInt(simplePlaceLimitSpinner.getValue().toString());
      label_limite.setText(getLabelWithProperty("Main.Item", new LabelProperty(count > 1)));
    });

    // Init part count
    partCountSpinner.setValue(1);
    // Init increment value for simple place
    partIncrementSimplePlaceSpinner.setValue(0);
    partIncrementSimplePlaceSpinner.setVisible(false);
    // Init count of allowed items
    simplePlaceLimitSpinner.setValue(1);

    setLayout(new MigLayout("", "[grow][grow]", "[][]"));

    if (modify) {
      MyCellarLabel labelModify = new MyCellarLabel("CreateStorage.SelectPlaceToModify");
      JPanel panelModify = new JPanel();
      panelModify.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(RAISED), "", 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
      panelModify.setLayout(new MigLayout("", "[]", "[]"));
      panelModify.add(labelModify, "split 2");
      panelModify.add(comboPlace);
      add(panelModify, "span 2, wrap");
    }
    add(new MyCellarLabel("Import.StorageName"), "span 2, split 3");
    add(nom_obj, "growx");
    add(isSimplePlaceCheckbox, "wrap");

    panelType = new JPanel();
    panelType.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(RAISED), getLabel("CreateStorage.TypeLines"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
    panelType.setLayout(new GridLayout(0, 2));
    panelType.add(allLinesSameRadio);
    panelType.add(notAllLinesSameRadio);

    panelStartCaisse = new JPanel();
    panelStartCaisse.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(RAISED), getLabel("CreateStorage.FirstShelveNumber"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
    panelStartCaisse.setLayout(new MigLayout("", "[]", "[]"));
    panelStartCaisse.add(partIncrementSimplePlaceSpinner, "wmin 50");

    panelLimite = new JPanel();
    panelLimite.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(RAISED), getLabel("CreateStorage.LimitPerShelve"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
    panelLimite.setLayout(new MigLayout("", "[][]", "[]"));
    panelLimite.add(isSimplePlaceLimitedCheckbox, "gapright 10");
    panelLimite.add(simplePlaceLimitSpinner, "split 2, wmin 50, hidemode 3");
    panelLimite.add(label_limite, "hidemode 3");

    panelTable = new JPanel();
    panelTable.setLayout(new MigLayout("", "[grow]", "[grow]"));
    panelTable.add(new JScrollPane(new JTable(model)), "grow");
    model.setValues(listPart);

    JPanel panelPartie = new JPanel();
    panelPartie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(RAISED), getLabel("CreateStorage.ShelveNumber"), 0, 0, FONT_PANEL), BorderFactory.createEmptyBorder()));
    panelPartie.setLayout(new MigLayout("", "[]", "[]"));
    panelPartie.add(partCountSpinner, "wmin 50");

    JPanel panelPartsConfig = new JPanel();
    panelPartsConfig.setLayout(new MigLayout("", "[][]", "[grow]"));
    panelPartsConfig.add(panelType, "growy, hidemode 3, gapright 30");
    panelPartsConfig.add(panelPartie, "growx, wmin 150, gapright 30");
    panelPartsConfig.add(panelStartCaisse, "growx, wmin 250, hidemode 3, gapright 30");
    panelPartsConfig.add(panelLimite, "growx, wmin 250, hidemode 3, gapright 30");

    add(panelPartsConfig, "span 2, wrap, hidemode 3");
    add(panelTable, "span 2, grow, wrap, hidemode 3");
    add(labelCreated, "center, span 2, wrap");
    add(createButton, "span 2, split 2, center");
    add(preview);

    notAllLinesSameRadio.addItemListener((e) -> model.setSameColumnNumber(!notAllLinesSameRadio.isSelected()));
    model.setSameColumnNumber(true);

    if (modify) {
      enableAll(false);
    }

    isSimplePlaceCheckbox.setSelected(true);
    setVisible(true);
  }

  private static void Debug(String sText) {
    Program.Debug("Creer_Rangement: " + sText);
  }

  private void initComboPlaces() {
    comboPlace.removeAllItems();
    comboPlace.addItem(Program.EMPTY_PLACE);
    Program.getAbstractPlaces().forEach(comboPlace::addItem);
  }

  private void updatePartList() {
    if (isSimplePlaceCheckbox.isSelected()) {
      return;
    }
    int newValue = Integer.parseInt(partCountSpinner.getValue().toString());
    if (newValue > listPart.size()) {
      while (listPart.size() < newValue) {
        listPart.add(new Part(listPart.size(), new LinkedList<>(List.of(new Row(0)))));
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
    final AbstractPlace abstractPlace = (AbstractPlace) e.getItem();
    labelCreated.setText("");
    if (Program.EMPTY_PLACE.equals(abstractPlace)) {
      nom_obj.setText("");
      model.setValues(new LinkedList<>());
      enableAll(false);
      return;
    }

    enableAll(true);
    nom_obj.setText(abstractPlace.getName());
    isSimplePlaceCheckbox.setSelected(abstractPlace.isSimplePlace());
    isSimplePlaceCheckbox.setEnabled(false);
    if (abstractPlace.isSimplePlace()) {
      SimplePlace simplePlace = (SimplePlace) abstractPlace;
      isSimplePlaceLimitedCheckbox.setSelected(simplePlace.isLimited());
      if (simplePlace.isLimited()) {
        simplePlaceLimitSpinner.setValue(simplePlace.getMaxItemCount());
      }
      partIncrementSimplePlaceSpinner.setValue(simplePlace.getPartNumberIncrement());
    } else {
      ComplexPlace complexPlace = (ComplexPlace) abstractPlace;
      allLinesSameRadio.setSelected(complexPlace.isSameColumnNumber());
      notAllLinesSameRadio.setSelected(!complexPlace.isSameColumnNumber());
      listPart = copyParts(complexPlace.getParts());
      model.setValues(listPart);
    }
    partCountSpinner.setValue(abstractPlace.getPartCount());
  }

  private void enableAll(boolean enable) {
    nom_obj.setEnabled(enable);
    isSimplePlaceCheckbox.setEnabled(enable);
    simplePlaceLimitSpinner.setEnabled(enable);
    isSimplePlaceLimitedCheckbox.setEnabled(enable);
    partCountSpinner.setEnabled(enable);
    partIncrementSimplePlaceSpinner.setEnabled(enable);
  }

  private void modifyPlace() {
    Debug("modifyPlace...");

    final AbstractPlace abstractPlace = (AbstractPlace) comboPlace.getSelectedItem();
    if (comboPlace.getSelectedIndex() == 0 || abstractPlace == null) {
      Debug("ERROR: Please select a place");
      Erreur.showSimpleErreur(getError(ERROR_SELECTSTORAGE));
      return;
    }

    final String nom = toCleanString(nom_obj.getText());
    if (!MyCellarControl.ctrlName(nom)) {
      return;
    }

    Debug("Advanced modifying...");
    if (isSimplePlaceCheckbox.isSelected()) {
      modifySimplePlace((SimplePlace) abstractPlace, nom);
    } else {
      modifyComplexPlace((ComplexPlace) abstractPlace, nom);
    }
  }

  private void modifySimplePlace(SimplePlace simplePlace, String nom) {
    Debug("Modifying Simple place...");
    partNumberIncrementSimplePlace = partIncrementSimplePlaceSpinner.getIntValue();
    islimited = isSimplePlaceLimitedCheckbox.isSelected();
    simplePlaceLimit = simplePlaceLimitSpinner.getIntValue();
    int nbPart = partCountSpinner.getIntValue();

    if (simplePlace.getPartCount() > nbPart) {
      final Map<Integer, Integer> numberOfObjectsPerPlace = simplePlace.getNumberOfObjectsPerPlace();

      for (int i = nbPart; i < simplePlace.getPartCount(); i++) {
        if (numberOfObjectsPerPlace.get(i) > 0) {
          Debug("ERROR: Unable to delete simple place part with objects!");
          Erreur.showSimpleErreur(MessageFormat.format(getError("CreerRangement.CantDeletePartCaisse"), (i + simplePlace.getPartNumberIncrement())));
          return;
        }
      }
    }

    int nb_bottle = simplePlace.getTotalCountCellUsed();
    if (nb_bottle > 0) {
      String name = simplePlace.getName();
      if (!name.equals(nom)) {
        String erreur_txt1, erreur_txt2;
        if (nb_bottle == 1) {
          Debug("MESSAGE: 1 object in this place, Modify?");
          erreur_txt1 = getErrorWithProperty("Error.1ItemInStorage", LabelProperty.SINGLE);
          erreur_txt2 = getErrorWithProperty("Error.confirmChangeStorage1Item", LabelProperty.SINGLE);
        } else {
          Debug("MESSAGE: " + nb_bottle + " objects in this place, Modify?");
          erreur_txt1 = MessageFormat.format(getErrorWithProperty("Error.NItemsInStorage", LabelProperty.PLURAL), nb_bottle);
          erreur_txt2 = getErrorWithProperty("Error.questionChangeStorageItems", LabelProperty.PLURAL);
        }
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), erreur_txt1 + SPACE + erreur_txt2, getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          // Modify Name of place
          Program.getStorage().getAllList()
              .stream()
              .filter(b -> b.getEmplacement().equals(name))
              .forEach(b -> b.setEmplacement(nom));
        }
      } else if (simplePlace.getPartNumberIncrement() != partNumberIncrementSimplePlace) {
        // Le numero de la premiere partie a change, renumeroter
        String erreur_txt1 = MessageFormat.format(getError("CreerRangement.UpdatedBottlePart"), partNumberIncrementSimplePlace, simplePlace.getPartNumberIncrement());
        String erreur_txt2 = getErrorWithProperty("CreerRangement.AskUpdateBottlePart", LabelProperty.PLURAL);

        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), erreur_txt1 + SPACE + erreur_txt2, getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          //Modify start part number
          final int difference = partNumberIncrementSimplePlace - simplePlace.getPartNumberIncrement();
          Program.getStorage().getAllList()
              .stream()
              .filter(b -> b.getEmplacement().equals(name))
              .forEach(b -> b.setNumLieu(b.getNumLieu() + difference));
        }
      }
    }
    updateSimplePlace(nom, nbPart, simplePlace);
    ProgramPanels.updateAllPanelsForUpdatingPlaces();
    Debug("Modifications completed");
    labelCreated.setText(getLabel("CreateStorage.StorageModified"), true);
    nom_obj.setModified(false);
  }

  private void modifyComplexPlace(ComplexPlace complexPlace, String nom) {
    Debug("Modifying complex place...");
    int nbBottles = complexPlace.getTotalCountCellUsed();
    for (Part p : listPart) {
      if (p.rows().isEmpty()) {
        Debug("ERROR: Wrong number of lines on part: " + p.getNumberAsDisplay());
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error.incorrectNumberLinesForShelve"), p.getNumberAsDisplay()));
        return;
      }
      for (Row r : p.rows()) {
        if (r.getColumnCount() == 0) {
          Debug("ERROR: Wrong number of columns on part: " + p.getNumberAsDisplay());
          Erreur.showSimpleErreur(MessageFormat.format(getError("Error.incorrectNumberColumnsForShelve"), p.getNumberAsDisplay()));
          return;
        }
      }
    }

    if (nbBottles == 0) {
      complexPlace.setName(nom);
      complexPlace.updatePlace(listPart);
      putTabStock();
      ProgramPanels.updateAllPanelsForUpdatingPlaces();
      labelCreated.setText(getLabel("CreateStorage.StorageModified"), true);
      nom_obj.setModified(false);
    } else {
      if (complexPlace.getPartCount() > listPart.size()) {
        int nb = 0;
        for (int i = listPart.size(); i < complexPlace.getPartCount(); i++) {
          nb += complexPlace.getCountCellUsed(i);
        }
        if (nb > 0) {
          Debug("ERROR: Unable to reduce the number of place");
          Erreur.showSimpleErreur(getError("Error.removeNotEmptyShelve"));
          return;
        }
      }
      boolean canContinue = true;
      for (int i = 0; i < listPart.size(); i++) {
        if (!canContinue) {
          Debug("ERROR: canContinue false, skipping part");
          continue;
        }
        Part part = listPart.get(i);
        int nbRow = -1;
        if (i < complexPlace.getPartCount()) {
          nbRow = complexPlace.getLineCountAt(i);
        }
        int newNbRow = part.rows().size();
        if (nbRow > newNbRow) {
          // Reduce the number of row, checking that the lines are empty
          int nb = 0;
          for (int j = newNbRow; j < nbRow; j++) {
            nb += complexPlace.getNbCaseUseInLine(i, j);
          }
          if (nb > 0) {
            canContinue = false;
            Debug("ERROR: Unable to lower the number of row");
            Erreur.showSimpleErreur(MessageFormat.format(getError("Error.removeNotEmptyShelveLines"), Integer.toString(i + 1)));
          }
        }
        if (canContinue) {
          for (int j = 0; j < part.rows().size(); j++) {
            if (!canContinue) {
              Debug("ERROR: canContinue false, skipping row");
              break;
            }
            int nbCol = -1;
            if (i < complexPlace.getPartCount() && j < complexPlace.getLineCountAt(i)) {
              nbCol = complexPlace.getColumnCountAt(i, j);
            }
            int newNbCol = part.getRowAt(j).getColumnCount();
            if (nbCol > newNbCol) {
              // Reduce the number of columns, checking that the lines are empty
              for (int k = newNbCol; k < nbCol; k++) {
                if (!canContinue) {
                  Debug("ERROR: canContinue false, skipping column");
                  break;
                }
                if (complexPlace.getObject(new PlacePosition.PlacePositionBuilderZeroBased(complexPlace)
                    .withNumPlace(i)
                    .withLine(j)
                    .withColumn(k)
                    .build()).isPresent()) {
                  canContinue = false;
                  Debug("ERROR: Unable to reduce the size of the number of column");
                  Erreur.showSimpleErreur(MessageFormat.format(getError("Error.removeNotEmptyShelveLineColumns"), Integer.toString(j + 1), Integer.toString(i + 1)));
                }
              }
            }
          }
        }
      }

      if (!canContinue) {
        return;
      }

      Debug("Updating complex place: " + complexPlace.getName());
      String name = complexPlace.getName();
      if (!name.equalsIgnoreCase(nom)) {
        String erreur_txt1 = getErrorWithProperty("Error.1ItemInStorage", LabelProperty.SINGLE);
        String erreur_txt2 = getErrorWithProperty("Error.confirmChangeStorage1Item", LabelProperty.SINGLE);
        if (nbBottles == 1) {
          Debug("MESSAGE: 1 object in this place, Modify?");
        } else {
          Debug("MESSAGE: " + nbBottles + " objects in this place, Modify?");
          erreur_txt1 = MessageFormat.format(getErrorWithProperty("Error.NItemsInStorage", LabelProperty.PLURAL), nbBottles);
          erreur_txt2 = getErrorWithProperty("Error.questionChangeStorageItems", LabelProperty.PLURAL);
        }
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), erreur_txt1 + SPACE + erreur_txt2, getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          //Modify Name of place
          complexPlace.setName(nom);
          complexPlace.updatePlace(listPart);
          Program.getStorage().getAllList()
              .stream()
              .filter(b -> b.getEmplacement().equals(name))
              .forEach(b -> b.setEmplacement(nom));
          nom_obj.setText("");
        } else {
          complexPlace.setName(nom);
          complexPlace.updatePlace(listPart);
        }
      } else {
        complexPlace.updatePlace(listPart);
      }
      putTabStock();
      ProgramPanels.updateAllPanelsForUpdatingPlaces();
      labelCreated.setText(getLabel("CreateStorage.StorageModified"), true);
    }
  }

  private void updateSimplePlace(String name, int nbPart, SimplePlace simplePlace) {
    simplePlace.setName(name);
    simplePlace.setLimited(islimited, simplePlaceLimit);
    simplePlace.setPartNumberIncrement(partNumberIncrementSimplePlace);
    simplePlace.setPartCount(nbPart);
    simplePlace.resetStockage();
    Program.setListCaveModified();
    Program.setModified();
    putTabStock();
  }

  private void putTabStock() {
    if (!PlaceUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }
  }

  private void create_actionPerformed() {
    Debug("create_actionPerforming...");
    String nom = toCleanString(nom_obj.getText());

    boolean bResul = MyCellarControl.ctrl_existingName(nom);
    bResul &= MyCellarControl.ctrlName(nom);

    if (isSimplePlaceCheckbox.isSelected()) {
      Debug("Creating a simple place...");
      int nbPart = Integer.parseInt(partCountSpinner.getValue().toString());
      partNumberIncrementSimplePlace = Integer.parseInt(partIncrementSimplePlaceSpinner.getValue().toString());
      islimited = isSimplePlaceLimitedCheckbox.isSelected();
      simplePlaceLimit = Integer.parseInt(simplePlaceLimitSpinner.getValue().toString());

      if (bResul) {
        Debug("Creating...");
        Program.addPlace(new SimplePlaceBuilder(nom)
            .nbParts(nbPart)
            .startSimplePlace(partNumberIncrementSimplePlace)
            .limited(islimited)
            .limit(simplePlaceLimit).build());
        MainFrame.updateManagePlaceButton();
        Debug("Creation of '" + nom + "' completed.");
        nom_obj.setText("");
        labelCreated.setText(getLabel("CreateStorage.Created"), true);
        ProgramPanels.updateAllPanelsForUpdatingPlaces();
      }
    } else {
      Debug("Creating complex place...");
      for (Part p : listPart) {
        if (bResul && p.rows().isEmpty()) {
          Erreur.showSimpleErreur(MessageFormat.format(getError("Error.incorrectNumberLinesForShelve"), p.getNumberAsDisplay()));
          bResul = false;
        }
        for (Row r : p.rows()) {
          if (bResul && r.getColumnCount() == 0) {
            Erreur.showSimpleErreur(MessageFormat.format(getError("Error.incorrectNumberColumnsForShelve"), p.getNumberAsDisplay()));
            bResul = false;
          }
        }
      }
      //Type rangement
      if (notAllLinesSameRadio.isSelected()) {
        Debug("Creating with different column number");
      } else {
        Debug("Creating place with same column number");
      }
      if (bResul) {
        createComplexPlace(nom);
      }
    }
    if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_CREATE_MESS, false) && bResul) {
      Erreur.showInformationMessageWithKey(getError("Error.canCreateAnotherStorageSameOptions"), MyCellarSettings.DONT_SHOW_CREATE_MESS);
    }
    if (bResul) {
      MainFrame.getInstance().enableAll(true);
    }
  }

  private void createComplexPlace(String name) {
    Program.addPlace(new ComplexPlace(name, listPart));
    MainFrame.updateManagePlaceButton();
    Debug("Creating " + name + " completed.");
    labelCreated.setText(getLabel("CreateStorage.Created"), true);
    nom_obj.setText("");
    ProgramPanels.updateAllPanelsForUpdatingPlaces();
  }

  /**
   * simplePlace_itemStateChanged: Case a cocher pour choisir un rangement de type
   * Caisse ou non
   *
   * @param e ItemEvent
   */
  private void simplePlace_itemStateChanged(ItemEvent e) {
    labelCreated.setText("");
    boolean checked = isSimplePlaceCheckbox.isSelected();
    preview.setEnabled(!checked);
    panelType.setVisible(!checked);
    panelTable.setVisible(!checked);
    partIncrementSimplePlaceSpinner.setVisible(checked);
    panelStartCaisse.setVisible(checked);
    panelLimite.setVisible(checked);
    isSimplePlaceLimitedCheckbox.setVisible(true);
    if (isSimplePlaceCheckbox.isSelected()) {
      final boolean checkLimiteSelected = isSimplePlaceLimitedCheckbox.isSelected();
      label_limite.setVisible(checkLimiteSelected);
      simplePlaceLimitSpinner.setVisible(checkLimiteSelected);
    } else {
      preview.setEnabled(true);
      partCountSpinner.setValue(1);
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
    labelCreated.setText("");
    simplePlaceLimitSpinner.setVisible(isSimplePlaceLimitedCheckbox.isSelected());
    label_limite.setVisible(isSimplePlaceLimitedCheckbox.isSelected());
  }

  private void preview_actionPerformed(ActionEvent e) {
    if (isSimplePlaceCheckbox.isSelected()) {
      return;
    }
    String nom = toCleanString(nom_obj.getText());
    if (!MyCellarControl.ctrlName(nom)) {
      return;
    }

    for (Part p : listPart) {
      if (p.rows().isEmpty()) {
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error.incorrectNumberLinesForShelve"), p.getNumberAsDisplay()), getError("Error.clickOKBeforePreview"));
        return;
      }
      for (Row r : p.rows()) {
        if (r.getColumnCount() == 0) {
          Erreur.showSimpleErreur(MessageFormat.format(getError("Error.incorrectNumberColumnsForShelve"), p.getNumberAsDisplay()), getError("Error.clickOKBeforePreview"));
          return;
        }
      }
    }

    XmlUtils.writePlacesToHTML("", List.of(new ComplexPlace(nom, listPart)), true);
    Program.open(Program.getPreviewHTMLFileName(), false);
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
    if (modify) {
      if (nom_obj.isModified() || model.isModified()) {
        String label = getError(ERROR_STORAGEMODIFICATIONINCOMPLETED);
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), label + SPACE + getError(ERROR_CONFIRMQUIT), getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          return false;
        }
      }
    } else {
      if (!toCleanString(nom_obj.getText()).isEmpty()) {
        String label = getError(ERROR_STORAGECREATIONINCOMPLETED);
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), label + SPACE + getError(ERROR_CONFIRMQUIT), getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          return false;
        }
      }
    }
    nom_obj.setModified(false);
    Debug("Quitting...");
    labelCreated.setText("");
    comboPlace.setSelectedIndex(0);
    return true;
  }

  @Override
  public void setUpdateViewType(UpdateViewType updateViewType) {
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
      updateViewType = null;
      new MyCellarSwingWorker() {
        @Override
        protected void done() {
          initComboPlaces();
        }
      }.execute();
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
