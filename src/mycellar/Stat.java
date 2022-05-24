package mycellar;

import mycellar.core.IMyCellar;
import mycellar.core.IMyCellarObject;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarEnum;
import mycellar.core.UpdateViewType;
import mycellar.core.datas.history.History;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.placesmanagement.Part;
import mycellar.placesmanagement.Rangement;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

import static mycellar.ProgramConstants.DATE_FORMATER_DDMMYYYY;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.MyCellarSettings.DEVISE;
import static mycellar.core.MyCellarSettings.TRANCHE_PRIX;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 9.3
 * @since 24/05/22
 */
public final class Stat extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private static final long serialVersionUID = -5333602919958999440L;
  private static final int PRICE_BRACKET_DEFAULT = 50;
  private final MyCellarLabel comboLabel = new MyCellarLabel("Main.Storage", LabelProperty.SINGLE.withDoubleQuote());
  private final MyCellarSimpleLabel end = new MyCellarSimpleLabel();
  private final MyCellarSimpleLabel moy = new MyCellarSimpleLabel();
  private final MyCellarComboBox<MyCellarEnum> listOptions = new MyCellarComboBox<>();
  private final MyCellarComboBox<PlaceComboItem> listPlaces = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> listChart = new MyCellarComboBox<>();
  private final JPanel panel = new JPanel();
  private final JScrollPane scroll;
  private final PanelChart panelChart = new PanelChart();
  private final MyCellarButton options = new MyCellarButton("Main.Settings", LabelProperty.SINGLE.withThreeDashes());
  private final List<StatData> listPrice = new LinkedList<>();
  private final List<StatData> listYear = new LinkedList<>();
  private final List<StatData> listHistory = new LinkedList<>();
  private final List<StatData> listNumberBottles = new LinkedList<>();
  private final ConcurrentMap<Integer, LongAdder> mapDeletedPerYear = new ConcurrentHashMap<>();
  private final ConcurrentMap<Integer, LongAdder> mapAddedPerYear = new ConcurrentHashMap<>();
  private String[] annee;
  private boolean allPriceBrackets = true;

  public Stat() {
    Debug("Stats");
    MyCellarLabel definition = new MyCellarLabel("Stats.Type");
    end.setHorizontalAlignment(SwingConstants.RIGHT);
    moy.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.setLayout(new MigLayout("", "[][][grow]", ""));
    panel.setFont(FONT_PANEL);

    updateBouteilleCountLabel();

    options.addActionListener(this::options_actionPerformed);

    listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllStorages")));
    Program.getPlaces().forEach(rangement -> listPlaces.addItem(new PlaceComboItem(rangement)));

    listOptions.addItem(new MyCellarEnum(StatType.PLACE.ordinal(), getLabel("Stats.Storages")));
    listOptions.addItem(new MyCellarEnum(StatType.YEAR.ordinal(), getLabel("Stats.Years")));
    listOptions.addItem(new MyCellarEnum(StatType.PRICE.ordinal(), getLabel("Stats.Prices")));
    listOptions.addItem(new MyCellarEnum(StatType.HISTORY.ordinal(), getLabel("Stats.history")));

    scroll = new JScrollPane(panel);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    listOptions.addItemListener(this::typeStats_itemStateChanged);
    listPlaces.addItemListener(this::listStatOptionItemStateChanged);

    MyCellarLabel chartType = new MyCellarLabel("Stats.chartType");
    listChart.addItem(getLabel("Stats.chartBar"));
    listChart.addItem(getLabel("Stats.chartPie"));
    listChart.addItemListener(this::chartItemStateChanged);
    listChart.setEnabled(listOptions.getSelectedIndex() != 0);

    setLayout(new MigLayout("", "[][][grow]", "[][][]20px[grow][][]"));
    add(definition);
    add(listOptions, "wrap");
    add(comboLabel);
    add(listPlaces, "wrap");
    add(chartType);
    add(listChart, "wrap");
    add(scroll, "span 3, split 2, grow 30, hidemode 3");
    add(panelChart, "span 3, grow, wrap");
    add(options);
    add(end, "span 2, align right, wrap");
    add(moy, "span 3, align right, wrap");
    options.setEnabled(false);

    Debug("Stats Done");
  }

  private static void Debug(String text) {
    Program.Debug("Stat: " + text);
  }

  private void updateBouteilleCountLabel() {
    int nbItems = Program.getNbItems();
    end.setText(MessageFormat.format(getLabel("Stats.TotalItems", new LabelProperty(nbItems > 1)), nbItems));
  }

  private void chartItemStateChanged(ItemEvent itemEvent) {
    final MyCellarEnum selectedItem = getSelectedStatType();
    if (itemEvent.getStateChange() != ItemEvent.SELECTED || selectedItem == null) {
      return;
    }
    if (listChart.getSelectedIndex() == 0) {
      Debug("Bar Chart");
      if (selectedItem.getValue() == StatType.YEAR.ordinal()) {
        panelChart.setDataBarChart(listYear, getLabel("Stats.Years"));
      } else if (selectedItem.getValue() == StatType.PRICE.ordinal()) {
        panelChart.setDataBarChart(listPrice, getLabel("Stats.Prices"));
      }
    } else if (listChart.getSelectedIndex() == 1) {
      Debug("Pie Chart");
      if (selectedItem.getValue() == StatType.YEAR.ordinal()) {
        panelChart.setDataPieChart(listYear, getLabel("Stats.Years"));
      } else if (selectedItem.getValue() == StatType.PRICE.ordinal()) {
        panelChart.setDataPieChart(listPrice, getLabel("Stats.Prices"));
      }
    }
  }

  /**
   * typeStats_itemStateChanged: Fonction appelle lors d'un changement dans la
   * premiere liste.
   *
   * @param e
   */
  private void typeStats_itemStateChanged(ItemEvent e) {
    final MyCellarEnum selectedItem = getSelectedStatType();
    if (e.getStateChange() != ItemEvent.SELECTED || selectedItem == null) {
      return;
    }
    scroll.setVisible(true);
    listChart.setEnabled(true);
    if (selectedItem.getValue() == StatType.YEAR.ordinal()) {
      Debug("By year");
      annee = Arrays.stream(Program.getYearsArray()).mapToObj(Integer::toString).toArray(String[]::new);
    }
    fillListOptionsChart(selectedItem);
    if (selectedItem.getValue() == StatType.PLACE.ordinal()) {
      Debug("By place");
      listChart.setEnabled(false);
      options.setEnabled(false);
      panelChart.setPlacesChart();
    } else if (selectedItem.getValue() == StatType.HISTORY.ordinal()) {
      Debug("By history");
      listChart.setEnabled(false);
      options.setEnabled(false);
      scroll.setVisible(false);
    }
  }

  private MyCellarEnum getSelectedStatType() {
    return (MyCellarEnum) listOptions.getSelectedItem();
  }

  private void listStatOptionItemStateChanged(ItemEvent e) {
    final MyCellarEnum selectedItem = getSelectedStatType();
    if ((e != null && e.getStateChange() != ItemEvent.SELECTED) || selectedItem == null) {
      return;
    }
    if (selectedItem.getValue() == StatType.PLACE.ordinal()) {
      if (listPlaces.getSelectedIndex() == 0) {
        displayAllPlaces();
      } else {
        displayOnePlace();
      }
    } else if (selectedItem.getValue() == StatType.YEAR.ordinal()) {
      displayYear();
    } else if (selectedItem.getValue() == StatType.PRICE.ordinal()) {
      displayByPrice();
    } else if (selectedItem.getValue() == StatType.HISTORY.ordinal()) {
      if (listPlaces.getSelectedIndex() == 0) {
        displayHistory();
      } else {
        displayBottleNumbers();
      }
    }
  }

  private void displayByPrice() {
    listPlaces.setEnabled(true);
    boolean all_bracket = listPlaces.getSelectedIndex() == 0;
    if (all_bracket != allPriceBrackets) {
      allPriceBrackets = all_bracket;
      listPrice.clear();
    }
    panel.removeAll();
    panel.repaint();

    options.setEnabled(true);
    int tranche = Program.getCaveConfigInt(TRANCHE_PRIX, PRICE_BRACKET_DEFAULT);
    if (tranche <= 0) {
      tranche = PRICE_BRACKET_DEFAULT;
      Program.putCaveConfigInt(TRANCHE_PRIX, PRICE_BRACKET_DEFAULT);
    }

    if (listPrice.isEmpty()) {
      Map<Integer, Integer> mapPrixCount = new HashMap<>();
      int ss_prix = 0;

      for (IMyCellarObject b : Program.getStorage().getAllList()) {
        if (!b.hasPrice()) {
          ss_prix++;
          continue;
        }
        int prix_int = b.getPrice().intValue();
        if (mapPrixCount.containsKey(prix_int)) {
          mapPrixCount.put(prix_int, mapPrixCount.get(prix_int) + 1);
        } else {
          mapPrixCount.put(prix_int, 1);
        }
      }
      for (int i = 0; i <= Program.getMaxPrice(); i += tranche) {
        String label = MessageFormat.format(getLabel("Stats.FromTo"), i, (i + tranche - 1), Program.getCaveConfigString(DEVISE, ""));
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
      listPrice.add(new StatData(getLabel("Stats.NoPrice"), ss_prix));
    }
    for (StatData price : listPrice) {
      final int priceCount = price.getCount();
      if (all_bracket || priceCount > 0) {
        panel.add(new MyCellarSimpleLabel(price.getName()));
        panel.add(new MyCellarSimpleLabel(MessageFormat.format(getLabel("Main.severalItems", new LabelProperty(priceCount > 1)), priceCount)), "span 2, align right, wrap");
      }
    }
    panel.updateUI();
    end.setText(MessageFormat.format(getLabel("Stats.TotalPrice"), Program.sumAllPrices(), Program.getCaveConfigString(DEVISE, "")));
    final int bottlesCount = Program.getStorage().getBottlesCount();
    if (bottlesCount > 0) {
      moy.setText(MessageFormat.format(getLabel("Stats.AveragePrice"), (Program.sumAllPrices() / bottlesCount), Program.getCaveConfigString(DEVISE, "")));
    }
    panelChart.setDataPieChart(listPrice, getLabel("Stats.Prices"));
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
          listYear.add(new StatData(an, Program.getTotalObjectForYear(year)));
        }
      }
      if (Program.isWineType()) {
        listYear.add(new StatData(getLabel("Main.NV"), Program.getNbNonVintage()));
      }
      listYear.add(new StatData(getLabel("Main.Other"), Program.getTotalOtherYears()));
    }
    for (StatData data : listYear) {
      panel.add(new MyCellarSimpleLabel(data.getName()));
      final int dataCount = data.getCount();
      panel.add(new MyCellarSimpleLabel(MessageFormat.format(getLabel("Main.severalItems", new LabelProperty(dataCount > 1)), dataCount)), "span 2, align right, wrap");
    }
    panel.updateUI();
    panelChart.setDataPieChart(listYear, getLabel("Stats.Years"));
    end.setText(MessageFormat.format(getLabel("Stats.Items", LabelProperty.PLURAL), Program.getNbItems()));
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
      mapAddedPerYear.forEach((year, value) -> listHistory.add(new StatData(year * 100, getLabel("Stats.in") + SPACE + year, value.intValue())));
      mapDeletedPerYear.forEach((year, value) -> listHistory.add(new StatData(year * 100 + 1, getLabel("Stats.out") + SPACE + year, value.intValue())));
      listHistory.sort(Comparator.comparingInt(o -> o.id));
    }
    final JFreeChart chart = panelChart.setDataBarChart(listHistory, getLabel("Stats.inout"));
    CategoryPlot cplot = (CategoryPlot) chart.getPlot();
    ((BarRenderer) cplot.getRenderer()).setBarPainter(new StandardBarPainter());

    BarRenderer r = (BarRenderer) chart.getCategoryPlot().getRenderer();
    for (int i = 0; i < listHistory.size(); i++) {
      r.setSeriesPaint(i, i % 2 == 0 ? Color.blue : Color.red);
    }
  }

  private void displayBottleNumbers() {
    Debug("By Bottles Numbers");
    panel.removeAll();
    options.setEnabled(false);
    moy.setText("");

    if (listNumberBottles.isEmpty()) {
      Program.getHistory()
          .stream()
          .filter(History::hasTotalBottle)
          .sorted(Comparator.comparing(History::getLocaleDate))
          .forEach(history -> listNumberBottles.add(new StatData(history.getLocaleDate().format(DATE_FORMATER_DDMMYYYY), history.getTotalBottle())));
    }
    panelChart.setLineChart(listNumberBottles, getLabel("Stats.bottleCount", LabelProperty.PLURAL));
  }

  private void displayOnePlace() {
    Debug("One place");
    panel.removeAll();
    panel.repaint();
    end.setText("");
    moy.setText("");
    options.setEnabled(false);
    PlaceComboItem placeComboItem = (PlaceComboItem) listPlaces.getSelectedItem();
    int nbItems = 0;
    if (placeComboItem != null && placeComboItem.getRangement() != null) {
      Rangement rangement = placeComboItem.getRangement();
      panelChart.setPlaceChart(rangement);
      nbItems = rangement.getTotalCountCellUsed();
      panel.add(new MyCellarSimpleLabel(rangement.getName()));
      displayPlace(rangement);
    }
    end.setText(MessageFormat.format(getLabel("Stats.Items", LabelProperty.PLURAL), nbItems));
  }

  private void displayPlace(Rangement cave) {
    final int nbEmplacements = cave.getNbParts();
    final int nbCaseUseAll = cave.getTotalCountCellUsed();
    final MyCellarLabel list_num_empl;
    if (nbEmplacements == 1) {
      list_num_empl = new MyCellarLabel("Stats.1Storage");
    } else {
      list_num_empl = new MyCellarLabel("Stats.NStorage", LabelProperty.SINGLE, Integer.toString(nbEmplacements));
    }
    final MyCellarSimpleLabel list_nb_bottle = new MyCellarSimpleLabel(MessageFormat.format(getLabel("Main.severalItems", new LabelProperty(nbCaseUseAll > 1)), nbCaseUseAll));
    panel.add(list_num_empl);
    panel.add(list_nb_bottle, "span 2, align right, wrap");
    if (!cave.isSimplePlace()) {
      displayNbBottlePlace(cave);
    }
    panel.add(new JSeparator(), "span 3, wrap");
  }

  private void displayAllPlaces() {
    Debug("All places");
    panelChart.setPlacesChart();
    panel.removeAll();
    panel.repaint();

    int nbBottle = 0;
    for (Rangement cave : Program.getPlaces()) {
      panel.add(new MyCellarSimpleLabel(cave.getName()));
      nbBottle += cave.getTotalCountCellUsed();
      displayPlace(cave);
    }
    moy.setText("");
    end.setText(MessageFormat.format(getLabel("Stats.TotalItems", new LabelProperty(nbBottle > 1)), nbBottle));
  }

  private void displayNbBottlePlace(Rangement cave) {
    for (int j = 0; j < cave.getNbParts(); j++) {
      panel.add(new MyCellarLabel("Stats.StorageNumber", LabelProperty.SINGLE, Integer.toString(j + 1)));
      panel.add(new MyCellarLabel("Main.severalItems", new LabelProperty(cave.getTotalCountCellUsed() > 1), Integer.toString(cave.getTotalCellUsed(j))), "span 2, align right, wrap");
    }
  }

  private void mapToAddedDeletedStat(History history) {
    if (history.isDeleted()) {
      mapDeletedPerYear.computeIfAbsent(history.getLocaleDate().getYear(), integer -> new LongAdder()).increment();
    } else {
      mapAddedPerYear.computeIfAbsent(history.getLocaleDate().getYear(), integer -> new LongAdder()).increment();
    }
  }

  private void options_actionPerformed(ActionEvent e) {
    Debug("options_actionPerforming...");
    options.setSelected(false);
    String value = JOptionPane.showInputDialog(this, getLabel("Stats.QuestionPriceBracket"));
    if (StringUtils.isNumeric(value)) {
      Program.putCaveConfigInt(TRANCHE_PRIX, Integer.parseInt(value));
      listPrice.clear();
      listStatOptionItemStateChanged(null);
    }
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    return true;
  }

  @Override
  public void tabClosed() {
    listHistory.clear();
    listNumberBottles.clear();
    listPrice.clear();
    listYear.clear();
    mapAddedPerYear.clear();
    mapDeletedPerYear.clear();
    Start.getInstance().updateMainPanel();
  }

  @Override
  public void setUpdateViewType(UpdateViewType updateViewType) {
  }

  @Override
  public void updateView() {
    listYear.clear();
    listPrice.clear();
    listNumberBottles.clear();
    listHistory.clear();
    mapAddedPerYear.clear();
    mapDeletedPerYear.clear();
    fillListOptionsChart(getSelectedStatType());
    listStatOptionItemStateChanged(null);
    updateBouteilleCountLabel();
  }

  private void fillListOptionsChart(MyCellarEnum selectedStatType) {
    listPlaces.removeAllItems();
    listPlaces.setEnabled(false);
    if (selectedStatType.getValue() == StatType.PLACE.ordinal()) {
      listPlaces.setEnabled(true);
      comboLabel.setText(getLabel("Main.Storage", LabelProperty.SINGLE.withDoubleQuote()));
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllStorages")));
      Program.getPlaces().forEach(rangement -> listPlaces.addItem(new PlaceComboItem(rangement)));
    } else if (selectedStatType.getValue() == StatType.HISTORY.ordinal()) {
      listPlaces.setEnabled(true);
      comboLabel.setText("");
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.inout")));
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.bottleCount", LabelProperty.PLURAL)));
    } else if (selectedStatType.getValue() == StatType.PRICE.ordinal()) {
      listPlaces.setEnabled(true);
      comboLabel.setText(getLabel("Stats.PriceBracket"));
      listPlaces.removeAllItems();
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllBrackets")));
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.BracketsWith", LabelProperty.PLURAL)));
    } else if (selectedStatType.getValue() == StatType.YEAR.ordinal()) {
      comboLabel.setText("");
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllYears")));
    }
    listPlaces.setSelectedIndex(0);
  }

  enum StatType {
    PLACE,
    YEAR,
    PRICE,
    HISTORY
  }

  private static final class PanelChart extends JPanel {

    private static final long serialVersionUID = -6697139633950076186L;

    private PanelChart() {
      setLayout(new MigLayout("", "grow", "grow"));
      setPlacesChart();
    }

    private void setPlacesChart() {
      DefaultPieDataset dataset = new DefaultPieDataset();
      Program.getPlaces().stream()
          .filter(Objects::nonNull)
          .forEach(rangement -> dataset.setValue(rangement.getName(), rangement.getTotalCountCellUsed()));

      JFreeChart chart = ChartFactory.createPieChart(getLabel("Stats.AllStorages"),          // chart title
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
      if (rangement.isSimplePlace()) {
        return;
      }
      DefaultPieDataset dataset = new DefaultPieDataset();
      for (Part part : rangement.getPlace()) {
        dataset.setValue(MessageFormat.format(getLabel("Stats.StorageNumber"), part.getNum() + 1), rangement.getTotalCellUsed(part.getNum()));
      }
      JFreeChart chart = ChartFactory.createPieChart(rangement.getName(),          // chart title
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
      datas.stream()
          .filter(statData -> statData.getCount() > 0)
          .forEach(statData -> dataset.setValue(statData.getName(), statData.getCount()));

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
      datas.stream()
          .filter(statData -> statData.getCount() > 0)
          .forEach(statData -> dataset.addValue(statData.getCount(), statData.getName(), statData.getName()));
      JFreeChart chart = ChartFactory.createBarChart3D(title,          // chart title
          "", getLabel("Stats.count"),
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

    private void setLineChart(List<StatData> datas, String title) {
      removeAll();

      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      datas.forEach(statData -> dataset.addValue(statData.getCount(), title, statData.getName()));

      final JFreeChart chart = ChartFactory.createLineChart(title,
          null, getLabel("Stats.count"),
          dataset, PlotOrientation.VERTICAL, true, true, false);
      ChartPanel chartPanel = new ChartPanel(chart);
      add(chartPanel, "grow");
      updateUI();
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

  static class PlaceComboItem {

    private final String label;
    private final Rangement rangement;

    private PlaceComboItem(Rangement rangement) {
      this.rangement = rangement;
      label = rangement.getName();
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
