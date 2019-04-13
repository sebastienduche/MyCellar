package mycellar.requester;

import mycellar.requester.ui.ValueSearch;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 08/03/19
 */

public interface IPredicate<T> {

	boolean apply(T type);
	boolean apply(T predicate, Object compare, int type);
	boolean isValueRequired();
	ValueSearch askforValue();
	String getName();
	int getType();
	String getLabelForValue(Object value);
}
