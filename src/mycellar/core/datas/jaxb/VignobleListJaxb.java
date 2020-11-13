package mycellar.core.datas.jaxb;

import mycellar.Program;
import mycellar.vignobles.CountryVignobleController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 2.2
 * @since 13/11/20
 */

@XmlRootElement(name = "vignobles")
@XmlAccessorType (XmlAccessType.FIELD)
public class VignobleListJaxb
{
	private static final String VIGNOBLE = ".vignoble";
	private static final String TEXT = ".txt";

	@XmlElement(name = "vignoble")
	private List<CountryVignobleJaxb> countryVignobleJaxbList = null;

	private long id;

	public VignobleListJaxb() {
		id = Program.generateID();
	}

	public List<CountryVignobleJaxb> getCountryVignobleJaxbList() {
		return countryVignobleJaxbList;
	}

	public void init() {
		countryVignobleJaxbList = new ArrayList<>();
		id = Program.generateID();
	}

	private void checkAvaibility() {
		if (countryVignobleJaxbList == null) {
			countryVignobleJaxbList = new ArrayList<>();
		}
	}

	public static VignobleListJaxb loadFrance() {
		VignobleListJaxb vignobleListJaxb = null;
		if (Program.hasWorkDir()) {
			vignobleListJaxb = loadById("FRA");
		}
		return (vignobleListJaxb != null) ? vignobleListJaxb : load("resources/vignobles.xml");
	}

