package mycellar.core;

import mycellar.JCompletionComboBox;
import mycellar.Program;
import mycellar.Start;
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

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 21/01/17
 */
public class PanelVignobles extends JPanel {

	private static final long serialVersionUID = -3053382428338158563L;
	private final MyCellarManageBottles instance;
	
	public PanelVignobles(MyCellarManageBottles instance, boolean modifyActive) {
		this.instance = instance;
		setLayout(new MigLayout("","[grow][grow]",""));
		instance.comboCountry = new JCompletionComboBox() {
			private static final long serialVersionUID = 8137073557763181546L;

			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive)
					Start.setPaneModified(true);
			}
		};
		instance.comboCountry.setCaseSensitive(false);
		instance.comboCountry.setEditable(true);
		instance.comboCountry.addItem("");
		for(Country c : Program.getCountries())
			instance.comboCountry.addItem(c);
		instance.comboCountry.addItemListener((e) -> {

				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(e.getItem() instanceof String) {
						instance.comboVignoble.removeAllItems();
						instance.comboAppelationAOC.removeAllItems();
						instance.comboAppelationIGP.removeAllItems();
						instance.comboVignoble.addItem("");
						instance.comboAppelationIGP.addItem("");
						instance.comboAppelationAOC.addItem("");
						instance.comboVignoble.setSelectedItem("");
						instance.comboAppelationAOC.setSelectedItem("");
						instance.comboAppelationIGP.setSelectedItem("");
						return;
					}
					final Country country = (Country)e.getItem();
					instance.comboVignoble.removeAllItems();
					instance.comboAppelationAOC.removeAllItems();
					instance.comboAppelationIGP.removeAllItems();
					instance.comboVignoble.addItem("");
					instance.comboAppelationIGP.addItem("");
					instance.comboAppelationAOC.addItem("");
					Vignobles vignobles = CountryVignobles.getVignobles(country);
					if(vignobles != null) {
						ArrayList<CountryVignoble> list = (ArrayList<CountryVignoble>)vignobles.getVignoble();
						for(CountryVignoble v : list)
							instance.comboVignoble.addItem(v);
					}
				}
		});

		instance.comboVignoble = new JCompletionComboBox() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive)
					Start.setPaneModified(true);
			}
		};
		instance.comboVignoble.setCaseSensitive(false);
		instance.comboVignoble.setEditable(true);
		instance.comboVignoble.addItemListener((e) -> {

				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(e.getItem() instanceof String) {
						instance.comboAppelationAOC.setSelectedItem("");
						instance.comboAppelationIGP.setSelectedItem("");
						return;
					}
					final CountryVignoble vignoble = (CountryVignoble)e.getItem();
					instance.comboAppelationAOC.removeAllItems();
					instance.comboAppelationIGP.removeAllItems();
					instance.comboAppelationIGP.addItem("");
					instance.comboAppelationAOC.addItem("");
					for(Appelation v : vignoble.getAppelation()) {
						if(v.getAOC() != null && !v.getAOC().isEmpty())
							instance.comboAppelationAOC.addItem(v);
						if(v.getIGP() != null && !v.getIGP().isEmpty())
							instance.comboAppelationIGP.addItem(new IGPItem(v));
					}
				}
		});

		add(instance.labelCountry, "w 150:150:150, split 2");
		add(instance.labelVignoble, "wrap");
		add(instance.comboCountry, "w 150:150:, split 2");
		add(instance.comboVignoble, "w 200:200:, grow");
		add(instance.manageVineyardButton, "alignx right, wrap");

		add(instance.labelAppelationAOC);
		add(instance.labelAppelationIGP, "wrap");

		instance.comboAppelationAOC = new JCompletionComboBox() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive)
					Start.setPaneModified(true);
			}
		};
		instance.comboAppelationAOC.setCaseSensitive(false);
		instance.comboAppelationAOC.setEditable(true);
		
		add(instance.comboAppelationAOC, "w 200:200:, growx");

		instance.comboAppelationIGP = new JCompletionComboBox() {
			private static final long serialVersionUID = 8137073557763181546L;
			@Override
			protected void doAfterModify() {
				super.doAfterModify();
				if(modifyActive)
					Start.setPaneModified(true);
			}
		};
		instance.comboAppelationIGP.setCaseSensitive(false);
		instance.comboAppelationIGP.setEditable(true);


		add(instance.comboAppelationIGP, "w 200:200:");
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("Main.Vignoble")));
	}
	
	public void updateList() {
		SwingUtilities.invokeLater(() -> {
			Debug("Updating List...");
			if(instance.comboCountry.getItemCount() == 0)
				instance.comboCountry.addItem("");
			for(Country c : Program.getCountries()) {
				if(!instance.comboCountry.hasItem(c))
					instance.comboCountry.addItem(c);
			}
			Debug("Updating List Done");
		});
	}

	private void Debug(String string) {
		Program.Debug("PanelVignoble: "+string);
	}
}
