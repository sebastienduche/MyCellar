package mycellar.core.datas.jaxb;

import mycellar.Program;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@XmlRootElement(name = "vignoble")
@XmlAccessorType (XmlAccessType.FIELD)
public class CountryVignobleJaxb implements Comparable<CountryVignobleJaxb>
{
	@XmlElement(name = "appelation")
	private List<AppelationJaxb> appelationJaxb = null;

	@XmlAttribute
	private String name;

	private long id;

	public CountryVignobleJaxb() {
		name = "";
		id = Program.generateID();
	}

	public long getId() {
		return id;
	}

	public List<AppelationJaxb> getUnmodifiableAppelation() {
		return Collections.unmodifiableList(appelationJaxb);
	}

	public List<AppelationJaxb> getSortedUnmodifiableAppelation() {
		if (appelationJaxb == null) {
			return Collections.emptyList();
		}
		Collections.sort(appelationJaxb);
		return Collections.unmodifiableList(appelationJaxb);
	}

	public List<AppelationJaxb> getAppelation() {
		return appelationJaxb;
	}

	public void setAppelation(List<AppelationJaxb> appelationJaxb) {
		this.appelationJaxb = appelationJaxb;
	}

	public void add(final AppelationJaxb _appelationJaxb) {
		if (_appelationJaxb == null) {
			return;
		}
		_appelationJaxb.makeItClean();
		if (!appelationJaxb.contains(_appelationJaxb)) {
			appelationJaxb.add(_appelationJaxb);
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void makeItClean() {
		appelationJaxb = appelationJaxb.stream()
				.filter(Predicate.not(AppelationJaxb::isEmpty))
				.distinct()
				.collect(Collectors.toList());
		id = Program.generateID();
	}

	public void checkAvaibility() {
		if (appelationJaxb == null) {
			appelationJaxb = new ArrayList<>();
		}
	}

	public boolean isEmpty() {
		return name.isBlank() && (appelationJaxb == null || appelationJaxb.isEmpty());
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(CountryVignobleJaxb vignoble) {
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
		CountryVignobleJaxb other = (CountryVignobleJaxb) obj;
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
		return Objects.hash(appelationJaxb, name);
	}
}
