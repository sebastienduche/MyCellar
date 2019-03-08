package mycellar.vignobles;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.Vignoble;
import mycellar.countries.Countries;
import mycellar.countries.Country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.7
 * @since 08/03/19
 */

public final class CountryVignobles {

	private final Map<Country, Vignobles> map = new HashMap<>();
	private final Map<CountryVignoble, Vignoble> mapVignobles = new HashMap<>();
	private final List<Vignoble> usedVignoblesList = new LinkedList<>();
	private final List<String> usedAppellationsList = new LinkedList<>();
	private static final CountryVignobles INSTANCE = new CountryVignobles();
	
	private CountryVignobles() {
		map.put(Countries.find("FRA"), Vignobles.loadFrance());
		map.put(Countries.find("ITA"), Vignobles.loadItalie());
	}
	
	public static void init() {
		INSTANCE.map.clear();
		INSTANCE.map.put(Countries.find("FRA"), Vignobles.loadFrance());
		INSTANCE.map.put(Countries.find("ITA"), Vignobles.loadItalie());
	}
	
	public static void close() {
		INSTANCE.map.clear();
	}
	
	public static void load() {
		Vignobles.loadAllCountries(INSTANCE.map);
	}
	
	public static Vignobles getVignobles(Country country) {
		return INSTANCE.map.get(country);
	}
	
	public static void createCountry(Country country) {
		Debug("Creating country... "+country.getName());
		if(country.getId() == null) {
			generateCountryId(country);
		}
		if(getVignobles(country) != null) {
			Debug("ERROR: the country already exist: "+country.getName());
			return;
		}
		Vignobles vignobles = new Vignobles();
		vignobles.setVignoble(new ArrayList<>());
		INSTANCE.map.put(country, vignobles);
		Debug("Creating country End");
	}
	
	public static void deleteCountry(Country country) {
		Debug("Deleting country... "+country.getName());
		INSTANCE.map.remove(country);
		boolean resul = Vignobles.delete(country);
		Debug("Deleting country End with resul="+resul);
	}
	
	private static void generateCountryId(Country country) {
		String id = Program.removeAccents(country.getName());
		id = id.toUpperCase();
		if(id.length() >= 3) {
			id = id.substring(0, 3);
		} else {
			id += "000";
			id = id.substring(0, 3);
		}
		boolean found;
		int i = 1;
		do {
			found = false;
    		for(Country c: INSTANCE.map.keySet()) {
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
		INSTANCE.usedVignoblesList.clear();
		LinkedList<Bouteille> list = Program.getStorage().getAllList();
		for (Bouteille b : list) {
			Vignoble vignoble = b.getVignoble();
			if(vignoble != null && !INSTANCE.usedVignoblesList.contains(vignoble)) {
				INSTANCE.usedVignoblesList.add(vignoble);
			}
			createVignobleInMap(vignoble);
		}
		for(Vignoble v : INSTANCE.usedVignoblesList) {
			addVignoble(v);	
		}
		Debug("addVignobleFromBottles... End");
	}
	
	static void createVignobleInMap(final Vignoble vignoble) {
		if(vignoble == null) {
			return;
		}
		if(vignoble.getCountry().isEmpty()) {
			return;
		}
		Country c = Countries.find(vignoble.getCountry());
		if(c == null) {
			return;
		}
		Vignobles country = getVignobles(c);
		if(country == null) {
			createCountry(c);
			country = getVignobles(c);
		}
		CountryVignoble vigne = country.findVignobleWithAppelation(vignoble);
		boolean found = true;
		if(vigne == null) {
			vigne = country.findVignoble(vignoble);
			found = false;
			if(vigne == null) {
				country.addVignoble(vignoble);
			}
			vigne = country.findVignoble(vignoble);
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
				vigne.add(appelation);
				vigne = country.findVignobleWithAppelation(vignoble);
			}
		}
		if(vigne == null && !appelation.isEmpty()) {
			Debug("ERROR: Unable to find created vignoble "+vignoble);
			return;
		}
		
		final Appelation appellation = country.findAppelation(vignoble);
		String val = vignoble.toString();
		if(appellation != null && !appellation.isEmpty() && !INSTANCE.usedAppellationsList.contains(val)) {
			INSTANCE.usedAppellationsList.add(val);
		}

		if (!vigne.isEmpty()) {
			INSTANCE.mapVignobles.put(vigne, vignoble);
		}
	}

	public static boolean isVignobleUsed(Country country, CountryVignoble vignoble) {
		Vignoble vigne = INSTANCE.mapVignobles.get(vignoble);
		if(vigne == null || !vigne.getCountry().equalsIgnoreCase(country.getId())) {
			return false;
		}
		return INSTANCE.usedVignoblesList.contains(vigne);
	}
	
	public static boolean isAppellationUsed(Country country, CountryVignoble vignoble, Appelation appellation) {
		var aop = appellation.getAOP() != null ? appellation.getAOP() : appellation.getAOC();
		Vignoble vignoble1 = new Vignoble(country.getId(), vignoble.getName(), appellation.getAOC(), appellation.getIGP(), aop);
		return INSTANCE.usedAppellationsList.contains(vignoble1.toString());
	}
	
	public static void renameVignoble(final CountryVignoble vignoble, final String name) {
		Vignoble vigne = INSTANCE.mapVignobles.get(vignoble);
		vignoble.setName(name);
		if(vigne == null) {
			Debug("ERROR: Unable to rename vignoble: "+vignoble.getName());
			return;
		}
		if(INSTANCE.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				Vignoble v = b.getVignoble();
				if(v != null && v.getName().equals(vigne.getName())) {
					v.setName(name);
				}
			}
		}
		vigne.setName(name);
		// Reload
		addVignobleFromBottles();
	}
	
