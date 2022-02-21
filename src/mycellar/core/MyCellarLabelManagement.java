package mycellar.core;

import mycellar.Program;
import mycellar.ProgramType;
import mycellar.core.language.LanguageFileLoader;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JOptionPane;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import static mycellar.ProgramConstants.DOUBLE_DOT;
import static mycellar.ProgramConstants.ERRORS_LABEL_KEY;
import static mycellar.ProgramConstants.INFOS_LABEL_KEY;
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
 * @version 0.4
 * @since 21/02/22
 */

public class MyCellarLabelManagement {

  private static final List<IMyCellarComponent> LABEL_LIST = new ArrayList<>();

  public static void add(IMyCellarComponent component) {
    LABEL_LIST.add(component);
  }

  public static void updateText(IMyCellarComponent component, LabelType type, String code, String value, LabelProperty labelProperty) {
    component.setText(getLabel(type, code, labelProperty, value));
  }

  public static String getLabel(LabelType type, String code) {
    return getLabel(type, code, null, null);
  }

  public static String getLabel(LabelType type, String code, LabelProperty labelProperty) {
    return getLabel(type, code, labelProperty, null);
  }

  public static String getLabel(LabelType type, String code, LabelProperty labelProperty, String labelValue) {
    if (type == null || code == null) {
      return "";
    }

    if (labelValue == null) {
      switch (type) {
        case INFO:
          return getLabel(INFOS_LABEL_KEY + code, labelProperty);
        case ERROR:
          return getError(ERRORS_LABEL_KEY + code, labelProperty);
        case INFO_OTHER:
          return getLabel(code, labelProperty);
        case ERROR_OTHER:
          return getError(code, labelProperty);
        default:
          throw new RuntimeException("Not implemented for type: " + type);
      }
    } else {
      switch (type) {
        case INFO:
          return MessageFormat.format(getLabel(INFOS_LABEL_KEY + code, labelProperty), labelValue).strip();
        case ERROR:
          return MessageFormat.format(getError(ERRORS_LABEL_KEY + code, labelProperty), labelValue).strip();
        case INFO_OTHER:
          return MessageFormat.format(getLabel(code, labelProperty), labelValue).strip();
        case ERROR_OTHER:
          return MessageFormat.format(getError(code, labelProperty), labelValue).strip();
        default:
          throw new RuntimeException("Not implemented for type: " + type);
      }
    }
  }

  public static void updateLabels() {
    LABEL_LIST.forEach(IMyCellarComponent::updateText);
  }

  public static String getLabel(String id) {
    return getLabel(id, true);
  }

  public static String getLabel(String id, LabelProperty labelProperty) {
    if (labelProperty == null) {
      return getLabel(id, true);
    }
    String label = getLabel(id, true);
    label = label.replaceAll(KEY_TYPE, getLabelForType(labelProperty));
    if (labelProperty.isThreeDashes()) {
      label += THREE_DOTS;
    }
    if (labelProperty.isDoubleQuote()) {
      label += LanguageFileLoader.isFrench() ? SPACE + DOUBLE_DOT : DOUBLE_DOT;
    }
    return label;
  }

  public static String getLabel(String id, boolean displayError) {
    try {
      return LanguageFileLoader.getLabel(id);
    } catch (MissingResourceException e) {
      if (displayError) {
        Program.Debug("Program: ERROR: Missing Label " + id);
        JOptionPane.showMessageDialog(null, "Missing Label " + id, "Error", JOptionPane.ERROR_MESSAGE);
      }
      return id;
    }
  }

  public static String getError(String id, LabelProperty labelProperty) {
    if (labelProperty == null) {
      return getError(id);
    }
    String label = getError(id);
    return label.replaceAll(KEY_TYPE, getLabelForType(labelProperty));
  }

  public static String getError(String id) {
    try {
      return LanguageFileLoader.getError(id);
    } catch (MissingResourceException e) {
      JOptionPane.showMessageDialog(null, "Missing Error " + id, "Error", JOptionPane.ERROR_MESSAGE);
      return id;
    }
  }

  private static String getLabelForType(LabelProperty labelProperty) {
    return getLabelForType(Program.getProgramType(), labelProperty);
  }

  public static String getLabelForType(ProgramType programType, LabelProperty labelProperty) {
    String value;
    String prefix;
    String postfix = labelProperty.isPlural() ? "s" : "";
    switch (labelProperty.getGrammar()) {
      case SINGLE:
        prefix = labelProperty.isPlural() ? "more" : "one";
        break;
      case THE:
        prefix = "the";
        break;
      case OF_THE:
        prefix = "ofthe";
        break;
      case NONE:
      default:
        prefix = "";
        break;
    }
    switch (programType) {
      case BOOK:
        value = getLabel("Program." + prefix + "book" + postfix);
        break;
      case MUSIC:
        value = getLabel("Program." + prefix + "disc" + postfix);
        break;
      case WINE:
      default:
        value = getLabel("Program." + prefix + "wine" + postfix);
    }
    if (labelProperty.isUppercaseFirst()) {
      value = StringUtils.capitalize(value);
    }
    return value;
  }
}
