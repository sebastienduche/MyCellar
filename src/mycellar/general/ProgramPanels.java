package mycellar.general;

import mycellar.AddVin;
import mycellar.Bouteille;
import mycellar.Creer_Tableaux;
import mycellar.Export;
import mycellar.ManageBottle;
import mycellar.MyCellarImage;
import mycellar.Parametres;
import mycellar.Program;
import mycellar.ScreenType;
import mycellar.Search;
import mycellar.ShowHistory;
import mycellar.Start;
import mycellar.Stat;
import mycellar.Utils;
import mycellar.capacity.CapacityPanel;
import mycellar.core.ICutCopyPastable;
import mycellar.core.IMyCellar;
import mycellar.core.IPlace;
import mycellar.core.IUpdatable;
import mycellar.core.LabelProperty;
import mycellar.core.MyCellarObject;
import mycellar.importer.Importer;
import mycellar.placesmanagement.CellarOrganizerPanel;
import mycellar.placesmanagement.Creer_Rangement;
import mycellar.placesmanagement.Supprimer_Rangement;
import mycellar.showfile.ShowFile;
import mycellar.vignobles.VineyardPanel;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.STAR;
import static mycellar.ProgramConstants.THREE_DOTS;
import static mycellar.ScreenType.ADDVIN;
import static mycellar.ScreenType.CAPACITY;
import static mycellar.ScreenType.CELL_ORGANIZER;
import static mycellar.ScreenType.CHOOSE_CELL;
import static mycellar.ScreenType.CREATE_PLACE;
import static mycellar.ScreenType.CREER_TABLEAU;
import static mycellar.ScreenType.EXPORT;
import static mycellar.ScreenType.HISTORY;
import static mycellar.ScreenType.IMPORTER;
import static mycellar.ScreenType.MODIFY_PLACE;
import static mycellar.ScreenType.PARAMETRES;
import static mycellar.ScreenType.SEARCH;
import static mycellar.ScreenType.SHOW_ERRORS;
import static mycellar.ScreenType.SHOW_FILE;
import static mycellar.ScreenType.SHOW_TRASH;
import static mycellar.ScreenType.SHOW_WORKSHEET;
import static mycellar.ScreenType.STATS;
import static mycellar.ScreenType.SUPPRIMER_RANGEMENT;
import static mycellar.ScreenType.VIGNOBLES;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2012</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 27/10/21
 */
public class ProgramPanels {

  public static final PanelInfos PANEL_INFOS = new PanelInfos();
  public static final JTabbedPane TABBED_PANE = new JTabbedPane();

  private static final Map<ScreenType, IMyCellar> OPENED_OBJECTS = new EnumMap<>(ScreenType.class);
  private static final Map<ScreenType, IUpdatable> UPDATABLE_OBJECTS = new EnumMap<>(ScreenType.class);
  private static final Map<Integer, IUpdatable> UPDATABLE_BOTTLES = new HashMap<>();

