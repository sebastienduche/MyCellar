package mycellar.requester;

import mycellar.requester.ui.ValueSearch;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 25/12/23
 */

public interface IPredicate<T> {

  default boolean apply(T type) {
    return apply(type, "", -1);
  }

  boolean apply(T predicate, Object compare, int type);

  default boolean isValueRequired() {
    return true;
  }

  ValueSearch askForValue();

  String getName();

  default int getType() {
    return 0;
  }

  default boolean isEmptyValueForbidden() {
    return true;
  }
}
