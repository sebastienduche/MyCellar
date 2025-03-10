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


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 11.5
 * @since 13/01/24
 */
public class Export extends JPanel implements ITabListener, Runnable, ICutCopyPastable, IMyCellar {

  private static final char OUVRIR = getLabel("OUVRIR").charAt(0);
  private static final char EXPORT = getLabel("EXPORT").charAt(0);
  private final MyCellarButton valider = new MyCellarButton("Main.Export");
  private final JTextField file = new JTextField();
  private final MyCellarButton browse = new MyCellarButton(OPEN);
  private final MyCellarButton parameters = new MyCellarButton("Main.Parameters", new ParametersAction());
  private final JProgressBar progressBar = new JProgressBar();
  private final MyCellarRadioButton MyCellarRadioButtonXML = new MyCellarRadioButton("Export.Xml", true);
  private final MyCellarRadioButton MyCellarRadioButtonHTML = new MyCellarRadioButton("Export.Html", false);
  private final MyCellarRadioButton MyCellarRadioButtonCSV = new MyCellarRadioButton("Export.Csv", false);
  private final MyCellarRadioButton MyCellarRadioButtonXLS = new MyCellarRadioButton("Export.Xls", false);
  private final MyCellarRadioButton MyCellarRadioButtonPDF = new MyCellarRadioButton("Export.Pdf", false);
  private final MyCellarSimpleLabel end = new MyCellarSimpleLabel();
  private final MyCellarButton openit = new MyCellarButton("Main.OpenTheFile");
  private final MyCellarButton options = new MyCellarButton("Main.Settings", LabelProperty.SINGLE.withThreeDashes(), new SettingsAction());
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
      Erreur.showInformationMessage(MessageFormat.format(getLabel("Main.SavedFile"), nomFichier.getAbsolutePath()));
    } catch (IOException | RuntimeException ex) {
      Erreur.showSimpleErreur(getError("Error160"), getError("Error161"));
      Program.showException(ex, false);
      return false;
    }
    return true;
  }

  private void initialize() {
    MyCellarLabel nameLabel = new MyCellarLabel("Export.FileName");
    end.setFont(FONT_DIALOG_BOLD);
    openit.setMnemonic(OUVRIR);
    openit.addActionListener((e) -> openit_actionPerformed());
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
    panelFormat.setBorder(BorderFactory.createTitledBorder(getLabel("Export.ExportFormat")));
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
      //Erreur utilisation de caracteres interdits
      if (MyCellarControl.controlPath(nomFichier)) {
        Filtre filtre = (Filtre) boiteFichier.getFileFilter();
        String nom = nomFichier.getAbsolutePath();
        nom = MyCellarControl.controlAndUpdateExtension(nom, filtre);
        file.setText(nom);
      }
    }
  }

  /**
   * openit_actionPerformed: Ouvrir le fichier issu de l'export.
   */
  private void openit_actionPerformed() {
    String nom = toCleanString(file.getText());
    if (!Program.open(nom, true)) {
      end.setText("");
    }
  }

  private void keylistener_actionPerformed(KeyEvent e) {
    if (e.getKeyCode() == OUVRIR && openit.isEnabled()) {
      openit_actionPerformed();
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
    end.setText(getLabel("Export.ExportInProgress"));

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
          MessageFormat.format(getError("Export.replaceFileQuestion"), aFile.getAbsolutePath()),
          getLabel("Main.AskConfirmation"),
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE)) {
        end.setText("");
        valider.setEnabled(true);
        return;
      }
    }

    if (MyCellarRadioButtonXML.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, Collections.singletonList(Filtre.FILTRE_XML))) {
        //"Le fichier saisi ne possede pas une extension XML: " + str_tmp3);
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error087"), nom));
        valider.setEnabled(true);
        return;
      }

      ListeBouteille liste = new ListeBouteille();
      myCellarObjects.forEach(liste::add);
      boolean ok = ListeBouteille.writeXML(liste, aFile);
      if (ok) {
        end.setText(getLabel("Export.Ended"));
        openit.setEnabled(true);
      } else {
        end.setText(getError("Error.exportError"));
      }
    } else if (MyCellarRadioButtonHTML.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, List.of(Filtre.FILTRE_HTML))) {
        //"Le fichier saisi ne possede pas une extension HTML: " + str_tmp3);
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error107"), nom));
        valider.setEnabled(true);
        return;
      }

      if (PlaceUtils.writeHTML(aFile, myCellarObjects, Program.getHTMLColumns())) {
        end.setText(getLabel("Export.Ended"));
        Erreur.showInformationMessage(MessageFormat.format(getLabel("Main.SavedFile"), aFile.getAbsolutePath()));
        openit.setEnabled(true);
      } else {
        end.setText(getError("Error.exportError"));
      }
    } else if (MyCellarRadioButtonCSV.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, List.of(Filtre.FILTRE_CSV))) {
        //"Le fichier saisi ne possede pas une extension CSV: " + str_tmp3);
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error108"), nom));
        valider.setEnabled(true);
        return;
      }

      progressBar.setVisible(true);
      if (PlaceUtils.writeCSV(aFile, myCellarObjects, progressBar)) {
        end.setText(getLabel("Export.Ended"));
        Erreur.showInformationMessage(MessageFormat.format(getLabel("Main.SavedFile"), aFile.getAbsolutePath()),
            getLabel("Export.CSVInfo"));
        openit.setEnabled(true);
      }
      progressBar.setVisible(false);
    } else if (MyCellarRadioButtonXLS.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, asList(Filtre.FILTRE_XLSX, Filtre.FILTRE_XLS, Filtre.FILTRE_ODS))) {
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error.notAnExcelFile"), nom));
        valider.setEnabled(true);
        return;
      }

      progressBar.setVisible(true);
      if (PlaceUtils.writeXLS(aFile, myCellarObjects, false, progressBar)) {
        end.setText(getLabel("Export.Ended"));
        Erreur.showInformationMessage(MessageFormat.format(getLabel("Main.SavedFile"), aFile.getAbsolutePath()));
        openit.setEnabled(true);
      } else {
        end.setText(getError("Error.exportError"));
        Erreur.showSimpleErreur(getError("Error160"), getError("Error161"));
      }
      progressBar.setVisible(false);
    } else if (MyCellarRadioButtonPDF.isSelected()) {
      if (MyCellarControl.hasInvalidExtension(nom, List.of(Filtre.FILTRE_PDF))) {
        //"Le fichier saisi ne possede pas une extension PDF: " + str_tmp3);
        end.setText("");
        Erreur.showSimpleErreur(MessageFormat.format(getError("Error157"), nom));
        valider.setEnabled(true);
        return;
      }

      if (exportToPDF(myCellarObjects, aFile)) {
        end.setText(getLabel("Export.Ended"));
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
      super("Main.Settings", LabelProperty.SINGLE.withThreeDashes());
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
        JOptionPane.showMessageDialog(MainFrame.getInstance(), panel, getLabel("Main.Columns"), JOptionPane.PLAIN_MESSAGE);
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
      super("Main.Settings", LabelProperty.SINGLE.withThreeDashes());
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      List<String> titre_properties = List.of(
          "Export.Xml",
          "Export.Html",
          "Export.Csv",
          "Export.Xls",
          "Export.Pdf");
      ArrayList<String> default_value = new ArrayList<>(List.of("false", "false", "false", "false", "false"));
      List<String> key_properties = List.of(EXPORT_DEFAULT, EXPORT_DEFAULT,
          EXPORT_DEFAULT, EXPORT_DEFAULT, EXPORT_DEFAULT);
      default_value.set(Program.getCaveConfigInt(key_properties.get(0), 0), "true");

      List<String> type_objet = List.of(MyOptions.MY_CELLAR_RADIO_BUTTON, MyOptions.MY_CELLAR_RADIO_BUTTON, MyOptions.MY_CELLAR_RADIO_BUTTON, MyOptions.MY_CELLAR_RADIO_BUTTON, MyOptions.MY_CELLAR_RADIO_BUTTON);
      MyOptions myoptions = new MyOptions(getLabel("Export.Options"), getLabel("Export.SelectDefaultMode"), titre_properties, default_value, key_properties, type_objet, false);
      myoptions.setVisible(true);
    }
  }
}
