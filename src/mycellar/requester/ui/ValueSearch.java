package mycellar.requester.ui;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2019</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 08/03/19
 */
public class ValueSearch {

  private final Object value;
  private final String label;

  public ValueSearch(Object value) {
    this.value = value;
    if (value != null) {
      label = value.toString();
    } else {
      label = "";
    }
  }

  public ValueSearch(Object value, String label) {
    this.value = value;
    this.label = label;
  }

  public Object getValue() {
    return value;
  }

  public String getLabel() {
    return label;
  }
}
