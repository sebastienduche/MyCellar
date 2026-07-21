package mycellar.core.storage;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetData;
import mycellar.core.datas.worksheet.WorkSheetList;
import mycellar.core.exceptions.MyCellarException;
import mycellar.vignobles.CountryVignobleController;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static mycellar.MyCellarUtils.isDefined;
import static mycellar.Program.NO_COUNTRY;
import static mycellar.Program.hasSameHistoryId;
import static mycellar.Program.hasSameId;
import static mycellar.ProgramConstants.HISTORY_XML;
import static mycellar.ProgramConstants.WORKSHEET_XML;
import static mycellar.core.MyCellarSettings.CONVERTED_TO_UUID;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceErrorKey.ERROR_CONFIRMDELETIONALLHISTORY;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONDELETECHECKEDHISTORY;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONDELETEENTEREDHISTORY;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONDELETEEXITEDHISTORY;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONDELETEMODIFIEDHISTORY;
import static mycellar.general.ResourceErrorKey.ERROR_QUESTIONDELETEVALIDATEDHISTORY;
import static mycellar.general.ResourceKey.MAIN_ASKCONFIRMATION;
import static mycellar.vignobles.CountryVignobleController.getUUIDFromCountry;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 9.0
 * @since 21/07/26
 */

public class SerializedStorage implements Storage {

  private static final HistoryList HISTORY_LIST = new HistoryList();
  private static final WorkSheetList WORKSHEET_LIST = new WorkSheetList();
  private static final int DISTINCT_NAME_LENGTH = 150;
  private final Set<String> distinctNames = new HashSet<>(); // Liste des noms
  private ListeBouteille listMyCellarObject = new ListeBouteille();
  private boolean worksheetModified = false;
  private boolean historyModified = false;

  private SerializedStorage() {
  }

  public static SerializedStorage getInstance() {
    return SerializedStorageHolder.INSTANCE;
  }

  private static void Debug(String sText) {
    Program.Debug("SerializedStorage: " + sText);
  }

  @Override
  public void addBouteilles(ListeBouteille listBouteille) {
    listMyCellarObject.getBouteille().addAll(listBouteille.getBouteille());
    for (var bottle : listMyCellarObject.getBouteille()) {
      final List<History> historyList = HISTORY_LIST.getHistory()
          .stream()
          .filter(hasSameHistoryId(bottle))
          .toList();
      // TODO REMOVE
      if (!Program.getCaveConfigBool(CONVERTED_TO_UUID, false)) {
        for (var history : historyList) {
          history.getBouteille().setUuid(bottle.getUuid());
        }
      }
      distinctNames.add(bottle.getNom());
      updateBottleCountryUUID(bottle);
    }
  }

  @Deprecated(since = "82")
  private static void updateBottleCountryUUID(Bouteille bottle) {
    // TODO REMOVE WHEN COMPLETELY ASSIGNED
    if (bottle.getVignoble() != null &&
        bottle.getVignoble().getCountryUuid() == null) {
      if (isDefined(bottle.getVignoble().getCountry())) {
        CountryVignobleController.TempCountry tempCountry = getUUIDFromCountry(bottle.getVignoble().getCountry());
        bottle.getVignoble().setCountryUuid(tempCountry.uuid());
        if (!bottle.getVignoble().getCountry().equalsIgnoreCase(tempCountry.countryId())) {
          bottle.getVignoble().setCountry(tempCountry.countryId());
        }
      } else {
        bottle.getVignoble().setCountryUuid(NO_COUNTRY.getUuid());
      }
    }
  }

  @Override
  public ListeBouteille getListMyCellarObject() {
    return listMyCellarObject;
  }

  @Override
  public void setListMyCellarObject(ListeBouteille listMyCellarObject) {
    this.listMyCellarObject = listMyCellarObject;
    distinctNames.clear();
    if (this.listMyCellarObject.bouteille == null) {
      this.listMyCellarObject.bouteille = new LinkedList<>();
    }
    this.listMyCellarObject.bouteille.forEach(bouteille -> distinctNames.add(bouteille.getNom()));
  }

  @Override
  public List<String> getDistinctNames() {
    return distinctNames
        .stream()
        .map(value -> value.length() > DISTINCT_NAME_LENGTH ? value.substring(0, DISTINCT_NAME_LENGTH) : value)
        .collect(toList());
  }

  @Override
  public void updateDistinctNames() {
    distinctNames.clear();
    getAllList().forEach(myCellarObject -> distinctNames.add(myCellarObject.getNom()));
  }

  @Override
  public void addHistory(HistoryState type, Bouteille bottle) {
    historyModified = true;
    Program.setModified();
    HISTORY_LIST.add(new History(bottle, type.getIndex(), listMyCellarObject.getItemsCount()));
  }

  @Override
  public void addToWorksheet(Bouteille bottle) {
    worksheetModified = true;
    Program.setModified();
    WORKSHEET_LIST.add(new WorkSheetData(bottle));
  }

  @Override
  public void removeFromWorksheet(Bouteille bottle) {
    worksheetModified = true;
    Program.setModified();
    final List<WorkSheetData> collect = WORKSHEET_LIST.getWorsheet()
        .stream()
        .filter(workSheetData -> workSheetData.getBouteilleId() == bottle.getId() ||
            workSheetData.getUuid() == bottle.getUuid())
        .toList();
    WORKSHEET_LIST.getWorsheet().removeAll(collect);
  }

  @Override
  public void clearWorksheet() {
    worksheetModified = true;
    Program.setModified();
    WORKSHEET_LIST.getWorsheet().clear();
  }

