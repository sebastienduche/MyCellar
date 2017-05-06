package mycellar.countries;

import java.net.URL;
import java.util.Collections;
import java.util.List;
 





import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import mycellar.Program;
 
/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 21/01/17
 */

@XmlRootElement(name = "countries")
@XmlAccessorType (XmlAccessType.FIELD)
public class Countries 
{
    @XmlElement(name = "country")
    private List<Country> countries = null;
    
    private static Countries instance = Countries.load();
    
    public static Countries getInstance() {
    	return instance;
    }

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}
	
	public static void init() {
		instance = Countries.load();
		Collections.sort(instance.getCountries());
	}
	
	private static Countries load() {
		Countries countries = null;
		try{
		JAXBContext jaxbContext = JAXBContext.newInstance(Countries.class);
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	     
	    //We had written this file in marshalling example
	    URL stream = Countries.class.getClassLoader().getResource("resources/countries.xml");
		if(stream == null) {
			Program.Debug("Vignobles: Missing resource countries.xml");
			return null;
		}
	    countries = (Countries) jaxbUnmarshaller.unmarshal( stream );
		}catch(Exception e){
			Program.showException(e);
		}
		return countries;
	}
	
	public static Country find(String id) {
		if("fr".equals(id))
			return Program.france;
		for(Country country : getInstance().getCountries()) {
			if(country.getId().equals(id))
				return country;
		}
		return null;
	}
	
	public static Country findByLabel(String label) {
		for(Country country : getInstance().getCountries()) {
			if(country.getLabel().equals(label))
				return country;
		}
		return null;
	}
	
	public static Country findByIdOrLabel(String label) {
		Country c = find(label);
		if(c == null)
			c = findByLabel(label);
		return c;
	}
	
	
	public static void add(Country country) {
		if(find(country.getId()) != null)
			return;
		getInstance().getCountries().add(country);
	}
 
}