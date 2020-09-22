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
import java.util.Optional;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.0
 * @since 02/09/20
 */

public final class CountryVignobles {

	private static final String FRA = "FRA";
	private static final String ITA = "ITA";
	private final Map<Country, Vignobles> countryToVignobles = new HashMap<>();

	private final Map<String, Vignoble> mapCountryVignobleIDToVignoble = new HashMap<>();
	private final List<Vignoble> usedVignoblesList = new LinkedList<>();
	private final List<String> usedAppellationsList = new LinkedList<>();

	private static final CountryVignobles INSTANCE = new CountryVignobles();
	private static boolean rebuildNeeded = false;

	private CountryVignobles() {
		countryToVignobles.put(Countries.find(FRA), Vignobles.loadFrance());
		countryToVignobles.put(Countries.find(ITA), Vignobles.loadItalie());
		setRebuildNeeded();
	}
	
	public static void init() {
		INSTANCE.countryToVignobles.clear();
		INSTANCE.countryToVignobles.put(Countries.find(FRA), Vignobles.loadFrance());
		INSTANCE.countryToVignobles.put(Countries.find(ITA), Vignobles.loadItalie());
		setRebuildNeeded();
	}
	
	public static void close() {
		INSTANCE.countryToVignobles.clear();
		setRebuildNeeded();
	}
	
	public static void load() {
		Vignobles.loadAllCountries(INSTANCE.countryToVignobles);
		setRebuildNeeded();
		rebuild();
	}
	
	public static Vignobles getVignobles(Country country) {
		return INSTANCE.countryToVignobles.get(country);
	}
	
	public static void createVignoblesCountry(Country country) {
		Debug("Creating country... " + country.getName());
		if (country.getId() == null) {
			generateCountryId(country);
		}
		if (getVignobles(country) != null) {
			Debug("ERROR: the country already exist: " + country.getName());
			return;
		}
		Vignobles vignobles = new Vignobles();
		vignobles.init();
		INSTANCE.countryToVignobles.put(country, vignobles);
		Debug("Creating country End");
	}
	
	public static void deleteCountry(Country country) {
		Debug("Deleting country... " + country.getName());
		INSTANCE.countryToVignobles.remove(country);
		boolean resul = Vignobles.delete(country);
		Debug("Deleting country End with resul=" + resul);
	}
	
	private static void generateCountryId(Country country) {
		String id = Program.removeAccents(country.getName()).toUpperCase() + "000";
		id = id.substring(0, 3);

		boolean found;
		int i = 1;
		do {
			found = false;
    		for (Country c: INSTANCE.countryToVignobles.keySet()) {
    			if (c.getId().equalsIgnoreCase(id)) {
    				id = id.substring(0, 3) + i;
    				i++;
    				found = true;
    			}
    		}
		} while(found);
		country.setId(id);
	}

	public static void rebuild() {
		if (!rebuildNeeded) {
			return;
		}
		Debug("rebuild...");
		INSTANCE.usedVignoblesList.clear();
		INSTANCE.mapCountryVignobleIDToVignoble.clear();
		INSTANCE.usedAppellationsList.clear();
		for (Bouteille b : Program.getStorage().getAllList()) {
			Vignoble vignoble = b.getVignoble();
			if (vignoble != null && !INSTANCE.usedVignoblesList.contains(vignoble)) {
				INSTANCE.usedVignoblesList.add(vignoble);
			}
			createVignobleInMap(vignoble);
		}
		for (Vignoble v : INSTANCE.usedVignoblesList) {
			addVignoble(v);	
		}
		rebuildNeeded = false;
		Debug("rebuild... End");
	}
	
	static void createVignobleInMap(final Vignoble vignoble) {
		if (vignoble == null || vignoble.getCountry().isEmpty()) {
			return;
		}

		Country country = Countries.find(vignoble.getCountry());
		if (country == null) {
			return;
		}
		Vignobles vignobles = getVignobles(country);
		if (vignobles == null) {
			createVignoblesCountry(country);
			vignobles = getVignobles(country);
		}
		Optional<CountryVignoble> countryVignoble = vignobles.findVignobleWithAppelation(vignoble);
		boolean found = true;
		if (countryVignoble.isEmpty()) {
			countryVignoble = vignobles.findVignoble(vignoble);
			found = false;
			if (countryVignoble.isEmpty()) {
				vignobles.addVignoble(vignoble);
			}
			countryVignoble = vignobles.findVignoble(vignoble);
		}
		if (countryVignoble.isEmpty()) {
			Debug("ERROR: Unable to find vignoble "+vignoble);
			return;
		}
		Appelation appelation = new Appelation();
		if (!found) {
			appelation.setAOC(vignoble.getAOC());
			appelation.setAOP(vignoble.getAOP());
			appelation.setIGP(vignoble.getIGP());
			if (!appelation.isEmpty()) {
				countryVignoble.get().add(appelation);
				countryVignoble = vignobles.findVignobleWithAppelation(vignoble);
			}
		}
		if (countryVignoble.isEmpty() && !appelation.isEmpty()) {
			Debug("ERROR: Unable to find created vignoble " + vignoble);
			return;
		}
		
		final Appelation appellation = vignobles.findAppelation(vignoble);
		String val = vignoble.toString();
		if (appellation != null && !appellation.isEmpty() && !INSTANCE.usedAppellationsList.contains(val)) {
			INSTANCE.usedAppellationsList.add(val);
		}

		if (countryVignoble.isPresent() && !countryVignoble.get().isEmpty()) {
			INSTANCE.mapCountryVignobleIDToVignoble.put(new CountryVignobleID(country, countryVignoble.get()).getId(), vignoble);
		}
	}

