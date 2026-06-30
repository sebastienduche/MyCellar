package mycellar.core.datas.jaxb;

import mycellar.MyCellarUtils;
import mycellar.Program;
import mycellar.ProgramConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static mycellar.Program.COUNTRY_LIST;
import static mycellar.Program.NO_COUNTRY;
import static mycellar.ProgramConstants.COUNTRIES_XML;
import static mycellar.ProgramConstants.FR;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 30/06/26
 */

@XmlRootElement(name = "countries")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryListJaxb {
  private static CountryListJaxb instance;
  @XmlElement(name = "country")
  private List<CountryJaxb> countries = null;

  public static CountryListJaxb getInstance() {
    if (instance == null) {
      instance = load();
    }
    return instance;
  }

  public static void init() {
    instance = load();
    if (instance != null) {
      Collections.sort(instance.getCountries());
    }
  }

  public static void close() {
    instance = new CountryListJaxb();
    instance.setCountries(new ArrayList<>());
  }

  private static CountryListJaxb load() {
    File f = null;
    if (Program.hasOpenedFile()) {
      f = new File(Program.getWorkDir(true), COUNTRIES_XML);
    }
    CountryListJaxb countryListJaxb = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(CountryListJaxb.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      if (f != null && f.exists()) {
        Debug("Loading countries file: " + f.getAbsolutePath());
        countryListJaxb = (CountryListJaxb) jaxbUnmarshaller.unmarshal(f);
        // If the uuid are null, we fill it with existing ones
        populateUUID(countryListJaxb);
      } else {
        return COUNTRY_LIST;
      }
    } catch (JAXBException | RuntimeException e) {
      Program.showException(e);
    }
    return countryListJaxb;
  }

  private static void populateUUID(CountryListJaxb countryListJaxb) {
    Debug("Populating UUID for CountryListJaxb");
    for (CountryJaxb country : countryListJaxb.getCountries()) {
      if (country.getUuid() == null) {
        Program.COUNTRY_LIST.getCountries()
            .stream()
            .filter(countryJaxb -> countryJaxb.getId().equals(country.getId())).findFirst()
            .ifPresent(countryJaxb -> country.setUuid(countryJaxb.getUuid()));
      }
      if (country.getUuid() == null) {
        country.setUuid(UUID.randomUUID());
      }
    }
  }

  public static boolean save() {
    if (instance == null) {
      return false;
    }
    Debug("Writing Countries File: " + COUNTRIES_XML);
    File f = new File(Program.getWorkDir(true), COUNTRIES_XML);
    try {
      JAXBContext jc = JAXBContext.newInstance(CountryListJaxb.class);
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(instance, new StreamResult(f));
    } catch (JAXBException e) {
      Program.showException(e);
      return false;
    }
    Debug("Writing Countries File Done");
    return true;
  }

  public static CountryListJaxb loadResourceFile() {
    CountryListJaxb countryListJaxb = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(CountryListJaxb.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      Debug("Loading countries resource file");
      URL url = CountryListJaxb.class.getClassLoader().getResource("resources/" + COUNTRIES_XML);
      if (url == null) {
        Debug("ERROR: Countries: Missing resource " + COUNTRIES_XML);
        countryListJaxb = new CountryListJaxb();
        countryListJaxb.setCountries(new ArrayList<>());
        return countryListJaxb;
      }
      countryListJaxb = (CountryListJaxb) jaxbUnmarshaller.unmarshal(url);
      Debug("Loading countries file Done");
    } catch (JAXBException | RuntimeException e) {
      Program.showException(e);
    }
    return countryListJaxb;
  }

  public static Optional<CountryJaxb> findByVignoble(VignobleJaxb vignoble) {
    if (vignoble.isAppellationEmpty() && MyCellarUtils.isNullOrEmpty(vignoble.getCountry())) {
      return Optional.empty();
    }
    CountryJaxb countryJaxb1 = findbyId(vignoble.getCountry()).orElse(null);
    CountryJaxb countryJaxb2 = null;
    if (vignoble.getCountryUuid() != null) {
      countryJaxb2 = findByUUID(vignoble.getCountryUuid()).orElse(null);
    }
    if (countryJaxb1 != null && countryJaxb2 != null) {
      if (countryJaxb1.getUuid().equals(countryJaxb2.getUuid())) {
        return Optional.of(countryJaxb2);
      } else {
        Debug("ERROR: findByVignoble: unexpected country - uuid: [%s - %s] vs [%s - %s]".formatted(countryJaxb1.getName(), countryJaxb1.getUuid(), countryJaxb2.getName(), countryJaxb2.getUuid()));
      }
    } else if (countryJaxb1 != null || countryJaxb2 != null) {
      Debug("ERROR: findByVignoble: " + countryJaxb1 + " vs " + countryJaxb2);
    }
    // TODO this should be removed
    return findbyId(vignoble.getCountry());
  }

  @Deprecated
  public static Optional<CountryJaxb> findbyId(String id) {
    if (FR.equals(id)) {
      id = ProgramConstants.FRA;
    }
    String finalId = id;
    return getInstance().getCountries()
        .stream()
        .filter(country -> country.getId().equals(finalId))
        .findFirst();
  }

  public static Optional<CountryJaxb> findByUUID(UUID uuid) {
    if (Objects.equals(uuid, NO_COUNTRY.getUuid())) {
      return Optional.of(NO_COUNTRY);
    }
    return getInstance().getCountries()
        .stream()
        .filter(country -> country.getUuid().equals(uuid))
        .findFirst();
  }

  @Deprecated
  public static Optional<CountryJaxb> findByLabel(String label) {
    return getInstance().getCountries()
        .stream()
        .filter(country -> country.getLabel().equals(label))
        .findFirst();
  }

  @Deprecated
  public static CountryJaxb findByIdOrLabel(String label) {
    return findbyId(label).orElseGet(() -> findByLabel(label).orElse(null));
  }

  public static void add(CountryJaxb countryJaxb) {
    getInstance().getCountries().add(countryJaxb);
  }

  public List<CountryJaxb> getCountries() {
    return countries;
  }

  private void setCountries(List<CountryJaxb> countries) {
    this.countries = countries;
  }

  private static void Debug(String s) {
    Program.Debug("CountryListJaxb: " + s);
  }

}
