package mycellar.core.storage;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.Program;
import mycellar.core.MyCellarObject;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetData;
import mycellar.core.datas.worksheet.WorkSheetList;
import mycellar.core.exceptions.MyCellarException;
import mycellar.core.text.LabelProperty;
import mycellar.vignobles.CountryVignobleController;

import javax.swing.JOptionPane;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static mycellar.ProgramConstants.HISTORY_XML;
import static mycellar.ProgramConstants.WORKSHEET_XML;
import static mycellar.core.text.MyCellarLabelManagement.getError;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 7.9
 * @since 26/12/23
 */

public class SerializedStorage implements Storage {

  private static final HistoryList HISTORY_LIST = new HistoryList();
  private static final WorkSheetList WORKSHEET_LIST = new WorkSheetList();
  private static final int DISTINCT_NAME_LENGTH = 100;
  private static final int DISTINCT_COMPOSER_ARTIST_LENGTH = 75;
  private final List<String> distinctNames = new LinkedList<>(); // Liste des noms
  private final List<String> distinctComposers = new LinkedList<>(); // Liste des composers
  private final List<String> distinctArtists = new LinkedList<>(); // Liste des artists
  private ListeBouteille listMyCellarObject = new ListeBouteille();
  private int itemsCount;
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

  public int getItemsCount() {
    return itemsCount;
  }

  @Override
  public void addBouteilles(ListeBouteille listBouteille) {
    if (Program.isWineType()) {
      listMyCellarObject.getBouteille().addAll(listBouteille.getBouteille());
      for (MyCellarObject myCellarObject : listMyCellarObject.bouteille) {
        final List<History> theBottle = HISTORY_LIST.getHistory().stream().filter(history -> history.getBouteille().getId() == myCellarObject.getId()).toList();
        if (myCellarObject.updateID() && !theBottle.isEmpty()) {
          theBottle.getFirst().getBouteille().setId(myCellarObject.getId());
        }
        if (!distinctNames.contains(myCellarObject.getNom())) {
          distinctNames.add(myCellarObject.getNom());
        }
      }
    } else if (Program.isMusicType()) {
      listMyCellarObject.getMusic().addAll(listBouteille.getMusic());
      for (Music myCellarObject : listMyCellarObject.music) {
        final List<History> theMusic = HISTORY_LIST.getHistory().stream().filter(history -> history.getMusic().getId() == myCellarObject.getId()).toList();
        if (myCellarObject.updateID() && !theMusic.isEmpty()) {
          theMusic.getFirst().getMusic().setId(myCellarObject.getId());
        }
        if (!distinctNames.contains(myCellarObject.getNom())) {
          distinctNames.add(myCellarObject.getNom());
        }
        if (!distinctComposers.contains(myCellarObject.getComposer())) {
          distinctComposers.add(myCellarObject.getComposer());
        }
        if (!distinctArtists.contains(myCellarObject.getArtist())) {
          distinctArtists.add(myCellarObject.getArtist());
        }
      }
    }
    itemsCount = listMyCellarObject.getItemsCount();
  }

  @Override
  public ListeBouteille getListMyCellarObject() {
    return listMyCellarObject;
  }

  @Override
  public void setListMyCellarObject(ListeBouteille listMyCellarObject) {
    this.listMyCellarObject = listMyCellarObject;
    distinctNames.clear();
    distinctComposers.clear();
    distinctArtists.clear();
    if (Program.isWineType()) {
      if (this.listMyCellarObject.bouteille == null) {
        this.listMyCellarObject.bouteille = new LinkedList<>();
      }
      for (MyCellarObject b : this.listMyCellarObject.bouteille) {
        if (!distinctNames.contains(b.getNom())) {
          distinctNames.add(b.getNom());
        }
      }
    } else if (Program.isMusicType()) {
      if (this.listMyCellarObject.music == null) {
        this.listMyCellarObject.music = new LinkedList<>();
      }
      for (Music b : this.listMyCellarObject.music) {
        if (!distinctNames.contains(b.getNom())) {
          distinctNames.add(b.getNom());
        }
        if (!distinctComposers.contains(b.getComposer())) {
          distinctComposers.add(b.getComposer());
        }
        if (!distinctArtists.contains(b.getArtist())) {
          distinctArtists.add(b.getArtist());
        }
      }
    } else {
      Program.throwNotImplementedForNewType();
    }
    itemsCount = listMyCellarObject.getItemsCount();
  }

  @Override
  public List<String> getDistinctNames() {
    return distinctNames
        .stream()
        .map(value -> value.length() > DISTINCT_NAME_LENGTH ? value.substring(0, DISTINCT_NAME_LENGTH) : value)
        .collect(toList());
  }

  @Override
  public List<String> getDistinctComposers() {
    return distinctComposers
        .stream()
        .map(value -> value.length() > DISTINCT_COMPOSER_ARTIST_LENGTH ? value.substring(0, DISTINCT_COMPOSER_ARTIST_LENGTH) : value)
        .collect(toList());
  }

  @Override
  public List<String> getDistinctArtists() {
    return distinctArtists
        .stream()
        .map(value -> value.length() > DISTINCT_COMPOSER_ARTIST_LENGTH ? value.substring(0, DISTINCT_COMPOSER_ARTIST_LENGTH) : value)
        .collect(toList());
  }


