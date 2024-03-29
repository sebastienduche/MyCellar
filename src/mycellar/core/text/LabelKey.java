package mycellar.core.text;

public class LabelKey {

  private final LabelType labelType;
  private final String key;
  private LabelProperty labelProperty;
  private String value;

  /**
   * Titre : Cave &agrave; vin
   * Description : Votre description
   * Copyright : Copyright (c) 2016
   * Soci&eacute;t&eacute; : Seb Informatique
   *
   * @author S&eacute;bastien Duch&eacute;
   * @version 0.5
   * @since 24/05/22
   */
  public LabelKey(String key) {
    labelType = LabelType.LABEL;
    this.key = key;
  }

  public LabelKey(LabelType labelType, String key) {
    this.labelType = labelType;
    this.key = key;
  }

  public LabelKey(String key, LabelProperty labelProperty) {
    labelType = LabelType.LABEL;
    this.key = key;
    this.labelProperty = labelProperty;
  }

  public LabelKey(String key, LabelProperty labelProperty, String value) {
    labelType = LabelType.LABEL;
    this.key = key;
    this.labelProperty = labelProperty;
    this.value = value;
  }

  public LabelType getLabelType() {
    return labelType;
  }

  public String getKey() {
    return key;
  }

  public LabelProperty getLabelProperty() {
    return labelProperty;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
