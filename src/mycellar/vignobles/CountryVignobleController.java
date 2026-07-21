package mycellar.vignobles;

import mycellar.Bouteille;
import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.ProgramConstants;
import mycellar.core.IMyCellarObject;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.core.datas.jaxb.VignobleListJaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.FRA;
import static mycellar.ProgramConstants.FRA_ID;
import static mycellar.ProgramConstants.ITA_ID;
import static mycellar.ProgramConstants.TEXT;
import static mycellar.core.datas.jaxb.VignobleListJaxb.VIGNOBLE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 4.0
 * @since 21/07/26
 */

public final class CountryVignobleController {

  private static final CountryVignobleController INSTANCE = new CountryVignobleController();
  private static boolean rebuildNeeded = false;
  private final Map<CountryJaxb, VignobleListJaxb> countryToVignobles = new HashMap<>();
  private static final VignobleListJaxb vignobleListFrance = loadFrance();
  private static final VignobleListJaxb vignobleListItaly = loadItaly();
  @Deprecated
  private final Map<Long, VignobleJaxb> mapCountryVignobleIDToVignoble = new HashMap<>();
  private final Map<UUID, VignobleJaxb> mapCountryVignobleUUIDToVignoble = new HashMap<>();
  @Deprecated
  private final Map<Long, Long> mapBottleAppellationIDToAppellationID = new HashMap<>(); // For Appellation Used
  private final Set<UUID> mapAppellationJaxbUUIDToAppellationUUID = new HashSet<>(); // For Appellation Used
  @Deprecated
  private final List<Long> usedVignoblesIDList = new LinkedList<>();
  private final Set<UUID> usedVignoblesUUIDList = new HashSet<>();
  @Deprecated
  private final Map<String, UUID> mapCountryIDToUUID = new HashMap<>();
  private boolean modified;

  private CountryVignobleController() {
    modified = false;
    CountryListJaxb.findByUUID(FRA_ID).ifPresent(country -> countryToVignobles.put(country, vignobleListFrance));
    CountryListJaxb.findByUUID(ITA_ID).ifPresent(country -> countryToVignobles.put(country, vignobleListItaly));
    setRebuildNeeded();
  }

  public static void reset() {
    INSTANCE.modified = false;
    INSTANCE.countryToVignobles.clear();
    CountryListJaxb.findByUUID(FRA_ID).ifPresent(country -> INSTANCE.countryToVignobles.put(country, vignobleListFrance));
    CountryListJaxb.findByUUID(ITA_ID).ifPresent(country -> INSTANCE.countryToVignobles.put(country, vignobleListItaly));
    setRebuildNeeded();
  }

  public static void close() {
    INSTANCE.modified = false;
    INSTANCE.countryToVignobles.clear();
    setRebuildNeeded();
  }

  record CountryToUuid(String filename, UUID uuid) {
  }

  public static void load() {
    Debug("Loading all countries");
    INSTANCE.countryToVignobles.clear();
    INSTANCE.mapCountryIDToUUID.clear();
    CountryListJaxb.findByUUID(FRA_ID).ifPresent(country -> INSTANCE.countryToVignobles.put(country, vignobleListFrance));
    CountryListJaxb.findByUUID(ITA_ID).ifPresent(country -> INSTANCE.countryToVignobles.put(country, vignobleListItaly));
    File dir = new File(Program.getWorkDir(true));
    File[] fileVignobles = dir.listFiles((pathname) -> pathname.getName().endsWith(VIGNOBLE));
    List<CountryToUuid> countriesToUuid = Program.getCountries().stream().map(countryJaxb -> new CountryToUuid(countryJaxb.getFilename(), countryJaxb.getUuid())).toList();
    List<String> countryFilenames = countriesToUuid.stream().map(CountryToUuid::filename).toList();
    if (fileVignobles != null) {
      boolean allGood = Arrays.stream(fileVignobles).anyMatch(country -> countryFilenames.contains(country.getName().substring(0, country.getName().indexOf(VIGNOBLE))));
      for (File f : fileVignobles) {
        if (allGood) {
          if (!countryFilenames.contains(f.getName().substring(0, f.getName().indexOf(VIGNOBLE)))) {
            continue;
          }
          loadFileWithUUID(f, countriesToUuid);
        } else {
          loadFileOldFormat(f, countriesToUuid);
        }
      }
      if (allGood) {
        // 2nd attempt for empty country
        for (File f : fileVignobles) {
          if (countryFilenames.contains(f.getName().substring(0, f.getName().indexOf(VIGNOBLE)))) {
            continue;
          }
          loadFileOldFormat(f, countriesToUuid);
        }
      }
    }
    CountryListJaxb.getInstance().getCountries().forEach(country -> {
      INSTANCE.mapCountryIDToUUID.put(country.getId(), country.getUuid());
      INSTANCE.countryToVignobles.put(country, INSTANCE.countryToVignobles.get(country));
    });
    Debug("Loading all countries Done");
    INSTANCE.modified = false;
    setRebuildNeeded();
    validate();
  }

