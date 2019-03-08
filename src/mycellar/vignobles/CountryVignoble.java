package mycellar.vignobles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement(name = "vignoble")
@XmlAccessorType (XmlAccessType.FIELD)
public class CountryVignoble implements Comparable<CountryVignoble>
{
	@XmlElement(name = "appelation")
    private List<Appelation> appelation = null;
    
    @XmlAttribute
    private String name;

	List<Appelation> getUnmodifiableAppelation() {
		return Collections.unmodifiableList(appelation);
	}

	public List<Appelation> getSortedUnmodifiableAppelation() {
		Collections.sort(appelation);
		return Collections.unmodifiableList(appelation);
	}

	public List<Appelation> getAppelation() {
		return appelation;
	}

	public void setAppelation(List<Appelation> appelation) {
		this.appelation = appelation;
	}

	public void add(final Appelation _appelation) {
		if (_appelation == null) {
			return;
		}
		_appelation.makeItClean();
		if (!appelation.contains(_appelation)) {
			appelation.add(_appelation);
		}
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	void makeItClean() {
		appelation = appelation.stream().distinct().collect(Collectors.toList());
	}

	void checkAvaibility() {
		if (appelation == null) {
			appelation = new ArrayList<>();
		}
	}

	boolean isEmpty() {
		return name.isEmpty() && (appelation == null || appelation.isEmpty());
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CountryVignoble other = (CountryVignoble) obj;
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
	public int hashCode() {
		return Objects.hash(appelation, name);
	}
}
