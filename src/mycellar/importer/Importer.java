package mycellar.importer;


import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Filtre;
import mycellar.ITabListener;
import mycellar.Music;
import mycellar.MyCellarControl;
import mycellar.MyCellarUtils;
import mycellar.Options;
import mycellar.Program;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.music.MyCellarMusicSupport;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.storage.ListeBouteille;
import mycellar.core.text.LabelKey;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.PopupListener;
import mycellar.frame.MainFrame;
import mycellar.myoptions.MyOptionKey;
import mycellar.myoptions.MyOptionObjectType;
import mycellar.myoptions.MyOptions;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.SimplePlaceBuilder;
import net.miginfocom.swing.MigLayout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static mycellar.MyCellarImage.OPEN;
import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.MyCellarUtils.removeQuotes;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.ProgramConstants.COMMA;
import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.ProgramConstants.IMPORT_COMBO_COUNT;
import static mycellar.ProgramConstants.SLASH;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR018;
import static mycellar.general.ResourceErrorKey.ERROR025;
import static mycellar.general.ResourceErrorKey.ERROR026;
import static mycellar.general.ResourceErrorKey.ERROR042;
import static mycellar.general.ResourceErrorKey.ERROR043;
import static mycellar.general.ResourceErrorKey.ERROR082;
import static mycellar.general.ResourceErrorKey.ERROR140;
import static mycellar.general.ResourceErrorKey.ERROR141;
import static mycellar.general.ResourceErrorKey.ERROR204;
import static mycellar.general.ResourceErrorKey.ERROR205;
import static mycellar.general.ResourceErrorKey.ERROR_CANTSELECTFIELDTWICE;
import static mycellar.general.ResourceErrorKey.ERROR_CHECKFILEPATH;
import static mycellar.general.ResourceErrorKey.ERROR_FILENAMESHOULDNTBEEMPTY;
import static mycellar.general.ResourceErrorKey.ERROR_FILENOTFOUND;
import static mycellar.general.ResourceErrorKey.ERROR_FORBIDDENCHARACTERS;
import static mycellar.general.ResourceErrorKey.ERROR_NOCOLUMNSELECTEDFORNAME;
import static mycellar.general.ResourceErrorKey.ERROR_NOTANEXCELFILE;
import static mycellar.general.ResourceErrorKey.ERROR_REQUIRESTORAGENAME;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTANEXCELFILE;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTCOLUMNFORBOTTLEIMPORT;
import static mycellar.general.ResourceErrorKey.ERROR_STORAGENAMEALREADYUSED;
import static mycellar.general.ResourceErrorKey.IMPORTER_UNKNOWNCELLTYPE;
import static mycellar.general.ResourceErrorKey.IMPORT_NOTITUNESFILE;
import static mycellar.general.ResourceKey.CREATESTORAGE_TITLE;
import static mycellar.general.ResourceKey.CSV_SEPARATORCOMMA;
import static mycellar.general.ResourceKey.CSV_SEPARATORDOTCOMMA;
import static mycellar.general.ResourceKey.CSV_SEPARATORDOUBLEDOT;
import static mycellar.general.ResourceKey.CSV_SEPARATORSLASH;
import static mycellar.general.ResourceKey.FILTER_XML;
import static mycellar.general.ResourceKey.IMPORT;
import static mycellar.general.ResourceKey.IMPORT_ACTION;
import static mycellar.general.ResourceKey.IMPORT_CHOOSECOLUMNS;
import static mycellar.general.ResourceKey.IMPORT_DEFAULTSTORAGENAME;
import static mycellar.general.ResourceKey.IMPORT_DONE;
import static mycellar.general.ResourceKey.IMPORT_ERROR;
import static mycellar.general.ResourceKey.IMPORT_FILETYPE;
import static mycellar.general.ResourceKey.IMPORT_FILLSTORAGENAME;
import static mycellar.general.ResourceKey.IMPORT_INPROGRESS;
import static mycellar.general.ResourceKey.IMPORT_ITUNES;
import static mycellar.general.ResourceKey.IMPORT_PATH;
import static mycellar.general.ResourceKey.IMPORT_SELECTSTORAGENAME;
import static mycellar.general.ResourceKey.IMPORT_SEPARATOR;
import static mycellar.general.ResourceKey.IMPORT_STORAGENAME;
import static mycellar.general.ResourceKey.IMPORT_SUCCESSFUL;
import static mycellar.general.ResourceKey.IMPORT_TITLE;
import static mycellar.general.ResourceKey.IMPORT_TXTCSV;
import static mycellar.general.ResourceKey.IMPORT_WITHTITLE;
import static mycellar.general.ResourceKey.IMPORT_XLS;
import static mycellar.general.ResourceKey.MAIN_BROWSE;
import static mycellar.general.ResourceKey.MAIN_NEWVALUE;
import static mycellar.general.ResourceKey.MAIN_OPENTHEFILE;
import static mycellar.general.ResourceKey.OUVRIR;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 16.8
 * @since 21/03/25
 */
