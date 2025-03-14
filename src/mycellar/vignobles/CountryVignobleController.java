package mycellar.vignobles;

import mycellar.Bouteille;
import mycellar.MyCellarUtils;
import mycellar.Program;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static mycellar.ProgramConstants.FRA;
import static mycellar.ProgramConstants.ITA;
import static mycellar.ProgramConstants.TEXT;
import static mycellar.core.datas.jaxb.VignobleListJaxb.VIGNOBLE;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.1
 * @since 14/03/25
 */

public final class CountryVignobleController {

  private static final CountryVignobleController INSTANCE = new CountryVignobleController();
  private static boolean rebuildNeeded = false;
  private final Map<CountryJaxb, VignobleListJaxb> countryToVignobles = new HashMap<>();
  private final Map<Long, VignobleJaxb> mapCountryVignobleIDToVignoble = new HashMap<>();
  private final Map<Long, Long> mapBottleAppellationIDToAppellationID = new HashMap<>(); // For Appellation Used
  private final List<Long> usedVignoblesIDList = new LinkedList<>();
  private boolean modified;

  private CountryVignobleController() {
    modified = false;
    CountryListJaxb.findbyId(FRA).ifPresent(country -> countryToVignobles.put(country, loadFrance()));
    CountryListJaxb.findbyId(ITA).ifPresent(country -> countryToVignobles.put(country, loadItaly()));
    setRebuildNeeded();
  }

