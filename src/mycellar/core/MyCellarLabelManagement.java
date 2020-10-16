package mycellar.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import mycellar.Program;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 16/10/20
 */

public class MyCellarLabelManagement {

  private static final List<IMyCellarComponent> LABEL_LIST = new ArrayList<>();
  
  static void add(IMyCellarComponent component) {
    LABEL_LIST.add(component);
  }
  
  static void updateText(IMyCellarComponent component, LabelType type, String code, String value, LabelProperty labelProperty) {
    if (value == null) {
      switch (type) {
        case INFO:
          component.setText(Program.getLabel("Infos" + code, labelProperty));
          break;
        case ERROR:
          component.setText(Program.getError("Errors" + code, labelProperty));
          break;
        case INFO_OTHER:
          component.setText(Program.getLabel(code, labelProperty));
          break;
        case ERROR_OTHER:
          component.setText(Program.getError(code, labelProperty));
          break;
      }
    } else {
      switch (type) {
        case INFO:
          component.setText(MessageFormat.format(Program.getLabel("Infos" + code, labelProperty), value).strip());
          break;
        case ERROR:
          component.setText(MessageFormat.format(Program.getError("Errors" + code, labelProperty), value).strip());
          break;
        case INFO_OTHER:
          component.setText(MessageFormat.format(Program.getLabel(code, labelProperty), value).strip());
          break;
        case ERROR_OTHER:
          component.setText(MessageFormat.format(Program.getError(code, labelProperty), value).strip());
          break;
      }
    }
  }
  
  public static void updateLabels() {
    LABEL_LIST.forEach(IMyCellarComponent::updateText);
  }
}
