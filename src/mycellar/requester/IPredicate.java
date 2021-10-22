package mycellar.requester;

import mycellar.requester.ui.ValueSearch;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 15/07/19
 */

public interface IPredicate<T> {

  boolean apply(T type);

  boolean apply(T predicate, Object compare, int type);

  boolean isValueRequired();

  ValueSearch askforValue();

  String getName();

  int getType();

  boolean isEmptyValueForbidden();
}
