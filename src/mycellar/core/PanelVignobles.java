package mycellar.core;

import mycellar.Bouteille;
import mycellar.JCompletionComboBox;
import mycellar.Program;
import mycellar.Start;
import mycellar.Vignoble;
import mycellar.actions.ManageVineyardAction;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import mycellar.vignobles.Appelation;
import mycellar.vignobles.CountryVignoble;
import mycellar.vignobles.CountryVignobles;
import mycellar.vignobles.Vignobles;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mycellar.Program.NO_APPELATION;
import static mycellar.Program.NO_COUNTRY;
import static mycellar.Program.NO_VIGNOBLE;
import static mycellar.Program.toCleanString;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.8
 * @since 06/11/20
 */
public class PanelVignobles extends JPanel {

	private static final long serialVersionUID = -3053382428338158563L;

	private final JCompletionComboBox<Country> comboCountry;
	private final JCompletionComboBox<CountryVignoble> comboVignoble;
	private final JCompletionComboBox<Appelation> comboAppelationAOC;
	private final JCompletionComboBox<String> comboAppelationIGP;

	public PanelVignobles(boolean modifyActive, boolean manageButton) {

		MyCellarLabel labelCountry = new MyCellarLabel(LabelType.INFO_OTHER, "Main.Country");
		MyCellarLabel labelVignoble = new MyCellarLabel(LabelType.INFO_OTHER, "Main.Vignoble");
		MyCellarLabel labelAppelationAOC = new MyCellarLabel(LabelType.INFO_OTHER, "Main.AppelationAOC");
		MyCellarLabel labelAppelationIGP = new MyCellarLabel(LabelType.INFO_OTHER, "Main.AppelationIGP");
		setLayout(new MigLayout("","[grow][grow]",""));
		comboCountry = new JCompletionComboBox<>() {
			private static final long serialVersionUID = 8137073557763181546L;

			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive) {
					Start.setPaneModified(true);
				}
			}
		};
		comboVignoble = new JCompletionComboBox<>() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive) {
					Start.setPaneModified(true);
				}
			}
		};
		comboAppelationAOC = new JCompletionComboBox<>() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive) {
					Start.setPaneModified(true);
				}
			}
		};
		comboAppelationIGP = new JCompletionComboBox<>() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive) {
					Start.setPaneModified(true);
				}
			}
		};
		comboAppelationIGP.setCaseSensitive(false);
		comboAppelationIGP.setEditable(true);
		comboVignoble.setCaseSensitive(false);
		comboVignoble.setEditable(true);
		comboCountry.setCaseSensitive(false);
		comboCountry.setEditable(true);
		comboAppelationAOC.setCaseSensitive(false);
		comboAppelationAOC.setEditable(true);
		comboCountry.addItem(NO_COUNTRY);
		Program.getCountries().forEach(comboCountry::addItem);

		comboCountry.addItemListener((e) -> {

				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(e.getItem() instanceof String) {
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
					final Country country = (Country)e.getItem();
					comboVignoble.removeAllItems();
					comboAppelationAOC.removeAllItems();
					comboAppelationIGP.removeAllItems();
					comboVignoble.addItem(NO_VIGNOBLE);
					comboAppelationAOC.addItem(NO_APPELATION);
					comboAppelationIGP.addItem("");
					CountryVignobles.getVignobles(country)
						.ifPresent(vignobles -> vignobles.getVignoble().forEach(comboVignoble::addItem));
				}
		});

		comboVignoble.addItemListener((e) -> {

				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(e.getItem() instanceof String) {
						comboAppelationAOC.setSelectedItem(NO_APPELATION);
						comboAppelationIGP.setSelectedItem("");
						return;
					}
					final CountryVignoble vignoble = (CountryVignoble)e.getItem();
					comboAppelationAOC.removeAllItems();
					comboAppelationIGP.removeAllItems();
					comboAppelationAOC.addItem(NO_APPELATION);
					comboAppelationIGP.addItem("");
					List<String> itemsIGP = new ArrayList<>();
					for(Appelation appelation : vignoble.getSortedUnmodifiableAppelation()) {
						if(appelation.getAOC() != null && !appelation.getAOC().isEmpty()) {
							comboAppelationAOC.addItem(appelation);
						}
						if(appelation.getIGP() != null && !appelation.getIGP().isEmpty()) {
							itemsIGP.add(appelation.getIGP());
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
			if(comboCountry.getItemCount() == 0) {
				comboCountry.addItem(NO_COUNTRY);
			}
			for(Country c : Program.getCountries()) {
				if(!comboCountry.hasItem(c)) {
					comboCountry.addItem(c);
				}
			}
			Debug("Updating List Done");
		});
	}

	public void resetCombos() {
		comboCountry.setSelectedIndex(0);
		if(comboVignoble.getItemCount() > 0) {
			comboVignoble.setSelectedIndex(0);
		}
	}

	private void Debug(String text) {
		Program.Debug("PanelVignoble: " + text);
	}

	public String getCountry() {
		Object o = comboCountry.getEditor().getItem();
		if(o instanceof Country) {
			return toCleanString(((Country) o).getId());
		}
		return toCleanString(o);
	}

	public void setModified(boolean modified) {
		comboCountry.setModified(modified);
		comboVignoble.setModified(modified);
		comboAppelationAOC.setModified(modified);
		comboAppelationIGP.setModified(modified);
	}

	public boolean isModified() {
		boolean modified = comboCountry.isModified();
		modified |= comboVignoble.isModified();
		modified |= comboAppelationAOC.isModified();
		modified |= comboAppelationIGP.isModified();
		return modified;
	}

	void resetCountrySelected() {
		comboCountry.setSelectedIndex(0);
	}

	public String getVignoble() {
		Object o = comboVignoble.getEditor().getItem();
		if (o instanceof CountryVignoble) {
			return toCleanString(((CountryVignoble) o).getName());
		}
		return toCleanString(o);
	}

	public String getAOC() {
		Object o = comboAppelationAOC.getEditor().getItem();
		if (o instanceof Appelation) {
			return toCleanString(((Appelation) o).getAOC());
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
		Vignoble vignoble = bottle.getVignoble();
		if (vignoble == null) {
			return;
		}

		Country country = null;
		if (Program.FRANCE.getId().equals(vignoble.country) || "fr".equals(vignoble.country)) {
			country = Program.FRANCE;
		} else if (vignoble.country != null) {
			country = Countries.findByIdOrLabel(vignoble.country);
		}

		Vignobles vignobles = null;
		if (country != null) {
			comboCountry.setSelectedItem(country);
			vignobles = CountryVignobles.getVignobles(country).orElse(null);
		}

		if (vignobles != null) {
			Optional<CountryVignoble> countryVignoble = vignobles.findVignoble(vignoble);
			if (countryVignoble.isPresent()) {
				comboVignoble.setSelectedItem(countryVignoble.get());
			} else {
				Debug("ERROR: Unable to find vignoble: " + vignoble.getName());
			}
		}

		if (vignoble.getAOC() != null) {
			comboAppelationAOC.setSelectedItem(vignoble.getAOC());
		}

		if (vignoble.getIGP() != null) {
			comboAppelationIGP.setSelectedItem(vignoble.getIGP());
		}
	}

	public Vignoble getSelectedVignoble() {
		Vignoble vignoble = new Vignoble();
		vignoble.setCountry(getCountry());
		vignoble.setName(getVignoble());
		vignoble.setAOC(getAOC());
		vignoble.setIGP(getIGP());
		return vignoble;
	}
}
