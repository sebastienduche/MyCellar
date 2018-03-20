package mycellar.requester;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 20/03/18
 */

public interface IPredicate<T> {

	boolean apply(T type);
	boolean apply(T predicate, Object compare, int type);
	boolean isValueRequired();
	Object askforValue();
	String getName();
	int getType();
}
