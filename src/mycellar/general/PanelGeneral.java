package mycellar.general;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Music;
import mycellar.MyCellarControl;
import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.actions.ManageCapacityAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellarObject;
import mycellar.core.IPanelModifyable;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.music.MyCellarMusicSupport;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.uicomponents.JCompletionComboBox;
import mycellar.core.uicomponents.JModifyComboBox;
import mycellar.core.uicomponents.JModifyTextField;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.PopupListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.LinkedList;

import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_CHECKTRANSFORMTO4DIGITSYEAR;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMQUIT;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMSAVE;
import static mycellar.general.ResourceErrorKey.ERROR_ITEMNOTYETADDED;
import static mycellar.general.ResourceErrorKey.ERROR_MODIFICATIONINCOMPLETED;
import static mycellar.general.ResourceErrorKey.ERROR_MODIFICATIONSINCOMPLETED;
import static mycellar.general.ResourceErrorKey.ERROR_UNCHECKTRANSFORMTO4DIGITSYEAR;
import static mycellar.general.ResourceKey.ADDVIN_ITEMMODIFIED;
import static mycellar.general.ResourceKey.ADDVIN_NBITEMSSELECTED;
import static mycellar.general.ResourceKey.ADDWINE_NOYEAR;
import static mycellar.general.ResourceKey.MAIN_ARTIST;
import static mycellar.general.ResourceKey.MAIN_CAPACITYORSUPPORT;
import static mycellar.general.ResourceKey.MAIN_COMPOSER;
import static mycellar.general.ResourceKey.MAIN_NAME;
import static mycellar.general.ResourceKey.MAIN_YEAR;
import static mycellar.general.ResourceKey.PARAMETER_CAPACITIESMANAGEMENT;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.4
 * @since 19/03/25
 */
public final class PanelGeneral extends JPanel implements ICutCopyPastable, IPanelModifyable {

  private final MyCellarLabel labelModified = new MyCellarLabel(ADDVIN_ITEMMODIFIED);
  private final MyCellarButton manageContenance = new MyCellarButton(PARAMETER_CAPACITIESMANAGEMENT);
  private final JModifyTextField year = new JModifyTextField();
  private final JModifyComboBox<String> type = new JModifyComboBox<>();
  private final MyCellarCheckBox noYear = new MyCellarCheckBox(ADDWINE_NOYEAR);
  private final MyCellarCheckBox yearAuto = new MyCellarCheckBox();
  private final int siecle = Program.getCaveConfigInt(MyCellarSettings.SIECLE, 20) - 1;
  private final JCompletionComboBox<String> name;
  private int selectedPaneIndex;
  private JCompletionComboBox<String> artist;
  private JCompletionComboBox<String> composer;
  private IMyCellarObject myCellarObject;
  private boolean severalItems;
  private boolean modificationFlagActive;

