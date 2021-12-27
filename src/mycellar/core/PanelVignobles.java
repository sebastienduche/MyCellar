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

import static mycellar.Program.NO_APPELATION;
import static mycellar.Program.NO_COUNTRY;
import static mycellar.Program.NO_VIGNOBLE;
import static mycellar.Program.toCleanString;
import static mycellar.ProgramConstants.FR;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 22/10/21
 */
public final class PanelVignobles extends JPanel {

  private static final long serialVersionUID = -3053382428338158563L;

  private final JCompletionComboBox<CountryJaxb> comboCountry;
  private final JCompletionComboBox<CountryVignobleJaxb> comboVignoble;
  private final JCompletionComboBox<AppelationJaxb> comboAppelationAOC;
  private final JCompletionComboBox<String> comboAppelationIGP;

  public PanelVignobles(boolean modifyActive, boolean manageButton, boolean editable) {

    MyCellarLabel labelCountry = new MyCellarLabel(LabelType.INFO_OTHER, "Main.Country");
    MyCellarLabel labelVignoble = new MyCellarLabel(LabelType.INFO_OTHER, "Main.Vignoble");
    MyCellarLabel labelAppelationAOC = new MyCellarLabel(LabelType.INFO_OTHER, "Main.AppelationAOC");
    MyCellarLabel labelAppelationIGP = new MyCellarLabel(LabelType.INFO_OTHER, "Main.AppelationIGP");
    setLayout(new MigLayout("", "[grow][grow]", ""));
    comboCountry = new JCompletionComboBox<>() {
      private static final long serialVersionUID = 8137073557763181546L;

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboVignoble = new JCompletionComboBox<>() {
      private static final long serialVersionUID = 8137073557763181546L;

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboAppelationAOC = new JCompletionComboBox<>() {
      private static final long serialVersionUID = 8137073557763181546L;

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboAppelationIGP = new JCompletionComboBox<>() {
      private static final long serialVersionUID = 8137073557763181546L;

      @Override
      protected void doAfterModify() {
        super.doAfterModify();
        if (modifyActive) {
          ProgramPanels.setSelectedPaneModified(true);
        }
      }
    };
    comboAppelationIGP.setCaseSensitive(false);
    comboVignoble.setCaseSensitive(false);
    comboCountry.setCaseSensitive(false);
    comboAppelationAOC.setCaseSensitive(false);

    comboAppelationIGP.setEditable(editable);
    comboVignoble.setEditable(editable);
    comboCountry.setEditable(editable);
    comboAppelationAOC.setEditable(editable);
    comboCountry.addItem(NO_COUNTRY);
    Program.getCountries().forEach(comboCountry::addItem);

    comboCountry.addItemListener((e) -> {

      if (e.getStateChange() == ItemEvent.SELECTED) {
        if (e.getItem() instanceof String) {
          comboVignoble.removeAllItems();
          comboAppelationAOC.removeAllItems();
          comboAppelationIGP.removeAllItems();
          comboVignoble.addItem(NO_VIGNOBLE);
          comboAppelationAOC.addItem(NO_APPELATION);
          comboAppelationIGP.addItem("");
          comboVignoble.setSelectedItem("");
          comboAppelationAOC.setSelectedItem(NO_APPELATION);
          comboAppelationIGP.setSelectedItem("");
          return;
        }
        final CountryJaxb countryJaxb = (CountryJaxb) e.getItem();
        comboVignoble.removeAllItems();
        comboAppelationAOC.removeAllItems();
        comboAppelationIGP.removeAllItems();
        comboVignoble.addItem(NO_VIGNOBLE);
        comboAppelationAOC.addItem(NO_APPELATION);
        comboAppelationIGP.addItem("");
        CountryVignobleController.getVignobles(countryJaxb)
            .ifPresent(vignobleListJaxb -> vignobleListJaxb.getCountryVignobleJaxbList()
                .forEach(comboVignoble::addItem));
      }
    });

    comboVignoble.addItemListener((e) -> {

      if (e.getStateChange() == ItemEvent.SELECTED) {
        if (e.getItem() instanceof String) {
          comboAppelationAOC.setSelectedItem(NO_APPELATION);
          comboAppelationIGP.setSelectedItem("");
          return;
        }
        final CountryVignobleJaxb vignoble = (CountryVignobleJaxb) e.getItem();
        comboAppelationAOC.removeAllItems();
        comboAppelationIGP.removeAllItems();
        comboAppelationAOC.addItem(NO_APPELATION);
        comboAppelationIGP.addItem("");
        List<String> itemsIGP = new ArrayList<>();
        for (AppelationJaxb appelationJaxb : vignoble.getSortedUnmodifiableAppelation()) {
          if (appelationJaxb.getAOC() != null && !appelationJaxb.getAOC().isEmpty()) {
            comboAppelationAOC.addItem(appelationJaxb);
          }
          if (appelationJaxb.getIGP() != null && !appelationJaxb.getIGP().isEmpty()) {
            itemsIGP.add(appelationJaxb.getIGP());
          }
        }
        Collections.sort(itemsIGP);
        itemsIGP.forEach(comboAppelationIGP::addItem);
      }
    });

    MyCellarButton manageVineyardButton = new MyCellarButton(LabelType.INFO, "165", new ManageVineyardAction());
    manageVineyardButton.setVisible(manageButton);
    add(labelCountry, "w 150:150:150, split 2");
    add(labelVignoble, "wrap");
    add(comboCountry, "w 150:150:, split 2");
    add(comboVignoble, "w 200:200:, grow");
    add(manageVineyardButton, "alignx right, wrap");

    add(labelAppelationAOC);
    add(labelAppelationIGP, "wrap");
    add(comboAppelationAOC, "w 200:200:, growx");
    add(comboAppelationIGP, "w 200:200:");

    setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("Main.Vignoble")));
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
    comboCountry.setSelectedIndex(0);
    if (comboVignoble.getItemCount() > 0) {
      comboVignoble.setSelectedIndex(0);
    }
  }

  private void Debug(String text) {
    Program.Debug("PanelVignoble: " + text);
  }

  public String getCountry() {
    Object o = comboCountry.getEditor().getItem();
    if (o instanceof CountryJaxb) {
      return toCleanString(((CountryJaxb) o).getId());
    }
    return toCleanString(o);
  }

  public boolean isModified() {
    boolean modified = comboCountry.isModified();
    modified |= comboVignoble.isModified();
    modified |= comboAppelationAOC.isModified();
    modified |= comboAppelationIGP.isModified();
    return modified;
  }

  public void setModified(boolean modified) {
    comboCountry.setModified(modified);
    comboVignoble.setModified(modified);
    comboAppelationAOC.setModified(modified);
    comboAppelationIGP.setModified(modified);
  }

  void resetCountrySelected() {
    comboCountry.setSelectedIndex(0);
  }

  public String getVignoble() {
    Object o = comboVignoble.getEditor().getItem();
    if (o instanceof CountryVignobleJaxb) {
      return toCleanString(((CountryVignobleJaxb) o).getName());
    }
    return toCleanString(o);
  }

  public String getAOC() {
    Object o = comboAppelationAOC.getEditor().getItem();
    if (o instanceof AppelationJaxb) {
      return toCleanString(((AppelationJaxb) o).getAOC());
    }
    return toCleanString(o);
  }

  public String getIGP() {
    return comboAppelationIGP.getEditor().getItem().toString();
  }

  void enableAll(boolean enable) {
    comboCountry.setEnabled(enable);
    comboVignoble.setEnabled(enable);
    comboAppelationAOC.setEnabled(enable);
    comboAppelationIGP.setEnabled(enable);
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
      comboAppelationAOC.setSelectedItem(vignobleJaxb.getAOC());
    }

    if (vignobleJaxb.getIGP() != null) {
      comboAppelationIGP.setSelectedItem(vignobleJaxb.getIGP());
    }
    comboCountry.setModified(false);
    comboVignoble.setModified(false);
    comboAppelationAOC.setModified(false);
    comboAppelationIGP.setModified(false);
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
