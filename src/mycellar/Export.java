package mycellar;

import com.sebastienduche.pdf.PDFPageProperties;
import com.sebastienduche.pdf.PDFProperties;
import com.sebastienduche.pdf.PDFTools;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.MyCellarFields;
import mycellar.core.storage.ListeBouteille;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarAction;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarRadioButton;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.PopupListener;
import mycellar.frame.MainFrame;
import mycellar.general.ResourceKey;
import mycellar.myoptions.MyOptionKey;
import mycellar.myoptions.MyOptions;
import mycellar.pdf.PDFOptions;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.showfile.ManageColumnModel;
import mycellar.xls.XLSOptions;
import net.miginfocom.swing.MigLayout;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static mycellar.MyCellarImage.OPEN;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.ProgramConstants.FONT_DIALOG_BOLD;
import static mycellar.core.MyCellarSettings.EXPORT_DEFAULT;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR087;
import static mycellar.general.ResourceErrorKey.ERROR157;
import static mycellar.general.ResourceErrorKey.ERROR160;
import static mycellar.general.ResourceErrorKey.ERROR161;
import static mycellar.general.ResourceErrorKey.ERROR_EXPORTERROR;
import static mycellar.general.ResourceErrorKey.ERROR_NOTANEXCELFILE;
import static mycellar.general.ResourceErrorKey.ERROR_NOTCSVFILE;
import static mycellar.general.ResourceErrorKey.ERROR_NOTHTMLFILE;
import static mycellar.general.ResourceErrorKey.EXPORT_REPLACEFILEQUESTION;
import static mycellar.general.ResourceKey.EXPORT_CSV;
import static mycellar.general.ResourceKey.EXPORT_CSVINFO;
import static mycellar.general.ResourceKey.EXPORT_ENDED;
import static mycellar.general.ResourceKey.EXPORT_EXPORTFORMAT;
import static mycellar.general.ResourceKey.EXPORT_EXPORTINPROGRESS;
import static mycellar.general.ResourceKey.EXPORT_FILENAME;
import static mycellar.general.ResourceKey.EXPORT_HTML;
import static mycellar.general.ResourceKey.EXPORT_OPTIONS;
import static mycellar.general.ResourceKey.EXPORT_PDF;
import static mycellar.general.ResourceKey.EXPORT_SELECTDEFAULTMODE;
import static mycellar.general.ResourceKey.EXPORT_XLS;
import static mycellar.general.ResourceKey.EXPORT_XML;
import static mycellar.general.ResourceKey.MAIN_ASKCONFIRMATION;
import static mycellar.general.ResourceKey.MAIN_COLUMN;
import static mycellar.general.ResourceKey.MAIN_EXPORT;
import static mycellar.general.ResourceKey.MAIN_OPENTHEFILE;
import static mycellar.general.ResourceKey.MAIN_PARAMETERS;
import static mycellar.general.ResourceKey.MAIN_SAVEDFILE;
import static mycellar.general.ResourceKey.MAIN_SETTINGS;
import static mycellar.myoptions.MyOptionObjectType.MY_CELLAR_RADIO_BUTTON;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 11.8
 * @since 12/03/25
 */
public class Export extends JPanel implements ITabListener, Runnable, ICutCopyPastable, IMyCellar {

  private static final char OUVRIR = getLabel(ResourceKey.OUVRIR).charAt(0);
  private static final char EXPORT = getLabel(ResourceKey.EXPORT).charAt(0);
  private final MyCellarButton valider = new MyCellarButton(MAIN_EXPORT);
  private final JTextField file = new JTextField();
  private final MyCellarButton browse = new MyCellarButton(OPEN);
  private final MyCellarButton parameters = new MyCellarButton(MAIN_PARAMETERS, new ParametersAction());
  private final JProgressBar progressBar = new JProgressBar();
  private final MyCellarRadioButton MyCellarRadioButtonXML = new MyCellarRadioButton(EXPORT_XML, true);
  private final MyCellarRadioButton MyCellarRadioButtonHTML = new MyCellarRadioButton(EXPORT_HTML, false);
  private final MyCellarRadioButton MyCellarRadioButtonCSV = new MyCellarRadioButton(EXPORT_CSV, false);
  private final MyCellarRadioButton MyCellarRadioButtonXLS = new MyCellarRadioButton(EXPORT_XLS, false);
  private final MyCellarRadioButton MyCellarRadioButtonPDF = new MyCellarRadioButton(EXPORT_PDF, false);
  private final MyCellarSimpleLabel end = new MyCellarSimpleLabel();
  private final MyCellarButton openit = new MyCellarButton(MAIN_OPENTHEFILE);
  private final MyCellarButton options = new MyCellarButton(MAIN_SETTINGS, LabelProperty.SINGLE.withThreeDashes(), new SettingsAction());
  private final List<? extends MyCellarObject> myCellarObjects;