	public static void renameAOC(final CountryVignoble vignoble, final Appelation appelation, final String name) {
		Vignoble vigne = INSTANCE.mapVignobles.get(vignoble);
		if(vigne == null) {
			Debug("ERROR: Unable to rename AOC: "+vignoble.getName());
			appelation.setAOC(name);
			return;
		}
		if(INSTANCE.usedVignoblesList.contains(vigne)) {
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
		vigne.setAOC(name);
		appelation.setAOC(name);
		// Reload
		addVignobleFromBottles();
	}
	
	public static void renameIGP(final CountryVignoble vignoble, final Appelation appelation, final String name) {
		Vignoble vigne = INSTANCE.mapVignobles.get(vignoble);
		if(vigne == null) {
			appelation.setIGP(name);
			Debug("ERROR: Unable to rename IGP: "+vignoble.getName());
			return;
		}
		if(INSTANCE.usedVignoblesList.contains(vigne)) {
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
		// Reload
		addVignobleFromBottles();
	}

	/**
	 * @param v
	 * @return
	 */
	private static void addVignoble(final Vignoble v) {
		if(v == null || v.getCountry() == null || v.getCountry().isEmpty()) {
			return;
		}
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
					vignoble.add(appelation);
				}
				else if(vignoble == null) {
					getVignobles(c).addVignoble(v);
				}
			} else {
				Appelation ap = getVignobles(c).findAppelation(v);
				v.setValues(ap);
				INSTANCE.mapVignobles.put(cv, v);
			}
		}	else {
			c = new Country(v.getCountry());
			generateCountryId(c);
			Vignobles vignobles = new Vignobles();
			vignobles.setVignoble(new ArrayList<>());
			vignobles.addVignoble(v);
			Countries.add(c);
			INSTANCE.map.put(c, vignobles);
		}
		if(!INSTANCE.usedVignoblesList.contains(v)) {
			INSTANCE.usedVignoblesList.add(v);
		}
		String val = v.toString();
		if(!INSTANCE.usedAppellationsList.contains(val)) {
			INSTANCE.usedAppellationsList.add(val);
		}
	}
	
	public static void addVignobleFromBottle(final Bouteille wine) {
		Debug("addVignobleFromBottle...");
		addVignoble(wine.getVignoble());
		Debug("addVignobleFromBottle... End");	
	}
	
	public static void save() {
		Debug("Saving...");
		for( Country c : INSTANCE.map.keySet()){
			Vignobles.save(c, INSTANCE.map.get(c));
		}
		Debug("Saved");
	}
	
	public static boolean hasCountryByName(final Country country) {
		for( Country c : INSTANCE.map.keySet()){
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
	private static void Debug(String sText) {
		Program.Debug("CountryVignobles: " + sText );
	}
}