	public static boolean isVignobleUsed(Country country, CountryVignoble vignoble) {
		Vignoble vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, vignoble).getId());
		return vigne != null && vigne.getCountry().equalsIgnoreCase(country.getId()) && INSTANCE.usedVignoblesList.contains(vigne);
	}
	
	public static boolean isAppellationUsed(Country country, CountryVignoble vignoble, Appelation appellation) {
		var aop = appellation.getAOP() != null ? appellation.getAOP() : appellation.getAOC();
		var igp = appellation.getIGP() != null ? appellation.getIGP() : "";
		Vignoble vignoble1 = new Vignoble(country.getId(), vignoble.getName(), appellation.getAOC(), igp, aop);
		return INSTANCE.usedAppellationsList.contains(vignoble1.toString());
	}
	
	public static void renameVignoble(final Country country, final CountryVignoble vignoble, final String name) {
		Vignoble bouteilleVignoble = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, vignoble).getId());
		vignoble.setName(name);
		if (bouteilleVignoble == null) {
			Debug("ERROR: Unable to rename vignoble: " + vignoble.getName());
			return;
		}
		if (INSTANCE.usedVignoblesList.contains(bouteilleVignoble)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				Vignoble v = b.getVignoble();
				if (v != null && v.getName().equals(bouteilleVignoble.getName())) {
					v.setName(name);
				}
			}
		}
		bouteilleVignoble.setName(name);
		setRebuildNeeded();
		rebuild();
	}
	
	public static void renameAOC(final Country country, final CountryVignoble vignoble, final Appelation appelation, final String name) {
		Vignoble vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, vignoble).getId());
		if (vigne == null) {
			Debug("ERROR: Unable to rename AOC: "+vignoble.getName());
			appelation.setAOC(name);
			return;
		}
		if(INSTANCE.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				Vignoble v = b.getVignoble();
				if (v != null && v.equals(vigne)) {
					if (v.getAOC() != null && v.getAOC().equals(appelation.getAOC())) {
						v.setAOC(name);
					}
				}
			}
		}
		vigne.setAOC(name);
		appelation.setAOC(name);
		setRebuildNeeded();
		rebuild();
	}
	
	public static void renameIGP(final Country country, final CountryVignoble vignoble, final Appelation appelation, final String name) {
		Vignoble vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, vignoble).getId());
		if (vigne == null) {
			appelation.setIGP(name);
			Debug("ERROR: Unable to rename IGP: " + vignoble.getName());
			return;
		}
		if (INSTANCE.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				Vignoble v = b.getVignoble();
				if(v != null && v.equals(vigne)) {
					if (v.getIGP() != null && v.getIGP().equals(appelation.getIGP())) {
						v.setIGP(name);
					}
				}
			}
		}
		vigne.setIGP(name);
		appelation.setIGP(name);
		setRebuildNeeded();
		rebuild();
	}

	/**
	 * @param bouteilleVignoble
	 * @return
	 */
	private static void addVignoble(final Vignoble bouteilleVignoble) {
		if (bouteilleVignoble == null || bouteilleVignoble.getCountry() == null || bouteilleVignoble.getCountry().isEmpty()) {
			return;
		}
		Country country = Countries.findByIdOrLabel(bouteilleVignoble.getCountry());
		if (country != null) {
			if (getVignobles(country) == null) {
				createVignoblesCountry(country);
			}
			Optional<CountryVignoble> countryVignoble = getVignobles(country).findVignobleWithAppelation(bouteilleVignoble);
			if (countryVignoble.isEmpty()) {
				Optional<CountryVignoble> vignoble = getVignobles(country).findVignoble(bouteilleVignoble);
				if (vignoble.isPresent() && !bouteilleVignoble.isAppellationEmpty()) {
					Appelation appelation = new Appelation();
					appelation.setAOC(bouteilleVignoble.getAOC());
					appelation.setAOP(bouteilleVignoble.getAOP());
					appelation.setIGP(bouteilleVignoble.getIGP());
					vignoble.get().add(appelation);
				} else if (vignoble.isEmpty()) {
					getVignobles(country).addVignoble(bouteilleVignoble);
				}
			} else {
				Appelation ap = getVignobles(country).findAppelation(bouteilleVignoble);
				bouteilleVignoble.setValues(ap);
				INSTANCE.mapCountryVignobleIDToVignoble.put(new CountryVignobleID(country, countryVignoble.get()).getId(), bouteilleVignoble);
			}
		}	else {
			country = new Country(bouteilleVignoble.getCountry());
			generateCountryId(country);
			Vignobles vignobles = new Vignobles();
			vignobles.init();
			vignobles.addVignoble(bouteilleVignoble);
			Countries.add(country);
			INSTANCE.countryToVignobles.put(country, vignobles);
		}
		if (!INSTANCE.usedVignoblesList.contains(bouteilleVignoble)) {
			INSTANCE.usedVignoblesList.add(bouteilleVignoble);
		}
		String val = bouteilleVignoble.toString();
		if (!INSTANCE.usedAppellationsList.contains(val)) {
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
		for (Country c : INSTANCE.countryToVignobles.keySet()){
			Vignobles.save(c, INSTANCE.countryToVignobles.get(c));
		}
		Debug("Saved");
	}
	
	public static boolean hasCountryByName(final Country country) {
		for (Country c : INSTANCE.countryToVignobles.keySet()){
			if (c.getName().equalsIgnoreCase(country.getName()))
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