  public static int findTab(ImageIcon image) {
    for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
      try {
        if (TABBED_PANE.getTabComponentAt(i) != null && TABBED_PANE.getIconAt(i) != null && TABBED_PANE.getIconAt(i).equals(image)) {
          return i;
        }
      } catch (RuntimeException ignored) {
      }
    }
    return -1;
  }

  public static int getSelectedTabIndex() {
    return TABBED_PANE.getSelectedIndex();
  }

  public static void updateSelectedTab() {
    UPDATABLE_OBJECTS.forEach((s, iUpdatable) -> {
      if (iUpdatable.equals(TABBED_PANE.getSelectedComponent())) {
        iUpdatable.updateView();
      }
    });
    UPDATABLE_BOTTLES.forEach((s, iUpdatable) -> {
      if (iUpdatable.equals(TABBED_PANE.getSelectedComponent())) {
        iUpdatable.updateView();
      }
    });
  }

  public static boolean isCutCopyPastTab() {
    return TABBED_PANE.getSelectedComponent() != null && TABBED_PANE.getSelectedComponent() instanceof ICutCopyPastable;
  }

  public static <T> T getSelectedComponent(Class<T> className) {
    return className.cast(TABBED_PANE.getSelectedComponent());
  }

  public static void clearObjectsVariables() {
    UPDATABLE_OBJECTS.clear();
    UPDATABLE_BOTTLES.clear();
    OPENED_OBJECTS.clear();
  }

  public static void updateAllPanels() {
    UPDATABLE_OBJECTS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateView());
    UPDATABLE_BOTTLES.forEach((s, iUpdatable) -> iUpdatable.setUpdateView());
  }

  public static void updateManagePlacePanel() {
    final IUpdatable managePlace = UPDATABLE_OBJECTS.get(CELL_ORGANIZER);
    if (managePlace != null) {
      managePlace.setUpdateView();
    }
  }

  public static void updatePanelsWithoutBottles() {
    UPDATABLE_OBJECTS.forEach((screenType, iUpdatable) -> iUpdatable.setUpdateView());
  }

  public static AddVin getAddVin() {
    return (AddVin) OPENED_OBJECTS.get(ADDVIN);
  }

  public static AddVin createAddVin() {
    AddVin addVin = (AddVin) OPENED_OBJECTS.get(ADDVIN);
    if (addVin == null) {
      addVin = new AddVin();
      OPENED_OBJECTS.put(ADDVIN, addVin);
      UPDATABLE_OBJECTS.put(ADDVIN, addVin);
    }
    return addVin;
  }

  public static Supprimer_Rangement getSupprimerRangement() {
    return (Supprimer_Rangement) OPENED_OBJECTS.get(SUPPRIMER_RANGEMENT);
  }

  public static Supprimer_Rangement createSupprimerRangement() {
    final Supprimer_Rangement supprimerRangement = (Supprimer_Rangement) createOpenedObject(Supprimer_Rangement.class, SUPPRIMER_RANGEMENT);
    UPDATABLE_OBJECTS.put(SUPPRIMER_RANGEMENT, supprimerRangement);
    return supprimerRangement;
  }

  public static void deleteSupprimerRangement() {
    OPENED_OBJECTS.remove(SUPPRIMER_RANGEMENT);
    UPDATABLE_OBJECTS.remove(SUPPRIMER_RANGEMENT);
  }

  public static Creer_Rangement getCreerRangement() {
    return (Creer_Rangement) OPENED_OBJECTS.get(CREATE_PLACE);
  }

  public static Creer_Rangement createCreerRangement() {
    Creer_Rangement creerRangement = (Creer_Rangement) OPENED_OBJECTS.get(CREATE_PLACE);
    if (creerRangement == null) {
      creerRangement = new Creer_Rangement(false);
      OPENED_OBJECTS.put(CREATE_PLACE, creerRangement);
    }
    return creerRangement;
  }

  public static Creer_Rangement getModifierRangement() {
    return (Creer_Rangement) OPENED_OBJECTS.get(MODIFY_PLACE);
  }

  public static Creer_Rangement createModifierRangement() {
    Creer_Rangement creerRangement = (Creer_Rangement) OPENED_OBJECTS.get(MODIFY_PLACE);
    if (creerRangement == null) {
      creerRangement = new Creer_Rangement(true);
      OPENED_OBJECTS.put(MODIFY_PLACE, creerRangement);
      UPDATABLE_OBJECTS.put(MODIFY_PLACE, creerRangement);
    }
    return creerRangement;
  }

  public static Optional<Search> getSearch() {
    return Optional.ofNullable((Search) OPENED_OBJECTS.get(SEARCH));
  }

  public static Search createSearch() {
    final Search search = (Search) createOpenedObject(Search.class, SEARCH);
    UPDATABLE_OBJECTS.put(SEARCH, search);
    return search;
  }

  public static Creer_Tableaux getCreerTableaux() {
    return (Creer_Tableaux) OPENED_OBJECTS.get(CREER_TABLEAU);
  }

  public static Creer_Tableaux createCreerTableaux() {
    final Creer_Tableaux creerTableaux = (Creer_Tableaux) createOpenedObject(Creer_Tableaux.class, CREER_TABLEAU);
    UPDATABLE_OBJECTS.put(CREER_TABLEAU, creerTableaux);
    return creerTableaux;
  }

  public static Importer getImporter() {
    return (Importer) OPENED_OBJECTS.get(IMPORTER);
  }

  public static Importer createImporter() {
    final Importer importer = (Importer) createOpenedObject(Importer.class, IMPORTER);
    return importer;
  }

  public static Export getExport() {
    return (Export) OPENED_OBJECTS.get(EXPORT);
  }

  public static Export createExport() {
    final Export export = (Export) createOpenedObject(Export.class, EXPORT);
    return export;
  }

  public static Stat getStat() {
    return (Stat) OPENED_OBJECTS.get(STATS);
  }

  public static Stat createStat() {
    final Stat stat = (Stat) createOpenedObject(Stat.class, STATS);
    UPDATABLE_OBJECTS.put(STATS, stat);
    return stat;
  }

  public static ShowHistory getShowHistory() {
    return (ShowHistory) OPENED_OBJECTS.get(HISTORY);
  }

  public static ShowHistory createShowHistory() {
    final ShowHistory showHistory = (ShowHistory) createOpenedObject(ShowHistory.class, HISTORY);
    return showHistory;
  }

  public static VineyardPanel getVineyardPanel() {
    return (VineyardPanel) OPENED_OBJECTS.get(VIGNOBLES);
  }

  public static CapacityPanel getCapacityPanel() {
    return (CapacityPanel) OPENED_OBJECTS.get(CAPACITY);
  }

  public static VineyardPanel createVineyardPanel() {
    final VineyardPanel vineyardPanel = (VineyardPanel) createOpenedObject(VineyardPanel.class, VIGNOBLES);
    return vineyardPanel;
  }

  public static CapacityPanel createCapacityPanel() {
    final CapacityPanel capacityPanel = (CapacityPanel) createOpenedObject(CapacityPanel.class, CAPACITY);
    return capacityPanel;
  }

  public static ShowFile getShowFile() {
    return (ShowFile) OPENED_OBJECTS.get(SHOW_FILE);
  }

  public static ShowFile createShowFile() {
    final ShowFile showFile = (ShowFile) createOpenedObject(ShowFile.class, SHOW_FILE);
    UPDATABLE_OBJECTS.put(SHOW_FILE, showFile);
    return showFile;
  }

  public static ShowFile getShowTrash() {
    return (ShowFile) OPENED_OBJECTS.get(SHOW_TRASH);
  }

  public static ShowFile createShowTrash() {
    ShowFile showFile = (ShowFile) OPENED_OBJECTS.get(SHOW_TRASH);
    if (showFile == null) {
      showFile = new ShowFile(ShowFile.ShowType.TRASH);
      OPENED_OBJECTS.put(SHOW_TRASH, showFile);
      UPDATABLE_OBJECTS.put(SHOW_TRASH, showFile);
    }
    return showFile;
  }

  public static ShowFile getShowWorksheet() {
    return (ShowFile) OPENED_OBJECTS.get(SHOW_WORKSHEET);
  }

  public static ShowFile createShowWorksheet() {
    ShowFile showFile = (ShowFile) OPENED_OBJECTS.get(SHOW_WORKSHEET);
    if (showFile == null) {
      showFile = new ShowFile(ShowFile.ShowType.WORK);
      OPENED_OBJECTS.put(SHOW_WORKSHEET, showFile);
      UPDATABLE_OBJECTS.put(SHOW_WORKSHEET, showFile);
    }
    return showFile;
  }

  public static ShowFile getShowErrors() {
    return (ShowFile) OPENED_OBJECTS.get(SHOW_ERRORS);
  }

  public static ShowFile createShowErrors() {
    ShowFile showFile = (ShowFile) OPENED_OBJECTS.get(SHOW_ERRORS);
    if (showFile == null) {
      showFile = new ShowFile(ShowFile.ShowType.ERROR);
      OPENED_OBJECTS.put(SHOW_ERRORS, showFile);
      UPDATABLE_OBJECTS.put(SHOW_ERRORS, showFile);
    }
    return showFile;
  }

  public static CellarOrganizerPanel getCellarOrganizerPanel() {
    return (CellarOrganizerPanel) OPENED_OBJECTS.get(CELL_ORGANIZER);
  }

  public static CellarOrganizerPanel createCellarOrganizerPanel() {
    final CellarOrganizerPanel cellarOrganizerPanel = (CellarOrganizerPanel) createOpenedObject(CellarOrganizerPanel.class, CELL_ORGANIZER);
    UPDATABLE_OBJECTS.put(CELL_ORGANIZER, cellarOrganizerPanel);
    return cellarOrganizerPanel;
  }

  public static Parametres getParametres() {
    return (Parametres) OPENED_OBJECTS.get(PARAMETRES);
  }

  public static Parametres createParametres() {
    final Parametres parametres = (Parametres) createOpenedObject(Parametres.class, PARAMETRES);
    return parametres;
  }

  public static void deleteParametres() {
    OPENED_OBJECTS.remove(PARAMETRES);
  }

  public static CellarOrganizerPanel getCellChoosePanel() {
    return (CellarOrganizerPanel) OPENED_OBJECTS.get(CHOOSE_CELL);
  }

  public static CellarOrganizerPanel createChooseCellPanel(IPlace iPlace) {
    CellarOrganizerPanel cellarOrganizerPanel = (CellarOrganizerPanel) OPENED_OBJECTS.get(CHOOSE_CELL);
    if (cellarOrganizerPanel == null) {
      cellarOrganizerPanel = new CellarOrganizerPanel(iPlace);
      OPENED_OBJECTS.put(CHOOSE_CELL, cellarOrganizerPanel);
      UPDATABLE_OBJECTS.put(CHOOSE_CELL, cellarOrganizerPanel);
    }
    return cellarOrganizerPanel;
  }

  public static void deleteChooseCellPanel() {
    OPENED_OBJECTS.remove(CHOOSE_CELL);
    UPDATABLE_OBJECTS.remove(CHOOSE_CELL);
  }

  private static IMyCellar createOpenedObject(Class<?> className, ScreenType id) {
    IMyCellar object = OPENED_OBJECTS.get(id);
    if (object == null) {
      try {
        Constructor<?> ctor = className.getConstructor();
        object = (IMyCellar) ctor.newInstance();
      } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
        Program.showException(e);
      }
      OPENED_OBJECTS.put(id, object);
    }
    return object;
  }

  public static void showBottle(MyCellarObject myCellarObject, boolean edit) {
    for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
      Component tab = TABBED_PANE.getComponentAt(i);
      if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(myCellarObject)) {
        TABBED_PANE.setSelectedIndex(i);
        return;
      }
    }
    ManageBottle manage = new ManageBottle(myCellarObject);
    manage.enableAll(edit);
    UPDATABLE_BOTTLES.put(myCellarObject.getId(), manage);
    String bottleName = myCellarObject.getNom();
    if (bottleName.length() > 30) {
      bottleName = bottleName.substring(0, 30) + SPACE + THREE_DOTS;
    }
    TABBED_PANE.addTab(bottleName, MyCellarImage.WINE, manage);
    TABBED_PANE.setSelectedIndex(TABBED_PANE.getTabCount() - 1);
    Utils.addCloseButton(TABBED_PANE, manage);
    Start.getInstance().updateMainPanel();
  }

  public static void removeBottleTab(Bouteille bottle) {
    for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
      Component tab = TABBED_PANE.getComponentAt(i);
      if (tab instanceof ManageBottle && ((ManageBottle) tab).getBottle().equals(bottle)) {
        TABBED_PANE.removeTabAt(i);
        return;
      }
    }
  }

  public static void setSelectedPaneModified(boolean modify) {
    if (TABBED_PANE.getSelectedComponent() != null) {
      int index = TABBED_PANE.getSelectedIndex();
      setPaneModified(index, modify);
    }
  }

  public static void setAllPanesModified(boolean modify) {
    for (int i = 0; i < TABBED_PANE.getTabCount(); i++) {
      setPaneModified(i, modify);
    }
  }

  private static void setPaneModified(int index, boolean modify) {
    String title = TABBED_PANE.getTitleAt(index);
    if (modify) {
      if (!title.endsWith(STAR)) {
        TABBED_PANE.setTitleAt(index, title + STAR);
        TABBED_PANE.updateUI();
      }
    } else {
      if (title.endsWith(STAR)) {
        title = title.substring(0, title.length() - 1);
      }
      TABBED_PANE.setTitleAt(index, title);
    }
  }

  public static void selectOrAddTab(Component component, String tabLabel, Icon icon) {
    try {
      TABBED_PANE.setSelectedComponent(component);
    } catch (IllegalArgumentException e) {
      addTab(component, tabLabel, icon);
      TABBED_PANE.setSelectedComponent(component);
    }
  }

  public static void addTab(Component component, String tabLabel, Icon icon) {
    try {
      TABBED_PANE.add(Program.getLabel(tabLabel, LabelProperty.SINGLE), component);
      TABBED_PANE.setIconAt(TABBED_PANE.getTabCount() - 1, icon);
      Utils.addCloseButton(TABBED_PANE, component);
    } catch (RuntimeException e) {
      Program.showException(e);
    }
  }

  public static void updateVisibility() {
    int count = TABBED_PANE.getTabCount();
    PANEL_INFOS.setVisible(count == 0);
    TABBED_PANE.setVisible(count > 0);
    if (count == 0) {
      PANEL_INFOS.refresh();
    }
  }
}
