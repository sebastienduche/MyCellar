package mycellar.vignobles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
 
@XmlRootElement(name = "vignoble")
@XmlAccessorType (XmlAccessType.FIELD)
public class CountryVignoble implements Comparable<CountryVignoble>
{
	@XmlElement(name = "appelation")
    private List<Appelation> appelation = null;
    
    @XmlAttribute
    private String name;

	public List<Appelation> getAppelation() {
		return appelation;
	}

	public void setAppelation(List<Appelation> appelation) {
		this.appelation = appelation;
	}

	//@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
 
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(CountryVignoble vignoble) {
		if (getName() == null || vignoble.getName() == null) {
			return -1;
		}
		return getName().compareTo(vignoble.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CountryVignoble other = (CountryVignoble) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
