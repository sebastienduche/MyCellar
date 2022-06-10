package mycellar.placesmanagement.places;

public class SimplePlaceBuilder {
  private final String name;
  private int nbParts;
  private int startSimplePlace;
  private boolean limited;
  private int limit;
  private boolean defaultPlace;

  public SimplePlaceBuilder(String name) {
    this.name = name;
    nbParts = 1;
    startSimplePlace = 0;
    limited = false;
    limit = -1;
    defaultPlace = false;
  }

  public SimplePlaceBuilder nbParts(int value) {
    nbParts = value;
    return this;
  }

  public SimplePlaceBuilder startSimplePlace(int value) {
    startSimplePlace = value;
    return this;
  }

  public SimplePlaceBuilder limited(boolean value) {
    limited = value;
    return this;
  }

  public SimplePlaceBuilder limit(int value) {
    limit = value;
    return this;
  }

  public SimplePlaceBuilder setDefaultPlace(boolean defaultPlace) {
    this.defaultPlace = defaultPlace;
    return this;
  }

  public SimplePlace build() {
    final SimplePlace rangement = new SimplePlace(name, nbParts);
    rangement.setLimited(limited, limit);
    rangement.setPartNumberIncrement(startSimplePlace);
    rangement.setDefaultPlace(defaultPlace);
    return rangement;
  }

}
