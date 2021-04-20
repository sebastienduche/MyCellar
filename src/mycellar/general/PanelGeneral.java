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

  protected final MyCellarLabel m_contenance = new MyCellarLabel(LabelType.INFO, "134");
  protected final MyCellarButton m_manageContenance = new MyCellarButton(LabelType.INFO, "400");
  protected JCompletionComboBox<String> name;
  protected final JModifyTextField m_year = new JModifyTextField();
  protected final JModifyComboBox<String> m_half = new JModifyComboBox<>();
  protected final MyCellarCheckBox m_noYear = new MyCellarCheckBox(LabelType.INFO, "399");
  protected final MyCellarCheckBox m_annee_auto = new MyCellarCheckBox("");
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
    add(m_annee_auto);
    add(m_contenance, "wrap");
    add(name, "grow");
    add(m_year, "width min(100,10%)");
    add(m_noYear);
    add(m_half, "push");
    if (Program.isWineType()) {
      add(m_manageContenance);
    }
  }

  public String getObjectName() {
    return name.getEditor().getItem().toString();
  }

  public void initializeExtraProperties() {
    name.setSelectedItem(myCellarObject.getNom());
    m_year.setText(myCellarObject.getAnnee());
    final boolean nonVintage = myCellarObject.isNonVintage();
    m_noYear.setSelected(nonVintage);
    m_year.setEditable(!nonVintage);
    m_half.removeAllItems();
    m_half.addItem("");
    MyCellarBottleContenance.getList().forEach(m_half::addItem);
    m_half.setSelectedItem(myCellarObject.getType());

    String half_tmp = "";
    if (m_half.getSelectedItem() != null) {
      half_tmp = m_half.getSelectedItem().toString();
    }
    if (!half_tmp.equals(myCellarObject.getType()) && !myCellarObject.getType().isEmpty()) {
      MyCellarBottleContenance.getList().add(myCellarObject.getType());
      m_half.addItem(myCellarObject.getType());
      m_half.setSelectedItem(myCellarObject.getType());
    }
  }

  protected void annee_auto_actionPerformed(ActionEvent e) {
    Debug("Annee_auto_actionPerformed...");
    if (!m_annee_auto.isSelected()) {
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
    m_half.setEnabled(enable && !multi);
    name.setEnabled(enable && !multi);
    m_year.setEditable(enable && !m_noYear.isSelected());
    m_noYear.setEnabled(enable);
    m_annee_auto.setEnabled(enable);
    m_manageContenance.setEnabled(enable);
  }

  public void setEditable(boolean editable) {
    m_half.setEnabled(editable);
    name.setEditable(editable);
    m_year.setEditable(editable);
  }

  public void resetMulti(int itemCount) {
    if (multi) {
      name.setSelectedItem(MessageFormat.format(Program.getLabel("AddVin.NbItemsSelected", LabelProperty.PLURAL), itemCount)); //" bouteilles selectionnees");
      name.setEnabled(false);
      m_annee_auto.setEnabled(false);
      m_noYear.setEnabled(false);
      m_year.setEditable(false);
      if (m_half.getItemCount() > 0) {
        m_half.setSelectedIndex(0);
      }
    }
  }

  public String getYear() {

    if (m_noYear.isSelected()) {
      return Bouteille.NON_VINTAGE;
    }

    String annee = m_year.getText();
    if (m_annee_auto.isSelected() && annee.length() == 2) {
      int n = Program.getCaveConfigInt(MyCellarSettings.ANNEE, 50);
      if (Program.safeParseInt(annee, -1) > n) {
        annee = siecle + annee;
      } else {
        annee = siecle + 1 + annee;
      }
    }
    return annee;
  }

  protected final void setYearAuto() {
    m_annee_auto.setText(MessageFormat.format(Program.getLabel("Infos117"), ((siecle + 1) * 100))); //"Annee 00 -> 2000");
    m_annee_auto.setSelected(Program.getCaveConfigBool(MyCellarSettings.ANNEE_AUTO, false));
  }

  public void updateView() {
    m_half.removeAllItems();
    m_half.addItem("");
    MyCellarBottleContenance.getList().forEach(m_half::addItem);
    m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
  }

  public PanelGeneral setMyCellarObject(IMyCellarObject myCellarObject) {
    this.myCellarObject = myCellarObject;
    return this;
  }

  public void setMulti(boolean multi) {
    this.multi = multi;
  }

  public void clear() {
    name.setSelectedIndex(0);
    m_year.setText("");
  }

  protected static void Debug(String s) {
    Program.Debug("PanelGeneral: " + s);
  }

  public void setModifyActive(boolean b) {
    m_year.setModifyActive(b);
    m_half.setModifyActive(b);
  }

  public void initValues() {
    initNameCombo();

    m_half.addItem("");
    MyCellarBottleContenance.getList().forEach(m_half::addItem);
    m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());

    setYearAuto();
    initYearAndContenance();
  }

  private void initYearAndContenance() {
    m_manageContenance.addActionListener(this::manageContenance_actionPerformed);
    m_annee_auto.addActionListener(this::annee_auto_actionPerformed);

    m_noYear.addActionListener((e) -> {
      if (m_noYear.isSelected()) {
        m_year.setText(Bouteille.NON_VINTAGE);
        m_year.setEditable(false);
      }	else {
        m_year.setText("");
        m_year.setEditable(true);
      }
    });
  }

  protected void manageContenance_actionPerformed(ActionEvent e) {
    Debug("Manage Capacity...");
    new ManageCapacityAction().actionPerformed(null);
    Debug("Manage Capacity... End");
  }

  public void setMouseListener(PopupListener popup_l) {
    name.addMouseListener(popup_l);
    m_year.addMouseListener(popup_l);
  }

  public void resetValues() {
    name.removeAllItems();
    name.addItem("");
    Program.getStorage().getBottleNames().forEach(name::addItem);

    name.setEnabled(true);
    name.setEditable(true);
    if (m_noYear.isSelected()) {
      m_year.setText(Bouteille.NON_VINTAGE);
    } else {
      m_year.setText("");
    }
    if (multi) {
      if (m_half.getItemCount() > 0) {
        m_half.setSelectedIndex(0);
      }
    } else {
      if (m_half.getItemCount() > 1) {
        m_half.setSelectedIndex(1);
      }
    }
  }

  public boolean performValidation() {
    String nom = name.getEditor().getItem().toString();
    if (MyCellarControl.hasInvalidBotteName(nom)) {
      return false;
    }

    // Controle de la date
    if (!multi && (m_year.isEditable() || !m_noYear.isSelected())) {
      String annee = m_year.getText();

      // Erreur sur la date
      if (MyCellarControl.hasInvalidYear(annee)) {
        m_year.setEditable(true);
        return false;
      }
      annee = getYear();
      m_year.setText(annee);
    }
    return true;
  }

  public String updateYear() {
    String year = "";
    if (!multi && (m_year.isEditable() || m_noYear.isSelected())) {
      year = getYear();
      m_year.setText(year);
    }
    return year;
  }

  public String getType() {
    return m_half.getSelectedItem() != null ? m_half.getSelectedItem().toString() : "";
  }

  public void setTypeDefault() {
    m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());
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
      if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, erreur_txt1 + " " + Program.getError("Error145"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
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

    m_half.removeAllItems();
    m_half.addItem("");
    MyCellarBottleContenance.getList().forEach(m_half::addItem);
    m_half.setSelectedItem(MyCellarBottleContenance.getDefaultValue());

    setYearAuto();
    m_manageContenance.setText(Program.getLabel("Infos400"));

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
    m_year.setModified(b);
    m_half.setModified(b);
  }

  public boolean isModified(IMyCellarObject myCellarObject) {
    boolean modified = name.isModified();
    modified |= m_year.isModified();
    modified |= (m_noYear.isSelected() != myCellarObject.isNonVintage());
    modified |= m_half.isModified();
    return modified;
  }
}
