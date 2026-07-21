package mycellar.showfile;


import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.actions.OpenAddVinAction;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellar;
import mycellar.core.IMyCellarEnum;
import mycellar.core.IUpdatable;
import mycellar.core.UpdateViewType;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.frame.MainFrame;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.PanelPlacePosition;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.ComplexPlace;
import mycellar.placesmanagement.places.PlacePosition;
import mycellar.placesmanagement.places.PlaceUtils;
import mycellar.placesmanagement.places.SimplePlace;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static mycellar.MyCellarUtils.convertStringFromHTMLString;
import static mycellar.MyCellarUtils.parseIntOrError;
import static mycellar.MyCellarUtils.safeStringToBigDecimal;
import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.core.common.MyCellarFields.AOC;
import static mycellar.core.common.MyCellarFields.COLOR;
import static mycellar.core.common.MyCellarFields.COLUMN;
import static mycellar.core.common.MyCellarFields.COMMENT;
import static mycellar.core.common.MyCellarFields.COUNTRY;
import static mycellar.core.common.MyCellarFields.IGP;
import static mycellar.core.common.MyCellarFields.LINE;
import static mycellar.core.common.MyCellarFields.MATURITY;
import static mycellar.core.common.MyCellarFields.NAME;
import static mycellar.core.common.MyCellarFields.NUM_PLACE;
import static mycellar.core.common.MyCellarFields.PARKER;
import static mycellar.core.common.MyCellarFields.PLACE;
import static mycellar.core.common.MyCellarFields.PRICE;
import static mycellar.core.common.MyCellarFields.STATUS;
import static mycellar.core.common.MyCellarFields.TYPE;
import static mycellar.core.common.MyCellarFields.VINEYARD;
import static mycellar.core.common.MyCellarFields.YEAR;
import static mycellar.core.common.MyCellarFields.getFieldsList;
import static mycellar.core.common.MyCellarFields.getFieldsListForImportAndWorksheet;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_1ITEMSELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_ALREADYINSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_CANTMODIFYSTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRM1DELETE;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMNDELETE;
import static mycellar.general.ResourceErrorKey.ERROR_ENTERNUMERICVALUEABOVEZERO;
import static mycellar.general.ResourceErrorKey.ERROR_ENTERVALIDYEAR;
import static mycellar.general.ResourceErrorKey.ERROR_INEXISTINGBOTTLE;
import static mycellar.general.ResourceErrorKey.ERROR_NITEMSSELECTED;
import static mycellar.general.ResourceErrorKey.ERROR_NOITEMTODELETE;
import static mycellar.general.ResourceErrorKey.ERROR_NOITEMTOMODIFY;
import static mycellar.general.ResourceErrorKey.ERROR_NOTENOUGHSPACESTORAGE;
import static mycellar.general.ResourceErrorKey.ERROR_PLEASESELECT;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTITEMTOMODIFY;
import static mycellar.general.ResourceErrorKey.ERROR_SELECTSTORAGE;
import static mycellar.general.ResourceKey.BOUTEILLE_TEMPORARYPLACE;
import static mycellar.general.ResourceKey.HISTORY_TOCHECK;
import static mycellar.general.ResourceKey.HISTORY_VALIDATED;
import static mycellar.general.ResourceKey.MAIN_CHOOSECELL;
import static mycellar.general.ResourceKey.MAIN_COLUMNS;
import static mycellar.general.ResourceKey.MAIN_MODIFY;
import static mycellar.general.ResourceKey.MAIN_NUMBEROFITEMS;
import static mycellar.general.ResourceKey.SHOWFILE_MORE;
import static mycellar.general.ResourceKey.SHOWFILE_NOBOTTLETORESTORE;
import static mycellar.general.ResourceKey.SHOWFILE_RESTOREONE;
import static mycellar.general.ResourceKey.SHOWFILE_RESTORESEVERAL;
import static mycellar.general.ResourceKey.SHOWFILE_SELECTTORESTORE;
import static mycellar.general.ResourceKey.SHOWFILE_STATUS;
import static mycellar.general.ResourceKey.SHOWFILE_VALID;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Societe : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.4
 * @since 27/06/26
 */

