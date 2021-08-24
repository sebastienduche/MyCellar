package mycellar.core;

import mycellar.Bouteille;
import org.w3c.dom.Element;

public abstract class MyCellarObject implements IMyCellarObject {

  public abstract Object fromXmlElemnt(Element element);

  protected boolean equalsValue(String value, String other) {
    if (value == null) {
      return other != null;
    } else {
      return !value.equals(other);
    }
  }

  public abstract void update(Bouteille bouteille);
}
