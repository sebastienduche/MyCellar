package mycellar.core;

import org.w3c.dom.Element;

public abstract class MyCellarObject implements IMyCellarObject {

  public abstract Object fromXmlElemnt(Element element);
}
