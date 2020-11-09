package mycellar.core.datas.jaxb;

import mycellar.Program;
import mycellar.countries.Countries;
import mycellar.countries.Country;
import mycellar.vignobles.CountryVignobles;

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

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.8
 * @since 09/11/20
 */

@XmlRootElement(name = "vignobles")
@XmlAccessorType (XmlAccessType.FIELD)
public class Vignobles
{
	private static final String VIGNOBLE = ".vignoble";
	private static final String TEXT = ".txt";

	@XmlElement(name = "vignoble")
	private List<CountryVignobleJaxb> vignoble = null;

	private long id;

	public Vignobles() {
		id = Program.generateID();
	}

	public List<CountryVignobleJaxb> getVignoble() {
		return vignoble;
	}

	public void init() {
		vignoble = new ArrayList<>();
		id = Program.generateID();
	}

	private void checkAvaibility() {
		if (vignoble == null) {
			vignoble = new ArrayList<>();
		}
	}

	public static Vignobles loadFrance() {
		if(!Program.hasWorkDir()) {
			return load("resources/vignobles.xml");
		}
		final Vignobles fra = loadById("FRA");
		return (fra != null) ? fra : load("resources/vignobles.xml");
	}

	public static Vignobles loadItalie() {
		if(!Program.hasWorkDir()) {
			return load("resources/italie.xml");
		}
		final Vignobles ita = loadById("ITA");
		return (ita != null) ? ita : load("resources/italie.xml");
	}

	private static Vignobles loadById(String id) {
		final Country country = Countries.findbyId(id).orElse(null);
		if (country != null) {
			File f = new File(Program.getWorkDir(true), country.getId() + VIGNOBLE);
			if (f.exists()) {
				return load(f);
			}
		}
		return null;
	}

	private static Vignobles load(File f) {
		Debug("Loading JAXB File " + f.getAbsolutePath());
		if (!f.exists()) {
			return null;
		}
		Vignobles v;
		try {
			JAXBContext jc = JAXBContext.newInstance(Vignobles.class);
			Unmarshaller u = jc.createUnmarshaller();
			v = (Vignobles)u.unmarshal(new FileInputStream(f));
		} catch(Exception e) {
			Program.showException(e);
			return null;
		}
		v.checkAvaibility();
		Collections.sort(v.vignoble);
		for (CountryVignobleJaxb vignoble: v.vignoble) {
			vignoble.checkAvaibility();
			for (AppelationJaxb appelationJaxb : vignoble.getUnmodifiableAppelation()) {
				appelationJaxb.makeItClean();
			}
			vignoble.makeItClean();
		}
		Debug("Loading JAXB File Done");
		return v;
	}

	public static void loadAllCountries(Map<Country, Vignobles> map) {
		Debug("Loading All countries");
		map.clear();
		File dir = new File(Program.getWorkDir(true));
		Countries.findbyId("FRA").ifPresent(country -> map.put(country, loadFrance()));
		Countries.findbyId("ITA").ifPresent(country -> map.put(country, loadItalie()));
		File[] fileVignobles = dir.listFiles((pathname) -> pathname.getName().endsWith(VIGNOBLE));
		if (fileVignobles != null) {
			for (File f : fileVignobles) {
				String name = f.getName();
				String id = name.substring(0, name.indexOf(VIGNOBLE));
				if (!id.equals(id.toUpperCase())) {
					continue;
				}
				name = name.substring(0, name.indexOf(VIGNOBLE));
				File fText = new File(f.getParent(), name + TEXT);
				String label = "";
				if (fText.exists()) {
					label = Program.readFirstLineText(fText);
				}
				Country country = Countries.findbyId(name).orElse(null);
				if (country == null) {
					country = new Country(name, name);
					Countries.add(country);
				}
				if (!label.isEmpty()) {
					country.setName(label);
				}
				if (!map.containsKey(country)) {
					map.put(country, load(f));
				} else {
					Vignobles v = load(f);
					if (v != null) {
						Vignobles v1 = map.get(country);
						for (CountryVignobleJaxb vignoble : v1.vignoble) {
							if (!v.vignoble.contains(vignoble)) {
								v.vignoble.add(vignoble);
							}
							else {
								CountryVignobleJaxb countryVignobleJaxb = v.vignoble.get(v.vignoble.indexOf(vignoble));
								if (vignoble.getUnmodifiableAppelation() != null) {
									for (AppelationJaxb appelationJaxb : vignoble.getUnmodifiableAppelation()) {
										countryVignobleJaxb.add(appelationJaxb);
									}
								} else {
									vignoble.setAppelation(new LinkedList<>());
								}
							}
						}
					}
				}
			}
		}
		Debug("Loading All countries Done");
	}

