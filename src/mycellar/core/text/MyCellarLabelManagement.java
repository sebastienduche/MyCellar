package mycellar.core.text;

import mycellar.Program;
import mycellar.ProgramType;
import mycellar.core.IMyCellarComponent;
import mycellar.general.IResource;
import mycellar.general.ResourceErrorKey;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JOptionPane;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.ProgramConstants.KEY_TYPE;
import static mycellar.ProgramConstants.SPACE;
import static mycellar.ProgramConstants.THREE_DOTS;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 17/03/25
 */

public class MyCellarLabelManagement {

  private static final List<IMyCellarComponent> LABEL_LIST = new ArrayList<>();

  public static void add(IMyCellarComponent component) {
    LABEL_LIST.add(component);
  }

  public static void updateText(IMyCellarComponent component, LabelKey labelKey) {
    component.setText(getLabel(labelKey.getLabelType(), labelKey.getResource(), labelKey.getLabelProperty(), labelKey.getValue()));
  }

  public static String getLabel(LabelType type, IResource resource, LabelProperty labelProperty, String labelValue) {
    if (type == null || resource == null) {
      return "";
    }

    if (labelValue == null) {
      return switch (type) {
        case LABEL -> getLabelWithProperty(resource, labelProperty);
        case ERROR -> getErrorWithProperty(resource, labelProperty);
        case NONE -> resource.getKey();
      };
    } else {
      return switch (type) {
        case LABEL -> getLabelWithProperty(resource, labelProperty, labelValue).strip();
        case ERROR -> getErrorWithProperty(resource, labelProperty, labelValue).strip();
        case NONE -> resource.getKey();
      };
    }
  }

  public static String getLabel(IResource id) {
    return getLabelFromCode(id.getKey(), true);
  }

  public static String getLabel(IResource id, Object... parameters) {
    return MessageFormat.format(getLabelFromCode(id.getKey(), true), parameters);
  }

  public static String getLabelWithProperty(IResource key, LabelProperty labelProperty) {
    if (labelProperty == null) {
      return getLabel(key);
    }
    String label = getLabel(key);
    label = label.replaceAll(KEY_TYPE, getLabelForType(labelProperty));
    if (labelProperty.isThreeDashes()) {
      label += THREE_DOTS;
    }
    if (labelProperty.isDoubleQuote()) {
      label += LanguageFileLoader.isFrench() ? SPACE + DOUBLE_DOT : DOUBLE_DOT;
    }
    return label;
  }

  public static String getLabelWithProperty(IResource key, LabelProperty labelProperty, Object... parameters) {
    return MessageFormat.format(getLabelWithProperty(key, labelProperty), parameters);
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

  public static String getErrorWithProperty(IResource key, LabelProperty labelProperty) {
    assert key instanceof ResourceErrorKey;
    if (labelProperty == null) {
      return getError((ResourceErrorKey) key);
    }
    String label = getError((ResourceErrorKey) key);
    return label.replaceAll(KEY_TYPE, getLabelForType(labelProperty));
  }

  public static String getErrorWithProperty(IResource key, LabelProperty labelProperty, Object... parameters) {
    return MessageFormat.format(getErrorWithProperty(key, labelProperty), parameters);
  }

  public static String getError(ResourceErrorKey id, Object... parameters) {
    return MessageFormat.format(getError(id), parameters);
  }


  private static String getError(ResourceErrorKey key) {
    try {
      return LanguageFileLoader.getError(key.getKey());
    } catch (MissingResourceException e) {
      JOptionPane.showMessageDialog(null, "Missing Error '" + key.getKey() + "'", "Error", JOptionPane.ERROR_MESSAGE);
      return key.getKey();
    }
  }

  public static void updateLabels() {
    LABEL_LIST.forEach(IMyCellarComponent::updateText);
  }

  private static String getLabelForType(LabelProperty labelProperty) {
    return getLabelForType(Program.getProgramType(), labelProperty);
  }

  public static String getLabelForType(ProgramType programType, LabelProperty labelProperty) {
    String value;
    String prefix;
    String postfix = labelProperty.isPlural() ? "s" : "";
    prefix = switch (labelProperty.getGrammar()) {
      case SINGLE -> labelProperty.isPlural() ? "more" : "one";
      case THE -> "the";
      case OF_THE -> "ofthe";
      default -> "";
    };
    value = switch (programType) {
      case BOOK -> getLabelFromCode("Program." + prefix + "book" + postfix, true);
      case MUSIC -> getLabelFromCode("Program." + prefix + "disc" + postfix, true);
      default -> getLabelFromCode("Program." + prefix + "wine" + postfix, true);
    };
    if (labelProperty.isUppercaseFirst()) {
      value = StringUtils.capitalize(value);
    }
    return value;
  }
}
