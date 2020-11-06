package mycellar.countries;

import mycellar.Program;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.7
 * @since 06/11/20
 */

@XmlRootElement(name = "countries")
@XmlAccessorType (XmlAccessType.FIELD)
public class Countries
{
	@XmlElement(name = "country")
	private List<Country> countries = null;

	private static final String FR = "fr";
	private static Countries instance = load();
	public static Countries getInstance() {
		return instance;
	}

	public List<Country> getCountries() {
		return countries;
	}

	private void setCountries(List<Country> countries) {
		this.countries = countries;
	}

	public static void init() {
		instance = load();
		if (instance != null) {
			Collections.sort(instance.getCountries());
		}
	}

	public static void close() {
		instance = new Countries();
		instance.setCountries(new ArrayList<>());
	}

	private static Countries load() {
		Countries countries = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Countries.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			//We had written this file in marshalling example
			URL stream = Countries.class.getClassLoader().getResource("resources/countries.xml");
			if (stream == null) {
				Program.Debug("ERROR: Vignobles: Missing resource countries.xml");
				return null;
			}
			countries = (Countries) jaxbUnmarshaller.unmarshal( stream );
		} catch (JAXBException | RuntimeException e) {
			Program.showException(e);
		}
		return countries;
	}

	public static Optional<Country> findbyId(String id) {
		if (FR.equals(id)) {
			return Optional.of(Program.FRANCE);
		}
		return getInstance().getCountries()
				.stream()
				.filter(country -> country.getId().equals(id))
				.findFirst();
	}

	private static Optional<Country> findByLabel(String label) {
		return getInstance().getCountries()
				.stream()
				.filter(country -> country.getLabel().equals(label))
				.findFirst();
	}

	public static Country findByIdOrLabel(String label) {
		return findbyId(label).orElse(findByLabel(label).orElse(null));
	}


	public static void add(Country country) {
		if (findbyId(country.getId()).isPresent()) {
			return;
		}
		getInstance().getCountries().add(country);
	}

}
