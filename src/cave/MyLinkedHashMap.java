package Cave;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.6
 * @since 05/08/07
 */

public class MyLinkedHashMap extends LinkedHashMap<String,Object> {

  static final long serialVersionUID = 123;
  public MyLinkedHashMap() {
  }

  public MyLinkedHashMap(int p0) {
    super(p0);
  }

  public MyLinkedHashMap(int p0, float p1) {
    super(p0, p1);
  }

  public MyLinkedHashMap(int p0, float p1, boolean p2) {
    super(p0, p1, p2);
  }

  public MyLinkedHashMap(Map<String,Object> p0) {
    super(p0);
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
      return super.get(cle).toString();
    }else{
      if ( defaut != null && !defaut.equals("") )
        super.put(cle, defaut);
    }
    return defaut;
  }

  public int getInt(String cle) {
    return Integer.parseInt(super.get(cle).toString());
  }

  public int getInt(String cle, int defaut) {
    if (super.containsKey(cle)) {
      try {
        return Integer.parseInt(super.get(cle).toString());
      }
      catch (NumberFormatException nfe) {
        super.put(cle, Integer.toString(defaut));
        return defaut;
      }
    }else{
      if ( defaut != -1)
        super.put(cle, Integer.toString(defaut));
    }
    return defaut;
  }

}