	public static VignobleListJaxb loadItalie() {
		VignobleListJaxb vignobleListJaxb = null;
		if (Program.hasWorkDir()) {
			vignobleListJaxb = loadById("ITA");
		}
		return (vignobleListJaxb != null) ? vignobleListJaxb : load("resources/italie.xml");
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

	private static VignobleListJaxb load(File file) {
		Debug("Loading JAXB File " + file.getAbsolutePath());
		if (!file.exists()) {
			return null;
		}
		VignobleListJaxb vignobleListJaxb;
		try {
			JAXBContext jc = JAXBContext.newInstance(VignobleListJaxb.class);
			Unmarshaller u = jc.createUnmarshaller();
			vignobleListJaxb = (VignobleListJaxb)u.unmarshal(new FileInputStream(file));
		} catch (Exception e) {
			Program.showException(e);
			return null;
		}
		vignobleListJaxb.checkAvaibility();
		Collections.sort(vignobleListJaxb.countryVignobleJaxbList);
		for (CountryVignobleJaxb vignoble: vignobleListJaxb.countryVignobleJaxbList) {
			vignoble.checkAvaibility();
			for (AppelationJaxb appelationJaxb : vignoble.getUnmodifiableAppelation()) {
				appelationJaxb.makeItClean();
			}
			vignoble.makeItClean();
		}
		vignobleListJaxb.countryVignobleJaxbList = vignobleListJaxb.countryVignobleJaxbList.stream()
				.filter(Predicate.not(CountryVignobleJaxb::isEmpty))
				.collect(Collectors.toList());
		Debug("Loading JAXB File Done");
		return vignobleListJaxb;
	}

	public static void loadAllCountries(Map<CountryJaxb, VignobleListJaxb> map) {
		Debug("Loading all countries");
		map.clear();
		File dir = new File(Program.getWorkDir(true));
		CountryListJaxb.findbyId("FRA").ifPresent(country -> map.put(country, loadFrance()));
		CountryListJaxb.findbyId("ITA").ifPresent(country -> map.put(country, loadItalie()));
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
						for (CountryVignobleJaxb vignoble : vignobleListJaxb.countryVignobleJaxbList) {
							if (!loadedVignobleListJaxb.countryVignobleJaxbList.contains(vignoble)) {
								loadedVignobleListJaxb.countryVignobleJaxbList.add(vignoble);
							}
							else {
								CountryVignobleJaxb countryVignobleJaxb = loadedVignobleListJaxb.countryVignobleJaxbList.get(loadedVignobleListJaxb.countryVignobleJaxbList.indexOf(vignoble));
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

	private static VignobleListJaxb load(final String ressource) {
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
		} catch (Exception e) {
			Program.showException(e);
		}

		if (vignobleListJaxb != null) {
			Collections.sort(vignobleListJaxb.countryVignobleJaxbList);
		}
		Debug("Loading vignoble Done");
		return vignobleListJaxb;
	}

	public static boolean save(CountryJaxb countryJaxb, VignobleListJaxb vignobleListJaxb) {
		Debug("Writing Country File: " + countryJaxb.getId());
		File fText = new File(Program.getWorkDir(true), countryJaxb.getId() + TEXT);
		try (FileWriter writer = new FileWriter(fText);
				 BufferedWriter buffer = new BufferedWriter(writer)){
			buffer.write(countryJaxb.getName());
		} catch (Exception e) {
			Program.showException(e);
			return false;
		}
		File f = new File(Program.getWorkDir(true), countryJaxb.getId() + VIGNOBLE);
		try {
			JAXBContext jc = JAXBContext.newInstance(VignobleListJaxb.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(vignobleListJaxb, new StreamResult(f));
		} catch (Exception e) {
			Program.showException(e);
			return false;
		}
		Debug("Writing Country File Done");
		return true;
	}

	public static boolean delete(CountryJaxb countryJaxb) {
		Debug("Deleting Country File: " + countryJaxb.getId());
		try {
			File fText = new File(Program.getWorkDir(true), countryJaxb.getId() + TEXT);
			Debug("Deleting " + fText.getAbsolutePath());
			fText.delete();
			File f = new File(Program.getWorkDir(true), countryJaxb.getId() + VIGNOBLE);
			Debug("Deleting " + f.getAbsolutePath());
			return f.delete();
		} catch (Exception e) {
			Program.showException(e);
			return false;
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
			appelationJaxb.setAOP(vignobleJaxb.getAOC());
			appelationJaxb.setIGP(vignobleJaxb.getIGP());
			if (vignobleToReturn.get().getUnmodifiableAppelation().contains(appelationJaxb)) {
				return vignobleToReturn;
			}
		}
		Debug("ERROR findVignobleWithAppelation " + vignobleJaxb.toString());
		return Optional.empty();
	}

	public Optional<AppelationJaxb> findAppelation(final VignobleJaxb vignobleJaxb) {
		CountryVignobleJaxb vigne = new CountryVignobleJaxb();
		vigne.setName(vignobleJaxb.getName());
		final AppelationJaxb appelationJaxbToReturn = new AppelationJaxb();
		appelationJaxbToReturn.setAOC(vignobleJaxb.getAOC());
		appelationJaxbToReturn.setAOP(vignobleJaxb.getAOC());
		appelationJaxbToReturn.setIGP(vignobleJaxb.getIGP());

		int index = countryVignobleJaxbList.indexOf(vigne);
		if (index != -1) {
			final CountryVignobleJaxb countryVignobleJaxb = countryVignobleJaxbList.get(index);

			final int index1 = countryVignobleJaxb.getUnmodifiableAppelation().indexOf(appelationJaxbToReturn);
			if (index1 != -1) {
				return Optional.of(countryVignobleJaxb.getUnmodifiableAppelation().get(index1));
			}
		}
		Debug("ERROR findAppelation " + vignobleJaxb.toString());
		return Optional.empty();
	}

	public void addVignoble(final VignobleJaxb vignobleJaxb) {
		Debug("Add Vignoble " + vignobleJaxb);
		CountryVignobleJaxb vigne = new CountryVignobleJaxb();
		vigne.setName(vignobleJaxb.getName());
		AppelationJaxb appelationJaxb = new AppelationJaxb();
		appelationJaxb.setAOC(vignobleJaxb.getAOC());
		appelationJaxb.setAOP(vignobleJaxb.getAOP());
		appelationJaxb.setIGP(vignobleJaxb.getIGP());
		LinkedList<AppelationJaxb> list = new LinkedList<>();
		list.add(appelationJaxb);
		vigne.setAppelation(list);
		countryVignobleJaxbList.add(vigne);
		CountryVignobleController.createVignobleInMap(vignobleJaxb);
		Collections.sort(countryVignobleJaxbList);
		Debug("Add vignoble Done");
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

	private static void Debug(String sText) {
		Program.Debug("VignobleListJaxb: " + sText);
	}

}
