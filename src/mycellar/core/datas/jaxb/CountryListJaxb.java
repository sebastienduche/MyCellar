package mycellar.core.datas.jaxb;

import mycellar.Program;

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
import java.util.Optional;

import static mycellar.Program.COUNTRIES_XML;
import static mycellar.Program.FR;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 1.1
 * @since 03/12/20
 */

@XmlRootElement(name = "countries")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryListJaxb {
  private static CountryListJaxb instance = load();
  @XmlElement(name = "country")
  private List<CountryJaxb> countries = null;

  public static CountryListJaxb getInstance() {
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
    if (Program.hasFile()) {
      f = new File(Program.getWorkDir(true), COUNTRIES_XML);
    }
    CountryListJaxb countryListJaxb = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(CountryListJaxb.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      if (f != null && f.exists()) {
        Debug("Loading countries file: " + f.getAbsolutePath());
        countryListJaxb = (CountryListJaxb) jaxbUnmarshaller.unmarshal(f);
      } else {
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
      }
    } catch (JAXBException | RuntimeException e) {
      Program.showException(e);
    }
    return countryListJaxb;
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

  private static void Debug(String s) {
    Program.Debug("CountryListJaxb: " + s);
  }

  public static Optional<CountryJaxb> findbyId(String id) {
    if (FR.equals(id)) {
      return Optional.of(Program.FRANCE);
    }
    return getInstance().getCountries()
        .stream()
        .filter(country -> country.getId().equals(id))
        .findFirst();
  }

  private static Optional<CountryJaxb> findByLabel(String label) {
    return getInstance().getCountries()
        .stream()
        .filter(country -> country.getLabel().equals(label))
        .findFirst();
  }

  public static CountryJaxb findByIdOrLabel(String label) {
    return findbyId(label).orElseGet(() -> findByLabel(label).orElse(null));
  }

  public static void add(CountryJaxb countryJaxb) {
    if (findbyId(countryJaxb.getId()).isPresent()) {
      return;
    }
    getInstance().getCountries().add(countryJaxb);
  }

  public List<CountryJaxb> getCountries() {
    return countries;
  }

  private void setCountries(List<CountryJaxb> countries) {
    this.countries = countries;
  }

}