public final class Importer extends JPanel implements ITabListener, Runnable, ICutCopyPastable, IMyCellar {

  private final MyCellarButton importe = new MyCellarButton(IMPORT_TITLE);
  private final MyCellarRadioButton type_txt = new MyCellarRadioButton(IMPORT_TXTCSV, true);
  private final MyCellarRadioButton type_xls = new MyCellarRadioButton(IMPORT_XLS, false);
  private final MyCellarRadioButton type_xml = new MyCellarRadioButton(FILTER_XML, false);
  private final MyCellarRadioButton type_iTunes = new MyCellarRadioButton(IMPORT_ITUNES, false);
  private final char importChar = getLabel(IMPORT).charAt(0);
  private final char ouvrirChar = getLabel(OUVRIR).charAt(0);
  private final List<MyCellarComboBox<MyCellarFields>> comboBoxList = new ArrayList<>(IMPORT_COMBO_COUNT);
  private final MyCellarCheckBox labelTitle = new MyCellarCheckBox(IMPORT_WITHTITLE);
  private final MyCellarLabel labelTitle2 = new MyCellarLabel(IMPORT_CHOOSECOLUMNS);
  private final MyCellarSimpleLabel label_progression = new MyCellarSimpleLabel();
  private final MyCellarLabel label2 = new MyCellarLabel(IMPORT_SEPARATOR);
  private final MyCellarComboBox<String> separatorCombo = new MyCellarComboBox<>();
  private final JTextField file = new JTextField();


