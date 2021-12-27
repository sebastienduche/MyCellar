package mycellar.core.storage;

import mycellar.core.exceptions.MyCellarException;
import mycellar.core.MyCellarObject;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetList;

import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.5
 * @since 24/08/21
 */

public interface Storage {

  void addBouteilles(ListeBouteille listBouteilles);

  ListeBouteille getListMyCellarObject();

  void setListMyCellarObject(ListeBouteille listMyCellarObject);

  List<String> getDistinctNames();

  void addHistory(HistoryState historyState, MyCellarObject myCellarObject);

  void addToWorksheet(MyCellarObject myCellarObject);

  void removeHistory(History oB);

  void removeFromWorksheet(MyCellarObject myCellarObject);

  void clearHistory(HistoryState historyState);

  void clearWorksheet();

  void saveHistory();

  void loadHistory();

  void saveWorksheet();

  void loadWorksheet();

  HistoryList getHistoryList();

  WorkSheetList getWorksheetList();

  boolean deleteWine(MyCellarObject myCellarObject) throws MyCellarException;

  boolean addWine(MyCellarObject myCellarObject);

  int getBottlesCount();

  List<? extends MyCellarObject> getAllList();

  boolean add(MyCellarObject myCellarObject);

  void close();

  List<String> getDistinctComposers();

  List<String> getDistinctArtists();
}
