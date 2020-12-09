package mycellar;

import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarEnum;
import mycellar.core.MyCellarLabel;
import mycellar.core.MyCellarSettings;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;


/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 7.5
 * @since 09/12/20
 */
public final class Stat extends JPanel implements ITabListener, IMyCellar, IUpdatable {

	private static final long serialVersionUID = -5333602919958999440L;
	private final MyCellarLabel comboLabel = new MyCellarLabel(LabelType.INFO, "081", LabelProperty.SINGLE.withDoubleQuote());
	private final MyCellarLabel end = new MyCellarLabel();
	private final MyCellarLabel moy = new MyCellarLabel();
	private final MyCellarComboBox<MyCellarEnum> listOptions = new MyCellarComboBox<>();
	private final MyCellarComboBox<PlaceComboItem> listPlaces = new MyCellarComboBox<>();
	private final MyCellarComboBox<String> listChart = new MyCellarComboBox<>();
	private final JPanel panel = new JPanel();
	private String[] annee;
	private final PanelChart panelChart = new PanelChart();
	private final MyCellarButton options = new MyCellarButton(LabelType.INFO, "156", LabelProperty.SINGLE.withThreeDashes());
	private final List<StatData> listPrice = new LinkedList<>();
	private final List<StatData> listYear = new LinkedList<>();
	private final List<StatData> listHistory = new LinkedList<>();
	private final ConcurrentMap<Integer, LongAdder> mapDeletedPerYear = new ConcurrentHashMap<>();
	private final ConcurrentMap<Integer, LongAdder> mapAddedPerYear = new ConcurrentHashMap<>();

	enum StatType {
		PLACE,
		YEAR,
		PRICE,
		HISTORY
	}


	/**
	 * Stat: Constructeur.
	 */
	public Stat() {
		Debug("Stats");
		MyCellarLabel definition = new MyCellarLabel(Program.getLabel("Infos174")); //"Type de statistiques:");
		end.setHorizontalAlignment(SwingConstants.RIGHT);
		moy.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.setLayout(new MigLayout("","[][][grow]",""));
		panel.setFont(Program.FONT_PANEL);
		Program.getCave().forEach(this::displayPlace);

		updateBouteilleCountLabel();

		options.addActionListener(this::options_actionPerformed);

		listPlaces.addItem(new PlaceComboItem(Program.getLabel("Infos182"))); //"Tous les rangement");
		Program.getCave().forEach(rangement -> listPlaces.addItem(new PlaceComboItem(rangement)));

		listOptions.addItem(new MyCellarEnum(StatType.PLACE.ordinal(), Program.getLabel("Infos183"))); //"Par Rangement");
		listOptions.addItem(new MyCellarEnum(StatType.YEAR.ordinal(), Program.getLabel("Infos184"))); //"Par Annee");
		listOptions.addItem(new MyCellarEnum(StatType.PRICE.ordinal(), Program.getLabel("Infos185"))); //"Par Prix");
		listOptions.addItem(new MyCellarEnum(StatType.HISTORY.ordinal(), Program.getLabel("Stat.history"))); //Historiques

		JScrollPane scroll = new JScrollPane(panel);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listOptions.addItemListener(this::typeStats_itemStateChanged);
		listPlaces.addItemListener((e) -> {
			if (e.getSource().equals(listPlaces) && e.getStateChange() == ItemEvent.SELECTED) {
				list2_itemStateChanged();
			}
		});

		MyCellarLabel chartType = new MyCellarLabel(Program.getLabel("Stat.chartType"));
		listChart.addItem(Program.getLabel("Stat.chartBar"));
		listChart.addItem(Program.getLabel("Stat.chartPie"));
		listChart.addItemListener(this::chartItemStateChanged);
		listChart.setEnabled(listOptions.getSelectedIndex() != 0);

		setLayout(new MigLayout("","[][][grow]", "[][][]20px[grow][][]"));
		add(definition);
		add(listOptions, "wrap");
		add(comboLabel);
		add(listPlaces, "wrap");
		add(chartType);
		add(listChart, "wrap");
		add(scroll, "span 3, split 2, grow 30");
		add(panelChart, "grow, wrap");
		add(options);
		add(end, "span 2, align right, wrap");
		add(moy, "span 3, align right, wrap");
		options.setEnabled(false);

		Debug("Stats OK");
	}

