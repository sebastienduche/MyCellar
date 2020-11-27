package mycellar.core.datas.jaxb;

import mycellar.Program;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Appelation")
public class AppelationJaxb implements Comparable<AppelationJaxb> {

	@XmlElement(name = "AOC")
	private String aoc;
	@XmlElement(name = "AOP")
	private String aop;
	@XmlElement(name = "IGP")
	private String igp;

	private long id;

	public AppelationJaxb() {
		aoc = "";
		id = Program.generateID();
	}

	public String getAOC() {
		return aoc;
	}

	public void setAOC(String aOC) {
		aoc = aOC;
	}

	@Deprecated
	public String getAOP() {
		return aop;
	}

	@Deprecated
	public void setAOP(String aOP) {
		aop = aOP;
	}

	public String getIGP() {
		return igp;
	}

	public void setIGP(String iGP) {
		igp = iGP;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return aoc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aoc == null) ? 0 : aoc.hashCode());
		result = prime * result + ((aop == null) ? 0 : aop.hashCode());
		result = prime * result + ((igp == null) ? 0 : igp.hashCode());
		return result;
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
		AppelationJaxb other = (AppelationJaxb) obj;
		if (aoc == null) {
			if (other.aoc != null) {
				return false;
			}
		} else if (!aoc.equals(other.aoc)) {
			return false;
		}
		if (igp == null || igp.isEmpty()) {
			return other.igp == null || other.igp.isEmpty();
		} else {
			return igp.equals(other.igp);
		}
	}

	public boolean isEmpty() {
		return (aoc == null || aoc.isBlank())
				&& (aop == null || aop.isBlank())
				&& (igp == null || igp.isBlank());
	}

	public void makeItClean() {
		id = Program.generateID();
		if (getAOC() == null) {
			setAOC("");
		}
		if (getIGP() == null) {
			setIGP("");
		}
	}

	@Override
	public int compareTo(AppelationJaxb appelationJaxb) {
		String value = getAOC();
		if (value == null) {
			value = "";
		}
		String appelationAoc = appelationJaxb.getAOC();
		if (appelationAoc == null) {
			appelationAoc = "";
		}
		return value.compareTo(appelationAoc);
	}
}
