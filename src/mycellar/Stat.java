package mycellar;

import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 6.0
 * @since 04/07/18
 */
public class Stat extends JPanel implements ITabListener {

	private static final long serialVersionUID = -5333602919958999440L;
	private final MyCellarLabel def2 = new MyCellarLabel();
	private final MyCellarLabel end = new MyCellarLabel();
	private final MyCellarLabel moy = new MyCellarLabel();
	private final MyCellarComboBox<String> listOptions = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> listPlaces = new MyCellarComboBox<>();
	private final JPanel panel = new JPanel();
	private String annee[];
	private final PanelChart panelChart = new PanelChart();
	private final MyCellarButton options = new MyCellarButton(Program.getLabel("Infos156"));
	private final List<StatData> listPrice = new LinkedList<>();
	private final List<StatData> listYear = new LinkedList<>();

	/**
	 * Stat: Constructeur.
	 *
	 */
	public Stat() {
		Debug("Stats");
		MyCellarLabel definition = new MyCellarLabel(Program.getLabel("Infos174")); //"Type de statistiques:");
		def2.setText(Program.getLabel("Infos105") + ":"); //"Rangement:");
		end.setHorizontalAlignment(SwingConstants.RIGHT);
		moy.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.setLayout(new MigLayout("","[][][grow]",""));
		panel.setFont(Program.FONT_PANEL);
		int nb_bottle = 0;
		moy.setText("");
		for (Rangement cave : Program.getCave()) {
			final MyCellarLabel list_num_empl;
			final MyCellarLabel list_nb_bottle;
			panel.add(new MyCellarLabel(cave.getNom()));
			if (cave.isCaisse()) {
				if (cave.getNbEmplacements() == 1) {
					list_num_empl = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
				}
				else {
					list_num_empl = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //emplacements
				}
				if (cave.getNbCaseUseAll() <= 1) {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),cave.getNbCaseUseAll())); //"bouteille");
				}
				else {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
				}
				nb_bottle += cave.getNbCaseUseAll();
				panel.add(list_num_empl);
				panel.add(list_nb_bottle, "align right, wrap");
				panel.add(new JSeparator(), "span 3, grow, wrap");
			}
			else {
				if (cave.getNbEmplacements() == 1) {
					list_num_empl = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
				}
				else {
					list_num_empl = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
				}
				if (cave.getNbCaseUseAll() <= 1) {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),cave.getNbCaseUseAll())); //"bouteille");
				}
				else {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
				}
				nb_bottle += cave.getNbCaseUseAll();
				panel.add(list_num_empl);
				panel.add(list_nb_bottle,"align right, wrap");
				displayNbBottlePlace(cave);
				panel.add(new JSeparator(), "wrap, span 3, grow");
			}
		}

		if (nb_bottle > 1) {
			end.setText(MessageFormat.format(Program.getLabel("Infos181"), nb_bottle)); //Nombre de bouteille total:
		}
		else {
			end.setText(MessageFormat.format(Program.getLabel("Infos180"), nb_bottle)); //Nombre de bouteilles totales:
		}

		options.addActionListener(this::options_actionPerformed);

		listPlaces.removeAllItems();
		listPlaces.addItem(Program.getLabel("Infos182")); //"Tous les rangement");
		for (Rangement cave : Program.getCave()) {
			listPlaces.addItem(cave.getNom());
		}
		listOptions.removeAllItems();
		listOptions.addItem(Program.getLabel("Infos183")); //"Par Rangement");
		listOptions.addItem(Program.getLabel("Infos184")); //"Par Année");
		listOptions.addItem(Program.getLabel("Infos185")); //"Par Prix");

		JScrollPane scroll = new JScrollPane(panel);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listOptions.addItemListener(this::list_itemStateChanged);
		listPlaces.addItemListener((e) -> {
			if(e.getSource().equals(listPlaces) && e.getStateChange() == ItemEvent.SELECTED)
				list2_itemStateChanged(e);
		});

		setLayout(new MigLayout("","[][][grow]", "[][]20px[grow][][]"));
		add(definition);
		add(listOptions, "wrap");
		add(def2);
		add(listPlaces, "wrap");
		add(scroll, "span 3, split 2, grow");
		add(panelChart, "grow, wrap");
		add(options);
		add(end, "span 2, align right, wrap");
		add(moy, "span 3, align right, wrap");
		options.setEnabled(false);

		Debug("Stats OK");
	}

	/**
	 * list_itemStateChanged: Fonction appellé lors d'un changement dans la
	 * première liste.
	 *
	 * @param e ItemEvent
	 */
	private void list_itemStateChanged(ItemEvent e) {
		try {
			if (listOptions.getSelectedIndex() == 0) {
				Debug("By place");
				options.setEnabled(false);
				panelChart.setPlacesChart(Program.getCave());
				def2.setText(Program.getLabel("Infos105") + ":"); //"Rangement:");
				listPlaces.removeAllItems();
				listPlaces.setEnabled(true);
				listPlaces.addItem(Program.getLabel("Infos182")); //"Tous les rangements");
				listPlaces.setSelectedIndex(0);
				for (Rangement r : Program.getCave()) {
					listPlaces.addItem(r.getNom());
				}
			}
			if (listOptions.getSelectedIndex() == 1) {
				def2.setText("");
				Debug("By year");
				int obj[] = Program.getAnnees();
				annee = new String[obj.length];
				int i = 0;
				for (int an : Program.getAnnees()) {
					annee[i++] = Integer.toString(an);
				}
				Arrays.sort(annee, Collator.getInstance());
				listPlaces.removeAllItems();
				listPlaces.addItem(Program.getLabel("Infos186")); //"Toutes les années");
				listPlaces.setSelectedIndex(0);
				listPlaces.setEnabled(false);
			}
			if (listOptions.getSelectedIndex() == 2) {
				Debug("By price");
				def2.setText(Program.getLabel("Infos187")); //"Tranche de prix:");
				listPlaces.removeAllItems();
				listPlaces.addItem(Program.getLabel("Infos188")); //"Toutes les tranches");
				listPlaces.addItem(Program.getLabel("Infos299")); //"Tranches avec bouteilles");
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * list2_itemStateChanged: Fonction appellé lors d'un changement dans la
	 * seconde liste.
	 *
	 * @param e ItemEvent
	 */
	private void list2_itemStateChanged(ItemEvent e) {

		try {
			if (listOptions.getSelectedIndex() == 0) {
				if (listPlaces.getSelectedIndex() == 0) {
					displayAllPlaces();
				}	else {
					displayOnePlace();
				}
			}	else if (listOptions.getSelectedIndex() == 1) { //Par Année
				displayYear();
			}	else if (listOptions.getSelectedIndex() == 2) { //Par prix
				displayByPrice();
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private void displayByPrice() {
		boolean all_bracket = true;
		listPlaces.setEnabled(true);
		if (listPlaces.getSelectedIndex() == 1) {
      all_bracket = false;
    }
		panel.removeAll();
		panel.repaint();

		options.setEnabled(true);
		int tranche = Program.getCaveConfigInt("TRANCHE_PRIX", 50);

		if (tranche <= 0) {
      tranche = 50;
      Program.putCaveConfigInt("TRANCHE_PRIX", 50);
    }
		Map<Integer, Integer> mapPrixCount = new HashMap<>();
		int ss_prix = 0;

		if(listPrice.isEmpty()) {

      for (Bouteille b : Program.getStorage().getAllList()) {
        if (b != null) {
          if (b.hasPrice()) {
						int prix_int = b.getPrice().intValue();
						if(mapPrixCount.containsKey(prix_int)) {
							mapPrixCount.put(prix_int, mapPrixCount.get(prix_int) + 1);
						}
						else {
							mapPrixCount.put(prix_int, 1);
						}
          }
          else {
						ss_prix++;
					}
        }
      }
      for (int i = 0; i <= Program.getMaxPrice(); i += tranche) {
        String label = MessageFormat.format(Program.getLabel("Infos190"), i, (i + tranche - 1), Program.getCaveConfigString("DEVISE", ""));
        int nb = 0;
        for (int j = i; j < (i + tranche); j++) {
          if (mapPrixCount.containsKey(j)) {
          	nb += mapPrixCount.get(j);
					}
        }
        if (all_bracket || nb > 0)
          listPrice.add(new StatData(label, nb));
      }
      listPrice.add(new StatData(Program.getLabel("Infos192"), ss_prix));
    }
		for(StatData price: listPrice) {
      if(all_bracket || price.getCount() > 0) {
        panel.add(new MyCellarLabel(price.getName()));
        if(price.getCount() > 1)
          panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), price.getCount())), "span 2, align right, wrap");
        else
          panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),price.getCount())), "span 2, align right, wrap");
      }
    }
		panel.updateUI();
		end.setText(MessageFormat.format(Program.getLabel("Infos244"),Program.getCellarValue(), Program.getCaveConfigString("DEVISE","")));
		if (Program.getStorage().getAllNblign() > 0)
      moy.setText(MessageFormat.format(Program.getLabel("Infos300"), (Program.getCellarValue() / Program.getStorage().getAllNblign()), Program.getCaveConfigString("DEVISE","")));
		panelChart.setDataChart(listPrice, Program.getLabel("Infos185"));
	}

	private void displayYear() {
		Debug("By year");
		panel.removeAll();
		options.setEnabled(false);
		moy.setText("");
		int nb_bottle = 0;
		if(listYear.isEmpty() && annee != null) {
        for (String an : annee) {
          int v = Integer.parseInt(an.trim());
          if ( v > 1000 && v < 9000) {
            int count = Program.getNbBouteilleAnnee(v);
            nb_bottle += count;
            listYear.add(new StatData(an, count));
          }
        }
        int nb_autre = Program.getNbNonVintage();

        nb_bottle += nb_autre;
        listYear.add(new StatData(Program.getLabel("Infos390"), nb_autre));

        nb_autre = Program.getNbAutreAnnee();

        nb_bottle += nb_autre;
        listYear.add(new StatData(Program.getLabel("Infos225"), nb_autre));
    }
		for(StatData data: listYear) {
      panel.add(new MyCellarLabel(data.getName()));
      if (data.getCount() <= 1)
        panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),data.getCount())), "span 2, align right, wrap"); //"bouteille");
      else
        panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), data.getCount())), "span 2, align right, wrap"); //"bouteilles")
    }
		panel.updateUI();
		panelChart.setDataChart(listYear, Program.getLabel("Infos184"));
		end.setText(Program.getLabel("Infos136") + ": " + nb_bottle);
	}

	private void displayOnePlace() {
		Debug("One place");
		panel.removeAll();
		panel.repaint();
		end.setText("");
		moy.setText("");
		options.setEnabled(false);
		int index = 0;
		if (listPlaces.getSelectedIndex() > 1) {
      index = listPlaces.getSelectedIndex() - 1;
    }
		Rangement cave = Program.getCave(index);
		int nb_bottle = 0;
		if (cave != null) {
			final MyCellarLabel list_num_empl;
			final MyCellarLabel list_nb_bottle;
			panelChart.setPlaceChart(cave);
			nb_bottle = cave.getNbCaseUseAll();
			panel.add(new MyCellarLabel(cave.getNom()));
			if (cave.isCaisse()) {
				if (cave.getNbEmplacements() == 1) {
					list_num_empl = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
				} else {
					list_num_empl = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
				}
				if (cave.getNbCaseUseAll() <= 1) {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"), cave.getNbCaseUseAll())); //"bouteille");
				} else {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
				}
				panel.add(list_num_empl);
				panel.add(list_nb_bottle, "span 2, align right, wrap");
				panel.add(new JSeparator(), "span 3, wrap");
			}
    	else {
				if (cave.getNbEmplacements() == 1) {
					list_num_empl = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
				}
				else {
					list_num_empl = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
				}
				if (cave.getNbCaseUseAll() <= 1) {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),cave.getNbCaseUseAll())); //"bouteille");
				}
				else {
					list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUseAll())); //"bouteilles");
				}
				panel.add(list_num_empl);
				panel.add(list_nb_bottle, "span 2, align right, wrap");
				displayNbBottlePlace(cave);
				panel.add(new JSeparator(), "span 3, wrap");
			}
		}
		end.setText(Program.getLabel("Infos136") + ": " + nb_bottle);
	}

	private void displayAllPlaces() {
		Debug("All places");
		panelChart.setPlacesChart(Program.getCave());
		panel.removeAll();
		panel.repaint();

		int nb_bottle = 0;
		for (Rangement cave : Program.getCave()) {
      final MyCellarLabel list_num_empl;
      final MyCellarLabel list_nb_bottle;
      panel.add(new MyCellarLabel(cave.getNom()));
      if (cave.isCaisse()) {
        if (cave.getNbEmplacements() == 1) {
          list_num_empl = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
        }
        else {
          list_num_empl = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
        }
        if (cave.getNbCaseUseAll() <= 1) {
          list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),cave.getNbCaseUseAll())); //"bouteille");
        }
        else {
          list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"),cave.getNbCaseUseAll())); //"bouteilles");
        }
        nb_bottle += cave.getNbCaseUseAll();
        panel.add(list_num_empl);
        panel.add(list_nb_bottle, "span 2, align right, wrap");
        panel.add(new JSeparator(), "span 3, wrap");
      }
      else {
        if (cave.getNbEmplacements() == 1) {
          list_num_empl = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
        }
        else {
          list_num_empl = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), cave.getNbEmplacements())); //"emplacements");
        }
        if (cave.getNbCaseUseAll() <= 1) {
          list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),cave.getNbCaseUseAll())); //"bouteille");
        }
        else {
          list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"),cave.getNbCaseUseAll())); //"bouteilles");
        }
        nb_bottle += cave.getNbCaseUseAll();
        panel.add(list_num_empl);
        panel.add(list_nb_bottle, "span 2, align right, wrap");
        displayNbBottlePlace(cave);
        panel.add(new JSeparator(), "span 3, wrap");
      }
    }
		moy.setText("");
		if (nb_bottle > 1) {
      end.setText(MessageFormat.format(Program.getLabel("Infos181"), nb_bottle));
    }
    else {
      end.setText(MessageFormat.format(Program.getLabel("Infos180"), nb_bottle));
    }
	}

	private void displayNbBottlePlace(Rangement cave) {
		for (int j = 0; j < cave.getNbEmplacements(); j++) {
      panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos179"), (j + 1)))); //Emplacement n°
      if (cave.getNbCaseUseAll() <= 1) {
        panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.1Bottle"),cave.getNbCaseUseAll())),"span 2, align right, wrap"); //"bouteille");
      }
      else {
        panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos161"), cave.getNbCaseUse(j))),"span 2, align right, wrap"); //"bouteilles");
      }
    }
	}

	/**
	 * options_actionPerformed: Appel de la fenètre d'options.
	 *
	 * @param e ActionEvent
	 */
	private void options_actionPerformed(ActionEvent e) {
		try {
			Debug("options_actionPerforming...");
			options.setSelected(false);
			String value = JOptionPane.showInputDialog(this, Program.getLabel("Infos194"));
			if(StringUtils.isNumeric(value)) {
				Program.putCaveConfigInt("TRANCHE_PRIX", new Integer(value));
				listPrice.clear();
				list2_itemStateChanged(null);
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Stat: " + sText );
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

	private class PanelChart extends JPanel {

		private static final long serialVersionUID = -6697139633950076186L;

		private PanelChart(){
			setLayout(new MigLayout("","grow","grow"));
			setPlacesChart(Program.getCave());
		}


		/** * Creates a chart */

		private void setPlacesChart(List<Rangement> rangements) {

			DefaultPieDataset dataset = new DefaultPieDataset();
			for(Rangement rangement: rangements) {
				if(rangement == null)
					continue;
				dataset.setValue(rangement.getNom(), rangement.getNbCaseUseAll());
			}
			JFreeChart chart = ChartFactory.createPieChart(Program.getLabel("Infos182"),          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			removeAll();
			add(chartPanel, "grow");
		}

		private void setPlaceChart(Rangement rangement) {

			removeAll();
			if(rangement.isCaisse())
				return;
			DefaultPieDataset dataset = new DefaultPieDataset();
			for(Part part: rangement.getPlace()){
				dataset.setValue(MessageFormat.format(Program.getLabel("Infos179"),part.getNum()), rangement.getNbCaseUse(part.getNum()-1));
			}
			JFreeChart chart = ChartFactory.createPieChart(rangement.getNom(),          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			this.add(chartPanel, "grow");
		}

		private void setDataChart(List<StatData> datas, String title) {

			removeAll();
			DefaultPieDataset dataset = new DefaultPieDataset();
			for(StatData part: datas) {
				if(part.getCount() > 0)
					dataset.setValue(part.getName(), part.getCount());
			}
			JFreeChart chart = ChartFactory.createPieChart(title,          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			add(chartPanel, "grow");
			updateUI();
		}
	}

	class StatData {

		private final String name;
		private final int count;


		private StatData(String name, int count) {
			super();
			this.name = name;
			this.count = count;
		}

		public String getName() {
			return name;
		}
		public int getCount() {
			return count;
		}
	}

	public void updateView() {
		listYear.clear();
		listPrice.clear();
		list2_itemStateChanged(null);
	}

}
