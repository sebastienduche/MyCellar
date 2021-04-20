package mycellar.general;

import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.JCompletionComboBox;
import mycellar.MyCellarControl;
import mycellar.Program;
import mycellar.Start;
import mycellar.actions.ManageCapacityAction;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellarObject;
import mycellar.core.JModifyComboBox;
import mycellar.core.JModifyTextField;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarCheckBox;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.PopupListener;
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
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 20/04/21
 */
public final class PanelGeneral extends JPanel implements ICutCopyPastable {

  protected final MyCellarButton manageContenance = new MyCellarButton(LabelType.INFO, "400");
  protected JCompletionComboBox<String> name;
  protected final JModifyTextField year = new JModifyTextField();
  protected final JModifyComboBox<String> type = new JModifyComboBox<>();
  protected final MyCellarCheckBox noYear = new MyCellarCheckBox(LabelType.INFO, "399");
  protected final MyCellarCheckBox yearAuto = new MyCellarCheckBox("");
  private final int siecle = Program.getCaveConfigInt(MyCellarSettings.SIECLE, 20) - 1;
  private IMyCellarObject myCellarObject;
  private boolean multi;

  public PanelGeneral() {
    name = new JCompletionComboBox<>() {
      private static final long serialVersionUID = 8137073557763181546L;

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        Start.setPaneModified(true);
      }
    };
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
    }
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
    MyCellarBottleContenance.getList().forEach(type::addItem);
    type.setSelectedItem(myCellarObject.getType());

    String half_tmp = "";
    if (type.getSelectedItem() != null) {
      half_tmp = type.getSelectedItem().toString();
    }
    if (!half_tmp.equals(myCellarObject.getType()) && !myCellarObject.getType().isEmpty()) {
      MyCellarBottleContenance.getList().add(myCellarObject.getType());
      type.addItem(myCellarObject.getType());
      type.setSelectedItem(myCellarObject.getType());
    }
  }

  protected void annee_auto_actionPerformed(ActionEvent e) {
    Debug("Annee_auto_actionPerformed...");
    if (!yearAuto.isSelected()) {
      Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false);

      if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_FALSE, false)) {
        String erreur_txt1 = MessageFormat.format(Program.getError("Error084"), ((siecle + 1) * 100)); //"En decochant cette option, vous dsactivez la transformation");
        Erreur.showKeyErreur(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_FALSE);
      }
    } else {
      Program.putCaveConfigBool(MyCellarSettings.ANNEE_AUTO, true);

      if (!Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO_TRUE, false)) {
        String erreur_txt1 = MessageFormat.format(Program.getError("Error086"), ((siecle + 1) * 100));//"En cochant cette option, vous activez la transformation");
        Erreur.showKeyErreur(erreur_txt1, "", MyCellarSettings.ANNEE_AUTO_TRUE);
      }
    }
    Debug("Annee_auto_actionPerformed...End");
  }

  public void enableAll(boolean enable) {
    type.setEnabled(enable && !multi);
    name.setEnabled(enable && !multi);
    year.setEditable(enable && !noYear.isSelected());
    noYear.setEnabled(enable);
    yearAuto.setEnabled(enable);
    manageContenance.setEnabled(enable);
  }

  public void setEditable(boolean editable) {
    type.setEnabled(editable);
    name.setEditable(editable);
    year.setEditable(editable);
  }

  public void resetMulti(int itemCount) {
    if (multi) {
      name.setSelectedItem(MessageFormat.format(Program.getLabel("AddVin.NbItemsSelected", LabelProperty.PLURAL), itemCount)); //" bouteilles selectionnees");
      name.setEnabled(false);
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

  protected final void setYearAuto() {
    yearAuto.setText(MessageFormat.format(Program.getLabel("Infos117"), ((siecle + 1) * 100))); //"Annee 00 -> 2000");
    yearAuto.setSelected(Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
  }

  public void updateView() {
    type.removeAllItems();
    type.addItem("");
    MyCellarBottleContenance.getList().forEach(type::addItem);
    type.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
  }

  public PanelGeneral setMyCellarObject(IMyCellarObject myCellarObject) {
    this.myCellarObject = myCellarObject;
    return this;
  }

  public void setMulti(boolean multi) {
    this.multi = multi;
  }

  public void clearValues() {
    name.setSelectedIndex(0);
    year.setText("");
  }

  protected static void Debug(String s) {
    Program.Debug("PanelGeneral: " + s);
  }

  public void setModifyActive(boolean b) {
    year.setModifyActive(b);
    type.setModifyActive(b);
  }

  public void initValues() {
    initNameCombo();

    type.addItem("");
    MyCellarBottleContenance.getList().forEach(type::addItem);
    type.setSelectedItem(MyCellarBottleContenance.getDefaultValue());

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
      }	else {
        year.setText("");
        year.setEditable(true);
      }
    });
  }

  protected void manageContenance_actionPerformed(ActionEvent e) {
    Debug("Manage Capacity...");
    new ManageCapacityAction().actionPerformed(null);
    Debug("Manage Capacity... End");
  }

  public void setMouseListener(PopupListener popupListener) {
    name.addMouseListener(popupListener);
    year.addMouseListener(popupListener);
  }

  public void resetValues() {
    name.removeAllItems();
    name.addItem("");
    Program.getStorage().getBottleNames().forEach(name::addItem);

    name.setEnabled(true);
    name.setEditable(true);
    if (noYear.isSelected()) {
      year.setText(Bouteille.NON_VINTAGE);
    } else {
      year.setText("");
    }
    if (multi) {
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
    if (!multi && (year.isEditable() || !noYear.isSelected())) {
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
    if (!multi && (year.isEditable() || noYear.isSelected())) {
      value = getYear();
      year.setText(value);
    }
    return value;
  }

  public String getType() {
    return type.getSelectedItem() != null ? type.getSelectedItem().toString() : "";
  }

  public void setTypeDefault() {
    type.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
  }

  public boolean runExit(boolean modify) {
    if (!name.getText().isEmpty()) {
      String erreur_txt1;
      if (!modify) {
        erreur_txt1 = Program.getError("Error144", SINGLE.withCapital());
      }	else {
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

    type.removeAllItems();
    type.addItem("");
    MyCellarBottleContenance.getList().forEach(type::addItem);
    type.setSelectedItem(MyCellarBottleContenance.getDefaultValue());

    setYearAuto();
    manageContenance.setText(Program.getLabel("Infos400"));

    initYearAndContenance();
  }

  private void initNameCombo() {
    LinkedList<String> list = new LinkedList<>();
    list.add("");
    list.addAll(Program.getStorage().getBottleNames());
    list.forEach(name::addItem);
    name.setCaseSensitive(false);
    name.setEditable(true);
  }

  public void resetModified(boolean b) {
    name.setModified(b);
    year.setModified(b);
    type.setModified(b);
  }

  public boolean isModified(IMyCellarObject myCellarObject) {
    boolean modified = name.isModified();
    modified |= year.isModified();
    modified |= (noYear.isSelected() != myCellarObject.isNonVintage());
    modified |= type.isModified();
    return modified;
  }
}
