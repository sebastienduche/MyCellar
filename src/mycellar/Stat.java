package mycellar;

import mycellar.core.IMyCellar;
import mycellar.core.IMyCellarEnum;
import mycellar.core.IMyCellarObject;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarObject;
import mycellar.core.UpdateViewType;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.history.History;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.Part;
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
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.DATE_FORMATER_DDMMYYYY;
import static mycellar.ProgramConstants.FONT_PANEL;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.core.MyCellarSettings.DEVISE;
import static mycellar.core.MyCellarSettings.TRANCHE_PRIX;
import static mycellar.core.text.LabelProperty.PLURAL;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.core.text.MyCellarLabelManagement.getLabelForType;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ResourceKey.MAIN_NAME;
import static mycellar.general.ResourceKey.MAIN_SEVERALITEMS;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2003
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 10.6
 * @since 08/03/25
 */
public final class Stat extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private static final int PRICE_BRACKET_DEFAULT = 50;
  private final MyCellarLabel comboLabel = new MyCellarLabel("Main.Storage", LabelProperty.SINGLE.withDoubleQuote());
  private final MyCellarSimpleLabel end = new MyCellarSimpleLabel();
  private final MyCellarSimpleLabel moy = new MyCellarSimpleLabel();
  private final MyCellarComboBox<StatsEnum> listStatsType = new MyCellarComboBox<>();
  private final MyCellarComboBox<PlaceComboItem> listPlaces = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> listChart = new MyCellarComboBox<>();
  private final JPanel panel = new JPanel();
  private final JScrollPane scroll;
  private final PanelChart panelChart = new PanelChart();
  private final JPanel panelOther = new JPanel();
  private final MyCellarButton options = new MyCellarButton("Main.Settings", LabelProperty.SINGLE.withThreeDashes());
  private final List<StatData> listPrice = new LinkedList<>();
  private final List<StatData> listYear = new LinkedList<>();
  private final List<StatData> listHistory = new LinkedList<>();
  private final List<StatData> listNumberBottles = new LinkedList<>();
  private final ConcurrentMap<Integer, LongAdder> mapDeletedPerYear = new ConcurrentHashMap<>();
  private final ConcurrentMap<Integer, LongAdder> mapAddedPerYear = new ConcurrentHashMap<>();
  private String[] years;
  private boolean allPriceBrackets = true;

  public Stat() {
    Debug("Stats");
    end.setHorizontalAlignment(SwingConstants.RIGHT);
    moy.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.setLayout(new MigLayout("", "[][][grow]", ""));
    panel.setFont(FONT_PANEL);

    updateCountLabel();

    options.addActionListener(this::options_actionPerformed);

    listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllStorages")));
    Program.getAbstractPlaces().forEach(abstractPlace -> listPlaces.addItem(new PlaceComboItem(abstractPlace)));

    listStatsType.addItem(StatsEnum.PLACE);
    listStatsType.addItem(StatsEnum.YEAR);
    listStatsType.addItem(StatsEnum.PRICE);
    listStatsType.addItem(StatsEnum.HISTORY);
    listStatsType.addItem(StatsEnum.OBJECT);

    scroll = new JScrollPane(panel);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    listStatsType.addItemListener(this::typeStats_itemStateChanged);
    listPlaces.addItemListener(this::listStatOptionItemStateChanged);

    listChart.addItem(getLabel("Stats.ChartBar"));
    listChart.addItem(getLabel("Stats.ChartPie"));
    listChart.addItemListener(this::chartItemStateChanged);
    listChart.setEnabled(listStatsType.getSelectedIndex() != 0);

    panelOther.setLayout(new MigLayout("", "grow", "grow"));

    setLayout(new MigLayout("", "[][][grow]", "[][][]20px[grow][][]"));
    add(new MyCellarLabel("Stats.Type"));
    add(listStatsType, "wrap");
    add(comboLabel);
    add(listPlaces, "wrap");
    add(new MyCellarLabel("Stats.ChartType"));
    add(listChart, "wrap");
    add(scroll, "span 3, split 2, grow 30, hidemode 3");
    add(panelChart, "span 3, grow, hidemode 3, wrap");
    add(panelOther, "span 3, grow, hidemode 3, wrap");
    add(options);
    add(end, "span 2, align right, wrap");
    add(moy, "span 3, align right, wrap");
    options.setEnabled(false);

    Debug("Stats Done");
  }

  private static void Debug(String text) {
    Program.Debug("Stat: " + text);
  }

  private void updateCountLabel() {
    int nbItems = Program.getNbItems();
    end.setText(MessageFormat.format(getLabelWithProperty("Stats.TotalItems", new LabelProperty(nbItems > 1)), nbItems));
  }

  private void chartItemStateChanged(ItemEvent itemEvent) {
    final StatsEnum selectedItem = getSelectedStatType();
    if (itemEvent.getStateChange() != ItemEvent.SELECTED || selectedItem == null) {
      return;
    }
    if (listChart.getSelectedIndex() == 0) {
      Debug("Bar Chart");
      if (selectedItem == StatsEnum.YEAR) {
        panelChart.setDataBarChart(listYear, getLabel("Stats.Years"));
      } else if (selectedItem == StatsEnum.PRICE) {
        panelChart.setDataBarChart(listPrice, getLabel("Stats.Prices"));
      }
    } else if (listChart.getSelectedIndex() == 1) {
      Debug("Pie Chart");
      if (selectedItem == StatsEnum.YEAR) {
        panelChart.setDataPieChart(listYear, getLabel("Stats.Years"));
      } else if (selectedItem == StatsEnum.PRICE) {
        panelChart.setDataPieChart(listPrice, getLabel("Stats.Prices"));
      }
    }
  }

  /**
   * typeStats_itemStateChanged: Fonction appelle lors d'un changement dans la
   * premiere liste.
   */
  private void typeStats_itemStateChanged(ItemEvent e) {
    final StatsEnum selectedItem = getSelectedStatType();
    if (e.getStateChange() != ItemEvent.SELECTED || selectedItem == null) {
      return;
    }
    panelOther.setVisible(false);
    scroll.setVisible(true);
    listChart.setEnabled(true);
    if (selectedItem == StatsEnum.YEAR) {
      Debug("By year");
      years = Arrays.stream(Program.getYearsArray()).mapToObj(Integer::toString).toArray(String[]::new);
    }
    fillListOptionsChart(selectedItem);
    if (selectedItem == StatsEnum.PLACE) {
      Debug("By place");
      listChart.setEnabled(false);
      options.setEnabled(false);
      panelChart.setPlacesChart();
    } else if (selectedItem == StatsEnum.HISTORY) {
      Debug("By history");
      listChart.setEnabled(false);
      options.setEnabled(false);
      scroll.setVisible(false);
    } else if (selectedItem == StatsEnum.OBJECT) {
      Debug("By object");
      listChart.setEnabled(false);
      options.setEnabled(false);
    }
  }

  private StatsEnum getSelectedStatType() {
    return (StatsEnum) listStatsType.getSelectedItem();
  }

  private void listStatOptionItemStateChanged(ItemEvent e) {
    final StatsEnum selectedItem = getSelectedStatType();
    if ((e != null && e.getStateChange() != ItemEvent.SELECTED) || selectedItem == null) {
      return;
    }
    if (selectedItem == StatsEnum.PLACE) {
      if (listPlaces.getSelectedIndex() == 0) {
        displayAllPlaces();
      } else {
        displayOnePlace();
      }
    } else if (selectedItem == StatsEnum.YEAR) {
      displayYear();
    } else if (selectedItem == StatsEnum.PRICE) {
      displayByPrice();
    } else if (selectedItem == StatsEnum.HISTORY) {
      if (listPlaces.getSelectedIndex() == 0) {
        displayHistory();
      } else {
        displayBottleNumbers();
      }
    } else if (selectedItem == StatsEnum.OBJECT) {
      displayByObject();
    }
  }

  private void displayByPrice() {
    listPlaces.setEnabled(true);
    panelChart.setVisible(true);
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
      Map<Integer, Integer> mapPriceCount = new HashMap<>();
      int withoutPrice = 0;

      for (MyCellarObject b : Program.getStorage().getAllList()) {
        if (!b.hasPrice()) {
          withoutPrice++;
          continue;
        }
        int price = b.getPrice().intValue();
        if (mapPriceCount.containsKey(price)) {
          mapPriceCount.computeIfPresent(price, (key, val) -> val + 1);
        } else {
          mapPriceCount.put(price, 1);
        }
      }
      for (int i = 0; i <= Program.getMaxPrice(); i += tranche) {
        String label = MessageFormat.format(getLabel("Stats.FromTo"), i, (i + tranche - 1), Program.getCaveConfigString(DEVISE, ""));
        int nb = 0;
        for (int j = i; j < (i + tranche); j++) {
          if (mapPriceCount.containsKey(j)) {
            nb += mapPriceCount.get(j);
          }
        }
        if (all_bracket || nb > 0) {
          listPrice.add(new StatData(label, nb));
        }
      }
      listPrice.add(new StatData(getLabel("Stats.NoPrice"), withoutPrice));
    }
    for (StatData price : listPrice) {
      final int priceCount = price.getCount();
      if (all_bracket || priceCount > 0) {
        panel.add(new MyCellarSimpleLabel(price.getName()));
        panel.add(new MyCellarSimpleLabel(MessageFormat.format(getLabelWithProperty(MAIN_SEVERALITEMS, new LabelProperty(priceCount > 1)), priceCount)), "span 2, align right, wrap");
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
      for (String value : years) {
        int year = Integer.parseInt(value.strip());
        if (year > 1000 && year < 9000) {
          listYear.add(new StatData(value, Program.getTotalObjectForYear(year)));
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
      panel.add(new MyCellarSimpleLabel(MessageFormat.format(getLabelWithProperty(MAIN_SEVERALITEMS, new LabelProperty(dataCount > 1)), dataCount)), "span 2, align right, wrap");
    }
    panel.updateUI();
    panelChart.setVisible(true);
    panelChart.setDataPieChart(listYear, getLabel("Stats.Years"));
    end.setText(MessageFormat.format(getLabelWithProperty("Stats.Items", LabelProperty.PLURAL), Program.getNbItems()));
  }

  private void displayHistory() {
    Debug("By history");
    panel.removeAll();
    options.setEnabled(false);
    panelChart.setVisible(true);
    moy.setText("");

    if (listHistory.isEmpty()) {
      Program.getHistory()
          .stream()
          .filter(History::isAddedOrDeleted)
          .forEach(this::mapToAddedDeletedStat);
      mapAddedPerYear.forEach((year, value) -> listHistory.add(new StatData(year * 100, getLabel("Stats.In") + SPACE + year, value.intValue())));
      mapDeletedPerYear.forEach((year, value) -> listHistory.add(new StatData(year * 100 + 1, getLabel("Stats.Out") + SPACE + year, value.intValue())));
      listHistory.sort(Comparator.comparingInt(o -> o.id));
    }
    final JFreeChart chart = panelChart.setDataBarChart(listHistory, getLabel("Stats.Inout"));
    CategoryPlot cplot = (CategoryPlot) chart.getPlot();
    ((BarRenderer) cplot.getRenderer()).setBarPainter(new StandardBarPainter());

    BarRenderer r = (BarRenderer) chart.getCategoryPlot().getRenderer();
    for (int i = 0; i < listHistory.size(); i++) {
      r.setSeriesPaint(i, i % 2 == 0 ? Color.blue : Color.red);
    }
  }

  private void displayByObject() {
    Debug("By object");
    panel.removeAll();
    panelOther.removeAll();
    options.setEnabled(false);
    moy.setText("");

    panel.add(new MyCellarSimpleLabel(MessageFormat.format(getLabelWithProperty("Stats.Items", LabelProperty.PLURAL), "")));
    panel.add(new MyCellarSimpleLabel(Integer.toString(Program.getNbItems())), "span 2, align right, wrap");
    panel.add(new MyCellarSimpleLabel(getLabelWithProperty("Stats.UniqueItems", LabelProperty.PLURAL)));
    panel.add(new MyCellarSimpleLabel(Integer.toString(Program.getStorage().getDistinctNames().size())), "span 2, align right, gapbottom 10px, wrap");
    if (Program.isWineType()) {
      panel.add(new MyCellarSimpleLabel(getLabel("Stats.ByColor")), "wrap");
      final Map<String, Long> collect = Program.getStorage().getAllList()
          .stream()
          .map(o -> (Bouteille) o)
          .collect(Collectors.groupingBy(Bouteille::getColor, Collectors.counting()));
      collect.forEach((color, value) -> {
        String label = BottleColor.getColor(color).toString();
        if (MyCellarUtils.isNullOrEmpty(label)) {
          label = getLabel("Stats.Unknown");
        }
        panel.add(new MyCellarSimpleLabel(label));
        panel.add(new MyCellarSimpleLabel(Long.toString(value)), "span 2, align right, wrap");
      });
    }

    // List of unique names and count
    final Map<String, Long> uniqueNamesCount = Program.getStorage().getAllList()
        .stream()
        .collect(Collectors.groupingBy(IMyCellarObject::getNom, Collectors.counting()));
    final Object[] sortedKeys = uniqueNamesCount.entrySet().stream().sorted(Map.Entry.comparingByKey()).toArray();
    String[][] tableValues = new String[sortedKeys.length][2];
    for (int i = 0; i < sortedKeys.length; i++) {
      Map.Entry<String, Long> entry = (Map.Entry<String, Long>) sortedKeys[i];
      tableValues[i][0] = entry.getKey();
      tableValues[i][1] = entry.getValue().toString();
    }
    final DefaultTableModel defaultTableModel = new DefaultTableModel(tableValues, new String[]{getLabel(MAIN_NAME), getLabel("Stats.Count")}) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    final JTable table = new JTable(defaultTableModel);
    final TableColumnModel columnModel = table.getColumnModel();
    final TableColumn column = columnModel.getColumn(1);
    column.setMinWidth(50);
    column.setMaxWidth(50);
    panelOther.setLayout(new MigLayout("", "grow", "[][grow]"));
    panelOther.add(new MyCellarSimpleLabel(getLabelWithProperty("Stats.NbObjectPerLabel", PLURAL)), "wrap");
    panelOther.add(new JScrollPane(table), "grow");
    panel.repaint();
    panelChart.setVisible(false);
    panelOther.setVisible(true);
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
    panelChart.setLineChart(listNumberBottles, getLabelWithProperty("Stats.BottleCount", LabelProperty.PLURAL));
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
      AbstractPlace abstractPlace = placeComboItem.getRangement();
      if (!abstractPlace.isSimplePlace()) {
        panelChart.setPlaceChart((ComplexPlace) abstractPlace);
      }
      nbItems = abstractPlace.getTotalCountCellUsed();
      panel.add(new MyCellarSimpleLabel(abstractPlace.getName()));
      displayPlace(abstractPlace);
    }
    end.setText(MessageFormat.format(getLabelWithProperty("Stats.Items", LabelProperty.PLURAL), nbItems));
  }

  private void displayPlace(AbstractPlace abstractPlace) {
    final int partCount = abstractPlace.getPartCount();
    final int nbCaseUseAll = abstractPlace.getTotalCountCellUsed();
    final MyCellarLabel list_num_empl;
    if (partCount == 1) {
      list_num_empl = new MyCellarLabel("Stats.1Shelve");
    } else {
      list_num_empl = new MyCellarLabel("Stats.NShelves", LabelProperty.SINGLE, Integer.toString(partCount));
    }
    panel.add(list_num_empl);
    panel.add(new MyCellarSimpleLabel(MessageFormat.format(getLabelWithProperty(MAIN_SEVERALITEMS, new LabelProperty(nbCaseUseAll > 1)), nbCaseUseAll)), "span 2, align right, wrap");
    if (!abstractPlace.isSimplePlace()) {
      displayNbBottlePlace(abstractPlace);
    }
    panel.add(new JSeparator(), "span 3, wrap");
  }

  private void displayAllPlaces() {
    Debug("All places");
    panelChart.setVisible(true);
    panelChart.setPlacesChart();
    panel.removeAll();
    panel.repaint();

    int countItems = 0;
    for (AbstractPlace abstractPlace : Program.getAbstractPlaces()) {
      panel.add(new MyCellarSimpleLabel(abstractPlace.getName()));
      countItems += abstractPlace.getTotalCountCellUsed();
      displayPlace(abstractPlace);
    }
    moy.setText("");
    end.setText(MessageFormat.format(getLabelWithProperty("Stats.TotalItems", new LabelProperty(countItems > 1)), countItems));
  }

  private void displayNbBottlePlace(AbstractPlace abstractPlace) {
    for (int j = 0; j < abstractPlace.getPartCount(); j++) {
      panel.add(new MyCellarLabel("Stats.ShelveNumber", LabelProperty.SINGLE, Integer.toString(j + 1)));
      panel.add(new MyCellarLabel(MAIN_SEVERALITEMS, new LabelProperty(abstractPlace.getTotalCountCellUsed() > 1), Integer.toString(abstractPlace.getCountCellUsed(j))), "span 2, align right, wrap");
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
  public void tabClosed() {
    listHistory.clear();
    listNumberBottles.clear();
    listPrice.clear();
    listYear.clear();
    mapAddedPerYear.clear();
    mapDeletedPerYear.clear();
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
    updateCountLabel();
  }

  private void fillListOptionsChart(StatsEnum selectedStatType) {
    listPlaces.removeAllItems();
    listPlaces.setEnabled(false);
    if (selectedStatType == StatsEnum.PLACE) {
      listPlaces.setEnabled(true);
      comboLabel.setText(getLabelWithProperty("Main.Storage", LabelProperty.SINGLE.withDoubleQuote()));
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllStorages")));
      Program.getAbstractPlaces().forEach(rangement -> listPlaces.addItem(new PlaceComboItem(rangement)));
    } else if (selectedStatType == StatsEnum.HISTORY) {
      listPlaces.setEnabled(true);
      comboLabel.setText("");
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.Inout")));
      listPlaces.addItem(new PlaceComboItem(getLabelWithProperty("Stats.BottleCount", LabelProperty.PLURAL)));
    } else if (selectedStatType == StatsEnum.PRICE) {
      listPlaces.setEnabled(true);
      comboLabel.setText(getLabel("Stats.PriceBracket"));
      listPlaces.removeAllItems();
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllBrackets")));
      listPlaces.addItem(new PlaceComboItem(getLabelWithProperty("Stats.BracketsWith", LabelProperty.PLURAL)));
    } else if (selectedStatType == StatsEnum.YEAR) {
      comboLabel.setText("");
      listPlaces.addItem(new PlaceComboItem(getLabel("Stats.AllYears")));
    } else if (selectedStatType == StatsEnum.OBJECT) {
      comboLabel.setText("");
      listPlaces.addItem(new PlaceComboItem(getLabelWithProperty("Stats.BottleCount", LabelProperty.PLURAL)));
    }
    listPlaces.setSelectedIndex(0);
  }

  private static final class PanelChart extends JPanel {

    private PanelChart() {
      setLayout(new MigLayout("", "grow", "grow"));
      setPlacesChart();
    }

    private void setPlacesChart() {
      DefaultPieDataset dataset = new DefaultPieDataset();
      Program.getAbstractPlaces().stream()
          .filter(Objects::nonNull)
          .forEach(abstractPlace -> dataset.setValue(abstractPlace.getName(), abstractPlace.getTotalCountCellUsed()));

      JFreeChart chart = ChartFactory.createPieChart(getLabel("Stats.AllStorages"),          // chart title
          dataset,                // data
          false,                   // include legend
          true,
          false);

      ChartPanel chartPanel = new ChartPanel(chart);
      removeAll();
      add(chartPanel, "grow");
    }

    private void setPlaceChart(ComplexPlace complexPlace) {
      removeAll();
      DefaultPieDataset dataset = new DefaultPieDataset();
      for (Part part : complexPlace.getParts()) {
        dataset.setValue(MessageFormat.format(getLabel("Stats.ShelveNumber"), part.getNumberAsDisplay()), complexPlace.getCountCellUsed(part));
      }
      JFreeChart chart = ChartFactory.createPieChart(complexPlace.getName(),          // chart title
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
          "", getLabel("Stats.Count"),
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
          null, getLabel("Stats.Count"),
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
    private final AbstractPlace rangement;

    private PlaceComboItem(AbstractPlace rangement) {
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

    public AbstractPlace getRangement() {
      return rangement;
    }
  }

  private enum StatsEnum implements IMyCellarEnum {
    PLACE(0, getLabel("Stats.Storages")),
    YEAR(1, getLabel("Stats.Years")),
    PRICE(2, getLabel("Stats.Prices")),
    HISTORY(3, getLabel("Stats.History")),
    OBJECT(4, getLabelForType(Program.getProgramType(), PLURAL.withCapital()));

    private final int index;
    private final String label;

    StatsEnum(int index, String label) {
      this.index = index;
      this.label = label;
    }

    @Override
    public int getValue() {
      return index;
    }

    @Override
    public String toString() {
      return label;
    }
  }
}
