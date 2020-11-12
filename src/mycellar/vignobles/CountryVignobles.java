package mycellar.vignobles;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.datas.jaxb.VignobleListJaxb;
import mycellar.countries.Countries;
import mycellar.countries.Country;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.4
 * @since 12/11/20
 */

public final class CountryVignobles {

	private static final String FRA = "FRA";
	private static final String ITA = "ITA";
	private final Map<Country, VignobleListJaxb> countryToVignobles = new HashMap<>();

	private final Map<String, VignobleJaxb> mapCountryVignobleIDToVignoble = new HashMap<>();
	private final Map<Long, Long> mapBottleAppelationIDToAppelationID = new HashMap<>();
	private final List<Long> usedVignoblesIDList = new LinkedList<>();

	private static final CountryVignobles INSTANCE = new CountryVignobles();
	private static boolean rebuildNeeded = false;

	private CountryVignobles() {
		Countries.findbyId(FRA).ifPresent(country -> countryToVignobles.put(country, VignobleListJaxb.loadFrance()));
		Countries.findbyId(ITA).ifPresent(country -> countryToVignobles.put(country, VignobleListJaxb.loadItalie()));
		setRebuildNeeded();
	}

	public static void init() {
		INSTANCE.countryToVignobles.clear();
		Countries.findbyId(FRA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, VignobleListJaxb.loadFrance()));
		Countries.findbyId(ITA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, VignobleListJaxb.loadItalie()));
		setRebuildNeeded();
	}

	public static void close() {
		INSTANCE.countryToVignobles.clear();
		setRebuildNeeded();
	}

	public static void load() {
		VignobleListJaxb.loadAllCountries(INSTANCE.countryToVignobles);
		setRebuildNeeded();
		rebuild();
	}

	public static Optional<VignobleListJaxb> getVignobles(Country country) {
		return Optional.ofNullable(INSTANCE.countryToVignobles.get(country));
	}

	public static Optional<VignobleListJaxb> createVignoblesCountry(Country country) {
		Debug("Creating country... " + country.getName());
		if (country.getId() == null) {
			generateCountryId(country);
		}
		if (getVignobles(country).isPresent()) {
			Debug("ERROR: the country already exist: " + country.getName());
			return Optional.empty();
		}
		VignobleListJaxb vignobleListJaxb = new VignobleListJaxb();
		vignobleListJaxb.init();
		INSTANCE.countryToVignobles.put(country, vignobleListJaxb);
		Debug("Creating country End");
		return Optional.of(vignobleListJaxb);
	}

	public static void deleteCountry(Country country) {
		Debug("Deleting country... " + country.getName());
		INSTANCE.countryToVignobles.remove(country);
		boolean resul = VignobleListJaxb.delete(country);
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
		INSTANCE.usedVignoblesIDList.clear();
		INSTANCE.mapCountryVignobleIDToVignoble.clear();
		INSTANCE.mapBottleAppelationIDToAppelationID.clear();
		List<VignobleJaxb> vignobleJaxbList = Program.getStorage().getAllList()
				.stream()
				.map(Bouteille::getVignoble)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		vignobleJaxbList.forEach(vignobleJaxb -> {
			if (!INSTANCE.usedVignoblesIDList.contains(vignobleJaxb.getId())) {
				INSTANCE.usedVignoblesIDList.add(vignobleJaxb.getId());
				addVignoble(vignobleJaxb);
			}
			createVignobleInMap(vignobleJaxb);
			mapAppelation(vignobleJaxb);
		});

//		Program.getStorage().getAllList()
//				.stream()
//				.map(Bouteille::getVignoble)
//				.filter(Objects::nonNull)
//				.forEach(CountryVignobles::createVignobleInMap);
//
//		vignobleJaxbList
//				.forEach(CountryVignobles::mapAppelation);
		rebuildNeeded = false;
		Debug("rebuild... End");
	}

	private static void mapAppelation(VignobleJaxb vignobleJaxb) {
		Countries.findbyId(vignobleJaxb.getCountry())
				.flatMap(CountryVignobles::getVignobles)
				.flatMap(vignobleListJaxb -> vignobleListJaxb.findAppelation(vignobleJaxb))
				.ifPresent(appelationJaxb -> INSTANCE.mapBottleAppelationIDToAppelationID.put(vignobleJaxb.getId(), appelationJaxb.getId()));
	}

	public static void createVignobleInMap(final VignobleJaxb vignobleJaxb) {
		if (vignobleJaxb == null || vignobleJaxb.getCountry().isEmpty()) {
			return;
		}

		Countries.findbyId(vignobleJaxb.getCountry()).ifPresent(country -> {
			VignobleListJaxb vignobleListJaxb = getVignobles(country)
					.orElse(createVignoblesCountry(country)
							.orElse(null));
			if (vignobleListJaxb == null) {
				Debug("ERROR: createVignobleInMap: Unable to create a VignobleCoutry!");
				return;
			}
			Optional<CountryVignobleJaxb> countryVignoble = vignobleListJaxb.findVignobleWithAppelation(vignobleJaxb);
			boolean found = true;
			if (countryVignoble.isEmpty()) {
				countryVignoble = vignobleListJaxb.findVignoble(vignobleJaxb);
				found = false;
				if (countryVignoble.isEmpty()) {
					vignobleListJaxb.addVignoble(vignobleJaxb);
				}
				countryVignoble = vignobleListJaxb.findVignoble(vignobleJaxb);
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
					countryVignoble = vignobleListJaxb.findVignobleWithAppelation(vignobleJaxb);
				}
			}
			if (countryVignoble.isEmpty() && !appelationJaxb.isEmpty()) {
				Debug("ERROR: Unable to find created vignoble " + vignobleJaxb);
				return;
			}

			if (countryVignoble.isPresent() && !countryVignoble.get().isEmpty()) {
				INSTANCE.mapCountryVignobleIDToVignoble.put(new CountryVignobleID(country, countryVignoble.get()).getId(), vignobleJaxb);
			}
		});

	}

	public static boolean isVignobleUsed(Country country, CountryVignobleJaxb vignoble) {
		VignobleJaxb vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, vignoble).getId());
		return vigne != null && vigne.getCountry().equalsIgnoreCase(country.getId()) && INSTANCE.usedVignoblesIDList.contains(vigne.getId());
	}

	public static boolean isAppellationUsed(AppelationJaxb appellation) {
		return INSTANCE.mapBottleAppelationIDToAppelationID.containsValue(appellation.getId());
	}

	public static void renameVignoble(final Country country, final CountryVignobleJaxb countryVignobleJaxb, final String name) {
		VignobleJaxb bouteilleVignobleJaxb = INSTANCE.mapCountryVignobleIDToVignoble.get(new CountryVignobleID(country, countryVignobleJaxb).getId());
		countryVignobleJaxb.setName(name);
		if (bouteilleVignobleJaxb == null) {
			Debug("ERROR: Unable to rename vignoble: " + countryVignobleJaxb.getName());
			return;
		}
		if (INSTANCE.usedVignoblesIDList.contains(bouteilleVignobleJaxb.getId())) {
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
		if (INSTANCE.usedVignoblesIDList.contains(vigne.getId())) {
			LinkedList<Bouteille> list = Program.getStorage().getAllList();
			list.stream()
					.map(Bouteille::getVignoble)
					.filter(Objects::nonNull)
					.filter(vignoble -> vignoble.getId() == vigne.getId() || vignoble.equals(vigne))
					.forEach(vignoble -> {
						if (vignoble.getAOC() != null && vignoble.getAOC().equals(appelationJaxb.getAOC())) {
							vignoble.setAOC(name);
						}
					});
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
		if (INSTANCE.usedVignoblesIDList.contains(vigne.getId())) {
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
			final VignobleListJaxb vignobleListJaxb = getVignobles(country).orElse(null);
			if (vignobleListJaxb == null) {
				Debug("ERROR: addVignoble: Unable to find vignobles for country " + country);
				return;
			}
			Optional<CountryVignobleJaxb> countryVignoble = vignobleListJaxb.findVignobleWithAppelation(bouteilleVignobleJaxb);
			if (countryVignoble.isEmpty()) {
				Optional<CountryVignobleJaxb> vignoble = vignobleListJaxb.findVignoble(bouteilleVignobleJaxb);
				if (vignoble.isPresent() && !bouteilleVignobleJaxb.isAppellationEmpty()) {
					AppelationJaxb appelationJaxb = new AppelationJaxb();
					appelationJaxb.setAOC(bouteilleVignobleJaxb.getAOC());
					appelationJaxb.setAOP(bouteilleVignobleJaxb.getAOP());
					appelationJaxb.setIGP(bouteilleVignobleJaxb.getIGP());
					vignoble.get().add(appelationJaxb);
				} else if (vignoble.isEmpty()) {
					vignobleListJaxb.addVignoble(bouteilleVignobleJaxb);
				}
			} else {
				vignobleListJaxb.findAppelation(bouteilleVignobleJaxb)
						.ifPresent(bouteilleVignobleJaxb::setValues);
				INSTANCE.mapCountryVignobleIDToVignoble.put(new CountryVignobleID(country, countryVignoble.get()).getId(), bouteilleVignobleJaxb);
			}
		}	else {
			country = new Country(bouteilleVignobleJaxb.getCountry());
			generateCountryId(country);
			VignobleListJaxb vignobleListJaxb = new VignobleListJaxb();
			vignobleListJaxb.init();
			vignobleListJaxb.addVignoble(bouteilleVignobleJaxb);
			Countries.add(country);
			INSTANCE.countryToVignobles.put(country, vignobleListJaxb);
		}
		if (!INSTANCE.usedVignoblesIDList.contains(bouteilleVignobleJaxb.getId())) {
			INSTANCE.usedVignoblesIDList.add(bouteilleVignobleJaxb.getId());
		}
	}

	public static void addVignobleFromBottle(final Bouteille wine) {
		Debug("addVignobleFromBottle...");
		addVignoble(wine.getVignoble());
		setRebuildNeeded();
		Debug("addVignobleFromBottle... End");
	}

	public static void save() {
		Debug("Saving...");
		for (Country c : INSTANCE.countryToVignobles.keySet()){
			VignobleListJaxb.save(c, INSTANCE.countryToVignobles.get(c));
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
	public static boolean isRebuildNeeded() {
		return rebuildNeeded;
	}

	private static void Debug(String sText) {
		Program.Debug("CountryVignobles: " + sText);
	}
}