  @Override
  public void addHistory(HistoryState type, MyCellarObject myCellarObject) {
    historyModified = true;
    Program.setModified();
    HISTORY_LIST.add(new History(myCellarObject, type.getIndex(), getItemsCount()));
  }

  @Override
  public void addToWorksheet(MyCellarObject myCellarObject) {
    worksheetModified = true;
    Program.setModified();
    WORKSHEET_LIST.add(new WorkSheetData(myCellarObject));
  }

  @Override
  public void removeFromWorksheet(MyCellarObject myCellarObject) {
    worksheetModified = true;
    Program.setModified();
    final List<WorkSheetData> collect = WORKSHEET_LIST.getWorsheet()
        .stream()
        .filter(workSheetData -> workSheetData.getBouteilleId() == myCellarObject.getId())
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
      case ALL -> getError("Error.confirmDeletionAllHistory");
      case ADD -> getError("Error.questionDeleteEnteredHistory");
      case MODIFY -> getError("Error.questionDeleteModifiedHistory");
      case DEL -> getError("Error.questionDeleteExitedHistory");
      case VALIDATED -> getError("Error.questionDeleteValidatedHistory", LabelProperty.OF_THE_PLURAL);
      case TOCHECK -> getError("Error.questionDeleteCheckedHistory", LabelProperty.OF_THE_PLURAL);
    };

    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, sValue, getLabel("Main.AskConfirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
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
  public boolean deleteWine(MyCellarObject myCellarObject) throws MyCellarException {

    final String nom = myCellarObject.getNom();
    final String annee = myCellarObject.getAnnee();
    final String emplacement = myCellarObject.getEmplacement();
    final int numLieu = myCellarObject.getNumLieu();
    final int ligne = myCellarObject.getLigne();
    final int colonne = myCellarObject.getColonne();

    Debug("DeleteWine: Trying deleting myCellarObject " + nom.strip() + " " + annee + " " + emplacement.strip() + " " + numLieu + " " + ligne + " " + colonne);
    boolean found = listMyCellarObject.remove(myCellarObject);
    if (found) {
      Debug("DeleteWine: Deleted by equals. " + myCellarObject);
    } else {
      final List<MyCellarObject> collect = getAllList().stream().filter(bouteille -> bouteille.getId() == myCellarObject.getId()).collect(toList());
      if (collect.isEmpty()) {
        return false;
      }
      if (collect.size() == 1) {
        Debug("DeleteWine: Deleted by Id. " + myCellarObject);
        found = listMyCellarObject.remove(collect.getFirst());
      } else {
        final List<MyCellarObject> resultBouteilles = getAllList().stream()
            .filter(
                bouteille -> emplacement.equals(bouteille.getEmplacement())
                    && nom.equals(bouteille.getNom())
                    && numLieu == bouteille.getNumLieu()
                    && (myCellarObject.getAbstractPlace().isSimplePlace() ? annee.equals(bouteille.getAnnee()) : (ligne == bouteille.getLigne() && colonne == bouteille.getColonne()))).collect(toList());
        if (resultBouteilles.isEmpty()) {
          Debug("ERROR: DeleteWine: Unable to find the object!");
          throw new MyCellarException("Unable to delete object: " + myCellarObject);
        } else {
          found = listMyCellarObject.remove(resultBouteilles.getFirst());
        }
      }
    }

    if (found) {
      Program.setModified();
      itemsCount--;
    }
    return found;
  }

  @Override
  public boolean addWine(MyCellarObject myCellarObject) {
    if (null == myCellarObject) {
      return false;
    }

    Debug("AddWine: Adding bottle " + myCellarObject.getNom() + " " + myCellarObject.getAnnee() + " " + myCellarObject.getEmplacement() + " " + myCellarObject.getNumLieu() + " " + myCellarObject.getLigne() + " " + myCellarObject.getColonne());
    myCellarObject.setModified();
    Program.setModified();

    if (!distinctNames.contains(myCellarObject.getNom())) {
      distinctNames.add(myCellarObject.getNom());
    }
    if (myCellarObject instanceof Bouteille bouteille) {
      CountryVignobleController.addVignobleFromBottle(bouteille);
    }
    if (myCellarObject instanceof Music music) {
      if (!distinctComposers.contains(music.getComposer())) {
        distinctComposers.add(music.getComposer());
      }
      if (!distinctArtists.contains(music.getArtist())) {
        distinctArtists.add(music.getArtist());
      }
    }
    itemsCount++;
    return listMyCellarObject.add(myCellarObject);
  }

  @Override
  public List<? extends MyCellarObject> getAllList() {
    if (Program.isMusicType()) {
      return listMyCellarObject.getMusic();
    }
    if (Program.isWineType()) {
      return listMyCellarObject.getBouteille();
    }
    Program.throwNotImplementedForNewType();
    return Collections.emptyList();
  }

  @Override
  public boolean add(MyCellarObject myCellarObject) {
    if (myCellarObject instanceof Bouteille b) {
      return listMyCellarObject.getBouteille().add(b);
    } else if (myCellarObject instanceof Music m) {
      return listMyCellarObject.getMusic().add(m);
    } else {
      Program.throwNotImplementedForNewType();
    }
    return false;
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
      listMyCellarObject.resetMusic();
    }
    distinctNames.clear();
    distinctComposers.clear();
    distinctArtists.clear();
  }

  private static class SerializedStorageHolder {
    private static final SerializedStorage INSTANCE = new SerializedStorage();
  }
}