  @Override
  public void clearHistory(HistoryState historyState) {
    Debug("Program: Clearing history: " + historyState);
    String sValue = switch (historyState) {
      case ALL -> getError(ERROR_CONFIRMDELETIONALLHISTORY);
      case ADD -> getError(ERROR_QUESTIONDELETEENTEREDHISTORY);
      case MODIFY -> getError(ERROR_QUESTIONDELETEMODIFIEDHISTORY);
      case DEL -> getError(ERROR_QUESTIONDELETEEXITEDHISTORY);
      case VALIDATED -> getError(ERROR_QUESTIONDELETEVALIDATEDHISTORY);
      case TOCHECK -> getError(ERROR_QUESTIONDELETECHECKEDHISTORY);
    };

    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, sValue, getLabel(MAIN_ASKCONFIRMATION), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
      return;
    }

    Program.setModified();
    historyModified = true;
    if (historyState == HistoryState.ALL) {
      HISTORY_LIST.clear();
      return;
    }
    HISTORY_LIST.getHistory()
        .stream()
        .filter(history -> history.getType() == historyState.getIndex())
        .forEach(this::removeHistory);
  }

  @Override
  public void removeHistory(History oB) {
    historyModified = true;
    HISTORY_LIST.remove(oB);
  }

  @Override
  public boolean deleteWine(Bouteille bottle) throws MyCellarException {

    final String nom = bottle.getNom();
    final String annee = bottle.getAnnee();
    final String emplacement = bottle.getEmplacement();
    final int numLieu = bottle.getNumLieu();
    final int ligne = bottle.getLigne();
    final int colonne = bottle.getColonne();

    Debug("DeleteWine: Trying deleting bottle " + nom.strip() + " " + annee + " " + emplacement.strip() + " " + numLieu + " " + ligne + " " + colonne);
    boolean found = listMyCellarObject.remove(bottle);
    if (found) {
      Debug("DeleteWine: Deleted by equals. " + bottle);
    } else {
      final List<Bouteille> foundList = getAllList().stream().filter(hasSameId(bottle)).toList();
      if (foundList.isEmpty()) {
        return false;
      }
      if (foundList.size() == 1) {
        Debug("DeleteWine: Deleted by Id. " + bottle);
        found = listMyCellarObject.remove(foundList.getFirst());
      } else {
        final List<Bouteille> resultBottles = getAllList().stream()
            .filter(
                bouteille -> emplacement.equals(bouteille.getEmplacement())
                    && nom.equals(bouteille.getNom())
                    && numLieu == bouteille.getNumLieu()
                    && (bottle.getAbstractPlace().isSimplePlace() ? annee.equals(bouteille.getAnnee()) : (ligne == bouteille.getLigne() && colonne == bouteille.getColonne()))).toList();
        if (resultBottles.isEmpty()) {
          Debug("ERROR: DeleteWine: Unable to find the object!");
          throw new MyCellarException("Unable to delete object: " + bottle);
        } else {
          found = listMyCellarObject.remove(resultBottles.getFirst());
        }
      }
    }

    if (found) {
      Program.setModified();
    }
    return found;
  }

  @Override
  public boolean addWine(Bouteille bottle) {
    if (null == bottle) {
      return false;
    }

    Debug(String.format("AddWine: Adding bottle '%s - %s' in '%s part %s line %s column %s'", bottle.getNom(), bottle.getAnnee(),
        bottle.getEmplacement(), bottle.getNumLieu(), bottle.getLigne(), bottle.getColonne()));
    bottle.setModified();
    Program.setModified();

    distinctNames.add(bottle.getNom());
    CountryVignobleController.findOrAddVignobleFromBottle(bottle);
    return listMyCellarObject.add(bottle);
  }

  @Override
  public List<Bouteille> getAllList() {
    return listMyCellarObject.getBouteille();
  }

  @Override
  public boolean add(Bouteille myCellarObject) {
    return listMyCellarObject.getBouteille().add(myCellarObject);
  }

  @Override
  public int getBottlesCount() {
    return listMyCellarObject.getBouteille().size();
  }

  @Override
  public void saveHistory() {
    if (historyModified) {
      historyModified = false;
      Debug("Saving History...");
      HistoryList.writeXML(new File(Program.getWorkDir(true) + HISTORY_XML));
      Debug("Saving History OK");
    }
  }

  @Override
  public void loadHistory() {
    historyModified = false;
    Debug("Loading History...");
    boolean resul = HistoryList.loadXML(new File(Program.getWorkDir(true) + HISTORY_XML));
    if (!resul) {
      HISTORY_LIST.clear();
      Debug("Loading History KO");
    } else {
      Debug("Loading History OK");
    }
  }

  @Override
  public HistoryList getHistoryList() {
    return HISTORY_LIST;
  }

  @Override
  public void saveWorksheet() {
    if (worksheetModified) {
      worksheetModified = false;
      Debug("Saving Worksheet...");
      WorkSheetList.writeXML(new File(Program.getWorkDir(true) + WORKSHEET_XML));
      Debug("Saving Worksheet OK");
    }
  }

  @Override
  public void loadWorksheet() {
    worksheetModified = false;
    Debug("Loading Worksheet...");
    boolean resul = WorkSheetList.loadXML(new File(Program.getWorkDir(true) + WORKSHEET_XML));
    if (!resul) {
      WORKSHEET_LIST.clear();
      Debug("Loading Worksheet KO");
    } else {
      Debug("Loading Worksheet OK");
    }
  }

  @Override
  public WorkSheetList getWorksheetList() {
    return WORKSHEET_LIST;
  }

  @Override
  public void close() {
    if (listMyCellarObject != null) {
      listMyCellarObject.resetBouteille();
    }
    distinctNames.clear();
  }

  private static class SerializedStorageHolder {
    private static final SerializedStorage INSTANCE = new SerializedStorage();
  }
}
