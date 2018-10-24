package mycellar;

import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.5
 * @since 04/07/18
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
	void replaceWineAll(Bouteille wine, int num_empl, int line, int column);
	int getAllNblign();

	LinkedList<Bouteille> getAllList();
	void close();
}
