package mycellar.importer;


import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Filtre;
import mycellar.ITabListener;
import mycellar.Music;
import mycellar.MyCellarControl;
import mycellar.MyCellarUtils;
import mycellar.MyOptions;
import mycellar.Options;
import mycellar.Program;
import mycellar.Start;
import mycellar.actions.OpenShowErrorsAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.music.MyCellarMusicSupport;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.storage.ListeBouteille;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static mycellar.MyCellarImage.OPEN;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.ProgramConstants.COMMA;
import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.ProgramConstants.IMPORT_COMBO_COUNT;
import static mycellar.ProgramConstants.KEY_TYPE;
import static mycellar.ProgramConstants.SLASH;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 15.9
 * @since 31/05/22
 */
public final class Importer extends JPanel implements ITabListener, Runnable, ICutCopyPastable, IMyCellar {

  static final long serialVersionUID = 280706;
  private final MyCellarButton importe = new MyCellarButton("Import.Title");
  private final MyCellarRadioButton type_txt = new MyCellarRadioButton("Import.TxtCsv", true);
  private final MyCellarRadioButton type_xls = new MyCellarRadioButton("Import.Xls", false);
  private final MyCellarRadioButton type_xml = new MyCellarRadioButton("Filter.Xml", false);
  private final MyCellarRadioButton type_iTunes = new MyCellarRadioButton("Import.ITunes", false);
  private final char importChar = getLabel("IMPORT").charAt(0);
  private final char ouvrirChar = getLabel("OUVRIR").charAt(0);
  private final List<MyCellarComboBox<MyCellarFields>> comboBoxList = new ArrayList<>(IMPORT_COMBO_COUNT);
  private final MyCellarCheckBox labelTitle = new MyCellarCheckBox("Import.WithTitle");
  private final MyCellarLabel labelTitle2 = new MyCellarLabel("Import.ChooseColumns");
  private final MyCellarSimpleLabel label_progression = new MyCellarSimpleLabel();
  private final MyCellarLabel label2 = new MyCellarLabel("Import.Separator");
  private final MyCellarComboBox<String> separateur = new MyCellarComboBox<>();
  private final JTextField file = new JTextField();


  public Importer() {
    Debug("Constructor");
    MyCellarButton openit = new MyCellarButton("Main.OpenTheFile");
    openit.setToolTipText(getLabel("Main.OpenTheFile"));
    MyCellarButton parcourir = new MyCellarButton(OPEN);
    parcourir.setToolTipText(getLabel("Main.Browse"));
    importe.setMnemonic(importChar);
    openit.setMnemonic(ouvrirChar);
    importe.setText(getLabel("Import.Action"));
    importe.addActionListener(this::importe_actionPerformed);
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
    openit.addActionListener(this::openit_actionPerformed);
    parcourir.addActionListener(this::parcourir_actionPerformed);
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
    panelFileType.setBorder(BorderFactory.createTitledBorder(getLabel("Import.FileType")));
    panelType.add(panelFileType);
    JPanel panelSeparator = new JPanel();
    panelSeparator.setLayout(new MigLayout("", "", ""));
    panelSeparator.add(label2);
    panelSeparator.add(separateur, "gapleft 10px");
    panelType.add(panelSeparator);
    add(panelType, "grow, wrap");
    JPanel panelFile = new JPanel();
    panelFile.setLayout(new MigLayout("", "[grow][][]", "[]"));
    panelFile.add(new MyCellarLabel("Import.Path"), "wrap");
    panelFile.add(file, "grow");
    panelFile.add(parcourir);
    panelFile.add(openit);
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
      if (i > 0) {
        combo.setEnabled(false);
      }
    }
    add(panelChoix, "grow, wrap");
    add(label_progression, "grow, center, hidemode 3, wrap");
    add(importe, "center");

