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
 * @version 1.8
 * @since 10/04/19
 */

public final class CountryVignobles {

	private final Map<Country, Vignobles> map = new HashMap<>();
	private final Map<String, Vignoble> mapVignobles = new HashMap<>();
	private final List<Vignoble> usedVignoblesList = new LinkedList<>();
	private final List<String> usedAppellationsList = new LinkedList<>();
	private static final CountryVignobles INSTANCE = new CountryVignobles();
	private static boolean rebuildNeeded = false;

	private CountryVignobles() {
		map.put(Countries.find("FRA"), Vignobles.loadFrance());
		map.put(Countries.find("ITA"), Vignobles.loadItalie());
		setRebuildNeeded();
	}
	
	public static void init() {
		INSTANCE.map.clear();
		INSTANCE.map.put(Countries.find("FRA"), Vignobles.loadFrance());
		INSTANCE.map.put(Countries.find("ITA"), Vignobles.loadItalie());
		setRebuildNeeded();
	}
	
	public static void close() {
		INSTANCE.map.clear();
		setRebuildNeeded();
	}
	
	public static void load() {
		Vignobles.loadAllCountries(INSTANCE.map);
		setRebuildNeeded();
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
		if (!rebuildNeeded) {
			return;
		}
		Debug("addVignobleFromBottles...");
		INSTANCE.usedVignoblesList.clear();
		INSTANCE.mapVignobles.clear();
		INSTANCE.usedAppellationsList.clear();
		for (Bouteille b : Program.getStorage().getAllList()) {
			Vignoble vignoble = b.getVignoble();
			if(vignoble != null && !INSTANCE.usedVignoblesList.contains(vignoble)) {
				INSTANCE.usedVignoblesList.add(vignoble);
			}
			createVignobleInMap(vignoble);
		}
		for (Vignoble v : INSTANCE.usedVignoblesList) {
			addVignoble(v);	
		}
		rebuildNeeded = false;
		Debug("addVignobleFromBottles... End");
	}
	
	static void createVignobleInMap(final Vignoble vignoble) {
		if(vignoble == null) {
			return;
		}
		if(vignoble.getCountry().isEmpty()) {
			return;
		}
		Country country = Countries.find(vignoble.getCountry());
		if(country == null) {
			return;
		}
		Vignobles vignobles = getVignobles(country);
		if(vignobles == null) {
			createCountry(country);
			vignobles = getVignobles(country);
		}
		CountryVignoble countryVignoble = vignobles.findVignobleWithAppelation(vignoble);
		boolean found = true;
		if(countryVignoble == null) {
			countryVignoble = vignobles.findVignoble(vignoble);
			found = false;
			if(countryVignoble == null) {
				vignobles.addVignoble(vignoble);
			}
			countryVignoble = vignobles.findVignoble(vignoble);
		}
		if(countryVignoble == null) {
			Debug("ERROR: Unable to find vignoble "+vignoble);
			return;
		}
		Appelation appelation = new Appelation();
		if(!found) {
			appelation.setAOC(vignoble.getAOC());
			appelation.setAOP(vignoble.getAOP());
			appelation.setIGP(vignoble.getIGP());
			if(!appelation.isEmpty()) {
				countryVignoble.add(appelation);
				countryVignoble = vignobles.findVignobleWithAppelation(vignoble);
			}
		}
		if(countryVignoble == null && !appelation.isEmpty()) {
			Debug("ERROR: Unable to find created vignoble "+vignoble);
			return;
		}
		
		final Appelation appellation = vignobles.findAppelation(vignoble);
		String val = vignoble.toString();
		if(appellation != null && !appellation.isEmpty() && !INSTANCE.usedAppellationsList.contains(val)) {
			INSTANCE.usedAppellationsList.add(val);
		}

		if (countryVignoble != null && !countryVignoble.isEmpty()) {
			INSTANCE.mapVignobles.put(new CountryVignobleID(country, countryVignoble).getId(), vignoble);
		}
	}

