package mycellar;

import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.UpdateViewType;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.text.LabelProperty;
import mycellar.core.text.LabelType;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.XmlUtils;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.FONT_DIALOG_SMALL;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 8.7
 * @since 26/04/22
 */
public final class Creer_Tableaux extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {
  static final long serialVersionUID = 260706;
  private final JTextField name = new JTextField();
  private final MyCellarRadioButton type_XML = new MyCellarRadioButton(LabelType.INFO, "210", false);
  private final MyCellarRadioButton type_HTML = new MyCellarRadioButton(LabelType.INFO, "211", true);
  private final MyCellarRadioButton type_XLS = new MyCellarRadioButton(LabelType.INFO, "233", false);
  private final TableauValues tableauValues = new TableauValues();
  @SuppressWarnings("deprecation")
  private final MyCellarLabel end = new MyCellarLabel();
  private final MyCellarButton preview = new MyCellarButton(LabelType.INFO_OTHER, "Main.OpenTheFile");
  private final char creerChar = getLabel("CREER").charAt(0);
  private final char ouvrirChar = getLabel("OUVRIR").charAt(0);
  private final MyCellarCheckBox selectall = new MyCellarCheckBox(LabelType.INFO, "126");
  private final MyCellarButton m_jcb_options = new MyCellarButton(LabelType.INFO, "156", LabelProperty.SINGLE.withThreeDashes());
  private final JTable table;
  private boolean updateView;
  private UpdateViewType updateViewType;

  public Creer_Tableaux() {
    Debug("Constructor");
    final MyCellarLabel fileLabel = new MyCellarLabel(LabelType.INFO, "095"); //"Nom du fichier genere:
    m_jcb_options.addActionListener(this::options_actionPerformed);
    final MyCellarButton browse = new MyCellarButton("...");
    browse.addActionListener(this::browse_actionPerformed);
    final MyCellarButton parameter = new MyCellarButton(LabelType.INFO_OTHER, "Main.Parameters");
    parameter.addActionListener(this::param_actionPerformed);
    final MyCellarLabel chooseLabel = new MyCellarLabel(LabelType.INFO, "096"); //"Selectionner les rangements a generer:
    final MyCellarButton create = new MyCellarButton(LabelType.INFO, "018"); //"Creer
    create.setMnemonic(creerChar);

    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(type_HTML);
    buttonGroup.add(type_XML);
    buttonGroup.add(type_XLS);
    table = new JTable(tableauValues);
    table.setAutoCreateRowSorter(true);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(TableauValues.ETAT);
    tc.setCellRenderer(new CheckboxCellRenderer());
    tc.setCellEditor(new CheckboxCellEditor());
    tc.setMinWidth(25);
    tc.setMaxWidth(25);

    type_XML.addActionListener(this::jradio_actionPerformed);
    type_HTML.addActionListener(this::jradio_actionPerformed);
    type_XLS.addActionListener(this::jradio_actionPerformed);

    initModelPlaces();

    JScrollPane jScrollPane = new JScrollPane(table);
    end.setHorizontalAlignment(SwingConstants.CENTER);
    end.setForeground(Color.red);
    end.setFont(FONT_DIALOG_SMALL);
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

    setLayout(new MigLayout("", "grow", "[][][grow]"));
    final JPanel panelFile = new JPanel();
    panelFile.setLayout(new MigLayout("", "grow", ""));
    panelFile.add(fileLabel, "wrap");
    panelFile.add(name, "grow, split 3");
    panelFile.add(browse);
    panelFile.add(parameter, "push");
    add(panelFile, "grow, wrap");
    final JPanel panelType = new JPanel();
    panelType.setLayout(new MigLayout("", "[grow][grow][grow]", ""));
    panelType.add(type_XML);
    panelType.add(type_HTML);
    panelType.add(type_XLS, "split 2");
    panelType.add(m_jcb_options, "push");
    panelType.setBorder(BorderFactory.createTitledBorder(getLabel("Infos151")));
    add(panelType, "grow, wrap");
    final JPanel panelTable = new JPanel();
    panelTable.setLayout(new MigLayout("", "grow", "grow"));
    panelTable.add(chooseLabel, "wrap");
    panelTable.add(jScrollPane, "grow, wrap");
    panelTable.add(selectall, "grow, push, wrap");
    panelTable.add(end, "grow, center, hidemode 3, wrap");
    panelTable.add(create, "gaptop 15px, split 2, center");
    panelTable.add(preview);
    add(panelTable, "grow");
    preview.setEnabled(false);
    Debug("Constructor Done");
  }

  private static void Debug(String sText) {
    Program.Debug("Creer_Tableaux: " + sText);
  }

  private void initModelPlaces() {
    tableauValues.removeAll();
    Program.getPlaces().forEach(tableauValues::addRangement);
  }

