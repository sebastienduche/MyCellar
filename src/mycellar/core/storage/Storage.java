package mycellar.core.storage;

import mycellar.Bouteille;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetList;
import mycellar.core.exceptions.MyCellarException;

import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.8
 * @since 03/10/25
 */

public interface Storage {

  void addBouteilles(ListeBouteille listBouteilles);

  ListeBouteille getListMyCellarObject();

  void setListMyCellarObject(ListeBouteille listMyCellarObject);

  List<String> getDistinctNames();

  void updateDistinctNames();

  void addHistory(HistoryState historyState, Bouteille bottle);

  void addToWorksheet(Bouteille bottle);

  void removeHistory(History oB);

  void removeFromWorksheet(Bouteille bottle);

  void clearHistory(HistoryState historyState);

  void clearWorksheet();

  void saveHistory();

  void loadHistory();

  void saveWorksheet();

  void loadWorksheet();

  HistoryList getHistoryList();

  WorkSheetList getWorksheetList();

  boolean deleteWine(Bouteille bottle) throws MyCellarException;

  boolean addWine(Bouteille bottle);

  int getBottlesCount();

  List<Bouteille> getAllList();

  boolean add(Bouteille bottle);

  void close();

}
