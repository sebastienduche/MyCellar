package mycellar;

import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.7
 * @since 27/06/19
 */

public interface Storage {
	
	void setListBouteilles(ListeBouteille listBouteilles);
	void addBouteilles(ListeBouteille listBouteilles);
	ListeBouteille getListBouteilles();
	LinkedList<String> getBottleNames();
	boolean addHistory(int nType, Bouteille oB);

	void removeHistory(History oB);
	boolean clearHistory(int _nValue);
	boolean saveHistory();
	boolean loadHistory();
	HistoryList getHistoryList();
	void setHistoryList(HistoryList list);
	boolean deleteWine(Bouteille oB);

	boolean addWine(Bouteille oB);
	int getBottlesCount();

	LinkedList<Bouteille> getAllList();
	void close();
}
