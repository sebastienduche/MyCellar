package mycellar.vignobles;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.datas.jaxb.VignoblesJaxb;
import mycellar.countries.Countries;
import mycellar.countries.Country;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.3
 * @since 10/11/20
 */

public final class CountryVignobles {

	private static final String FRA = "FRA";
	private static final String ITA = "ITA";
	private final Map<Country, VignoblesJaxb> countryToVignobles = new HashMap<>();

	private final Map<String, VignobleJaxb> mapCountryVignobleIDToVignoble = new HashMap<>();
	private final List<VignobleJaxb> usedVignoblesList = new LinkedList<>();
	private final List<String> usedAppellationsList = new LinkedList<>();

	private static final CountryVignobles INSTANCE = new CountryVignobles();
	private static boolean rebuildNeeded = false;

	private CountryVignobles() {
		Countries.findbyId(FRA).ifPresent(country -> countryToVignobles.put(country, VignoblesJaxb.loadFrance()));
		Countries.findbyId(ITA).ifPresent(country -> countryToVignobles.put(country, VignoblesJaxb.loadItalie()));
		setRebuildNeeded();
	}

	public static void init() {
		INSTANCE.countryToVignobles.clear();
		Countries.findbyId(FRA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, VignoblesJaxb.loadFrance()));
		Countries.findbyId(ITA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, VignoblesJaxb.loadItalie()));
		setRebuildNeeded();
	}

	public static void close() {
		INSTANCE.countryToVignobles.clear();
		setRebuildNeeded();
	}

	public static void load() {
		VignoblesJaxb.loadAllCountries(INSTANCE.countryToVignobles);
		setRebuildNeeded();
		rebuild();
	}

	public static Optional<VignoblesJaxb> getVignobles(Country country) {
		return Optional.ofNullable(INSTANCE.countryToVignobles.get(country));
	}

	public static Optional<VignoblesJaxb> createVignoblesCountry(Country country) {
		Debug("Creating country... " + country.getName());
		if (country.getId() == null) {
			generateCountryId(country);
		}
		if (getVignobles(country).isPresent()) {
			Debug("ERROR: the country already exist: " + country.getName());
			return Optional.empty();
		}
		VignoblesJaxb vignoblesJaxb = new VignoblesJaxb();
		vignoblesJaxb.init();
		INSTANCE.countryToVignobles.put(country, vignoblesJaxb);
		Debug("Creating country End");
		return Optional.of(vignoblesJaxb);
	}

	public static void deleteCountry(Country country) {
		Debug("Deleting country... " + country.getName());
		INSTANCE.countryToVignobles.remove(country);
		boolean resul = VignoblesJaxb.delete(country);
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
			VignobleJaxb vignobleJaxb = b.getVignoble();
			if (vignobleJaxb != null && !INSTANCE.usedVignoblesList.contains(vignobleJaxb)) {
				INSTANCE.usedVignoblesList.add(vignobleJaxb);
			}
			createVignobleInMap(vignobleJaxb);
		}
		for (VignobleJaxb v : INSTANCE.usedVignoblesList) {
			addVignoble(v);
		}
		rebuildNeeded = false;
		Debug("rebuild... End");
	}

	public static void createVignobleInMap(final VignobleJaxb vignobleJaxb) {
		if (vignobleJaxb == null || vignobleJaxb.getCountry().isEmpty()) {
			return;
		}

		Countries.findbyId(vignobleJaxb.getCountry()).ifPresent(country -> {
			VignoblesJaxb vignoblesJaxb = getVignobles(country)
					.orElse(createVignoblesCountry(country)
							.orElse(null));
			if (vignoblesJaxb == null) {
				Debug("ERROR: createVignobleInMap: Unable to create a VignobleCoutry!");
				return;
			}
			Optional<CountryVignobleJaxb> countryVignoble = vignoblesJaxb.findVignobleWithAppelation(vignobleJaxb);
			boolean found = true;
			if (countryVignoble.isEmpty()) {
				countryVignoble = vignoblesJaxb.findVignoble(vignobleJaxb);
				found = false;
				if (countryVignoble.isEmpty()) {
					vignoblesJaxb.addVignoble(vignobleJaxb);
				}
				countryVignoble = vignoblesJaxb.findVignoble(vignobleJaxb);
			}
			if (countryVignoble.isEmpty()) {
				Debug("ERROR: Unable to find vignoble " + vignobleJaxb);
				return;
			}
			AppelationJaxb appelationJaxb = new AppelationJaxb();
			if (!found) {
				appelationJaxb.setAOC(vignobleJaxb.getAOC());
				appelationJaxb.setAOP(vignobleJaxb.getAOP());
				appelationJaxb.setIGP(vignobleJaxb.getIGP());
				if (!appelationJaxb.isEmpty()) {
					countryVignoble.get().add(appelationJaxb);
					countryVignoble = vignoblesJaxb.findVignobleWithAppelation(vignobleJaxb);
				}
			}
			if (countryVignoble.isEmpty() && !appelationJaxb.isEmpty()) {
				Debug("ERROR: Unable to find created vignoble " + vignobleJaxb);
				return;
			}

			final AppelationJaxb appellation = vignoblesJaxb.findAppelation(vignobleJaxb);
			final String val = vignobleJaxb.toString();
			if (appellation != null && !appellation.isEmpty() && !INSTANCE.usedAppellationsList.contains(val)) {
				INSTANCE.usedAppellationsList.add(val);
			}

			if (countryVignoble.isPresent() && !countryVignoble.get().isEmpty()) {
				INSTANCE.mapCountryVignobleIDToVignoble.put(new CountryVignobleID(country, countryVignoble.get()).getId(), vignobleJaxb);
			}
		});

	}

	public static boolean isVignobleUsed(Country country, CountryVignobleJaxb vignoble) {
		VignobleJaxb vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, vignoble).getId());
		return vigne != null && vigne.getCountry().equalsIgnoreCase(country.getId()) && INSTANCE.usedVignoblesList.contains(vigne);
	}

	public static boolean isAppellationUsed(Country country, CountryVignobleJaxb countryVignobleJaxb, AppelationJaxb appellation) {
		var aop = appellation.getAOP() != null ? appellation.getAOP() : appellation.getAOC();
		var igp = appellation.getIGP() != null ? appellation.getIGP() : "";
		VignobleJaxb vignobleJaxb = new VignobleJaxb(country.getId(), countryVignobleJaxb.getName(), appellation.getAOC(), igp, aop);
		return INSTANCE.usedAppellationsList.contains(vignobleJaxb.toString());
	}

	public static void renameVignoble(final Country country, final CountryVignobleJaxb countryVignobleJaxb, final String name) {
		VignobleJaxb bouteilleVignobleJaxb = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, countryVignobleJaxb).getId());
		countryVignobleJaxb.setName(name);
		if (bouteilleVignobleJaxb == null) {
			Debug("ERROR: Unable to rename vignoble: " + countryVignobleJaxb.getName());
			return;
		}
		if (INSTANCE.usedVignoblesList.contains(bouteilleVignobleJaxb)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			for (Bouteille b : list) {
				VignobleJaxb v = b.getVignoble();
				if (v != null && v.getName().equals(bouteilleVignobleJaxb.getName())) {
					v.setName(name);
				}
			}
		}
		bouteilleVignobleJaxb.setName(name);
		setRebuildNeeded();
		rebuild();
	}

	public static void renameAOC(final Country country, final CountryVignobleJaxb countryVignobleJaxb, final AppelationJaxb appelationJaxb, final String name) {
		VignobleJaxb vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, countryVignobleJaxb).getId());
		if (vigne == null) {
			Debug("ERROR: Unable to rename AOC: " + countryVignobleJaxb.getName());
			appelationJaxb.setAOC(name);
			return;
		}
		// TODO RENAME DOES NOT WORK
		if (INSTANCE.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			list.stream()
					.map(Bouteille::getVignoble)
					.filter(Objects::nonNull)
					.filter(vignoble -> vignoble.equals(vigne))
					.forEach(vignoble -> {
						if (vignoble.getAOC() != null && vignoble.getAOC().equals(appelationJaxb.getAOC())) {
							vignoble.setAOC(name);
						}
					});
//			for (Bouteille b : list) {
//				Vignoble v = b.getVignoble();
//				if (v != null && v.equals(vigne)) {
//					if (v.getAOC() != null && v.getAOC().equals(appelation.getAOC())) {
//						v.setAOC(name);
//					}
//				}
//			}
		}
		vigne.setAOC(name);
		appelationJaxb.setAOC(name);
		setRebuildNeeded();
		rebuild();
	}

	public static void renameIGP(final Country country, final CountryVignobleJaxb countryVignobleJaxb, final AppelationJaxb appelationJaxb, final String name) {
		VignobleJaxb vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, countryVignobleJaxb).getId());
		if (vigne == null) {
			appelationJaxb.setIGP(name);
			Debug("ERROR: Unable to rename IGP: " + countryVignobleJaxb.getName());
			return;
		}
		if (INSTANCE.usedVignoblesList.contains(vigne)) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			list.stream()
					.map(Bouteille::getVignoble)
					.filter(Objects::nonNull)
					.filter(vignoble -> vignoble.equals(vigne))
					.forEach(vignoble -> {
						if (vignoble.getIGP() != null && vignoble.getIGP().equals(appelationJaxb.getIGP())) {
							vignoble.setIGP(name);
						}
					});
