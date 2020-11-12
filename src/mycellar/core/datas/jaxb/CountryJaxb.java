package mycellar.core.datas.jaxb;

import mycellar.Program;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 12/11/20
 */

@XmlRootElement(name = "country")
@XmlAccessorType (XmlAccessType.FIELD)
public class CountryJaxb implements Comparable<CountryJaxb>
{
	@XmlAttribute
	private String id;

	@XmlAttribute
	private String name;

	public CountryJaxb() {}

	public CountryJaxb(String name) {
		id = null;
		this.name = name;
	}

	public CountryJaxb(String id, String name) {
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
		if (id == null) {
			return "";
		}
		String label = Program.getLabel("Country." + id, false);
		if (label.equals("Country." + id)) {
			return getName();
		}
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
		CountryJaxb other = (CountryJaxb) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(CountryJaxb o) {
		return getLabel().compareTo(o.getLabel());
	}
}
