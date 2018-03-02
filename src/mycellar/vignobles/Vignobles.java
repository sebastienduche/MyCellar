package mycellar.vignobles;

import mycellar.Program;
import mycellar.Vignoble;
import mycellar.countries.Countries;
import mycellar.countries.Country;

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
import java.util.Map;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 1.2
 * @since 02/03/18
 */

@XmlRootElement(name = "vignobles")
@XmlAccessorType (XmlAccessType.FIELD)
public class Vignobles
{
	private static final String VIGNOBLE = ".vignoble";
	private static final String TEXT = ".txt";
	@XmlElement(name = "vignoble")
	private ArrayList<CountryVignoble> vignoble = null;

	public ArrayList<CountryVignoble> getVignoble() {
		return vignoble;
	}

	public void setVignoble(ArrayList<CountryVignoble> vignoble) {
		this.vignoble = vignoble;
	}

	public static Vignobles loadFrance() {
		if(!Program.hasWorkDir()) {
			return load("resources/vignobles.xml");
		}
		Country fra = Countries.find("FRA");
		if (fra != null) {
			File f = new File(Program.getWorkDir(true), fra.getId() + VIGNOBLE);
			if (f.exists()) {
				return load(f);
			}
		}
		return load("resources/vignobles.xml");
	}

	public static Vignobles loadItalie() {
		if(!Program.hasWorkDir()) {
			return load("resources/italie.xml");
		}
		Country ita = Countries.find("ITA");
		if (ita != null) {
			File f = new File(Program.getWorkDir(true), ita.getId() + VIGNOBLE);
			if (f.exists()) {
				return load(f);
			}
		}
		return load("resources/italie.xml");
	}

