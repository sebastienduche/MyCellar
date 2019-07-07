package mycellar.showfile;


import mycellar.BottleColor;
import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.History;
import mycellar.ITabListener;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.RangementUtils;
import mycellar.Start;
import mycellar.StateButtonEditor;
import mycellar.StateButtonRenderer;
import mycellar.StateEditor;
import mycellar.StateRenderer;
import mycellar.TabEvent;
import mycellar.ToolTipRenderer;
import mycellar.Vignoble;
import mycellar.core.MyCellarButton;
import mycellar.core.MyCellarComboBox;
import mycellar.core.MyCellarEnum;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarFields;
import mycellar.core.MyCellarLabel;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 6.2
 * @since 07/07/19
 */

public class ShowFile extends JPanel implements ITabListener {

  private static final MyCellarEnum NONE = new MyCellarEnum(0, "");
  private static final MyCellarEnum VALIDATED = new MyCellarEnum(1, Program.getLabel("History.Validated"));

  private static final long serialVersionUID = 1265789936970092250L;
  private final MyCellarLabel m_oTitleLabel = new MyCellarLabel();
  private final MyCellarLabel m_oResultLabel = new MyCellarLabel();
  private final MyCellarButton m_oCreatePlacesButton = new MyCellarButton(new CreatePlacesAction());
  private final MyCellarButton m_oManageButton = new MyCellarButton(new ManageColumnAction());
  private final MyCellarButton m_oDeleteButton = new MyCellarButton(MyCellarImage.DELETE);
  private final MyCellarButton m_oModifyButton = new MyCellarButton(new ModifyBottlesAction());
  private final MyCellarButton m_oReloadButton = new MyCellarButton(new ReloadErrorsAction());
  private final MyCellarComboBox<String> m_oPlaceCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> m_oTypeCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<MyCellarEnum> m_oStatusCbx = new MyCellarComboBox<>();
  private TableShowValues tv;
  private JTable m_oTable;
  private boolean updateView = false;
  private final List<ShowFileColumn<?>> columns = new ArrayList<>();
  private final ShowType showType;
  private final LinkedList<Bouteille> workingBottles = new LinkedList<>();
  private ShowFileColumn<Boolean> checkBoxStartColumn;
  private ShowFileColumn<?> modifyButtonColumn;
  private ShowFileColumn<MyCellarEnum> checkedButtonColumn;

  public enum ShowType {
    NORMAL,
    TRASH,
    ERROR,
    WORK
  }

  public ShowFile() {
    showType = ShowType.NORMAL;
    try {
      initializeStandardColumns();
      jbInit();
    } catch (Exception e) {
      Program.showException(e);
    }
  }

  public ShowFile(ShowType showType) {
    this.showType = showType;
    if (isWork()) {
      initializeStandardColumns();
    }
    try {
      jbInit();
    } catch (Exception e) {
      Program.showException(e);
    }
  }

