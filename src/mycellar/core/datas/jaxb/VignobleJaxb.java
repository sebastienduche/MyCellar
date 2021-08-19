//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.14 at 12:30:49 PM CEST 
//


package mycellar.core.datas.jaxb;

import mycellar.core.IdGenerator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/*
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 1.6
 * @since 20/11/20
 */

/**
 * <p>This class is linked to the Vignoble that contains the Bouteille object
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AOC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IGP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AOP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "country",
    "name",
    "aoc",
    "igp",
    "aop",
    "id",
})
@XmlRootElement(name = "vignoble")
public class VignobleJaxb implements Serializable {

  private static final long serialVersionUID = -4668411717652334826L;

  @XmlElement(required = true)
  public String country;
  @XmlElement(name = "AOC")
  public String aoc;
  @XmlElement(name = "IGP")
  public String igp;
  @XmlElement()
  private String name;
  @XmlElement(name = "AOP")
  private String aop;

  @XmlElement()
  private long id;

  public VignobleJaxb() {
    id = IdGenerator.generateID();
  }

  public VignobleJaxb(String country, String name, String aoc, String igp) {
    this.country = country;
    this.name = name;
    this.aoc = aoc;
    this.igp = igp;
    id = IdGenerator.generateID();
  }

  public static boolean isEmpty(VignobleJaxb vignobleJaxb) {
    return vignobleJaxb == null ||
        vignobleJaxb.getCountry() == null ||
        vignobleJaxb.getCountry().isBlank() ||
        vignobleJaxb.isAppellationEmpty();
  }

  public long getId() {
    return id;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAOC() {
    return aoc;
  }

  public void setAOC(String aoc) {
    this.aoc = aoc;
  }

  public String getIGP() {
    return igp;
  }

  public void setIGP(String igp) {
    this.igp = igp;
  }

  @Deprecated
  public String getAOP() {
    return aop;
  }

  @Deprecated
  public void setAOP(String aop) {
    this.aop = aop;
  }

  public boolean isAppellationEmpty() {
    return (aoc == null || aoc.isEmpty())
        && (aop == null || aop.isEmpty())
        && (igp == null || igp.isEmpty());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((aoc == null) ? 0 : aoc.hashCode());
    result = prime * result + ((aop == null) ? 0 : aop.hashCode());
    result = prime * result + ((country == null) ? 0 : country.hashCode());
    result = prime * result + ((igp == null) ? 0 : igp.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    if (!getClass().equals(obj.getClass())) {
      return false;
    }
    VignobleJaxb other = (VignobleJaxb) obj;
    if (aoc == null) {
      if (other.aoc != null) {
        return false;
      }
    } else if (!aoc.equals(other.aoc)) {
      return false;
    }
    if (aop == null) {
      if (other.aop != null) {
        return false;
      }
    } else if (!aop.equals(other.aop)) {
      return false;
    }
    if (country == null) {
      if (other.country != null) {
        return false;
      }
    } else if (!country.equals(other.country)) {
      return false;
    }
    if (igp == null) {
      if (other.igp != null) {
        return false;
      }
    } else if (!igp.equals(other.igp)) {
      return false;
    }
    if (name == null) {
      return other.name == null;
    } else {
      return name.equals(other.name);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("VignobleJaxb [country=");
    if (country != null) {
      CountryJaxb c = CountryListJaxb.findByIdOrLabel(country);
      if (c != null) {
        sb.append(c.getId());
      } else {
        sb.append(country);
      }
    }
    sb.append(" name=");
    sb.append(name);
    sb.append(" aoc=");
    sb.append(aoc);
    sb.append(" igp=");
    sb.append(igp).append("]");
    return sb.toString();
  }

  public void setValues(AppelationJaxb ap) {
    aoc = ap.getAOC();
    igp = ap.getIGP();
  }

  public String getSearchLabel() {
    StringBuilder sb = new StringBuilder();
    if (country != null) {
      CountryJaxb c = CountryListJaxb.findByIdOrLabel(country);
      if (c != null) {
        sb.append(c.getLabel());
      } else {
        sb.append(country);
      }
    }
    if (name != null && !name.isEmpty()) {
      sb.append("-");
      sb.append(name);
    }
    if (aoc != null && !aoc.isEmpty()) {
      sb.append("-");
      sb.append(aoc);
    }
    if (igp != null && !igp.isEmpty()) {
      sb.append("-");
      sb.append(igp);
    }
    return sb.toString();
  }
}