  public static void init() {
    INSTANCE.modified = false;
    INSTANCE.countryToVignobles.clear();
    CountryListJaxb.findbyId(FRA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, loadFrance()));
    CountryListJaxb.findbyId(ITA).ifPresent(country -> INSTANCE.countryToVignobles.put(country, loadItaly()));
    setRebuildNeeded();
  }

  public static void close() {
    INSTANCE.modified = false;
    INSTANCE.countryToVignobles.clear();
    setRebuildNeeded();
  }

  public static void load() {
    loadAllCountries(INSTANCE.countryToVignobles);
    INSTANCE.modified = false;
    setRebuildNeeded();
    rebuild();
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
    vignobleListJaxb.init();
    INSTANCE.modified = true;
    INSTANCE.countryToVignobles.put(countryJaxb, vignobleListJaxb);
    Debug("Creating country Done");
    return Optional.of(vignobleListJaxb);
  }

  public static void deleteCountry(CountryJaxb countryJaxb) {
    Debug("Deleting country... " + countryJaxb.getName());
    INSTANCE.modified = true;
    INSTANCE.countryToVignobles.remove(countryJaxb);
    boolean resul = VignobleListJaxb.delete(countryJaxb);
    Debug("Deleting country done with resul = " + resul);
  }

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
    INSTANCE.mapCountryVignobleIDToVignoble.clear();
    INSTANCE.mapBottleAppellationIDToAppellationID.clear();
    List<VignobleJaxb> vignobleJaxbList = Program.getStorage().getAllList()
        .stream()
        .map(myCellarObject -> (Bouteille) myCellarObject)
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
    Debug("rebuild... Done");
  }

  private static void mapAppellation(VignobleJaxb vignobleJaxb) {
    CountryListJaxb.findbyId(vignobleJaxb.getCountry())
        .flatMap(CountryVignobleController::getVignobles)
        .flatMap(vignobleListJaxb -> vignobleListJaxb.findAppelation(vignobleJaxb))
        .ifPresent(appelationJaxb -> INSTANCE.mapBottleAppellationIDToAppellationID.put(vignobleJaxb.getId(), appelationJaxb.getId()));
  }

  public static void createVignobleInMap(final VignobleJaxb vignobleJaxb) {
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
          if (vignobleListJaxb.addVignoble(vignobleJaxb)) {
            INSTANCE.modified = true;
          }
        }
        countryVignoble = vignobleListJaxb.findVignoble(vignobleJaxb);
      }
      if (countryVignoble.isEmpty()) {
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
    INSTANCE.modified = true;
    if (bouteilleVignobleJaxb == null) {
      Debug("WARNING: No bottles to modify with Vignoble name: " + oldName);
      return;
    }
    if (INSTANCE.usedVignoblesIDList.contains(bouteilleVignobleJaxb.getId())) {
      List<? extends IMyCellarObject> list = Program.getStorage().getAllList();
      for (IMyCellarObject b : list) {
        VignobleJaxb v = ((Bouteille) b).getVignoble();
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
    INSTANCE.modified = true;
    if (vigne == null) {
      Debug("WARNING: No bottles to modify with AOC name: " + oldName);
      return;
    }
    if (INSTANCE.usedVignoblesIDList.contains(vigne.getId())) {
      List<? extends IMyCellarObject> list = Program.getStorage().getAllList();
      list.stream()
          .map(myCellarObject -> (Bouteille) myCellarObject)
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
    INSTANCE.modified = true;
    if (vigne == null) {
      Debug("WARNING: No bottles to modify with IGP name: " + oldName);
      return;
    }
    if (INSTANCE.usedVignoblesIDList.contains(vigne.getId())) {
      List<? extends IMyCellarObject> list = Program.getStorage().getAllList();
      list.stream()
          .map(myCellarObject -> (Bouteille) myCellarObject)
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
          appelationJaxb.setIGP(bouteilleVignobleJaxb.getIGP());
          vignoble.get().add(appelationJaxb);
          INSTANCE.modified = true;
        } else if (vignoble.isEmpty()) {
          if (vignobleListJaxb.addVignoble(bouteilleVignobleJaxb)) {
            INSTANCE.modified = true;
          }
        }
      } else {
        vignobleListJaxb.findAppelation(bouteilleVignobleJaxb)
            .ifPresent(bouteilleVignobleJaxb::setValues);
        INSTANCE.mapCountryVignobleIDToVignoble.put(countryVignoble.get().getId(), bouteilleVignobleJaxb);
      }
    } else {
      INSTANCE.modified = true;
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
    Debug("addVignobleFromBottle... Done");
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

  public static void loadAllCountries(Map<CountryJaxb, VignobleListJaxb> map) {
    Debug("Loading all countries");
    map.clear();
    File dir = new File(Program.getWorkDir(true));
    CountryListJaxb.findbyId(FRA).ifPresent(country -> map.put(country, loadFrance()));
    CountryListJaxb.findbyId(ITA).ifPresent(country -> map.put(country, loadItaly()));
    File[] fileVignobles = dir.listFiles((pathname) -> pathname.getName().endsWith(VIGNOBLE));
    if (fileVignobles != null) {
      for (File f : fileVignobles) {
        String name = f.getName();
        String id = name.substring(0, name.indexOf(VIGNOBLE));
        if (!id.equals(id.toUpperCase())) {
          Debug("Deleting vignoble file with wrong name " + name);
          f.delete();
          continue;
        }
        name = name.substring(0, name.indexOf(VIGNOBLE));
        File fText = new File(f.getParent(), name + TEXT);
        String label = Program.readFirstLineText(fText);

        CountryJaxb countryJaxb = CountryListJaxb.findbyId(name)
            .orElseGet(() -> CountryListJaxb.findByIdOrLabel(label));
        if (countryJaxb == null) {
          countryJaxb = new CountryJaxb(id, label);
          CountryListJaxb.add(countryJaxb);
        }
        if (!label.isEmpty()) {
          countryJaxb.setName(label);
        }
        if (!map.containsKey(countryJaxb)) {
          map.put(countryJaxb, load(f));
        } else {
          VignobleListJaxb loadedVignobleListJaxb = load(f);
          if (loadedVignobleListJaxb != null) {
            VignobleListJaxb vignobleListJaxb = map.get(countryJaxb);
            for (CountryVignobleJaxb vignoble : vignobleListJaxb.getCountryVignobleJaxbList()) {
              if (!loadedVignobleListJaxb.getCountryVignobleJaxbList().contains(vignoble)) {
                loadedVignobleListJaxb.getCountryVignobleJaxbList().add(vignoble);
              } else {
                CountryVignobleJaxb countryVignobleJaxb = loadedVignobleListJaxb.getCountryVignobleJaxbList().get(loadedVignobleListJaxb.getCountryVignobleJaxbList().indexOf(vignoble));
                if (vignoble.getUnmodifiableAppelation() != null) {
                  vignoble.getUnmodifiableAppelation().forEach(countryVignobleJaxb::add);
                } else {
                  vignoble.setAppelation(new LinkedList<>());
                }
              }
            }
          }
        }
      }
    }
    Debug("Loading all countries Done");
  }

  public static VignobleListJaxb loadFrance() {
    VignobleListJaxb vignobleListJaxb = null;
    if (Program.hasWorkDir()) {
      vignobleListJaxb = loadById(FRA);
    }
    return (vignobleListJaxb != null) ? vignobleListJaxb : VignobleListJaxb.load("resources/vignobles.xml");
  }

  public static VignobleListJaxb loadItaly() {
    VignobleListJaxb vignobleListJaxb = null;
    if (Program.hasWorkDir()) {
      vignobleListJaxb = loadById(ITA);
    }
    return (vignobleListJaxb != null) ? vignobleListJaxb : VignobleListJaxb.load("resources/italie.xml");
  }

  private static VignobleListJaxb loadById(String id) {
    final CountryJaxb countryJaxb = CountryListJaxb.findbyId(id).orElse(null);
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

  private static void Debug(String text) {
    Program.Debug("CountryVignobleController: " + text);
  }
}
