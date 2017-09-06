package mycellar.vignobles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.Vignoble;
import mycellar.countries.Countries;
import mycellar.countries.Country;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.3
 * @since 06/09/17
 */

public final class CountryVignobles {

	private final HashMap<Country, Vignobles> map = new HashMap<Country, Vignobles>();
	private final HashMap<CountryVignoble, Vignoble> mapVignobles = new HashMap<CountryVignoble, Vignoble>();
	private final LinkedList<Vignoble> usedVignoblesList = new LinkedList<Vignoble>();
	private final LinkedList<String> usedAppellationsList = new LinkedList<String>();
	private static final CountryVignobles instance = new CountryVignobles();
	
	private CountryVignobles() {
		map.put(Countries.find("FRA"), Vignobles.loadFrance());
		map.put(Countries.find("ITA"), Vignobles.loadItalie());
	}
	
	public static void init() {
		instance.map.clear();
		instance.map.put(Countries.find("FRA"), Vignobles.loadFrance());
		instance.map.put(Countries.find("ITA"), Vignobles.loadItalie());
	}
	
	public static void load() {
		Vignobles.loadAllCountries(instance.map);
	}
	
	public static Vignobles getVignobles(Country country) {
		return instance.map.get(country);
	}
	
	public static boolean createCountry(Country country) {
		Debug("Creating country... "+country.getName());
		if(country.getId() == null)
			generateCountryId(country);
		if(getVignobles(country) != null)
			return false;
		Vignobles vignobles = new Vignobles();
		vignobles.setVignoble(new ArrayList<CountryVignoble>());
		instance.map.put(country, vignobles);
		Debug("Creating country End");
		return true;
	}
	
	public static boolean deleteCountry(Country country) {
		Debug("Deleting country... "+country.getName());
		instance.map.remove(country);
		boolean resul = Vignobles.delete(country);
		Debug("Deleting country End");
		return resul;
	}
	
	private static void generateCountryId(Country country) {
		String id = Program.removeAccents(country.getName());
		id = id.toUpperCase();
		if(id.length() >= 3)
			id = id.substring(0, 3);
		else {
			id += "000";
			id = id.substring(0, 3);
		}
		boolean found = false;
		int i = 1;
		do {
			found = false;
    		for(Country c: instance.map.keySet()) {
    			if(c.getId().equalsIgnoreCase(id)) {
    				id = id.substring(0, 3) + i;
    				i++;
    				found = true;
    			}
    		}
		}while(found);
		country.setId(id);
	}

	public static void addVignobleFromBottles() {
		Debug("addVignobleFromBottles...");
		instance.usedVignoblesList.clear();
		LinkedList<Bouteille> list = Program.getStorage().getAllList();
		for (mycellar.Bouteille b : list) {
			Vignoble vignoble = b.getVignoble();
			if(vignoble != null && !instance.usedVignoblesList.contains(vignoble)) {
				instance.usedVignoblesList.add(vignoble);
			}
			createVignobleInMap(vignoble);
		}
		for(Vignoble v : instance.usedVignoblesList) {
			addVignoble(v);	
		}
		Debug("addVignobleFromBottles... End");
	}
	
	static void createVignobleInMap(Vignoble vignoble) {
		if(vignoble == null)
			return;
		if(vignoble.getCountry().isEmpty())
			return;
		Country c = Countries.find(vignoble.getCountry());
		if(c == null)
			return;
		if(getVignobles(c) == null) {
			createCountry(c);
		}
		CountryVignoble vigne = getVignobles(c).findVignobleWithAppelation(vignoble);
		boolean found = true;
		if(vigne == null) {
			vigne = getVignobles(c).findVignoble(vignoble);
			found = false;
			if(vigne == null)
				getVignobles(c).addVignoble(vignoble);
			vigne = getVignobles(c).findVignoble(vignoble);
		}
		if(vigne == null) {
			Debug("ERROR: Unable to find vignoble "+vignoble);
			return;
		}
		Appelation appelation = new Appelation();
		if(!found) {
			appelation.setAOC(vignoble.getAOC());
			appelation.setAOP(vignoble.getAOP());
			appelation.setIGP(vignoble.getIGP());
			if(!appelation.isEmpty()) {
				vigne.getAppelation().add(appelation);
				vigne = getVignobles(c).findVignobleWithAppelation(vignoble);
			}
		}
		if(vigne == null && !appelation.isEmpty()) {
			Debug("ERROR: Unable to find created vignoble "+vignoble);
			return;
		}
		
		Appelation appellation = getVignobles(c).findAppelation(vignoble);
		String val = vignoble.toString();
		if(appellation != null && !appellation.isEmpty() && !instance.usedAppellationsList.contains(val)) {
			instance.usedAppellationsList.add(val);
		}

		instance.mapVignobles.put(vigne, vignoble);
	}

