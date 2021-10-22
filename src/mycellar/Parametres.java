package mycellar;

import mycellar.actions.ManageCapacityAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSpinner;
import mycellar.core.PopupListener;
import mycellar.core.TabEvent;
import mycellar.general.ProgramPanels;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;

import static mycellar.Program.toCleanString;
import static mycellar.core.MyCellarSettings.ANNEE;
import static mycellar.core.MyCellarSettings.DEVISE;
import static mycellar.core.MyCellarSettings.DIR;
import static mycellar.core.MyCellarSettings.DONT_SHOW_CREATE_MESS;
import static mycellar.core.MyCellarSettings.DONT_SHOW_INFO;
import static mycellar.core.MyCellarSettings.DONT_SHOW_TAB_MESS;
import static mycellar.core.MyCellarSettings.FIC_EXCEL;
import static mycellar.core.MyCellarSettings.FILE_EXCEL;
import static mycellar.core.MyCellarSettings.LANGUAGE;
import static mycellar.core.MyCellarSettings.SIECLE;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 12.8
 * @since 09/06/21
 */
public final class Parametres extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar {

  private static final long serialVersionUID = -4208146070057957967L;
  private final MyCellarLabel label_fic_bak;
  private final MyCellarComboBox<String> langue = new MyCellarComboBox<>();
  private final MyCellarButton parcourir_excel = new MyCellarButton("..."); //Parcourir
  private final JTextField file_bak = new JTextField();
  private final JTextField devise = new JTextField();
  private final MyCellarCheckBox jcb_excel = new MyCellarCheckBox(Program.getLabel("Infos234"), false);
  private final MyCellarButton buttonResetMessageDialog;
  private final MyCellarCheckBox jcb_annee_control = new MyCellarCheckBox(Program.getLabel("Infos169"), false);
  private final MyCellarLabel label_annee;
  private final MyCellarLabel label_annee2;
  private final MyCellarLabel label_siecle;
  private final MyCellarSpinner annee = new MyCellarSpinner(0, 99);
  private final MyCellarSpinner siecle = new MyCellarSpinner(18, 99);


