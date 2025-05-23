package mycellar.general;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarSettings;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.uicomponents.JModifyComboBox;
import mycellar.core.uicomponents.JModifyFormattedTextField;
import mycellar.core.uicomponents.JModifyTextField;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import mycellar.core.uicomponents.PopupListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.text.NumberFormat;

import static mycellar.MyCellarUtils.convertStringFromHTMLString;
import static mycellar.ProgramConstants.CHAR_COMMA;
import static mycellar.ProgramConstants.CHAR_DOT;
import static mycellar.ProgramConstants.EURO;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.ADDVIN_COLOR;
import static mycellar.general.ResourceKey.ADDVIN_NBITEMSTOADD;
import static mycellar.general.ResourceKey.ADDVIN_STILL1TOADD;
import static mycellar.general.ResourceKey.ADDVIN_STILLNTOADD;
import static mycellar.general.ResourceKey.MAIN_MATURITY;
import static mycellar.general.ResourceKey.MAIN_NUMBEROFITEMS;
import static mycellar.general.ResourceKey.MAIN_PRICE;
import static mycellar.general.ResourceKey.MAIN_RATING;
import static mycellar.general.ResourceKey.MYCELLARMANAGEBOTTLES_LASTMODIFIED;
import static mycellar.general.ResourceKey.MYCELLARMANAGEBOTTLES_STATUS;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.8
 * @since 21/03/25
 */
public final class PanelWineAttribute extends JPanel {
  @Serial
  private static final long serialVersionUID = 183053076444982489L;

  private final MyCellarSimpleLabel labelStillToAdd = new MyCellarSimpleLabel();
  private final MyCellarSimpleLabel lastModified = new MyCellarSimpleLabel();

  private final JModifyFormattedTextField price = new JModifyFormattedTextField(NumberFormat.getNumberInstance());
  private final JModifyTextField maturity = new JModifyTextField();
  private final JModifyTextField parker = new JModifyTextField();
  private final JModifyComboBox<BottleColor> colorList = new JModifyComboBox<>();
  private final MyCellarSpinner nbItems = new MyCellarSpinner(1, 999);
  private final JModifyComboBox<BottlesStatus> statusList = new JModifyComboBox<>();

  public PanelWineAttribute() {
    setModificationDetectionActive(false);
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
    add(new MyCellarLabel(MAIN_MATURITY));
    add(new MyCellarLabel(MAIN_RATING));
    add(new MyCellarLabel(ADDVIN_COLOR), "wrap");
    add(maturity, "width min(200,40%)");
    add(parker, "width min(150,30%)");
    add(colorList, "wrap, width min(150,30%)");
    add(new MyCellarLabel(MAIN_PRICE), "wrap");
    add(price, "width min(100,45%), split 2");
    add(new MyCellarSimpleLabel(Program.getCaveConfigString(MyCellarSettings.DEVISE, EURO)), "gapleft 5px");
    add(new MyCellarLabel(MAIN_NUMBEROFITEMS, ""), "split, span 2");
    add(nbItems, "width min(50,10%)");
    add(labelStillToAdd, "wrap");
    add(new MyCellarLabel(MYCELLARMANAGEBOTTLES_STATUS));
    add(new MyCellarLabel(MYCELLARMANAGEBOTTLES_LASTMODIFIED), "wrap");
    add(statusList, "width min(150,30%)");
    add(lastModified);
    setModificationDetectionActive(true);
  }

  public void setModificationDetectionActive(boolean active) {
    price.setActive(active);
    maturity.setActive(active);
    parker.setActive(active);
    colorList.setActive(active);
    statusList.setActive(active);
  }

  public void initializeExtraProperties(IMyCellarObject myCellarObject, boolean m_bmulti, boolean isEditionMode) {
    setModificationDetectionActive(false);
    enableAll(true, m_bmulti, isEditionMode);
    nbItems.setValue(1);
    nbItems.setEnabled(false);

    price.setText(convertStringFromHTMLString(myCellarObject.getPrix()));
    if (Program.isWineType()) {
      Bouteille bottle = (Bouteille) myCellarObject;
      maturity.setText(bottle.getMaturity());
      parker.setText(bottle.getParker());
      colorList.setSelectedItem(BottleColor.getColor(bottle.getColor()));
    }
    setModificationDetectionActive(true);
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
    setModificationDetectionActive(false);
    parker.setText("");
    price.setText("");
    maturity.setText("");
    nbItems.setValue(1);
    setModificationDetectionActive(true);
  }

  public void initValues() {
    nbItems.setToolTipText(getLabel(ADDVIN_NBITEMSTOADD));
    nbItems.setValue(1);
    labelStillToAdd.setForeground(Color.red);
    nbItems.addChangeListener((e) -> {
      labelStillToAdd.setText("");
      if (nbItems.getIntValue() < 1) {
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
    price.reset();
    maturity.reset();
    parker.reset();
    colorList.reset();
    statusList.reset();
    lastModified.setText("");
    nbItems.setValue(1);
    labelStillToAdd.setText("");
  }

  public void updateStatusAndTime(IMyCellarObject bottle) {
    statusList.setSelectedItem(BottlesStatus.getStatus(bottle.getStatus()));
    lastModified.setText(bottle.getLastModified());
  }

  public void initStatusAndTime(IMyCellarObject bottle) {
    setModificationDetectionActive(false);
    statusList.setSelectedItem(BottlesStatus.getStatus(bottle.getStatus()));
    lastModified.setText(bottle.getLastModified());
    setModificationDetectionActive(true);
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
    setModificationDetectionActive(false);
    colorList.setSelectedItem(BottleColor.NONE);
    statusList.setSelectedItem(BottlesStatus.NONE);
    setModificationDetectionActive(true);
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
    labelStillToAdd.setText(getLabel(count > 1 ? ADDVIN_STILLNTOADD : ADDVIN_STILL1TOADD, count));
  }

  public void seNbItemsEnabled(boolean b) {
    nbItems.setEnabled(b);
  }
}
