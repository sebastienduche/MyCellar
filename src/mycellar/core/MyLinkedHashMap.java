package mycellar.core;

import java.io.Serial;
import java.util.LinkedHashMap;

import static mycellar.MyCellarUtils.isDefined;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 14/10/21
 */

public class MyLinkedHashMap extends LinkedHashMap<String, Object> {

  @Serial
  private static final long serialVersionUID = 8042502398856598188L;

  public MyLinkedHashMap() {
  }

  public String getString(String cle, String defaut) {
    if (super.containsKey(cle)) {
      return get(cle).toString();
    } else {
      if (isDefined(defaut)) {
        put(cle, defaut);
      }
    }
    return defaut;
  }

  public int getInt(String cle, int defaut) {
    if (containsKey(cle)) {
      try {
        return Integer.parseInt(get(cle).toString());
      } catch (NumberFormatException nfe) {
        put(cle, Integer.toString(defaut));
        return defaut;
      }
    } else {
      if (defaut != -1) {
        put(cle, Integer.toString(defaut));
      }
    }
    return defaut;
  }
}