  private void initializeStandardColumns() {
    checkBoxStartColumn = new ShowFileColumn<>(25, true, true, "") {
      @Override
      void setValue(Bouteille b, Object value) {
        setMapValue(b,(Boolean) value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return getMapValue(b);
      }
    };
    columns.add(checkBoxStartColumn);
    columns.add(new ShowFileColumn<>(MyCellarFields.NAME) {

      @Override
      void setValue(Bouteille b, Object value) {
        b.setNom((String) value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return Program.convertStringFromHTMLString(b.getNom());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.YEAR, 50) {

      @Override
      void setValue(Bouteille b, Object value) {
        if (Program.hasYearControl() && !Bouteille.isValidYear((String) value)) {
          Erreur.showSimpleErreur(Program.getError("Error053"));
        } else {
          b.setAnnee((String) value);
        }
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return b.getAnnee();
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.TYPE) {

      @Override
      void setValue(Bouteille b, Object value) {
        b.setType((String) value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return b.getType();
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.PLACE) {

      @Override
      void setValue(Bouteille b, Object value) {
        setRangementValue(b, MyCellarFields.PLACE, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if(b.isInTemporaryStock()) {
          return Program.getLabel("Bouteille.TemporaryPlace");
        }
        return Program.convertStringFromHTMLString(b.getEmplacement());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.NUM_PLACE, 50) {

      @Override
      void setValue(Bouteille b, Object value) {
        setRangementValue(b, MyCellarFields.NUM_PLACE, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return Integer.toString(b.getNumLieu());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.LINE, 50) {

      @Override
      void setValue(Bouteille b, Object value) {
        setRangementValue(b, MyCellarFields.LINE, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getRangement() == null || b.getRangement().isCaisse()) {
          return "";
        }
        return Integer.toString(b.getLigne());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.COLUMN, 50) {

      @Override
      void setValue(Bouteille b, Object value) {
        setRangementValue(b, MyCellarFields.COLUMN, value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getRangement() == null || b.getRangement().isCaisse()) {
          return "";
        }
        return Integer.toString(b.getColonne());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.PRICE, 50) {

      @Override
      void setValue(Bouteille b, Object value) {
        b.setPrix((String) value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return Program.convertStringFromHTMLString(b.getPrix());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.COMMENT) {

      @Override
      void setValue(Bouteille b, Object value) {
        b.setComment(Program.convertStringFromHTMLString((String) value));
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return Program.convertStringFromHTMLString(b.getComment());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.MATURITY) {

      @Override
      void setValue(Bouteille b, Object value) {
        b.setMaturity(Program.convertStringFromHTMLString((String) value));
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return Program.convertStringFromHTMLString(b.getMaturity());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.PARKER) {

      @Override
      void setValue(Bouteille b, Object value) {
        b.setParker((String) value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return b.getParker();
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.COLOR) {

      @Override
      void setValue(Bouteille b, Object value) {
        b.setColor(((BottleColor) value).name());
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return BottleColor.getColor(b.getColor());
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.COUNTRY, 100, false) {

      @Override
      void setValue(Bouteille b, Object value) {}

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getVignoble() == null) {
          return "";
        }
        Country country = Countries.find(b.getVignoble().getCountry());
        if (country != null) {
          return country.getLabel();
        }
        return b.getVignoble().getCountry();
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.VINEYARD, 100, false) {

      @Override
      void setValue(Bouteille b, Object value) {}

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getVignoble() == null) {
          return "";
        }
        return b.getVignoble().getName();
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.AOC, 100, false) {

      @Override
      void setValue(Bouteille b, Object value) {
        Vignoble v = b.getVignoble();
        if (v == null) {
          return;
        }
        v.setAOC((String) value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getVignoble() == null) {
          return "";
        }
        return b.getVignoble().getAOC();
      }
    });
    columns.add(new ShowFileColumn<>(MyCellarFields.IGP, 100, false) {

      @Override
      void setValue(Bouteille b, Object value) {
        Vignoble v = b.getVignoble();
        if (v == null) {
          return;
        }
        v.setIGP((String) value);
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        if (b.getVignoble() == null) {
          return "";
        }
        return b.getVignoble().getIGP();
      }
    });
    modifyButtonColumn = new ShowFileColumn<>(100, true, Program.getLabel("Infos360")) {
      @Override
      void setValue(Bouteille b, Object value) {}

      @Override
      Object getDisplayValue(Bouteille b) {
        return null;
      }

      @Override
      public boolean execute(Bouteille b, int row, int column) {
        Start.getInstance().showBottle(b, true);
        return false;
      }
    };
    columns.add(modifyButtonColumn);
    checkedButtonColumn = new ShowFileColumn<MyCellarEnum>(100, true, true, Program.getLabel("ShowFile.Valid")) {
      @Override
      void setValue(Bouteille b, Object value) {
    	  MyCellarEnum status = (MyCellarEnum) value; 
        setMapValue(b, status);
        if (VALIDATED.equals(status)) {
          Program.getStorage().addHistory(History.VALIDATED, b);
        }
      }

      @Override
      Object getDisplayValue(Bouteille b) {
        return getMapValue(b);
      }
    };
    columns.add(checkedButtonColumn);
  }

  public void addWorkingBottles(Collection<Bouteille> bottles) {
    if (bottles != null) {
      workingBottles.addAll(bottles.stream().filter(bouteille -> !workingBottles.contains(bouteille)).collect(Collectors.toList()));
      tv.setBottles(workingBottles);
    }
  }

  private void jbInit() {

    m_oTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    m_oResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
    m_oResultLabel.setForeground(Color.red);
    setLayout(new MigLayout("", "[][grow]", "[]10px[grow][]"));
    if (isTrash()) {
      m_oDeleteButton.setText(Program.getLabel("ShowFile.Restore"));
      m_oDeleteButton.setIcon(MyCellarImage.RESTORE);
    } else {
      m_oDeleteButton.setText(Program.getLabel("Infos051"));
    }

    m_oDeleteButton.addActionListener((e) -> {
      if (isTrash()) {
        restore();
      } else {
        delete();
      }
    });
    add(m_oTitleLabel, "align left");
    if (isNormal() || isWork()) {
      add(m_oManageButton, "align right, split 3");
      add(m_oModifyButton, "align right");
    } else if (isError()){
      add(m_oCreatePlacesButton, "align right, split 3");
      add(m_oReloadButton, "align right");
    }
    add(m_oDeleteButton, "align right, wrap");


    for (Rangement r : Program.getCave()) {
      m_oPlaceCbx.addItem(r.getNom());
    }

    m_oTypeCbx.addItem("");
    for (String type : MyCellarBottleContenance.getList()) {
      m_oTypeCbx.addItem(type);
    }
    
    m_oStatusCbx.addItem(NONE);
    m_oStatusCbx.addItem(VALIDATED);

    List<ShowFileColumn<?>> cols = new LinkedList<>(columns);
    // Remplissage de la table
    if (isTrash()) {
      tv = new TableShowValues();
      tv.setBottles(Program.getTrash());
      m_oTable = new JTable(tv);
    } else if (isError()) {
      tv = new ErrorShowValues();
      ((ErrorShowValues) tv).setErrors(Program.getErrors());
      m_oTable = new JTable(tv);
      m_oTitleLabel.setText(Program.getLabel("ShowFile.manageError"));
    } else {
      tv = new ShowFileModel();
      String savedColumns;
      if (isWork()) {
        tv.setBottles(workingBottles);
        savedColumns = Program.getShowColumnsWork();
      } else {
        tv.setBottles(Program.getStorage().getAllList());
        savedColumns = Program.getShowColumns();
      }
      cols = new ArrayList<>();
      if (!savedColumns.isEmpty()) {
        String[] values = savedColumns.split(";");
        for (ShowFileColumn<?> c : columns) {
          for (String s : values) {
            if (s.equals(c.getField().name())) {
              cols.add(c);
            }
          }
        }
      }
      if (cols.isEmpty()) {
        cols = columns.stream().filter((field) -> {
          return !field.getField().equals(MyCellarFields.VINEYARD)
              && !field.getField().equals(MyCellarFields.AOC)
              && !field.getField().equals(MyCellarFields.IGP)
              && !field.getField().equals(MyCellarFields.COUNTRY);
        }).collect(Collectors.toList());
      } else {
        cols.add(0, checkBoxStartColumn);
        cols.add(modifyButtonColumn);
        if (isWork()) {
          cols.add(checkedButtonColumn);
        }
      }
      ((ShowFileModel) tv).setColumns(cols);

      m_oTable = new JTable(tv);
    }

    m_oTable.setAutoCreateRowSorter(true);
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(m_oTable.getModel());
    sorter.setComparator(TableShowValues.PRICE,(String o1, String o2) -> {
      BigDecimal price1;
      if(o1.isEmpty()) {
        price1 = BigDecimal.ZERO;
      } else {
        try {
          price1 = Program.stringToBigDecimal(o1);
        }
        catch (NumberFormatException ignored) {
          price1 = BigDecimal.ZERO;
        }
      }
      BigDecimal price2;
      if(o2.isEmpty()) {
        price2 = BigDecimal.ZERO;
      } else {
        try {
          price2 = Program.stringToBigDecimal(o2);
        }
        catch (NumberFormatException ignored) {
          price2 = BigDecimal.ZERO;
        }
      }
      return price1.compareTo(price2);
    });
    m_oTable.setRowSorter(sorter);
    List<RowSorter.SortKey> sortKeys = new ArrayList<>();
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    sorter.setSortKeys(sortKeys);
    sorter.sort();
    TableColumnModel tcm = m_oTable.getColumnModel();
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
    tc.setCellRenderer(new StateRenderer());
    tc.setCellEditor(new StateEditor());
    tc.setMinWidth(25);
    tc.setMaxWidth(25);
    updateModel(cols);

    m_oTable.setPreferredScrollableViewportSize(new Dimension(300, 200));
    m_oTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    add(new JScrollPane(m_oTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "grow, span 2, wrap");
    add(m_oResultLabel, "span 2, alignx center, hidemode 3");
  }

  private boolean isNormal() {
    return showType == ShowType.NORMAL;
  }

  private boolean isTrash() {
    return showType == ShowType.TRASH;
  }

  private boolean isError() {
    return showType == ShowType.ERROR;
  }

  private boolean isWork() {
    return showType == ShowType.WORK;
  }

  private void delete() {
    try {
      LinkedList<Bouteille> toDeleteList = getSelectedBouteilles();

      if (toDeleteList.isEmpty()) {
        //"Aucun vin à supprimer!");
        Erreur.showSimpleErreur(Program.getError("Error064"), Program.getError("Error065"), true);
      } else {
        String erreur_txt1, erreur_txt2;
        if (toDeleteList.size() == 1) {
          erreur_txt1 = Program.getError("Error067"); //"1 vin sélectionné.");
          erreur_txt2 = Program.getError("Error068"); //"Voulez-vous le supprimer?");
        } else {
          erreur_txt1 = MessageFormat.format(Program.getError("Error130"), toDeleteList.size()); //vins sélectionnés.");
          erreur_txt2 = Program.getError("Error131"); //"Voulez-vous les supprimer?");
        }
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          if (isError()) {
            for (Bouteille b : toDeleteList) {
              Program.getErrors().remove(new MyCellarError(MyCellarError.ID.INEXISTING_PLACE, b));
            }
          } else {
            for (Bouteille b : toDeleteList) {
              Program.getStorage().addHistory(History.DEL, b);
              Program.getStorage().deleteWine(b);
              Program.setToTrash(b);
              if (isWork()) {
                workingBottles.remove(b);
              }
            }
            RangementUtils.putTabStock();
          }
        }
        refresh();
      }
    } catch (Exception exc) {
      Program.showException(exc);
    }

  }

  private LinkedList<Bouteille> getSelectedBouteilles() {
    final LinkedList<Bouteille> list = new LinkedList<>();
    int max_row = tv.getRowCount();
    if (max_row == 0) {
      return list;
    }
    int row = 0;
    if (tv instanceof ShowFileModel) {
      ShowFileModel showFileModel = (ShowFileModel) tv;
      do {
        if (showFileModel.getValueAt(row, TableShowValues.ETAT).equals(Boolean.TRUE)) {
          list.add(showFileModel.getBottle(row));
        }
        row++;
      } while (row < max_row);
    } else {
      do {
        if (tv.getValueAt(row, TableShowValues.ETAT).equals(Boolean.TRUE)) {
          list.add(tv.getBottle(row));
        }
        row++;
      } while (row < max_row);
    }

    return list;
  }

  private void restore() {
    try {
      final LinkedList<Bouteille> toRestoreList = getSelectedBouteilles();

      if (toRestoreList.isEmpty()) {
        Erreur.showSimpleErreur(Program.getLabel("ShowFile.NoBottleToRestore"), Program.getLabel("ShowFile.SelectToRestore"), true);
      } else {
        String erreur_txt1, erreur_txt2;
        if (toRestoreList.size() == 1) {
          erreur_txt1 = Program.getError("Error067"); //"1 vin sélectionné.");
          erreur_txt2 = Program.getLabel("ShowFile.RestoreOne");
        } else {
          erreur_txt1 = MessageFormat.format(Program.getError("Error130"), toRestoreList.size()); //vins sélectionnés.");
          erreur_txt2 = Program.getLabel("ShowFile.RestoreSeveral");
        }
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + " " + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          LinkedList<Bouteille> cantRestoreList = new LinkedList<>();
          for (Bouteille b : toRestoreList) {
            Program.getTrash().remove(b);
            Rangement r = Program.getCave(b.getEmplacement());
            if (r != null) {
              if (r.isCaisse()) {
                Program.getStorage().addHistory(History.ADD, b);
                Program.getStorage().addWine(b);
              } else {
                if (r.getBouteille(b.getNumLieu() - 1, b.getLigne() - 1, b.getColonne() - 1) == null) {
                  Program.getStorage().addHistory(History.ADD, b);
                  Program.getStorage().addWine(b);
                } else {
                  cantRestoreList.add(b);
                }
              }
            }
          }
          if (!cantRestoreList.isEmpty()) {
            Program.modifyBottles(cantRestoreList);
          }
        }
        refresh();
      }
    } catch (Exception exc) {
      Program.showException(exc);
    }

  }

  public void refresh() {
    if (isTrash()) {
      tv.setBottles(Program.getTrash());
    } else if (isError()) {
      ((ErrorShowValues) tv).setErrors(Program.getErrors());
    } else if (isWork()){
      tv.setBottles(workingBottles);
    } else {
      tv.setBottles(Program.getStorage().getAllList());
    }
    m_oResultLabel.setText("");
  }

  private void setRangementValue(Bouteille b, MyCellarFields field, Object value) {

    Rangement rangement = b.getRangement();
    boolean bError = false;
    int nValueToCheck = -1;
    String empl = b.getEmplacement();
    int num_empl = b.getNumLieu();
    int line = b.getLigne();
    int column = b.getColonne();

    Program.setModified();

    if (field == MyCellarFields.PLACE) {
      empl = (String) value;
      rangement = Program.getCave(empl);
    } else if (field == MyCellarFields.NUM_PLACE) {
      try {
        num_empl = Integer.parseInt((String) value);
        nValueToCheck = num_empl;
      } catch (Exception e) {
        Erreur.showSimpleErreur(Program.getError("Error196"));
        bError = true;
      }
    } else if (field == MyCellarFields.LINE) {
      try {
        line = Integer.parseInt((String) value);
        nValueToCheck = line;
      } catch (Exception e) {
        Erreur.showSimpleErreur(Program.getError("Error196"));
        bError = true;
      }
    } else if (field == MyCellarFields.COLUMN) {
      try {
        column = Integer.parseInt((String) value);
        nValueToCheck = column;
      } catch (Exception e) {
        Erreur.showSimpleErreur(Program.getError("Error196"));
        bError = true;
      }
    }

    if (!bError && (field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN)) {
      if (rangement != null && !rangement.isCaisse() && nValueToCheck <= 0) {
        Erreur.showSimpleErreur(Program.getError("Error197"));
        bError = true;
      }
    }

    if (!bError && (b.getEmplacement().compareTo(empl) != 0 || b.getNumLieu() != num_empl || b.getLigne() != line || b.getColonne() != column)) {
      // Controle de l'emplacement de la bouteille
      if (rangement != null && rangement.canAddBottle(num_empl, line, column)) {
        Bouteille bTemp = null;
        if (!rangement.isCaisse()) {
          bTemp = rangement.getBouteille(num_empl - 1, line - 1, column - 1);
        }
        if (bTemp != null) {
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error059"), Program.convertStringFromHTMLString(bTemp.getNom()), bTemp.getAnnee()));
        } else {
          if (field == MyCellarFields.PLACE) {
            b.setEmplacement((String) value);
          } else if (field == MyCellarFields.NUM_PLACE) {
            b.setNumLieu(Integer.parseInt((String) value));
          } else if (field == MyCellarFields.LINE) {
            b.setLigne(Integer.parseInt((String) value));
          } else if (field == MyCellarFields.COLUMN) {
            b.setColonne(Integer.parseInt((String) value));
          }
          if (field == MyCellarFields.PLACE && rangement.isCaisse()) {
            int nNumEmpl = b.getNumLieu();//Integer.parseInt((String) values[row][NUM_PLACE]);
            if (nNumEmpl > rangement.getLastNumEmplacement()) {
              b.setNumLieu(rangement.getFreeNumPlaceInCaisse());
            }
            b.setLigne(0);
            b.setColonne(0);
          }
          RangementUtils.putTabStock();
        }
      } else {
        if (rangement != null && rangement.isCaisse()) {
          Erreur.showSimpleErreur(Program.getError("Error154"));
        } else {
          if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, Program.getError("Error198"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
            Start.getInstance().showBottle(b, true);
          }
        }
      }
    }
  }

  public void setUpdateView() {
    updateView = true;
  }

  /**
   * Mise à jour de la liste des rangements
   */
  public void updateView() {
    if (!updateView) {
      return;
    }
    updateView = false;
    m_oPlaceCbx.removeAllItems();
    for (Rangement r : Program.getCave()) {
      m_oPlaceCbx.addItem(r.getNom());
    }

    m_oTypeCbx.removeAllItems();
    m_oTypeCbx.addItem("");
    for (String type : MyCellarBottleContenance.getList()) {
      m_oTypeCbx.addItem(type);
    }

    updateModel(columns);
  }

  private void updateModel(List<ShowFileColumn<?>> columns) {
    TableColumnModel tcm = m_oTable.getColumnModel();
    TableColumn tc;
    if (isError()) {
      tc = tcm.getColumn(ErrorShowValues.PLACE);
      tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
      tc = tcm.getColumn(ErrorShowValues.TYPE);
      tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
      tc = tcm.getColumn(ErrorShowValues.STATUS);
      tc.setCellRenderer(new FontBoldTableCellRenderer());
      tc = tcm.getColumn(ErrorShowValues.BUTTON);
      tc.setCellRenderer(new StateButtonRenderer(Program.getLabel("Infos071"), MyCellarImage.ADD));
      tc.setCellEditor(new StateButtonEditor());
    } else if (isNormal() || isWork()) {
      int i = 0;
      for (ShowFileColumn<?> column : columns) {
        tc = tcm.getColumn(i);
        if (column.getField().equals(MyCellarFields.PLACE)) {
          tc.setCellEditor(new DefaultCellEditor(m_oPlaceCbx));
        } else if (column.getField().equals(MyCellarFields.PLACE)) {
          tc.setCellEditor(new DefaultCellEditor(m_oTypeCbx));
        } else if (column.isButton()) {
          tc.setCellRenderer(new StateButtonRenderer(column.getButtonLabel()));
          tc.setCellEditor(new StateButtonEditor());
          tc.setMinWidth(column.getWidth());
          tc.setMaxWidth(column.getWidth());
        } else if (column.isCheckBox()) {
          tc.setCellEditor(new DefaultCellEditor(m_oStatusCbx));
          //tc.setCellEditor(new StateEditor());
          tc.setMinWidth(column.getWidth());
          tc.setMaxWidth(column.getWidth());
        }
        i++;
      }
    }
  }


  @Override
  public boolean tabWillClose(TabEvent event) {
    if (isError()) {
    	if(Program.getErrors().stream().anyMatch(MyCellarError::isNotSolved)) {
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), Program.getLabel("ShowFile.QuitErrors"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION)) {
          return false;
        }
    		return true;
    	}
    }
    RangementUtils.putTabStock();
    return true;
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
  }

  private class ManageColumnAction extends AbstractAction {

    private static final long serialVersionUID = 8165964725562440277L;

    private ManageColumnAction() {
      super(Program.getLabel("Main.Columns"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JPanel panel = new JPanel();
      ArrayList<MyCellarFields> list = new ArrayList<>();
      list.add(MyCellarFields.NAME);
      list.add(MyCellarFields.YEAR);
      list.add(MyCellarFields.TYPE);
      list.add(MyCellarFields.PLACE);
      list.add(MyCellarFields.NUM_PLACE);
      list.add(MyCellarFields.LINE);
      list.add(MyCellarFields.COLUMN);
      list.add(MyCellarFields.PRICE);
      list.add(MyCellarFields.COMMENT);
      list.add(MyCellarFields.MATURITY);
      list.add(MyCellarFields.PARKER);
      list.add(MyCellarFields.COLOR);
      list.add(MyCellarFields.COUNTRY);
      list.add(MyCellarFields.VINEYARD);
      list.add(MyCellarFields.AOC);
      list.add(MyCellarFields.IGP);
      List<ShowFileColumn<?>> cols = ((ShowFileModel) tv).getColumns();
      final List<ShowFileColumn<?>> showFileColumns = cols.stream().filter(ShowFileColumn::isDefault).collect(Collectors.toList());
      ManageColumnModel modelColumn = new ManageColumnModel(list, showFileColumns);
      JTable table = new JTable(modelColumn);
      TableColumnModel tcm = table.getColumnModel();
      TableColumn tc = tcm.getColumn(0);
      tc.setCellRenderer(new StateRenderer());
      tc.setCellEditor(new StateEditor());
      tc.setMinWidth(25);
      tc.setMaxWidth(25);
      panel.add(new JScrollPane(table));
      JOptionPane.showMessageDialog(null, panel, Program.getLabel("Main.Columns"), JOptionPane.PLAIN_MESSAGE);
      cols = new ArrayList<>();
      cols.add(checkBoxStartColumn);
      Program.setModified();
      List<Integer> properties = modelColumn.getSelectedColumns();
      for (ShowFileColumn<?> c : columns) {
        if (properties.contains(c.getField().ordinal())) {
          cols.add(c);
        }
      }
      cols.add(modifyButtonColumn);
      if (isWork()) {
        cols.add(checkedButtonColumn);
      }
      if (!cols.isEmpty()) {
        ((ShowFileModel) tv).removeAllColumns();
        ((ShowFileModel) tv).setColumns(cols);
        updateModel(cols);
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
      Program.saveShowColumns(buffer.toString());
    }
  }

  class ModifyBottlesAction extends AbstractAction {

    private static final long serialVersionUID = -7590310564039085580L;

    private ModifyBottlesAction() {
      super(Program.getLabel("Infos079"), MyCellarImage.WINE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

      try {
        LinkedList<Bouteille> bottles = getSelectedBouteilles();

        if (bottles.isEmpty()) {
          //"Aucun vin à modifier!");
          //"Veuillez sélectionner les vins à modifier.");
          Erreur.showSimpleErreur(Program.getError("Error071"), Program.getError("Error072"), true);
        } else {
          Debug("Modifying " + bottles.size() + " bottles...");
          Program.modifyBottles(bottles);
        }
      } catch (Exception exc) {
        Program.showException(exc);
      }
    }
  }

  private class ReloadErrorsAction extends AbstractAction {

	private static final long serialVersionUID = 983425309954475989L;


	private ReloadErrorsAction() {
      super(Program.getLabel("ShowFile.reloadErrors"));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
      RangementUtils.putTabStock();
      ((ErrorShowValues)tv).setErrors(Program.getErrors());
    }
  }

  private class CreatePlacesAction extends AbstractAction {

	private static final long serialVersionUID = -3652414491735669984L;


	private CreatePlacesAction() {
      super(Program.getLabel("Infos267"));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
      RangementUtils.findRangementToCreate();
    }
  }

  public void Debug(String text) {
    Program.Debug("ShowFile: " + text);
  }
}
