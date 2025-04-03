package mycellar.showfile;


import mycellar.Bouteille;
import mycellar.Erreur;
import mycellar.ITabListener;
import mycellar.Music;
import mycellar.MyCellarImage;
import mycellar.Program;
import mycellar.actions.OpenAddVinAction;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellar;
import mycellar.core.IMyCellarEnum;
import mycellar.core.IMyCellarObject;
import mycellar.core.IUpdatable;
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
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static mycellar.MyCellarUtils.convertStringFromHTMLString;
import static mycellar.MyCellarUtils.parseIntOrError;
import static mycellar.MyCellarUtils.safeStringToBigDecimal;
import static mycellar.Program.isWineType;
import static mycellar.Program.throwNotImplementedIfNotFor;
import static mycellar.ProgramConstants.COLUMNS_SEPARATOR;
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
import static mycellar.general.ResourceKey.MAIN_CHOOSEDURATION;
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
 * @version 1.1
 * @since 03/04/25
 */

public abstract class AbstractShowFilePanel extends JPanel implements ITabListener, IMyCellar, IUpdatable {

  final MyCellarSimpleLabel titleLabel = new MyCellarSimpleLabel();
  final MyCellarLabel labelCount = new MyCellarLabel(MAIN_NUMBEROFITEMS, "");
  final MyCellarButton deleteButton = new MyCellarButton(MyCellarImage.DELETE);
  final MyCellarButton modifyButton = new MyCellarButton(MAIN_MODIFY, new ModifyBottlesAction());
  final MyCellarComboBox<AbstractPlace> placeCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<BottleColor> colorCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<MusicSupport> musicSupportCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<BottlesStatus> statusCbx = new MyCellarComboBox<>();
  private final MyCellarComboBox<State> verifyStatusCbx = new MyCellarComboBox<>();
  private final ShowFileColumn<State> checkedButtonColumn;
  private boolean updateView = false;
  private UpdateViewType updateViewType;
  final MyCellarComboBox<String> typeCbx = new MyCellarComboBox<>();
  final List<ShowFileColumn<?>> columns = new ArrayList<>();
  final LinkedList<IMyCellarObject> workingBottles = new LinkedList<>();
  TableShowValues model;
  JTable table;
  ShowFileColumn<Boolean> checkBoxStartColumn;
  ShowFileColumn<?> modifyButtonColumn;