  private static void loadFileWithUUID(File f, List<CountryToUuid> countriesToUuid) {
    String name = f.getName();
    String id = name.substring(0, name.indexOf(VIGNOBLE));
    if (!id.equals(id.toUpperCase())) {
      Debug("Deleting vignoble file with wrong name " + name);
      f.delete();
      return;
    }
    File fText = new File(f.getParent(), id + TEXT);
    List<String> lines = Program.readTextFile(fText);
    String label = lines.isEmpty() ? "" : lines.getFirst();
    UUID uuid = lines.size() < 2 ? null : UUID.fromString(lines.get(1));
    if (uuid == null) {
      Debug("ERROR: No uuid found in " + fText.getAbsolutePath());
    }

    CountryJaxb countryJaxb = null;
    if (uuid != null) {
      countryJaxb = CountryListJaxb.findByUUID(uuid).orElse(null);
      if (countryJaxb == null) {
        Debug("ERROR: Country Jaxb not found for UUID: " + uuid);
      }
      final String countryName = id;
      CountryToUuid found = countriesToUuid.stream().filter(countryToUuid -> countryToUuid.filename().equals(countryName)).findFirst().orElse(null);
      if (found == null || !found.uuid().equals(uuid)) {
        Debug("ERROR: Not the same UUID for the country: " + name);
      }
    }
    // It shouldn't be needed
    if (countryJaxb == null) {
      Debug("ERROR: Searching country by name: " + name);
      countryJaxb = CountryListJaxb.findbyId(name)
          .orElseGet(() -> CountryListJaxb.findByIdOrLabel(label));
    }
    if (countryJaxb == null) {
      uuid = UUID.randomUUID();
      countryJaxb = new CountryJaxb(id, label, uuid);
      CountryListJaxb.add(countryJaxb);
    }
    if (!label.isEmpty() && !label.equals(countryJaxb.getName())) {
      countryJaxb.setName(label);
    }
    if (countryJaxb.getUuid() == null) {
      Debug("ERROR: No uuid in " + countryJaxb);
      countryJaxb.setUuid(UUID.randomUUID());
    }
    if (uuid != null && !countryJaxb.getUuid().equals(uuid)) {
      countryJaxb.setUuid(uuid);
    }
    if (!INSTANCE.countryToVignobles.containsKey(countryJaxb)) {
      INSTANCE.countryToVignobles.put(countryJaxb, load(f));
    } else {
      var loadedUserVignobleListJaxb = load(f);
      if (loadedUserVignobleListJaxb != null) {
        VignobleListJaxb vignobleListJaxb = INSTANCE.countryToVignobles.get(countryJaxb);
        for (var systemCountryVignobleJaxb : vignobleListJaxb.getCountryVignobleJaxbList()) {
          if (!loadedUserVignobleListJaxb.getCountryVignobleJaxbList().contains(systemCountryVignobleJaxb)) {
            // Can't we delete Systen Countries
            loadedUserVignobleListJaxb.getCountryVignobleJaxbList().add(systemCountryVignobleJaxb);
          } else {
            var systemCountryVignobleJaxbFromUser = loadedUserVignobleListJaxb.getCountryVignobleJaxbList().get(loadedUserVignobleListJaxb.getCountryVignobleJaxbList().indexOf(systemCountryVignobleJaxb));
            if (systemCountryVignobleJaxb.getUnmodifiableAppelation() != null) {
              // Add the new system appellation to user !!
              systemCountryVignobleJaxb.getUnmodifiableAppelation().forEach(systemCountryVignobleJaxbFromUser::add);
            } else {
              systemCountryVignobleJaxb.setAppelation(new LinkedList<>());
            }
          }
        }
      }
    }
  }