  public Importer() {
    Debug("Constructor");
    MyCellarButton openItButton = new MyCellarButton(MAIN_OPENTHEFILE);
    openItButton.setToolTipText(getLabel(MAIN_OPENTHEFILE));
    MyCellarButton browseButton = new MyCellarButton(OPEN);
    browseButton.setToolTipText(getLabel(MAIN_BROWSE));
    importe.setMnemonic(importChar);
    openItButton.setMnemonic(ouvrirChar);
    importe.setText(getLabel(IMPORT_ACTION));
    importe.addActionListener(this::runImport);
    labelTitle.setHorizontalTextPosition(SwingConstants.LEFT);
    label_progression.setForeground(Color.red);
    label_progression.setFont(new Font("Dialog", Font.BOLD, 12));
    label_progression.setHorizontalAlignment(SwingConstants.CENTER);
    ButtonGroup checkboxGroup1 = new ButtonGroup();
    checkboxGroup1.add(type_txt);
    checkboxGroup1.add(type_xls);
    checkboxGroup1.add(type_xml);
    if (Program.isMusicType()) {
      checkboxGroup1.add(type_iTunes);
      type_iTunes.addItemListener(this::type_itemStateChanged);
    }
    type_txt.addItemListener(this::type_itemStateChanged);
    openItButton.addActionListener(this::openit_actionPerformed);
    browseButton.addActionListener(this::parcourir_actionPerformed);
    type_xls.addItemListener(this::type_itemStateChanged);
    type_xml.addItemListener(this::type_itemStateChanged);

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });

    file.addMouseListener(new PopupListener());

    setLayout(new MigLayout("", "grow", ""));
    JPanel panelType = new JPanel();
    panelType.setLayout(new MigLayout("", "[][]", "[]"));
    JPanel panelFileType = new JPanel();
    panelFileType.setLayout(new MigLayout());
    panelFileType.add(type_txt);
    panelFileType.add(type_xls, "gapleft 15px");
    panelFileType.add(type_xml, "gapleft 15px");
    if (Program.isMusicType()) {
      panelFileType.add(type_iTunes, "gapleft 15px");
    }
    panelFileType.setBorder(BorderFactory.createTitledBorder(getLabel(IMPORT_FILETYPE)));
    panelType.add(panelFileType);
    JPanel panelSeparator = new JPanel();
    panelSeparator.setLayout(new MigLayout("", "", ""));
    panelSeparator.add(label2);
    panelSeparator.add(separatorCombo, "gapleft 10px");
    panelType.add(panelSeparator);
    add(panelType, "grow, wrap");
    JPanel panelFile = new JPanel();
    panelFile.setLayout(new MigLayout("", "[grow][][]", "[]"));
    panelFile.add(new MyCellarLabel(IMPORT_PATH), "wrap");
    panelFile.add(file, "grow");
    panelFile.add(browseButton);
    panelFile.add(openItButton);
    add(panelFile, "grow,wrap");
    JPanel panel = new JPanel();
    panel.setLayout(new MigLayout());
    panel.add(labelTitle);
    add(panel, "wrap");
    JPanel panelChoix = new JPanel();
    panelChoix.setLayout(new MigLayout("", "[][][][]", ""));
    panelChoix.add(labelTitle2, "span 4,wrap");
    for (int i = 0; i < IMPORT_COMBO_COUNT; i++) {
      MyCellarComboBox<MyCellarFields> combo = new MyCellarComboBox<>();
      combo.addItem(MyCellarFields.EMPTY);
      Objects.requireNonNull(MyCellarFields.getFieldsListForImportAndWorksheet())
          .forEach(combo::addItem);
      if (i < IMPORT_COMBO_COUNT - 1) {
        int index = i + 1;
        combo.addActionListener((e) -> updateCombo(e, index));
      }
      combo.addItem(MyCellarFields.USELESS);
      comboBoxList.add(combo);
      panelChoix.add(combo, i % 6 == 5 ? "wrap" : "");
      combo.setEnabled(i == 0);
    }
    add(panelChoix, "grow, wrap");
    add(label_progression, "grow, center, hidemode 3, wrap");
    add(importe, "center");

    separatorCombo.addItem(getLabel(CSV_SEPARATORDOTCOMMA));
    separatorCombo.addItem(getLabel(CSV_SEPARATORDOUBLEDOT));
    separatorCombo.addItem(getLabel(CSV_SEPARATORSLASH));
    separatorCombo.addItem(getLabel(CSV_SEPARATORCOMMA));

    Debug("Constructor Done");
  }

  private static void Debug(String text) {
    Program.Debug("Importer: " + text);
  }

  /**
   * type_txt_itemStateChanged: Selection d'un type de fichier
   *
   * @param e ItemEvent
   */
  private void type_itemStateChanged(ItemEvent e) {
    resetLabelProgress();
    label2.setVisible(type_txt.isSelected());
    separatorCombo.setVisible(type_txt.isSelected());
    boolean typeXml = type_xml.isSelected() || type_iTunes.isSelected();
    comboBoxList.forEach(c -> c.setVisible(!typeXml));
    labelTitle.setVisible(!typeXml);
    labelTitle2.setVisible(!typeXml);
  }

  private void resetLabelProgress() {
    label_progression.setText("");
  }

  private void runImport(ActionEvent e) {
    new Thread(this).start();
  }

  private void updateCombo(ActionEvent e, int index) {
    if (((MyCellarComboBox<?>) e.getSource()).getSelectedIndex() == 0) {
      for (int i = index; i < IMPORT_COMBO_COUNT; i++) {
        final var comboBox = comboBoxList.get(i);
        comboBox.setEnabled(false);
        comboBox.setSelectedIndex(0);
      }
    } else {
      comboBoxList.get(index).setEnabled(true);
    }
  }

  /**
   * Read an XSL row
   */
  private LinkedList<String> readRow(Row row) {
    final Iterator<Cell> cellIterator = row.cellIterator();
    LinkedList<String> valueList = new LinkedList<>();
    while (cellIterator.hasNext()) {
      final Cell cell = cellIterator.next();
      if (cell.getCellType() == CellType.NUMERIC) {
        valueList.add(Double.toString(cell.getNumericCellValue()));
      } else if (cell.getCellType() == CellType.STRING) {
        valueList.add(cell.getStringCellValue());
      } else {
        throw new UnsupportedOperationException(getError(IMPORTER_UNKNOWNCELLTYPE, cell.getCellType()));
      }
    }
    return valueList;
  }

  private void parcourir_actionPerformed(ActionEvent e) {
    Debug("parcourir_actionPerforming...");
    JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR, ""));
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    if (type_txt.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_TXT);
    } else if (type_xls.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
    } else if (type_xml.isSelected() || type_iTunes.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    }

    if (boiteFichier.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      if (nomFichier == null) {
        setCursor(Cursor.getDefaultCursor());
        Erreur.showSimpleErreur(getError(ERROR_FILENOTFOUND, ""));
        Debug("ERROR: browseFile: File not found during Opening!");
        return;
      }
      Program.putCaveConfigString(MyCellarSettings.DIR, boiteFichier.getCurrentDirectory().toString());
      Filtre filtre = (Filtre) boiteFichier.getFileFilter();
      String fic = MyCellarControl.controlAndUpdateExtension(nomFichier.getAbsolutePath(), filtre);
      file.setText(fic);
      Debug("parcourir_actionPerforming... Done");
    }
  }

  /**
   * openit_actionPerformed: Ouverture du fichier a importer
   *
   * @param e ActionEvent
   */
  private void openit_actionPerformed(ActionEvent e) {
    Debug("openit_actionPerforming...");
    final String filename = toCleanString(file.getText());
    if (!filename.isBlank()) {
      File f = new File(filename);
      file.setText(f.getAbsolutePath());
      if (!Program.open(filename, true)) {
        resetLabelProgress();
        Debug("ERROR: File not found: " + f.getAbsolutePath());
      }
    }
  }

  /**
   * Do the import
   */
  @Override
  public void run() {
    try {
      Debug("Importing...");
      importe.setEnabled(false);

      String filename = toCleanString(file.getText());
      if (filename.isBlank()) {
        Debug("ERROR: filename cannot be empty");
        resetLabelProgress();
        Erreur.showSimpleErreur(getError(ERROR_FILENAMESHOULDNTBEEMPTY));
        importe.setEnabled(true);
        return;
      }

      int counterChoice = 0;
      EnumMap<MyCellarFields, Integer> mapFieldCount = new EnumMap<>(MyCellarFields.class);
      if (type_xls.isSelected() || type_txt.isSelected()) {
        for (var combo : comboBoxList) {
          if (combo.getSelectedIndex() != 0) {
            counterChoice++;
          }
        }

        for (int i = 0; i < counterChoice; i++) {
          final var comboBox = comboBoxList.get(i);
          final MyCellarFields selectedField = (MyCellarFields) comboBox.getSelectedItem();
          if (MyCellarFields.isRealField(selectedField)) {
            mapFieldCount.put(selectedField, mapFieldCount.getOrDefault(selectedField, 0) + 1);
          }
        }
      }

      //Ouverture du fichier a importer
      File f = new File(filename);
      file.setText(f.getAbsolutePath());
      filename = f.getAbsolutePath();
      if (!f.exists()) {
        resetLabelProgress();
        Debug("ERROR: File not found: " + filename);
        Erreur.showSimpleErreur(getError(ERROR_FILENOTFOUND, filename), getError(ERROR_CHECKFILEPATH));
        importe.setEnabled(true);
        return;
      }

      if ((type_xls.isSelected() || type_txt.isSelected()) && counterChoice == 0) {
        resetLabelProgress();
        Debug("ERROR: No field selected");
        // Please select fields
        Erreur.showSimpleErreur(ERROR025, ERROR026);
        importe.setEnabled(true);
        return;
      }

      if (type_xls.isSelected()) {
        if (MyCellarControl.hasInvalidExtension(filename, asList(Filtre.FILTRE_XLSX, Filtre.FILTRE_XLS, Filtre.FILTRE_ODS))) {
          resetLabelProgress();
          Debug("ERROR: Not a XLS File");
          Erreur.showSimpleErreur(getError(ERROR_NOTANEXCELFILE, filename), getError(ERROR_SELECTANEXCELFILE));
          importe.setEnabled(true);
          return;
        }
      } else if (type_txt.isSelected()) {
        if (MyCellarControl.hasInvalidExtension(filename, asList(Filtre.FILTRE_TXT, Filtre.FILTRE_CSV))) {
          resetLabelProgress();
          Debug("ERROR: Not a Text File");
          importe.setEnabled(true);
          return;
        }
      } else {
        if (MyCellarControl.hasInvalidExtension(filename, List.of(Filtre.FILTRE_XML))) {
          resetLabelProgress();
          Debug("ERROR: Not a XML File");
          Erreur.showSimpleErreur(getError(ERROR204, filename), getError(ERROR205));
          importe.setEnabled(true);
          return;
        }
      }

      if (type_xml.isSelected()) {
        importFromXML(f);
        return;
      }

      if (type_iTunes.isSelected()) {
        importFromITunes(f);
        return;
      }

      boolean isMoreThanOne = false;
      for (var key : mapFieldCount.keySet()) {
        if (mapFieldCount.get(key) > 1) {
          isMoreThanOne = true;
          break;
        }
      }
      if (isMoreThanOne) {
        resetLabelProgress();
        Debug("ERROR: fields cannot be selected more than one time");
        Erreur.showSimpleErreur(ERROR_CANTSELECTFIELDTWICE, ERROR018);
        importe.setEnabled(true);
        return;
      }

      if (mapFieldCount.get(MyCellarFields.NAME) == null) {
        resetLabelProgress();
        Debug("ERROR: No column for wine name");
        Erreur.showSimpleErreur(ERROR_NOCOLUMNSELECTEDFORNAME, ERROR_SELECTCOLUMNFORBOTTLEIMPORT);
        importe.setEnabled(true);
        return;
      }

      AbstractPlace new_rangement = null;
      if (mapFieldCount.get(MyCellarFields.PLACE) == null) {
        resetLabelProgress();
        Debug("ERROR: No place defined, a place will be create");
        Erreur.showInformationMessage(ERROR140, ERROR141);

        List<MyOptionKey> myOptionKeys = new ArrayList<>();
        int i;
        List<AbstractPlace> simplePlaces = Program.getSimplePlaces();
        for (i = 0; i < simplePlaces.size(); i++) {
          AbstractPlace cave = simplePlaces.get(i);
          myOptionKeys.add(new MyOptionKey(new LabelKey(cave.getName()), MyOptionObjectType.MY_CELLAR_RADIO_BUTTON, MyCellarSettings.RANGEMENT_DEFAULT, "false"));
        }
        myOptionKeys.add(new MyOptionKey(MAIN_NEWVALUE, "true", MyCellarSettings.RANGEMENT_DEFAULT, MyOptionObjectType.MY_CELLAR_RADIO_BUTTON));
        myOptionKeys.add(new MyOptionKey(IMPORT_DEFAULTSTORAGENAME, "", MyCellarSettings.RANGEMENT_NAME, MyOptionObjectType.JTEXT_FIELD));
        MyOptions myoptions = new MyOptions(getLabel(CREATESTORAGE_TITLE), getLabel(IMPORT_SELECTSTORAGENAME), myOptionKeys);
        myoptions.setVisible(true);
        int num_r = Program.getCaveConfigInt(MyCellarSettings.RANGEMENT_DEFAULT, -1);
        if (num_r == Program.getPlaceLength()) {
          String nom1 = Program.getCaveConfigString(MyCellarSettings.RANGEMENT_NAME, "");
          boolean resul;
          do {
            do {
              // Controle sur le filename
              resul = true;
              if (nom1.contains("\"") || nom1.contains(COLUMNS_SEPARATOR) || nom1.contains("<") || nom1.contains(">") || nom1.contains("?") || nom1.contains("\\") ||
                  nom1.contains(SLASH) || nom1.contains("|") || nom1.contains("*")) {
                Options options = new Options(getLabel(IMPORT_STORAGENAME), getLabel(IMPORT_FILLSTORAGENAME), getLabel(IMPORT_STORAGENAME), nom1,
                    getError(ERROR_FORBIDDENCHARACTERS), false);
                options.setVisible(true);
                nom1 = options.getValue();
                resul = false;
              }
            } while (!resul);
            do {
              // Controle sur la longueur du filename
              if (nom1.isEmpty()) {
                Options options = new Options(getLabel(IMPORT_STORAGENAME), getLabel(IMPORT_FILLSTORAGENAME), getLabel(IMPORT_STORAGENAME), nom1,
                    getError(ERROR_REQUIRESTORAGENAME), false);
                options.setVisible(true);
                nom1 = options.getValue();
                resul = false;
              }
            } while (nom1.isEmpty());
            if (resul) {
              do {
                // Check if storage exists
                resul = true;
                if (!nom1.isEmpty()) {
                  if (PlaceUtils.isExistingPlace(nom1)) {
                    Options options = new Options(getLabel(IMPORT_STORAGENAME), getLabel(IMPORT_FILLSTORAGENAME), getLabel(IMPORT_STORAGENAME), nom1,
                        getError(ERROR_STORAGENAMEALREADYUSED), false);
                    options.setVisible(true);
                    nom1 = options.getValue();
                    resul = false;
                  }
                }
              } while (!resul);
            }
          } while (!resul);
          Debug("Creating new place with name: " + nom1);
          new_rangement = new SimplePlaceBuilder(nom1).build();
          Program.addPlace(new_rangement);
          MainFrame.updateManagePlaceButton();
        } else {
          new_rangement = Program.getAbstractPlaceAt(num_r);
        }
      }
      if (type_txt.isSelected()) {
        //Cas des fichiers TXT
        Debug("Importing Text File...");
        String fieldSeparator = switch (separatorCombo.getSelectedIndex()) {
          case 1 -> DOUBLE_DOT;
          case 2 -> SLASH;
          case 3 -> COMMA;
          default -> COLUMNS_SEPARATOR;
        };

        try (var reader = new BufferedReader(new FileReader(f))) {
          String line = reader.readLine();
          if (line != null) {
            if (line.split(fieldSeparator).length <= 1) {
              resetLabelProgress();
              Debug("ERROR: No separator found");
              Erreur.showSimpleErreur(ERROR042, ERROR043);
              importe.setEnabled(true);
              reader.close();
              return;
            }
          }
          if (labelTitle.isSelected()) {
            // Skip one line
            line = reader.readLine();
          }
          setLabelInProgress();
          int maxNumPlace = 0;
          while (line != null) {
            String[] readValues = line.split(fieldSeparator);
            IMyCellarObject bottle = createObject();
            bottle.updateID();
            for (int i = 0; i < readValues.length; i++) {
              String value = removeQuotes(readValues[i]);
              value = MyCellarUtils.convertToHTMLString(value);
              MyCellarFields selectedField = getSelectedField(i);
              bottle.validateValue(selectedField, value);
              bottle.setValue(selectedField, value);
              if (selectedField.equals(MyCellarFields.NUM_PLACE) && maxNumPlace < bottle.getNumLieu()) {
                maxNumPlace = bottle.getNumLieu();
              }
            }
            if (isNullOrEmpty(bottle.getEmplacement()) && new_rangement != null) {
              bottle.setEmplacement(new_rangement.getName());
              new_rangement.setPartCount(maxNumPlace + 1);
            }
            Program.getStorage().addWine(bottle);
            line = reader.readLine();
          }
          displayImportDone();
        } catch (MyCellarException e) {
          Erreur.showSimpleErreur(this, e.getMessage());
          displayImportError();
        }
      } else {
        if (!importExcelFile(filename, new_rangement)) {
          return;
        }
      }
      importe.setEnabled(true);
    } catch (IOException exc) {
      Program.showException(exc);
    }
    if (PlaceUtils.putTabStock()) {
      if (Program.isMusicType()) {
        MyCellarMusicSupport.load();
      } else {
        MyCellarBottleContenance.load();
      }
    } else {
      new OpenShowErrorsAction().actionPerformed(null);
    }
    Debug("Importing... Done");
  }

  private void setLabelInProgress() {
    label_progression.setText(getLabel(IMPORT_INPROGRESS));
  }

  private IMyCellarObject createObject() {
    if (Program.isWineType()) {
      return new Bouteille();
    }
    if (Program.isMusicType()) {
      return new Music();
    }
    Program.throwNotImplementedForNewType();
    return null;
  }

  private void displayImportDone() {
    label_progression.setText(getLabel(IMPORT_SUCCESSFUL), true);
    Debug("Import OK.");
  }

  private void displayImportError() {
    label_progression.setText(getLabel(IMPORT_ERROR), true);
    Debug("Import Error.");
  }

  private boolean importExcelFile(final String nom, final AbstractPlace rangement) {
    Debug("Importing XLS file...");

    setLabelInProgress();
    //Ouverture du fichier Excel
    try (var workbook = new XSSFWorkbook(new FileInputStream(nom))) {

      //Selection de la feuille
      var sheet = workbook.getSheetAt(0);
      //Lecture de cellules
      Iterator<Row> iterator = sheet.iterator();
      //Ecriture du vin pour chaque ligne
      boolean skipLine = labelTitle.isSelected();
      int maxNumPlace = 0;
      while (iterator.hasNext()) {
        LinkedList<String> valueList = readRow(iterator.next());
        final long count = valueList.stream().filter(s -> !s.isEmpty()).count();
        if (skipLine && count > 0) {
          Debug("Skipping title line");
          skipLine = false;
          continue;
        }
        if (count > 0) {
          IMyCellarObject myCellarObject = createObject();
          myCellarObject.updateID();

          int i = 0;
          for (String value : valueList) {
            MyCellarFields selectedField = getSelectedField(i);
            //Alimentation de la HashMap
            Debug("Write " + selectedField + "->" + value);
            myCellarObject.setValue(selectedField, value);
            if (selectedField.equals(MyCellarFields.NUM_PLACE) && maxNumPlace < myCellarObject.getNumLieu()) {
              maxNumPlace = myCellarObject.getNumLieu();
            }

            if (isNullOrEmpty(myCellarObject.getEmplacement()) && rangement != null) {
              myCellarObject.setEmplacement(rangement.getName());
              rangement.setPartCount(maxNumPlace + 1);
            }
            i++;
          }
          Program.getStorage().addWine(myCellarObject);
        }
      }
    } catch (IOException e) {
      resetLabelProgress();
      Debug("ERROR: File not found: " + nom);
      //Fichier non trouve. Verifier le chemin
      Erreur.showSimpleErreur(getError(ERROR_FILENOTFOUND, nom), getError(ERROR_CHECKFILEPATH));
      importe.setEnabled(true);
      return false;
    } catch (Exception e) {
      Program.showException(e, false);
      resetLabelProgress();
      Debug("ERROR: " + e);
      Erreur.showSimpleErreur(getError(ERROR082));
      importe.setEnabled(true);
      return false;
    }

    displayImportDone();
    importe.setEnabled(true);
    return true;
  }

  private void importFromXML(File f) {
    setLabelInProgress();
    ListeBouteille.loadXML(f);
    showImportDone();
  }

  private void importFromITunes(File f) {
    setLabelInProgress();
    final List<Music> list;
    try {
      list = new ItunesLibraryImporter().loadItunesLibrary(f);
    } catch (NoITunesFileException e) {
      Debug("ERROR:" + e);
      Erreur.showSimpleErreur(getError(IMPORT_NOTITUNESFILE));
      resetLabelProgress();
      importe.setEnabled(true);
      return;
    }
    list.forEach(music -> music.setEmplacement(Program.DEFAULT_PLACE.getName()));
    Program.getStorage().getListMyCellarObject().getMusic().addAll(list);
    showImportDone();
  }

  private void showImportDone() {
    importe.setEnabled(true);
    label_progression.setText(getLabel(IMPORT_DONE), true);
    if (!PlaceUtils.putTabStock()) {
      new OpenShowErrorsAction().actionPerformed(null);
    }
  }

  private MyCellarFields getSelectedField(int i) {
    if (i < comboBoxList.size()) {
      return (MyCellarFields) comboBoxList.get(i).getSelectedItem();
    }
    return MyCellarFields.USELESS;
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == importChar && e.isControlDown()) {
      runImport(null);
    }
    if (e.getKeyCode() == ouvrirChar && e.isControlDown()) {
      openit_actionPerformed(null);
    }
  }

  @Override
  public void cut() {
    String text = file.getSelectedText();
    String fullText = file.getText();
    if (text != null) {
      file.setText(fullText.substring(0, file.getSelectionStart()) + fullText.substring(file.getSelectionEnd()));
      Program.CLIPBOARD.copy(text);
    }
  }

  @Override
  public void copy() {
    Program.CLIPBOARD.copy(file.getSelectedText());
  }

  @Override
  public void paste() {
    String fullText = file.getText();
    file.setText(fullText.substring(0, file.getSelectionStart()) + Program.CLIPBOARD.paste() + fullText.substring(file.getSelectionEnd()));
  }

}
