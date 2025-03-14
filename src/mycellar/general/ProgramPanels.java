package mycellar.general;

import mycellar.AddVin;
import mycellar.CreateTablePanel;
import mycellar.Export;
import mycellar.ITabListener;
import mycellar.ManageBottle;
import mycellar.MyCellarImage;
import mycellar.ParametersPanel;
import mycellar.Program;
import mycellar.ScreenType;
import mycellar.ShowHistory;
import mycellar.Stat;
import mycellar.capacity.CapacityPanel;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IPanelModifyable;
import mycellar.core.IPlacePosition;
import mycellar.core.IUpdatable;
import mycellar.core.MyCellarObject;
import mycellar.core.MyCellarSwingWorker;
import mycellar.core.UpdateViewType;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.text.LabelProperty;
import mycellar.core.uicomponents.JButtonTabComponent;
import mycellar.frame.MainFrame;
import mycellar.importer.Importer;
import mycellar.placesmanagement.CellarOrganizerPanel;
import mycellar.placesmanagement.Creer_Rangement;
import mycellar.placesmanagement.Supprimer_Rangement;
import mycellar.search.Search;
import mycellar.showfile.ErrorShowPanel;
import mycellar.showfile.ShowFile;
import mycellar.showfile.TrashPanel;
import mycellar.showfile.WorksheetPanel;
import mycellar.vignobles.VineyardPanel;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.STAR;
import static mycellar.ProgramConstants.THREE_DOTS;
import static mycellar.ScreenType.ADDVIN;
import static mycellar.ScreenType.CAPACITY;
import static mycellar.ScreenType.CELL_ORGANIZER;
import static mycellar.ScreenType.CHOOSE_CELL0;
import static mycellar.ScreenType.CHOOSE_CELL1;
import static mycellar.ScreenType.CHOOSE_CELL2;
import static mycellar.ScreenType.CHOOSE_CELL3;
import static mycellar.ScreenType.CHOOSE_CELL4;
import static mycellar.ScreenType.CREATE_PLACE;
import static mycellar.ScreenType.CREATE_TABLE;
import static mycellar.ScreenType.EXPORT;
import static mycellar.ScreenType.HISTORY;
import static mycellar.ScreenType.IMPORTER;
import static mycellar.ScreenType.MODIFY_PLACE;
import static mycellar.ScreenType.PARAMETERS;
import static mycellar.ScreenType.SEARCH;
import static mycellar.ScreenType.SHOW_ERRORS;
import static mycellar.ScreenType.SHOW_FILE;
import static mycellar.ScreenType.SHOW_TRASH;
import static mycellar.ScreenType.SHOW_WORKSHEET;
import static mycellar.ScreenType.STATS;
import static mycellar.ScreenType.SUPPRIMER_RANGEMENT;
import static mycellar.ScreenType.VIGNOBLES;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2012
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.1
 * @since 14/03/25
 */
public class ProgramPanels {

  public static final PanelInfos PANEL_INFOS = new PanelInfos();
  private static final JTabbedPane TABBED_PANE = new JTabbedPane();
  private static final List<TabLabel> TAB_LABELS = new ArrayList<>();

  private static final Map<ScreenType, IMyCellar> OPENED_PANELS = new EnumMap<>(ScreenType.class);
  private static final Map<ScreenType, IUpdatable> UPDATABLE_PANELS = new EnumMap<>(ScreenType.class);
  private static final Map<Integer, IUpdatable> UPDATABLE_MYCELLAROBJECTS = new HashMap<>();

