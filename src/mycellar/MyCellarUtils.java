package mycellar;

import mycellar.core.MyCellarObject;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 19/10/21
 */
public final class MyCellarUtils {

  public static boolean isNullOrEmpty(String value) {
    return value == null || value.isBlank();
  }

  public static boolean isDefined(String value) {
    return !isNullOrEmpty(value);
  }

  public static String nonNullValueOrDefault(String value, String defaultValue) {
    return value == null ? defaultValue : value;
  }

  public static void assertObjectType(MyCellarObject myCellarObject, Class<?> aClass) {
    if (!aClass.isInstance(myCellarObject)) {
      throw new ClassCastException("Invalid class cast: " + aClass);
    }
  }
}
