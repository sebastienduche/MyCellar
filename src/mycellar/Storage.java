package mycellar;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.0
 * @since 17/01/16
 */

public interface Storage {
	
	public void initialize();
	public void setListBouteilles(ListeBouteille listBouteilles);
	public void setListBouteilles(LinkedList<Bouteille> listBouteilles);
	public void addBouteilles(ListeBouteille listBouteilles);
	public ListeBouteille getListBouteilles();
	public LinkedList<String> getBottleNames();
	
	public boolean addHistory(int nType, Bouteille oB);
	public void removeHistory(History oB);
	public boolean clearHistory(int _nValue);
	public void clearHistory();
	public boolean saveHistory();
	public boolean loadHistory();
	public HistoryList getHistory();
	
	public boolean deleteWine(Bouteille oB);
	public void addAnnee(int annee);
	public void removeAnnee(int annee);
	public boolean addWine(Bouteille oB);
	public void replaceWineAll(Bouteille wine, int num_empl, int line, int column);
	
	public int getAllNblign();
	public Bouteille getAllAt( int nIndex );
	public void setAll( Bouteille[] bList);
	public LinkedList<Bouteille> getAllList();
	public void setAllList(LinkedList<Bouteille> _all);
	
	public LinkedHashMap<Integer,Integer> getAnneeList();
	public void setAnnee( LinkedHashMap<Integer,Integer> l );
	public int getNbBouteilleAnnee(int an);
	public int getNbAutreAnnee();
	public int getNbNonVintage();
	
	public void close();
	
	public boolean readRangement( LinkedList<Rangement> cave );

}
