package mycellar.core.datas.jaxb;

import mycellar.Program;
import mycellar.core.IdGenerator;
import mycellar.vignobles.CountryVignobleController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static mycellar.ProgramConstants.TEXT;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.7
 * @since 14/03/25
 */

@XmlRootElement(name = "vignobles")
@XmlAccessorType(XmlAccessType.FIELD)
public class VignobleListJaxb {
  public static final String VIGNOBLE = ".vignoble";

  @XmlElement(name = "vignoble")
  private List<CountryVignobleJaxb> countryVignobleJaxbList = null;

  private long id;

  public VignobleListJaxb() {
    id = IdGenerator.generateID();
  }

  public static VignobleListJaxb load(final String ressource) {
    Debug("Loading vignoble from resource: " + ressource);
    VignobleListJaxb vignobleListJaxb = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(VignobleListJaxb.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      URL stream = VignobleListJaxb.class.getClassLoader().getResource(ressource);
      if (stream == null) {
        Debug("ERROR: Vignobles: Missing resource " + ressource);
        return null;
      }
      vignobleListJaxb = (VignobleListJaxb) jaxbUnmarshaller.unmarshal(stream);
    } catch (JAXBException e) {
      Program.showException(e);
    }

    if (vignobleListJaxb != null) {
      Collections.sort(vignobleListJaxb.countryVignobleJaxbList);
    }
    Debug("Loading vignoble Done");
    return vignobleListJaxb;
  }

  public static boolean save(final CountryJaxb countryJaxb, final VignobleListJaxb vignobleListJaxb) {
    final String countryId = countryJaxb.getId();
    Debug("Writing Country File: " + countryId);
    File fText = new File(Program.getWorkDir(true), countryId + TEXT);
    try (FileWriter writer = new FileWriter(fText);
         BufferedWriter buffer = new BufferedWriter(writer)) {
      buffer.write(countryJaxb.getName());
    } catch (IOException e) {
      Program.showException(e);
      return false;
    }
    File f = new File(Program.getWorkDir(true), countryId + VIGNOBLE);
    try {
      JAXBContext jc = JAXBContext.newInstance(VignobleListJaxb.class);
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(vignobleListJaxb, new StreamResult(f));
    } catch (JAXBException e) {
      Program.showException(e);
      return false;
    }
    Debug("Writing Country File Done");
    return true;
  }

  public static boolean delete(CountryJaxb countryJaxb) {
    final String id = countryJaxb.getId();
    Debug("Deleting Country File: " + id);
    try {
      File fText = new File(Program.getWorkDir(true), id + TEXT);
      Debug("Deleting " + fText.getAbsolutePath());
      fText.delete();
      File f = new File(Program.getWorkDir(true), id + VIGNOBLE);
      Debug("Deleting " + f.getAbsolutePath());
      return f.delete();
    } catch (RuntimeException e) {
      Program.showException(e);
      return false;
    }
  }

  private static void Debug(String sText) {
    Program.Debug("VignobleListJaxb: " + sText);
  }

  public List<CountryVignobleJaxb> getCountryVignobleJaxbList() {
    return countryVignobleJaxbList;
  }

  public void setCountryVignobleJaxbList(List<CountryVignobleJaxb> list) {
    countryVignobleJaxbList = list;
  }

  public void init() {
    countryVignobleJaxbList = new ArrayList<>();
    id = IdGenerator.generateID();
  }

  public void checkAvailability() {
    if (countryVignobleJaxbList == null) {
      countryVignobleJaxbList = new ArrayList<>();
    }
  }

  public Optional<CountryVignobleJaxb> findVignoble(final VignobleJaxb vignobleJaxb) {
    return countryVignobleJaxbList.stream().filter(countryVignoble -> vignobleJaxb.getName().equals(countryVignoble.getName())).findFirst();
  }

  public Optional<CountryVignobleJaxb> findVignobleWithAppelation(final VignobleJaxb vignobleJaxb) {
    final Optional<CountryVignobleJaxb> vignobleToReturn = findVignoble(vignobleJaxb);
    if (vignobleToReturn.isPresent()) {
      final AppelationJaxb appelationJaxb = new AppelationJaxb();
      appelationJaxb.setAOC(vignobleJaxb.getAOC());
      appelationJaxb.setIGP(vignobleJaxb.getIGP());
      if (appelationJaxb.isEmpty()) {
        return Optional.empty();
      }
      if (vignobleToReturn.get().getUnmodifiableAppelation().contains(appelationJaxb)) {
        return vignobleToReturn;
      }
    }
    if (!vignobleJaxb.getName().isBlank()) {
      Debug("ERROR findVignobleWithAppelation " + vignobleJaxb);
    } else if (!vignobleJaxb.getAOC().isBlank() || !vignobleJaxb.getIGP().isBlank()) {
      Debug("WARNING findVignobleWithAppelation " + vignobleJaxb);
    }
    return Optional.empty();
  }

  public Optional<AppelationJaxb> findAppelation(final VignobleJaxb vignobleJaxb) {
    CountryVignobleJaxb vigne = new CountryVignobleJaxb();
    vigne.setName(vignobleJaxb.getName());
    final AppelationJaxb appelationJaxbToFind = new AppelationJaxb();
    appelationJaxbToFind.setAOC(vignobleJaxb.getAOC());
    appelationJaxbToFind.setIGP(vignobleJaxb.getIGP());
    if (appelationJaxbToFind.isEmpty()) {
      return Optional.empty();
    }

    int index = countryVignobleJaxbList.indexOf(vigne);
    if (index != -1) {
      final CountryVignobleJaxb countryVignobleJaxb = countryVignobleJaxbList.get(index);

      final int index1 = countryVignobleJaxb.getUnmodifiableAppelation().indexOf(appelationJaxbToFind);
      if (index1 != -1) {
        return Optional.of(countryVignobleJaxb.getUnmodifiableAppelation().get(index1));
      }
    }
    Debug("ERROR findAppelation " + vignobleJaxb);
    return Optional.empty();
  }

  public boolean addVignoble(final VignobleJaxb vignobleJaxb) {
    Debug("Add Vignoble " + vignobleJaxb);
    CountryVignobleJaxb vigne = new CountryVignobleJaxb();
    vigne.setName(vignobleJaxb.getName());
    AppelationJaxb appelationJaxb = new AppelationJaxb();
    appelationJaxb.setAOC(vignobleJaxb.getAOC());
    appelationJaxb.setIGP(vignobleJaxb.getIGP());
    if (vigne.getName().isBlank() && appelationJaxb.isEmpty()) {
      Debug("Add Vignoble cancelled");
      return false;
    }
    LinkedList<AppelationJaxb> list = new LinkedList<>();
    list.add(appelationJaxb);
    vigne.setAppelation(list);
    countryVignobleJaxbList.add(vigne);
    CountryVignobleController.createVignobleInMap(vignobleJaxb);
    Collections.sort(countryVignobleJaxbList);
    Debug("Add vignoble Done");
    return true;
  }

  public CountryVignobleJaxb addVignoble(final String name) {
    Debug("Adding vignoble with name " + name);
    CountryVignobleJaxb vigne = new CountryVignobleJaxb();
    vigne.setName(name);
    vigne.setAppelation(new LinkedList<>());
    countryVignobleJaxbList.add(vigne);
    Collections.sort(countryVignobleJaxbList);
    Debug("Adding vignoble Done");
    return vigne;
  }

  public void delVignoble(final CountryVignobleJaxb vigne) {
    countryVignobleJaxbList.remove(vigne);
  }

}