  AbstractShowFilePanel(boolean worksheet) {
    checkBoxStartColumn = new ShowFileColumn<>(25, true, true, "", Boolean.FALSE) {
      @Override
      void setValue(IMyCellarObject b, Boolean value) {
        setMapValue(b, value);
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        return getMapValue(b);
      }
    };
    columns.add(checkBoxStartColumn);
    columns.add(new ShowFileColumn<>(MyCellarFields.NAME) {

      @Override
      void setValue(IMyCellarObject b, Object value) {
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        return convertStringFromHTMLString(b.getNom());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.YEAR, 50) {

      @Override
      void setValue(IMyCellarObject b, String value) {
        if (Program.hasYearControl() && Bouteille.isInvalidYear(value)) {
          Erreur.showSimpleErreur(getError(ERROR_ENTERVALIDYEAR));
        } else {
          setStringValue(b, value);
        }
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        return b.getAnnee();
      }
    });
    if (isWineType()) {
      columns.add(new ShowFileColumn<String>(MyCellarFields.TYPE) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          setStringValue(b, value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return b.getKind();
        }
      });
    }
    columns.add(new ShowFileColumn<AbstractPlace>(MyCellarFields.PLACE) {

      @Override
      void setValue(IMyCellarObject b, AbstractPlace value) {
        if (Program.EMPTY_PLACE.equals(value)) {
          Erreur.showSimpleErreur(getError(ERROR_SELECTSTORAGE));
          return;
        }
        setPlaceValue(b, MyCellarFields.PLACE, value);
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        if (b.isInTemporaryStock()) {
          return getLabel(BOUTEILLE_TEMPORARYPLACE);
        }
        return convertStringFromHTMLString(b.getEmplacement());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.NUM_PLACE, 50) {

      @Override
      void setValue(IMyCellarObject b, String value) {
        setPlaceValue(b, MyCellarFields.NUM_PLACE, value);
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        return Integer.toString(b.getNumLieu());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.LINE, 50) {

      @Override
      void setValue(IMyCellarObject b, String value) {
        setPlaceValue(b, MyCellarFields.LINE, value);
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        if (b.getAbstractPlace().isSimplePlace()) {
          return "";
        }
        return Integer.toString(b.getLigne());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.COLUMN, 50) {

      @Override
      void setValue(IMyCellarObject b, String value) {
        setPlaceValue(b, MyCellarFields.COLUMN, value);
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        if (b.getAbstractPlace().isSimplePlace()) {
          return "";
        }
        return Integer.toString(b.getColonne());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.PRICE, 50) {

      @Override
      void setValue(IMyCellarObject b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        return convertStringFromHTMLString(b.getPrix());
      }
    });
    columns.add(new ShowFileColumn<String>(MyCellarFields.COMMENT) {

      @Override
      void setValue(IMyCellarObject b, String value) {
        setStringValue(b, value);
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        return convertStringFromHTMLString(b.getComment());
      }
    });
    if (isWineType()) {
      columns.add(new ShowFileColumn<String>(MyCellarFields.MATURITY) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          setStringValue(b, value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          return convertStringFromHTMLString(((Bouteille) b).getMaturity());
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.PARKER) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          setStringValue(b, value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          return ((Bouteille) b).getParker();
        }
      });
      columns.add(new ShowFileColumn<BottleColor>(MyCellarFields.COLOR) {

        @Override
        void setValue(IMyCellarObject b, BottleColor value) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          b.setModified();
          Program.setModified();
          ((Bouteille) b).setColor(value.name());
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          return BottleColor.getColor(((Bouteille) b).getColor());
        }
      });
    } else if (Program.isMusicType()) {
      columns.add(new ShowFileColumn<MusicSupport>(MyCellarFields.SUPPORT) {

        @Override
        void setValue(IMyCellarObject b, MusicSupport value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setMusicSupport(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return ((Music) b).getMusicSupport();
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.STYLE) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setGenre(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return ((Music) b).getGenre();
        }
      });

      columns.add(new ShowFileColumn<String>(MyCellarFields.COMPOSER) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setComposer(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return ((Music) b).getComposer();
        }
      });

      columns.add(new ShowFileColumn<String>(MyCellarFields.ARTIST) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setArtist(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return ((Music) b).getArtist();
        }
      });

      columns.add(new ShowFileColumn<>(MyCellarFields.DURATION) {

        @Override
        public boolean execute(IMyCellarObject b, int row, int column) {
          throwNotImplementedIfNotFor(b, Music.class);
          Music music = (Music) b;
          PanelDuration panelDuration = new PanelDuration(DurationConverter.getTimeFromDisplay((String) getDisplayValue(music)));
          if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MainFrame.getInstance(), panelDuration,
              getLabel(MAIN_CHOOSEDURATION), JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.PLAIN_MESSAGE)) {
            b.setModified();
            Program.setModified();
            music.setDuration(DurationConverter.getValueFromTime(panelDuration.getTime()));
          }
          return false;
        }

        @Override
        void setValue(IMyCellarObject b, Object value) {
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return DurationConverter.getFormattedDisplay(((Music) b).getDuration());
        }
      });

      columns.add(new ShowFileColumn<Integer>(MyCellarFields.EXTERNAL_ID) {

        @Override
        void setValue(IMyCellarObject b, Integer value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setExternalId(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return ((Music) b).getExternalId();
        }
      });

      columns.add(new ShowFileColumn<String>(MyCellarFields.ALBUM) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          b.setModified();
          Program.setModified();
          ((Music) b).setAlbum(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return ((Music) b).getAlbum();
        }
      });
    }
    if (!worksheet) {
      columns.add(new ShowFileColumn<BottlesStatus>(MyCellarFields.STATUS) {

        @Override
        void setValue(IMyCellarObject b, BottlesStatus value) {
          b.setModified();
          Program.setModified();
          b.setStatus(value.name());
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          return BottlesStatus.getStatus(b.getStatus());
        }
      });
    }

