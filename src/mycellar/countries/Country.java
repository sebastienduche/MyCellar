package mycellar.countries;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import mycellar.Program;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 14/01/17
 */

@XmlRootElement(name = "country")
@XmlAccessorType (XmlAccessType.FIELD)
public class Country implements Comparable<Country>
{    
    @XmlAttribute
    private String id;
    
    @XmlAttribute
    private String name;
    
    public Country() {
    }
    
    public Country(String name) {
    	this.id = null;
    	this.name = name;
    }
    
    public Country(String id, String name) {
    	this.id = id;
    	this.name = name;
    }

    public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLabel() {
		if(id == null)
			return "";
		String label = Program.getLabel("Country."+id, false);
		if(label.equals("Country."+id))
			return getName();
		return label;
	}
 
	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Country o) {
		return getLabel().compareTo(o.getLabel());
	}
	
	
}