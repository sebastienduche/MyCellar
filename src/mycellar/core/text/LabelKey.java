package mycellar.core.text;

import mycellar.general.IResource;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 18/03/25
 */
public class LabelKey {

  private final LabelType labelType;
  private String value;
  private final IResource resource;

  public LabelKey(IResource resource) {
    labelType = LabelType.LABEL;
    this.resource = resource;
  }

  public LabelKey(LabelType labelType, IResource resource) {
    this.labelType = labelType;
    this.resource = resource;
  }

  public LabelKey(IResource resource, String value) {
    labelType = LabelType.LABEL;
    this.value = value;
    this.resource = resource;
  }

  // Specific case when the key is a value and not a resource
  public LabelKey(String value) {
    labelType = LabelType.NONE;
    this.value = value;
    resource = null;
  }

  LabelType getLabelType() {
    return labelType;
  }

  public IResource getResource() {
    return resource;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
