package mycellar.general;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.JModifyComboBox;
import mycellar.core.JModifyFormattedTextField;
import mycellar.core.JModifyTextField;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import mycellar.core.MyCellarSpinner;
import mycellar.core.PopupListener;
import mycellar.core.common.bottle.BottleColor;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 20/04/21
 */
public final class PanelWineAttribute extends JPanel {
  private static final long serialVersionUID = 183053076444982489L;

  protected final MyCellarLabel labelStillToAdd = new MyCellarLabel("");
  protected final MyCellarLabel lastModified = new MyCellarLabel("");

  protected final JModifyFormattedTextField price = new JModifyFormattedTextField(NumberFormat.getNumberInstance());
  protected final JModifyTextField maturity = new JModifyTextField();
  protected final JModifyTextField parker = new JModifyTextField();
  protected final JModifyComboBox<BottleColor> colorList = new JModifyComboBox<>();
  protected final MyCellarSpinner nbItems = new MyCellarSpinner(1, 999);
  protected final JModifyComboBox<BottlesStatus> statusList = new JModifyComboBox<>();

  public PanelWineAttribute() {
    colorList.addItem(BottleColor.NONE);
    colorList.addItem(BottleColor.RED);
    colorList.addItem(BottleColor.PINK);
    colorList.addItem(BottleColor.WHITE);

    statusList.addItem(BottlesStatus.NONE);
    statusList.addItem(BottlesStatus.CREATED);
    statusList.addItem(BottlesStatus.MODIFIED);
    statusList.addItem(BottlesStatus.VERIFIED);
    statusList.addItem(BottlesStatus.TOCHECK);

    setLayout(new MigLayout("","[]30px[]30px[]",""));
    add(new MyCellarLabel(LabelType.INFO, "391"));
    add(new MyCellarLabel(LabelType.INFO, "392"));
    add(new MyCellarLabel(LabelType.INFO_OTHER, "AddVin.Color"),"wrap");
    add(maturity,"width min(200,40%)");
    add(parker,"width min(150,30%)");
    add(colorList,"wrap, width min(150,30%)");
    add(new MyCellarLabel(LabelType.INFO, "135"),"wrap");
    add(price,"width min(100,45%), split 2");
    add(new MyCellarLabel(Program.getCaveConfigString(MyCellarSettings.DEVISE, "€")),"gapleft 5px");
    add(new MyCellarLabel(LabelType.INFO, "405", LabelProperty.PLURAL),"split, span 2");
    add(nbItems,"width min(50,10%)");
    add(labelStillToAdd,"wrap");
    add(new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.status"));
    add(new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.lastModified"), "wrap");
    add(statusList, "width min(150,30%)");
    add(lastModified);
  }

  public void initializeExtraProperties(Bouteille bottle, boolean m_bmulti, boolean isEditionMode) {
    enableAll(true, m_bmulti, isEditionMode);
    nbItems.setValue(1);
    nbItems.setEnabled(false);

    price.setText(Program.convertStringFromHTMLString(bottle.getPrix()));
    maturity.setText(bottle.getMaturity());
    parker.setText(bottle.getParker());
    colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
  }

  public void enableAll(boolean enable, boolean multi, boolean isEditionMode) {
    price.setEditable(enable);
    maturity.setEditable(enable);
    parker.setEditable(enable);
    colorList.setEnabled(enable);
    statusList.setEnabled(enable);
    nbItems.setEnabled(enable && !multi && !isEditionMode);
  }

  public void clearValues() {
    parker.setText("");
    price.setText("");
    maturity.setText("");
    nbItems.setValue(1);
  }

  public void setModifyActive() {
    price.setModifyActive(false);
    maturity.setModifyActive(false);
    parker.setModifyActive(false);
    colorList.setModifyActive(true);
    statusList.setModifyActive(true);
  }

  public void initValues() {
    nbItems.setToolTipText(Program.getLabel("AddVin.NbItemsToAdd", LabelProperty.PLURAL));
    nbItems.setValue(1);
    labelStillToAdd.setForeground(Color.red);
    nbItems.addChangeListener((e) -> {
      labelStillToAdd.setText("");
      if (Integer.parseInt(nbItems.getValue().toString()) <= 0) {
        nbItems.setValue(1);
      }
    });

    price.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == ',' || e.getKeyChar() == '.') {
          e.consume();
          char sep = Program.getDecimalSeparator();
          String text = price.getText();
          price.setText(text+sep);
        }
      }
    });
  }

  public void setMouseListener(PopupListener popup_l) {
    price.addMouseListener(popup_l);
    maturity.addMouseListener(popup_l);
    parker.addMouseListener(popup_l);
  }

  public void resetValues() {
    price.setText("");
    maturity.setText("");
    parker.setText("");
    colorList.setSelectedItem(BottleColor.NONE);
    statusList.setSelectedItem(BottlesStatus.NONE);
    lastModified.setText("");
    colorList.setModified(false);
    statusList.setModified(false);
    nbItems.setValue(1);
    labelStillToAdd.setText("");
  }

  public void updateStatusAndTime(IMyCellarObject bottle) {
    statusList.setSelectedItem(BottlesStatus.getStatus(bottle.getStatus()));
    lastModified.setText(bottle.getLastModified());
  }

  public String getPrice() {
    return price.getText();
  }

  public String getMaturity() {
    return maturity.getText();
  }

  public String getParker() {
    return  parker.getText();
  }

  public String getColor() {
    if (colorList.getSelectedItem() != null) {
      return ((BottleColor) colorList.getSelectedItem()).name();
    }
    return "";
  }

  public String getStatus() {
    if (statusList.isModified() && statusList.getSelectedItem() != null) {
      return ((BottlesStatus)statusList.getSelectedItem()).name();
    }
    return BottlesStatus.MODIFIED.name();
  }

  public void resetModified(boolean b) {
    maturity.setModified(b);
    parker.setModified(b);
    colorList.setModified(b);
    statusList.setModified(b);
    price.setModified(b);
  }

  public boolean isModified() {
    boolean modified = maturity.isModified();
    modified |= parker.isModified();
    modified |= colorList.isModified();
    modified |= statusList.isModified();
    modified |= price.isModified();
    return modified;
  }

  public void runExit() {
    colorList.setSelectedItem(BottleColor.NONE);
    statusList.setSelectedItem(BottlesStatus.NONE);
  }

  public void setStatus(IMyCellarObject myCellarObject) {
    statusList.setSelectedItem(BottlesStatus.getStatus(myCellarObject.getStatus()));
    lastModified.setText(myCellarObject.getLastModified());
  }

  public int getNbItems() {
    return Integer.parseInt(nbItems.getValue().toString());
  }

  public JModifyComboBox<BottleColor> getColorList() {
    return colorList;
  }
  public JModifyComboBox<BottlesStatus> getStatusList() {
    return statusList;
  }

  public void setEditable(boolean b) {
    price.setEditable(b);
    maturity.setEditable(b);
    parker.setEditable(b);
    colorList.setEditable(b);
    statusList.setEditable(b);
  }

  public void setStillNbItems(int count) {
    nbItems.setValue(count);
    labelStillToAdd.setText(MessageFormat.format(Program.getLabel("AddVin.stillNtoAdd", new LabelProperty(count > 1)), count));
  }

  public void seNbItemsEnabled(boolean b) {
    nbItems.setEnabled(b);
  }
}