    if (isWineType()) {
      columns.add(new ShowFileColumn<>(MyCellarFields.COUNTRY, 100, false) {

        @Override
        void setValue(IMyCellarObject b, Object value) {
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
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
        void setValue(IMyCellarObject b, Object value) {
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          if (((Bouteille) b).getVignoble() == null) {
            return "";
          }
          return ((Bouteille) b).getVignoble().getName();
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.AOC, 100, false) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          VignobleJaxb v = ((Bouteille) b).getVignoble();
          if (v == null) {
            return;
          }
          b.setModified();
          Program.setModified();
          v.setAOC(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          if (((Bouteille) b).getVignoble() == null) {
            return "";
          }
          return ((Bouteille) b).getVignoble().getAOC();
        }
      });
      columns.add(new ShowFileColumn<String>(MyCellarFields.IGP, 100, false) {

        @Override
        void setValue(IMyCellarObject b, String value) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          VignobleJaxb v = ((Bouteille) b).getVignoble();
          if (v == null) {
            return;
          }
          b.setModified();
          Program.setModified();
          v.setIGP(value);
        }

        @Override
        Object getDisplayValue(IMyCellarObject b) {
          throwNotImplementedIfNotFor(b, Bouteille.class);
          if (((Bouteille) b).getVignoble() == null) {
            return "";
          }
          return ((Bouteille) b).getVignoble().getIGP();
        }
      });
    }
    modifyButtonColumn = new ShowFileColumn<>(100, true, getLabel(SHOWFILE_MORE)) {
      @Override
      void setValue(IMyCellarObject b, Object value) {
      }

      @Override
      Object getDisplayValue(IMyCellarObject b) {
        return null;
      }

      @Override
      public boolean execute(IMyCellarObject myCellarObject, int row, int column) {
        if (Program.isNotExistingMyCellarObject(myCellarObject)) {
          Debug("Object " + myCellarObject.getNom() + " [" + myCellarObject.getId() + "] doesn't exist");
          Erreur.showSimpleErreur(getError(ERROR_INEXISTINGBOTTLE, myCellarObject.getNom()));
          return false;
        }
        ProgramPanels.showBottle(myCellarObject, true);
        return false;
      }
    };
    columns.add(modifyButtonColumn);
    checkedButtonColumn = new ShowFileColumn<>(100, true, false, getLabel(SHOWFILE_VALID), null) {
      @Override
      void setValue(IMyCellarObject b, State value) {
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
      Object getDisplayValue(IMyCellarObject b) {
        return getMapValue(b);
      }

      @Override
      public String getColumnName() {
        return getLabel(SHOWFILE_STATUS);
      }
    };
    if (worksheet) {
      columns.add(checkedButtonColumn);
    }
  }

  void postInit() {
    initPlacesCombo();

    if (Program.isMusicType()) {
      Arrays.stream(MusicSupport.values()).forEach(musicSupportCbx::addItem);
    } else if (Program.isWineType()) {
      Arrays.stream(BottleColor.values()).forEach(colorCbx::addItem);
    }
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
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
    sorter.setComparator(TableShowValues.PRICE, (String o1, String o2) -> {
      BigDecimal price1;
      if (o1.isEmpty()) {
        price1 = BigDecimal.ZERO;
      } else {
        price1 = safeStringToBigDecimal(o1, BigDecimal.ZERO);
      }
      BigDecimal price2;
      if (o2.isEmpty()) {
        price2 = BigDecimal.ZERO;
      } else {
        price2 = safeStringToBigDecimal(o2, BigDecimal.ZERO);
      }
      return price1.compareTo(price2);
    });
    table.setRowSorter(sorter);
    List<RowSorter.SortKey> sortKeys = new ArrayList<>();
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    sorter.setSortKeys(sortKeys);
    sorter.sort();
  }