	private static Vignobles load(File f) {
		Debug("Loading JAXB File "+f.getAbsolutePath());
		Vignobles v;
		if(!f.exists())
			return null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Vignobles.class);
			Unmarshaller u = jc.createUnmarshaller();
			v = (Vignobles)u.unmarshal(new FileInputStream(f));
		} catch( Exception e ) {
			Program.showException(e);
			return null;
		}
		if(v.vignoble == null)
			v.vignoble = new ArrayList<>();
		Collections.sort(v.vignoble);
		Debug("Loading JAXB File Done");
		return v;
	}

	public static void loadAllCountries(Map<Country, Vignobles> map) {
		Debug("Loading All countries");
		map.clear();
		File dir = new File(Program.getWorkDir(true));
		map.put(Countries.find("FRA"), Vignobles.loadFrance());
		map.put(Countries.find("ITA"), Vignobles.loadItalie());
		File fileVignobles[] = dir.listFiles((pathname) -> {
				return pathname.getName().endsWith(VIGNOBLE);
		});
		if (fileVignobles != null) {
			for (File f : fileVignobles) {
				String name = f.getName();
				String id = name.substring(0, name.indexOf(VIGNOBLE));
				if (!id.equals(id.toUpperCase()))
					continue;
				name = name.substring(0, name.indexOf(VIGNOBLE));
				Country country = Countries.find(name);
				File fText = new File(f.getParent(), name + TEXT);
				String label = "";
				if (fText.exists())
					label = Program.readFirstLineText(fText);
				if (country == null) {
					country = new Country(name, name);
					if (!label.isEmpty())
						country.setName(label);
					Countries.add(country);
				} else {
					if (!label.isEmpty())
						country.setName(label);
				}
				if (!map.containsKey(country)) {
					map.put(country, load(f));
				} else {
					Vignobles v = load(f);
					if (v != null) {
						Vignobles v1 = map.get(country);
						for (CountryVignoble vignoble : v1.vignoble) {
							if (!v.vignoble.contains(vignoble))
								v.vignoble.add(vignoble);
							else {
								CountryVignoble countryVignoble = v.vignoble.get(v.vignoble.indexOf(vignoble));
								if (vignoble.getAppelation() != null) {
									for (Appelation appelation : vignoble.getAppelation()) {
										if (!countryVignoble.getAppelation().contains(appelation)) {
											countryVignoble.getAppelation().add(appelation);
										}
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

	private static Vignobles load(String ressource) {
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
			vignobles = (Vignobles) jaxbUnmarshaller.unmarshal( stream );
		}catch(Exception e){
			Program.showException(e);
		}

		if (vignobles != null) {
			Collections.sort(vignobles.vignoble);
		}
		Debug("Loading vignoble Done");
		return vignobles;
	}

	public static boolean save(Country c, Vignobles vignoble) {
		Debug("Writing Country File");
		try {
			File fText = new File(Program.getWorkDir(true), c.getId() + TEXT);
			FileWriter writer = new FileWriter(fText);
			BufferedWriter buffer = new BufferedWriter(writer);
			buffer.write(c.getName());
			buffer.flush();
			buffer.close();
			File f = new File(Program.getWorkDir(true), c.getId() + VIGNOBLE);
			JAXBContext jc = JAXBContext.newInstance(Vignobles.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(vignoble, new StreamResult(f));
		} catch( Exception e ) {
			Program.showException(e);
			return false;
		}
		Debug("Writing Country File Done");
		return true;
	}

	public static boolean delete(Country c) {
		Debug("Deleting Country File");
		try {
			File fText = new File(Program.getWorkDir(true), c.getId() + TEXT);
			Debug("Deleting "+fText.getAbsolutePath());
			fText.delete();
			File f = new File(Program.getWorkDir(true), c.getId() + VIGNOBLE);
			Debug("Deleting "+f.getAbsolutePath());
			return f.delete();
		} catch( Exception e ) {
			Program.showException(e);
			return false;
		}
	}

	public CountryVignoble findVignoble(Vignoble v) {
		CountryVignoble vigne = new CountryVignoble();
		vigne.setName(v.getName());
		int index = vignoble.indexOf(vigne);
		if(index != -1)
			return vignoble.get(index);
		Debug("ERROR findVignoble "+v.toString());
		return null;
	}

	public CountryVignoble findVignobleWithAppelation(Vignoble v) {
		CountryVignoble vigne = new CountryVignoble();
		vigne.setName(v.getName());
		int index = vignoble.indexOf(vigne);
		if(index != -1) {
			CountryVignoble vignoble1 = vignoble.get(index);
			Appelation appelation = new Appelation();
			appelation.setAOC(v.getAOC());
			appelation.setAOP(v.getAOC());
			appelation.setIGP(v.getIGP());
			if(vignoble1.getAppelation().contains(appelation))
				return vignoble1;
		}
		Debug("ERROR findVignobleWithAppelation "+v.toString());
		return null;
	}

	public Appelation findAppelation(Vignoble v) {
		CountryVignoble vigne = new CountryVignoble();
		vigne.setName(v.getName());
		int index = vignoble.indexOf(vigne);
		if(index != -1) {
			CountryVignoble vignoble1 = vignoble.get(index);
			Appelation appelation = new Appelation();
			appelation.setAOC(v.getAOC());
			appelation.setAOP(v.getAOC());
			appelation.setIGP(v.getIGP());
			if(vignoble1.getAppelation().contains(appelation))
				return appelation;
		}
		Debug("ERROR findAppelation "+v.toString());
		return null;
	}

//	public CountryVignoble findXMLVignoble(CountryVignoble v) {
//		int index = vignoble.indexOf(v);
//		if(index != -1) {
//			return vignoble.get(index);
//		}
//		Debug("ERROR findXMLVignoble "+v.toString());
//		return null;
//	}

	public void addVignoble(Vignoble v) {
		Debug("add Vignoble "+v);
		CountryVignoble vigne = new CountryVignoble();
		vigne.setName(v.getName());
		Appelation appelation = new Appelation();
		appelation.setAOC(v.getAOC());
		appelation.setAOP(v.getAOP());
		appelation.setIGP(v.getIGP());
		LinkedList<Appelation> list = new LinkedList<>();
		list.add(appelation);
		vigne.setAppelation(list);
		vignoble.add(vigne);
		CountryVignobles.createVignobleInMap(v);
		Collections.sort(vignoble);
		Debug("add vignoble Done");
	}

	public CountryVignoble addVignoble(String name) {
		Debug("Loading vignoble "+name);
		CountryVignoble vigne = new CountryVignoble();
		vigne.setName(name);
		vigne.setAppelation(new LinkedList<>());
		vignoble.add(vigne);
		Collections.sort(vignoble);
		Debug("Loading vignoble Done");
		return vigne;
	}

	public void delVignoble(CountryVignoble vigne) {
		vignoble.remove(vigne);
	}

	/**
	 * Debug
	 *
	 * @param sText String
	 */
	public static void Debug(String sText) {
		Program.Debug("Vignobles: " + sText );
	}

}
