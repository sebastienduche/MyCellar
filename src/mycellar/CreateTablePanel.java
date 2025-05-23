package mycellar;

import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.UpdateViewType;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.PopupListener;
import mycellar.general.XmlUtils;
import mycellar.myoptions.MyOptionKey;
import mycellar.myoptions.MyOptionObjectType;
import mycellar.myoptions.MyOptions;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.PlaceUtils;
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
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static mycellar.MyCellarImage.OPEN;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.core.MyCellarSettings.CREATE_TAB_DEFAULT;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_LISTGENERATED;
import static mycellar.general.ResourceErrorKey.ERROR_LISTOFITEMSINSTORAGEGENERATED;
import static mycellar.general.ResourceErrorKey.ERROR_NOSTORAGESELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_NOTANEXCELFILE;
import static mycellar.general.ResourceErrorKey.ERROR_NOTAXMLFILE;
import static mycellar.general.ResourceErrorKey.ERROR_NOTHTMLFILE;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTSTORAGETOGENERATE;
import static mycellar.general.ResourceErrorKey.ERROR_SIMPLESTORAGESELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_SIMPLESTORAGESSELECTED;
import static mycellar.general.ResourceKey.CREATETABLE_FILEGENERATED;
import static mycellar.general.ResourceKey.CREATETABLE_FILETOGENERATE;
import static mycellar.general.ResourceKey.CREATETABLE_SELECTSTORAGESTOGENERATE;
import static mycellar.general.ResourceKey.CREER;
import static mycellar.general.ResourceKey.EXPORT_EXPORTFORMAT;
import static mycellar.general.ResourceKey.EXPORT_HTML;
import static mycellar.general.ResourceKey.EXPORT_OPTIONS;
import static mycellar.general.ResourceKey.EXPORT_SELECTDEFAULTMODE;
import static mycellar.general.ResourceKey.EXPORT_XLS;
import static mycellar.general.ResourceKey.EXPORT_XML;
import static mycellar.general.ResourceKey.MAIN_CREATE;
import static mycellar.general.ResourceKey.MAIN_OPENTHEFILE;
import static mycellar.general.ResourceKey.MAIN_PARAMETERS;
import static mycellar.general.ResourceKey.MAIN_SELECTALL;
import static mycellar.general.ResourceKey.MAIN_SETTINGSMENU;
import static mycellar.general.ResourceKey.OUVRIR;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2005
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 10.2
 * @since 25/03/25
 */
public final class CreateTablePanel extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar, IUpdatable {
  private final JTextField name = new JTextField();
  private final MyCellarRadioButton type_XML = new MyCellarRadioButton(EXPORT_XML, false);
  private final MyCellarRadioButton type_HTML = new MyCellarRadioButton(EXPORT_HTML, true);
  private final MyCellarRadioButton type_XLS = new MyCellarRadioButton(EXPORT_XLS, false);
  private final TableauValues tableauValues = new TableauValues();
  private final MyCellarSimpleLabel end = new MyCellarSimpleLabel();
  private final MyCellarButton preview = new MyCellarButton(MAIN_OPENTHEFILE);
  private final char createChar = getLabel(CREER).charAt(0);
  private final char ouvrirChar = getLabel(OUVRIR).charAt(0);
  private final MyCellarCheckBox selectAll = new MyCellarCheckBox(MAIN_SELECTALL);
  private final MyCellarButton m_jcb_options = new MyCellarButton(MAIN_SETTINGSMENU);
  private final JTable table;
  private boolean updateView;
  private UpdateViewType updateViewType;