  void delete() {
    try {
      List<IMyCellarObject> toDeleteList = getSelectedMyCellarObjects();

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
          for (IMyCellarObject b : toDeleteList) {
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

  List<IMyCellarObject> getSelectedMyCellarObjects() {
    int max_row = model.getRowCount();
    if (max_row == 0) {
      return Collections.emptyList();
    }
    final LinkedList<IMyCellarObject> list = new LinkedList<>();
    int row = 0;
    if (model instanceof ShowFileModel showFileModel) {
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

  void restore() {
    final List<IMyCellarObject> toRestoreList = getSelectedMyCellarObjects();

    if (toRestoreList.isEmpty()) {
      Erreur.showInformationMessage(SHOWFILE_NOBOTTLETORESTORE, SHOWFILE_SELECTTORESTORE);
    } else {
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
        LinkedList<IMyCellarObject> cantRestoreList = new LinkedList<>();
        for (IMyCellarObject b : toRestoreList) {
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
  }

  abstract protected void refresh();

  private void setPlaceValue(IMyCellarObject b, MyCellarFields field, Object value) {
    AbstractPlace abstractPlace = b.getAbstractPlace();
    int nValueToCheck = -1;
    String empl = b.getEmplacement();
    int num_empl = b.getNumLieu();
    int line = b.getLigne();
    int column = b.getColonne();

    if (field == MyCellarFields.PLACE) {
      abstractPlace = (AbstractPlace) value;
      empl = abstractPlace.getName();
    } else if (field == MyCellarFields.NUM_PLACE) {
      Integer i = parseIntOrError(String.valueOf(value));
      if (i == null) {
        return;
      }
      num_empl = i;
      nValueToCheck = i;
    } else if (field == MyCellarFields.LINE) {
      Integer i = parseIntOrError(String.valueOf(value));
      if (i == null) {
        return;
      }
      line = i;
      nValueToCheck = i;
    } else if (field == MyCellarFields.COLUMN) {
      Integer i = parseIntOrError(String.valueOf(value));
      if (i == null) {
        return;
      }
      column = i;
      nValueToCheck = i;
    }

    PlacePosition place = null;
    if (field == MyCellarFields.PLACE) {
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

    if (field == MyCellarFields.NUM_PLACE || field == MyCellarFields.LINE || field == MyCellarFields.COLUMN) {
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
          final IMyCellarObject bouteille = ((ComplexPlace) abstractPlace).getObject(place).orElse(null);
          if (bouteille != null) {
            Erreur.showSimpleErreur(getError(ERROR_ALREADYINSTORAGE, convertStringFromHTMLString(bouteille.getNom()), bouteille.getAnnee()));
            hasObject = true;
          }
        }
        if (!hasObject) {
          if (field == MyCellarFields.PLACE) {
            b.setEmplacement(empl);
            b.setNumLieu(place.getPart());
            b.setLigne(place.getLine());
            b.setColonne(place.getColumn());
          } else if (field == MyCellarFields.NUM_PLACE) {
            b.setNumLieu(Integer.parseInt((String) value));
          } else if (field == MyCellarFields.LINE) {
            b.setLigne(Integer.parseInt((String) value));
          } else if (field == MyCellarFields.COLUMN) {
            b.setColonne(Integer.parseInt((String) value));
          }
          if (field == MyCellarFields.PLACE && abstractPlace.isSimplePlace()) {
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

  List<ShowFileColumn<?>> filterColumns(boolean worksheet) {
    String savedColumns;
    if (worksheet) {
      model.setMyCellarObjects(workingBottles);
      savedColumns = Program.getShowColumnsWork();
    } else {
      model.setMyCellarObjects(Program.getStorage().getAllList());
      savedColumns = Program.getShowColumns();
    }
    labelCount.setValue(Integer.toString(model.getRowCount()));
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
              && !field.getField().equals(MyCellarFields.COUNTRY)).collect(toList());
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

    public ManageColumnsAction(boolean worksheet) {
      this.worksheet = worksheet;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JPanel panel = new JPanel();
      List<MyCellarFields> list;
      if (worksheet) {
        list = MyCellarFields.getFieldsListForImportAndWorksheet();
      } else {
        list = MyCellarFields.getFieldsList();
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
        for (ShowFileColumn<?> c : columns) {
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
      List<IMyCellarObject> selectedObjects = getSelectedMyCellarObjects();
      if (selectedObjects.isEmpty()) {
        Erreur.showInformationMessage(ERROR_NOITEMTOMODIFY, ERROR_SELECTITEMTOMODIFY);
        return;
      }

      Debug("Modifying " + selectedObjects.size() + " objects...");
      LinkedList<IMyCellarObject> existingObjects = new LinkedList<>();
      for (IMyCellarObject bottle : selectedObjects) {
        if (Program.isNotExistingMyCellarObject(bottle)) {
          Debug("Object " + bottle.getNom() + " [" + bottle.getId() + "] doesn't exist");
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
