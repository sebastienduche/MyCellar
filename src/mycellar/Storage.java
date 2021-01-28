package mycellar;

import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetList;

import java.util.LinkedList;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.1
 * @since 28/01/21
 */

public interface Storage {
	
	void setListBouteilles(ListeBouteille listBouteilles);
	void addBouteilles(ListeBouteille listBouteilles);
	ListeBouteille getListBouteilles();
	LinkedList<String> getBottleNames();
	void addHistory(HistoryState historyState, Bouteille oB);
	void addToWorksheet(Bouteille oB);

	void removeHistory(History oB);
	void removeFromWorksheet(Bouteille oB);
	void clearHistory(HistoryState historyState);
	void clearWorksheet();
	void saveHistory();
	void loadHistory();
	void saveWorksheet();
	void loadWorksheet();
	HistoryList getHistoryList();
	WorkSheetList getWorksheetList();
	boolean deleteWine(Bouteille oB);

	boolean addWine(Bouteille oB);
	int getBottlesCount();

	LinkedList<Bouteille> getAllList();
	void close();
}