  public PanelGeneral() {
    setModificationDetectionActive(false);
    name = new JCompletionComboBox<>() {
      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modificationFlagActive) {
          ProgramPanels.setPaneModified(selectedPaneIndex, true);
        }
      }
    };
    if (Program.isMusicType()) {
      artist = new JCompletionComboBox<>() {
        @Serial
        private static final long serialVersionUID = 8137073557763181546L;

        @Override
        protected void doAfterModify() {
          super.doAfterModify();
          if (modificationFlagActive) {
            ProgramPanels.setSelectedPaneModified(true);
          }
        }
      };

      composer = new JCompletionComboBox<>() {
        @Serial
        private static final long serialVersionUID = 8137073557763181546L;

        @Override
        protected void doAfterModify() {
          super.doAfterModify();
          if (modificationFlagActive) {
            ProgramPanels.setSelectedPaneModified(true);
          }
        }
      };
    }
    labelModified.setVisible(false);
    labelModified.setForeground(Color.red);
    setLayout(new MigLayout("", "[grow]30px[]10px[]10px[]30px[]10px[]", ""));
    add(labelModified, "hidemode 3");
    add(new MyCellarLabel(MAIN_NAME), "newline, grow");
    add(new MyCellarLabel(MAIN_YEAR));
    add(yearAuto);
    add(new MyCellarLabel(MAIN_CAPACITYORSUPPORT), "wrap");
    add(name, "growx");
    add(year, "width min(100,10%)");
    add(noYear);
    add(type, "push");
    if (Program.isWineType()) {
      add(manageContenance);
    } else if (Program.isMusicType()) {
      JPanel panelArtistComposer = new JPanel();
      panelArtistComposer.setBounds(0, 0, 0, 0);
      panelArtistComposer.setLayout(new MigLayout("", "0px[]10px[]0px"));
      panelArtistComposer.add(new MyCellarLabel(MAIN_ARTIST), "grow");
      panelArtistComposer.add(new MyCellarLabel(MAIN_COMPOSER), "grow, wrap");
      panelArtistComposer.add(artist, "width min(100,10%)");
      panelArtistComposer.add(composer, "width min(100,10%)");
      add(panelArtistComposer, "span 5, newline");
    }
    setModificationDetectionActive(true);
  }

  private static void Debug(String text) {
    Program.Debug("PanelGeneral: " + text);
  }

  public void setModificationDetectionActive(boolean active) {
    modificationFlagActive = active;
    year.setActive(active);
    type.setActive(active);
  }

  public String getObjectName() {
    return name.getEditor().getItem().toString();
  }

  public void initializeExtraProperties() {
    setModificationDetectionActive(false);
    name.setSelectedItem(myCellarObject.getNom());
    year.setText(myCellarObject.getAnnee());
    final boolean nonVintage = myCellarObject.isNonVintage();
    noYear.setSelected(nonVintage);
    year.setEditable(!nonVintage);
    type.removeAllItems();
    type.addItem("");
    if (Program.isMusicType()) {
      MyCellarMusicSupport.getList().forEach(type::addItem);
      composer.setSelectedItem(((Music) myCellarObject).getComposer());
      artist.setSelectedItem(((Music) myCellarObject).getArtist());
    } else {
      MyCellarBottleContenance.getList().forEach(type::addItem);
    }
    type.setSelectedItem(myCellarObject.getKind());

    String half_tmp = "";
    if (type.getSelectedItem() != null) {
      half_tmp = type.getSelectedItem().toString();
    }
    if (!half_tmp.equals(myCellarObject.getKind()) && !myCellarObject.getKind().isEmpty()) {
      addItemToTheList();
      type.addItem(myCellarObject.getKind());
      type.setSelectedItem(myCellarObject.getKind());
    }
    setModificationDetectionActive(true);
  }

  private void addItemToTheList() {
    if (Program.isMusicType()) {
      MyCellarMusicSupport.getList().add(myCellarObject.getKind());
    } else {
      MyCellarBottleContenance.getList().add(myCellarObject.getKind());
    }
  }

  private void annee_auto_actionPerformed(ActionEvent e) {
    Debug("Annee_auto_actionPerformed...");
    if (!yearAuto.isSelected()) {
      Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false);

      if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_FALSE, false)) {
        String erreur_txt1 = getError(ERROR_UNCHECKTRANSFORMTO4DIGITSYEAR, ((siecle + 1) * 100));
        Erreur.showInformationMessageWithKey(erreur_txt1, MyCellarSettings.ANNEE_AUTO_FALSE);
      }
    } else {
      Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, true);

      if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_TRUE, false)) {
        String erreur_txt1 = getError(ERROR_CHECKTRANSFORMTO4DIGITSYEAR, ((siecle + 1) * 100));
        Erreur.showInformationMessageWithKey(erreur_txt1, MyCellarSettings.ANNEE_AUTO_TRUE);
      }
    }
    Debug("Annee_auto_actionPerformed...Done");
  }

  public void enableAll(boolean enable) {
    type.setEnabled(enable && !severalItems);
    name.setEnabled(enable && !severalItems);
    if (Program.isMusicType()) {
      composer.setEnabled(enable && !severalItems);
      artist.setEnabled(enable && !severalItems);
    }
    year.setEditable(enable && !noYear.isSelected());
    noYear.setEnabled(enable);
    yearAuto.setEnabled(enable);
    manageContenance.setEnabled(enable);
  }

  public void setEditable(boolean editable) {
    type.setEnabled(editable);
    name.setEditable(editable);
    year.setEditable(editable);
    if (Program.isMusicType()) {
      composer.setEditable(editable);
      artist.setEditable(editable);
    }
  }

  public void setViewToSeveralItemsMode(int itemCount) {
    if (itemCount > 1) {
      name.setSelectedItem(getLabel(ADDVIN_NBITEMSSELECTED, itemCount));
      name.setEnabled(false);
      if (Program.isMusicType()) {
        composer.setEnabled(false);
        artist.setEnabled(false);
      }
      yearAuto.setEnabled(false);
      noYear.setEnabled(false);
      year.setEditable(false);
      if (type.getItemCount() > 0) {
        type.setSelectedIndex(0);
      }
    }
  }

  public String getYear() {
    if (noYear.isSelected()) {
      return Bouteille.NON_VINTAGE;
    }

    String annee = year.getText();
    if (yearAuto.isSelected() && annee.length() == 2) {
      int n = Program.getCaveConfigInt(MyCellarSettings.ANNEE, 50);
      if (MyCellarUtils.safeParseInt(annee, -1) > n) {
        annee = siecle + annee;
      } else {
        annee = (siecle + 1) + annee;
      }
    }
    return annee;
  }

  private void setYearAuto() {
    yearAuto.setText(getLabel(ResourceKey.PANELGENERAL_4DIGITSYEAR, ((siecle + 1) * 100)));
    yearAuto.setSelected(Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
  }

  public void updateView() {
    setModificationDetectionActive(false);
    loadTypeComboBox();
    setModificationDetectionActive(true);
  }

  private void loadTypeComboBox() {
    type.removeAllItems();
    type.addItem("");
    if (Program.isMusicType()) {
      MyCellarMusicSupport.getList().forEach(type::addItem);
      type.setSelectedItem(MyCellarMusicSupport.getDefaultValue());
    } else {
      MyCellarBottleContenance.getList().forEach(type::addItem);
      type.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
    }
  }

  public void setMyCellarObject(IMyCellarObject myCellarObject) {
    this.myCellarObject = myCellarObject;
  }

  public void setSeveralItems(boolean severalItems) {
    this.severalItems = severalItems;
  }

  public void clearValues() {
    setModificationDetectionActive(false);
    name.setSelectedIndex(0);
    if (Program.isMusicType()) {
      composer.setSelectedIndex(0);
      artist.setSelectedIndex(0);
    }
    year.setText("");
    setModificationDetectionActive(true);
  }

  public void initValues() {
    initNameCombo();
    initComposerArtistCombo();

    loadTypeComboBox();

    setYearAuto();
    initYearAndContenance();
  }

  private void initYearAndContenance() {
    manageContenance.addActionListener((e) -> new ManageCapacityAction().actionPerformed(null));
    yearAuto.addActionListener(this::annee_auto_actionPerformed);

    noYear.addActionListener((e) -> {
      if (noYear.isSelected()) {
        year.setText(Bouteille.NON_VINTAGE);
        year.setEditable(false);
      } else {
        year.setText("");
        year.setEditable(true);
      }
    });
  }

  public void setMouseListener(PopupListener popupListener) {
    name.addMouseListener(popupListener);
    year.addMouseListener(popupListener);
    if (Program.isMusicType()) {
      composer.addMouseListener(popupListener);
      artist.addMouseListener(popupListener);
    }
  }

  public void resetValues() {
    name.removeAllItems();
    name.addItem("");
    Program.getStorage().getDistinctNames().forEach(name::addItem);
    name.setEnabled(true);
    name.setEditable(true);

    if (Program.isMusicType()) {
      composer.removeAllItems();
      composer.addItem("");
      Program.getStorage().getDistinctComposers().forEach(composer::addItem);
      composer.setEnabled(true);
      composer.setEditable(true);

      artist.removeAllItems();
      artist.addItem("");
      Program.getStorage().getDistinctArtists().forEach(artist::addItem);
      artist.setEnabled(true);
      artist.setEditable(true);
    }

    if (noYear.isSelected()) {
      year.setText(Bouteille.NON_VINTAGE);
    } else {
      year.setText("");
    }
    if (severalItems) {
      if (type.getItemCount() > 0) {
        type.setSelectedIndex(0);
      }
    } else {
      if (type.getItemCount() > 1) {
        type.setSelectedIndex(1);
      }
    }
  }

  public boolean performValidation() {
    String nom = name.getEditor().getItem().toString();
    if (MyCellarControl.hasInvalidObjectName(nom)) {
      return false;
    }

    if (severalItems) {
      return true;
    }

    // Controle de la date
    if (year.isEditable() || !noYear.isSelected()) {
      String annee = year.getText();

      // Erreur sur la date
      if (MyCellarControl.hasInvalidYear(annee)) {
        year.setEditable(true);
        return false;
      }
      annee = getYear();
      year.setText(annee);
    }
    return true;
  }

  public String updateYear() {
    String value = "";
    if (!severalItems && (year.isEditable() || noYear.isSelected())) {
      value = getYear();
      year.setText(value);
    }
    return value;
  }

  public String getType() {
    return type.getSelectedItem() != null ? type.getSelectedItem().toString() : "";
  }

  public String getTypeIfModified() {
    if (type.isModified()) {
      return getType();
    }
    return null;
  }

  public void setTypeDefault() {
    if (Program.isMusicType()) {
      type.setSelectedItem(MyCellarMusicSupport.getDefaultValue());
    } else {
      type.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
    }
  }

  public boolean askForQuit(boolean modify) {
    if (isModified()/*!name.getText().isEmpty()*/) {
      String label;
      if (modify) {
        label = getError(name.isEnabled() ? ERROR_MODIFICATIONINCOMPLETED : ERROR_MODIFICATIONSINCOMPLETED);
      } else {
        label = getError(ERROR_ITEMNOTYETADDED);
      }
      String message = String.format("%s %s", label, getError(ERROR_CONFIRMQUIT));
      Debug("Message: Confirm to Quit?");
      if (JOptionPane.NO_OPTION == Erreur.showAskConfirmationMessage(message)) {
        Debug("Don't Quit.");
        return true;
      }
    }
    return false;
  }

  public boolean askForSave(boolean modify) {
    if (isModified()) {
      String label;
      if (modify) {
        label = getError(name.isEnabled() ? ERROR_MODIFICATIONINCOMPLETED : ERROR_MODIFICATIONSINCOMPLETED);
      } else {
        label = getError(ERROR_ITEMNOTYETADDED);
      }
      String message = String.format("%s %s", label, getError(ERROR_CONFIRMSAVE));
      Debug("Message: Confirm Save?");
      if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
        Debug("Don't Quit.");
        return true;
      }
    }
    return false;
  }

  @Override
  public void cut() {
    String text = name.getEditor().getItem().toString();
    if (text != null) {
      Program.CLIPBOARD.copy(text);
      name.getEditor().setItem("");
    }
  }

  @Override
  public void copy() {
    Program.CLIPBOARD.copy(name.getEditor().getItem().toString());
  }

  @Override
  public void paste() {
    String text = Program.CLIPBOARD.paste();
    if (text != null && !text.isEmpty()) {
      name.getEditor().setItem(text);
    }
  }

  public void initializeForEdition() {
    setModificationDetectionActive(false);
    initNameCombo();
    initComposerArtistCombo();
    loadTypeComboBox();

    setYearAuto();
    manageContenance.setText(getLabel(PARAMETER_CAPACITIESMANAGEMENT));

    initYearAndContenance();
    setModificationDetectionActive(true);
  }

  private void initNameCombo() {
    LinkedList<String> list = new LinkedList<>();
    list.add("");
    list.addAll(Program.getStorage().getDistinctNames());
    list.forEach(name::addItem);
    name.setCaseSensitive(false);
    name.setEditable(true);
  }

  private void initComposerArtistCombo() {
    if (!Program.isMusicType()) {
      return;
    }
    LinkedList<String> list = new LinkedList<>();
    list.add("");
    list.addAll(Program.getStorage().getDistinctComposers());
    list.forEach(composer::addItem);
    composer.setCaseSensitive(false);
    composer.setEditable(true);

    list.clear();
    list.add("");
    list.addAll(Program.getStorage().getDistinctArtists());
    list.forEach(artist::addItem);
    artist.setCaseSensitive(false);
    artist.setEditable(true);
  }

  public void resetModified(boolean b) {
    name.setModified(b);
    year.setModified(b);
    type.setModified(b);
    if (Program.isMusicType()) {
      composer.setModified(b);
      artist.setModified(b);
    }
  }

  public boolean isModified(IMyCellarObject iMyCellarbject) {
    boolean modified = name.isModified();
    modified |= year.isModified();
    modified |= (noYear.isSelected() != iMyCellarbject.isNonVintage());
    modified |= type.isModified();
    if (Program.isMusicType()) {
      modified |= composer.isModified();
      modified |= artist.isModified();
    }
    return modified;
  }

  @Override
  public void setModified(boolean modified) {
    labelModified.setVisible(modified);
  }

  @Override
  public void setPaneIndex(int index) {
    selectedPaneIndex = index;
  }

  @Override
  public boolean isModified() {
    return labelModified.isVisible();
  }
}