	public static boolean isVignobleUsed(Country country, CountryVignoble vignoble) {
		Vignoble vigne = INSTANCE.mapVignobles.get(new CountryVignobleID(country, vignoble).getId());
		if(vigne == null || !vigne.getCountry().equalsIgnoreCase(country.getId())) {
			return false;
		}
		return INSTANCE.usedVignoblesList.contains(vigne);
	}
	
	public static boolean isAppellationUsed(Country country, CountryVignoble vignoble, Appelation appellation) {
		var aop = appellation.getAOP() != null ? appellation.getAOP() : appellation.getAOC();
		var igp = appellation.getIGP() != null ? appellation.getIGP() : "";
		Vignoble vignoble1 = new Vignoble(country.getId(), vignoble.getName(), appellation.getAOC(), igp, aop);
		return INSTANCE.usedAppellationsList.contains(vignoble1.toString());
	}
	
	public static void renameVignoble(final Country country, final CountryVignoble vignoble, final String name) {
		Vignoble vigne = INSTANCE.mapVignobles.get(new CountryVignobleID(country, vignoble).getId());
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
		setRebuildNeeded();
		addVignobleFromBottles();
	}
	
	public static void renameAOC(final Country country, final CountryVignoble vignoble, final Appelation appelation, final String name) {
		Vignoble vigne = INSTANCE.mapVignobles.get(new CountryVignobleID(country, vignoble).getId());
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
		setRebuildNeeded();
		addVignobleFromBottles();
	}
	
	public static void renameIGP(final Country country, final CountryVignoble vignoble, final Appelation appelation, final String name) {
		Vignoble vigne = INSTANCE.mapVignobles.get(new CountryVignobleID(country, vignoble).getId());
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
		setRebuildNeeded();
		addVignobleFromBottles();
	}

	/**
	 * @param vignoble1
	 * @return
	 */
	private static void addVignoble(final Vignoble vignoble1) {
		if(vignoble1 == null || vignoble1.getCountry() == null || vignoble1.getCountry().isEmpty()) {
			return;
		}
		Country country = Countries.findByIdOrLabel(vignoble1.getCountry());
		if(country != null) {
			if(getVignobles(country) == null) {
				createCountry(country);
			}
			CountryVignoble countryVignoble = getVignobles(country).findVignobleWithAppelation(vignoble1);
			if(countryVignoble == null) {
				CountryVignoble vignoble = getVignobles(country).findVignoble(vignoble1);
				if(vignoble != null && !vignoble1.isAppellationEmpty()) {
					Appelation appelation = new Appelation();
					appelation.setAOC(vignoble1.getAOC());
					appelation.setAOP(vignoble1.getAOP());
					appelation.setIGP(vignoble1.getIGP());
					vignoble.add(appelation);
				}
				else if(vignoble == null) {
					getVignobles(country).addVignoble(vignoble1);
				}
			} else {
				Appelation ap = getVignobles(country).findAppelation(vignoble1);
				vignoble1.setValues(ap);
				INSTANCE.mapVignobles.put(new CountryVignobleID(country, countryVignoble).getId(), vignoble1);
			}
		}	else {
			country = new Country(vignoble1.getCountry());
			generateCountryId(country);
			Vignobles vignobles = new Vignobles();
			vignobles.setVignoble(new ArrayList<>());
			vignobles.addVignoble(vignoble1);
			Countries.add(country);
			INSTANCE.map.put(country, vignobles);
		}
		if(!INSTANCE.usedVignoblesList.contains(vignoble1)) {
			INSTANCE.usedVignoblesList.add(vignoble1);
		}
		String val = vignoble1.toString();
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
		for (Country c : INSTANCE.map.keySet()){
			Vignobles.save(c, INSTANCE.map.get(c));
		}
		Debug("Saved");
	}
	
	public static boolean hasCountryByName(final Country country) {
		for (Country c : INSTANCE.map.keySet()){
			if(c.getName().equalsIgnoreCase(country.getName()))
				return true;
		}
		return false;
	}

	public static void setRebuildNeeded() {
		rebuildNeeded = true;
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
