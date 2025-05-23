package mycellar;

import mycellar.actions.ManageCapacityAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.MyCellarSettings;
import mycellar.core.text.Language;
import mycellar.core.text.LanguageFileLoader;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.core.uicomponents.PopupListener;
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

import static java.util.Arrays.asList;
import static mycellar.MyCellarImage.OPEN;
import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.core.MyCellarSettings.ANNEE;
import static mycellar.core.MyCellarSettings.DEVISE;
import static mycellar.core.MyCellarSettings.DIR;
import static mycellar.core.MyCellarSettings.DONT_SHOW_CREATE_MESS;
import static mycellar.core.MyCellarSettings.DONT_SHOW_INFO;
import static mycellar.core.MyCellarSettings.DONT_SHOW_TAB_MESS;
import static mycellar.core.MyCellarSettings.FILE_EXCEL;
import static mycellar.core.MyCellarSettings.GLOBAL_LANGUAGE;
import static mycellar.core.MyCellarSettings.HAS_EXCEL_FILE;
import static mycellar.core.MyCellarSettings.SIECLE;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_FILENOTFOUND;
import static mycellar.general.ResourceErrorKey.ERROR_NOTANEXCELFILE;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTANEXCELFILE;
import static mycellar.general.ResourceKey.MAIN_BROWSE;
import static mycellar.general.ResourceKey.MAIN_GENERAL;
import static mycellar.general.ResourceKey.MAIN_VALIDATE;
import static mycellar.general.ResourceKey.PARAMETERS_OTHERS;
import static mycellar.general.ResourceKey.PARAMETER_ACTIVATE;
import static mycellar.general.ResourceKey.PARAMETER_ACTIVATEHIDDENMESSAGE;
import static mycellar.general.ResourceKey.PARAMETER_AUTOEXPORTEXCEL;
import static mycellar.general.ResourceKey.PARAMETER_BELONGSTO;
import static mycellar.general.ResourceKey.PARAMETER_CAPACITIESMANAGEMENT;
import static mycellar.general.ResourceKey.PARAMETER_CENTURY;
import static mycellar.general.ResourceKey.PARAMETER_CHOOSELANGUAGE;
import static mycellar.general.ResourceKey.PARAMETER_CURRENCY;
import static mycellar.general.ResourceKey.PARAMETER_DATECONTROL;
import static mycellar.general.ResourceKey.PARAMETER_EXCELFILE;
import static mycellar.general.ResourceKey.PARAMETER_YEARGREATERTHAN;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2004
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 14.0
 * @since 13/03/25
 */
public final class ParametersPanel extends JPanel implements ITabListener, ICutCopyPastable, IMyCellar {

  private final MyCellarLabel label_fic_bak;
  private final MyCellarComboBox<String> langue = new MyCellarComboBox<>();
  private final MyCellarButton parcourir_excel = new MyCellarButton(OPEN);
  private final JTextField file_bak = new JTextField();
  private final JTextField devise = new JTextField();
  private final MyCellarCheckBox jcb_excel = new MyCellarCheckBox(PARAMETER_AUTOEXPORTEXCEL, false);
  private final MyCellarButton buttonResetMessageDialog;
  private final MyCellarCheckBox jcb_annee_control = new MyCellarCheckBox(PARAMETER_ACTIVATE, false);
  private final MyCellarLabel label_annee;
  private final MyCellarLabel label_annee2;
  private final MyCellarLabel label_siecle;
  private final MyCellarSpinner annee = new MyCellarSpinner(0, 99);
  private final MyCellarSpinner siecle = new MyCellarSpinner(18, 99);