  private static void loadFileOldFormat(File f, List<CountryToUuid> countriesToUuid) {
    String name = f.getName();
    String id = name.substring(0, name.indexOf(VIGNOBLE));
    if (!id.equals(id.toUpperCase())) {
      Debug("Deleting vignoble file with wrong name " + name);
      f.delete();
      return;
    }
    name = name.substring(0, name.indexOf(VIGNOBLE));
    File fText = new File(f.getParent(), name + TEXT);
    List<String> lines = Program.readTextFile(fText);
    String label = lines.isEmpty() ? "" : lines.getFirst();
    UUID uuid = lines.size() < 2 ? null : UUID.fromString(lines.get(1));

    CountryJaxb countryJaxb = null;
    if (uuid != null) {
      Debug("WARNING: UUID shouldn't be present for " + name);
      countryJaxb = CountryListJaxb.findByUUID(uuid).orElse(null);
      if (countryJaxb == null) {
        Debug("ERROR: Country Jaxb not found for UUID " + uuid);
      }
      final String countryName = name;
      CountryToUuid found = countriesToUuid.stream().filter(countryToUuid -> countryToUuid.filename().equals(countryName)).findFirst().orElse(null);
      if (found == null || !found.uuid().equals(uuid)) {
        Debug("ERROR: Not the same UUID for the country " + name);
      }
    }
    if (countryJaxb == null) {
      countryJaxb = CountryListJaxb.findbyId(name)
          .orElseGet(() -> CountryListJaxb.findByIdOrLabel(label));
    }
    if (countryJaxb == null) {
      uuid = UUID.randomUUID();
      countryJaxb = new CountryJaxb(id, label, uuid);
      CountryListJaxb.add(countryJaxb);
    }
    if (!label.isEmpty() && !label.equals(countryJaxb.getName())) {
      countryJaxb.setName(label);
    }
    if (countryJaxb.getUuid() == null) {
      countryJaxb.setUuid(UUID.randomUUID());
    }
    if (uuid != null && !countryJaxb.getUuid().equals(uuid)) {
      countryJaxb.setUuid(uuid);
    }
    if (!INSTANCE.countryToVignobles.containsKey(countryJaxb)) {
      Debug("WARNING: Old file loaded for for the country " + name + " file " + f.getName());
      INSTANCE.countryToVignobles.put(countryJaxb, load(f));
    }
  }

  private static void validate() {
    INSTANCE.mapCountryIDToUUID.forEach((s, uuid) -> {
      if (uuid == null) throw new IllegalArgumentException("Country ID is null for country " + s);
    });
    INSTANCE.countryToVignobles.forEach((systemCountryVignobleJaxb, countryVignoble) -> {
      if (systemCountryVignobleJaxb.getUuid() == null) {
        throw new IllegalArgumentException("UUID is null for " + systemCountryVignobleJaxb.getId());
      }
      if (countryVignoble != null) {
        countryVignoble.getCountryVignobleJaxbList().forEach(countryVignobleJaxb -> {
          if (countryVignobleJaxb.getUuid() == null) {
            throw new IllegalArgumentException("Country Vignoble Jaxb UUID is null for " + countryVignobleJaxb.getId());
          }
        });
      }
    });
  }

  private static VignobleListJaxb loadFrance() {
    VignobleListJaxb vignobleListJaxb = null;
    if (Program.hasWorkDir()) {
      vignobleListJaxb = loadByUuid(FRA_ID);
    }
    return (vignobleListJaxb != null) ? vignobleListJaxb : VignobleListJaxb.load("resources/vignobles.xml");
  }

  private static VignobleListJaxb loadItaly() {
    VignobleListJaxb vignobleListJaxb = null;
    if (Program.hasWorkDir()) {
      vignobleListJaxb = loadByUuid(ITA_ID);
    }
    return (vignobleListJaxb != null) ? vignobleListJaxb : VignobleListJaxb.load("resources/italie.xml");
  }

