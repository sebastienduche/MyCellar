package mycellar.core.datas.jaxb;

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
 * @version 0.8
 * @since 12/11/20
 */

@XmlRootElement(name = "countries")
@XmlAccessorType (XmlAccessType.FIELD)
public class CountryListJaxb
{
	@XmlElement(name = "country")
	private List<CountryJaxb> countries = null;

	private static final String FR = "fr";
	private static CountryListJaxb instance = load();

	public static CountryListJaxb getInstance() {
		return instance;
	}

	public List<CountryJaxb> getCountries() {
		return countries;
	}

	private void setCountries(List<CountryJaxb> countries) {
		this.countries = countries;
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
		CountryListJaxb countryListJaxb = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CountryListJaxb.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			//We had written this file in marshalling example
			URL stream = CountryListJaxb.class.getClassLoader().getResource("resources/countries.xml");
			if (stream == null) {
				Program.Debug("CountryListJaxb: ERROR: Vignobles: Missing resource countries.xml");
				return null;
			}
			countryListJaxb = (CountryListJaxb) jaxbUnmarshaller.unmarshal( stream );
		} catch (JAXBException | RuntimeException e) {
			Program.showException(e);
		}
		return countryListJaxb;
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
		return findbyId(label).orElse(findByLabel(label).orElse(null));
	}


	public static void add(CountryJaxb countryJaxb) {
		if (findbyId(countryJaxb.getId()).isPresent()) {
			return;
		}
		getInstance().getCountries().add(countryJaxb);
	}

}
