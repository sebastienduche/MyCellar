package mycellar.vignobles;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.datas.jaxb.VignobleListJaxb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static mycellar.Program.FRA;
import static mycellar.Program.ITA;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.5
 * @since 13/11/20
 */

public final class CountryVignobleController {

	private final Map<CountryJaxb, VignobleListJaxb> countryToVignobles = new HashMap<>();

	private final Map<Long, VignobleJaxb> mapCountryVignobleIDToVignoble = new HashMap<>();
	private final Map<Long, Long> mapBottleAppellationIDToAppellationID = new HashMap<>(); // For Appellation Used
	private final List<Long> usedVignoblesIDList = new LinkedList<>();

	private static final CountryVignobleController INSTANCE = new CountryVignobleController();
	private static boolean rebuildNeeded = false;

	private CountryVignobleController() {
		CountryListJaxb.findbyId(FRA).ifPresent(country -> countryToVignobles.put(country, VignobleListJaxb.loadFrance()));
		CountryListJaxb.findbyId(ITA).ifPresent(country -> countryToVignobles.put(country, VignobleListJaxb.loadItalie()));
		setRebuildNeeded();
	}

	public static void init() {
		INSTANCE.countryToVignobles.clear();
		CountryListJaxb.findbyId(FRA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, VignobleListJaxb.loadFrance()));
		CountryListJaxb.findbyId(ITA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, VignobleListJaxb.loadItalie()));
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

	public static Optional<VignobleListJaxb> getVignobles(CountryJaxb countryJaxb) {
		return Optional.ofNullable(INSTANCE.countryToVignobles.get(countryJaxb));
	}

	public static Optional<VignobleListJaxb> createCountry(CountryJaxb countryJaxb) {
		Debug("Creating country... " + countryJaxb.getName());
		if (countryJaxb.getId() == null) {
			generateCountryId(countryJaxb);
		}
		if (getVignobles(countryJaxb).isPresent()) {
			Debug("ERROR: the country already exist: " + countryJaxb.getName());
			return Optional.empty();
		}
		VignobleListJaxb vignobleListJaxb = new VignobleListJaxb();
		vignobleListJaxb.init();
		INSTANCE.countryToVignobles.put(countryJaxb, vignobleListJaxb);
		Debug("Creating country End");
		return Optional.of(vignobleListJaxb);
	}

	public static void deleteCountry(CountryJaxb countryJaxb) {
		Debug("Deleting country... " + countryJaxb.getName());
		INSTANCE.countryToVignobles.remove(countryJaxb);
		boolean resul = VignobleListJaxb.delete(countryJaxb);
		Debug("Deleting country End with resul=" + resul);
	}

	public static void generateCountryId(CountryJaxb countryJaxb) {
		String id = Program.removeAccents(countryJaxb.getName()).toUpperCase() + "000";
		id = id.substring(0, 3);

		boolean found;
		int i = 1;
		do {
			found = false;
			for (CountryJaxb c: INSTANCE.countryToVignobles.keySet()) {
				if (c.getId().equalsIgnoreCase(id)) {
					id = id.substring(0, 3) + i;
					i++;
					found = true;
				}
			}
		} while(found);
		countryJaxb.setId(id);
	}

	public static void rebuild() {
		if (!rebuildNeeded) {
			return;
		}
		Debug("rebuild...");
		INSTANCE.usedVignoblesIDList.clear();
		INSTANCE.mapCountryVignobleIDToVignoble.clear();
		INSTANCE.mapBottleAppellationIDToAppellationID.clear();
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
			mapAppellation(vignobleJaxb);
		});

		rebuildNeeded = false;
		Debug("rebuild... End");
	}

	private static void mapAppellation(VignobleJaxb vignobleJaxb) {
		CountryListJaxb.findbyId(vignobleJaxb.getCountry())
				.flatMap(CountryVignobleController::getVignobles)
				.flatMap(vignobleListJaxb -> vignobleListJaxb.findAppelation(vignobleJaxb))
				.ifPresent(appelationJaxb -> INSTANCE.mapBottleAppellationIDToAppellationID.put(vignobleJaxb.getId(), appelationJaxb.getId()));
	}

	public static void createVignobleInMap(final VignobleJaxb vignobleJaxb) {
		if (VignobleJaxb.isEmpty(vignobleJaxb)) {
			return;
		}

		CountryListJaxb.findbyId(vignobleJaxb.getCountry()).ifPresent(country -> {
			VignobleListJaxb vignobleListJaxb = getVignobles(country)
					.orElseGet(() -> createCountry(country)
							.orElse(null));
			if (vignobleListJaxb == null) {
				Debug("ERROR: createVignobleInMap: Unable to create a VignobleListJaxb!");
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
				Debug("ERROR: Unable to find VignobleJaxb " + vignobleJaxb);
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
				Debug("ERROR: Unable to find created VignobleJaxb " + vignobleJaxb);
				return;
			}

			if (countryVignoble.isPresent() && !countryVignoble.get().isEmpty()) {
				INSTANCE.mapCountryVignobleIDToVignoble.put(countryVignoble.get().getId(), vignobleJaxb);
			}
		});

	}

	public static boolean isVignobleUsed(CountryJaxb countryJaxb, CountryVignobleJaxb countryVignobleJaxb) {
		VignobleJaxb vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(countryVignobleJaxb.getId());
		return vigne != null && vigne.getCountry().equalsIgnoreCase(countryJaxb.getId()) && INSTANCE.usedVignoblesIDList.contains(vigne.getId());
	}

	public static boolean isAppellationUsed(AppelationJaxb appellation) {
		return INSTANCE.mapBottleAppellationIDToAppellationID.containsValue(appellation.getId());
	}

	public static void renameVignoble(final CountryVignobleJaxb countryVignobleJaxb, final String name) {
		VignobleJaxb bouteilleVignobleJaxb = INSTANCE.mapCountryVignobleIDToVignoble.get(countryVignobleJaxb.getId());
		final String oldName = countryVignobleJaxb.getName();
		countryVignobleJaxb.setName(name);
		if (bouteilleVignobleJaxb == null) {
			Debug("WARNING: No bottles to modify with Vignoble name: " + oldName);
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

	public static void renameAOC(final CountryVignobleJaxb countryVignobleJaxb, final AppelationJaxb appelationJaxb, final String name) {
		VignobleJaxb vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(countryVignobleJaxb.getId());
		final String oldName = appelationJaxb.getAOC();
		appelationJaxb.setAOC(name);
		if (vigne == null) {
			Debug("WARNING: No bottles to modify with AOC name: " + oldName);
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

	public static void renameIGP(final CountryVignobleJaxb countryVignobleJaxb, final AppelationJaxb appelationJaxb, final String name) {
		VignobleJaxb vigne = INSTANCE.mapCountryVignobleIDToVignoble.get(countryVignobleJaxb.getId());
		final String oldName = appelationJaxb.getIGP();
		appelationJaxb.setIGP(name);
		if (vigne == null) {
			Debug("WARNING: No bottles to modify with IGP name: " + oldName);
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
		if (VignobleJaxb.isEmpty(bouteilleVignobleJaxb)) {
			return;
		}
		CountryJaxb countryJaxb = CountryListJaxb.findByIdOrLabel(bouteilleVignobleJaxb.getCountry());
		if (countryJaxb != null) {
			if (getVignobles(countryJaxb).isEmpty()) {
				createCountry(countryJaxb);
			}
			final VignobleListJaxb vignobleListJaxb = getVignobles(countryJaxb).orElse(null);
			if (vignobleListJaxb == null) {
				Debug("ERROR: addVignoble: Unable to find vignobles for country " + countryJaxb);
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
				INSTANCE.mapCountryVignobleIDToVignoble.put(countryVignoble.get().getId(), bouteilleVignobleJaxb);
			}
		}	else {
			countryJaxb = new CountryJaxb(bouteilleVignobleJaxb.getCountry());
			generateCountryId(countryJaxb);
			VignobleListJaxb vignobleListJaxb = new VignobleListJaxb();
			vignobleListJaxb.init();
			vignobleListJaxb.addVignoble(bouteilleVignobleJaxb);
			CountryListJaxb.add(countryJaxb);
			INSTANCE.countryToVignobles.put(countryJaxb, vignobleListJaxb);
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
		for (CountryJaxb c : INSTANCE.countryToVignobles.keySet()){
			VignobleListJaxb.save(c, INSTANCE.countryToVignobles.get(c));
		}
		Debug("Saved");
	}

	public static boolean hasCountryWithName(final String country) {
		for (CountryJaxb c : INSTANCE.countryToVignobles.keySet()) {
			if (c.getName().equalsIgnoreCase(country)) {
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
		Program.Debug("CountryVignobleController: " + sText);
	}
}