package mycellar.vignobles;

import mycellar.core.datas.jaxb.CountryVignobleJaxb;
import mycellar.countries.Country;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 09/11/20
 */

public class CountryVignobleID {

  private final String id;

  CountryVignobleID(Country country, CountryVignobleJaxb vignoble) {
    id = country.getId() + "-" + vignoble.getName();
  }

  public String getId() {
    return id;
  }
}