  public static void setModified() {
    INSTANCE.modified = true;
    Program.setModified();
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
    INSTANCE.modified = true;
    INSTANCE.countryToVignobles.put(countryJaxb, vignobleListJaxb);
    UUID uuid = INSTANCE.mapCountryIDToUUID.getOrDefault(countryJaxb.getId(), null);
    if (uuid != null) {
      if (!uuid.equals(countryJaxb.getUuid())) {
        Debug("ERROR: the country already exist: [%s - %s] with a different UUID (expected: %s - found: %s)".formatted(countryJaxb.getId(), countryJaxb.getName(), uuid, countryJaxb.getUuid()));
      }
    } else {
      INSTANCE.mapCountryIDToUUID.put(countryJaxb.getId(), countryJaxb.getUuid());
    }
    CountryListJaxb.add(countryJaxb);
    Debug("Creating country Done");
    return Optional.of(vignobleListJaxb);
  }

  public static void deleteCountry(CountryJaxb countryJaxb) {
    Debug("Deleting country... " + countryJaxb.getName());
    INSTANCE.modified = true;
    INSTANCE.countryToVignobles.remove(countryJaxb);
    INSTANCE.mapCountryIDToUUID.remove(countryJaxb.getId());
    boolean resul = VignobleListJaxb.delete(countryJaxb);
    Debug("Deleting country done with success = " + resul);
  }

  @Deprecated
  private static void generateCountryId(CountryJaxb countryJaxb) {
    String id = MyCellarUtils.removeAccents(countryJaxb.getName()).toUpperCase() + "000";
    id = id.substring(0, 3);

    boolean found;
    int i = 1;
    do {
      found = false;
      for (CountryJaxb c : INSTANCE.countryToVignobles.keySet()) {
        if (c.getId().equalsIgnoreCase(id)) {
          id = id.substring(0, 3) + i;
          i++;
          found = true;
        }
      }
    } while (found);
    countryJaxb.setId(id);
  }

  public static void rebuild() {
    if (!rebuildNeeded) {
      return;
    }
    Debug("rebuild...");
    INSTANCE.usedVignoblesIDList.clear();
    INSTANCE.usedVignoblesUUIDList.clear();
    INSTANCE.mapCountryVignobleIDToVignoble.clear();
    INSTANCE.mapCountryVignobleUUIDToVignoble.clear();
    INSTANCE.mapBottleAppellationIDToAppellationID.clear();
    INSTANCE.mapAppellationJaxbUUIDToAppellationUUID.clear();
    List<VignobleJaxb> vignobleJaxbList = Program.getStorage().getAllList()
        .stream()
        .map(Bouteille::getVignoble)
        .filter(Objects::nonNull)
        .toList();

    vignobleJaxbList.forEach(vignobleJaxb -> {
      if (usedVignoblesNotContains(vignobleJaxb)) {
        addUsedVignobles(vignobleJaxb);
        addVignoble(vignobleJaxb);
      }
      createVignobleInMap(vignobleJaxb);
      mapAppellation(vignobleJaxb);
    });

    rebuildNeeded = false;
    Debug("rebuild... Done");
  }

  private static void addUsedVignobles(VignobleJaxb vignobleJaxb) {
    INSTANCE.usedVignoblesIDList.add(vignobleJaxb.getId());
    INSTANCE.usedVignoblesUUIDList.add(vignobleJaxb.getUuid());
  }

  private static boolean usedVignoblesNotContains(VignobleJaxb vignobleJaxb) {
    return !INSTANCE.usedVignoblesIDList.contains(vignobleJaxb.getId()) ||
        !INSTANCE.usedVignoblesUUIDList.contains(vignobleJaxb.getUuid());
  }

  private static boolean usedVignoblesContains(VignobleJaxb vignobleJaxb) {
    return INSTANCE.usedVignoblesIDList.contains(vignobleJaxb.getId()) || INSTANCE.usedVignoblesUUIDList.contains(vignobleJaxb.getUuid());
  }

  private static void mapAppellation(VignobleJaxb vignobleJaxb) {
    CountryListJaxb.findByVignoble(vignobleJaxb)
        .flatMap(CountryVignobleController::getVignobles)
        .flatMap(vignobleListJaxb -> vignobleListJaxb.findAppelation(vignobleJaxb))
        .ifPresent(appelationJaxb -> INSTANCE.mapBottleAppellationIDToAppellationID.put(vignobleJaxb.getId(), appelationJaxb.getId()));
    CountryListJaxb.findByVignoble(vignobleJaxb)
        .flatMap(CountryVignobleController::getVignobles)
        .flatMap(vignobleListJaxb -> vignobleListJaxb.findAppelation(vignobleJaxb))
        .ifPresent(unused -> INSTANCE.mapAppellationJaxbUUIDToAppellationUUID.add(vignobleJaxb.getUuid()));
  }

