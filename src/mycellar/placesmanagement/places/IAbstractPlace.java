package mycellar.placesmanagement.places;

import java.util.Optional;

import mycellar.core.MyCellarObject;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.Place;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2022
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 27/05/22
 */
public interface IAbstractPlace {

	@Deprecated
    public boolean isSimplePlace();
	@Deprecated
	public int getStartSimplePlace();
	public String getName();
	public int getPartCount();
	public void clearStorage(MyCellarObject myCellarObject);
	public void clearStorage(MyCellarObject myCellarObject, Place place);
	public int getCountCellUsed(int part);
	public boolean addObject(MyCellarObject myCellarObject);
	public void removeObject(MyCellarObject myCellarObject) throws MyCellarException;
	public void updateToStock(MyCellarObject myCellarObject);
	public boolean canAddObjectAt(MyCellarObject b);
	public boolean canAddObjectAt(Place place);
	public boolean canAddObjectAt(int tmpNumEmpl, int tmpLine, int tmpCol);
	public Optional<MyCellarObject> getObject(int num_empl, int line, int column);
	public Optional<MyCellarObject> getObject(Place place);
	public String toXml();
	public void resetStockage();
}
