package mycellar.core;

import org.w3c.dom.Element;

public abstract class MyCellarObject implements IMyCellarObject {

  public abstract Object fromXmlElement(Element element);

  protected boolean equalsValue(String value, String other) {
    if (value == null) {
      return other != null;
    } else {
      return !value.equals(other);
    }
  }

  public abstract void update(MyCellarObject bouteille);

  public abstract Object cast(MyCellarObject myCellarObject);

  public abstract Object castCopy(MyCellarObject myCellarObject);
}