  public Export() {
    myCellarObjects = Program.getStorage().getAllList();
    initialize();
  }

  public Export(final List<MyCellarObject> myCellarObjects) {
    this.myCellarObjects = myCellarObjects;
    initialize();
  }

  public static boolean exportToPDF(final List<? extends MyCellarObject> bottles, File nomFichier) {
    try {
      final PDFProperties pdfProperties = Program.getPDFProperties();
      PDFPageProperties pageProperties = new PDFPageProperties(30, 20, 20, 20, PDType1Font.HELVETICA, pdfProperties.getDefaultFontSize(), 50);
      final PDFTools pdf = new PDFTools(pdfProperties, pageProperties, true);
      pdf.writeData(Program.getPDFRows(bottles, pdfProperties));
      pdf.save(nomFichier);
      Erreur.showInformationMessage(getLabel(MAIN_SAVEDFILE, nomFichier.getAbsolutePath()));
    } catch (IOException | RuntimeException ex) {
      Erreur.showSimpleErreur(getError(ERROR160), getError(ERROR161));
      Program.showException(ex, false);
      return false;
    }
    return true;
  }

  private void initialize() {
    MyCellarLabel nameLabel = new MyCellarLabel(EXPORT_FILENAME);
    end.setFont(FONT_DIALOG_BOLD);
    openit.setMnemonic(OUVRIR);
    openit.addActionListener((e) -> openIt_actionPerformed());
    MyCellarRadioButtonXML.addActionListener(this::jradio_actionPerformed);
    MyCellarRadioButtonHTML.addActionListener(this::jradio_actionPerformed);
    MyCellarRadioButtonCSV.addActionListener(this::jradio_actionPerformed);
    end.setHorizontalAlignment(SwingConstants.CENTER);
    end.setForeground(Color.red);
    MyCellarRadioButtonXLS.addActionListener(this::jradio_actionPerformed);
    MyCellarRadioButtonPDF.addActionListener(this::jradio_actionPerformed);

    file.addMouseListener(new PopupListener());

    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(MyCellarRadioButtonXML);
    buttonGroup.add(MyCellarRadioButtonHTML);
    buttonGroup.add(MyCellarRadioButtonCSV);
    buttonGroup.add(MyCellarRadioButtonXLS);
    buttonGroup.add(MyCellarRadioButtonPDF);

    valider.setMnemonic(EXPORT);

    valider.addActionListener((e) -> export());
    browse.addActionListener((e) -> browse_actionPerformed());

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keylistener_actionPerformed(e);
      }
    });

    setLayout(new MigLayout("", "grow", "[][][]push[]"));
    JPanel panelFormat = new JPanel();
    panelFormat.setLayout(new MigLayout("", "grow", ""));
    panelFormat.add(MyCellarRadioButtonXML, "split 6");
    panelFormat.add(MyCellarRadioButtonHTML);
    panelFormat.add(MyCellarRadioButtonCSV);
    panelFormat.add(MyCellarRadioButtonXLS);
    panelFormat.add(MyCellarRadioButtonPDF);
    panelFormat.add(options, "w 100:100:100, push");
    panelFormat.setBorder(BorderFactory.createTitledBorder(getLabel(EXPORT_EXPORTFORMAT)));
    add(panelFormat, "grow, wrap");
    JPanel panelTitle = new JPanel();
    panelTitle.setLayout(new MigLayout("", "grow", ""));
    panelTitle.add(nameLabel, "split 4");
    panelTitle.add(file, "grow");
    panelTitle.add(browse);
    panelTitle.add(parameters);
    add(panelTitle, "grow, wrap");
    JPanel panelEnd = new JPanel();
    panelEnd.setLayout(new MigLayout("", "grow", ""));

    panelEnd.add(end, "grow, center, hidemode 3, wrap");
    panelEnd.add(valider, "center, split 2");
    panelEnd.add(openit);
    add(panelEnd, "grow, wrap");
    add(progressBar, "grow, center, hidemode 3");
    openit.setEnabled(false);
    options.setEnabled(false);
    progressBar.setVisible(false);

    int val = Program.getCaveConfigInt(EXPORT_DEFAULT, 0);

    MyCellarRadioButtonXML.setSelected(val == 0);
    MyCellarRadioButtonHTML.setSelected(val == 1);
    MyCellarRadioButtonCSV.setSelected(val == 2);
    MyCellarRadioButtonXLS.setSelected(val == 3);
    MyCellarRadioButtonPDF.setSelected(val == 4);
    options.setEnabled(val != 0);

    setVisible(true);
  }

  private void export() {
    new Thread(this).start();
  }

  /**
   * browse_actionPerformed: Fonction pour parcourir les repertoires.
   */
  private void browse_actionPerformed() {
    end.setText("");
    JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(MyCellarSettings.DIR));
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    if (MyCellarRadioButtonPDF.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_PDF);
    } else if (MyCellarRadioButtonXLS.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
    } else if (MyCellarRadioButtonCSV.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_CSV);
    } else if (MyCellarRadioButtonHTML.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_HTML);
    } else if (MyCellarRadioButtonXML.isSelected()) {
      boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XML);
    }

    if (boiteFichier.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File nomFichier = boiteFichier.getSelectedFile();
      Program.putCaveConfigString(MyCellarSettings.DIR, boiteFichier.getCurrentDirectory().toString());
      // Check for unauthorised characters
      if (MyCellarControl.controlPath(nomFichier)) {
        Filtre filtre = (Filtre) boiteFichier.getFileFilter();
        String nom = nomFichier.getAbsolutePath();
        nom = MyCellarControl.controlAndUpdateExtension(nom, filtre);
        file.setText(nom);
      }
    }
  }

  /**
   * Open the exported file.
   */
  private void openIt_actionPerformed() {
    String nom = toCleanString(file.getText());
    if (!Program.open(nom, true)) {
      end.setText("");
    }
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == OUVRIR && openit.isEnabled()) {
      openIt_actionPerformed();
    } else if (e.getKeyCode() == EXPORT) {
      export();
    } else if (e.getKeyCode() == KeyEvent.VK_F1) {
      Program.getAide();
    }
  }

  private void jradio_actionPerformed(ActionEvent e) {
    end.setText("");
    options.setEnabled(!MyCellarRadioButtonXML.isSelected());
  }

  /**
   * run: Export
   */
  @Override
  public void run() {
    valider.setEnabled(false);
    openit.setEnabled(false);
    String nom = toCleanString(file.getText());
    end.setText(getLabel(EXPORT_EXPORTINPROGRESS));

    if (!MyCellarControl.controlPath(nom)) {
      end.setText("");
      valider.setEnabled(true);
      return;
    }

    File aFile = new File(nom);
    if (aFile.exists()) {
      // Existing file. replace?
      if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
          MainFrame.getInstance(),
          MessageFormat.format(getError(EXPORT_REPLACEFILEQUESTION), aFile.getAbsolutePath()),
          getLabel(MAIN_ASKCONFIRMATION),
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE)) {
        end.setText("");
        valider.setEnabled(true);
        return;
      }
    }

    if (MyCellarRadioButtonXML.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, Collections.singletonList(Filtre.FILTRE_XML))) {
        // Error, not a xml file
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError(ERROR087), nom));
        valider.setEnabled(true);
        return;
      }

      ListeBouteille liste = new ListeBouteille();
      myCellarObjects.forEach(liste::add);
      boolean ok = ListeBouteille.writeXML(liste, aFile);
      if (ok) {
        end.setText(getLabel(EXPORT_ENDED));
        openit.setEnabled(true);
      } else {
        end.setText(getError(ERROR_EXPORTERROR));
      }
    } else if (MyCellarRadioButtonHTML.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, List.of(Filtre.FILTRE_HTML))) {
        // Error: Not a html file
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError(ERROR_NOTHTMLFILE), nom));
        valider.setEnabled(true);
        return;
      }

      if (PlaceUtils.writeHTML(aFile, myCellarObjects, Program.getHTMLColumns())) {
        end.setText(getLabel(EXPORT_ENDED));
        Erreur.showInformationMessage(getLabel(MAIN_SAVEDFILE, aFile.getAbsolutePath()));
        openit.setEnabled(true);
      } else {
        end.setText(getError(ERROR_EXPORTERROR));
      }
    } else if (MyCellarRadioButtonCSV.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, List.of(Filtre.FILTRE_CSV))) {
        // Error not a csv file
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError(ERROR_NOTCSVFILE), nom));
        valider.setEnabled(true);
        return;
      }

      progressBar.setVisible(true);
      if (PlaceUtils.writeCSV(aFile, myCellarObjects, progressBar)) {
        end.setText(getLabel(EXPORT_ENDED));
        Erreur.showInformationMessage(getLabel(MAIN_SAVEDFILE, aFile.getAbsolutePath()),
            getLabel(EXPORT_CSVINFO));
        openit.setEnabled(true);
      }
      progressBar.setVisible(false);
    } else if (MyCellarRadioButtonXLS.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, asList(Filtre.FILTRE_XLSX, Filtre.FILTRE_XLS, Filtre.FILTRE_ODS))) {
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError(ERROR_NOTANEXCELFILE), nom));
        valider.setEnabled(true);
        return;
      }

      progressBar.setVisible(true);
      if (PlaceUtils.writeXLS(aFile, myCellarObjects, false, progressBar)) {
        end.setText(getLabel(EXPORT_ENDED));
        Erreur.showInformationMessage(getLabel(MAIN_SAVEDFILE, aFile.getAbsolutePath()));
        openit.setEnabled(true);
      } else {
        end.setText(getError(ERROR_EXPORTERROR));
        Erreur.showSimpleErreur(getError(ERROR160), getError(ERROR161));
      }
      progressBar.setVisible(false);
    } else if (MyCellarRadioButtonPDF.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, List.of(Filtre.FILTRE_PDF))) {
        // Error, not a pdf file
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError(ERROR157), nom));
        valider.setEnabled(true);
        return;
      }

      if (exportToPDF(myCellarObjects, aFile)) {
        end.setText(getLabel(EXPORT_ENDED));
        openit.setEnabled(true);
      } else {
        end.setText("");
      }
    }
    valider.setEnabled(true);
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

  class SettingsAction extends MyCellarAction {

    private SettingsAction() {
      super(MAIN_SETTINGS, LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      end.setText("");
      if (MyCellarRadioButtonPDF.isSelected()) {
        PDFOptions ef = new PDFOptions();
        ef.setAlwaysOnTop(true);
        ef.setVisible(true);
        options.setSelected(false);
      } else if (MyCellarRadioButtonXLS.isSelected()) {
        XLSOptions xf = new XLSOptions();
        xf.setAlwaysOnTop(true);
        xf.setVisible(true);
        options.setSelected(false);
      } else if (MyCellarRadioButtonCSV.isSelected()) {
        CSVOptions cf = new CSVOptions();
        cf.setAlwaysOnTop(true);
        cf.setVisible(true);
        options.setSelected(false);
      } else if (MyCellarRadioButtonHTML.isSelected()) {
        List<MyCellarFields> fieldsList = MyCellarFields.getFieldsList();
        ManageColumnModel modelColumn = new ManageColumnModel(fieldsList, Program.getHTMLColumns());
        JTable table = new JTable(modelColumn);
        TableColumnModel tcm = table.getColumnModel();
        TableColumn tc = tcm.getColumn(0);
        tc.setCellRenderer(new CheckboxCellRenderer());
        tc.setCellEditor(new CheckboxCellEditor());
        tc.setMinWidth(25);
        tc.setMaxWidth(25);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        JOptionPane.showMessageDialog(MainFrame.getInstance(), panel, getLabel(MAIN_COLUMN), JOptionPane.PLAIN_MESSAGE);
        Program.setModified();
        List<Integer> properties = modelColumn.getSelectedColumns();
        List<MyCellarFields> cols = new ArrayList<>();
        for (MyCellarFields c : fieldsList) {
          if (properties.contains(c.getIndex())) {
            cols.add(c);
          }
        }
        Program.saveHTMLColumns(cols);
      }
    }
  }

  static class ParametersAction extends MyCellarAction {

    private ParametersAction() {
      super(MAIN_SETTINGS, LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      String val = Program.getCaveConfigString(EXPORT_DEFAULT, "0");
      List<MyOptionKey> optionKeys = List.of(
          new MyOptionKey(EXPORT_XML, "0".equals(val) ? "true" : "false", EXPORT_DEFAULT, MY_CELLAR_RADIO_BUTTON),
          new MyOptionKey(EXPORT_HTML, "1".equals(val) ? "true" : "false", EXPORT_DEFAULT, MY_CELLAR_RADIO_BUTTON),
          new MyOptionKey(EXPORT_CSV, "2".equals(val) ? "true" : "false", EXPORT_DEFAULT, MY_CELLAR_RADIO_BUTTON),
          new MyOptionKey(EXPORT_XLS, "3".equals(val) ? "true" : "false", EXPORT_DEFAULT, MY_CELLAR_RADIO_BUTTON),
          new MyOptionKey(EXPORT_PDF, "4".equals(val) ? "true" : "false", EXPORT_DEFAULT, MY_CELLAR_RADIO_BUTTON)
      );
      MyOptions myoptions = new MyOptions(getLabel(EXPORT_OPTIONS), getLabel(EXPORT_SELECTDEFAULTMODE), optionKeys);
      myoptions.setVisible(true);
    }
  }
}
