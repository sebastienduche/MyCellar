package mycellar.general;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.Music;
import mycellar.MyCellarControl;
import mycellar.Program;
import mycellar.Start;
import mycellar.actions.ManageCapacityAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellarObject;
import mycellar.core.JCompletionComboBox;
import mycellar.core.JModifyComboBox;
import mycellar.core.JModifyTextField;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
import mycellar.core.common.music.MyCellarMusicSupport;
import mycellar.core.datas.MyCellarBottleContenance;
import net.miginfocom.swing.MigLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.LinkedList;

import static mycellar.core.LabelProperty.OF_THE_PLURAL;
import static mycellar.core.LabelProperty.OF_THE_SINGLE;
import static mycellar.core.LabelProperty.SINGLE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 21/10/21
 */
public final class PanelGeneral extends JPanel implements ICutCopyPastable {

  private final MyCellarButton manageContenance = new MyCellarButton(LabelType.INFO, "400");
  private final JModifyTextField year = new JModifyTextField();
  private final JModifyComboBox<String> type = new JModifyComboBox<>();
  private final MyCellarCheckBox noYear = new MyCellarCheckBox(LabelType.INFO, "399");
  private final MyCellarCheckBox yearAuto = new MyCellarCheckBox("");
  private final int siecle = Program.getCaveConfigInt(MyCellarSettings.SIECLE, 20) - 1;
  private final JCompletionComboBox<String> name;
  private JCompletionComboBox<String> artist;
  private JCompletionComboBox<String> composer;
  private IMyCellarObject myCellarObject;
  private boolean severalItems;