    separateur.addItem(getLabel("CSV.SeparatorDotComma"));
    separateur.addItem(getLabel("CSV.SeparatorDoubleDot"));
    separateur.addItem(getLabel("CSV.SeparatorSlash"));
    separateur.addItem(getLabel("CSV.SeparatorComma"));

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
    separateur.setVisible(type_txt.isSelected());
    boolean typeXml = type_xml.isSelected() || type_iTunes.isSelected();
    comboBoxList.forEach(c -> c.setVisible(!typeXml));
    labelTitle.setVisible(!typeXml);
    labelTitle2.setVisible(!typeXml);
  }

  private void resetLabelProgress() {
    label_progression.setText("");
  }

  private void importe_actionPerformed(ActionEvent e) {
    new Thread(this).start();
  }

  /**
   * updateCombo: Choix
   *
   * @param e     ActionEvent
   * @param index int
   */
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
   * Realise la lecture d'une ligne d'un fichier XLS
   *
   * @param row Row Ligne d'une feuille Excel
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
        throw new UnsupportedOperationException(MessageFormat.format(getError("Importer.unknownCellType"), cell.getCellType()));
      }
    }
    return bottle;
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
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error.fileNotFound"), ""));
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
    if (!filename.isEmpty()) {
      File f = new File(filename);
      file.setText(f.getAbsolutePath());
      if (!Program.open(filename, true)) {
        resetLabelProgress();
        Debug("ERROR: File not found: " + f.getAbsolutePath());
      }
    }
  }

  /**
   * run: Fonction d'import
   */
  @Override
  public void run() {
    try {
      Debug("Importing...");
      importe.setEnabled(false);

      String filename = toCleanString(file.getText());
      if (filename.isEmpty()) {
        //Erreur le filename ne doit pas etre vide
        Debug("ERROR: filename cannot be empty");
        resetLabelProgress();
        Erreur.showSimpleErreur(getError("Error.fileNameShouldntBeEmpty"));
        importe.setEnabled(true);
        return;
      }

      int nb_choix = 0;
      EnumMap<MyCellarFields, Integer> mapFieldCount = new EnumMap<>(MyCellarFields.class);
      if (type_xls.isSelected() || type_txt.isSelected()) {
        for (var combo : comboBoxList) {
          if (combo.getSelectedIndex() != 0) {
            nb_choix++;
          }
        }

        for (int i = 0; i < nb_choix; i++) {
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
        //Fichier non trouve Verifier le chemin
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error.fileNotFound"), filename), getError("Error.checkFilePath"));
        importe.setEnabled(true);
        return;
      }

      if ((type_xls.isSelected() || type_txt.isSelected()) && nb_choix == 0) {
        resetLabelProgress();
        Debug("ERROR: No field selected");
        // Aucuns champs selectionnes
        // Veuillez selectionner des champs pour que les donnees soient traitees
        Erreur.showSimpleErreur(getError("Error025"), getError("Error026"));
        importe.setEnabled(true);
        return;
      }

      if (type_xls.isSelected()) {
        if (MyCellarControl.hasInvalidExtension(filename, Arrays.asList(Filtre.FILTRE_XLSX.toString(), Filtre.FILTRE_XLS.toString(), Filtre.FILTRE_ODS.toString()))) {
          resetLabelProgress();
          Debug("ERROR: Not a XLS File");
          Erreur.showSimpleErreur(MessageFormat.format(getError("Error.notAnExcelFile"), filename), getError("Error.selectAnExcelFile"));
          importe.setEnabled(true);
          return;
        }
      } else if (type_txt.isSelected()) {
        if (MyCellarControl.hasInvalidExtension(filename, Arrays.asList(Filtre.FILTRE_TXT.toString(), Filtre.FILTRE_CSV.toString()))) {
          resetLabelProgress();
          Debug("ERROR: Not a Text File");
          Erreur.showSimpleErreur(MessageFormat.format(getError("Error023"), filename), getError("Error024"));
          importe.setEnabled(true);
          return;
        }
      } else {
        if (MyCellarControl.hasInvalidExtension(filename, Collections.singletonList(Filtre.FILTRE_XML.toString()))) {
          resetLabelProgress();
          Debug("ERROR: Not a XML File");
          Erreur.showSimpleErreur(MessageFormat.format(getError("Error204"), filename), getError("Error205"));
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
        //"Un champ ne doit pas etre selectionne 2 fois.
        //"Veuillez choisir un champ different pour chaque colonne.
        Erreur.showSimpleErreur(getError("Error017"), getError("Error018"));
        importe.setEnabled(true);
        return;
      }

      if (mapFieldCount.get(MyCellarFields.NAME) == null) {
        resetLabelProgress();
        Debug("ERROR: No column for wine name");
        //"Aucune colonne n'indique le nom du vin.
        //"Veuillez selectionner une colonne avec le nom du vin
        Erreur.showSimpleErreur(getError("Error.NoColumnSelectedForName", LabelProperty.OF_THE_SINGLE), getError("Error143", LabelProperty.SINGLE));
        importe.setEnabled(true);
        return;
      }

      Rangement new_rangement = null;
      if (mapFieldCount.get(MyCellarFields.PLACE) == null) {
        resetLabelProgress();
        Debug("ERROR: No place defined, a place will be create");
        //Il n'y a pas de rangements definis dans le fichier.
        //Un rangement par defaut va etre cree.
        Erreur.showInformationMessage(getError("Error140"), getError("Error141"));

        int nb_caisse = Program.getSimplePlaceCount() + 2;

        String[] titre_properties = new String[nb_caisse];
        String[] default_value = new String[nb_caisse];
        String[] key_properties = new String[nb_caisse];
        String[] type_objet = new String[nb_caisse];
        int i;
        List<Rangement> simplePlaces = Program.getSimplePlaces();
        for (i = 0; i < simplePlaces.size(); i++) {
          Rangement cave = simplePlaces.get(i);
          titre_properties[i] = KEY_TYPE + cave.getName();
          key_properties[i] = MyCellarSettings.RANGEMENT_DEFAULT;
          default_value[i] = "false";
          type_objet[i] = MyOptions.MY_CELLAR_RADIO_BUTTON;
        }
        titre_properties[i] = "Main.NewValue";
        key_properties[i] = MyCellarSettings.RANGEMENT_DEFAULT;
        default_value[i] = "true";
        type_objet[i] = MyOptions.MY_CELLAR_RADIO_BUTTON;
        i++;
        titre_properties[i] = getLabel("Import.DefaultStorageName");
        key_properties[i] = MyCellarSettings.RANGEMENT_NAME;
        default_value[i] = "";
        type_objet[i] = MyOptions.JTEXT_FIELD;
        MyOptions myoptions = new MyOptions(getLabel("CreateStorage.Title"), getLabel("Import.SelectStorageName"), List.of(titre_properties), List.of(default_value), List.of(key_properties), List.of(type_objet), false);
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
                Options options = new Options(getLabel("Import.StorageName"), getLabel("Import.FillStorageName"), getLabel("Import.StorageName"), nom1,
                    getError("Error.forbiddenCharacters"), false);
                options.setVisible(true);
                nom1 = options.getValue();
                resul = false;
              }
            } while (!resul);
            do {
              // Controle sur la longueur du filename
              if (nom1.isEmpty()) {
                Options options = new Options(getLabel("Import.StorageName"), getLabel("Import.FillStorageName"), getLabel("Import.StorageName"), nom1,
                    getError("Error.requireStorageName"), false);
                options.setVisible(true);
                nom1 = options.getValue();
                resul = false;
              }
            } while (nom1.isEmpty());
            if (resul) {
              do {
                // Controle de l'existance du rangement
                resul = true;
                if (!nom1.isEmpty()) {
                  if (RangementUtils.isExistingPlace(nom1)) {
                    Options options = new Options(getLabel("Import.StorageName"), getLabel("Import.FillStorageName"), getLabel("Import.StorageName"), nom1,
                        getError("Error.storageNameAlreadyUsed"), false);
                    options.setVisible(true);
                    nom1 = options.getValue();
                    resul = false;
                  }
                }
              } while (!resul);
            }
          } while (!resul);
          Debug("Creating new place with name: " + nom1);
          new_rangement = new Rangement.SimplePlaceBuilder(nom1).build();
          Program.addPlace(new_rangement);
        } else {
          new_rangement = Program.getPlaceAt(num_r);
        }
      }
      if (type_txt.isSelected()) {
        //Cas des fichiers TXT
        Debug("Importing Text File...");
        String separe;
        switch (separateur.getSelectedIndex()) {
          case 1:
            separe = DOUBLE_DOT;
            break;
          case 2:
            separe = SLASH;
            break;
          case 3:
            separe = COMMA;
            break;
          case 0:
          default:
            separe = COLUMNS_SEPARATOR;
        }

        try (var reader = new BufferedReader(new FileReader(f))) {
          String line = reader.readLine();
          if (line != null) {
            if (line.split(separe).length <= 1) {
              resetLabelProgress();
              Debug("ERROR: No separator found");
              //"Le separateur selectionne n'a pas ete trouve.
              //"Veuillez selectionner le separateur utilise dans votre fichier.
              Erreur.showSimpleErreur(getError("Error042"), getError("Error043"));
              importe.setEnabled(true);
              reader.close();
              return;
            }
          }
          if (labelTitle.isSelected()) {
            line = reader.readLine();
          }
          setLabelInProgress();
          int maxNumPlace = 0;
          while (line != null) {
            String[] lu = line.split(separe);
            MyCellarObject bottle = createObject();
            bottle.updateID();
            for (int i = 0; i < lu.length; i++) {
              String value = lu[i];
              if (value.length() > 1 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
                value = value.substring(1, value.length() - 1);
              }
              value = MyCellarUtils.convertToHTMLString(value);
              MyCellarFields selectedField = getSelectedField(i);
              bottle.setValue(selectedField, value);
              if (selectedField.equals(MyCellarFields.NUM_PLACE) && maxNumPlace < bottle.getNumLieu()) {
                maxNumPlace = bottle.getNumLieu();
              }
            }
            if ((bottle.getEmplacement() == null || bottle.getEmplacement().isEmpty()) && new_rangement != null) {
              bottle.setEmplacement(new_rangement.getName());
              new_rangement.setPartCount(maxNumPlace + 1);
            }
            Program.getStorage().addWine(bottle);
            line = reader.readLine();
          }
        }
        displayImportDone();
      } else {
        if (!importExcelFile(filename, new_rangement)) {
          return;
        }
      }
      importe.setEnabled(true);
    } catch (IOException exc) {
      Program.showException(exc);
    }
    if (RangementUtils.putTabStock()) {
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
    label_progression.setText(getLabel("Import.InProgress"));
  }

  private MyCellarObject createObject() {
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
    label_progression.setText(getLabel("Import.Successful"), true);
    Debug("Import OK.");
  }

  private boolean importExcelFile(final String nom, final Rangement rangement) {
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
        LinkedList<String> bottleValues = readRow(iterator.next());
        final long count = bottleValues.stream().filter(s -> !s.isEmpty()).count();
        if (skipLine && count > 0) {
          Debug("Skipping title line");
          skipLine = false;
          continue;
        }
        if (count > 0) {
          MyCellarObject bottle = createObject();
          bottle.updateID();

          int i = 0;
          for (String value : bottleValues) {
            //Recuperation des champs selectionnes
            MyCellarFields selectedField = getSelectedField(i);
            //Alimentation de la HashMap
            Debug("Write " + selectedField + "->" + value);
            bottle.setValue(selectedField, value);
            if (selectedField.equals(MyCellarFields.NUM_PLACE) && maxNumPlace < bottle.getNumLieu()) {
              maxNumPlace = bottle.getNumLieu();
            }

            if ((bottle.getEmplacement() == null || bottle.getEmplacement().isEmpty()) && rangement != null) {
              bottle.setEmplacement(rangement.getName());
              rangement.setPartCount(maxNumPlace + 1);
            }
            i++;
          }
          Program.getStorage().addWine(bottle);
        }
      }
    } catch (IOException e) {
      resetLabelProgress();
      Debug("ERROR: File not found: " + nom);
      //Fichier non trouve. Verifier le chemin
      Erreur.showSimpleErreur(MessageFormat.format(getError("Error.fileNotFound"), nom), getError("Error.checkFilePath"));
      importe.setEnabled(true);
      return false;
    } catch (Exception e) {
      Program.showException(e, false);
      resetLabelProgress();
      Debug("ERROR: " + e);
      Erreur.showSimpleErreur(getError("Error082"));
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
      Erreur.showSimpleErreur(getError("Import.NotITunesFile"));
      resetLabelProgress();
      importe.setEnabled(true);
      return;
    }
    list.forEach(music -> music.setEmplacement(Program.DEFAULT_PLACE.getName()));
    list.forEach(music -> music.setEmplacement(Program.NEW_DEFAULT_PLACE.getName()));
    Program.getStorage().getListMyCellarObject().getMusic().addAll(list);
    showImportDone();
  }

  private void showImportDone() {
    importe.setEnabled(true);
    label_progression.setText(getLabel("Import.Done"), true);
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

  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == importChar && e.isControlDown()) {
      importe_actionPerformed(null);
    }
    if (e.getKeyCode() == ouvrirChar && e.isControlDown()) {
      openit_actionPerformed(null);
    }
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
