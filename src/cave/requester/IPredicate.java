package Cave.requester;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 11/06/14
 */

public interface IPredicate<T> {

	boolean apply(T type);
	boolean apply(T type, Object compare);
	boolean isValueRequired();
	Object askforValue();
	String getName();
}