	private void updateBouteilleCountLabel() {
		int nb_bottle = Program.getNbBouteille();
		end.setText(MessageFormat.format(Program.getLabel("Infos180", new LabelProperty(nb_bottle > 1)), nb_bottle)); //Nombre de bouteille total:
	}

	private void chartItemStateChanged(ItemEvent itemEvent) {
		if (listChart.getSelectedIndex() == 0) {
			Debug("Bar Chart");
			if (listOptions.getSelectedIndex() == 1) {
				panelChart.setDataBarChart(listYear, Program.getLabel("Infos184"));
			} else if (listOptions.getSelectedIndex() == 2) {
				panelChart.setDataBarChart(listPrice, Program.getLabel("Infos185"));
			}
		} else if (listChart.getSelectedIndex() == 1) {
			Debug("Pie Chart");
			if (listOptions.getSelectedIndex() == 1) {
				panelChart.setDataPieChart(listYear, Program.getLabel("Infos184"));
			} else if (listOptions.getSelectedIndex() == 2) {
				panelChart.setDataPieChart(listPrice, Program.getLabel("Infos185"));
			}
		}
	}

	/**
	 * typeStats_itemStateChanged: Fonction appelle lors d'un changement dans la
	 * premiere liste.
	 *
	 * @param e ItemEvent
	 */
	private void typeStats_itemStateChanged(ItemEvent e) {
		try {
			final MyCellarEnum selectedItem = (MyCellarEnum)listOptions.getSelectedItem();
			if (selectedItem == null) {
				return;
			}
			listChart.setEnabled(true);
			if (selectedItem.getValue() == StatType.PLACE.ordinal()) {
				Debug("By place");
				listChart.setEnabled(false);
				options.setEnabled(false);
				panelChart.setPlacesChart(Program.getCave());
				comboLabel.setText(Program.getLabel("Infos081", LabelProperty.SINGLE.withDoubleQuote())); //"Rangement:");
				listPlaces.removeAllItems();
				listPlaces.setEnabled(true);
				listPlaces.addItem(new PlaceComboItem(Program.getLabel("Infos182"))); //"Tous les rangements");
				listPlaces.setSelectedIndex(0);
				Program.getCave().forEach(rangement -> listPlaces.addItem(new PlaceComboItem(rangement)));
			} else if (selectedItem.getValue() == StatType.YEAR.ordinal()) {
				Debug("By year");
				comboLabel.setText("");
				int[] annees = Program.getAnnees();
				annee = new String[annees.length];
				int i = 0;
				for (int an : annees) {
					annee[i++] = Integer.toString(an);
				}
				Arrays.sort(annee, Collator.getInstance());
				listPlaces.removeAllItems();
				listPlaces.addItem(new PlaceComboItem(Program.getLabel("Infos186"))); //"Toutes les annees");
				listPlaces.setSelectedIndex(0);
				listPlaces.setEnabled(false);
			} else if (selectedItem.getValue() == StatType.PRICE.ordinal()) {
				Debug("By price");
				comboLabel.setText(Program.getLabel("Infos187")); //"Tranche de prix:");
				listPlaces.removeAllItems();
				listPlaces.addItem(new PlaceComboItem(Program.getLabel("Infos188"))); //"Toutes les tranches");
				listPlaces.addItem(new PlaceComboItem(Program.getLabel("Stat.BracketsWith", LabelProperty.PLURAL))); //"Tranches avec bouteilles");
			} else if (selectedItem.getValue() == StatType.HISTORY.ordinal()) {
				Debug("By History");
				listChart.setEnabled(false);
				options.setEnabled(false);
				panelChart.setPlacesChart(Program.getCave());
				listPlaces.removeAllItems();
				listPlaces.addItem(new PlaceComboItem(Program.getLabel("Infos186"))); //"Toutes les annees");
				listPlaces.setSelectedIndex(0);
				listPlaces.setEnabled(false);
			}
		}	catch (Exception exc) {
			Program.showException(exc);
		}
	}