  public Parametres() {
    setLayout(new MigLayout("", "grow", ""));
    label_fic_bak = new MyCellarLabel(LabelType.INFO, "162"); //"Nom du fichier Excel:");
    MyCellarLabel label_langue = new MyCellarLabel(LabelType.INFO, "231"); //"Choix de la langue:");
    MyCellarLabel label_devise = new MyCellarLabel(LabelType.INFO, "163");
    label_annee = new MyCellarLabel(LabelType.INFO, "292");
    label_annee2 = new MyCellarLabel(LabelType.INFO, "293");
    label_siecle = new MyCellarLabel(LabelType.INFO, "295");
    jcb_excel.setText(Program.getLabel("Infos169"));
    buttonResetMessageDialog = new MyCellarButton(LabelType.INFO, "160");
    MyCellarButton buttonManageContenance = new MyCellarButton(LabelType.INFO, "400", new ManageCapacityAction());
    MyCellarButton valider = new MyCellarButton(LabelType.INFO, "315");
    parcourir_excel.setToolTipText(Program.getLabel("Infos157"));
    jcb_annee_control.setText(Program.getLabel("Infos169"));
    setLabels();

    jcb_annee_control.addActionListener((e) -> {
      label_annee.setEnabled(jcb_annee_control.isSelected());
      label_annee2.setEnabled(jcb_annee_control.isSelected());
      label_siecle.setEnabled(jcb_annee_control.isSelected());
      annee.setEnabled(jcb_annee_control.isSelected());
      siecle.setEnabled(jcb_annee_control.isSelected());
    });

    PopupListener popup_l = new PopupListener();
    file_bak.addMouseListener(popup_l);
    devise.addMouseListener(popup_l);
    file_bak.setText(Program.getCaveConfigString(FILE_EXCEL, ""));

    annee.setValue(Program.getCaveConfigInt(ANNEE, 50));
    siecle.setValue(Program.getCaveConfigInt(SIECLE, 19));

    devise.setText(Program.getCaveConfigString(DEVISE, ""));
    Program.getLanguages().forEach(langue::addItem);
    String the_language = Program.getGlobalConfigString(LANGUAGE, "");
    langue.setSelectedIndex(Program.getLanguageIndex(the_language));

    valider.addActionListener(this::valider_actionPerformed);
    parcourir_excel.addActionListener(this::parcourir_excel_actionPerformed);
    jcb_excel.addActionListener(this::jcb_excel_actionPerformed);
    buttonResetMessageDialog.addActionListener(this::jcb_message_actionPerformed);

    JPanel dateControlPanel = new JPanel();
    JPanel generalPanel = new JPanel();
    JPanel excelPanel = new JPanel();
    JPanel otherPanel = new JPanel();
    dateControlPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos294")));
    generalPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Main.General")));
    excelPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Infos234")));
    otherPanel.setBorder(BorderFactory.createTitledBorder(Program.getLabel("Parameters.Others")));
    generalPanel.setLayout(new MigLayout("", "[][]30px[][]", ""));
    generalPanel.add(label_langue);
    generalPanel.add(langue, "gapleft 10");
    generalPanel.add(label_devise);
    generalPanel.add(devise, "w 100:100:100, wrap");
    add(generalPanel, "grow, wrap");
    excelPanel.setLayout(new MigLayout("", "[100:100:100][][grow]", ""));
    excelPanel.add(jcb_excel);
    excelPanel.add(label_fic_bak);
    excelPanel.add(file_bak, "grow, split 2");
    excelPanel.add(parcourir_excel);
    add(excelPanel, "grow, wrap");
    dateControlPanel.setLayout(new MigLayout("", "[100:100:100][][][][][]", ""));
    dateControlPanel.add(jcb_annee_control);
    dateControlPanel.add(label_annee);
    dateControlPanel.add(annee);
    dateControlPanel.add(label_annee2);
    dateControlPanel.add(siecle);
    dateControlPanel.add(label_siecle);
    add(dateControlPanel, "grow, wrap");
    otherPanel.setLayout(new MigLayout("", "[][]", ""));
    otherPanel.add(buttonResetMessageDialog, "span 2, wrap");
    otherPanel.add(buttonManageContenance);
    add(otherPanel, "grow, wrap");

    add(valider, "gaptop 15px, center");

    jcb_annee_control.setEnabled(Program.hasFile());
    jcb_excel.setEnabled(Program.hasFile());
    devise.setEnabled(Program.hasFile());

    boolean excel = Program.getCaveConfigBool(FIC_EXCEL, false);
    file_bak.setEnabled(excel);
    label_fic_bak.setEnabled(excel);
    jcb_excel.setSelected(excel);
    parcourir_excel.setEnabled(excel);

    if (Program.getCaveConfigBool(MyCellarSettings.ANNEE_CTRL, false)) {
      jcb_annee_control.setSelected(true);
    }
    label_annee.setEnabled(jcb_annee_control.isSelected());
    label_annee2.setEnabled(jcb_annee_control.isSelected());
    label_siecle.setEnabled(jcb_annee_control.isSelected());
    annee.setEnabled(jcb_annee_control.isSelected());
    siecle.setEnabled(jcb_annee_control.isSelected());
  }

  private void setLabels() {
    jcb_excel.setText(Program.getLabel("Infos169"));
    parcourir_excel.setToolTipText(Program.getLabel("Infos157"));
    jcb_annee_control.setText(Program.getLabel("Infos169"));
  }

  /**
   * valider_actionPerformed: Valider les modifications et quitter.
   *
   * @param e ActionEvent
   */
  private void valider_actionPerformed(ActionEvent e) {
    try {
      modifyLanguage();
      if (jcb_excel.isSelected()) {
        Program.putCaveConfigBool(FIC_EXCEL, true);
        String fic = file_bak.getText();
        if (MyCellarControl.hasInvalidExtension(fic, Arrays.asList(Filtre.FILTRE_XLSX.toString(), Filtre.FILTRE_XLS.toString(), Filtre.FILTRE_ODS.toString()))) {
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error034"), fic), Program.getError("Error035"));
          return;
        } else {
          Program.putCaveConfigString(FILE_EXCEL, fic);
        }
      } else {
        Program.putCaveConfigBool(FIC_EXCEL, false);
      }

      Program.putCaveConfigString(DEVISE, toCleanString(devise.getText()));
      try {
        int val = Integer.parseInt(annee.getValue().toString());
        Program.putCaveConfigInt(ANNEE, val);
        val = Integer.parseInt(siecle.getValue().toString());
        Program.putCaveConfigInt(SIECLE, val);
      } catch (NumberFormatException ignored) {
      }

      Program.setYearControl(jcb_annee_control.isSelected());

      Program.saveGlobalProperties();
    } catch (RuntimeException exc) {
      Program.showException(exc);
    }
  }

