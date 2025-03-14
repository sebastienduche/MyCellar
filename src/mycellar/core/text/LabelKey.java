package mycellar.core.text;

import mycellar.general.IResource;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 14/03/25
 */
public class LabelKey {

  private final LabelType labelType;
  @Deprecated(since = "version80")
  private final String key;
  private LabelProperty labelProperty;
  private String value;
  private final IResource resource;

  public LabelKey(IResource resource) {
    labelType = LabelType.LABEL;
    key = resource.getKey();
    this.resource = resource;
  }

  public LabelKey(LabelType labelType, IResource resource) {
    this.labelType = labelType;
    key = resource.getKey();
    this.resource = resource;
  }

  public LabelKey(IResource resource, LabelProperty labelProperty) {
    labelType = LabelType.LABEL;
    this.labelProperty = labelProperty;
    key = resource.getKey();
    this.resource = resource;
  }

  public LabelKey(IResource resource, LabelProperty labelProperty, String value) {
    labelType = LabelType.LABEL;
    this.labelProperty = labelProperty;
    this.value = value;
    key = resource.getKey();
    this.resource = resource;
  }

  // Specific case when the key is a value and not a resource
  public LabelKey(String value) {
    labelType = LabelType.NONE;
    this.value = value;
    key = null;
    resource = null;
  }

  LabelType getLabelType() {
    return labelType;
  }

  @Deprecated(since = "version80")
  public String getKey() {
    return key;
  }

  public IResource getResource() {
    return resource;
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
