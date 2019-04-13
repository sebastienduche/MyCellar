package mycellar.vignobles;

import mycellar.countries.Country;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 10/04/19
 */

public class CountryVignobleID {

  private final String id;

  CountryVignobleID(Country country, CountryVignoble vignoble) {
    id = country.getId() + "-" + vignoble.getName();
  }

  public String getId() {
    return id;
  }
}