  /**
   * parcourir_excel_actionPerformed: Boite Parcourir.
   *
   * @param e ActionEvent
   */
  private void parcourir_excel_actionPerformed(ActionEvent e) {
    JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(DIR, ""));
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
    if (JFileChooser.APPROVE_OPTION == boiteFichier.showOpenDialog(this)) {
      File nomFichier = boiteFichier.getSelectedFile();
      if (nomFichier == null) {
        setCursor(Cursor.getDefaultCursor());
        Erreur.showSimpleErreur(Program.getError("FileNotFound"));
        Program.Debug("ERROR: parcourir_excel: File not found while Opening!");
        return;
      }
      String fic = nomFichier.getAbsolutePath();
      Filtre filtre = (Filtre) boiteFichier.getFileFilter();
      fic = MyCellarControl.controlAndUpdateExtension(fic, filtre);
      file_bak.setText(fic);
      Program.putCaveConfigString(FILE_EXCEL, fic);
      Program.putCaveConfigString(DIR, boiteFichier.getCurrentDirectory().toString());
    }
  }

  private void jcb_excel_actionPerformed(ActionEvent e) {
    file_bak.setEnabled(jcb_excel.isSelected());
    label_fic_bak.setEnabled(jcb_excel.isSelected());
    parcourir_excel.setEnabled(jcb_excel.isSelected());
  }

  /**
   * Modification de la langue a la fermeture de la boite de dialogue
   */
  private void modifyLanguage() {
    String thelangue = Program.getLanguage(langue.getSelectedIndex());
    String currentLanguage = Program.getGlobalConfigString(LANGUAGE, "" + LanguageFileLoader.Language.FRENCH.getLanguage());
    if (thelangue.equals(currentLanguage)) {
      return;
    }
    Program.putGlobalConfigString(LANGUAGE, thelangue);
    Program.setLanguage(LanguageFileLoader.getLanguage(thelangue.charAt(0)));
    if (LanguageFileLoader.getInstance().isLoaded()) {
      setLabels();
    } else {
      langue.setSelectedIndex(0);
      Program.setLanguage(LanguageFileLoader.Language.FRENCH);
      JOptionPane.showMessageDialog(null, "Language corrupted, Default French language selected.\nReinstall your language.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void jcb_message_actionPerformed(ActionEvent e) {
    Program.putCaveConfigBool(DONT_SHOW_INFO, false);
    Program.putCaveConfigBool(DONT_SHOW_TAB_MESS, false);
    Program.putCaveConfigBool(DONT_SHOW_CREATE_MESS, false);
    buttonResetMessageDialog.setEnabled(false);
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return true;
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
    ProgramPanels.deleteParametres();
  }

  @Override
  public void cut() {
    String text = file_bak.getSelectedText();
    String fullText = file_bak.getText();
    if (text != null) {
      file_bak.setText(fullText.substring(0, file_bak.getSelectionStart()) + fullText.substring(file_bak.getSelectionEnd()));
      Program.CLIPBOARD.copier(text);
    }
  }

  @Override
  public void copy() {
    String text = file_bak.getSelectedText();
    if (text != null) {
      Program.CLIPBOARD.copier(text);
    }
  }

  @Override
  public void paste() {
    String fullText = file_bak.getText();
    file_bak.setText(fullText.substring(0, file_bak.getSelectionStart()) + Program.CLIPBOARD.coller() + fullText.substring(file_bak.getSelectionEnd()));
  }
}
