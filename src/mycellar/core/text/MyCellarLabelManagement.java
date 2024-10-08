package mycellar.core.text;

import mycellar.Program;
import mycellar.ProgramType;
import mycellar.core.IMyCellarComponent;
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
 * @version 0.9
 * @since 25/05/22
 */

public class MyCellarLabelManagement {

  private static final List<IMyCellarComponent> LABEL_LIST = new ArrayList<>();

  public static void add(IMyCellarComponent component) {
    LABEL_LIST.add(component);
  }

  public static void updateText(IMyCellarComponent component, LabelKey labelKey) {
    component.setText(getLabel(labelKey.getLabelType(), labelKey.getKey(), labelKey.getLabelProperty(), labelKey.getValue()));
  }

  public static String getLabelCode(String code) {
    return getLabel(LabelType.LABEL, code, null, null);
  }

  public static String getLabel(LabelType type, String code, LabelProperty labelProperty, String labelValue) {
    if (type == null || code == null) {
      return "";
    }

    if (labelValue == null) {
      return switch (type) {
        case LABEL -> getLabel(code, labelProperty);
        case ERROR -> getError(code, labelProperty);
        case NONE -> code;
      };
    } else {
      return switch (type) {
        case LABEL -> MessageFormat.format(getLabel(code, labelProperty), labelValue).strip();
        case ERROR -> MessageFormat.format(getError(code, labelProperty), labelValue).strip();
        case NONE -> code;
      };
    }
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
        JOptionPane.showMessageDialog(null, "Missing Label '" + id + "'", "Error", JOptionPane.ERROR_MESSAGE);
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
      JOptionPane.showMessageDialog(null, "Missing Error '" + id + "'", "Error", JOptionPane.ERROR_MESSAGE);
      return id;
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
      case BOOK -> getLabel("Program." + prefix + "book" + postfix);
      case MUSIC -> getLabel("Program." + prefix + "disc" + postfix);
      default -> getLabel("Program." + prefix + "wine" + postfix);
    };
    if (labelProperty.isUppercaseFirst()) {
      value = StringUtils.capitalize(value);
    }
    return value;
  }
}
