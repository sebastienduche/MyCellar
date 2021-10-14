package mycellar;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 14/10/21
 */
public final class MyCellarUtils {

  public static boolean isNullOrEmpty(String value) {
    return value == null || value.isBlank();
  }

  public static boolean isDefined(String value) {
    return !isNullOrEmpty(value);
  }
}
