package mycellar;

import mycellar.core.IMyCellarObject;
import mycellar.core.datas.history.History;
import mycellar.core.datas.history.HistoryList;
import mycellar.core.datas.history.HistoryState;
import mycellar.core.datas.worksheet.WorkSheetList;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.2
 * @since 09/04/21
 */

public interface Storage {
	
	void setListBouteilles(ListeBouteille listBouteilles);
	void addBouteilles(ListeBouteille listBouteilles);
	ListeBouteille getListBouteilles();
	LinkedList<String> getBottleNames();
	void addHistory(HistoryState historyState, IMyCellarObject myCellarObject);
	void addToWorksheet(IMyCellarObject myCellarObject);

	void removeHistory(History oB);
	void removeFromWorksheet(IMyCellarObject myCellarObject);
	void clearHistory(HistoryState historyState);
	void clearWorksheet();
	void saveHistory();
	void loadHistory();
	void saveWorksheet();
	void loadWorksheet();
	HistoryList getHistoryList();
	WorkSheetList getWorksheetList();
	boolean deleteWine(IMyCellarObject myCellarObject);

	boolean addWine(IMyCellarObject myCellarObject);
	int getBottlesCount();

	List<? extends IMyCellarObject> getAllList();
	boolean add(IMyCellarObject myCellarObject);
	void close();
}
