package mycellar.general;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.uicomponents.JModifyComboBox;
import mycellar.core.uicomponents.JModifyFormattedTextField;
import mycellar.core.uicomponents.JModifyTextField;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.core.uicomponents.PopupListener;
import mycellar.core.common.bottle.BottleColor;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.NumberFormat;

import static mycellar.ProgramConstants.CHAR_COMMA;
import static mycellar.ProgramConstants.CHAR_DOT;
import static mycellar.ProgramConstants.EURO;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 22/10/21
 */
public final class PanelWineAttribute extends JPanel {
  private static final long serialVersionUID = 183053076444982489L;

  private final MyCellarLabel labelStillToAdd = new MyCellarLabel("");
  private final MyCellarLabel lastModified = new MyCellarLabel("");

  private final JModifyFormattedTextField price = new JModifyFormattedTextField(NumberFormat.getNumberInstance());
  private final JModifyTextField maturity = new JModifyTextField();
  private final JModifyTextField parker = new JModifyTextField();
  private final JModifyComboBox<BottleColor> colorList = new JModifyComboBox<>();
  private final MyCellarSpinner nbItems = new MyCellarSpinner(1, 999);
  private final JModifyComboBox<BottlesStatus> statusList = new JModifyComboBox<>();

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

    setLayout(new MigLayout("", "[]30px[]30px[]", ""));
    add(new MyCellarLabel(LabelType.INFO, "391"));
    add(new MyCellarLabel(LabelType.INFO, "392"));
    add(new MyCellarLabel(LabelType.INFO_OTHER, "AddVin.Color"), "wrap");
    add(maturity, "width min(200,40%)");
    add(parker, "width min(150,30%)");
    add(colorList, "wrap, width min(150,30%)");
    add(new MyCellarLabel(LabelType.INFO, "135"), "wrap");
    add(price, "width min(100,45%), split 2");
    add(new MyCellarLabel(Program.getCaveConfigString(MyCellarSettings.DEVISE, EURO)), "gapleft 5px");
    add(new MyCellarLabel(LabelType.INFO, "405", LabelProperty.PLURAL), "split, span 2");
    add(nbItems, "width min(50,10%)");
    add(labelStillToAdd, "wrap");
    add(new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.status"));
    add(new MyCellarLabel(LabelType.INFO_OTHER, "MyCellarManageBottles.lastModified"), "wrap");
    add(statusList, "width min(150,30%)");
    add(lastModified);
  }

  public void initializeExtraProperties(MyCellarObject myCellarObject, boolean m_bmulti, boolean isEditionMode) {
    enableAll(true, m_bmulti, isEditionMode);
    nbItems.setValue(1);
    nbItems.setEnabled(false);

    price.setText(Program.convertStringFromHTMLString(myCellarObject.getPrix()));
    if (Program.isWineType()) {
      Bouteille bottle = (Bouteille) myCellarObject;
      maturity.setText(bottle.getMaturity());
      parker.setText(bottle.getParker());
      colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
    }
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
    price.setActive(true);
    maturity.setActive(true);
    parker.setActive(true);
    colorList.setActive(true);
    statusList.setActive(true);
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
        if (e.getKeyChar() == CHAR_COMMA || e.getKeyChar() == CHAR_DOT) {
          e.consume();
          char sep = Program.getDecimalSeparator();
          String text = price.getText();
          price.setText(text + sep);
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
    price.setModified(false);
    maturity.setModified(false);
    parker.setModified(false);
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

  public String getPriceIfModified() {
    if (price.isModified()) {
      return price.getText();
    }
    return null;
  }

  public String getMaturity() {
    return maturity.getText();
  }

  public String getMaturityIfModified() {
    if (maturity.isModified()) {
      return maturity.getText();
    }
    return null;
  }

  public String getParker() {
    return parker.getText();
  }
  public String getParkerIfModified() {
    if (parker.isModified()) {
      return parker.getText();
    }
    return null;
  }

  public String getColor() {
    if (colorList.getSelectedItem() != null) {
      return ((BottleColor) colorList.getSelectedItem()).name();
    }
    return BottleColor.NONE.name();
  }

  public String getColorIfModified() {
    if (colorList.isModified() && colorList.getSelectedItem() != null) {
      return ((BottleColor) colorList.getSelectedItem()).name();
    }
    return null;
  }

  public String getStatusIfModified() {
    if (statusList.isModified() && statusList.getSelectedItem() != null) {
      return ((BottlesStatus) statusList.getSelectedItem()).name();
    }
    return null;
  }

  public void setStatus(IMyCellarObject myCellarObject) {
    statusList.setSelectedItem(BottlesStatus.getStatus(myCellarObject.getStatus()));
    lastModified.setText(myCellarObject.getLastModified());
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
