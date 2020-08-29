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

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 29/08/20
 */
public class PanelVignobles extends JPanel {

	private static final long serialVersionUID = -3053382428338158563L;

	private final JCompletionComboBox comboCountry;
	private final JCompletionComboBox comboVignoble;
	private final JCompletionComboBox comboAppelationAOC;
	private final JCompletionComboBox comboAppelationIGP;
	private final MyCellarLabel labelCountry = new MyCellarLabel();
	private final MyCellarLabel labelVignoble = new MyCellarLabel();
	private final MyCellarLabel labelAppelationAOC = new MyCellarLabel();
	private final MyCellarLabel labelAppelationIGP = new MyCellarLabel();
	private final MyCellarButton manageVineyardButton = new MyCellarButton(LabelType.INFO, "165", new ManageVineyardAction());
	
	public PanelVignobles(boolean modifyActive, boolean manageButton) {

		labelCountry.setText(Program.getLabel("Main.Country"));
		labelVignoble.setText(Program.getLabel("Main.Vignoble"));
		labelAppelationAOC.setText(Program.getLabel("Main.AppelationAOC"));
		labelAppelationIGP.setText(Program.getLabel("Main.AppelationIGP"));
		setLayout(new MigLayout("","[grow][grow]",""));
		comboCountry = new JCompletionComboBox() {
			private static final long serialVersionUID = 8137073557763181546L;

			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive) {
					Start.setPaneModified(true);
				}
			}
		};
		comboVignoble = new JCompletionComboBox() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive) {
					Start.setPaneModified(true);
				}
			}
		};
		comboAppelationAOC = new JCompletionComboBox() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive) {
					Start.setPaneModified(true);
				}
			}
		};
		comboAppelationIGP = new JCompletionComboBox() {
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
		comboCountry.addItem("");
		for(Country c : Program.getCountries()) {
			comboCountry.addItem(c);
		}
		comboCountry.addItemListener((e) -> {

				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(e.getItem() instanceof String) {
						comboVignoble.removeAllItems();
						comboAppelationAOC.removeAllItems();
						comboAppelationIGP.removeAllItems();
						comboVignoble.addItem("");
						comboAppelationIGP.addItem("");
						comboAppelationAOC.addItem("");
						comboVignoble.setSelectedItem("");
						comboAppelationAOC.setSelectedItem("");
						comboAppelationIGP.setSelectedItem("");
						return;
					}
					final Country country = (Country)e.getItem();
					comboVignoble.removeAllItems();
					comboAppelationAOC.removeAllItems();
					comboAppelationIGP.removeAllItems();
					comboVignoble.addItem("");
					comboAppelationIGP.addItem("");
					comboAppelationAOC.addItem("");
					Vignobles vignobles = CountryVignobles.getVignobles(country);
					if(vignobles != null) {
						ArrayList<CountryVignoble> list = (ArrayList<CountryVignoble>)vignobles.getVignoble();
						for(CountryVignoble v : list) {
							comboVignoble.addItem(v);
						}
					}
				}
		});

		comboVignoble.addItemListener((e) -> {

				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(e.getItem() instanceof String) {
						comboAppelationAOC.setSelectedItem("");
						comboAppelationIGP.setSelectedItem("");
						return;
					}
					final CountryVignoble vignoble = (CountryVignoble)e.getItem();
					comboAppelationAOC.removeAllItems();
					comboAppelationIGP.removeAllItems();
				comboAppelationIGP.addItem("");
					comboAppelationAOC.addItem("");
					List<String> itemsIGP = new ArrayList<>();
					for(Appelation v : vignoble.getSortedUnmodifiableAppelation()) {
						if(v.getAOC() != null && !v.getAOC().isEmpty()) {
							comboAppelationAOC.addItem(v);
						}
						if(v.getIGP() != null && !v.getIGP().isEmpty()) {
							itemsIGP.add(v.getIGP());
						}
					}
					Collections.sort(itemsIGP);
					itemsIGP.forEach(comboAppelationIGP::addItem);
				}
		});

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
				comboCountry.addItem("");
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
			return ((Country) o).getId();
		}
		return o.toString();
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
			return ((CountryVignoble) o).getName();
		}
		return o.toString();
	}

	public String getAOC() {
		Object o = comboAppelationAOC.getEditor().getItem();
		if (o instanceof Appelation) {
			return ((Appelation) o).getAOC();
		}
		return o.toString();
	}

	public String getIGP() {
		return comboAppelationIGP.getEditor().getItem().toString();
	}

	public JCompletionComboBox getComboCountry() {
		return comboCountry;
	}

	public JCompletionComboBox getComboVignoble() {
		return comboVignoble;
	}

	public JCompletionComboBox getComboAppelationAOC() {
		return comboAppelationAOC;
	}

	public JCompletionComboBox getComboAppelationIGP() {
		return comboAppelationIGP;
	}

	public MyCellarLabel getLabelCountry() {
		return labelCountry;
	}

	public MyCellarLabel getLabelVignoble() {
		return labelVignoble;
	}

	public MyCellarLabel getLabelAppelationAOC() {
		return labelAppelationAOC;
	}

	public MyCellarLabel getLabelAppelationIGP() {
		return labelAppelationIGP;
	}

	public MyCellarButton getManageVineyardButton() {
		return manageVineyardButton;
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

		Vignobles vignobles = null;
		if (Program.france.getId().equals(vignoble.country)) {
			comboCountry.setSelectedItem(Program.france);
			vignobles = CountryVignobles.getVignobles(Program.france);
		} else if ("fr".equals(vignoble.country)) {
			comboCountry.setSelectedItem(Program.france);
			vignobles = CountryVignobles.getVignobles(Program.france);
		} else if (vignoble.country != null) {
			Country c = Countries.findByIdOrLabel(vignoble.country);
			if (c != null) {
				comboCountry.setSelectedItem(c);
				vignobles = CountryVignobles.getVignobles(c);
			}
		}

		if (vignobles != null) {
			CountryVignoble countryVignoble = vignobles.findVignoble(vignoble);
			if (countryVignoble != null) {
				comboVignoble.setSelectedItem(countryVignoble);
			}
		}

		if (vignoble.aoc != null) {
			comboAppelationAOC.setSelectedItem(vignoble.aoc);
		}

		if (vignoble.igp != null) {
			comboAppelationIGP.setSelectedItem(vignoble.igp);
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