public abstract class AbstractShowFilePanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  final MyCellarSimpleLabel titleLabel = new MyCellarSimpleLabel();
  final MyCellarLabel labelCount = new MyCellarLabel(MAIN_NUMBEROFITEMS, "");
  final MyCellarButton deleteButton = new MyCellarButton(MyCellarImage.DELETE);
  final MyCellarButton modifyButton = new MyCellarButton(MAIN_MODIFY, new ModifyBottlesAction());
  final MyCellarComboBox<AbstractPlace> placeCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<BottleColor> colorCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<BottlesStatus> statusCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<State> verifyStatusCbx = new MyCellarComboBox<>();
  private final ShowFileColumn<State> checkedButtonColumn;
  private boolean updateView = false;
  private UpdateViewType updateViewType;
  final MyCellarComboBox<String> typeCbx = new MyCellarComboBox<>();
  final List<ShowFileColumn<?>> availableColumns = new ArrayList<>();
  final Set<Bouteille> workingBottles = new LinkedHashSet<>();
  TableShowValues model;
  JTable table;
  ShowFileColumn<Boolean> checkBoxStartColumn;
  ShowFileColumn<?> modifyButtonColumn;

  AbstractShowFilePanel(boolean worksheet) {
    checkBoxStartColumn = new ShowFileColumn<>(25, true, true, "", Boolean.FALSE) {
      @Override
      void setValue(Bouteille b, Boolean value) {
        setMapValue(b, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return getMapValue(b);
      }
    };
    availableColumns.add(checkBoxStartColumn);
    availableColumns.add(new ShowFileColumn<>(NAME) {

      @Override
      void setValue(Bouteille b, Object value) {
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return convertStringFromHTMLString(b.getNom());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(YEAR, 50) {

      @Override
      void setValue(Bouteille b, String value) {
        if (Program.hasYearControl() && Bouteille.isInvalidYear(value)) {
          Erreur.showSimpleErreur(getError(ERROR_ENTERVALIDYEAR));
        } else {
          setStringValue(b, value);
        }
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return b.getAnnee();
      }
    });
    availableColumns.add(new ShowFileColumn<String>(TYPE) {

      @Override
      void setValue(Bouteille b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return b.getKind();
      }
    });
    availableColumns.add(new ShowFileColumn<AbstractPlace>(PLACE) {

      @Override
      void setValue(Bouteille b, AbstractPlace value) {
        if (Program.EMPTY_PLACE.equals(value)) {
          Erreur.showSimpleErreur(getError(ERROR_SELECTSTORAGE));
          return;
        }
        setPlaceValue(b, PLACE, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.isInTemporaryStock()) {
          return getLabel(BOUTEILLE_TEMPORARYPLACE);
        }
        return convertStringFromHTMLString(b.getEmplacement());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(NUM_PLACE, 50) {

      @Override
      void setValue(Bouteille b, String value) {
        setPlaceValue(b, NUM_PLACE, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return Integer.toString(b.getNumLieu());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(LINE, 50) {

      @Override
      void setValue(Bouteille b, String value) {
        setPlaceValue(b, LINE, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getAbstractPlace().isSimplePlace()) {
          return "";
        }
        return Integer.toString(b.getLigne());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(COLUMN, 50) {

      @Override
      void setValue(Bouteille b, String value) {
        setPlaceValue(b, COLUMN, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getAbstractPlace().isSimplePlace()) {
          return "";
        }
        return Integer.toString(b.getColonne());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(PRICE, 50) {

      @Override
      void setValue(Bouteille b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return convertStringFromHTMLString(b.getPrix());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(COMMENT) {

      @Override
      void setValue(Bouteille b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return convertStringFromHTMLString(b.getComment());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(MATURITY) {

      @Override
      void setValue(Bouteille b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return convertStringFromHTMLString(b.getMaturity());
      }
    });
    availableColumns.add(new ShowFileColumn<String>(PARKER) {

      @Override
      void setValue(Bouteille b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return b.getParker();
      }
    });
    availableColumns.add(new ShowFileColumn<BottleColor>(COLOR) {

      @Override
      void setValue(Bouteille b, BottleColor value) {
        b.setModified();
        Program.setModified();
        b.setColor(value.name());
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return BottleColor.getColor(b.getColor());
      }
    });

    if (!worksheet) {
      availableColumns.add(new ShowFileColumn<BottlesStatus>(STATUS) {

        @Override
        void setValue(Bouteille b, BottlesStatus value) {
          b.setModified();
          Program.setModified();
          b.setStatus(value.name());
        }

        @Override
        Object getDisplayValue(Bouteille b) {
          return BottlesStatus.getStatus(b.getStatus());
        }
      });
    }

    modifyButtonColumn = new ShowFileColumn<>(100, true, getLabel(SHOWFILE_MORE)) {
      @Override
      void setValue(Bouteille b, Object value) {
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return null;
      }

      @Override
      public boolean execute(Bouteille myCellarObject, int row, int column) {
        if (Program.isNotExistingMyCellarObject(myCellarObject)) {
          Debug("Object " + myCellarObject.getNom() + " [" + myCellarObject.getUuid() + "] doesn't exist");
          Erreur.showSimpleErreur(getError(ERROR_INEXISTINGBOTTLE, myCellarObject.getNom()));
          return false;
        }
        ProgramPanels.showBottle(myCellarObject, true);
        return false;
      }
    };
    availableColumns.add(modifyButtonColumn);
    checkedButtonColumn = new ShowFileColumn<>(100, true, false, getLabel(SHOWFILE_VALID), null) {
      @Override
      void setValue(Bouteille b, State value) {
        setMapValue(b, value);
        if (State.VALIDATED == value) {
          b.setStatus(BottlesStatus.VERIFIED.name());
          b.setModified();
          Program.setModified();
          Program.getStorage().addHistory(HistoryState.VALIDATED, b);
        } else if (State.TO_CHECK == value) {
          b.setStatus(BottlesStatus.TOCHECK.name());
          b.setModified();
          Program.setModified();
          Program.getStorage().addHistory(HistoryState.TOCHECK, b);
        }
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return getMapValue(b);
      }

      @Override
      public String getColumnName() {
        return getLabel(SHOWFILE_STATUS);
      }
    };
    if (worksheet) {
      availableColumns.add(checkedButtonColumn);
    }
  }

  void postInit() {
    initPlacesCombo();

    Arrays.stream(BottleColor.values()).forEach(colorCbx::addItem);
    Arrays.stream(BottlesStatus.values()).forEach(statusCbx::addItem);

    typeCbx.addItem("");
    MyCellarBottleContenance.getList().forEach(typeCbx::addItem);

    verifyStatusCbx.addItem(State.NONE);
    verifyStatusCbx.addItem(State.VALIDATED);
    verifyStatusCbx.addItem(State.TO_CHECK);

    refresh();

    table.setPreferredScrollableViewportSize(new Dimension(300, 200));
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "grow, span 2, wrap");
    add(labelCount, "grow, span 2, align right, wrap");
  }

  void addTableSorter() {
    if (table == null) {
      throw new NullPointerException("table must be initialized first");
    }
    table.setAutoCreateRowSorter(true);
    TableRowSorter<TableModel> sorter = buildTableModelTableRowSorter();
    table.setRowSorter(sorter);
    List<RowSorter.SortKey> sortKeys = new ArrayList<>();
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    sorter.setSortKeys(sortKeys);
    sorter.sort();
  }

  private TableRowSorter<TableModel> buildTableModelTableRowSorter() {
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
    sorter.setComparator(TableShowValues.PRICE, Comparator.comparing(AbstractShowFilePanel::getPrice));
    return sorter;
  }

  private static BigDecimal getPrice(String value) {
    BigDecimal price;
    if (value.isBlank()) {
      price = BigDecimal.ZERO;
    } else {
      price = safeStringToBigDecimal(value, BigDecimal.ZERO);
    }
    return price;
  }

  void delete() {
    try {
      List<Bouteille> toDeleteList = getSelectedBottles();

      if (toDeleteList.isEmpty()) {
        Erreur.showInformationMessage(ERROR_NOITEMTODELETE, ERROR_PLEASESELECT);
      } else {
        String erreur_txt1, erreur_txt2;
        if (toDeleteList.size() == 1) {
          erreur_txt1 = getError(ERROR_1ITEMSELECTED);
          erreur_txt2 = getError(ERROR_CONFIRM1DELETE);
        } else {
          erreur_txt1 = getError(ERROR_NITEMSSELECTED, toDeleteList.size());
          erreur_txt2 = getError(ERROR_CONFIRMNDELETE);
        }
        String message = String.format("%s %s", erreur_txt1, erreur_txt2);
        if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
          for (Bouteille b : toDeleteList) {
            Program.getStorage().addHistory(HistoryState.DEL, b);
            final AbstractPlace rangement = b.getAbstractPlace();
            rangement.removeObject(b);
            Program.setToTrash(b);
            workingBottles.remove(b);
          }
        }
        refresh();
      }
    } catch (MyCellarException exc) {
      Program.showException(exc);
    }
  }

  List<Bouteille> getSelectedBottles() {
    int rowCount = model.getRowCount();
    if (rowCount == 0) {
      return Collections.emptyList();
    }
    final LinkedList<Bouteille> list = new LinkedList<>();
    int row = 0;
    if (model instanceof ShowFileModel showFileModel) {
      do {
        if (showFileModel.getValueAt(row, TableShowValues.ETAT).equals(Boolean.TRUE)) {
          list.add(showFileModel.getBottle(row));
        }
        row++;
      } while (row < rowCount);
    } else {
      do {
        if (model.getValueAt(row, TableShowValues.ETAT).equals(Boolean.TRUE)) {
          list.add(model.getBottle(row));
        }
        row++;
      } while (row < rowCount);
    }

    return list;
  }

  void restore() {
    final List<Bouteille> toRestoreList = getSelectedBottles();
    if (toRestoreList.isEmpty()) {
      Erreur.showInformationMessage(SHOWFILE_NOBOTTLETORESTORE, SHOWFILE_SELECTTORESTORE);
      return;
    }

    String erreur_txt1, erreur_txt2;
    if (toRestoreList.size() == 1) {
      erreur_txt1 = getError(ERROR_1ITEMSELECTED);
      erreur_txt2 = getLabel(SHOWFILE_RESTOREONE);
    } else {
      erreur_txt1 = getError(ERROR_NITEMSSELECTED, toRestoreList.size());
      erreur_txt2 = getLabel(SHOWFILE_RESTORESEVERAL);
    }
    String message = String.format("%s %s", erreur_txt1, erreur_txt2);
    if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(message)) {
      LinkedList<Bouteille> cantRestoreList = new LinkedList<>();
      for (Bouteille b : toRestoreList) {
        Program.getTrash().remove(b);
        if (b.isInExistingPlace()) {
          AbstractPlace r = b.getAbstractPlace();
          if (r.isSimplePlace()) {
            Program.getStorage().addHistory(HistoryState.ADD, b);
            Program.getStorage().addWine(b);
          } else {
            if (((ComplexPlace) r).getObject(b.getPlacePosition()).isEmpty()) {
              Program.getStorage().addHistory(HistoryState.ADD, b);
              Program.getStorage().addWine(b);
            } else {
              cantRestoreList.add(b);
            }
          }
        }
      }
      if (!cantRestoreList.isEmpty()) {
        OpenAddVinAction.open(cantRestoreList);
      }
    }
    refresh();
  }

  protected abstract void refresh();

  private void setPlaceValue(Bouteille b, MyCellarFields field, Object value) {
    AbstractPlace abstractPlace = b.getAbstractPlace();
    int nValueToCheck = -1;
    String empl = b.getEmplacement();
    int num_empl = b.getNumLieu();
    int line = b.getLigne();
    int column = b.getColonne();

    if (field == PLACE) {
      abstractPlace = (AbstractPlace) value;
      empl = abstractPlace.getName();
    } else if (field == NUM_PLACE) {
      Integer i = parseIntOrError(String.valueOf(value));
      if (i == null) {
        return;
      }
      num_empl = i;
      nValueToCheck = i;
    } else if (field == LINE) {
      Integer i = parseIntOrError(String.valueOf(value));
      if (i == null) {
        return;
      }
      line = i;
      nValueToCheck = i;
    } else if (field == COLUMN) {
      Integer i = parseIntOrError(String.valueOf(value));
      if (i == null) {
        return;
      }
      column = i;
      nValueToCheck = i;
    }

    PlacePosition place = null;
    if (field == PLACE) {
      placeCbx.setSelectedIndex(0);
      if (!abstractPlace.isSimplePlace()) {
        final PanelPlacePosition panelPlace = new PanelPlacePosition(abstractPlace, true, false, true, true, false, true, false);
        JOptionPane.showMessageDialog(MainFrame.getInstance(), panelPlace,
            getLabel(MAIN_CHOOSECELL),
            JOptionPane.PLAIN_MESSAGE);
        place = panelPlace.getSelectedPlacePosition();
        if (place.hasPlace()) {
          abstractPlace = place.getAbstractPlace();
          empl = abstractPlace.getName();
          num_empl = place.getPart();
          line = place.getLine();
          column = place.getColumn();
        } else {
          num_empl = -1;
        }
      }
    }

    if (field == NUM_PLACE || field == LINE || field == COLUMN) {
      if (abstractPlace != null && !abstractPlace.isSimplePlace() && nValueToCheck <= 0) {
        Erreur.showSimpleErreur(getError(ERROR_ENTERNUMERICVALUEABOVEZERO));
        return;
      }
    }

    if (b.getEmplacement().compareTo(empl) != 0 || b.getNumLieu() != num_empl || b.getLigne() != line || b.getColonne() != column) {
      // Controle de l'emplacement de la bouteille
      if (place == null) {
        place = new PlacePosition.PlacePositionBuilder(abstractPlace != null ? abstractPlace : Program.EMPTY_PLACE).withNumPlace(num_empl).withLine(line).withColumn(column).build();
      }
      if (abstractPlace != null && (place != null && abstractPlace.canAddObjectAt(place))) {
        boolean hasObject = false;
        if (abstractPlace.isComplexPlace()) {
          final Bouteille bouteille = ((ComplexPlace) abstractPlace).getObject(place).orElse(null);
          if (bouteille != null) {
            Erreur.showSimpleErreur(getError(ERROR_ALREADYINSTORAGE, convertStringFromHTMLString(bouteille.getNom()), bouteille.getAnnee()));
            hasObject = true;
          }
        }
        if (!hasObject) {
          if (field == PLACE) {
            b.setEmplacement(empl);
            b.setNumLieu(place.getPart());
            b.setLigne(place.getLine());
            b.setColonne(place.getColumn());
          } else if (field == NUM_PLACE) {
            b.setNumLieu(Integer.parseInt((String) value));
          } else if (field == LINE) {
            b.setLigne(Integer.parseInt((String) value));
          } else if (field == COLUMN) {
            b.setColonne(Integer.parseInt((String) value));
          }
          if (field == PLACE && abstractPlace.isSimplePlace()) {
            if (b.getNumLieu() > abstractPlace.getLastPartNumber()) {
              b.setNumLieu(((SimplePlace) abstractPlace).getFreeNumPlace());
            }
            b.setLigne(0);
            b.setColonne(0);
          }
          PlaceUtils.putTabStock();
          Program.setModified();
          b.setModified();
          Program.getStorage().addHistory(HistoryState.MODIFY, b);
        }
      } else {
        if (abstractPlace != null && abstractPlace.isSimplePlace()) {
          Erreur.showSimpleErreur(getError(ERROR_NOTENOUGHSPACESTORAGE));
        } else {
          if (JOptionPane.YES_OPTION == Erreur.showAskConfirmationMessage(getError(ERROR_CANTMODIFYSTORAGE))) {
            ProgramPanels.showBottle(b, true);
          }
        }
      }
    }
  }

  @Override
  public void setUpdateViewType(UpdateViewType updateViewType) {
    updateView = true;
    this.updateViewType = updateViewType;
  }

  @Override
  public void updateView() {
    refresh();
    if (!updateView) {
      return;
    }
    updateView = false;
    model.fireTableStructureChanged();
    if (updateViewType == UpdateViewType.PLACE || updateViewType == UpdateViewType.ALL) {
      initPlacesCombo();
    }

    if (updateViewType == UpdateViewType.CAPACITY || updateViewType == UpdateViewType.ALL) {
      typeCbx.removeAllItems();
      typeCbx.addItem("");
      MyCellarBottleContenance.getList().forEach(typeCbx::addItem);
    }
  }

  private void initPlacesCombo() {
    placeCbx.removeAllItems();
    placeCbx.addItem(Program.EMPTY_PLACE);
    Program.getAbstractPlaces().forEach(placeCbx::addItem);
  }

  void updateModel(boolean editable, boolean worksheet) {
    if (table == null) {
      throw new NullPointerException("table must be initialized first");
    }
    TableColumnModel tcm = table.getColumnModel();
    TableColumn[] tc1 = new TableColumn[5];
    for (int w = 0; w < 5; w++) {
      tc1[w] = tcm.getColumn(w);
      tc1[w].setCellRenderer(new ToolTipRenderer());
      switch (w) {
        case 1:
          tc1[w].setMinWidth(150);
          break;
        case 2:
          tc1[w].setMinWidth(50);
          break;
        case 4:
          tc1[w].setMinWidth(100);
          break;
        default:
          tc1[w].setMinWidth(30);
          break;
      }
    }
    TableColumn tc = tcm.getColumn(TableShowValues.ETAT);
    tc.setCellRenderer(new CheckboxCellRenderer());
    tc.setCellEditor(new CheckboxCellEditor());
    tc.setMinWidth(25);
    tc.setMaxWidth(25);

    if (!editable) {
      return;
    }

    List<ShowFileColumn<?>> cols = filterColumns(worksheet);
    int i = 0;
    final int columnCount = tcm.getColumnCount();
    for (ShowFileColumn<?> column : cols) {
      if (i >= columnCount) {
        Debug("ERROR: i >= columnCount: Column: " + column.getField().name() + " " + i + " >= " + columnCount);
        Debug("----");
        i++;
        continue;
      }
      tc = tcm.getColumn(i);
      if (column.getField().equals(PLACE)) {
        tc.setCellEditor(new DefaultCellEditor(placeCbx));
      } else if (column.getField().equals(TYPE)) {
        tc.setCellEditor(new DefaultCellEditor(typeCbx));
      } else if (column.getField().equals(COLOR)) {
        tc.setCellEditor(new DefaultCellEditor(colorCbx));
      } else if (column.getField().equals(STATUS)) {
        tc.setCellEditor(new DefaultCellEditor(statusCbx));
      } else if (column.isButton()) {
        tc.setCellRenderer(new ButtonCellRenderer(column.getButtonLabel()));
        tc.setCellEditor(new ButtonCellEditor());
        tc.setMinWidth(column.getWidth());
        tc.setMaxWidth(column.getWidth());
      } else if (column.isCheckBox()) {
        tc.setCellRenderer(new CheckboxCellRenderer());
        tc.setCellEditor(new CheckboxCellEditor());
        tc.setMinWidth(column.getWidth());
        tc.setMaxWidth(column.getWidth());
      } else if (checkedButtonColumn.equals(column)) {
        tc.setCellEditor(new DefaultCellEditor(verifyStatusCbx));
        tc.setMinWidth(column.getWidth());
        tc.setMaxWidth(column.getWidth());
      }
      i++;
    }
  }

  List<ShowFileColumn<?>> filterColumns(boolean worksheet) {
    String savedColumns;
    if (worksheet) {
      model.setBottles(workingBottles.stream().toList());
      savedColumns = Program.getShowColumnsWork();
    } else {
      model.setBottles(Program.getStorage().getAllList());
      savedColumns = Program.getShowColumns();
    }
    labelCount.setValue(Integer.toString(model.getRowCount()));
    List<ShowFileColumn<?>> cols = new ArrayList<>();
    if (!savedColumns.isEmpty()) {
      String[] values = savedColumns.split(COLUMNS_SEPARATOR);
      for (ShowFileColumn<?> c : availableColumns) {
        for (String s : values) {
          if (s.equals(c.getField().name())) {
            cols.add(c);
          }
        }
      }
    }
    if (cols.isEmpty()) {
      cols = availableColumns.stream().filter((field) ->
              !field.getField().equals(VINEYARD)
                  && !field.getField().equals(AOC)
                  && !field.getField().equals(IGP)
                  && !field.getField().equals(COUNTRY))
          .collect(toList());
    } else {
      if (!cols.contains(checkBoxStartColumn)) {
        cols.addFirst(checkBoxStartColumn);
      }
      if (!cols.contains(modifyButtonColumn)) {
        cols.add(modifyButtonColumn);
      }
      if (worksheet && !cols.contains(checkedButtonColumn)) {
        cols.add(checkedButtonColumn);
      }
    }
    return cols;
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    PlaceUtils.putTabStock();
    return true;
  }

  public abstract void Debug(String text);

  class ManageColumnsAction extends AbstractAction {

    private final boolean worksheet;

    ManageColumnsAction(boolean worksheet) {
      this.worksheet = worksheet;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JPanel panel = new JPanel();
      List<MyCellarFields> list;
      if (worksheet) {
        list = getFieldsListForImportAndWorksheet();
      } else {
        list = getFieldsList();
      }
      List<ShowFileColumn<?>> cols = ((ShowFileModel) model).getColumns();
      final List<ShowFileColumn<?>> showFileColumns = cols.stream().filter(ShowFileColumn::isDefault).collect(toList());
      ManageColumnModel modelColumn = new ManageColumnModel(list, showFileColumns);
      JTable jTable = new JTable(modelColumn);
      TableColumnModel tcm = jTable.getColumnModel();
      TableColumn tc = tcm.getColumn(0);
      tc.setCellRenderer(new CheckboxCellRenderer());
      tc.setCellEditor(new CheckboxCellEditor());
      tc.setMinWidth(25);
      tc.setMaxWidth(25);
      panel.add(new JScrollPane(jTable));
      JOptionPane.showMessageDialog(MainFrame.getInstance(), panel, getLabel(MAIN_COLUMNS), JOptionPane.PLAIN_MESSAGE);
      List<Integer> properties = modelColumn.getSelectedColumns();
      if (!properties.isEmpty()) {
        cols = new ArrayList<>();
        cols.add(checkBoxStartColumn);
        Program.setModified();
        for (ShowFileColumn<?> c : availableColumns) {
          if (properties.contains(c.getField().getIndex())) {
            cols.add(c);
          }
        }
        cols.add(modifyButtonColumn);
        if (worksheet) {
          cols.add(checkedButtonColumn);
        }
      }
      int i = 0;
      StringBuilder buffer = new StringBuilder();
      for (ShowFileColumn<?> c : cols) {
        if (!c.isDefault()) {
          continue;
        }
        if (i > 0) {
          buffer.append(';');
        }
        i++;
        buffer.append(c.getField().name());
      }
      if (worksheet) {
        Program.saveShowColumnsWork(buffer.toString());
      } else {
        Program.saveShowColumns(buffer.toString());
      }
      if (!cols.isEmpty()) {
        ((ShowFileModel) model).removeAllColumns();
        ((ShowFileModel) model).setColumns(cols);
        updateModel(true, worksheet);
      }
    }
  }

  class ModifyBottlesAction extends AbstractAction {
    private ModifyBottlesAction() {
      super("", MyCellarImage.WINE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      List<Bouteille> selectedObjects = getSelectedBottles();
      if (selectedObjects.isEmpty()) {
        Erreur.showInformationMessage(ERROR_NOITEMTOMODIFY, ERROR_SELECTITEMTOMODIFY);
        return;
      }

      Debug("Modifying " + selectedObjects.size() + " objects...");
      LinkedList<Bouteille> existingObjects = new LinkedList<>();
      for (var bottle : selectedObjects) {
        if (Program.isNotExistingMyCellarObject(bottle)) {
          Debug("Object " + bottle.getNom() + " [" + bottle.getUuid() + "] doesn't exist");
          Erreur.showSimpleErreur(getError(ERROR_INEXISTINGBOTTLE, bottle.getNom()));
        } else {
          existingObjects.add(bottle);
        }
      }
      OpenAddVinAction.open(existingObjects);
    }
  }

  private enum State implements IMyCellarEnum {

    NONE(0, ""),
    VALIDATED(1, getLabel(HISTORY_VALIDATED)),
    TO_CHECK(2, getLabel(HISTORY_TOCHECK));

    private final int index;
    private final String label;

    State(int index, String label) {
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