//			for (Bouteille b : list) {
//				Vignoble v = b.getVignoble();
//				if(v != null && v.equals(vigne)) {
//					if (v.getIGP() != null && v.getIGP().equals(appelation.getIGP())) {
//						v.setIGP(name);
//					}
//				}
//			}
		}
		vigne.setIGP(name);
		appelationJaxb.setIGP(name);
		setRebuildNeeded();
		rebuild();
	}

	private static void addVignoble(final VignobleJaxb bouteilleVignobleJaxb) {
		if (bouteilleVignobleJaxb == null || bouteilleVignobleJaxb.getCountry() == null || bouteilleVignobleJaxb.getCountry().isEmpty()) {
			return;
		}
		Country country = Countries.findByIdOrLabel(bouteilleVignobleJaxb.getCountry());
		if (country != null) {
			if (getVignobles(country).isEmpty()) {
				createVignoblesCountry(country);
			}
			final VignoblesJaxb vignoblesJaxb = getVignobles(country).orElse(null);
			if (vignoblesJaxb == null) {
				Debug("ERROR: addVignoble: Unable to find vignobles for country " + country);
				return;
			}
			Optional<CountryVignobleJaxb> countryVignoble = vignoblesJaxb.findVignobleWithAppelation(bouteilleVignobleJaxb);
			if (countryVignoble.isEmpty()) {
				Optional<CountryVignobleJaxb> vignoble = vignoblesJaxb.findVignoble(bouteilleVignobleJaxb);
				if (vignoble.isPresent() && !bouteilleVignobleJaxb.isAppellationEmpty()) {
					AppelationJaxb appelationJaxb = new AppelationJaxb();
					appelationJaxb.setAOC(bouteilleVignobleJaxb.getAOC());
					appelationJaxb.setAOP(bouteilleVignobleJaxb.getAOP());
					appelationJaxb.setIGP(bouteilleVignobleJaxb.getIGP());
					vignoble.get().add(appelationJaxb);
				} else if (vignoble.isEmpty()) {
					vignoblesJaxb.addVignoble(bouteilleVignobleJaxb);
				}
			} else {
				AppelationJaxb ap = vignoblesJaxb.findAppelation(bouteilleVignobleJaxb);
				bouteilleVignobleJaxb.setValues(ap);
				INSTANCE.mapCountryVignobleIDToVignoble.put(new CountryVignobleID(country, countryVignoble.get()).getId(), bouteilleVignobleJaxb);
			}
		}	else {
			country = new Country(bouteilleVignobleJaxb.getCountry());
			generateCountryId(country);
			VignoblesJaxb vignoblesJaxb = new VignoblesJaxb();
			vignoblesJaxb.init();
			vignoblesJaxb.addVignoble(bouteilleVignobleJaxb);
			Countries.add(country);
			INSTANCE.countryToVignobles.put(country, vignoblesJaxb);
		}
		if (!INSTANCE.usedVignoblesList.contains(bouteilleVignobleJaxb)) {
			INSTANCE.usedVignoblesList.add(bouteilleVignobleJaxb);
		}
		String val = bouteilleVignobleJaxb.toString();
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
			VignoblesJaxb.save(c, INSTANCE.countryToVignobles.get(c));
		}
		Debug("Saved");
	}

	public static boolean hasCountryByName(final Country country) {
		for (Country c : INSTANCE.countryToVignobles.keySet()) {
			if (c.getName().equalsIgnoreCase(country.getName())) {
				return true;
			}
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