  public CreateTablePanel() {
    Debug("Constructor");
    final MyCellarLabel fileLabel = new MyCellarLabel(CREATETABLE_FILETOGENERATE);
    m_jcb_options.addActionListener(this::options_actionPerformed);
    final MyCellarButton browse = new MyCellarButton(OPEN);
    browse.addActionListener(this::browse_actionPerformed);
    final MyCellarButton parameter = new MyCellarButton(MAIN_PARAMETERS);
    parameter.addActionListener(this::param_actionPerformed);
    final MyCellarLabel chooseLabel = new MyCellarLabel(CREATETABLE_SELECTSTORAGESTOGENERATE);
    final MyCellarButton create = new MyCellarButton(MAIN_CREATE);
    create.setMnemonic(createChar);

    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(type_HTML);
    buttonGroup.add(type_XML);
    buttonGroup.add(type_XLS);
    table = new JTable(tableauValues);
    table.setAutoCreateRowSorter(true);
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(TableauValues.STATE);
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
    end.setFont(FONT_DIALOG_BOLD);
    preview.setMnemonic(ouvrirChar);
    selectAll.setHorizontalAlignment(SwingConstants.RIGHT);
    selectAll.setHorizontalTextPosition(SwingConstants.LEFT);
    selectAll.addActionListener(this::selectall_actionPerformed);
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
    switch (Program.getCaveConfigInt(CREATE_TAB_DEFAULT, 1)) {
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
    panelType.setBorder(BorderFactory.createTitledBorder(getLabel(EXPORT_EXPORTFORMAT)));
    add(panelType, "grow, wrap");
    final JPanel panelTable = new JPanel();
    panelTable.setLayout(new MigLayout("", "grow", "grow"));
    panelTable.add(chooseLabel, "wrap");
    panelTable.add(jScrollPane, "grow, wrap");
    panelTable.add(selectAll, "grow, push, wrap");
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
    Program.getAbstractPlaces().forEach(tableauValues::addRangement);
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
      if (MyCellarControl.hasInvalidExtension(filename, singletonList(Filtre.FILTRE_XML))) {
        Debug("ERROR: Not a XML File");
        Erreur.showSimpleErreur(getError(ERROR_NOTAXMLFILE, filename));
        return;
      }
    } else if (type_HTML.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(filename, singletonList(Filtre.FILTRE_HTML))) {
        Debug("ERROR: Not a HTML File");
        Erreur.showSimpleErreur(getError(ERROR_NOTHTMLFILE, filename));
        return;
      }
    } else if (type_XLS.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(filename, asList(Filtre.FILTRE_XLSX, Filtre.FILTRE_XLS, Filtre.FILTRE_ODS))) {
        Debug("ERROR: Not a XLS File");
        Erreur.showSimpleErreur(getError(ERROR_NOTANEXCELFILE, filename));
        return;
      }
    }
    int max_row = tableauValues.getRowCount();
    int row = 0;
    LinkedList<AbstractPlace> rangements = new LinkedList<>();
    do {
      if (tableauValues.getValueAt(row, TableauValues.STATE).equals(Boolean.TRUE)) {
        rangements.add(tableauValues.getRangementAt(row));
      }
      row++;
    } while (row < max_row);

    if (rangements.isEmpty()) {
      Debug("ERROR: No place selected");
      Erreur.showInformationMessage(ERROR_NOSTORAGESELECTED, ERROR_SELECTSTORAGETOGENERATE);
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
      caisseCount = rangements.stream().filter(AbstractPlace::isSimplePlace).count();
      PlaceUtils.writeXLSTable(filename, rangements);
    }

    if (!Program.getCaveConfigBool(MyCellarSettings.DONT_SHOW_TAB_MESS, false)) {
      if (caisseCount > 0) {
        String erreur_txt1, erreur_txt2;
        if (caisseCount == 1) {
          erreur_txt1 = getError(ERROR_SIMPLESTORAGESELECTED);
          erreur_txt2 = getError(ERROR_LISTOFITEMSINSTORAGEGENERATED);
        } else {
          erreur_txt1 = getError(ERROR_SIMPLESTORAGESSELECTED);
          erreur_txt2 = getError(ERROR_LISTGENERATED);
        }
        Erreur.showInformationMessageWithKey(erreur_txt1, erreur_txt2, MyCellarSettings.DONT_SHOW_TAB_MESS);
      }
    }
    end.setText(getLabel(CREATETABLE_FILEGENERATED), true);
    preview.setEnabled(true);
  }

  private void preview_actionPerformed(ActionEvent e) {
    Debug("preview_actionPerforming...");
    Program.open(name.getText(), false);
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == createChar && e.isControlDown()) {
      create_actionPerformed(null);
    }
    if (e.getKeyCode() == ouvrirChar && e.isControlDown() && preview.isEnabled()) {
      preview_actionPerformed(null);
    }
  }

  private void selectall_actionPerformed(ActionEvent e) {
    for (int i = 0; i < tableauValues.getRowCount(); i++) {
      tableauValues.setValueAt(selectAll.isSelected(), i, 0);
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
    String val = Program.getCaveConfigString(CREATE_TAB_DEFAULT, "1");
    List<MyOptionKey> myOptionKeys = List.of(
        new MyOptionKey(EXPORT_XML, "0".equals(val) ? "true" : "false", CREATE_TAB_DEFAULT, MyOptionObjectType.MY_CELLAR_RADIO_BUTTON),
        new MyOptionKey(EXPORT_HTML, "1".equals(val) ? "true" : "false", CREATE_TAB_DEFAULT, MyOptionObjectType.MY_CELLAR_RADIO_BUTTON),
        new MyOptionKey(EXPORT_XLS, "2".equals(val) ? "true" : "false", CREATE_TAB_DEFAULT, MyOptionObjectType.MY_CELLAR_RADIO_BUTTON)
    );
    MyOptions myoptions = new MyOptions(getLabel(EXPORT_OPTIONS), getLabel(EXPORT_SELECTDEFAULTMODE), myOptionKeys);
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