  public PanelGeneral() {
    name = new JCompletionComboBox<>() {
      private static final long serialVersionUID = 8137073557763181546L;

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        ProgramPanels.setSelectedPaneModified(true);
      }
    };
    if (Program.isMusicType()) {
      artist = new JCompletionComboBox<>() {
        private static final long serialVersionUID = 8137073557763181546L;

        @Override
        protected void doAfterModify() {
          super.doAfterModify();
          ProgramPanels.setSelectedPaneModified(true);
        }
      };

      composer = new JCompletionComboBox<>() {
        private static final long serialVersionUID = 8137073557763181546L;

        @Override
        protected void doAfterModify() {
          super.doAfterModify();
          ProgramPanels.setSelectedPaneModified(true);
        }
      };
    }
    setLayout(new MigLayout("", "[grow]30px[]10px[]10px[]30px[]10px[]", ""));
    add(new MyCellarLabel(LabelType.INFO, "208"), "grow");
    add(new MyCellarLabel(LabelType.INFO, "189"));
    add(yearAuto);
    add(new MyCellarLabel(LabelType.INFO, "134"), "wrap");
    add(name, "grow");
    add(year, "width min(100,10%)");
    add(noYear);
    add(type, "push");
    if (Program.isWineType()) {
      add(manageContenance);
    } else if (Program.isMusicType()) {
      JPanel panelArtistComposer = new JPanel();
      panelArtistComposer.setBounds(0, 0, 0, 0);
      panelArtistComposer.setLayout(new MigLayout("","0px[grow][grow]0px"));
      panelArtistComposer.add(new MyCellarLabel(LabelType.INFO_OTHER, "Main.Artist"), "grow");
      panelArtistComposer.add(new MyCellarLabel(LabelType.INFO_OTHER, "Main.Composer"), "grow, wrap");
      panelArtistComposer.add(artist, "grow");
      panelArtistComposer.add(composer, "grow");
      add(panelArtistComposer, "newline, grow");
    }
  }

  private static void Debug(String s) {
    Program.Debug("PanelGeneral: " + s);
  }

  public String getObjectName() {
    return name.getEditor().getItem().toString();
  }

  public void initializeExtraProperties() {
    name.setSelectedItem(myCellarObject.getNom());
    year.setText(myCellarObject.getAnnee());
    final boolean nonVintage = myCellarObject.isNonVintage();
    noYear.setSelected(nonVintage);
    year.setEditable(!nonVintage);
    type.removeAllItems();
    type.addItem("");
    if (Program.isMusicType()) {
      MyCellarMusicSupport.getList().forEach(type::addItem);
      composer.setSelectedItem(((Music)myCellarObject).getComposer());
      artist.setSelectedItem(((Music)myCellarObject).getArtist());
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
        String erreur_txt1 = MessageFormat.format(Program.getError("Error084"), ((siecle + 1) * 100)); //"En decochant cette option, vous dsactivez la transformation");
        Erreur.showInformationMessageWithKey(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_FALSE);
      }
    } else {
      Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, true);

      if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_TRUE, false)) {
        String erreur_txt1 = MessageFormat.format(Program.getError("Error086"), ((siecle + 1) * 100));//"En cochant cette option, vous activez la transformation");
        Erreur.showInformationMessageWithKey(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_TRUE);
      }
    }
    Debug("Annee_auto_actionPerformed...End");
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
      name.setSelectedItem(MessageFormat.format(Program.getLabel("AddVin.NbItemsSelected", LabelProperty.PLURAL), itemCount)); //" bouteilles selectionnees
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
      if (Program.safeParseInt(annee, -1) > n) {
        annee = siecle + annee;
      } else {
        annee = (siecle + 1) + annee;
      }
    }
    return annee;
  }

  private void setYearAuto() {
    yearAuto.setText(MessageFormat.format(Program.getLabel("Infos117"), ((siecle + 1) * 100))); //"Annee 00 -> 2000");
    yearAuto.setSelected(Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
  }

  public void updateView() {
    loadTypeComboBox();
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

  public PanelGeneral setMyCellarObject(IMyCellarObject myCellarObject) {
    this.myCellarObject = myCellarObject;
    return this;
  }

  public void setSeveralItems(boolean severalItems) {
    this.severalItems = severalItems;
  }

  public void clearValues() {
    name.setSelectedIndex(0);
    if (Program.isMusicType()) {
      composer.setSelectedIndex(0);
      artist.setSelectedIndex(0);
    }
    year.setText("");
  }

  public void setModifyActive(boolean b) {
    year.setModifyActive(b);
    type.setModifyActive(b);
  }

  public void initValues() {
    initNameCombo();
    initComposerArtistCombo();

    loadTypeComboBox();

    setYearAuto();
    initYearAndContenance();
  }

  private void initYearAndContenance() {
    manageContenance.addActionListener(this::manageContenance_actionPerformed);
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

  private void manageContenance_actionPerformed(ActionEvent e) {
    Debug("Manage Capacity...");
    new ManageCapacityAction().actionPerformed(null);
    Debug("Manage Capacity... End");
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
    if (MyCellarControl.hasInvalidBotteName(nom)) {
      return false;
    }

    // Controle de la date
    if (!severalItems && (year.isEditable() || !noYear.isSelected())) {
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

  public boolean runExit(boolean modify) {
    if (!name.getText().isEmpty()) {
      String erreur_txt1;
      if (!modify) {
        erreur_txt1 = Program.getError("Error144", SINGLE.withCapital());
      } else {
        erreur_txt1 = Program.getError("Error148", name.isEnabled() ? OF_THE_SINGLE : OF_THE_PLURAL);
      }
      Debug("Message: Confirm to Quit?");
      if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), erreur_txt1 + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
        Debug("Don't Quit.");
        return false;
      }
    }
    return true;
  }

  @Override
  public void cut() {
    String text = name.getEditor().getItem().toString();
    if (text != null) {
      Program.CLIPBOARD.copier(text);
      name.getEditor().setItem("");
    }
  }

  @Override
  public void copy() {
    String text = name.getEditor().getItem().toString();
    if (text != null) {
      Program.CLIPBOARD.copier(text);
    }
  }

  @Override
  public void paste() {
    String text = Program.CLIPBOARD.coller();
    if (text != null && !text.isEmpty()) {
      name.getEditor().setItem(text);
    }
  }

  public void initializeForEdition() {
    initNameCombo();
    initComposerArtistCombo();
    loadTypeComboBox();

    setYearAuto();
    manageContenance.setText(Program.getLabel("Infos400"));

    initYearAndContenance();
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
}
