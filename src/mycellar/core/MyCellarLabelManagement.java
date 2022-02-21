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

  private static final String INFOS = "Infos";
  private static final String ERRORS = "Errors";

  private static final List<IMyCellarComponent> LABEL_LIST = new ArrayList<>();

  public static void add(IMyCellarComponent component) {
    LABEL_LIST.add(component);
  }

  public static void updateText(IMyCellarComponent component, LabelType type, String code, String value, LabelProperty labelProperty) {
    component.setText(getLabel(type, code, labelProperty, value));
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
          return Program.getLabel(INFOS + code, labelProperty);
        case ERROR:
          return Program.getError(ERRORS + code, labelProperty);
        case INFO_OTHER:
          return Program.getLabel(code, labelProperty);
        case ERROR_OTHER:
          return Program.getError(code, labelProperty);
        default:
          throw new RuntimeException("Not implemented for type: " + type);
      }
    } else {
      switch (type) {
        case INFO:
          return MessageFormat.format(Program.getLabel(INFOS + code, labelProperty), labelValue).strip();
        case ERROR:
          return MessageFormat.format(Program.getError(ERRORS + code, labelProperty), labelValue).strip();
        case INFO_OTHER:
          return MessageFormat.format(Program.getLabel(code, labelProperty), labelValue).strip();
        case ERROR_OTHER:
          return MessageFormat.format(Program.getError(code, labelProperty), labelValue).strip();
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
    label = label.replaceAll(KEY_TYPE, getLabelForType(labelProperty.isPlural(), labelProperty.isUppercaseFirst(), labelProperty.getGrammar()));
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
    return label.replaceAll(KEY_TYPE, getLabelForType(labelProperty.isPlural(), labelProperty.isUppercaseFirst(), labelProperty.getGrammar()));
  }

  public static String getError(String id) {
    try {
      return LanguageFileLoader.getError(id);
    } catch (MissingResourceException e) {
      JOptionPane.showMessageDialog(null, "Missing Error " + id, "Error", JOptionPane.ERROR_MESSAGE);
      return id;
    }
  }

  public static String getLabelForType(boolean plural, boolean firstLetterUppercase, Grammar grammar) {
    return getLabelForType(Program.getProgramType(), plural, firstLetterUppercase, grammar);
  }

  public static String getLabelForType(ProgramType theType, boolean plural, boolean firstLetterUppercase, Grammar grammar) {
    String value;
    String prefix;
    String postfix = plural ? "s" : "";
    switch (grammar) {
      case SINGLE:
        prefix = plural ? "more" : "one";
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
    switch (theType) {
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
    if (firstLetterUppercase) {
      value = StringUtils.capitalize(value);
    }
    return value;
  }
}
