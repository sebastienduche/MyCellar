package mycellar.core;

import java.util.LinkedHashMap;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 21/04/21
 */

public class MyLinkedHashMap extends LinkedHashMap<String,Object> {

  static final long serialVersionUID = 123;
  public MyLinkedHashMap() {
  }

  public String getString(String cle) {
    try {
      return super.get(cle).toString();
    }
    catch (Exception e) {
      return null;
    }
  }

  public String getString(String cle, String defaut) {
    if (super.containsKey(cle)) {
      return get(cle).toString();
    } else {
      if (defaut != null && !defaut.isEmpty()) {
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
