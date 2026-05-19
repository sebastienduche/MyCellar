package mycellar.core.datas.jaxb;

import mycellar.core.text.MyCellarLabelManagement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.UUID;

import static mycellar.ProgramConstants.COUNTRY_LABEL_KEY;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 18/05/26
 */

@XmlRootElement(name = "country")
@XmlAccessorType(XmlAccessType.NONE)
public class CountryJaxb implements Comparable<CountryJaxb> {
  @Deprecated
  @XmlAttribute
  private String id;

  @XmlAttribute
  private String name;

  @XmlAttribute(required = true)
  private UUID uuid;

  public CountryJaxb() {
  }

  public CountryJaxb(String name) {
    id = null;
    uuid = UUID.randomUUID();
    this.name = name;
  }

  public CountryJaxb(String id, String name) {
    this.id = id;
    uuid = UUID.randomUUID();
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getLabel() {
    if (id == null) {
      return "";
    }
    String label = MyCellarLabelManagement.getLabelFromCode(COUNTRY_LABEL_KEY + id, false);
    if (label.equals(COUNTRY_LABEL_KEY + id) || label.isBlank()) {
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
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!Objects.equals(getClass(), obj.getClass())) {
      return false;
    }
    CountryJaxb other = (CountryJaxb) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      return other.name == null;
    } else {
      return name.equals(other.name);
    }
  }

  @Override
  public int compareTo(CountryJaxb o) {
    return getLabel().compareTo(o.getLabel());
  }
}