	private static Vignobles load(final String ressource) {
		Debug("Loading vignoble "+ressource);
		Vignobles vignobles = null;
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(Vignobles.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			//We had written this file in marshalling example
			URL stream = Vignobles.class.getClassLoader().getResource(ressource);
			if(stream == null) {
				Debug("Vignobles: Missing resource "+ressource);
				return null;
			}
			vignobles = (Vignobles) jaxbUnmarshaller.unmarshal(stream);
		} catch(Exception e){
			Program.showException(e);
		}

		if (vignobles != null) {
			Collections.sort(vignobles.vignoble);
		}
		Debug("Loading vignoble Done");
		return vignobles;
	}

	public static boolean save(Country c, Vignobles vignoble) {
		Debug("Writing Country File: " + c.getId());
		File fText = new File(Program.getWorkDir(true), c.getId() + TEXT);
		try (FileWriter writer = new FileWriter(fText);
				 BufferedWriter buffer = new BufferedWriter(writer)){
			buffer.write(c.getName());
		} catch(Exception e) {
			Program.showException(e);
			return false;
		}
		File f = new File(Program.getWorkDir(true), c.getId() + VIGNOBLE);
		try {
			JAXBContext jc = JAXBContext.newInstance(Vignobles.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(vignoble, new StreamResult(f));
		} catch(Exception e) {
			Program.showException(e);
			return false;
		}
		Debug("Writing Country File Done");
		return true;
	}

	public static boolean delete(Country c) {
		Debug("Deleting Country File: "+c.getId());
		try {
			File fText = new File(Program.getWorkDir(true), c.getId() + TEXT);
			Debug("Deleting "+fText.getAbsolutePath());
			fText.delete();
			File f = new File(Program.getWorkDir(true), c.getId() + VIGNOBLE);
			Debug("Deleting "+f.getAbsolutePath());
			return f.delete();
		} catch(Exception e) {
			Program.showException(e);
			return false;
		}
	}

	public Optional<CountryVignobleJaxb> findVignoble(final Vignoble v) {
		return vignoble.stream().filter(countryVignoble -> v.getName().equals(countryVignoble.getName())).findFirst();
	}

	public Optional<CountryVignobleJaxb> findVignobleWithAppelation(final Vignoble v) {
		final Optional<CountryVignobleJaxb> vignobleToReturn = findVignoble(v);
		if (vignobleToReturn.isPresent()) {
			final AppelationJaxb appelationJaxb = new AppelationJaxb();
			appelationJaxb.setAOC(v.getAOC());
			appelationJaxb.setAOP(v.getAOC());
			appelationJaxb.setIGP(v.getIGP());
			if (vignobleToReturn.get().getUnmodifiableAppelation().contains(appelationJaxb)) {
				return vignobleToReturn;
			}
		}
		Debug("ERROR findVignobleWithAppelation " + v.toString());
		return Optional.empty();
	}

	public AppelationJaxb findAppelation(final Vignoble v) {
		CountryVignobleJaxb vigne = new CountryVignobleJaxb();
		vigne.setName(v.getName());
		final AppelationJaxb appelationJaxbToReturn = new AppelationJaxb();
		appelationJaxbToReturn.setAOC(v.getAOC());
		appelationJaxbToReturn.setAOP(v.getAOC());
		appelationJaxbToReturn.setIGP(v.getIGP());

		int index = vignoble.indexOf(vigne);
		if (index != -1) {
			final CountryVignobleJaxb vignoble1 = vignoble.get(index);

			if (vignoble1.getUnmodifiableAppelation().contains(appelationJaxbToReturn)) {
				return appelationJaxbToReturn;
			}
		}
		Debug("ERROR findAppelation "+v.toString());
		return null;
	}

	public void addVignoble(final Vignoble v) {
		Debug("add Vignoble "+v);
		CountryVignobleJaxb vigne = new CountryVignobleJaxb();
		vigne.setName(v.getName());
		AppelationJaxb appelationJaxb = new AppelationJaxb();
		appelationJaxb.setAOC(v.getAOC());
		appelationJaxb.setAOP(v.getAOP());
		appelationJaxb.setIGP(v.getIGP());
		LinkedList<AppelationJaxb> list = new LinkedList<>();
		list.add(appelationJaxb);
		vigne.setAppelation(list);
		vignoble.add(vigne);
		CountryVignobles.createVignobleInMap(v);
		Collections.sort(vignoble);
		Debug("add vignoble Done");
	}

	public CountryVignobleJaxb addVignoble(final String name) {
		Debug("Loading vignoble "+name);
		CountryVignobleJaxb vigne = new CountryVignobleJaxb();
		vigne.setName(name);
		vigne.setAppelation(new LinkedList<>());
		vignoble.add(vigne);
		Collections.sort(vignoble);
		Debug("Loading vignoble Done");
		return vigne;
	}

	public void delVignoble(final CountryVignobleJaxb vigne) {
		vignoble.remove(vigne);
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	private static void Debug(String sText) {
		Program.Debug("Vignobles: " + sText );
	}

}