  public ParametersPanel() {
    setLayout(new MigLayout("", "grow", ""));
    label_fic_bak = new MyCellarLabel(PARAMETER_EXCELFILE);
    MyCellarLabel label_langue = new MyCellarLabel(PARAMETER_CHOOSELANGUAGE);
    MyCellarLabel label_devise = new MyCellarLabel(PARAMETER_CURRENCY);
    label_annee = new MyCellarLabel(PARAMETER_YEARGREATERTHAN);
    label_annee2 = new MyCellarLabel(PARAMETER_BELONGSTO);
    label_siecle = new MyCellarLabel(PARAMETER_CENTURY);
    buttonResetMessageDialog = new MyCellarButton(PARAMETER_ACTIVATEHIDDENMESSAGE);
    MyCellarButton buttonManageContenance = new MyCellarButton(PARAMETER_CAPACITIESMANAGEMENT, new ManageCapacityAction());
    MyCellarButton valider = new MyCellarButton(MAIN_VALIDATE);
    parcourir_excel.setToolTipText(getLabel(MAIN_BROWSE));
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
    file_bak.setText(Program.getCaveConfigString(FILE_EXCEL));

    annee.setValue(Program.getCaveConfigInt(ANNEE, 50));
    siecle.setValue(Program.getCaveConfigInt(SIECLE, 19));

    devise.setText(Program.getCaveConfigString(DEVISE));
    LanguageFileLoader.getLanguages().forEach(langue::addItem);
    String the_language = Program.getGlobalConfigString(GLOBAL_LANGUAGE);
    langue.setSelectedIndex(LanguageFileLoader.getLanguageIndex(the_language));

    valider.addActionListener(this::valider_actionPerformed);
    parcourir_excel.addActionListener(this::parcourir_excel_actionPerformed);
    jcb_excel.addActionListener(this::jcb_excel_actionPerformed);
    buttonResetMessageDialog.addActionListener(this::jcb_message_actionPerformed);

    JPanel dateControlPanel = new JPanel();
    JPanel generalPanel = new JPanel();
    JPanel excelPanel = new JPanel();
    JPanel otherPanel = new JPanel();
    dateControlPanel.setBorder(BorderFactory.createTitledBorder(getLabel(PARAMETER_DATECONTROL)));
    generalPanel.setBorder(BorderFactory.createTitledBorder(getLabel(MAIN_GENERAL)));
    excelPanel.setBorder(BorderFactory.createTitledBorder(getLabel(PARAMETER_AUTOEXPORTEXCEL)));
    otherPanel.setBorder(BorderFactory.createTitledBorder(getLabel(PARAMETERS_OTHERS)));
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

    jcb_annee_control.setEnabled(Program.hasOpenedFile());
    jcb_excel.setEnabled(Program.hasOpenedFile());
    devise.setEnabled(Program.hasOpenedFile());

    boolean excel = Program.getCaveConfigBool(HAS_EXCEL_FILE, false);
    file_bak.setEnabled(excel);
    label_fic_bak.setEnabled(excel);
    jcb_excel.setSelected(excel);
    parcourir_excel.setEnabled(excel);

    if (Program.getCaveConfigBool(MyCellarSettings.HAS_YEAR_CTRL, false)) {
      jcb_annee_control.setSelected(true);
    }
    label_annee.setEnabled(jcb_annee_control.isSelected());
    label_annee2.setEnabled(jcb_annee_control.isSelected());
    label_siecle.setEnabled(jcb_annee_control.isSelected());
    annee.setEnabled(jcb_annee_control.isSelected());
    siecle.setEnabled(jcb_annee_control.isSelected());
  }

  private void setLabels() {
    jcb_excel.setText(getLabel(PARAMETER_ACTIVATE));
    parcourir_excel.setToolTipText(getLabel(MAIN_BROWSE));
    jcb_annee_control.setText(getLabel(PARAMETER_ACTIVATE));
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
        Program.putCaveConfigBool(HAS_EXCEL_FILE, true);
        String fic = file_bak.getText();
        if (MyCellarControl.hasInvalidExtension(fic, asList(Filtre.FILTRE_XLSX, Filtre.FILTRE_XLS, Filtre.FILTRE_ODS))) {
          Erreur.showSimpleErreur(getError(ERROR_NOTANEXCELFILE, fic), getError(ERROR_SELECTANEXCELFILE));
          return;
        } else {
          Program.putCaveConfigString(FILE_EXCEL, fic);
        }
      } else {
        Program.putCaveConfigBool(HAS_EXCEL_FILE, false);
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
    JFileChooser boiteFichier = new JFileChooser(Program.getCaveConfigString(DIR));
    boiteFichier.removeChoosableFileFilter(boiteFichier.getFileFilter());
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_ODS);
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLS);
    boiteFichier.addChoosableFileFilter(Filtre.FILTRE_XLSX);
    if (JFileChooser.APPROVE_OPTION == boiteFichier.showOpenDialog(this)) {
      File nomFichier = boiteFichier.getSelectedFile();
      if (nomFichier == null) {
        setCursor(Cursor.getDefaultCursor());
        Erreur.showSimpleErreur(getError(ERROR_FILENOTFOUND, ""));
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
    String theLanguage = LanguageFileLoader.getLanguageFromIndex(langue.getSelectedIndex());
    String currentLanguage = Program.getGlobalConfigString(GLOBAL_LANGUAGE, Language.FRENCH.toString());
    if (theLanguage.equals(currentLanguage)) {
      return;
    }
    Program.putGlobalConfigString(GLOBAL_LANGUAGE, theLanguage);
    Program.setLanguage(Language.getLanguage(theLanguage.charAt(0)));
    if (LanguageFileLoader.getInstance().isLoaded()) {
      setLabels();
    } else {
      langue.setSelectedIndex(0);
      Program.setLanguage(Language.FRENCH);
      JOptionPane.showMessageDialog(null, "Language corrupted, Default French language selected.\nReinstall the application.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void jcb_message_actionPerformed(ActionEvent e) {
    Program.putCaveConfigBool(DONT_SHOW_INFO, false);
    Program.putCaveConfigBool(DONT_SHOW_TAB_MESS, false);
    Program.putCaveConfigBool(DONT_SHOW_CREATE_MESS, false);
    buttonResetMessageDialog.setEnabled(false);
  }

  @Override
  public void tabClosed() {
    ProgramPanels.deleteParameters();
  }

  @Override
  public void cut() {
    String text = file_bak.getSelectedText();
    String fullText = file_bak.getText();
    if (text != null) {
      file_bak.setText(fullText.substring(0, file_bak.getSelectionStart()) + fullText.substring(file_bak.getSelectionEnd()));
      Program.CLIPBOARD.copy(text);
    }
  }

  @Override
  public void copy() {
    Program.CLIPBOARD.copy(file_bak.getSelectedText());
  }

  @Override
  public void paste() {
    String fullText = file_bak.getText();
    file_bak.setText(fullText.substring(0, file_bak.getSelectionStart()) + Program.CLIPBOARD.paste() + fullText.substring(file_bak.getSelectionEnd()));
  }
}