  public static void createVignobleInMap(final VignobleJaxb vignobleJaxb) {
    CountryListJaxb.findByVignoble(vignobleJaxb).ifPresent(country -> {
      VignobleListJaxb vignobleListJaxb = getVignobles(country)
          .orElseGet(() -> createCountry(country)
              .orElse(null));
      if (vignobleListJaxb == null) {
        Debug("ERROR: createVignobleInMap: Unable to create a VignobleListJaxb!");
        return;
      }
      CountryVignobleJaxb countryVignoble = vignobleListJaxb.findVignobleWithAppelation(vignobleJaxb);
      boolean found = true;
      if (countryVignoble == null) {
        countryVignoble = vignobleListJaxb.findVignoble(vignobleJaxb).orElse(null);
        found = false;
        if (countryVignoble == null) {
          AppelationJaxb appelationJaxb = vignobleListJaxb.addVignoble(vignobleJaxb);
          if (appelationJaxb != null) {
            INSTANCE.modified = true;
          }
        }
        countryVignoble = vignobleListJaxb.findVignoble(vignobleJaxb).orElse(null);
      }
      if (countryVignoble == null) {
        if (!vignobleJaxb.getName().isBlank()) {
          Debug("ERROR: Unable to find VignobleJaxb " + vignobleJaxb);
        }
        return;
      }
      AppelationJaxb appelationJaxb = new AppelationJaxb();
      if (!found) {
        appelationJaxb.setAOC(vignobleJaxb.getAOC());
        appelationJaxb.setIGP(vignobleJaxb.getIGP());
        if (!appelationJaxb.isEmpty()) {
          INSTANCE.modified = true;
          countryVignoble.add(appelationJaxb);
          countryVignoble = vignobleListJaxb.findVignobleWithAppelation(vignobleJaxb);
        }
      }
      if (countryVignoble == null && !appelationJaxb.isEmpty()) {
        Debug("ERROR: Unable to find created VignobleJaxb " + vignobleJaxb);
        return;
      }

      if (countryVignoble != null && !countryVignoble.isEmpty()) {
        putCountryVignobleToVignoble(countryVignoble, vignobleJaxb);
      }
    });
  }

  private static void putCountryVignobleToVignoble(CountryVignobleJaxb countryVignoble, VignobleJaxb vignobleJaxb) {
    INSTANCE.mapCountryVignobleIDToVignoble.put(countryVignoble.getId(), vignobleJaxb);
    INSTANCE.mapCountryVignobleUUIDToVignoble.put(countryVignoble.getUuid(), vignobleJaxb);
  }

  static boolean isVignobleUsed(CountryJaxb countryJaxb, CountryVignobleJaxb countryVignobleJaxb) {
    VignobleJaxb vigne = getVignobleJaxbInMapFrom(countryVignobleJaxb);
    return vigne != null && vigne.getCountry().equalsIgnoreCase(countryJaxb.getId()) && usedVignoblesContains(vigne);
  }

  private static VignobleJaxb getVignobleJaxbInMapFrom(CountryVignobleJaxb countryVignobleJaxb) {
    VignobleJaxb vignobleJaxb = INSTANCE.mapCountryVignobleUUIDToVignoble.get(countryVignobleJaxb.getUuid());
    if (vignobleJaxb == null) {
      vignobleJaxb = INSTANCE.mapCountryVignobleIDToVignoble.get(countryVignobleJaxb.getId());
    }
    return vignobleJaxb;
  }

  static boolean isAppellationUsed(AppelationJaxb appellation) {
    return INSTANCE.mapAppellationJaxbUUIDToAppellationUUID.contains(appellation.getUuid()) || INSTANCE.mapBottleAppellationIDToAppellationID.containsValue(appellation.getId());
  }

  public static boolean isVignobleUsed(VignobleJaxb vignoble) {
    return INSTANCE.usedVignoblesUUIDList.contains(vignoble.getUuid());
  }

