package mycellar.showfile;


import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.Music;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.Start;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellar;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.LabelType;
import mycellar.core.MyCellarEnum;
import mycellar.core.MyCellarError;
import mycellar.core.MyCellarObject;
import mycellar.core.UpdateViewType;
import mycellar.core.common.MyCellarFields;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.common.music.DurationConverter;
import mycellar.core.common.music.MusicSupport;
import mycellar.core.common.music.PanelDuration;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.datas.worksheet.WorkSheetData;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.tablecomponents.ButtonCellEditor;
import mycellar.core.tablecomponents.ButtonCellRenderer;
import mycellar.core.tablecomponents.CheckboxCellEditor;
import mycellar.core.tablecomponents.CheckboxCellRenderer;
import mycellar.core.tablecomponents.ToolTipRenderer;
import mycellar.core.uicomponents.MyCellarButton;
import mycellar.core.uicomponents.MyCellarComboBox;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.TabEvent;
import mycellar.general.ProgramPanels;
import mycellar.placesmanagement.PanelPlace;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import mycellar.placesmanagement.RangementUtils;
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
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
import static mycellar.ProgramConstants.SPACE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Societe : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 11.1
 * @since 05/01/22
 */

public class ShowFile extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  private static final MyCellarEnum NONE = new MyCellarEnum(0, "");
  private static final MyCellarEnum VALIDATED = new MyCellarEnum(1, Program.getLabel("History.Validated"));
  private static final MyCellarEnum TOCHECK = new MyCellarEnum(2, Program.getLabel("History.ToCheck"));

  private static final long serialVersionUID = 1265789936970092250L;
  @SuppressWarnings("deprecation")
  private final MyCellarLabel titleLabel = new MyCellarLabel();
  private final MyCellarButton createPlacesButton = new MyCellarButton(LabelType.INFO, "267", new CreatePlacesAction());
  private final MyCellarButton manageColumnsButton = new MyCellarButton(LabelType.INFO_OTHER, "Main.Columns", new ManageColumnsAction());
  private final MyCellarButton deleteButton = new MyCellarButton(MyCellarImage.DELETE);
  private final MyCellarButton modifyButton = new MyCellarButton(LabelType.INFO, "079", new ModifyBottlesAction());
  private final MyCellarButton reloadButton = new MyCellarButton(LabelType.INFO_OTHER, "ShowFile.reloadErrors", new ReloadErrorsAction());
  private final MyCellarButton removeFromWorksheetButton = new MyCellarButton(LabelType.INFO_OTHER, "ShowFile.removeFromWorksheet", new RemoveFromWorksheetAction());
  private final MyCellarButton clearWorksheetButton = new MyCellarButton(LabelType.INFO_OTHER, "ShowFile.clearWorksheet", new ClearWorksheetAction());
  private final MyCellarComboBox<Rangement> placeCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<BottleColor> colorCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<MusicSupport> musicSupportCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<BottlesStatus> statusCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<String> typeCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<MyCellarEnum> verifyStatusCbx = new MyCellarComboBox<>();
  private final List<ShowFileColumn<?>> columns = new ArrayList<>();
  private final ShowType showType;
  private final LinkedList<MyCellarObject> workingBottles = new LinkedList<>();
  private TableShowValues model;
  private JTable table;
  private boolean updateView = false;
  private ShowFileColumn<Boolean> checkBoxStartColumn;
  private ShowFileColumn<?> modifyButtonColumn;
  private ShowFileColumn<MyCellarEnum> checkedButtonColumn;
  private UpdateViewType updateViewType;

  public ShowFile() {
    showType = ShowType.NORMAL;
    try {
      initializeStandardColumns();
      init();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  public ShowFile(ShowType showType) {
    this.showType = showType;
    if (isWork()) {
      initializeStandardColumns();
      final List<Integer> bouteilles = Program.getWorksheetList().getWorsheet().stream().map(WorkSheetData::getBouteilleId).collect(Collectors.toList());
      workingBottles.addAll(Program.getExistingBottles(bouteilles));
    }
    try {
      init();
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  private void initializeStandardColumns() {
    checkBoxStartColumn = new ShowFileColumn<>(25, true, true, "", Boolean.FALSE) {
      @Override
      void setValue(MyCellarObject b, Boolean value) {
        setMapValue(b, value);
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return getMapValue(b);
      }
    };
    columns.add(checkBoxStartColumn);
    columns.add(new ShowFileColumn<>(MyCellarFields.NAME) {

      @Override
      void setValue(MyCellarObject b, Object value) {
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return Program.convertStringFromHTMLString(b.getNom());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.YEAR, 50) {

      @Override
      void setValue(MyCellarObject b, String value) {
        if (Program.hasYearControl() && Bouteille.isInvalidYear(value)) {
          Erreur.showSimpleErreur(Program.getError("Error053"));
        } else {
          setStringValue(b, value);
        }
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return b.getAnnee();
      }
    });
    if (Program.isWineType()) {
      columns.add(new ShowFileColumn<String>(MyCellarFields.TYPE) {

        @Override
        void setValue(MyCellarObject b, String value) {
          setStringValue(b, value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return b.getKind();
        }
      });
    }
    columns.add(new ShowFileColumn<Rangement>(MyCellarFields.PLACE) {

      @Override
      void setValue(MyCellarObject b, Rangement value) {
        if (Program.EMPTY_PLACE.equals(value)) {
          Erreur.showSimpleErreur(Program.getError("Error055"));
          return;
        }
        setRangementValue(b, MyCellarFields.PLACE, value);
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        if (b.isInTemporaryStock()) {
          return Program.getLabel("Bouteille.TemporaryPlace");
        }
        return Program.convertStringFromHTMLString(b.getEmplacement());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.NUM_PLACE, 50) {

      @Override
      void setValue(MyCellarObject b, String value) {
        setRangementValue(b, MyCellarFields.NUM_PLACE, value);
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return Integer.toString(b.getNumLieu());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.LINE, 50) {

      @Override
      void setValue(MyCellarObject b, String value) {
        setRangementValue(b, MyCellarFields.LINE, value);
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        if (b.getRangement() == null || b.getRangement().isSimplePlace()) {
          return "";
        }
        return Integer.toString(b.getLigne());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.COLUMN, 50) {

      @Override
      void setValue(MyCellarObject b, String value) {
        setRangementValue(b, MyCellarFields.COLUMN, value);
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        if (b.getRangement() == null || b.getRangement().isSimplePlace()) {
          return "";
        }
        return Integer.toString(b.getColonne());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.PRICE, 50) {

      @Override
      void setValue(MyCellarObject b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return Program.convertStringFromHTMLString(b.getPrix());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.COMMENT) {

      @Override
      void setValue(MyCellarObject b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return Program.convertStringFromHTMLString(b.getComment());
      }
    });
    if (Program.isWineType()) {
      columns.add(new ShowFileColumn<String>(MyCellarFields.MATURITY) {

        @Override
        void setValue(MyCellarObject b, String value) {
          setStringValue(b, value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          return Program.convertStringFromHTMLString(((Bouteille) b).getMaturity());
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.PARKER) {

        @Override
        void setValue(MyCellarObject b, String value) {
          setStringValue(b, value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          return ((Bouteille) b).getParker();

        }
      });
      columns.add(new ShowFileColumn<BottleColor>(MyCellarFields.COLOR) {

        @Override
        void setValue(MyCellarObject b, BottleColor value) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          b.setModified();
          Program.setModified();
          ((Bouteille) b).setColor(value.name());
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          return BottleColor.getColor(((Bouteille) b).getColor());
        }
      });
    } else if (Program.isMusicType()) {
      columns.add(new ShowFileColumn<MusicSupport>(MyCellarFields.SUPPORT) {

        @Override
        void setValue(MyCellarObject b, MusicSupport value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setMusicSupport(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return ((Music) b).getMusicSupport();
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.STYLE) {

        @Override
        void setValue(MyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setGenre(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return ((Music) b).getGenre();
        }
      });

      columns.add(new ShowFileColumn<String>(MyCellarFields.COMPOSER) {

        @Override
        void setValue(MyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setComposer(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return ((Music) b).getComposer();
        }
      });

      columns.add(new ShowFileColumn<String>(MyCellarFields.ARTIST) {

        @Override
        void setValue(MyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setArtist(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return ((Music) b).getArtist();
        }
      });

      columns.add(new ShowFileColumn<>(MyCellarFields.DURATION) {

        @Override
        public boolean execute(MyCellarObject b, int row, int column) {
          Program.throwNotImplementedIfNotFor(b, Music.class);
          Music music = (Music) b;
          PanelDuration panelDuration = new PanelDuration(DurationConverter.getTimeFromDisplay((String) getDisplayValue(music)));
          if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Start.getInstance(), panelDuration,
              Program.getLabel("Main.ChooseDuration"), JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.PLAIN_MESSAGE)) {
            b.setModified();
            Program.setModified();
            music.setDuration(DurationConverter.getValueFromTime(panelDuration.getTime()));
          }
          return false;
        }

        @Override
        void setValue(MyCellarObject b, Object value) {
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return DurationConverter.getFormattedDisplay(((Music) b).getDuration());
        }
      });

      columns.add(new ShowFileColumn<Integer>(MyCellarFields.EXTERNAL_ID) {

        @Override
        void setValue(MyCellarObject b, Integer value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setExternalId(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return ((Music) b).getExternalId();
        }
      });

      columns.add(new ShowFileColumn<String>(MyCellarFields.ALBUM) {

        @Override
        void setValue(MyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setAlbum(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return ((Music) b).getAlbum();
        }
      });
    }
    if (!isWork()) {
      columns.add(new ShowFileColumn<BottlesStatus>(MyCellarFields.STATUS) {

        @Override
        void setValue(MyCellarObject b, BottlesStatus value) {
          b.setModified();
          Program.setModified();
          b.setStatus(value.name());
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          return BottlesStatus.getStatus(b.getStatus());
        }
      });
    }

    if (Program.isWineType()) {
      columns.add(new ShowFileColumn<>(MyCellarFields.COUNTRY, 100, false) {

        @Override
        void setValue(MyCellarObject b, Object value) {
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          Bouteille bouteille = (Bouteille) b;
          if (bouteille.getVignoble() == null) {
            return "";
          }
          CountryJaxb countryJaxb = CountryListJaxb.findbyId(bouteille.getVignoble().getCountry()).orElse(null);
          if (countryJaxb != null) {
            return countryJaxb.getLabel();
          }
          return bouteille.getVignoble().getCountry();
        }
      });
      columns.add(new ShowFileColumn<>(MyCellarFields.VINEYARD, 100, false) {

        @Override
        void setValue(MyCellarObject b, Object value) {
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          if (((Bouteille) b).getVignoble() == null) {
            return "";
          }
          return ((Bouteille) b).getVignoble().getName();
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.AOC, 100, false) {

        @Override
        void setValue(MyCellarObject b, String value) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          VignobleJaxb v = ((Bouteille) b).getVignoble();
          if (v == null) {
            return;
          }
          b.setModified();
          Program.setModified();
          v.setAOC(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          if (((Bouteille) b).getVignoble() == null) {
            return "";
          }
          return ((Bouteille) b).getVignoble().getAOC();
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.IGP, 100, false) {

        @Override
        void setValue(MyCellarObject b, String value) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          VignobleJaxb v = ((Bouteille) b).getVignoble();
          if (v == null) {
            return;
          }
          b.setModified();
          Program.setModified();
          v.setIGP(value);
        }

        @Override
        Object getDisplayValue(MyCellarObject b) {
          Program.throwNotImplementedIfNotFor(b, Bouteille.class);
          if (((Bouteille) b).getVignoble() == null) {
            return "";
          }
          return ((Bouteille) b).getVignoble().getIGP();
        }
      });
    }
    modifyButtonColumn = new ShowFileColumn<>(100, true, Program.getLabel("Infos360")) {
      @Override
      void setValue(MyCellarObject b, Object value) {
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return null;
      }

      @Override
      public boolean execute(MyCellarObject myCellarObject, int row, int column) {
        if (!Program.isExistingBottle(myCellarObject)) {
          Debug("Inexisting bottle " + myCellarObject.getNom() + " [" + myCellarObject.getId() + "]");
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("ShowFile.InexisitingBottle", LabelProperty.THE_SINGLE), myCellarObject.getNom()));
          return false;
        }
        ProgramPanels.showBottle(myCellarObject, true);
        return false;
      }
    };
    columns.add(modifyButtonColumn);
    checkedButtonColumn = new ShowFileColumn<>(100, true, false, Program.getLabel("ShowFile.Valid"), null) {
      @Override
      void setValue(MyCellarObject b, MyCellarEnum value) {
        setMapValue(b, value);
        if (VALIDATED.equals(value)) {
          b.setStatus(BottlesStatus.VERIFIED.name());
          b.setModified();
          Program.setModified();
          Program.getStorage().addHistory(HistoryState.VALIDATED, b);
        } else if (TOCHECK.equals(value)) {
          b.setStatus(BottlesStatus.TOCHECK.name());
          b.setModified();
          Program.setModified();
          Program.getStorage().addHistory(HistoryState.TOCHECK, b);
        }
      }

      @Override
      Object getDisplayValue(MyCellarObject b) {
        return getMapValue(b);
      }

      @Override
      public String getColumnName() {
        return Program.getLabel("ShowFile.Status");
      }
    };
    if (isWork()) {
      columns.add(checkedButtonColumn);
    }
  }

  public void addWorkingBottles(Collection<Bouteille> bottles) {
    final List<Bouteille> bouteilles = bottles
        .stream()
        .filter(bouteille -> !workingBottles.contains(bouteille))
        .collect(Collectors.toList());
    for (Bouteille bottle : bouteilles) {
      Program.getStorage().addToWorksheet(bottle);
    }
    workingBottles.addAll(bouteilles);
    model.setBottles(workingBottles);
  }

  private void init() {
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    setLayout(new MigLayout("", "[][grow]", "[]10px[grow][]"));
    if (isTrash()) {
      deleteButton.setText(Program.getLabel("ShowFile.Restore"));
      deleteButton.setIcon(MyCellarImage.RESTORE);
    } else {
      deleteButton.setText(Program.getLabel("Main.Delete"));
    }

    deleteButton.addActionListener((e) -> {
      if (isTrash()) {
        restore();
      } else {
        delete();
      }
    });
    add(titleLabel, "align left");
    if (isNormal()) {
      add(manageColumnsButton, "align right, split 3");
      add(modifyButton, "align right");
    } else if (isWork()) {
      add(manageColumnsButton, "align right, split 5");
      add(clearWorksheetButton, "align right");
      add(removeFromWorksheetButton, "align right");
      add(modifyButton, "align right");
    } else if (isError()) {
      add(createPlacesButton, "align right, split 3");
      add(reloadButton, "align right");
    }
    add(deleteButton, "align right, wrap");

    placeCbx.addItem(Program.EMPTY_PLACE);
    Program.getCave().forEach(placeCbx::addItem);

    Arrays.stream(MusicSupport.values()).forEach(musicSupportCbx::addItem);
    Arrays.stream(BottleColor.values()).forEach(colorCbx::addItem);
    Arrays.stream(BottlesStatus.values()).forEach(statusCbx::addItem);

    typeCbx.addItem("");
    MyCellarBottleContenance.getList().forEach(typeCbx::addItem);

    verifyStatusCbx.addItem(NONE);
    verifyStatusCbx.addItem(VALIDATED);
    verifyStatusCbx.addItem(TOCHECK);

    List<ShowFileColumn<?>> cols = new LinkedList<>(columns);
    // Remplissage de la table
    if (isTrash()) {
      model = new TableShowValues();
      model.setBottles(Program.getTrash());
      table = new JTable(model);
    } else if (isError()) {
      model = new ErrorShowValues();
      ((ErrorShowValues) model).setErrors(Program.getErrors());
      table = new JTable(model);
      titleLabel.setText(Program.getLabel("ShowFile.manageError"));
    } else {
      model = new ShowFileModel();
      String savedColumns;
      if (isWork()) {
        model.setBottles(workingBottles);
        savedColumns = Program.getShowColumnsWork();
      } else {
        model.setBottles(Program.getStorage().getAllList());
        savedColumns = Program.getShowColumns();
      }
      cols = new ArrayList<>();
      if (!savedColumns.isEmpty()) {
        String[] values = savedColumns.split(COLUMNS_SEPARATOR);
        for (ShowFileColumn<?> c : columns) {
          for (String s : values) {
            if (s.equals(c.getField().name())) {
              cols.add(c);
            }
          }
        }
      }
      if (cols.isEmpty()) {
        cols = columns.stream().filter((field) ->
            !field.getField().equals(MyCellarFields.VINEYARD)
                && !field.getField().equals(MyCellarFields.AOC)
                && !field.getField().equals(MyCellarFields.IGP)
                && !field.getField().equals(MyCellarFields.COUNTRY)).collect(Collectors.toList());
      } else {
        cols.add(0, checkBoxStartColumn);
        cols.add(modifyButtonColumn);
        if (isWork()) {
          cols.add(checkedButtonColumn);
        }
      }
      ((ShowFileModel) model).setColumns(cols);

      table = new JTable(model);
    }

    table.setAutoCreateRowSorter(true);
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
    sorter.setComparator(TableShowValues.PRICE, (String o1, String o2) -> {
      BigDecimal price1;
      if (o1.isEmpty()) {
        price1 = BigDecimal.ZERO;
      } else {
        price1 = Program.safeStringToBigDecimal(o1, BigDecimal.ZERO);
      }
      BigDecimal price2;
      if (o2.isEmpty()) {
        price2 = BigDecimal.ZERO;
      } else {
        price2 = Program.safeStringToBigDecimal(o2, BigDecimal.ZERO);
      }
      return price1.compareTo(price2);
    });
    table.setRowSorter(sorter);
    List<RowSorter.SortKey> sortKeys = new ArrayList<>();
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    sorter.setSortKeys(sortKeys);
    sorter.sort();
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
    updateModel(cols);

    table.setPreferredScrollableViewportSize(new Dimension(300, 200));
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "grow, span 2, wrap");
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
      LinkedList<MyCellarObject> toDeleteList = getSelectedBouteilles();

      if (toDeleteList.isEmpty()) {
        //"Aucun vin a supprimer!
        Erreur.showInformationMessage(Program.getError("Error064", LabelProperty.SINGLE), Program.getError("Error065", LabelProperty.THE_PLURAL));
      } else {
        String erreur_txt1, erreur_txt2;
        if (toDeleteList.size() == 1) {
          erreur_txt1 = Program.getError("Error067", LabelProperty.SINGLE); //"1 vin selectionne
          erreur_txt2 = Program.getError("Error068"); //"Voulez-vous le supprimer?
        } else {
          erreur_txt1 = MessageFormat.format(Program.getError("Error130", LabelProperty.PLURAL), toDeleteList.size()); //vins selectionnes.
          erreur_txt2 = Program.getError("Error131"); //"Voulez-vous les supprimer?
        }
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + SPACE + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
          if (isError()) {
            for (MyCellarObject b : toDeleteList) {
              Program.getErrors().remove(new MyCellarError(MyCellarError.ID.INEXISTING_PLACE, b));
            }
          } else {
            for (MyCellarObject b : toDeleteList) {
              Program.getStorage().addHistory(HistoryState.DEL, b);
              final Rangement rangement = b.getRangement();
              if (rangement != null) {
                rangement.removeObject(b);
              } else {
                Program.getStorage().deleteWine(b);
              }
              Program.setToTrash(b);
              if (isWork()) {
                workingBottles.remove(b);
              }
            }
          }
        }
        refresh();
      }
    } catch (MyCellarException exc) {
      Program.showException(exc);
    }
  }

  private LinkedList<MyCellarObject> getSelectedBouteilles() {
    final LinkedList<MyCellarObject> list = new LinkedList<>();
    int max_row = model.getRowCount();
    if (max_row == 0) {
      return list;
    }
    int row = 0;
    if (model instanceof ShowFileModel) {
      ShowFileModel showFileModel = (ShowFileModel) model;
      do {
        if (showFileModel.getValueAt(row, TableShowValues.ETAT).equals(Boolean.TRUE)) {
          list.add(showFileModel.getMyCellarObject(row));
        }
        row++;
      } while (row < max_row);
    } else {
      do {
        if (model.getValueAt(row, TableShowValues.ETAT).equals(Boolean.TRUE)) {
          list.add(model.getMyCellarObject(row));
        }
        row++;
      } while (row < max_row);
    }

    return list;
  }

  private void restore() {
    final LinkedList<MyCellarObject> toRestoreList = getSelectedBouteilles();

    if (toRestoreList.isEmpty()) {
      Erreur.showInformationMessage(Program.getLabel("ShowFile.NoBottleToRestore", LabelProperty.SINGLE), Program.getLabel("ShowFile.SelectToRestore", LabelProperty.THE_PLURAL));
    } else {
      String erreur_txt1, erreur_txt2;
      if (toRestoreList.size() == 1) {
        erreur_txt1 = Program.getError("Error067", LabelProperty.SINGLE); //"1 vin selectionne.");
        erreur_txt2 = Program.getLabel("ShowFile.RestoreOne");
      } else {
        erreur_txt1 = MessageFormat.format(Program.getError("Error130", LabelProperty.PLURAL), toRestoreList.size()); //vins selectionnes.");
        erreur_txt2 = Program.getLabel("ShowFile.RestoreSeveral");
      }
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, erreur_txt1 + SPACE + erreur_txt2, Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
        LinkedList<MyCellarObject> cantRestoreList = new LinkedList<>();
        for (MyCellarObject b : toRestoreList) {
          Program.getTrash().remove(b);
          if (b.isInExistingPlace()) {
            Rangement r = b.getRangement();
            if (r.isSimplePlace()) {
              Program.getStorage().addHistory(HistoryState.ADD, b);
              Program.getStorage().addWine(b);
            } else {
              if (r.getObject(b).isEmpty()) {
                Program.getStorage().addHistory(HistoryState.ADD, b);
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
  }

  private void refresh() {
    SwingUtilities.invokeLater(() -> {
      if (isTrash()) {
        model.setBottles(Program.getTrash());
      } else if (isError()) {
        ((ErrorShowValues) model).setErrors(Program.getErrors());
      } else if (isWork()) {
        model.setBottles(workingBottles);
      } else {
        model.setBottles(Program.getStorage().getAllList());
      }
    });
  }

  private void setRangementValue(MyCellarObject b, MyCellarFields field, Object value) {
    Rangement rangement = b.getRangement();
    int nValueToCheck = -1;
    String empl = b.getEmplacement();
    int num_empl = b.getNumLieu();
    int line = b.getLigne();
    int column = b.getColonne();

    if (field == MyCellarFields.PLACE) {
      rangement = (Rangement) value;
      empl = rangement.getName();
    } else if (field == MyCellarFields.NUM_PLACE) {
      try {
        num_empl = Integer.parseInt((String) value);
        nValueToCheck = num_empl;
      } catch (NumberFormatException e) {
        Erreur.showSimpleErreur(Program.getError("Error196"));
        return;
      }
    } else if (field == MyCellarFields.LINE) {
      try {
        line = Integer.parseInt((String) value);
        nValueToCheck = line;
      } catch (NumberFormatException e) {
        Erreur.showSimpleErreur(Program.getError("Error196"));
        return;
      }
    } else if (field == MyCellarFields.COLUMN) {
      try {
        column = Integer.parseInt((String) value);
        nValueToCheck = column;
      } catch (NumberFormatException e) {
        Erreur.showSimpleErreur(Program.getError("Error196"));
        return;
      }
    }

    Place place = null;
    if (field == MyCellarFields.PLACE) {
      placeCbx.setSelectedIndex(0);
      if (!rangement.isSimplePlace()) {
        final PanelPlace panelPlace = new PanelPlace(rangement, true, false, true, true, false);
        JOptionPane.showMessageDialog(Start.getInstance(), panelPlace,
            Program.getLabel("Main.ChooseCell"),
            JOptionPane.PLAIN_MESSAGE);
        place = panelPlace.getSelectedPlace();
        if (place.hasPlace()) {
          rangement = place.getRangement();
          empl = rangement.getName();
          num_empl = place.getPlaceNum();
          line = place.getLine();
          column = place.getColumn();
        } else {
          num_empl = -1;
        }
      }
    }

    if (field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN) {
      if (rangement != null && !rangement.isSimplePlace() && nValueToCheck <= 0) {
        Erreur.showSimpleErreur(Program.getError("Error197"));
        return;
      }
    }

    if (b.getEmplacement().compareTo(empl) != 0 || b.getNumLieu() != num_empl || b.getLigne() != line || b.getColonne() != column) {
      // Controle de l'emplacement de la bouteille
      if (place == null) {
        place = new Place.PlaceBuilder(rangement != null ? rangement : Program.EMPTY_PLACE).withNumPlace(num_empl).withLine(line).withColumn(column).build();
      }
      if (rangement != null && (place != null && rangement.canAddObjectAt(place))) {
        boolean hasObject = false;
        if (!rangement.isSimplePlace()) {
          final MyCellarObject bouteille = rangement.getObject(place).orElse(null);
          if (bouteille != null) {
            Erreur.showSimpleErreur(MessageFormat.format(Program.getError("Error059"), Program.convertStringFromHTMLString(bouteille.getNom()), bouteille.getAnnee()));
            hasObject = true;
          }
        }
        if (!hasObject) {
          if (field == MyCellarFields.PLACE) {
            b.setEmplacement(empl);
            b.setNumLieu(place.getPlaceNum());
            b.setLigne(place.getLine());
            b.setColonne(place.getColumn());
          } else if (field == MyCellarFields.NUM_PLACE) {
            b.setNumLieu(Integer.parseInt((String) value));
          } else if (field == MyCellarFields.LINE) {
            b.setLigne(Integer.parseInt((String) value));
          } else if (field == MyCellarFields.COLUMN) {
            b.setColonne(Integer.parseInt((String) value));
          }
          if (field == MyCellarFields.PLACE && rangement.isSimplePlace()) {
            if (b.getNumLieu() > rangement.getLastPartNumber()) {
              b.setNumLieu(rangement.getFreeNumPlaceInSimplePlace());
            }
            b.setLigne(0);
            b.setColonne(0);
          }
          RangementUtils.putTabStock();
          Program.setModified();
          b.setModified();
          Program.getStorage().addHistory(HistoryState.MODIFY, b);
        }
      } else {
        if (rangement != null && rangement.isSimplePlace()) {
          Erreur.showSimpleErreur(Program.getError("Error154"));
        } else {
          if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, Program.getError("Error198", LabelProperty.THE_SINGLE), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
            ProgramPanels.showBottle(b, true);
          }
        }
      }
    }
  }

  @Override
  public void setUpdateView(UpdateViewType updateViewType) {
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
      placeCbx.removeAllItems();
      Program.getCave().forEach(placeCbx::addItem);
    }

    if (updateViewType == UpdateViewType.CAPACITY || updateViewType == UpdateViewType.ALL) {
      typeCbx.removeAllItems();
      typeCbx.addItem("");
      MyCellarBottleContenance.getList().forEach(typeCbx::addItem);
    }

    updateModel(columns);
  }

  private void updateModel(List<ShowFileColumn<?>> columnsModel) {
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc;
    if (isError()) {
      tc = tcm.getColumn(ErrorShowValues.PLACE);
      tc.setCellEditor(new DefaultCellEditor(placeCbx));
      tc = tcm.getColumn(ErrorShowValues.TYPE);
      tc.setCellEditor(new DefaultCellEditor(typeCbx));
      tc = tcm.getColumn(ErrorShowValues.STATUS);
      tc.setCellRenderer(new FontBoldTableCellRenderer());
      tc = tcm.getColumn(ErrorShowValues.BUTTON);
      tc.setCellRenderer(new ButtonCellRenderer(Program.getLabel("Infos071"), MyCellarImage.ADD));
      tc.setCellEditor(new ButtonCellEditor());
    } else if (isNormal() || isWork()) {
      List<ShowFileColumn<?>> cols = filterColumns();
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
        if (column.getField().equals(MyCellarFields.PLACE)) {
          tc.setCellEditor(new DefaultCellEditor(placeCbx));
        } else if (column.getField().equals(MyCellarFields.TYPE)) {
          tc.setCellEditor(new DefaultCellEditor(typeCbx));
        } else if (column.getField().equals(MyCellarFields.COLOR)) {
          tc.setCellEditor(new DefaultCellEditor(colorCbx));
        } else if (column.getField().equals(MyCellarFields.SUPPORT)) {
          tc.setCellEditor(new DefaultCellEditor(musicSupportCbx));
        } else if (column.getField().equals(MyCellarFields.DURATION)) {
          tc.setCellEditor(new SimpleButtonEditor());
        } else if (column.getField().equals(MyCellarFields.STATUS)) {
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
  }

  private List<ShowFileColumn<?>> filterColumns() {
    String savedColumns;
    if (isWork()) {
      model.setBottles(workingBottles);
      savedColumns = Program.getShowColumnsWork();
    } else {
      model.setBottles(Program.getStorage().getAllList());
      savedColumns = Program.getShowColumns();
    }
    List<ShowFileColumn<?>> cols = new ArrayList<>();
    if (!savedColumns.isEmpty()) {
      String[] values = savedColumns.split(COLUMNS_SEPARATOR);
      for (ShowFileColumn<?> c : columns) {
        for (String s : values) {
          if (s.equals(c.getField().name())) {
            cols.add(c);
          }
        }
      }
    }
    if (cols.isEmpty()) {
      cols = columns.stream().filter((field) ->
          !field.getField().equals(MyCellarFields.VINEYARD)
              && !field.getField().equals(MyCellarFields.AOC)
              && !field.getField().equals(MyCellarFields.IGP)
              && !field.getField().equals(MyCellarFields.COUNTRY)).collect(Collectors.toList());
    } else {
      if (!cols.contains(checkBoxStartColumn)) {
        cols.add(0, checkBoxStartColumn);
      }
      if (!cols.contains(modifyButtonColumn)) {
        cols.add(modifyButtonColumn);
      }
      if (isWork() && !cols.contains(checkedButtonColumn)) {
        cols.add(checkedButtonColumn);
      }
    }
    return cols;
  }

  @Override
  public boolean tabWillClose(TabEvent event) {
    if (isError()) {
      if (Program.getErrors().stream().anyMatch(MyCellarError::isNotSolved)) {
        return JOptionPane.NO_OPTION != JOptionPane.showConfirmDialog(Start.getInstance(), Program.getLabel("ShowFile.QuitErrors"), Program.getLabel("Infos049"), JOptionPane.YES_NO_OPTION);
      }
    }
    RangementUtils.putTabStock();
    return true;
  }

  @Override
  public void tabClosed() {
    Start.getInstance().updateMainPanel();
  }

  public void Debug(String text) {
    Program.Debug("ShowFile: " + text);
  }

  public enum ShowType {
    NORMAL,
    TRASH,
    ERROR,
    WORK
  }

  private static class CreatePlacesAction extends AbstractAction {

    private static final long serialVersionUID = -3652414491735669984L;

    private CreatePlacesAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      RangementUtils.findRangementToCreate();
    }
  }

  private class ManageColumnsAction extends AbstractAction {

    private static final long serialVersionUID = 8165964725562440277L;

    private ManageColumnsAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JPanel panel = new JPanel();
      List<MyCellarFields> list;
      if (isWork()) {
        list = MyCellarFields.getFieldsListForImportAndWorksheet();
      } else {
        list = MyCellarFields.getFieldsList();
      }
      List<ShowFileColumn<?>> cols = ((ShowFileModel) model).getColumns();
      final List<ShowFileColumn<?>> showFileColumns = cols.stream().filter(ShowFileColumn::isDefault).collect(Collectors.toList());
      ManageColumnModel modelColumn = new ManageColumnModel(list, showFileColumns);
      JTable jTable = new JTable(modelColumn);
      TableColumnModel tcm = jTable.getColumnModel();
      TableColumn tc = tcm.getColumn(0);
      tc.setCellRenderer(new CheckboxCellRenderer());
      tc.setCellEditor(new CheckboxCellEditor());
      tc.setMinWidth(25);
      tc.setMaxWidth(25);
      panel.add(new JScrollPane(jTable));
      JOptionPane.showMessageDialog(Start.getInstance(), panel, Program.getLabel("Main.Columns"), JOptionPane.PLAIN_MESSAGE);
      List<Integer> properties = modelColumn.getSelectedColumns();
      if (!properties.isEmpty()) {
        cols = new ArrayList<>();
        cols.add(checkBoxStartColumn);
        Program.setModified();
        for (ShowFileColumn<?> c : columns) {
          if (properties.contains(c.getField().ordinal())) {
            cols.add(c);
          }
        }
        cols.add(modifyButtonColumn);
        if (isWork()) {
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
      if (isWork()) {
        Program.saveShowColumnsWork(buffer.toString());
      } else {
        Program.saveShowColumns(buffer.toString());
      }
      if (!cols.isEmpty()) {
        ((ShowFileModel) model).removeAllColumns();
        ((ShowFileModel) model).setColumns(cols);
        updateModel(cols);
      }
    }
  }

  class ModifyBottlesAction extends AbstractAction {

    private static final long serialVersionUID = -7590310564039085580L;

    private ModifyBottlesAction() {
      super("", MyCellarImage.WINE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      LinkedList<MyCellarObject> bottles = getSelectedBouteilles();
      if (bottles.isEmpty()) {
        //"Aucun vin a modifier!
        //"Veuillez selectionner les vins a modifier.
        Erreur.showInformationMessage(Program.getError("Error071", LabelProperty.SINGLE), Program.getError("Error072", LabelProperty.THE_PLURAL));
        return;
      }

      Debug("Modifying " + bottles.size() + " bottles...");
      LinkedList<MyCellarObject> existingBottles = new LinkedList<>();
      for (MyCellarObject bottle : bottles) {
        if (!Program.isExistingBottle(bottle)) {
          Debug("Inexisting bottle " + bottle.getNom() + " [" + bottle.getId() + "]");
          Erreur.showSimpleErreur(MessageFormat.format(Program.getError("ShowFile.InexisitingBottle", LabelProperty.THE_SINGLE), bottle.getNom()));
        } else {
          existingBottles.add(bottle);
        }
      }
      Program.modifyBottles(existingBottles);
    }
  }

  private class ReloadErrorsAction extends AbstractAction {

    private static final long serialVersionUID = 983425309954475989L;

    private ReloadErrorsAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      RangementUtils.putTabStock();
      ((ErrorShowValues) model).setErrors(Program.getErrors());
    }
  }

  private class ClearWorksheetAction extends AbstractAction {

    private static final long serialVersionUID = 983425309954475988L;

    private ClearWorksheetAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      workingBottles.clear();
      SwingUtilities.invokeLater(() -> {
        Program.getStorage().clearWorksheet();
        model.setBottles(workingBottles);
      });
    }
  }

  private class RemoveFromWorksheetAction extends AbstractAction {

    private static final long serialVersionUID = 983425309954475987L;

    private RemoveFromWorksheetAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(() -> {
        getSelectedBouteilles().forEach(Program.getStorage()::removeFromWorksheet);
        workingBottles.removeAll(getSelectedBouteilles());
        Program.setModified();
        model.fireTableDataChanged();
      });
    }
  }
}
