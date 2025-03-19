package mycellar.core.text;

import mycellar.Program;
import mycellar.core.IMyCellarComponent;
import mycellar.general.IResource;
import mycellar.general.ResourceErrorKey;

import javax.swing.JOptionPane;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 19/03/25
 */

public class MyCellarLabelManagement {

  private static final List<IMyCellarComponent> LABEL_LIST = new ArrayList<>();

  public static void add(IMyCellarComponent component) {
    LABEL_LIST.add(component);
  }

  public static void updateText(IMyCellarComponent component, LabelKey labelKey) {
    component.setText(getLabel(labelKey));
  }

  public static String getLabel(IResource id) {
    return getLabelFromCode(id.getKey(), true);
  }

  public static String getLabel(IResource id, Object... parameters) {
    return MessageFormat.format(getLabelFromCode(id.getKey(), true), parameters);
  }

  public static String getLabelFromCode(String id, boolean displayError) {
    try {
      return LanguageFileLoader.getLabel(id);
    } catch (MissingResourceException e) {
      if (displayError) {
        Program.Debug("Program: ERROR: Missing Label " + id);
        JOptionPane.showMessageDialog(null, "Missing Label '" + id + "'", "Error", JOptionPane.ERROR_MESSAGE);
      }
      return id;
    }
  }

  public static String getError(IResource id, Object... parameters) {
    return MessageFormat.format(getError(id), parameters);
  }

  public static void updateLabels() {
    LABEL_LIST.forEach(IMyCellarComponent::updateText);
  }

  private static String getLabel(LabelKey labelKey) {
    LabelType type = labelKey.getLabelType();
    IResource resource = labelKey.getResource();
    String labelValue = labelKey.getValue();
    if (type == null || resource == null) {
      return "";
    }

    if (labelValue == null) {
      return switch (type) {
        case LABEL -> getLabel(resource);
        case NONE -> resource.getKey();
      };
    } else {
      return switch (type) {
        case LABEL -> getLabel(resource, labelValue).strip();
        case NONE -> resource.getKey();
      };
    }
  }

  private static String getError(IResource key) {
    assert key instanceof ResourceErrorKey;
    try {
      return LanguageFileLoader.getError(key.getKey());
    } catch (MissingResourceException e) {
      JOptionPane.showMessageDialog(null, "Missing Error '" + key.getKey() + "'", "Error", JOptionPane.ERROR_MESSAGE);
      return key.getKey();
    }
  }
}