  private void browse_actionPerformed(ActionEvent e) {
    Debug("browse_actionPerforming...");
    JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR, ""));
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
      Program.putCaveConfigString(MyCellarSettings.DIR, boiteFichier.getCurrentDirectory().toString());
      Filtre filtre = (Filtre) boiteFichier.getFileFilter();
      String nom = file.getAbsolutePath();
      nom = MyCellarControl.controlAndUpdateExtension(nom, filtre);
      name.setText(nom);
    }
  }

  private void create_actionPerformed(ActionEvent e) {
    Debug("create_actionPerforming...");
    String filename = toCleanString(name.getText());

    if (!MyCellarControl.controlPath(filename)) {
      return;
    }

    File path = new File(filename);
    name.setText(path.getAbsolutePath());

    //Verify file type. Is it XML File?
    if (type_XML.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(filename, Collections.singletonList(Filtre.FILTRE_XML.toString()))) {
        Debug("ERROR: Not a XML File");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error087"), filename));
        return;
      }
    } else if (type_HTML.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(filename, Collections.singletonList(Filtre.FILTRE_HTML.toString()))) {
        Debug("ERROR: Not a HTML File");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error107"), filename));
        return;
      }
    } else if (type_XLS.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(filename, Arrays.asList(Filtre.FILTRE_XLSX.toString(), Filtre.FILTRE_XLS.toString(), Filtre.FILTRE_ODS.toString()))) {
        Debug("ERROR: Not a XLS File");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error034"), filename));
        return;
      }
    }
    int max_row = tableauValues.getRowCount();
    int row = 0;
    LinkedList<Rangement> rangements = new LinkedList<>();
    do {
      if (tableauValues.getValueAt(row, TableauValues.ETAT).equals(Boolean.TRUE)) {
        rangements.add(tableauValues.getRangementAt(row));
      }
      row++;
    } while (row < max_row);

    if (rangements.isEmpty()) {
      Debug("ERROR: No place selected");
      Erreur.showInformationMessage(getError("Error089"), getError("Error090"));
      return;
    }
    long caisseCount = 0;
    if (type_XML.isSelected()) {
      Debug("Exporting in XML in progress...");
      XmlUtils.writePlacesToXML(filename, rangements, false);
    } else if (type_HTML.isSelected()) {
      Debug("Exporting in HTML in progress...");
      XmlUtils.writePlacesToHTML(filename, rangements, false);
    } else if (type_XLS.isSelected()) {
      Debug("Exporting in XLS in progress...");
      caisseCount = rangements.stream().filter(Rangement::isSimplePlace).count();
      RangementUtils.write_XLSTab(filename, rangements);
    }

    if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_TAB_MESS, false)) {
      if (caisseCount > 0) {
        String erreur_txt1, erreur_txt2;
        if (caisseCount == 1) {
          erreur_txt1 = getError("Error091"); //"Vous avez selectionne un rangement de type Caisse
          erreur_txt2 = getError("Error092", LabelProperty.PLURAL); //"Une liste des vins de ce rangement a ete generee.
        } else {
          erreur_txt1 = getError("Error127"); //"Vous avez selectionne des rangements de type Caisse
          erreur_txt2 = getError("Error128", LabelProperty.PLURAL); //"Une liste des vins de ces rangements a ete generee.
        }
        Erreur.showInformationMessageWithKey(erreur_txt1, erreur_txt2, MyCellarSettings.DONT_SHOW_TAB_MESS);
      }
    }
    end.setText(getLabel("Infos097"), true); //"Fichier genere.
    preview.setEnabled(true);
  }

  private void preview_actionPerformed(ActionEvent e) {
    Debug("preview_actionPerforming...");
    Program.open(name.getText(), false);
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == creerChar && e.isControlDown()) {
      create_actionPerformed(null);
    }
    if (e.getKeyCode() == ouvrirChar && e.isControlDown() && preview.isEnabled()) {
      preview_actionPerformed(null);
    }
  }

  private void selectall_actionPerformed(ActionEvent e) {
    for (int i = 0; i < tableauValues.getRowCount(); i++) {
      tableauValues.setValueAt(selectall.isSelected(), i, 0);
    }
    table.updateUI();
  }

  private void options_actionPerformed(ActionEvent e) {
    XLSTabOptions oXLSTabOptions = new XLSTabOptions();
    oXLSTabOptions.setVisible(true);
    m_jcb_options.setSelected(false);
  }

  private void jradio_actionPerformed(ActionEvent e) {
    m_jcb_options.setEnabled(type_XLS.isSelected());
  }

  private void param_actionPerformed(ActionEvent e) {
    Debug("param_actionPerforming...");
    List<String> titre_properties = List.of(
        getLabel("Infos210"),
        getLabel("Infos211"),
        getLabel("Infos233"));
    List<String> key_properties = List.of(
        MyCellarSettings.CREATE_TAB_DEFAULT,
        MyCellarSettings.CREATE_TAB_DEFAULT,
        MyCellarSettings.CREATE_TAB_DEFAULT);
    String val = Program.getCaveConfigString(key_properties.get(0), "1");
    List<String> default_value = List.of("0".equals(val) ? "true" : "false", "1".equals(val) ? "true" : "false", "2".equals(val) ? "true" : "false");
    List<String> type_objet = List.of(MyOptions.MY_CELLAR_RADIO_BUTTON, MyOptions.MY_CELLAR_RADIO_BUTTON, MyOptions.MY_CELLAR_RADIO_BUTTON);
    MyOptions myoptions = new MyOptions(getLabel("Infos310"), getLabel("Infos309"), titre_properties, default_value, key_properties, type_objet, false);
    myoptions.setVisible(true);
  }

  @Override
  public void cut() {
    String text = name.getSelectedText();
    String fullText = name.getText();
    if (text != null) {
      name.setText(fullText.substring(0, name.getSelectionStart()) + fullText.substring(name.getSelectionEnd()));
      Program.CLIPBOARD.copy(text);
    }
  }

  @Override
  public void copy() {
    Program.CLIPBOARD.copy(name.getSelectedText());
  }

  @Override
  public void paste() {
    String fullText = name.getText();
    name.setText(fullText.substring(0, name.getSelectionStart()) + Program.CLIPBOARD.paste() + fullText.substring(name.getSelectionEnd()));
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
  public void setUpdateViewType(UpdateViewType updateViewType) {
    updateView = true;
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    if (!updateView) {
      return;
    }
    updateView = false;
    if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
      new MyCellarSwingWorker() {
        @Override
        protected void done() {
          initModelPlaces();
          tableauValues.fireTableDataChanged();
        }
      }.execute();
    }
  }
}