	/**
	 * list2_itemStateChanged: Fonction appelle lors d'un changement dans la
	 * seconde liste.
	 */
	private void list2_itemStateChanged() {

		try {
			final MyCellarEnum selectedItem = (MyCellarEnum)listOptions.getSelectedItem();
			if (selectedItem == null) {
				return;
			}
			if (selectedItem.getValue() == StatType.PLACE.ordinal()) {
				if (listPlaces.getSelectedIndex() == 0) {
					displayAllPlaces();
				}	else {
					displayOnePlace();
				}
			}	else if (selectedItem.getValue() == StatType.YEAR.ordinal()) { //Par Annee
				displayYear();
			}	else if (selectedItem.getValue() == StatType.PRICE.ordinal()) { //Par prix
				displayByPrice();
			} else if (selectedItem.getValue() == StatType.HISTORY.ordinal()) {
				displayHistory();
			}
		}	catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private void displayByPrice() {
		listPlaces.setEnabled(true);
		boolean all_bracket = true;
		if (listPlaces.getSelectedIndex() == 1) {
      all_bracket = false;
    }
		panel.removeAll();
		panel.repaint();

		options.setEnabled(true);
		int tranche = Program.getCaveConfigInt(MyCellarSettings.TRANCHE_PRIX, 50);

		if (tranche <= 0) {
      tranche = 50;
      Program.putCaveConfigInt(MyCellarSettings.TRANCHE_PRIX, 50);
    }

		if (listPrice.isEmpty()) {
			Map<Integer, Integer> mapPrixCount = new HashMap<>();
			int ss_prix = 0;

      for (Bouteille b : Program.getStorage().getAllList()) {
				if (b == null) {
					continue;
				}
				if (!b.hasPrice()) {
					ss_prix++;
					continue;
				}
				int prix_int = b.getPrice().intValue();
				if(mapPrixCount.containsKey(prix_int)) {
					mapPrixCount.put(prix_int, mapPrixCount.get(prix_int) + 1);
				}	else {
					mapPrixCount.put(prix_int, 1);
				}
			}
      for (int i = 0; i <= Program.getMaxPrice(); i += tranche) {
        String label = MessageFormat.format(Program.getLabel("Infos190"), i, (i + tranche - 1), Program.getCaveConfigString(MyCellarSettings.DEVISE, ""));
        int nb = 0;
        for (int j = i; j < (i + tranche); j++) {
          if (mapPrixCount.containsKey(j)) {
          	nb += mapPrixCount.get(j);
					}
        }
        if (all_bracket || nb > 0) {
					listPrice.add(new StatData(label, nb));
				}
      }
      listPrice.add(new StatData(Program.getLabel("Infos192"), ss_prix));
    }
		for (StatData price: listPrice) {
			final int priceCount = price.getCount();
			if (all_bracket || priceCount > 0) {
        panel.add(new MyCellarLabel(price.getName()));
				panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.severalItems", new LabelProperty(priceCount > 1)), priceCount)), "span 2, align right, wrap");
      }
    }
		panel.updateUI();
		end.setText(MessageFormat.format(Program.getLabel("Infos244"),Program.getCellarValue(), Program.getCaveConfigString(MyCellarSettings.DEVISE,"")));
		final int bottlesCount = Program.getStorage().getBottlesCount();
		if (bottlesCount > 0) {
			moy.setText(MessageFormat.format(Program.getLabel("Infos300"), (Program.getCellarValue() / bottlesCount), Program.getCaveConfigString(MyCellarSettings.DEVISE, "")));
		}
		if (listChart.getSelectedIndex() == 0) {
			panelChart.setDataBarChart(listPrice, Program.getLabel("Infos185"));
		} else {
			panelChart.setDataPieChart(listPrice, Program.getLabel("Infos185"));
		}
	}

	private void displayYear() {
		Debug("By year");
		panel.removeAll();
		options.setEnabled(false);
		moy.setText("");
		if (listYear.isEmpty()) {
        for (String an : annee) {
          int year = Integer.parseInt(an.strip());
          if (year > 1000 && year < 9000) {
            listYear.add(new StatData(an, Program.getNbBouteilleAnnee(year)));
          }
        }
        listYear.add(new StatData(Program.getLabel("Infos390"), Program.getNbNonVintage()));
        listYear.add(new StatData(Program.getLabel("Infos225"), Program.getNbAutreAnnee()));
    }
		for (StatData data: listYear) {
      panel.add(new MyCellarLabel(data.getName()));
			final int dataCount = data.getCount();
			panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.severalItems", new LabelProperty(dataCount > 1)), dataCount)), "span 2, align right, wrap"); //"bouteille");
    }
		panel.updateUI();
		if (listChart.getSelectedIndex() == 0) {
			panelChart.setDataBarChart(listYear, Program.getLabel("Infos184"));
		} else {
			panelChart.setDataPieChart(listYear, Program.getLabel("Infos184"));
		}
		end.setText(MessageFormat.format(Program.getLabel("Infos098", LabelProperty.PLURAL), Program.getNbBouteille()));
	}

	private void displayHistory() {
		Debug("By history");
		panel.removeAll();
		options.setEnabled(false);
		moy.setText("");

		if (listHistory.isEmpty()) {
			Program.getHistory()
					.stream()
					.filter(History::isAddedOrDeleted)
					.forEach(this::mapToAddedDeletedStat);
			mapAddedPerYear.forEach((year, value) -> listHistory.add(new StatData(year, Program.getLabel("Stat.in") + " " + year, value.intValue())));
			mapDeletedPerYear.forEach((year, value) -> listHistory.add(new StatData(year, Program.getLabel("Stat.out") + " " + year, value.intValue())));
			listHistory.sort(Comparator.comparingInt(o -> o.id));
		}
		final JFreeChart chart = panelChart.setDataBarChart(listHistory, Program.getLabel("Stat.history"));
		CategoryPlot cplot = (CategoryPlot)chart.getPlot();
		((BarRenderer)cplot.getRenderer()).setBarPainter(new StandardBarPainter());

		BarRenderer r = (BarRenderer)chart.getCategoryPlot().getRenderer();
		for (int i = 0; i < listHistory.size(); i++) {
			r.setSeriesPaint(i, i % 2 == 0 ? Color.blue : Color.red);
		}
	}

	private void displayOnePlace() {
		Debug("One place");
		panel.removeAll();
		panel.repaint();
		end.setText("");
		moy.setText("");
		options.setEnabled(false);
		PlaceComboItem placeComboItem = (PlaceComboItem) listPlaces.getSelectedItem();
		int nbBottle = 0;
		if (placeComboItem!= null && placeComboItem.getRangement() != null) {
			Rangement cave = placeComboItem.getRangement();
			panelChart.setPlaceChart(cave);
			nbBottle = cave.getNbCaseUseAll();
			panel.add(new MyCellarLabel(cave.getNom()));
			displayPlace(cave);
		}
		end.setText(MessageFormat.format(Program.getLabel("Infos098", LabelProperty.PLURAL), nbBottle));
	}

	private void displayPlace(Rangement cave) {
		final int nbEmplacements = cave.getNbEmplacements();
		final int nbCaseUseAll = cave.getNbCaseUseAll();
		final MyCellarLabel list_num_empl;
		if (nbEmplacements == 1) {
			list_num_empl = new MyCellarLabel(Program.getLabel("Infos175")); //"1 emplacement");
		} else {
			list_num_empl = new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos176"), nbEmplacements)); //"emplacements");
		}
		final MyCellarLabel list_nb_bottle = new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.severalItems", new LabelProperty(nbCaseUseAll > 1)), nbCaseUseAll)); //"bouteille");
		panel.add(list_num_empl);
		panel.add(list_nb_bottle, "span 2, align right, wrap");
		if (!cave.isCaisse()) {
			displayNbBottlePlace(cave);
		}
		panel.add(new JSeparator(), "span 3, wrap");
	}

	private void displayAllPlaces() {
		Debug("All places");
		panelChart.setPlacesChart(Program.getCave());
		panel.removeAll();
		panel.repaint();

		int nbBottle = 0;
		for (Rangement cave : Program.getCave()) {
      panel.add(new MyCellarLabel(cave.getNom()));
			nbBottle += cave.getNbCaseUseAll();
			displayPlace(cave);
		}
		moy.setText("");
		end.setText(MessageFormat.format(Program.getLabel("Infos180", new LabelProperty(nbBottle > 1)), nbBottle));
	}

	private void displayNbBottlePlace(Rangement cave) {
		for (int j = 0; j < cave.getNbEmplacements(); j++) {
      panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Infos179"), (j + 1)))); //Emplacement
			panel.add(new MyCellarLabel(MessageFormat.format(Program.getLabel("Main.severalItems", new LabelProperty(cave.getNbCaseUseAll() > 1)),cave.getNbCaseUseAll())),"span 2, align right, wrap"); //"bouteille");
    }
	}

	private void mapToAddedDeletedStat(History history) {
		if (history.isDeleted()) {
			mapDeletedPerYear.computeIfAbsent(history.getLocaleDate().getYear(), integer -> new LongAdder()).increment();
		} else {
			mapAddedPerYear.computeIfAbsent(history.getLocaleDate().getYear(), integer -> new LongAdder()).increment();
		}
	}

	/**
	 * options_actionPerformed: Appel de la fenetre d'options.
	 *
	 * @param e ActionEvent
	 */
	private void options_actionPerformed(ActionEvent e) {
		try {
			Debug("options_actionPerforming...");
			options.setSelected(false);
			String value = JOptionPane.showInputDialog(this, Program.getLabel("Infos194"));
			if (StringUtils.isNumeric(value)) {
				Program.putCaveConfigInt(MyCellarSettings.TRANCHE_PRIX, Integer.parseInt(value));
				listPrice.clear();
				list2_itemStateChanged();
			}
		}
		catch (Exception exc) {
			Program.showException(exc);
		}
	}

	private static void Debug(String sText) {
		Program.Debug("Stat: " + sText);
	}

	@Override
	public boolean tabWillClose(TabEvent event) {
		return true;
	}

	@Override
	public void tabClosed() {
		Start.getInstance().updateMainPanel();
	}

	@Override
	public void setUpdateView() {
	}

	private static class PanelChart extends JPanel {

		private static final long serialVersionUID = -6697139633950076186L;

		private PanelChart(){
			setLayout(new MigLayout("","grow","grow"));
			setPlacesChart(Program.getCave());
		}


		/** * Creates a chart */

		private void setPlacesChart(List<Rangement> rangements) {

			DefaultPieDataset dataset = new DefaultPieDataset();
			for (Rangement rangement : rangements) {
				if (rangement == null) {
					continue;
				}
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
			if (rangement.isCaisse()) {
				return;
			}
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (Part part: rangement.getPlace()){
				dataset.setValue(MessageFormat.format(Program.getLabel("Infos179"),part.getNum()), rangement.getNbCaseUse(part.getNum()-1));
			}
			JFreeChart chart = ChartFactory.createPieChart(rangement.getNom(),          // chart title
					dataset,                // data
					false,                   // include legend
					true,
					false);

			ChartPanel chartPanel = new ChartPanel(chart);
			add(chartPanel, "grow");
		}

		private void setDataPieChart(List<StatData> datas, String title) {
			removeAll();
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (StatData part: datas) {
				if (part.getCount() > 0) {
					dataset.setValue(part.getName(), part.getCount());
				}
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

		private JFreeChart setDataBarChart(List<StatData> datas, String title) {
			removeAll();
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			for (StatData part: datas) {
				if (part.getCount() > 0) {
					dataset.addValue(part.getCount(), part.getName(), part.getName());
				}
			}
			JFreeChart chart = ChartFactory.createBarChart3D(title,          // chart title
					"", Program.getLabel("Stat.count"),
					dataset,                // data
					PlotOrientation.VERTICAL,
					true,                   // include legend
					true,
					true);

			ChartPanel chartPanel = new ChartPanel(chart);
			add(chartPanel, "grow");
			updateUI();
			return chart;
		}
	}

	static class StatData {

		private final String name;
		private final int count;
		private int id;

		private StatData(String name, int count) {
			this.name = name;
			this.count = count;
		}

		private StatData(int id, String name, int count) {
			this.id = id;
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

	@Override
	public void updateView() {
		listYear.clear();
		listPrice.clear();
		listPlaces.removeAllItems();
		listPlaces.addItem(new PlaceComboItem(Program.getLabel("Infos182"))); //"Tous les rangement");
    Program.getCave().forEach(rangement -> listPlaces.addItem(new PlaceComboItem(rangement)));
		list2_itemStateChanged();
		updateBouteilleCountLabel();
	}

	static class PlaceComboItem {

		private final String label;
		private final Rangement rangement;

		private PlaceComboItem(Rangement rangement) {
			this.rangement = rangement;
			label = rangement.getNom();
		}

		private PlaceComboItem(String label) {
			rangement = null;
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}

		public Rangement getRangement() {
			return rangement;
		}
	}
}
