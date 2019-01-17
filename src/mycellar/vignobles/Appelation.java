package mycellar.vignobles;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
 
@XmlRootElement
public class Appelation {
 
	private String AOC;
	private String AOP;
	private String IGP;
	
	public String getAOC() {
		return AOC;
	}
	
	@XmlElement(required=false)
	public void setAOC(String aOC) {
		AOC = aOC;
	}
	
	public String getAOP() {
		return AOP;
	}
	
	@XmlElement(required=false)
	public void setAOP(String aOP) {
		AOP = aOP;
	}
	
	public String getIGP() {
		return IGP;
	}
	
	@XmlElement(required=false)
	public void setIGP(String iGP) {
		IGP = iGP;
	}
 
	@Override
	public String toString() {
		return AOC;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((AOC == null) ? 0 : AOC.hashCode());
		result = prime * result + ((AOP == null) ? 0 : AOP.hashCode());
		result = prime * result + ((IGP == null) ? 0 : IGP.hashCode());
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
		Appelation other = (Appelation) obj;
		if (AOC == null) {
			if (other.AOC != null) {
				return false;
			}
		} else if (!AOC.equals(other.AOC)) {
			return false;
		}
		//if(other.AOP ! = null && other.AOP)
		/*if (AOP == null) {
			if (other.AOP != null)
				return false;
		} else if (!AOP.equals(other.AOP))
			return false;*/
		if (IGP == null || IGP.isEmpty()) {
			if (other.IGP != null && !other.IGP.isEmpty())
				return false;
		} else if (!IGP.equals(other.IGP)) {
			return false;
		}
		return true;
	}
 
	public boolean isEmpty() {
    	return (AOC == null || AOC.isEmpty())
    			&& (AOP == null || AOP.isEmpty())
    			&& (IGP == null || IGP.isEmpty());
    }
	
	public String getKeyString() {
		StringBuilder sb = new StringBuilder();
		if(AOC != null) {
			sb.append(AOC);
		}
		sb.append("-");
		if(AOP != null) {
			sb.append(AOP);
		}
		sb.append("-");
		if(IGP != null) {
			sb.append(IGP);
		}
		return sb.toString();
	}

	void makeItClean() {
		if (getAOC() != null) {
			if (getAOP() == null || getAOP().isEmpty()) {
				setAOP(getAOC());
			}
		} else {
			setAOC("");
		}
		if (getIGP() == null) {
			setIGP("");
		}
	}
}
