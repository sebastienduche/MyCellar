package mycellar.core;

import org.w3c.dom.Element;

public abstract class MyCellarObject implements IMyCellarObject {

  public abstract Object fromXmlElemnt(Element element);

  protected boolean equalsValue(String value, String other) {
    if (value == null) {
      if (other != null) {
        return true;
      }
    } else if (!value.equals(other)) {
      return true;
    }
    return false;
  }
}
