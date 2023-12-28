package mycellar.core;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.actions.ManageVineyardAction;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.datas.jaxb.VignobleListJaxb;
import mycellar.core.uicomponents.JCompletionComboBox;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarCheckBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.general.ProgramPanels;
import mycellar.vignobles.CountryVignobleController;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mycellar.MyCellarUtils.toCleanString;
import static mycellar.Program.NO_APPELATION;
import static mycellar.Program.NO_COUNTRY;
import static mycellar.Program.NO_VIGNOBLE;
import static mycellar.ProgramConstants.FR;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2017
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.9
 * @since 25/12/23
 */
public final class PanelVignobles extends JPanel {

  private final JCompletionComboBox<CountryJaxb> comboCountry;
  private final JCompletionComboBox<CountryVignobleJaxb> comboVignoble;
  private final JCompletionComboBox<AppelationJaxb> comboAppellationAOC;
  private final JCompletionComboBox<String> comboAppellationIGP;
  private final MyCellarCheckBox keepPreviousVignoble;

  public PanelVignobles(boolean modifyActive, boolean manageButton, boolean editable) {

    MyCellarLabel labelCountry = new MyCellarLabel("Main.Country");
    MyCellarLabel labelVignoble = new MyCellarLabel("Main.Vineyard");
    MyCellarLabel labelAppellationAOC = new MyCellarLabel("Main.AppelationAOC");
    MyCellarLabel labelAppellationIGP = new MyCellarLabel("Main.AppelationIGP");
    setLayout(new MigLayout("", "[grow][grow]", ""));
    comboCountry = new JCompletionComboBox<>() {
      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboVignoble = new JCompletionComboBox<>() {

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboAppellationAOC = new JCompletionComboBox<>() {

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboAppellationIGP = new JCompletionComboBox<>() {

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboAppellationIGP.setCaseSensitive(false);
    comboVignoble.setCaseSensitive(false);
    comboCountry.setCaseSensitive(false);
    comboAppellationAOC.setCaseSensitive(false);

    keepPreviousVignoble = new MyCellarCheckBox("PanelVignobles.keepValues", true);

    comboAppellationIGP.setEditable(editable);
    comboVignoble.setEditable(editable);
    comboCountry.setEditable(editable);
    comboAppellationAOC.setEditable(editable);
    comboCountry.addItem(NO_COUNTRY);
    Program.getCountries().forEach(comboCountry::addItem);

    comboCountry.addItemListener((e) -> {

      if (e.getStateChange() == ItemEvent.SELECTED) {
        if (e.getItem() instanceof String) {
          comboVignoble.removeAllItems();
          comboAppellationAOC.removeAllItems();
          comboAppellationIGP.removeAllItems();
          comboVignoble.addItem(NO_VIGNOBLE);
          comboAppellationAOC.addItem(NO_APPELATION);
          comboAppellationIGP.addItem("");
          comboVignoble.setSelectedItem("");
          comboAppellationAOC.setSelectedItem(NO_APPELATION);
          comboAppellationIGP.setSelectedItem("");
        } else if (e.getItem() instanceof CountryJaxb countryJaxb) {
          comboVignoble.removeAllItems();
          comboAppellationAOC.removeAllItems();
          comboAppellationIGP.removeAllItems();
          comboVignoble.addItem(NO_VIGNOBLE);
          comboAppellationAOC.addItem(NO_APPELATION);
          comboAppellationIGP.addItem("");
          CountryVignobleController.getVignobles(countryJaxb)
              .ifPresent(vignobleListJaxb -> vignobleListJaxb.getCountryVignobleJaxbList()
                  .forEach(comboVignoble::addItem));
        }
      }
    });

    comboVignoble.addItemListener((e) -> {

      if (e.getStateChange() == ItemEvent.SELECTED) {
        if (e.getItem() instanceof String) {
          comboAppellationAOC.setSelectedItem(NO_APPELATION);
          comboAppellationIGP.setSelectedItem("");
        } else if (e.getItem() instanceof CountryVignobleJaxb vignoble) {
          comboAppellationAOC.removeAllItems();
          comboAppellationIGP.removeAllItems();
          comboAppellationAOC.addItem(NO_APPELATION);
          comboAppellationIGP.addItem("");
          List<String> itemsIGP = new ArrayList<>();
          for (AppelationJaxb appelationJaxb : vignoble.getSortedUnmodifiableAppelation()) {
            if (appelationJaxb.getAOC() != null && !appelationJaxb.getAOC().isEmpty()) {
              comboAppellationAOC.addItem(appelationJaxb);
            }
            if (appelationJaxb.getIGP() != null && !appelationJaxb.getIGP().isEmpty()) {
              itemsIGP.add(appelationJaxb.getIGP());
            }
          }
          Collections.sort(itemsIGP);
          itemsIGP.forEach(comboAppellationIGP::addItem);
        }
      }
    });

    MyCellarButton manageVineyardButton = new MyCellarButton("Main.VineyardManagement", new ManageVineyardAction());
    manageVineyardButton.setVisible(manageButton);
    keepPreviousVignoble.setVisible(manageButton);
    add(keepPreviousVignoble, "wrap");
    add(labelCountry, "w 150:150:150, split 2");
    add(labelVignoble, "wrap");
    add(comboCountry, "w 150:150:, split 2");
    add(comboVignoble, "w 200:200:, grow");
    add(manageVineyardButton, "alignx right, wrap");

    add(labelAppellationAOC);
    add(labelAppellationIGP, "wrap");
    add(comboAppellationAOC, "w 200:200:, growx");
    add(comboAppellationIGP, "w 200:200:");

    setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getLabel("Main.Vineyard")));
  }

  public void updateList() {
    SwingUtilities.invokeLater(() -> {
      Debug("Updating List...");
      if (comboCountry.getItemCount() == 0) {
        comboCountry.addItem(NO_COUNTRY);
      }
      for (CountryJaxb c : Program.getCountries()) {
        if (!comboCountry.hasItem(c)) {
          comboCountry.addItem(c);
        }
      }
      Debug("Updating List Done");
    });
  }

  public void resetCombos() {
    if (keepPreviousVignoble.isVisible() && keepPreviousVignoble.isSelected()) {
      return;
    }
    comboCountry.setSelectedIndex(0);
    if (comboVignoble.getItemCount() > 0) {
      comboVignoble.setSelectedIndex(0);
    }
  }

  public boolean isKeepPreviousVineyardSelected() {
    return keepPreviousVignoble.isSelected();
  }

  public void setKeepPreviousVineyardSelected(boolean selected) {
    keepPreviousVignoble.setSelected(selected);
  }

  private void Debug(String text) {
    Program.Debug("PanelVignoble: " + text);
  }

  public String getCountry() {
    Object o = comboCountry.getEditor().getItem();
    if (o instanceof CountryJaxb countryJaxb) {
      return toCleanString(countryJaxb.getId());
    }
    return toCleanString(o);
  }

  public boolean isModified() {
    boolean modified = comboCountry.isModified();
    modified |= comboVignoble.isModified();
    modified |= comboAppellationAOC.isModified();
    modified |= comboAppellationIGP.isModified();
    return modified;
  }

  public void setModified(boolean modified) {
    comboCountry.setModified(modified);
    comboVignoble.setModified(modified);
    comboAppellationAOC.setModified(modified);
    comboAppellationIGP.setModified(modified);
  }

  void resetCountrySelected() {
    comboCountry.setSelectedIndex(0);
  }

  public String getVignoble() {
    Object o = comboVignoble.getEditor().getItem();
    if (o instanceof CountryVignobleJaxb countryVignobleJaxb) {
      return toCleanString(countryVignobleJaxb.getName());
    }
    return toCleanString(o);
  }

  public String getAOC() {
    Object o = comboAppellationAOC.getEditor().getItem();
    if (o instanceof AppelationJaxb appelationJaxb) {
      return toCleanString(appelationJaxb.getAOC());
    }
    return toCleanString(o);
  }

  public String getIGP() {
    return comboAppellationIGP.getEditor().getItem().toString();
  }

  void enableAll(boolean enable) {
    comboCountry.setEnabled(enable);
    comboVignoble.setEnabled(enable);
    comboAppellationAOC.setEnabled(enable);
    comboAppellationIGP.setEnabled(enable);
  }

  public void initializeVignobles(final Bouteille bottle) {
    VignobleJaxb vignobleJaxb = bottle.getVignoble();
    if (vignobleJaxb == null) {
      return;
    }

    CountryJaxb countryJaxb = null;
    if (Program.FRANCE.getId().equals(vignobleJaxb.country) || FR.equals(vignobleJaxb.country)) {
      countryJaxb = Program.FRANCE;
    } else if (vignobleJaxb.country != null) {
      countryJaxb = CountryListJaxb.findByIdOrLabel(vignobleJaxb.country);
    }

    VignobleListJaxb vignobleListJaxb = null;
    if (countryJaxb != null) {
      comboCountry.setSelectedItem(countryJaxb);
      vignobleListJaxb = CountryVignobleController.getVignobles(countryJaxb).orElse(null);
    }

    if (vignobleListJaxb != null) {
      vignobleListJaxb.findVignoble(vignobleJaxb).ifPresentOrElse(comboVignoble::setSelectedItem,
          () -> Debug("ERROR: Unable to find vignoble: " + vignobleJaxb.getName()));
    }

    if (vignobleJaxb.getAOC() != null) {
      comboAppellationAOC.setSelectedItem(vignobleJaxb.getAOC());
    }

    if (vignobleJaxb.getIGP() != null) {
      comboAppellationIGP.setSelectedItem(vignobleJaxb.getIGP());
    }
    comboCountry.setModified(false);
    comboVignoble.setModified(false);
    comboAppellationAOC.setModified(false);
    comboAppellationIGP.setModified(false);
  }

  public VignobleJaxb getSelectedVignoble() {
    VignobleJaxb vignobleJaxb = new VignobleJaxb();
    vignobleJaxb.setCountry(getCountry());
    vignobleJaxb.setName(getVignoble());
    vignobleJaxb.setAOC(getAOC());
    vignobleJaxb.setIGP(getIGP());
    return vignobleJaxb;
  }
}