  public static int findTab(ImageIcon image, Component component) {
    for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
      try {
        if (TABBED_PANE.getTabComponentAt(i) != null && TABBED_PANE.getIconAt(i) != null && TABBED_PANE.getIconAt(i).equals(image)) {
          return i;
        }
      } catch (RuntimeException ignored) {
      }
    }
    if (component != null) {
      return TABBED_PANE.indexOfComponent(component);
    }
    return -1;
  }

  public static int getSelectedTabIndex() {
    return TABBED_PANE.getSelectedIndex();
  }

  public static void updateSelectedTab() {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        UPDATABLE_PANELS.forEach((s, iUpdatable) -> {
          if (iUpdatable.equals(TABBED_PANE.getSelectedComponent())) {
            iUpdatable.updateView();
          }
        });
        UPDATABLE_MYCELLAROBJECTS.forEach((s, iUpdatable) -> {
          if (iUpdatable.equals(TABBED_PANE.getSelectedComponent())) {
            iUpdatable.updateView();
          }
        });
        updateVisibility();
      }
    }.execute();
  }

  public static boolean isCutCopyPastTab() {
    return TABBED_PANE.getSelectedComponent() != null && TABBED_PANE.getSelectedComponent() instanceof ICutCopyPastable;
  }

  public static <T> T getSelectedComponent(Class<T> className) {
    return className.cast(TABBED_PANE.getSelectedComponent());
  }

  public static void updateAllPanels() {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        Program.Debug("ProgramPanels: updateAllPanels");
        UPDATABLE_PANELS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.ALL));
        UPDATABLE_MYCELLAROBJECTS.forEach((s, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.ALL));
      }
    }.execute();
  }

  public static void updateAllPanelsForUpdatingPlaces() {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        Program.Debug("ProgramPanels: updateAllPanelsForUpdatingPlaces");
        UPDATABLE_PANELS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.PLACE));
        UPDATABLE_MYCELLAROBJECTS.forEach((s, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.PLACE));
      }
    }.execute();
  }

  public static void updateAllPanelsForUpdatingCapacity() {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        Program.Debug("ProgramPanels: updateAllPanelsForUpdatingCapacity");
        UPDATABLE_PANELS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.CAPACITY));
        UPDATABLE_MYCELLAROBJECTS.forEach((s, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.CAPACITY));
      }
    }.execute();
  }

  public static void updateAllPanelsForUpdatingVineyard() {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        Program.Debug("ProgramPanels: updateAllPanelsForUpdatingVineyard");
        UPDATABLE_PANELS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.VINEYARD));
        UPDATABLE_MYCELLAROBJECTS.forEach((s, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.VINEYARD));
      }
    }.execute();
  }

  public static void updateCellOrganizerPanel(boolean forceUpdate) {
    final IUpdatable cellOrganizer = UPDATABLE_PANELS.get(CELL_ORGANIZER);
    if (cellOrganizer != null) {
      cellOrganizer.setUpdateViewType(UpdateViewType.ALL);
      if (forceUpdate) {
        cellOrganizer.updateView();
      }
    }
  }

  public static void updatePanelsWithoutBottles() {
    UPDATABLE_PANELS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateViewType(UpdateViewType.ALL));
  }

  public static AddVin createAddVin() {
    AddVin addVin = (AddVin) OPENED_PANELS.get(ADDVIN);
    if (addVin == null) {
      addVin = new AddVin();
      OPENED_PANELS.put(ADDVIN, addVin);
      UPDATABLE_PANELS.put(ADDVIN, addVin);
    }
    return addVin;
  }

  public static Supprimer_Rangement createSupprimerRangement() {
    final Supprimer_Rangement supprimerRangement = (Supprimer_Rangement) createOpenedObject(Supprimer_Rangement.class, SUPPRIMER_RANGEMENT);
    UPDATABLE_PANELS.put(SUPPRIMER_RANGEMENT, supprimerRangement);
    return supprimerRangement;
  }

  public static void deleteSupprimerRangement() {
    OPENED_PANELS.remove(SUPPRIMER_RANGEMENT);
    UPDATABLE_PANELS.remove(SUPPRIMER_RANGEMENT);
  }

  public static Creer_Rangement createCreerRangement() {
    Creer_Rangement creerRangement = (Creer_Rangement) OPENED_PANELS.get(CREATE_PLACE);
    if (creerRangement == null) {
      creerRangement = new Creer_Rangement(false);
      OPENED_PANELS.put(CREATE_PLACE, creerRangement);
    }
    return creerRangement;
  }

  public static Creer_Rangement createModifierRangement() {
    Creer_Rangement creerRangement = (Creer_Rangement) OPENED_PANELS.get(MODIFY_PLACE);
    if (creerRangement == null) {
      creerRangement = new Creer_Rangement(true);
      OPENED_PANELS.put(MODIFY_PLACE, creerRangement);
      UPDATABLE_PANELS.put(MODIFY_PLACE, creerRangement);
    }
    return creerRangement;
  }

  public static Optional<Search> getSearch() {
    return Optional.ofNullable((Search) OPENED_PANELS.get(SEARCH));
  }

  public static Search createSearch() {
    final Search search = (Search) createOpenedObject(Search.class, SEARCH);
    UPDATABLE_PANELS.put(SEARCH, search);
    return search;
  }

  public static void updateSearchTable() {
    final Search search = (Search) OPENED_PANELS.get(SEARCH);
    if (search != null) {
      search.updateTable();
    }
  }

  public static CreateTablePanel createCreateTable() {
    final CreateTablePanel createTable = (CreateTablePanel) createOpenedObject(CreateTablePanel.class, CREATE_TABLE);
    UPDATABLE_PANELS.put(CREATE_TABLE, createTable);
    return createTable;
  }

  public static Importer createImporter() {
    return (Importer) createOpenedObject(Importer.class, IMPORTER);
  }

  public static Export createExport() {
    return (Export) createOpenedObject(Export.class, EXPORT);
  }

  public static Stat createStat() {
    final Stat stat = (Stat) createOpenedObject(Stat.class, STATS);
    UPDATABLE_PANELS.put(STATS, stat);
    return stat;
  }

  public static ShowHistory createShowHistory() {
    return (ShowHistory) createOpenedObject(ShowHistory.class, HISTORY);
  }

  public static VineyardPanel createVineyardPanel() {
    return (VineyardPanel) createOpenedObject(VineyardPanel.class, VIGNOBLES);
  }

  public static CapacityPanel createCapacityPanel() {
    return (CapacityPanel) createOpenedObject(CapacityPanel.class, CAPACITY);
  }

  public static ShowFile createShowFile() {
    final ShowFile showFile = (ShowFile) createOpenedObject(ShowFile.class, SHOW_FILE);
    UPDATABLE_PANELS.put(SHOW_FILE, showFile);
    return showFile;
  }

  public static TrashPanel createShowTrash() {
    final TrashPanel showFile = (TrashPanel) createOpenedObject(TrashPanel.class, SHOW_TRASH);
    UPDATABLE_PANELS.put(SHOW_TRASH, showFile);
    return showFile;
  }

  public static WorksheetPanel createWorksheetPanel() {
    final WorksheetPanel worksheetPanel = (WorksheetPanel) createOpenedObject(WorksheetPanel.class, SHOW_WORKSHEET);
    UPDATABLE_PANELS.put(SHOW_WORKSHEET, worksheetPanel);
    return worksheetPanel;
  }

  public static ErrorShowPanel createShowErrors() {
    final ErrorShowPanel errorShowPanel = (ErrorShowPanel) createOpenedObject(ErrorShowPanel.class, SHOW_ERRORS);
    UPDATABLE_PANELS.put(SHOW_ERRORS, errorShowPanel);
    return errorShowPanel;
  }

  public static CellarOrganizerPanel createCellarOrganizerPanel() {
    final CellarOrganizerPanel cellarOrganizerPanel = (CellarOrganizerPanel) createOpenedObject(CellarOrganizerPanel.class, CELL_ORGANIZER);
    UPDATABLE_PANELS.put(CELL_ORGANIZER, cellarOrganizerPanel);
    return cellarOrganizerPanel;
  }

  public static ParametersPanel createParametres() {
    return (ParametersPanel) createOpenedObject(ParametersPanel.class, PARAMETERS);
  }

  public static void deleteParameters() {
    OPENED_PANELS.remove(PARAMETERS);
  }

  public static CellarOrganizerPanel createChooseCellPanel(IPlacePosition iPlace) {
    CellarOrganizerPanel cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL0);
    if (cellarOrganizerPanel == null) {
      cellarOrganizerPanel = new CellarOrganizerPanel(iPlace);
      OPENED_PANELS.put(CHOOSE_CELL0, cellarOrganizerPanel);
      UPDATABLE_PANELS.put(CHOOSE_CELL0, cellarOrganizerPanel);
      return cellarOrganizerPanel;
    }
    cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL1);
    if (cellarOrganizerPanel == null) {
      cellarOrganizerPanel = new CellarOrganizerPanel(iPlace);
      OPENED_PANELS.put(CHOOSE_CELL1, cellarOrganizerPanel);
      UPDATABLE_PANELS.put(CHOOSE_CELL1, cellarOrganizerPanel);
      return cellarOrganizerPanel;
    }
    cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL2);
    if (cellarOrganizerPanel == null) {
      cellarOrganizerPanel = new CellarOrganizerPanel(iPlace);
      OPENED_PANELS.put(CHOOSE_CELL2, cellarOrganizerPanel);
      UPDATABLE_PANELS.put(CHOOSE_CELL2, cellarOrganizerPanel);
      return cellarOrganizerPanel;
    }
    cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL3);
    if (cellarOrganizerPanel == null) {
      cellarOrganizerPanel = new CellarOrganizerPanel(iPlace);
      OPENED_PANELS.put(CHOOSE_CELL3, cellarOrganizerPanel);
      UPDATABLE_PANELS.put(CHOOSE_CELL3, cellarOrganizerPanel);
      return cellarOrganizerPanel;
    }
    cellarOrganizerPanel = new CellarOrganizerPanel(iPlace);
    OPENED_PANELS.put(CHOOSE_CELL4, cellarOrganizerPanel);
    UPDATABLE_PANELS.put(CHOOSE_CELL4, cellarOrganizerPanel);
    return cellarOrganizerPanel;
  }

  public static void deleteChooseCellPanel(IPlacePosition iPlace) {
    CellarOrganizerPanel cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL0);
    if (cellarOrganizerPanel != null && cellarOrganizerPanel.getIPlace() == iPlace) {
      OPENED_PANELS.remove(CHOOSE_CELL0);
      UPDATABLE_PANELS.remove(CHOOSE_CELL0);
      return;
    }
    cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL1);
    if (cellarOrganizerPanel != null && cellarOrganizerPanel.getIPlace() == iPlace) {
      OPENED_PANELS.remove(CHOOSE_CELL1);
      UPDATABLE_PANELS.remove(CHOOSE_CELL1);
      return;
    }
    cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL2);
    if (cellarOrganizerPanel != null && cellarOrganizerPanel.getIPlace() == iPlace) {
      OPENED_PANELS.remove(CHOOSE_CELL2);
      UPDATABLE_PANELS.remove(CHOOSE_CELL2);
      return;
    }
    cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL3);
    if (cellarOrganizerPanel != null && cellarOrganizerPanel.getIPlace() == iPlace) {
      OPENED_PANELS.remove(CHOOSE_CELL3);
      UPDATABLE_PANELS.remove(CHOOSE_CELL3);
      return;
    }
    cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_PANELS.get(CHOOSE_CELL4);
    if (cellarOrganizerPanel != null && cellarOrganizerPanel.getIPlace() == iPlace) {
      OPENED_PANELS.remove(CHOOSE_CELL4);
      UPDATABLE_PANELS.remove(CHOOSE_CELL4);
    }
  }

  private static IMyCellar createOpenedObject(Class<?> className, ScreenType id) {
    IMyCellar object = OPENED_PANELS.get(id);
    if (object == null) {
      try {
        Constructor<?> ctor = className.getConstructor();
        object = (IMyCellar) ctor.newInstance();
      } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
        Program.showException(e);
      }
      OPENED_PANELS.put(id, object);
    }
    return object;
  }

  public static void showBottle(MyCellarObject myCellarObject, boolean edit) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
          Component tab = TABBED_PANE.getComponentAt(i);
          if (tab instanceof ManageBottle manageBottle && manageBottle.getMyCellarObject().equals(myCellarObject)) {
            TABBED_PANE.setSelectedIndex(i);
            return;
          }
        }
        ManageBottle manage = new ManageBottle(myCellarObject);
        manage.enableAll(edit);
        UPDATABLE_MYCELLAROBJECTS.put(myCellarObject.getId(), manage);
        String bottleName = myCellarObject.getNom();
        if (bottleName.length() > 30) {
          bottleName = bottleName.substring(0, 30) + SPACE + THREE_DOTS;
        }
        addTab(bottleName, MyCellarImage.WINE, manage);
      }
    }.execute();
  }

  public static void removeObjectTab(MyCellarObject myCellarObject) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
          Component tab = TABBED_PANE.getComponentAt(i);
          if (tab instanceof ManageBottle manageBottle && manageBottle.getMyCellarObject().equals(myCellarObject)) {
            removeTabAt(i);
            return;
          }
        }
      }
    }.execute();
  }

  public static void setSelectedPaneModified(boolean modify) {
    if (TABBED_PANE.getSelectedComponent() != null) {
      setPaneModified(TABBED_PANE.getSelectedIndex(), modify);
    }
  }

  public static void setAllPanesModified(boolean modify) {
    for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
      setPaneModified(i, modify);
    }
  }

  public static void setPaneModified(int index, boolean modified) {
    if (index == -1) {
      return;
    }
    if (modified) {
      MainFrame.setApplicationTitleModified();
    }
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        if (index < TABBED_PANE.getTabCount()) {
          Component tab = TABBED_PANE.getComponentAt(index);
          if (tab instanceof IPanelModifyable iPanelModifyable) {
            iPanelModifyable.setModified(modified);
          }
        }
      }
    }.execute();
  }

  public static void selectOrAddTab(Component component, IResource key, Icon icon) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        try {
          TABBED_PANE.setSelectedComponent(component);
          if (component instanceof IPanelModifyable iPanelModifyable) {
            iPanelModifyable.setPaneIndex(TABBED_PANE.getSelectedIndex());
          }
        } catch (IllegalArgumentException e) {
          addTab(getLabelWithProperty(key, LabelProperty.SINGLE), icon, component);
        }
      }
    }.execute();
  }

  public static void insertTab(String title, Icon icon, Component component, int index) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        TABBED_PANE.insertTab(title, icon, component, null, index);
        addCloseButtonToTab(component, getSelectedTabIndex(), true);
        insertTabLabel(index, title);
        TABBED_PANE.setSelectedIndex(index);
      }
    }.execute();
  }

  public static void addTab(String title, Icon icon, Component component) {
    new MyCellarSwingWorker() {
      @Override
      protected void done() {
        TABBED_PANE.addTab(title, icon, component);
        addCloseButtonToTab(component);
        int index = TABBED_PANE.getTabCount() - 1;
        if (component instanceof IPanelModifyable iPanelModifyable) {
          iPanelModifyable.setPaneIndex(index);
        }
        addTabLabel(index, title);
        TABBED_PANE.setSelectedIndex(index);
        updateVisibility();
      }
    }.execute();
  }

  public static void setTitleAt(int index, String title) {
    TABBED_PANE.setTitleAt(index, title);
    updateTabLabel(index, title);
    TABBED_PANE.setSelectedIndex(index);
  }

  private static void updateVisibility() {
    int count = TABBED_PANE.getTabCount();
    PANEL_INFOS.setVisible(count == 0);
    TABBED_PANE.setVisible(count > 0);
    if (count == 0) {
      PANEL_INFOS.refresh();
    }
  }

  private static void addTabLabel(int index, String label) {
    TAB_LABELS.add(new TabLabel(index, label));
  }

  private static void insertTabLabel(int index, String label) {
    TAB_LABELS.stream().filter(tabLabel -> tabLabel.getIndex() >= index).forEach(TabLabel::incrementIndex);
    TAB_LABELS.add(new TabLabel(index, label));
  }

  private static void updateTabLabel(int index, String label) {
    TAB_LABELS.stream()
        .filter(tabLabel -> tabLabel.getIndex() == index)
        .forEach(tabLabel -> tabLabel.setLabel(label));
    TAB_LABELS.add(new TabLabel(index, label));
  }

  public static void removeSelectedTab() {
    removeTabAt(TABBED_PANE.getSelectedIndex());
  }

  public static void removeTabAt(int index) {
    TABBED_PANE.removeTabAt(index);
    final List<TabLabel> tabLabels = TAB_LABELS.stream().filter(tabLabel -> tabLabel.getIndex() == index).toList();
    TAB_LABELS.removeAll(tabLabels);
    TAB_LABELS.stream().filter(tabLabel -> tabLabel.getIndex() > index).forEach(TabLabel::decrementIndex);
  }

  private static void addCloseButtonToTab(final Component component) {
    addCloseButtonToTab(component, -1, true);
  }

  private static void addCloseButtonToTab(final Component component, int indexToGoBack, boolean leftTabDirection) {
    final int index = TABBED_PANE.indexOfComponent(component);
    if (index != -1) {
      TABBED_PANE.setTabComponentAt(index,
          new JButtonTabComponent(TABBED_PANE, indexToGoBack));
    }

    TABBED_PANE.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_W)
            && (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)) {

          // Ctrl-W permet de fermer les onglets du JTabbedPane
          final int selectedIndex = TABBED_PANE.getSelectedIndex();
          if ((selectedIndex != -1)
              && (TABBED_PANE.getSelectedComponent().equals(component))) {

            // Un onglet est actif, supprimer le composant
            removeTabAt(selectedIndex);
            int previousIndex = leftTabDirection ? selectedIndex - 1 : indexToGoBack;
            if (previousIndex != -1 && TABBED_PANE.getTabCount() > previousIndex) {
              TABBED_PANE.setSelectedIndex(previousIndex);
            }

            // dereference itself
            TABBED_PANE.removeKeyListener(this);

            e.consume();
          }
        }
      }
    });
  }

  public static void removeAll() {
    TABBED_PANE.removeAll();
    TAB_LABELS.clear();
    UPDATABLE_PANELS.clear();
    UPDATABLE_MYCELLAROBJECTS.clear();
    OPENED_PANELS.clear();
  }

  public static boolean runExit() {
    for (Component c : TABBED_PANE.getComponents()) {
      if (c instanceof ITabListener iTabListener) {
        if (!iTabListener.tabWillClose(null)) {
          Program.Debug("ProgramPanels: Exiting program cancelled!");
          return false;
        }
      }
    }
    return true;
  }

  public static JTabbedPane getTabbedPane() {
    return TABBED_PANE;
  }

  public static boolean saveObjects() throws MyCellarException {
    for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
      Component tab = TABBED_PANE.getComponentAt(i);
      if (tab instanceof IPanelModifyable iPanelModifyable) {
        if (iPanelModifyable.isModified()) {
          if (tab instanceof ManageBottle manageBottle) {
            if (!manageBottle.save()) {
              return false;
            }
          }
          if (tab instanceof AddVin addVin) {
            if (!addVin.save()) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  static class TabLabel {
    private int index;
    private String label;
    private String modifiedLabel;
    private boolean modified;

    private TabLabel(int index, String label) {
      this.index = index;
      this.label = label;
      modifiedLabel = label + STAR;
      modified = false;
    }

    public int getIndex() {
      return index;
    }

    public String getLabel() {
      return modified ? modifiedLabel : label;
    }

    public void setLabel(String label) {
      this.label = label;
      modifiedLabel = label + STAR;
    }

    public boolean isModified() {
      return modified;
    }

    public void setModified(boolean modified) {
      this.modified = modified;
    }

    public void decrementIndex() {
      index--;
    }

    public void incrementIndex() {
      index++;
    }
  }
}