	public static boolean isVignobleUsed(CountryVignoble vignoble) {
		Vignoble vigne = instance.mapVignobles.get(vignoble);
		if(vigne == null) {
			return false;
		}
		return instance.usedVignoblesList.contains(vigne);
	}
	
	public static boolean isAppellationUsed(Country country, CountryVignoble vignoble, Appelation appellation) {
		String val = country.getId() + "-" + vignoble.toString() + "-"+ appellation.getKeyString();
		return instance.usedAppellationsList.contains(val);
	}
	
	public static void renameVignoble(CountryVignoble vignoble, String name) {
		Vignoble vigne = instance.mapVignobles.get(vignoble);
		vignoble.setName(name);
		if(vigne == null) {
			return;
		}
		if(instance.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				Vignoble v = b.getVignoble();
				if(v != null && v.equals(vigne)) {
					v.setName(name);
				}
			}
		}
		vigne.setName(name);
	}
	
	public static void renameAOC(CountryVignoble vignoble, Appelation appelation, String name) {
		Vignoble vigne = instance.mapVignobles.get(vignoble);
		if(vigne == null) {
			appelation.setAOC(name);
			return;
		}
		if(instance.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				Vignoble v = b.getVignoble();
				if(v != null && v.equals(vigne)) {
					if(v.getAOC() != null && v.getAOC().equals(appelation.getAOC())) {
						v.setAOC(name);
					}
				}
			}
		}
		if(vigne != null)
			vigne.setAOC(name);
		appelation.setAOC(name);
	}
	
	public static void renameIGP(CountryVignoble vignoble, Appelation appelation, String name) {
		Vignoble vigne = instance.mapVignobles.get(vignoble);
		if(vigne == null) {
			appelation.setIGP(name);
			return;
		}
		if(instance.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				Vignoble v = b.getVignoble();
				if(v != null && v.equals(vigne)) {
					if(v.getIGP() != null && v.getIGP().equals(appelation.getIGP())) {
						v.setIGP(name);
					}
				}
			}
		}
		vigne.setIGP(name);
		appelation.setIGP(name);
	}

	/**
	 * @param v
	 * @return
	 */
	private static void addVignoble(Vignoble v) {
		if(v == null || v.getCountry() == null || v.getCountry().isEmpty())
			return;
		Country c = Countries.findByIdOrLabel(v.getCountry());
		if(c != null) {
			if(getVignobles(c) == null) {
				createCountry(c);
			}
			CountryVignoble cv = getVignobles(c).findVignobleWithAppelation(v);
			if(cv == null) {
				CountryVignoble vignoble = getVignobles(c).findVignoble(v);
				if(vignoble != null && !v.isAppellationEmpty()) {
					Appelation appelation = new Appelation();
					appelation.setAOC(v.getAOC());
					appelation.setAOP(v.getAOP());
					appelation.setIGP(v.getIGP());
					vignoble.getAppelation().add(appelation);
				}
				else if(vignoble == null)
					getVignobles(c).addVignoble(v);
			}
			else {
				Appelation ap = getVignobles(c).findAppelation(v);
				v.setValues(ap);
				instance.mapVignobles.put(cv, v);
			}
		}
		else {
			c = new Country(v.getCountry());
			generateCountryId(c);
			Vignobles vignobles = new Vignobles();
			vignobles.setVignoble(new ArrayList<CountryVignoble>());
			vignobles.addVignoble(v);
			Countries.add(c);
			instance.map.put(c, vignobles);
		}
		if(!instance.usedVignoblesList.contains(v))
			instance.usedVignoblesList.add(v);
		String val = v.toString();
		if(!instance.usedAppellationsList.contains(val))
			instance.usedAppellationsList.add(val);
	}
	
	public static void addVignobleFromBottle(Bouteille wine) {
		Debug("addVignobleFromBottle...");
		addVignoble(wine.getVignoble());
		Debug("addVignobleFromBottle... End");	
	}
	
	public static void save() {
		Debug("Saving...");
		for( Country c : instance.map.keySet()){
			Vignobles.save(c, instance.map.get(c));
		}
		Debug("Saved");
	}
	
	public static boolean hasCountryByName(Country country) {
		for( Country c : instance.map.keySet()){
			if(c.getName().equalsIgnoreCase(country.getName()))
				return true;
		}
		return false;
	}
	
	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("CountryVignobles: " + sText );
	}
}