  static void renameVignoble(final CountryVignobleJaxb countryVignobleJaxb, final String name) {
    VignobleJaxb bouteilleVignobleJaxb = getVignobleJaxbInMapFrom(countryVignobleJaxb);
    final String oldName = countryVignobleJaxb.getName();
    countryVignobleJaxb.setName(name);
    INSTANCE.modified = true;
    if (bouteilleVignobleJaxb == null) {
      Debug("WARNING: No bottles to modify with Vignoble name: " + oldName);
      return;
    }
    if (usedVignoblesContains(bouteilleVignobleJaxb)) {
      List<Bouteille> list = Program.getStorage().getAllList();
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
    VignobleJaxb vigne = getVignobleJaxbInMapFrom(countryVignobleJaxb);
    final String oldName = appelationJaxb.getAOC();
    appelationJaxb.setAOC(name);
    INSTANCE.modified = true;
    if (vigne == null) {
      Debug("WARNING: No bottles to modify with AOC name: " + oldName);
      return;
    }
    final VignobleJaxb vignobleJaxb = vigne;
    if (usedVignoblesContains(vigne)) {
      List<? extends IMyCellarObject> list = Program.getStorage().getAllList();
      list.stream()
          .map(myCellarObject -> (Bouteille) myCellarObject)
          .map(Bouteille::getVignoble)
          .filter(Objects::nonNull)
          .filter(vignoble -> vignoble.getId() == vignobleJaxb.getId() || vignoble.getUuid() == vignobleJaxb.getUuid() || vignoble.equals(vignobleJaxb))
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
    VignobleJaxb vigne = getVignobleJaxbInMapFrom(countryVignobleJaxb);
    final String oldName = appelationJaxb.getIGP();
    appelationJaxb.setIGP(name);
    INSTANCE.modified = true;
    if (vigne == null) {
      Debug("WARNING: No bottles to modify with IGP name: " + oldName);
      return;
    }
    final VignobleJaxb vignobleJaxb = vigne;
    if (usedVignoblesContains(vigne)) {
      List<? extends IMyCellarObject> list = Program.getStorage().getAllList();
      list.stream()
          .map(myCellarObject -> (Bouteille) myCellarObject)
          .map(Bouteille::getVignoble)
          .filter(Objects::nonNull)
          .filter(vignoble -> vignoble.equals(vignobleJaxb))
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

  private static AppelationJaxb addVignoble(final VignobleJaxb bouteilleVignobleJaxb) {
    if (VignobleJaxb.isEmpty(bouteilleVignobleJaxb) && bouteilleVignobleJaxb.getCountryUuid() == null) {
      return null;
    }
    AppelationJaxb appelationJaxb = null;
    CountryJaxb countryJaxb = CountryListJaxb.findByUUID(bouteilleVignobleJaxb.getCountryUuid()).orElse(null);
    if (countryJaxb == null) {
      countryJaxb = CountryListJaxb.findByIdOrLabel(bouteilleVignobleJaxb.getCountry());
    }
    if (countryJaxb != null) {
      if (getVignobles(countryJaxb).isEmpty()) {
        createCountry(countryJaxb);
      }
      final VignobleListJaxb vignobleListJaxb = getVignobles(countryJaxb).orElse(null);
      if (vignobleListJaxb == null) {
        Debug("ERROR: addVignoble: Unable to find vignobles for country " + countryJaxb);
        return null;
      }
      CountryVignobleJaxb countryVignoble = vignobleListJaxb.findVignobleWithAppelation(bouteilleVignobleJaxb);
      if (countryVignoble == null) {
        countryVignoble = vignobleListJaxb.findVignoble(bouteilleVignobleJaxb).orElse(null);
        if (countryVignoble != null && !bouteilleVignobleJaxb.isAppellationEmpty()) {
          appelationJaxb = new AppelationJaxb();
          appelationJaxb.setAOC(bouteilleVignobleJaxb.getAOC());
          appelationJaxb.setIGP(bouteilleVignobleJaxb.getIGP());
          countryVignoble.add(appelationJaxb);
          INSTANCE.modified = true;
        } else if (countryVignoble == null) {
          appelationJaxb = vignobleListJaxb.addVignoble(bouteilleVignobleJaxb);
          if (appelationJaxb != null) {
            INSTANCE.modified = true;
          }
        }
      } else {
        appelationJaxb = vignobleListJaxb.findAppelation(bouteilleVignobleJaxb).orElse(null);
        if (appelationJaxb != null) {
          bouteilleVignobleJaxb.setValues(appelationJaxb, countryJaxb);
        }
        putCountryVignobleToVignoble(countryVignoble, bouteilleVignobleJaxb);
      }
    } else {
      INSTANCE.modified = true;
      countryJaxb = new CountryJaxb(bouteilleVignobleJaxb.getCountry());
      generateCountryId(countryJaxb);
      VignobleListJaxb vignobleListJaxb = new VignobleListJaxb();
      appelationJaxb = vignobleListJaxb.addVignoble(bouteilleVignobleJaxb);
      CountryListJaxb.add(countryJaxb);
      INSTANCE.countryToVignobles.put(countryJaxb, vignobleListJaxb);
    }
    if (usedVignoblesNotContains(bouteilleVignobleJaxb)) {
      addUsedVignobles(bouteilleVignobleJaxb);
    }
    return appelationJaxb;
  }

  public static void findOrAddVignobleFromBottle(final Bouteille wine) {
    Debug("findOrAddVignobleFromBottle...");
    AppelationJaxb appelationJaxb = addVignoble(wine.getVignoble());
    if (appelationJaxb != null && wine.getVignoble() != null) {
      CountryJaxb countryJaxb = CountryListJaxb.findByVignoble(wine.getVignoble()).orElse(null);
      wine.getVignoble().setValues(appelationJaxb, countryJaxb);
    }
    setRebuildNeeded();
    Debug("findOrAddVignobleFromBottle... Done");
  }

  private static VignobleListJaxb load(File file) {
    Debug("Loading JAXB File " + file.getAbsolutePath());
    if (!file.exists()) {
      return null;
    }
    VignobleListJaxb vignobleListJaxb;
    try {
      JAXBContext jc = JAXBContext.newInstance(VignobleListJaxb.class);
      Unmarshaller u = jc.createUnmarshaller();
      vignobleListJaxb = (VignobleListJaxb) u.unmarshal(new FileInputStream(file));
    } catch (FileNotFoundException | JAXBException e) {
      Program.showException(e);
      return null;
    }
    vignobleListJaxb.checkAvailability();
    Collections.sort(vignobleListJaxb.getCountryVignobleJaxbList());
    for (CountryVignobleJaxb vignoble : vignobleListJaxb.getCountryVignobleJaxbList()) {
      vignoble.checkAvaibility();
      for (AppelationJaxb appelationJaxb : vignoble.getUnmodifiableAppelation()) {
        appelationJaxb.makeItClean();
      }
      vignoble.makeItClean();
    }
    vignobleListJaxb.setCountryVignobleJaxbList(vignobleListJaxb.getCountryVignobleJaxbList().stream()
        .filter(Predicate.not(CountryVignobleJaxb::isEmpty))
        .collect(Collectors.toList()));
    Debug("Loading JAXB File Done");
    return vignobleListJaxb;
  }

  private static VignobleListJaxb loadByUuid(UUID uuid) {
    final CountryJaxb countryJaxb = CountryListJaxb.findByUUID(uuid).orElse(null);
    if (countryJaxb != null) {
      File f = new File(Program.getWorkDir(true), countryJaxb.getId() + VIGNOBLE);
      if (f.exists()) {
        return load(f);
      }
    }
    return null;
  }

  public static void save() {
    if (INSTANCE.modified) {
      Debug("Saving...");
      for (CountryJaxb c : INSTANCE.countryToVignobles.keySet()) {
        VignobleListJaxb.save(c, INSTANCE.countryToVignobles.get(c));
      }
      Debug("Saved");
      INSTANCE.modified = false;
    }
  }

  static boolean hasCountryWithName(final String country) {
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

  public static TempCountry getUUIDFromCountry(String country) {
    if (ProgramConstants.FR.equals(country)) {
      country = FRA;
    }
    UUID uuid = INSTANCE.mapCountryIDToUUID.getOrDefault(country, null);
    var tempCountry = new TempCountry(uuid, country);
    if (uuid == null) {
      CountryJaxb countryJaxb = new CountryJaxb(country);
      createCountry(countryJaxb);
      tempCountry = new TempCountry(countryJaxb.getUuid(), countryJaxb.getId());
      uuid = INSTANCE.mapCountryIDToUUID.getOrDefault(countryJaxb.getId(), null);
      if (uuid == null) {
        throw new IllegalArgumentException("Country ID " + country + " not found");
      }
    }
    return tempCountry;
  }

  public record TempCountry(UUID uuid, String countryId) {
  }

  private static void Debug(String text) {
    Program.Debug("CountryVignobleController: " + text);
  }
}
